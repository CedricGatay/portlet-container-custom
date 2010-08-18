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

package com.sun.portal.portletcontainer.driver.policy;

import com.sun.portal.container.service.policy.DistributionType;
import com.sun.portal.container.service.policy.EventPolicy;

/**
 * EventPolicyImpl provides a concrete implementation of the EventPolicy
 *
 */
public class EventPolicyImpl implements EventPolicy {

    private PortletPolicyDescriptor portletPolicyDescriptor;

    public EventPolicyImpl(PortletPolicyDescriptor portletPolicyDescriptor) {
        this.portletPolicyDescriptor = portletPolicyDescriptor;
    }

    public DistributionType getDistributionType() {
        if(this.portletPolicyDescriptor != null) {
            return this.portletPolicyDescriptor.getEventDistribution();
        }
        return DistributionType.ALL_PORTLETS_ON_PAGE;
    }

    public DistributionType getContainerEventDistributionType() {
        if(this.portletPolicyDescriptor != null) {
            return this.portletPolicyDescriptor.getContainerEventDistribution();
        }
        return DistributionType.ALL_PORTLETS;
    }
    
    public int getMaxGenerationOfEvents() {
        if(this.portletPolicyDescriptor != null) {
            return this.portletPolicyDescriptor.getMaxEventGeneration();
        }
        return DEFAULT_MAX_EVENT_GENERATION;
    }
}
