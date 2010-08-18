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
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * The security constraint descriptor is the descriptor for defining
 * portlet security information.
 * <P>
 */
public class SecurityConstraintDescriptor {
    
    
    // Global Variables
    private List<String> displayNames;
    private Map<String, String> displayNameMap;
    private List<String> portletNames;
    private List<String> constraintDescs;
    private String transportGuaranteeType = PortletDescriptorConstants.NONE;
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(SecurityConstraintDescriptor.class, "PCCLogMessages");
    
    /**
     * Loads the security constraint descriptor
     */
    public void load( Element element, String namespaceURI ) throws
            DeploymentDescriptorException {
        
        NodeList displayNameElements =
                element.getElementsByTagName(PortletDescriptorConstants.DISPLAY_NAME);
        if(displayNameElements.getLength()>0){
            String displayName;
            displayNames = new ArrayList<String>();
            displayNameMap = new HashMap<String, String>();
            for (int i = 0; i < displayNameElements.getLength(); i++) {
                Element displayNameElement = (Element)displayNameElements.item(i);
                displayName = PortletXMLDocumentHelper.getTextTrim(displayNameElement);
                displayNames.add( displayName );
                
                Map displayNameAttributes = PortletXMLDocumentHelper.getAttributeTable(displayNameElement);
                Iterator dnit = displayNameAttributes.entrySet().iterator();
                String lang;
                if(dnit.hasNext()) {
                    while(dnit.hasNext()) {
                        Map.Entry entry = (Map.Entry) dnit.next();
                        lang = (String)entry.getKey();
                        if (lang.equals(PortletDescriptorConstants.XML_LANG_ATTR)) {
                            displayNameMap.put((String)entry.getValue(), (String)displayName);
                        }
                    }
                } else {
                    lang = Locale.getDefault().toString().replace('_', '-');
                    displayNameMap.put(lang, displayName);
                }
            }
        }
        
        NodeList portletCollectionElements =
                element.getElementsByTagName( PortletDescriptorConstants.PORTLET_COLLECTION);
        if(portletCollectionElements.getLength()>0){
            portletNames = new ArrayList<String>(portletCollectionElements.getLength());
            for (int i = 0; i < portletCollectionElements.getLength(); i++) {
                Element portletCollectionElement = (Element)portletCollectionElements.item(i);
                NodeList portletNameElements =
                        portletCollectionElement.getElementsByTagName(PortletDescriptorConstants.PORTLET_NAME);
                for (int j = 0; j < portletNameElements.getLength(); j++) {
                    Element portletNameElement = (Element)portletNameElements.item(j);
                    String portletName = PortletXMLDocumentHelper.getTextTrim(portletNameElement);
                    portletNames.add( portletName );
                }
            }
        }
        
        Element constraintElement =
                PortletXMLDocumentHelper.getChildElement(element, PortletDescriptorConstants.USER_DATA_CONSTRAINT);
        if (constraintElement == null ) {
            logger.warning("PSPL_PCCCSPPCCD0013");
        } else {
            constraintDescs = new ArrayList<String>();
            transportGuaranteeType = PortletXMLDocumentHelper.getChildTextTrim(constraintElement, PortletDescriptorConstants.TRANSPORT_GUARANTEE);
            NodeList descriptionElements =
                    element.getElementsByTagName( PortletDescriptorConstants.DESCRIPTION);
            for (int i = 0; i < descriptionElements.getLength(); i++) {
                Element descriptionElement = (Element)descriptionElements.item(i);
                constraintDescs.add(PortletXMLDocumentHelper.getTextTrim(descriptionElement));
            }
            
        }
        
    }
    
    public List<String> getConstrainedPortlets() {
        if(portletNames == null){
            return Collections.emptyList();
        }
        return portletNames;
    }
    
    public String getTransportGuaranteeType() {
        return transportGuaranteeType;
    }
    
    /**
     * Return the values of display-name property
     * @return List
     */
    public List getDisplayNames() {
        if(displayNames == null){
            return Collections.emptyList();
        }
        return displayNames;
    }
    
    /**
     * Returns display names in a Map
     * <P>
     * @return <code>Map</code> of lang/display name pairs of the
     * display names. Empty <code>Map</code> will be returned
     * if not defined.
     */
    public Map getDisplayNameMap() {
        if(displayNameMap == null){
            return Collections.emptyMap();
        }
        return displayNameMap;
    }
    
    public List getConstraintDescriptions() {
        if(constraintDescs == null){
            return Collections.emptyList();
        }
        return constraintDescs;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer( "PortletDescriptor ");
        
        sb.append( " display names [" );
        Iterator iterator = displayNames.iterator();
        while ( iterator.hasNext() ) {
            sb.append( (String)iterator.next() );
        }
        sb.append( " ]" );
        
        sb.append( " constrained portlet names [" );
        Iterator iterator1 = portletNames.iterator();
        while ( iterator1.hasNext() ) {
            sb.append( (String)iterator1.next() );
        }
        sb.append( " ]" );
        
        if (transportGuaranteeType != null) {
            sb.append( " transport guarantee type [" );
            sb.append( transportGuaranteeType );
            sb.append( " ]" );
            
            sb.append( " constrained portlet names [" );
            Iterator iterator2 = constraintDescs.iterator();
            while ( iterator2.hasNext() ) {
                sb.append( (String)iterator2.next() );
            }
            sb.append( " ]" );
        }
        return sb.toString();
    }
    
    
}
