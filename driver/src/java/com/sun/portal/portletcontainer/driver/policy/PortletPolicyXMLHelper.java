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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * PortletPolicyXMLHelper is a Helper class to get DOM Elements.
 *
 */
public class PortletPolicyXMLHelper {
    
    public static Element getChildElement(Element element, String tagName) {
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
    
    
    public static String getChildTextTrim(Element element, String tagName) {
        Element childElement = getChildElement(element, tagName);
        if(childElement != null) {
            return getTextTrim(childElement);
        }
        return null;
    }
    
    public static String getTextTrim(Element element) {
        Node node = element.getChildNodes().item(0);
        if(node != null && node.getNodeType() == Node.TEXT_NODE)
            return node.getNodeValue().trim();
        return "";
    }
    
    public static List<Element> getChildElements(Element element, String tagName) {
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
    
    private static List<Element> getElementList(Element element) {
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
}
