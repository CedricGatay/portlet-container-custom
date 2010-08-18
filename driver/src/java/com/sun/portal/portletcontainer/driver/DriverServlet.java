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


package com.sun.portal.portletcontainer.driver;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;
import com.sun.portal.portletcontainer.admin.PortletRegistryCache;
import com.sun.portal.portletcontainer.admin.PropertiesContext;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryContext;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import com.sun.portal.portletcontainer.driver.admin.PortletAdminData;
import com.sun.portal.portletcontainer.driver.admin.PortletAdminDataFactory;
import com.sun.portal.portletcontainer.invoker.InvokerException;
import com.sun.portal.portletcontainer.invoker.WindowInvokerConstants;

import com.sun.portal.portletcontainer.invoker.util.InvokerUtil;
import java.io.File;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

public class DriverServlet extends HttpServlet {
    
    private static String PORTLET_WINDOW = "PortletWindow";
    private static final String WAR = 
            WindowInvokerConstants.DRIVER_PARAM_PREFIX + "war";
    private static final String PATH =
            WindowInvokerConstants.DRIVER_PARAM_PREFIX + "path";
    private static final String AUTO =
            WindowInvokerConstants.DRIVER_PARAM_PREFIX + "auto";
    private ServletContext context;
    
    public void init(ServletConfig config)
    throws ServletException {
        super.init(config);
        context = config.getServletContext();
        PropertiesContext.init();
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
			if(isDisplay(request)) {
                handlePortletDisplay(request, response);
			} else if (isDeploy(request)) {
                handlePortletDeploy(request, response);
			} else if (isUndeploy(request)) {
                handlePortletUndeploy(request, response);
			} else if (isList(request)) {
                handlePortletList(request, response);
			}
        } catch(Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }

	private boolean isDisplay(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return "/ospc".equals(servletPath);
	}

	private boolean isDeploy(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return "/deploy".equals(servletPath);
	}

	private boolean isUndeploy(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return "/undeploy".equals(servletPath);
	}

	private boolean isList(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return "/list".equals(servletPath);
	}

    private void handlePortletDisplay(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // Update the cache for each request
        PortletRegistryCache.init();
        // Remove driver related parameters
        DriverUtil.init(request);

        String portletWindowName = getPortletWindowName(request);
		if(portletWindowName == null) {
			throw new Exception("PortletId not found, use pc.portletId as the parameter to specify the portlet Id(i.e application-name.portlet-name)");
		}
        PortletRegistryContext portletRegistryContext = 
                DriverUtil.getPortletRegistryContext();

        PortletContent portletContent = getPortletContent(
                context, request, response, portletWindowName);
        // Show the portlet identified by the key
        StringBuffer buffer = null;
        String driverAction = DriverUtil.getDriverAction(request);
        if(driverAction == null) {
            driverAction = WindowInvokerConstants.RENDER;
        }
        String title = null;
        if(WindowInvokerConstants.ACTION.equals(driverAction)) {
            URL url = executeProcessAction(request, portletContent);
            try {
                if(url != null) {
                    response.sendRedirect(url.toString());
                } else {
                    response.sendRedirect(request.getRequestURL().toString());
                }
            } catch (IOException ioe) {
                throw new InvokerException("Failed during sendRedirect", ioe);
            }
        } else if(WindowInvokerConstants.RENDER.equals(driverAction)) {
            buffer = portletContent.getContent();
            title = portletContent.getTitle();
        } else if(WindowInvokerConstants.RESOURCE.equals(driverAction)) {
            // Invoke Resource
            portletContent.getResources();
        }

        ChannelMode portletWindowMode =
                DriverUtil.getPortletWindowModeOfPortletWindow(
                request, portletWindowName);

        setPortletWindowData(request, portletRegistryContext, 
                portletWindowName, title, portletWindowMode, buffer);

        InvokerUtil.setResponseProperties(request, response, 
                portletContent.getResponseProperties());

        RequestDispatcher rd = context.getRequestDispatcher("/single-portlet.jsp");
        rd.forward(request, response);
        InvokerUtil.clearResponseProperties(
                portletContent.getResponseProperties());
    }

