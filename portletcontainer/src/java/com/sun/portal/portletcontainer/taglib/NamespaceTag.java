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

import com.sun.portal.portletcontainer.portlet.impl.PortletRequestConstants;
import java.io.IOException;
import javax.portlet.PortletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;

/**
 * This class implements the Namespace tag. It generates a string value in the portlet
 * namespace and therefore ensures the uniqueness of the string value in the whole
 * portal page.
 * 
 * This class applicable for Portlet v2.0
 */
public class NamespaceTag extends TagSupport {

    @Override
    public int doStartTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        PortletResponse portletResponse = (PortletResponse) request.getAttribute(
                PortletRequestConstants.PORTLET_RESPONSE_ATTRIBUTE);

        try {
            pageContext.getOut().print(portletResponse.getNamespace());
        } catch (IOException e) {
            throw new JspTagException("Error: IOException while writing");
        }
        return SKIP_BODY;
    }
}
