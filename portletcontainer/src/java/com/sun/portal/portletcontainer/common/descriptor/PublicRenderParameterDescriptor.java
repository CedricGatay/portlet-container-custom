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

import com.sun.portal.container.service.PublicRenderParameterHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The public-render-parameters defines a render parameter that is allowed to be public
 * with other portlets.
 * The identifier must be used for referencing this public render parameter in the portlet code.
 * Used in: portlet-app
 *
 * Java content class for public-render-parameterType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/C:/Sun/jwsdp-1.6/jaxb/bin/portlet-app_2_0.xsd line 541)
 * <p>
 * <pre>
 * &lt;complexType name="public-render-parameterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="identifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 */
public class PublicRenderParameterDescriptor {
    
    private String id;
    private PublicRenderParameterHolder publicRenderParameterHolder;
    
    public PublicRenderParameterDescriptor() {
    }
    
    public void load( Element publicRenderParameterElement, String namespaceURI, String defaultNameSpace){
        //load id
        id = PortletXMLDocumentHelper.getId(publicRenderParameterElement);
        
        //load identifier
        String identifier = PortletXMLDocumentHelper.getChildTextTrim(publicRenderParameterElement, PortletDescriptorConstants.IDENTIFIER);
        
        // Since it can be either qname or name, first load qname, if qname is not present
        // load name and create qname with defaultnamespace
        QName qname = PortletXMLDocumentHelper.generateQName(PortletXMLDocumentHelper.getChildElement(publicRenderParameterElement, PortletDescriptorConstants.QNAME), defaultNameSpace);
        if(qname == null){
            String name = PortletXMLDocumentHelper.getChildTextTrim(publicRenderParameterElement, PortletDescriptorConstants.NAME);
            if(name != null){
                qname = new QName(defaultNameSpace, name);
            }
        }
        
        //load alias
        NodeList aliasElements = publicRenderParameterElement.getElementsByTagName(PortletDescriptorConstants.ALIAS);
        List<QName> aliases = null;
        if(aliasElements.getLength()>0){
            aliases = new ArrayList<QName>(aliasElements.getLength());
            for(int i=0; i<aliasElements.getLength(); i++){
                Element alias = (Element)aliasElements.item(i);
                aliases.add(PortletXMLDocumentHelper.generateQName(alias, defaultNameSpace));
            }
        }
        
        //load descriptions
        Map<String, String> descriptionMap = PortletXMLDocumentHelper.getDescriptionMap(publicRenderParameterElement);
        
        publicRenderParameterHolder = new PublicRenderParameterHolder(identifier, descriptionMap, qname, aliases);
    }
    
    /**
     * Returns the PublicRenderParameterHolder associated with the descriptor
     *
     * @return the PublicRenderParameterHolder associated with the descriptor
     */
    public PublicRenderParameterHolder getPublicRenderParameterHolder() {
        return this.publicRenderParameterHolder;
    }
    
    /**
     * Gets the value of the identifier property.
     *
     * @return {@link java.lang.String}
     */
    public String getIdentifier(){
        return publicRenderParameterHolder.getIdentifier();
    }
    
    /**
     * Gets the value of the id property.
     *
     * @return {@link java.lang.String}
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
     * Gets the value of default description, if any
     *
     * @return  {@link java.lang.String}
     */
    public String getDescription(){
        return PortletXMLDocumentHelper.getDescription(getDescriptionMap());
    }
    
    /**
     * Gets the value of description associated with particular xml:lang
     *
     * @return  {@link java.lang.String}
     */
    public String getDescription(String lang){
        return getDescriptionMap().get(lang);
    }
    
    /**
     * Returns descriptions in a Map.
     * <P>
     * @return <code>Map</code> of lang/description  pairs of the
     * public-render-parameter descriptions. Empty <code>Map</code> will be returned
     * if not defined.
     */
    public Map<String, String> getDescriptionMap() {
        return publicRenderParameterHolder.getDescriptionMap();
    }
    
    /**
     * Gets the value of the qname or name property.
     *
     * @return {@link javax.xml.namespace.QName}
     */
    public QName getQName(){
        return publicRenderParameterHolder.getQName();
    }
    
    /**
     * Gets the value of the alias property.
     *
     * @return List
     */
    public List<QName> getAliases(){
        return publicRenderParameterHolder.getAliases();
    }
}
