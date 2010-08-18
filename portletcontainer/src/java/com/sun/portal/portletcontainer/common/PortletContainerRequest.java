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

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Collections;
import javax.portlet.PortletPreferences;

import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.ChannelState;
import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelURLFactory;
import com.sun.portal.container.PortletID;
import com.sun.portal.container.EntityID;
import com.sun.portal.portletcontainer.common.descriptor.SharedSessionAttributeDescriptor;
import java.util.HashMap;

/**
 * <code>PortletContainerRequest</code> encapsulates the containerRequest sent by the
 * portlet container to PAE.  Portlet container is responsible to use the
 * set methods to set the informtaion that is necessary for the PAE to process
 * the containerRequest.  PAE then can use the get methods to get the containerRequest
 * information.
 */
public class PortletContainerRequest {
    
    // key for PortletContainerRequest object in http containerRequest attribute
    public static final String PORTLET_CONTAINER_REQUEST = 
		PortletContainerConstants.PREFIX + "portlet_container_request";
    
    private ContainerRequest containerRequest = null;
    private PortletPreferences preferences = null;
    private List<String> actions = null;
    private String portletName = null;
    private String portletWindowName = null;
    private String responseContentType = null;
    private Map<String, Object> attributes;
	private List<SharedSessionAttributeDescriptor> sharedSessionAttributeDescriptors = null;
	private Map<String, Object> sharedSessionAttributesProcess = null;
    
    public PortletContainerRequest( ContainerRequest containerRequest ) {
        this.containerRequest = containerRequest;
    }
    
    /**
     * Returns the <code>PortletPreferences</code>
     *
     * @return  the <code>PortletPreferences</code>
     * @see javax.portlet.PortletPreferences
     **/
    public PortletPreferences getPortletPreferences() {
        return preferences;
    }

    /**
     * Sets the <code>PortletPreferences</code>
     *
     * @param  preferences     the <code>PortletPreferences</code> to set to
     * @see javax.portlet.PortletPreferences
     */
    public void setPortletPreferences( PortletPreferences preferences ) {
        this.preferences = preferences;
    }
    
    /**
     * Returns the <code>UserInfo</code>
     *
     * @return  the <code>UserInfo</code> Map
     **/
    public Map getUserInfo() {
        return containerRequest.getUserInfo();
    }
    
    /**
     * Sets the <code>UserInfo</code> Map
     *
     * @param  userInfo    the <code>UserInfo</code> to set to
     */
    public void setUserInfo( Map userInfo ) {
        containerRequest.setUserInfo(userInfo);
    }
    
    /**
     * Returns action list that describes what PAE needs to do.
     * Valid actions are ISCACHEVALID, PREPARE, RENDER, ACTION.
     *
     * @return  the action list
     * @see com.sun.portal.portletcontainercommon.PortletActions
     **/
    public List<String> getActions() {
        return actions;
    }
    
    /**
     * Sets the action list that describes what PAE needs to do
     * Valid actions are RENDER, ACTION, EVENT, RESOURCE.
     *
     * @param actions  the actions associated with portlet lifecycle
     */
    public void setActions( List<String> actions ) {
        this.actions = actions;
    }
    
    /**
     * Returns the portlet name
     *
     * @return  the portlet name
     **/
    public String getPortletName() {
        return portletName;
    }
    
    /**
     * Sets the portlet name
     *
     * @param  portletName     the portlet name to set to
     */
    public void setPortletName( String portletName ) {
        this.portletName = portletName;
    }
    
    /**
     * Returns the portlet window name
     *
     * @return  the portlet window name
     **/
    public String getPortletWindowName() {
        return portletWindowName;
    }
    
    /**
     * Sets the portlet window name
     *
     * @param portletWindowName     the portlet window name to set to
     */
    public void setPortletWindowName( String portletWindowName ) {
        this.portletWindowName = portletWindowName;
    }
    
