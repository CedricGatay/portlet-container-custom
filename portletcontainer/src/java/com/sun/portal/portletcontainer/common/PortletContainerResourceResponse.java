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

package com.sun.portal.portletcontainer.common;

import com.sun.portal.container.GetResourceResponse;
//import javax.portlet.CacheControl;

public class PortletContainerResourceResponse extends PortletContainerResponse {

    private GetResourceResponse response;
    private PortletContainerCacheControl cacheControl;
    
    public PortletContainerResourceResponse(GetResourceResponse response) {
        super(response);
        this.response = response;
        cacheControl = new PortletContainerCacheControl(response.getChannelCacheControl());        
    }

    /**
     * Sets the content type for this response.
     * 
     * @param contentType the content type
     */
    public void setContentType(String contentType) {
        response.setContentType(contentType);
    }
    
    /**
     * Sets the resource content returned from the <code>serveResource()<code> of the portlet.
     * The resource content is present in the OutputStream.
     * 
     * @param bytes the output present in the OutputStream
     **/
    public void setContentAsBytes(byte[] bytes) {
        response.setContentAsBytes(bytes);
    }

    /**
     * Returns the resource content returned from the <code>serveResource()<code> of the portlet.
     * The resource content is present in the OutputStream.
     * 
     * @return the resource content that is present in the OutputStream.
     **/
    public byte[] getContentAsBytes() {
        return response.getContentAsBytes();
    }

    /**
     * Sets the resource content returned from the <code>serveResource()<code> of the portlet.
     * The resource content is present in the Writer.
     * 
     * @param buffer the output present in the Writer
     **/
    public void setContentAsBuffer(StringBuffer buffer) {
        response.setContentAsBuffer(buffer);
    }

    /**
     * Returns the resource content returned from the <code>serveResource()<code> of the portlet.
     * The resource content is present in the Writer.
     * 
     * @return the resource content that is present in the Writer.
     **/
    public StringBuffer getContentAsBuffer() {
        return response.getContentAsBuffer();
    }
    
    /**
     * Retrieves the <CODE>CacheControl</CODE> object.
     *
     * @return The <CODE>CacheControl</CODE> object stored.
     */    
    public PortletContainerCacheControl getCacheControl() {
        return cacheControl;
    }
}
