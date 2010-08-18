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
import org.w3c.dom.Element;


public class SecurityRoleDescriptor {
    // Portlet Descriptor Element Names
    public static final String ROLE_NAME = "role-name";
    
    // Global variable
    private String roleName;
    private List<String> descriptions = new ArrayList<String>();
    
    public SecurityRoleDescriptor() {
    }
    
    /**
     * Loads the security role descriptor into memory.
     */
    public void load(Element element, String namespaceURI) {
        roleName = PortletXMLDocumentHelper.getChildTextTrim(element, ROLE_NAME);
        List descriptionElements =
                PortletXMLDocumentHelper.getChildElements(element, PortletDescriptorConstants.DESCRIPTION);
        int numDescription = descriptionElements.size();
        for (int i = 0; i < numDescription; i++) {
            Element descriptionElement = (Element)descriptionElements.get(i);
            descriptions.add(PortletXMLDocumentHelper.getTextTrim(descriptionElement));
        }
    }
    
    /**
     * Returns role name.
     * <P>
     * @return <code>String</code> of the role name.
     */
    public String getRoleName() {
        return roleName;
    }
    
    /**
     * Returns the description as a <code>String</code>. If there's more
     * than one descriptions are defined, returns the first one.
     * <P>
     * @return <code>String</code> of the description. The return
     * value could be null if no description is defined.
     */
    public String getDescription() {
        String description = null;
        if ( !descriptions.isEmpty() ) {
            description = (String)descriptions.get(0);
        }
        return description;
    }
    
    /**
     * Returns role description in a List.
     * <P>
     * @return <code>List</code> of <code>String</code>s. Empty
     * <code>List</code> will be returned if not defined.
     */
    public List getDescriptions() {
        return descriptions;
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
        
        sb.append( " descriptions [" );
        Iterator iterator0 = descriptions.iterator();
        while ( iterator0.hasNext() ) {
            sb.append( (String)iterator0.next() );
        }
        sb.append( " ]" );
        
        return sb.toString();
    }
    
}
