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


package com.sun.portal.portletcontainer.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PropertiesContext loads the content of driverconfig.properties
 */
public class PropertiesContext {

    private static Logger logger = Logger.getLogger(PropertiesContext.class.getPackage().getName(),
            "PALogMessages");

	private static String CONFIG_FILE = "DriverConfig.properties";
    // Constants for properties defined in the config file
    private static final String PORTLET_RENDER_MODE_PARALLEL = "portletRenderModeParallel";
    private static final String ENABLE_AUTODEPLOY = "enableAutodeploy";
    private static final String AUTODEPLOY_DIR_WATCH_INTERVAL = "autodeployDirWatchInterval";
    private static final String PERSISTENCE_TYPE = "persistenceType";
    private static final String JDBC_URL = "jdbc.url";
    private static final String JDBC_DRIVER = "jdbc.driver";
    private static final String JDBC_USER = "jdbc.user";
    private static final String JDBC_PASSWORD = "jdbc.password";

	private static boolean renderModeParallel;
	private static boolean enableAutoDeploy;
	private static long autodeployDirWatchInterval;
	private static boolean persistenceTypeFile = true;
	private static Map<String, String> databaseProperties;

    public static void init() {
        InputStream defaultConfigBundle = null;
        Properties defaultProperties = new Properties();
        try {
            String configFile = PortletRegistryHelper.getConfigFileLocation() + File.separator + CONFIG_FILE;
            defaultConfigBundle = new FileInputStream(configFile);
            defaultProperties.load(defaultConfigBundle);
        } catch (IOException e) {
			logger.log(Level.WARNING, "PSPL_CSPPAM0016", e);
        } finally {
            if (defaultConfigBundle != null) {
                try {
                    defaultConfigBundle.close();
                } catch (IOException e) {
                    //drop through
                }
            }
        }
        Properties configProperties = new Properties(defaultProperties);
		renderModeParallel = getPortletRenderModeParallel(configProperties);
		enableAutoDeploy = getEnableAutoDeploy(configProperties);
		autodeployDirWatchInterval = computeAutodeployDirWatchInterval(configProperties);
		persistenceTypeFile = getPersistToFile(configProperties);

		databaseProperties = new HashMap<String, String>();
		setDatabaseProperties(configProperties);
    }

    public static boolean isPortletRenderModeParallel() {
		return renderModeParallel;
    }

    public static boolean enableAutodeploy() {
		return enableAutoDeploy;
    }

    public static long getAutodeployDirWatchInterval() {
		return autodeployDirWatchInterval;
    }

	public static boolean persistToFile() {
		return persistenceTypeFile;
	}

	public static Map<String, String> getDatabaseProperties() {
		return databaseProperties;
	}

	private static boolean getEnableAutoDeploy(Properties configProperties) {
        String value = configProperties.getProperty(ENABLE_AUTODEPLOY);
		if(logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "PSPL_CSPPAM0035",
					new String[] {ENABLE_AUTODEPLOY, value} );
		}
        if("true".equals(value))
            return true;
        return false;
	}

	private static boolean getPersistToFile(Properties configProperties) {
        String value = configProperties.getProperty(PERSISTENCE_TYPE);
		if(logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "PSPL_CSPPAM0035",
					new String[] {PERSISTENCE_TYPE, value} );
		}
        if("file".equals(value)) {
			return true;
		} else if("database".equals(value)) {
			return false;
		}
        return true;
	}

	private static long computeAutodeployDirWatchInterval(Properties configProperties) {
        String value = configProperties.getProperty(AUTODEPLOY_DIR_WATCH_INTERVAL);
		if(logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "PSPL_CSPPAM0035",
					new String[] {AUTODEPLOY_DIR_WATCH_INTERVAL, value} );
		}
        long watchInterval;
        try {
            watchInterval = Long.parseLong(value);
        } catch (NumberFormatException nfe){
            watchInterval = -1;
        }
        if(watchInterval <= 0) {
            watchInterval = 5;
        }
        return (watchInterval*1000);
	}

	private static boolean getPortletRenderModeParallel(Properties configProperties) {
        String value = configProperties.getProperty(PORTLET_RENDER_MODE_PARALLEL);
		if(logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "PSPL_CSPPAM0035",
					new String[] {PORTLET_RENDER_MODE_PARALLEL, value} );
		}
        if("true".equals(value))
            return true;
        return false;
	}

	private static String getJdbcUrl(Properties configProperties) {
        String value = configProperties.getProperty(JDBC_URL);
		if(logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "PSPL_CSPPAM0035",
					new String[] {JDBC_URL, value} );
		}
		return value;
	}

	private static String getJdbcDriver(Properties configProperties) {
        String value = configProperties.getProperty(JDBC_DRIVER);
		if(logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "PSPL_CSPPAM0035",
					new String[] {JDBC_DRIVER, value} );
		}
		return value;
	}

	private static String getJdbcUser(Properties configProperties) {
        String value = configProperties.getProperty(JDBC_USER);
		if(logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "PSPL_CSPPAM0035",
					new String[] {JDBC_USER, value} );
		}
		return value;
	}

	private static String getJdbcPassword(Properties configProperties) {
        String value = configProperties.getProperty(JDBC_PASSWORD);
		if(logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "PSPL_CSPPAM0035",
					new String[] {JDBC_PASSWORD, value} );
		}
		return value;
	}

	private static void setDatabaseProperties(Properties configProperties) {
		databaseProperties.put("toplink.jdbc.user", getJdbcUser(configProperties));
		databaseProperties.put("toplink.jdbc.password", getJdbcPassword(configProperties));
		String url = getJdbcUrl(configProperties);
		databaseProperties.put("toplink.jdbc.url", url);
		databaseProperties.put("toplink.jdbc.driver", getJdbcDriver(configProperties));
		// There is a weird issue when using HSQLDB as database provider for TopLink JPA.
		// We need to explicitly include the toplink.target-database
		if(url.indexOf("hsql") != -1) {
			databaseProperties.put("toplink.target-database", "HSQL");
		}
	}
}
