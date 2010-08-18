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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResponse;

import com.sun.portal.container.ChannelURLFactory;
import com.sun.portal.container.ContainerLogger;
import com.sun.portal.portletcontainer.common.PortletActions;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.BaseURL;
import javax.portlet.PortletURL;
import javax.portlet.PortletURLGenerationListener;
import javax.portlet.ResourceURL;
import javax.servlet.http.Cookie;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class provides implementation of the PortletResponse interface.
 */
public abstract class PortletResponseImpl implements PortletResponse {
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PortletContainerRequest pcRequest;
    private PortletContainerResponse pcResponse;
    private PortletRequest portletRequest;
    private PortletAppDescriptor portletAppDescriptor;
    private PortletDescriptor portletDescriptor;
    private Document document;
    
    private static Logger logger = ContainerLogger.getLogger(PortletResponseImpl.class, "PAELogMessages");
    
    /**
     * Initialize the global variables.
     * <P>
     *
     * @param request          The <code>HttpServletRequest</code> of the PAE
     * @param response          The <code>HttpServletResponse</code> of the PAE
     * @param pcRequest     The <code>PortletContainerRequest</code>
     * @param pcResponse     The <code>PortletContainerResponse</code>
     * @param portletRequest         The <code>PortletRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     */
    protected void init( HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerRequest pcRequest,
            PortletContainerResponse pcResponse,
            PortletRequest portletRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor) {
        this.request = request;
        this.response = response;
        this.pcRequest = pcRequest;
        this.pcResponse = pcResponse;
        this.portletRequest = portletRequest;
        this.portletAppDescriptor = portletAppDescriptor;
        this.portletDescriptor = portletDescriptor;
    }
    
    /**
     * Clears the global variables.
     */
    protected void clear() {
        this.request = null;
        this.response = null;
        this.pcRequest = null;
        this.pcResponse = null;
        this.portletRequest = null;
        this.portletAppDescriptor = null;
        this.portletDescriptor = null;
    }
    
    protected HttpServletRequest getHttpServletRequest() {
        return request;
    }

	public HttpServletResponse getHttpServletResponse() {
        return response;
    }
    
    protected PortletContainerRequest getPortletContainerRequest() {
        return pcRequest;
    }
    
    protected PortletContainerResponse getPortletContainerResponse() {
        return pcResponse;
    }
    
    protected PortletAppDescriptor getPortletAppDescriptor() {
        return portletAppDescriptor;
    }
    
    protected PortletDescriptor getPortletDescriptor() {
        return portletDescriptor;
    }
    
	/**
     * Adds a String property to an existing key to be returned to the portal.
     * If there are no property values already associated with the key,
     * a new key is created.
     * <p>
     * This method allows response properties to have multiple values.
     * <p>
     * Response properties can be viewed as header values set for the portal application.
     * If these header values are intended to be transmitted to the client they should be
     * set before the response is committed.
     *
     * @param key
     *            the key of the property to be returned to the portal
     * @param value
     *            the value of the property to be returned to the portal
     *
     * @exception java.lang.IllegalArgumentException
     *                if key is <code>null</code>.
     */
    
    public void addProperty(String key, String value) {
		checkNullProperty(key);

        Map<String, List<String>> properties = pcResponse.getStringProperties();
        List<String> values = null;
        if(properties == null) {
            properties = new HashMap<String, List<String>>();
        } else {
            values = properties.get(key);
        }
        if(values == null) {
            values = new ArrayList<String>();
        }
        values.add(value);
        properties.put(key, values);
        pcResponse.setStringProperties(properties);
    }
    
    /**
     * Adds a HTTP Cookie property to the response.<br>
     * The portlet should note that the cookie may not make
     * it to the client, but may be stored at the portal.
     * <p>
     * This method allows response properties to have multiple cookies.
     * <p>
     *
     * @param  cookie the cookie to be added to the response
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if cookie is <code>null</code>.
     * @since 2.0
     */
    
