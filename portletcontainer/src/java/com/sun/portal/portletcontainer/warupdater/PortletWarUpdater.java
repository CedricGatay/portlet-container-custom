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


package com.sun.portal.portletcontainer.warupdater;

import com.sun.portal.portletcontainer.common.PortletDeployConfigReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 * PortletWarUpdater is responsible for inserting the Context Params and
 * PortletAppEngine servlet definition into the web.xml of the war file.
 */
public class PortletWarUpdater {

    private static Logger logger = PortletWarUpdaterUtil.getLogger(
                PortletWarUpdater.class, "PWULogMessages");

    private File warFile;
    private JarFile jar;
    private static Properties configProps;
    private String warFileLocation;
    private static final String PORTLET_TLD_FILE = "sun-portlet.tld";
    private static final String PORTLET_2_0_TLD_FILE = "sun-portlet_2_0.tld";
    private static final String XML_PORTLET_REQUEST_JS_FILE = "XMLPortletRequest.js";
    private static final String JS_PREFIX = "js" + "/";
	private static final String JS_DIR = "js";
    private static final String WEB_INF_PREFIX = "WEB-INF" + "/";
	private static final String WEB_INF_DIR = "WEB-INF";
    private static final String WEB_XML = "web.xml";
    private static final String DEFAULT_WEB_XML = "default-web.xml";
    private static final String WEB_XML_NAME = WEB_INF_PREFIX + WEB_XML;
    private String fileSuffix = "";
    public static final String ADD_WEB_XML = "addWebXML";

    /**
     * Constructor which takes customized deployConfig properties file
     */
    public PortletWarUpdater() {
        configProps = 
                PortletDeployConfigReader.getPortletDeployDefaultConfigProperties();
    }

    /**
     * Constructor which takes java.util.Properties object containing
     * customized config properties.
     *
     * @param deployConfigCustomizedProperties the customized properties
     */
    public PortletWarUpdater(Properties deployConfigCustomizedProperties) {
        configProps = 
                PortletDeployConfigReader.getPortletDeployConfigProperties(
                    deployConfigCustomizedProperties);
    }
        
    /**
     * Constructor which takes customized deployConfig properties file
     *
     * @param deployConfigFileLocation the location of the PortletDeployConfig.properties file
     */
    public PortletWarUpdater(String deployConfigFileLocation) {
        configProps = 
                PortletDeployConfigReader.getPortletDeployConfigProperties(
                deployConfigFileLocation);
        
        logger.log(Level.FINEST, "PSPL_CSPPCWU0004", warFileLocation);
    }

    
    /**
     * Prepares the portlet web application by inserting portlet container specific
     * artifacts.
     * @param warFile java.io.File object for portlet web application 
     * @param warFileDestination    
     *
     * @throws com.sun.portal.portletcontainer.warupdater.PortletWarUpdaterException if any
     *          exception occurs while inserting portlet container specific artifacts.
     * @return the prepared portlet web application
     */
    
    public boolean preparePortlet(File warFile, String warFileDestination) 
            throws PortletWarUpdaterException {

        if (warFileDestination == null) {
            throw new NullPointerException("The location where the updated war will be stored cannot be null");
        }
        
        if (!PortletWarUpdaterUtil.makeDir(warFileDestination)) {
            Object[] tokens = {warFileDestination};
            throw new PortletWarUpdaterException("cannotCreateDirectory", tokens);
        }

        if(logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "PSPL_CSPPCWU0005", 
                    new String[]{warFile.getAbsolutePath(), warFileDestination});
        }
       
        this.warFile = warFile;
        this.warFileLocation = warFileDestination;
        String warNameOnly = PortletWarUpdaterUtil.getWarName(warFile.getName());
        this.fileSuffix = PortletWarUpdaterUtil.getFileSuffix(warNameOnly);
        
        String portletAppName = warNameOnly.substring(0,
                warNameOnly.lastIndexOf('.'));
        logger.log(Level.FINER, "PSPL_CSPPCWU0006", portletAppName);

        try {
            jar = new JarFile(warFile);
        } catch (IOException ioe) {
            Object[] tokens = {warNameOnly};
            throw new PortletWarUpdaterException("errorGettingJarFile", ioe,
                    tokens);
        }

