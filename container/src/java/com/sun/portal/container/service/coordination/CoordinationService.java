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

import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.ContainerResponse;
import com.sun.portal.container.ContainerType;
import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletEvent;
import com.sun.portal.container.service.Service;
import com.sun.portal.container.service.ServiceException;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * The <code>CoordinationService</code> provides mechanism for coordinating between portlets. 
 * This can be either eventing or public render parameters.
 *
 */
public interface CoordinationService extends Service {
    
    /**
     * This is responsible for publishing the events that are present in the queue.
     *
     * @param containerRequest The ContainerRequest object
     * @param containerResponse The ContainerResponse object
     * @param eventQueue contains the portlet events
     * @throws com.sun.portal.container.service.ServiceException when an exception occurs during the execution.
     *
     * @return List of Portlets updated during event processing
     *
     */
    public List<EntityID> publishEvent(ContainerRequest containerRequest, ContainerResponse containerResponse,
            Queue<PortletEvent> eventQueue) throws ServiceException;
    
    /**
     * This is responsible for setting the render parameters.
     *
     * @param containerRequest The ContainerRequest object
     * @param containerResponse The ContainerResponse object
     * @param portletEntityId The portlet entity Id
     * @param renderParameters The Map of render parameters
     */
    public void setRenderParameters(ContainerRequest containerRequest, ContainerResponse containerResponse,
            EntityID portletEntityId, Map<String, String[]> renderParameters);
    
    /**
     * Those that are interested in processing the event should register using this method.
     *
     * @param containerType the ContainerType
     * @param coordinationSubscriber the CoordinationSubscriber
     */
    public void registerSubscriber(ContainerType containerType, CoordinationSubscriber coordinationSubscriber);
}
