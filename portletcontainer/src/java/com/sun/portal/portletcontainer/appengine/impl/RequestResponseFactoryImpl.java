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


package com.sun.portal.portletcontainer.appengine.impl;

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.portletcontainer.common.PortletContainerResourceRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResourceResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.portal.portletcontainer.appengine.RequestResponseFactory;
import com.sun.portal.portletcontainer.appengine.StringServletOutputStream;
import com.sun.portal.portletcontainer.common.PortletContainerActionRequest;
import com.sun.portal.portletcontainer.common.PortletContainerActionResponse;
import com.sun.portal.portletcontainer.common.PortletContainerEventRequest;
import com.sun.portal.portletcontainer.common.PortletContainerEventResponse;
import com.sun.portal.portletcontainer.common.PortletContainerRenderRequest;
import com.sun.portal.portletcontainer.common.PortletContainerRenderResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import com.sun.portal.portletcontainer.portlet.impl.ActionRequestPool;
import com.sun.portal.portletcontainer.portlet.impl.ActionResponsePool;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.portlet.impl.EventRequestPool;
import com.sun.portal.portletcontainer.portlet.impl.EventResponsePool;
import com.sun.portal.portletcontainer.portlet.impl.RenderRequestPool;
import com.sun.portal.portletcontainer.portlet.impl.RenderResponsePool;
import com.sun.portal.portletcontainer.portlet.impl.ResourceRequestPool;
import com.sun.portal.portletcontainer.portlet.impl.ResourceResponsePool;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;

/**
 * The request and response factory is responsible for generating the
 * Portlet request and response objects for the Portlet Application Servlet
 * Engine.
 * <P>
 * Object pooling is used in order to optimize the process and avoid messive
 * generation of objects.There are 4 pools the request and response factory
 * maintains:
 * <UL>
 *   <LI>Action request pool
 *   <LI>Action response pool
 *   <LI>Render request pool
 *   <LI>Render response pool
 * </UL>
 * <P>
 * The get**() methods are used to get a reusable object from the pools.
 * <P>
 * The release**() methods are used to release objects back to the pools.
 * Clients of this class are responsible for releasing the corresponding
 * objects by calling these methods.
 **/
public class RequestResponseFactoryImpl implements RequestResponseFactory {
    
    // Property names for req/res factory object in servlet context
    
    public static final String MIN_SIZE = "minSizeParam";
    public static final String MAX_SIZE = "maxSizeParam";
    public static final String PARTITION = "partitionParam";
    
    private ActionRequestPool actionRequestPool;
    private RenderRequestPool renderRequestPool;
    private ActionResponsePool actionResponsePool;
    private RenderResponsePool renderResponsePool;
    private EventRequestPool eventRequestPool;
    private EventResponsePool eventResponsePool;
    private ResourceRequestPool resourceRequestPool;
    private ResourceResponsePool resourceResponsePool;
    private ServletContext servletContext;
    
    private static Logger logger = ContainerLogger.getLogger(RequestResponseFactoryImpl.class, "PAELogMessages");
    
    //
    // Request and response pools are initialized to size 0.
    // items are added to to the pool as they are used
    // and returned. The pools are allowed to expand
    // to a very large size.
    //
    // We always let the pools offer instances beyond the
    // max pool size.
    //
    static private final int DEFAULT_MIN_SIZE = 0;
    static private final int DEFAULT_MAX_SIZE = 200;
    static private final int DEFAULT_PARTITION_SIZE = 10;
    
    static private final int ACTION_REQUEST = 1;
    static private final int RENDER_REQUEST = 2;
    static private final int ACTION_RESPONSE = 3;
    static private final int RENDER_RESPONSE = 4;
    static private final int EVENT_REQUEST = 7;
    static private final int EVENT_RESPONSE = 8;
    static private final int RESOURCE_REQUEST = 9;
    static private final int RESOURCE_RESPONSE = 10;
    
    static private final String ACTION_REQUEST_POOL = "actionRequestPool";
    static private final String ACTION_RESPONSE_POOL = "actionResponsePool";
    static private final String RENDER_REQUEST_POOL = "renderRequestPool";
    static private final String RENDER_RESPONSE_POOL = "renderResponsePool";
    static private final String RESOURCE_REQUEST_POOL = "resourceRequestPool";
    static private final String RESOURCE_RESPONSE_POOL = "resourceResponsePool";
    static private final String EVENT_REQUEST_POOL = "eventRequestPool";
    static private final String EVENT_RESPONSE_POOL = "eventResponsePool";
    
