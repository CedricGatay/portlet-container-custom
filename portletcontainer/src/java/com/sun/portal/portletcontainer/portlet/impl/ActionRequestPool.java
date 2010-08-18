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
import javax.portlet.ActionRequest;
import javax.portlet.PortletContext;
import javax.portlet.PortalContext;
import java.util.logging.Logger;

import com.sun.portal.portletcontainer.common.PortletContainerActionRequest;
import com.sun.portal.portletcontainer.common.PortletContainerActionResponse;
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
public class ActionRequestPool extends ObjectPool {
    
    private static Logger logger = ContainerLogger.getLogger(ActionRequestPool.class, "PAELogMessages");

    
    private static class ActionRequestPoolManager implements ObjectManager {
        
        // constructor
        public ActionRequestPoolManager(ServletContext context) {
        }
        
        /**
         * Creates a new object.
         * <P>
         * @param param Passed in params if needed.
         */
        public Object createObject(Object param) {
            ActionRequestImpl actionRequest = new ActionRequestImpl();
            logger.finest("PSPL_PAECSPPI0001");
            
            return actionRequest;
        }
        
        /**
         * Destroys an object.
         */
        public void destroyObject(Object o) {
            //do nothing
        }
    }
    
    // constructor
    public ActionRequestPool( ServletContext context, int minSize, int maxSize, boolean overflow, int partitionSize) {
        
        super(new ActionRequestPoolManager(context), minSize, maxSize, overflow, partitionSize );
    }
    
    
    /**
     * Obtains an action request object from the pool.
     * <P>
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcActionRequest     The <code>PortletContainerActionRequest</code>
     * @param pcActionResponse     The <code>PortletContainerActionResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * <P>
     * @return <code>ActionRequest</code>
     */
    public ActionRequest obtainObject(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerActionRequest pcActionRequest,
            PortletContainerActionResponse pcActionResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor) {
        
        logger.finest("PSPL_PAECSPPI0002");
        
        ActionRequestImpl actionRequest = (ActionRequestImpl)(getPool().obtainObject(null));
        actionRequest.init(request, response, pcActionRequest, pcActionResponse, 
                portletContext, portalContext, portletDescriptor);
        return actionRequest;
    }
    
    /**
     * Releases an object back to the pool.
     *
     * @param <code>ActionRequest</code>
     */
    public void releaseObject(ActionRequest actionRequest) {
        ((ActionRequestImpl)actionRequest).clear();
        getPool().releaseObject(actionRequest);
        
        logger.finest("PSPL_PAECSPPI0003");
    }
}
