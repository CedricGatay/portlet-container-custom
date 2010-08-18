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

package com.sun.portal.container.service.deployment.impl;

import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletWindowContext;
import com.sun.portal.container.service.PublicRenderParameterHolder;
import com.sun.portal.container.service.ServiceAdapter;
import com.sun.portal.container.service.deployment.DeploymentService;
import java.util.List;
import java.util.Map;
import com.sun.portal.container.service.EventHolder;

public class DeploymentServiceLocalImpl extends ServiceAdapter implements DeploymentService{
    
    public Map<String, String> verifySupportedPublicRenderParameters(
            PortletWindowContext windowContext, 
            EntityID entityId, 
            List<PublicRenderParameterHolder> publicRenderParameterHolder){
        
        return windowContext.verifySupportedPublicRenderParameters(entityId, 
                publicRenderParameterHolder);
    }    

    public List<PublicRenderParameterHolder> getSupportedPublicRenderParameterHolders(
            PortletWindowContext windowContext, 
            EntityID entityId, Map<String, String[]> renderParameters){
        return windowContext.getSupportedPublicRenderParameterHolders(entityId, renderParameters);
    }
    
    public EventHolder verifySupportedProcessingEvent(
            PortletWindowContext windowContext, 
            EntityID entityId, EventHolder eventHolder){
        return windowContext.verifySupportedProcessingEvent(entityId, eventHolder);
    }    
}
