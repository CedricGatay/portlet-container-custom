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
 * This class provides access to the portlet preferences descriptor.
 */
public class PortletPreferencesDescriptor {

    // Preferences Descriptor Element Names
    public static final String PREFERENCE = "preference";
    public static final String PREFERENCES_VALIDATOR = "preferences-validator";

    // Global variables
    private List<PreferenceDescriptor> preferenceDescriptors;
    private String portletName;
    private String validatorName;

    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PortletPreferencesDescriptor.class, "PCCLogMessages");

    public PortletPreferencesDescriptor(String portletName ) {
	    this.portletName = portletName;
        this.preferenceDescriptors = new ArrayList<PreferenceDescriptor>();
    }

    /**
     * Loads the portlet preferences descriptor.
     * <P>
     * @param element The portlet preferences element
     */
    public void load( Element element, String namespaceURI ) {
        List preferenceElements =
                PortletXMLDocumentHelper.getChildElements(element, PREFERENCE);
	if ( preferenceElements.isEmpty()) {
	      logger.log( Level.WARNING, "PSPL_PCCCSPPCCD0014", this.portletName);
	}

        int numPreference = preferenceElements.size();
        for (int i = 0; i < numPreference; i++) {
            Element preferenceElement = (Element)preferenceElements.get(i);
	    PreferenceDescriptor preferenceDescriptor = new PreferenceDescriptor(this.portletName);
	    preferenceDescriptor.load( preferenceElement, namespaceURI );
	    preferenceDescriptors.add( preferenceDescriptor );
	}

        validatorName = PortletXMLDocumentHelper.getChildTextTrim(element, PREFERENCES_VALIDATOR);
    }

    /**
     * Returns the preference descriptors in a <code>List</code>.
     * <P>
     * @return A <code>List</code> of <code>PreferenceDescriptor</code>s.
     */
    public List getPreferenceDescriptors() {
        return preferenceDescriptors;
    }

    /**
     * Returns a specified <code>PreferenceDescriptor</code>.
     * <P>
     * @param preferenceName The preference name
     * @return A <code>PreferenceDescriptor</code>
     */
    public PreferenceDescriptor getPreferenceDescriptor( 
					String preferenceName ) {

        PreferenceDescriptor preference = null;
        boolean stop = false;
        Iterator iterator = preferenceDescriptors.iterator();
        while ( iterator.hasNext() && !stop ) {
            PreferenceDescriptor preferenceDescriptor = (PreferenceDescriptor)iterator.next();
            if (preferenceDescriptor.getPrefName().equals( preferenceName ) ) {
                preference = preferenceDescriptor;
            stop = true;
	    }
	}

	return preference;
    }

    /** 
     * Returns the <code>PreferencesValidator</class> class name if it
     * is defined.
     * <P>
     * @return <code>String</code>  Returns null
     * if the validator is not defined.
     */
    public String getPreferencesValidatorName() {
        return validatorName;
    }
    
    /**
     * The toString method.
     * <P>
     * @return the <code>String</code> representation of the portlet
     * preferences descriptor.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "PortletPreferencesDescriptor [");

	Iterator iterator = preferenceDescriptors.iterator();
	while ( iterator.hasNext() ) {
	    PreferenceDescriptor preferenceDescriptor = (PreferenceDescriptor)iterator.next();
	    sb.append( preferenceDescriptor.toString() );
	}

        if ( validatorName != null ) {
            sb.append( " preferences validator name [" );
            sb.append( validatorName );
            sb.append( "] ");
        }
        
	sb.append("]");

	return ( sb.toString() );
    }

}
