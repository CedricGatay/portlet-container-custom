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
import com.sun.portal.container.service.caching.impl.CachingServiceImpl;
import com.sun.portal.container.service.caching.impl.ClientCachingServiceImpl;
import com.sun.portal.container.service.coordination.impl.ContainerEventServiceImpl;
import com.sun.portal.container.service.coordination.impl.CoordinationServiceImpl;
import com.sun.portal.container.service.policy.impl.PolicyServiceImpl;
import com.sun.portal.container.service.deployment.impl.DeploymentServiceLocalImpl;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 * The <code>ServiceManager</code> manages all services. The standard services are
 * CoordinationService, PortalDataInfoService, PortletPreferenceService, CachingService
 * The optional services include PolicyService.
 */
public class ServiceManager {
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(ServiceManager.class, "CLogMessages");
    
    private Map<String,Object> services;
    private static ServletContext servletContext;
    
    private String[] STANDARD_SERVICES = {
        Service.COORDINATION_SERVICE,
        Service.CACHING_SERVICE,
        Service.CLIENT_CACHING_SERVICE,
        Service.DEPLOYMENT_SERVICE_LOCAL,
        Service.CONTAINER_EVENT_SERVICE };
    
    private String[] OPTIONAL_SERVICES = {
        Service.POLICY_SERVICE,
        Service.DEPLOYMENT_SERVICE_REMOTE};

    private static ServiceManager serviceManager = null;

    private ServiceManager() {
        if(services == null)
            services = new ConcurrentHashMap();
    }
    
    /**
     * Returns the ServiceManager. It can be used to access the services that have been
     * initialized.
     * @return the ServiceManager.
     */
    public static ServiceManager getServiceManager() {
        if(serviceManager == null) {
            synchronized(ServiceManager.class){
                if(serviceManager == null) {
                    serviceManager = new ServiceManager();
                }
            }
        }
        return serviceManager;
    }

    /**
     * Initializes the ServiceManager.
     * It adds the services based on the following lookup.
     * 1. Check for the explicit configuration as context-param within web.xml
     *   using the service key, if found use the value which will be
     *   implementation of the service
     *    eg: <context-param>
     *           <param-name>com.sun.portal.container.service.PortalDataInfoService</param-name>
     *            <param-value>com.sun.portal.portletcontainer.driver.impl.PortalDataInfoServicempl</param-value>
     *         </context-param>
     *
     * 2. Check for entries within META-INF/services/<service-key>,
     *   if found use the value which will be the implementation of the service
     *
     * @param context the servletcontext object
     */
    public void init(ServletContext context) {
        servletContext = context;
        // Initialize the ServiceManager only if it is not initialized
        if(this.services.isEmpty()) {
            addStandardServices(context);
            addOptionalServices(context);
        }
    }

