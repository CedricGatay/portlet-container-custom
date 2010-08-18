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

import com.sun.portal.container.PortletEvent;
import com.sun.portal.portletcontainer.appengine.PortletAppEngineUtils;
import com.sun.portal.portletcontainer.common.PortletContainerConstants;
import com.sun.portal.portletcontainer.common.PortletContainerEventRequest;
import com.sun.portal.portletcontainer.common.PortletContainerEventResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventResponseImpl extends StateAwareResponseImpl implements EventResponse {
    
    private PortletContainerEventRequest pcEventRequest;
    private PortletContainerEventResponse pcEventResponse;
    private EventRequest eventRequest;
    
    /**
     * Initialize the global variables.
     * <P>
     *
     * @param request          The <code>HttpServletRequest</code> of the PAE
     * @param response          The <code>HttpServletResponse</code> of the PAE
     * @param pcEventRequest     The <code>PortletContainerEventRequest</code>
     * @param pcEventResponse     The <code>PortletContainerEventResponse</code>
     * @param eventRequest     The <code>EventRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     */
    protected void init(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerEventRequest pcEventRequest,
            PortletContainerEventResponse pcEventResponse,
            EventRequest eventRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor) {
        super.init(request, response, pcEventRequest, pcEventResponse, 
                eventRequest, portletAppDescriptor, portletDescriptor);
        this.pcEventRequest = pcEventRequest;
        this.pcEventResponse = pcEventResponse;
        this.eventRequest = eventRequest;
    }
    
    protected void clear() {
        super.clear();
        this.pcEventRequest = null;
        this.pcEventResponse = null;
        this.eventRequest = null;
    }
    
    /**
     * Sets a parameter map for the render request.
     * <p>
     * All previously set render parameters are cleared.
     * <p>
     * These parameters will be accessible in all sub-sequent render calls via
     * the <code>PortletRequest.getParameter</code> call until a new request
     * is targeted to the portlet.
     * <p>
     * The given parameters do not need to be encoded prior to calling this
     * method.
     * <p>
     * The portlet should not modify the map any further after calling
     * this method.
     *
     * @param parameters
     *            Map containing parameter names for the render phase as keys
     *            and parameter values as map values. The keys in the parameter
     *            map must be of type String. The values in the parameter map
     *            must be of type String array (<code>String[]</code>).
     *
     * @exception java.lang.IllegalArgumentException
     *                if parameters is <code>null</code>, if any of the
     *                keys in the Map are <code>null</code>, if any of
     *                the keys is not a String, or if any of the values is not a
     *                String array.
     * @exception java.lang.IllegalStateException
     *                if the method is invoked after <code>sendRedirect</code>
     *                has been called.
     */
    public void setRenderParameters(Map parameters) {
        //Check if all the params are Strings and corresponding values are String[]
        checkRenderParameterMap(parameters);
        pcEventResponse.setRenderParameters( parameters );
    }
    
    /**
     * Sets a String parameter for the render request.
     * <p>
     * These parameters will be accessible in all sub-sequent render calls via
     * the <code>PortletRequest.getParameter</code> call until a request is
     * targeted to the portlet.
     * <p>
     * This method replaces all parameters with the given key.
     * <p>
     * The given parameter do not need to be encoded prior to calling this
     * method.
     *
     * @param key
     *            key of the render parameter
     * @param value
     *            value of the render parameter
     *
     * @exception java.lang.IllegalArgumentException
     *                if key is <code>null</code>.
     * @exception java.lang.IllegalStateException
     *                if the method is invoked after <code>sendRedirect</code>
     *                has been called.
     */
    public void setRenderParameter(String key, String value) {
        List<String> deletedRenderParameters = 
                getDeletedRenderParameterList(key, value, pcEventResponse.getDeletedRenderParameters());
        pcEventResponse.setDeletedRenderParameters( deletedRenderParameters );
        Map<String, String[]> renderMap = 
                getRenderParameterMap(key, value, pcEventResponse.getRenderParameters());
        pcEventResponse.setRenderParameters( renderMap );
    }
    
    /**
     * Sets a String array parameter for the render request.
     * <p>
     * These parameters will be accessible in all sub-sequent render calls via
     * the <code>PortletRequest.getParameter</code> call until a request is
     * targeted to the portlet.
     * <p>
     * This method replaces all parameters with the given key.
     * <p>
     * The given parameter do not need to be encoded prior to calling this
     * method.
     *
     * @param key
     *            key of the render parameter
     * @param values
     *            values of the render parameter
     *
     * @exception java.lang.IllegalArgumentException
     *                if key or value are <code>null</code>.
     * @exception java.lang.IllegalStateException
     *                if the method is invoked after <code>sendRedirect</code>
     *                has been called.
     */
    public void setRenderParameter(String key, String[] values) {
        Map<String, String[]> renderMap = 
                getRenderParameterMap(key, values, pcEventResponse.getRenderParameters());
        pcEventResponse.setRenderParameters( renderMap );
    }
    
    /**
     * Set the portlet window state to the given portlet window state.
     * <p>
     * Possible values are the standard window states and any custom
     * window states supported by the portal and the portlet.
     * Standard window states are:
     * <ul>
     * <li>MINIMIZED
     * <li>NORMAL
     * <li>MAXIMIZED
     * </ul>
     *
     * @param windowState     the new portlet window state
     *
     * @exception WindowStateException
     *                   if the portlet cannot switch to the specified
     *                   windowstate.
     *                   To avoid this exception the portlet can check the allowed
     *                   window states with <code>Request.isWindowStateAllowed()</code>.
     * @exception java.lang.IllegalStateException
     *                    if the method is invoked after <code>sendRedirect</code> has been called.
     *
     * @see WindowState
     */
    public void setWindowState(WindowState windowState)
    throws WindowStateException {
        
        if ( !eventRequest.isWindowStateAllowed( windowState ) ) {
            throw new WindowStateException("Invalid setting window state", windowState);
        }
        
        pcEventResponse.setChannelState(
                PortletAppEngineUtils.getChannelState( windowState ));
    }
    
    /**
     * Sets the portlet mode of a portlet to the given portlet mode.
     * <p>
     * Possible values are the standard portlet modes and any custom
     * portlet modes supported by the portal and the portlet. Portlets
     * must declare in the deployment descriptor the portlet modes they
     * support for each markup type.
     * Standard portlet modes are:
     * <ul>
     * <li>EDIT
     * <li>HELP
     * <li>VIEW
     * </ul>
     * <p>
     * Note: The portlet may still be called in a different window
     *       state in the next render call, depending on the portlet container / portal.
     *
     * @param portletMode    the new portlet mode
     *
     * @exception PortletModeException
     *                   if the portlet cannot switch to this mode,
     *                   because the portlet does not support it for this markup,
     *                   or the current user is not allowed to switch
     *                   to this portal mode
     *                   To avoid this exception the portlet can check the allowed
     *                   portlet modes with <code>Request.isPortletModeAllowed()</code>.
     * @exception java.lang.IllegalStateException
     *                    if the method is invoked after <code>sendRedirect</code> has been called.
     */
    public void setPortletMode(PortletMode portletMode)
    throws PortletModeException {
        if ( !eventRequest.isPortletModeAllowed( portletMode ) ) {
            throw new PortletModeException("Attempt to set an invalid portlet mode: ", portletMode);
        }
        
        pcEventResponse.setChannelMode(
                PortletAppEngineUtils.getChannelMode( portletMode ));
        
    }
    
    /**
     * Returns a <code>Map</code> of the render parameters currently set on
     * this response.
     * <p>
     * The values in the returned <code>Map</code> are from type String array (<code>String[]</code>).
     * <p>
     * If no parameters exist this method returns an empty <code>Map</code>.
     *
     * @since 2.0
     *
     * @return <code>Map</code> containing render parameter names as keys and
     *         parameter values as map values, or an empty <code>Map</code> if
     *         no parameters exist. The keys in the parameter map are of type
     *         String. The values in the parameter map are of type String array (<code>String[]</code>).
     */
    public Map<String, String[]> getRenderParameterMap() {
        Map<String, String[]> map = pcEventResponse.getRenderParameters();
        if(map == null) {
            return Collections.emptyMap();
        } else {
            return map;
        }
    }
    
    /**
     * Returns the currently set portlet mode on this reponse.
     *
     * @since 2.0
     *
     * @return the portlet mode, or <code>null</code> if none is set
     */
    public PortletMode getPortletMode() {
        return PortletAppEngineUtils.getPortletMode(pcEventResponse.getChannelMode());
    }
    
    /**
     * Returns the currently set window state on this response.
     *
     * @since 2.0
     *
     * @return the window state, or <code>null</code> if none is set
     */
    public WindowState getWindowState() {
        return PortletAppEngineUtils.getWindowState(pcEventResponse.getChannelState());
    }
    
    /**
     * Maintain the current render parameters of the request for
     * the response.
     * <p>
     * All previously set render parameters are cleared.
     * <p>
     * These parameters will be accessible in all
     * subsequent render calls via the
     * <code>PortletRequest.getParameter</code> call until
     * a new request is targeted to the portlet.
     * <p>
     * The given parameters do not need to be encoded
     * prior to calling this method.
     *
     * @param  request   The request the portlet has been provided
     *                   with by the portlet container for the current
     *                   <code>processEvent</code> call.
     */
    public void setRenderParameters(EventRequest request) {
        Map immutableRenderParameters = request.getParameterMap();
        Map renderParameters = new HashMap(immutableRenderParameters.size());
        renderParameters.putAll(immutableRenderParameters);
        pcEventResponse.setRenderParameters( renderParameters );
    }
    
	/**
	 * Removes the specified public render parameter.
	 * The name must reference a public render parameter defined
	 * in the portlet deployment descriptor under the
	 * <code>public-render-parameter</code> element with the
	 * <code>identifier</code> mapping to the parameter name.
	 * 
	 * @param name			a <code>String</code> specifying 
	 *					the name of the public render parameter to be removed
	 *
	 * @exception  java.lang.IllegalArgumentException 
	 *                            if name is <code>null</code>.
	 * @since 2.0
	 */
    public void removePublicRenderParameter(String name) {
		if(checkIfPublicRenderParameter(name)) {
			List<String> deletedRenderParameters = getDeletedRenderParameterList(name,
					null, pcEventResponse.getDeletedRenderParameters());
			pcEventResponse.setDeletedRenderParameters(deletedRenderParameters);
		}
    }

    /**
     * Sets the PortletEvent in the event queue
     *
     * @param event The PortletEvent
     */
    protected void setEventQueue(PortletEvent event) {
        // If the processEvent is successful, this event queue will set in PortletContainerEventResponse
        Queue<PortletEvent> eventQueue =
                (Queue) eventRequest.getAttribute(PortletContainerConstants.EVENTS);
        if (eventQueue == null) {
            eventQueue = new ConcurrentLinkedQueue<PortletEvent>();
        }
        eventQueue.offer(event);
        
        eventRequest.setAttribute(PortletContainerConstants.EVENTS, eventQueue);
    }
    
}
