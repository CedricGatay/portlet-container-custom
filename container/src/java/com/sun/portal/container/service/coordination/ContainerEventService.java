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

package com.sun.portal.container.service.coordination;

import com.sun.portal.container.EntityID;
import com.sun.portal.container.service.EventHolder;
import com.sun.portal.container.service.Service;
import java.io.Serializable;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

/**
 * The <code>ContainerEventService</code> provides mechanism for sending the
 * container events to the ContainerEventSubsribers.
 *
 */
public interface ContainerEventService extends Service {
    
    /**
     * Publishes the Container Event with the given payload. 
     * If the event is not supported, it is ignored.
     * 
     * @param eventQName  the event name to publish, must not be null
     * @param value the value of this event, must have a valid JAXB binding and be serializable, or null.
     * 
     * @return List of Portlets updated during event processing
     */
    public List<EntityID> setEvent(QName eventQName, Serializable value);
    
    /**
     * Publishes the Container Event with the given payload and is sent to the
     * portlet specified by the EntityID.
     * If the event is not supported, it is ignored.
     * 
     * @param eventQName  the event name to publish, must not be null
     * @param value the value of this event, must have a valid JAXB binding and be serializable, or null.
     * @param entityID EntityID of the portlet for which the event is published
     */
    public void setEvent(QName eventQName, Serializable value, 
            EntityID entityID);
    
    /**
     * Publishes the Container Event with the given payload. 
     * If the event is not supported, it is ignored.
     * 
     * @param eventQName  the event name to publish, must not be null
     * @param value the value of this event, must have a valid JAXB binding and be serializable, or null.
     * @param request the HttpServletRequest object 
     * @param response the HttpServletResponse object 
     * 
     * @return List of Portlets updated during event processing
     */
    public List<EntityID> setEvent(QName eventQName, Serializable value, 
            HttpServletRequest request, HttpServletResponse response);
    
    /**
     * Publishes the Container Event with the given payload. 
     * If the event is not supported, it is ignored.
     * 
     * @param eventQName  the event name to publish, must not be null
     * @param value the value of this event, must have a valid JAXB binding and be serializable, or null.
     * @param entityID EntityID of the portlet for which the event is published
     * @param request the HttpServletRequest object 
     * @param response the HttpServletResponse object 
     */
    public void setEvent(QName eventQName, Serializable value, EntityID entityID, 
            HttpServletRequest request, HttpServletResponse response);
    
    /**
     * Returns the list container events that is supported.
     * 
     * @return the list container events that is supported.
     */
    public List<EventHolder> getSupportedEvents();
    
    /**
     * Those that are interested in processing the container event 
     * should register using this method.
     *
     * @param containerEventSubscriber the ContainerEventSubscriber
     */
    public void registerSubscriber(ContainerEventSubscriber containerEventSubscriber);
}
