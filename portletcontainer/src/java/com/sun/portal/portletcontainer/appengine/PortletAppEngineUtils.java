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
 

package com.sun.portal.portletcontainer.appengine;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;
import com.sun.portal.container.ChannelURLType;
import com.sun.portal.container.ContainerLogger;
import com.sun.portal.portletcontainer.common.PortletActions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletContext;

/*
 * This class include utility methods for the Portlet Application Engine.
 */
public class PortletAppEngineUtils {

    private static int MINOR_VERSION = 5;
    private static final String SERVLET_MAPPING = "/servlet/PortletAppEngineServlet";
    
    private static Logger logger = ContainerLogger.getLogger(PortletAppEngineUtils.class, "PAELogMessages");
    
    static Map<WindowState, ChannelState> windowStateChannelStateMap = 
		new HashMap<WindowState, ChannelState>();

	static {
        windowStateChannelStateMap.put(
                WindowState.MINIMIZED, ChannelState.MINIMIZED);
        windowStateChannelStateMap.put(
                WindowState.MAXIMIZED, ChannelState.MAXIMIZED);
        windowStateChannelStateMap.put(
                WindowState.NORMAL, ChannelState.NORMAL);
	}
	
    static Map<ChannelState, WindowState> channelStateWindowStateMap = 
		new HashMap<ChannelState, WindowState>();

	static {
        channelStateWindowStateMap.put(
                ChannelState.MINIMIZED, WindowState.MINIMIZED);
        channelStateWindowStateMap.put(
                ChannelState.MAXIMIZED, WindowState.MAXIMIZED);
        channelStateWindowStateMap.put(
                ChannelState.NORMAL, WindowState.NORMAL);
	}
	
    static Map<PortletMode, ChannelMode> portletModeChannelModeMap = 
		new HashMap<PortletMode, ChannelMode>();

	static {
        portletModeChannelModeMap.put(
                PortletMode.VIEW, ChannelMode.VIEW);
        portletModeChannelModeMap.put(
                PortletMode.EDIT, ChannelMode.EDIT);
        portletModeChannelModeMap.put(
                PortletMode.HELP,ChannelMode.HELP);
    }

    static Map<ChannelMode, PortletMode> channelModePortletModeMap = 
		new HashMap<ChannelMode, PortletMode>();

	static {
        channelModePortletModeMap.put(
                ChannelMode.VIEW, PortletMode.VIEW);
        channelModePortletModeMap.put(
                ChannelMode.EDIT, PortletMode.EDIT);
        channelModePortletModeMap.put(
                ChannelMode.HELP,PortletMode.HELP);
    }

    static Map<String,ChannelURLType> urlTypeMap = 
		new HashMap<String,ChannelURLType>();

	static {
        urlTypeMap.put(
                PortletActions.ACTION, ChannelURLType.ACTION);
        urlTypeMap.put(
                PortletActions.RENDER, ChannelURLType.RENDER);
        urlTypeMap.put(
				PortletActions.RESOURCE, ChannelURLType.RESOURCE);
    }

    /**
     * Converts the given action to <code>ChannelURLType</code>.
     * <P>
     * 
     * @param action the given action
	 * 
     * @return the ChannelURLType corresponding to the given action
     */
	public static ChannelURLType getURLType(String action) {
		return urlTypeMap.get(action);
	}

    /**
     * Converts the given <code>ChannelMode</code> to <code>PortletMode</code>.
     * <P>
     * 
     * @param channelMode the ChannelMode
	 * 
     * @return the PortletMode corresponding to the given ChannelMode
     */
    public static PortletMode getPortletMode(ChannelMode channelMode) {
        PortletMode portletMode = channelModePortletModeMap.get(channelMode);
		
		if(portletMode == null && channelMode != null) {
			portletMode = new PortletMode(channelMode.toString());
        }
        
        return portletMode;
    }

    /**
     * Converts the given <code>PortletMode</code> to <code>ChannelMode</code>.
     * <P>
     * 
     * @param portletMode the PortletMode
	 * 
     * @return the ChannelMode corresponding to the PortletMode
     */
    public static ChannelMode getChannelMode(PortletMode portletMode) {
        ChannelMode channelMode = portletModeChannelModeMap.get(portletMode);

        if (channelMode == null && portletMode != null) {
			channelMode = new ChannelMode(portletMode.toString());
        }

        return channelMode;
    }

    /**
     * Converts the given <code>com.sun.portal.container.ChannelState</code> 
     * to <code>javax.portlet.WindowState</code>.
     * <P>
     * 
     * @param channelState the ChannelState
	 * 
     * @return the WindowState corresponding to the ChannelState
     */
    public static WindowState getWindowState(ChannelState channelState) {
        WindowState windowState = channelStateWindowStateMap.get(channelState);

        if (windowState == null && channelState != null) {
			windowState = new WindowState(channelState.toString());
        }
	
		return windowState;
    }

    /**
     * Converts the given <code>javax.portlet.WindowState</code> 
     * to <code>com.sun.portal.container.ChannelState</code>.
     * <P>
     * 
     * @param windowState the WindowState
	 * 
     * @return the ChannelState corresponding to the WindowState
     */
    public static ChannelState getChannelState(WindowState windowState) {
        ChannelState channelState = windowStateChannelStateMap.get(windowState);
        
        if (channelState == null && windowState != null) {
			channelState = new ChannelState(windowState.toString());
        }
	
		return channelState;
    }

    /**
     * Extracts and returns the Portlet Application name from the 
     * Servlet Context.
     *
     * @param context the Servlet Context
     * @return the portlet application name.
     */
    public static String getPortletAppName(ServletContext context) {
        String contextPath = null;
        if(context.getMinorVersion() >= MINOR_VERSION) {
            contextPath = context.getContextPath(); // Method is available only in Servlet 2.5 and above
        } else {
			String servletContextName = context.getServletContextName();
			if(servletContextName == null || servletContextName.trim().length() == 0) {
				String realPath = context.getRealPath(SERVLET_MAPPING).replace('\\', '/');
				// realPath will be of the form http://host/contextPath/SERVLET_MAPPING
				// extract the contextPath
				if(realPath != null) {
					int index = realPath.indexOf(SERVLET_MAPPING);
					int lastIndex = realPath.lastIndexOf("/", index-1);
					contextPath = realPath.substring(lastIndex, index);
					if(logger.isLoggable(Level.FINEST)) {
						logger.log(Level.FINEST, "PSPL_PAECSPPA0032",
								new String[] { realPath, String.valueOf(index), String.valueOf(lastIndex) });
					}
				}
			} else {
				contextPath = servletContextName;
			}
        }
        String portletAppName;
        int index = contextPath.indexOf("/");
        if(index != -1) {
            portletAppName = contextPath.substring(index+1);
        } else {
            portletAppName = contextPath;
        }
        return portletAppName;
    }
}
