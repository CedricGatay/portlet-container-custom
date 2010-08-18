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

package com.sun.portal.container.service.caching.impl;

import com.sun.portal.container.PortletWindowContext;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.ContainerResponse;
import com.sun.portal.container.service.caching.ClientCachingService;
import com.sun.portal.container.service.caching.ResourceCacheEntry;
import com.sun.portal.container.service.ServiceAdapter;
import javax.servlet.http.HttpServletResponse;

public class ClientCachingServiceImpl extends ServiceAdapter implements ClientCachingService {
    private static final String DESCRIPTION = "Enables caching on the browser by setting the appropriate headers of the HTTP Response";
    private static final String CACHED_RESOURCES = "javax.portlet.cached_resources";
    private static final int TIME_PRODUCT = 1000;
   
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
            ResourceCacheEntry resourceCacheEntry, boolean isPublic) {
        ConcurrentMap<String, ResourceCacheEntry> resources = null;
        if(isPublic) {
            resources = getPublicResourceMap(containerRequest);            
        } else {
            resources = getPrivateResourceMap(containerRequest);
        }
        HttpServletRequest request = containerRequest.getHttpServletRequest();
        String resourceURI = request.getRequestURI();
        resources.put(resourceURI, resourceCacheEntry);
        HttpServletResponse response = containerResponse.getHttpServletResponse();
        updateResponseHeaders(resourceCacheEntry, response);
    }

    /**
     * This method checks the HTTP Request headers to see if the cache has expired. It also removes 
     * the <CODE>ResourceCacheEntry</CODE> from its cache, if VALIDATION cache is not being used.
     *
     * @param containerRequest The <CODE>ContainerRequest</CODE> object which contains the original <CODE>HttpServletRequest</CODE>
     * @param containerResponse The <CODE>ContainerResponse</CODE> object which contains the original <CODE>HttpServletResponse</CODE> whose headers are updated
     * @return <CODE>boolean</CODE> indicating whether the cache in the Browser has expired.
     */   
    public boolean hasResourceCacheExpired(ContainerRequest containerRequest, 
            ContainerResponse containerResponse) {
        HttpServletRequest request = containerRequest.getHttpServletRequest();
        long now = Calendar.getInstance().getTimeInMillis();
        long header = request.getDateHeader("If-Modified-Since");
        String eTag = request.getHeader("If-None-Match");
        if(header > 0) {
            if(header > now) {
                HttpServletResponse response = containerResponse.getHttpServletResponse();
                updateResponseHeaders(getCachedResource(containerRequest), response);
                response.setStatus(response.SC_NOT_MODIFIED);
                return false;
            } else {
                if(eTag == null) {
                    removeCachedResource(containerRequest);
                }
                return true;
            }
        } else {
            removeCachedResource(containerRequest);
            return true;
        }
    }

    /**
     * This method returns the <CODE>ResourceCacheEntry</CODE> object which is stored in the cache, 
     * corresponding to the containerRequest uri in the <CODE>ContainerRequest</CODE> argument.
     * 
     * @param containerRequest The <CODE>ContainerRequest</CODE> object which contains the original <CODE>HttpServletRequest</CODE>
     * @return <CODE>ResourceCacheEntry</CODE> stored in the cache or <CODE>null</CODE> if not present
     */    
    public ResourceCacheEntry getCachedResource(ContainerRequest containerRequest) {
        String uri = containerRequest.getHttpServletRequest().getRequestURI();
        ConcurrentMap<String, ResourceCacheEntry> resources = getPublicResourceMap(containerRequest);
        if(resources.containsKey(uri)) {
            return resources.get(uri);
        } else {
            return getPrivateResourceMap(containerRequest).get(uri);
        }
    }

    /**
     * This method removes the <CODE>ResourceCacheEntry</CODE> object from the cache. This is 
     * typically called when the cache in the Browser has expired and VALIDATION caching is not 
     * being used.
     * 
     * @param containerRequest The <CODE>ContainerRequest</CODE> object which contains the original <CODE>HttpServletRequest</CODE>
     */    
    public void removeCachedResource(ContainerRequest containerRequest) {
        ConcurrentMap<String, ResourceCacheEntry> resources = getPublicResourceMap(containerRequest);
        String uri = containerRequest.getHttpServletRequest().getRequestURI();
        if(resources.containsKey(uri)) {
            resources.remove(uri);
        } else {
            getPrivateResourceMap(containerRequest).remove(uri);
        }
    }

    public String getName() {
        return CLIENT_CACHING_SERVICE;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    private ConcurrentMap<String, ResourceCacheEntry> getPrivateResourceMap(ContainerRequest containerRequest) {
        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        ConcurrentMap<String, ResourceCacheEntry> cachedResources = 
                (ConcurrentMap<String, ResourceCacheEntry>) portletWindowContext.getProperty(CACHED_RESOURCES);
        if (cachedResources == null) {
            cachedResources = new ConcurrentHashMap<String, ResourceCacheEntry>();
            portletWindowContext.setProperty(CACHED_RESOURCES, cachedResources);
        }
        return cachedResources;        
    }
    
    private ConcurrentMap<String, ResourceCacheEntry> getPublicResourceMap(ContainerRequest containerRequest) {   
        ServletContext context = getServletContext();
        ConcurrentMap<String, ResourceCacheEntry> cachedResources = (ConcurrentMap<String, ResourceCacheEntry>) context.getAttribute(CACHED_RESOURCES);
        if (cachedResources == null) {
            cachedResources = new ConcurrentHashMap<String, ResourceCacheEntry>();
            context.setAttribute(CACHED_RESOURCES, cachedResources);
        }
        return cachedResources;                
    }

    /**
     * This updates the cache with the <code>ResourceCacheEntry</code> provided. It also ensures 
     * that the HTTP Response status is set to 304 (NOT MODIFIED) so that the cache in the browser 
     * is reused.
     * 
     * 
     * @param containerRequest The <CODE>ContainerRequest</CODE> object which contains the original <CODE>HttpServletRequest</CODE>
     * @param containerResponse The <CODE>ContainerResponse</CODE> object which contains the original <CODE>HttpServletResponse</CODE> whose headers are updated
     * @param resourceCacheEntry The <CODE>ResourceCacheEntry</CODE> object which is stored in the cache
     * @param isPublic The <CODE>boolean</CODE> which indicates whether the caching scope is public or not
     */    
    public void updateAndReuseResourceCache(ContainerRequest containerRequest, ContainerResponse containerResponse, ResourceCacheEntry resourceCacheEntry, boolean isPublic) {
        updateResourceCache(containerRequest, containerResponse, resourceCacheEntry, isPublic);
        HttpServletResponse response = containerResponse.getHttpServletResponse();
        response.setStatus(response.SC_NOT_MODIFIED);
        return;
    }

    private void updateResponseHeaders(ResourceCacheEntry resourceCacheEntry, HttpServletResponse response) {
        int expirationTime = resourceCacheEntry.getExpirationTime();
        String eTag = resourceCacheEntry.getETag();
        long now = Calendar.getInstance().getTimeInMillis();

        if (response.containsHeader("Cache-Control")) {
            response.setHeader("Cache-Control", "public, max-age=" + expirationTime + ", must-revalidate");
        } else {
            response.addHeader("Cache-Control", "public, max-age=" + expirationTime + ", must-revalidate");
        }
        if (response.containsHeader("Expires")) {
            response.setDateHeader("Expires", now + expirationTime * TIME_PRODUCT);
        } else {
            response.addDateHeader("Expires", now + expirationTime * TIME_PRODUCT);
        }
        if (response.containsHeader("Last-Modified")) {
            response.setDateHeader("Last-Modified", now + expirationTime * TIME_PRODUCT);
        } else {
            response.addDateHeader("Last-Modified", now + expirationTime * TIME_PRODUCT);
        }
        if (response.containsHeader("Pragma")) {
            response.setHeader("Pragma", null);
        }
        if (eTag != null) {
            response.addHeader("ETag", eTag);
        }
    }
}
