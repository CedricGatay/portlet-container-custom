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
import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletEvent;
import com.sun.portal.container.service.EventHolder;
import com.sun.portal.container.service.ServiceAdapter;
import com.sun.portal.container.service.coordination.ContainerEventService;
import com.sun.portal.container.service.coordination.ContainerEventSubscriber;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

/**
 * ContainerEventServiceImpl provides concrete implementation for the ContainerEventService.
 *
 */
public class ContainerEventServiceImpl extends ServiceAdapter implements ContainerEventService {
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(ContainerEventServiceImpl.class, "CLogMessages");
    private List<ContainerEventSubscriber> subscribers;
    private List<EventHolder> supportedEvents;
    private static final String DESCRIPTION = "Responsible for sending container events to all the registered subscribers";
    private static final String CONTAINER_EVENT_NAMESPACE = "urn:oasis:names:tc:wsrp:v2:types";
    
    @Override
    public void init(ServletContext context) {
        if(subscribers == null)
            subscribers = new CopyOnWriteArrayList<ContainerEventSubscriber>();
        supportedEvents = new ArrayList<EventHolder>();
        initContainerEvents();
    }
    
    @Override
    public String getName() {
        return CONTAINER_EVENT_SERVICE;
    }
    
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
    
    @Override
    public void destroy() {
        subscribers = null;
    }
    
    public List<EntityID> setEvent(QName eventQName, Serializable value) {
        return setContainerEvent(eventQName, value, null);
    }

    public void setEvent(QName eventQName, Serializable value, 
            EntityID entityID) {
        setContainerEvent(eventQName, value, entityID);
    }

    public List<EntityID> setEvent(QName eventQName, Serializable value,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        return setContainerEvent(eventQName, value, null, request, response);
    }

    public void setEvent(QName eventQName, Serializable value, EntityID entityID,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        setContainerEvent(eventQName, value, entityID, request , response);
    }

    public List<EventHolder> getSupportedEvents() {
        return this.supportedEvents;
    }
    
    private List<EntityID> setContainerEvent(QName eventQName, Serializable value, 
                                    EntityID entityID,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        PortletEvent event = getPortletEvent(eventQName, value);
        List<EntityID> portlets = null;
        if(this.supportedEvents.contains(event.getEventHolder())) {
            for(ContainerEventSubscriber containerEventSubscriber: subscribers){
                try {
                    if(request == null && response == null) {
                        portlets = 
                                containerEventSubscriber.processContainerEvent(event, entityID);
                    } else {
                        portlets = 
                                containerEventSubscriber.processContainerEvent(event, entityID, 
                                                                request, response);
                    }
                } catch (ContainerException ex) {
                    if(logger.isLoggable(Level.WARNING)) {
                        logger.log(Level.WARNING, "PSC_CSPCS025", new String[] { ex.toString(), event.getName()});
                    }
                }
            }
        }
        return portlets;
    }

    private List<EntityID> setContainerEvent(QName eventQName, Serializable value, 
                                    EntityID entityID) {
        return setContainerEvent(eventQName, value, entityID, null, null);
    }
    
    private PortletEvent getPortletEvent(QName eventQName, Serializable value) {
        PortletEvent event = new PortletEvent(getEventHolder(eventQName, value), eventQName, value);
        return event;
    }

    private EventHolder getEventHolder(QName eventQName, Serializable value) {
        if(eventQName == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        String valueType = null;
        if(value != null) {
            valueType = value.getClass().getName();
        }

        EventHolder eventHolder = new EventHolder(Collections.EMPTY_MAP, eventQName, 
                Collections.EMPTY_LIST, valueType);
        return eventHolder;
    }
    
    public void registerSubscriber(ContainerEventSubscriber containerEventSubscriber) {
        if(!this.subscribers.contains(containerEventSubscriber)) {
            this.subscribers.add(containerEventSubscriber);
        }
    }
    
    
    //TODO: get the list from xml file
    private void initContainerEvents() {
        // Login Event
        QName qname = new QName(CONTAINER_EVENT_NAMESPACE, "login");
        this.supportedEvents.add(getEventHolder(qname, null));
        qname = new QName(CONTAINER_EVENT_NAMESPACE, "logout");
        this.supportedEvents.add(getEventHolder(qname, null));
        qname = new QName(CONTAINER_EVENT_NAMESPACE, "eventHandlingFailed");
        this.supportedEvents.add(getEventHolder(qname, null));
        qname = new QName(CONTAINER_EVENT_NAMESPACE, "newNavigationalContextScope");
        this.supportedEvents.add(getEventHolder(qname, null));
        qname = new QName(CONTAINER_EVENT_NAMESPACE, "newMode");
        this.supportedEvents.add(getEventHolder(qname, null));
        qname = new QName(CONTAINER_EVENT_NAMESPACE, "newWindowState");
        this.supportedEvents.add(getEventHolder(qname, null));
    }
}
