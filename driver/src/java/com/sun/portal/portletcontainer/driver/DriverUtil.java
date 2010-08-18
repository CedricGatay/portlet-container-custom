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
import com.sun.portal.portletcontainer.admin.PortletUndeployerInfo;
import com.sun.portal.portletcontainer.common.PortletContainerConstants;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryContext;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryContextAbstractFactory;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryContextFactory;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import com.sun.portal.portletcontainer.driver.admin.AdminConstants;
import com.sun.portal.portletcontainer.invoker.WindowInvokerConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * DriverUtil has utility methods needed for the driver
 *
 */
public class DriverUtil {
    
    private static Logger logger = Logger.getLogger(DriverUtil.class.getPackage().getName(),
            "PCDLogMessages");
    private static int renderParameterPrefixLength = PortletContainerConstants.RENDER_PARAM_PREFIX.length();
    private static int scopedAttributesPrefixLength = PortletContainerConstants.SCOPED_ATTRIBUTES_PREFIX.length();
    
    public static void init(HttpServletRequest request) {
        initParamMap(request);
        removeUnusedObjects(request);
    }
    
    public static String getAdminURL(HttpServletRequest request) {
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(request.getScheme());
        urlBuffer.append("://");
        urlBuffer.append(request.getServerName());
        urlBuffer.append(":");
        urlBuffer.append(request.getServerPort());
        urlBuffer.append(request.getContextPath());
        urlBuffer.append("/admin");
        return urlBuffer.toString();
    }
    
    public static String getPortletsURL(HttpServletRequest request) {
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(request.getScheme());
        urlBuffer.append("://");
        urlBuffer.append(request.getServerName());
        urlBuffer.append(":");
        urlBuffer.append(request.getServerPort());
        urlBuffer.append(request.getContextPath());
        urlBuffer.append("/dt");
        return urlBuffer.toString();
    }
    
    public static String getWSRPURL(HttpServletRequest request) {
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(request.getScheme());
        urlBuffer.append("://");
        urlBuffer.append(request.getServerName());
        urlBuffer.append(":");
        urlBuffer.append(request.getServerPort());
        urlBuffer.append(request.getContextPath());
        urlBuffer.append("/rdt");
        return urlBuffer.toString();
    }
    
    public static String getWSRPTabName() {
        return DesktopMessages.getLocalizedString(AdminConstants.WSRP_TAB);
    }
    
    public static boolean isWSRPAvailable(){
        boolean available = false;
        try{
            //If following is successful, that means, we have wsrp consumer available
            Class.forName("com.sun.portal.wsrp.consumer.markup.WSRPContainer");
            available = true;
        }catch(Exception e){
            logger.finest("PSPCD_CSPPD0034");
        }
        return available;
    }
    
    public static String getDriverAction(HttpServletRequest request) {
        return request.getParameter(WindowInvokerConstants.DRIVER_ACTION);
    }
    
    public static String getPortletRemove(HttpServletRequest request) {
        return request.getParameter(WindowInvokerConstants.PORTLET_REMOVE_KEY);
    }
    
    public static String getPortletWindowFromRequest(HttpServletRequest request) {
        return request.getParameter(WindowInvokerConstants.PORTLET_WINDOW_KEY);
    }
    
     private static String getPortletWindowModeFromRequest(HttpServletRequest request) {
        return request.getParameter(WindowInvokerConstants.PORTLET_WINDOW_MODE_KEY);
     }
     
     private static String getPortletWindowStateFromRequest(HttpServletRequest request) {
        return request.getParameter(WindowInvokerConstants.PORTLET_WINDOW_STATE_KEY);
     }
     
    public static ChannelMode getPortletWindowModeOfPortletWindow(HttpServletRequest request, String portletWindowName) {
        String portletWindowMode = getPortletWindowModeFromRequest(request);
        String portletWindowNameInRequest = getPortletWindowFromRequest(request);
        if(portletWindowMode != null && portletWindowName.equals(portletWindowNameInRequest)) {
            return new ChannelMode(portletWindowMode);
        }
        return null;
    }
    
    public static ChannelState getPortletWindowStateOfPortletWindow(HttpServletRequest request, String portletWindowName) {
        String portletWindowState = getPortletWindowStateFromRequest(request);
        String portletWindowNameInRequest = getPortletWindowFromRequest(request);
        if(portletWindowState != null && portletWindowName.equals(portletWindowNameInRequest)) {
            return new ChannelState(portletWindowState);
        }
        return null;
    }
    
    /*
     * Remove the driver params and retains rest of the params
     */
    private static void initParamMap(HttpServletRequest request) {
        Map<String, String[]> parsedMap = new HashMap<String, String[]>();
        Map<String, String[]> parameterMap = request.getParameterMap();

        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        for(Map.Entry<String, String[]> mapEntry : entries) {
            String key = mapEntry.getKey();
            if(!key.startsWith(WindowInvokerConstants.DRIVER_PARAM_PREFIX) 
                    && (!key.startsWith(WindowInvokerConstants.KEYWORD_PREFIX))){
                parsedMap.put(key, mapEntry.getValue());
            }
        }
        String query = request.getQueryString();
        if(query != null) {
            int index = query.indexOf("?");
            if(index != -1) {
                String queryString = query.substring(index+1).replace('?', '&');
                StringTokenizer andTokens = new StringTokenizer(queryString, "&");
                while(andTokens.hasMoreTokens()) {
                    StringTokenizer equalTokens = new StringTokenizer(andTokens.nextToken(), "=");
                    if(equalTokens.countTokens() == 2) {
                        String key = equalTokens.nextToken();
                        String value = equalTokens.nextToken();
                        String[] values = parsedMap.get(key);
                        if(values != null) {
                            List<String> list = new ArrayList(Arrays.asList(values));
                            list.add(value);
                            values = list.toArray(new String[0]);
                        } else {
                            values = new String[1];
                            values[0] = value;
                        }
                        parsedMap.put(key, values);
                    } else {
                        logger.log(Level.WARNING, "PSPCD_CSPPD0026", queryString);
                        break;
                    }
                }
            }
        }

		//Get charset and decode the parameters		
		String reqEncoding = I18n.DEFAULT_CHARSET;
		if(request.getCharacterEncoding()!= null){
			reqEncoding = request.getCharacterEncoding();
		}
		parsedMap = decodeParams(reqEncoding, parsedMap);
        
        request.setAttribute(WindowInvokerConstants.PORTLET_PARAM_MAP, parsedMap);
    }
    
