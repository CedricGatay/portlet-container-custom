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
 * [Name of File] [ver.__] [Date]
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */

package com.sun.portal.container;

import java.util.List;
import java.util.Map;

/**
 * The <CODE>ChannelURL</CODE> is responsible for creating the portlet URL.
 */
public interface ChannelURL {
    
    /**
     * Sets the current mode of the channel.
     *
     * @param  channelMode   the channel mode to be set to
     */
    public void setChannelMode( ChannelMode channelMode ) ;
    
    /**
     * Returns the current mode of the channel.
     *
     * @return  the current channel mode
     **/
    public ChannelMode getChannelMode();
    
    /**
     * Sets the current window state of the channel.
     *
     * @param  windowState   the window state to be set to
     */
    public void setWindowState( ChannelState windowState );
    
    /**
     * Returns the current window state of the channel.
     *
     * @return  current window state
     **/
    public ChannelState getWindowState();
    
    /**
     * Sets the current URL type of the channel.
     *
     * @param channelURLType the ChannelURLType to be set to
     */
    public void setURLType(ChannelURLType channelURLType);
    
    /**
     * Returns the current URL type of the channel.
     *
     * @return the current URL type of the channel.
     */
    public ChannelURLType getURLType();
    
    /**
     * Sets the parameter on this URL
     *
     * @param name the parameter name
     * @param value the parameter value
     */
    public void setParameter(String name, String value);
    
    /**
     * Sets the parameter on this URL
     *
     * @param name the parameter name
     * @param values the parameter values
     */
    public void setParameter(String name, String[] values);
    
    /**
     * Sets the parameters Map on this URL
     *
     * @param parametersMap Map containing parameter name and value.
     */
    public void setParameters(Map<String, String[]> parametersMap);
    
    /**
     * Returns the Map of parameter name and value set on this URL.
     *
     * @return Map of parameter name and value set on this URL.
     */
    public Map<String, String[]> getParameters();
    
    /**
     * Sets the property on this URL
     * This method resets all properties previously added with the same key.
     *
     * @param name the parameter name
     * @param value the parameter value
     */
    public void setProperty(String name, String value);
    
    /**
     * Adds the property on this URL.
     * This method allows URL properties to have multiple values.
     *
     * @param name the parameter name
     * @param value the parameter value
     */
    public void addProperty(String name, String value);
    
    /**
     * Returns the Map of property name and values set on this URL.
     *
     * @return Map of property name and valuse set on this URL.
     */
    public Map<String, List<String>> getProperties();
    
    /**
     * Indicates the security setting for this URL.
     *
     * @param  secure  true, if secure connection is required, false otherwise
     */
    public void setSecure(boolean secure);
    
    /**
     * Return true if this URL uses secure connection, false otherwise.
     *
     * @return true if this URL uses secure connection, false otherwise.
     */
    public boolean isSecure();
    
    /**
     * Sets the cache level on this URL. This is to specify the type
     * of caching required for the resource.
     * The possible values are FULL, PORTLET, PAGE.
     *
     * @param cacheLevel the cacheability level.
     */
    public void setCacheLevel(String cacheLevel);
    
    /**
     * Returns the cache level of this URL.
     * Possible return values are: FULL, PORTLET or PAGE.
     *
     * @return the cache level of this URL.
     */
    public String getCacheLevel();
    
    /**
     * Sets the resource ID on this URL
     *
     * @param resourceID the resource id
     */
    public void setResourceID(String resourceID);
    
    /**
     * Returns the String representation of this URL
     *
     * @return the String representation of this URL
     */
    public String toString();
    
}
