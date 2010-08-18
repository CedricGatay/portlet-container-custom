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

package com.sun.portal.container.service.coordination.impl;

import com.sun.portal.container.ContainerException;
import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.ContainerResponse;
import com.sun.portal.container.ContainerType;
import com.sun.portal.container.ContainerUtil;
import com.sun.portal.container.ContentException;
import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletEvent;
import com.sun.portal.container.PortletType;
import com.sun.portal.container.PortletWindowContextException;
import com.sun.portal.container.service.EventHolder;
import com.sun.portal.container.service.PublicRenderParameterHolder;
import com.sun.portal.container.service.coordination.CoordinationService;
import com.sun.portal.container.service.coordination.CoordinationSubscriber;
import com.sun.portal.container.service.Service;
import com.sun.portal.container.service.ServiceManager;
import com.sun.portal.container.service.ServiceException;
import com.sun.portal.container.service.ServiceAdapter;
import com.sun.portal.container.service.coordination.ContainerEventService;
import com.sun.portal.container.service.deployment.DeploymentService;
import com.sun.portal.container.service.policy.DistributionType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;

/**
 * CoordinationServiceImpl provides concrete implementation for the CoordinationService.
 *
 */
public class CoordinationServiceImpl extends ServiceAdapter implements CoordinationService {
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(CoordinationServiceImpl.class, "CLogMessages");
    private Map<ContainerType, CoordinationSubscriber> subscribers;
    private static final String DESCRIPTION = "Responsible for sending events and render parameters to all the registered subscribers";
    
    @Override
    public void init(ServletContext context) {
        if(subscribers == null)
            subscribers = new ConcurrentHashMap<ContainerType, CoordinationSubscriber>();
    }
    
    @Override
    public String getName() {
        return COORDINATION_SERVICE;
    }
    
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
    
    @Override
    public void destroy() {
        subscribers = null;
    }
    
    public List<EntityID> publishEvent(ContainerRequest containerRequest, ContainerResponse containerResponse
            , Queue<PortletEvent> eventQueue) throws ServiceException {
        if (eventQueue == null) {
            return Collections.emptyList();
        }
        int eventGeneration = 0;
        List<EntityID> updatedPortlets = new ArrayList<EntityID>();
        return publishEventInternal(containerRequest, containerResponse, eventQueue, updatedPortlets, eventGeneration);
    }

