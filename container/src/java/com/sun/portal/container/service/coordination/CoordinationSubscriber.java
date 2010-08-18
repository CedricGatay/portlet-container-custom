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

import com.sun.portal.container.ContainerException;
import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.ContainerResponse;
import com.sun.portal.container.ContentException;
import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletEvent;
import java.util.Map;
import java.util.Queue;
import javax.xml.namespace.QName;

/**
 * The <code>CoordinationSubscriber</code> is responsible for processing events and render parameters.
 * Both PortletContainer and WSRP Consumer implements this interface.
 *
 */
public interface CoordinationSubscriber {
    
    /**
     * This is responsible for sending the event to the portlets.
     *
     * @param containerRequest The ContainerRequest object
     * @param containerResponse The ContainerResponse object
     * @param portletsProcessingEventQNames  the Map of portlets and the events that should sent to those portlets
     * @param event the event that has both name and payload
     *
     * @throws com.sun.portal.container.ContainerException when an error occurs in the container code.
     * @throws com.sun.portal.container.ContentException when an error occurs in the process event code.
     *
     * @return events set by the portlets that were processing the events
     */
    public Queue<PortletEvent> processEvent(ContainerRequest containerRequest, ContainerResponse containerResponse, 
            Map<EntityID, QName> portletsProcessingEventQNames, PortletEvent event) 
            throws ContainerException, ContentException;
  
   /**
     * This is responsible for processing the render parameters.
     * 
     * @param containerRequest The ContainerRequest object
     * @param containerResponse The ContainerResponse object
     * @param portletEntityId the entity Id of the portlet
     * @param renderParameters the Map of render parameters
     * @param publicRenderParameter true if the renderParameters is public
     */
    public void processRenderParameters(ContainerRequest containerRequest, ContainerResponse containerResponse,
            EntityID portletEntityId,  Map<String, String[]> renderParameters, boolean publicRenderParameter);
}
