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

import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptorConstants;
import java.util.Map;
import java.util.Collections;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.portlet.RenderResponse;
import com.sun.portal.portletcontainer.appengine.PortletAppEngineUtils;
import com.sun.portal.portletcontainer.common.PortletContainerRenderRequest;
import com.sun.portal.portletcontainer.common.PortletContainerRenderResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import javax.portlet.RenderRequest;

/**
 * This class provides implementation of the RenderRequest interface.
 */
public class RenderRequestImpl extends PortletRequestImpl implements RenderRequest {

    // Global variables
    private PortletContainerRenderRequest pcRenderRequest;
    private PortletContainerRenderResponse pcRenderResponse;

    /**
     * Initialize the global variables.
     * <P>
     *
     * @param request          The <code>HttpServletRequest</code> of the PAE
     * @param response          The <code>HttpServletResponse</code> of the PAE
     * @param pcRenderRequest     The <code>PortletContainerRenderRequest</code>
     * @param pcRenderResponse     The <code>PortletContainerRenderResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     */
    protected void init(HttpServletRequest request, 
            HttpServletResponse response,
            PortletContainerRenderRequest pcRenderRequest,
            PortletContainerRenderResponse pcRenderResponse, 
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor) {

        this.pcRenderRequest = pcRenderRequest;
        this.pcRenderResponse = pcRenderResponse;
        String scopeID = this.getParameter(PortletRequest.ACTION_SCOPE_ID);
        Map scopedAttributes = pcRenderRequest.getScopedAttributes(scopeID);
        super.init(request, response, pcRenderRequest, pcRenderResponse, 
                portletContext, portalContext, portletDescriptor, scopedAttributes);
    }

    /**
     * Clears the global variables.
     */
    @Override
    protected void clear() {
        this.pcRenderRequest = null;
        this.pcRenderResponse = null;
        super.clear();
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
        Map<String, String[]> map = pcRenderRequest.getRenderParameters();
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
        return PortletAppEngineUtils.getPortletMode(pcRenderRequest.getPortletWindowMode());
    }

    /**
     * Returns the current window state of the portlet.
     *
     * @return   the window state
     */

    public WindowState getWindowState() {
        return PortletAppEngineUtils.getWindowState(pcRenderRequest.getWindowState());
    }

    /**
     *
     * Returns the value of the specified client request property
     * as a <code>String</code>. If the request did not include a property
     * of the specified name, this method returns <code>null</code>.
     *
     * A portlet can access portal/portlet-container specific properties
     * through this method and, if available, the
     * headers of the HTTP client request.
     * <p>
     * This method should only be used if the
     * property has only one value. If the property might have
     * more than one value, use {@link #getProperties}.
     * <p>
     * If this method is used with a multivalued
     * parameter, the value returned is equal to the first value
     * in the enumeration returned by <code>getProperties</code>.
     *
     * @param name        a <code>String</code> specifying the
     *                    property name
     *
     * @return            a <code>String</code> containing the
     *                    value of the requested
     *                    property, or <code>null</code>
     *                    if the request does not
     *                    have a property of that name
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */

    @Override
    public String getProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name should not be null.");
        }

        String retVal = null;

        if (name.equals(RenderResponse.EXPIRATION_CACHE)) {
            if (pcRenderResponse.getCacheControl().getExpirationTime() != PortletDescriptorConstants.EXPIRATION_CACHE_NOT_DEFINED) {
                retVal = Integer.toString(pcRenderResponse.getCacheControl().getExpirationTime());
            }
        } else {
            retVal = super.getProperty(name);
        }
        return retVal;
    }

    /**
     * This method provides access to the ETag which can be accessed by the Portlets.
     * @return The ETag as a <CODE>String</CODE>
     */
    public String getETag() {
        return pcRenderRequest.getETag();
    }
}
