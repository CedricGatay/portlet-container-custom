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
import java.io.StringWriter;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.logging.Logger;

import com.sun.portal.portletcontainer.common.PortletContainerRenderRequest;
import com.sun.portal.portletcontainer.common.PortletContainerRenderResponse;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectManager;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectPool;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;

/**
 * This class maintains reusable portlet response objects in a pool.
 * <P>
 * Clients can use the <code>obtainObject()</code> method to retrieve an
 * portlet response object, and <code>releaseObject()</code> to release
 * it back to the pool once is done with it.
 **/
public class RenderResponsePool extends ObjectPool {
    
    private static Logger logger = ContainerLogger.getLogger(RenderResponsePool.class, "PAELogMessages");
    
    private static class RenderResponsePoolManager implements ObjectManager {
        
        public RenderResponsePoolManager() {
        }
        
        /**
         * Creates a new object.
         * <P>
         * @param param Passed in params if needed.
         */
        public Object createObject(Object param) {
            RenderResponseImpl renderResponse = new RenderResponseImpl();
            
            logger.finest("PSPL_PAECSPPI0010");
            
            return renderResponse;
        }
        
        /**
         * Destroys an object.
         */
        public void destroyObject(Object o) {
            //do nothing
        }
    }
    
    // constructor
    public RenderResponsePool(int minSize, int maxSize, boolean overflow, int partitionSize) {
        
        super( new RenderResponsePoolManager(), minSize, maxSize, overflow, partitionSize );
    }
    
    /**
     * Obtains an portlet response object from the pool.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcRenderRequest     The <code>PortletContainerRenderRequest</code>
     * @param pcRenderResponse     The <code>PortletContainerRenderResponse</code>
     * @param renderRequest     The <code>RenderRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * @param stringWriter     The <code>StringWriter</code>
     * @param outputStream     The <code>StringServletOutputStream</code>
     * <P>
     * @return <code>RenderResponse</code>
     */
    public RenderResponse obtainObject(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerRenderRequest pcRenderRequest,
            PortletContainerRenderResponse pcRenderResponse,
            RenderRequest renderRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor,
            StringWriter stringWriter,
            StringServletOutputStream outputStream) {
        
        logger.finest("PSPL_PAECSPPI0011");
        
        RenderResponseImpl renderResponse = (RenderResponseImpl)(getPool().obtainObject(null));
        renderResponse.init(request, response, pcRenderRequest, pcRenderResponse,
                renderRequest, portletAppDescriptor, portletDescriptor, stringWriter, outputStream);
        return renderResponse;
    }
    
    /**
     * Releases an object back to the pool.
     *
     * @param <code>RenderResponse</code>
     */
    public void releaseObject(RenderResponse renderResponse) {
        ((RenderResponseImpl)renderResponse).clear();
        getPool().releaseObject(renderResponse);
        logger.finest("PSPL_PAECSPPI0012");
    }
}
