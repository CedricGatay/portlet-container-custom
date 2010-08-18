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
 

package com.sun.portal.container.service.caching;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * The <code>PortletCacheEntry</code> class represents an entry in the 
 * portlet content cache
 */

public class PortletCacheEntry implements Serializable {

    private int type;
    private StringBuffer content;
    private String titleResource;
    private Map<String, List<Element>> elementProperties;
    private Map<String, List<String>> stringProperties;
    private long expirationTime;
    private String eTag;
	private String localeString;
    private boolean needsValidation;

    /**
     * Constructor for a class representing an entry in the
     * portlet content cache.
     * <p>
     * @param cacheType either <code>PortletCacheType.TYPE_EXPIRATION
     *        </code> or <code>PortletCacheType.TYPE_VALIDATING</code>
     * @param cachedContent portlet content represented in the <code>
     *        StringBuffer</code> format
     * @param titleResource portlet title resource represented in the <code>
     *        String</code> format
     * @param expiration duration (in seconds) of the expiration
     *        cache, only used in the case of EXPIRATION cache
     * @param eTag ETag represented in the <code>String</code> format
     */
    public PortletCacheEntry(int cacheType,
                             StringBuffer cachedContent,
                             String titleResource,
							 Map<String, List<Element>> elementProperties,
							 Map<String, List<String>> stringProperties,
                             int expiration,
                             String eTag,
							 String localeString) {

        this.type = cacheType;
        this.content = cachedContent;
        this.titleResource = titleResource;
		this.elementProperties = elementProperties;
		this.stringProperties = stringProperties;
        setExpirationTime(expiration);
        setETag(eTag);
		this.localeString = localeString;
    }
    
    public int getCacheType() {
        return type;
    }

    public StringBuffer getCachedContent() {
        return content;
    }     
   
    public String getTitleResource() {
        return titleResource;
    }

    public Map<String, List<Element>> getElementProperties(){
        return this.elementProperties;
    }

    public Map<String, List<String>> getStringProperties() {
        return this.stringProperties;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
    
    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public void setExpirationTime(int expiration) {
        if (expiration == -1) {
            // cache never expires
            this.expirationTime = java.lang.Long.MAX_VALUE;
        } else {
            this.expirationTime = java.lang.System.currentTimeMillis() + (long) expiration * 1000;
        }
    }
    
    public void setNeedsValidation(boolean needsValidation) {
        this.needsValidation = needsValidation;
    }
    
    public boolean needsValidation() {
        return needsValidation;
    }

	public String getLocaleString() {
		return this.localeString;
	}
}
