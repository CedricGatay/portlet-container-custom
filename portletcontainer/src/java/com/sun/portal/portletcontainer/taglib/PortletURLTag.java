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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.portlet.PortletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.http.HttpServletRequest;
import javax.portlet.PortletURL;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;


/**
 * This class provides methods that are common to both ActionURL and RenderURL.
 * 
 * This class applicable for Portlet v2.0
 */
public abstract class PortletURLTag extends BaseURLTag {

    protected String portletMode;
    protected String windowState;
    protected boolean copyCurrentRenderParameters = false;
    
    @Override
    public int doStartTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        PortletResponse portletResponse = (PortletResponse) request.getAttribute(
                PortletRequestConstants.PORTLET_RESPONSE_ATTRIBUTE);

        PortletURL portletURL = createURL(portletResponse);
        if(portletURL == null) {
            throw new JspException("Response must be either RenderResponse or ResourceResponse");
        }

        try {
            if (portletMode != null) {
                portletURL.setPortletMode(new PortletMode(portletMode.toLowerCase(Locale.ENGLISH)));
            }
            if (windowState != null) {
                portletURL.setWindowState(new WindowState(windowState.toLowerCase(Locale.ENGLISH)));
            }
            if (getSecure() != null) {
                String secure = getSecure().toLowerCase(Locale.ENGLISH);
                if (secure.equals("true")) {
                    portletURL.setSecure(true);
                } else if (secure.equals("false")) {
                    portletURL.setSecure(false);
                } else {
                    throw new JspException("invalid value for attribute secure");
                }
            }
            setBaseURL(portletURL);
            
        } catch (PortletModeException e) {
            throw new JspException("invalid value for attribute mode", e);
        } catch (WindowStateException e) {
            throw new JspException("invalid value for attribute state", e);
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

        if (copyCurrentRenderParameters) {
            // Convert immutable map to mutable map
            copyCurrentRenderParameters(new HashMap(portletRequest.getPrivateParameterMap()));
            getRemovedParametersList().clear();
        }
        setAdditionalParameters(getBaseURL());
        String urlString = getBaseURL().toString();
        
        if (isEscapeXml(portletRequest)) {
            urlString = URLHelper.escapeURL(urlString);
        }

        if (getVar() == null) {
            try {
                pageContext.getOut().print(urlString);
            } catch (IOException e) {
                throw new JspTagException("Error: IOException while writing: " + e.getMessage());
            }
        } else {
            pageContext.setAttribute(getVar(), urlString);
        }
        return EVAL_PAGE;
    }

    @Override
    public void setParent(Tag t) {
        super.setParent(t);
        this.portletMode = null;
        this.windowState = null;
    }

    /**
     * Sets the portletMode attribute.
     * This indicates the portlet mode that the 
     * portlet must have when this link is executed
     *
     * @param portletMode the Portlet Mode
     */
    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    /**
     * Sets the windowState attribute.
     * This indicates indicates the window state that the
     * portlet should have when this link is executed
     *
     * @param windowState the Window State
     */
    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    /**
     * Sets the copyCurrentRenderParameters attribute.
     * If set to true, requests that the private render parameters of the 
     * portlet of the current request must attached to this URL.
     *
     * @param copyCurrentRenderParameters true if private render parameters should be attached to this URL
     */
    public void setCopyCurrentRenderParameters(boolean copyCurrentRenderParameters) {
        this.copyCurrentRenderParameters = copyCurrentRenderParameters;
    }

    private void copyCurrentRenderParameters(Map<String, String[]> renderParameterMap){
        // Remove the removedParameters from the Map
        if(!getRemovedParametersList().isEmpty()) {
            for(String name: getRemovedParametersList()) {
                renderParameterMap.remove(name);
            }
        }
        Map<String, String[]> parameterMap = new HashMap<String, String[]>(getBaseURL().getParameterMap());
        Set<Map.Entry<String, String[]>> entries = renderParameterMap.entrySet();
        // Merge the parameterMap and current render parameterMap
        for(Map.Entry<String, String[]> mapEntry : entries) {
            String renderParameterKey = mapEntry.getKey();
            String[] renderParameterValues = mapEntry.getValue();
            String[] parameterValues = parameterMap.get(renderParameterKey);
            if(parameterValues != null) {
                List<String> renderParameterValuesList = new ArrayList(Arrays.asList(renderParameterValues));
                List<String> parameterValuesList = new ArrayList(Arrays.asList(parameterValues));
                parameterValuesList.addAll(renderParameterValuesList);
                parameterMap.put(renderParameterKey,parameterValuesList.toArray(new String[0]));
            } else {
                parameterMap.put(renderParameterKey, renderParameterValues);
            }
        }
        getBaseURL().setParameters(parameterMap);
    }

    /**
     * This creates the PortletURL depending on the specific tag.
     * @param portletResponse
     * @return PortletURL
     */
    public abstract PortletURL createURL(PortletResponse portletResponse);
}
