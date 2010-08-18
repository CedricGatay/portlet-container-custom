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
package com.sun.portal.portletcontainer.common;

import com.sun.portal.container.ContainerLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PortletDeployConfigReader class is responsible for reading and PortletDeployConfig.properties
 * file.
 * 
 */
public class PortletDeployConfigReader {

    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PortletDeployConfigReader.class, "PCCLogMessages");    
    
    //properties key from PortletDeployConfig.properties
    public static final String VALIDATE_PROPERTY = "portletXML.validate";
    public static final String VENDOR_PORTLET_XML_PREFIX = "vendorPortletXML";
    public static final String NAME_SUFFIX = "name";
    public static final String IMPL_SUFFIX = "impl";
    public static final String VALIDATE_SUFFIX = "validate";
    private static final String PORTLET_DEPLOY_CONFIG_FILE = "PortletDeployConfig.properties";
    private static final String PORTLET_DEPLOY_CONFIG_DEFAULT_FILE = "PortletDeployConfigDefault.properties";
    private static Properties configProps = null;

    /**
     * Returns default properties for portlet deploment.
     * 
     * @return java.util.Properties the Properties object that represents 
     *          default PortletDeployConfig.properties.
     */
    public static Properties getPortletDeployDefaultConfigProperties() {
        init();
        return new Properties(configProps);
    }

    /**
     * Returns Properties object after merging the given properties withthe 
     * default properties.
     * 
     * @param deployConfigCustomProperties the customized config properties
     * 
     * @return the Properties object that represents default as well as 
     *         customized Config properties.
     */
    public static Properties getPortletDeployConfigProperties(
            Properties deployConfigCustomProperties) {        
        
        Properties customProperties = getPortletDeployDefaultConfigProperties();
        
        if (deployConfigCustomProperties != null && 
                deployConfigCustomProperties.size()>0) {
            for(Map.Entry entry : deployConfigCustomProperties.entrySet()){
                customProperties.put(entry.getKey(), entry.getValue());
            }
        }

        return customProperties;
    }

    
    /**
     * Load the Properties file available at the given location and merge it 
     * with the default properties.
     * 
     * @param deployConfigFileLocation the location of the PortletDeployConfig.properties file
     * 
     * @return the Properties object that represents default as well as 
     *         customized PortletDeployConfig.properties.
     */
    public static Properties getPortletDeployConfigProperties(String deployConfigFileLocation) {        
        Properties customProperties = getPortletDeployDefaultConfigProperties();
        if (deployConfigFileLocation != null) {
            InputStream configPropsStream = null;
            try {
                configPropsStream = new FileInputStream(deployConfigFileLocation +
                        File.separator +  PORTLET_DEPLOY_CONFIG_FILE);
                customProperties.load(configPropsStream);
            } catch (Exception e) {
                logger.log(Level.WARNING, "PSPL_PCCCSPPCCD0022", e);
            } finally {
                if (configPropsStream != null) {
                    try {
                        configPropsStream.close();
                    } catch (IOException e) {
                        //drop through
                    }
                }
            }
        }

        return customProperties;
    }

    /**
     * Read the default configuration file and load it in the Properties
     */
    private static synchronized void loadPortletDeployConfigDefaultFile() {
        //recheck again
        if (configProps != null) {
            return;
        }
        InputStream configPropsStream = null;
        configProps = new Properties();
        try {
            configPropsStream = Thread.currentThread().getContextClassLoader().
                    getResourceAsStream(PORTLET_DEPLOY_CONFIG_DEFAULT_FILE);
            if(configPropsStream!=null){
                configProps.load(configPropsStream);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (configPropsStream != null) {
                try {
                    configPropsStream.close();
                } catch (IOException e) {
                    //drop through
                }
            }
        }
        return;
    }

    private static void init() {
        if (configProps == null) {
            loadPortletDeployConfigDefaultFile();
        }
    }
    
    public static void main(String args[]){
        ////////include src/conf in the classpath////// 
        /////////Test1//////
        //Properties p = getPortletDeployConfigProperties();
        ////////Test 2//////
        Properties customP = new Properties();
        customP.setProperty("test", "test1234");
        Properties p = getPortletDeployConfigProperties(customP);
        Enumeration proName =  p.propertyNames();
        while(proName.hasMoreElements()){
            Object key = proName.nextElement();
            System.out.println(key + "::" + p.getProperty(key.toString()));
        }
        System.out.println(p);
    }
    
}
