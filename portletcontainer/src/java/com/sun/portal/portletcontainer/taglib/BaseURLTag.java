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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.portlet.BaseURL;
import javax.portlet.PortletRequest;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * This class provides methods that are common to both PortletURL and ResourceURL.
 * 
 * This class applicable for Portlet v2.0
 */
public abstract class BaseURLTag extends BodyTagSupport implements ParamBaseTag, PropertyBaseTag {
    
    private String var;
    private String secure;
    private boolean escapeXml = true;
    private BaseURL baseURL;
    private List<String> removedParametersList;
    
    @Override
    public void setParent(Tag t) {
        super.setParent(t);
        this.var = null;
        this.secure = null;
        this.baseURL = null;
        this.removedParametersList = new ArrayList<String>();
    }

    /**
     * Sets the escapeXml attribute. This determines whether characters
     * <,>,&,', " in the resulting output should be converted to their corresponding
     * character entity codes
     * 
     * @param escapeXml true if the URL should be XML escaped
     */
    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    /**
     * Returns the escapeXml attribute set on this URL.
     * 
     * @return the escapeXml attribute set on this URL.
     */
    protected boolean isEscapeXml(PortletRequest portletRequest) {
        Boolean escapeXmlValue = (Boolean) portletRequest.getAttribute(PortletRequestConstants.ESCAPE_XML_VALUE);
        if(escapeXmlValue != null) {
            return this.escapeXml && escapeXmlValue.booleanValue();
        } else {
            return this.escapeXml;
        }
    }
            
    /**
     * Sets the secure attribute.
     * This  indicates if the resulting URL should be a
     * secure connection (secure="true") or an insecure one (secure="false").
     * 
     * @param secure "true" if the URL should be a secure connection, "false" for an insecure one.
     */
    public void setSecure(String secure) {
        this.secure = secure;
    }

    /**
     * Returns the secure attribute set on this URL.
     * 
     * @return the secure attribute set on this URL.
     */
    protected String getSecure() {
        return this.secure;
    }
    
    /**
     * Sets the var attribute.
     * 
     * @param var name of the exported scoped variable
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Returns the var attribute set on this URL.
     * 
     * @return the var attribute set on this URL.
     */
    protected String getVar() {
        return this.var;
    }
    
    /**
     * Sets the BaseURL. 
     * 
     * @param baseURL the BaseURL. 
     */
    protected void setBaseURL(BaseURL baseURL) {
        this.baseURL = baseURL;
    }
    
    /**
     * Returns the BaseURL object.
     *
     * @return the BaseURL Object.
     */
    protected BaseURL getBaseURL() {
        return baseURL;
    }    
    
    /**
     * Returns the List of Parameters to be removed.
     *
     * @return the List of Parameters to be removed.
     */
    protected List<String> getRemovedParametersList() {
        return this.removedParametersList;
    }
    
    /**
     * Adds the name and value to the parameter map. 
     * 
     * @param name the parameter name
     * @param value the parameter value
     * 
     */
    public void addParam(String name, String value) {
        // If the value is empty the parameter must be removed from the URL
        if(value.equals("")) {
            value = null;
            getBaseURL().setParameter(name, value);
            this.removedParametersList.add(name);
        } else {
            // If the same name of a parameter occurs more than once within the URL
            // the values must be delivered as parameter value array with
            // the values in the order of the declaration within the URL tag
            // While merging the parameters set on the URL with the private parameters
            // private parameters comes last.
            Map<String, String[]> map = getBaseURL().getParameterMap();
            String[] values = map.get(name);
            if(values != null) {
                List<String> list = new ArrayList(Arrays.asList(values));
                list.add(value);
                values = list.toArray(new String[0]);
                getBaseURL().setParameter(name, values);
            } else {
                getBaseURL().setParameter(name, value);
            }
        }
    }

    /**
     * Adds the name and value to the property map. 
     * 
     * @param name the property name
     * @param value the property value
     * 
     */
    public void addProperty(String name, String value) {
        getBaseURL().addProperty(name, value);
    }
    
    /**
     * This sets any additional parameters corresponding to the specific tag.
     *
     * @param baseURL the BaseURL Object.
     */
    public abstract void setAdditionalParameters(BaseURL baseURL);

}
