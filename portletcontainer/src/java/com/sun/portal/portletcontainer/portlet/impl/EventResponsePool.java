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
import com.sun.portal.portletcontainer.common.PortletContainerEventRequest;
import com.sun.portal.portletcontainer.common.PortletContainerEventResponse;
import java.util.logging.Logger;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectManager;
import com.sun.portal.portletcontainer.appengine.util.pool.ObjectPool;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventResponsePool extends ObjectPool {
    
    private static Logger logger = ContainerLogger.getLogger(EventResponsePool.class, "PAELogMessages");

    // constructor
    private static class EventResponsePoolManager implements ObjectManager {
        
        public EventResponsePoolManager() {
        }
        
        /**
         * Creates a new object.
         * <P>
         * @param param Passed in params if needed.
         */
        public Object createObject(Object param) {
            EventResponse eventResponse = new EventResponseImpl();
            
            logger.finest("PSPL_PAECSPPI0019");
            
            return eventResponse;
        }
        
        /**
         * Destroys an object.
         */
        public void destroyObject(Object o) {
            //do nothing
        }
    }
    
    // constructor
    public EventResponsePool( int minSize, int maxSize, boolean overflow, int partitionSize) {
        
        super( new EventResponsePoolManager(), minSize, maxSize, overflow, partitionSize );
    }
    
    /**
     * Obtains an event response object from the pool.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcEventRequest     The <code>PortletContainerEventRequest</code>
     * @param pcEventResponse     The <code>PortletContainerEventResponse</code>
     * @param eventRequest     The <code>EventRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * <P>
     * @return <code>EventResponse</code>
     */
    public EventResponse obtainObject(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerEventRequest pcEventRequest,             
            PortletContainerEventResponse pcEventResponse, 
            EventRequest eventRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor) {
        
        logger.finest("PSPL_PAECSPPI0020");
        EventResponseImpl eventResponse = (EventResponseImpl)(getPool().obtainObject(null));
        eventResponse.init(request, response, pcEventRequest, 
                pcEventResponse, eventRequest, portletAppDescriptor, portletDescriptor);
        return eventResponse;
    }
    
    /**
     * Releases an object back to the pool.
     *
     * @param <code>ActionResponse</code>
     */
    public void releaseObject(EventResponse eventResponse) {
        ((EventResponseImpl)eventResponse).clear();
        getPool().releaseObject(eventResponse);
        
        logger.finest("PSPL_PAECSPPI0021");
    }
    
}
