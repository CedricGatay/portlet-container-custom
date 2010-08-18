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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.ClientDataRequest;
import javax.portlet.PortletRequest;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.sun.portal.container.ContainerLogger;
import java.net.URLDecoder;

/**
 * RDRequestWrapper is a wrapped HttpServletRequest object that has methods
 * common to all Portlet Request Wrapper Objects.
 */
public class RDRequestWrapper extends HttpServletRequestWrapper {

    private static Logger logger = ContainerLogger.getLogger(RDRequestWrapper.class, "PAELogMessages");

    private static final String FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
    private static final String FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";
    private static final String FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";
    private static final String FORWARD_PATH_INFO = "javax.servlet.forward.path_info";
    private static final String FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";

    private ServletContext servletContext;
	private HttpServletRequest request;
    private PortletRequest portletRequest;
    private boolean isInclude;
	private boolean isNamed;
    private boolean isParamMapInitialized;
    private Map<String, String[]> paramMap;
    private String requestURI;
    private String servletPath;
    private String pathInfo;
    private String queryString;
	private String lifecyclePhase;

	public RDRequestWrapper(ServletContext servletContext,
		HttpServletRequest request, PortletRequest portletRequest,
		String requestURI, String servletPath, String pathInfo,
		String queryString, String lifecyclePhase,
		boolean isInclude, boolean isNamed) {

		super(request);
        this.servletContext = servletContext;
		this.request = request;
        this.portletRequest = portletRequest;
        this.requestURI = requestURI;
        this.requestURI = requestURI;
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
		this.queryString = queryString;
		this.lifecyclePhase = lifecyclePhase;
        this.isInclude = isInclude;
		this.isNamed = isNamed;
		initializeParamMap();
    }
    
    @Override
    public String getParameter(String name) {
        
        String retVal = null;
        if (paramMap != null) {
            String[] value = paramMap.get(name);
            if (value != null) {
                retVal = value[0];
            }
        }
		if(retVal == null) {
			retVal = super.getParameter(name);
		}
        return retVal;
    }
    
    @Override
    public Enumeration getParameterNames() {
        
        return Collections.enumeration(paramMap.keySet());
    }
    
    @Override
    public String[] getParameterValues(String name) {
        
        String[] retVal = null;
        
        if (paramMap != null) {
            retVal = paramMap.get(name);
        }
        return retVal;
    }
    
    @Override
    public Map getParameterMap() {
        
        return Collections.unmodifiableMap(paramMap);
    }
    
    @Override
    public String getRemoteAddr() {
        return null;
    }
    
    @Override
    public String getRemoteHost() {
        return null;
    }
    
    @Override
    public String getRealPath(String path) {
        return null;
    }
    
    @Override
    public String getLocalAddr() {
        return null;
    }
    
    @Override
    public String getLocalName() {
        return null;
    }
    
    @Override
    public StringBuffer getRequestURL() {
        return null;
    }
    
    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public String getPathInfo() {
		return this.pathInfo;
    }
    
    @Override
    public String getPathTranslated() {
        String pathTranslated = getPathInfo();
		if(pathTranslated == null || this.servletContext == null) {
			return null;
		} else {
			return this.servletContext.getRealPath(pathTranslated);
		}
    }
    
    @Override
    public String getQueryString() {
		return this.queryString;
    }
    
    @Override
    public String getRequestURI() {
		return this.requestURI;
    }
    
    @Override
    public String getServletPath() {
		return this.servletPath;
    }
    
    @Override
    public String getScheme() {
        return portletRequest.getScheme();
    }
    
    @Override
    public String getServerName() {
        return portletRequest.getServerName();
    }
    
    @Override
    public int getServerPort() {
        return portletRequest.getServerPort();
    }
    
    @Override
    public Object getAttribute(String name) {
		if (this.isInclude || (name == null)) {
			return portletRequest.getAttribute(name);
		}

        if(FORWARD_REQUEST_URI.equals(name)) {
			if(this.isNamed) {
				return null;
			} else {
				return this.requestURI;
			}

        } else if(FORWARD_SERVLET_PATH.equals(name)) {
			if(this.isNamed) {
				return null;
			} else {
				return this.servletPath;
			}

        } else if(FORWARD_PATH_INFO.equals(name)) {
			if(this.isNamed) {
				return null;
			} else {
				return this.pathInfo;
			}

        } else if(FORWARD_QUERY_STRING.equals(name)) {
			if(this.isNamed) {
				return null;
			} else {
				return this.queryString;
			}

        } else if(FORWARD_CONTEXT_PATH.equals(name)) {
			if(this.isNamed) {
				return null;
			} else {
				return this.portletRequest.getContextPath();
			}
        }

		return portletRequest.getAttribute(name);
    }
    
    @Override
    public Enumeration getAttributeNames() {
        return portletRequest.getAttributeNames();
    }
    
    @Override
    public void setAttribute(String name, Object value) {
		portletRequest.setAttribute(name, value);
    }
    
    @Override
    public void removeAttribute(String name) {
		portletRequest.removeAttribute(name);
    }
    
    @Override
    public Locale getLocale() {
        return portletRequest.getLocale();
    }
    
    @Override
    public Enumeration getLocales() {
        return portletRequest.getLocales();
    }
    
    @Override
    public boolean isSecure() {
        return portletRequest.isSecure();
    }
    
    @Override
    public String getAuthType() {
        return portletRequest.getAuthType();
    }
    
    @Override
    public String getContextPath() {
        return portletRequest.getContextPath();
    }
    
    @Override
    public String getRemoteUser() {
        return portletRequest.getRemoteUser();
    }
    
