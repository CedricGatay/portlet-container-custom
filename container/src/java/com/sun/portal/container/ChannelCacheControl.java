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
 * [Name of File] [ver.__] [Date]
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */


package com.sun.portal.container;

/**
 * The <code>ChannelCacheControl</code> interface represents cache settings for 
 * a piece of markup. 
 * The settings are only valid for the current request.
 */
public class ChannelCacheControl {

    private int expirationTime;
    private boolean isPublicScope;
    private String eTag;
    private boolean useCachedContent;

    /**
     * Get the currently set expiration time.
     * If no expiration time is set on this response the
     * default defined in the portlet deployment descriptor
     * with the <code>expiration-cache<code> tag is returned,
     * or <code>0</code> if no default is defined.
     * 
     * @return  the currently set expiration time in seconds,
     *          or <code>0</code> if no expiration time
     *          is set.
     */    
    public int getExpiration() {
        return expirationTime;
    }

    /**
     * Sets a new expiration time for the current response
     * in seconds.
     * <p>
     * If the expiration value is set to 0, caching is disabled for this
     * portlet; if the value is set to -1, the cache does not expire.
     * <p>
     * 
     * @param expirationTime  expiration time in seconds
     */    
    public void setExpiration(int expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * Returns a boolean indicating whether the
     * caching scope is set to public for the current response.
     * <p>
     * Public cache scope indicates that the cache entry can be shared across
     * users. Non-public, or private cache scope indicates that the cache entry
     * must not be shared across users.
     * 
     * @return true if the cache scope is public for the
     *         current response.
     */
    public boolean isPublicScope() {
        return isPublicScope;
    }

    /**
     * Sets the caching scope for the current response
     * to public with <code>true</code> as 
     * <code>publicScope</code> and to private with
     * <code>false</code> as <code>publicScope</code>.
     * <p>
     * Public cache scope indicates that the cache entry can be shared across
     * users. Non-public, or private cache scope indicates that the cache entry 
     * must not be shared across users.
     * 
     * @param isPublicScope  indicating if the cache entry can be shared across users
     */    
    public void setPublicScope(boolean isPublicScope) {
        this.isPublicScope = isPublicScope;
    }

    /**
     * Returns the ETag for the current response that is
     * used as validation tag, or <code>null</null>
     * if no ETag is set on the response.
     * 
     * @return  the ETag for the current response that is
     *          used as validation tag, or <code>null</null>
     *          if no ETag is set.
     */    
    public String getETag() {
        return eTag;
    }

    /**
     * Sets an ETag for the current response that is
     * used as validation tag. If an ETag was already
     * set it is replaced with the new value.
     * <p>
     * Setting the ETag to <code>null</code> removes
     * the currently set ETag.
     *  
     * @param eTag  the ETag token
     */    
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    /**
     * Returns a boolean indicating whether the
     * cached content for the provided ETag at the request
     * can still be considerated valid.
     * If not set, the default is <code>false</code>.
     * 
     * @return  boolean indicating whether the
     *          caching scope is set to public for the current response
     */    
    public boolean useCachedContent() {
        return useCachedContent;
    }

    /**
     * Sets the indication whether the cached content
     * for the provided ETag at the request is still valid or not.
     * If set to <code>true</code> no output should be rendered,
     * but a new expiration time should be set for the
     * markup with the given ETag .
     *        
     * @param useCachedContent
     */    
    public void setUseCachedContent(boolean useCachedContent) {
        this.useCachedContent = useCachedContent;
    }
        
}
