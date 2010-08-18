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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * PortletXMLDocumentHelper is a Helper class to create DOM Elements and Attributes.
 */
public class PortletXMLDocumentHelper {
    
    protected static Element getChildElement(Element element, String tagName) {
        NodeList childNodes = element.getChildNodes();
        int numChildren  = childNodes.getLength();
        
        for (int i = 0; i < numChildren; i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element childElement = (Element)childNode;
            
            if (tagName != null) {
                String childTagName = childElement.getTagName();
                if (!childTagName.equals(tagName)) {
                    continue;
                }
            }
            return childElement;
        }
        return null;
    }
    
    protected static List<Element> getChildElements(Element element, String tagName) {
        List<Element> elements = getElementList(element);
        int numElements  = elements.size();
        List<Element> childElements = new ArrayList<Element>(numElements);
        for (int i = 0; i < numElements; i++) {
            Element childElement = (Element)elements.get(i);
            if (tagName != null) {
                String childTagName = childElement.getTagName();
                if (!childTagName.equals(tagName)) {
                    continue;
                }
            }
            childElements.add(childElement);
        }
        return childElements;
    }
    
    /**
     * Returns a List of text content of all descendant Elements with a given tag name
     * Null is returned if there is no child element with given tag name
     *
     * @param parentElement Parent Element node
     * @param tagName child Element name
     * @return List<String> containing text content
     */
    public static List<String> getChildElementsText(Element parentElement, String tagName) {
        List<Element> childElements = getChildElements(parentElement, tagName);
        if(childElements != null && childElements.size()>0){
            List<String> textList = new ArrayList<String>(childElements.size());
            for(Element element : childElements){
                textList.add(element.getTextContent().trim());
            }
            return textList;
        }
        return Collections.emptyList();
    }
    
    /**
     * Returns a Set of text content of all descendant Elements with a given tag name
     * Null is returned if there is no child element with given tag name
     *
     * @param parentElement Parent Element node
     * @param tagName child Element name
     * @return List<String> containing text content
     */
    public static Set<String> getChildElementsTextSet(Element parentElement, String tagName) {
        List<Element> childElements = getChildElements(parentElement, tagName);
        if(childElements!=null && childElements.size()>0){
            Set<String> textList = new HashSet<String>(childElements.size());
            for(Element element : childElements){
                textList.add(element.getTextContent().trim());
            }
            return textList;
        }
        return Collections.EMPTY_SET;
    }
    
    protected static List<Element> getElementList(Element element) {
        if(element == null){
            return Collections.emptyList();
        }
        
        NodeList childNodes = element.getChildNodes();
        int numChildren  = childNodes.getLength();
        
        List<Element> elementList = new ArrayList<Element>(numChildren);
        
        for (int i = 0; i < numChildren; i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            elementList.add((Element)childNode);
        }
        
        return elementList;
    }
    
    public static String getChildTextTrim(Element element, String tagName) {
        Element childElement = PortletXMLDocumentHelper.getChildElement(element, tagName);
        if(childElement != null) {
            return getTextTrim(childElement);
        }
        return null;
    }
    
    public static String getTextTrim(Element element) {
		if(element != null) {
			Node node = element.getChildNodes().item(0);
			if(node != null && node.getNodeType() == Node.TEXT_NODE)
				return node.getNodeValue().trim();
		}
        return "";
    }
    
    protected static Map<String, String> getAttributeTable(Element element) {
        NamedNodeMap attrs = element.getAttributes();
        if(attrs==null){
            return Collections.emptyMap();
        }
        
        int numAttrs = attrs.getLength();
        Map<String, String> attributeTable = new HashMap<String, String>(numAttrs);
        for (int i = 0; i < numAttrs; i++) {
            Node na = attrs.item(i);
            if (na.getNodeType() != Node.ATTRIBUTE_NODE) {
                continue;
            }
            Attr a = (Attr)na;
            attributeTable.put(a.getName(), a.getValue());
        }
        return attributeTable;
    }
    
