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


package com.sun.portal.portletcontainer.driver.portletwindow;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelURL;
import com.sun.portal.container.ChannelState;
import com.sun.portal.container.ChannelURLType;
import com.sun.portal.portletcontainer.common.PortletActions;
import com.sun.portal.portletcontainer.invoker.WindowInvokerConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PortletWindowURL provides the concrete implementation of ChannelURL interface.
 */
public class PortletWindowURL implements ChannelURL {
    
    private String desktopURL;
    private Map<String, String[]> parametersMap;
    private Map<String, List<String>> properties;
    private ChannelURLType urlType;
    private ChannelMode newPortletWindowMode;
    private ChannelState newWindowState;
    private boolean secure = false;
    private String cacheLevel;
    private String resourceID;
    private static Logger logger = Logger.getLogger(PortletWindowURL.class.getPackage().getName(),
            "PCDLogMessages");
    private static final String ENC = "UTF-8";
    
    
    public PortletWindowURL(String desktopURL) {
        this.desktopURL = desktopURL;
    }
    
    public void setChannelMode(ChannelMode newChannelMode) {
        this.newPortletWindowMode = newChannelMode;
    }
    
    public void setWindowState(ChannelState newWindowState) {
        this.newWindowState = newWindowState;
    }
    
    public void setURLType(ChannelURLType urlType) {
        this.urlType = urlType;
    }
    
    public void setParameter(String name, String value) {
        String values[] = new String[1];
        values[0] = value;
        setParameter( name, values );
    }
    
    public void setParameter(String name, String[] values) {
        if (parametersMap == null) {
            parametersMap = new HashMap<String, String[]>();
        }
        parametersMap.put(name, values);
    }
    
    public void setParameters(Map<String, String[]> parametersMap) {
        this.parametersMap = parametersMap;
    }
    
    public void setProperty(String name, String value) {
        if(name == null) {
            return;
        }
        if(properties == null) {
            properties = new HashMap<String, List<String>>();
        }
        List<String> values = new ArrayList();
        values.add(value);
        properties.put(name, values);
    }
    
    public void addProperty(String name, String value) {
        if(name == null) {
            return;
        }
        List<String> values = null;
        if (properties == null) {
            properties = new HashMap();
        } else {
            values = properties.get(name);
        }
        
        if(values == null) {
            values = new ArrayList<String>();
        }
        if(value == null) {
            value = "";
        }
        values.add(value);
        properties.put(name, values);
    }
    
    public void setSecure(boolean secure) {
        this.secure = secure;
    }
    
