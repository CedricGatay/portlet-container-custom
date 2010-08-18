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


package com.sun.portal.portletcontainer.admin.registry;

import com.sun.portal.portletcontainer.context.registry.PortletRegistryContext;
import com.sun.portal.portletcontainer.admin.PortletRegistryElement;
import com.sun.portal.portletcontainer.admin.PortletRegistryHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * AbstractPortletRegistryElement provides partial implementation of the PortletRegistryElement
 * interface. It has implementation for the methods that are common to all elements
 * like PortletApp, PortletWindow or PortletWindowPreference
 */
public abstract class AbstractPortletRegistryElement implements PortletRegistryTags, PortletRegistryElement {
    
    private String name;
    private String userName;
    private String portletName;
    private String isRemote;
    private String lang;
    private Map collectionMapTable;
    private Map collectionStringTable;
    
    private static Logger logger = Logger.getLogger(AbstractPortletRegistryElement.class.getPackage().getName(),
            "PALogMessages");

    public AbstractPortletRegistryElement() {
        isRemote = Boolean.FALSE.toString(); // By default the Portlet Window is not remote
        collectionMapTable = new HashMap();
        collectionStringTable = new HashMap();
    }
    
    public void setCollectionProperty(String key, Map values) {
        setMap(key, values);
    }
    
    public Map getCollectionProperty(String key) {
        return getMapValue(key);
    }
    
    public void setCollectionProperty(String key, List values) {
        setList(key, values);
    }
    
    public void setStringProperty(String key, String value) {
        setString(key, value);
    }
    
    public String getStringProperty(String key) {
        return getStringValue(key);
    }
    
    public String getName() {
        if(this.name == null || this.name.trim().length() == 0)
            return getPortletName();
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getPortletName() {
        return this.portletName;
    }
    
    public void setPortletName(String portletName){
        this.portletName = portletName;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public String getUserName() {
        if(this.userName == null)
            return PortletRegistryContext.USER_NAME_DEFAULT;
        return this.userName;
    }
    
    public void setUserName(String userName){
        this.userName = userName;
    }
    
    public String getRemote() {
        return this.isRemote;
    }
    
    public void setRemote(String isRemote){
        this.isRemote = isRemote;
    }

	public String getProperties() {
		String propertiesXmlString = null;
		try {
			Document document = PortletRegistryHelper.getDocumentBuilder().newDocument();
			// Create Properties tag and append it to the document
			Element propertiesTag = XMLDocumentHelper.createElement(document, PROPERTIES_TAG);
			document.appendChild(propertiesTag);
			createPropertiesTag(document, propertiesTag);
			propertiesXmlString = PortletRegistryHelper.DOMtoString(document);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "PSPL_CSPPAM0036", ex);
		}
		return propertiesXmlString;
	}

	protected void populateValues(String propertiesXmlString) {
		try {
			Document document = PortletRegistryHelper.StringToDOM(propertiesXmlString);
			Element root = PortletRegistryHelper.getRootElement(document);
			populateValuesFromPropertiesTag(root);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "PSPL_CSPPAM0036", ex);
		}
	}

    private void setList(String key, List values){
        Map m = new HashMap();
        if(values != null) {
            int size = values.size();
            for (int i = 0; i < size; i++) {
                String s = (String) values.get(i);
                m.put(s, s);
            }
        }
        setMap(key, m);
    }
    
    private void setMap(String key, Map values){
        collectionMapTable.put(key, values);
    }
    
    private void setString(String key, String value){
        collectionStringTable.put(key, value);
    }
    
    private Map getMapValue(String key){
        return (Map)collectionMapTable.get(key);
    }
    
    private String getStringValue(String key){
        return (String)collectionStringTable.get(key);
    }
    
    protected Map getMapCollectionTable() {
        return this.collectionMapTable;
    }
    
    protected Map getStringCollectionTable() {
        return this.collectionStringTable;
    }
    
    protected void populateValues(Element rootTag) {
        // Get the child element of Properties Tag and a list of Child element, which will be
        // a list of Collection/String tags
        Element propertiesTag = XMLDocumentHelper.getChildElement(rootTag, PROPERTIES_TAG);
		populateValuesFromPropertiesTag(propertiesTag);
	}

