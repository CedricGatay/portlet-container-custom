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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.PortletContext;
import javax.portlet.PortalContext;
import java.util.logging.Logger;

import com.sun.portal.portletcontainer.common.PortletContainerRenderRequest;
import com.sun.portal.portletcontainer.common.PortletContainerRenderResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectManager;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectPool;

/**
 * This class maintains reusable portlet request objects in a pool.
 * <P>
 * Clients can use the <code>obtainObject()</code> method to retrieve an
 * portlet request object, and <code>releaseObject()</code> to release
 * it back to the pool once is done with it.
 **/
public class RenderRequestPool extends ObjectPool {
    
    private static Logger logger = ContainerLogger.getLogger(RenderRequestPool.class, "PAELogMessages");

    private static class RenderRequestPoolManager implements ObjectManager {

        // constructor
        public RenderRequestPoolManager(ServletContext context) {
        }
        
        /**
         * Creates a new object.
         * <P>
         * @param param Passed in params if needed.
         */
        public Object createObject(Object param) {
            RenderRequestImpl renderRequest = new RenderRequestImpl();
            
            logger.finest("PSPL_PAECSPPI0007");
            
            return renderRequest;
        }
        
        /**
         * Destroys an object.
         */
        public void destroyObject(Object o) {
            //do nothing
        }
    }
    
    // constructor
    public RenderRequestPool( ServletContext context, int minSize, int maxSize, boolean overflow, int partitionSize) {
        
        super(new RenderRequestPoolManager(context), minSize, maxSize, overflow, partitionSize );
    }
    
    
    /**
     * Obtains an render request object from the pool.
     * <P>
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcRenderRequest     The <code>PortletContainerRenderRequest</code>
     * @param pcRenderResponse     The <code>PortletContainerRenderResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * <P>
     * @return <code>RenderRequest</code>
     */
    public RenderRequest obtainObject(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerRenderRequest pcRenderRequest,
            PortletContainerRenderResponse pcRenderResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor ) {
        
        logger.finest("PSPL_PAECSPPI0008");
        RenderRequestImpl renderRequest = (RenderRequestImpl)(getPool().obtainObject(null));
        renderRequest.init(request, response, pcRenderRequest, pcRenderResponse, 
                portletContext, portalContext, portletDescriptor);
        
        return (RenderRequestImpl)renderRequest;
    }
    
    /**
     * Releases an object back to the pool.
     *
     * @param <code>RenderRequest</code>
     */
    public void releaseObject(RenderRequest renderRequest) {
        ((RenderRequestImpl)renderRequest).clear();
        getPool().releaseObject(renderRequest);
        
        logger.finest("PSPL_PAECSPPI0009");
    }
}
