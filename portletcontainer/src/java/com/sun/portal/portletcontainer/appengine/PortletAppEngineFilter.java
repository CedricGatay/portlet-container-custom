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

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.service.ServiceFinder;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The portlet app engine filter is a Servlet Filter for the
 * portlet application engine servlet.
 * <p/>
 * The portlet app engine filter is intended to process requests 
 * before and after the portlet app engine servlet is invoked. It invokes
 * PortletAppEngineInterceptor before and after the service method of 
 * the portlet app engine servlet.
 */
public class PortletAppEngineFilter implements Filter {

    private ServletContext context;
    private PortletAppEngineInterceptor portletAppEngineInterceptor;
    
    private static Logger logger = ContainerLogger.getLogger(PortletAppEngineFilter.class, "PAELogMessages");

    /**
     * Gets the implementation of PortletAppEngineInterceptor as follows.
     * Look for a resource called 
     * <code>/META-INF/services/com.sun.portal.portletcontainer.appengine.PortletAppEngineInterceptor</code>. 
     * If found, interpret it as a properties file, and read out the first 
     * entry. Interpret the first entry as a fully qualify class name of a 
     * class that implements <code>com.sun.portal.portletcontainer.appengine.PortletAppEngineInterceptor</code>
     * 
     * After getting the implementations, initializes them
     * 
     * @param config Filter Config Object
     * @throws javax.servlet.ServletException If any exception happens during initialization
     */
    public void init(FilterConfig config) throws ServletException {
        context = config.getServletContext();
        // Get the implementation of PortletAppEngineInterceptor using META-INF/services
        try {
            Object implObject = ServiceFinder.getServiceImplementationInstance(PortletAppEngineInterceptor.class.getName());
            if(implObject != null) {
                portletAppEngineInterceptor = (PortletAppEngineInterceptor)implObject;
                portletAppEngineInterceptor.init(context);
                logger.log(Level.FINE, "PSPL_PAECSPPA0017");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "PSPL_PAECSPPA0016", e);
        }
    }
    
    /**
     * Invokes the beforeInvoke and afterInvoke methods of PortletAppEngineInterceptor 
     * before and after calling the service method of PortletAppEngineServlet respectively. 
     * 
     * @param request The ServletRequest object
     * @param response The ServletResponse Object
     * @param chain The FilterChain Object
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;

        if(portletAppEngineInterceptor != null) {
            portletAppEngineInterceptor.beforeInvoke(httpServletRequest, httpServletResponse);
        }
        
        chain.doFilter(request, response);
        
        if(portletAppEngineInterceptor != null) {
            portletAppEngineInterceptor.afterInvoke(httpServletRequest, httpServletResponse);
        }
    }
    
    /**
     * Invokes the destroy method of PortletAppEngineInterceptor.
     */
    public void destroy() {
        if(portletAppEngineInterceptor != null) {
            portletAppEngineInterceptor.destroy();
        }
    }
    
}
