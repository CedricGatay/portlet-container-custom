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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * A custom portlet mode that one or more portlets in
 * this portlet application supports.
 * If the portal does not need to provide some management functionality
 * for this portlet mode, the portal-managed element needs to be set
 * to "false", otherwise to "true". Default is "true".
 * If the portlet provides a decoration-name for this portlet mode the portal is
 * encouraged to use the provided name as key under which in the portlet
 * resource bundle the localized value of the decoration name is available.
 * If the portlet does not provide a resource bundle the portal should use
 * the name provided with the decoration-name tag.
 * decorations.
 * Used in: portlet-app
 * 
 * Java content class for custom-portlet-modeType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object.
 * <p>
 * <pre>
 * &lt;complexType name="custom-portlet-modeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="portlet-mode" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}portlet-modeType"/>
 *         &lt;element name="portal-managed" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}portal-managedType" minOccurs="0"/>
 *         &lt;element name="decoration-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */

public class CustomPortletModeDecriptor {
        
    private String id;
    private boolean portalManaged;
    private String portletMode;
    private Map<String, String> descriptionMap;
    
    /**
     * default constructor
     */
    public CustomPortletModeDecriptor() {
    }

    /**
     * loads <custom-portlet-mode> tag of portlet app
     * @param customPortletModeElement 
     * @param namespaceURI 
     */    
    public void load(Element customPortletModeElement, String namespaceURI){
        //load id
        id = PortletXMLDocumentHelper.getId(customPortletModeElement);

        //load portletMode property
        portletMode = PortletXMLDocumentHelper.getChildTextTrim(customPortletModeElement, PortletDescriptorConstants.PORTLET_MODE);
        
        //load portalManaged property
        portalManaged = Boolean.getBoolean(PortletXMLDocumentHelper.getChildTextTrim(customPortletModeElement, PortletDescriptorConstants.PORTAL_MANAGED));
        
        //load description properties
        descriptionMap = 
                PortletXMLDocumentHelper.getDescriptionMap(customPortletModeElement);        
    }
    
    /**
     * Gets a list of all descriptions
     *
     * @return List containing description text
     */
    public List<String> getDescriptions(){
        return new ArrayList<String>(descriptionMap.values());
    }

    /**
     * Gets the value of default description 
     *
     * @return  {@link java.lang.String}
     */
    public String getDescription(){
        return PortletXMLDocumentHelper.getDescription(descriptionMap);        
    }
    
    /**
     * Gets the value of description associated with particular xml:lang
     *
     * @return  {@link java.lang.String}
     */
    public String getDescription(String lang){
        return descriptionMap.get(lang);
    }    
    
    /**
     * Returns descriptions in a Map.
     * <P>
     * @return <code>Map</code> of lang/description  pairs of the
     * custom-portlet-mode descriptions. Empty <code>Map</code> will be returned
     * if not defined.
     */
    public Map getDescriptionMap() {
        return descriptionMap;
    }

    
    /**
     * Gets the value of the portalManaged property.
     * 
     * @return  boolean
     */
    public boolean getPortalManaged(){
        return portalManaged;
    }


    /**
     * Gets the value of the portletMode property.
     * 
     * @return {@link java.lang.String}
     */
    public String getPortletMode(){
        return portletMode;
    }

 
    /**
     * Gets the value of the id property.
     * 
     * @return {@link java.lang.String}
     */
    public String getId(){
        return id;
    }
}