    public void setCacheLevel(String cacheLevel) {
        this.cacheLevel = cacheLevel;
    }
    
    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }
    
    public ChannelState getWindowState() {
        return this.newWindowState;
    }
    
    public ChannelMode getChannelMode() {
        return this.newPortletWindowMode;
    }
    
    public ChannelURLType getURLType() {
        return urlType;
    }
    
    public Map<String, String[]> getParameters() {
        return parametersMap;
    }
    
    public Map<String, List<String>> getProperties() {
        return properties;
    }
    
    public boolean isSecure() {
        return this.secure;
    }
    
    public String getCacheLevel() {
        return cacheLevel;
    }
     
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        int index = this.desktopURL.indexOf("?");
        String processURL;
        if(index != -1){
            processURL = this.desktopURL.substring(index+1);
        } else {
            processURL = this.desktopURL;
        }
        StringTokenizer tokens = new StringTokenizer(processURL, "&");
        String token, internalKey, internalValue;
        HashMap keyValueMap = new HashMap();
        int equalIndex;
        while(tokens.hasMoreTokens()){
            token = tokens.nextToken();
            equalIndex = token.indexOf("=");
            if(equalIndex != -1) {
                internalKey = token.substring(0,equalIndex);
                internalValue = token.substring(equalIndex+1);
                keyValueMap.put(internalKey, internalValue);
            }
        }
        
        buffer.append(this.desktopURL.substring(0,index));
        buffer.append("?");
        keyValueMap.put(WindowInvokerConstants.PORTLET_ACTION, urlType.toString());
        //check if PortletAction is RESOURCE
        if(urlType.toString().equals(PortletActions.RESOURCE))
            keyValueMap.put(WindowInvokerConstants.DRIVER_ACTION,WindowInvokerConstants.RESOURCE);
        
        if (this.newWindowState != null) {
            keyValueMap.put(WindowInvokerConstants.NEW_PORTLET_WINDOW_STATE_KEY, this.newWindowState.toString());
        }
        if (this.newPortletWindowMode != null) {
            keyValueMap.put(WindowInvokerConstants.NEW_PORTLET_WINDOW_MODE_KEY, this.newPortletWindowMode.toString());
        }
        if(this.resourceID != null) {
            try{
                keyValueMap.put(WindowInvokerConstants.RESOURCE_ID_KEY,URLEncoder.encode(this.resourceID,ENC));
            }catch(UnsupportedEncodingException uee){
                logger.log(Level.WARNING, "PSPCD_CSPPD0049", uee);
            }
        }
        if(this.cacheLevel != null) {
            keyValueMap.put(WindowInvokerConstants.RESOURCE_URL_CACHE_LEVEL_KEY, cacheLevel);
        }
        
        Iterator itr = keyValueMap.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry)itr.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            buffer.append("&").append(key);
            buffer.append("=").append(value);
        }
        
        if (this.parametersMap != null) {
            Set<Map.Entry<String, String[]>> entries = this.parametersMap.entrySet();
            for(Map.Entry<String, String[]> mapEntry : entries) {
                String key = mapEntry.getKey();
                String[] values = mapEntry.getValue();
                try {
                    if (isEncodingNeeded(key)) {
                        key = URLEncoder.encode(key, ENC);
                    }
                    for (int j = 0; j < values.length; j++) {
                        buffer.append("&").append(key);
                        if (isEncodingNeeded(values[j])) {
                            buffer.append("=").append(URLEncoder.encode(values[j], ENC));
                        } else {
                            buffer.append("=").append(values[j]);
                        }
                    }
                } catch (UnsupportedEncodingException uee) {
                    if(logger.isLoggable(Level.INFO)) {
                        logger.log(Level.INFO, "PSPCD_CSPPD0009", uee.toString());
                    }
                }
            }
        }
        
        if(this.properties != null) {
            Set<Map.Entry<String, List<String>>> entries = this.properties.entrySet();
            for(Map.Entry<String, List<String>> mapEntry : entries) {
                String key = mapEntry.getKey();
                List<String> values = mapEntry.getValue();
                try {
                    if (isEncodingNeeded(key)) {
                        key = URLEncoder.encode(key, ENC);
                    }
                    for (String value : values) {
                        buffer.append("&").append(key);
                        if (isEncodingNeeded(value)) {
                            buffer.append("=").append(URLEncoder.encode(value, ENC));
                        } else {
                            buffer.append("=").append(value);
                        }
                    }
                } catch (UnsupportedEncodingException uee) {
                    if(logger.isLoggable(Level.INFO)) {
                        logger.log(Level.INFO, "PSPCD_CSPPD0009", uee.toString());
                    }
                }
            }
        }
        
        return buffer.toString();
    }
    
    /**
     * Before any URL encoding, check to see if encoding is needed.
     *
     * @param value value to be checked
     * @return <code>boolean</code>
     */
    private boolean isEncodingNeeded(String value) {
        boolean needsEncoding = false;
		if(value != null) {
			int length = value.length();
			char c;
			for (int i = 0; !needsEncoding && i < length; i++) {
				c = value.charAt(i);
				needsEncoding = (c < 'a' || c > 'z') && (c < 'A' || c > 'Z') &&
						c != '.' && c != '-' && c != '*' && c != '_';
			}
		}
        return needsEncoding;
    }
    
    
}
