package org.epics.archiverappliance.mgmt.bpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.epics.archiverappliance.common.BPLAction;
import org.epics.archiverappliance.common.TimeUtils;
import org.epics.archiverappliance.config.ApplianceInfo;
import org.epics.archiverappliance.config.ConfigService;
import org.epics.archiverappliance.config.PVTypeInfo;
import org.epics.archiverappliance.utils.ui.GetUrlContent;
import org.epics.archiverappliance.utils.ui.MimeTypeConstants;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * 
 * @epics.BPLAction - Stop archiving the specified PV. The PV needs to be paused first. 
 * @epics.BPLActionParam pv - The name of the pv.
 * @epics.BPLActionParam deleteData - Should we delete the data that has already been recorded. Optional, by default, we do not delete the data for this PV. Can be <code>true</code> or <code>false</code>. 
 * @epics.BPLActionEnd
 * 
 * @author mshankar
 *
 */
public class DeletePV implements BPLAction {
	private static Logger logger = Logger.getLogger(DeletePV.class.getName());

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse resp, ConfigService configService) throws IOException {
		String pvName = req.getParameter("pv");
		if(pvName == null || pvName.equals("")) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		boolean deleteData = false;
		String deleteDataStr=req.getParameter("deleteData");
		if(deleteDataStr!=null && !deleteDataStr.equals("")) {
			deleteData = Boolean.parseBoolean(deleteDataStr);
		}
		
		// String pvNameFromRequest = pvName;
		String realName = configService.getRealNameForAlias(pvName);
		if(realName != null)  {
			logger.info("The name " + pvName + " is an alias for " + realName + ". Deleting this instead.");
			pvName = realName;
		}

		ApplianceInfo info = configService.getApplianceForPV(pvName);
		if(info == null) {
			logger.debug("Unable to find appliance for PV " + pvName);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		PVTypeInfo typeInfo = configService.getTypeInfoForPV(pvName);
		if(typeInfo == null) {
			logger.debug("Unable to find typeinfo for PV...");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		HashMap<String, Object> infoValues = new HashMap<String, Object>();
		resp.setContentType(MimeTypeConstants.APPLICATION_JSON);
		if(!typeInfo.isPaused()) {
			infoValues.put("validation", "We will not stop archiving PV " + pvName + " if it is not paused. Please pause recording first.");
			logger.error(infoValues.get("validation"));
			try(PrintWriter out = resp.getWriter()) {
				out.println(JSONValue.toJSONString(infoValues));
			}
			return;
		}
		
		HashMap<String, String> pvStatus = new HashMap<String, String>();
		
		pvStatus.put("Engine start", TimeUtils.convertToHumanReadableString(System.currentTimeMillis()/1000));

		String engineDeletePVURL = info.getEngineURL() + "/deletePV" 
				+ "?pv=" + URLEncoder.encode(pvName, "UTF-8")
				+ "&deleteData=" + Boolean.toString(deleteData); 
		logger.info("Stopping archiving pv in engine using URL " + engineDeletePVURL);
		JSONObject pvEngineStatus = GetUrlContent.getURLContentAsJSONObject(engineDeletePVURL);
		if(pvEngineStatus == null) {
			infoValues.put("validation", "Unknown status from engine when stoppping archiving/deleting PV " + pvName + ".");
			logger.error(infoValues.get("validation"));
			try(PrintWriter out = resp.getWriter()) {
				out.println(JSONValue.toJSONString(infoValues));
			}
			return;
		}

		pvStatus.put("Engine end", TimeUtils.convertToHumanReadableString(System.currentTimeMillis()/1000));

		logger.info("Stopping archiving for PV " + pvName + (deleteData ? " and also deleting existing data" : " but keeping existing data"));

		pvStatus.put("ETL start", TimeUtils.convertToHumanReadableString(System.currentTimeMillis()/1000));
		String etlDeletePVURL = info.getEtlURL() + "/deletePV" 
				+ "?pv=" + URLEncoder.encode(pvName, "UTF-8")
				+ "&deleteData=" + Boolean.toString(deleteData); 
		logger.info("Stopping archiving pv in ETL using URL " + etlDeletePVURL);

		JSONObject etlStatus = GetUrlContent.getURLContentAsJSONObject(etlDeletePVURL);
		pvStatus.put("ETL end", TimeUtils.convertToHumanReadableString(System.currentTimeMillis()/1000));
		GetUrlContent.combineJSONObjects(pvStatus, etlStatus);
		if(pvStatus != null && !pvStatus.equals("")) {
			logger.debug("Removing pv " + pvName + " from the cluster");
			pvStatus.put("Start removing PV from cluster", TimeUtils.convertToHumanReadableString(System.currentTimeMillis()/1000));
			configService.removePVFromCluster(pvName);
			pvStatus.put("Done removing PV from cluster", TimeUtils.convertToHumanReadableString(System.currentTimeMillis()/1000));
			
			logger.debug("Removing aliases for pv " + pvName + " from the cluster");
			pvStatus.put("Start removing aliases from cluster", TimeUtils.convertToHumanReadableString(System.currentTimeMillis()/1000));
			List<String> aliases = configService.getAllAliases();
			for(String alias : aliases) { 
				String realNameForAlias = configService.getRealNameForAlias(alias);
				if(pvName.equals(realNameForAlias)) { 
					logger.debug("Removing alias " + alias + " for pv " + pvName);
					configService.removeAlias(alias, realNameForAlias);
				}
			}
			pvStatus.put("Done removing aliases from cluster", TimeUtils.convertToHumanReadableString(System.currentTimeMillis()/1000));

			
			try(PrintWriter out = resp.getWriter()) {
				out.println(JSONValue.toJSONString(pvStatus));
			}
		} else {
			try(PrintWriter out = resp.getWriter()) {
				infoValues.put("validation", "Unable to stop archiving pv PV " + pvName);
				out.println(JSONValue.toJSONString(infoValues));
			}
		}
		
		

	}
}
