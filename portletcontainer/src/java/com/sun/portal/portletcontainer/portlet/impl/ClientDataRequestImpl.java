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

import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import javax.portlet.ClientDataRequest;
import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class provides implementation of the ClientDataRequest interface.
 *
 */
public abstract class ClientDataRequestImpl extends PortletRequestImpl implements ClientDataRequest {
    
    private boolean gotReader;
    private boolean gotInputStream;
    
    private static String ILLEGAL_OPERATION_ERROR = "Cannot perform this function for portlet: ";
    private static String UNSUPPORTED_ENCODING_ERROR = "The specified character encoding is not supported.";
    
    /**
     * Initialize the global variables.
     * <P>
     *
     *
     * @param request          The <code>HttpServletRequest</code> of the PAE
     * @param response          The <code>HttpServletResponse</code> of the PAE
     * @param pcRequest     The <code>PortletContainerRequest</code>
     * @param pcResponse     The <code>PortletContainerResponse</code>
     * @param portletContext     The <code>PortletContext</code>
     * @param portalContext     The <code>PortalContext</code>
     * @param pDescriptor       The <code>PortletDescriptor</code> object
     */
    @Override
    protected void init(HttpServletRequest request, 
            HttpServletResponse response,
            PortletContainerRequest pcRequest,
            PortletContainerResponse pcResponse,
            PortletContext portletContext, 
            PortalContext portalContext,
            PortletDescriptor pDescriptor,
            Map attributes) {
        
        super.init(request, response, pcRequest, pcResponse,
                portletContext, portalContext, pDescriptor, attributes);
        this.gotReader = false;
        this.gotInputStream = false;
        
    }
    
    /**
     * Clears the global variables.
     */
    @Override
    protected void clear() {
        super.clear();
        this.gotReader = false;
        this.gotInputStream = false;        
    }
    
    /**
     * Retrieves the body of the HTTP request from client to
     * portal as binary data using
     * an <CODE>InputStream</CODE>. Either this method or
     * {@link #getReader} may be called to read the body, but not both.
     * <p>
     * For HTTP POST data of type application/x-www-form-urlencoded
     * this method throws an <code>IllegalStateException</code>
     * as this data has been already processed by the
     * portal/portlet-container and is available as request parameters.
     *
     * @return an input stream containing the body of the request
     *
     * @exception java.lang.IllegalStateException
     *            if getReader was already called, or it is a
     *            HTTP POST data of type application/x-www-form-urlencoded
     * @exception IOException
     *            if an input or output exception occurred
     */
    public InputStream getPortletInputStream() throws IOException {
        
        // check if getReader is already called
        if (gotReader) {
            throw new IllegalStateException(ILLEGAL_OPERATION_ERROR + getPortletContainerRequest().getPortletName());
        }
        
        // check if mime type is application/x-window-form-urlencoded
        if (getContentType() != null && getContentType().equals("application/x-www-form-urlencoded")) {
            throw new IllegalStateException(ILLEGAL_OPERATION_ERROR + getPortletContainerRequest().getPortletName());
        }
        
        gotInputStream = true;
        return getHttpServletRequest().getInputStream();
    }
    
    /**
     * Retrieves the body of the HTTP request from client to the portal
     * as character data using
     * a <code>BufferedReader</code>.  The reader translates the character
     * data according to the character encoding used on the body.
     * Either this method or {@link #getPortletInputStream} may be called to read the
     * body, not both.
     *
     * <p>
     * For HTTP POST data of type application/x-www-form-urlencoded
     * this method throws an <code>IllegalStateException</code>
     * as this data has been already processed by the
     * portal/portlet-container and is available as request parameters.
     *
     * @return	a <code>BufferedReader</code>
     *		containing the body of the request
     *
     * @exception  java.io.UnsupportedEncodingException
     *             if the character set encoding used is
     * 	           not supported and the text cannot be decoded
     * @exception  java.lang.IllegalStateException
     *                 if {@link #getPortletInputStream} method
     * 		     has been called on this request,  it is a
     *                   HTTP POST data of type application/x-www-form-urlencoded.
     * @exception  java.io.IOException
     *                 if an input or output exception occurred
     *
     * @see #getInputStream
     */
    
    public BufferedReader getReader() throws UnsupportedEncodingException, IOException {
        
        // check if the getPortletInputStream has called
        if (gotInputStream) {
            throw new IllegalStateException(ILLEGAL_OPERATION_ERROR + getPortletContainerRequest().getPortletName());
        }
        
        // check if mime type is application/x-window-form-urlencoded
        if (getContentType() != null && getContentType().equals("application/x-www-form-urlencoded")) {
            throw new IllegalStateException(ILLEGAL_OPERATION_ERROR + getPortletContainerRequest().getPortletName());
        }
        
        gotReader = true;
        return getHttpServletRequest().getReader();
    }
    
    /**
     * Overrides the name of the character encoding used in the body of this
     * request. This method must be called prior to reading input
     * using {@link #getReader} or {@link #getPortletInputStream}.
     * <p>
     *
     * @param	enc	a <code>String</code> containing the name of
     *			the chararacter encoding.
     *
     * @exception	java.io.UnsupportedEncodingException if this
     *                  is not a valid encoding
     * @exception	java.lang.IllegalStateException      if this method is called after
     *                  reading request parameters or reading input using
     *                  <code>getReader()</code>
     */
    
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        if (gotReader || gotInputStream) {
            throw new IllegalStateException("Illegal setting character encoding after getReader() or getPortletInputStream() is called");
        }
        
        if (!enc.equals(getCharacterEncoding())) {
            throw new UnsupportedEncodingException(UNSUPPORTED_ENCODING_ERROR);
        }
        
        getHttpServletRequest().setCharacterEncoding(enc);
    }
    
    
    /**
     * Returns the MIME type of the body of the request,
     * or null if the type is not known.
     *
     * @return        a <code>String</code> containing the name
     *                of the MIME type of the request, or null
     *                if the type is not known
     */
    public String getContentType() {
        return getHttpServletRequest().getContentType();
    }
    
    /**
     * Returns the length, in bytes, of the request body
     * and made available by the input stream, or -1 if the
     * length is not known.
     *
     * @return        an integer containing the length of the
     *            request body or -1 if the length is not known
     *
     */
    public int getContentLength() {
        int retVal = -1;
        
        if (getContentType() != null && !getContentType().equals("application/x-www-form-urlencoded")) {
            retVal = getHttpServletRequest().getContentLength();
        }
        return retVal;
    }
    
    
    /**
     * Returns the name of the HTTP method with which this request was made, 
     * for example, GET, POST, or PUT.
     * 
     * @since 2.0
     * @return  a String specifying the name of the HTTP method with which 
     *          this request was made
     */
    public String getMethod() {
        return getHttpServletRequest().getMethod();
    }
}
