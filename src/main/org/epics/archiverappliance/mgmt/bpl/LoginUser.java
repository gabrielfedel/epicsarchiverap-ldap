package org.epics.archiverappliance.mgmt.bpl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.epics.archiverappliance.common.BPLAction;
import org.epics.archiverappliance.config.ConfigService;

public class LoginUser implements BPLAction {

	private static final Logger logger = Logger.getLogger(LoginUser.class);
	
	@Override
	public void execute(HttpServletRequest req, HttpServletResponse resp, ConfigService configService)
			throws IOException {
		
			HttpSession session = req.getSession();
			
			logger.info("Username " + req.getAttribute("username") + " logged in");
			
			session.setAttribute("username", req.getAttribute("username"));
	}

}
