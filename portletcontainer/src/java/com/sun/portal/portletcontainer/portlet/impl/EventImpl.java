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

package com.sun.portal.portletcontainer.portlet.impl;

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.PortletEvent;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.portlet.Event;
import javax.xml.namespace.QName;

/**
 * EventImpl provides concrete implementation of Event interface.
 *
 */
public class EventImpl implements Event {
    
    private PortletEvent portletEvent;
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(EventRequestImpl.class, "PAELogMessages");
    
    public EventImpl(PortletEvent event) {
        this.portletEvent = event;
    }
    
    /**
     * Get the portletEvent QName.
     * 
     * @return the QName of the portletEvent, never <code>null</code>.
     */
    public QName getQName() {
        return this.portletEvent.getQName();
    }
    
    /**
     * Get the local part of the portletEvent name.
     * 
     * @return the local part of the portletEvent, never <code>null</code>.
     */
    public String getName() {
        return this.portletEvent.getName();
    }
    
    /**
     * Get the portletEvent payload.
     * 
     * @return portletEvent payload, must be serializable.
     *          May return <code>null</code> if this portletEvent has no payload.
     */
    public Serializable getValue() {
        Serializable value = this.portletEvent.getValue();
        return value;
    }
}
