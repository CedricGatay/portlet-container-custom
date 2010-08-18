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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/** 
 * This tag defines a property that may be added to an actionURL, renderURL
 * or resourceURL.
 * 
 * This class applicable for Portlet v2.0
 */ 
public class PropertyTag extends TagSupport{
    private String name;
    private String value;

    @Override
    public int doStartTag() throws JspException {
        // If name is null or empty, no action is performed.
        if (name != null && name.length() > 0) {
            PropertyBaseTag propertyBaseTag = (PropertyBaseTag) findAncestorWithClass(this, PropertyBaseTag.class);
            propertyBaseTag.addProperty(name, value);
        }

        return SKIP_BODY;
    }

    /**
     * Sets the name attribute.
     * This specifies the name of the parameter to add to the URL
     *
     * @param name the name of the parameter to add to the URL
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the value attribute.
     * This specifies the value of the parameter to add to the URL
     *
     * @param value the value of the parameter to add to the URL
     */
    public void setValue(String value) {
        this.value = (value == null ? "" : value);
    }
}
