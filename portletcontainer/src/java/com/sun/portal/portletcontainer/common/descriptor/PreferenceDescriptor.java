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

import com.sun.portal.container.ContainerLogger;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.w3c.dom.Element;


/**
 * This class provides access to the default values of the portlet
 * preferences.
 */
public class PreferenceDescriptor {
        
    // Global variables
    private String prefName;
    private List<String> prefValues = new ArrayList();
    private List<String> descriptions = new ArrayList();
    private boolean readOnly = false;
    private boolean multiValue = false;
    private String portletName;
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PreferenceDescriptor.class, "PCCLogMessages");
    
    public PreferenceDescriptor(String portletName ) {
        this.portletName = portletName;
    }
    
    /**
     * Loads the preference descriptor.
     * <P>
     * @param element the preference element
     */
    public void load( Element element, String namespaceURI ) {
        prefName = PortletXMLDocumentHelper.getChildTextTrim(element, 
                PortletDescriptorConstants.PREF_NAME);
        if ( prefName == null ) {
            logger.log( Level.WARNING, "PSPL_PCCCSPPCCD0011", this.portletName);
        }
        
        List prefValueElements = PortletXMLDocumentHelper.getChildElements(element, 
                PortletDescriptorConstants.PREF_VALUE);
        if ( prefValueElements.isEmpty() ) {
            logger.log( Level.WARNING, "PSPL_PCCCSPPCCD0012", new String[] { prefName, this.portletName } );
        } else {
            int numPrefValue = prefValueElements.size();
            for (int i = 0; i < numPrefValue; i++) {
                Element prefValueElement = (Element)prefValueElements.get(i);
                prefValues.add(PortletXMLDocumentHelper.getTextTrim(prefValueElement));
            }
        }
        
        List descriptionElements = PortletXMLDocumentHelper.getChildElements(element, 
                PortletDescriptorConstants.DESCRIPTION);
        int numDescription = descriptionElements.size();
        for (int i = 0; i < numDescription; i++) {
            Element descriptionElement = (Element)descriptionElements.get(i);
            descriptions.add(PortletXMLDocumentHelper.getTextTrim(descriptionElement));
        }
        
        String readOnly = PortletXMLDocumentHelper.getChildTextTrim(element, 
                PortletDescriptorConstants.READ_ONLY);
        if (readOnly != null && readOnly.equals("true")) {
            this.readOnly = true;
        }
        
        Element multiValueElement = PortletXMLDocumentHelper.getChildElement(element, 
                PortletDescriptorConstants.MULTI_VALUE);
        if (multiValueElement != null) {
            multiValue = true;
        }
    }
    
    /**
     * Returns the preference name.
     * @return A <code>String</code>.
     */
    public String getPrefName() {
        return prefName;
    }
    
    /**
     * Returns the preference values as a <code>List</code>.
     * @return A <code>List</code> of <code>String</code>s.
     */
    public List getPrefValues() {
        return prefValues;
    }
    
    /**
     * Returns the description. If description is not define, null is returned.
     */
    public String getDescription() {
        String description = null;
        if ( !descriptions.isEmpty() ) {
            description = (String)descriptions.get(0);
        }
        return description;
    }
    
    /**
     * Returns the descriptions in a <code>List</code>.
     * <P>
     * @return A <code>List</code> of the <code>String</code>s. The
     * returned value could be empty list if description is not defined.
     */
    public List getDescriptions() {
        return descriptions;
    }
    
    /**
     * Returns if the preference is modifiable. If <read-only> is
     * not defined in the descriptor for a portlet preference, false is return.
     */
    public boolean getReadOnly() {
        return this.readOnly;
    }
    
    /**
     * Returns if the preference has multi-values.
     */
    public boolean getMultiValueFlag() {
        return multiValue;
    }
    
    /**
     * The toString method.
     * <P>
     * @return the <code>String</code> representation of the portlet
     * preference descriptor.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "PreferenceDescriptor [");
        
        sb.append( " preference name [" );
        if ( prefName != null ) {
            sb.append( prefName );
        } else {
            sb.append( "NULL" );
        }
        sb.append("]");
        
        sb.append( " preference values [" );
        Iterator iterator = prefValues.iterator();
        while ( iterator.hasNext() ) {
            sb.append( (String)iterator.next() );
        }
        sb.append("]");
        
        sb.append( " description [" );
        if ( !descriptions.isEmpty() ) {
            sb.append( (String)descriptions.get(0) );
        } else {
            sb.append( "NULL" );
        }
        sb.append("]");
        
        sb.append( " read-only [" );
        if ( this.readOnly ) {
            sb.append( "fase" );
        } else {
            sb.append( "true" );
        }
        sb.append("]");
        
        sb.append( "]" );
        sb.append( "\n" );
        
        return ( sb.toString() );
    }
}
