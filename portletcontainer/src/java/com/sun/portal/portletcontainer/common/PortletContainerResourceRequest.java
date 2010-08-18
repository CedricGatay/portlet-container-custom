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

import com.sun.portal.container.GetResourceRequest;
import com.sun.portal.container.WindowRequestReader;
import java.util.Map;

public class PortletContainerResourceRequest extends PortletContainerRequest {

    private GetResourceRequest request;

    public PortletContainerResourceRequest(GetResourceRequest request) {
        super(request);
        this.request = request;
    }

    /**
     * Returns the character encoding of the current request.
     *
     * @return  the character encoding of the current request.
     **/
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }


    /**
     * Returns a <code>Map</code> of the resource parameters.
     *
     * @return  the resource parameters map.
     **/
    public Map<String, String[]> getResourceParameters() {
        return request.getResourceParameters();
    }

    /**
     * Returns the resource ID set on the ResourceURL or <code>null</code>
     * if no resource ID was set onj the URL.
     *
     * @return  the resource ID set on the ResourceURL,or <code>null</code>
     *          if no resource ID was set on the URL.
     */
    public String getResourceID() {
        return request.getResourceID();
    }

    /**
     * Returns the cache level set on the ResourceURL.
     *
     * @return the cache level set on the ResourceURL
     **/
    public String getCacheLevel() {
        return request.getCacheLevel();
    }
    
    /**
     * This method provides access to the ETag which can be accessed by the Portlets.
     *
     * @return The ETag as a <CODE>String</CODE>
     */    
    public String getETag() {
        return request.getETag();        
    }        

	   /**
     * This method provides access to the WindowRequestReader that is used to manage parameters
     *
     * @return WindowRequestReader
     */
    public WindowRequestReader getWindowRequestReader() {
        return request.getWindowRequestReader();

    }
}
