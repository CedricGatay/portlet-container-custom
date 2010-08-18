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
package com.sun.portal.portletcontainer.invoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import org.w3c.dom.Element;

/**
 * ResponseProperties holds the properties set by the Portlet. These properties
 * include cookies and headers that are set on the Response. It also includes
 * DOM element should be added to the markup head section of the response to the client.
 * 
 */
public class ResponseProperties {

    private Map<String, List<String>> responseHeaders;
    private Map<String, List<Element>> markupHeaders;
    private List<Cookie> cookies;

    public ResponseProperties() {
        responseHeaders = new HashMap<String, List<String>>();
        markupHeaders = new HashMap<String, List<Element>>();
        cookies = new ArrayList<Cookie>();
    }

    /**
     * Returns the Map of response headers that are set are by the portlet using addProperty method
     * of PortletResponse. If there is no response headers, returns and empty map.
     * 
     * @return the Map of the response headers that set by the portlet
     */
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders == null ? Collections.EMPTY_MAP : responseHeaders;
    }

    /**
     * Returns the list of DOM Elements that are set by the portlet using addProperty method
     * of PortletResponse with the property name as "javax.portlet.markup.head.element".
     * If there is no DOM elements, it returns an empty list.
     * 
     * @return the list of the DOM Elements that set by the portlet
     */
    public List<Element> getMarkupHeadElements() {
        List<Element> markupHeadElements = null;
        if(markupHeaders != null) {
            markupHeadElements = markupHeaders.get("javax.portlet.markup.head.element");
        }
        return markupHeadElements == null ? Collections.EMPTY_LIST : markupHeadElements;
    }

    /**
     * Returns the list of Cookies that are set by the portlet using addProperty method
     * of PortletResponse.
     * If there is no cookies, it returns an empty list.
     * 
     * @return the list of the DOM Elements that set by the portlet
     */
    public List<Cookie> getCookies() {
        return cookies == null ? Collections.EMPTY_LIST : cookies;
    }

    /**
     * Sets the response headers.
     * 
     * @param responseHeaders the response headers
     */
    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        if (responseHeaders != null) {
			this.responseHeaders = mergeMaps(this.responseHeaders, responseHeaders);
        }
    }

    /**
     * Sets the markup headers.
     * 
     * @param markupHeaders the markup headers
     */
    public void setMarkupHeaders(Map<String, List<Element>> markupHeaders) {
        if (markupHeaders != null) {
			this.markupHeaders = mergeMaps(this.markupHeaders, markupHeaders);
        }
    }

    /**
     * Sets the cookies.
     * 
     * @param cookies the cookies
     */
    public void setCookies(List<Cookie> cookies) {
        if (cookies != null) {
            this.cookies.addAll(cookies);
        }
    }

    /**
     *  Clears the response properties
     */
    public void clear() {
        this.responseHeaders.clear();
        this.markupHeaders.clear();
        this.cookies.clear();
    }

	/*
	public static Map<String, List<Element>> mergeMaps(
			Map<String, List<Element>> parameterMap1,
            Map<String, List<Element>> parameterMap2) {

        Map<String, List<Element>>  mergedParameterMap = new HashMap<String, List<Element>>();
		// Get the key from Map1 and merge values from Map1 with that of Map2
		Set<Map.Entry<String, List<Element>>> entries1 = parameterMap1.entrySet();
		for(Map.Entry<String, List<Element>> mapEntry1 : entries1) {
			String key1 = mapEntry1.getKey();
			List<Element> values1 = mapEntry1.getValue();
			List<Element> values2 = parameterMap2.get(key1);
			if(values2 != null) {
				values1.addAll(values2);
			}
			mergedParameterMap.put(key1,values1);
		}
		// Get the key from Map2 and if not present in Map1, add it
		Set<Map.Entry<String, List<Element>>> entries2 = parameterMap2.entrySet();
		for(Map.Entry<String, List<Element>> mapEntry2 : entries2) {
			String key2 = mapEntry2.getKey();
			if(!parameterMap1.containsKey(key2)) {
				mergedParameterMap.put(key2, mapEntry2.getValue());
			}
		}
		return mergedParameterMap;
    }
	 */
	public static Map mergeMaps(
			Map parameterMap1,
            Map parameterMap2) {

        Map  mergedParameterMap = new HashMap();
		// Get the key from Map1 and merge values from Map1 with that of Map2
		Set<Map.Entry> entries1 = parameterMap1.entrySet();
		for(Map.Entry mapEntry1 : entries1) {
			Object key1 = mapEntry1.getKey();
			List values1 = (List)mapEntry1.getValue();
			List values2 = (List)parameterMap2.get(key1);
			if(values2 != null) {
				values1.addAll(values2);
			}
			mergedParameterMap.put(key1,values1);
		}
		// Get the key from Map2 and if not present in Map1, add it
		Set<Map.Entry> entries2 = parameterMap2.entrySet();
		for(Map.Entry mapEntry2 : entries2) {
			Object key2 = mapEntry2.getKey();
			if(!parameterMap1.containsKey(key2)) {
				mergedParameterMap.put(key2, mapEntry2.getValue());
			}
		}
		return mergedParameterMap;
    }
}