    public void addProperty(Cookie cookie) {
		checkNullCookie(cookie);

		List<Cookie> properties = pcResponse.getCookieProperties();
        if(properties == null) {
            properties = new ArrayList<Cookie>();
        }
        properties.add(cookie);
        pcResponse.setCookieProperties(properties);
    }
    
    /**
     * Adds an XML DOM element property to the response.
     * <p>
     * If a DOM element with the provided key already exists
     * the provided element will be stored in addition to the
     * existing element under the same key.
     * <p>
     * If the element is <code>null</code> the key is removed from
     * the response.
     * <p>
     * Properties can be used by portlets to provide vendor specific information
     * to the portal.
     *
     * @param key
     *            the key of the property to be returned to the portal
     * @param  element
     *            the XML DOM element to be added to the response
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if key is <code>null</code>.
     * @since 2.0
     */
    public void addProperty(String key, Element element) {
		checkNullProperty(key);

        Map<String, List<Element>> properties = pcResponse.getElementProperties();
        if(properties == null) {
            if(element != null) {
                properties = new HashMap<String, List<Element>>();
                List<Element> values = new ArrayList();
                values.add(element);
                properties.put(key, values);
            }
        } else {
            List<Element> values = properties.get(key);
            if(element == null) {
                if(values != null) {
                    values.remove(key);
                    properties.put(key, values);
                }
            } else {
                if(values == null) {
                    values = new ArrayList<Element>();
                }
                values.add(element);
                properties.put(key, values);
            }
        }
        pcResponse.setElementProperties(properties);
    }
    
   /**
    * Creates an element of the type specified to be used in the 
    * {@link addProperty(String,Element)} method. 
    * 
    * @param tagName	name of the element type to instantiate
    * @return  A new Element object with the nodeName attribute set to tagName, 
    *          and localName, prefix, and namespaceURI set to null.
    * @throws org.w3c.dom.DOMException
    *     INVALID_CHARACTER_ERR: Raised if the specified name 
    *     contains an illegal character.
    */
   public Element createElement(String tagName) {
        if (document == null) {
            try {
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
                document = dbfactory.newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e) {
                throw new IllegalStateException("Cannot create the document builder factory", e);
            }
        }
        return document.createElement(tagName);
    }

    /**
     * Sets a String property to be returned to the portal.
     * <p>
     * Properties can be used by portlets to provide vendor specific
     * information to the portal.
     *
     * This method resets all properties previously added with the
     * same key.
     *
     * @param  key    the key of the property to be returned to the portal
     * @param  value  the value of the property to be returned to the portal
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if key is <code>null</code>.
     */
    public void setProperty(String key, String value) {
		checkNullProperty(key);

        Map<String, List<String>> properties = pcResponse.getStringProperties();
        if(properties == null) {
            properties = new HashMap<String, List<String>>();
        }
        List<String> values = new ArrayList();
        values.add(value);
        properties.put(key, values);
        pcResponse.setStringProperties(properties);
    }
    
    /**
     * Returns the encoded URL of the resource, like servlets,
     * JSPs, images and other static files, at the given path.
     * <p>
     * Some portal/portlet-container implementation may require
     * those URLs to contain implementation specific data encoded
     * in it. Because of that, portlets should use this method to
     * create such URLs.
     * <p>
     * The <code>encodeURL</code> method may include the session ID
     * and other portal/portlet-container specific information into the URL.
     * If encoding is not needed, it returns the URL unchanged.
     *
     * @param   path
     *          the URI path to the resource. This must be either
     *          an absolute URL (e.g.
     *          <code>http://my.co/myportal/mywebap/myfolder/myresource.gif</code>)
     *          or a full path URI (e.g. <code>/myfolder/myresource.gif</code>).
     *
     * @return   the resource URL as string
     *
     * @exception java.lang.IllegalArgumentException - if path doesn't
     * have a leading slash or is not an absolute URL
     */
    
