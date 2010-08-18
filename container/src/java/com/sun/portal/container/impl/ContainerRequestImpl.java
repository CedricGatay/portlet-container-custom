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
 * [Name of File] [ver.__] [Date]
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */

package com.sun.portal.container.impl;

import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;
import com.sun.portal.container.ChannelURLFactory;
import com.sun.portal.container.ContainerUtil;
import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletID;
import com.sun.portal.container.PortletWindowContext;
import com.sun.portal.container.service.policy.PolicyManager;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A <code>ContainerRequest</code> encapsulates the request sent by the
 * aggregation engine to the container.
 **/
public class ContainerRequestImpl implements ContainerRequest {
        
    private HttpServletRequest request;
    private ChannelState windowState = ChannelState.NORMAL;
    private ChannelMode channelMode = ChannelMode.VIEW;
    private ChannelState previousWindowState;
    private ChannelMode previousChannelMode;
    private EntityID entityID;
	private String userID;
    private Principal userPrincipal;
    private Locale locale;
    private List<String> roles;
    private List<ChannelState> allowableWindowStates;
    private List<ChannelMode> allowableChannelModes;
    private List<String> allowableContentTypes;
    private Map<String,String> userInfo;
    private ChannelURLFactory channelURLFactory;
    private String charEncoding;
    private boolean isReadOnly;
    private Map<String, Object> attributes;
    private PortletWindowContext portletWindowContext;
    private Map<String, List<String>> properties;
    private Map<String, Map<String, Serializable>> scopedAttributes;
    private PolicyManager policyManager;
    private String namespace;
    private String windowID;
    private String portalInfo;
	private Map<PortletID, List<String>> portletNamespaces;
	private Map<PortletID, List<String>> portletWindowIDs;
	private List<String> requestSharedAttributes;
	private HttpSession session;
	private Map<String, Object> sharedSessionAttributes;
    
    public HttpServletRequest getHttpServletRequest() {
        return request;
    }
    
    public void setHttpServletRequest( HttpServletRequest request ) {
        this.request = request;
    }
    
	public HttpSession getHttpSession() {
		return this.session;
	}

	public void setHttpSession(HttpSession session) {
		this.session = session;
	}

    public ChannelState getWindowState() {
        return windowState;
    }
    
    public void setWindowState( ChannelState windowState ) {
        this.windowState = windowState;
    }
    
    public ChannelMode getChannelMode() {
        return channelMode;
    }
    
    public void setChannelMode( ChannelMode channelMode ) {
        this.channelMode = channelMode;
    }

    public EntityID getEntityID() {
        return entityID;
    }
    
    public void setEntityID( EntityID entityID ) {
        this.entityID = entityID;
    }
    
    public Locale getLocale() {
		Locale theLocale = this.locale;
		if(theLocale == null && this.request != null) {
			theLocale = this.request.getLocale();
		}
        return theLocale;
    }

