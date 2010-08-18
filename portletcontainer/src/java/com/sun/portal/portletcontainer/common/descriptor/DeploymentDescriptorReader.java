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
package com.sun.portal.portletcontainer.common.descriptor;

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.VendorPortletXML;
import com.sun.portal.portletcontainer.common.PortletContainerUtil;
import com.sun.portal.portletcontainer.common.PortletDeployConfigReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.zip.ZipEntry;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * The deployment descriptor reader reads the portlet deployment descriptor
 * file and parses the elements of the XML file.
 */
public class DeploymentDescriptorReader {

    private static final String WEB_INF_PREFIX = "WEB-INF" + "/";
    public static final String PORTLET_NAMESPACE =
            "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd";

    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(DeploymentDescriptorReader.class,
                                                             "PCCLogMessages");

    //global variables
    private Properties deployConfigProperties;
    private String version;

    /**
     * Constructor that uses ServletContext initialization parameters
     * to process the portlet.xml and vendor portlet.xml
     * 
     * @param context the ServletContext object
     */
    public DeploymentDescriptorReader(ServletContext context) {
        this.deployConfigProperties = new Properties();
        Enumeration e = context.getInitParameterNames();
        while (e.hasMoreElements()) {
            String parameter = (String) e.nextElement();
            deployConfigProperties.put(parameter,
                                       context.getInitParameter(parameter));
        }
    }

