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

import com.sun.portal.portletcontainer.appengine.StringServletOutputStream;
import com.sun.portal.portletcontainer.common.PortletContainerResourceRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResourceResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptorConstants;
import java.io.StringWriter;
import java.util.Locale;
import javax.portlet.CacheControl;
import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class provides implementation of the ResourceResponse interface.
 */
public class ResourceResponseImpl extends MimeResponseImpl implements ResourceResponse{
    private PortletContainerResourceRequest pcResourceRequest;
    private PortletContainerResourceResponse pcResourceResponse;
    private ResourceRequest resourceRequest;
    
    /**
     * Initialize the global variables.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcResourceRequest     The <code>PortletContainerResourceRequest</code>
     * @param pcResourceResponse     The <code>PortletContainerResourceResponse</code>
     * @param resourceRequest     The <code>ResourceRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * @param stringWriter     The <code>StringWriter</code>
     * @param outputStream     The <code>StringServletOutputStream</code>
     *
     */
     protected void init( HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerResourceRequest pcResourceRequest,
            PortletContainerResourceResponse pcResourceResponse,
            ResourceRequest resourceRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor,
            StringWriter stringWriter,
            StringServletOutputStream outputStream) {
        
        super.init(request, response, pcResourceRequest, pcResourceResponse, 
                resourceRequest, portletAppDescriptor, portletDescriptor, 
                stringWriter, outputStream);
        this.pcResourceRequest = pcResourceRequest;
        this.pcResourceResponse = pcResourceResponse;
        this.resourceRequest = resourceRequest;
     }
     
    /**
     * Clears the global variables.
     */
    @Override
    protected void clear() {
        super.clear();

        this.pcResourceRequest = null;
        this.pcResourceResponse = null;
        this.resourceRequest = null;
    }

    /**
     * Sets the locale of the response, setting the headers 
     * (including the Content-Type's charset) as appropriate. 
     * This method should be called before a call to getWriter(). 
     * By default, the response locale is the default locale provided
     * by the portlet container.
     * 
     * @param loc  the new locale of the response
     */
    public void setLocale(Locale loc) {
        getHttpServletResponse().setLocale(loc);
    }

    /**
     * Sets the character encoding (MIME charset) of the response being 
     * sent to the client, for example, to UTF-8. If the character encoding 
     * has already been set by either the portlet container,
     * <code>setContentType(java.lang.String)</code> or 
     * <code>setLocale(java.util.Locale)</code>, this method overrides it. Calling 
     * <code>setContentType(java.lang.String)</code> with the String  of 
     * <code>text/html</code> and calling this method with the String of 
     * <code>UTF-8</code> is equivalent with calling <code>setContentType</code> 
     * with the String of <code>text/html; charset=UTF-8</code>.
     * <p>
     * This method can be called repeatedly to change the character encoding. 
     * This method has no effect if it is called after getWriter has been called 
     * or after the response has been committed.
     * 
     * @param charset a String specifying only the character set defined by 
     *                IANA Character Sets (http://www.iana.org/assignments/character-sets)
     */
    public void setCharacterEncoding(String charset) {
        getHttpServletResponse().setCharacterEncoding(charset);
    }

    /**
     * Sets the length of the content body in the response.
     * 
     * @param len an integer specifying the length of the content being returned
     */
    public void setContentLength(int len) {
        getHttpServletResponse().setContentLength(len);
    }
    
    @Override
    public void setContentType(String type) {
        contentType = type;
        getHttpServletResponse().setContentType(type);
        // In some portal, the response may be wrapped and setContentType may not be
        // honoured, hence set it on GetResourceResponse via PortletContainerResourceResponse
        // so that in the portal it can be obtained from GetResourceResponse
        pcResourceResponse.setContentType(type);
    }

    protected void setCacheability(ResourceURL resourceURL) {
        String cacheLevel = this.pcResourceRequest.getCacheLevel();
        if(cacheLevel == null || cacheLevel.trim().length() == 0)
            resourceURL.setCacheability(ResourceURL.PAGE);
        else
            resourceURL.setCacheability(cacheLevel);
    }
    
    @Override
    public PortletURL createActionURL() throws IllegalStateException {
        
        String cacheLevel = this.pcResourceRequest.getCacheLevel();
        if(cacheLevel != null && !cacheLevel.equals(ResourceURL.PAGE))
            throw new IllegalStateException("The cacheability level of the resource URL triggering this" +
                    " serveResource call is not PAGE and thus does not allow for creating action URLs.");
        return super.createActionURL();
    }
    
    @Override
    public PortletURL createRenderURL() throws java.lang.IllegalStateException {
        String cacheLevel = this.pcResourceRequest.getCacheLevel();
        if(cacheLevel != null && !cacheLevel.equals(ResourceURL.PAGE))
            throw new IllegalStateException("The cacheability level of the resource URL triggering this" +
                    " serveResource call is not PAGE and thus does not allow for creating render URLs.");
        return super.createRenderURL();
    }
  
    @Override
    public void setProperty(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Property name should not be null.");
        }

        if (key.equals(EXPIRATION_CACHE)) {
            int cacheVal = pcResourceResponse.getCacheControl().getExpirationTime();
            if (cacheVal != PortletDescriptorConstants.EXPIRATION_CACHE_NOT_DEFINED) {
                try {
                    cacheVal = Integer.parseInt(value);
                } catch (NumberFormatException ne) {
                    //cache value do not change
                }
                pcResourceResponse.getCacheControl().setExpirationTime(cacheVal);
            }
        } else if (key.equals(CACHE_SCOPE)) {
            if (pcResourceResponse.getCacheControl().getExpirationTime() != PortletDescriptorConstants.EXPIRATION_CACHE_NOT_DEFINED) {
                pcResourceResponse.getCacheControl().setPublicScope(PUBLIC_SCOPE.equals(value));
            }
        } else if (key.equals(PUBLIC_SCOPE)) {
            pcResourceResponse.getCacheControl().setPublicScope("true".equals(value));
        } else if (key.equals(PRIVATE_SCOPE)) {
            pcResourceResponse.getCacheControl().setPublicScope("false".equals(value));
        } else if (key.equals(ETAG)) {
            pcResourceResponse.getCacheControl().setETag(value);
        } else if (key.equals(USE_CACHED_CONTENT)) {
            boolean useCachedContent = Boolean.parseBoolean(value);
            pcResourceResponse.getCacheControl().setUseCachedContent(useCachedContent);
        } else {
            super.setProperty(key, value);
        }
    }
    
    public CacheControl getCacheControl() {
        return new CacheControlImpl(pcResourceResponse.getCacheControl());
    }    
}
