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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.util.logging.Logger;

import com.sun.portal.portletcontainer.common.PortletContainerActionRequest;
import com.sun.portal.portletcontainer.common.PortletContainerActionResponse;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectManager;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectPool;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;

/**
 * This class maintains reusable action response objects in a pool.
 * <P>
 * Clients can use the <code>obtainObject()</code> method to retrieve an
 * action response object, and <code>releaseObject()</code> to release
 * it back to the pool once is done with it.
 **/
public class ActionResponsePool extends ObjectPool {
    
    private static Logger logger = ContainerLogger.getLogger(ActionResponsePool.class, "PAELogMessages");
    
    // constructor
    private static class ActionResponsePoolManager implements ObjectManager {
        
        
        public ActionResponsePoolManager() {
        }
        
        /**
         * Creates a new object.
         * <P>
         * @param param Passed in params if needed.
         */
        public Object createObject(Object param) {
            ActionResponse actionResponse = new ActionResponseImpl();
            
            logger.finest("PSPL_PAECSPPI0004");
            
            return actionResponse;
        }
        
        /**
         * Destroys an object.
         */
        public void destroyObject(Object o) {
            //do nothing
        }
    }
    
    // constructor
    public ActionResponsePool( int minSize, int maxSize, boolean overflow, int partitionSize) {
        
        super( new ActionResponsePoolManager(), minSize, maxSize, overflow, partitionSize );
    }
    
    /**
     * Obtains an action response object from the pool.
     * 
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcActionRequest     The <code>PortletContainerActionRequest</code>
     * @param pcActionResponse     The <code>PortletContainerActionResponse</code>
     * @param actionRequest     The <code>ActionRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * <P>
     * @return <code>ActionResponse</code>
     */
    public ActionResponse obtainObject(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerActionRequest pcActionRequest,             
            PortletContainerActionResponse pcActionResponse,             
            ActionRequest actionRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor) {
        
        logger.finest("PSPL_PAECSPPI0005");
        ActionResponseImpl actionResponse = (ActionResponseImpl)(getPool().obtainObject(null));
        actionResponse.init(request, response, pcActionRequest, 
                pcActionResponse, actionRequest, portletAppDescriptor, portletDescriptor);
        return actionResponse;
    }
    
    /**
     * Releases an object back to the pool.
     *
     * @param <code>ActionResponse</code>
     */
    public void releaseObject(ActionResponse actionResponse) {
        ((ActionResponseImpl)actionResponse).clear();
        getPool().releaseObject(actionResponse);
        
        logger.finest("PSPL_PAECSPPI0006");
    }
}
