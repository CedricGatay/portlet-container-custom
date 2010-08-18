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
 * A custom window state that one or more portlets in this
 * portlet application supports.
 * Used in: portlet-app
 *
 * Java content class for custom-window-stateType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object.
 * <p>
 * <pre>
 * &lt;complexType name="custom-window-stateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="window-state" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}window-stateType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 */

public class CustomWindowStateDecriptor {
    
    private String id;
    private String windowState;
    private Map<String, String> descriptionMap;
    
    /**
     *
     *
     */
    public void load(Element customWindowStateElement, String namespaceURI){
        //load id
        id = PortletXMLDocumentHelper.getId(customWindowStateElement);
        
        //load window state
        windowState = PortletXMLDocumentHelper.getChildTextTrim(customWindowStateElement, PortletDescriptorConstants.WINDOW_STATE);
        
        //load descriptions
        descriptionMap =
                PortletXMLDocumentHelper.getDescriptionMap(customWindowStateElement);
    }
    
    /**
     * Gets the value of the windowState property.
     * @return String
     */
    public String getWindowState(){
        return windowState;
    }
    
    /**
     * Gets the value of the Description property.
     * @return List 
     */
    public List<String> getDescriptions(){
        return new ArrayList<String>(descriptionMap.values());
    }
    
    /**
     * Returns default description, if any
     */
    public String getDescription(){
        return PortletXMLDocumentHelper.getDescription(descriptionMap);
    }
    
    /**
     * Returns description associated with particular xml:lang
     */
    public String getDescription(String lang){
        return descriptionMap.get(lang);
    }
    
    /**
     * Returns descriptions in a Map.
     * <P>
     * @return <code>Map</code> of lang/description  pairs of the
     * custom-window-state descriptions. Empty <code>Map</code> will be returned
     * if not defined.
     */
    public Map getDescriptionMap() {
        return descriptionMap;
    }
    
    /**
     * Gets the value of the id property.
     *
     * @return  {@link java.lang.String}
     */
    public String getId(){
        return id;
    }
    
}
