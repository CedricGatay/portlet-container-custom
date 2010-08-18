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

import java.util.Set;
import org.w3c.dom.Element;

/**
 * Java content class for filter-mappingType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object.
 * <p>
 * <pre>
 * &lt;complexType name="filter-mappingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filter-name" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}filter-nameType"/>
 *         &lt;element name="portlet-name" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}portlet-nameType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public class FilterMappingDescriptor {
         
    private String filterName;
    private Set portletNames;
    

    
    /**
     * loads <filter-mapping> tag of portlet app
     * @param filterMappingElement 
     * @param namespaceURI 
     */
    public void load(Element filterMappingElement, String namespaceURI){
        //----------------------------------
        //loading of mandatory child elements
        //----------------------------------
        filterName = PortletXMLDocumentHelper.getChildTextTrim(filterMappingElement, PortletDescriptorConstants.FILTER_NAME);
        portletNames = PortletXMLDocumentHelper.getChildElementsTextSet(filterMappingElement, PortletDescriptorConstants.PORTLET_NAME);        
    }
    
    /**
     * Gets the filter-name property 
     * @return String
     */    
    public String getFilterName(){
        return filterName;
    }
    
    /**
     * Gets the portlet-name property
     * @return 
     */    
    public Set getPortletNames(){
        return portletNames;
    }
        
}
