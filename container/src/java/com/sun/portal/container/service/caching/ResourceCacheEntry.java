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

/**
 * The <code>ResourceCacheEntry</code> encapsulates the Resource cache. The actual resource is not cached as 
 * it is delegated to the browser.
 * The expiration time and the ETag are stored in this object which can be used to verify 
 * if the cache in the browser has expired and validate if the expired cache can be reused.
 */

public class ResourceCacheEntry implements Serializable {
    
    private int expirationTime;
    private String eTag;
    
    /**
     * Default constructor for <CODE>ResourceCacheEntry</CODE>
     */
    public ResourceCacheEntry() {
    }
    
    /**
     * Overloaded constructor for <CODE>ResourceCacheEntry</CODE>
     * @param expirationTime The <CODE>int</CODE> value representing the expiration time of the browser cache in seconds
     * @param eTag The <CODE>String</CODE> value representing the browser opaque ETag which will be used for cache validation
     */
    public ResourceCacheEntry(int expirationTime, String eTag) {
        setExpirationTime(expirationTime);
        this.eTag = eTag;
    }
    
    /**
     * Returns the ETag value stored
     * @return The <CODE>String</CODE> ETag stored
     */
    public String getETag() {
        return eTag;
    }
    
    /**
     * Sets the ETag value
     * @param eTag The <CODE>String</CODE> ETag value
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }
    
    /**
     * Returns the expiration time of the cache
     * @return The <CODE>int</CODE> expiration time of the cache
     */
    public int getExpirationTime() {
        return expirationTime;
    }
    
    /**
     * Sets the expiration time of the cache
     * @param expirationTime The <CODE>int</CODE> value of the expiration time in seconds
     */
    public void setExpirationTime(int expirationTime) {
        if (expirationTime == -1) {
            // cache never expires
            this.expirationTime = Integer.MAX_VALUE;
        } else {
            this.expirationTime = expirationTime;
        }
    }
    
}
