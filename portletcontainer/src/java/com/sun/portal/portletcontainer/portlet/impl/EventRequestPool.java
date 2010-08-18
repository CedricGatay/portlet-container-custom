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
import javax.portlet.PortletContext;
import javax.portlet.PortalContext;
import java.util.logging.Logger;
import com.sun.portal.portletcontainer.common.PortletContainerEventRequest;
import com.sun.portal.portletcontainer.common.PortletContainerEventResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectManager;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectPool;
import javax.portlet.EventRequest;

public class EventRequestPool extends ObjectPool{
    
    private static Logger logger = ContainerLogger.getLogger(EventRequestPool.class, "PAELogMessages");

    private static class EventRequestPoolManager implements ObjectManager {
        
        // constructor
        public EventRequestPoolManager(ServletContext context) {
        }
        
        /**
         * Creates a new object.
         * <P>
         * @param param Passed in params if needed.
         */
        public Object createObject(Object param) {
            EventRequestImpl eventRequest = new EventRequestImpl();
            
            logger.finest("PSPL_PAECSPPI0016");
            
            return eventRequest;
        }
        
        /**
         * Destroys an object.
         */
        public void destroyObject(Object o) {
            //do nothing
        }
    }
    
    // constructor
    public EventRequestPool( ServletContext context, int minSize, int maxSize, boolean overflow, int partitionSize) {
        
        super(new EventRequestPoolManager(context), minSize, maxSize, overflow, partitionSize );
    }
    
    
    /**
     * Obtains an event request object from the pool.
     * <P>
     *
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcEventRequest     The <code>PortletContainerEventRequest</code>
     * @param pcEventResponse     The <code>PortletContainerEventResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * <P>
     * @return <code>EventRequest</code>
     */
    public EventRequest obtainObject(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerEventRequest pcEventRequest,
            PortletContainerEventResponse pcEventResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor) {
        
        logger.finest("PSPL_PAECSPPI0017");
        
        EventRequestImpl eventRequest = (EventRequestImpl)(getPool().obtainObject(null));
        eventRequest.init(request, response, pcEventRequest, pcEventResponse, 
                portletContext,portalContext,portletDescriptor);
        return (EventRequestImpl)eventRequest;
    }
    
    /**
     * Releases an object back to the pool.
     *
     * @param <code>ActionRequest</code>
     */
    public void releaseObject(EventRequest eventRequest) {
        ((EventRequestImpl)eventRequest).clear();
        getPool().releaseObject(eventRequest);
        
        logger.finest("PSPL_PAECSPPI0018");
    }
    
}