    /**
     * Initializes the global variables.
     *
     * @param context The servlet context
     */
    public RequestResponseFactoryImpl( ServletContext context ) {
        servletContext = context;
        String minParam = context.getInitParameter(REQUEST_RESPONSE_FACTORY + "." + MIN_SIZE);
        int min;
        if(minParam != null) {
            min = Integer.parseInt(minParam.trim());
        } else {
            min = DEFAULT_MIN_SIZE;
        }
        
        String maxParam = context.getInitParameter(REQUEST_RESPONSE_FACTORY + "." + MAX_SIZE);
        int max;
        if(maxParam != null) {
            max = Integer.parseInt(maxParam.trim());
        } else {
            max = DEFAULT_MAX_SIZE;
        }
        
        String partitionParam = context.getInitParameter(REQUEST_RESPONSE_FACTORY + "." + PARTITION);
        int partition;
        if(partitionParam != null) {
            partition = Integer.parseInt(partitionParam.trim());
        } else {
            partition = DEFAULT_PARTITION_SIZE;
        }
        
        logger.log(Level.FINEST, "PSPL_PAECSPPAI0001",min);
        logger.log(Level.FINEST, "PSPL_PAECSPPAI0002",max);
        logger.log(Level.FINEST, "PSPL_PAECSPPAI0003",partition);
        initRequestResponsePool(context,
                min,
                max,
                partition,
                ACTION_REQUEST);
        initRequestResponsePool(context,
                min,
                max,
                partition,
                ACTION_RESPONSE);
        initRequestResponsePool(context,
                min,
                max,
                partition,
                RENDER_REQUEST);
        initRequestResponsePool(context,
                min,
                max,
                partition,
                RENDER_RESPONSE);
        initRequestResponsePool(context,
                min,
                max,
                partition,
                RESOURCE_REQUEST);
        initRequestResponsePool(context,
                min,
                max,
                partition,
                RESOURCE_RESPONSE);
        initRequestResponsePool(context,
                min,
                max,
                partition,
                EVENT_REQUEST);
        initRequestResponsePool(context,
                min,
                max,
                partition,
                EVENT_RESPONSE);
        context.setAttribute(ACTION_REQUEST_POOL, actionRequestPool);
        context.setAttribute(ACTION_RESPONSE_POOL, actionResponsePool);
        context.setAttribute(RENDER_REQUEST_POOL, renderRequestPool);
        context.setAttribute(RENDER_RESPONSE_POOL, renderResponsePool);
        context.setAttribute(RESOURCE_REQUEST_POOL, resourceRequestPool);
        context.setAttribute(RESOURCE_RESPONSE_POOL, resourceResponsePool);
        context.setAttribute(EVENT_REQUEST_POOL, eventRequestPool);
        context.setAttribute(EVENT_RESPONSE_POOL, eventResponsePool);
    }
    
    /**
     * Gets a reusable action request object from the pool.
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcActionRequest     The <code>PortletContainerActionRequest</code>
     * @param pcActionResponse     The <code>PortletContainerActionRequest</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return <code>ActionRequest</code>
     */
    public ActionRequest getActionRequest( HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerActionRequest pcActionRequest,
            PortletContainerActionResponse pcActionResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor) {
        if(actionRequestPool == null) {
            actionRequestPool = (ActionRequestPool)servletContext.getAttribute(ACTION_REQUEST_POOL);
        }
        return actionRequestPool.obtainObject(request, response,
                pcActionRequest,
                pcActionResponse,
                portletContext,
                portalContext,
                portletDescriptor);
    }
    
    /**
     * Gets a reusable render request object from the pool.
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcRenderRequest     The <code>PortletContainerRenderRequest</code>
     * @param pcRenderResponse     The <code>PortletContainerRenderResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return <code>RenderRequest</code>
     */
    public RenderRequest getRenderRequest( HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerRenderRequest pcRenderRequest,
            PortletContainerRenderResponse pcRenderResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor) {
        if(renderRequestPool == null) {
            renderRequestPool = (RenderRequestPool)servletContext.getAttribute(RENDER_REQUEST_POOL);
        }
        return renderRequestPool.obtainObject(request, response,
                pcRenderRequest,
                pcRenderResponse,
                portletContext,
                portalContext,
                portletDescriptor);
    }
    
    /**
     * Gets a reusable action response object from the pool.
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcActionRequest     The <code>PortletContainerActionRequest</code>
     * @param pcActionResponse     The <code>PortletContainerActionResponse</code>
     * @param actionRequest     The <code>ActionRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return <code>ActionResponse</code>
     */
    public ActionResponse getActionResponse(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerActionRequest pcActionRequest,
            PortletContainerActionResponse pcActionResponse,
            ActionRequest actionRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor) {
        if(actionResponsePool == null) {
            actionResponsePool = (ActionResponsePool)servletContext.getAttribute(ACTION_RESPONSE_POOL);
        }
        return actionResponsePool.obtainObject(request, response, pcActionRequest, 
                pcActionResponse, actionRequest, portletAppDescriptor, portletDescriptor);
    }
    
