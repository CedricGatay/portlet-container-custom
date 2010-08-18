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

package com.sun.portal.container.impl;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;
import com.sun.portal.container.ChannelURLFactory;
import com.sun.portal.container.Container;
import com.sun.portal.container.ContainerConstants;
import com.sun.portal.container.ContainerException;
import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.ContainerResponse;
import com.sun.portal.container.EntityID;
import com.sun.portal.container.ExecuteActionRequest;
import com.sun.portal.container.ExecuteActionResponse;
import com.sun.portal.container.ExecuteEventRequest;
import com.sun.portal.container.ExecuteEventResponse;
import com.sun.portal.container.GetMarkupRequest;
import com.sun.portal.container.GetMarkupResponse;
import com.sun.portal.container.GetResourceRequest;
import com.sun.portal.container.GetResourceResponse;
import com.sun.portal.container.PortletWindowContext;
import com.sun.portal.container.WindowRequestReader;
import com.sun.portal.container.service.ServiceManager;
import com.sun.portal.container.service.policy.impl.PolicyManagerImpl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AbstractContainer implements the request/response methods of the Container interface.
 *
 */
public abstract class AbstractContainer implements Container {

    public enum RequestType { ACTION, EVENT, RENDER, RESOURCE };
    
    public AbstractContainer() {
    }

    public GetMarkupRequest createGetMarkUpRequest(HttpServletRequest request, 
            EntityID portletEntityId, ChannelState channelState, 
            ChannelMode channelMode, PortletWindowContext portletWindowContext, 
            ChannelURLFactory channelURLFactory) {
        ContainerRequest containerRequest = 
                assembleContainerRequest(request, portletEntityId, channelState, channelMode,
                    portletWindowContext, channelURLFactory, RequestType.RENDER);
        GetMarkupRequest getMarkupRequestImpl =  (GetMarkupRequest)containerRequest;
        getMarkupRequestImpl.setCharacterEncoding(request.getCharacterEncoding());
        //
        // determine if the portlet is the target of an url
        boolean isTarget = getIsTarget(getMarkupRequestImpl, portletEntityId);
        if(isTarget){
            // If yes, set it in the GetMarkupRequest.
            getMarkupRequestImpl.setIsTarget(isTarget);
            // and clear this target for next request by setting it false
            setIsTarget(getMarkupRequestImpl, portletEntityId, false);
        }

        return getMarkupRequestImpl;
    }

    public GetMarkupResponse createGetMarkUpResponse(HttpServletResponse response) {
        ContainerResponse containerResponse = 
                assembleContainerResponse(response, RequestType.RENDER);
        return (GetMarkupResponse)containerResponse;
    }

    public ExecuteActionRequest createExecuteActionRequest(HttpServletRequest request, 
            EntityID portletEntityId, ChannelState channelState, 
            ChannelMode channelMode, PortletWindowContext portletWindowContext, 
            ChannelURLFactory channelURLFactory, WindowRequestReader windowRequestReader) {
        ContainerRequest containerRequest = 
                assembleContainerRequest(request, portletEntityId, channelState, channelMode,
                    portletWindowContext, channelURLFactory, RequestType.ACTION);
        ExecuteActionRequest executeActionRequestImpl = (ExecuteActionRequest)containerRequest;
        executeActionRequestImpl.setWindowRequestReader(windowRequestReader);
        executeActionRequestImpl.setCharacterEncoding(request.getCharacterEncoding());
        return executeActionRequestImpl;
    }

    public ExecuteActionResponse createExecuteActionResponse(HttpServletResponse response) {
        ContainerResponse containerResponse = 
                assembleContainerResponse(response, RequestType.ACTION);
        return (ExecuteActionResponse)containerResponse;
    }

    public ExecuteEventRequest createExecuteEventRequest(HttpServletRequest request, 
            EntityID portletEntityId, ChannelState channelState, 
            ChannelMode channelMode, PortletWindowContext portletWindowContext, 
            ChannelURLFactory channelURLFactory) {
        ContainerRequest containerRequest = 
                assembleContainerRequest(request, portletEntityId, channelState, channelMode,
                    portletWindowContext, channelURLFactory, RequestType.EVENT);
        ExecuteEventRequest executeEventRequestImpl = (ExecuteEventRequest)containerRequest;
        executeEventRequestImpl.setCharacterEncoding(request.getCharacterEncoding());
        return executeEventRequestImpl;
    }

