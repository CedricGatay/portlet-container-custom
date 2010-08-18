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

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletWindowContext;
import com.sun.portal.container.service.caching.CachingService;
import com.sun.portal.container.service.caching.NoSuchCachedContentException;
import com.sun.portal.container.service.caching.PortletCacheEntry;
import com.sun.portal.container.service.ServiceAdapter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 * The <code>CachingServiceImpl</code> class provides a default
 * implementation for the <code>CacheManager</code> interface. In the
 * initial implementation the only supported PortletState/PortletMode
 * is NORMAL/VIEW and the cached data contains only the portlet
 * content.
 */

public class CachingServiceImpl extends ServiceAdapter implements CachingService {

    private static Logger logger = ContainerLogger.getLogger(CachingServiceImpl.class, "CLogMessages");

    public static final String CACHED_PORTLETS = "javax.portlet.cache_portlets";

    private static final String DESCRIPTION = "Caches the portlet content";

	@Override
    public String getName() {
        return CACHING_SERVICE;
    }

	@Override
    public String getDescription() {
        return DESCRIPTION;
    }

	public boolean isCachingEnabled(ContainerRequest containerRequest) {
		boolean cachingEnabled;
		if(containerRequest.getUserID() == null) {
			cachingEnabled = false;
		} else {
			cachingEnabled = true;
		}
		return cachingEnabled;
	}
	