    protected void populateValuesFromPropertiesTag(Element propertiesTag) {
        List<Element> tags = XMLDocumentHelper.createElementList(propertiesTag);
        String tagName;
        for(Element tag : tags) {
            // Check the name of the tag
            tagName = tag.getTagName();
            // Get the attributes for the Tag.
            Map<String, String> attributes = XMLDocumentHelper.createAttributeTable(tag);
            String attributeName = attributes.get(NAME_KEY);
            if(tagName.equals(STRING_TAG)) {
                setStringProperty(attributeName, attributes.get(VALUE_KEY));
            } else if (tagName.equals(COLLECTION_TAG)){
                List<Element> innerTags = XMLDocumentHelper.createElementList(tag);
                Map<String, Object> mapValues = new HashMap<String, Object>();
                for(Element innerTag: innerTags) {
                    String innerTagName = innerTag.getTagName();
                    if(innerTagName.equals(STRING_TAG)){
                        // Get the attributes for String Tag.
                        Map<String, String> stringAttributes = XMLDocumentHelper.createAttributeTable(innerTag);
                        String innerName = stringAttributes.get(NAME_KEY);
                        String innerValue = stringAttributes.get(VALUE_KEY);
                        if(innerName == null) // If there is no name, use the value for key also
                            mapValues.put(innerValue, innerValue);
                        else
                            mapValues.put(innerName, innerValue);
                    } else if(innerTagName.equals(COLLECTION_TAG)){
                        // Get the attributes for the Tag.
                        Map<String, String> innerAttributes = XMLDocumentHelper.createAttributeTable(innerTag);
                        String innerName = innerAttributes.get(NAME_KEY);
                        List<Element> stringTags = XMLDocumentHelper.createElementList(innerTag);
                        List<String> listValues = new ArrayList();
                        for(Element stringTag : stringTags) {
                            // Get the attributes for String Tag.
                            Map<String, String> stringAttributes = XMLDocumentHelper.createAttributeTable(stringTag);
                            listValues.add(stringAttributes.get(VALUE_KEY));
                        }
                        mapValues.put(innerName, listValues);
                    }
                }
                setCollectionProperty(attributeName, mapValues);
            }
        }
    }
    
    
    protected void createPropertiesTag(Document document, Element propertiesTag) {
        
        // Create String Tags under Properties, with the attribute "name", whose value is the key of the map.
        // and with attribute "value" whose value is the value for the key in the map.
        Map stringTags = getStringCollectionTable();
        Set mappings = stringTags.entrySet();
        Iterator itr = mappings.iterator();
        Element stringTag;
        String stringName, value;
        while(itr.hasNext()) {
            Map.Entry me = (Map.Entry)itr.next();
            stringName = (String)me.getKey();
            value = (String)me.getValue();
            // If there is no value, continue
            if(value == null || value.trim().length() == 0)
                continue;
            // Create String tag, add attributes
            stringTag = XMLDocumentHelper.createElement(document, STRING_TAG);
            stringTag.setAttribute(NAME_KEY, stringName);
            stringTag.setAttribute(VALUE_KEY, value);
            propertiesTag.appendChild(stringTag);
        }
        
        // Create collection Tags under Properties, with the attribute "name", whose value is the key of the map.
        // The value of the Map is a HashMap.
        // 1. If the value of the HashMap is a HashMap, need to create a Collection Tag, with "name"
        //    attribute having the key of the HashMap, value ..#2
        // 2. If the value of the HashMap is a String and
        //     a. if key and value of the HashMap are same, create "String" Tag with "value" attribute
        //     b. if key and value of the HashMap are different, create "String" Tag with both "name"
        //        and "value" attribute.
        Map collectionTags = getMapCollectionTable();
        mappings = collectionTags.entrySet();
        itr = mappings.iterator();
        Element collectionTag, innerCollectionTag;
        Map values;
        String key;
        Object obj;
        while(itr.hasNext()) {
            Map.Entry me = (Map.Entry)itr.next();
            stringName = (String)me.getKey();
            values = (Map)me.getValue();
            Set keys = values.entrySet();
            Iterator keysItr = keys.iterator();
            
            // If there are no values, do not create collections tag.
            if(keysItr.hasNext()) {
                // Create Collections tag, add String child elements and append it to the document
                collectionTag = XMLDocumentHelper.createElement(document, COLLECTION_TAG);
                collectionTag.setAttribute(NAME_KEY, stringName);
                propertiesTag.appendChild(collectionTag);
                while(keysItr.hasNext()) {
                    Map.Entry entry = (Map.Entry) keysItr.next();
                    key = (String)entry.getKey();
                    obj = entry.getValue();
                    if(obj instanceof String) {
                        value = (String)obj;
                        createStringTag(document, collectionTag, key, value);
                    } else if(obj instanceof List) { // If its a List
                        List innerValuesList = (List)obj;
                        innerCollectionTag = XMLDocumentHelper.createElement(document, COLLECTION_TAG);
                        innerCollectionTag.setAttribute(NAME_KEY, key);
                        collectionTag.appendChild(innerCollectionTag);
                        int innerValuesListSize = innerValuesList.size();
                        for(int i=0; i<innerValuesListSize; i++) {
                            value = (String)innerValuesList.get(i);
                            createStringTag(document, innerCollectionTag, value, value);
                        }
                    } else if(obj instanceof Map){ // If its a Map
                        Map innerValuesMap = (Map)obj;
                        Set innerKeys = values.keySet();
                        Iterator innerkeysItr = innerKeys.iterator();
                        innerCollectionTag = XMLDocumentHelper.createElement(document, COLLECTION_TAG);
                        innerCollectionTag.setAttribute(NAME_KEY, key);
                        collectionTag.appendChild(innerCollectionTag);
                        while(innerkeysItr.hasNext()) {
                            key = (String)innerkeysItr.next();
                            value = (String)innerValuesMap.get(key);
                            createStringTag(document, innerCollectionTag, key, value);
                        }
                    }
                }
            }
        }
    }
    
    private void createStringTag(Document document, Element collectionTag, String key, String value) {
        // If key and value are same then add only value
        Element stringTag = XMLDocumentHelper.createElement(document, STRING_TAG);
        if(key.equals(value)) {
            stringTag.setAttribute(VALUE_KEY, value);
        } else {
            stringTag.setAttribute(NAME_KEY, key);
            stringTag.setAttribute(VALUE_KEY, value);
        }
        collectionTag.appendChild(stringTag);
        
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractPortletRegistryElement other = (AbstractPortletRegistryElement) obj;
        if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
            return false;
        }
        if (this.portletName != other.portletName &&
				(this.portletName == null || !this.portletName.equals(other.portletName))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 17 * hash + (this.portletName != null ? this.portletName.hashCode() : 0);
        return hash;
    }
}