    public ExecuteEventResponse createExecuteEventResponse(HttpServletResponse response) {
        ContainerResponse containerResponse = 
                assembleContainerResponse(response, RequestType.EVENT);
        return (ExecuteEventResponse)containerResponse;
    }

    public ExecuteEventRequest createExecuteEventRequest(ContainerRequest containerRequest) {
        ExecuteEventRequestImpl executeEventRequestImpl = new ExecuteEventRequestImpl();
        // Populate ExecuteEventRequest with existing values
        executeEventRequestImpl.setAllowableChannelModes(containerRequest.getAllowableChannelModes());
        executeEventRequestImpl.setAllowableContentTypes(containerRequest.getAllowableContentTypes());
        executeEventRequestImpl.setAllowableWindowStates(containerRequest.getAllowableWindowStates());
		executeEventRequestImpl.setAttributes(containerRequest.getAttributes());
        executeEventRequestImpl.setChannelMode(containerRequest.getChannelMode());
        executeEventRequestImpl.setChannelURLFactory(containerRequest.getChannelURLFactory());
        executeEventRequestImpl.setCharacterEncoding(containerRequest.getCharacterEncoding());
        executeEventRequestImpl.setHttpServletRequest(containerRequest.getHttpServletRequest());
        executeEventRequestImpl.setHttpSession(containerRequest.getHttpSession());
		executeEventRequestImpl.setLocale(containerRequest.getLocale());
		executeEventRequestImpl.setNamespace(containerRequest.getNamespace());
        executeEventRequestImpl.setPolicyManager(containerRequest.getPolicyManager());
		executeEventRequestImpl.setPortalInfo(containerRequest.getPortalInfo());
        executeEventRequestImpl.setPortletWindowContext(containerRequest.getPortletWindowContext());
        executeEventRequestImpl.setPortletNamespaces(containerRequest.getPortletNamespaces());
        executeEventRequestImpl.setPortletWindowIDs(containerRequest.getPortletWindowIDs());
        executeEventRequestImpl.setRoles(containerRequest.getRoles());
		executeEventRequestImpl.setUserID(containerRequest.getUserID());
        executeEventRequestImpl.setUserInfo(containerRequest.getUserInfo());
		executeEventRequestImpl.setUserPrincipal(containerRequest.getUserPrincipal());
		executeEventRequestImpl.setWindowID(containerRequest.getWindowID());
        executeEventRequestImpl.setWindowState(containerRequest.getWindowState());
        return executeEventRequestImpl;
    }

    public ExecuteEventResponse createExecuteEventResponse(ContainerResponse containerResponse) {
        // Populate ExecuteEventRequest with exisiting values
        ExecuteEventResponseImpl executeEventResponseImpl = new ExecuteEventResponseImpl();
        executeEventResponseImpl.setHttpServletResponse(containerResponse.getHttpServletResponse());
        return executeEventResponseImpl;
    }

    public GetResourceRequest createGetResourceRequest(HttpServletRequest request, 
            EntityID portletEntityId, ChannelState channelState, 
            ChannelMode channelMode, PortletWindowContext portletWindowContext, 
            ChannelURLFactory channelURLFactory, WindowRequestReader windowRequestReader) {
        ContainerRequest containerRequest = 
                assembleContainerRequest(request, portletEntityId, channelState, channelMode,
                    portletWindowContext, channelURLFactory, RequestType.RESOURCE);
        GetResourceRequest GetResourceRequestImpl = (GetResourceRequest)containerRequest;
        GetResourceRequestImpl.setWindowRequestReader(windowRequestReader);
        GetResourceRequestImpl.setCharacterEncoding(request.getCharacterEncoding());
        return (GetResourceRequest)containerRequest;
    }

    public GetResourceResponse createGetResourceResponse(HttpServletResponse response) {
        ContainerResponse containerResponse = 
                assembleContainerResponse(response, RequestType.RESOURCE);
        return (GetResourceResponse)containerResponse;
    }