    @Override
    public Principal getUserPrincipal() {
        return portletRequest.getUserPrincipal();
    }
    
    @Override
    public String getRequestedSessionId() {
        return portletRequest.getRequestedSessionId();
    }
    
    @Override
    public boolean isRequestedSessionIdValid() {
        return portletRequest.isRequestedSessionIdValid();
    }
    
    @Override
    public String getHeader(String name) {
        return portletRequest.getProperty(name);
    }
    
    @Override
    public Enumeration getHeaders(String name) {
        return portletRequest.getProperties(name);
    }
    
    @Override
    public Enumeration getHeaderNames() {
        return portletRequest.getPropertyNames();
    }
    
    @Override
    public Cookie[] getCookies() {
        return portletRequest.getCookies();
    }
    
    @Override
    public long getDateHeader(String name) {
        String value = portletRequest.getProperty(name);
        long date = 0L;
        try {
            date = Long.parseLong(value);
        } catch(NumberFormatException nfe) {
            if(logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "PSPL_PAECSPPI0033", 
                        new String[] { name, value });
            }
        }
        return date;
    }
    
    @Override
    public int getIntHeader(String name) {
        String value = portletRequest.getProperty(name);
        int intValue = 0;
        try {
            intValue = Integer.parseInt(value);
        } catch(NumberFormatException nfe) {
            if(logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "PSPL_PAECSPPI0034", 
                        new String[] { name, value });
            }
        }
        return intValue;
    }

    @Override
    public String getCharacterEncoding() {
		if (this.lifecyclePhase.equals(PortletRequest.ACTION_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {
			return ((ClientDataRequest)portletRequest).getCharacterEncoding();
		} else {
			return null;
		}
    }

    @Override
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
		if (this.lifecyclePhase.equals(PortletRequest.ACTION_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {
			((ClientDataRequest)portletRequest).setCharacterEncoding(enc);
		}
    }

    @Override
    public String getContentType() {
		if (this.lifecyclePhase.equals(PortletRequest.ACTION_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {
			return ((ClientDataRequest)portletRequest).getContentType();
		} else {
			return null;
		}
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
		if (this.lifecyclePhase.equals(PortletRequest.ACTION_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {
			return (ServletInputStream)((ClientDataRequest)portletRequest).getPortletInputStream();
		} else {
			return null;
		}
    }

    @Override
    public int getContentLength() {
		if (this.lifecyclePhase.equals(PortletRequest.ACTION_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			return ((ClientDataRequest)portletRequest).getContentLength();
		} else {
			return 0;
		}
    }

    @Override
    public String getMethod() {
		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE)) {
			return "GET";
		} else {
			return request.getMethod();
		}
    }

    @Override
    public BufferedReader getReader() throws IOException {
		if (this.lifecyclePhase.equals(PortletRequest.ACTION_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			return ((ClientDataRequest)portletRequest).getReader();
		} else {
			return null;
		}
    }

    @Override
    public String getProtocol() {
        return "HTTP/1.1";
    }

    /**
     * This private initializeParamMap() method initializes a local HashMap
     * which merges the original ActionRequest's parameter map and the
     * parameters specified in the query string used to create the
     * PortletRequestDispatcher (see PLT.19.1). Some highlights
     * here:
     *
     * 1. The parameters specified in the query string take precedence over
     * other portlet render parameters of the same name passed to the included
     * servlet/JSP.
     *
     * 2. The values in this local HashMap are from type String array
     * (String[]). If it is a single-valued parameter, a StringArray of length 1
     * is created to store the value. This is compliant with both the
     * implementations of RenderRequestImpl and ContainerRequest classes.
     */
    private void initializeParamMap() {

        if (!isParamMapInitialized) {
            String queryStringLocal = getQueryString();

            if (queryStringLocal != null) {
				paramMap = new HashMap<String, String[]>();
				String decodedQueryString = null;
				try {
					decodedQueryString = URLDecoder.decode(queryStringLocal, "UTF-8");
				} catch (UnsupportedEncodingException usee) {
					decodedQueryString = queryStringLocal;
				}
                StringTokenizer queryStringTokens = new StringTokenizer(decodedQueryString, "&", false);
				String values[] = new String[1];
                while (queryStringTokens.hasMoreTokens()) {
                    StringTokenizer equalTokens = new StringTokenizer(queryStringTokens.nextToken(), "=");
                    if(equalTokens.countTokens() == 2) {
                        String key = equalTokens.nextToken();
						values[0] = equalTokens.nextToken();
						addParamToMap(paramMap, key, values);
					}
                }
				for (Map.Entry<String, String[]> entry : this.portletRequest.getParameterMap().entrySet()) {
					String key = entry.getKey();
					String[] value = entry.getValue();
					if (paramMap.containsKey(key)) {
						addParamToMap(paramMap, key, value);
					} else {
						paramMap.put(key, value);
					}
				}
            } else {
	            paramMap = new HashMap(this.portletRequest.getParameterMap());
			}
			isParamMapInitialized = true;
        }
    }

	private static final String[] NULL_ARRAY = new String[0];

	private void addParamToMap(Map<String, String[]> map, String key, String[] values) {
		String[] newValues = null;
		String[] origValues = map.get(key);
		if (origValues == null) {
			origValues = NULL_ARRAY;
		}
		newValues = new String[origValues.length + values.length];
		System.arraycopy(origValues, 0, newValues,0, origValues.length);
		System.arraycopy(values, 0, newValues, origValues.length, values.length);
		map.put(key, newValues);
	}
}