    /**
     * Constructor that uses PortletDeployConfig.properties file from
     * the deployConfigFileLocation to process the portlet.xml
     * 
     * @param deployConfigFileLocation the location of PortletDeployConfig.properties file
     */
    public DeploymentDescriptorReader(String deployConfigFileLocation) {
        this.deployConfigProperties = 
                PortletDeployConfigReader.getPortletDeployConfigProperties(deployConfigFileLocation);
        //this.warFile = warFile;
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "PSPL_PCCCSPPCCD0001",
                       deployConfigFileLocation);
        }
    }

    /**
     * Constructor that uses java.util.Properties object to initialize this class
     * 
     * @param deployConfigProperties properties required for parsing portlet.xml
     */
    public DeploymentDescriptorReader(Properties deployConfigProperties) {
        this.deployConfigProperties = deployConfigProperties;
    }
    
    
    /**
     * Reads the deployment descriptor's xml file, and returns the root
     * element.
     * <P>
     * In the case of unable to read the deployment descriptor, an error
     * is logged.
     * <P>
     * @param portletAppName the name of the portlet webapplication
     * @param portletStream The input stream of the deployment
     * descriptor's xml file
     * @param validate true if the portlet.xml needs to be validated against the schema
     * 
     * @return The root Element of the document, null if the resource does not
     * exists.
     */
    private Element readDeploymentDescriptor(String portletAppName,
                                             InputStream portletStream,
                                             boolean validate)
            throws DeploymentDescriptorException {
        Element element = null;
        byte[] portletXMLStreamBytes = null;
        ByteArrayOutputStream portletXMLByteOuputStream = null;
        InputStream portletXMLStreamParse = null;
        InputStream portletXMLStreamValidate = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //Added because of QName types in portlet 2.0
        dbf.setNamespaceAware(Boolean.TRUE);
        try {
            // parse an XML document into a DOM tree
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document document = null;
            // If validating the schema store the input stream as bytes.
            // This is because the stream has to used in both DOM generation
            // and also during schema validation
            if (validate) {
                portletXMLByteOuputStream = new ByteArrayOutputStream(4096);
                int data = -1;
                while((data = portletStream.read()) != -1) {
                    portletXMLByteOuputStream.write(data);
                }
                portletXMLStreamBytes = portletXMLByteOuputStream.toByteArray();
                portletXMLStreamParse = new ByteArrayInputStream(portletXMLStreamBytes);
                document = docBuilder.parse(portletXMLStreamParse);
            } else {
                document = docBuilder.parse(portletStream);
            }
            element = document.getDocumentElement();
            if (element != null) {
                //load portlet version
                if (element.hasAttribute(PortletDescriptorConstants.VERSION)) {
                    this.version = element.getAttribute(PortletDescriptorConstants.VERSION);
                }
            }

            // validating the schema
            if (validate) {
                if (this.version != null) {
                    Schema schema = PortletSchema.getSchema(this.version);
                    if (schema != null) {
                        // Create a Validator object, which can be used to validate
                        // an instance document.
                        Validator validator = schema.newValidator();

                        // Validate the Portlet XMl Stream
                        portletXMLStreamValidate = new ByteArrayInputStream(portletXMLStreamBytes);
                        validator.validate(new StreamSource(portletXMLStreamValidate));
                    }
                } else {
                    logger.log(Level.WARNING, "PSPL_PCCCSPPCCD0021");
                }
            }
        } catch (ParserConfigurationException pce) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "PSPL_PCCCSPPCCD0003", new String[] {portletAppName, pce.toString()});
            }
            throw new DeploymentDescriptorException("document builder cannot be created", pce);
        } catch (SAXException saxe) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "PSPL_PCCCSPPCCD0029", new String[] {portletAppName, saxe.toString()});
            }
            throw new DeploymentDescriptorException("error parsing portlet stream", saxe);
        } catch (IOException ioe) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "PSPL_PCCCSPPCCD0030", new String[] {portletAppName, ioe.toString()});
            }
            throw new DeploymentDescriptorException("error reading portlet stream", ioe);
        } finally {
            portletXMLStreamBytes = null;
            if(portletXMLByteOuputStream != null) {
                try {
                    portletXMLByteOuputStream.close();
                } catch (IOException ignored) {}
            }
            if(portletXMLStreamParse != null) {
                try {
                    portletXMLStreamParse.close();
                } catch (IOException ignored) {}
            }
            if(portletXMLStreamValidate != null) {
                try {
                    portletXMLStreamValidate.close();
                } catch (IOException ignored) {}
            }
        }
        return element;
    }

    /**
     * Loads the deployment descriptor into PortletAppDescriptor structure.
     * <P>
     *
     * @param portletAppName the name of the portlet webapplication
     * @param portletStream the portlet xml input stream
     * 
     * @return The PortletAppDescriptor object, null if portlet app descriptor
     * can not be read or can not be instantiated.
     * @throws com.sun.portal.portletcontainer.common.descriptor.DeploymentDescriptorException
     */
    public PortletAppDescriptor loadPortletAppDescriptor(String portletAppName,
                                                         InputStream portletStream)
            throws DeploymentDescriptorException {

        PortletAppDescriptor appDescriptor = null;
        if (portletStream != null) {
            Element root = readDeploymentDescriptor(portletAppName, portletStream,
                                                    isValidatePortletXML());
            if (root != null) {
                logger.log(Level.FINE, "PSPL_PCCCSPPCCD0004", portletAppName);
                appDescriptor = new PortletAppDescriptor(portletAppName);
                appDescriptor.load(root, PORTLET_NAMESPACE);
            }
        }

        return appDescriptor;
    }

    /**
     * Loads the web application deployment descriptor to read the
     * URL patterns and stores the URL patterns in the portlet application
     * descriptor
     * <P>
     *
     * @param portletAppDescriptor the PortletAppDescriptor object
     * @param webXmlStream the web xml input stream
     * 
     */
    public void readWebAppDescriptor(PortletAppDescriptor portletAppDescriptor,
                            InputStream webXmlStream)
            throws DeploymentDescriptorException {

        List<String> servletURLPatterns = new ArrayList<String>();
        if (webXmlStream != null) {
            try {
                // parse an XML document into a DOM tree
                DocumentBuilder docBuilder = PortletContainerUtil.getDocumentBuilder();
                Document document = docBuilder.parse(webXmlStream);
                Element element = document.getDocumentElement();
                if (element != null) {
                    NodeList servletMappingElements = element.getElementsByTagName("servlet-mapping");
                    for (int i = 0; i < servletMappingElements.getLength(); i++) {
                        Element servletMappingElement = (Element)servletMappingElements.item(i);
                        String urlPattern = 
                                PortletXMLDocumentHelper.getChildTextTrim(servletMappingElement, "url-pattern");
                        servletURLPatterns.add(urlPattern);
                    }
                }
            } catch (ParserConfigurationException pce) {
                if(logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, "PSPL_PCCCSPPCCD0033", 
                            new String[] {portletAppDescriptor.getName(), pce.toString()});
                }
                throw new DeploymentDescriptorException("document builder cannot be created", pce);
            } catch (SAXException saxe) {
                if(logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, "PSPL_PCCCSPPCCD0034", 
                            new String[] {portletAppDescriptor.getName(), saxe.toString()});
                }
                throw new DeploymentDescriptorException("error parsing web xml stream", saxe);
            } catch (IOException ioe) {
                if(logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, "PSPL_PCCCSPPCCD0035", 
                            new String[] {portletAppDescriptor.getName(), ioe.toString()});
                }
                throw new DeploymentDescriptorException("error reading web xml stream", ioe);
            } 
        }
        portletAppDescriptor.setURLPatterns(servletURLPatterns);
    }
    
    /**
     * Returns the deployment descriptor object that corresponds to the portlet.xml
     * 
     * @param portletAppName the name of the portlet webapplication
     * @param portletStream the portlet xml input stream
     * @param validate true if the portlet.xml needs to be validated against the schema
     * @return The PortletAppDescriptor object
     * 
     * @throws com.sun.portal.portletcontainer.common.descriptor.DeploymentDescriptorException
     */
    public PortletAppDescriptor getPortletAppDescriptor(String portletAppName,
                                                        InputStream portletStream,
                                                        boolean validate)
            throws DeploymentDescriptorException {
        PortletAppDescriptor appDescriptor = null;
        if (portletStream != null) {
            Element root = readDeploymentDescriptor(portletAppName, portletStream, validate);
            if (root != null) {
                logger.log(Level.FINE, "PSPL_PCCCSPPCCD0004", portletAppName);
                appDescriptor = new PortletAppDescriptor(portletAppName);
                appDescriptor.load(root, PORTLET_NAMESPACE);
            }
        }

        return appDescriptor;
    }

    /**
     * Loads the deployment descriptors for the vendor portlet xmls based on the
     * initialization parameters present in the Servlet Context
     * <P>
     *
     * @param context the ServletContext object
     *
     * @return The Map that has the vendor portlet xml name as the key and the value is a
     *         the object that represents the vendor portlet xml.
     * @throws com.sun.portal.portletcontainer.common.descriptor.DeploymentDescriptorException 
     * 
     */
    public Map<String, Object> getPortletExtensionDescriptors(ServletContext context)
            throws DeploymentDescriptorException {

        Map deploymentExtensionDescriptors = new HashMap<String, Object>();
        // get the list of vendor portlet xmls
        List<String> vendorNames = getVendorNames();
        for (String vendorName : vendorNames) {
            String name = deployConfigProperties.getProperty(getVendorPortletXMLNameProperty(vendorName));
            String implName = deployConfigProperties.getProperty(getVendorPortletXMLImplProperty(vendorName));
            if(name != null) {
                deploymentExtensionDescriptors.put(name,
                                                   loadVendorPortletXMLImpl(context, name, implName));
            }
        }
        return deploymentExtensionDescriptors;
    }

    /**
     * Processes the vendor portlet xml during deployment. This should be invoked 
     * when the portlet application is deployed. This is based on the values in the
     * PortletDeployConfig.properties file.After processing returns a Map where the key
     * is the name of the vendor portlet xml and the value is a Object that represents 
     * the vendor portlet xml.
     * 
     * @param warFile the Portlet WebApplication File that contains the vendor portlet xml
     * @param schemaLocation the location of the schema for vendor portlet xml
     * 
     * @return a Map where the key is the name of the vendor portlet xml and the 
     * value is a Object that represents the vendor portlet xml.
     *
     * @throws java.lang.Exception 
     * 
     */
    public Map<String, Object> processDeployPortletExtensionDescriptor(File warFile, String schemaLocation)
            throws Exception {

        // get the list of vendor portlet xmls
        List<String> vendorNames = getVendorNames();
        Map<String, Object> extensionDescriptors = new HashMap<String, Object>();
        for (String vendorName : vendorNames) {
            String name = deployConfigProperties.getProperty(getVendorPortletXMLNameProperty(vendorName));
            String implName = deployConfigProperties.getProperty(getVendorPortletXMLImplProperty(vendorName));
            String validate = deployConfigProperties.getProperty(getVendorPortletXMLValidateProperty(vendorName));
            InputStream vendorPortletXmlStream = null;
            try {
                vendorPortletXmlStream = getVendorPortletXmlStream(warFile, name);
                Object extensionDescriptor = processDeployVendorPortletXML(warFile, vendorPortletXmlStream, 
                                            name, implName, schemaLocation, validate);
                extensionDescriptors.put(name, extensionDescriptor);
            } finally {
                if (vendorPortletXmlStream != null) {
                    try {
                        vendorPortletXmlStream.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return extensionDescriptors;
    }

    /**
     * Processes the vendor portlet xml during deployment. This should be invoked 
     * when the portlet application is deployed. This is based on the values in the
     * PortletDeployConfig.properties file for the vendor. After processing returns
     * a Object that represents the vendor portlet xml.
     * 
     * @param vendorPortletXMLName the name of the vendor portlet xml
     * @param vendorPortletXmlStream the InputStream of the vendor portlet xml
     * @param schemaLocation the location of the schema for vendor portlet xml
     * 
     * @return The object that represents the vendor portlet xml.
     *
     * @throws java.lang.Exception 
     * 
     */
    public Object processDeployPortletExtensionDescriptor(String vendorPortletXMLName, InputStream vendorPortletXmlStream, String schemaLocation)
            throws Exception {

        // get the list of vendor portlet xmls
        Object extensionDescriptor = null;
        String vendorName = getVendorName(vendorPortletXMLName);
        if(vendorName != null) {
            String name = deployConfigProperties.getProperty(getVendorPortletXMLNameProperty(vendorName));
            String implName = deployConfigProperties.getProperty(getVendorPortletXMLImplProperty(vendorName));
            String validate = deployConfigProperties.getProperty(getVendorPortletXMLValidateProperty(vendorName));
            extensionDescriptor = processDeployVendorPortletXML(null, vendorPortletXmlStream, name, implName, schemaLocation, validate);
        } else {
            logger.log(Level.WARNING, "PSPL_PCCCSPPCCD0026", vendorPortletXMLName);
        }
        return extensionDescriptor;
    }

    /**
     * Processes the vendor portlet xml during undeployment. This should be invoked 
     * when the portlet application is undeployed. This is based on the values in the
     * PortletDeployConfig.properties file.
     * 
     * @param warFile the Portlet WebApplication File that contains the vendor portlet xml
     * @param schemaLocation the location of the schema for vendor portlet xml
     * 
     * @throws java.lang.Exception 
     */
    public void processUndeployPortletExtensionDescriptor(File warFile, String schemaLocation)
            throws Exception {

        // get the list of vendor portlet xmls
        List<String> vendorNames = getVendorNames();
        for (String vendorName : vendorNames) {
            String name = deployConfigProperties.getProperty(getVendorPortletXMLNameProperty(vendorName));
            String implName = deployConfigProperties.getProperty(getVendorPortletXMLImplProperty(vendorName));
            String validate = deployConfigProperties.getProperty(getVendorPortletXMLValidateProperty(vendorName));
            InputStream vendorPortletXmlStream = null;
            try {
                vendorPortletXmlStream = getVendorPortletXmlStream(warFile, name);
                processUnDeployVendorPortletXML(vendorPortletXmlStream, name, implName);
            } finally {
                if (vendorPortletXmlStream != null) {
                    try {
                        vendorPortletXmlStream.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    /**
     * Processes the vendor portlet xml during undeployment. This should be invoked 
     * when the portlet application is undeployed. This is based on the values in the
     * PortletDeployConfig.properties file.
     * 
     * @param vendorPortletXMLName the name of the vendor portlet xml
     * @param vendorPortletXmlStream the InputStream of the vendor portlet xml
     * @param schemaLocation the location of the schema for vendor portlet xml
     * 
     * @throws java.lang.Exception 
     */
    public void processUndeployPortletExtensionDescriptor(String vendorPortletXMLName, InputStream vendorPortletXmlStream, String schemaLocation)
            throws Exception {

        String vendorName = getVendorName(vendorPortletXMLName);
        if(vendorName != null) {
            String name = deployConfigProperties.getProperty(getVendorPortletXMLNameProperty(vendorName));
            String implName = deployConfigProperties.getProperty(getVendorPortletXMLImplProperty(vendorName));
            String validate = deployConfigProperties.getProperty(getVendorPortletXMLValidateProperty(vendorName));
            processUnDeployVendorPortletXML(vendorPortletXmlStream, name, implName);
        } else {
            logger.log(Level.WARNING, "PSPL_PCCCSPPCCD0026", vendorPortletXMLName);
        }
    }

    public String getVersion() {
        return version;
    }

    //default is true
    private boolean isValidatePortletXML() {
        boolean validate = true;
        if (deployConfigProperties != null) {
            String validateString = deployConfigProperties.getProperty(PortletDeployConfigReader.VALIDATE_PROPERTY);
            logger.log(Level.INFO, "PSPL_PCCCSPPCCD0002", validateString);
            if ("false".equals(validateString)) {
                validate = false;
            }
        }
        return validate;
    }

    // Get the list of vendor names from the properties, 
    // key will be of the form vendorPortletXML.<vendorName>.XXX
    private List<String> getVendorNames() {
        List<String> vendorNames = new ArrayList<String>();
        if (deployConfigProperties != null) {
            Enumeration e = deployConfigProperties.propertyNames();
            while (e.hasMoreElements()) {
                String property = (String) e.nextElement();
                if (property.startsWith(PortletDeployConfigReader.VENDOR_PORTLET_XML_PREFIX)) {
                    StringTokenizer tokens = new StringTokenizer(property, ".");
                    int count = tokens.countTokens();
                    if (count == 3) {
                        // second token is the vendor name
                        tokens.nextToken();
                        String vendorName = tokens.nextToken();
                        if (!vendorNames.contains(vendorName)) {
                            vendorNames.add(vendorName);
                        }
                    }
                }
            }
        }
        return vendorNames;
    }

    // Get the name of the vendor
    // key will be of the form vendorPortletXML.<vendorName>.name=<vendorPortletXMLName>
    private String getVendorName(String vendorPortletXMLName) {
        String vendorName = null;
        if (deployConfigProperties != null) {
            Enumeration e = deployConfigProperties.propertyNames();
            while (e.hasMoreElements()) {
                String property = (String) e.nextElement();
                if (property.startsWith(PortletDeployConfigReader.VENDOR_PORTLET_XML_PREFIX)
                    && property.endsWith(PortletDeployConfigReader.NAME_SUFFIX)) {
                    if(vendorPortletXMLName.equals(deployConfigProperties.get(property))) {
                    StringTokenizer tokens = new StringTokenizer(property, ".");
                        int count = tokens.countTokens();
                        if (count == 3) {
                            // second token is the vendor name
                            tokens.nextToken();
                            vendorName = tokens.nextToken();
                            break;
                        }
                    }
                }
            }
        }
        return vendorName;
    }

    private String getVendorPortletXMLNameProperty(String vendorName) {
        StringBuffer property = new StringBuffer();
        property.append(PortletDeployConfigReader.VENDOR_PORTLET_XML_PREFIX);
        property.append(".");
        property.append(vendorName);
        property.append(".");
        property.append(PortletDeployConfigReader.NAME_SUFFIX);
        return property.toString();
    }

    private String getVendorPortletXMLImplProperty(String vendorName) {
        StringBuffer property = new StringBuffer();
        property.append(PortletDeployConfigReader.VENDOR_PORTLET_XML_PREFIX);
        property.append(".");
        property.append(vendorName);
        property.append(".");
        property.append(PortletDeployConfigReader.IMPL_SUFFIX);
        return property.toString();
    }

    private String getVendorPortletXMLValidateProperty(String vendorName) {
        StringBuffer property = new StringBuffer();
        property.append(PortletDeployConfigReader.VENDOR_PORTLET_XML_PREFIX);
        property.append(".");
        property.append(vendorName);
        property.append(".");
        property.append(PortletDeployConfigReader.VALIDATE_SUFFIX);
        return property.toString();
    }

    private VendorPortletXML getVendorPortletXMLImpl(String implName) throws Exception {
        Class portletClass = Thread.currentThread().getContextClassLoader().
                    loadClass(implName);
        return (VendorPortletXML) (portletClass.newInstance());
    }
    
    private Object processDeployVendorPortletXML(File warFile, 
                                            InputStream vendorPortletXmlStream,
                                            String name,
                                            String implName,
                                            String schemaLocation,
                                            String validate) throws Exception {

        Object extensionDescriptor = null;
        // If vendor portlet xml is present continue
        if(vendorPortletXmlStream != null) {
            try {
                VendorPortletXML vendorPortletXML = getVendorPortletXMLImpl(implName);
                extensionDescriptor = vendorPortletXML.processDeploy(warFile, vendorPortletXmlStream,
                                                   schemaLocation,
                                                   Boolean.parseBoolean(validate));
            } catch (Exception e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    LogRecord record = new LogRecord(Level.WARNING,
                                                     "PSPL_PCCCSPPCCD0020");
                    record.setLoggerName(logger.getName());
                    record.setParameters(new String[]{name, implName});
                    record.setThrown(e);
                    logger.log(record);
                }
                throw e;
            }
        }
        return extensionDescriptor;
    }

    private void processUnDeployVendorPortletXML(InputStream vendorPortletXmlStream,
                                                String name,
                                                String implName) throws Exception {

        // If vendor portlet xml is present continue
        if(vendorPortletXmlStream != null) {
            try {
                VendorPortletXML vendorPortletXML = getVendorPortletXMLImpl(implName);
                vendorPortletXML.processUndeploy(vendorPortletXmlStream);
            } catch (Exception e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    LogRecord record = new LogRecord(Level.WARNING,
                                                     "PSPL_PCCCSPPCCD0020");
                    record.setLoggerName(logger.getName());
                    record.setParameters(new String[]{name, implName});
                    record.setThrown(e);
                    logger.log(record);
                }
                throw e;
            }
        }
    }

    private Object loadVendorPortletXMLImpl(ServletContext context, String name, 
            String implName) throws DeploymentDescriptorException {

        InputStream vendorPortletXmlStream = null;
        Object deploymentExtensionDescriptor = null;
        try {
            vendorPortletXmlStream = getVendorPortletXmlStream(context, name);
            // If vendor portlet xml is present continue
            if(vendorPortletXmlStream != null) {
                Class portletClass = Thread.currentThread().getContextClassLoader().
                        loadClass(implName);
                VendorPortletXML vendorPortletXML = (VendorPortletXML) (portletClass.newInstance());
                deploymentExtensionDescriptor = vendorPortletXML.loadPortletExtensionDescriptor(vendorPortletXmlStream);
            }
        } catch (Exception e) {
            if (logger.isLoggable(Level.SEVERE)) {
                LogRecord record = new LogRecord(Level.SEVERE,
                                                 "PSPL_PCCCSPPCCD0025");
                record.setLoggerName(logger.getName());
                record.setParameters(new String[]{name, implName});
                record.setThrown(e);
                logger.log(record);
            }
            throw new DeploymentDescriptorException("error loading VendorPortletXML", e);
        } finally {
            if (vendorPortletXmlStream != null) {
                try {
                    vendorPortletXmlStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return deploymentExtensionDescriptor;
    }

    // get the vendor-portlet.xml as InputStream from the ServletContext

    private InputStream getVendorPortletXmlStream(ServletContext context,
                                                  String name) throws Exception {
        InputStream vendorPortletStream =
                context.getResourceAsStream("/WEB-INF/" + name);

        return vendorPortletStream;
    }

    // get the vendor-portlet.xml as InputStream from the Webapplication

    private InputStream getVendorPortletXmlStream(File warFile, String name)
            throws Exception {

        InputStream vendorPortletStream = null;
		if(warFile.isDirectory()) {
			File vendorPortletXmlFile = new File(warFile + File.separator
									+ "WEB-INF" + File.separator
									+ name);
			vendorPortletStream = new FileInputStream(vendorPortletXmlFile);
		} else {
			JarFile jar = new JarFile(warFile);
			ZipEntry vendorPortletXMLEntry = jar.getEntry(WEB_INF_PREFIX + name);
			if (vendorPortletXMLEntry != null) {
				vendorPortletStream = (InputStream) jar.getInputStream(vendorPortletXMLEntry);
			}
		}

        return vendorPortletStream;
    }

    public static void main(String args[]) {
        try {
            String portletXML = "<?xml version='1.0' encoding='UTF-8' ?><portlet-app xmlns='http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd' version='2.0'><portlet><description>HelloLiferayPortlet</description><portlet-name>HelloLiferayPortlet</portlet-name><display-name>HelloLiferayPortlet</display-name><portlet-class>com.test.HelloLiferayPortlet</portlet-class><expiration-cache>0</expiration-cache><supports>	<mime-type>text/html</mime-type><portlet-mode>VIEW</portlet-mode></supports><resource-bundle>com.test.messages</resource-bundle><portlet-info>	<title>HelloLiferayPortlet</title>	<short-title>HelloLiferayPortlet</short-title></portlet-info>	</portlet></portlet-app>";
            Properties prop = new Properties();
            prop.put("portletXML.validate", "false");
            
            /*
            String portletXMLLocation = "/tmp/portlet1.xml";
            java.io.File portletFile = new java.io.File(portletXMLLocation);
            java.io.InputStream ioStream = new java.io.FileInputStream(portletFile);
             */
            java.io.InputStream ioStream = new java.io.ByteArrayInputStream(portletXML.getBytes());
            DeploymentDescriptorReader descriptorReader = new DeploymentDescriptorReader(prop);
            long startTime = System.currentTimeMillis();
            PortletAppDescriptor appDesc = descriptorReader.loadPortletAppDescriptor("portletwebapp",
                                                                                     ioStream);
            long endTime = System.currentTimeMillis();
            System.out.println("Done in (millisec) : " + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
