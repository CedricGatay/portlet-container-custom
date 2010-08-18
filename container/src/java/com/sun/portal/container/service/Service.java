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

package com.sun.portal.container.service;

import javax.servlet.ServletContext;

/**
 * The <code>Service</code> provides basic methods that all services should inherit.
 */
public interface Service {
    // Key for CoordinationService in Service Manager
    public final static String COORDINATION_SERVICE = "com.sun.portal.container.service.CoordinationService";
    // Key for CachingService in Service Manager
    public final static String CACHING_SERVICE = "com.sun.portal.container.service.CachingService";
    // Key for ClientCachingService in Service Manager
    public final static String CLIENT_CACHING_SERVICE = "com.sun.portal.container.service.ClientCachingService";
    // Key for PolicyService in Service Manager
    public final static String POLICY_SERVICE = "com.sun.portal.container.service.PolicyService";
    // Key for DeploymentService for local portlets in Service Manager
    public final static String DEPLOYMENT_SERVICE_LOCAL = "com.sun.portal.container.service.DeploymentService_Local";
    // Key for DeploymentService for remote (WSRP) portlets in Service Manager
    public final static String DEPLOYMENT_SERVICE_REMOTE = "com.sun.portal.container.service.DeploymentService_Remote";
    // Key for ContainerEventService in Service Manager
    public final static String CONTAINER_EVENT_SERVICE = "com.sun.portal.container.service.ContainerEventService";

    /**
     * Initialization of service using the Servlet Context
     * @param context the ServletContext Object
     */
    public void init(ServletContext context);

    /**
     * Get the name of the service
     * @return the name of the service
     */
    public String getName();
    
    /**
     * Get the description of the service
     * @return the description of the service
     */
    public String getDescription();
    
    /**
     * destroy the service
     */
    public void destroy();
}
