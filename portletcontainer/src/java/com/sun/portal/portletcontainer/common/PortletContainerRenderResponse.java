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

import com.sun.portal.container.GetMarkupResponse;

public class PortletContainerRenderResponse extends PortletContainerResponse {
    
    private GetMarkupResponse response;
    private PortletContainerCacheControl cacheControl;
    
    public PortletContainerRenderResponse( GetMarkupResponse response ) {
        super( response );
        this.response = response;
        cacheControl = new PortletContainerCacheControl(response.getChannelCacheControl());
    }
    
    /**
     * Returns the markup content of the portlet window.
     *
     * @return  markup content of the portlet window
     **/
    public StringBuffer getMarkup() {
        return response.getMarkup();
    }
    
    /**
     * Sets the markup content of the portlet window.
     *
     * @return  markup content of the portlet window
     **/
    public void setMarkup( StringBuffer markup ) {
        response.setMarkup( markup );
    }
    
    /**
     * Returns the title of the portlet window as a <code>String</code>.
     *
     * @return  the title
     **/
    public String getTitle() {
        return response.getTitle();
    }
    
    /**
     * Sets the title of the portlet window.
     *
     * @param  title    the title of the portlet window
     **/
    public void setTitle( String title) {
        response.setTitle( title );
    }
    
    public void setExpirationTime(int expirationTime) {
        this.cacheControl.setExpirationTime(expirationTime);
    }

    
    /**
     * Retrieves the <CODE>PortletContainerCacheControl</CODE> object.
     *
     * @return The <CODE>PortletContainerCacheControl</CODE> object stored.
     */
    public PortletContainerCacheControl getCacheControl() {
        return cacheControl;
    }
}
