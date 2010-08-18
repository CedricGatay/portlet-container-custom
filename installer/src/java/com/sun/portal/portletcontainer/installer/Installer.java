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

package com.sun.portal.portletcontainer.installer;

import java.util.Properties;

/**
 *  An interface which defines the Container specific methods which are invoked by
 *  the PortletContainerConfigurator class. The classes which realize this interface
 *  provide the container specific implementations for the declared methods.
 */
public interface Installer {
    /**
     * Constants for the various container supported. 
     * As new containers are added please update the getSupportedContainers() 
     * method of InstallerFactory.
     */
    public static final String TOMCAT = "Tomcat";
    public static final String TOMCAT6 = "Tomcat6";
    public static final String GLASSFISH = "GlassFish";
    public static final String JETTY = "Jetty";
    public static final String WEBLOGIC = "WebLogic";
    
	public static final String SERVER_INFO_FILE = "server.info";

    // Portlet Container home
    public static final String PORTLET_CONTAINER_HOME = "portlet-container";
    
    // WebAppDeployer class
    public static final String WEBAPP_DEPLOYER_CLASS = "deployment.manager.class";

	// Server Home key that will be added to server-info.txt during installation
    public static final String SERVER_HOME = "SERVER_HOME";

    /**
     * Provides the Portlet Container home.
     *
     * @param serverHome The installation directory of the container.
     * @param domain  The domain directory or the webapps directory or something similar which is container specific.
     *
     * @return String The path of the Portlet Container home.
     */
    public String getPCHome(String serverHome, String domain);
    
    /**
     * Provides the Ant target to invoke.
     *
     * @return String The Ant target which must be invoked.
     */
    public String getAntTarget();
    
    /**
     * Updates the Properties with the server home and the domain directory 
     * with the expected property names.
     *
     * @param serverHome The installation directory of the container.
     * @param domain The domain directory or the webapps directory or something similar which is container specific.
     * @param properties The Properties which must be updated with the container specific property names and the 
     *                   provided values.
     */
    public void updateProperties(String serverHome, String domain, Properties properties);

    String getUninstallAntTarget();
}
