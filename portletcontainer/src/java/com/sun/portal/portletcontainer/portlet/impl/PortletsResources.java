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
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.StringTokenizer;

import java.util.MissingResourceException;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *  The PortletResources class stores the  portlet information  
 *  specified through the resource bundle 
 *  for all the supported locales specified in portlet descriptor.
 */
public class PortletsResources {

    private Map<String,Map<String,ResourceBundle>> portletResourceMap = null;
    private Map<String, Map<String,PortletMetaDataResource>> portletMetadataResourceMap = null;
    private PortletAppDescriptor portletAppDescriptor = null;
    private static Logger logger = ContainerLogger.getLogger(PortletsResources.class,
            "PAELogMessages");

    /** Creates a new instance of PortletsResources */
    public PortletsResources(PortletAppDescriptor pd) {
        portletAppDescriptor = pd;
        init();
    }

    private void init() {
        if (getPortletAppD() != null) {
            List<String> portlets = getPortletNames();
            if (portlets != null) {
                portletResourceMap = new HashMap<String,Map<String,ResourceBundle>>();
                portletMetadataResourceMap = new HashMap();
                for(String portlet : portlets) {
                    loadResources(portlet);
                }
            }
        }
    }

    private void loadResources(String portlet) {
        Map<String,ResourceBundle> resBundleMap = new HashMap<String,ResourceBundle>();
        Map<String,PortletMetaDataResource> metadataBundleMap = new HashMap<String,PortletMetaDataResource>();
        String rbName = getResourceBundleName(portlet);
        if (rbName != null && rbName.trim().length() != 0) {
            List<String> locales = getSupportedLocales(portlet);
            if (locales != null && locales.size() > 0) {
                for(String localeString : locales) {
                    String lang = "";
                    String country = "";
                    String variant = "";
                    StringTokenizer tokenizer = new StringTokenizer(localeString, "_");
                    lang = tokenizer.nextToken();
                    if (tokenizer.hasMoreTokens()) {
                        country = tokenizer.nextToken();
                    }
                    if (tokenizer.hasMoreTokens()) {
                        variant = tokenizer.nextToken();
                    }
                    Locale thisLocale = new Locale(lang, country, variant);
                    ResourceBundle bundle = loadResource(rbName, thisLocale);
                    if (bundle != null) {
                        resBundleMap.put(thisLocale.toString(), bundle);
                        PortletMetaDataResource pmds = new PortletMetaDataResource(bundle);
                        if (pmds.hasData()) {
                            metadataBundleMap.put(thisLocale.toString(), pmds);
                        }
                    }
                }
            }
            ResourceBundle bundle = loadResource(rbName, Locale.getDefault());
            if (bundle != null) {
                resBundleMap.put(Locale.getDefault().toString(), bundle);
                metadataBundleMap.put(Locale.getDefault().toString(),
                        new PortletMetaDataResource(bundle));
            }
            portletResourceMap.put(portlet, resBundleMap);
            portletMetadataResourceMap.put(portlet, metadataBundleMap);
        }
    }

    private ResourceBundle loadResource(String rbName, Locale locale) {
        ResourceBundle retval;
        if (locale == null) {
            locale = Locale.getDefault();
        }

        try {
            retval = ResourceBundle.getBundle(rbName, locale,
                    Thread.currentThread().getContextClassLoader());
        } catch (MissingResourceException ex) {
            logger.log(Level.FINE, "PSPL_PAECSPPI0013", rbName);
            retval = null;
        }
        return retval;
    }

    /**
     * This method is different from getPortletMetadataResourceMap() method.
     * It returns a map of ResourceBundle class while
     * getPortletMetadataResourceMap() returns a map of PortletMetaDataResource class.
     * @return java.util.Map
     */
    public Map<String,Map<String,ResourceBundle>> getPortletResourceMap() {
        return portletResourceMap;
    }

    /**
     * This method is different from getPortletResourceMap() method.
     * It returns a map of PortletMetaDataResource class while  getPortletResourceMap()
     * returns a map of ResourceBundle class.
     * @return java.util.Map
     */
    public Map<String, Map<String,PortletMetaDataResource>> getPortletMetadataResourceMap() {
        return portletMetadataResourceMap;
    }

    private PortletAppDescriptor getPortletAppD() {
        return portletAppDescriptor;
    }

    private PortletDescriptor getPortletDescriptor(String portlet) {
        return getPortletAppD().getPortletsDescriptor().getPortletDescriptor(portlet);
    }

    private List getSupportedLocales(String portlet) {
        return getPortletDescriptor(portlet).getSupportedLocales();
    }

    private String getResourceBundleName(String portlet) {
        return getPortletDescriptor(portlet).getResourceBundle();
    }

    private List getPortletNames() {
        return getPortletAppD().getPortletsDescriptor().getPortletNames();
    }
}


