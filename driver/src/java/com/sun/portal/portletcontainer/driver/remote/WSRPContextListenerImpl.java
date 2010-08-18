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
package com.sun.portal.portletcontainer.driver.remote;

import com.sun.portal.container.ContainerLogger;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class WSRPContextListenerImpl implements ServletContextListener{

    private static Logger logger = ContainerLogger.getLogger(
			WSRPContextListenerImpl.class, "PCDLogMessages");
	
	private static String CONTAINER_FACTORY = 
			"com.sun.portal.wsrp.consumer.markup.WSRPContainerFactory";
	
	public void contextInitialized(ServletContextEvent arg0) {
		try{
			//If following is successful, that means, 
			//we have wsrp consumer available
			Class clazz = Class.forName(CONTAINER_FACTORY);
			
			Method m = clazz.getMethod("getInstance");
			m.invoke(null);
			
			logger.info("PSPCD_CSPPD0050");
		}catch(Exception e){
			logger.info("PSPCD_CSPPD0051");
			//Do nothing
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

}
