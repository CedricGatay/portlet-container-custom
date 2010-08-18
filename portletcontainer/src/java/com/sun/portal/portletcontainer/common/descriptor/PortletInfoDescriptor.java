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
import java.util.StringTokenizer;
import org.w3c.dom.Element;

/**
 * The portlet info descriptor is the descriptor for the specific
 * portlet information. For example, the resource bundle definition.
 * <P>
 * There are two ways to specify the resource bundle in the
 * descriptor, the first way is to define in the descriptor directly, the
 * second way is to reference to a resource bundle in the descriptor.
 * <P>
 * While getting the resource bundle descriptor information, clients
 * need to first use the <code>getType</code>() method to figure out
 * which way the resource bundle is defined.
 */
public class PortletInfoDescriptor {

    // Portlet info descriptor element names
    public static final String TITLE = "title";
    public static final String SHORT_TITLE = "short-title";
    public static final String KEYWORDS = "keywords";

    // Global variables
    private String title;
    private String shortTitle;
    private List keywords = new ArrayList();
    
    public PortletInfoDescriptor() {
    }

    /**
     * Loads the portlet info descriptor into memory.
     * <P>
     * @param The portlet info descriptor Element 
     */
    public void load( Element element, String namespaceURI ) {
        title = PortletXMLDocumentHelper.getChildTextTrim(element, TITLE);
        shortTitle = PortletXMLDocumentHelper.getChildTextTrim(element, SHORT_TITLE);
        String keywordsStr = PortletXMLDocumentHelper.getChildTextTrim(element, KEYWORDS);
        if ( keywordsStr != null ) {
            loadKeywords( keywordsStr );
        }
        
    }


    /**
     * Returns the title. 
     * <P>
     * @return <code>String</code> of the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the short title. 
     * <P>
     * @return <code>String</code> of the title.
     */
    public String getShortTitle() {
        return shortTitle;
    }

    /**
     * Returns the keywords. 
     * <P>
     * @return <code>List</code> of <code>String</code>s represent the 
     * keywords.
     */
    public List getKeywords() {
        return keywords;
    }
    
    /*
     * Loads the keywords into a <code>List</code>
     */
    private void loadKeywords( String keywordsStr ) {
        StringTokenizer st = new StringTokenizer( keywordsStr, ",", false );
        while ( st.hasMoreTokens() ) {
	    String kw = st.nextToken().trim();
            keywords.add(kw);
        }
        
    }


    /**
     * The toString method.
     * <P>
     * @return the <code>String</code> representation of the portlet
     * info descriptor.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "PortletInfoDescriptor ");

        if (title != null) {
            sb.append( " title [" );
            sb.append( title );
            sb.append( "]" );
        }
        
        if ( shortTitle != null ) {
            sb.append( " short title [" );
            sb.append( shortTitle );
            sb.append( "]" );
        }
        
        sb.append( " keywords [" );
        for ( int i = 0; i < keywords.size(); i++ ) {
            sb.append( (String)keywords.get(i) );
            sb.append( ", " );
        }
        sb.append( "]" );
                      
        return sb.toString();
        
    }
    
        
      
}
