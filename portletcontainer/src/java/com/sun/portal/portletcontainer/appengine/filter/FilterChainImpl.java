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

package com.sun.portal.portletcontainer.appengine.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventPortlet;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;

/**
 * Implementation class for javax.portlet.filter.FilterChain
 */
public class FilterChainImpl implements FilterChain{
    
    
    private Portlet portlet = null;
    private List filterList = null;
    private int pos = 0;
    
    /**
     * Constructor for FilterChain implementation
     * @param p portlet
     * @param filters 
     */
    public FilterChainImpl(Portlet p, List filters ) {
        this.portlet = p;
		if(filters != null) {
			this.filterList = filters;
		} else {
			this.filterList = Collections.EMPTY_LIST;
		}
    }
    
    
    /**
     * Causes the next filter in the chain to be invoked, 
     * or if the calling filter is the last filter in the chain, 
     * causes the portlet at the end of the chain to be invoked.
     * 
     * @param request  the current action request. 
     * @param response  the current action response
     *                   
     * @throws IOException  if an IO error occured in the filter processing
     * @throws PortletException  if a portlet exception occured in the filter processing
     */
    public void doFilter(ActionRequest aReq, ActionResponse aRes)
    throws IOException, PortletException {
        if (pos < filterList.size()) {
            ActionFilter filter = (ActionFilter)filterList.get(pos++);
            
            filter.doFilter(aReq, aRes, this);
            return;
        }
        
        // Reached at the end of the filter chain --
        // invoke target method of portlet instance
        portlet.processAction(aReq, aRes);
        
    }
    

    /**
     * Causes the next filter in the chain to be invoked, 
     * or if the calling filter is the last filter in the chain, 
     * causes the portlet at the end of the chain to be invoked.
     * 
     * @param request  the current render request.
     *  
     * @param response  the current render response.
     *  
     * @throws IOException  if an IO error occured in the filter processing
     * @throws PortletException  if a portlet exception occured in the filter processing
     */    
    public void doFilter(RenderRequest rReq, RenderResponse rRes)
    throws IOException, PortletException {
        if (pos < filterList.size()) {
            RenderFilter filter = (RenderFilter)filterList.get(pos++);
                        
            filter.doFilter(rReq, rRes, this);
            return;
        }
        
        // Reached at the end of the filter chain --
        // invoke target method of portlet instance
        portlet.render(rReq, rRes);
        
    }
    
    /**
     * Causes the next filter in the chain to be invoked, 
     * or if the calling filter is the last filter in the chain, 
     * causes the portlet at the end of the chain to be invoked.
     * 
     * @param request  the current event request. 
     * @param response  the current event response.
     *  
     * @throws IOException  if an IO error occured in the filter processing
     * @throws PortletException  if a portlet exception occured in the filter processing
     */
    public void doFilter(EventRequest eventRequest, EventResponse eventResponse)
    throws IOException, PortletException {
        if (pos < filterList.size()) {
            EventFilter filter = (EventFilter)filterList.get(pos++);
            
            filter.doFilter(eventRequest, eventResponse, this);
            return;
        }
        
        // Reached at the end of the filter chain --
        // invoke target method of portlet instance
        ((EventPortlet)portlet).processEvent(eventRequest, eventResponse);        
    }
    
   /**
     * Causes the next filter in the chain to be invoked, 
     * or if the calling filter is the last filter in the chain, 
     * causes the portlet at the end of the chain to be invoked.
     * 
     * @param request  the current resource request. 
     * @param response  the current resource response.
     *  
     * @throws IOException  if an IO error occured in the filter processing
     * @throws PortletException  if a portlet exception occured in the filter processing
     */    
    public void doFilter(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
    throws IOException, PortletException {
        if (pos < filterList.size()) {
            ResourceFilter filter = (ResourceFilter)filterList.get(pos++);
            
            filter.doFilter(resourceRequest, resourceResponse, this);
            return;
        }
        
        // Reached at the end of the filter chain --
        // invoke target method of portlet instance
        ((ResourceServingPortlet)portlet).serveResource(resourceRequest, resourceResponse);        
    }
        
    
}
