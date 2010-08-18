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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The <code>ServiceFinder</code> searches <code>META-INF/services/{service-name}</code>
 * resource, its first line is read and assumed to be the name of the
 * service implementation class to use.
 */

public final class ServiceFinder {
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(ServiceFinder.class, "CLogMessages");
    
    private static final String META_INF_SERVICES = "META-INF/services/";
    
    /**
     * Package-private constructor to disable instantiation of this class.
     */
    ServiceFinder() {
    }
    
    /**
     * Searches <code>META-INF/services/{service-name}</code>
     * resource, its first line is read and assumed to be the name of the
     * service implementation class to use. Instantiates the implementation class
     * and returns the instance.
     * @param serviceName Fully qualified name of the service
     *                    for which an implementation name is requested
     * @return the implementation instance of the service
     */
    public static Object getServiceImplementationInstance(String serviceName) throws Exception {
        String serviceEntry = getServiceImplementationName(serviceName);
        Object implObject;
        if(serviceEntry != null) {
            Class implementationClass = Thread.currentThread().getContextClassLoader().loadClass(serviceEntry);
            implObject = implementationClass.newInstance();
        } else {
            implObject = null;
        }
        return implObject;
    }
    
    /**
     * Searches <code>META-INF/services/{service-name}</code>
     * resource, its first line is read and assumed to be the name of the
     * service implementation class to use.
     * @param serviceName Fully qualified name of the service
     *                    for which an implementation name is requested
     * @return the implementation name of the service
     */
    public static String getServiceImplementationName(String serviceName) {
        String fullServiceName = META_INF_SERVICES + serviceName;
        String result = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            return result;
        }
        
        InputStream input = null;
        BufferedReader reader = null;
        try {
            input = loader.getResourceAsStream(fullServiceName);
            if (input != null) {
                try {
                    reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                } catch (Exception e) {
                    reader = new BufferedReader(new InputStreamReader(input));
                }
                result = reader.readLine();
                if(logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "PSC_CSPCS010", new String[] {serviceName, result});
                }
            }
        } catch (Exception ex) {
            if(logger.isLoggable(Level.SEVERE)) {
                LogRecord logRecord = new LogRecord(Level.SEVERE, "PSC_CSPCS017");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[]{fullServiceName});
                logRecord.setThrown(ex);
                logger.log(logRecord);
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {}
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {}
            }
        }
        
        return result;
    }
}
