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
 * [Name of File] [ver.__] [Date]
 * 
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
*/

package com.sun.portal.container;

/**
 * A <code>GetResourceResponse</code> encapsulates the resource response sent by the
 * aggregation engine to the container.
 **/
public interface GetResourceResponse extends ContainerResponse {

    /**
     * Sets the content type for this response.
     * 
     * @param contentType the content type
     */
    public void setContentType(String contentType);    
    
    /**
     * Sets the content type for this response.
     * 
     * @return the content type set on the response.
     */
    public String getContentType();    
    
    /**
     * Sets the resource content returned from the <code>serveResource()<code> of the portlet.
     * The resource content is present in the OutputStream.
     * 
     * @param bytes the output present in the OutputStream
     **/
    public void setContentAsBytes(byte[] bytes);

    /**
     * Returns the resource content returned from the <code>serveResource()<code> of the portlet.
     * The resource content is present in the OutputStream.
     * 
     * @return the resource content that is present in the OutputStream.
     **/
    public byte[] getContentAsBytes();

    /**
     * Sets the resource content returned from the <code>serveResource()<code> of the portlet.
     * The resource content is present in the Writer.
     * 
     * @param buffer the output present in the Writer
     **/
    public void setContentAsBuffer(StringBuffer buffer);

    /**
     * Returns the resource content returned from the <code>serveResource()<code> of the portlet.
     * The resource content is present in the Writer.
     * 
     * @return the resource content that is present in the Writer.
     **/
    public StringBuffer getContentAsBuffer();
    
    /**
     * Returns the cache control object on which various cache settings can be set.
     * valid for the resource returned in this response.
     * 
     * @return  Cache control for the current response.
     */    
    public ChannelCacheControl getChannelCacheControl();    
    
}

