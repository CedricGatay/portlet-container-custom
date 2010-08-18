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

import com.sun.portal.container.ChannelMode;
import com.sun.portal.portletcontainer.admin.PortletRegistryCache;
import com.sun.portal.portletcontainer.admin.PropertiesContext;
import com.sun.portal.portletcontainer.invoker.InvokerException;
import com.sun.portal.portletcontainer.invoker.WindowInvokerConstants;
import com.sun.portal.portletcontainer.driver.DriverUtil;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;

/**
 * This servlet is written for running JSR 168 TCK on WSRP channels.
 * JSR 168 TCK application will send request to this servlet and this servlet
 * will modify portlet name (channel name) by prefixing 'wsrp.' in the portlet
 * name and will forward the request to WSRP consumer.
 * This servlet is similar to the TCKServlet written for JSR 168 portlet
 * container.
 */

public class RemoteTCKServlet extends HttpServlet {
    
    private static String PORTLET_NAME_KEY_IN_TCK = "portletName";
    private static List portletNames;
    ServletContext context;
    
    public void init(ServletConfig config)
    throws ServletException {
        super.init(config);
        context = config.getServletContext();
        PropertiesContext.init();
        PortletRegistryCache.init();
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        DriverUtil.init(request);
        try {
            response.setContentType("text/html;charset=UTF-8");
            String portletWindowName = getPortletWindowName(request);
            StringBuffer buffer = null;
            if(portletWindowName == null){
                // Get the portletName and render it
                List portletNamesList = getSelectedPortletWindows(request);
                buffer = renderPortlets(context, request, response, portletNamesList);
            } else {
                String driverAction = DriverUtil.getDriverAction(request);
                if(driverAction == null) {
                    driverAction = WindowInvokerConstants.RENDER;
                }
                if(WindowInvokerConstants.ACTION.equals(driverAction)) {
                    RemotePortletContent portletContent = getPortletContent(context,
                            request, response, portletWindowName);
                    URL url = portletContent.executeAction();
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
                    // Render all portlets
                    List portletNamesList = getSelectedPortletWindows(request);
                    buffer = renderPortlets(context, request, response, portletNamesList);
                }
            }
            PrintWriter out = response.getWriter();
            out.println(buffer);
            out.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
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
    
    private List processNames(String[] portletNames) {
        // The portletName in the TCK request is of the form
        // webappname/portletname. Portlet container expects it in the form
        // webappname.portletname. Window (channel) name for remote TCK portlet
        // starts with "wsrp."
        int size = portletNames.length;
        List portletNamesList = new ArrayList();
        for(int i=0; i<size; i++) {
            portletNamesList.add("wsrp." + portletNames[i].replace('/', '.'));
        }
        System.out.println("Processed name for remote portlets ==> " + portletNamesList);
        return portletNamesList;
    }
    
    private ChannelMode getPortletWindowMode(HttpServletRequest request) {
        String value = DriverUtil.getPortletWindowFromRequest(request);
        ChannelMode portletWindowMode = DriverUtil.getPortletWindowModeOfPortletWindow(request, value);
        if(portletWindowMode == null) {
            return ChannelMode.VIEW;
        }
        return portletWindowMode;
    }
    
    private String getValue(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(true);
        return (String)session.getAttribute(name);
    }
    
    private void setValue(HttpServletRequest request, String name, String value) {
        HttpSession session = request.getSession(true);
        session.setAttribute(name, value);
    }
    
    private RemotePortletContent getPortletContent(ServletContext context,
            HttpServletRequest request, HttpServletResponse response,
            String portletWindowName) throws Exception {
        RemotePortletContent portletContent = new RemotePortletContent(context,
                request, response);
        portletContent.setPortletWindowName(portletWindowName);
        ChannelMode portletWindowMode = getPortletWindowMode(request);
        portletContent.setPortletWindowMode(portletWindowMode);
        return portletContent;
    }
    
    public List getSelectedPortletWindows(HttpServletRequest request) {
        String[] portletWindows = request.getParameterValues(PORTLET_NAME_KEY_IN_TCK);
        List selected = null;
        if (portletWindows != null && portletWindows.length != 0) {
            selected = processNames(portletWindows);
            portletNames = selected;
        } else {
            selected = portletNames;
        }
        if (selected == null) {
            selected = Collections.EMPTY_LIST;
        }
        return selected;
    }
    
    private StringBuffer renderPortlets(ServletContext context,
            HttpServletRequest request, HttpServletResponse response,
            List portletNamesList) throws Exception {
        int numPortletWindows = portletNamesList.size();
        String portletWindowName;
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < numPortletWindows; i++) {
            portletWindowName = (String)portletNamesList.get(i);
            RemotePortletContent portletContent = getPortletContent(context,
                    request, response, portletWindowName);
            buffer.append(portletContent.getContent());
        }
        return buffer;
    }
    
}
