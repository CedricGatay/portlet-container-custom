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
 * [Name of File] [ver.__] [Date]
 * 
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
*/

package com.sun.portal.container.impl;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;
import com.sun.portal.container.ExecuteEventResponse;
import com.sun.portal.container.PortletEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class ExecuteEventResponseImpl extends ContainerResponseImpl implements ExecuteEventResponse {

    private ChannelState newWindowState;
    private ChannelMode newChannelMode;
    private Map<String, String[]> renderParameters = new HashMap<String, String[]>();
    private Map<String, String[]> publicRenderParameters = new HashMap<String, String[]>();
    private List<String> deletedRenderParameters;
    private PortletEvent currentEvent;
    private Queue<PortletEvent> eventQueue;
    private boolean eventFailed;
    private String eventFailedMessage;

    public ChannelState getNewWindowState() {
        return newWindowState;
    }

    public void setNewWindowState( ChannelState newWindowState ) {
        this.newWindowState = newWindowState;
    }

    public ChannelMode getNewChannelMode() {
        return newChannelMode;
    }

    public void setNewChannelMode( ChannelMode newChannelMode ) {
        this.newChannelMode = newChannelMode;
    }

    public Map<String, String[]> getRenderParameters() {
        return renderParameters;
    }

    public void setRenderParameters( Map<String, String[]> renderParameters ) {
        this.renderParameters = renderParameters;
    }

    public List<String> getDeletedRenderParameters() {
        return this.deletedRenderParameters;
    }

    public void setDeletedRenderParameters( List<String> deletedRenderParameters ) {
        this.deletedRenderParameters = deletedRenderParameters;
    }

    public PortletEvent getCurrentEvent() {
        return this.currentEvent;
    }

    public void setCurrentEvent(PortletEvent event) {
        this.currentEvent = event;
    }

    public void setEventQueue(Queue<PortletEvent> eventQueue) {
        this.eventQueue = eventQueue;
    }

    public Queue<PortletEvent> getEventQueue() {
        return this.eventQueue;
    }

    public void setEventFailed(boolean eventFailed) {
        this.eventFailed = eventFailed;
    }

    public boolean eventFailed() {
        return this.eventFailed;
    }

    public void setEventFailedMessage(String eventFailedMessage) {
        this.eventFailedMessage = eventFailedMessage;
    }

    public String getEventFailedMessage() {
        return this.eventFailedMessage;
    }
    
    public Map<String, String[]> getPublicRenderParameters() {
        return this.publicRenderParameters;
    }

    public void setPublicRenderParameters(Map<String, String[]> publicRenderParameters) {
        this.publicRenderParameters = publicRenderParameters;
    }
    
}
