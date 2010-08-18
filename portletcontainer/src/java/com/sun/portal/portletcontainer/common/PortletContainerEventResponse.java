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


package com.sun.portal.portletcontainer.common;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;
import com.sun.portal.container.ExecuteEventResponse;
import com.sun.portal.container.PortletEvent;
import java.util.List;

import java.util.Map;
import java.util.Queue;

public class PortletContainerEventResponse extends PortletContainerResponse {
    
    private ExecuteEventResponse response;
	private ChannelMode channelMode;
	private ChannelState channelState;
    
    public PortletContainerEventResponse( ExecuteEventResponse response ) {
        super( response );
        this.response = response;
    }

    /**
     * Returns the window state of the portlet window.
     *
     * @return  the window state
     **/
    public ChannelState getChannelState() {
        return this.channelState;
    }
    
    /**
     * Sets the window state of the portlet window.
     *
     * @param  channelState   the window state to be set to
     */
    public void setChannelState(ChannelState channelState) {
        this.channelState = channelState;
    }
    
    /**
     * Returns the new window state of the portlet window.
     *
     * @return  new window state
     **/
    public ChannelState getNewChannelState() {
        return response.getNewWindowState();
    }
    
    /**
     * Sets the new window state of the portlet window.
     *
     * @param  newChannelState   the new window state to be set to
     */
    public void setNewChannelState(ChannelState newChannelState) {
        response.setNewWindowState(newChannelState);
    }
    
    /**
     * Returns the current mode of the portlet window.
     *
     * @return  the current portlet window mode
     **/
    public ChannelMode getChannelMode() {
        return this.channelMode;
    }
    
    /**
     * Sets the mode of the portlet window.
     *
     * @param channelMode   the portlet window mode to be set to
     */
    public void setChannelMode(ChannelMode channelMode) {
        this.channelMode = channelMode;
    }
    
    /**
     * Returns the new mode of the portlet window.
     *
     * @return  the new portlet window mode
     **/
    public ChannelMode getNewChannelMode() {
        return response.getNewChannelMode();
    }
    
    /**
     * Sets the new mode of the portlet window.
     *
     * @param newChannelMode   the new portlet window mode to be set to
     */
    public void setNewChannelMode(ChannelMode newChannelMode) {
        response.setNewChannelMode(newChannelMode);
    }
    
    /**
     * Returns a <code>Map</code> of the render parameters.
     *
     * @return  the render parameters map.
     **/
    public Map<String, String[]> getRenderParameters( ) {
        return response.getRenderParameters();
    }
    
    /**
     * Set the render parameters map.  Portlet container may use this API to
     * set the render parameters for the future rendering.
     *
     * @param renderParameters map
     **/
    public void setRenderParameters( Map<String, String[]> renderParameters ) {
        response.setRenderParameters( renderParameters );
    }
	
    /**
     * Sets the public render parameters.
     * 
     * @param publicRenderParameters  the public render parameters.
     */
    public void setPublicRenderParameters(Map<String, String[]> publicRenderParameters) {
        response.setPublicRenderParameters(publicRenderParameters);
    }
    
    /**
     * Returns a <code>List</code> of render parameters to be deleted.
     *
     * @return  the render parameters to be deleted
     **/
    public List<String> getDeletedRenderParameters() {
        return response.getDeletedRenderParameters();
    }
    
    /**
     * Sets the render parameters to be deleted.
     * Value set cannot be <code>null</code>.
     *
     * @param deletedRenderParameters  the parameters to be deleted
     */
    public void setDeletedRenderParameters( List<String> deletedRenderParameters ) {
        response.setDeletedRenderParameters(deletedRenderParameters);
    }
    
    /**
     * Returns the current Event.
     *
     * @return  the current Event.
     **/
    public PortletEvent getCurrentEvent() {
        return response.getCurrentEvent();
    }
    
    /**
     * Sets the current Event.
     *
     * @param  event  the current Event
     */
    public void setCurrentEvent(PortletEvent event) {
        response.setCurrentEvent(event);
    }
    
    /**
     * Sets the portlet events in a Queue.
     * @param eventQueue the portlet event queue
     */
    public void setEventQueue(Queue<PortletEvent> eventQueue){
        response.setEventQueue(eventQueue);
    }
    
    /**
     * Returns the portlet events that are in the Queue.
     *
     * @return the portlet events
     */
    public Queue<PortletEvent> getEventQueue(){
        return response.getEventQueue();
    }
}