        // update web.xml of the portlet war
        logger.log(Level.FINER, "PSPL_CSPPCWU0008");
        File newWebXMLFile = null;
        InputStream webXMLStream = null;
        try {
            ZipEntry webXMLEntry = jar.getEntry(WEB_INF_PREFIX + WEB_XML);
            if(webXMLEntry == null){
                if(shouldAddWebXML()){
                    //Get hold on bundled default web.xml 
                    webXMLStream = getDefaultWebXML();
                } else {
                    logger.log(Level.INFO, "PSPL_CSPPCWU0003");
                    return false;
                }
            } else {
                webXMLStream = (InputStream) jar.getInputStream(webXMLEntry);
            }
            
            newWebXMLFile = PortletWebAppUpdater.addWebAppParam(
                    webXMLStream, configProps, portletAppName);

        } catch (IOException ioe) {
            Object[] tokens = {portletAppName};
            throw new PortletWarUpdaterException("errorUpdatingWebApp", ioe,
                    tokens);
        } catch (Exception ex) {
            Object[] tokens = {portletAppName};
            throw new PortletWarUpdaterException("errorUpdatingWebApp", ex,
                    tokens);
        } finally {
            if (webXMLStream != null) {
                try {
                    webXMLStream.close();
                } catch (IOException ioe) {
                    throw new PortletWarUpdaterException("errorStreamClose", ioe);
                }
            }
        }

