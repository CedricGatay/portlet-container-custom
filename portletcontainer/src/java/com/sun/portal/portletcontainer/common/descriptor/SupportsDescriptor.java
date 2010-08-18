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
import java.util.Locale;
import org.w3c.dom.Element;


/**
 * The suppors descriptor is the descriptor for the portlet modes that
 * are supported for a portlet.
 * <P>
 */
public class SupportsDescriptor {
    
    // Supports Element Names
    public static final String MIME_TYPE = "mime-type";
    public static final String PORTLET_MODE = "portlet-mode";
    
    // Global variables
    private String mimeType;
    private List<String> portletModes = new ArrayList<String>();
    
    /**
     * Loads the init param descriptor into memory.
     * <P>
     * @param The supports Element
     */
    public void load( Element element, String  namespaceURI ) {
        mimeType = PortletXMLDocumentHelper.getChildTextTrim(element, MIME_TYPE);
        List modeElements =
                PortletXMLDocumentHelper.getChildElements(element, PORTLET_MODE);
        if(modeElements != null){
            int numMode = modeElements.size();
            for (int i = 0; i < numMode; i++) {
                Element modeElement = (Element)modeElements.get(i);
                // Save the modes in uppercase
                portletModes.add( PortletXMLDocumentHelper.getTextTrim(modeElement).toUpperCase(Locale.ENGLISH) );
            }
        }
        
    }
    
    /**
     * Returns the mime type.
     * <P>
     * @return <code>String</code> of the mime type.
     */
    public String getMimeType() {
        return mimeType;
    }
    
    /**
     * Returns the supported portlet modes.
     * <P>
     * @return <code>List</code> of the portlet modes for this mime type.
     */
    public List<String> getPortletModes() {
        return portletModes;
    }
    
    /**
     * The toString method.
     * <P>
     * @return the <code>String</code> representation of the supports
     * descriptor.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "SupportsDescriptor [");
        
        sb.append( "mime type [").append(mimeType).append("] ");
        sb.append( "portlet mode [");
        for ( int i = 0; i < portletModes.size(); i++ ) {
            sb.append( (String)portletModes.get(i));
            sb.append(", ");
        }
        sb.append("] ]");
        
        return sb.toString();
    }
}