    public String encodeURL(String path) {
        
        ChannelURLFactory portletWindowURLFactory = getPortletContainerRequest().getPortletWindowURLFactory();
        if (path.startsWith("/") || (path.indexOf(":") != -1)) {
            return portletWindowURLFactory.encodeURL( getHttpServletRequest(), getHttpServletResponse(), path );
        } else {
            logger.log(Level.WARNING, "PSPL_PAECSPPI0022", path);
            throw new IllegalArgumentException("Path:" + path + 
                    " must be a valid absolute URL or a full path URI");
        }
        
    }
    
    /**
     * The value returned by this method should be prefixed or appended to
     * elements, such as JavaScript variables or function names, to ensure
     * they are unique in the context of the portal page.
     *
     * @return   the namespace
     */
    
    public String getNamespace() {
        return pcRequest.getNamespace();
    }
    
    /**
     * Creates a portlet URL targeting the portlet. If no portlet mode, window
     * state or security modifier is set in the PortletURL the current values
     * are preserved. If a request is triggered by the PortletURL, it results in
     * a render request.
     * <p>
     * The returned URL can be further extended by adding portlet-specific
     * parameters and portlet modes and window states.
     * <p>
     * The created URL will per default not contain any parameters of the
     * current render request.
     *
     * @return a portlet render URL
     * @throws java.lang.IllegalStateException
     *             if the cacheability level of the resource URL
     *             triggering this <code>serveResource</code> call,
     *             or one of the parent calls, have defined a stricter
     *             cachability level.
     */
    public PortletURL createRenderURL() throws IllegalStateException {
        PortletURL url = new PortletURLImpl(
                portletRequest,
                pcRequest,
                getPortletAppDescriptor(),
                getPortletDescriptor(),
                PortletActions.RENDER);
        callURLGenerationListeners(PortletActions.RENDER, url);
        return url;
    }
    
    /**
     * Creates a portlet URL targeting the portlet. If no portlet mode, window
     * state or security modifier is set in the PortletURL the current values
     * are preserved. If a request is triggered by the PortletURL, it results in
     * an action request.
     * <p>
     * The returned URL can be further extended by adding portlet-specific
     * parameters and portlet modes and window states.
     * <p>
     * The created URL will per default not contain any parameters of the
     * current render request.
     *
     * @return a portlet action URL
     * @throws java.lang.IllegalStateException
     *             if the cacheability level of the resource URL
     *             triggering this <code>serveResource</code> call,
     *             or one of the parent calls, have defined a stricter
     *             cachability level.
     */
    public PortletURL createActionURL() throws IllegalStateException {
        PortletURL url = new PortletURLImpl(
                portletRequest,
                pcRequest,
                getPortletAppDescriptor(),
                getPortletDescriptor(),
                PortletActions.ACTION);
        callURLGenerationListeners(PortletActions.ACTION, url);
        return url;
    }
	
    // If there any listeners for URL Generation call them
    protected void callURLGenerationListeners(String action, BaseURL portletURL) {
        List<PortletURLGenerationListener> urlGenerationListeners = getPortletAppDescriptor().getURLGenerationListeners();
        if(urlGenerationListeners != null) {
            for(PortletURLGenerationListener listener: urlGenerationListeners){
                if(PortletActions.ACTION.equals(action)) {
                    listener.filterActionURL((PortletURL)portletURL);
                } else if(PortletActions.RENDER.equals(action)) {
                    listener.filterRenderURL((PortletURL)portletURL);
                } else if(PortletActions.RESOURCE.equals(action)) {
                    listener.filterResourceURL((ResourceURL)portletURL);
                }
            }
        }
    }

	protected void checkNullProperty(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Property name should not be null.");
        }
	}

	protected void checkNullCookie(Cookie cookie) {
        if ( cookie == null ) {
            throw new IllegalArgumentException("Cookie should not be null.");
        }
	}

}
