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

import com.sun.portal.portletcontainer.common.PortletContainerCacheControl;
import javax.portlet.CacheControl;

/**
 * The CacheControl interface represents cache settings for a piece of markup. 
 * The settings are only valid for the current request.
 * The CacheControlImpl class provides the implementation of the CacheContol interface.
 */
public class CacheControlImpl implements CacheControl{

    private boolean useCachedContent;
    private PortletContainerCacheControl pcCacheControl;

    public CacheControlImpl(PortletContainerCacheControl pcCacheControl){
        this.pcCacheControl = pcCacheControl;
    }
    
 
    /**
     * Get the currently set expiration time, or null if no expiration time is set.
     * This call returns the same value as the getProperty(EXPIRATION_CACHE) call.
     * @return the currently set expiration time in seconds, or <code>null</code> if no expiration time is set.
     */
    public int getExpirationTime() {
        return this.pcCacheControl.getExpirationTime();
    }

    /**
     * Sets a new expiration time for the current response in seconds.
     * If the expiration value is set to 0, caching is disabled for this portlet; if the value is set to -1, the cache does not expire.
     * This call is equivalent to calling setProperty(EXPIRATION_CACHE).
     * @param expirationTime expiration time in seconds
     */
    public void setExpirationTime(int expirationTime) {
        this.pcCacheControl.setExpirationTime(expirationTime);
    }
    
    /**
     * Returns a boolean indicating whether the caching scope is set to public for the current response. If no caching scope is set, the default scope is not public and thus returns false.
     * Public cache scope indicates that the cache entry can be shared across users. Non-public, or private cache scope indicates that the cache entry must not be shared across users.
     * This call is equivalent to calling getProperty(CACHE_SCOPE).equals(PUBLIC_SCOPE).
     * @return <CODE>boolean</CODE> indicating whether the caching scope is set to public for the current response
     */
    public boolean isPublicScope() {
        return this.pcCacheControl.isPublicScope();
    }
    
    /**
     * Sets the caching scope for the current response to public with true as publicScope and to private with false as publicScope.
     * Public cache scope indicates that the cache entry can be shared across users. Non-public, or private cache scope indicates that the cache entry must not be shared across users.
     * This call is equivalent to calling (publicScope ? setProperty(CACHE_SCOPE, PUBLIC_SCOPE | setProperty(CACHE_SCOPE, PRIVATE_SCOPE)
     * @param publicScope 
     */
    public void setPublicScope(boolean publicScope) {
        this.pcCacheControl.setPublicScope(publicScope);
    }
    
    /**
     * Returns the ETag for the current response that is used as validation tag, or null if no ETag is set.
     * This call is equivalent to calling getProperty(ETAG).
     * @return the ETag for the current response that is used as validation tag, or null if no ETag is set.
     */
    public String getETag() {
        return this.pcCacheControl.getETag();
    }
    
    /**
     * Sets an ETag for the current response that is used as validation tag. 
     * If an ETag was already set it is replaced with the new value.
     * This call is equivalent to calling setProperty(ETAG, token).
     * @param eTag 
     */
    public void setETag(String eTag) {
        this.pcCacheControl.setETag(eTag);
    }
    
    /**
     * Returns a boolean indicating whether the cached content must be used. 
     * A value of true indicates that the cache is still valid and must be used.
     * This call is equivalent to calling getProperty(USE_CACHED_CONTENT).
     * @return <CODE>boolean</CODE> indicating whether the cached content must be used.
     */
    public boolean useCachedContent() {
        return this.pcCacheControl.useCachedContent();
    }
    
    /**
     * Sets the <CODE>boolean</CODE> indicating whether the cached content must be reused.
     * This call is equivalent to setProperty(USE_CACHED_CONTENT).
     * @param useCachedContent <CODE>boolean</CODE> indicating whether the cached content must be used or not.
     */
    public void setUseCachedContent(boolean useCachedContent) {
        this.pcCacheControl.setUseCachedContent(useCachedContent);
    }
}
