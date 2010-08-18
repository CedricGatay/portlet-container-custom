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


package com.sun.portal.portletcontainer.admin.mbeans;

import com.sun.portal.container.PortletLang;
import com.sun.portal.portletcontainer.admin.PortletRegistryGenerator;
import com.sun.portal.portletcontainer.admin.PortletRegistryHelper;
import com.sun.portal.portletcontainer.admin.PortletUndeployerInfo;
import com.sun.portal.portletcontainer.admin.deployment.WebAppDeployer;
import com.sun.portal.portletcontainer.admin.deployment.WebAppDeployerException;
import com.sun.portal.portletcontainer.admin.deployment.WebAppDeployerFactory;
import com.sun.portal.portletcontainer.admin.registry.PortletRegistryConstants;
import com.sun.portal.portletcontainer.admin.registry.PortletRegistryTags;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import com.sun.portal.portletcontainer.warupdater.PortletWarUpdater;
import com.sun.portal.portletcontainer.warupdater.PortletWarUpdaterUtil;
import java.io.File;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PortletAdmin class is a concrete implementation of PortletAdminMBean
 * This class is used by Netbeans Portal Pack. DO NOT make any changes without 
 * sending a mail to http://portalpack.netbeans.org
 */
public class PortletAdmin implements PortletAdminMBean {

    // Create a logger for this class
    private static Logger logger = Logger.getLogger(PortletAdmin.class.getPackage().getName(),
            "PALogMessages");

    public PortletAdmin() {
    }

    private boolean preparePortlet(String absolutePathOfWar, String destination) throws Exception {
        logger.log(Level.INFO, "PSPL_CSPPAM0031", absolutePathOfWar);
        String configFileLocation = PortletRegistryHelper.getConfigFileLocation();
        //Create the updated war file in the war file location
        PortletWarUpdater portletWarUpdater = new PortletWarUpdater(configFileLocation);

        //Get the updated war file
        return portletWarUpdater.preparePortlet(new File(absolutePathOfWar), destination);
    }
    
    private Boolean registerPortlet(String absolutePathOfWar, Properties roles,
            Properties userinfo, Properties portletWindow, File preparedWarFile) throws Exception {
		
        String warNameOnly = PortletWarUpdaterUtil.getWarName(absolutePathOfWar);
        String warFileLocation = PortletRegistryHelper.getWarFileLocation();
        String destFile = warFileLocation + File.separator + warNameOnly;
        logger.log(Level.FINE, "PSPL_CSPPAM0001", destFile);
        PortletRegistryGenerator portletRegistryGenerator = new PortletRegistryGenerator();
        JarFile jarFile = new JarFile(absolutePathOfWar);
        Manifest manifest = jarFile.getManifest();
        PortletLang portletLanguage = PortletLang.JAVA;
        if (manifest != null) {
            Attributes attributes = manifest.getMainAttributes();
            if (attributes != null) {
                String typeOfPortlet = attributes.getValue("Portlet-Type");
                if (typeOfPortlet != null && typeOfPortlet.equalsIgnoreCase("ror")) {
                    portletLanguage = PortletLang.ROR;
                }
            }
        }
        logger.log(Level.FINE, "PSPL_CSPPAM0033", portletLanguage.toString());
        portletRegistryGenerator.register(preparedWarFile, warNameOnly, warFileLocation,
                roles, userinfo, portletWindow, portletLanguage);
        logger.log(Level.FINE, "PSPL_CSPPAM0004", warNameOnly);
        return Boolean.TRUE;
    }

    public Boolean deploy(String absolutePathOfWar, Properties roles,
            Properties userinfo, boolean deployToContainer) throws Exception {

		Properties portletWindow = new Properties();
		portletWindow.setProperty(PortletRegistryTags.VISIBLE_KEY, PortletRegistryConstants.VISIBLE_TRUE);
        return deploy(absolutePathOfWar, roles, userinfo, portletWindow, deployToContainer);
    }

