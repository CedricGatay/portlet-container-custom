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

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.PortletEvent;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Collections;
import javax.portlet.PortletMode;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.portlet.PortletModeException;
import com.sun.portal.portletcontainer.common.PortletContainerActionRequest;
import com.sun.portal.portletcontainer.common.PortletContainerActionResponse;
import com.sun.portal.portletcontainer.appengine.PortletAppEngineUtils;
import com.sun.portal.portletcontainer.common.PortletContainerConstants;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.PortletURL;

/**
 * This class provides implementation of the ActionResponse interface.
 */
public class ActionResponseImpl extends StateAwareResponseImpl implements ActionResponse {

    private PortletContainerActionRequest pcActionRequest;
    private PortletContainerActionResponse pcActionResponse;
    private ActionRequest actionRequest;
    private boolean sendRedirectIsCalled = false;
    private boolean setMethodIsCalled = false;

    private static Logger logger = ContainerLogger.getLogger(ActionResponseImpl.class, "PAELogMessages");

    /**
     * Initialize the global variables.
     * <P>
     *
     * @param request          The <code>HttpServletRequest</code> of the PAE
     * @param response          The <code>HttpServletResponse</code> of the PAE
     * @param pcActionRequest     The <code>PortletContainerActionRequest</code>
     * @param pcActionResponse     The <code>PortletContainerActionResponse</code>
     * @param actionRequest     The <code>ActionRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     */
    protected void init(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerActionRequest pcActionRequest,
            PortletContainerActionResponse pcActionResponse,
            ActionRequest actionRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor) {
        super.init(request, response, pcActionRequest, pcActionResponse,
                actionRequest, portletAppDescriptor, portletDescriptor);
        this.actionRequest = actionRequest;
        this.pcActionRequest = pcActionRequest;
        this.pcActionResponse = pcActionResponse;
        this.sendRedirectIsCalled = false;
        this.setMethodIsCalled = false;
    }

