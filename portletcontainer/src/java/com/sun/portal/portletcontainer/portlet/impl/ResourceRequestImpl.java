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

package com.sun.portal.portletcontainer.portlet.impl;

import com.sun.portal.portletcontainer.appengine.PortletAppEngineUtils;
import com.sun.portal.portletcontainer.common.PortletContainerResourceRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResourceResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.portlet.WindowState;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.util.Collections;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.sun.portal.container.WindowRequestReader;

/**
 * This class provides implementation of the ResourceRequest interface.
 */
public class ResourceRequestImpl extends ClientDataRequestImpl implements ResourceRequest {

    // Global variables
    private PortletContainerResourceRequest pcResourceRequest;
    private PortletContainerResourceResponse pcResourceResponse;

    /**
     * Initialize the global variables.
     * <P>
     *
     * @param request          The <code>HttpServletRequest</code> of the PAE
     * @param response          The <code>HttpServletResponse</code> of the PAE
     * @param pcResourceRequest     The <code>PortletContainerResourceRequest</code>
     * @param pcResourceResponse    The <code>PortletContainerResourceResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     */
    protected void init(HttpServletRequest request, 
            HttpServletResponse response,
            PortletContainerResourceRequest pcResourceRequest,
            PortletContainerResourceResponse pcResourceResponse, 
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor) {

        this.pcResourceRequest = pcResourceRequest;
        this.pcResourceResponse = pcResourceResponse;
        String scopeID = this.getParameter(PortletRequest.ACTION_SCOPE_ID);
        Map scopedAttributes = pcResourceRequest.getScopedAttributes(scopeID);
        super.init(request, response, pcResourceRequest, pcResourceResponse, 
                portletContext, portalContext, portletDescriptor, scopedAttributes);
    }

    /**
     * Clears the global variables.
     */
    @Override
    protected void clear() {
        this.pcResourceRequest = null;
        this.pcResourceResponse = null;
        super.clear();
    }

    /**
     * Returns the name of the character encoding used in the body of this request.
     * This method returns <code>null</code> if the request
     * does not specify a character encoding.
     *
     * @return		a <code>String</code> containing the name of
     *			the chararacter encoding, or <code>null</code>
     *			if the request does not specify a character encoding.
     */
    public String getCharacterEncoding() {

        return pcResourceRequest.getCharacterEncoding();
    }