    public void setRenderParameters(ContainerRequest containerRequest, ContainerResponse containerResponse,
            EntityID portletEntityId, Map<String, String[]> renderParameters) {
        
        DeploymentService deploymentServiceForPublishPortlet = getDeploymentService(portletEntityId);
        if(deploymentServiceForPublishPortlet != null){
            // Get the list of public render parameter holders that this portlet supports
            List<PublicRenderParameterHolder> supportedPRPHoldersFromPublishPortlet =
                    deploymentServiceForPublishPortlet.
                    getSupportedPublicRenderParameterHolders(
                        containerRequest.getPortletWindowContext(), 
                        portletEntityId, 
                        renderParameters);
            if(renderParameters != null && supportedPRPHoldersFromPublishPortlet != null
                    && !supportedPRPHoldersFromPublishPortlet.isEmpty()) {
                // Get the list of portlets from PortalWindowContext which returns it based on the PublicRenderParameterPolicy
                List<EntityID> processPortletList = null;
                try {
                    DistributionType distributionType = 
						ContainerUtil.getPublicRenderParameterDistributionType(containerRequest);
                    processPortletList = 
                            containerRequest.getPortletWindowContext().getPortletWindows(PortletType.ALL,
                            distributionType);
                    if(logger.isLoggable(Level.FINER)) {
                        logger.log(Level.FINER, "PSC_CSPCS027", new Object[] { processPortletList, distributionType });
                    }
                } catch(PortletWindowContextException pwce) {
                    // log and continue the render parameter processing
                    if(logger.isLoggable(Level.SEVERE)) {
                        LogRecord logRecord = new LogRecord(Level.SEVERE, "PSC_CSPCS013");
                        logRecord.setParameters(new Object[]{portletEntityId});
                        logRecord.setThrown(pwce);
                        logRecord.setLoggerName(logger.getName());
                        logger.log(logRecord);
                    }
                }
                if(processPortletList != null) {
                    DeploymentService deploymentServiceForProcessPortlet = null;
                    for(EntityID processPortletEntityId: processPortletList) {
                        // For each portletEntity Id check whether it supports the any of the supported render parameters
                        // If it supports any render parameter, get its value from the input render parameter and if present
                        // set it in the map and send it to CoordinationSubscriber for processing
                        deploymentServiceForProcessPortlet = getDeploymentService(processPortletEntityId);

                        if(deploymentServiceForProcessPortlet!=null){
                            Map<String, String[]> prpMapForProcessPortlet = new HashMap<String,String[]>();
                            boolean supportsPRP = false;
                            Map<String, String> supportedPRPIdentifiersFromProcessPortlet =
                                    deploymentServiceForProcessPortlet.verifySupportedPublicRenderParameters(
                                    containerRequest.getPortletWindowContext(),
                                    processPortletEntityId,
                                    supportedPRPHoldersFromPublishPortlet);

                            if(logger.isLoggable(Level.FINER)) {
                                logger.log(Level.FINER, "PSC_CSPCS028",
                                        new Object[] { processPortletEntityId, supportedPRPHoldersFromPublishPortlet,
                                            supportedPRPIdentifiersFromProcessPortlet});
                            }
                            Set<Map.Entry<String, String>> supportedPRPIdentifiersSet =
                                    supportedPRPIdentifiersFromProcessPortlet.entrySet();

                            for(Map.Entry<String, String> identifierPair: supportedPRPIdentifiersSet) {
                                supportsPRP = true;
                                // The render parameter that is set is always the identifier
								if(renderParameters.containsKey(identifierPair.getKey())) {
									String[] values = renderParameters.get(identifierPair.getKey());
									prpMapForProcessPortlet.put(identifierPair.getValue(), values);
								}
                            }
                            if(supportsPRP) {
                                CoordinationSubscriber renderParameterSubscriber = 
                                        getCoordinationSubscriber(processPortletEntityId);
                                if(renderParameterSubscriber != null) {
                                    renderParameterSubscriber.processRenderParameters(
                                            containerRequest, 
                                            containerResponse,
                                            processPortletEntityId, 
                                            prpMapForProcessPortlet, true);
                                }
                            }
                        }
                    }
                }
            }
        }
        // Process all the render parameters of the portlet that initiated this action
        CoordinationSubscriber renderParameterSubscriber = getCoordinationSubscriber(portletEntityId);
        if(renderParameterSubscriber != null) {
            renderParameterSubscriber.processRenderParameters(containerRequest, containerResponse,
                portletEntityId, renderParameters, false);
        }
    }
    
    public void registerSubscriber(ContainerType containerType, CoordinationSubscriber coordinationSubscriber) {
        if(!this.subscribers.containsKey(containerType)) {
            this.subscribers.put(containerType, coordinationSubscriber);
        }
    }

