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

package com.sun.portal.container.service.deployment;

import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletWindowContext;
import com.sun.portal.container.service.PublicRenderParameterHolder;
import com.sun.portal.container.service.Service;
import com.sun.portal.container.service.EventHolder;


import java.util.List;
import java.util.Map;

/**
 * The Public Render Parameter OR Public Navigational Parameter ( as they are 
 * called in WSRP) information is being stored differently in case of Local  
 * and Remote portlets. The <code>DeploymentService</code> provides abstraction 
 * over this information.
 * The <code>CoordinationService</code> will use <code>DeploymentService</code>
 * to seemlessly coordinate between local and remote portlets.
 */
public interface DeploymentService extends Service {
    
    /**
     * Returns a list of PublicRenderParameterHolder objects supported by the 
     * portlet based on the render parameters. If renderParameters map is NULL,
     * it returns all the PublicRenderParameterHolder objects associated with
     * this portlet.
     * The PublicRenderParameterHolder object holds information about a Public 
     * Render Parameter (or PublicNavigationParameter) supported by the portlet.
     *
     * @param windowContext PortletWindowContext
     * @param portletEntityId the entity id of the portlet
     * @param renderParameters the render parameters of the portlet
     *
     * @return list of PublicRenderParameterHolder objects supported by the portlet.
     */    
    public List<PublicRenderParameterHolder> getSupportedPublicRenderParameterHolders(
            PortletWindowContext windowContext, 
            EntityID portletEntityId, Map<String, String[]> renderParameters);
    
    /**
     * 
     * Verifies whether the portlet can publish or process the public render 
     * parameters. The identifier used by publishing portlet may be different
     * from the identifier used by processing portlet for the same 
     * Public Render Parameter. This method returns a map of publish portlet 
     * identifiers vs. process portlet identifiers.
     * 
     * @param windowContext PortletWindowContext
     * @param portletEntityId the entity id of the portlet
     * @param publicRenderParameterHolder List of publishing portlet's public render parameters
     * 
     * @return Map of publish portlet identifiers vs. process portlet identifiers.
     */    
    public Map<String, String> verifySupportedPublicRenderParameters(
            PortletWindowContext windowContext, 
            EntityID portletEntityId, 
            List<PublicRenderParameterHolder> publicRenderParameterHolder);    
   
     /**
     * 
     * Verifies whether the portlet present in entityId processes event mentioned
     * in eventHolder. This method returns eventHolder which contains QName of
     * the event processed by the portlet.
     * 
     * @param windowContext PortletWindowContext
     * @param entityId the entity id of the portlet
     * @param eventHolder EventHolder with the event QName
     * 
     * @return Map of publish portlet identifiers vs. process portlet identifiers.
     */    
    public EventHolder verifySupportedProcessingEvent(
            PortletWindowContext windowContext, 
            EntityID entityId, EventHolder eventHolder);

    
}
