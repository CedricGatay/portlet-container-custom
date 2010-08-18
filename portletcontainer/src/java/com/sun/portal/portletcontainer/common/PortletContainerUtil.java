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


package com.sun.portal.portletcontainer.common;

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.portletcontainer.appengine.LifecycleManager;
import com.sun.portal.portletcontainer.common.descriptor.DeploymentExtensionDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * The PortletContainerUtil class provides utility methods used in the Portlet
 * Container.
 *
 */
public class PortletContainerUtil {
    
    private static Logger logger = ContainerLogger.getLogger(PortletContainerUtil.class, "PCCLogMessages");
    private static final String SUN_PORTLET_XML = "sun-portlet.xml";

	/**
	 * Merges the two maps, if the same key is present in both map1 and map2..
	 * (a) if append is true then the values in map1 comes first, followed by the values in map2
	 * (b) if append is false, the values of map1 is retained, while that of map2 is ignored
	 * 
	 * @param parameterMap1 the parameter map
	 * @param parameterMap2 the parameter map
	 * @param append determines if the contents of the map has to be appended
	 * 
	 * @return merged parameter map
	 */
	public static Map<String, String[]> getMergedParameterMap(Map<String, String[]> parameterMap1,
                                 Map<String, String[]> parameterMap2, boolean append) {
        Map<String, String[]> mergedParameterMap = null;
        if(parameterMap1 == null) {
            mergedParameterMap = parameterMap2;
        } else if(parameterMap2 == null) {
            mergedParameterMap = parameterMap1;
        } else {
            mergedParameterMap = new HashMap<String, String[]>();
            // Get the key from Map1 and merge values from Map1 with that of Map2
            Set<Map.Entry<String, String[]>> entries1 = parameterMap1.entrySet();
            for(Map.Entry<String, String[]> mapEntry : entries1) {
                String key1 = mapEntry.getKey();
                String[] values1 = mapEntry.getValue();
                String[] values2 = parameterMap2.get(key1);
                if(append && values2 != null) {
                    List<String> list1 = new ArrayList(Arrays.asList(values1));
                    List<String> list2 = new ArrayList(Arrays.asList(values2));
                    list1.addAll(list2);
                    mergedParameterMap.put(key1,list1.toArray(new String[0]));
                } else {
                    mergedParameterMap.put(key1, values1);
                }
            }
            // Get the key from Map2 and if not present in Map1, add it
            Set<Map.Entry<String, String[]>> entries2 = parameterMap2.entrySet();
            for(Map.Entry<String, String[]> mapEntry : entries2) {
                String key2 = mapEntry.getKey();
                if(!parameterMap1.containsKey(key2)) {
                    mergedParameterMap.put(key2, mapEntry.getValue());
                }
            }
        }
        return mergedParameterMap;
    }

	/**
	 * Returns the resource bundle for the portlet based on the locale.
	 *
	 * @param portletResourceMap the map that has resource bundle for the portlet
	 * @param portletName the name of the portlet
	 * @param locale the locale
	 *
	 * @return the resource bundle for the portlet based on the locale.
	 */
	public static ResourceBundle getResourceBundle(
		Map<String,Map<String,ResourceBundle>> portletResourceMap,
		String portletName, Locale locale) {

        ResourceBundle bundle = null;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String localeString = locale.toString();
        if (portletResourceMap != null) {
            Map<String,ResourceBundle> resBundleMap = portletResourceMap.get(portletName);
            if (resBundleMap != null) {
                Set<Map.Entry<String, ResourceBundle>> entries = resBundleMap.entrySet();
                for(Map.Entry<String, ResourceBundle> mapEntry : entries) {
                    String key = mapEntry.getKey();
                    if (localeString.startsWith(key)) {
                        bundle = mapEntry.getValue();
                        if (bundle != null) {
                            break;
                        }
                    }
                }
            }
        }
        if(bundle != null) {
            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "PSPL_PCCCSPPCCD0031", new String[] { portletName, localeString });
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "PSPL_PCCCSPPCCD0032", new Object[]{portletName, localeString});
            }
        }
        return bundle;
    }

	/**
	 * Returns the DeploymentExtensionDescriptor that corresponds to sun-portlet.xml.
	 *
	 * @param portletAppContext the servlet context
	 *
	 * @return the DeploymentExtensionDescriptor that corresponds to sun-portlet.xml
	 */
	public static DeploymentExtensionDescriptor getSunPortletDescriptor(ServletContext portletAppContext) {
		if(portletAppContext != null) {
			LifecycleManager lifecycleManager =
				(LifecycleManager)portletAppContext.getAttribute(LifecycleManager.LIFECYCLE_MANAGER);

			return getSunPortletDescriptor(lifecycleManager);
		} else {
			return null;
		}
	}

	/**
	 * Returns the DeploymentExtensionDescriptor that corresponds to sun-portlet.xml.
	 *
	 * @param lifecycleManager the LifecycleManager
	 *
	 * @return the DeploymentExtensionDescriptor that corresponds to sun-portlet.xml
	 */
	public static DeploymentExtensionDescriptor getSunPortletDescriptor(LifecycleManager lifecycleManager) {
		DeploymentExtensionDescriptor sunPortletDescriptor = null;
		if(lifecycleManager != null) {
			sunPortletDescriptor = 
				(DeploymentExtensionDescriptor)lifecycleManager.getPortletExtensionDescriptor(SUN_PORTLET_XML);
		}

		return sunPortletDescriptor;
	}

	/**
	 * Returns the DocumentBuilder in which the entity resolver is set to a dummy value
	 * to prevent the parser from contacting internet to resolve the DTD.
	 *
	 * @return the DocumentBuilder
	 *
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
        factory.setCoalescing(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(new NoOpEntityResolver());
        return builder;
    }

}
