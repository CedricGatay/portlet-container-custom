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

package com.sun.portal.portletcontainer.admin.deployment;

import com.sun.portal.portletcontainer.admin.ServerType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Factory class to provide access to the WebAppDeployer implementation class.
 * This is a singleton class which reads the configuration file to obtain the
 * name of the class which implements WebAppDeployer interface and loads the
 * class, creates an instance of that class, and returns the same for the callers.
 */
public class WebAppDeployerFactory {
    private WebAppDeployer manager = null;
    
    private static final String DEPLOYMENT_MANAGER_CLASS = "deployment.manager.class";
    private static final String DEFAULT_DEPLOYMENT_MANAGER_CLASS = DefaultWebAppDeployer.class.getName();
    private static Logger logger = Logger.getLogger(WebAppDeployerFactory.class.getPackage().getName(),
            "PALogMessages");
    
    private static final WebAppDeployerFactory factory = new WebAppDeployerFactory();
    
    /**
     * Reads the configuration file to intialize the manager with the appropriate WebAppDeployer
     * implementation class.
     */
    private WebAppDeployerFactory(){
        InputStream configStream = null;
        try {
			configStream = Thread.currentThread().getContextClassLoader().
					getResourceAsStream(WebAppDeployer.CONFIG_FILE);
			if(configStream != null) {
                Properties properties = new Properties();
				properties.load(configStream);
                String deploymentManagerClass = properties.getProperty(DEPLOYMENT_MANAGER_CLASS);
                manager = getWebAppDeployerInstance(deploymentManagerClass);
			} else {
				if(ServerType.isJetty()) {
					manager = getWebAppDeployerInstance(JettyWebAppDeployer.class.getName());
				} else {
					manager = getWebAppDeployerInstance(DEFAULT_DEPLOYMENT_MANAGER_CLASS);
				}
			}
        } catch (Throwable t) {
            System.out.println("Exception: " + t.toString() + ". Using DefaultWebAppDeployer");
            try {
                manager = getWebAppDeployerInstance(DEFAULT_DEPLOYMENT_MANAGER_CLASS);
            } catch (Throwable t1) {
                System.out.println("Exception initializing DefaultWebAppDeployer: " + t1.toString());
            }
        } finally {
            if(configStream != null){
                try {
                    configStream.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
    }

	private WebAppDeployer getWebAppDeployerInstance(String name)
			throws Exception {
		return (WebAppDeployer)Class.forName(name).newInstance();
	}
    
    /**
     * Returns the singleton instance of the Factory class.
     *
     * @return WebAppDeployerFactory The singleton instance of this Factory class.
     */
    public static WebAppDeployerFactory getInstance(){
        return factory;
    }
    
    /**
     * Returns the WebAppDeployer implementation instance as provided by the configuration.
     *
     * @return WebAppDeployer The instance of the class implementing WebAppDeployer.
     */
    public WebAppDeployer getDeploymentManager(){
        return manager;
    }
    
}
