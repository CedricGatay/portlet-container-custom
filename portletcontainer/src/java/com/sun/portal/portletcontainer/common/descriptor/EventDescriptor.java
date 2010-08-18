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

import com.sun.portal.container.service.EventHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Java content class for event-definitionType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/C:/Sun/jwsdp-1.6/jaxb/bin/portlet-app_2_0.xsd line 351)
 * <p>
 * <pre>
 * &lt;complexType name="event-definitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="alias" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="value-type" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}fully-qualified-classType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * The event-definitionType is used to declare events the portlet can either
 * receive or emit.
 * The name must be unique and must be the one the
 * portlet is using in its code for referencing this event.
 * Used in: portlet-app
 */
public class EventDescriptor {
    
    private String id;
    private EventHolder eventHolder;
        
    /**
     * Loads <event-definition> tag
	 * 
     * @param eventDefinitionElement 
	 * @param namespaceURI
	 * @param defaultNameSpace 
     */
    public void load( Element eventDefinitionElement, String namespaceURI, String defaultNameSpace){
        
        //load descriptions
        Map descriptionMap = PortletXMLDocumentHelper.getDescriptionMap(eventDefinitionElement);
        
        // Since it can be either qname or name, first load qname, if qname is not present
        // load name and create qname with defaultnamespace
        QName qname = PortletXMLDocumentHelper.generateQName(PortletXMLDocumentHelper.getChildElement(eventDefinitionElement, PortletDescriptorConstants.QNAME), defaultNameSpace);
        if(qname == null){
            String name = PortletXMLDocumentHelper.getChildTextTrim(eventDefinitionElement, PortletDescriptorConstants.NAME);
            if(name != null){
                qname = new QName(defaultNameSpace, name);
            }
        }
        
        //load alias
        List<QName> aliases = null;
        NodeList aliasElements = eventDefinitionElement.getElementsByTagName(PortletDescriptorConstants.ALIAS);
        if(aliasElements.getLength()>0){
            aliases = new ArrayList<QName>(aliasElements.getLength());
            for(int i=0; i<aliasElements.getLength(); i++){
                Element alias = (Element)aliasElements.item(i);
                aliases.add(PortletXMLDocumentHelper.generateQName(alias, defaultNameSpace));
            }
        }
        
        //load value-type
        String valueType = PortletXMLDocumentHelper.getChildTextTrim(eventDefinitionElement, PortletDescriptorConstants.VALUE_TYPE);
        
        eventHolder = new EventHolder(descriptionMap, qname, aliases, valueType);
    }
    
    /**
     * Returns the EventHolder associated with the descriptor
     *
     * @return the EventHolder associated with the descriptor
     */
    public EventHolder getEventHolder() {
        return this.eventHolder;
    }
    
    /**
     * Gets the value of the id property.
     *
     * @return String
     */    
    public String getId(){
        return id;
    }
    
    /**
     * Gets the list of all descriptions associated with all languages
     *
     * @return  {@link java.lang.String}
     */
    public List<String> getDescriptions(){
        return new ArrayList<String>(getDescriptionMap().values());
    }
    
    /**
     * Get the value of description property for default lang
     *
     * @return String
     */    
    public String getDescription(){
        return PortletXMLDocumentHelper.getDescription(getDescriptionMap());
    }
    
    /**
     * Get the value of description property for a 
     * specific xml:lng
     *
     * @param lang 
     * @return String 
     */    
    public String getDescription(String lang){
        return getDescriptionMap().get(lang);
    }
    
    /**
     * Returns descriptions in a Map.
     * <P>
     * @return <code>Map</code> of lang/description  pairs of the
     * event descriptions. Empty <code>Map</code> will be returned
     * if not defined.
     */
    public Map<String, String> getDescriptionMap() {
        return eventHolder.getDescriptionMap();
    }

    /**
     * Gets the value of the valueType property.
     * 
     * 
     * @return {@link java.lang.String}
     */
    public String getValueType(){
        return eventHolder.getValueType();
    }
    
    /**
     * Gets the value of the qname or name property.
     *
     * @return {@link javax.xml.namespace.QName}
     */
    public QName getQName(){
        return eventHolder.getQName();
    }
    
    /**
     * Gets the value of the alias property.
     *
     * @return List
     */    
    public List<QName> getAliases(){
        return eventHolder.getAliases();
    }
}