    public void setLocale( Locale locale ) {
        this.locale = locale;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID( String userID ) {
        this.userID = userID;
    }

    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public List<ChannelState> getAllowableWindowStates() {
        return allowableWindowStates;
    }
    
    public void setAllowableWindowStates( List<ChannelState> allowableWindowStates ) {
        this.allowableWindowStates = allowableWindowStates;
    }
    
    public List<ChannelMode> getAllowableChannelModes() {
        return allowableChannelModes;
    }
    
    public void setAllowableChannelModes( List<ChannelMode> allowableChannelModes ) {
        this.allowableChannelModes = allowableChannelModes;
    }
    
    public List<String> getAllowableContentTypes() {
        return allowableContentTypes;
    }
    
    public void setAllowableContentTypes( List<String> allowableContentTypes ) {
        this.allowableContentTypes = allowableContentTypes;
    }
    
    public Map<String,String> getUserInfo() {
        return userInfo;
    }
    
    public void setUserInfo( Map<String,String> userInfo ) {
        this.userInfo = userInfo;
    }
    
    public ChannelMode getPreviousChannelMode() {
        return previousChannelMode;
    }
    
    public void setPreviousChannelMode(ChannelMode channelMode) {
        previousChannelMode = channelMode;
    }
    
    public ChannelState getPreviousWindowState() {
        return previousWindowState;
    }
    
    public void setPreviousWindowState( ChannelState windowState ) {
        previousWindowState = windowState;
    }
    
    public ChannelURLFactory getChannelURLFactory() {
        return channelURLFactory;
    }
    
    public void setChannelURLFactory(ChannelURLFactory channelURLFactory) {
        this.channelURLFactory = channelURLFactory;
    }
    
    public void setCharacterEncoding( String charEncoding ) {
        this.charEncoding = charEncoding;
    }
    
    public String getCharacterEncoding() {
        return charEncoding;
    }
    
    public void setIsReadOnly( boolean isReadOnly ) {
        this.isReadOnly = isReadOnly;
    }
    
    public boolean getIsReadOnly() {
        return isReadOnly;
    }
    
    public void setAttribute(String name, Object value) {
        if(attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        attributes.put(name, value);
    }
    
    public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
    }
    
    public Map<String, Object> getAttributes() {
        if(this.attributes == null) {
            return Collections.EMPTY_MAP;
        } else {
            return this.attributes;
        }
    }
    
    public void setPortletWindowContext(PortletWindowContext portletWindowContext) {
        this.portletWindowContext = portletWindowContext;
    }
    
    public PortletWindowContext getPortletWindowContext(){
        return this.portletWindowContext;
    }
    
    public Map<String, List<String>> getProperties() {
        return this.properties;
    }
    
    public void setProperty(String key, String value) {
        if ( key == null ) {
            throw new IllegalArgumentException("Property name should not be null.");
        }
        if(properties == null) {
            properties = new HashMap<String, List<String>>();
        }
        List<String> values = new ArrayList();
        values.add(value);
        properties.put(key, values);
    }
    
    public void addProperty(String key, String value) {
        if ( key == null ) {
            throw new IllegalArgumentException("Property name should not be null.");
        }
        
        List<String> values = null;
        if(properties == null) {
            properties = new HashMap<String, List<String>>();
        } else {
            values = properties.get(key);
        }
        
        if(values == null) {
            values = new ArrayList<String>();
        }
        values.add(value);
        properties.put(key, values);
    }

    public void setScopedAttributes(String scopeID, Map<String, Serializable> scopedAttributes) {
        if(this.scopedAttributes == null) {
            this.scopedAttributes = new HashMap<String, Map<String, Serializable>>();
        }
        this.scopedAttributes.put(scopeID, scopedAttributes);
    }
    
    public Map<String, Serializable> getScopedAttributes(String scopeID) {
        Map<String, Serializable> localScopedAttributes = null;
        if(this.scopedAttributes != null) {
            localScopedAttributes = this.scopedAttributes.get(scopeID);
        }
        if(localScopedAttributes != null) {
            return localScopedAttributes;
        } else {
            return Collections.emptyMap();
        }
    }
    
    public PolicyManager getPolicyManager() {
        return this.policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager ) {
        this.policyManager = policyManager;
    }
    
    public String getNamespace() {
        if(this.namespace == null) {
            if(this.entityID != null) {
                this.namespace = getJsSafeDefaultNamespace();
            }
        }
        return this.namespace;
    }

    private String getJsSafeDefaultNamespace() {
        return ContainerUtil.getJavascriptSafeName(this.entityID.toString());
    }
    
    public void setNamespace(String namespace) {
        this.namespace = ContainerUtil.getJavascriptSafeName(namespace);
    }

    public String getWindowID() {
        if(this.windowID == null) {
            if(this.entityID != null) {
                this.windowID = this.entityID.toString();
            }
        }
        return this.windowID;
    }

    public void setWindowID(String windowID) {
        this.windowID = windowID;
    }

    public String getPortalInfo() {
        return this.portalInfo;
    }

    public void setPortalInfo(String portalInfo) {
        this.portalInfo = portalInfo;
    }

	public String getNamespace(PortletID portletID) {
		if(this.portletNamespaces != null) {
			List<String> namespaces = this.portletNamespaces.get(portletID);
			if(namespaces != null && !namespaces.isEmpty()) {
				return namespaces.get(0);
			}
		}
		return null;
	}

	public Map<PortletID, List<String>> getPortletNamespaces() {
		return this.portletNamespaces;
	}

	public void setPortletNamespaces(Map<PortletID, List<String>> portletNamespaces) {
		this.portletNamespaces = portletNamespaces;
	}
	
	public String getWindowID(PortletID portletID) {
		if(this.portletWindowIDs != null) {
			List<String> windowIDs = this.portletWindowIDs.get(portletID);
			if(windowIDs != null && !windowIDs.isEmpty()) {
				return windowIDs.get(0);
			}
		}
		return null;
	}

	public Map<PortletID, List<String>> getPortletWindowIDs() {
		return this.portletWindowIDs;
	}
	
	public void setPortletWindowIDs(Map<PortletID, List<String>> portletWindowIDs) {
		this.portletWindowIDs = portletWindowIDs;
	}

	public List<String> getRequestSharedAttributes() {
		return this.requestSharedAttributes;
	}

	public void setRequestSharedAttributes(List<String> requestSharedAttributes) {
		this.requestSharedAttributes = requestSharedAttributes;
	}

	public void setSharedSessionAttributesPublish(Map<String, Object> sharedSessionAttributes) {
		this.sharedSessionAttributes = sharedSessionAttributes;
	}

	public Map<String, Object> getSharedSessionAttributesPublish() {
		return this.sharedSessionAttributes;
	}

}