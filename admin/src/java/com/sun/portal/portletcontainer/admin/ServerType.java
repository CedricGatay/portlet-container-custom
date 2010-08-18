/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.portal.portletcontainer.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dg154973
 */
public class ServerType {

	private static final String PC_ENV_CONF = "pcenv.conf";
	private static final String AUTO_DEPLOY_DIR_SUFFIX = ".autodeploy.dir";
	private static final String SERVER_NAME = "server.name";
	
	public static final String GLASSFISH_ID = "glassfish";
	public static final String TOMCAT_ID = "tomcat";
	public static final String JETTY_ID = "jetty";
	public static final String WEBLOGIC_ID = "weblogic";

	private static boolean glassfish;
	private static boolean tomcat;
	private static boolean weblogic;
	private static boolean jetty;
	private static String autodeployDir;
	private static String serverHome;

	private static Properties properties = new Properties();

    private static Logger logger = Logger.getLogger(ServerType.class.getPackage().getName(),
            "PALogMessages");

	static {
		loadConfigFile();
	}

	public static boolean isGlassfish() {
	
		if (!glassfish) {
			String value = getGlassFishDomainRoot();
			if (value != null) {
				glassfish = true;
			} else {
				glassfish = false;
			}
		}
		return glassfish;
	}

	public static boolean isTomcat() {
		if (!tomcat) {
			tomcat = isPresent(
					"/org/apache/catalina/startup/Bootstrap.class");

			if (!tomcat) {
				tomcat = isPresent(
						"/org/apache/catalina/startup/Embedded.class");
			}
		}
		return tomcat;
	}

	public static boolean isWebLogic() {

		if (!weblogic) {
			weblogic = isPresent(
					"/weblogic/Server.class");
		}
		return weblogic;
	}

	public static boolean isJetty() {
		if (!jetty) {
			jetty = isPresent(
					"/org/mortbay/jetty/Server.class");
		}
		return jetty;
	}
	
	private static boolean isPresent(String className) {
		try {
			ClassLoader.getSystemClassLoader().loadClass(className);
			return true;
		}
		catch (ClassNotFoundException cnfe) {

			if (ServerType.class.getResource(className) != null) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static String getAutodeployDir() {
		if(autodeployDir == null) {
			String serverId = null;
			if(isGlassfish()) {
				serverId = GLASSFISH_ID;
				autodeployDir = serverHome + File.separator + "autodeploy";
			} else if(isTomcat()) {
				serverId = TOMCAT_ID;
				autodeployDir = serverHome + File.separator + "webapps";
			} else if(isJetty()) {
				serverId = JETTY_ID;
				autodeployDir = serverHome + File.separator + "webapps";
			} else if(isWebLogic()) {
				serverId = WEBLOGIC_ID;
				autodeployDir = serverHome + File.separator + "autodeploy";
			} else {
				autodeployDir = getAutodeployPropertyValue();
			}
			if(logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "PSPL_CSPPAM0039",
						new String[] { serverId, autodeployDir });
			}
		}
		return autodeployDir;
	}

	public static String getServerHome() {
		if(serverHome == null) {
			String serverId = null;
			if(isGlassfish()) {
				serverId = GLASSFISH_ID;
				serverHome = getGlassFishDomainRoot();
			} else if(isTomcat()) {
				serverId = TOMCAT_ID;
				serverHome = getTomcatHome();
			} else if(isJetty()) {
				serverId = JETTY_ID;
				serverHome = getJettyHome();
			} else if(isWebLogic()) {
				serverId = WEBLOGIC_ID;
				serverHome = getWebLogicHome();
			} else {
				serverId = getServerType();
				if(autodeployDir == null) {
					autodeployDir = getAutodeployPropertyValue();
				}
				if(autodeployDir != null) {
					int index = autodeployDir.indexOf("/");
					if(index != -1) {
						serverHome = autodeployDir.substring(0, index);
					}
				}
			}
			if(logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "PSPL_CSPPAM0003",
						new String[] { serverId, serverHome });
			}
		}
		return serverHome;
	}

	private static String getGlassFishDomainRoot() {
		return System.getProperty("com.sun.aas.instanceRoot");
	}

	private static String getTomcatHome() {
		return System.getProperty("catalina.base");
	}

	private static String getJettyHome() {
		return System.getProperty("jetty.home");
	}

	private static String getWebLogicHome() {
		return System.getenv("DOMAIN_HOME");
	}

    private static void loadConfigFile() {
		if(properties.isEmpty()) {
			InputStream configStream = null;
			try {
				configStream = Thread.currentThread().getContextClassLoader().
						getResourceAsStream(PC_ENV_CONF);
				if(configStream != null) {
					properties.load(configStream);
				}
			} catch (IOException e) {
				logger.log(Level.WARNING, "PSPL_CSPPAM0040", e.getMessage());
			} finally {
				if (configStream != null) {
					try {
						configStream.close();
					} catch (IOException e) {
						//drop through
					}
				}
			}
			if(logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "PSPL_CSPPAM0041", properties);
			}
		}
    }

	public static String getAutodeployPropertyValue() {
		String autodeployValue = getAutodeployProperty();
		if(autodeployValue == null) {
			return autodeployValue;
		}
		String serverDir = null;
		String serverAutoDeployDir = null;
		String property = null;
		int index = autodeployValue.indexOf("/");
		if(index != -1) {
			serverDir = autodeployValue.substring(index+1);
			int i = autodeployValue.indexOf("${");
			int j = autodeployValue.indexOf("}");
			if(i != -1 && j != -1) {
				property = autodeployValue.substring(i+2, j);
			}
		}

		if(property != null) {
			String dirValue = System.getProperty(property);
			if(dirValue != null) {
				serverAutoDeployDir = dirValue + "/" + serverDir;
			}
		} else {
			serverAutoDeployDir = autodeployValue;
		}
		File file = new File(serverAutoDeployDir);
		if(file.exists()) {
			logger.log(Level.INFO, "PSPL_CSPPAM0042", serverAutoDeployDir);
		} else {
			logger.log(Level.WARNING, "PSPL_CSPPAM0042", serverAutoDeployDir);
		}
		return serverAutoDeployDir;
	}

	private static String getAutodeployProperty() {
		return properties.getProperty(getServerType() + AUTO_DEPLOY_DIR_SUFFIX);
	}

	private static String getServerType() {
		String serverType = properties.getProperty(SERVER_NAME);
		if(serverType == null || serverType.trim().length() == 0) {
			return null;
		}
		return serverType;
	}

}
