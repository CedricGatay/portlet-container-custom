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

package com.sun.portal.container.service.policy;

import com.sun.portal.container.service.Service;
import javax.servlet.http.HttpServletRequest;


/**
 * PolicyService determines how the events are distributed and render parameters set.
 *
 */
public interface PolicyService extends Service {
    
   /**
     * Returns the Event Policy in effect.
     *
     * @return the Event Policy in effect.
     */
    public EventPolicy getEventPolicy();
    
   /**
     * Returns the Container Event Policy in effect.
     *
     * @return the Container Event Policy in effect.
     */
    public ContainerEventPolicy getContainerEventPolicy();
    
    /**
     * Returns the Public Render Parameter Policy in effect.
     *
     * @return the Public Render Parameter Policy in effect.
     */
    public PublicRenderParameterPolicy getPublicRenderParameterPolicy();
    
    /**
     * Returns true if the portlets has to be rendered in parallel.
     * The portal should call the portlet container's getMarkUp method in different threads
     * inorder to take the advantage of the portlet container rendering the portlets in
     * parallel.
     *
     * @param request the HttpServletRequest object that the Portal can use to set an 
     * attribute which the PolicyService implementation can use.
     * 
     * @return true if the portlets has to be rendered in parallel.
     */
    public boolean renderPortletsInParallel(HttpServletRequest request);
}