    /**
     * Returns the HttpServletRequest.
     *
     * @return  the HttpServletRequest
     **/
    public HttpServletRequest getHttpServletRequest() {
        return containerRequest.getHttpServletRequest();
    }
    
    /**
     * Returns the current window state of the portlet window.
     *
     * @return  current window state
     **/
    public ChannelState getWindowState() {
        return containerRequest.getWindowState();
    }
    
    /**
     * Returns the current mode of the portlet window.
     *
     * @return  the current portlet window mode
     **/
    public ChannelMode getPortletWindowMode() {
        return containerRequest.getChannelMode();
    }
    
    /**
     * Returns the entityID of the portlet window that this containerRequest is directed to.
     *
     * @return the entity ID
     */
    public EntityID getEntityID() {
        return containerRequest.getEntityID();
    }
    
    /**
     * Returns the ChannelURLFactory class.
     *
     *
     * @return the ChannelURLFactory class.
     */
    public ChannelURLFactory getPortletWindowURLFactory() {
        return containerRequest.getChannelURLFactory();
    }
    
    /**
     * Returns the ID of the current user.
     *
     * @return  the ID of the current user.
     **/
    public String getUserID() {
        return containerRequest.getUserID();
    }
    
    /**
     * Returns the current user principal
     *
     * @return the current user principal
     **/
	public Principal getUserPrincipal() {
		return containerRequest.getUserPrincipal();
	}
    
    /**
     * Returns a <code>List</code> of all the
     * roles of the current user.  Each value in the list is a String
     * representation of a role value.
     *
     * @return  the roles of the current user
     **/
    public List<String> getRoles() {
        return containerRequest.getRoles();
    }
    
    /**
     * Returns a <code>List</code> of all the
     * allowable window states for the current portlet window to be changed to in
     * this user containerRequest.  Each value in the list is a <code>ChannelState
     * </code> object.
     *
     * @return the allowable window states
     */
    public List<ChannelState> getAllowableWindowStates() {
        return containerRequest.getAllowableWindowStates();
    }
    
    /**
     * Returns a <code>List</code> of all the
     * allowable portlet window modes for the current portlet window to be changed to in
     * this user containerRequest.  Each value in the list is a <code>ChannelMode
     * </code> object.
     *
     * @return the allowable portlet window modes
     */
    public List<ChannelMode> getAllowablePortletWindowModes() {
        return containerRequest.getAllowableChannelModes();
    }
    
    /**
     * Returns a <code>List</code> of all the
     * allowable content type for the current portlet window.
     *
     * @return  the allowable content types
     **/
    public List<String> getAllowableContentTypes() {
        return containerRequest.getAllowableContentTypes();
    }
    
    /**
     * Returns the user locale.
     *
     * @return  current user locale.
     **/
    public Locale getLocale() {
        return containerRequest.getLocale();
    }
    
