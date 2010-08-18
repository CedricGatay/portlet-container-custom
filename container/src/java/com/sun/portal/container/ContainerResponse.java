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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Element;

/**
 * A <code>ContainerResponse</code> encapsulates the response sent by the
 * container to the aggregation engine.
 **/
public interface ContainerResponse {
    
    /**
     * Returns the HttpServletResponse.
     *
     * @return  the HttpServletResponse
     **/
    public HttpServletResponse getHttpServletResponse();
    
    /**
     * Sets the HttpServletResponse.
     *
     * @param  response   the HttpServletResponse to be set to
     */
    public void setHttpServletResponse( HttpServletResponse response );
    
    /**
     * Returns a <code>Map</code> of String properties. 
     * Properties can be used by portlets to send vendor
     * specific information to the portal/portlet-container.
     *
     * @return  the properties map that has List of String as values
     **/
    public Map<String, List<String>> getStringProperties();
    
    /**
     * Sets a <code>Map</code> of String properties. 
     * 
     * @param properties the map of key and values
     */
    public void setStringProperties( Map<String, List<String>> properties );
    
    /**
     * Returns a <code>Map</code> of DOM Element properties. 
     * Properties can be used by portlets to send vendor
     * specific information to the portal/portlet-container.
     *
     * @return  the properties map that has DOM Element as values
     **/
    public Map<String, List<Element>> getElementProperties();
    
    /**
     * Sets <code>Map</code> of DOM Element properties. 
     * 
     * @param properties the map of key and DOM Element values
     */
    public void setElementProperties( Map<String, List<Element>> properties );
    
    /**
     * Returns a <code>List</code> of Http Servlet Cookies. 
     * Properties can be used by portlets to send vendor
     * specific information to the portal/portlet-container.
     *
     * @return  the List of Cookies.
     **/
    public List<Cookie> getCookieProperties();
    
    /**
     * Sets a <code>List</code> of Http Servlet Cookies
     * 
     * @param properties the List of Cookies.
     */
    public void setCookieProperties( List<Cookie> properties );
}

