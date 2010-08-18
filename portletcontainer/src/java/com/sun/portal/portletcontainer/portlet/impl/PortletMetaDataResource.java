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
 

package com.sun.portal.portletcontainer.portlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 *  The PortletMetaDataResource  represents the resource information
 *  for a  locale.
 *  It contains localized information like portlet title, short-title and keywords.
 */
public class PortletMetaDataResource implements Serializable {
    
    static final long serialVersionUID = 2L; 
    private Map metadataMap = new HashMap();
    
    public PortletMetaDataResource(ResourceBundle bundle) {
        if(bundle!=null){
            Enumeration keys = bundle.getKeys();
            while(keys.hasMoreElements()){
                String key = (String)keys.nextElement();
                if(key.equals(PortletResourceBundle.RB_TITLE)) {
                    metadataMap.put(PortletResourceBundle.RB_TITLE,bundle.getString(PortletResourceBundle.RB_TITLE));
                } else if(key.equals(PortletResourceBundle.RB_SHORT_TITLE)) {
                    metadataMap.put(PortletResourceBundle.RB_SHORT_TITLE,bundle.getString(PortletResourceBundle.RB_SHORT_TITLE));
                } else if(key.equals(PortletResourceBundle.RB_KEYWORDS)) {
                    metadataMap.put(PortletResourceBundle.RB_KEYWORDS,bundle.getString(PortletResourceBundle.RB_KEYWORDS));
                }
            } //End of while
        }//End of if
    }//End of Constructor
    
    public String getTitle() {
        return (String)metadataMap.get(PortletResourceBundle.RB_TITLE);
    }
    
    public String getShortTitle() {
        return (String)metadataMap.get(PortletResourceBundle.RB_SHORT_TITLE);
    }
    
    public String getKeywords() {
        return (String)metadataMap.get(PortletResourceBundle.RB_KEYWORDS);
    }
    
    public List getKeywordsList() {
        return getListProperty(PortletResourceBundle.RB_KEYWORDS);
    }
    
    public String getString(String key){
        String value = null;
        if(key!=null){
            if(key.equals(PortletResourceBundle.RB_TITLE)) {
                value = getTitle();
            } else if(key.equals(PortletResourceBundle.RB_SHORT_TITLE)) {
                value = getShortTitle();
            } else if(key.equals(PortletResourceBundle.RB_KEYWORDS)) {
                value = getKeywords();
            }
        }
        return value;
    }
    
    /**
     * This method checks whether ResourceBundle has provided any
     * metadata (title, short-title & keywords)
     * @return  boolean
     */
    public boolean hasData(){
        return (metadataMap.size() > 0) ;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!metadataMap.isEmpty()) {
            String newLine = System.getProperty("line.separator");
            Iterator keyIterator = metadataMap.keySet().iterator();
            while (keyIterator.hasNext()) {
                String key = (String) keyIterator.next();
                sb.append(key).append("=").append(metadataMap.get(key)).append(newLine);
            }
        }
        return sb.toString();
    }
    
    private List getListProperty(String key) {
        List retval = new ArrayList();
        String strValue = (String)metadataMap.get(key);
        if (strValue != null) {
            StringTokenizer st = new StringTokenizer(strValue, ",", false );
            while ( st.hasMoreTokens() ) {
                String keyword = st.nextToken().trim();
                retval.add(keyword);
            }
        }
        return retval;
    }
    
}
