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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.w3c.dom.Element;

/**
 * The init param descriptor is the descriptor for a init param of
 * a portlet.
 * <P>
 */
public class InitParamDescriptor {
        
    // Global variables
    private String paramName;
    private String paramValue;
    private List<String> descriptions = new ArrayList<String>();
    private Map descriptionMap = new HashMap();
    
    /**
     * Loads the init param descriptor into memory.
     * <P>
     * @param The init param Element
     */
    public void load( Element element, String namespaceURI ) {
        paramName = PortletXMLDocumentHelper.getChildTextTrim(element, PortletDescriptorConstants.PARAM_NAME);
        paramValue = PortletXMLDocumentHelper.getChildTextTrim(element, PortletDescriptorConstants.PARAM_VALUE);
        
        
        List descriptionElements = PortletXMLDocumentHelper.getChildElements(element, PortletDescriptorConstants.DESCRIPTION);
        int numDescription = descriptionElements.size();
        String description;
        for (int i = 0; i < numDescription; i++) {
            Element descriptionElement = (Element)descriptionElements.get(i);
            description = PortletXMLDocumentHelper.getTextTrim(descriptionElement);
            descriptions.add(description);
            Map descAttributes = PortletXMLDocumentHelper.getAttributeTable(descriptionElement);
            Iterator dait = descAttributes.entrySet().iterator();
            String lang;
            while(dait.hasNext()) {
                Map.Entry entry = (Map.Entry) dait.next();
                lang = (String)entry.getKey();
                if (lang.equals(PortletDescriptorConstants.XML_LANG_ATTR)) {
                    descriptionMap.put(entry.getValue(), description);
                }
            }
        }
    }
    
    public String getParamName() {
        return paramName;
    }
    
    public String getParamValue() {
        return paramValue;
    }
    
    public String getDescription() {
        String description = null;
        if ( !descriptions.isEmpty() ) {
            description = (String)descriptions.get(0);
        }
        return description;
    }
    
    /**
     * Returns descriptions in a Map.
     * <P>
     * @return <code>Map</code> of lang/description  pairs of the
     * descriptions. Empty <code>Map</code> will be returned
     * if not defined.
     */
    public Map getDescriptionMap() {
        return descriptionMap;
    }
    
    /**
     * Returns the init param descriptions in a <code>List</code>.
     * <P>
     * @return A <code>List</code> of the <code>String</code>s. The
     * returned value could be empty list if description is not defined.
     */
    public List getDescriptions() {
        return descriptions;
    }
    
    /**
     * The toString method.
     * <P>
     * @return the <code>String</code> representation of the init param
     * descriptor.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "InitParamDescriptor [");
        
        sb.append( "param name [").append(paramName).append("] ");
        sb.append( "param value [").append(paramValue).append("] ");
        sb.append( "descriptions [");
        Iterator iterator0 = descriptions.iterator();
        while ( iterator0.hasNext() ) {
            sb.append( (String)iterator0.next() );
        }
        sb.append( " ]" );
        
        sb.append("]");
        sb.append( "\n");
        return sb.toString();
    }
}
