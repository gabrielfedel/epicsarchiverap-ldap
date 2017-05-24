package org.epics.archiverappliance.mgmt.bpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.epics.archiverappliance.common.BPLAction;
import org.epics.archiverappliance.config.ConfigService;
import org.epics.archiverappliance.utils.ui.MimeTypeConstants;
import org.json.simple.JSONValue;

public class LoginUser implements BPLAction {

	private static final Logger logger = Logger.getLogger(LoginUser.class);

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse resp, ConfigService configService)
			throws IOException {

		HashMap<String, Object> infoValues = new HashMap<String, Object>();
		resp.setContentType(MimeTypeConstants.APPLICATION_JSON);

		HttpSession session = req.getSession();

		logger.info("Username " + req.getParameter("username") + " trying to log in...");

		logger.info("Session = " + session);

		if (session.getAttribute("username") == null ||
				session.getAttribute("username") != req.getParameter("username")) {

			try {

				req.login(req.getParameter("username"), req.getParameter("password"));

				logger.info("Login successful!");

				session.setAttribute("username", req.getParameter("username"));

				logger.info("Username " + (String) session.getAttribute("username") + " signed in...");

				infoValues.put("validate", "authenticated");
			}
			catch (ServletException  e) {

				logger.info("Login failed with error " + e);

				session.invalidate();

				infoValues.put("error", "failed with error " + e);

				logger.error("Ops", e);
			}

		}
		else if (session.getAttribute("username") == req.getParameter("username")) {

			infoValues.put("validate", "user already logged");
		}

		try(PrintWriter out = resp.getWriter()) {
			out.println(JSONValue.toJSONString(infoValues));
		}

	}

}
