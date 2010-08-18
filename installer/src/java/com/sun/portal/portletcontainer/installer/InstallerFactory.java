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

/**
 * A factory class which provides the instance of a particular Installer implementation
 * based on the container on which the installation has to happen. This follows the Singleton
 * pattern and has only one instance.
 */
public class InstallerFactory {
    private static InstallerFactory factory = new InstallerFactory();
    
    private InstallerFactory(){        
    }
    
    public static InstallerFactory getInstance(){
        return factory;
    }
    
    /**
     * Creates a container specific Installer implementation's instance and returns the same.
     *
     * @param container The container on which the installation has to be performed.
     *
     * @return Installer The instance of the container specific Installer implementation.
     */
    public Installer getInstaller(String container){
        if(container == null || container.equalsIgnoreCase(Installer.GLASSFISH)) {
            return new GlassfishInstaller();
        } else if(container.equalsIgnoreCase(Installer.TOMCAT)) {
            return new TomcatInstaller();
        } else if(container.equalsIgnoreCase(Installer.TOMCAT6)) {
            return new Tomcat6Installer();
        } else if(container.equalsIgnoreCase(Installer.JETTY)) {
            return new JettyInstaller();
        } else if(container.equalsIgnoreCase(Installer.WEBLOGIC)) {
            return new WebLogicInstaller();
        }
        return null;
    }
    
    /**
     * Provides the different containers that are supported. This should be updated
     * as new containers are added in the Installer interface.
     *
     * @return String[] An array of supported Container names.
     */
    public static String[] getSupportedContainers(){
        return new String[] {
			Installer.GLASSFISH,
			Installer.TOMCAT,
			Installer.TOMCAT6,
			Installer.JETTY,
			Installer.WEBLOGIC
		};
    }
    
}
