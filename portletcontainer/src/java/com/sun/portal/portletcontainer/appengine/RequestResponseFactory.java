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


package com.sun.portal.portletcontainer.appengine;

import com.sun.portal.portletcontainer.common.PortletContainerActionRequest;
import com.sun.portal.portletcontainer.common.PortletContainerActionResponse;
import com.sun.portal.portletcontainer.common.PortletContainerEventRequest;
import com.sun.portal.portletcontainer.common.PortletContainerEventResponse;
import com.sun.portal.portletcontainer.common.PortletContainerRenderRequest;
import com.sun.portal.portletcontainer.common.PortletContainerRenderResponse;
import com.sun.portal.portletcontainer.common.PortletContainerResourceRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResourceResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * The request response factory provides access to the <code>ActionReqeust</code>,
 * <code>RenderRequest</code>, <code>ActionResponse</code>, and <code>ActionResponse</code>
 * objects for the portlet container.
 * <P>
 * @see <code>PortletAppEngineServlet</code>
 */
public interface RequestResponseFactory {
    
    // Key for RequestResponseFactory object in servlet context
    public static final String REQUEST_RESPONSE_FACTORY = "request_response_factory";
    
    /**
     * Returns a <code>ActionRequest</code> object based on the passed
     * servlet request.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcActionRequest     The <code>PortletContainerActionRequest</code>
     * @param pcActionResponse     The <code>PortletContainerActionRequest</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return a <code>ActionRequest</code> object
     */
    public ActionRequest getActionRequest( HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerActionRequest pcActionRequest,
            PortletContainerActionResponse pcActionResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor );
    
    /**
     * Returns a <code>ActionResponse</code> object.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcActionRequest     The <code>PortletContainerActionRequest</code>
     * @param pcActionResponse     The <code>PortletContainerActionResponse</code>
     * @param actionRequest     The <code>ActionRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return a <code>ActionResponse</code> object
     */
    public ActionResponse getActionResponse(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerActionRequest pcActionRequest,
            PortletContainerActionResponse pcActionResponse,
            ActionRequest actionRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor);
    
    /**
     * Releases the passed in action request.
     */
    public void releaseActionRequest( ActionRequest actionRequest );
    
    /**
     * Releases the passed in action response.
     */
    public void releaseActionResponse( ActionResponse actionResponse );
    
    /**
     * Returns a <code>RenderRequest</code> object based on the passed
     * servlet request.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcRenderRequest     The <code>PortletContainerRenderRequest</code>
     * @param pcRenderResponse     The <code>PortletContainerRenderResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return a <code>RenderRequest</code> object
     */
    public RenderRequest getRenderRequest( HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerRenderRequest pcRenderRequest,
            PortletContainerRenderResponse pcRenderResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor );
    /**
     * Returns a <code>RenderResponse</code> object.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcRenderRequest     The <code>PortletContainerRenderRequest</code>
     * @param pcRenderResponse     The <code>PortletContainerRenderResponse</code>
     * @param renderRequest     The <code>RenderRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * @param stringWriter The <code>StringWriter</code> that will be used as the
     * writer when <code>RenderResponse.getWriter()</code> is called.
     * @param outputStream     The <code>StringServletOutputStream</code>
     *
     * @return a <code>RenderResponse</code> object
     */
    public RenderResponse getRenderResponse(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerRenderRequest pcRenderRequest,             
            PortletContainerRenderResponse pcRenderResponse,
            RenderRequest renderRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor,
            StringWriter stringWriter,
            StringServletOutputStream outputStream);
    
    /**
     * Releases the passed in render request.
     */
    public void releaseRenderRequest( RenderRequest renderRequest );
    
    /**
     * Releases the passed in portlet response.
     */
    public void releaseRenderResponse( RenderResponse renderResponse );
    
    /**
     * Returns a <code>EventRequest</code> object based on the passed
     * servlet request.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcEventRequest     The <code>PortletContainerEventRequest</code>
     * @param pcEventResponse     The <code>PortletContainerEventResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return a <code>EventRequest</code> object
     */
    public EventRequest getEventRequest( HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerEventRequest pcEventRequest,
            PortletContainerEventResponse pcEventResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor);
    
    /**
     * Returns a <code>EventResponse</code> object.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcEventRequest     The <code>PortletContainerEventRequest</code>
     * @param pcEventResponse     The <code>PortletContainerEventResponse</code>
     * @param eventRequest     The <code>EventRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return a <code>EventResponse</code> object
     */
    public EventResponse getEventResponse(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerEventRequest pcEventRequest,             
            PortletContainerEventResponse pcEventResponse,             
            EventRequest eventRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor);
    
    /**
     * Releases the passed in action request.
     */
    public void releaseEventRequest( EventRequest eventRequest );
    
    /**
     * Releases the passed in action response.
     */
    public void releaseEventResponse( EventResponse eventResponse );
    
    
     /**
     * Returns a <code>ResourceRequest</code> object based on the passed
     * servlet request.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcResourceRequest     The <code>PortletContainerResourceRequest</code>
     * @param pcResourceResponse    The <code>PortletContainerResourceResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext      The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return a <code>ResourceRequest</code> object
     */
    public ResourceRequest getResourceRequest( HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerResourceRequest pcResourceRequest,
            PortletContainerResourceResponse pcResourceResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor );
    
    /**
     * Returns a <code>ResourceResponse</code> object.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcResourceRequest     The <code>PortletContainerResourceRequest</code>
     * @param pcResourceResponse     The <code>PortletContainerResourceResponse</code>
     * @param resourceRequest     The <code>ResourceRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * @param stringWriter The <code>StringWriter</code> that will be used as the
     * writer when <code>ResourceResponse.getWriter()</code> is called.
     * @param outputStream     The <code>StringServletOutputStream</code>
     *
     * @return a <code>ResourceResponse</code> object
     */
    public ResourceResponse getResourceResponse(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerResourceRequest pcResourceRequest,             
            PortletContainerResourceResponse pcResourceResponse,             
            ResourceRequest resourceRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor,
            StringWriter stringWriter,
            StringServletOutputStream outputStream);
    
    /**
     * Releases the passed in resource request.
     * 
     * @param resourceRequest <CODE>ResourceRequest</CODE>
     * 
     */
    public void releaseResourceRequest( ResourceRequest resourceRequest );
    
    /**
     * Releases the passed in resource response.
     * 
     * @param resourceResponse <CODE>ResourceResponse</CODE>
     */
    public void releaseResourceResponse( ResourceResponse resourceResponse );
    
}
