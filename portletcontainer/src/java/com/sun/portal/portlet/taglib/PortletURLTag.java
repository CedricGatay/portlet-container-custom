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
import com.sun.portal.portletcontainer.taglib.ParamBaseTag;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.http.HttpServletRequest;

import javax.portlet.PortletURL;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;


/**
 * This class provides methods that are common to both ActionURL and RenderURL.
 * 
 * This class applicable for Portlet v1.0
 */
public abstract class PortletURLTag extends BodyTagSupport implements ParamBaseTag {        
    
    protected String mode;
    protected String state;
    protected String var;
    protected String secure;
    protected PortletURL portletURL;
        
    @Override
    public int doStartTag()
        throws JspException {
            
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        
        RenderResponse renderResponse = (RenderResponse)request.getAttribute(
                PortletRequestConstants.PORTLET_RESPONSE_ATTRIBUTE);
        
        portletURL = createURL(renderResponse);        
        
	try {
            if (mode != null) {              
                portletURL.setPortletMode(new PortletMode(mode.toLowerCase(Locale.ENGLISH)));
            
            }        
            if (state != null) {    
                portletURL.setWindowState(new WindowState(state.toLowerCase(Locale.ENGLISH)));
            }
            if (secure != null) {
                secure = secure.toLowerCase(Locale.ENGLISH);
	        if (secure.equals("true")) {
  	            portletURL.setSecure(true);
		} else if (secure.equals("false")) {
		    portletURL.setSecure(false);
		} else {
		    throw new JspException("invalid value for attribute secure");
		}
            }	    
        } catch (PortletModeException e) {
            throw new JspException("invalid value for attribute mode",e);
        } catch (WindowStateException e) {
	    throw new JspException("invalid value for attribute state",e);
        } catch (PortletSecurityException e) {
	    throw new JspException("invalid value for attribute secure",e);
	}       
        return EVAL_BODY_INCLUDE;
    }       
        

    public int doEndTag() 
        throws JspException {

        String urlString = portletURL.toString();
        if (var == null) {
            try {
                pageContext.getOut().print(urlString);
            } catch (IOException e) {
                throw new JspTagException("Error: IOException while writing");
            }
        } else {
            pageContext.setAttribute(var, urlString);
        }
        return EVAL_PAGE;            
    }
    
    public void setParent (Tag t) {
        super.setParent(t);
        this.mode = null;
        this.state = null;
        this.var = null;
	this.secure = null;
        this.portletURL = null;
    }
        
    public void setPortletMode(String mode) {
        this.mode = mode;
    }
    
    public void setWindowState(String state) {
        this.state = state;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public PortletURL getURL() {
        return portletURL;
    }
   
    public void setSecure(String secure) {
        this.secure = secure;
    }
 
    public void addParam(String name, String value) {
		this.portletURL.setParameter(name, value);
    }
	
    abstract public PortletURL createURL(RenderResponse res);
}
