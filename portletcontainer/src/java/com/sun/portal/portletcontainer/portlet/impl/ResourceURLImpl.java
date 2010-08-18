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

import javax.portlet.ResourceURL;
import javax.portlet.PortletRequest;
import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResourceRequest;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;

/**
 * This class implements ResourceURL interface.
 **/
public class ResourceURLImpl extends BaseURLImpl implements ResourceURL {

    private PortletContainerRequest pcRequest;
    public ResourceURLImpl(PortletRequest portletRequest, 
            PortletContainerRequest pcRequest,
            PortletAppDescriptor portletAppDescriptor, 
            PortletDescriptor portletDescriptor, 
            String action) {

        super.init(portletRequest, pcRequest, portletAppDescriptor, portletDescriptor, action);
        this.pcRequest = pcRequest;
    }

    /**
     * Allows setting a resource ID that can be retrieved when serving the
     * resource through the {@link ResourceRequest#getResourceID} method.
     *
     * @param resourceID
     *            ID for this resource URL
     */
    public void setResourceID(String resourceID) {
        //TODO Implementation required to check cacheability of parent URL in serveResource()
        getChannelURL().setResourceID(resourceID);
    }

    /**
     * Returns the cache level of this resource URL.
     * <p>
     * Possible return values are: <code>FULL, PORTLET</code>
     * or <code>PAGE</code>.
     *
     * @return  the cache level of this resource URL.
     */
    public String getCacheability() {
        return getChannelURL().getCacheLevel();
    }

    /**
     * Sets the cache level of this resource URL.
     * <p>
     * Possible values are: <code>FULL, PORTLET</code>
     * or <code>PAGE</code>.
     * <p>
     * Note that if this URL is created inside a
     * <code>serveResource</code> call it must have
     * at minimum the same cacheablity, or a more
     * restrictive one, as the parent resource URL,
     * otherwise an IllegalStateException is thrown.
     * <p>
     * The default cache level of a resource URL is either the cache level of the
     * parent resource URL, or <code>PAGE</code> if no parent resource URL is
     * available.
     *
     * @param cacheLevel  the cache level of this resource URL.
     * @throws java.lang.IllegalStateException
     * 			if this resource URL has a weaker cache level
     * 			than the parent resource URL.
     * @throws java.lang.IllegalArgumentException
     * 			if the cacheLevel is unknown to the portlet container
     */
    public void setCacheability(String cacheLevel) {
        
        Object lifeCyclePhase = getPortletRequest().getAttribute(PortletRequest.LIFECYCLE_PHASE);
        
        if(cacheLevel == null || (!cacheLevel.equals(ResourceURL.FULL) 
                                   && !cacheLevel.equals(ResourceURL.PAGE))
                                   && !cacheLevel.equals(ResourceURL.PORTLET)) {

            throw new IllegalArgumentException("Invalid cacheLevel.");
		}
        
        if(!lifeCyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {
            getChannelURL().setCacheLevel(cacheLevel);
            return;
        }
        
        String parentCacheLevel = ((PortletContainerResourceRequest)pcRequest).getCacheLevel();
        if(parentCacheLevel != null) {
            if(parentCacheLevel.equals(ResourceURL.FULL) 
					&& !cacheLevel.equals(ResourceURL.FULL)) {
                throw new IllegalStateException("Unable to set weaker cacheLevel than parent resourceURL");
			}
            else if(parentCacheLevel.equals(ResourceURL.PORTLET)
					&& !cacheLevel.equals(ResourceURL.FULL) &&
					!cacheLevel.equals(ResourceURL.PORTLET)) {
                throw new IllegalStateException("Unable to set weaker cacheLevel than parent resourceURL");
			}
        }
        getChannelURL().setCacheLevel(cacheLevel);   
    }
}