        //Using the new web.xml, get updated war file
        String warFileName = portletAppName + fileSuffix;
        File destFile = new File(warFileDestination, warFileName);
        try {
            File newWarFile = getUpdatedWarFile(newWebXMLFile);
            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "PSPL_CSPPCWU0009",
                        newWarFile.getAbsolutePath());
            }
            if (newWarFile != null) {
                PortletWarUpdaterUtil.copyFile(newWarFile, destFile, true, false);
                //delete the temporary war file.
                newWarFile.delete();
                //warFileName = destFile.getAbsolutePath();
            } else {
                Object[] tokens = {warFileName};
                throw new PortletWarUpdaterException("errorJarUpdate", tokens);
            }            
        } catch (IOException ioe) {
            Object[] tokens = {warFileName};
            throw new PortletWarUpdaterException("errorJarUpdate", ioe, tokens);
        } catch (Exception ex) {
            Object[] tokens = {warFileName};
            throw new PortletWarUpdaterException("errorJarUpdate", ex, tokens);
        }
        
        return true;
    }

	/**
     * Prepares the portlet web application directory by inserting portlet container specific
     * artifacts.
     * @param explodedDirectory java.io.File object for portlet web application's exploded directory
     *
     * @throws com.sun.portal.portletcontainer.warupdater.PortletWarUpdaterException if any
     *          exception occurs while inserting portlet container specific artifacts.
     * @return the prepared portlet web application's exploded directory
     */

    public boolean preparePortlet(String portletAppName, File explodedDirectory)
            throws PortletWarUpdaterException {
		
		if(explodedDirectory == null) {
			throw new NullPointerException("The exploded directory cannot be null");
        }

		if(!explodedDirectory.isDirectory()) {
			Object[] tokens = {explodedDirectory.getAbsolutePath()};
			throw new PortletWarUpdaterException("notADirectory", tokens);
		}

        if(logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "PSPL_CSPPCWU0014", portletAppName);
        }

		File webXMLFile = new File(explodedDirectory + File.separator
									+ WEB_INF_DIR + File.separator
									+ "web.xml");

        InputStream webXMLStream = null;
		File newWebXMLFile = null;

		if(webXMLFile == null || !webXMLFile.exists()) {
			if(shouldAddWebXML()){
				//Get hold on bundled default web.xml
				webXMLStream = getDefaultWebXML();
				try {
					PortletWarUpdaterUtil.copyFile(webXMLStream, webXMLFile);
				} catch(IOException ioe) {
					Object[] tokens = {explodedDirectory};
					throw new PortletWarUpdaterException("cannotCopyWebXMLToDirectory", ioe, tokens);
				}

			} else {
				logger.log(Level.INFO, "PSPL_CSPPCWU0003");
                return false;
			}
		}

		try {

			webXMLStream = new FileInputStream(webXMLFile);

			newWebXMLFile = PortletWebAppUpdater.addWebAppParam(
                    webXMLStream, configProps, portletAppName);

		} catch(IOException ioe) {
			Object[] tokens = {portletAppName};
            throw new PortletWarUpdaterException("errorUpdatingWebApp", ioe,
                    tokens);
        } catch (Exception ex) {
            Object[] tokens = {portletAppName};
            throw new PortletWarUpdaterException("errorUpdatingWebApp", ex,
                    tokens);
        } finally {
            if (webXMLStream != null) {
                try {
                    webXMLStream.close();
                } catch (IOException ioe) {
                    throw new PortletWarUpdaterException("errorStreamClose", ioe);
                }
            }
        }

		try {
			//Use the new new web.xml
			PortletWarUpdaterUtil.copyFile(newWebXMLFile, webXMLFile, true, true);

			addFile(explodedDirectory, WEB_INF_DIR, PORTLET_TLD_FILE);
			addFile(explodedDirectory, WEB_INF_DIR, PORTLET_2_0_TLD_FILE);
			addFile(explodedDirectory, JS_DIR, XML_PORTLET_REQUEST_JS_FILE);

		} catch (IOException ioe) {
			Object[] tokens = {explodedDirectory};
            throw new PortletWarUpdaterException("errorDirectoryUpdate", ioe, tokens);
		} catch(Exception ex) {
			Object[] tokens = {explodedDirectory};
            throw new PortletWarUpdaterException("errorDirectoryUpdate", ex, tokens);
		}

		return true;

	}
    private File getUpdatedWarFile(File webXMLFile) throws Exception {

        // Create temp war file
        // so, recreate the temp war : "abc" + ".war" + ".tmp
        String tempWarFileName = warFile.getName().substring(0,
                warFile.getName().lastIndexOf('.')) + fileSuffix + ".tmp";
        File tempJarFile = new File(warFileLocation, tempWarFileName);
        tempJarFile.deleteOnExit();

        // Initialize a flag that will indicate that the jar was updated.
        boolean jarUpdated = false;

        try {
            // Create a temp jar file with no manifest. (The manifest will
            // be copied when the entries are copied.)
            JarOutputStream tempJar = new JarOutputStream(
                    new FileOutputStream(tempJarFile));

            // Allocate a buffer for reading entry data.
            byte[] buffer = new byte[1024];
            int bytesRead;

            try {
                //open the web.xml file
                FileInputStream webXMLin = new FileInputStream(webXMLFile);

                try {
                    // Create a jar entry and add it to the temp jar.
                    JarEntry entry = new JarEntry(WEB_XML_NAME);
                    tempJar.putNextEntry(entry);

                    // Read the file and write it to the jar.
                    while ((bytesRead = webXMLin.read(buffer)) != -1) {
                        tempJar.write(buffer, 0, bytesRead);
                    }

                } finally {
                    webXMLin.close();
                }

                // Add portlet tld files to the war
				addFile(tempJar, WEB_INF_PREFIX, PORTLET_TLD_FILE);
				addFile(tempJar, WEB_INF_PREFIX, PORTLET_2_0_TLD_FILE);
				addFile(tempJar, JS_PREFIX, null);
				addFile(tempJar, JS_PREFIX, XML_PORTLET_REQUEST_JS_FILE);

                // Loop through the jar entries and add them to the temp jar,
                // skipping the entry that was added to the temp jar already.
                for (Enumeration entries = jar.entries(); entries.hasMoreElements();) {
                    // Get the next entry.
                    JarEntry entry = (JarEntry) entries.nextElement();
                    InputStream entryStream = null;
                    try {
                        // If the entry has not been added already, add it.
                        if (entry.getName().equals(WEB_XML_NAME) ||
                                entry.getName().equals(WEB_INF_PREFIX + PORTLET_TLD_FILE) ||
								entry.getName().equals(WEB_INF_PREFIX + PORTLET_2_0_TLD_FILE) ||
								entry.getName().equals(JS_PREFIX) ||
								entry.getName().equals(JS_PREFIX + XML_PORTLET_REQUEST_JS_FILE)) {

							continue;

						} else {
                            // Get an input stream for the entry.
                            entryStream = jar.getInputStream(entry);

                            // Read the entry and write it to the temp jar.
                            tempJar.putNextEntry(new JarEntry(entry.getName()));

                            while ((bytesRead = entryStream.read(buffer)) != -1) {
                                tempJar.write(buffer, 0, bytesRead);
                            }
                        }
                    } finally {
                        if (entryStream != null) {
                            entryStream.close();
                        }
                    }
                }
                jarUpdated = true;
            } catch (Exception ex) {
                logger.log(Level.INFO, "PSPL_CSPPCWU0001", ex);
                // Add a stub entry here, so that the jar will close without an
                // exception.
                //Why is this exception eaten up here ? Changing it to throw the exception back.
                //tempJar.putNextEntry(new JarEntry("stub"));
                throw ex;
            } finally {
                tempJar.close();
            }
        } finally {
            jar.close();
            // If the jar was not updated, delete the temp jar file.
            if (!jarUpdated) {
                tempJarFile.delete();
            }
        }
        // If the jar was updated, delete the original jar file and rename the
        // temp jar file to the original name.
        if (jarUpdated) {
            return tempJarFile;
        }
        return null;
    }

	// If filename is null, adds the prefix as a directory
    private void addFile(JarOutputStream tempJar, String prefix, 
			String fileName) throws Exception {

		if(fileName == null) {
            JarEntry entry = new JarEntry(prefix);
            tempJar.putNextEntry(entry);
			return;
		}

        InputStream resourceStream = getResourceAsStream(fileName);

        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            // Create a jar entry and add it to the temp jar.
            JarEntry entry = new JarEntry(prefix + fileName);
            tempJar.putNextEntry(entry);

            // Read the file and write it to the jar.
            while ((bytesRead = resourceStream.read(buffer)) != -1) {
                tempJar.write(buffer, 0, bytesRead);
            }
        } finally {
            if (resourceStream != null) {
                resourceStream.close();
            }
        }
    }

	private void addFile(File docBase, String dir,
            String fileName) throws Exception {

        InputStream resourceStream = getResourceAsStream(fileName);

		File dirObj = new File(docBase + File.separator + dir);
		if(!dirObj.exists())
			dirObj.mkdirs();

		File destFile = new File(dirObj, fileName);
		PortletWarUpdaterUtil.copyFile(resourceStream, destFile);
		
    }

    private InputStream getResourceAsStream(String fileName) {
        InputStream resourceStream = null;
        if(fileName.equals(PORTLET_TLD_FILE) || fileName.equals(PORTLET_2_0_TLD_FILE)) {
            resourceStream =
                this.getClass().getClassLoader().getResourceAsStream("META-INF/" + fileName);
        } else {
            resourceStream =
                this.getClass().getClassLoader().getResourceAsStream(fileName);
        }
        return resourceStream;
    }

    private InputStream getDefaultWebXML(){
        return this.getClass().getClassLoader().getResourceAsStream(
                DEFAULT_WEB_XML);
    }
    
    /**
     * Checks whether a blank web.xml is required to inserted.
     */     
    private boolean shouldAddWebXML(){
        return (("true").equals(
                configProps.getProperty(ADD_WEB_XML)))? true : false;
    }
    
    public static void main(String args[]){
        try{
            
            String unMessagedWarFile = "D:/work/PorletContainer/samples/WelcomePortlet/dist/WelcomePortlet.war";
            String warDestinationFolder = "d:/";
            try{
                unMessagedWarFile = args[0];
                warDestinationFolder = args[1];
            }catch(Exception ee){
                System.out.println("Number of input parameters: " + args.length);
            }
            //Test1
            //PortletWarUpdater warUpdater = new PortletWarUpdater();
            //Test2
            System.out.println("Preparing portlet : " + unMessagedWarFile);
            Properties customizedProperties = new Properties();
            //customizedProperties.put(ADD_WEB_XML, "true");
            customizedProperties.setProperty("request_response_factory.maxSizeParam", "152");
            PortletWarUpdater warUpdater = new PortletWarUpdater(customizedProperties);

           // boolean result = warUpdater.preparePortlet(
           //         new File(unMessagedWarFile), warDestinationFolder);
			warUpdater.preparePortlet(args[0], new File(args[1]));
           // System.out.println("Prepare portlet call " + (result? "success" : "failed") );
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}