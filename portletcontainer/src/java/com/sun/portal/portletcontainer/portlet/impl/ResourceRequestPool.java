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

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectManager;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectPool;
import com.sun.portal.portletcontainer.common.PortletContainerResourceRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResourceResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.util.logging.Logger;
import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.portlet.ResourceRequest;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class maintains reusable resource request objects in a pool.
 * <P>
 * Clients can use the <code>obtainObject()</code> method to retrieve an
 * resource request object, and <code>releaseObject()</code> to release
 * it back to the pool once is done with it.
 **/
public class ResourceRequestPool extends ObjectPool {

    private static Logger logger = ContainerLogger.getLogger(ResourceRequestPool.class, "PAELogMessages");

    private static class ResourceRequestPoolManager implements ObjectManager {

        // constructor
        public ResourceRequestPoolManager(ServletContext context) {
        }

        /**
         * Creates a new object.
         * <P>
         * @param param Passed in params if needed.
         */
        public Object createObject(Object param) {
            ResourceRequestImpl resourceRequest = new ResourceRequestImpl();

            logger.finest("PSPL_PAECSPPI0024");
            return resourceRequest;
        }

        /**
         * Destroys an object.
         */
        public void destroyObject(Object o) {
            //do nothing
        }
    }

    // constructor
    public ResourceRequestPool(ServletContext context, int minSize, int maxSize, boolean overflow, int partitionSize) {

        super(new ResourceRequestPoolManager(context), minSize, maxSize, overflow, partitionSize);
    }


    /**
     * Obtains an resource request object from the pool.
     * <P>
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcResourceRequest     The <code>PortletContainerResourceRequest</code>
     * @param pcResourceResponse    The <code>PortletContainerResourceResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * <P>
     * @return <code>ResourceRequest</code>
     */
    public ResourceRequest obtainObject(HttpServletRequest request, 
            HttpServletResponse response, 
            PortletContainerResourceRequest pcResourceRequest, 
            PortletContainerResourceResponse pcResourceResponse, 
            PortletContext portletContext, 
            PortalContext portalContext, 
            PortletDescriptor portletDescriptor) {

        logger.finest("PSPL_PAECSPPI0025");
        ResourceRequestImpl resourceRequest = (ResourceRequestImpl) (getPool().obtainObject(null));
        resourceRequest.init(request, response, pcResourceRequest, pcResourceResponse, portletContext, portalContext, portletDescriptor);

        return resourceRequest;
    }

    /**
     * Releases an object back to the pool.
     *
     * @param <code>ResourceRequest</code>
     */
    public void releaseObject(ResourceRequest resourceRequest) {
        ((ResourceRequestImpl) resourceRequest).clear();
        getPool().releaseObject(resourceRequest);

        logger.finest("PSPL_PAECSPPI0026");
    }
}