    private List<EntityID> publishEventInternal(ContainerRequest containerRequest, 
            ContainerResponse containerResponse,
            Queue<PortletEvent> eventQueue, 
            List<EntityID> updatedPortlets, 
            int eventGeneration) throws ServiceException {
        if(eventGeneration == ContainerUtil.getMaxEventGeneration()) {
            if(logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "PSC_CSPCS011", String.valueOf(eventGeneration));
            }
            return updatedPortlets;
        }
        eventGeneration++;
        // Get the list of portlets from PortalWindowContext which returns it based on the EventPolicy
        List<EntityID> portletList = null;       
        try {
            DistributionType distributionType = ContainerUtil.getEventDistributionType(containerRequest);
            portletList = containerRequest.getPortletWindowContext().getPortletWindows(PortletType.ALL,
                    distributionType);
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "PSC_CSPCS026", new Object[] { portletList, distributionType });
            }
        } catch(PortletWindowContextException pwce) {
            // log and stop the event processing
            if(logger.isLoggable(Level.SEVERE)) {
                LogRecord logRecord = new LogRecord(Level.SEVERE, "PSC_CSPCS014");
                logRecord.setThrown(pwce);
                logRecord.setLoggerName(logger.getName());
                logger.log(logRecord);
            }
            return updatedPortlets;
        }
        Queue<PortletEvent> nextEventQueue = null;
        for(CoordinationSubscriber eventSubscriber: subscribers.values()){
            Queue<PortletEvent> localQueue = new ConcurrentLinkedQueue();
            for(PortletEvent portletEvent : eventQueue) {
                localQueue.add(portletEvent);
            }
            while(!localQueue.isEmpty()) {
                PortletEvent currentEvent = localQueue.poll();
                Map<EntityID, QName> portletsProcessingEventQNames = new ConcurrentHashMap<EntityID, QName>();
                List<EntityID> eventProcessingPortletList = new ArrayList<EntityID>();
                for(EntityID portletEntityIdInList: portletList) {
                    //If the currentEvent is a Container Event, check whether it needs to be sent any particular portlet
                    // that is present in the containerEventPortlets list
                    if(isContainerEvent(currentEvent.getEventHolder())) {
                        if(containerRequest.getEntityID() != null
                                && !containerRequest.getEntityID().equals(portletEntityIdInList)) {
                                continue;
                        }
                    }
                    DeploymentService deploymentServiceForPublishPortlet = getDeploymentService(portletEntityIdInList);
                    // For each portletEntity Id check whether it processes the event
                    // If it supports the event, add both the portletEntityId and the event QName to the Map
                    // and send the Map to CoordinationSubscriber for processing
                     
                    EventHolder eventHolder = deploymentServiceForPublishPortlet.verifySupportedProcessingEvent(containerRequest.getPortletWindowContext(),
                            portletEntityIdInList, currentEvent.getEventHolder() );
                    
                    if(logger.isLoggable(Level.FINER)) {
                        logger.log(Level.FINER, "PSC_CSPCS029",
                                new Object[] { portletEntityIdInList, currentEvent.getEventHolder(), eventHolder });
                    }
                    if(eventHolder != null) {
                        portletsProcessingEventQNames.put(portletEntityIdInList, eventHolder.getQName());
                        eventProcessingPortletList.add(portletEntityIdInList);
                    }
                }
                
                try {
                    // Process the event for all the portlets in the list
                    Queue<PortletEvent> eventNext = eventSubscriber.processEvent(containerRequest, containerResponse,
                            portletsProcessingEventQNames, currentEvent);
                    // Save the events returned in the next generation event queue
                    if(eventNext != null) {
                        if(nextEventQueue == null) {
                            nextEventQueue = new ConcurrentLinkedQueue<PortletEvent>();
                        }
                        for(PortletEvent event: eventNext){
                            nextEventQueue.add(event);
                        }
                    }
                    updatedPortlets.addAll(eventProcessingPortletList);
                } catch (ContentException ex) {
                    // log and continue the event distribution
                    if(logger.isLoggable(Level.SEVERE)) {
                        LogRecord logRecord = new LogRecord(Level.SEVERE, "PSC_CSPCS009");
                        logRecord.setParameters(new Object[]{currentEvent.getName()});
                        logRecord.setThrown(ex);
                        logRecord.setLoggerName(logger.getName());
                        logger.log(logRecord);
                    }
                } catch (ContainerException ex) {
                    // log and continue the event distribution
                    if(logger.isLoggable(Level.SEVERE)) {
                        LogRecord logRecord = new LogRecord(Level.SEVERE, "PSC_CSPCS009");
                        logRecord.setParameters(new Object[]{currentEvent.getName()});
                        logRecord.setThrown(ex);
                        logRecord.setLoggerName(logger.getName());
                        logger.log(logRecord);
                    }
                }
            }
        }
        if (nextEventQueue != null) {
            updatedPortlets = publishEventInternal(containerRequest, containerResponse, nextEventQueue, updatedPortlets, eventGeneration);
        }
        return updatedPortlets;
    }
    
    private boolean isContainerEvent(EventHolder currentEventHolder) {
        if(getContainerEventService() != null &&
                getContainerEventService().getSupportedEvents().contains(currentEventHolder)) {
            return true;
        }
        return false;
    }
    
    private ContainerEventService getContainerEventService() {
        return (ContainerEventService)ContainerUtil.getService(Service.CONTAINER_EVENT_SERVICE);
    }
    
    //This method returns DeploymentService based on portlet type
    private DeploymentService getDeploymentService(EntityID entityId){
        DeploymentService deploymentService = null;
        if(entityId.getPortletID() != null){
            deploymentService = (DeploymentService)ServiceManager.
                    getServiceManager().getService(Service.DEPLOYMENT_SERVICE_LOCAL);
        } else {
            deploymentService = (DeploymentService)ServiceManager.
                    getServiceManager().getService(Service.DEPLOYMENT_SERVICE_REMOTE);            
        }
        return deploymentService;
    }
        
    //This method returns CoordinationSubscriber based on portlet type
    private CoordinationSubscriber getCoordinationSubscriber(EntityID entityId){
        CoordinationSubscriber subscriber = null;
        if(entityId.getPortletID()!=null){
            subscriber = this.subscribers.get(ContainerType.PORTLET_CONTAINER);
        } else {
            subscriber = this.subscribers.get(ContainerType.WSRP_CONSUMER);            
        }
        return subscriber;
        
    }
}
