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

import javax.servlet.http.HttpServletRequest;

/**
 * PortletWindowContextFactory is responsible to creating the PortletWindowContext
 * object.
 */
public interface PortletWindowContextFactory {

    /**
     * Returns PortletWindowContext implementation
     *
     * @param request the HttpServletRequest object
     *
     * @throws com.sun.portal.container.PortletWindowContextException if error occurs while creating the Object
     *
     * @return the PortletWindowContext implementation
     */
    public PortletWindowContext getPortletWindowContext(HttpServletRequest request) 
    throws PortletWindowContextException ;

    /**
     * Returns PortletWindowContext implementation for a user
     * 
     * @param request the HttpServletRequest object
     * @param userID the user Id
     * @return the PortletWindowContext implementation for a user
     * @throws com.sun.portal.container.PortletWindowContextException if error occurs while creating the Object
     */
    public PortletWindowContext getPortletWindowContext(HttpServletRequest request, String userID) 
    throws PortletWindowContextException ;
}
