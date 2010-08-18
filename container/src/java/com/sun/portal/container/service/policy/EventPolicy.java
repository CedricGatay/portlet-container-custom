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
 * EventPolicy provides information about the event policy.
 *
 */
public interface EventPolicy {
    
    /**
     * Default value for the maximum number of event generation.
     */
    public static int DEFAULT_MAX_EVENT_GENERATION = 3;
    
    /**
     * Returns the current DistributionType
     * @return the DistributionType
     */
   public DistributionType getDistributionType();
    
   /**
     * Returns the maximum number of event generation allowed.
     * @return maximum number of event generation allowed
     */
   public int getMaxGenerationOfEvents();    
}