    /**
     * Sets the content type of the response
     *
     * @param responseContentType the content type of the response
     */
    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }
    
    /**
     * Returns the content type of the response
     *
     * @return content type of the response
     */
    public String getResponseContentType() {
        return responseContentType;
    }
    
    /**
     * Returns a Map containing the name and values of the attributes available
     * to this containerRequest. This method returns an empty Map if the containerRequest
     * has no attributes available to it.
     *
     * @param attributes a Map of name and value of the containerRequest's attributes
     */
    public void setAttributes(Map attributes) {
        this.attributes = attributes;
    }
    
    /**
     * Returns a Map containing the name and values of the attributes available
     * to this containerRequest. This method returns an empty Map if the containerRequest
     * has no attributes available to it.
     *
     * @return a Map of name and value of the containerRequest's attributes
     */
    public Map getAttributes() {
        if(this.attributes == null) {
            return Collections.EMPTY_MAP;
        } else {
            return this.attributes;
        }
    }
    
    /**
     * Returns a <code>Map</code> of String properties. 
     * Properties can be used by portlets to access 
     * portal/portlet-container specific properties
     *
     * @return  the properties map
     **/
    public Map<String, List<String>> getProperties() {
        return this.containerRequest.getProperties();
    }

    /**
     * Sets the attributes that are scoped. The attributes that should be
     * available across scopes is set using this method. This is applicable
     * per scope ID.
     * 
     * @param scopeID the scope ID
     * @param scopedAttributes the attributes that are scoped
     */
    public void setScopedAttributes(String scopeID, Map scopedAttributes) {
        this.containerRequest.setScopedAttributes(scopeID, scopedAttributes);
    }
    
    /**
     * Returns a Map containing the name and values of the attributes available 
     * for this scopeID. This method returns an empty Map if the request 
     * has no attributes available to it.
     * 
     * @param scopeID the scope ID
     *
     * @return a Map of name and value of the scoped attributes
     */
    public Map getScopedAttributes(String scopeID) {
        return this.containerRequest.getScopedAttributes(scopeID);
    }
    
    /**
     * Returns a unique id that identifies the instance of the portlet.
     * The value returned by this method is used to prefix or append to
     * elements, such as JavaScript variables or function names, to ensure
     * they are unique in the context of the portal page.
     * If the namespace is not set, returns the default namespace based
     * on EntityID or null if EntityID is null.
     *
     * @return a unique id the identifies the instance of the portlet
     */
    public String getNamespace() {
        return this.containerRequest.getNamespace();
    }
    
    /**
     * Returns the namespace that corresponds to the portletID.
     *
	 * @param portletID the portletID
	 * 
	 * @return the namespace that corresponds to the portletID.
     */
    public String getNamespace(PortletID portletID) {
        return this.containerRequest.getNamespace(portletID);
    }
    
    /**
     * Returns the portlet window ID.
     * If the portlet window ID is not set, returns the default 
     * portlet window ID based on EntityID or null if EntityID is null.
     *
     * @return the portlet window ID.
     */
    public String getWindowID() {
        return this.containerRequest.getWindowID();
    }
    /**
     * Returns information about the portal like vendor, version, etc.
     *
     * @return the information about the portal.
     */
    public String getPortalInfo() {
        return this.containerRequest.getPortalInfo();
    }
    
    /**
     * Sets the HttpSession used by the Portlet
     *
     * @param  session  the HttpSession used by the Portlet
     */
	public void setHttpSession(HttpSession session) {
		this.containerRequest.setHttpSession(session);
	}

	public void setSharedSessionAttributeDescriptors(
			List<SharedSessionAttributeDescriptor> sharedSessionAttributeDescriptors) {
		this.sharedSessionAttributeDescriptors = sharedSessionAttributeDescriptors;
	}

	public List<SharedSessionAttributeDescriptor> getSharedSessionAttributeDescriptors() {
		return this.sharedSessionAttributeDescriptors;
	}

	public void addSharedSessionAttribute(String name, Object value) {
		Map<String, Object> sharedSessionAttributesPublish =
				this.containerRequest.getSharedSessionAttributesPublish();
		if(sharedSessionAttributesPublish == null) {
			sharedSessionAttributesPublish = new HashMap<String, Object>();
		}
		sharedSessionAttributesPublish.put(name, value);
		this.containerRequest.setSharedSessionAttributesPublish(sharedSessionAttributesPublish);
	}

	public void removeSharedSessionAttribute(String name) {
		Map<String, Object> sharedSessionAttributesPublish =
				this.containerRequest.getSharedSessionAttributesPublish();
		if(sharedSessionAttributesPublish != null) {
			sharedSessionAttributesPublish.remove(name);
			this.containerRequest.setSharedSessionAttributesPublish(sharedSessionAttributesPublish);
		}
	}

	public void setSharedSessionAttributesProcess(Map<String, Object> sharedSessionAttributesProcess) {
		this.sharedSessionAttributesProcess = sharedSessionAttributesProcess;
	}

	public Object getSharedSessionAttribute(String name) {
		Object value = null;
		if(this.sharedSessionAttributesProcess != null) {
			value = this.sharedSessionAttributesProcess.get(name);
		}
		return value;
	}
}
