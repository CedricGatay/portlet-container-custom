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

import com.sun.portal.container.service.policy.ContainerEventPolicy;
import com.sun.portal.container.service.policy.DistributionType;

/**
 * ContainerEventPolicyImpl provides the implementation of the ContainerEventPolicy
 * It gets the data from portlet-policy.xml
 *
 */
public class ContainerEventPolicyImpl implements ContainerEventPolicy {

    private PortletPolicyDescriptor portletPolicyDescriptor;

    public ContainerEventPolicyImpl(PortletPolicyDescriptor portletPolicyDescriptor) {
        this.portletPolicyDescriptor = portletPolicyDescriptor;
    }

    public DistributionType getDistributionType() {
        if(this.portletPolicyDescriptor != null) {
            return this.portletPolicyDescriptor.getContainerEventDistribution();
        }
        return DistributionType.ALL_PORTLETS;
    }
    
    public int getMaxGenerationOfEvents() {
        if(this.portletPolicyDescriptor != null) {
            return this.portletPolicyDescriptor.getContainerMaxEventGeneration();
        }
        return DEFAULT_MAX_EVENT_GENERATION;
    }

    public boolean isEnabled(String eventName) {
        boolean enabled = false;
        if(this.portletPolicyDescriptor != null) {
            Boolean status = this.portletPolicyDescriptor.getContainerEventStatus(eventName);
            if(status != null) {
                enabled = status.booleanValue();
            }
        }
        return enabled;
    }
}