	/**
	 * Adds the service and its implementation to the system
	 * @param name name of service
	 * @param implementation Object of service implementation class
	 */
    public void addService(String name, Service implementation) {
        if(this.services.containsKey(name)) {
            return;
        }

		if (implementation != null) {
			services.put(name, implementation);
		}
		else {
			if(logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "PSC_CSPCS030", name);
			}
		}
	}
	
    /**
     * Adds the service and its implementation to the system.
     * @param name the name of the service
     * @param implementation the class that implements these service.
     * @param standard true if the service is standard, false if the service is optional
     */
    public void addService(String name, String implementation, boolean standard) {
        if(this.services.containsKey(name)) {
            return;
        }
        try {
            Class implementationClass = Thread.currentThread().getContextClassLoader().loadClass(implementation);
            Object implementationObject = implementationClass.newInstance();
            Service service = (Service)implementationObject;
            service.init(servletContext);
            services.put(name, implementationObject);
            if(logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "PSC_CSPCS004", new String[] { name, implementation});
            }
        } catch (ClassNotFoundException cnfe) {
            // Log at WARNING if its a standard service
            // or log at FINE if its a optional service
            if(standard) {
                if(logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "PSC_CSPCS018", new String[] { name, cnfe.getMessage() } );
                }
            } else {
                if(logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "PSC_CSPCS018", new String[] { name, cnfe.getMessage() } );
                }
            }
        } catch (IllegalAccessException iae) {
            logRecord(Level.WARNING, name, iae);
        } catch (InstantiationException ie) {
            logRecord(Level.WARNING, name, ie);
        }
    }
    
    /**
     * Returns the Service implementation of the service.
     * @param name the name of the service
     * @return the service implementation of the service
     */
    public Object getService(String name){
        return services.get(name);
    }
    
    /**
     * Removes the service.
     * @param name the name of the service
     */
    public void removeService(String name){
        Service service = (Service)getService(name);
        service.destroy();
        services.remove(name);
        if(logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "PSC_CSPCS005", name);
        }
    }
    
    private void addStandardServices(ServletContext context) {
        for(int i=0; i < STANDARD_SERVICES.length; i++){
            String serviceName = STANDARD_SERVICES[i];
            String serviceImplementation = getServiceImplementation(context, serviceName);
            if(serviceImplementation != null) {
                addService(serviceName, serviceImplementation, true);
            } else {
                logger.log(Level.SEVERE, "PSC_CSPCS007", serviceName);
            }
        }
    }
    
    private void addOptionalServices(ServletContext context) {
        for(int i=0; i < OPTIONAL_SERVICES.length; i++){
            String serviceName = OPTIONAL_SERVICES[i];
            String serviceImplementation = getServiceImplementation(context, serviceName);
            if(serviceImplementation != null) {
                addService(serviceName, serviceImplementation, false);
            } else {
                logger.log(Level.INFO, "PSC_CSPCS007", serviceName);
            }
        }
    }

    private void removeServices() {
        Set<String> serviceNames  = services.keySet();
        for(String serviceName: serviceNames) {
            removeService(serviceName);
        }
    }
    
    private String getServiceImplementation(ServletContext context, String serviceName) {
        String serviceImplementation = context.getInitParameter(serviceName);
        if(serviceImplementation == null) {
            serviceImplementation = ServiceFinder.getServiceImplementationName(serviceName);
        }
        if(serviceImplementation == null) {
            serviceImplementation = getDefaultImplementationName(serviceName);
        }
        return serviceImplementation;
    }
    
    /**
     * Called when the context is destroyed. This removes all the services from the
     * system
     * @param context the servlet context object
     */
    public void destroy(ServletContext context) {
        removeServices();
    }
    
    private void logRecord(Level logLevel, String name, Exception ex) {
        if(logger.isLoggable(logLevel)) {
            LogRecord logRecord = new LogRecord(logLevel, "PSC_CSPCS003");
            logRecord.setLoggerName(logger.getName());
            logRecord.setParameters(new String[]{name});
            logRecord.setThrown(ex);
            logger.log(logRecord);
        }
    }

    private String getDefaultImplementationName(String serviceName) {
        if(Service.COORDINATION_SERVICE.equals(serviceName)) {
            return CoordinationServiceImpl.class.getName();
        } else if(Service.CACHING_SERVICE.equals(serviceName)) {
            return CachingServiceImpl.class.getName();
        } else if(Service.CLIENT_CACHING_SERVICE.equals(serviceName)) {
            return ClientCachingServiceImpl.class.getName();
        } else if(Service.POLICY_SERVICE.equals(serviceName)) {
            return PolicyServiceImpl.class.getName();
        } else if(Service.DEPLOYMENT_SERVICE_LOCAL.equals(serviceName)){
            return DeploymentServiceLocalImpl.class.getName();
        } else if(Service.DEPLOYMENT_SERVICE_REMOTE.equals(serviceName)){
            return "com.sun.portal.wsrp.consumer.common.DeploymentServiceRemoteImpl";
        } else if(Service.CONTAINER_EVENT_SERVICE.equals(serviceName)){
            return ContainerEventServiceImpl.class.getName();
        } 
        return null;
    }
}
