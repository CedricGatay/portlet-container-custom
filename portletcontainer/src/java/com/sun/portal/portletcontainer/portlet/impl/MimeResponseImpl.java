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
import com.sun.portal.portletcontainer.appengine.PortletAppEngineConstants;
import com.sun.portal.portletcontainer.appengine.StringServletOutputStream;
import com.sun.portal.portletcontainer.common.PortletActions;
import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.Logger;
import javax.portlet.CacheControl;
import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceURL;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Element;

/**
 * The <CODE>MimeResponseImpl</CODE> implements <CODE>MimeResponse</CODE> interface to assist a
 * portlet in returing MIME content.
 *
 */

public abstract class MimeResponseImpl extends PortletResponseImpl implements MimeResponse {

    private HttpServletResponse response;
    private HttpServletRequest request;
    private PortletContainerRequest pcRequest;
    private PortletContainerResponse pcResponse;
    private PortletRequest portletRequest;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private StringServletOutputStream outputStream;
    protected String contentType;
    private boolean gotWriter;
    private boolean gotOutputStream;
    private boolean committed;
    private int bufSize;

    private static Logger logger = ContainerLogger.getLogger(MimeResponseImpl.class, "PAELogMessages");

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
     * @param stringWriter     The <code>StringWriter</code>
     * @param outputStream     The <code>StringServletOutputStream</code>
     */
    protected void init(HttpServletRequest request, 
            HttpServletResponse response, 
            PortletContainerRequest pcRequest, 
            PortletContainerResponse pcResponse, 
            PortletRequest portletRequest, 
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor,
            StringWriter stringWriter, 
            StringServletOutputStream outputStream) {
        super.init(request, response, pcRequest, pcResponse, 
                portletRequest, portletAppDescriptor, portletDescriptor);
        this.request = request;
        this.response = response;
        this.pcRequest = pcRequest;
        this.pcResponse = pcResponse;
        this.portletRequest = portletRequest;
        this.printWriter = new PrintWriter(stringWriter);
        this.stringWriter = stringWriter;
        this.outputStream = outputStream;
        this.gotWriter = false;
        this.gotOutputStream = false;
        this.committed = false;
        this.bufSize = PortletAppEngineConstants.INITIAL_BUFFER_SIZE;
    }

    /**
     * Clears the global variables.
     */
    @Override
    protected void clear() {
        super.clear();

        this.request = null;
        this.response = null;
        this.pcRequest = null;
        this.pcResponse = null;
        this.portletRequest = null;
        this.printWriter = null;
        this.stringWriter = null;
        this.outputStream = null;
        this.gotWriter = false;
        this.gotOutputStream = false;
        this.committed = false;
        this.bufSize = 0;
        this.contentType = null;
    }

    /**
     * Returns the MIME type that can be used to contribute
     * markup to the portlet response.
     * <p>
     * If no content type was set previously using the {@link #setContentType} method
     * this method retuns <code>null</code>.
     *
     * @see #setContentType
     *
     * @return   the MIME type of the response, or <code>null</code>
     *           if no content type is set
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the MIME type for the render response. The portlet must set the
     * content type before calling {@link #getWriter} or
     * {@link #getPortletOutputStream}.
     * <p>
     * Calling <code>setContentType</code> after <code>getWriter</code> or
     * <code>getOutputStream</code> does not change the content type.
     * <p>
     * The portlet container will ignore any character encoding
     * specified as part of the content type.
     *
     * @param type
     *            the content MIME type
     *
     * @throws java.lang.IllegalArgumentException
     *             if the given type is not in the list returned by
     *             <code>PortletRequest.getResponseContentTypes</code>
     *
     * @see PortletRequest#getResponseContentTypes
     * @see #getContentType
     */
    public void setContentType(String type) {
        //if gotWriter or gotOutputStream is true, return without setting the content type.
        if (!gotWriter || !gotOutputStream) {

            if (type.indexOf(";") != -1) {
                type = type.substring(0, type.indexOf(";"));
            }
            boolean found = false;
            Enumeration e = portletRequest.getResponseContentTypes();
            while (e.hasMoreElements() && !found) {
                String next = (String) e.nextElement();
                if (type.equalsIgnoreCase(next)) {
                    found = true;
                }
                //If allowable content types list has a wild card content type, 
                //then allow any content type.
                if(next.equals("*/*"))
                    found = true;
            }
            if (found) {
                contentType = type;
                response.setContentType(type);
            } else {
                throw new IllegalArgumentException("Unsupported response content type: " + type);
            }
        }
    }

