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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 *
 */
public class PortletContainerConfigurator {

    private static Logger logger = Logger.getLogger(PortletContainerConfigurator.class.getPackage().getName(),
            "PCSDKLogMessages");
    private static Logger configuratorLogger = Logger.getLogger("com.sun.portal.portletcontainer.configurator",
            "PCSDKLogMessages");
    private static final String PORTLET_CONTAINER_ZIP = "portlet-container.zip";
    
    private static final String ANT_FILE = "setup.xml";
    private static final String PC_LOG_FILE = "portlet-container-configurator.out";
    private static final String PC_CONFIGURATOR_LOG_FILE = "portlet-container-configurator.log";
    private static final String LOG_DIRECTORY = "logs";
    protected static final String DOMAIN_DIRECTORY = InstallerMessages.getString("PortletContainerConfigurator.DomainDirectory");
    protected static final String WEBAPPS_DIRECTORY = InstallerMessages.getString("PortletContainerConfigurator.WebAppsDirectory");

    public PortletContainerConfigurator() {
        super();
    }

    // Modify glassfish home, pc home
    // copy lib to domain root
    // copy war to autodeploy
    public static void main(String[] args) {

        if (args.length == 0) {
            // Use GUI installer
            PortletContainerConfigurator configurator = new PortletContainerConfigurator();
            GUIInstaller installer = new GUIInstaller(configurator);
            installer.init(500, 135);
        } else {
            // You passed in CLI arguments, you must be knowing how to use it!
            // Check the input
            if (args.length < 2) {
                logger.severe("configuration-usage");
                return;
            }

            //args[0] = <AppserverInstallDir>
            //args[1] = <DomainRoot>
            //args[2] = <Container>
            //args[3] = <ANT_HOME>
            PortletContainerConfigurator configurator = new PortletContainerConfigurator();
            try {
                String container = null;
                if(args.length >= 3) {
                    container = args[2];
                    String[] supportedContainers = InstallerFactory.getSupportedContainers();
                    boolean validContainer = false;
                    for (int i = 0; i < supportedContainers.length; i++) {
                        if (supportedContainers[i].equalsIgnoreCase(container)) {
                            validContainer = true;
                            break;
                        }
                    }
                    if (!validContainer) {
                        logger.severe("configuration-usage");
                        return;
                    }
                }
                if (container == null || container.equalsIgnoreCase(Installer.GLASSFISH)) {
                    // Check if the Glassfish has ANT, if not ask for it
                    File antDir = new File(args[0] + "/lib/ant");
                    String antHome = null;
                    if(antDir.exists()) {
                        antHome = args[0] + "/lib/ant";
                    } else {
                        antHome = getAntHome(args);
                        if (antHome == null) {
                            logger.severe("ant-not-available");
                            return;
                        }
                    }
                    configurator.install(args[0], args[1], container, antHome);
                } else {
                    String antHome = getAntHome(args);
                    if (antHome == null) {
                        logger.severe("ant-not-available");
                        return;
                    }
                    configurator.install(args[0], args[1], container, antHome);
                }
                logger.info("configuration-successful");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "configuration-failed", e);
            }
        }
    }

    public void install(String appServerInstallRoot, String domainRoot) throws Exception {
        install(appServerInstallRoot, domainRoot, null, null);
    }

    /**
     * Portlet container configurator extracts its files under <domaindir>/portlet-container directory.
     * So, all file required by portlet container in one domain is referenced from within domain directory.
     * Even though this does lead to duplication of files across domains, this is a preferred way of handling
     * going forward.
     */
    public void install(String appServerInstallRoot, String domainRoot,
            String container, String antHome) throws Exception {

        /* Get the appropriate Installer implementation */
        Installer installer = InstallerFactory.getInstance().getInstaller(container);

		File domainRootDir = new File(domainRoot);
		if(!domainRootDir.isDirectory()) {
			throw new Exception("The directory " + domainRoot + " is not valid");
		}

		checkInstallDomainDir(appServerInstallRoot, domainRoot, container);

		String pcConfigDestinationPath = installer.getPCHome(appServerInstallRoot,
                domainRoot);

        File f = new File(pcConfigDestinationPath);
        if (!f.exists()) {
            boolean status = f.mkdirs();
            if (status) {
                configuratorLogger.log(Level.FINEST, "create-dir-successful",
                        pcConfigDestinationPath);
            } else {
                logger.log(Level.WARNING, "create-dir-failed",
                        pcConfigDestinationPath);
            }
        }
        String logDirectory = pcConfigDestinationPath + File.separator + LOG_DIRECTORY;
        File logDir = new File(logDirectory);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
        String logfileName = logDirectory + File.separator + PC_LOG_FILE;
		initLoggerSettings(logDirectory);

		configuratorLogger.log(Level.INFO, "ant-home", antHome);

        // Extract the configurator jar to  different location
        extractJar(pcConfigDestinationPath, PORTLET_CONTAINER_ZIP);
        // Extract the zip file to the specified location
        extractZip(pcConfigDestinationPath, PORTLET_CONTAINER_ZIP);
        // Delete the zip file
        deleteZip(pcConfigDestinationPath, PORTLET_CONTAINER_ZIP);

        Properties properties = new Properties();
        installer.updateProperties(appServerInstallRoot, domainRoot, properties);

        File propertyFile = null;
        FileOutputStream fos = null;
        try {
            propertyFile = File.createTempFile("antconfig", null);
            propertyFile.deleteOnExit();
            fos = new FileOutputStream(propertyFile);
            properties.store(fos, null);
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "cannot-create-temp-file", ioe);
        } finally {
            fos.close();
        }

		if (antHome == null) {
            AntRunner.runAnt(appServerInstallRoot,
                    pcConfigDestinationPath + File.separator + ANT_FILE,
                    installer.getAntTarget(), propertyFile.getPath(),
                    logfileName);
        } else {
            AntRunner.executeAntTask(pcConfigDestinationPath + File.separator + ANT_FILE,
                    installer.getAntTarget(), getAntCommand(antHome),
                    propertyFile.getPath(), logfileName);
        }

        // Store App server home in pcenv.conf file
        storeAppServerHome(appServerInstallRoot, domainRoot,
                pcConfigDestinationPath, installer);
    }

    /*
     * Gets the ant home from the arguments or from environment
     */
    private static String getAntHome(String[] args) {
        String antHome = null;
        if (args.length == 4) {
            antHome = args[3];
        } else {
            antHome = System.getenv("ANT_HOME");
        }
        return antHome;
    }

    /*
     * Extract the jar contents
     */
    private void extractJar(String destination, String filename) throws IOException {
        if (configuratorLogger.isLoggable(Level.FINE)) {
            configuratorLogger.log(Level.FINE, "extract-jar",
                    destination + File.separator + filename);
        }
        JarFile jar = new JarFile(getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20",
                " "));
        ZipEntry entry = jar.getEntry(filename);
        if (entry == null) {
            logger.log(Level.SEVERE, "cannot-find-file", filename);
            return;
        }

        File file = new File(destination, entry.getName());
        InputStream inStream = jar.getInputStream(entry);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

        copyInputStream(inStream, out);
    }

    private void extractZip(String destination, String zipName) throws IOException {
        String absoluteZipName = destination + File.separator + zipName;
        if (configuratorLogger.isLoggable(Level.FINE)) {
            configuratorLogger.log(Level.FINE, "extract-zip", absoluteZipName);
        }
        ZipFile zipFile = new ZipFile(absoluteZipName);

        Enumeration entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            if (entry.isDirectory()) {
                if (configuratorLogger.isLoggable(Level.FINEST)) {
                    configuratorLogger.log(Level.FINEST, "extracting-dir",
                            destination + File.separator + entry.getName());
                }
                File file = new File(destination, entry.getName());
                file.mkdirs();
                continue;
            }
            if (configuratorLogger.isLoggable(Level.FINEST)) {
                configuratorLogger.log(Level.FINEST, "extracting-file",
                        destination + File.separator + entry.getName());
            }
            File file = new File(destination, entry.getName());
            InputStream inStream = zipFile.getInputStream(entry);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            copyInputStream(inStream, out);
        }

        zipFile.close();
    }

    private void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[2048];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }

    public void copyFile(String fromFile, String toFile) throws IOException {
        File from = new File(fromFile);
        File to = new File(toFile);

        if (configuratorLogger.isLoggable(Level.FINER)) {
            configuratorLogger.log(Level.FINER, "copy-file",
                    new String[]{fromFile, toFile});
        }
        FileInputStream inStream = new FileInputStream(from);

        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(to));
        copyInputStream(inStream, out);
    }

    private void storeAppServerHome(String appServerInstallRoot,
            String domainRoot, String portletContainerHome,
            Installer installer) throws Exception {

		Properties configProperties = new Properties();
        String configFileName = portletContainerHome + File.separator + 
				"config" + File.separator + Installer.SERVER_INFO_FILE;
        FileOutputStream outStream = null;
        try {
            installer.updateProperties(appServerInstallRoot, domainRoot,
                    configProperties);
            outStream = new FileOutputStream(configFileName);
            configProperties.store(outStream, "The Server Home");
        } finally {
            if (outStream != null) {
                outStream.close();
            }
            configuratorLogger.log(Level.FINE, "store-as-home", configFileName);
        }
    }

    private void deleteZip(String pcConfigDestinationPath,
            String zipName) {
        File file = new File(pcConfigDestinationPath, zipName);
        file.delete();
    }

    private String getAntCommand(String antHome) {
        if (System.getProperty("os.name").indexOf("Windows") != -1) {
            if (antHome.indexOf(' ') != -1) {
                antHome = "\"" + antHome + "\"";
            }
            return antHome + File.separator + "bin" + File.separator + "ant.bat";
        } else {
            return antHome + File.separator + "bin" + File.separator + "ant";
        }
    }

    public void uninstall(String appServerInstallRoot,
            String domainRoot) throws Exception {
        /* Get the appropriate Installer implementation */
        Installer installer = InstallerFactory.getInstance().getInstaller(null);
        String pcConfigDestinationPath = installer.getPCHome(appServerInstallRoot,
                domainRoot);
        Properties properties = new Properties();
        installer.updateProperties(appServerInstallRoot, domainRoot, properties);

        File propertyFile = null;
        FileOutputStream fos = null;
        try {
            propertyFile = File.createTempFile("antconfig", null);
            propertyFile.deleteOnExit();
            fos = new FileOutputStream(propertyFile);
            properties.store(fos, null);
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "cannot-create-temp-file", ioe);
        } finally {
            fos.close();
        }

        String logDirectory = pcConfigDestinationPath + File.separator + LOG_DIRECTORY;
        File logDir = new File(logDirectory);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
        String logfileName = logDirectory + File.separator + PC_LOG_FILE;
        AntRunner.runAnt(appServerInstallRoot,
                pcConfigDestinationPath + File.separator + ANT_FILE,
                installer.getUninstallAntTarget(), propertyFile.getPath(),
                logfileName);


        File pcHome = new File(pcConfigDestinationPath);
        pcHome.delete();
        return;
    }

    private static void initLoggerSettings(String logDirectory) {
        try {
			String logfileName = logDirectory + File.separator + PC_CONFIGURATOR_LOG_FILE;

            Handler fh = new FileHandler(logfileName, true);
            fh.setFormatter(new SimpleFormatter());

            Logger installerLogger = Logger.getLogger("com.sun.portal.portletcontainer.configurator");
            initLoggerSettings(installerLogger, fh);
        } catch (Exception e){
            //ignored
        }
    }

    private static void initLoggerSettings(Logger logger, Handler fh){
        logger.setUseParentHandlers(false);
        logger.addHandler(fh);
        logger.setLevel(Level.FINEST);
    }

	private void checkInstallDomainDir(String appServerInstallRoot,
			String domainRoot, String container) throws Exception {

		if(container == null || Installer.GLASSFISH.equalsIgnoreCase(container)) {
			String domainXml = domainRoot + File.separator + "config"
					+ File.separator + "domain.xml" ;
			File file = new File(domainXml);
			if(!file.exists()) {
				throw new Exception(domainRoot + " is not valid glassfish domain directory");
			}
		} else if(Installer.TOMCAT.equalsIgnoreCase(container)) {
			String commonLib = appServerInstallRoot + File.separator + "common"
					+ File.separator + "lib" ;
			File file = new File(commonLib);
			if(!file.exists()) {
				throw new Exception(appServerInstallRoot + " is not valid Tomcat5 server directory");
			}
		} else if(Installer.TOMCAT6.equalsIgnoreCase(container)) {
			File file = new File(appServerInstallRoot, "webapps");
			if(!file.exists()) {
				throw new Exception(appServerInstallRoot + " is not valid Tomcat6 server directory");
			}
		} else if(Installer.JETTY.equalsIgnoreCase(container)) {
			String jettyXml = appServerInstallRoot + File.separator + "etc"
					+ File.separator + "jetty.xml" ;
			File file = new File(jettyXml);
			if(!file.exists()) {
				throw new Exception(appServerInstallRoot + " is not valid Jetty server directory");
			}
		} else if(Installer.WEBLOGIC.equalsIgnoreCase(container)) {
			File file = new File(domainRoot, "startWebLogic.sh");
			if(!file.exists()) {
				throw new Exception(domainRoot + " is not valid WebLogic domain directory");
			}
		}
	}
}
