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
import com.sun.portal.portletcontainer.appengine.PortletAppEngineUtils;
import com.sun.portal.portletcontainer.common.PortletActions;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.PortletRequest;
import javax.portlet.WindowStateException;
import javax.portlet.PortletModeException;
import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import javax.xml.namespace.QName;

/**
 * The portlet URLs are generated from the PortletResponse using the
 * createURL() method. The portlet URLs are created in case the
 * portlet wants to generates links in its content or form to point
 * back to itself. This class provide the environment for the portlets
 * to create a URL.
 *
 * The created URL using the desktop URL that passed to the portlet
 * container, and set the request parameter "action" to "process" and
 * "provider" to the Portlet Window name, so that assures the
 * Portlet Window will be called to process the Portlet URL.
 *
 * @see PortletResponseImpl#createURL
 */
public class PortletURLImpl extends BaseURLImpl implements PortletURL {

    public PortletURLImpl(PortletRequest portletRequest,
            PortletContainerRequest pcRequest,
            PortletAppDescriptor portletAppDescriptor, 
            PortletDescriptor portletDescriptor, 
            String action) {

        super.init(portletRequest, pcRequest, portletAppDescriptor, portletDescriptor, action);
    }

    /**
     * Indicates the window state the portlet should be in, if this
     * portlet URL triggers a request.
     * <p>
     * A URL can not have more than one window state attached to it.
     * If more than one window state is set only the last one set
     * is attached to the URL.
     *
     * @param windowState
     *               the portlet window state
     * @exception <code>WindowStateException</code>
     *                   if the portlet cannot switch to this state,
     *                   because the portal does not support this state, the portlet has not
     *                   declared in its deployment descriptor that it supports this state, or the current
     *                   user is not allowed to switch to this state.
     *                   The <code>PortletRequest.isWindowStateAllowed()</code> method can be used
     *                   to check if the portlet can set a given window state.
     * @see PortletRequest#isWindowStateAllowed
     */
    public void setWindowState(WindowState windowState) throws WindowStateException {

        if (!getPortletRequest().isWindowStateAllowed(windowState)) {
            throw new WindowStateException("Invalid setting window state",
                    windowState);
        }
        getChannelURL().setWindowState(PortletAppEngineUtils.getChannelState(windowState));
    }

    /**
     * Indicates the portlet mode the portlet must be in, if this
     * portlet URL triggers a request.
     * <p>
     * A URL can not have more than one portlet mode attached to it.
     * If more than one portlet mode is set only the last one set
     * is attached to the URL.
     *
     * @param portletMode
     *               the portlet mode
     * @exception <code>PortletModeException</code>
     *                   if the portlet cannot switch to this mode,
     *                   because the portal does not support this mode, the portlet has not
     *                   declared in its deployment descriptor that it supports this mode for the current markup,
     *                   or the current user is not allowed to switch to this mode.
     *                   The <code>PortletRequest.isPortletModeAllowed()</code> method can be used
     *                   to check if the portlet can set a given portlet mode.
     * @see PortletRequest#isPortletModeAllowed
     */
    public void setPortletMode(PortletMode portletMode) throws PortletModeException {

        if (!getPortletRequest().isPortletModeAllowed(portletMode)) {
            throw new PortletModeException("Invalid setting portlet mode",
                    portletMode);
        }

        getChannelURL().setChannelMode(PortletAppEngineUtils.getChannelMode(portletMode));
    }

    /**
     * Returns the currently set portlet mode on this PortletURL.
     *
     * @since 2.0
     *
     * @return   the portlet mode, or <code>null</code> if none is set
     */
    public PortletMode getPortletMode() {
        ChannelMode portletWindowMode = getChannelURL().getChannelMode();
        PortletMode portletMode = null;

        if (portletWindowMode != null) {
            portletMode = PortletAppEngineUtils.getPortletMode(portletWindowMode);
        }

        return portletMode;
    }

    /**
     * Returns the currently set window state on this PortletURL.
     *
     * @since 2.0
     *
     * @return   the window state, or <code>null</code> if none is set
     */
    public WindowState getWindowState() {
        ChannelState newWindowState = getChannelURL().getWindowState();
        WindowState windowState = null;
        if (newWindowState != null) {
            windowState = PortletAppEngineUtils.getWindowState(newWindowState);
        }

        return windowState;
    }

  /**
	* Removes the specified public render parameter.
	* The name must reference a public render parameter defined
	* in the portlet deployment descriptor under the
	* <code>public-render-parameter</code> element with the
	* <code>identifier</code> mapping to the parameter name.
	* <p>
	* Note that calling this method on a PortletURL of type
	* Action URL does not have any effect.
	* 
	* @param name			a <code>String</code> specifying 
	*					the name of the public render parameter to be removed
	*
	* @exception  java.lang.IllegalArgumentException 
	*                            if name is <code>null</code>.
	* @since 2.0
	*/
    public void removePublicRenderParameter(String name) {
		if(!PortletActions.ACTION.equals(getAction())) {
			QName publicRenderParameterQName = getPortletAppDescriptor().getPublicRenderParameterQName(name);
			if(publicRenderParameterQName != null) {
				getChannelURL().setParameter(name, (String)null);
			}
		}
    }
}