    public Boolean deploy(String absolutePathOfWar, Properties roles,
            Properties userinfo, Properties portletWindow,
			boolean deployToContainer) throws Exception {
        
        String warDestination = PortletRegistryHelper.getWarFileLocation();

        String warName = PortletWarUpdaterUtil.getWarName(absolutePathOfWar);
        
        boolean prepareSuccess = preparePortlet(absolutePathOfWar, warDestination);
        Boolean registerSuccess = Boolean.FALSE;
		
        if(prepareSuccess){
            File preparedWarFile = new File(warDestination, warName);
            registerSuccess = registerPortlet(absolutePathOfWar, roles, userinfo, portletWindow, preparedWarFile);
        }
        if (registerSuccess.booleanValue()) {
            if (deployToContainer) {
                WebAppDeployer webAppDeployer = 
                        WebAppDeployerFactory.getInstance().getDeploymentManager();
                if (webAppDeployer != null) {
                    webAppDeployer.deploy(warName);
                } else {
                    throw new WebAppDeployerException("No WebAppDeployer Found");
                }
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

	public Boolean deployDirectory(File warDir, String warName, Properties roles,
		Properties userinfo, Properties portletWindow) throws Exception {

        logger.log(Level.INFO, "PSPL_CSPPAM0031", warName);

        String configFileLocation = PortletRegistryHelper.getConfigFileLocation();
        //Create the updated war file in the war file location
        PortletWarUpdater portletWarUpdater = new PortletWarUpdater(configFileLocation);

        //Get the updated war file
        boolean prepareSuccess = portletWarUpdater.preparePortlet(warName, warDir);

        Boolean registerSuccess = Boolean.FALSE;

        if(prepareSuccess){
	        String warDestination = PortletRegistryHelper.getWarFileLocation();
			PortletRegistryGenerator portletRegistryGenerator = new PortletRegistryGenerator();
	        portletRegistryGenerator.register(warDir, warName, warDestination,
				roles, userinfo, portletWindow, PortletLang.JAVA);
        }
		
		return registerSuccess.booleanValue();
	}

    public Boolean unregisterPortlet(String warName) throws Exception {
        logger.log(Level.INFO, "PSPL_CSPPAM0032", warName);
        String warFileLocation = PortletRegistryHelper.getWarFileLocation();
        String configFileLocation = PortletRegistryHelper.getConfigFileLocation();
        PortletRegistryGenerator portletRegistryGenerator = new PortletRegistryGenerator();
        String warNameOnly = PortletWarUpdaterUtil.getWarName(warName);
        logger.log(Level.FINE, "PSPL_CSPPAM0012", warNameOnly);
        Boolean value = portletRegistryGenerator.unregister(
                configFileLocation, warFileLocation, warNameOnly);
        if(value.booleanValue()) {
            PortletUndeployerInfo portletUndeployerInfo;
            try {
                portletUndeployerInfo = new PortletUndeployerInfo();
                portletUndeployerInfo.write(warNameOnly);
            } catch (PortletRegistryException pre) {
                logger.log(Level.WARNING, "PSPL_CSPPAM0028", pre);
            }
            // remove the portlet war created by PortletWarUpdater
            boolean remove = deletePortlet(warNameOnly, warFileLocation);
            // Remove the portlet war and portlet xml created in pc.home/war directory
            portletRegistryGenerator.removePortletWar(warFileLocation, warNameOnly);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean undeploy(String warName, boolean undeployFromContainer)
            throws Exception {
        Boolean value = unregisterPortlet(warName);
        if (value.booleanValue()) {
            if (undeployFromContainer) {
                WebAppDeployer webAppDeployer = WebAppDeployerFactory.getInstance().getDeploymentManager();
                if (webAppDeployer != null) {
                    String warNameOnly = PortletWarUpdaterUtil.getWarName(warName);
                    boolean success = webAppDeployer.undeploy(warNameOnly);
                    if (!success) {
                        value = Boolean.FALSE;
                        throw new WebAppDeployerException("Cannot undeploy");
                    }
                } else {
                    value = Boolean.FALSE;
                    logger.log(Level.WARNING, "PSPL_CSPPAM0026");
                    throw new WebAppDeployerException("No WebAppDeployer Found");
                }
            }
            value = Boolean.TRUE;
        }
        return value;
    }

    public void copyFile(String sourceFile, String destFile) throws Exception {
        PortletWarUpdaterUtil.copyFile(sourceFile, destFile);
    }
    
    /**
     * Deletes the portlet application from the stored location
     *
     * @param warNameOnly name of the portlet application (without extension)
     * @param warFileLocation deployed location of the portlet
     *
     * @return true if the deletion is successful.
     */
    public boolean deletePortlet(String warNameOnly, String warFileLocation) {
        String warName = warNameOnly + ".war";
        File destFile = new File(warFileLocation, warName);
        boolean remove = destFile.delete();
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "PSPL_CSPPCWU0011",
                    new String[]{destFile.getAbsolutePath(), String.valueOf(remove)});
        }
        return remove;
    }

	public static void main(String[] args) throws Exception {
		if(args.length != 2) {
			System.err.println("first parameter is exploded directory root and second argument is name of the war");
			System.exit(-1);
		}
		PortletAdminMBean portletAdmin = new PortletAdmin();
		portletAdmin.deployDirectory(new File(args[0]), args[1], null, null, null);
	}
}
