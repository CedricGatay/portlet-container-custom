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
import java.util.Locale;
import org.w3c.dom.Element;


public class SecurityRoleRefDescriptor {
    // Portlet Descriptor Element Names
    
    // Global variable
    private String roleName;
    private String roleLink;
    private List descriptions = new ArrayList();
    private Map descriptionMap = new HashMap();
    
    public SecurityRoleRefDescriptor() {
    }
    
    public void load(Element element, String namespaceURI) {
        roleName = PortletXMLDocumentHelper.getChildTextTrim(element, PortletDescriptorConstants.ROLE_NAME);
        roleLink = PortletXMLDocumentHelper.getChildTextTrim(element, PortletDescriptorConstants.ROLE_LINK);
        
        List descriptionElements = PortletXMLDocumentHelper.getChildElements(element, PortletDescriptorConstants.DESCRIPTION);
        int numDescriptions = descriptionElements.size();
        String description;
        for (int i = 0; i < numDescriptions; i++) {
            Element descriptionElement = (Element)descriptionElements.get(i);
            description = PortletXMLDocumentHelper.getTextTrim(descriptionElement);
            descriptions.add( description );
            
            Map descAttributes = PortletXMLDocumentHelper.getAttributeTable(descriptionElement);
            Iterator dait = descAttributes.entrySet().iterator();
            String lang;
            if(dait.hasNext()) {
                while(dait.hasNext()) {
                    Map.Entry entry = (Map.Entry) dait.next();
                    lang = (String)entry.getKey();
                    if (lang.equals(PortletDescriptorConstants.XML_LANG_ATTR)) {
                        descriptionMap.put(entry.getValue(), description);
                    }
                }
            } else {
                lang = Locale.getDefault().toString().replace('_', '-');
                descriptionMap.put(lang, description);
            }
        }
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public String getRoleLink() {
        return roleLink;
    }
    
    public String getDescription() {
        String description = null;
        if ( !descriptions.isEmpty() ) {
            description = (String)descriptions.get(0);
        }
        return description;
    }
    
    public List getDescriptions() {
        return descriptions;
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
    
    public String toString() {
        StringBuffer sb = new StringBuffer( "SecurityRoleDescriptor ");
        
        sb.append( " role name [" );
        if ( roleName != null ) {
            sb.append( roleName );
        } else {
            sb.append( "NULL" );
        }
        sb.append("]");
        
        sb.append( " role link [" );
        if ( roleLink != null ) {
            sb.append( roleLink );
        } else {
            sb.append( "NULL" );
        }
        sb.append("]");
        
        sb.append( " description [" );
        if ( !descriptions.isEmpty() ) {
            sb.append( (String)descriptions.get(0) );
        } else {
            sb.append( "NULL" );
        }
        sb.append("]");
        
        return sb.toString();
    }
    
}
