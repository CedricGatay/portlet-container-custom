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

import com.sun.portal.container.ChannelURL;
import com.sun.portal.container.ChannelURLFactory;
import com.sun.portal.container.ContainerLogger;
import com.sun.portal.portletcontainer.appengine.PortletAppEngineUtils;
import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import com.sun.portal.portletcontainer.common.URLHelper;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptorConstants;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.BaseURL;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.xml.namespace.QName;

/**
 * The BaseURLImpl defines the basic capabilities of a portlet URL pointing back to the portlet.
 *
 */
public abstract class BaseURLImpl implements BaseURL {

    // Global variables
    private PortletRequest portletRequest;
    private ChannelURL portletWindowURL;
    private PortletAppDescriptor portletAppDescriptor;
    private PortletDescriptor portletDescriptor;
    private String action;
    private Boolean escapeXml;

    private static Logger logger = ContainerLogger.getLogger(BaseURLImpl.class, "PAELogMessages");

    protected void init(PortletRequest portletRequest, 
            PortletContainerRequest pcRequest,
            PortletAppDescriptor portletAppDescriptor, 
            PortletDescriptor portletDescriptor, 
            String action) {

        this.portletRequest = portletRequest;
        ChannelURLFactory channelURLFactory = pcRequest.getPortletWindowURLFactory();
        if (channelURLFactory != null) {
            portletWindowURL = channelURLFactory.createChannelURL();
            if (portletWindowURL != null) {
                portletWindowURL.setURLType(PortletAppEngineUtils.getURLType(action));
            }
        } else {
            logger.log(Level.SEVERE, "PSPL_PAECSPPI0023", pcRequest.getEntityID());
        }
        this.portletAppDescriptor = portletAppDescriptor;
        this.portletDescriptor = portletDescriptor;
        this.action = action;
        String[] escapeXmlValue = this.portletAppDescriptor.getContainerRuntimeOption(
                                this.portletDescriptor.getPortletID(), PortletDescriptorConstants.ESCAPE_XML);
        if(escapeXmlValue != null) {
            this.escapeXml = Boolean.valueOf(escapeXmlValue[0]);
            this.portletRequest.setAttribute(PortletRequestConstants.ESCAPE_XML_VALUE, escapeXml);
        }
    }

    protected PortletRequest getPortletRequest() {
        return this.portletRequest;
    }
    
    protected ChannelURL getChannelURL() {
        return this.portletWindowURL;
    }

    protected PortletDescriptor getPortletDescriptor() {
        return this.portletDescriptor;
    }

    protected PortletAppDescriptor getPortletAppDescriptor() {
        return this.portletAppDescriptor;
    }

    protected String getAction() {
        return this.action;
    }
    
    /**
     * Returns a <code>Map</code> of the parameters
     * currently set on this portlet URL via the
     * <code>setParameter</code> or <code>setParameters</code>
     * methods.
     * <p>
     * The values in the returned <code>Map</code> are from type
     * String array (<code>String[]</code>).
     * <p>
     * If no parameters exist this method returns an empty <code>Map</code>.
     *
     * @return     <code>Map</code> containing parameter names as
     *             keys and parameter values as map values, or an empty <code>Map</code>
     *             if no parameters exist. The keys in the parameter
     *             map are of type String. The values in the parameter map are of type
     *             String array (<code>String[]</code>).
     *
     * @since 2.0
     */

    public Map<String, String[]> getParameterMap() {
        Map map = getChannelURL().getParameters();
        if(map == null) {
            map = Collections.EMPTY_MAP;
        }
        return map;
    }

    /**
     * Sets the given String parameter to this URL. 
     * <p>
     * This method replaces all parameters with the given key.
     * <p>
     * The <code>PortletURL</code> implementation 'x-www-form-urlencoded' encodes
     * all  parameter names and values. Developers should not encode them.
     * <p>
     * A portlet container may prefix the attribute names internally 
     * in order to preserve a unique namespace for the portlet.
     * <p>
     * A parameter value of <code>null</code> indicates that this
     * parameter should be removed.
     *
     * @param   name
     *          the parameter name
     * @param   value
     *          the parameter value
     *
     * @exception  java.lang.IllegalArgumentException 
     *                            if name is <code>null</code>.
     */

