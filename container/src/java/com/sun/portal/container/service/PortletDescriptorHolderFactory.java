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
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The <code>PortletDescriptorHolderFactory</code> is a factory object that creates and returns
 * a new instance of the PortletDescriptorHolder.
 * PortletDescriptorHolderFactory initializes PortletDescriptorHolder implementation
 * based on the following algorithm.
 * Look for a resource called
 * <code>/META-INF/services/com.sun.portal.container.service.PortletDescriptorHolder</code>.  If found,
 * interpret it as a properties file, and read out the first entry. Interpret the first entry
 * as a fully qualify class name of a class that implements
 * <code>com.sun.portal.container.service.PortletDescriptorHolder</code>.
 */
public class PortletDescriptorHolderFactory {
    
    private PortletDescriptorHolderFactory() {
    }
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PortletDescriptorHolderFactory.class, "CLogMessages");
    
    private static String DEFAULT_IMPL = "com.sun.portal.portletcontainer.appengine.impl.PortletDescriptorHolderImpl";
    private static PortletDescriptorHolder portletDescriptorHolder = null;
    
    /**
     * Returns an instance of the PortletDescriptorHolder
     * 
     * @return an instance of the PortletDescriptorHolder object
     */
    public static PortletDescriptorHolder getPortletDescriptorHolder() throws Exception {
        if(portletDescriptorHolder == null) {
            synchronized(PortletDescriptorHolderFactory.class){
                if(portletDescriptorHolder == null) {
                    String serviceEntry = null;
                    try {
                        Object implObject = ServiceFinder.getServiceImplementationInstance(PortletDescriptorHolder.class.getName());
                        if(implObject != null) {
                            portletDescriptorHolder = (PortletDescriptorHolder)implObject;
                        } else {
                            portletDescriptorHolder = (PortletDescriptorHolder)getInstance(DEFAULT_IMPL);
                        }
                    } catch (Exception ex) {
                        if(logger.isLoggable(Level.SEVERE)) {
                            LogRecord logRecord = new LogRecord(Level.SEVERE, "PSC_CSPCS008");
                            logRecord.setLoggerName(logger.getName());
                            logRecord.setParameters(new String[]{serviceEntry});
                            logRecord.setThrown(ex);
                            logger.log(logRecord);
                        }
                        portletDescriptorHolder = (PortletDescriptorHolder)getInstance(DEFAULT_IMPL);
                    }
                }
            }
        }
        return portletDescriptorHolder;
    }
    
    private static Object getInstance(String serviceEntry ) throws Exception {
        Class implementationClass = Thread.currentThread().getContextClassLoader().loadClass(serviceEntry);
        return implementationClass.newInstance();
    }
}
