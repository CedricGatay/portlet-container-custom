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

import java.io.IOException;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.portal.container.ContainerLogger;

/**
 * The <code>PortletRequestDispatcherImpl</code> class provides a default
 * implementation for the <code>javax.portlet.PortletRequestDispatcher</code>
 * interface.
 */

public class PortletRequestDispatcherImpl implements PortletRequestDispatcher {
    
    private RequestDispatcher servletRD;
    private boolean namedDispatcher = true;
    private ServletContext servletContext;

    private static Logger logger = ContainerLogger.getLogger(PortletRequestDispatcherImpl.class, "PAELogMessages");
            
    // Variables used to populate values as attributes
    private String queryString;
    private String servletPath;
    private String pathInfo;

    /**
     * Creates the portlet request dispatcher instance. 
     * This should be called to construct a named dispatcher.
     * 
     * @param servletContext the Servlet Context
     * @param servletRD  the servlet request dispatcher.
     */
    public PortletRequestDispatcherImpl(ServletContext servletContext, 
            RequestDispatcher servletRD) {
        this.servletRD = servletRD;
        this.servletContext = servletContext;
    }
    
    /**
     * Creates the portlet request dispatcher instance. 
     * 
     * @param servletContext the Servlet Context
     * @param servletRD  the servlet request dispatcher.
     * @param servletURLPatterns list of the servlet URL patterns
     * @param path   a <code>String</code> specifying the pathname
     *               to the resource
     */
    public PortletRequestDispatcherImpl(ServletContext servletContext, 
            RequestDispatcher servletRD, List<String> servletURLPatterns, String path) {

    	this(servletContext, servletRD);
    	namedDispatcher = false;
		
        if (path != null) {
            String pathWithoutQueryString = path;
            int questionIndex = path.indexOf("?");
            if(questionIndex != -1) {
                pathWithoutQueryString = path.substring(0, questionIndex);
                queryString = path.substring(questionIndex + 1, path.length());
			}

			if(servletURLPatterns != null) {
				int urlPatternIndex;
				for (String urlPattern : servletURLPatterns) {
					if (urlPattern.endsWith("/*")) {
						urlPatternIndex = urlPattern.indexOf("/*");
						urlPattern = urlPattern.substring(0, urlPatternIndex);

						if (pathWithoutQueryString.startsWith(urlPattern)) {
							pathInfo = pathWithoutQueryString.substring(urlPattern.length());
							servletPath = urlPattern;
							break;
						}
					}
				}
			}
            
			if ((pathInfo == null) && (servletPath == null)) {
				pathInfo = "";
				servletPath = pathWithoutQueryString;
            }
        }
    }

    /**
     * 
     * Includes the content of a resource (servlet, JSP page, HTML file) in the
     * response. In essence, this method enables programmatic server-side
     * includes.
     * <p>
     * The included servlet cannot set or change the response status code or set
     * headers; any attempt to make a change is ignored.
     * <p>
     * This method is kept in order to provide backward compatability with
     * version 1.0. Please use {@link #include(PortletRequest, PortletResponse)} instead
     * of this method.
     * 
     * @param request
     *            a {@link RenderRequest} object that contains the client
     *            request. Must be either the render request passed to the
     *            portlet in <code>render</code> or a wrapped version of this
     *            render request.
     * 
     * @param response
     *            a {@link RenderResponse} object that contains the render
     *            response. Must be either the render response passed to the
     *            portlet in <code>render</code> or a wrapped version of this
     *            render response.
     * 
     * @exception PortletException
     *                if the included resource throws a ServletException, or
     *                other exceptions that are not Runtime- or IOExceptions.
     * 
     * @exception java.io.IOException
     *                if the included resource throws this exception
     * 
     */

    public void include(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
		dispatch(request, response, true);
    }

    /**
     * 
     * Includes the content of a resource (servlet, JSP page, HTML file) in the
     * response. In essence, this method enables programmatic server-side
     * includes.
     * <p>
     * The included servlet cannot set or change the response status code or set
     * headers; any attempt to make a change is ignored.
     * 
     * 
     * @param request
     *            a {@link PortletRequest} object that contains the portlet
     *            request. Must be either the original request passed to the
     *            portlet or a wrapped version of this request.
     * 
     * @param response
     *            a {@link PortletResponse} object that contains the portlet
     *            response. Must be either the portlet response passed to the
     *            portlet or a wrapped version of this response.
     * 
     * @exception PortletException
     *                if the included resource throws a ServletException, or
     *                other exceptions that are not Runtime- or IOExceptions.
     * 
     * @exception java.io.IOException
     *                if the included resource throws this exception
     *                
     * @since 2.0
     */

    public void include(PortletRequest request, PortletResponse response)
            throws PortletException, IOException {
		dispatch(request, response, true);
    }