    /**
     * Returns the Service for the serviceName
     * @param serviceName the name of the service
     * @return the Service for the serviceName
     */
    protected Object getService(String serviceName) {
        return ServiceManager.getServiceManager().getService(serviceName);
    }

    /**
     * Assembles the ContainerRequest object
     */
    private ContainerRequestImpl assembleContainerRequest(
            HttpServletRequest request,
            EntityID entityId,
            ChannelState channelState,
            ChannelMode channelMode,
            PortletWindowContext portletWindowContext,
            ChannelURLFactory channelURLFactory,
            RequestType requestType) {
        
        ContainerRequestImpl containerRequest = null;
        if ( requestType.equals(RequestType.ACTION) ) {
            containerRequest = new ExecuteActionRequestImpl();
        } else if(requestType.equals(RequestType.EVENT)) {
            containerRequest = new ExecuteEventRequestImpl();
        } else if(requestType.equals(RequestType.RENDER)) {
            containerRequest = new GetMarkupRequestImpl();
        } else if(requestType.equals(RequestType.RESOURCE)) {
            containerRequest = new GetResourceRequestImpl();
        }
        if(containerRequest != null) {
            containerRequest.setHttpServletRequest(request);
            containerRequest.setEntityID(entityId);
            containerRequest.setWindowState(channelState);
            containerRequest.setChannelMode(channelMode);
            containerRequest.setPortletWindowContext(portletWindowContext);
            containerRequest.setChannelURLFactory(channelURLFactory);

            List<ChannelState> allowableWindowStates = new ArrayList<ChannelState>();
            allowableWindowStates.add(ChannelState.MAXIMIZED);
            allowableWindowStates.add(ChannelState.MINIMIZED);
            allowableWindowStates.add(ChannelState.NORMAL);
            containerRequest.setAllowableWindowStates(allowableWindowStates);

            List<ChannelMode> allowablePortletWindowModes = new ArrayList<ChannelMode>();
            allowablePortletWindowModes.add(ChannelMode.VIEW);
            allowablePortletWindowModes.add(ChannelMode.EDIT);
            allowablePortletWindowModes.add(ChannelMode.HELP);
            containerRequest.setAllowableChannelModes(allowablePortletWindowModes);

            String contentType = portletWindowContext.getContentType();
            List<String> allowableContentTypes = new ArrayList<String>();
            allowableContentTypes.add(contentType);
            containerRequest.setAllowableContentTypes(allowableContentTypes);

            containerRequest.setPolicyManager(new PolicyManagerImpl());
            
        }
        return containerRequest;
    }
    
    /**
     * Assembles the ContainerResponse object
     */
    private ContainerResponseImpl assembleContainerResponse(
            HttpServletResponse response,
            RequestType requestType) {
        
        // get container response.
        ContainerResponseImpl containerResponse = null;
        if ( requestType.equals(RequestType.ACTION) ) {
            containerResponse = new ExecuteActionResponseImpl();
        } else if(requestType.equals(RequestType.EVENT)) {
            containerResponse = new ExecuteEventResponseImpl();
        } else if(requestType.equals(RequestType.RENDER)) {
            containerResponse = new GetMarkupResponseImpl();
        } else if(requestType.equals(RequestType.RESOURCE)) {
            containerResponse = new GetResourceResponseImpl();
        }
        if(containerResponse != null) {
            containerResponse.setHttpServletResponse(response);
        }
        
        return containerResponse;
    }
        
    /**
     * Determine if the entity is the target of the request.
     */
    protected boolean getIsTarget(ContainerRequest containerRequest, EntityID entityId) {
        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        String targetValue = (String) portletWindowContext.getProperty(
                ContainerConstants.ENTITY_TARGET + entityId);
        if (targetValue != null) {
            Boolean target = Boolean.valueOf(targetValue);
            return target.booleanValue();
        } else {
            return false;
        }
    }   
    
    /**
     * Mark the current entity as the target or not the target based
     * on isTarget parameter
     */
    protected void setIsTarget(ContainerRequest containerRequest, EntityID entityId, boolean isTarget) {
        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        portletWindowContext.setProperty(ContainerConstants.ENTITY_TARGET + entityId, 
                String.valueOf(isTarget));
    }    
}
