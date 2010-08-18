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

import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.ContainerResponse;
import com.sun.portal.container.service.Service;

/**
 * The <code>ClientCachingService</code> is responsible for managing the resource contents
 * that are cached on the Browser by appropriately setting the HTTP Response headers. 
 * The operations on the cached resources include adding, retrieving, updating, and removing 
 * the cache entries to/from the browser cache repository.
 * <p>
 * The portlet specification defines two basic portlet content 
 * caching mechanisms: EXPIRATION cache and VALIDATING cache.
 * Similar mechanism is supported by HTTP 1.1 and the browsers conforming to 
 * HTTP 1.1 support both caching mechanisms and the ClientCachingService leverages
 * the same.
 * <p>
 * <code>ClientCachingService</code> is an interface that is used by the 
 * portlet container.
 */
public interface ClientCachingService  extends Service {

    /**
     * This method updates the cache with the <code>ResourceCacheEntry</code> provided, in the public 
     * or private scope as identified by the isPublic <code>boolean</code> parameter. This also sets 
     * the HTTP Response headers to ensure that the Browser caches the content.
     *
     * @param containerRequest The <CODE>ContainerRequest</CODE> object which contains the original <CODE>HttpServletRequest</CODE>
     * @param containerResponse The <CODE>ContainerResponse</CODE> object which contains the original <CODE>HttpServletResponse</CODE> whose headers are updated
     * @param resourceCacheEntry The <CODE>ResourceCacheEntry</CODE> object which is stored in the cache
     * @param isPublic The <CODE>boolean</CODE> which indicates whether the caching scope is public or not
     */
    public void updateResourceCache(ContainerRequest containerRequest, ContainerResponse containerResponse, 
            ResourceCacheEntry resourceCacheEntry, boolean isPublic);
    
    /**
     * This updates the cache with the <code>ResourceCacheEntry</code> provided. It also ensures 
     * that the HTTP Response status is set to 304 (NOT MODIFIED) so that the cache in the browser 
     * is reused.
     *
     * @param containerRequest The <CODE>ContainerRequest</CODE> object which contains the original <CODE>HttpServletRequest</CODE>
     * @param containerResponse The <CODE>ContainerResponse</CODE> object which contains the original <CODE>HttpServletResponse</CODE> whose headers are updated
     * @param resourceCacheEntry The <CODE>ResourceCacheEntry</CODE> object which is stored in the cache
     * @param isPublic The <CODE>boolean</CODE> which indicates whether the caching scope is public or not
     */
    public void updateAndReuseResourceCache(ContainerRequest containerRequest, ContainerResponse containerResponse, 
            ResourceCacheEntry resourceCacheEntry, boolean isPublic);
    
    /**
     * This method checks the HTTP Request headers to see if the cache has expired. It also removes 
     * the <CODE>ResourceCacheEntry</CODE> from its cache, if VALIDATION cache is not being used.
     *
     * @param containerRequest The <CODE>ContainerRequest</CODE> object which contains the original <CODE>HttpServletRequest</CODE>
     * @param containerResponse The <CODE>ContainerResponse</CODE> object which contains the original <CODE>HttpServletResponse</CODE> whose headers are updated
     * @return <CODE>boolean</CODE> indicating whether the cache in the Browser has expired.
     */
    public boolean hasResourceCacheExpired(ContainerRequest containerRequest,
            ContainerResponse containerResponse);
    
    /**
     * This method returns the <CODE>ResourceCacheEntry</CODE> object which is stored in the cache, 
     * corresponding to the containerRequest uri in the <CODE>ContainerRequest</CODE> argument.
     *
     * @param containerRequest The <CODE>ContainerRequest</CODE> object which contains the original <CODE>HttpServletRequest</CODE>
     * @return <CODE>ResourceCacheEntry</CODE> stored in the cache or <CODE>null</CODE> if not present
     */
    public ResourceCacheEntry getCachedResource(ContainerRequest containerRequest);
    
    /**
     * This method removes the <CODE>ResourceCacheEntry</CODE> object from the cache. This is 
     * typically called when the cache in the Browser has expired and VALIDATION caching is not 
     * being used.
     *
     * @param containerRequest The <CODE>ContainerRequest</CODE> object which contains the original <CODE>HttpServletRequest</CODE>
     */
    public void removeCachedResource(ContainerRequest containerRequest);
}
