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
 

package com.sun.portal.portletcontainer.taglib;

import javax.portlet.ActionRequest;
import javax.portlet.BaseURL;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;

/**
 * This class implements the ActionURL tag. It allows the creation
 * of an action URL.
 * 
 * This class applicable for Portlet v2.0
 */
public class ActionURLTag extends PortletURLTag {
        
    private String name;

    public PortletURL createURL(PortletResponse portletResponse) {
        if(portletResponse instanceof RenderResponse) {
            return ((RenderResponse)portletResponse).createActionURL();
        } else if(portletResponse instanceof ResourceResponse) {
            return ((ResourceResponse)portletResponse).createActionURL();
        } else {
            return null;
        }
    }

    public void setAdditionalParameters(BaseURL baseURL) {
        if(getName() != null) {
            baseURL.setParameter(ActionRequest.ACTION_NAME, getName());
        }
    }

    /**
     * Returns the name attribute set on this URL.
     *
     * @return the name attribute set on this URL.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute.
     * This specifies the name of the action that can be used by GenericPortlet 
     * to dispatch to methods annotated with ProcessAction
     *
     * @param name the name of the action
     */
    public void setName(String name) {
        this.name = name;
    }

}