    public PortletCacheEntry getCachedPortlet(ContainerRequest containerRequest) {

        PortletCacheEntry cacheEntry = null;

        EntityID portletEntityID = containerRequest.getEntityID();
		ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>> privateCachedPortlets =
				getPrivateCachedPortletsMap(containerRequest);

		PortletCacheEntry pce = getPortletCacheEntry(containerRequest,
			privateCachedPortlets, portletEntityID);
		boolean isPublic = false;
		
        if (pce == null) {
			isPublic = true;
			ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>> publicCachedPortlets =
					getPublicCachedPortletsMap(containerRequest);

			pce = getPortletCacheEntry(containerRequest,
					publicCachedPortlets, portletEntityID);
        }

        if (pce != null) {
            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "PSC_CSPCS014", 
                        new Object[] {portletEntityID, Boolean.toString(isPublic)});
            }
            long now = System.currentTimeMillis();
            if (pce.getExpirationTime() > now) {
                pce.setNeedsValidation(false);
                cacheEntry = pce;
            } else {
                // Check if the ETag is present
                if (pce.getETag() != null) {
                    pce.setNeedsValidation(true);
                    cacheEntry = pce;
                } else {
                    // remove the cache entry since it's no longer
                    // valid
                    removeCachedPortlet(containerRequest);
                }
            }
			if(cacheEntry != null) {
				if(!containerRequest.getLocale().toString().equals(
						pce.getLocaleString())) {
					// remove and clear the cache entry as the locale has changed
					removeCachedPortlet(containerRequest);
					cacheEntry = null;
				}
			}
        }
        return cacheEntry;
    }

    public void putCachedPortlet(ContainerRequest containerRequest,
			PortletCacheEntry cacheEntry, boolean isPublic) {

        EntityID portletEntityID = containerRequest.getEntityID();
        /* If the cache is to be shared across users, update the map in the ServletContext.
         * Otherwiser update the cache in the session.
         */
        ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>> cachedPortlets = null;
        if (isPublic) {
            cachedPortlets = getPublicCachedPortletsMap(containerRequest);
        } else {
            cachedPortlets = getPrivateCachedPortletsMap(containerRequest);
        }
		ConcurrentMap<String,PortletCacheEntry> cacheEntries =
				cachedPortlets.get(portletEntityID);
		if(cacheEntries == null) {
            cacheEntries = new ConcurrentHashMap<String,PortletCacheEntry>();
		}
		cacheEntries.put(containerRequest.getLocale().toString(), cacheEntry);
        cachedPortlets.put(portletEntityID, cacheEntries);
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "PSC_CSPCS015",
                    new Object[]{portletEntityID, Boolean.toString(isPublic)});
        }
    }

    public void removeCachedPortlet(ContainerRequest containerRequest) {

        EntityID portletEntityID = containerRequest.getEntityID();
		ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>> privateCachedPortlets =
				getPrivateCachedPortletsMap(containerRequest);

		if (privateCachedPortlets.containsKey(portletEntityID)) {
            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "PSC_CSPCS016",
                        new Object[]{portletEntityID, "Private"});
            }
			ConcurrentMap<String,PortletCacheEntry> cacheEntries =
					privateCachedPortlets.get(portletEntityID);
			if(cacheEntries != null) {
				cacheEntries.remove(containerRequest.getLocale().toString());
			} else {
	            privateCachedPortlets.remove(portletEntityID);
			}
        } else {
			ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>> publicCachedPortlets =
					getPublicCachedPortletsMap(containerRequest);
            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "PSC_CSPCS016",
                        new Object[]{portletEntityID, "Public"});
            }
			ConcurrentMap<String,PortletCacheEntry> cacheEntries =
					publicCachedPortlets.get(portletEntityID);
			if(cacheEntries != null) {
				cacheEntries.remove(containerRequest.getLocale().toString());
			} else {
	            publicCachedPortlets.remove(portletEntityID);
			}
        }
    }

    public void removeAllCachedPortlets(ContainerRequest containerRequest) {
        getPublicCachedPortletsMap(containerRequest).clear();
        getPrivateCachedPortletsMap(containerRequest).clear();
    }

    public boolean isCacheExpired(ContainerRequest containerRequest) throws IllegalStateException, NoSuchCachedContentException {

        boolean cacheExpired = true;

        EntityID portletEntityID = containerRequest.getEntityID();
	    ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>> privateCachedPortlets =
				getPrivateCachedPortletsMap(containerRequest);
		PortletCacheEntry pce = getPortletCacheEntry(containerRequest,
				privateCachedPortlets, portletEntityID);

		if (pce == null) {
			ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>> publicCachedPortlets =
					getPublicCachedPortletsMap(containerRequest);

			pce = getPortletCacheEntry(containerRequest,
					publicCachedPortlets, portletEntityID);
        }

        if (pce != null) {
            if (pce.getCacheType() == PortletCacheType.TYPE_EXPIRATION) {
                long now = System.currentTimeMillis();
                if (pce.getExpirationTime() > now) {
                    cacheExpired = false;
                }
            }
        } else {
            // cached item is not found
            throw new NoSuchCachedContentException("Cache entry is not found. EntityID:" + portletEntityID);
        }
        return cacheExpired;
    }

    /**
     * Convenient method to retrieve the cache <code>ConcurrentMap<String,PortletCacheEntry></code> from the
     * encapsulated <code>PorviderContext</code> in the <code>
     * ContainerRequest</code> object.
     * <p>
     * 
     * @param containerRequest The <code>ContainerRequest</code> object that
     *        encapsulates the <code>PorviderContext</code> object
     * <p>
     * @return a ConcurrentMap<String,PortletCacheEntry> that contains all the cache entries for
     *         the user session
     */
    private ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>>
			getPrivateCachedPortletsMap(ContainerRequest containerRequest) {

        PortletWindowContext portletWindowContext = containerRequest.getPortletWindowContext();
        ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>> cachedPortlets =
                (ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>>)
				portletWindowContext.getProperty(CACHED_PORTLETS);

        if (cachedPortlets == null) {
            cachedPortlets = new ConcurrentHashMap<EntityID,ConcurrentMap<String,PortletCacheEntry>>();
            portletWindowContext.setProperty(CACHED_PORTLETS, cachedPortlets);
        }
        return cachedPortlets;
    }

    /**
     * convenient method to retrieve the cache ConcurrentMap<String,PortletCacheEntry> from the
     * encapsulated <code>PorviderContext</code> in the <code>
     * ContainerRequest</code> object.
     * <p>
     * 
     * @param containerRequest The <code>ContainerRequest</code> object that
     *        encapsulates the <code>PorviderContext</code> object
     * <p>
     * @return a ConcurrentMap<String,PortletCacheEntry> that contains all the cache entries
     */
    private ConcurrentMap<EntityID, ConcurrentMap<String,PortletCacheEntry>>
			getPublicCachedPortletsMap(ContainerRequest containerRequest) {

        ServletContext context = getServletContext();

        ConcurrentMap<EntityID, ConcurrentMap<String,PortletCacheEntry>> cachedPortlets =
				(ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>>)
				context.getAttribute(CACHED_PORTLETS);

        if (cachedPortlets == null) {
            cachedPortlets = new ConcurrentHashMap<EntityID,ConcurrentMap<String,PortletCacheEntry>>();
            context.setAttribute(CACHED_PORTLETS, cachedPortlets);
        }
        return cachedPortlets;
    }

	private PortletCacheEntry getPortletCacheEntry(ContainerRequest containerRequest,
			ConcurrentMap<EntityID,ConcurrentMap<String,PortletCacheEntry>> cachedPortlets,
			EntityID portletEntityID) {

		PortletCacheEntry pce = null;
		ConcurrentMap<String,PortletCacheEntry> cacheEntries =
				cachedPortlets.get(portletEntityID);
		if(cacheEntries != null) {
            pce = cacheEntries.get(containerRequest.getLocale().toString());
		}
		return pce;
	}
}
