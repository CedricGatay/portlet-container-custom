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
 

package com.sun.portal.container;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * The <code>WindowRequestReader</code> is responsible for parsing and providing 
 * the request parameters set and used by the portlet.
 */
public interface WindowRequestReader { 

    /**
     * Returns the new Portlet Window Mode set on this request. If the Portlet Window
     * Mode has changed, the new Portlet Window Mode will be set on the request. This
     * method is responsible for parsing and returning it.
     *
     * @param request The HttpServletRequest Object
     *
     * @return the new Portlet Window Mode set on this request
     */
    public ChannelMode readNewPortletWindowMode(HttpServletRequest request);

    /**
     * Returns the new Portlet Window State set on this request. If the Portlet Window
     * State has changed, the new Portlet Window State will be set on the request. This
     * method is responsible for parsing and returning it.
     *
     * @param request The HttpServletRequest Object
     *
     * @return the new Portlet Window State set on this request
     */
    public ChannelState readNewWindowState(HttpServletRequest request);

    /**
     * Returns the URL type set on this request. The URL type can be either ACTION,
     * RENDER or RESOURCE. During the creation of the URL, one of these values 
     * will be set  on the request. This method is responsible for parsing and returning it.
     *
     * @param request The HttpServletRequest Object
     *
     * @return the URL type set on this request
     *
     */
    public ChannelURLType readURLType(HttpServletRequest request);
    
    /**
     * Returns the parameters set on this request by the portlet. The portlet specific
     * parameters associated with the URL will be set on the request. This
     * method is responsible for parsing and returning it. 
     * The keys in the parameter map are of type String. 
     * The values in the parameter map are of type String array.
     *
     * @param request The HttpServletRequest Object
     *
     * @return the Map of parameters set on this request by the portlet
     */
    public Map<String, String[]> readParameterMap(HttpServletRequest request);

    /**
     * Returns the cache level set on this request. The cache level can be
     * FULL, PORTLET or PAGE. 
     * During the creation of the URL, one of these values may be set on the request. 
     * This method is responsible for parsing and returning it
     *
     * @param request The HttpServletRequest Object
     *
     * @return the cache level set on this request.
     */
    public String getCacheLevel(HttpServletRequest request);
    
    /**
     * Returns the resource ID set on this request. 
     * During the creation of the URL, the value may be set on the request. 
     * This method is responsible for parsing and returning it
     *
     * @param request The HttpServletRequest Object
     *
     * @return the resource ID set on this request.
     */
    public String getResourceID(HttpServletRequest request);
}
