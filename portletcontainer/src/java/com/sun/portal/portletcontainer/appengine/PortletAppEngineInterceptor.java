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

package com.sun.portal.portletcontainer.appengine;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * PortletAppEngineInterceptor provides a way to plugin processing logic
 * before and after the Portlet App Engine Servlet invokes the Portlet.
 */
public interface PortletAppEngineInterceptor {
  
    /**
     * Initializes PortletAppEngineInterceptor object.
     * 
     * @param context The Servlet Context object
     */
  public void init(ServletContext context);
  
  /**
     * Processes the request before the Portlet is invoked.
     * @param request The HttpServletRequest Object
     * @param response The HttpServletResponse Object
     */
  public void beforeInvoke(HttpServletRequest request, HttpServletResponse response);
  
  /**
     * Processes the request after the Portlet is invoked.
     * @param request The HttpServletRequest Object
     * @param response The HttpServletResponse Object
     */
  public void afterInvoke(HttpServletRequest request, HttpServletResponse response);
  
    /**
     * Destroys PortletAppEngineInterceptor object.
     */
  public void destroy() ;
} 