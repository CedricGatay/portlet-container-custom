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

import com.sun.portal.container.ContainerLogger;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * The <code>ServiceManagerContextListenerImpl</code> class is
 * implements an interface <code>ServletContextListener
 * </code> so that ServiceManager be automatically initiated and initialized
 * when the servlet container starts up, and will be destroyed when
 * the lifecycle of servlet container ends.
 *
 */
public class ServiceManagerContextListenerImpl implements ServletContextListener {
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(ServiceManagerContextListenerImpl.class, "CLogMessages");
    
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        ServiceManager serviceManager = ServiceManager.getServiceManager();
        logger.info("PSC_CSPCS001");
        serviceManager.init(context);
        logger.info("PSC_CSPCS002");
    }
    
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        ServiceManager serviceManager = ServiceManager.getServiceManager();
        serviceManager.destroy(context);
    }
    
}
