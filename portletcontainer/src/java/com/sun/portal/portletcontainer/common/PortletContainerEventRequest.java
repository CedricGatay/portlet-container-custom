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

import com.sun.portal.container.ExecuteEventRequest;
import java.util.Map;

public class PortletContainerEventRequest extends PortletContainerRequest {

    private ExecuteEventRequest request;

    public PortletContainerEventRequest(ExecuteEventRequest request) {
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
     * Returns a <code>Map</code> of the event parameters.
     *
     * @return  the event parameters map.
     **/
    public Map<String, String[]> getEventParameters() {
        return request.getEventParameters();
    }
}
