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

package com.sun.portal.container;

import java.util.Map;
import java.net.URL;
import java.util.List;
import java.util.Queue;

/**
 * A <code>ExecuteActionResponse</code> encapsulates the action response sent by the
 * aggregation engine to the container.
 **/
public interface ExecuteActionResponse extends ContainerResponse {

    /**
     * Returns the URL the portal server should redirect to.
     *
     * @return  redirect URL
     **/
    public URL getRedirectURL();
    
    /**
     * Sets the URL the portal server should redirect to.
     * Setting to <code>null</code> means no redirection is required.
     * 
     * @param redirectURL the url to redirect
     */
    public void setRedirectURL( URL redirectURL );

    /**
     * Returns the new window state of the channel.
     *
     * @return  new window state
     **/
    public ChannelState getNewWindowState();
    
    /**
     * Sets the new window state of the channel.
     * Setting to <code>null</code> means no change in window state.
     *
     * @param  newWindowState   the new window state to be set to
     */
    public void setNewWindowState( ChannelState newWindowState );

    /**
     * Returns the new mode of the channel.
     *
     * @return  the new channel mode
     **/
    public ChannelMode getNewChannelMode();

    /**
     * Sets the new mode of the channel.
     * Setting to <code>null</code> means no change in channel mode.
     *
     * @param  newChannelMode   the new channel mode to be set to
     */
    public void setNewChannelMode( ChannelMode newChannelMode );

    /**
     * Returns a <code>Map</code> of render parameters.  These are parameters
     * that are used for content generation.
     *
     * @return  the render parameter map
     **/
    public Map<String, String[]> getRenderParameters();
    
    /**
     * Sets the render parameter.
     * Value set cannot be <code>null</code>.
     * 
     * @param renderParameters  the parameters for the next render call.
     */
    public void setRenderParameters( Map<String, String[]> renderParameters );
    
    /**
     * Returns a <code>Map</code> which contains only public render parameters.  
     *
     * @return  the public render parameter map
     **/
    public Map<String, String[]> getPublicRenderParameters();
    
    /**
     * Sets the public render parameters.
     * Value set cannot be <code>null</code>.
     * 
     * @param publicRenderParameters  the public render parameters.
     */
    public void setPublicRenderParameters( Map<String, String[]> publicRenderParameters);

    
    /**
     * Returns a <code>List</code> of render parameters to be deleted.
     *
     * @return  the render parameters to be deleted
     **/
    public List<String> getDeletedRenderParameters();

    /**
     * Sets the render parameters to be deleted.
     * Value set cannot be <code>null</code>.
     * 
     * @param deletedRenderParameters  the parameters to be deleted
     */
    public void setDeletedRenderParameters( List<String> deletedRenderParameters );

    /**
     * Sets the portlet events in a Queue.
     *
     * @param eventQueue the portlet event queue
     */
    public void setEventQueue(Queue<PortletEvent> eventQueue);
    
    /**
     * Returns the portlet events contained in the Queue.
     *
     * @return the portlet events
     */
    public Queue<PortletEvent> getEventQueue();

    /**
     * Sets the list of portlets that have been updated during eventing.
     *
     * @param updatedPortlets the list of portlets that have been updated during eventing.
     */
    public void setEventUpdatedPortlets(List<EntityID> eventUpdatedPortlets);

    /**
     * Returns list of portlets that have been updated during eventing.
     *
     * @return the list of portlets that have been updated during eventing.
     */
    public List<EntityID> getEventUpdatedPortlets();

    /**
     * Sets the list of portlets along with ChannelState that have been updated
	 * for the portlets during eventing.
     *
     * @param eventUpdatedPortletsState the list of portlets along with channel state
	 * that have been updated during eventing.
     */
	public void setEventUpdatedPortletsState(Map<EntityID, ChannelState> eventUpdatedPortletsState);

    /**
     * Returns list of portlets along with ChannelState that have been updated
	 * for the portlets during eventing.
	 *
     * @return list of portlets along with channel state that have been updated
	 * during eventing.
     */
	public Map<EntityID, ChannelState> getEventUpdatedPortletsState();
}