    protected static Element createElement(Document d, String tagName) {
        Element e = d.createElement(tagName);
        return e;
    }
    
    /**
     * This method returns a <code>Map</code> of xml:lang/description
     *
	 * @param descriptionParentElement  the Parent Element node referring to description
	 * 
	 * @return Map of xml:lang/description
	 */
    public static Map<String, String> getDescriptionMap(Element descriptionParentElement){
        Map<String, String> descriptionMap = null;
        List<Element> descriptionElements =
                PortletXMLDocumentHelper.getChildElements(descriptionParentElement,
                PortletDescriptorConstants.DESCRIPTION);
        if(descriptionElements.size() > 0){
            descriptionMap = new HashMap<String, String>(descriptionElements.size());
            for (Element description : descriptionElements) {
                String lang = "";
                if(description.hasAttribute(PortletDescriptorConstants.XML_LANG_ATTR)){
                    lang = description.getAttribute(PortletDescriptorConstants.XML_LANG_ATTR);
                }
                descriptionMap.put(lang, PortletXMLDocumentHelper.getTextTrim(description));
            }
        } else{
            descriptionMap =  Collections.emptyMap();
        }
        
        return descriptionMap;
    }
    
    /**
     * This method returns a <code>Map</code> of xml:lang/display-name
	 * 
	 * @param displayNameParentElement  the Parent Element node referring to display-name
	 * 
	 * @return Map of xml:lang/display-name
     *
     */
    public static Map<String, String> getDisplayNameMap(Element displayNameParentElement){
        Map<String, String> displayNameMap = null;
        List<Element> displayNameElements =
                PortletXMLDocumentHelper.getChildElements(displayNameParentElement,
                PortletDescriptorConstants.DISPLAY_NAME);
        if(displayNameElements.size() > 0){
            displayNameMap = new HashMap<String, String>(displayNameElements.size());
            for (Element displayName : displayNameElements) {
                String lang = "";
                if(displayName.hasAttribute(PortletDescriptorConstants.XML_LANG_ATTR)){
                    lang = displayName.getAttribute(PortletDescriptorConstants.XML_LANG_ATTR);
                }
                displayNameMap.put(lang, PortletXMLDocumentHelper.getTextTrim(displayName));
            }
        } else{
            displayNameMap =  Collections.emptyMap();
        }
        
        return displayNameMap;
    }
    
    /**
     * Returns the description for the default language.
     *
	 * @param descriptionMap map that has language as the key
	 * and description for the language as the value.
	 * 
	 * @return the description for the default language.
	 */
    public static String getDescription(Map<String, String> descriptionMap){
        String description = null;
        if(descriptionMap!=null && !descriptionMap.isEmpty()){
            if(descriptionMap.size()==1){
                //Return the only value in the description map
                description = (String)descriptionMap.values().toArray()[0];
            }else{
                //if there is any description with no la key, return it
                //if there is any description with no 'en' key, return it
                //if there is any description with no 'EN' key, return it
                //else return last description
                if(descriptionMap.containsKey("")){
                    //Return the value for which no lang key is specified
                    description = descriptionMap.get("");
                }else if(descriptionMap.containsKey(PortletDescriptorConstants.DEFAULT_XML_LANG)){
                    description = descriptionMap.get(PortletDescriptorConstants.DEFAULT_XML_LANG);
                }else if(descriptionMap.containsKey(PortletDescriptorConstants.DEFAULT_XML_LANG.toUpperCase())){
                    description = descriptionMap.get(PortletDescriptorConstants.DEFAULT_XML_LANG.toUpperCase());
                }else{
                    description = (String)descriptionMap.values().toArray()[0];
                }
            }
        }
        
        return description;
    }
    
    
    public static String getId(Element element){
        if(element.hasAttribute(PortletDescriptorConstants.ID)){
            return element.getAttribute(PortletDescriptorConstants.ID);
        }
        return null;
    }
    
    
    public static QName generateQName(Element element, String defaultEventNameSpace){
        QName qname = null;
        if(element != null){
            String textContent = getTextTrim(element);
            String prefix = getPrefixFromTextContent(textContent);
            String textContentWithoutPrefix = textContent;
            if(prefix!=null){
                int idx = textContent.indexOf(":");
                if(idx != -1){
                    textContentWithoutPrefix = textContent.substring(idx+1);
                }
            }
            
            String nsAttributeValue = element.lookupNamespaceURI(prefix);
            // If prefix is null, nsAttributeValue can be the portlet namespace
			// If that is the case, use default namespace
            if(nsAttributeValue != null && 
				!nsAttributeValue.equals(DeploymentDescriptorReader.PORTLET_NAMESPACE)){
				
                if(prefix!=null) {
                    qname = new QName(nsAttributeValue, textContentWithoutPrefix, prefix);
                } else {
                    qname = new QName(nsAttributeValue, textContentWithoutPrefix);
                }
            } else {
                qname = new QName(defaultEventNameSpace, textContentWithoutPrefix);
            }
        }
        
        
        return qname;
    }
    
