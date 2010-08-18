/*
 * CDDL HEADER START
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.sun.com/cddl/cddl.html and legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */

package com.sun.portal.portletcontainer.driver.remote;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ResourceProxyServlet is wrapper for
 * com.sun.portal.wsrp.consumer.resourceproxy.ResourceProxyServlet that is
 * present in WSRP classes.
 */
public class ResourceProxyServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(ResourceProxyServlet.class.getPackage().getName(),
            "PALogMessages");

	private static String WSRP_RESOURCE_PROXY_SERVLET =
			"com.sun.portal.wsrp.consumer.resourceproxy.ResourceProxyServlet";

	HttpServlet wsrpResourceProxyServlet = null;

    public void init(ServletConfig config)
    throws ServletException {
        super.init(config);
		wsrpResourceProxyServlet = getWSRPResourceProxyServlet();
		if(wsrpResourceProxyServlet != null) {
			wsrpResourceProxyServlet.init(config);
		}
    }

    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
		if(wsrpResourceProxyServlet != null) {
			wsrpResourceProxyServlet.service(request, response);
		}
    }

	public void destroy() {
		if(wsrpResourceProxyServlet != null) {
			wsrpResourceProxyServlet.destroy();
		}
	}

	private HttpServlet getWSRPResourceProxyServlet() {
		try {
			// If following is successful, that means,
			// we have wsrp consumer available
			Class clazz = Class.forName(WSRP_RESOURCE_PROXY_SERVLET);
			wsrpResourceProxyServlet = (HttpServlet)clazz.newInstance();
			logger.log(Level.INFO, "PSPCD_CSPPD0011");
		} catch(Exception e){
		}
		return wsrpResourceProxyServlet;
	}

}
