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


package com.sun.portal.portletcontainer.impl;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;
import com.sun.portal.container.ChannelURLFactory;
import com.sun.portal.container.ChannelURLType;
import com.sun.portal.container.ContainerException;
import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.ContainerResponse;
import com.sun.portal.container.ContainerType;
import com.sun.portal.container.ContainerUtil;
import com.sun.portal.container.ContentException;
import com.sun.portal.container.EntityID;
import com.sun.portal.container.ExecuteActionRequest;
import com.sun.portal.container.ExecuteActionResponse;
import com.sun.portal.container.ExecuteEventRequest;
import com.sun.portal.container.ExecuteEventResponse;
import com.sun.portal.container.GetMarkupRequest;
import com.sun.portal.container.GetMarkupResponse;
import com.sun.portal.container.GetResourceRequest;
import com.sun.portal.container.GetResourceResponse;
import com.sun.portal.container.PortletEvent;
import com.sun.portal.container.PortletWindowContext;
import com.sun.portal.container.PortletWindowContextAbstractFactory;
import com.sun.portal.container.PortletWindowContextException;
import com.sun.portal.container.PortletWindowContextFactory;
import com.sun.portal.container.WindowRequestReader;
import com.sun.portal.container.impl.AbstractContainer;
import com.sun.portal.container.service.PublicRenderParameterHolder;
import com.sun.portal.container.service.ServiceException;
import com.sun.portal.container.service.caching.ClientCachingService;
import com.sun.portal.container.service.caching.CachingService;
import com.sun.portal.container.service.coordination.CoordinationService;
import com.sun.portal.container.service.coordination.CoordinationSubscriber;
import com.sun.portal.container.service.caching.PortletCacheEntry;
import com.sun.portal.portletcontainer.common.PortletActions;
import com.sun.portal.container.service.caching.impl.PortletCacheType;
import com.sun.portal.portletcontainer.common.PortletContainerActionRequest;
import com.sun.portal.portletcontainer.common.PortletContainerActionResponse;
import com.sun.portal.portletcontainer.common.PortletContainerConstants;
import com.sun.portal.portletcontainer.common.PortletContainerErrorCode;
import com.sun.portal.portletcontainer.common.PortletContainerRenderRequest;
import com.sun.portal.portletcontainer.common.PortletContainerRenderResponse;
import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResponse;
import com.sun.portal.portletcontainer.common.PortletContainerEventRequest;
import com.sun.portal.portletcontainer.common.PortletContainerEventResponse;
import com.sun.portal.container.service.caching.ResourceCacheEntry;
import com.sun.portal.container.service.Service;
import com.sun.portal.container.service.coordination.ContainerEventService;
import com.sun.portal.container.service.coordination.ContainerEventSubscriber;
import com.sun.portal.container.service.policy.ContainerEventPolicy;
import com.sun.portal.portletcontainer.common.PortletContainerResourceRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResourceResponse;
import com.sun.portal.portletcontainer.common.PortletContainerUtil;
import com.sun.portal.portletcontainer.common.descriptor.DeploymentExtensionDescriptor;
import java.io.FileNotFoundException;
import java.io.Serializable;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

/**
 * The <code>PortletContainer</code> class provides an implementation for the
 * <code>Container</code> interface according to JSR168/286 specification.  In
 * this implementation, <code>PortletContainer</code> assembles the requests
 * into <code>PortletContainerActionRequest</code> or
 * <code>PortletContainerRenderRequest</code> or <code>PortletContainerEventRequest</code>
 * or <code>PortletContainerResourceRequest</code> and dispatch them to <code>
 * PortletAppEngineServlet</code> for processing.  The responses from <code>
 * PortletAppEngineServlet</code> come back as
 * <code>PortletContainerActionResponse</code> or
 * <code>PortletContainerRenderResponse</code> or
 * <code>PortletContainerEventResponse</code> or
 * <code>PortletContainerResourceResponse</code>.
 * <code>PortletContainer</code> also works with the <code>
 * CacheManager</code> to cache and retrieve contents when appropriate.
 */
public class PortletContainer extends AbstractContainer implements CoordinationSubscriber, ContainerEventSubscriber {

