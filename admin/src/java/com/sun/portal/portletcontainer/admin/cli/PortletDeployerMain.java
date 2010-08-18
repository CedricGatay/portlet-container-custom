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


package com.sun.portal.portletcontainer.admin.cli;

import com.sun.portal.portletcontainer.admin.PortletRegistryCache;
import com.sun.portal.portletcontainer.admin.PortletRegistryHelper;
import com.sun.portal.portletcontainer.admin.deployment.WebAppDeployerException;
import com.sun.portal.portletcontainer.admin.mbeans.PortletAdmin;
import com.sun.portal.portletcontainer.admin.mbeans.PortletAdminMBean;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class is the Main class for the Portlet Deployment/Undeployment Tool.
 * This class is invoked from the ant script with the right
 * command line options.
 */
public class PortletDeployerMain {
    private static Logger logger = Logger.getLogger(PortletDeployerMain.class.getPackage().getName(),
            "PALogMessages");
    
    private static void initLoggerSettings() {
        try {
            String location =  PortletRegistryHelper.getLogLocation();
            String logFileName = location + "/" + "portlet-deploy.log";
            Handler fh = new FileHandler(logFileName, true);
            fh.setFormatter(new SimpleFormatter());
            
            Logger adminLogger = Logger.getLogger("com.sun.portal.portletcontainer.admin");
            initLoggerSettings(adminLogger, fh);
            Logger mbeansLogger = Logger.getLogger("com.sun.portal.portletcontainer.admin.mbeans");
            initLoggerSettings(mbeansLogger, fh);
            Logger deploymentLogger = Logger.getLogger("com.sun.portal.portletcontainer.admin.deployment");
            initLoggerSettings(deploymentLogger, fh);
            Logger registryLogger = Logger.getLogger("com.sun.portal.portletcontainer.admin.registry");
            initLoggerSettings(registryLogger, fh);
            Logger warUpdaterLogger = Logger.getLogger("com.sun.portal.portletcontainer.warupdater");
            initLoggerSettings(warUpdaterLogger, fh);
            
            initLoggerSettings(logger, fh);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private static void initLoggerSettings(Logger logger, Handler fh){
        logger.setUseParentHandlers(false);
        logger.addHandler(fh);
        logger.setLevel(Level.FINEST);
    }
    
    public static void main(String[] args) {
        initLoggerSettings();
        PortletRegistryCache.init();
        PortletAdminMBean portletAdmin = new PortletAdmin();
        StringBuffer messageBuffer = new StringBuffer();
        StringBuffer buffer = new StringBuffer();
        boolean undeploy = false;
        for(int i=0; i<args.length; i++) {
            buffer.append(args[i]);
            buffer.append(", ");
        }
        logger.log(Level.INFO, "PSPL_CSPPAM0027", buffer);
        // In case of undeploy, undeploy-portlet string hardcoded in the undeploy-portlet target
        if("undeploy-portlet".equals(args[0])) {
            undeploy = true;
        }
        try {
            if(!undeploy) {
                if(args.length > 0) {
                    if(args[0].trim().length() == 0) {
                        throw new Exception("Invalid Portlet Application");
                    }
                }
                try {
                    String portletWebAppName = args[0].replace("\\", "/");
                    Properties roleProperties = new Properties();
                    if(args.length == 2) {
                        try {
                            roleProperties.load(new FileInputStream(args[1]));
                         } catch (IOException e) { }
                    }
                    portletAdmin.deploy(portletWebAppName, roleProperties, new Properties(), true);                    
                    messageBuffer.append("Successfully Deployed and a Portlet Window created.");
                } catch (Exception ex) {
                    if(ex instanceof WebAppDeployerException){
                        messageBuffer.append("Unable to hot deploy the war file. Manually deploy the war file located at ");
                        messageBuffer.append(PortletRegistryHelper.getUpdatedAbsoluteWarFileName(args[0]));
                    } else {
                        messageBuffer.append("Deployment Failed.");
                        messageBuffer.append(ex.getMessage());
                    }
                }
            } else {
                if(args.length > 1) {
                    if(args[1].trim().length() == 0) {
                        throw new Exception("Invalid Portlet Application Name");
                    }
                }
                try {
                    Boolean success = portletAdmin.undeploy(args[1], true);
                    messageBuffer.append("Successfully undeployed.");
                } catch (Exception ex) {
                    if(ex instanceof WebAppDeployerException){
                        messageBuffer.append("Unable to undeploy the war file. Manually undeploy the war file ");
                        messageBuffer.append(args[0]);
                        messageBuffer.append(".war");
                    } else {
                        messageBuffer.append("Undeployment Failed.");
                        messageBuffer.append(ex.getMessage());
                    }
                }
            }
            System.out.println(messageBuffer);
            System.exit(0);
        } catch(Exception e){
            if(logger.isLoggable(Level.SEVERE)) {
                LogRecord logRecord = new LogRecord(Level.SEVERE, "PSPL_CSPPAM0013");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[] {args[0] });
                logRecord.setThrown(e);
                logger.log(logRecord);
            }
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}

