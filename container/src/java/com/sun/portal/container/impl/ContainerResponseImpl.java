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

package com.sun.portal.container.impl;

import com.sun.portal.container.ContainerResponse;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;

import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Element;

/**
 * A <code>ContainerResponse</code> encapsulates the response sent by the
 * container to the aggregation engine.
 **/
public class ContainerResponseImpl implements ContainerResponse {

    private HttpServletResponse response;
    private Map<String, List<String>> stringProperties;
    private Map<String, List<Element>> elementProperties;
    private List<Cookie> cookieProperties;

    public HttpServletResponse getHttpServletResponse() {
        return response;
    }

    public void setHttpServletResponse( HttpServletResponse response ) {
        this.response = response;
    }

    public Map<String, List<String>> getStringProperties() {
        return this.stringProperties;
    }
    
    public void setStringProperties( Map<String, List<String>> properties ) {
        this.stringProperties = properties;
    }
    
    public Map<String, List<Element>> getElementProperties(){
        return this.elementProperties;
    }
    
    public void setElementProperties( Map<String, List<Element>> properties ) {
        this.elementProperties = properties;
    }
    
    public List<Cookie> getCookieProperties() {
        return this.cookieProperties;
    }
    
    public void setCookieProperties( List<Cookie> properties ) {
        this.cookieProperties = properties;
    }
}