    private CachingService cacheManager;
    private CoordinationService coordinationService;
    private ClientCachingService clientCachingService;
    private ServletContext servletContext;
    private boolean serializeAll;
    private static List<String> actionList;
    private static List<String> renderList;
    private static List<String> eventList;
    private static List<String> resourceList;
    private static final String PAE_NAME = "/servlet/PortletAppEngineServlet";
    private static final String DISPATCHER_STATE = "javax.portlet.pc.dispatcher_state";
    private static final String FIRST_THREAD = "javax.portlet.pc.first_thread";
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PortletContainer.class, "PCLogMessages");
    static {
        actionList = new ArrayList<String>();
        renderList = new ArrayList<String>();
        eventList = new ArrayList<String>();
        resourceList = new ArrayList<String>();

        actionList.add(PortletActions.ACTION);
        renderList.add(PortletActions.RENDER);
        eventList.add(PortletActions.EVENT);
        resourceList.add(PortletActions.RESOURCE);
    }

    void init(ServletContext servletContext) {
        // Obtain the standard services and have the handle locally
        coordinationService = (CoordinationService) getService(Service.COORDINATION_SERVICE);
        coordinationService.registerSubscriber(ContainerType.PORTLET_CONTAINER, this);
        cacheManager = (CachingService) getService(Service.CACHING_SERVICE);
        clientCachingService = (ClientCachingService) getService(Service.CLIENT_CACHING_SERVICE);
        ContainerEventService containerEventService = (ContainerEventService) getService(Service.CONTAINER_EVENT_SERVICE);
        containerEventService.registerSubscriber(this);
        this.servletContext = servletContext;
        // Since PolicyService is optional it is not obtained during init
    }

    public void getMarkup(GetMarkupRequest getMarkUpRequest, GetMarkupResponse getMarkUpResponse) throws ContainerException, ContentException {
        EntityID entityID = getMarkUpRequest.getEntityID();
        Map<String, String[]> renderParameters = getRenderParameters(getMarkUpRequest, entityID);
        getMarkUpRequest.setRenderParameters(renderParameters);

        //get the saved scopes attributes
        String scopeID = getActionScopeID(renderParameters);
        if(scopeID != null) {
            Map<String, Serializable> scopedAttributes = getScopedAttributes(getMarkUpRequest, entityID);
            getMarkUpRequest.setScopedAttributes(scopeID, scopedAttributes);
        }
        PortletContainerRenderResponse pcRenderResponse = null;

        boolean cachingEnabled = getCachingService().isCachingEnabled(getMarkUpRequest);
        boolean isInViewMode = (getMarkUpRequest.getChannelMode()).equals(ChannelMode.VIEW);

        // Determine whether we can use the cached content.
        // Content will be left as null if the cached content cannot be
        // used or not available.
        PortletCacheEntry portletCacheEntry = null;

        if (cachingEnabled && isInViewMode) {
            if (getMarkUpRequest.getIsTarget()) {
                getCachingService().removeCachedPortlet(getMarkUpRequest);
            } else {
                portletCacheEntry = getCachingService().getCachedPortlet(getMarkUpRequest);
            }
        }

        try {
            if (portletCacheEntry == null || (portletCacheEntry != null && portletCacheEntry.needsValidation())) {
                HttpServletRequest request = getMarkUpRequest.getHttpServletRequest();
                PortletContainerErrorCode errorCode = null;
                String title = getTitle(getMarkUpRequest, entityID);
                if (title != null) {
                    request.setAttribute(PortletContainerConstants.PORTLET_TITLE_IN_REGISTRY + entityID, title);
                }

                if (portletCacheEntry != null) {
                    // Set the ETag in the request so that the Portlet can access it.
                    getMarkUpRequest.setETag(portletCacheEntry.getETag());
                }
                pcRenderResponse = (PortletContainerRenderResponse) invokePAE(PortletActions.RENDER, getMarkUpRequest, getMarkUpResponse);
                errorCode = pcRenderResponse.getErrorCode();

                if (errorCode.equals(PortletContainerErrorCode.NO_ERROR)) {
                    // cache content
                    if (cachingEnabled && isInViewMode) {
                        int expirationCache = pcRenderResponse.getCacheControl().getExpirationTime();
                        String eTag = pcRenderResponse.getCacheControl().getETag();
                        // Check if the markup needs to be read from the Response.
                        boolean readMarkup = true;
                        if (portletCacheEntry != null && portletCacheEntry.needsValidation()) {
                            boolean useCachedContent = pcRenderResponse.getCacheControl().useCachedContent();
                            if (useCachedContent) {
                                // Set the new expiration time
                                portletCacheEntry.setExpirationTime(expirationCache);
                                // If the portlet has set a new ETag update that.
                                if (eTag != null) {
                                    portletCacheEntry.setETag(eTag);
                                }
                                getCachingService().putCachedPortlet(getMarkUpRequest, portletCacheEntry, pcRenderResponse.getCacheControl().isPublicScope());
                                // markup need not be read, as the cached content is reused.
                                readMarkup = false;
                            }
                        }

                        if (readMarkup) {
                            int cacheType = (eTag != null) ? PortletCacheType.TYPE_VALIDATING : PortletCacheType.TYPE_EXPIRATION;

                            // check if the entry needs to be cached
                            if ((expirationCache > 0) || (expirationCache == -1)) {
                                portletCacheEntry = new PortletCacheEntry(
										cacheType, pcRenderResponse.getMarkup(),
										pcRenderResponse.getTitle(),
										pcRenderResponse.getElementProperties(),
										pcRenderResponse.getStringProperties(),
										expirationCache, eTag, getMarkUpRequest.getLocale().toString());
                                getCachingService().putCachedPortlet(
										getMarkUpRequest, portletCacheEntry,
										pcRenderResponse.getCacheControl().isPublicScope());
                            }
                        }
                    }
                } else {
					logPortletAppEngineException(entityID, pcRenderResponse.getException());
                    throw new ContentException("PortletContainer.getMarkup(): Exception thrown from render() of " + entityID, 
                        errorCode, pcRenderResponse.getException());
                }
            }
            if (portletCacheEntry != null) {
                getMarkUpResponse.setMarkup(portletCacheEntry.getCachedContent());
                getMarkUpResponse.setTitle(portletCacheEntry.getTitleResource());
				getMarkUpResponse.setElementProperties(portletCacheEntry.getElementProperties());
				getMarkUpResponse.setStringProperties(portletCacheEntry.getStringProperties());
            }
        } catch (ContentException e) {
            throw e;
        } catch (Exception e) {
            throw new ContainerException("PortletContainer.getMarkup(): ", e);
        }

		// Clear RenderURL parameters
		if(renderURLParameterCacheDisabled(entityID)) {
			Map<String, String[]> renderURLParameters = getRenderURLParameters(getMarkUpRequest, entityID);
			if(renderURLParameters != null && !renderURLParameters.isEmpty()) {
				Set<String> keys = renderURLParameters.keySet();
				for(String key : keys) {
					renderParameters.remove(key);
				}
				setRenderParameters(getMarkUpRequest, entityID, renderParameters);
			}
		}
    }

    public void executeAction(ExecuteActionRequest executeActionRequest, 
            ExecuteActionResponse executeActionResponse, 
            ChannelURLType channelURLType) throws ContainerException, ContentException {

        EntityID entityID = executeActionRequest.getEntityID();
        PortletContainerActionResponse pcActionResponse = null;

        // clear out the cache
        getCachingService().removeCachedPortlet(executeActionRequest);
        if (ChannelURLType.RENDER.equals(channelURLType)) {
            Map<String, String[]> urlParameters = null;
            WindowRequestReader windowRequestReader = executeActionRequest.getWindowRequestReader();
            if (windowRequestReader != null) {
                urlParameters = windowRequestReader.readParameterMap(executeActionRequest.getHttpServletRequest());
            }
            // Since it is the RenderUrl, no action is called, invoke the CoordinationService
            // to set the render parameters
            if (urlParameters != null) {
				// Save URL parameters if render-url-parameter-cache-disabled element is true as
				// these URL parameters will be used in getMarkUp to remove them from the
				// render parameters
				if(renderURLParameterCacheDisabled(entityID)) {
					setRenderURLParameters(executeActionRequest, entityID, urlParameters);
				}
				
				// Get the list of parameters to be deleted(i.e those value is "null")
				executeActionResponse.setDeletedRenderParameters(getDeletedParameters(urlParameters));
				// Get the parameters set on the URL(RenderURL) and merge(by overwriting) it with 
				// the public render parameters that may have been set for this portlet
				Map<String, String[]> publicParametersMap = getPublicParameters(executeActionRequest, executeActionRequest.getEntityID());
				Map<String, String[]> renderParameters = PortletContainerUtil.getMergedParameterMap(urlParameters, publicParametersMap, false);
                getCoordinationService().setRenderParameters(executeActionRequest, executeActionResponse, entityID, renderParameters);
            }

            //
            // mark the portlet as the target of the request
            // so that render kinda method can use it
            // to send it out to the container.
            setIsTarget(executeActionRequest, entityID, true);
        } else if (ChannelURLType.ACTION.equals(channelURLType)) {

            // Check if the action parameters is already set..
            // if set use it else get it from the Window Request Reader
            Map<String, String[]> actionParameters = executeActionRequest.getActionParameters();
            if(actionParameters == null) {
                WindowRequestReader windowRequestReader = executeActionRequest.getWindowRequestReader();
                if (windowRequestReader != null) {
                    actionParameters = windowRequestReader.readParameterMap(executeActionRequest.getHttpServletRequest());
                }
            }
			// Get the list of parameters to be deleted(i.e those value is "null")
			// This will be later used during render parameter processing
			List<String> deletedRenderParameters = getDeletedParameters(actionParameters);
			executeActionResponse.setDeletedRenderParameters(deletedRenderParameters);
            // Get only the public parameters from render parameters and 
            // merge(by appending) with the parameters set on this(Action)URL
            Map<String, String[]> publicParameters = getPublicParameters(executeActionRequest, entityID);
            Map<String, String[]> mergedParameters = PortletContainerUtil.getMergedParameterMap(actionParameters, publicParameters, true);
			// Remove the deleted parameters from the action parameters
			if (deletedRenderParameters != null) {
                for (String deletedRenderParameter : deletedRenderParameters) {
                    mergedParameters.remove(deletedRenderParameter);
                }
            }
            executeActionRequest.setActionParameters(mergedParameters);

            try {
                pcActionResponse = (PortletContainerActionResponse) invokePAE(PortletActions.ACTION, executeActionRequest, executeActionResponse);
                PortletContainerErrorCode errorCode = pcActionResponse.getErrorCode();
                if (!errorCode.equals(PortletContainerErrorCode.NO_ERROR)) {
					logPortletAppEngineException(entityID, pcActionResponse.getException());
                    throw new ContentException("PortletContainer.executeAction(): Exception thrown from processAction() of " + entityID, 
                        errorCode, pcActionResponse.getException());
                }
                // Save scoped attributes only if the scopeID is not null
                String scopeID = getActionScopeID(executeActionResponse.getRenderParameters());
                if(scopeID != null) {
                    this.setScopedAttributes(executeActionRequest, entityID, executeActionRequest.getScopedAttributes(scopeID));
                }
                // Process events(Portlet 2.0)
                Queue<PortletEvent> eventQueue = executeActionResponse.getEventQueue();
                List<EntityID> updatedPortlets = getCoordinationService().publishEvent(executeActionRequest, executeActionResponse, eventQueue);
                if(logger.isLoggable(Level.FINER)) {
                    logger.log(Level.FINER, "PSPL_PCCSPCPCI0013", updatedPortlets);
                }
				executeActionResponse.setEventUpdatedPortlets(updatedPortlets);
                // Set the render parameters for this portlet.
                // The render parameters set in the processAction will be available in render if there is
                // no processEvent in this portlet(i.e if this portlet does not process the event)
                // If this portlet processes any event, then it has to propagate the render parameters obtained
                // from processAction, by calling eventResponse.setEventParameters(eventRequest)
                if (executeActionResponse.getRedirectURL() == null) {
                    Map renderParametersFromAction = executeActionResponse.getRenderParameters();
                    // Check for render parameters from Event only if this portlet processes the event
                    Map renderParameters = null;
                    if (eventQueue != null && updatedPortlets.contains(entityID)) {
                        Map renderParametersFromEvent = getRenderParameters(executeActionRequest, executeActionRequest.getEntityID());
                        // If the render parameters from event is not empty, use it, 
                        // If the render parameters from event is empty, it means
                        // render parameters have not been propagated, so don't set it.
                        if (renderParametersFromEvent.isEmpty()) {
                            renderParameters = null;
                        } else {
                            renderParameters = renderParametersFromEvent;
                        }
                    } else {
                        // Since this portlet does not process any event, get the render parameters set in processAction
                        // and merge(by overwriting) it with the public render parameters that may have been set for this portlet
                        Map<String, String[]> publicParametersMap = getPublicParameters(executeActionRequest, executeActionRequest.getEntityID());
                        renderParameters = PortletContainerUtil.getMergedParameterMap(renderParametersFromAction, publicParametersMap, false);
                    }
                    getCoordinationService().setRenderParameters(executeActionRequest, executeActionResponse, entityID, renderParameters);
                }
	        } catch (ContentException e) {
		        throw e;
            } catch (Exception e) {
                throw new ContainerException("PortletContainer.executeAction(): ", e);
            }
        } 
    }

    public void getResources(GetResourceRequest getResourceRequest, 
            GetResourceResponse getResourceResponse) throws ContainerException, ContentException {
        EntityID entityID = getResourceRequest.getEntityID();

        PortletContainerResourceResponse pcResourceResponse = null;

        boolean cachingEnabled = getCachingService().isCachingEnabled(getResourceRequest);
        boolean isInViewMode = (getResourceRequest.getChannelMode()).equals(ChannelMode.VIEW);

        HttpServletRequest request = getResourceRequest.getHttpServletRequest();

        WindowRequestReader windowRequestReader = getResourceRequest.getWindowRequestReader();
        // Check if the resource parameters is already set..
        // if set use it else get it from the Window Request Reader
        Map<String, String[]> resourceParameters = getResourceRequest.getResourceParameters();
        if(resourceParameters == null && windowRequestReader != null) {
            resourceParameters = windowRequestReader.readParameterMap(request);
        }
		// Get only the public parameters from render parameters and 
		// merge(by appending) with the parameters set on this(Resource)URL
        Map<String, String[]> publicParameters = getRenderParameters(getResourceRequest, entityID);
        Map<String, String[]> mergedParameters = PortletContainerUtil.getMergedParameterMap(resourceParameters, publicParameters, true);
        getResourceRequest.setResourceParameters(mergedParameters);

        // Check if the resource ID is already set..
        // if set use it else get it from the Window Request Reader
        String resourceID = getResourceRequest.getResourceID();
        if (resourceID == null && windowRequestReader != null) {
            getResourceRequest.setResourceID(windowRequestReader.getResourceID(request));
        }

        // Check if the cache Level is already set..
        // if set use it else get it from the Window Request Reader
        String cacheLevel = getResourceRequest.getCacheLevel();
        if (cacheLevel == null && windowRequestReader != null) {
            getResourceRequest.setCacheLevel(windowRequestReader.getCacheLevel(request));
        }

        // Determine whether we can use the cached content.
        // Content will be left as null if the cached content cannot be
        // used or not available.
        ResourceCacheEntry resourceCacheEntry = null;
        boolean needsValidation = false;
        if (cachingEnabled && isInViewMode) {
            boolean hasCacheExpired = getClientCachingService().hasResourceCacheExpired(getResourceRequest, getResourceResponse);
            if (!hasCacheExpired) {
                // Return as the client caching service implemenation would have set the response headers.
                return;
            }
            // Cache has expired. If the entry is still present, then the cache needs to be validated.
            resourceCacheEntry = getClientCachingService().getCachedResource(getResourceRequest);
            if (resourceCacheEntry != null) {
                needsValidation = true;
            }
        }

        try {
            if (resourceCacheEntry == null || needsValidation) {
                PortletContainerErrorCode errorCode = null;
                if (needsValidation) {
                    // Set the ETag in the Request so that the Portlet can access it
                    getResourceRequest.setETag(resourceCacheEntry.getETag());
                }
                // get the saved scope attributes and set in the GetResourceRequest before calling the serveResource of the Portlet
                Map<String, String[]> currentResourceParameters = getResourceRequest.getResourceParameters();
                String scopeID = getActionScopeID(currentResourceParameters);
                if(scopeID != null) {
                    Map<String, Serializable> scopedAttributes = getScopedAttributes(getResourceRequest, entityID);
                    getResourceRequest.setScopedAttributes(scopeID, scopedAttributes);
                }
                pcResourceResponse = (PortletContainerResourceResponse) invokePAE(PortletActions.RESOURCE, getResourceRequest, getResourceResponse);
                errorCode = pcResourceResponse.getErrorCode();

                if (errorCode.equals(PortletContainerErrorCode.NO_ERROR)) {
                    if (cachingEnabled && isInViewMode) {
                        int expirationTime = pcResourceResponse.getCacheControl().getExpirationTime();
                        String eTag = pcResourceResponse.getCacheControl().getETag();
                        boolean isPublic = pcResourceResponse.getCacheControl().isPublicScope();

                        if (needsValidation) {
                            boolean useCachedContent = pcResourceResponse.getCacheControl().useCachedContent();
                            if (useCachedContent) {
                                resourceCacheEntry.setETag(eTag);
                                resourceCacheEntry.setExpirationTime(expirationTime);
                                getClientCachingService().updateAndReuseResourceCache(getResourceRequest, getResourceResponse, resourceCacheEntry, isPublic);
                                return;
                            }
                        }
                        // Check if caching is required
                        if (expirationTime > 0 || expirationTime == -1) {
                            resourceCacheEntry = new ResourceCacheEntry(expirationTime, eTag);
                            getClientCachingService().updateResourceCache(getResourceRequest, getResourceResponse, resourceCacheEntry, isPublic);
                        }
                    }
                } else {
					logPortletAppEngineException(entityID, pcResourceResponse.getException());
                    throw new ContentException("PortletContainer.getResource(): Exception thrown from serveResource() of " + entityID, 
                        errorCode, pcResourceResponse.getException());
                }
            }
        } catch (ContentException e) {
            throw e;
        } catch (Exception e) {
            throw new ContainerException("PortletContainer.getResource(): ", e);
        }
    }

    public Queue<PortletEvent> processEvent(ContainerRequest containerRequest, 
            ContainerResponse containerResponse, 
            Map<EntityID, QName> portletsProcessingEventQNames, 
            PortletEvent event) throws ContainerException, ContentException {
        EntityID publishingPortletEntityId = containerRequest.getEntityID();
        Map publishingRenderParameters = null;
        // Get the render parameters set on the processAction/processEvent of the publishing portlet
        if(containerResponse instanceof ExecuteActionResponse) {
            publishingRenderParameters = ((ExecuteActionResponse) containerResponse).getRenderParameters();
        } else if(containerResponse instanceof ExecuteEventResponse) {
            publishingRenderParameters = ((ExecuteEventResponse) containerResponse).getRenderParameters();
        } else {
            publishingRenderParameters = Collections.emptyMap();
        }
        
        // For each portlet in the Map, call the execute event
        Set<Map.Entry<EntityID, QName>> entrySet = portletsProcessingEventQNames.entrySet();
        Queue<PortletEvent> nextEventQueue = null;
		Map<EntityID, ChannelState> eventUpdatedPortletsState = null;
        if(containerResponse instanceof ExecuteActionResponse) {
            eventUpdatedPortletsState = ((ExecuteActionResponse) containerResponse).getEventUpdatedPortletsState();
        }
		if(eventUpdatedPortletsState == null) {
			eventUpdatedPortletsState = new HashMap<EntityID, ChannelState>();
		}
        for (Map.Entry<EntityID, QName> entry : entrySet) {
            EntityID portletEntityId = entry.getKey();
            ExecuteEventRequest executeEventRequest = createExecuteEventRequest(containerRequest);
            ExecuteEventResponse executeEventResponse = createExecuteEventResponse(containerResponse);
            executeEventRequest.setEntityID(portletEntityId);
			executeEventRequest.setNamespace(getNamespace(executeEventRequest, portletEntityId));
			executeEventRequest.setWindowID(getWindowID(executeEventRequest, portletEntityId));
			executeEventRequest.setChannelMode(ChannelMode.VIEW);
			executeEventRequest.setWindowState(ChannelState.NORMAL);
            // Use the event name associated with the portlet in the Map
            event.setQName(entry.getValue());
            executeEventResponse.setCurrentEvent(event);
            // Process only local portlets. Continue to next portlet if the current portlet is remote. 
            // Remote portlets will have portletid = null
            if (portletEntityId.getPortletID() == null ){
                continue;
            }
            // If the entity ID of the publishing portlet is same as the entityId of the
            // portlet processing event, the render parameters set in processAction or previous processEvent
            // of the publishing portlet needs to sent to the processEvent.
            if (portletEntityId.equals(publishingPortletEntityId)) {
                Map currentRenderParameters = getRenderParameters(containerRequest, portletEntityId);
                currentRenderParameters.putAll(publishingRenderParameters);
                executeEventRequest.setEventParameters(currentRenderParameters);
            } else {
                // Get the render parameters for the current portlet that may have been
                // set in the previous event's response and set on this portlet's request
                // Do not carry the scopeID render parameter as it follows different logic
                // which is present in executeEvent method
                Map currentRenderParameters = getRenderParameters(containerRequest, portletEntityId);
                removeActionScopeID(currentRenderParameters);
                executeEventRequest.setEventParameters(currentRenderParameters);
            }
            executeEvent(executeEventRequest, executeEventResponse);
			eventUpdatedPortletsState.put(portletEntityId, executeEventResponse.getNewWindowState());
			if(containerResponse instanceof ExecuteActionResponse) {
				((ExecuteActionResponse) containerResponse).setEventUpdatedPortletsState(eventUpdatedPortletsState);
			}
            if (nextEventQueue == null) {
                nextEventQueue = new ConcurrentLinkedQueue<PortletEvent>();
            }
            Queue<PortletEvent> eventQueue = executeEventResponse.getEventQueue();
            if (eventQueue != null) {
                for (PortletEvent eventNext : eventQueue) {
                    nextEventQueue.add(eventNext);
                }
            }
        }

        return nextEventQueue;
    }

    public void processRenderParameters(ContainerRequest containerRequest, 
            ContainerResponse containerResponse, 
            EntityID portletEntityId, 
            Map<String, String[]> currentRenderParameters, 
            boolean publicRenderParameter) {
        // If publicRenderParameter is present(true), get the existing render parameters
        // and overwrite with the input render parameters
        // If publicRenderParameter is not present(false), get only the public render parameters
        // from existing render parameters and overwrite with the input render parameters
        //TODO - improve this code
        if (currentRenderParameters != null) {
            Map tempRenderParameters;
            if (publicRenderParameter) {
                Map previousRenderParameters = getRenderParameters(containerRequest, portletEntityId);
                previousRenderParameters.putAll(currentRenderParameters);
                tempRenderParameters = previousRenderParameters;
            } else {
                tempRenderParameters = currentRenderParameters;
                // Get the list of public render parameter holders that this portlet supports
                List<PublicRenderParameterHolder> supportedPublicRenderParameterHolders =
                        containerRequest.getPortletWindowContext().getSupportedPublicRenderParameterHolders(portletEntityId, currentRenderParameters);
                if (supportedPublicRenderParameterHolders != null) {
                    Map previousRenderParameters = getRenderParameters(containerRequest, portletEntityId);
                    List<String> supportedPublicRenderParameters = new ArrayList<String>(supportedPublicRenderParameterHolders.size());
                    for (PublicRenderParameterHolder supportedPublicRenderParameterHolder : supportedPublicRenderParameterHolders) {
                        supportedPublicRenderParameters.add(supportedPublicRenderParameterHolder.getIdentifier());
                    }
                    Set<Map.Entry<String, String[]>> entrySet = previousRenderParameters.entrySet();
                    for (Map.Entry<String, String[]> entry : entrySet) {
                        String supportedPublicRenderParameter = entry.getKey();
                        if (supportedPublicRenderParameters.contains(supportedPublicRenderParameter)) {
                            tempRenderParameters.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
            //Remove the render parameters marked for deletion.
            List<String> deletedRenderParameters = null;
            if (containerResponse instanceof ExecuteActionResponse) {
                deletedRenderParameters = ((ExecuteActionResponse) containerResponse).getDeletedRenderParameters();
            } else if (containerResponse instanceof ExecuteEventResponse) {
                deletedRenderParameters = ((ExecuteEventResponse) containerResponse).getDeletedRenderParameters();
            }
			if(deletedRenderParameters == null) {
				deletedRenderParameters = new ArrayList<String>();
			}
			// Also remove those render parameters with value null.
			Set<Map.Entry<String, String[]>> entrySet = tempRenderParameters.entrySet();
			for (Map.Entry<String, String[]> entry : entrySet) {
				if(entry.getValue() == null) {
					if(!deletedRenderParameters.contains(entry.getKey())) {
						deletedRenderParameters.add(entry.getKey());
					}
				}
			}
			for (String deletedRenderParameter : deletedRenderParameters) {
				tempRenderParameters.remove(deletedRenderParameter);
			}
			
            setRenderParameters(containerRequest, portletEntityId, tempRenderParameters);
        }
    }

    public void executeEvent(ExecuteEventRequest executeEventRequest, 
            ExecuteEventResponse executeEventResponse) throws ContainerException, ContentException {
        getCachingService().removeCachedPortlet(executeEventRequest);
        Map<String, String[]> currentEventParameters = executeEventRequest.getEventParameters();
        //get the saved scope attributes and set in the ExecuteEventRequest before calling the processEvent of the Portlet
        String scopeID = getActionScopeID(currentEventParameters);
        if(scopeID != null) {
            Map<String, Serializable> scopedAttributes = getScopedAttributes(executeEventRequest, executeEventRequest.getEntityID());
            executeEventRequest.setScopedAttributes(scopeID, scopedAttributes);
        }
        PortletContainerResponse pcResponse = null;
        try {
            pcResponse = (PortletContainerResponse) invokePAE(PortletActions.EVENT, executeEventRequest, executeEventResponse);
            PortletContainerErrorCode errorCode = pcResponse.getErrorCode();
            if (!errorCode.equals(PortletContainerErrorCode.NO_ERROR)) {
                logger.log(Level.SEVERE, "PSPL_PCCSPCPCI0001", new Object[]{executeEventRequest.getEntityID(), errorCode});
                executeEventResponse.setEventFailed(true);
                executeEventResponse.setEventFailedMessage(pcResponse.getException().getMessage());
            } else {
                Map renderParameters = executeEventResponse.getRenderParameters();
                // If the scopeID in the currentEventParameters is null, see whether scopeID has been
                // set in EventResponse's render parameters.This happens if this portlet's processEvent 
                // has been called from the processAction of a different portlet
                if(scopeID == null) {
                    scopeID = getActionScopeID(renderParameters);
                }
                if(scopeID != null) {
                    // Save the scopeID as a render parameter
                    setActionScopeID(scopeID, renderParameters);
                    // Save scoped attributes
                    this.setScopedAttributes(executeEventRequest, executeEventRequest.getEntityID(), executeEventRequest.getScopedAttributes(scopeID));
                }
                getCoordinationService().setRenderParameters(executeEventRequest, executeEventResponse, executeEventRequest.getEntityID(), renderParameters);
            }
        } catch (Exception e) {
            throw new ContainerException("PortletContainer.executeEvent(): ", e);
        } //end try
    }

    private PortletContainerResponse invokePAE(String action, 
            ContainerRequest containerRequest, 
            ContainerResponse containerResponse) throws IOException, ServletException, ContentException {

        PortletContainerRequest pcRequest = null;
        PortletContainerResponse pcResponse = null;

        if (action.equals(PortletActions.RENDER)) {
            pcRequest = new PortletContainerRenderRequest((GetMarkupRequest) containerRequest);
            pcResponse = new PortletContainerRenderResponse((GetMarkupResponse) containerResponse);
            pcRequest.setActions(renderList);
        } else if (action.equals(PortletActions.ACTION)) {
            pcRequest = new PortletContainerActionRequest((ExecuteActionRequest) containerRequest);
            pcResponse = new PortletContainerActionResponse((ExecuteActionResponse) containerResponse);
            pcRequest.setActions(actionList);
        } else if (action.equals(PortletActions.EVENT)) {
            pcRequest = new PortletContainerEventRequest((ExecuteEventRequest) containerRequest);
            pcResponse = new PortletContainerEventResponse((ExecuteEventResponse) containerResponse);
            // Set the current event in PortletContainerEventResponse
            ((PortletContainerEventResponse) pcResponse).setCurrentEvent(((ExecuteEventResponse) containerResponse).getCurrentEvent());
            pcRequest.setActions(eventList);
        } else if (action.equals(PortletActions.RESOURCE)) {
            pcRequest = new PortletContainerResourceRequest((GetResourceRequest) containerRequest);
            pcResponse = new PortletContainerResourceResponse((GetResourceResponse) containerResponse);
            pcRequest.setActions(resourceList);
        }

        return invokePAE(action, containerRequest, containerResponse, pcRequest, pcResponse);
    }

    protected PortletContainerResponse invokePAE(String action, 
            ContainerRequest containerRequest, 
            ContainerResponse containerResponse, 
            PortletContainerRequest pcRequest, 
            PortletContainerResponse pcResponse) throws IOException, ServletException, ContentException {

        HttpServletRequest request = containerRequest.getHttpServletRequest();
        HttpServletResponse response = containerResponse.getHttpServletResponse();
        EntityID entityID = containerRequest.getEntityID();

		String namespace = getNamespace(containerRequest, entityID);
        String appName = entityID.getPortletApplicationName();
        String portletName = entityID.getPortletName();
		String portletWindowName = entityID.getPortletWindowName();

		if (logger.isLoggable(Level.FINE)) {
			logger.log(Level.FINE, "PSPL_PCCSPCPCI0002",
				new String[]{ appName, portletName, portletWindowName, action });
		}

		PortletPreferences portletPreferences = null;
        String appNameContext = getAppNameContext(appName);
        ServletContext portletAppContext = servletContext.getContext(appNameContext);
		if(portletAppContext == null) {
            logger.log(Level.SEVERE, "PSPL_PCCSPCPCI0014", appNameContext);
            throw new ContentException("Cannot get ServletContext object for" + appNameContext,
				PortletContainerErrorCode.PORTLET_UNAVAILABLE);
		}
        ResourceBundle bundle = getResourceBundle(portletAppContext, entityID, containerRequest.getLocale());
        try {
            portletPreferences = containerRequest.getPortletWindowContext().getPreferences(portletWindowName, bundle, containerRequest.getIsReadOnly());
        } catch (PortletWindowContextException pwce) {
            if (logger.isLoggable(Level.SEVERE)) {
                LogRecord record = new LogRecord(Level.SEVERE, "PSPL_PCCSPCPCI0006");
                record.setParameters(new Object[]{entityID});
                record.setThrown(pwce);
                record.setLoggerName(logger.getName());
                logger.log(record);
            }
        }

        String responseType = containerRequest.getPortletWindowContext().getContentType();
        pcRequest.setResponseContentType(responseType);

        pcRequest.setPortletPreferences(portletPreferences);
        pcRequest.setPortletName(portletName);
		pcRequest.setPortletWindowName(portletWindowName);
		pcRequest.setSharedSessionAttributesProcess(getSharedSessionAttributes(containerRequest));
        // Get the attributes from ContainerRequest and set in PortletContainerRequest
        pcRequest.setAttributes(containerRequest.getAttributes());

        PAERequestWrapper requestWrapper = new PAERequestWrapper(request, namespace);
		requestWrapper.setRequestSharedAttributes(containerRequest.getRequestSharedAttributes());
        requestWrapper.setAttribute(PortletContainerRequest.PORTLET_CONTAINER_REQUEST, pcRequest);
        requestWrapper.setAttribute(PortletContainerResponse.PORTLET_CONTAINER_RESPONSE, pcResponse);

        DispatcherState ds = null;

        boolean portletRenderModeParallel = false;
        if (ContainerUtil.getPolicyService() != null) {
            portletRenderModeParallel = ContainerUtil.getPolicyService().renderPortletsInParallel(request);
        } else {
            portletRenderModeParallel = false;
        }
        if (portletRenderModeParallel) {
            serializeAll = false;
        } else {
            serializeAll = true;
        }

        if(logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "PSPL_PCCSPCPCI0009", Boolean.toString(portletRenderModeParallel));
        }
        
        // It has been verified that executing the portlets of portal
        // page in a parallel fashion (sharing the same containerRequest
        // object) would result in a session messup problem if those
        // portlets are in different portlet applications and use
        // session. This is true even though we sync all the
        // sessionEnabled portlet requests and only allow parallel
        // processing on those sessless portlets.
        //
        // Although the servlet spec still does not allow concurrent
        // threads sharing the same containerRequest object, with the
        // implementation below, it is ok to have portlet requests
        // of mixed sessionEnabled portlets and sessionless portlets
        // to be processed in a semi-parallel manner (logic in
        // DispatcherState class) if THOSE POSTLETS ARE PACKAGED
        // IN THE SAME PORTLET APPLICATION.
        while (true) {
            synchronized (request) {
                ds = (DispatcherState) request.getAttribute(DISPATCHER_STATE);
                if (ds == null) {
                    ds = new DispatcherState();
                    request.setAttribute(DISPATCHER_STATE, ds);
                }

                if (!ds.canEnter(appName, serializeAll)) {
                    try {
                        request.wait();
                    } catch (InterruptedException e) {
                        pcResponse.setErrorCode(PortletContainerErrorCode.PORTLET_UNAVAILABLE);
                        return pcResponse;
                        // counter already incremented by caller
                    }
                } else {
                    // if handle session throws exception we should exit from DispatcherState
                    try {
                        handleSession(containerRequest);
                        requestWrapper.setAttribute(PortletContainerConstants.HTTP_SESSION_ID, getSessionId(containerRequest));
                    } catch (Exception e) {
                        if (logger.isLoggable(Level.INFO)) {
                            logger.log(Level.INFO, "PSPL_PCCSPCPCI0008", e.getMessage());
                        }
                    }
                    break;
                }
            }
        }
        RequestDispatcher rd = portletAppContext.getRequestDispatcher(PAE_NAME);

		PAEResponseWrapper responseWrapper = new PAEResponseWrapper(response);
        
		try {
			if(rd == null) {
				throw new FileNotFoundException();
			}
            rd.include(requestWrapper, responseWrapper);
        } catch(FileNotFoundException fnfe) {
            logger.log(Level.SEVERE, "PSPL_PCCSPCPCI0010", appNameContext);
            throw new ContentException("Invalid webapplication:" + appNameContext, 
				PortletContainerErrorCode.PORTLET_UNAVAILABLE);
        } finally {
            synchronized (request) {
                if (ds.exit()) {
                    request.notifyAll();
                }
            }
        }
		setSharedSessionAttributes(containerRequest);
        return pcResponse;
    }

    private Map<String, String[]> getPublicParameters(ExecuteActionRequest executeActionRequest,
                                                            EntityID entityID) {
        Map<String, String[]> renderParameters = getRenderParameters(executeActionRequest, entityID);
        List<PublicRenderParameterHolder> publicRenderParameterHolders = 
                executeActionRequest.getPortletWindowContext().getSupportedPublicRenderParameterHolders(entityID, 
                renderParameters);
        Map<String, String[]> publicParameters = new HashMap<String, String[]>();
        if(publicRenderParameterHolders != null) {
            for(PublicRenderParameterHolder publicRenderParameterHolder : publicRenderParameterHolders) {
                String name = publicRenderParameterHolder.getIdentifier();
                publicParameters.put(name, renderParameters.get(name));
            }
        }
        return publicParameters;
    }

	//Returns a list of parameters whose value is "null"
	private List<String> getDeletedParameters(Map<String, String[]> actionParameters) {
		//remove keys that have the value as "null" from the map
		List<String> deletedParameters = new ArrayList<String>();
		Set<Map.Entry<String, String[]>> entries = actionParameters.entrySet();
		for(Map.Entry<String, String[]> mapEntry : entries) {
			String[] values = mapEntry.getValue();
			if(values == null || values[0].equals("null")) {
				deletedParameters.add(mapEntry.getKey());
			}
		}
		return deletedParameters;
	}
	
    private CachingService getCachingService() {
        return this.cacheManager;
    }

    private CoordinationService getCoordinationService() {
        return this.coordinationService;
    }

    private ClientCachingService getClientCachingService() {
        return this.clientCachingService;
    }

    private void handleSession(ContainerRequest containerRequest) {

        HttpServletRequest request = containerRequest.getHttpServletRequest();
        HttpSession httpSession = null;

        // If the http_session_id does not exist and the requested session id is not valid
        // then invalidate the session.
        if (!isSessionIdExist(containerRequest) && !request.isRequestedSessionIdValid()) {
            httpSession = request.getSession(false);
            if (httpSession != null) {
                // session INVALID,
                //probably due to new user login
                logger.log(Level.FINE, "PSPL_PCCSPCPCI0012");
				if(request.isRequestedSessionIdValid()) {
					httpSession.invalidate();
				}
                request.setAttribute(PortletContainerConstants.SESSION_INVALID, Boolean.TRUE);
            }
        }

        // In the situation that:
        //   a)the sessionID has been set in HttpSession
        //     during the processing of the previous "client
        //     containerRequest" (browser driven action), and
        //   b)the current thread is the first one of those
        //     threads with this "client containerRequest",
        // the code needs to make sure the HttpSession object
        // is being created appropriately in the Portal Server
        // web application (A typical example of this scenario
        // is the desktop reload).
        //
        // This is basically a workaround which deals with the
        // situation that the session cookie was not
        // appropriately set due to the unpredictable context
        // switching in processing the previous client containerRequest.
        if (request.getAttribute(FIRST_THREAD) == null) {
            httpSession = request.getSession(true);
            setSessionId(containerRequest, httpSession.getId());
            request.setAttribute(FIRST_THREAD, Boolean.TRUE);
        }
    }

    private String getSessionId(ContainerRequest containerRequest) {
        HttpServletRequest request = containerRequest.getHttpServletRequest();
        HttpSession httpSession = request.getSession();
        return (String)httpSession.getAttribute(PortletContainerConstants.HTTP_SESSION_ID);
    }

	private void logPortletAppEngineException(EntityID entityID, Exception exception) {
		logger.log(Level.SEVERE, "PSPL_PCCSPCPCI0001", new Object[]{entityID, exception});
	}

	// Returns the public render parameters that are in the action or resource url parameters
	private Map<String, String[]> getPublicParametersFromActionParameters(ContainerRequest containerRequest, 
		EntityID portletEntityId, Map<String,String[]> actionParameters) {
		
		Map<String, String[]> publicRenderParameters = null;
		// Get the list of public render parameter holders that this portlet supports
		List<PublicRenderParameterHolder> supportedPublicRenderParameterHolders =
				containerRequest.getPortletWindowContext().getSupportedPublicRenderParameterHolders(portletEntityId, actionParameters);
		if (supportedPublicRenderParameterHolders != null) {
			List<String> supportedPublicRenderParameters = new ArrayList<String>(supportedPublicRenderParameterHolders.size());
			for (PublicRenderParameterHolder supportedPublicRenderParameterHolder : supportedPublicRenderParameterHolders) {
				supportedPublicRenderParameters.add(supportedPublicRenderParameterHolder.getIdentifier());
			}
			publicRenderParameters = new HashMap<String, String[]>(supportedPublicRenderParameters.size());
			Set<Map.Entry<String, String[]>> entrySet = actionParameters.entrySet();
			for (Map.Entry<String, String[]> entry : entrySet) {
				String supportedPublicRenderParameter = entry.getKey();
				if (supportedPublicRenderParameters.contains(supportedPublicRenderParameter)) {
					publicRenderParameters.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return publicRenderParameters;
	}
	
    private void setSessionId(ContainerRequest containerRequest, String sessionId) {
        HttpServletRequest request = containerRequest.getHttpServletRequest();
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(PortletContainerConstants.HTTP_SESSION_ID, sessionId);
    }

    private boolean isSessionIdExist(ContainerRequest containerRequest) {
        HttpServletRequest request = containerRequest.getHttpServletRequest();
        HttpSession httpSession = request.getSession();
        boolean retValue = (httpSession.getAttribute(PortletContainerConstants.HTTP_SESSION_ID) == null) ? false : true;
        return retValue;
    }

	private String getAppNameContext(String appName) {
		String appNameContext = "/" + appName;
		return appNameContext;
	}

	private boolean renderURLParameterCacheDisabled(EntityID entityID) {
        String appNameContext = getAppNameContext(entityID.getPortletApplicationName());
		DeploymentExtensionDescriptor sunPortletDescriptor = 
			PortletContainerUtil.getSunPortletDescriptor(servletContext.getContext(appNameContext));
		if(sunPortletDescriptor != null) {
			return sunPortletDescriptor.renderURLParameterCacheDisabled(entityID.getPortletName());
		} else {
			return false;
		}

	}
    /**
     * Retrieve the saved parameters.
     */
    private Map<String, String[]> getRenderParameters(ContainerRequest containerRequest, 
            EntityID portletEntityId) {
        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        Map renderParameters = (Map) portletWindowContext.getProperty(
                PortletContainerConstants.RENDER_PARAM_PREFIX + portletEntityId);
        if (renderParameters == null || renderParameters.size() == 0) {
            renderParameters = new HashMap();
        }
        return renderParameters;
    }

    /**
     * Save the parameters.
     */
    private void setRenderParameters(ContainerRequest containerRequest, 
            EntityID portletEntityId, Map<String, String[]> renderParameters) {
        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        portletWindowContext.setProperty(PortletContainerConstants.RENDER_PARAM_PREFIX + portletEntityId, renderParameters);
    }

    /**
     * Retrieve the saved render URL parameters.
     */
    private Map<String, String[]> getRenderURLParameters(ContainerRequest containerRequest,
            EntityID portletEntityId) {
        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        Map renderURLParameters = (Map) portletWindowContext.getProperty(
                PortletContainerConstants.RENDER_URL_PARAM_PREFIX + portletEntityId);
        if (renderURLParameters == null || renderURLParameters.size() == 0) {
            renderURLParameters = new HashMap();
        }
        return renderURLParameters;
    }

    /**
     * Save the render URL parameters.
     */
    private void setRenderURLParameters(ContainerRequest containerRequest,
            EntityID portletEntityId, Map<String, String[]> renderURLParameters) {
        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        portletWindowContext.setProperty(PortletContainerConstants.RENDER_URL_PARAM_PREFIX + portletEntityId, renderURLParameters);
    }

    /**
     * Retrieve the scoped attributes
     */
    private Map<String, Serializable> getScopedAttributes(ContainerRequest containerRequest, EntityID portletEntityId) {
        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        Map scopedAttributes = (Map) portletWindowContext.getProperty(
                PortletContainerConstants.SCOPED_ATTRIBUTES_PREFIX + portletEntityId);
        if (scopedAttributes == null || scopedAttributes.size() == 0) {
            scopedAttributes = new HashMap();
        }
        return scopedAttributes;
    }

     /**
     * Save the scoped attributes.
     */
    private void setScopedAttributes(ContainerRequest containerRequest, EntityID portletEntityId, Map scopedAttributes) {
        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        portletWindowContext.setProperty(PortletContainerConstants.SCOPED_ATTRIBUTES_PREFIX + portletEntityId, scopedAttributes);
    }

    /**
     * Retrieves the action scope ID from the render parameters
     */
    private String getActionScopeID(Map<String, String[]> renderParameters) {
        String[] scopeID = renderParameters.get(PortletRequest.ACTION_SCOPE_ID);
        if(scopeID != null) {
            return scopeID[0];
        }
        return null;
    }

    /**
     * Sets the action scope ID in the render parameters
     */
    private void setActionScopeID(String scopeID, Map<String, String[]> renderParameters) {
        if(getActionScopeID(renderParameters) == null) {
            renderParameters.put(PortletRequest.ACTION_SCOPE_ID, new String[] { scopeID});
        }
    }

    /**
     * Removes the action scope ID from the render parameters
     */
    private void removeActionScopeID(Map<String, String[]> renderParameters) {
        if(getActionScopeID(renderParameters) != null) {
            renderParameters.remove(PortletRequest.ACTION_SCOPE_ID);
        }
    }

    private String getTitle(GetMarkupRequest getMarkUpRequest, EntityID entityID) {
        PortletWindowContext portletWindowContext = getMarkUpRequest.getPortletWindowContext();
        String portletWindowName = entityID.getPortletWindowName();
        Locale locale = getMarkUpRequest.getLocale();
        if (locale == null) {
            locale = getMarkUpRequest.getHttpServletRequest().getLocale();
        }
        String title = null;
        try {
            title = portletWindowContext.getPortletWindowTitle(portletWindowName, locale.toString());
        } catch (PortletWindowContextException pwce) {
            if (logger.isLoggable(Level.INFO)) {
                LogRecord logRecord = new LogRecord(Level.INFO, "PSPL_PCCSPCPCI0007");
                logRecord.setParameters(new Object[]{portletWindowName});
                logRecord.setThrown(pwce);
                logger.log(logRecord);
            }
        }
        return title;
    }

	private String getNamespace(ContainerRequest containerRequest, EntityID portletEntityId) {
		String namespace = containerRequest.getNamespace(portletEntityId.getPortletID());
		if(namespace == null) {
			return ContainerUtil.getJavascriptSafeName(portletEntityId.toString());
		}
		return namespace;
	}

	private String getWindowID(ContainerRequest containerRequest, EntityID portletEntityId) {
		String windowID = containerRequest.getWindowID(portletEntityId.getPortletID());
		if(windowID == null) {
			return portletEntityId.toString();
		}
		return windowID;
	}

    protected ResourceBundle getResourceBundle(ServletContext portletAppContext, EntityID entityID, Locale locale) {
        String portletName = entityID.getPortletName();
        //Get the  Resources map for the  portlet web application.
        //This is constructed and set as an attribute of servlet context, during the
        //init of  the PAEServlet, of the  portlet web application.
        //if called before the init, resource not be available and will return 'null'.
        Map<String,Map<String,ResourceBundle>> portletResourceMap = (Map)portletAppContext.getAttribute(PortletContainerConstants.PORTLET_RESOURCES);
        ResourceBundle bundle = PortletContainerUtil.getResourceBundle(portletResourceMap, portletName, locale);
        return bundle;
    }

    public List<EntityID> processContainerEvent(PortletEvent event, EntityID portletEntityID,
                            HttpServletRequest request, HttpServletResponse response) 
                            throws ContainerException {
        List<EntityID> updatedPortlets = null;
        try {
            // Check whether the event can be sent
            // This is done is checking the Container Event Policy
            ContainerEventPolicy containerEventPolicy = ContainerUtil.getPolicyService().getContainerEventPolicy();
            boolean isEnabled = containerEventPolicy.isEnabled(event.getName());
            if(isEnabled) {
                PortletWindowContextAbstractFactory afactory = new PortletWindowContextAbstractFactory();
                PortletWindowContextFactory factory = afactory.getPortletWindowContextFactory();
                PortletWindowContext portletWindowContext = factory.getPortletWindowContext(request);
                ChannelURLFactory channelURLFactory = null;
                ExecuteEventRequest executeEventRequest = createExecuteEventRequest(request,
                                                                                    portletEntityID,
                                                                                    ChannelState.NORMAL,
                                                                                    ChannelMode.VIEW,
                                                                                    portletWindowContext,
                                                                                    channelURLFactory);
                ExecuteEventResponse executeEventResponse = createExecuteEventResponse(response);
                Queue<PortletEvent> eventQueue = new ConcurrentLinkedQueue<PortletEvent>();
                eventQueue.add(event);
                // Use the Policy for Container Events
                executeEventRequest.getPolicyManager().setEventPolicy(containerEventPolicy);
                updatedPortlets = getCoordinationService().publishEvent(executeEventRequest, executeEventResponse, eventQueue);
            }
        } catch (ServiceException ex) {
            throw new ContainerException("Exception while processing Container Event", ex);
        } catch (PortletWindowContextException ex) {
            throw new ContainerException("Exception while processing Container Event", ex);
        }
        if(updatedPortlets == null) {
            updatedPortlets = Collections.emptyList();
        }
        return updatedPortlets;
    }

    public List<EntityID> processContainerEvent(PortletEvent event,
                                      EntityID portletEntityID)
                    throws ContainerException {
        return Collections.emptyList();
    }

	private void setSharedSessionAttributes(ContainerRequest containerRequest) {

		Map<String, Object> sharedSessionAttributes = containerRequest.getSharedSessionAttributesPublish();
		if(sharedSessionAttributes != null && !sharedSessionAttributes.isEmpty()) {
	        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
	        portletWindowContext.setProperty(PortletContainerConstants.SHARED_SESSION_ATTRIBUTES_PREFIX, sharedSessionAttributes);
		}
	}

	private Map<String, Object> getSharedSessionAttributes(ContainerRequest containerRequest) {

        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        Map sharedSessionAttributes = (Map) portletWindowContext.getProperty(
                PortletContainerConstants.SHARED_SESSION_ATTRIBUTES_PREFIX);
        if (sharedSessionAttributes == null || sharedSessionAttributes.isEmpty()) {
            sharedSessionAttributes = new HashMap();
        }
        return sharedSessionAttributes;
	}
}