    /**
     * Clears the global variables.
     */
    protected void clear() {
        super.clear();
        this.actionRequest = null;
        this.pcActionRequest = null;
        this.pcActionResponse = null;
        this.sendRedirectIsCalled = false;
        this.setMethodIsCalled = false;
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
    public void setWindowState(WindowState windowState) throws WindowStateException {

        if (!actionRequest.isWindowStateAllowed(windowState)) {
            throw new WindowStateException("Invalid setting window state",
                    windowState);
        }

        if (sendRedirectIsCalled) {
            throw new IllegalStateException("Illegal to set window state after sendRedirect is called.");
        }

        setMethodIsCalled = true;

        pcActionResponse.setChannelState(PortletAppEngineUtils.getChannelState(windowState));
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
    public void setPortletMode(PortletMode portletMode) throws PortletModeException {
        if (!actionRequest.isPortletModeAllowed(portletMode)) {
            throw new PortletModeException("Attempt to set an invalid portlet mode: ",
                    portletMode);
        }

        if (sendRedirectIsCalled) {
            throw new IllegalStateException("Illegal to set portlet mode after sendRedirect is called.");
        }

        setMethodIsCalled = true;
        pcActionResponse.setChannelMode(PortletAppEngineUtils.getChannelMode(portletMode));
    }

    /**
     * Instructs the portlet container to send a redirect response
     * to the client using the specified redirect location URL.
     * <p>
     * This method only accepts an absolute URL (e.g.
     * <code>http://my.co/myportal/mywebap/myfolder/myresource.gif</code>)
     * or a full path URI (e.g. <code>/myportal/mywebap/myfolder/myresource.gif</code>).
     * If required,
     * the portlet container may encode the given URL before the
     * redirection is issued to the client.
     * <p>
     * The sendRedirect method can not be invoked after any of the
     * following methods of the ActionResponse interface has been called:
     * <ul>
     * <li>setPortletMode
     * <li>setWindowState
     * <li>setRenderParameter
     * <li>setRenderParameters
     * </ul>
     *
     *
     * @param		location	the redirect location URL
     *
     * @exception	IOException
     *                    If an input or output exception occurs.
     * @exception	java.lang.IllegalArgumentException
     *                    If a relative path URL is given
     * @exception       java.lang.IllegalStateException If the method
     *                    is invoked after any of above mentioned methods of
     *                    the ActionResponse interface has been called.
     */
    public void sendRedirect(String location) throws IOException {

        if (setMethodIsCalled) {
            throw new IllegalStateException("Illegal to sendRedirect after setting window state, portlet mode, or render parameters.");
        }

        URL retURL = null;

        try {
            retURL = new URL(getLocationURL(location));
        } catch (MalformedURLException mfue) {
            throw new IllegalArgumentException(mfue.getMessage());
        }

        sendRedirectIsCalled = true;
        pcActionResponse.setRedirectURL(retURL);
    }

    /**
     * Instructs the portlet container to send a redirect response
     * to the client using the specified redirect location URL and
     * encode a render URL as parameter on the redirect URL.
     * <p>
     * This method only accepts an absolute URL (e.g.
     * <code>http://my.co/myportal/mywebap/myfolder/myresource.gif</code>)
     * or a full path URI (e.g. <code>/myportal/mywebap/myfolder/myresource.gif</code>).
     * If required,
     * the portlet container may encode the given URL before the
     * redirection is issued to the client.
     * <p>
     * The portlet container will attach a render URL with the currently set portlet mode, window state
     * and render parameters on the <code>ActionResponse</code> and the current public render parameters.
     * The attached URL will be available as query parameter value under the key provided with the
     * <code>renderUrlParamName</code> parameter.
     * <p>
     * New values for
     * <ul>
     * <li>setPortletMode
     * <li>setWindowState
     * <li>setRenderParameter
     * <li>setRenderParameters
     * </ul>
     * are only used for creating the render URL and not remembered after the redirect
     * is issued.
     *
     * @param		location	the redirect location URL
     * @param     renderUrlParamName	name of the query parameter under which the portlet container should
     *                                store a render URL to this portlet
     *
     * @exception	java.io.IOException
     *                    if an input or output exception occurs.
     * @exception	java.lang.IllegalArgumentException
     *                    if a relative path URL is given
     */
    public void sendRedirect(String location, String renderUrlParamName) throws IOException {
        if (setMethodIsCalled) {
            throw new IllegalStateException("Illegal to sendRedirect after setting window state, portlet mode, or render parameters.");
        }

        URL retURL = null;

        try {
            //add the current windows state, portlet mode, render parameter and current public parameter
            String renderURL = getCurrentRenderURL();
            StringBuffer urlBuffer = new StringBuffer(getLocationURL(location));
            if (urlBuffer.indexOf("?") != -1){
                //add the URL to the other Parameters
                urlBuffer.append("&");
                urlBuffer.append(renderUrlParamName);
                urlBuffer.append("=");
                urlBuffer.append(renderURL);
            } else {
                //add the URL as a query Parameter
                urlBuffer.append("?");
                urlBuffer.append(renderUrlParamName);
                urlBuffer.append("=");
                urlBuffer.append(renderURL);
            }
            retURL = new URL(urlBuffer.toString());
        } catch (MalformedURLException mfue) {
            throw new IllegalArgumentException(mfue.getMessage());
        }

        sendRedirectIsCalled = true;
        pcActionResponse.setRedirectURL(retURL);
    }

     private String getLocationURL(String location) {
        //Prepend servlet schema, host, and port
        if (location.indexOf("://") == -1) {
            StringBuffer b = new StringBuffer(128);

            b.append(pcActionRequest.getHttpServletRequest().getScheme());
            b.append("://");
            b.append(pcActionRequest.getHttpServletRequest().getServerName());
            b.append(":");
            b.append(pcActionRequest.getHttpServletRequest().getServerPort());
            b.append(location);
            location = b.toString();
        }
        return location;
     }
     
    /**
     * Sets a parameter map for the render request.
     * <p>
     * All previous set render parameters are cleared.
     * <p>
     * These parameters will be accessible in all
     * sub-sequent render calls via the
     * <code>PortletRequest.getParameter</code> call until
     * a new request is targeted to the portlet.
     * <p>
     * The given parameters do not need to be encoded
     * prior to calling this method.
     *
     * @param  parameters   Map containing parameter names for
     *                      the render phase as
     *                      keys and parameter values as map
     *                      values. The keys in the parameter
     *                      map must be of type String. The values
     *                      in the parameter map must be of type
     *                      String array (<code>String[]</code>).
     *
     * @exception	java.lang.IllegalArgumentException
     *                      if parameters is <code>null</code>, if
     *                      any of the key/values in the Map are <code>null</code>,
     *                      if any of the keys is not a String, or if any of
     *                      the values is not a String array.
     * @exception       java.lang.IllegalStateException
     *                    if the method is invoked after <code>sendRedirect</code> has been called.
     */
    public void setRenderParameters(Map parameters) {
        if (sendRedirectIsCalled) {
            throw new IllegalStateException("Illegal to set parameters after sendRedirect is called.");
        }
        //Check if all the params are Strings and corresponding values are String[]
        checkRenderParameterMap(parameters);
        setMethodIsCalled = true;
        pcActionResponse.setRenderParameters(parameters);
    }

    /**
     * Sets a String parameter for the render request.
     * <p>
     * These parameters will be accessible in all
     * sub-sequent render calls via the
     * <code>PortletRequest.getParameter</code> call until
     * a request is targeted to the portlet.
     * <p>
     * This method replaces all parameters with the given key.
     * <p>
     * The given parameter do not need to be encoded
     * prior to calling this method.
     *
     * @param  key    key of the render parameter
     * @param  value  value of the render parameter
     * @exception	java.lang.IllegalArgumentException
     *                      if key or value is <code>null</code>.
     * @exception java.lang.IllegalStateException
     *                    if the method is invoked after <code>sendRedirect</code> has been called.
     */
    public void setRenderParameter(String key, String value) {
        if (sendRedirectIsCalled) {
            throw new IllegalStateException("Illegal to set parameter after sendRedirect is called.");
        }
        List<String> deletedRenderParameters = getDeletedRenderParameterList(key,
                value, pcActionResponse.getDeletedRenderParameters());
        pcActionResponse.setDeletedRenderParameters(deletedRenderParameters);
        Map<String, String[]> renderMap = getRenderParameterMap(key, value,
                pcActionResponse.getRenderParameters());
        pcActionResponse.setRenderParameters(renderMap);
        setMethodIsCalled = true;
    }

    /**
     * Sets a String array parameter for the render request.
     * <p>
     * These parameters will be accessible in all
     * sub-sequent render calls via the
     * <code>PortletRequest.getParameter</code> call until
     * a request is targeted to the portlet.
     * <p>
     * This method replaces all parameters with the given key.
     * <p>
     * The given parameter do not need to be encoded
     * prior to calling this method.
     *
     * @param  key     key of the render parameter
     * @param  values  values of the render parameter
     * @exception	java.lang.IllegalArgumentException
     *                      if key or value is <code>null</code>.
     * @exception java.lang.IllegalStateException
     *                    if the method is invoked after <code>sendRedirect</code> has been called.
     */
    public void setRenderParameter(String key, String[] values) {
        if (sendRedirectIsCalled) {
            throw new IllegalStateException("Illegal to set parameter after sendRedirect is called.");
        }

        Map<String, String[]> renderMap = getRenderParameterMap(key, values,
                pcActionResponse.getRenderParameters());
        pcActionResponse.setRenderParameters(renderMap);
        setMethodIsCalled = true;
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
        Map<String, String[]> map = pcActionResponse.getRenderParameters();
        if (map == null) {
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
        return PortletAppEngineUtils.getPortletMode(pcActionResponse.getChannelMode());
    }

    /**
     * Returns the currently set window state on this response.
     *
     * @since 2.0
     *
     * @return the window state, or <code>null</code> if none is set
     */
    public WindowState getWindowState() {
        return PortletAppEngineUtils.getWindowState(pcActionResponse.getChannelState());
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
					null, pcActionResponse.getDeletedRenderParameters());
			pcActionResponse.setDeletedRenderParameters(deletedRenderParameters);
		}
    }

    protected void setEventQueue(PortletEvent event) {
        // If the processAction is successful, this event queue will set in PortletContainerActionResponse
        Queue<PortletEvent> eventQueue = (Queue) actionRequest.getAttribute(PortletContainerConstants.EVENTS);
        if (eventQueue == null) {
            eventQueue = new ConcurrentLinkedQueue<PortletEvent>();
        }
        eventQueue.offer(event);

        actionRequest.setAttribute(PortletContainerConstants.EVENTS, eventQueue);
    }

    // Create a render URL with the currently set portlet mode, window state and render parameters 
    // and the current public render parameters.
    private String getCurrentRenderURL() {
        PortletURL renderURL = createRenderURL();
        try {
            if (getPortletMode() != null) {
                renderURL.setPortletMode(getPortletMode());
            }
            if (getWindowState() != null) {
                renderURL.setWindowState(getWindowState());
            }
            Map map = getRenderParameterMap();
            if (map != null) {
                renderURL.setParameters(map);
                // Add public render parameters
                Set<Map.Entry<String, String[]>> entries = map.entrySet();
                for(Map.Entry<String, String[]> mapEntry : entries) {
                    if (checkIfPublicRenderParameter(mapEntry.getKey())) {
                        renderURL.setParameter(mapEntry.getKey(), mapEntry.getValue());
                    }
                }
            }
        } catch (PortletModeException e) {
            logger.log(Level.WARNING, "PSPL_PAECSPPI0035", e);
        } catch (WindowStateException e) {
            logger.log(Level.WARNING, "PSPL_PAECSPPI0036", e);
        }
        return renderURL.toString();
    }
}
