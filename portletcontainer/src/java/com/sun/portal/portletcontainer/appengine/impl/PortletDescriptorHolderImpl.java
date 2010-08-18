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
import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletID;
import com.sun.portal.container.service.EventHolder;
import com.sun.portal.container.service.PublicDescriptorHolderException;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.container.service.PortletDescriptorHolder;
import com.sun.portal.container.service.PublicRenderParameterHolder;
import com.sun.portal.container.service.Service;
import com.sun.portal.container.service.ServiceManager;
import com.sun.portal.container.service.coordination.ContainerEventService;
import com.sun.portal.portletcontainer.common.descriptor.EventDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PublicRenderParameterDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

/**
 * PortletDescriptorHolderImpl provides concrete implementation of PortletDescriptorHolder
 * interface.
 *
 */
public class PortletDescriptorHolderImpl implements PortletDescriptorHolder {
    
    static List<String> containerEvents = new ArrayList<String>();

	static {
        containerEvents.add("urn:oasis:names:tc:wsrp:v2:types");
	}

    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PortletDescriptorHolderImpl.class, "PAELogMessages");

    // Key = QName of the Event, value = list of portlet Ids that will be processing this event
    private Map<QName, List<PortletID>> eventProcessingPortlets;
    // Key = portlet Id, value = list of events it is supporting for processing
    private Map<PortletID, List<EventHolder>> portletSupportingProcessingEvents;
    // Key = QName of the Event, value = list of portlet Ids that will be publishing this event
    private Map<QName, List<PortletID>> eventPublishingPortlets;
    // Key = portlet Id, value = list of events it is supporting for publishing
    private Map<PortletID, List<EventHolder>> portletSupportingPublishingEvents;
    // Key = public render parameter holder, value = list of portlet Ids that will be supporting this public render parameter
    private Map<PublicRenderParameterHolder, List<PortletID>> publicRenderParameterSupportingPortlets;
    // Key = portlet Id, value = list of public render parameters it is supporting
    private Map<PortletID, List<PublicRenderParameterHolder>> portletSupportingPublicRenderParameters;
    // Key = Portlet Application Name, value = List of public render parameter holders in that application
    private Map<String, List<PublicRenderParameterHolder>> appPublicRenderParameterHolders;
    // Key = Portlet Application Name, value = List of event holders in that application
    private Map<String, List<EventHolder>> appEventHolders;
    
    public PortletDescriptorHolderImpl() {
        eventProcessingPortlets = new ConcurrentHashMap<QName, List<PortletID>>();
        portletSupportingProcessingEvents = new ConcurrentHashMap<PortletID, List<EventHolder>>();
        eventPublishingPortlets = new ConcurrentHashMap<QName, List<PortletID>>();
        portletSupportingPublishingEvents = new ConcurrentHashMap<PortletID, List<EventHolder>>();
        publicRenderParameterSupportingPortlets = new ConcurrentHashMap<PublicRenderParameterHolder, List<PortletID>>();
        portletSupportingPublicRenderParameters = new ConcurrentHashMap<PortletID, List<PublicRenderParameterHolder>>();
        appPublicRenderParameterHolders = new ConcurrentHashMap<String, List<PublicRenderParameterHolder>>();
        appEventHolders = new ConcurrentHashMap<String, List<EventHolder>>();
    }
    
    public void load(Object descriptor) {
        PortletAppDescriptor portletAppDescriptor = (PortletAppDescriptor)descriptor;
        String portletAppName = portletAppDescriptor.getName();
        logger.log(Level.FINEST, "PSPL_PAECSPPA0018", portletAppName);
        // Save the EventHolders for this PortletAppDescriptor
        if(!portletAppDescriptor.getEventDescriptors().isEmpty()) {
            for(EventDescriptor eventDescriptor: portletAppDescriptor.getEventDescriptors()) {
                // load Event Holder objects of the portlet application
                List<EventHolder> eventHolders = this.appEventHolders.get(portletAppName);
                if(eventHolders == null) {
                    eventHolders = new CopyOnWriteArrayList<EventHolder>();
                }
                eventHolders.add(eventDescriptor.getEventHolder());
                appEventHolders.put(portletAppName, eventHolders);
            }
        }
        
        // Save the PublicRenderParameterHolders for this PortletAppDescriptor
        if(!portletAppDescriptor.getPublicRenderParameterDescriptors().isEmpty()) {
            for(PublicRenderParameterDescriptor publicRenderParameterDescriptor: portletAppDescriptor.getPublicRenderParameterDescriptors()) {
                // load Public Render Parameter Holder object for supporting render parameters of the portlet
                List<PublicRenderParameterHolder> publicRenderParameterHolders = this.appPublicRenderParameterHolders.get(portletAppName);
                if(publicRenderParameterHolders == null) {
                    publicRenderParameterHolders = new CopyOnWriteArrayList<PublicRenderParameterHolder>();
                }
                publicRenderParameterHolders.add(publicRenderParameterDescriptor.getPublicRenderParameterHolder());
                appPublicRenderParameterHolders.put(portletAppName, publicRenderParameterHolders);
            }
        }
        // load processing events for portlet
        createEventProcessingPortlets(portletAppDescriptor.getProcessingEvents());
        
        //load processing event qnames for portlet 
        createSupportedEventProcessingPortlets(portletAppDescriptor.getSupportedProcessingEvents());
        
        // load publishing events for portlet
        createEventPublishingPortlets(portletAppDescriptor.getPublishingEvents());
        
        // load supporting render parameters for portlet
        createPublicRenderParameterPortlets(portletAppDescriptor.getPublicRenderParameters());
        
        // print information
        printInformation();
    }
    
    public void remove(Object descriptor) {
        PortletAppDescriptor portletAppDescriptor = (PortletAppDescriptor)descriptor;
        String portletAppName = portletAppDescriptor.getName();
        logger.log(Level.FINEST, "PSPL_PAECSPPA0019", portletAppName);
        List<PortletID> portletIDs = portletAppDescriptor.getPortletsDescriptor().getPortletIDs();
        
        removeEventProcessingPortlets(portletAppDescriptor.getProcessingEvents(), portletIDs);
        removeEventPublishingPortlets(portletAppDescriptor.getPublishingEvents(), portletIDs);
        removeSupportedEventProcessingPortlets(portletAppDescriptor.getSupportedProcessingEvents(), portletIDs);
        removePortletSupportingProcessingEvents(portletIDs);
        removePortletSupportingPublishingEvents(portletIDs);
        removeEventHolders(portletAppName);
        // get public render parameter descriptors from the descriptor
        List<PublicRenderParameterDescriptor> publicRenderParameterDescriptors = portletAppDescriptor.getPublicRenderParameterDescriptors();
        removePublicRenderParameterSupportingPortlets(publicRenderParameterDescriptors, portletIDs);
        removePortletSupportingPublicRenderParameters(portletIDs);
        removePublicRenderParameterHolders(portletAppName);
        // print information
        printInformation();
    }
    
    public EventHolder verifySupportedPublishingEvent(EntityID portletEntityId, EventHolder supportedEventHolder) {
        // Check whether it supports any ContainerEvent, if yes do not publish it
        if(getContainerEventService() != null &&
                getContainerEventService().getSupportedEvents().contains(supportedEventHolder.getQName())) {
            return null;
        }
        List<PortletID> portlets = getEventPublishingProcessingPortlets(this.eventPublishingPortlets, supportedEventHolder.getQName());
        PortletID portletID = portletEntityId.getPortletID();
        if(portlets.contains(portletID)) {
            return supportedEventHolder;
        }
        return null;
    }
    
    public List<EventHolder> getSupportedPublishingEventHolders(EntityID portletEntityId) {
        PortletID portletID = portletEntityId.getPortletID();
        List<EventHolder> eventHolders = null;
        if(portletID != null) {
            eventHolders = this.portletSupportingPublishingEvents.get(portletID);
        }
        if(eventHolders == null) {
            return Collections.emptyList();
        }
        return eventHolders;
    }

	public List<PortletID> getEventPublishingPortlets(QName eventQname) {
        List<PortletID> portletIDs = null;
        if(eventQname != null) {
            portletIDs = this.eventPublishingPortlets.get(eventQname);
        }
        if(portletIDs == null) {
            return Collections.emptyList();
        }
        return portletIDs;
	}
	
	public Map<QName, List<PortletID>> getAllEventPublishingPortlets() {
		return Collections.unmodifiableMap(this.eventPublishingPortlets);
	}

    public EventHolder verifySupportedProcessingEvent(EntityID portletEntityId, EventHolder supportedEventHolder) {
        List<PortletID> portlets = getEventPublishingProcessingPortlets(this.eventProcessingPortlets, supportedEventHolder.getQName());
        PortletID portletID = portletEntityId.getPortletID();
        if(portlets.contains(portletID)) {
            return supportedEventHolder;
        } else {
            // Check whether supportedEventQName is present in the aliases of any event definition
            // or whether the supportedEventQName is same as the qname of any event definition 
            // or whether aliases of the supportedEvent contains the eventQName
			// or whether any qname is common in both aliases
            // if yes use the event qname of that event definition to get the portlet list
            for(List<EventHolder> allEventHolders: this.appEventHolders.values()) {
                for(EventHolder eventHolder: allEventHolders) {
                    if(eventHolder.getAliases().contains(supportedEventHolder.getQName())
                            || eventHolder.getQName().equals(supportedEventHolder.getQName())
                            || supportedEventHolder.getAliases().contains(eventHolder.getQName())
							|| checkAliases(eventHolder, supportedEventHolder)) {
                        portlets = getEventPublishingProcessingPortlets(this.eventProcessingPortlets, eventHolder.getQName());
                        if(portlets != null && portlets.contains(portletID)){
                            return eventHolder;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public List<EventHolder> getSupportedProcessingEventHolders(EntityID portletEntityId) {
        PortletID portletID = portletEntityId.getPortletID();
        List<EventHolder> eventHolders = null;
        if(portletID != null) {
            eventHolders = this.portletSupportingProcessingEvents.get(portletID);
        }
        if(eventHolders == null) {
            return Collections.emptyList();
        }
        return eventHolders;
    }

	public List<PortletID> getEventProcessingPortlets(QName eventQname) {
        List<PortletID> portletIDs = null;
        if(eventQname != null) {
            portletIDs = this.eventProcessingPortlets.get(eventQname);
        }
        if(portletIDs == null) {
            return Collections.emptyList();
        }
        return portletIDs;
	}
	
	public Map<QName, List<PortletID>> getAllEventProcessingPortlets() {
		return Collections.unmodifiableMap(this.eventProcessingPortlets);
	}

    public Map<String, String> verifySupportedPublicRenderParameters(EntityID portletEntityId, 
            List<PublicRenderParameterHolder> supportedPublicRenderParameterHolders) {
        Map<String, String> supportedPublicRenderParametersMap = null;        
        if(supportedPublicRenderParameterHolders != null) {
            supportedPublicRenderParametersMap = new HashMap<String, String>(supportedPublicRenderParameterHolders.size());
            for(PublicRenderParameterHolder supportedPublicRenderParameterHolder: supportedPublicRenderParameterHolders) {
                List<PortletID> portlets = this.publicRenderParameterSupportingPortlets.get(supportedPublicRenderParameterHolder);
                if(portlets != null) {
                    PortletID portletID = portletEntityId.getPortletID();
                    if(portlets.contains(portletID)){
                        supportedPublicRenderParametersMap.put(supportedPublicRenderParameterHolder.getIdentifier(), 
                                supportedPublicRenderParameterHolder.getIdentifier());
                    } else {
                        // Check whether supportedPublicRenderParameterHolder's qname is present in the aliases of any 
                        // public render parameter definition or whether supportedPublicRenderParameterHolder's qname is 
                        // same as the qname of any public render parameter definition or whether aliases of the 
                        // supportedPublicRenderParameterHolder contains the qname of any public render parameter definition
                        // if yes use the render parameter qname of that public render parameter definition to get the portlet list
                        for(List<PublicRenderParameterHolder> allPublicRenderParameterHolders: this.appPublicRenderParameterHolders.values()) {
                            for(PublicRenderParameterHolder publicRenderParameterHolder: allPublicRenderParameterHolders) {
                                List<QName> aliases = publicRenderParameterHolder.getAliases();
                                if(aliases.contains(supportedPublicRenderParameterHolder.getQName())
                                        || publicRenderParameterHolder.getQName().equals(supportedPublicRenderParameterHolder.getQName())
                                        || supportedPublicRenderParameterHolder.getAliases().contains(publicRenderParameterHolder.getQName())) {
                                    portlets = this.publicRenderParameterSupportingPortlets.get(publicRenderParameterHolder);
                                    if(portlets != null && portlets.contains(portletID)){
                                        supportedPublicRenderParametersMap.put(supportedPublicRenderParameterHolder.getIdentifier(), 
                                                publicRenderParameterHolder.getIdentifier());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(supportedPublicRenderParametersMap == null)
            return Collections.emptyMap();
        else 
            return supportedPublicRenderParametersMap;
    }

    public List<PublicRenderParameterHolder> getSupportedPublicRenderParameterHolders(EntityID portletEntityId,
            Map<String, String[]> renderParameters) {
        PortletID portletID = portletEntityId.getPortletID();
        List<PublicRenderParameterHolder> publicRenderParameterHolders = new ArrayList<PublicRenderParameterHolder>();
        if(portletID != null) {
            List<PublicRenderParameterHolder> supportingPublicRenderParameterHolders = this.portletSupportingPublicRenderParameters.get(portletID);
            Set<Map.Entry<String, List<PublicRenderParameterHolder>>> entries = this.appPublicRenderParameterHolders.entrySet();
            for(Map.Entry<String, List<PublicRenderParameterHolder>> mapEntry : entries) {
                if(supportingPublicRenderParameterHolders != null) {
                    if(renderParameters == null) {
                        for(PublicRenderParameterHolder publicRenderParameterHolder: mapEntry.getValue()) {
                            if(supportingPublicRenderParameterHolders.contains(publicRenderParameterHolder)) {
                                publicRenderParameterHolders.add(publicRenderParameterHolder);
                            }
                        }
                    } else {
                        for(PublicRenderParameterHolder publicRenderParameterHolder: mapEntry.getValue()) {
                            if(renderParameters.containsKey(publicRenderParameterHolder.getIdentifier())
                                    && supportingPublicRenderParameterHolders.contains(publicRenderParameterHolder)) {
                                publicRenderParameterHolders.add(publicRenderParameterHolder);
                            }
                        }
                    }
                }
            }
        }
        return publicRenderParameterHolders;
    }

    public void setEventHolder(String portletAppName, EventHolder eventHolder)
            throws PublicDescriptorHolderException {
        if(this.appEventHolders != null) {
            // load Event Holder objects of the portlet application
            List<EventHolder> eventHolders = this.appEventHolders.get(portletAppName);
            if(eventHolders == null) {
                eventHolders = new CopyOnWriteArrayList<EventHolder>();
            }
            if(!eventHolders.contains(eventHolder)) {
                eventHolders.add(eventHolder);
                appEventHolders.put(portletAppName, eventHolders);
            } else {
                throw new PublicDescriptorHolderException("The Event:" + eventHolder.getQName() + " is already present");
            }
        }
        // print information
        printInformation();
    }

    public void setSupportedPublishingEvent(EntityID portletEntityId,
                                            EventHolder eventHolder) throws PublicDescriptorHolderException {
        if(this.portletSupportingPublishingEvents != null) {
            List<EventHolder> eventHolders = this.portletSupportingPublishingEvents.get(portletEntityId.getPortletID());
            if(eventHolders != null && eventHolders.contains(eventHolder)) {
                throw new PublicDescriptorHolderException("The SupportingPublishingEvent:" + eventHolder.getQName() + " is already present");
            }
        }
        List<EventHolder> eventHolders = new ArrayList<EventHolder>();
        eventHolders.add(eventHolder);
        Map<PortletID, List<EventHolder>> publishingEvents = new HashMap<PortletID, List<EventHolder>>();
        publishingEvents.put(portletEntityId.getPortletID(), eventHolders);
        createEventPublishingPortlets(publishingEvents);
        // print information
        printInformation();
    }

    public void setSupportedProcessingEvent(EntityID portletEntityId,
                                            EventHolder eventHolder) throws PublicDescriptorHolderException {
        if(this.portletSupportingProcessingEvents != null) {
            List<EventHolder> eventHolders = this.portletSupportingPublishingEvents.get(portletEntityId.getPortletID());
            if(eventHolders != null && eventHolders.contains(eventHolder)) {
                throw new PublicDescriptorHolderException("The SupportingProcessingEvent:" + eventHolder.getQName() + " is already present");
            }
        }
        List<EventHolder> eventHolders = new ArrayList<EventHolder>();
        eventHolders.add(eventHolder);
        Map<PortletID, List<EventHolder>> processingEvents = new HashMap<PortletID, List<EventHolder>>();
        processingEvents.put(portletEntityId.getPortletID(), eventHolders);
        createEventProcessingPortlets(processingEvents);
        // print information
        printInformation();
    }

    public void setPublicRenderParameterHolder(String portletAppName,
                                               PublicRenderParameterHolder publicRenderParameterHolder)
            throws PublicDescriptorHolderException {
        if(this.appPublicRenderParameterHolders != null) {
            // load PublicRenderParameterHolder objects of the portlet application
            List<PublicRenderParameterHolder> publicRenderParameterHolders = this.appPublicRenderParameterHolders.get(portletAppName);
            if(publicRenderParameterHolders == null) {
                publicRenderParameterHolders = new CopyOnWriteArrayList<PublicRenderParameterHolder>();
            }
            if(!publicRenderParameterHolders.contains(publicRenderParameterHolder)) {
                publicRenderParameterHolders.add(publicRenderParameterHolder);
                appPublicRenderParameterHolders.put(portletAppName, publicRenderParameterHolders);
            } else {
                throw new PublicDescriptorHolderException("The PublicRenderParameter:" + publicRenderParameterHolder.getQName() + " is already present");
            }
        }
        // print information
        printInformation();
    }

    public void setSupportedPublicRenderParameter(EntityID portletEntityId,
                                                  PublicRenderParameterHolder publicRenderParameterHolder)
            throws PublicDescriptorHolderException {
        if(this.portletSupportingPublicRenderParameters != null) {
            List<PublicRenderParameterHolder> publicRenderParameterHolders = this.portletSupportingPublicRenderParameters.get(portletEntityId.getPortletID());
            if(publicRenderParameterHolders != null && publicRenderParameterHolders.contains(publicRenderParameterHolder)) {
                throw new PublicDescriptorHolderException("The SupportedPublicRenderParameter:" + 
                        publicRenderParameterHolder.getQName() + " is already present");
            }
        }
        List<PublicRenderParameterHolder> publicRenderParameterHolders = new ArrayList<PublicRenderParameterHolder>();
        publicRenderParameterHolders.add(publicRenderParameterHolder);
        Map<PortletID, List<PublicRenderParameterHolder>> publicRenderParameters = 
                new HashMap<PortletID, List<PublicRenderParameterHolder>>();
        publicRenderParameters.put(portletEntityId.getPortletID(), publicRenderParameterHolders);
        createPublicRenderParameterPortlets(publicRenderParameters);
        // print information
        printInformation();
    }

	private boolean checkAliases(EventHolder eventHolder, EventHolder supportedEventHolder) {
		boolean containsAlias = false;
		for(QName qname : eventHolder.getAliases()) {
			if(supportedEventHolder.getAliases().contains(qname)) {
				containsAlias = true;
				break;
			}
		}
		return containsAlias;
	}
    
    /**
     * Returns the list of portlets that support the event processing/publishing.
     *
     * @param eventPublishingProcessingPortlets a Map of QName of the event 
     *                                          and list of portlets that support it
     * @param eventQName the QName of the event
     * @return list of portlets that support the event processing/publishing.
     */
    private List<PortletID> getEventPublishingProcessingPortlets(Map<QName, List<PortletID>> eventPublishingProcessingPortlets, 
            QName eventQName) {
        List<PortletID> portlets = new ArrayList<PortletID>();
        if(eventQName != null) {
            Set<Map.Entry<QName, List<PortletID>>> entries = eventPublishingProcessingPortlets.entrySet();
            for(Map.Entry<QName, List<PortletID>> mapEntry : entries) {
                QName descriptorEventQName = mapEntry.getKey();
                if(descriptorEventQName.equals(eventQName)) {
                    portlets.addAll(mapEntry.getValue());
                }
            }
        }
        return portlets;
    }
    
    /**
     * Generates a list of portlets that are processing events or publishing the events
     *
     * @param portletProcessingPublishingEventsFromDescriptor Map where portlet id is the key and list of events is the value
     * @param eventProcessingPublishingPortlets Map where event is the key and list of portlets is the value
     */
    private void setEventPortlets(Map<PortletID, List<EventHolder>> portletProcessingPublishingEventsFromDescriptor,
            Map<QName, List<PortletID>> eventProcessingPublishingPortlets,
            Map<PortletID, List<EventHolder>> portletSupportingProcessingPublishingEvents) {
        Set<Map.Entry<PortletID, List<EventHolder>>> entries = portletProcessingPublishingEventsFromDescriptor.entrySet();
        for(Map.Entry<PortletID, List<EventHolder>> mapEntry : entries) {
            for(EventHolder eventHolder: mapEntry.getValue()){
                List<EventHolder> supportingEventHolders = portletSupportingProcessingPublishingEvents.get(mapEntry.getKey());
                if(supportingEventHolders == null) {
                    supportingEventHolders = new ArrayList<EventHolder>();
                }
                if(!supportingEventHolders.contains(eventHolder)) {
                    supportingEventHolders.add(eventHolder);
                }
                portletSupportingProcessingPublishingEvents.put(mapEntry.getKey(), supportingEventHolders);

                List<PortletID> portletIDs = eventProcessingPublishingPortlets.get(eventHolder.getQName());
                if(portletIDs == null) {
                    portletIDs = new ArrayList<PortletID>();
                }
                if(!portletIDs.contains(mapEntry.getKey())) {
                    portletIDs.add(mapEntry.getKey());
                }
                eventProcessingPublishingPortlets.put(eventHolder.getQName(), portletIDs);
            }
        }
    }
    
    private void setPublicRenderParameterPortlets(Map<PortletID, List<PublicRenderParameterHolder>> portletPublicRenderParametersFromDescriptor,
            Map<PublicRenderParameterHolder, List<PortletID>> publicRenderParameterSupportingPortlets) {
        Set<Map.Entry<PortletID, List<PublicRenderParameterHolder>>> entries = portletPublicRenderParametersFromDescriptor.entrySet();
        for(Map.Entry<PortletID, List<PublicRenderParameterHolder>> mapEntry : entries) {
            for(PublicRenderParameterHolder publicRenderParameterHolder: mapEntry.getValue()){
                List<PublicRenderParameterHolder> supportingPublicRenderParameterHolders = portletSupportingPublicRenderParameters.get(mapEntry.getKey());
                if(supportingPublicRenderParameterHolders == null) {
                    supportingPublicRenderParameterHolders = new ArrayList<PublicRenderParameterHolder>();
                }
                if(!supportingPublicRenderParameterHolders.contains(publicRenderParameterHolder)) {
                    supportingPublicRenderParameterHolders.add(publicRenderParameterHolder);
                }
                this.portletSupportingPublicRenderParameters.put(mapEntry.getKey(), supportingPublicRenderParameterHolders);
                
                List<PortletID> portletIDs = publicRenderParameterSupportingPortlets.get(publicRenderParameterHolder);
                if(portletIDs == null) {
                    portletIDs = new ArrayList<PortletID>();
                }
                if(!portletIDs.contains(mapEntry.getKey())) {
                    portletIDs.add(mapEntry.getKey());
                }
                publicRenderParameterSupportingPortlets.put(publicRenderParameterHolder, portletIDs);
            }
        }
    }
    
    // This adds those processing events that are not declared in event definition(i.e container events)
    // The events should not be a wildcard
    private void createSupportedEventProcessingPortlets(Map<PortletID, List<QName>> portletProcessingEvents) {
        Set<Map.Entry<PortletID, List<QName>>> entries = portletProcessingEvents.entrySet();
        for(Map.Entry<PortletID, List<QName>> mapEntry : entries) {
            for(QName qname: mapEntry.getValue()){
                // If the local part ends with ".", it means its a wildcard, ignore it
                if(!qname.getLocalPart().endsWith(".")
					&& containerEvents.contains(qname.getNamespaceURI())) {
                    List<PortletID> portletIDs = this.eventProcessingPortlets.get(qname);
                    if(portletIDs == null) {
                        portletIDs = new ArrayList<PortletID>();
                    }
                    if(!portletIDs.contains(mapEntry.getKey())) {
                        portletIDs.add(mapEntry.getKey());
                        this.eventProcessingPortlets.put(qname, portletIDs);
                    }
                }
            }
        }
    }

    private void createEventProcessingPortlets(Map<PortletID, List<EventHolder>> portletProcessingEventsFromDescriptor) {
        setEventPortlets(portletProcessingEventsFromDescriptor, 
                this.eventProcessingPortlets, this.portletSupportingProcessingEvents);
    }
    
    private void createEventPublishingPortlets(Map<PortletID, List<EventHolder>> portletPublishingEventsFromDescriptor) {
        setEventPortlets(portletPublishingEventsFromDescriptor, 
                this.eventPublishingPortlets, this.portletSupportingPublishingEvents);
    }
    
    private void createPublicRenderParameterPortlets(Map<PortletID, List<PublicRenderParameterHolder>> portletPublicRenderParametersFromDescriptor) {
        setPublicRenderParameterPortlets(portletPublicRenderParametersFromDescriptor, 
                this.publicRenderParameterSupportingPortlets);
    }

    // Remove the portlets associated with the qname and 
    // if there  are no portlets associated with the qname remove the qname from the Map
    private void removeEventProcessingPortlets(Map<PortletID, List<EventHolder>> portletProcessingEvents,
            List<PortletID> portletIDs) {
        Set<Map.Entry<PortletID, List<EventHolder>>> entries = portletProcessingEvents.entrySet();
        for(Map.Entry<PortletID, List<EventHolder>> mapEntry : entries) {
            for(EventHolder eventHolder: mapEntry.getValue()){
                List<PortletID> tempPortletIDs = 
                        this.eventProcessingPortlets.get(eventHolder.getQName());
                if(tempPortletIDs != null) {
                    for(PortletID portletID : portletIDs) {
                        tempPortletIDs.remove(portletID);
                    }
                    if(tempPortletIDs.isEmpty()) {
                        this.eventProcessingPortlets.remove(eventHolder.getQName());
                    }
                }
            }
        }
    }

    // Remove the portlets associated with the qname and 
    // if there  are no portlets associated with the qname remove the qname from the Map
    private void removeSupportedEventProcessingPortlets(Map<PortletID, List<QName>> supportedProcessingEvents,
                                                        List<PortletID> portletIDs) {
        Set<Map.Entry<PortletID, List<QName>>> entries = supportedProcessingEvents.entrySet();
        for(Map.Entry<PortletID, List<QName>> mapEntry : entries) {
            for(QName qname: mapEntry.getValue()){
                List<PortletID> tempPortletIDs = 
                        this.eventProcessingPortlets.get(qname);
                if(tempPortletIDs != null) {
                    for(PortletID portletID : portletIDs) {
                        tempPortletIDs.remove(portletID);
                    }
                    if(tempPortletIDs.isEmpty()) {
                        this.eventProcessingPortlets.remove(qname);
                    }
                }
            }
        }
    }

    // Remove the portlets associated with the qname and 
    // if there  are no portlets associated with the qname remove the qname from the Map
    private void removeEventPublishingPortlets(Map<PortletID, List<EventHolder>> portletPublishingEvents,
            List<PortletID> portletIDs) {
        Set<Map.Entry<PortletID, List<EventHolder>>> entries = portletPublishingEvents.entrySet();
        for(Map.Entry<PortletID, List<EventHolder>> mapEntry : entries) {
            for(EventHolder eventHolder: mapEntry.getValue()){
                List<PortletID> tempPortletIDs = 
                        this.eventPublishingPortlets.get(eventHolder.getQName());
                if(tempPortletIDs != null) {
                    for(PortletID portletID : portletIDs) {
                        tempPortletIDs.remove(portletID);
                    }
                    if(tempPortletIDs.isEmpty()) {
                        this.eventPublishingPortlets.remove(eventHolder.getQName());
                    }
                }
            }
        }
    }

    private void removePortletSupportingProcessingEvents(List<PortletID> portletIDs) {
        if(portletIDs != null) {
            for(PortletID portletID : portletIDs) {
                this.portletSupportingProcessingEvents.remove(portletID);
            }
        }
    }

    private void removePortletSupportingPublishingEvents(List<PortletID> portletIDs) {
        if(portletIDs != null) {
            for(PortletID portletID : portletIDs) {
                this.portletSupportingPublishingEvents.remove(portletID);
            }
        }
    }

    private void removePublicRenderParameterSupportingPortlets(List<PublicRenderParameterDescriptor> publicRenderParameterDescriptors,
            List<PortletID> portletIDs) {
        for(PublicRenderParameterDescriptor publicRenderParameterDescriptor: publicRenderParameterDescriptors) {
            if(publicRenderParameterDescriptor.getPublicRenderParameterHolder() != null) {
                List<PortletID> tempPortletIDs = 
                        publicRenderParameterSupportingPortlets.get(publicRenderParameterDescriptor.getPublicRenderParameterHolder());
                for(PortletID portletID : portletIDs) {
                    tempPortletIDs.remove(portletID);
                }
                if(tempPortletIDs.isEmpty()) {
                    publicRenderParameterSupportingPortlets.remove(publicRenderParameterDescriptor.getPublicRenderParameterHolder());
                }
            }
        }
    }
    
    private void removePortletSupportingPublicRenderParameters(List<PortletID> portletIDs) {
        if(portletIDs != null) {
            for(PortletID portletID : portletIDs) {
                portletSupportingPublicRenderParameters.remove(portletID);
            }
        }
    }

    private void removePublicRenderParameterHolders(String portletAppName) {
        appPublicRenderParameterHolders.remove(portletAppName);
    }
    
    private void removeEventHolders(String portletAppName) {
        appEventHolders.remove(portletAppName);
    }
    
    private ContainerEventService getContainerEventService() {
        return (ContainerEventService)ServiceManager.getServiceManager().getService(Service.CONTAINER_EVENT_SERVICE);
    }

    private void printInformation() {
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "PSPL_PAECSPPA0020", this.eventProcessingPortlets);
            logger.log(Level.FINER, "PSPL_PAECSPPA0021", this.eventPublishingPortlets);
            logger.log(Level.FINER, "PSPL_PAECSPPA0034", this.portletSupportingProcessingEvents);
            logger.log(Level.FINER, "PSPL_PAECSPPA0035", this.portletSupportingPublishingEvents);
            logger.log(Level.FINER, "PSPL_PAECSPPA0027", this.publicRenderParameterSupportingPortlets);
            logger.log(Level.FINER, "PSPL_PAECSPPA0028", this.portletSupportingPublicRenderParameters);
            logger.log(Level.FINER, "PSPL_PAECSPPA0029", this.appPublicRenderParameterHolders);
            logger.log(Level.FINER, "PSPL_PAECSPPA0033", this.appEventHolders);
        }
    }

}
