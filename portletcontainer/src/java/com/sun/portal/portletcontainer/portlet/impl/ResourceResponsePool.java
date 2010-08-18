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
import com.sun.portal.portletcontainer.appengine.StringServletOutputStream;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectManager;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectPool;
import com.sun.portal.portletcontainer.common.PortletContainerResourceRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResourceResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.logging.Logger;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class maintains reusable portlet response objects in a pool.
 * <P>
 * Clients can use the <code>obtainObject()</code> method to retrieve an
 * portlet response object, and <code>releaseObject()</code> to release
 * it back to the pool once is done with it.
 **/
public class ResourceResponsePool extends ObjectPool{
 private static Logger logger = ContainerLogger.getLogger(ResourceResponsePool.class, "PAELogMessages");
    
    private static class ResourceResponsePoolManager implements ObjectManager {
        
        public ResourceResponsePoolManager() {
        }
        
        /**
         * Creates a new object.
         * <P>
         * @param param Passed in params if needed.
         */
        public Object createObject(Object param) {
            ResourceResponseImpl resourceResponse = new ResourceResponseImpl();
            
            logger.finest("PSPL_PAECSPPI0027");
            
            return resourceResponse;
        }
        
        /**
         * Destroys an object.
         */
        public void destroyObject(Object o) {
            //do nothing
        }
    }
    
    // constructor
    public ResourceResponsePool(int minSize, int maxSize, boolean overflow, int partitionSize) {
        
        super( new ResourceResponsePoolManager(), minSize, maxSize, overflow, partitionSize );
    }
    
    /**
     * Obtains an resource response object from the pool.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcResourceRequest     The <code>PortletContainerResourceRequest</code>
     * @param pcResourceResponse     The <code>PortletContainerResourceResponse</code>
     * @param resourceRequest     The <code>ResourceRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * @param stringWriter     The <code>StringWriter</code>
     * @param outputStream     The <code>StringServletOutputStream</code>
     * <P>
     * @return <code>ResourceResponse</code>
     */
    public ResourceResponse obtainObject(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerResourceRequest pcResourceRequest,
            PortletContainerResourceResponse pcResourceResponse,
            ResourceRequest resourceRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor,
            StringWriter stringWriter,
            StringServletOutputStream outputStream) {
        
           logger.finest("PSPL_PAECSPPI0028");
        
        ResourceResponseImpl resourceResponse = (ResourceResponseImpl)(getPool().obtainObject(null));
        resourceResponse.init(request, response, pcResourceRequest, pcResourceResponse, 
                resourceRequest, portletAppDescriptor, portletDescriptor, stringWriter, outputStream);
        return resourceResponse;
    }
    
    /**
     * Releases an object back to the pool.
     *
     * @param <code>ResourceResponse</code>
     */
    public void releaseObject(ResourceResponse resourceResponse) {
        ((ResourceResponseImpl)resourceResponse).clear();
        getPool().releaseObject(resourceResponse);
        logger.finest("PSPL_PAECSPPI0029");
    }
}
