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
import javax.portlet.Event;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortalContext;
import javax.portlet.EventRequest;
import com.sun.portal.portletcontainer.common.PortletContainerEventRequest;
import com.sun.portal.portletcontainer.common.PortletContainerEventResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.util.Collections;
import java.util.Map;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;

public class EventRequestImpl extends PortletRequestImpl implements EventRequest {

    private PortletContainerEventRequest pcEventRequest;
    private PortletContainerEventResponse pcEventResponse;

    /**
     * Initialize the global variables.
     * <P>
     *
     * @param request          The <code>HttpServletRequest</code> of the PAE
     * @param response          The <code>HttpServletResponse</code> of the PAE
     * @param pcEventRequest     The <code>PortletContainerEventRequest</code>
     * @param pcEventResponse     The <code>PortletContainerEventResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     */
    protected void init(HttpServletRequest request, 
            HttpServletResponse response,
            PortletContainerEventRequest pcEventRequest,
            PortletContainerEventResponse pcEventResponse, 
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor) {

        this.pcEventRequest = pcEventRequest;
        this.pcEventResponse = pcEventResponse;
        String scopeID = this.getParameter(PortletRequest.ACTION_SCOPE_ID);
        Map scopedAttributes = pcEventRequest.getScopedAttributes(scopeID);
        super.init(request, response, pcEventRequest, pcEventResponse, 
                portletContext, portalContext, portletDescriptor, scopedAttributes);
    }

    /**
     * Clears the global variables.
     */
    @Override
    protected void clear() {
        this.pcEventRequest = null;
        this.pcEventResponse = null;
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
        Map<String, String[]> map = pcEventRequest.getEventParameters();
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
     * Returns the event that trigged the call to the processEvent method.
     *
     * @return      the event that triggered the current processEvent call.
     */

    public Event getEvent() {
        Event event = new EventImpl(pcEventResponse.getCurrentEvent());
        return event;
    }

    /**
     * Returns the name of the HTTP method with which the orginal action request was made,
     * for example, POST, or PUT.
     *
     * @since 2.0
     * @return  a String specifying the name of the HTTP method with which
     *          this request was made
     */
    public String getMethod() {
        return getHttpServletRequest().getMethod();
    }

    /**
     * Returns the current mode of the portlet.
     *
     * @return   the portlet mode
     */
    public PortletMode getPortletMode() {
        return PortletAppEngineUtils.getPortletMode(pcEventRequest.getPortletWindowMode());
    }

    /**
     * Returns the current window state of the portlet.
     *
     * @return   the window state
     */

    public WindowState getWindowState() {
        return PortletAppEngineUtils.getWindowState(pcEventRequest.getWindowState());
    }
}
