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

import com.sun.portal.portletcontainer.common.URLHelper;
import com.sun.portal.portletcontainer.portlet.impl.PortletRequestConstants;
import java.io.IOException;
import java.util.Locale;
import javax.portlet.BaseURL;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;

/**
 * This class implements the ResourceURL tag. It allows the creation
 * of an resource URL.
 * 
 * This class applicable for Portlet v2.0
 */
public class ResourceURLTag extends BaseURLTag {

    private String id;
    private String cacheability;

    @Override
    public int doStartTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        PortletResponse portletResponse = (PortletResponse) request.getAttribute(
                PortletRequestConstants.PORTLET_RESPONSE_ATTRIBUTE);

        ResourceURL resourceURL = createURL(portletResponse);
        if(resourceURL == null) {
            throw new JspException("Response must be ResourceResponse");
        }

        try {

            if (cacheability != null) {
                resourceURL.setCacheability(cacheability);
            }
            if (id != null) {
                resourceURL.setResourceID(id);
            }

            if (getSecure() != null) {
                String secure = getSecure().toLowerCase(Locale.ENGLISH);
                if (secure.equals("true")) {
                    resourceURL.setSecure(true);
                } else if (secure.equals("false")) {
                    resourceURL.setSecure(false);
                } else {
                    throw new JspException("invalid value for attribute secure");
                }
            }
            setBaseURL(resourceURL);
            
        } catch (PortletSecurityException e) {
            throw new JspException("invalid value for attribute secure", e);
        }
        return EVAL_BODY_INCLUDE;
    }


    @Override
    public int doEndTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        PortletRequest portletRequest = (PortletRequest) request.getAttribute(
                PortletRequestConstants.PORTLET_REQUEST_ATTRIBUTE);

        String urlString = getBaseURL().toString();
        if (isEscapeXml(portletRequest)) {
            urlString = URLHelper.escapeURL(urlString);
        }

        if (getVar() == null) {
            try {
                pageContext.getOut().print(urlString);
            } catch (IOException e) {
                throw new JspTagException("Error: IOException while writing");
            }
        } else {
            pageContext.setAttribute(getVar(), urlString);
        }

        return EVAL_PAGE;
    }

    @Override
    public void setParent(Tag t) {
        super.setParent(t);
        this.id = null;
        this.cacheability = null;
    }

    public ResourceURL createURL(PortletResponse portletResponse) {
        if(portletResponse instanceof RenderResponse) {
            return ((RenderResponse)portletResponse).createResourceURL();
        } else if(portletResponse instanceof ResourceResponse) {
            return ((ResourceResponse)portletResponse).createResourceURL();
        } else {
            return null;
        }
    }

    /**
     * Sets the id attribute. 
     * This sets the ID for this resource
     * 
     * @param id the ID for this resource
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the cacheability attribute. 
     * This defines the cacheability of the markup returned by this resource URL
     * 
     * @param cacheability valid values are "FULL", "PORTLET", "PAGE"
     */
    public void setCacheability(String cacheability) {
        this.cacheability = cacheability;
    }

    public void setAdditionalParameters(BaseURL baseURL) {
        //No additional parameters present
    }
}
