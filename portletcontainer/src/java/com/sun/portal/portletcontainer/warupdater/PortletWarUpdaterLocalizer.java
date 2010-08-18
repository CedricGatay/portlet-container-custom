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
 

package com.sun.portal.portletcontainer.warupdater;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;
import java.text.MessageFormat;

/**
 * PortletWarUpdaterLocalizer is responsible for getting
 * the localized debug/warning/error messages from the 
 * properties file.
 */
class PortletWarUpdaterLocalizer {

    public static final String RESOURCE_BASE = "PortletWarUpdater";
    public static ResourceBundle rb =
        PropertyResourceBundle.getBundle(RESOURCE_BASE,
                                         Locale.getDefault());

    public static void setLocale(java.util.Locale locale) {
        // load resource bundle based on locale
        rb = PropertyResourceBundle.getBundle(RESOURCE_BASE, locale);
    }

    /**
     * given a string representation of the locale (i.e. en_US)
     * create a Locale obj.
     */
    public static Locale getLocale(String stringformat) {

        if (stringformat == null)
            return Locale.getDefault();
        StringTokenizer tk = new StringTokenizer(stringformat,"_");
        String lang = "";
        String country = "";
        String variant = "";
        if (tk.hasMoreTokens())
            lang = tk.nextToken();
        if (tk.hasMoreTokens())
            country = tk.nextToken();
        if (tk.hasMoreTokens())
            variant = tk.nextToken();
        return new java.util.Locale(lang,country, variant);
        
    }

    public static String getLocalizedString(String key) {
        return rb.getString(key);
    }
                                            
    public static String getLocalizedString(String key,
                                            Object[] objs) {

        if (objs != null && objs.length > 0) {
            MessageFormat mf = new MessageFormat("");
            mf.setLocale(rb.getLocale());
            mf.applyPattern(rb.getString(key));
            return mf.format(objs);
        } else {
            return rb.getString(key);
        }
    }

    public static void debug(String key) {

        debug(key, null);
    }

    public static void debug(String key, Object[] tokens) {

        String msg = getLocalizedString(key, tokens);
        Object[] toks = { msg };
        System.out.println(
            getLocalizedString("msgDebug", toks));
    }

    public static void warning(String key) {
        
        warning(key, null);
    }
        
    public static void warning(String key, Object[] tokens) {
        
        String msg = getLocalizedString(key, tokens);
        Object[] toks = { msg };
        System.out.println(
            getLocalizedString("msgWarning", toks));
    }

    public static void error(String key) {
        
        error(key, null);
    }
        
    public static void error(String key, Object[] tokens) {
        
        String msg = getLocalizedString(key, tokens);
        Object[] toks = { msg };
        System.err.println(
            getLocalizedString("msgError", toks));
    }
}
