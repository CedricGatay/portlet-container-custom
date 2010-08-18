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

import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import com.sun.portal.portletcontainer.admin.registry.PortletRegistryTags;
import com.sun.portal.portletcontainer.warupdater.PortletWarUpdaterUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * PortletRegistryHelper is a Helper class to write to and read from the
 * portlet registry xml files.
 */
public class PortletRegistryHelper implements PortletRegistryTags {
    
    //private static final String PORTLET_CONTAINER_DIR_PROPERTY = "com.sun.portal.portletcontainer.dir";
    private static final String PORTLET_CONTAINER_DEFAULT_DIR_NAME = "portlet-container";
    private static final String PORTLET_CONTAINER_DEFAULT_DATA_DIR_NAME = "data";
    private static final String PORTLET_CONTAINER_DEFAULT_LOG_DIR_NAME = "logs";
    private static final String PORTLET_CONTAINER_DEFAULT_WAR_DIR_NAME = "war";
    private static final String PORTLET_CONTAINER_DEFAULT_AUTODEPLOY_DIR_NAME = "autodeploy";
    private static final String PORTLET_CONTAINER_DEFAULT_CONFIG_DIR_NAME = "config";
    private static final String PORTLET_CONTAINER_HOME_TOKEN = "@portlet-container-home@";
    private static final String PORTLET_CONTAINER_HOME_SYS_PROPERTY = "com.sun.portal.portletcontainer.dir";
    
    private static Logger logger = Logger.getLogger(PortletRegistryHelper.class.getPackage().getName(),
            "PALogMessages");
    
    private static String portletContainerHome = null;

    private PortletRegistryHelper() {
    }
    
    public static DocumentBuilder getDocumentBuilder() throws PortletRegistryException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = dbf.newDocumentBuilder();
            docBuilder.setEntityResolver(new NoOpEntityResolver());
        } catch (ParserConfigurationException pce){
            throw new PortletRegistryException(pce);
        }
        return docBuilder;
    }
    
    public static Document readFile(File file) throws PortletRegistryException {
        try {
            return getDocumentBuilder().parse(file);
        }catch(SAXException saxe){
            throw new PortletRegistryException(saxe);
        }catch(IOException ioe){
            throw new PortletRegistryException(ioe);
        }
    }
    
    public static Element getRootElement(Document document) {
        if(document != null)
            return document.getDocumentElement();
        return null;
    }
    
    
    public static synchronized void writeFile(Document document, File file) throws PortletRegistryException {
        try {
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");

            DOMSource source = new DOMSource(document);
            //StreamResult result = new StreamResult(System.out);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch(TransformerConfigurationException tce){
            throw new PortletRegistryException(tce);
        } catch(TransformerException te){
            throw new PortletRegistryException(te);
        } catch(Exception e) {
            throw new PortletRegistryException(e);
        }
    }

    public static synchronized String DOMtoString(Document document) throws PortletRegistryException {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(document);
			StringWriter outString = new StringWriter();
            StreamResult result = new StreamResult(outString);
            transformer.transform(source, result);
			return outString.toString();

        } catch(TransformerConfigurationException tce){
            throw new PortletRegistryException(tce);
        } catch(TransformerException te){
            throw new PortletRegistryException(te);
        } catch(Exception e) {
            throw new PortletRegistryException(e);
        }
    }

    public static synchronized Document StringToDOM(String propertiesXmlString) throws PortletRegistryException {
        try {
			DocumentBuilder builder = getDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(propertiesXmlString)));
			return document;
        } catch(Exception e) {
            throw new PortletRegistryException(e);
        }
    }

	private static String getPortletContainerHome() {
		String home = ServerType.getServerHome();
		if(home != null) {
			home = home + "/" + PORTLET_CONTAINER_DEFAULT_DIR_NAME;
		}
		return home;
	}

    private static String getPortletContainerDir() {
        if(portletContainerHome == null) {
			portletContainerHome = getPortletContainerHome();
			if(portletContainerHome == null || PORTLET_CONTAINER_HOME_TOKEN.equals(portletContainerHome)){
				portletContainerHome = System.getProperty(PORTLET_CONTAINER_HOME_SYS_PROPERTY);
				if(portletContainerHome == null) {
					portletContainerHome = System.getProperty("user.home") + File.separator
							+ PORTLET_CONTAINER_DEFAULT_DIR_NAME;
				}
			}

			if(logger.isLoggable(Level.INFO)){
				logger.log(Level.INFO, "PSPL_CSPPAM0006", portletContainerHome);
			}
        }
        return portletContainerHome;
    }
    
    public static String getAutoDeployLocation()  {
        String autoDeployLocation = getPortletContainerDir() + File.separator
                + PORTLET_CONTAINER_DEFAULT_AUTODEPLOY_DIR_NAME;
        if(!makeDir(autoDeployLocation)) {
            throw new RuntimeException("cannotCreateDirectory");
        }
        return autoDeployLocation;
    }
    
    public static String getRegistryLocation() throws PortletRegistryException {
        String registryLocation = getPortletContainerDir() + File.separator
                + PORTLET_CONTAINER_DEFAULT_DATA_DIR_NAME;
        if(!makeDir(registryLocation)) {
            throw new PortletRegistryException("cannotCreateDirectory");
        }
        return registryLocation;
    }
    
    public static String getLogLocation() throws PortletRegistryException {
        String logDirLocation = getPortletContainerDir() + File.separator
                + PORTLET_CONTAINER_DEFAULT_LOG_DIR_NAME;
        if(!makeDir(logDirLocation)) {
            throw new PortletRegistryException("cannotCreateDirectory");
        }
        return logDirLocation;
    }
    
    public static String getWarFileLocation() throws PortletRegistryException {
        String warFileLocation = getPortletContainerDir() + File.separator
                + PORTLET_CONTAINER_DEFAULT_WAR_DIR_NAME;
        if(!makeDir(warFileLocation)) {
            throw new PortletRegistryException("cannotCreateDirectory");
        }
        return warFileLocation;
    }
    
    public static String getUpdatedAbsoluteWarFileName(String warFileName) {
        StringBuffer warFileLocation = new StringBuffer();
        try {
            warFileLocation.append(PortletRegistryHelper.getWarFileLocation());
        } catch (PortletRegistryException pre){
            warFileLocation.append("");
        }
        
        String warName = PortletWarUpdaterUtil.getWarName(warFileName);
        warFileLocation.append(File.separator);
        warFileLocation.append(warName);
        return warFileLocation.toString();
    }
    
    public static String getConfigFileLocation() {
        String configFileLocation = getPortletContainerDir() + File.separator
                + PORTLET_CONTAINER_DEFAULT_CONFIG_DIR_NAME;
        return configFileLocation;
    }

    private static boolean makeDir(String dirName) {
        File dir = new File(dirName);
        if (dir.exists()) {
            return true;
        } else {
            return dir.mkdirs();
        }
    }
}
