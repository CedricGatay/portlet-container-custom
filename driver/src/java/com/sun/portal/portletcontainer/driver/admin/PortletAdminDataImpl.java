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

package com.sun.portal.portletcontainer.driver.admin;

import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletType;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryContext;
import com.sun.portal.portletcontainer.admin.mbeans.PortletAdmin;
import com.sun.portal.portletcontainer.admin.mbeans.PortletAdminMBean;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * PortletAdminDataImpl provides concrete implementation of PortletAdminData interface
 */
public class PortletAdminDataImpl implements PortletAdminData, Serializable {
    
    private static Logger logger = Logger.getLogger(PortletAdminDataImpl.class.getPackage().getName(),
            "PCDLogMessages");
    static final long serialVersionUID = 3L;
    private PortletRegistryContext portletRegistryContext;
    
    public void init(PortletRegistryContext portletRegistryContext) throws PortletRegistryException {
        this.portletRegistryContext = portletRegistryContext;
    }
    
    public boolean deploy(String absolutePathOfWar, boolean deployToContainer) throws Exception {
        return deploy(absolutePathOfWar, null, null, null, deployToContainer);
    }
    
    public boolean deploy(String absolutePathOfWar, String rolesFilename,
		String userInfoFilename, Properties portletWindow, 
		boolean deployToContainer) throws Exception {
		
        boolean success;
        try {
            PortletAdminMBean portletadmin = new PortletAdmin();
            Properties roleProperties = new Properties();
            if(rolesFilename != null) {
                roleProperties.load(new FileInputStream(rolesFilename));
            }
            Properties userInfoProperties = new Properties();
            if(userInfoFilename != null) {
                userInfoProperties.load(new FileInputStream(userInfoFilename));
            }
            portletadmin.deploy(absolutePathOfWar, roleProperties, userInfoProperties, portletWindow, deployToContainer);
            success = true;
        } catch (Exception e) {
            if(logger.isLoggable(Level.SEVERE)){
                LogRecord logRecord = new LogRecord(Level.SEVERE, "PSPCD_CSPPD0023");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[] { absolutePathOfWar });
                logRecord.setThrown(e);
                logger.log(logRecord);
            }
            success = false;
            throw e;
        }
        return success;
    }
    
    public boolean deployDirectory(File warDir, String warName, String rolesFilename,
		String userInfoFilename, Properties portletWindow) throws Exception {

        boolean success;
        try {
            PortletAdminMBean portletadmin = new PortletAdmin();
            Properties roleProperties = new Properties();
            if(rolesFilename != null) {
                roleProperties.load(new FileInputStream(rolesFilename));
            }
            Properties userInfoProperties = new Properties();
            if(userInfoFilename != null) {
                userInfoProperties.load(new FileInputStream(userInfoFilename));
            }
            portletadmin.deployDirectory(warDir, warName, roleProperties, userInfoProperties, portletWindow);
            success = true;
        } catch (Exception e) {
            if(logger.isLoggable(Level.SEVERE)){
                LogRecord logRecord = new LogRecord(Level.SEVERE, "PSPCD_CSPPD0040");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[] { warName });
                logRecord.setThrown(e);
                logger.log(logRecord);
            }
            success = false;
            throw e;
        }
        return success;
    }

    public boolean undeploy(String warName, boolean undeployFromContainer) throws Exception {
        Boolean success;
        try {
            PortletAdminMBean portletadmin = new PortletAdmin();
            success = portletadmin.undeploy(warName, undeployFromContainer);
        } catch (Exception e) {
            if(logger.isLoggable(Level.SEVERE)){
                LogRecord logRecord = new LogRecord(Level.SEVERE, "PSPCD_CSPPD0031");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[] { warName });
                logRecord.setThrown(e);
                logger.log(logRecord);
            }
            success = Boolean.FALSE;
            throw e;
        }
        return success.booleanValue();
    }
    
    public List<String> getPortletNames() {
        try {
            return portletRegistryContext.getAvailablePortlets();
        } catch (PortletRegistryException pre) {
            logger.log(Level.SEVERE,"PSPCD_CSPPD0024", pre);
        }
        return Collections.EMPTY_LIST;
    }
    
    public List<String> getPortletApplicationNames() {
        List portletApps = new ArrayList();
        try {
            List<EntityID> entityIds = portletRegistryContext.getEntityIds();
            if(entityIds != null) {
                String portletAppName;
                for(EntityID entityId: entityIds){
                    if(entityId != null) {
                        portletAppName = entityId.getPortletApplicationName();
                        if(portletAppName != null && !portletAppName.equals("null") 
                                    && !portletApps.contains(portletAppName)) {
                            portletApps.add(portletAppName);
                        }
                    }
                }
            }
        } catch (PortletRegistryException pre) {
            logger.log(Level.SEVERE,"PSPCD_CSPPD0032", pre);
        }
        return portletApps;
    }
    
    public List getPortletWindowNames() {
        try {
            return portletRegistryContext.getAllPortletWindows(PortletType.LOCAL);
        } catch (PortletRegistryException pre) {
            logger.log(Level.SEVERE,"PSPCD_CSPPD0024", pre);
        }
        return Collections.EMPTY_LIST;
    }
    
    public boolean createPortletWindow(String portletName, String portletWindowName, String title) throws Exception {
        boolean success;
        try {
            portletRegistryContext.createPortletWindow(portletName, portletWindowName, title, Locale.getDefault().toString());
            success = true;
        } catch (PortletRegistryException pre) {
            if(logger.isLoggable(Level.SEVERE)){
                LogRecord logRecord = new LogRecord(Level.SEVERE, "PSPCD_CSPPD0025");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[] { portletName });
                logRecord.setThrown(pre);
                logger.log(logRecord);
            }
            success = false;
            throw pre;
        }
        return success;
    }
    
    public boolean modifyPortletWindow(String portletWindowName, String width, boolean visible) throws Exception {
        boolean success;
        try {
            String exisitingWidth = getWidth(portletWindowName);
            if(!exisitingWidth.equals(width)) {
                portletRegistryContext.setWidth(portletWindowName, width);
            }
            boolean exisitingVisibleValue = isVisible(portletWindowName);
            if(exisitingVisibleValue != visible){
                portletRegistryContext.showPortletWindow(portletWindowName, visible);
            }
            success = true;
        } catch (PortletRegistryException pre) {
            if(logger.isLoggable(Level.SEVERE)){
                LogRecord logRecord = new LogRecord(Level.SEVERE, "PSPCD_CSPPD0025");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[] { portletWindowName });
                logRecord.setThrown(pre);
                logger.log(logRecord);
            }
            success = false;
            throw pre;
        }
        return success;
    }
    
    public boolean isVisible(String portletWindowName) throws Exception {
        return portletRegistryContext.isVisible(portletWindowName);
    }
    
    public String getWidth(String portletWindowName) throws Exception {
        return portletRegistryContext.getWidth(portletWindowName);
    }
}