    private void handlePortletDeploy(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

		String warName = request.getParameter(WAR);
		if(warName == null) {
			throw new Exception("WarName not found, use dt.war as the parameter to specify the war name");
		}
		String path = request.getParameter(PATH);
		if(path == null) {
			throw new Exception("Path not found, use dt.path as the parameter to specify the complete path for the war");
		}
		File filePath = new File(path);
		if(!filePath.exists()) {
			throw new Exception("Invalid Path");
		}
		
		boolean deployToContainer = false;
		String auto = request.getParameter(AUTO);
		if("true".equals(auto)) {
			deployToContainer = true;
		}

		PortletAdminData portletAdminData =
				PortletAdminDataFactory.getPortletAdminData();

		if(filePath.isDirectory()) {
			portletAdminData.deployDirectory(filePath, warName, null, null, null);
		} else {
			portletAdminData.deploy(filePath.getAbsolutePath(), deployToContainer);
		}
    }

    private void handlePortletUndeploy(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

		String warName = request.getParameter(WAR);
		if(warName == null) {
			throw new Exception("WarName not found, use dt.war as the parameter to specify the war name");
		}
		int index = warName.indexOf(".war");
		if(index != -1) {
			warName = warName.substring(0, index);
		}
		boolean unDeployToContainer = false;
		String auto = request.getParameter(AUTO);
		if("true".equals(auto)) {
			unDeployToContainer = true;
		}

		PortletAdminData portletAdminData =
				PortletAdminDataFactory.getPortletAdminData();

		portletAdminData.undeploy(warName, unDeployToContainer);
    }

    private void handlePortletList(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

		PortletAdminData portletAdminData =
				PortletAdminDataFactory.getPortletAdminData();

		List<String> portletWindowNames = portletAdminData.getPortletWindowNames();
		StringBuffer portletWindows = new StringBuffer();
		for(String portletWindowName : portletWindowNames) {
			portletWindows.append(portletWindowName);
			portletWindows.append(",");
		}
		PrintWriter writer = response.getWriter();
		writer.println(portletWindows);
    }

    private URL executeProcessAction(HttpServletRequest request, PortletContent portletContent) throws InvokerException {
        String portletWindowName = DriverUtil.getPortletWindowFromRequest(request);
        ChannelMode portletWindowMode = DriverUtil.getPortletWindowModeOfPortletWindow(request, portletWindowName);
        ChannelState portletWindowState = DriverUtil.getPortletWindowStateOfPortletWindow(request, portletWindowName);
        portletContent.setPortletWindowName(portletWindowName);
        portletContent.setPortletWindowMode(portletWindowMode);
        portletContent.setPortletWindowState(portletWindowState);
        URL url = portletContent.executeAction();
        return url;
    }
    
    private PortletContent getPortletContent(ServletContext context, HttpServletRequest request, HttpServletResponse response,
            String portletWindowName) throws Exception {
        PortletContent portletContent = new PortletContent(context, request, response);
        portletContent.setPortletWindowName(portletWindowName);
        ChannelMode portletWindowMode = DriverUtil.getPortletWindowModeOfPortletWindow(request, portletWindowName);
        ChannelState portletWindowState = DriverUtil.getPortletWindowStateOfPortletWindow(request, portletWindowName);
        portletContent.setPortletWindowMode(portletWindowMode);
        portletContent.setPortletWindowState(portletWindowState);
        return portletContent;
    }

    private String getPortletWindowName(HttpServletRequest request) {
        String value = DriverUtil.getPortletWindowFromRequest(request);
        if(value == null) {
            value = getValue(request, WindowInvokerConstants.PORTLET_WINDOW_KEY);
        } else {
            setValue(request, WindowInvokerConstants.PORTLET_WINDOW_KEY, value);
        }
        return value;
    }
    
    private String getValue(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(true);
        return (String)session.getAttribute(name);
    }
    
    private void setValue(HttpServletRequest request, String name, String value) {
        HttpSession session = request.getSession(true);
        session.setAttribute(name, value);
    }
    
    private void setPortletWindowData(HttpServletRequest request, PortletRegistryContext portletRegistryContext, String portletWindowName, String title,
            ChannelMode mode, StringBuffer content) throws PortletRegistryException {
        PortletWindowDataImpl portletWindowDataImpl = new PortletWindowDataImpl();
        portletWindowDataImpl.init(request, portletRegistryContext, portletWindowName);
        portletWindowDataImpl.setContent(content);
        portletWindowDataImpl.setTitle(title);
        portletWindowDataImpl.setCurrentMode(mode);
        portletWindowDataImpl.setCurrentWindowState(ChannelState.NORMAL);
        request.setAttribute(PORTLET_WINDOW, portletWindowDataImpl);
    }
}
