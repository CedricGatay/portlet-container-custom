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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Java content class for filterType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object.
 * <p>
 * <pre>
 * &lt;complexType name="filterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="display-name" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}display-nameType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="filter-name" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}filter-nameType"/>
 *         &lt;element name="filter-class" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}fully-qualified-classType"/>
 *         &lt;element name="lifecycle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="init-param" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}init-paramType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public class FilterDescriptor {

    // Global variables
    private String filterName;
    private String filterClass;
    private Set<String> lifecycle;
    private Map<String, String> descriptionMap;
    private List<InitParamDescriptor> initParamDescriptors;
    
   /**
     * Loads the <filter> tag into java objects.
     * <P>
     * 
     * @param filterElement 
     * @param namespaceURI 
     */    
    public void load( Element filterElement, String namespaceURI ) {

        //----------------------------------
        //loading of mandatory child elements
        //----------------------------------
        filterName = PortletXMLDocumentHelper.getChildTextTrim(filterElement, PortletDescriptorConstants.FILTER_NAME);
        filterClass = PortletXMLDocumentHelper.getChildTextTrim(filterElement, PortletDescriptorConstants.FILTER_CLASS);
        lifecycle = PortletXMLDocumentHelper.getChildElementsTextSet(filterElement, PortletDescriptorConstants.LIFECYCLE);
        
        //----------------------------------
        //loading of optional child elements
        //----------------------------------
        
        //Load description elements
        //InitParamDescriptor.java has old way to load description elements
        //This class is using new utility method
        
        descriptionMap = PortletXMLDocumentHelper.getDescriptionMap(filterElement);

        // loads init param descriptor
        NodeList initParamElements = filterElement.getElementsByTagName(PortletDescriptorConstants.INIT_PARAM);
        if(initParamElements.getLength()>0){
            initParamDescriptors = new ArrayList<InitParamDescriptor>(initParamElements.getLength());
            for (int i = 0; i < initParamElements.getLength(); i++) {
                Element initParamElement = (Element)initParamElements.item(i);
                InitParamDescriptor initParamDescriptor = new InitParamDescriptor();
                initParamDescriptor.load( initParamElement, namespaceURI );
                initParamDescriptors.add( initParamDescriptor );
            }//end of for
        }//end of if
        
    }
    
    /**
     * Gets the value of the filter-name property
     * @return String
     */    
    public String getFilterName(){
        return filterName;
    }
    
    /**
     * Gets the value of the filter-name property
     * @return String
     */    
    public String getFilterClass(){
        return filterClass;
    }
    
    /**
     * Gets the value of the lifecycle property
     * @return String
     */    
     public Set<String> getFilterLifecycles(){
        return lifecycle;
    }   
    
    /**
     * Gets the value of the description property
     * @return List
     */     
    public List<String> getDescriptions(){
        return new ArrayList<String>(descriptionMap.values());
    }
    
    /**
     * Gets the default value of the description property
     * @return String
     */    
    public String getDescription(){
        return PortletXMLDocumentHelper.getDescription(descriptionMap);
    }
    
    /**
     * Gets the language specific value of the description property 
     * @param lang 
     * @return 
     */    
    public String getDescription(String lang){
        return descriptionMap.get(lang);
    }
    
   /**
     * Returns the init param descriptors.
     * <P>
     * @return <code>List</code> of <code>InitParamDescriptor</code>s.
     */
    public List<InitParamDescriptor> getInitParamDescriptors() {
        if(initParamDescriptors == null){
            return Collections.emptyList();
        }
        return initParamDescriptors;
    }    
    
}
