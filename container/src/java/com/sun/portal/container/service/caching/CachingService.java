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
import com.sun.portal.container.service.Service;

/**
 * The <code>CachingService</code> is responsible for managing the cache contents
 * for the portlets. The operations on the cached portlet contents 
 * include adding, retrieving, updating, and removing the cache 
 * entries to/from the cache repository.
 * <p>
 * The portlet specification defines two basic portlet content 
 * caching mechanisms: EXPIRATION cache and VALIDATING cache.
 * Regardless of which mechanism gets used, the cache (mechanism) 
 * is per portlet entity per user session.  The cached data should 
 * not be shared across different users of the same portlet entity. 
 * <p>
 * <code>CachingService</code> is an interface that is used by the 
 * portlet container.
 *
 */
public interface CachingService extends Service {

    /**
     * Returns true if Caching is enabled.
	 * The default implementation enables caching only for authenticated
	 * users.
     *
     * @param containerRequest The <code>ContainerRequest</code> object that
     *        encapsulates the portlet entity ID
     *
     * @return true if the caching is enabled
     */
	public boolean isCachingEnabled(ContainerRequest containerRequest);
	
    /**
     * Returns the <code>PortletCacheEntry</code> for the portlet of
     * the specified portlet entity ID encapsulated in the <code>
     * ContainerRequest</code>.
     * <p>
     * This method returns <code>null</code> in the following two 
     * situations: 
     * <p>
     * <ul>
     *     <li> no cache entry is found for this portlet entity
     *     <li> if the cache type is EXPIRATION and the cached data
     *          has already expired
     * </ul>
     *
     * @param containerRequest The <code>ContainerRequest</code> object that
     *        encapsulates the portlet entity ID
     * 
     * @return the PortletCacheEntry for the portlet entity ID encapsulated in the containerRequest
     */
    public PortletCacheEntry getCachedPortlet(ContainerRequest containerRequest);

    /**
     * Called by the portlet container to add or update the cache
     * entry for the protlet of the specified entity ID encapsulated 
     * in the <code>ContainerRequest</code>.
     * <p>
     * @param containerRequest The <code>ContainerRequest</code> object that
     *        encapsulates the portlet entity ID
     * @param entry The <code>PortletCacheEntry</code> object 
     * @param isPublic The <code>boolean</code> value which indicates 
     *        if this is to be shared with other users or not
     *
     */
    public void putCachedPortlet(ContainerRequest containerRequest,
				 PortletCacheEntry entry, boolean isPublic);

    /**
     * Called by the portlet container to remove the cache entry
     * for the protlet of the specified entity ID encapsulated 
     * in the <code>ContainerRequest</code>.
     * <p>
     * @param containerRequest The <code>ContainerRequest</code> object that
     *        encapsulates the portlet entity ID
     *
     */
    public void removeCachedPortlet(ContainerRequest containerRequest);

    /**
     * Called by the portlet container to remove all the cache 
     * entries in the user session.
     * <p>
     * @param containerRequest The <code>ContainerRequest</code> object that
     *        encapsulates the information of the cache repository
     *        in the user session.
     *
     */
    public void removeAllCachedPortlets(ContainerRequest containerRequest);

    /**
     * Return if the cached entry for the portlet with EXPIRATION
     * cache type already expired. 
     * <P>
     * @param containerRequest The <code>ContainerRequest</code> object that
     *        encapsulates the portlet entity ID
     * <p>
     * @return true, if the cached entry for the portlet with 
     * EXPIRATION cache type has already expired
     *<p>
     * @throws java.lang.IllegalStateException if the cache
     *            type is not EXPIRATION.
     * @throws com.sun.portal.container.service.caching.NoSuchCachedContentException 
     *            if the cache entry for the portlet entity is 
     *            not found.
     *
     **/
    public boolean isCacheExpired(ContainerRequest containerRequest)
	throws IllegalStateException, NoSuchCachedContentException;
}
