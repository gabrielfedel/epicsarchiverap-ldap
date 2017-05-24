package org.epics.archiverappliance.mgmt;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class MgmtAuthenticationFilter implements Filter {
	private static Logger logger = Logger.getLogger(MgmtAuthenticationFilter.class.getName());
	private FilterConfig filterConfig = null;

	@Override
	public void destroy() {
		this.filterConfig = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpSession session = ((HttpServletRequest) request).getSession();

		logger.info("Session " + session);

		/* User has been authenticated */
		if (session.getAttribute("username") != null) {
			logger.info("Username " + session.getAttribute("username") + " is defined in session");
			chain.doFilter(request, response);
		}
		else {
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, 
					"Current user does not have enough rights to perform this operation!");
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

}