  /**
   * Returns a <code>Map</code> of the public parameters of this request.
   * Public parameters may be shared with other portlets or components and
   * defined in the portlet deployment descriptor with the 
   * <code>supported-public-render-parameter</code> element.  
   * The returned parameters are "x-www-form-urlencoded" decoded.
   * <p>
   * The values in the returned <code>Map</code> are from type
   * String array (<code>String[]</code>).
   * <p>
   * If no public parameters exist this method returns an empty <code>Map</code>.
   *
   * @since 2.0
   * @return     an immutable <code>Map</code> containing public parameter names as 
   *             keys and public parameter values as map values, or an empty <code>Map</code>
   *             if no public parameters exist. The keys in the parameter
   *             map are of type String. The values in the parameter map are of type
   *             String array (<code>String[]</code>).
   */
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = pcResourceRequest.getResourceParameters();
        if(map == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(map);
        }
    }

    /**
     * Returns a <code>Map</code> of the private parameters of this request.
     * Private parameters are not shared with other portlets or components.
     * The returned parameters are "x-www-form-urlencoded" decoded.
     * <p>
     * The values in the returned <code>Map</code> are from type
     * String array (<code>String[]</code>).
     * <p>
     * If no private parameters exist this method returns an empty <code>Map</code>.
     *
     * @since 2.0
     * @return     an immutable <code>Map</code> containing private parameter names as
     *             keys and private parameter values as map values, or an empty <code>Map</code>
     *             if no private parameters exist. The keys in the parameter
     *             map are of type String. The values in the parameter map are of type
     *             String array (<code>String[]</code>).
     */
    public Map<String, String[]> getPrivateParameterMap() {
        return getPublicPrivateParameterMap(getParameterMap(), false);
    }

    /**
     * Returns a <code>Map</code> of the public parameters of this request.
     * Public parameters may be shared with other portlets or components and
     * defined in the portlet deployment descriptor with the
     * <code>supported-public-render-parameter</code> element.
     * The returned parameters are "x-www-form-urlencoded" decoded.
     * <p>
     * The values in the returned <code>Map</code> are from type
     * String array (<code>String[]</code>).
     * <p>
     * If no public parameters exist this method returns an empty <code>Map</code>.
     *
     * @since 2.0
     * @return     an immutable <code>Map</code> containing public parameter names as
     *             keys and public parameter values as map values, or an empty <code>Map</code>
     *             if no public parameters exist. The keys in the parameter
     *             map are of type String. The values in the parameter map are of type
     *             String array (<code>String[]</code>).
     */
    public Map<String, String[]> getPublicParameterMap() {
        return getPublicPrivateParameterMap(getParameterMap(), true);
    }

    /**
     * Returns the current mode of the portlet.
     *
     * @return   the portlet mode
     */
    public PortletMode getPortletMode() {
        return PortletAppEngineUtils.getPortletMode(pcResourceRequest.getPortletWindowMode());
    }

    /**
     * Returns the current window state of the portlet.
     *
     * @return   the window state
     */

    public WindowState getWindowState() {

        return PortletAppEngineUtils.getWindowState(pcResourceRequest.getWindowState());
    }

    /**
     * Returns the validation tag if the portlet container
     * has a cached response for this validation tag, or
     * <code>null</code> if no cached response exists.
     * <p>
     * This call returns the same value as
     * <code>ResourceRequest.getProperty(ResourceRequest.ETAG)</code>.
     *
     * @return  the validation tag if the portlet container
     *          has a cached response for this validation tag, or
     *          <code>null</code> if no cached response exists.
     */
    public String getETag() {
        return pcResourceResponse.getCacheControl().getETag();
    }

    /**
     * Returns the resource ID set on the ResourceURL or <code>null</code>
     * if no resource ID was set onj the URL.
     *
     * @return  the resource ID set on the ResourceURL,or <code>null</code>
     *          if no resource ID was set on the URL.
     */
    public String getResourceID() {
        return pcResourceRequest.getResourceID();
    }

    /**
     * Returns a <code>Map</code> of the private render parameters of this request.
     * Private parameters are not shared with other portlets or components.  
     * The returned parameters are "x-www-form-urlencoded" decoded.
     * <p>
     * The parameters returned do not include the resource parameters that
     * the portlet may have set on the resource URL triggering this
     * <code>serveResource</code> call.
     * <p>
     * The values in the returned <code>Map</code> are from type
     * String array (<code>String[]</code>).
     * <p>
     * If no private parameters exist this method returns an empty <code>Map</code>.
     *
     * @return     an immutable <code>Map</code> containing private parameter names as 
     *             keys and private parameter values as map values, or an empty <code>Map</code>
     *             if no private parameters exist. The keys in the parameter
     *             map are of type String. The values in the parameter map are of type
     *             String array (<code>String[]</code>).
     */
	public Map<String, String[]> getPrivateRenderParameterMap() {
        
        Map<String,String[]> privateRenderParametersRef = pcResourceRequest.getResourceParameters();
        HashMap<String,String[]> privateRenderParametersCopy = new HashMap<String,String[]>(pcResourceRequest.getResourceParameters());
    
        if(privateRenderParametersCopy == null || privateRenderParametersCopy.isEmpty()) {
            return Collections.emptyMap();
        }

        HttpServletRequest request = getHttpServletRequest();
        WindowRequestReader windowReqReader = pcResourceRequest.getWindowRequestReader();
        Map<String,String[]> urlParameters = windowReqReader.readParameterMap(request);

        if(urlParameters == null || urlParameters.isEmpty()) {
            return getPublicPrivateParameterMap(getParameterMap(),false);
        }

        Set<String> urlParamKeys = urlParameters.keySet();
        Iterator<String> itr = urlParamKeys.iterator();

        while(itr.hasNext()) {
            String urlKey = itr.next();
            if(privateRenderParametersCopy.containsKey(urlKey)) {
                privateRenderParametersCopy.remove(urlKey);
            }
        }

        return getPublicPrivateParameterMap(Collections.unmodifiableMap
                    (privateRenderParametersCopy),false);
	}
    /**
        * Returns the cache level of this resource request.
        * <p>
        * Possible return values are:
        * <code>ResourceURL.FULL, ResourceURL.PORTLET</code>
        * or <code>ResourceURL.PAGE</code>.
        *
        * @return  the cache level of this resource request.
    */
    public String getCacheability() {
        return this.pcResourceRequest.getCacheLevel();
    }
}