    private static void removeUnusedObjects(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        List undeployedPortlets = null;
        PortletUndeployerInfo portletUndeployerInfo;
        try {
            portletUndeployerInfo = new PortletUndeployerInfo();
            undeployedPortlets = portletUndeployerInfo.read();
        } catch (PortletRegistryException pre) {
            logger.log(Level.WARNING, "PSPCD_CSPPD0027", pre);
        }
        if(undeployedPortlets != null && !undeployedPortlets.isEmpty()) {
            // Remove the render parameters for the undeployed portlets from the session
            Enumeration attrNames = session.getAttributeNames();
            String attrName, entityId, warName;
            while(attrNames.hasMoreElements()){
                attrName = (String)attrNames.nextElement();
                int index = attrName.indexOf(PortletContainerConstants.RENDER_PARAM_PREFIX);
                if(index != -1){
                    entityId = attrName.substring(renderParameterPrefixLength);
                    try {
                        int delimiter = entityId.indexOf("|");
                        if(delimiter != -1) {
                            warName = entityId.substring(0, delimiter);
                            if(undeployedPortlets.contains(warName)){
                                session.removeAttribute(attrName);
                            }
                        }
                    } catch (Exception e) {
                        // If the war name is not present, exception is thrown, ignore it
                        // as some entity ids may not contain warname, for example WSRP portlet windows
                    }
                }
                index = attrName.indexOf(PortletContainerConstants.SCOPED_ATTRIBUTES_PREFIX);
                if(index != -1){
                    entityId = attrName.substring(scopedAttributesPrefixLength);
                    try {
                        int delimiter = entityId.indexOf("|");
                        if(delimiter != -1) {
                            warName = entityId.substring(0, delimiter);
                            if(undeployedPortlets.contains(warName)){
                                session.removeAttribute(attrName);
                            }
                        }
                    } catch (Exception e) {
                        // If the war name is not present, exception is thrown, ignore it
                        // as some entity ids may not contain warname, for example WSRP portlet windows
                    }
                }
            }
            // Remove PortletWindowData Object for the undeployed portlets from the session
            removePortletWindowData(session, undeployedPortlets);
        }
    }
    
    // If the session contains a PortletWindowData object corresponding to the war that was
    // deployed, remove it
    private static void removePortletWindowData(HttpSession session, List<String> undeployedPortlets) {
        PortletWindowData portletWindowData = null;
        Map<String, SortedSet<PortletWindowData>> portletWindowContents = (Map)session.getAttribute(DesktopConstants.PORTLET_WINDOWS);
        boolean found = false;
        if(portletWindowContents != null) {
            Set set = portletWindowContents.entrySet();
            Iterator<Map.Entry> setItr = set.iterator();
            while(setItr.hasNext()) {
                Map.Entry<String, SortedSet<PortletWindowData>> mapEntry = setItr.next();
                SortedSet<PortletWindowData> portletWindowDataSet = mapEntry.getValue();
                for(Iterator<PortletWindowData> itr = portletWindowDataSet.iterator(); itr.hasNext();) {
                    portletWindowData = itr.next();
                    String portletName = portletWindowData.getPortletName();
                    int delimiter = portletName.indexOf(".");
                    if(delimiter != -1) {
                        String warName = portletName.substring(0, delimiter);
                        if(undeployedPortlets.contains(warName)){
                            itr.remove();
                        }
                    }
                }
            }
        }
    }

    /**
     * decode the request parameters using the character set
     */
    private static Map<String, String[]> decodeParams(String charset, Map<String, String[]> parsedMap) {
        Map<String, String[]> decodedMap = new HashMap<String, String[]>();
        if ( parsedMap != null ) {
            Set<Map.Entry<String, String[]>> entries = parsedMap.entrySet();
            for(Map.Entry<String, String[]> mapEntry : entries) {
                String key = mapEntry.getKey();
                String[] values = mapEntry.getValue();
                String decodedkey = I18n.decodeCharset(key, charset);
                for (int i = 0; i < values.length; i++) {
                    values[i] = I18n.decodeCharset(values[i], charset);
                }
                //put it back in the hashmap
                decodedMap.put(decodedkey, values);
            }
        }
        return decodedMap;
    }
    
    public static PortletRegistryContext getPortletRegistryContext() throws PortletRegistryException {
        PortletRegistryContextAbstractFactory afactory = new PortletRegistryContextAbstractFactory();
        PortletRegistryContextFactory factory = afactory.getPortletRegistryContextFactory();
        return factory.getPortletRegistryContext();
    }
}
