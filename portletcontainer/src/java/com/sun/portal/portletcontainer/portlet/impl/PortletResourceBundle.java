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

import com.sun.portal.container.ContainerLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortletResourceBundle extends ResourceBundle {
	
    private ResourceBundle resourceBundle;
    private Object[] inlineValues;
    private boolean[] valuesDefinedInline;
    private boolean[] valuesInRB = new boolean[]{ true, true, true };
    private String rbName;
    private static List keys = new ArrayList();

    private static Logger logger = ContainerLogger.getLogger(PortletResourceBundle.class, "PAELogMessages");
    
    // Attributes used in the ResourceBundle to refer title, short-title and keywords
    public static final String RB_TITLE = "javax.portlet.title";
    public static final String RB_SHORT_TITLE = "javax.portlet.short-title";
    public static final String RB_KEYWORDS = "javax.portlet.keywords";

    // Prefix for the attributes used in the ResourceBundle to refer preference name and attribute
    public static final String RB_PREFERENCE_NAME = "javax.portlet.preference.name.";
    public static final String RB_PREFERENCE_VALUE = "javax.portlet.preference.value.";

    // static initializer
    static {
		keys.add(RB_TITLE);
        keys.add(RB_SHORT_TITLE);
		keys.add(RB_KEYWORDS);
    }

    // inlineValues values should be empty Strings "" if the values
    // are not inline.
    public PortletResourceBundle(ResourceBundle rb, String rbName, 
                                 Object[] inlineValues,
                                 boolean[] valuesDefinedInline) {
        if (rb == null) {
            valuesInRB[0] = valuesInRB[1] = valuesInRB[2] = false;
        }
        
        this.resourceBundle = rb;
        this.inlineValues = inlineValues;
        this.valuesDefinedInline = valuesDefinedInline;
        this.rbName = rbName;
    }

    public Object handleGetObject(String key) throws MissingResourceException {
        Object obj = null;
        if (key.equals(RB_TITLE)) {
            obj = getValue(key,0);

        } else if (key.equals(RB_SHORT_TITLE)) {
            obj = getValue(key,1);
			
        } else if (key.equals(RB_KEYWORDS)) {
            obj = getValue(key,2);

        } else {
            if (resourceBundle == null) {
                throw new
                    MissingResourceException("PortletResourceBundle.handleGetObject: missing resource bundle", this.rbName, key);
            }
            
            obj = resourceBundle.getObject(key);
        }
        return obj;
    }

    private Object getValue(String key,int i) {
        Object obj = null;
        if (valuesInRB[i]) {
            try {
                obj = resourceBundle.getObject(key);
            }
            catch (MissingResourceException ex) {
                valuesInRB[i] = false;
                obj = searchInlineValue(i, key);                
            }
        } else {
            obj = searchInlineValue(i, key);            
        }
        return obj;
    }

    private Object searchInlineValue(int i, String key) {
        Object obj = null;
        if (this.valuesDefinedInline[i]) {                    
            obj = this.inlineValues[i];
        } else {
			if(logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "PSPL_PAECSPPI0043", 
					new String[] {key, this.rbName});
			}
			obj = "";
        }                 
        return obj;        
    }
    

    public Enumeration getKeys() {

        if (resourceBundle != null) {
            for (Enumeration e = resourceBundle.getKeys(); e.hasMoreElements(); ) {
				Object key = e.nextElement();
				if (!keys.contains(key)) { 
                    keys.add(key);
				}
            }
        }
        
		return Collections.enumeration(keys);
    }

}
