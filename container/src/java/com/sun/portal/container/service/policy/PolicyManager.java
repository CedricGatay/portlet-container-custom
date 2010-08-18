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

/**
 * PolicyManager is responsible for dynamically setting the Policies.
 * 
 */
public interface PolicyManager {

    /**
     * Returns the Event Policy in effect.
     *
     * @return the Event Policy in effect.
     */
    public EventPolicy getEventPolicy();
    
    /**
     * Sets the Event Policy. This can be used to override
     * the exisiting policy in effect.
     *
     * @param eventPolicy the Event Policy
     */
    public void setEventPolicy(EventPolicy eventPolicy);
    
    /**
     * Returns the Public Render Parameter Policy in effect.
     *
     * @return the Public Render Parameter Policy in effect.
     */
    public PublicRenderParameterPolicy getPublicRenderParameterPolicy();
    
    /**
     * Sets the PublicRenderParameter Policy. This can be used to override
     * the exisiting policy in effect.
     *
     * @param publicRenderParameterPolicy the Public Render Parameter Policy
     */
    public void setPublicRenderParameterPolicy(PublicRenderParameterPolicy publicRenderParameterPolicy);
}