	@Override
	public void addProperty(String key, String value) {
		checkNullProperty(key);

		if(!isCommitted()) {
			super.addProperty(key, value);
		}
	}

	@Override
    public void addProperty(String key, Element element) {
		checkNullProperty(key);

		if(!isCommitted()) {
			super.addProperty(key, element);
		}
	}

	@Override
    public void setProperty(String key, String value) {
		checkNullProperty(key);

		if(!isCommitted()) {
			super.setProperty(key, value);
		}
	}

	@Override
    public void addProperty(Cookie cookie) {
		checkNullCookie(cookie);

		if(!isCommitted()) {
			super.addProperty(cookie);
		}
	}
	
	/**
     * Returns the name of the charset used for the MIME body sent in this
     * response.
     *
     * <p>
     * See <a href="http://ds.internic.net/rfc/rfc2045.txt">RFC 2047</a> for
     * more information about character encoding and MIME.
     *
     * @return a <code>String</code> specifying the name of the charset, for
     *         example, <code>ISO-8859-1</code>
     *
     */
    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    /**
     * Returns a PrintWriter object that can send character text to the portal.
     * <p>
     * Before calling this method the content type of the render response must
     * be set using the {@link #setContentType} method.
     * <p>
     * Either this method or {@link #getPortletOutputStream} may be called to
     * write the body, not both.
     *
     * @return a <code>PrintWriter</code> object that can return character
     *         data to the portal
     *
     * @exception java.io.IOException
     *                if an input or output exception occurred
     * @exception java.lang.IllegalStateException
     *                if the <code>getPortletOutputStream</code> method has
     *                been called on this response, or if no content type was
     *                set using the <code>setContentType</code> method.
     *
     * @see #setContentType
     * @see #getPortletOutputStream
     */
    public PrintWriter getWriter() throws IOException {
        if (gotOutputStream) {
            throw new IllegalStateException("MimeResponseImpl.getWriter: illegal getting writer since getPortletOutputStream() has already been called");
        }
        // If the contentType is not set use the content type of PortletRequest.getResponseContentType() 
        if (!isContentTypeSet()) {
            setContentType(this.portletRequest.getResponseContentType());
        }
        gotWriter = true;
        return this.printWriter;
    }

    /*
     * <code>isContentTypeSet</code> is called by
     * <code>getPortletOutputStream</code> and <code>getWriter</code>
     * to see if the content type is set, if not then return false,
     * otherwise return true.
     */
    private boolean isContentTypeSet() {
        boolean set = true;

        if (getContentType() == null) {
            set = false;
        }
        return set;
    }

    /**
     * Returns the locale assigned to the response.
     *
     * @return Locale of this response
     */
    public Locale getLocale() {
        return portletRequest.getLocale();
    }

    /**
     * Sets the preferred buffer size for the body of the response. The portlet
     * container will use a buffer at least as large as the size requested.
     * <p>
     * This method must be called before any response body content is written;
     * if content has been written, or the portlet container does not support
     * buffering, this method may throw an <code>IllegalStateException</code>.
     *
     * @param size
     *            the preferred buffer size
     *
     * @exception java.lang.IllegalStateException
     *                if this method is called after content has been written,
     *                or the portlet container does not support buffering
     *
     * @see #getBufferSize
     * @see #flushBuffer
     * @see #isCommitted
     * @see #reset
     */
    public void setBufferSize(int size) {
        if(this.stringWriter.getBuffer().length() > 0) {
            throw new IllegalStateException("This method cannot be called after content has been written");
        }
        bufSize = size;
    }

    /**
     * Returns the actual buffer size used for the response. If no buffering is
     * used, this method returns 0.
     *
     * @return the actual buffer size used
     *
     * @see #setBufferSize
     * @see #flushBuffer
     * @see #isCommitted
     * @see #reset
     */
    public int getBufferSize() {
        return bufSize;
    }

    /**
     * Forces any content in the buffer to be written to the underlying output stream. A call to
     * this method automatically commits the response.
     *
     * @exception java.io.IOException
     *                if an error occured when writing the output
     *
     * @see #setBufferSize
     * @see #getBufferSize
     * @see #isCommitted
     * @see #reset
     */
    public void flushBuffer() throws IOException {
        committed = true;
    }

