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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A container provides an execution environment in which channels' contents
 * are generated.  It is a distinct component separate from the portal server's
 * aggregation mechanism.  The <code>Container</code> interface defines the
 * rule of interaction between the aggregation and the container.
 **/
public interface Container {

    /**
     * Creates the GetMarkUpRequest object. It contains information
     * for containers to process the request.
     *
     * @param request  The HttpServletRequest Object
     * @param portletEntityId The Portlet Entity ID.
     * @param channelState the current portlet window state
     * @param channelMode the current portlet window mode
     * @param portletWindowContext The PortletWindowContext object
     * @param channelURLFactory The ChannelURLFactory Object
     *
     * @return the GetMarkupRequest Object
     */
    public GetMarkupRequest createGetMarkUpRequest(HttpServletRequest request,
            EntityID portletEntityId, ChannelState channelState, ChannelMode channelMode,
            PortletWindowContext portletWindowContext, ChannelURLFactory channelURLFactory);

    /**
     * Creates the GetMarkUpResponse object. It is for containers to
     * return the result back to the aggregation engine.
     *
     * @param response  The HttpServletResponse Object
     *
     * @return the GetMarkupResponse Object
     */
    public GetMarkupResponse createGetMarkUpResponse(HttpServletResponse response);
    
    /**
     * Creates the ExecuteActionRequest object. It contains information
     * for containers to process the request.
     *
     * @param request  The HttpServletRequest Object
     * @param portletEntityId The Portlet Entity ID.
     * @param channelState the current portlet window state
     * @param channelMode the current portlet window mode
     * @param portletWindowContext The PortletWindowContext object
     * @param channelURLFactory The ChannelURLFactory Object
     * @param windowRequestReader The WindowRequestReader Object
     *
     * @return the ExecuteActionRequest Object
     */
    public ExecuteActionRequest createExecuteActionRequest(HttpServletRequest request,
            EntityID portletEntityId, ChannelState channelState, ChannelMode channelMode,
            PortletWindowContext portletWindowContext, ChannelURLFactory channelURLFactory,
            WindowRequestReader windowRequestReader);
    
    /**
     * Creates the ExecuteActionResponse object. It is for containers to
     * return the result back to the aggregation engine.
     *
     * @param response  The HttpServletResponse Object
     *
     * @return the ExecuteActionResponse Object
     */
    public ExecuteActionResponse createExecuteActionResponse(HttpServletResponse response);
    
    /**
     * Creates the ExecuteEventRequest object. It contains information
     * for containers to process the request.
     *
     * @param request  The HttpServletRequest Object
     * @param portletEntityId The Portlet Entity ID.
     * @param channelState the current portlet window state
     * @param channelMode the current portlet window mode
     * @param portletWindowContext The PortletWindowContext object
     * @param channelURLFactory The ChannelURLFactory Object
     *
     * @return the ExecuteEventRequest Object
     */
    public ExecuteEventRequest createExecuteEventRequest(HttpServletRequest request,
            EntityID portletEntityId, ChannelState channelState, ChannelMode channelMode,
            PortletWindowContext portletWindowContext, ChannelURLFactory channelURLFactory);
    
    /**
     * Creates the ExecuteEventRequest object based on the existing ContainerRequest object. It contains information
     * for containers to process the request.
     *
     * @param containerRequest  The containerRequest Object
     *
     * @return the ExecuteEventRequest Object
     */
    public ExecuteEventRequest createExecuteEventRequest(ContainerRequest containerRequest);
    
    /**
     * Creates the ExecuteEventResponse object based on the ContainerResponse object. It is for containers to
     * return the result back to the aggregation engine.
     *
     * @param containerResponse  The ContainerResponse Object
     *
     * @return the ExecuteEventResponse Object
     */
    public ExecuteEventResponse createExecuteEventResponse(ContainerResponse containerResponse);
    
