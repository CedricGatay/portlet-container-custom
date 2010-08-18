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

import com.sun.portal.container.ChannelMode;
import com.sun.portal.portletcontainer.appengine.PortletAppEngineUtils;
import com.sun.portal.portletcontainer.appengine.StringServletOutputStream;
import com.sun.portal.portletcontainer.common.PortletContainerConstants;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptorConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.io.StringWriter;
import java.util.List;
import javax.portlet.PortletMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import com.sun.portal.portletcontainer.common.PortletContainerRenderRequest;
import com.sun.portal.portletcontainer.common.PortletContainerRenderResponse;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import javax.portlet.CacheControl;
import javax.portlet.ResourceURL;

/**
 * This class provides implementation of the RenderResponse interface.
 */
public class RenderResponseImpl extends MimeResponseImpl implements RenderResponse {

    private RenderRequest renderRequest;
    private HttpServletResponse response;
    private PortletContainerRenderRequest pcRenderRequest;
    private PortletContainerRenderResponse pcRenderResponse;
    private Collection<PortletMode> portletModes;
    
    /**
     * Initialize the global variables.
     * <P>
     * @param request The <code>HttpServletRequest</code> object
     * @param response The <code>HttpServletResponse</code> object
     * @param pcRenderRequest     The <code>PortletContainerRenderRequest</code>
     * @param pcRenderResponse     The <code>PortletContainerRenderResponse</code>
     * @param renderRequest     The <code>RenderRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     * @param stringWriter     The <code>StringWriter</code>
     * @param outputStream     The <code>StringServletOutputStream</code>
     *
     */
    protected void init(HttpServletRequest request, 
            HttpServletResponse response,
            PortletContainerRenderRequest pcRenderRequest,
            PortletContainerRenderResponse pcRenderResponse, 
            RenderRequest renderRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor,
            StringWriter stringWriter,
            StringServletOutputStream outputStream) {

        super.init(request, response, pcRenderRequest, pcRenderResponse, 
                renderRequest, portletAppDescriptor, portletDescriptor, 
                stringWriter, outputStream);
        this.response = response;
        this.renderRequest = renderRequest;
        this.pcRenderRequest = pcRenderRequest;
        this.pcRenderResponse = pcRenderResponse;
    }

    /**
     * Clears the global variables.
     */
    @Override
    protected void clear() {
        super.clear();

        this.response = null;
        this.renderRequest = null;
        this.pcRenderRequest = null;
        this.pcRenderResponse = null;
    }

    /**
     * This method sets the title of the portlet.
     * <p>
     * The value can be a text String
     *
     * @param  title    portlet title as text String or resource URI
     */
    public void setTitle(String title) {
		//Do not set the title after the response is committed
		if(isCommitted())
			return;
		
        // If the title returned from getTitle method of GenericPortlet is same as the input title, use the
        // title set in PAE or use the input title
        String genericPortletTitle = (String)renderRequest.getAttribute(PortletContainerConstants.TITLE_FROM_GENERIC_PORTLET);
        if(genericPortletTitle != null && genericPortletTitle.equals(title)){
            String tmpTitle = (String)renderRequest.getAttribute(PortletContainerConstants.PORTLET_TITLE);
            if(tmpTitle != null) {
                title = tmpTitle;
            }
        }
        pcRenderResponse.setTitle(title);
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
    @Override
    public void setProperty(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Property name should not be null.");
        }

        if (key.equals(EXPIRATION_CACHE)) {
            int cacheVal = pcRenderResponse.getCacheControl().getExpirationTime();
            if (cacheVal != PortletDescriptorConstants.EXPIRATION_CACHE_NOT_DEFINED) {
                try {
                    cacheVal = Integer.parseInt(value);
                } catch (NumberFormatException ne) {
                    //cache value do not change
                }
                pcRenderResponse.getCacheControl().setExpirationTime(cacheVal);
            }
        } else if (key.equals(CACHE_SCOPE)) {
            if (pcRenderResponse.getCacheControl().getExpirationTime() != PortletDescriptorConstants.EXPIRATION_CACHE_NOT_DEFINED) {
                pcRenderResponse.getCacheControl().setPublicScope(PUBLIC_SCOPE.equals(value));
            }
        } else if (key.equals(PUBLIC_SCOPE)) {
            pcRenderResponse.getCacheControl().setPublicScope("true".equals(value));
        } else if (key.equals(PRIVATE_SCOPE)) {
            pcRenderResponse.getCacheControl().setPublicScope("false".equals(value));
        } else if (key.equals(ETAG)) {
            pcRenderResponse.getCacheControl().setETag(value);
        } else if (key.equals(USE_CACHED_CONTENT)) {
            boolean useCachedContent = Boolean.parseBoolean(value);
            pcRenderResponse.getCacheControl().setUseCachedContent(useCachedContent);
        } else {
            super.setProperty(key, value);
        }
    }

    /**
     * This method allows the portlet to tell the portal the next possible
     * portlet modes that the make sense from the portlet point of view.
     * <p>
     * If set, the portal should honor these enumeration of portlet modes and
     * only provide the end user with choices to the provided portlet modes or a
     * subset of these modes based on access control considerations.
     * <p>
     * If the portlet does not set any next possible portlet modes the default
     * is that all portlet modes that the portlet has defined supporting in the
     * portlet deployment descriptor are meaningful new portlet modes.
     *
     * @param portletModes
     *            <code>Enumeration</code> of <code>PortletMode</code> objects with the
     *            next possible portlet modes that the make sense from the
     *            portlet point of view, must not be <code>null</code> or an
     *            empty enumeration.
     * @since 2.0
     */
    public void setNextPossiblePortletModes(Collection<PortletMode> portletModes) {
        if(portletModes == null || portletModes.isEmpty()) {
            List<ChannelMode> channelModes = this.pcRenderRequest.getAllowablePortletWindowModes();
            this.portletModes = new ArrayList<PortletMode>(channelModes.size());
            for(ChannelMode channelMode: channelModes){
                this.portletModes.add(PortletAppEngineUtils.getPortletMode(channelMode));
            }
        } else {
            PortletMode[] inputPortletModes = (PortletMode[])portletModes.toArray(new PortletMode[0]);
            PortletMode[] outputPortletModes = new PortletMode[inputPortletModes.length];
            System.arraycopy(inputPortletModes, 0, outputPortletModes, 0, inputPortletModes.length);
            this.portletModes = Arrays.asList(outputPortletModes);
        }
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
    public CacheControl getCacheControl() {
        return new CacheControlImpl(pcRenderResponse.getCacheControl());
    }
    
    protected void setCacheability(ResourceURL resourceURL) {
        resourceURL.setCacheability(ResourceURL.PAGE);
    }
}
