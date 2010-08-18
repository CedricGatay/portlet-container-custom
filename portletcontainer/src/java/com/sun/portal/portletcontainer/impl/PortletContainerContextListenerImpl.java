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
 

package com.sun.portal.portletcontainer.impl;

import com.sun.portal.container.ContainerFactory;
import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.ContainerType;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;


/**
 * The <code>PortletContainerContextListenerImpl</code> class is
 * funtionally the same as <code>PortletContainer</code> class but
 * implements an additional interface <code>ServletContextListener
 * </code> so that it will be automatically initiated and initialized
 * when the servlet container starts up, and will be destroyed when
 * the lifecycle of servlet container ends.
 *
 */
public class PortletContainerContextListenerImpl
        extends PortletContainer implements ServletContextListener {
    
    private static Logger logger = ContainerLogger.getLogger(PortletContainerContextListenerImpl.class, "PCLogMessages");
    
    public void contextInitialized( ServletContextEvent sce ) {
        ServletContext context = sce.getServletContext();
        init(context);
        ContainerFactory.setContainer(ContainerType.PORTLET_CONTAINER, this);
        logger.info("PSPL_PCCSPCPCI0003");
    }
    
    public void contextDestroyed( ServletContextEvent sce ) {
        ContainerFactory.setContainer(ContainerType.PORTLET_CONTAINER, null);
    }
}