    public void setParameter(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("The parameter name cannot be null.");
        }
        if (value == null) {
			// If the value is null, set its value as null in the URL
			getChannelURL().setParameter(name, (String)null);
        } else {
            // If the input name is same as the public render parameter, then it will not replace
            // the public render parameter name, rather it will be merged such that in the values, the
            // public render parameter values must be the last entries in the list
            QName publicRenderParameterQName = this.portletAppDescriptor.getPublicRenderParameterQName(name);
            if(publicRenderParameterQName != null) {
                Map<String, String[]> map = getParameterMap();
                String[] values = map.get(name);
                if(values != null) {
                    LinkedList<String> list = new LinkedList(Arrays.asList(values));
                    list.addFirst(value);
                    values = list.toArray(new String[0]);
                    setParameter(name, values);
                } else {
                    getChannelURL().setParameter(name, value);
                }
            } else {
                getChannelURL().setParameter(name, value);
            }
        }
    }

    /**
     * Sets the given String array parameter to this URL. 
     * <p>
     * This method replaces all parameters with the given key.
     * <p>
     * The <code>PortletURL</code> implementation 'x-www-form-urlencoded' encodes
     * all  parameter names and values. Developers should not encode them.
     * <p>
     * A portlet container may prefix the attribute names internally 
     * in order to preserve a unique namespace for the portlet.
     *
     * @param   name
     *          the parameter name
     * @param   values
     *          the parameter values
     *
     * @exception  java.lang.IllegalArgumentException 
     *                            if name is <code>null</code>.
     */

    public void setParameter(String name, String[] values) {
        if (name == null || values == null) {
            throw new IllegalArgumentException("The parameter name or values cannot be null.");
        }
        // If the input name is same as the public render parameter, then it will not replace
        // the public render parameter name, rather it will be merged such that in the values, the
        // public render parameter values must be the last entries in the list
        QName publicRenderParameterQName = this.portletAppDescriptor.getPublicRenderParameterQName(name);
        if(publicRenderParameterQName != null) {
            Map<String, String[]> map = getParameterMap();
            String[] existingValues = map.get(name);
            if(existingValues != null) {
                LinkedList<String> list = new LinkedList(Arrays.asList(existingValues));
                for(int i=0; i<values.length; i++) {
                    list.addFirst(values[i]);
                }
                existingValues = list.toArray(new String[0]);
                setParameter(name, existingValues);
            } else {
                getChannelURL().setParameter(name, values);
            }
        } else {
            getChannelURL().setParameter(name, values);
        }
    }

    /**
     * Sets a parameter map for this URL.
     * <p>
     * All previously set parameters are cleared.
     * <p>
     * The <code>PortletURL</code> implementation 'x-www-form-urlencoded' encodes
     * all  parameter names and values. Developers should not encode them.
     * <p>
     * A portlet container
     * may prefix the attribute names internally, in order to preserve
     * a unique namespace for the portlet.
     *
     * @param  parameters   Map containing parameter names for
     *                      the render phase as
     *                      keys and parameter values as map
     *                      values. The keys in the parameter
     *                      map must be of type String. The values
     *                      in the parameter map must be of type
     *                      String array (<code>String[]</code>).
     *
     * @exception  java.lang.IllegalArgumentException
     *                      if parameters is <code>null</code>.
     *                      if any of the keys is not a String, or if any of
     *                      the values is not a String array.
     */

    public void setParameters(Map<String, String[]> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("Cannot set parameter with null map object.");
        }
        Iterator i = parameters.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (!(key instanceof String) || !(val instanceof String[])) {
                throw new IllegalArgumentException("Parameter keys should be type String and values should be type String[].");
            }
        }

        getChannelURL().setParameters(parameters);
    }

    /**
     * Indicated the security setting for this URL.
     * <p>
     * Secure set to <code>true</code> indicates that the portlet requests
     * a secure connection between the client and the portlet window for
     * this URL. Secure set to <code>false</code> indicates that the portlet
     * does not need a secure connection for this URL. If the security is not
     * set for a URL, it will stay the same as the current request.
     *
     * @param  secure  true, if portlet requests to have a secure connection
     *                 between its portlet window and the client; false, if
     *                 the portlet does not require a secure connection.
     *
     * @throws PortletSecurityExecption  if the run-time environment does
     *                                   not support the indicated setting
     */

    public void setSecure(boolean secure) throws PortletSecurityException {
        if (secure && (!getPortletRequest().isSecure())) {
            throw new PortletSecurityException("The Portal Server is not running on secure mode");
        }

        getChannelURL().setSecure(secure);
    }

    /**
     * Returns the portlet URL string representation to be embedded in the
     * markup.<br>
     * Note that the returned String may not be a valid URL, as it may
     * be rewritten by the portal/portlet-container before returning the
     * markup to the client.
     *
     * @return   the encoded URL as a string
     */

    @Override
    public String toString() {
        String portletURLString = getChannelURL().toString();
        if(this.escapeXml != null && this.escapeXml.booleanValue()) {
            return URLHelper.escapeURL(portletURLString);
        } else {
            return portletURLString;
        }
    }

    /**
     * Writes the portlet URL to the output stream using the provided writer.
     * <p>
     * Note that the URL written to the output stream may not be a valid URL, as it may
     * be rewritten by the portal/portlet-container before returning the
     * markup to the client.
     * <p>
     * The URL written to the output stream is always XML escaped. For writing
     * non-escaped URLs use {@link #write(java.io.Writer, boolean)}.
     *
     * @param out  the writer to write the portlet URL to
     * @throws java.io.IOException  if an I/O error occured while writing the URL
     *
     * @since 2.0
     */
    public void write(Writer out) throws IOException {
        write(out, true);
    }

    /**
     * Writes the portlet URL to the output stream using the provided writer.
     * If the parameter escapeXML is set to true the URL will be escaped to be
     * valid XML characters, i.e. &lt, &gt, &amp, &#039, &#034 will get converted
     * into their corresponding character entity codes (&lt to &&lt, &gt to &&gt,
     * &amp to &&amp, &#039 to &&#039, &#034 to &&#034).
     * If escapeXML is set to flase no escaping will be done.
     * <p>
     * Note that the URL written to the output stream may not be a valid URL, as it may
     * be rewritten by the portal/portlet-container before returning the
     * markup to the client.
     *
     * @param out       the writer to write the portlet URL to
     * @param escapeXML denotes if the URL should be XML escaped before written to the output
     *                  stream or not
     * @throws java.io.IOException  if an I/O error occured while writing the URL
     *
     * @since 2.0
     */
    public void write(Writer out, boolean escapeXML) throws IOException {
        String urlString = toString();
        if(this.escapeXml != null) {
            escapeXML = this.escapeXml.booleanValue();
        }
        if(escapeXML) {
            urlString = URLHelper.escapeURL(urlString);
        }
        out.write(urlString);
    }


    /**
     * Adds a String property to an existing key on the URL.
     * <p>
     * This method allows URL properties to have multiple values.
     * <p>
     * Properties can be used by portlets to provide vendor specific information
     * to the URL.
     *
     * @param key
     *            the key of the property
     * @param value
     *            the value of the property
     *
     * @exception java.lang.IllegalArgumentException
     *                if key is <code>null</code>.
     *
     * @since 2.0
     */
    public void addProperty(String key, String value) {
        if(key == null) {
            throw new IllegalArgumentException("The key cannot be null");
        }
        getChannelURL().addProperty(key, value);
    }


    /**
     * Sets a String property on the URL.
     * <p>
     * Properties can be used by portlets to provide vendor specific information
     * to the URL.
     * <p>
     * This method resets all properties previously added with the same key.
     *
     * @param key
     *            the key of the property
     * @param value
     *            the value of the property
     *
     * @exception java.lang.IllegalArgumentException
     *                if key is <code>null</code>.
     *
     * @since 2.0
     */
    public void setProperty(String key, String value) {
        if(key == null) {
            throw new IllegalArgumentException("The key cannot be null");
        }
        getChannelURL().setProperty(key, value);
    }
}
