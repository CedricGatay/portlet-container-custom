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


package com.sun.portal.portletcontainer.common;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import com.sun.portal.container.ContainerResponse;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.w3c.dom.Element;

/**
 * <code>PortletContainerResponse</code> encapsulates the containerResponse from the
 * PAE back to the portlet container.  PAE is responsible to use the set
 * methods to set the results that it wants to return back to the portlet
 * container.  Portlet container can then use the get methods to get the
 * containerResponse back.
 */
public class PortletContainerResponse {
    
    // key for PortletContainerResponse object in http request attribute
    public static final String PORTLET_CONTAINER_RESPONSE =
		PortletContainerConstants.PREFIX + "portlet_container_response";
    
    private ContainerResponse containerResponse;
    private PortletContainerErrorCode errorCode = PortletContainerErrorCode.NO_ERROR;
    private Exception exception;
    
    
    public PortletContainerResponse( ContainerResponse containerResponse ) {
        this.containerResponse = containerResponse;
    }
    
    /**
     * Returns the code of the error that occured during the current operation.
     *
     * @return  <code>PortletContainerErrorCode.NO_ERROR</code> if there is no
     * error, otherwise code defined in PortletContainerErrorCode.
     **/
    public PortletContainerErrorCode getErrorCode() {
        return errorCode;
    }
    
    /**
     * Sets the code of the error that occured during the current operation.
     *
     * @param  errorCode   <code>PortletContainerErrorCode.NO_ERROR</code> if
     * there is no error, otherwise code defined in PortletContainerErrorCode.
     */
    public void setErrorCode( PortletContainerErrorCode errorCode ) {
        this.errorCode = errorCode;
    }
    
    /**
     * Returns the exception that occured during the current operation.
     *
     * @return  the exception that occured during the current operation, null
     * otherwise.
     **/
    public Exception getException() {
        return exception;
    }
    
    /**
     * Sets the exception that occured during the current operation.
     *
     * @param  exception   exception that occured during the current operation.
     */
    public void setException( Exception exception ) {
        this.exception = exception;
    }
    
    /**
     * Returns the HttpServletResponse.
     *
     * @return  the HttpServletResponse
     **/
    public HttpServletResponse getHttpServletResponse() {
        return containerResponse.getHttpServletResponse();
    }
    
    /**
     * Returns a <code>Map</code> of String properties.
     * Properties can be used by portlets to send vendor
     * specific information to the portal/portlet-container.
     *
     * @return  the properties map that has List of String as values
     **/
    public Map<String, List<String>> getStringProperties() {
        return containerResponse.getStringProperties();
    }
    
    /**
     * Sets a <code>Map</code> of String properties.
     *
     * @param properties the map of key and values
     */
    public void setStringProperties( Map<String, List<String>> properties ) {
        containerResponse.setStringProperties(properties);
    }
    
    /**
     * Returns a <code>Map</code> of DOM Element properties.
     * Properties can be used by portlets to send vendor
     * specific information to the portal/portlet-container.
     *
     * @return  the properties map that has DOM Element as values
     **/
    public Map<String, List<Element>> getElementProperties(){
        return containerResponse.getElementProperties();
    }
    
    /**
     * Sets <code>Map</code> of DOM Element properties.
     *
     * @param properties the map of key and DOM Element values
     */
    public void setElementProperties( Map<String, List<Element>> properties ) {
        containerResponse.setElementProperties(properties);
    }
    
    /**
     * Returns a <code>List</code> of Http Servlet Cookies.
     * Properties can be used by portlets to send vendor
     * specific information to the portal/portlet-container.
     *
     * @return  the List of Cookies.
     **/
    public List<Cookie> getCookieProperties() {
        return containerResponse.getCookieProperties();
    }
    
    /**
     * Sets a <code>List</code> of Http Servlet Cookies
     *
     * @param properties the List of Cookies.
     */
    public void setCookieProperties( List<Cookie> properties ) {
        containerResponse.setCookieProperties(properties);
    }
}