    /**
     * Forwards a portlet request from a portlet to another resource (servlet, JSP file, or HTML file) 
     * on the server. This method allows the portlet to do preliminary processing of a 
     * request and another resource to generate the response.
     * <p>
     * The <code>forward</code> method should be called before the response has been 
     * committed to the portlet container (before response body output has been flushed). 
     * If the response already has been committed, this method throws an 
     * <code>IllegalStateException</code>. Uncommitted output in the response buffer 
     * is automatically cleared before the forward.
     * <p>
     * The request and response parameters must be either the same objects as were passed to 
     * the calling portletor be wrapped versions of these.
     * 
     * @param request  a request object that represents the request to the 
     *                 portlet
     * @param response a reponse object that contains the portlet response
     *  
     * @exception PortletException
     *                if the included resource throws a ServletException, or
     *                other exceptions that are not Runtime- or IOExceptions.
     * @exception java.io.IOException
     *                if the included resource throws this exception
     * @exception java.lang.IllegalStateException
     *                if the response was already committed
     * @since 2.0
     */
    public void forward(PortletRequest request, PortletResponse response)
            throws PortletException, IOException, IllegalStateException {

		dispatch(request, response, false);
    }

    protected void dispatch(PortletRequest request, PortletResponse response, boolean include)
            throws PortletException, IOException {

        String lifecyclePhase = getLifecyclePhase(request);
        // create RequestWrapper and ResponseWrapper objects
        RDRequestWrapper rdRequestWrapper =
                getRDRequestWrapper(lifecyclePhase, request, include);
        RDResponseWrapper rdResponseWrapper =
                getRDResponseWrapper(lifecyclePhase, rdRequestWrapper, response, include);
        // invoke the corresponding servlet's request dispatcher
        if(rdRequestWrapper == null || rdResponseWrapper == null) {
            throw new NullPointerException("No request/response wrappers found");
        }

        setRequestResponseAttributes(rdRequestWrapper, request, response);

        try {
			if(include) {
	            servletRD.include(rdRequestWrapper, rdResponseWrapper);
			} else {
			    servletRD.forward(rdRequestWrapper, rdResponseWrapper);
			}
        } catch (ServletException se) {
            throw new PortletException("A ServletException was thrown in the target servlet or JSP.", se);
        }
    }

    private RDRequestWrapper getRDRequestWrapper(String lifecyclePhase,
		PortletRequest portletRequest, boolean include) {

		String contextPath = portletRequest.getContextPath();
		String requestURI = getRequestURI(contextPath, this.servletPath, this.pathInfo);

		logAttributes(requestURI, contextPath, servletPath, pathInfo, queryString);

		HttpServletRequest request = (HttpServletRequest)portletRequest.getAttribute(
                PortletRequestConstants.HTTP_SERVLET_REQUEST);

        RDRequestWrapper rdRequestWrapper =
			new RDRequestWrapper(this.servletContext, request,
				portletRequest, requestURI, this.servletPath,
				this.pathInfo, this.queryString, lifecyclePhase,
				include, this.namedDispatcher);

		return rdRequestWrapper;
    }

    private RDResponseWrapper getRDResponseWrapper(String lifecyclePhase,
		RDRequestWrapper rdRequestWrapper, PortletResponse portletResponse, boolean include) {

        HttpServletResponse response = (HttpServletResponse)rdRequestWrapper.getAttribute(
                PortletRequestConstants.HTTP_SERVLET_RESPONSE);

        RDResponseWrapper rdResponseWrapper =
			new RDResponseWrapper(response, portletResponse,
				lifecyclePhase, include);

		return rdResponseWrapper;
    }

    private String getLifecyclePhase(PortletRequest portletRequest) {
        return (String)portletRequest.getAttribute(PortletRequest.LIFECYCLE_PHASE);
    }
    
    private String getRequestURI(String contextPath, String servletPath, String pathInfo) {
        StringBuffer requestURI = new StringBuffer();
        requestURI.append(contextPath);
        if(servletPath != null) {
            requestURI.append(servletPath);
        }
        if(pathInfo != null) {
            requestURI.append(pathInfo);
        }
        return requestURI.toString();
    }

	private void logAttributes(String requestURI, String contextPath,
		String servletPath, String pathInfo, String queryString) {

		if(logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "PSPL_PAECSPPI0047",
                    new String[]{ requestURI, contextPath, servletPath,
					pathInfo,  queryString});
		}
	}

    private void setRequestResponseAttributes(RDRequestWrapper rdRequestWrapper,
            PortletRequest portletRequest, PortletResponse portletResponse) {
        // set request/response values as attributes in HttpServletRequestWrapper
        // Note: "javax.portlet.PortletConfig" will be set by the PAE
        rdRequestWrapper.setAttribute(PortletRequestConstants.PORTLET_REQUEST_ATTRIBUTE,
                                portletRequest);
        rdRequestWrapper.setAttribute(PortletRequestConstants.PORTLET_RESPONSE_ATTRIBUTE,
                                portletResponse);
    }

}
