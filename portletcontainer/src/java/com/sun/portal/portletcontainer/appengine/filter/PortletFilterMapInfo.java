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

import com.sun.portal.portletcontainer.common.PortletActions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;
import javax.portlet.filter.EventFilter;

/**
 * Class to contain all the filters information applicable 
 * for a particular portlet. It arranges filter objects for a particular
 * portlet in different lifecycle
 */
public class PortletFilterMapInfo {
    private String portletName;
    //Not specifying object type for following collections
    //as the return type will be collection of objects
    private List renderFilters;
    private List actionFilters;
    private List resourceFilters;
    private List eventFilters;
    
    
    /**
     * Constructor for PortletFilterMapInfo
     * @param portletName Portlet name
     */
    public PortletFilterMapInfo(String portletName) {
        this.portletName = portletName;
    }
    
    /**
     * Method to add a new filter 
     * @param filterObj Filter object
     * @param lifecyclePhases lifecycle phases implemented by this filter object 
     */
    public void addFilter(Object filterObj, Set lifecyclePhases){
        if(lifecyclePhases.contains(PortletActions.RENDER_PHASE)
            && (filterObj instanceof RenderFilter)
        ){
            if(renderFilters==null){
                renderFilters = new ArrayList();
            }
            renderFilters.add(filterObj);
        }
        if(lifecyclePhases.contains(PortletActions.ACTION_PHASE)
            && (filterObj instanceof ActionFilter)        
        ){
            if(actionFilters==null){
                actionFilters = new ArrayList();
            }
            actionFilters.add(filterObj);
        }
        if(lifecyclePhases.contains(PortletActions.RESOURCE_PHASE)
            && (filterObj instanceof ResourceFilter)        
        ){
            if(resourceFilters==null){
                resourceFilters = new ArrayList();
            }
            resourceFilters.add(filterObj);
        }
        if(lifecyclePhases.contains(PortletActions.EVENT_PHASE)
            && (filterObj instanceof EventFilter)        
        ){
            if(eventFilters==null){
                eventFilters = new ArrayList();
            }
            eventFilters.add(filterObj);
        }    
    }
    
    /**
     * Method to return a list of filter objects applicable for a particular 
     * lifecycle phase of a portlet.
     * @param lifecyclePhase java.lang.String
     * @return java.util.List containing filter objects
     */
    public List getApplicableFilters(String lifecyclePhase){
        if(PortletActions.ACTION_PHASE.equals(lifecyclePhase)){
            return actionFilters;
        }else
         if(PortletActions.RENDER_PHASE.equals(lifecyclePhase)){
            return renderFilters;
        }else
        if(PortletActions.EVENT_PHASE.equals(lifecyclePhase)){
            return eventFilters;
        }else
        if(PortletActions.RESOURCE_PHASE.equals(lifecyclePhase)){
            return resourceFilters;
        }
       return null;
    }
    
}