    /**
     * Creates the ExecuteEventResponse object. It is for containers to
     * return the result back to the aggregation engine.
     *
     * @param response  The HttpServletResponse Object
     *
     * @return the ExecuteActionResponse Object
     */
    public ExecuteEventResponse createExecuteEventResponse(HttpServletResponse response);
    
    /**
     * Creates the GetResourceRequest object. It contains information
     * for containers to process the request.
     *
     * @param request  The HttpServletRequest Object
     * @param portletEntityId The Portlet Entity ID.
     * @param channelState the current portlet window state
     * @param channelMode the current portlet window mode
     * @param portletWindowContext The PortletWindowContext object
     * @param channelURLFactory The ChannelURLFactory Object
     * @param windowRequestReader The WindowRequestReader Object
     *
     * @return the GetResourceRequest Object
     */
    public GetResourceRequest createGetResourceRequest(HttpServletRequest request,
            EntityID portletEntityId, ChannelState channelState, ChannelMode channelMode,
            PortletWindowContext portletWindowContext, ChannelURLFactory channelURLFactory,
            WindowRequestReader windowRequestReader);

    /**
     * Creates the GetResourceResponse object. It is for containers to
     * return the result back to the aggregation engine.
     *
     * @param response  The HttpServletResponse Object
     *
     * @return the GetResourceResponse Object
     */
    public GetResourceResponse createGetResourceResponse(HttpServletResponse response);
    
    /**
     * Gets the markup segment from a channel to be aggregated with other
     * channels to form a portal page.
     * <p>
     * @param request <code>GetMarkupRequest</code> object contains information
     * for containers to process the request.
     * @param response <code>GetMarkupResponse</code> object for containers to
     * return the result back to the aggregation engine.
     *
     * @exception <code>ContainerException</code> when an error occurs in the
     *            container code.
     * @exception <code>ContentException</code> when an error occurs in the
     *            content generation code.
     **/
    public void getMarkup(
                  GetMarkupRequest request,
                  GetMarkupResponse response ) throws ContainerException, ContentException;

    /**
     * Executes an action of a channel.
     * <p>
     * @param request <code>ExecuteActionRequest</code> object contains 
     * information for containers to process the request.
     * @param response <code>ExecuteActionResponse</code> object for containers
     * to return the result back to the aggregation engine.
     * @param channelURLType <code>ChannelURLType</code> object determines the type
     * of the URL, it can be either action, render or resource.
     *
     * @exception <code>ContainerException</code> when an error occurs in the
     *            container code.
     * @exception <code>ContentException</code> when an error occurs in the
     *            process action code.
     **/
    public void executeAction(
                  ExecuteActionRequest request,
                  ExecuteActionResponse response,
                  ChannelURLType channelURLType) throws ContainerException, ContentException;
    
    /**
     * Executes an event of a channel.
     * <p>
     * @param request <code>ExecuteEventRequest</code> object contains 
     * information for containers to process the request.
     * @param response <code>ExecuteEventResponse</code> object for containers
     * to return the result back to the aggregation engine.
     *
     * @exception <code>ContainerException</code> when an error occurs in the
     *            container code.
     * @exception <code>ContentException</code> when an error occurs in the
     *            process event code.
     **/
    public void executeEvent(
                  ExecuteEventRequest request,
                  ExecuteEventResponse response ) throws ContainerException, ContentException;
    
    /**
     * Gets the Resource information  for a channel
     * <p>
     * @param request <code>GetResourceRequest</code>  object contains 
     * information for containers to process the request.
     * @param response <code>GetResourceResponse</code> object for containers
     * to return the result back to the aggregation engine.
     *
     * @exception <code>ContainerException</code> when an error occurs in the
     *            container code. 
     * * @exception <code>ContentException</code> when an error occurs in the
     *            serveResource code.
     * 
     ***/
     public void getResources(
                 GetResourceRequest request, 
                 GetResourceResponse response) throws ContainerException, ContentException;
}
