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

package com.sun.portal.container.service.policy.impl;

import com.sun.portal.container.service.policy.EventPolicy;
import com.sun.portal.container.service.policy.PolicyManager;
import com.sun.portal.container.service.policy.PublicRenderParameterPolicy;

/**
 * PolicyManagerImpl provides default implementation for the PolicyManager.
 * 
 */
public class PolicyManagerImpl implements PolicyManager {

    private EventPolicy eventPolicy;
    private PublicRenderParameterPolicy publicRenderParameterPolicy;

    public PolicyManagerImpl() {
    }

    public void setEventPolicy(EventPolicy eventPolicy) {
        this.eventPolicy = eventPolicy;
    }

    public EventPolicy getEventPolicy() {
        return eventPolicy;
    }
    
    public void setPublicRenderParameterPolicy(PublicRenderParameterPolicy publicRenderParameterPolicy) {
        this.publicRenderParameterPolicy = publicRenderParameterPolicy;
    }

    public PublicRenderParameterPolicy getPublicRenderParameterPolicy() {
        return publicRenderParameterPolicy;
    }
}
