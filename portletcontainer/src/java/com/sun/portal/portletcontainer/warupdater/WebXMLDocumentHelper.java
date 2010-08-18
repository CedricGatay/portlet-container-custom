package com.sun.portal.portletcontainer.warupdater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * WebXMLDocumentHelper is a Helper class to create DOM Elements and Attributes.
 */
public class WebXMLDocumentHelper {
    
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
    
    public static List getChildElements(Element element, String tagName) {
        List elements = getElementList(element);
        int numElements  = elements.size();
        List childElements = new ArrayList();
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
    
    protected static List<Node> getElementList(Element element) {
        List<Node> elementList = new ArrayList<Node>();
        
        NodeList childNodes = element.getChildNodes();
        int numChildren  = childNodes.getLength();
        
        for (int i = 0; i < numChildren; i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            elementList.add(childNode);
        }
        
        return elementList;
    }
    
    public static String getChildTextTrim(Element element, String tagName) {
        Element childElement = WebXMLDocumentHelper.getChildElement(element, tagName);
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
    
    protected static Map getAttributeTable(Element element) {
        Map attributeTable = new HashMap();
        NamedNodeMap attrs = element.getAttributes();
        if (attrs != null) {
            int numAttrs = attrs.getLength();
            for (int i = 0; i < numAttrs; i++) {
                Node na = attrs.item(i);
                if (na.getNodeType() != Node.ATTRIBUTE_NODE) {
                    continue;
                }
                Attr a = (Attr)na;
                attributeTable.put(a.getName(), a.getValue());
            }
        }
        return attributeTable;
    }
    
    protected static Element createElement(Document d, String tagName) {
        Element e = d.createElement(tagName);
        return e;
    }
    
    /**
     * Add the content to the root at the end.
     */
    public static void addContent(Element root, List newchildren) {
        Iterator it = newchildren.iterator();
        while (it.hasNext()){
            root.appendChild((Node)it.next());
        }
    }
    
    public static void addContent(Element root, Element child) {
        root.appendChild(child);
    }
    
    public static void addContent(Element root, Document doc, String text) {
        Text textNode = doc.createTextNode(text);
        root.appendChild(textNode);
    }
    
    /**
     * Insert the content to the root at the start.
     */
    public static void insertContent(Element root, List newchildren) {
        Node firstChild = root.getFirstChild();
        Iterator it = newchildren.iterator();
        while (it.hasNext()){
            root.insertBefore((Node)it.next(), firstChild);
        }
    }
}




