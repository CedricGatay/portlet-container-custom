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

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import java.util.Map;
import java.util.Collections;
import com.sun.portal.portletcontainer.appengine.PortletAppEngineUtils;
import com.sun.portal.portletcontainer.common.PortletContainerActionRequest;
import com.sun.portal.portletcontainer.common.PortletContainerActionResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import javax.portlet.ActionRequest;


/**
 * This class provides implementation of the ActionRequest interface.
 */
public class ActionRequestImpl extends ClientDataRequestImpl implements ActionRequest {

    // Global variables
    private PortletContainerActionRequest pcActionRequest;
    private PortletContainerActionResponse pcActionResponse;

    /**
     * Initialize the global variables.
     * <P>
     *
     * @param request          The <code>HttpServletRequest</code> of the PAE
     * @param response          The <code>HttpServletResponse</code> of the PAE
     * @param pcActionRequest     The <code>PortletContainerActionRequest</code>
     * @param pcActionResponse     The <code>PortletContainerActionResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     */
    protected void init(HttpServletRequest request, 
            HttpServletResponse response,
            PortletContainerActionRequest pcActionRequest,
            PortletContainerActionResponse pcActionResponse, 
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor) {

        this.pcActionRequest = pcActionRequest;
        this.pcActionResponse = pcActionResponse;
        Map scopedAttributes = null;
        super.init(request, response, pcActionRequest, pcActionResponse, 
                portletContext, portalContext, portletDescriptor, scopedAttributes);
    }

    /**
     * Clears the global variables.
     */
    @Override
    protected void clear() {
        this.pcActionRequest = null;
        this.pcActionResponse = null;
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

        return pcActionRequest.getCharacterEncoding();
    }

    /**
     * Returns a <code>Map</code> of the parameters of this request.
     * Request parameters are extra information sent with the request.
     * The returned parameters are "x-www-form-urlencoded" decoded.
     * <p>
     * The values in the returned <code>Map</code> are from type
     * String array (<code>String[]</code>).
     * <p>
     * If no parameters exist this method returns an empty <code>Map</code>.
     *
     * @return     an immutable <code>Map</code> containing parameter names as
     *             keys and parameter values as map values, or an empty <code>Map</code>
     *             if no parameters exist. The keys in the parameter
     *             map are of type String. The values in the parameter map are of type
     *             String array (<code>String[]</code>).
     */
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = pcActionRequest.getActionParameters();

        return Collections.unmodifiableMap(map);
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
        ChannelMode portletWindowMode = pcActionResponse.getNewChannelMode();
        PortletMode portletMode = null;

        if (portletWindowMode != null) {
            portletMode = PortletAppEngineUtils.getPortletMode(portletWindowMode);
        } else {
            portletMode = PortletAppEngineUtils.getPortletMode(pcActionRequest.getPortletWindowMode());
        }

        return portletMode;
    }

    /**
     * Returns the current window state of the portlet.
     *
     * @return   the window state
     */

    public WindowState getWindowState() {
        ChannelState newWindowState = pcActionResponse.getNewChannelState();
        WindowState windowState = null;
        if (newWindowState != null) {
            windowState = PortletAppEngineUtils.getWindowState(newWindowState);
        } else {
            windowState = PortletAppEngineUtils.getWindowState(pcActionRequest.getWindowState());
        }

        return windowState;
    }
}
