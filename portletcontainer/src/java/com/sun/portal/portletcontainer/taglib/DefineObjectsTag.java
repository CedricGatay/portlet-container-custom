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


package com.sun.portal.portletcontainer.taglib;

import com.sun.portal.portletcontainer.portlet.impl.PortletRequestConstants;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * This class implements the DefineObject tag. It makes available
 * to the JSP variables : actionRequest, actionResponse, renderRequest,
 * renderResponse, eventRequest, eventResponse, resourceRequest, resourceResponse,
 * portletConfig, portletSession, portletSessionScope, portletPreferences
 * and portletPreferencesValues
 *
 * This class applicable for Portlet v2.0
 */
public class DefineObjectsTag extends TagSupport {

    @Override
    public int doStartTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        PortletRequest portletRequest = (PortletRequest) request.getAttribute(
                PortletRequestConstants.PORTLET_REQUEST_ATTRIBUTE);
        PortletResponse portletResponse = (PortletResponse) request.getAttribute(
                PortletRequestConstants.PORTLET_RESPONSE_ATTRIBUTE);
        if (portletRequest instanceof RenderRequest) {
            pageContext.setAttribute(PortletTaglibConstants.RENDER_REQUEST_VAR,
                    (RenderRequest) portletRequest);
            pageContext.setAttribute(PortletTaglibConstants.RENDER_RESPONSE_VAR,
                    (RenderResponse) portletResponse);
        } else if (portletRequest instanceof ActionRequest) {
            pageContext.setAttribute(PortletTaglibConstants.ACTION_REQUEST_VAR,
                    (ActionRequest) portletRequest);
            pageContext.setAttribute(PortletTaglibConstants.ACTION_RESPONSE_VAR,
                    (ActionResponse) portletResponse);
        } else if (portletRequest instanceof EventRequest) {
            pageContext.setAttribute(PortletTaglibConstants.EVENT_REQUEST_VAR,
                    (EventRequest) portletRequest);
            pageContext.setAttribute(PortletTaglibConstants.EVENT_RESPONSE_VAR,
                    (EventResponse) portletResponse);
        } else if (portletRequest instanceof ResourceRequest) {
            pageContext.setAttribute(PortletTaglibConstants.RESOURCE_REQUEST_VAR,
                    (ResourceRequest) portletRequest);
            pageContext.setAttribute(PortletTaglibConstants.RESOURCE_RESPONSE_VAR,
                    (ResourceResponse) portletResponse);
        }

        PortletConfig pConfig = (PortletConfig) request.getAttribute(
                PortletRequestConstants.PORTLET_CONFIG_ATTRIBUTE);

        pageContext.setAttribute(PortletTaglibConstants.PORTLET_CONFIG_VAR,
                pConfig);

        pageContext.setAttribute(PortletTaglibConstants.PORTLET_NAME_VAR,
                pConfig.getPortletName());

        PortletSession portletSession = portletRequest.getPortletSession(false);
        pageContext.setAttribute(PortletTaglibConstants.PORTLET_SESSION_VAR,
                portletSession);

        if (portletSession != null) {
            pageContext.setAttribute(PortletTaglibConstants.PORTLET_SESSION_SCOPE_VAR,
                    portletSession.getAttributeMap());
        }

        PortletPreferences portletPreferences = portletRequest.getPreferences();
        pageContext.setAttribute(PortletTaglibConstants.PORTLET_PREFERENCES_VAR,
                portletPreferences);
        pageContext.setAttribute(PortletTaglibConstants.PORTLET_PREFERENCES_VALUES_VAR,
                portletPreferences.getMap());
        return SKIP_BODY;
    }
}