    /**
     * Gets a reusable render response object from the pool.
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcRenderRequest     The <code>PortletContainerRenderRequest</code>
     * @param pcRenderResponse     The <code>PortletContainerRenderResponse</code>
     * @param renderRequest     The <code>RenderRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * @param stringWriter The <code>StringWriter</code> that will be used as the
     * writer when <code>ResourceResponse.getWriter()</code> is called.
     * @param outputStream     The <code>StringServletOutputStream</code>
     *
     * @return <code>RenderResponse</code>
     */
    public RenderResponse getRenderResponse(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerRenderRequest pcRenderRequest,
            PortletContainerRenderResponse pcRenderResponse,
            RenderRequest renderRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor,
            StringWriter stringWriter,
            StringServletOutputStream outputStream) {
        if(renderResponsePool == null) {
            renderResponsePool = (RenderResponsePool)servletContext.getAttribute(RENDER_RESPONSE_POOL);
        }
        return
                renderResponsePool.obtainObject(request, response,
                pcRenderRequest, pcRenderResponse,
                renderRequest, portletAppDescriptor, portletDescriptor, stringWriter, outputStream);
    }
    
    /**
     * Releases the action request object back to the pool.
     *
     * @param actionRequest The <code>ActionRequest</code>
     */
    public void releaseActionRequest( ActionRequest actionRequest ) {
        actionRequestPool.releaseObject( actionRequest );
    }
    
    /**
     * Releases the render request object back to the pool.
     *
     * @param renderRequest The <code>RenderRequest</code>
     */
    public void releaseRenderRequest( RenderRequest renderRequest ) {
        renderRequestPool.releaseObject( renderRequest );
    }
    
    /**
     * Releases the action response object back to the pool.
     *
     * @param actionResponse The <code>ActionResponse</code>
     */
    public void releaseActionResponse( ActionResponse actionResponse ) {
        actionResponsePool.releaseObject( actionResponse );
    }
    
    /**
     * Releases the render response object back to the pool.
     *
     * @param renderResponse The <code>RenderResponse</code>
     */
    public void releaseRenderResponse( RenderResponse renderResponse ) {
        renderResponsePool.releaseObject( renderResponse );
    }
    
    /**
     * Gets a reusable event request object from the pool.
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcEventRequest     The <code>PortletContainerEventRequest</code>
     * @param pcEventResponse     The <code>PortletContainerEventResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return <code>EventRequest</code>
     */
    public EventRequest getEventRequest(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerEventRequest pcEventRequest,
            PortletContainerEventResponse pcEventResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor) {
        if(eventRequestPool == null) {
            eventRequestPool = (EventRequestPool)servletContext.getAttribute(EVENT_REQUEST_POOL);
        }
        return eventRequestPool.obtainObject(request, response, 
                pcEventRequest,
                pcEventResponse,
                portletContext,
                portalContext,
                portletDescriptor);
    }
    
    /**
     * Gets a reusable event response object from the pool.
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcEventRequest     The <code>PortletContainerEventRequest</code>
     * @param pcEventResponse     The <code>PortletContainerEventResponse</code>
     * @param eventRequest     The <code>EventRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return <code>EventResponse</code>
     */
    public EventResponse getEventResponse(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerEventRequest pcEventRequest,             
            PortletContainerEventResponse pcEventResponse, 
            EventRequest eventRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor) {
        if(eventResponsePool == null) {
            eventResponsePool = (EventResponsePool)servletContext.getAttribute(EVENT_RESPONSE_POOL);
        }
        return eventResponsePool.obtainObject(request, response, pcEventRequest, 
                pcEventResponse, eventRequest, portletAppDescriptor, portletDescriptor);
        
    }
    
    /**
     * Releases the event request object back to the pool.
     *
     * @param eventRequest The <code>EventRequest</code>
     */
    public void releaseEventRequest(EventRequest eventRequest) {
        eventRequestPool.releaseObject(eventRequest);
    }
    
    /**
     * Releases the event response object back to the pool.
     *
     * @param eventResponse The <code>EventResponse</code>
     */
    public void releaseEventResponse(EventResponse eventResponse) {
        eventResponsePool.releaseObject(eventResponse);
    }
    
     /**
     * Gets a reusable resource request object from the pool.
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcResourceRequest     The <code>PortletContainerResourceRequest</code>
     * @param pcResourceResponse    The <code>PortletContainerResourceResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext      The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     *
     * @return <code>ResourceRequest</code>
     */
    public ResourceRequest getResourceRequest(HttpServletRequest request, 
            HttpServletResponse response, 
            PortletContainerResourceRequest pcResourceRequest, 
            PortletContainerResourceResponse pcResourceResponse, 
            PortletContext portletContext, PortalContext portalContext, 
            PortletDescriptor portletDescriptor) {
         if(resourceRequestPool == null) {
            resourceRequestPool = (ResourceRequestPool)servletContext.getAttribute(RESOURCE_REQUEST_POOL);
        }
        return resourceRequestPool.obtainObject(request, response,
                pcResourceRequest,
                pcResourceResponse,
                portletContext,
                portalContext,
                portletDescriptor);
    }
    
