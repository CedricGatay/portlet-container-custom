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
import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletEvent;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The <code>CoordinationSubscriber</code> is responsible for processing the container events
 *
 */
public interface ContainerEventSubscriber {

    /**
     * This is responsible for sending the event to the portlets.
     *
     * @param event the event that has name and may have payload
     * @param portletEntityID  the entity id of the portlet for which the event should be sent. Can be null.
     *                         If null, the event is sent to all portlets
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     *
     * @throws com.sun.portal.container.ContainerException when an error occurs in the container code.
     * 
     * @return List of Portlets updated during event processing
     */
    public List<EntityID> processContainerEvent(PortletEvent event, EntityID portletEntityID,
            HttpServletRequest request, HttpServletResponse response) 
            throws ContainerException;
    
    /**
     * This is responsible for sending the event to the portlets.
     *
     * @param event the event that has name and may have payload
     * @param portletEntityID  the entity id of the portlet for which the event should be sent. Can be null.
     *                         If null, the event is sent to all portlets
     *
     * @throws com.sun.portal.container.ContainerException when an error occurs in the container code.
     * 
     * @return List of Portlets updated during event processing
     */
    public List<EntityID> processContainerEvent(PortletEvent event, EntityID portletEntityID)
                        throws ContainerException;

}
