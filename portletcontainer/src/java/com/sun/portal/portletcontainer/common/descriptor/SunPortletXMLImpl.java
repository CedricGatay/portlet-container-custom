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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * SunPortletXMLImpl provides a concrete implementation of VendorPortletXML
 * interface and is responsible for processing sun-portlet.xml
 * 
 */
public class SunPortletXMLImpl implements VendorPortletXML {

    private String version;

    // Create a logger for this class

    private static Logger logger = ContainerLogger.getLogger(SunPortletXMLImpl.class,
                                                             "PCCLogMessages");
    public static final String SUN_PORTAL_NAMESPACE =
            "http://www.sun.com/software/xml/ns/portal_server";
    private static String SUN_PORTLET_SCHEMA_FILE = "sun-portlet.xsd";
    private static Schema schema;

    public Object processDeploy(File warFile, InputStream vendorPortletXMLStream,
                              String schemaLocation, boolean validate) throws Exception {
        return loadPortletExtensionDescriptor(vendorPortletXMLStream, schemaLocation, validate);
    }

    public void processUndeploy(InputStream vendorPortletXMLStream)
            throws Exception {
        //do nothing
    }

    public Object loadPortletExtensionDescriptor(InputStream vendorPortletXMLStream)
            throws Exception {
        return loadPortletExtensionDescriptor(vendorPortletXMLStream, null, false);
    }
    
    public Object loadPortletExtensionDescriptor(InputStream vendorPortletXMLStream,
                              String schemaLocation, boolean validate) throws Exception {
        DeploymentExtensionDescriptor extensionDescriptor = null;

        if (vendorPortletXMLStream != null) {
            Element root = readPortletExtensionDescriptor(vendorPortletXMLStream,
                                                          schemaLocation, validate);
            if (root != null) {
                extensionDescriptor = new DeploymentExtensionDescriptor();
                extensionDescriptor.load(root, SUN_PORTAL_NAMESPACE);
            }
        }
        return extensionDescriptor;
    }

    public Element readPortletExtensionDescriptor(InputStream vendorPortletXMLStream,
                                                  String schemaLocation,
                                                  boolean validate) throws DeploymentDescriptorException {
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "PSPL_PCCCSPPCCD0023",
                       Boolean.toString(validate));
        }

        Element element = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // parse an XML document into a DOM tree
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document document = docBuilder.parse(vendorPortletXMLStream);
            element = document.getDocumentElement();
            if (element != null) {
                //load portlet version
                if (element.hasAttribute(PortletDescriptorConstants.VERSION)) {
                    this.version = element.getAttribute(PortletDescriptorConstants.VERSION);
                }
            }

            // validating the schema
            if (validate) {
                // validate the DOM tree
                Schema sunSchema = null;
                try {
                    sunSchema = getSunPortletSchema(schemaLocation,
                                                    SUN_PORTLET_SCHEMA_FILE);
                } catch (SAXException saxe) {
                    logger.log(Level.WARNING, "PSPL_PCCCSPPCCD0024", saxe);
                }
                if (sunSchema != null) {
                    // Create a Validator object, which can be used to validate
                    // an instance document.
                    Validator validator = sunSchema.newValidator();

                    // Validate the DOM tree.
                    validator.validate(new DOMSource(document));
                }
            }
        } catch (ParserConfigurationException pce) {
            logger.log(Level.SEVERE, "PSPL_PCCCSPPCCD0003", pce);
            throw new DeploymentDescriptorException("error reading stream", pce);
        } catch (SAXException saxe) {
            logger.log(Level.SEVERE, "PSPL_PCCCSPPCCD0003", saxe);
            throw new DeploymentDescriptorException("error reading stream", saxe);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "PSPL_PCCCSPPCCD0003", e);
            throw new DeploymentDescriptorException("error reading stream", e);
        }
        return element;
    }

    /**
     * Returns the Validator for the Sun Portlet XML(sun-portlet.xml) Schema
     * 
     * @param schemaLocation location of the schema file
     * @param schemaFilename name of the schema file
     * 
     * @return the Validator for the Sun Portlet XML(sun-portlet.xml) Schema
     * 
     * @throws org.xml.sax.SAXException
     */
    public static Schema getSunPortletSchema(String schemaLocation,
                                             String schemaFilename)
            throws SAXException {
        if (schema == null) {
            InputStream sunPortletSchemaStream = null;
            try {
                sunPortletSchemaStream = new FileInputStream(schemaLocation + File.separator + schemaFilename);

                // create a SchemaFactory capable of understanding WXS schemas
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                // Set the resource resolver to redirect the references to external resources
                factory.setResourceResolver(new PortletLSResourceResolver());

                // load a WXS schema, represented by a Schema instance
                Source schemaFile = new StreamSource(sunPortletSchemaStream);
                schema = factory.newSchema(schemaFile);
            } catch (FileNotFoundException ex) {
                throw new SAXException(ex);
            } finally {
                try {
                    sunPortletSchemaStream.close();
                } catch (IOException ex) {
                //ignored
                }
            }
        }
        return schema;
    }
}