    /**
     * Clears the content of the underlying buffer in the response without
     * clearing properties set. If the response has been committed, this method
     * throws an <code>IllegalStateException</code>.
     *
     * @exception IllegalStateException
     *                if this method is called after response is comitted
     *
     * @see #setBufferSize
     * @see #getBufferSize
     * @see #isCommitted
     * @see #reset
     */
    public void resetBuffer() {
        if (committed) {
            throw new IllegalStateException("MimeResponseImpl.resetBuffer: illegal resetting buffer: buffer has already commited.");
        }

        if (gotOutputStream) {
            this.outputStream.reset();
        }
        if (gotWriter) {
            stringWriter.getBuffer().setLength(0);
        }
    }

    /**
     * Returns a boolean indicating if the response has been committed.
     *
     * @return a boolean indicating if the response has been committed
     *
     * @see #setBufferSize
     * @see #getBufferSize
     * @see #flushBuffer
     * @see #reset
     */
    public boolean isCommitted() {
        int size = 0;

        if (gotOutputStream) {
            size = this.outputStream.size();
        } else if (gotWriter) {
            size = stringWriter.getBuffer().length();
        }

        if (size > PortletAppEngineConstants.INITIAL_BUFFER_SIZE) {
            committed = true;
        }

        return committed;
    }

    /**
     * Clears any data that exists in the buffer as well as the properties set.
     * If the response has been committed, this method throws an
     * <code>IllegalStateException</code>.
     *
     * @exception java.lang.IllegalStateException
     *                if the response has already been committed
     *
     * @see #setBufferSize
     * @see #getBufferSize
     * @see #flushBuffer
     * @see #isCommitted
     */
    public void reset() {
        resetBuffer();
    }

    /**
     * Returns a <code>OutputStream</code> suitable for writing binary data in
     * the response. The portlet container does not encode the binary data.
     * <p>
     * Before calling this method the content type of the render response must
     * be set using the {@link #setContentType} method.
     * <p>
     * Calling <code>flush()</code> on the OutputStream commits the response.
     * <p>
     * Either this method or {@link #getWriter} may be called to write the body,
     * not both.
     *
     * @return a <code>OutputStream</code> for writing binary data
     *
     * @exception java.lang.IllegalStateException
     *                if the <code>getWriter</code> method has been called on
     *                this response, or if no content type was set using the
     *                <code>setContentType</code> method.
     *
     * @exception java.io.IOException
     *                if an input or output exception occurred
     *
     * @see #setContentType
     * @see #getWriter
     */
    public OutputStream getPortletOutputStream() throws IOException {
        if (gotWriter) {
            throw new IllegalStateException("MimeResponseImpl.getOutputStream: illegal getting output stream: getWriter() has already been called");
        }
        // If the contentType is not set use the content type of PortletRequest.getResponseContentType() 
        if (!isContentTypeSet()) {
            setContentType(this.portletRequest.getResponseContentType());
        }

        gotOutputStream = true;
        return (OutputStream) this.outputStream;
    }

    /**
     * Creates a portlet URL targeting the portlet. If no security modifier is
     * set in the PortletURL the current values are preserved. The current
     * render parameters, portlet mode and window state are preserved.
     * <p>
     * If a request is triggered by the PortletURL, it results in a serve
     * resource request of the <code>ResourceServingPortlet</code> interface.
     * <p>
     * The returned URL can be further extended by adding portlet-specific
     * parameters .
     * <p>
     * The created URL will per default contain the current 
     * cacheability setting of the parent resource. 
     * If no parent resource is available, <code>PAGE</code> is the default.
     * 
     * @since 2.0
     * @return a portlet resource URL
     */
    public ResourceURL createResourceURL() {
        ResourceURL resourceURL = new ResourceURLImpl(
                portletRequest,
                pcRequest,
                getPortletAppDescriptor(),
                getPortletDescriptor(),
                PortletActions.RESOURCE);
        setCacheability(resourceURL);
        callURLGenerationListeners(PortletActions.RESOURCE, resourceURL);
        return resourceURL;
    }

    /**
     * Returns the cache control object allowing to set
     * specific cache settings valid for the markup
     * returned in this response.
     *
     * @return  Cache control for the current response.
     *
     * @since 2.0
     */
    public abstract CacheControl getCacheControl();

    /**
     * Sets the cache level on this resource URL.
     * 
     * @param resourceURL  the resource URL.
     * @throws java.lang.IllegalStateException
     * 			if this resource URL has a weaker cache level
     * 			than the parent resource URL.
     * @throws java.lang.IllegalArgumentException
     * 			if the cacheLevel is unknown to the portlet container
     */
    protected abstract void setCacheability(ResourceURL resourceURL);

}
