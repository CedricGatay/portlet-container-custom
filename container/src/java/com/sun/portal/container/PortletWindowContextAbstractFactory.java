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

package com.sun.portal.container;

import com.sun.portal.container.service.ServiceFinder;

/**
 * PortletWindowContextAbstractFactory is responsible to creating the PortletWindowContextFactory
 * object.
 */
public class PortletWindowContextAbstractFactory {
    
    private PortletWindowContextFactory portletWindowContextFactory;
    public PortletWindowContextAbstractFactory() {
    }
    
    /**
     * Returns the PortletWindowContextFactory implementation. The implementation is found using the
     * following methodology..
     * Check for entry within META-INF/services/com.sun.portal.container.PortletWindowContextAbstractFactory,
     *   if found use the value which will be the implementation of the PortletWindowContextFactory interface
     *
     * @throws com.sun.portal.container.PortletWindowContextException 
     * @return the PortletWindowContextFactory implementation
     */
    public PortletWindowContextFactory getPortletWindowContextFactory() throws PortletWindowContextException {
        if(portletWindowContextFactory == null) {
            Object instance = null;
            try {
                instance = ServiceFinder.getServiceImplementationInstance(PortletWindowContextAbstractFactory.class.getName());
            } catch (Exception ex) {
                throw new PortletWindowContextException(ex);
            }
            portletWindowContextFactory = (PortletWindowContextFactory)instance;
        }
        return portletWindowContextFactory;
    }
}
