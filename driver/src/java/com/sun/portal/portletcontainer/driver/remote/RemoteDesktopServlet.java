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

import com.sun.portal.container.PortletType;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryContext;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import com.sun.portal.portletcontainer.driver.DesktopConstants;
import com.sun.portal.portletcontainer.driver.DesktopServlet;
import com.sun.portal.portletcontainer.driver.DriverUtil;
import com.sun.portal.portletcontainer.driver.PortletContent;
import com.sun.portal.portletcontainer.driver.PortletWindowData;
import com.sun.portal.portletcontainer.invoker.InvokerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RemoteDesktopServlet extends DesktopServlet {

    public void init(ServletConfig config)
    throws ServletException {
        super.init(config);
    }
    
    protected List getVisiblePortletWindows(PortletRegistryContext portletRegistryContext) throws InvokerException {
        List visiblePortletWindows = null;
        try {
            visiblePortletWindows = portletRegistryContext.getVisiblePortletWindows(PortletType.REMOTE);
        } catch (PortletRegistryException pre) {
            visiblePortletWindows = Collections.EMPTY_LIST;
            throw new InvokerException("Cannot get Portlet List", pre);
        }
        return visiblePortletWindows;
    }
    
    protected PortletContent getPortletContentObject(ServletContext context, 
            HttpServletRequest request, HttpServletResponse response) throws InvokerException{
        return new RemotePortletContent(context, request, response);
    }    
    
    protected PortletWindowData getPortletWindowDataObject(HttpServletRequest request, 
            Map portletContents, PortletRegistryContext portletRegistryContext, 
            String portletWindowName) throws PortletRegistryException{
        
        RemotePortletWindowDataImpl portletWindowData = new RemotePortletWindowDataImpl();
        Map portletContentMap = (Map)portletContents.get(portletWindowName);        
        portletWindowData.init(request, portletRegistryContext, portletWindowName);        
        portletWindowData.setContent((StringBuffer)portletContentMap.get(DesktopConstants.PORTLET_CONTENT));
        portletWindowData.setTitle((String)portletContentMap.get(DesktopConstants.PORTLET_TITLE));
        portletWindowData.setCurrentMode(DriverUtil.getPortletWindowModeOfPortletWindow(request, portletWindowName));
        portletWindowData.setCurrentWindowState(getCurrentPortletWindowState(request, portletWindowName));
        
        return portletWindowData;
    }     
    
   protected String getPresentationURI(){
        return "/wsrpportlet.jsp";
    }    
    
}