    private static String getPrefixFromTextContent(String textContent){
        String prefix = null;
        if(textContent != null){
            int idx = textContent.indexOf(":");
            if(idx != -1){
                prefix = textContent.substring(0, idx);
            }
        }
        
        return prefix;
        
    }
    
    /**
     * Returns a List of text content of all descendant Elements with a given tag name
     * Null is returned if there is no child element with given tag name
     *
     * @param parentElement Parent Element node
	 * @param defaultNameSpace the default namespace
	 * @param tagName child Element name
	 * 
     * @return List<QName> containing text content
     */
    public static List<QName> getSupportedEventChildElementsQName(Element parentElement, String defaultNameSpace, String tagName) {
        List<Element> childElements = getChildElements(parentElement, tagName);
        if(childElements!=null && childElements.size()>0){
            List<QName> qnameList = new ArrayList<QName>(childElements.size());
            for(Element element : childElements){
                // Since it can be either qname or name, first load qname, if qname is not present
                // load name and create qname with defaultnamespace
                QName qname = generateQName(element, defaultNameSpace);
                if(qname == null){
                    String name = getChildTextTrim(element, PortletDescriptorConstants.NAME);
                    if(name != null){
                        qname = new QName(defaultNameSpace, name);
                    }
                }
                if(qname != null) {
                    qnameList.add(qname);
                }
            }
            return qnameList;
        }
        return Collections.EMPTY_LIST;
    }
    
    
    /**
     * Reads <container-runtime-option> elements and
     * returns a map of key and values where key is of type
     * String and values are of type String array.
     *
     * @param element Either <portlet> or <portlet-app>
     * @return Map containing runtime key and array of values
     */
    public static Map<String, String[]> getRuntimeOptions(Element element){
        Map<String, String[]> optionsMap = null;
        if(element != null){
            NodeList list = element.getElementsByTagName(PortletDescriptorConstants.CONTAINER_RUNTIME_OPTION);
            if(list.getLength() > 0){
                optionsMap = new HashMap<String, String[]>(list.getLength());
                Element option = null;
                String name = null;
                for(int i=0; i<list.getLength(); i++){
                    option = (Element)list.item(i);
                    name = getChildTextTrim(option, PortletDescriptorConstants.PARAM_NAME);
                    if(name != null) {
                        List<Element> childElements = 
                                getChildElements(option, PortletDescriptorConstants.PARAM_VALUE);
                        String[] values = null;
                        if(childElements != null){
                            values = new String[childElements.size()];
                            int j = 0;
                            for(Element childElement: childElements) {
                                values[j++] = getTextTrim(childElement);
                            }
                        }
                        optionsMap.put(name, values);
                    }
                }
            }
        }
        if(optionsMap == null) {
            optionsMap = Collections.emptyMap();
        }
        return optionsMap;
    }
}
