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
 

package com.sun.portal.portlet.taglib;


import com.sun.portal.portletcontainer.portlet.impl.PortletRequestConstants;
import com.sun.portal.portletcontainer.taglib.PortletTaglibConstants;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * This class implements the DefineObject tag. It makes available
 * to the JSP variables : renderRequest, renderResponse, and portletConfig.
 * 
 * This class applicable for Portlet v1.0
 */
public class DefineObjectsTag extends TagSupport {
        
    @Override
    public int doStartTag()
        throws JspException {
            
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        
        RenderRequest renderRequest = (RenderRequest)request.getAttribute(
                PortletRequestConstants.PORTLET_REQUEST_ATTRIBUTE);
        RenderResponse renderResponse = (RenderResponse)request.getAttribute(
                PortletRequestConstants.PORTLET_RESPONSE_ATTRIBUTE);
        PortletConfig portletConfig = (PortletConfig)request.getAttribute(
                PortletRequestConstants.PORTLET_CONFIG_ATTRIBUTE);
        
        pageContext.setAttribute(
                PortletTaglibConstants.RENDER_REQUEST_VAR, renderRequest);
        pageContext.setAttribute(
                PortletTaglibConstants.RENDER_RESPONSE_VAR, renderResponse);
        pageContext.setAttribute(
                PortletTaglibConstants.PORTLET_CONFIG_VAR, portletConfig);                
        
        return SKIP_BODY;
    }           
}