     /**
     * Gets a reusable resource response object from the pool.
     *
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
     * @return <code>ResourceResponse</code>
     */

    public ResourceResponse getResourceResponse(HttpServletRequest request, 
            HttpServletResponse response, 
            PortletContainerResourceRequest pcResourceRequest, 
            PortletContainerResourceResponse pcResourceResponse, 
            ResourceRequest resourceRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor,
            StringWriter stringWriter, 
            StringServletOutputStream outputStream) {
         if(resourceResponsePool == null) {
            resourceResponsePool = (ResourceResponsePool)servletContext.getAttribute(RESOURCE_RESPONSE_POOL);
        }
        return resourceResponsePool.obtainObject(request, response,
                pcResourceRequest, pcResourceResponse,
                resourceRequest, portletAppDescriptor, portletDescriptor, stringWriter, outputStream);
    }

     /**
     * Releases the resource request object back to the pool.
     *
     * @param resourceRequest The <code>ResourceRequest</code>
     */
    public void releaseResourceRequest(ResourceRequest resourceRequest) {
        resourceRequestPool.releaseObject( resourceRequest );
    }

     /**
     * Releases the resource response object back to the pool.
     *
     * @param resourceResponse The <code>ResourceResponse</code>
     */
    public void releaseResourceResponse(ResourceResponse resourceResponse) {
        resourceResponsePool.releaseObject(resourceResponse);
    }
    /**
     * Initializes the portlet request pool.
     */
    private void initRequestResponsePool(ServletContext context, int minSize,
            int maxSize, int partition,
            int type) {
        
        if ( minSize < 0 ) {
            logger.log(Level.FINER, "PSPL_PAECSPPAI0004",new Integer(DEFAULT_MIN_SIZE));
            minSize = DEFAULT_MIN_SIZE;
        }
        if ( maxSize < 0 ) {
            logger.log(Level.FINER, "PSPL_PAECSPPAI0005",new Integer(DEFAULT_MAX_SIZE));
            maxSize = DEFAULT_MAX_SIZE;
        }
        if ( partition < 0 ) {
            logger.log(Level.FINER, "PSPL_PAECSPPAI0006",new Integer(DEFAULT_PARTITION_SIZE));
            partition = DEFAULT_PARTITION_SIZE;
        }
        if ( minSize > maxSize ) {
            logger.log(Level.FINER, "PSPL_PAECSPPAI0007",new Integer(DEFAULT_MAX_SIZE));
            minSize = DEFAULT_MAX_SIZE;
        }
        if ( partition > maxSize ) {
            logger.log(Level.FINER, "PSPL_PAECSPPAI0008",new Integer(DEFAULT_MAX_SIZE));
            partition = DEFAULT_MAX_SIZE;
        }
        
        switch ( type ) {
            
            case ACTION_REQUEST:
                actionRequestPool = new ActionRequestPool( context,
                        minSize,
                        maxSize,
                        true,
                        partition);
                break;
                
            case RENDER_REQUEST:
                renderRequestPool = new RenderRequestPool( context,
                        minSize,
                        maxSize,
                        true,
                        partition);
                break;
                
            case ACTION_RESPONSE:
                actionResponsePool = new ActionResponsePool( minSize,
                        maxSize,
                        true,
                        partition);
                break;
                
            case RENDER_RESPONSE:
                renderResponsePool = new RenderResponsePool( minSize,
                        maxSize,
                        true,
                        partition);
                break;
            case EVENT_REQUEST:
                eventRequestPool = new EventRequestPool( context,
                        minSize,
                        maxSize,
                        true,
                        partition);
                break;
            case EVENT_RESPONSE:
                eventResponsePool = new EventResponsePool( minSize,
                        maxSize,
                        true,
                        partition);
                
                break;
            case RESOURCE_RESPONSE:
                resourceResponsePool = new ResourceResponsePool(minSize,
                        maxSize,
                        true,
                        partition);
                break;
            case RESOURCE_REQUEST:
                resourceRequestPool = new ResourceRequestPool(context,minSize,
                        maxSize,
                        true,
                        partition);
                break;
                    
        }
        
    }
    
    /**
     * Clears the pool objects.
     */
    public void destroy() {
        actionRequestPool = null;
        renderRequestPool = null;
        actionResponsePool = null;
        renderResponsePool = null;
        eventRequestPool = null;
        eventResponsePool = null;
        resourceRequestPool = null;
        resourceResponsePool = null;
    }
}
