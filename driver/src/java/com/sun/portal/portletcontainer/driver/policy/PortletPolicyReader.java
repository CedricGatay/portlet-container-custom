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

package com.sun.portal.portletcontainer.driver.policy;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The PortletPolicyReader reads the portlet policy file and
 * parses the elements of the XML file.
 */
public class PortletPolicyReader {
    
    // Schema location and namespace
    public static final String PORTLET_POLICY_NAMESPACE =
            "https://portlet-container.dev.java.net/xml/portlet-policy.xsd";
    
    // Create a logger for this class
    private static Logger logger = Logger.getLogger(PortletPolicyReader.class.getPackage().getName(),
            "PCDLogMessages");
    
    //global variables
    private String portletSchemaLocation;
    
    public PortletPolicyReader(String schemaLocation) {
        portletSchemaLocation = schemaLocation;
        logger.log(Level.FINE, "PSPCD_CSPPD0044", schemaLocation );
    }
    
    /**
     * Reads the portlet policy descriptor's xml file, and returns the root
     * element.
     * <P>
     * In the case of unable to read the descriptor, an error is logged.
     * <P>
     * @param portletStream The input stream of the descriptor's xml file
     * @return the root Element of the document, null if the resource does not
     * exists.
     */
    private Element readPortletPolicyDescriptor(InputStream inputStream, String schemaLocation)
            throws PortletPolicyException {
        Element element = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(Boolean.TRUE);
        try {
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document document = docBuilder.parse(inputStream);
            element = document.getDocumentElement();
        } catch (ParserConfigurationException pce){
            logger.log(Level.SEVERE, "PSPCD_CSPPD0046", pce);
            throw new PortletPolicyException("error reading stream", pce );
        } catch (SAXException saxe){
            logger.log(Level.SEVERE, "PSPCD_CSPPD0046", saxe);
            throw new PortletPolicyException("error reading stream", saxe );
        } catch (IOException e) {
            logger.log(Level.SEVERE, "PSPCD_CSPPD0046", e);
            throw new PortletPolicyException("error reading stream", e );
        }
        logger.log(Level.FINE, "PSPCD_CSPPD0047", element.getTagName());
        return element;
    }
    
    /**
     * Loads the portlet policy descriptor into portlet policy structure.
     * <P>
     * 
     * @return PortletPolicyDescriptor object, null if PortletPolicyDescriptor
     * cannot be read or cannot be instantiated.
     * @param inputStream portlet-policy.xml contents as a stream
     * @throws com.sun.portal.portletcontainer.driver.impl.PortletPolicyException if not able to read the xml file
     */
    public PortletPolicyDescriptor loadPortletPolicyDescriptor(InputStream inputStream)
    throws PortletPolicyException {
        
        PortletPolicyDescriptor portletPolicyDescriptor = null;
        if ( inputStream != null ) {
            String schemaLocation = PORTLET_POLICY_NAMESPACE;
            Element root = readPortletPolicyDescriptor(inputStream, schemaLocation);
            if ( root != null ) {
                portletPolicyDescriptor = new PortletPolicyDescriptor();
                portletPolicyDescriptor.load(root, PORTLET_POLICY_NAMESPACE);
            }
        }
        
        return portletPolicyDescriptor;
    }
    
    public static void main(String args[]){
        try{
            String portletXMLLocation = "/tmp/portlet-policy.xml";
            java.io.File portletFile = new java.io.File(portletXMLLocation);
            java.io.InputStream ioStream = new java.io.FileInputStream(portletFile);
            PortletPolicyReader descriptorReader = new PortletPolicyReader(null);
            PortletPolicyDescriptor portletPolicyDescriptor = descriptorReader.loadPortletPolicyDescriptor(ioStream);
            System.out.println("PortletPolicyDescriptor : " + portletPolicyDescriptor);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
