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

package com.sun.portal.container;

import com.sun.portal.container.service.policy.PolicyManager;
import java.io.Serializable;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpSession;

/**
 * A <code>ContainerRequest</code> encapsulates the request sent by the
 * aggregation engine to the container.
 **/
public interface ContainerRequest {

    /**
     * Sets the HttpServletRequest.
     *
     * @param  request   the HttpServletRequest to be set to
     */
    public void setHttpServletRequest( HttpServletRequest request );
    
    /**
     * Returns the HttpServletRequest.
     *
     * @return  the HttpServletRequest
     **/
    public HttpServletRequest getHttpServletRequest();

    /**
     * Sets the HttpSession.
     *
     * @param  session   the HttpSession to be set to
     */
    public void setHttpSession(HttpSession session);

    /**
     * Returns the HttpSession.
     *
     * @return  the HttpSession
     **/
    public HttpSession getHttpSession();

    /**
     * Sets the current window state of the channel.
     *
     * @param  windowState   the window state to be set to
     */
    public void setWindowState( ChannelState windowState );

    /**
     * Returns the current window state of the channel.
     *
     * @return  current window state
     **/
    public ChannelState getWindowState();

    /**
     * Sets the current mode of the channel.
     *
     * @param  channelMode   the channel mode to be set to
     */
    public void setChannelMode( ChannelMode channelMode ) ;
    
    /**
     * Returns the current mode of the channel.
     *
     * @return  the current channel mode
     **/
    public ChannelMode getChannelMode();

    /**
     * Sets the entityID of the channel
     *
     * @param entityID entityID of the channel
     */
    public void setEntityID(EntityID entityID);
    
    /**
     * Returns the entityID of the channel that this request is directed to.
     *
     * @return  the entity ID
     **/
    public EntityID getEntityID();

    /**
     * Sets the ID of the current user, or <code>null</code> if
     * authless user.
     *
     * @param userID the ID of the current user
     **/
    public void setUserID(String userID);

    /**
     * Returns the ID of the current user, or <code>null</code> if
     * authless user.
     *
     * @return  the userID
     **/
    public String getUserID();

    /**
     * Sets the current user principal..
     *
     * @param userPrincipal the current user principal.
     **/
    public void setUserPrincipal(Principal userPrincipal);

    /**
     * Returns the current user principal.
     *
     * @return  the current user principal
     **/
    public Principal getUserPrincipal();

    /**
     * Sets the user locale.
     *
     * @param  locale the user locale
     */
    public void setLocale( Locale locale );

    /**
     * Returns the user locale.
     *
     * @return  current user locale.
     *
     **/
    public Locale getLocale();

    /**
     * Sets a role <code>List</code> of all the
     * roles of the current user.  Each value in the list is a String
     * representation of a role value.
     *
     * @param  roles   the roles of the current user
     */
    public void setRoles( List<String> roles );

    /**
     * Returns an <code>List</code> of all the roles of the current user,
     * or <code>null</code> if authless user or Roles information not available.
     * Each value in the list is a String representation of a role value.
     *
     * @return  the roles of the current user
     **/
    public List<String> getRoles();

    /**
     * Set the UserInfo map.
     *
     * @param  userInfo   the map that holds the user related information
     **/
    public void setUserInfo( Map<String,String> userInfo );
    
    /**
     * Returns a <code>Map</code> of user related information, or
     * <code>null</code> if authless user or UserInfo not available.
     *
     * @return  the UserInfo map
     **/
    public Map<String,String> getUserInfo();

    /**
     * Sets a window states <code>List</code> of all
     * the allowable window states for the current channel to be changed to in 
     * this user request.  Each value in the list is a <code>ChannelState
     * </code> object.
     * 
     * @param allowableWindowStates   all the allowable window state
     */
    public void setAllowableWindowStates( List<ChannelState> allowableWindowStates );
    
    /**
     * Returns an <code>List</code> of all the
     * allowable window states for the current channel to be changed to in 
     * this user request.  Each value in the list is a <code>ChannelState
     * </code> object.
     * 
     * 
     * @return the allowable window states
     */
    public List<ChannelState> getAllowableWindowStates();

    /**
     * Sets a channel mode <code>List</code> of all
     * the allowable channel modes for the current channel to be changed to in 
     * this user request.  Each value in the list is a <code>ChannelMode
     * </code> object.
     *
     * @param  allowableChannelModes   all the allowable channel mode
     **/
    public void setAllowableChannelModes( List<ChannelMode> allowableChannelModes );
    
    /**
     * Returns an <code>List</code> of all the
     * allowable channel modes for the current channel to be changed to in 
     * this user request.  Each value in the list is a <code>ChannelMode
     * </code> object.
     *
     * @return  the allowable channel modes
     **/
    public List<ChannelMode> getAllowableChannelModes();


    /**
     * Sets a content type <code>List</code> of all
     * the allowable content type for the current channel. 
     *
     * @param  allowableContentTypes   all the allowable content type
     **/
    public void setAllowableContentTypes( List<String> allowableContentTypes );
    
    /**
     * Returns an <code>List</code> of all the
     * allowable content type for the current channel. 
     *
     * @return  the allowable content types
     **/
    public List<String> getAllowableContentTypes() ;

    /**
     * Sets the ChannelURLFactory 
     *
     * @param  channelURLFactory   the ChannelURLFactory impl to be used. 
     */
    public void setChannelURLFactory(ChannelURLFactory channelURLFactory);
    
    /**
     * Returns the instance of ChannelURLFactory used by the 
     * caller to help container manage URLs.
     *
     * @return instance of ChannelURLFactory used by caller 
     **/
    public ChannelURLFactory getChannelURLFactory();

    /**
     * Sets the PortletWindowContext 
     *
     * @param  portletWindowContext   the PortletWindowContext impl to be used. 
     */
    public void setPortletWindowContext(PortletWindowContext portletWindowContext);
    
    /**
     * Returns the instance of PortletWindowContext used by the 
     * caller to help container get portal specific data
     *
     * @return instance of PortletWindowContext used by caller 
     **/
    public PortletWindowContext getPortletWindowContext();


    /**
     * Sets the character encoding of the current request.
     *
     * @param  charEncoding    the character encoding of the request
     */
    public void setCharacterEncoding( String charEncoding );

    /**
    * Returns the character encoding of the current request.
    *
    * @return  the character encoding
    **/
    public String getCharacterEncoding();

    /**
     * Sets whether change in persistent is allowed in the request.
     *
     * @param  isReadOnly   whether this request should be read only
     */
    public void setIsReadOnly( boolean isReadOnly );

    /**
     * Returns whether the request is allowed to cause any persistence
     * change during execution.
     *
     * @return read only value
     **/
    public boolean getIsReadOnly();

    /**
     * Sets the attribute. Any attribute that is set using this method can be
     * obtained using PortletRequest's getAttribute method
     * 
     * @param name name of the attribute
     * @param value value of the attribute
     */
    public void setAttribute(String name, Object value);

    /**
     * Sets the attributes. Any attribute that is set using this method can be
     * obtained using PortletRequest's getAttribute method
     * 
	 * @param attributes attributes to be set on this request
     */
    public void setAttributes(Map<String, Object> attributes);
   
    /**
     * Returns a Map containing the name and values of the attributes available 
     * to this request. This method returns an empty Map if the request 
     * has no attributes available to it.
     * 
     * @return a Map of name and value of the request's attributes
     */
    public Map<String, Object> getAttributes();
   
    /**
     * Returns a <code>Map</code> of String properties. 
     * Properties can be used by portlets to access 
     * portal/portlet-container specific properties
     *
     * @return  the properties map
     **/
    public Map<String, List<String>> getProperties();
    
    /**
     * Sets a String property to be sent to the portlet.
     *
     * Properties can be used by portlets to access 
     * portal/portlet-container specific properties
     *
     * This method resets all properties previously added with the
     * same key.
     *
     * @param  key    the key of the property to be returned to the portlet
     * @param  value  the value of the property to be returned to the portlet
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if key is <code>null</code>.
     */
    public void setProperty(String key, String value);
    
    /**
     * Adds a String property to an existing key to be sent to the
     * portlet.
     * 
     * This method allows the properties to have multiple values.
     * 
     * Properties can be used by portlets to access 
     * portal/portlet-container specific properties
     *
     * @param  key    the key of the property to be returned to the portlet
     * @param  value  the value of the property to be returned to the portlet
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if key is <code>null</code>.
     */
    public void addProperty(String key, String value);
 
    /**
     * Sets the attributes that are scoped. The attributes that should be
     * available across scopes is set using this method. This is applicable
     * per scope ID.
     * 
     * @param scopeID the scope ID
     * @param scopedAttributes the attributes that are scoped
     */
    public void setScopedAttributes(String scopeID, Map<String, Serializable> scopedAttributes);

    /**
     * Returns a Map containing the name and values of the attributes available 
     * for this scopeID. This method returns an empty Map if the request 
     * has no attributes available to it.
     * 
     * @param scopeID the scope ID
     * @return a Map of name and value of the scoped attributes
     */
    public Map<String, Serializable> getScopedAttributes(String scopeID);
    
    /**
     * Returns the PolicyManager that can used to set various policies.
     *
     * @return the PolicyManager that can used to set various policies.
     */
    public PolicyManager getPolicyManager();

    /**
     * Sets the PolicyManager
     *
     * @param  policyManager  the PolicyManager
     **/
    public void setPolicyManager(PolicyManager policyManager);
    
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
    public String getNamespace();

    /**
     * Sets the unique id that identifies the instance of the portlet
     *
     * @param  namespace  the unique id that identifies the instance of the portlet
     **/
    public void setNamespace(String namespace);
    
    /**
     * Returns the portlet window ID.
     * If the portlet window ID is not set, returns the default 
     * portlet window ID based on EntityID or null if EntityID is null.
     *
     * @return the portlet window ID.
     */
    public String getWindowID();

    /**
     * Sets the portlet window ID.
     *
     * @param  windowID  the portlet window ID.
     * 
     **/
    public void setWindowID(String windowID);
    
    /**
     * Returns information about the portal like vendor, version, etc.
     *
     * @return the information about the portal.
     */
    public String getPortalInfo();

    /**
     * Sets the information about the portal.
     *
     * @param  portalInfo  the information about the portal
     * 
     **/
    public void setPortalInfo(String portalInfo);
	
    /**
     * Returns the namespace that corresponds to the portletID.
     *
	 * @param portletID the portletID
	 * 
	 * @return the namespace that corresponds to the portletID.
     */
    public String getNamespace(PortletID portletID);
    
    /**
     * Returns the namespaces that corresponds to the portletIDs.
     *
	 * @param portletNamespaces Map of PortletID and namespaces that correspond to it
     */
    public Map<PortletID, List<String>> getPortletNamespaces();
	
    /**
     * Sets the namespace for the portletID.
     *
	 * @param portletNamespaces Map of PortletID and namespaces that correspond to it
     */
    public void setPortletNamespaces(Map<PortletID, List<String>> portletNamespaces);
	
    /**
     * Returns the windowID that corresponds to the portletID.
     *
	 * @param portletID the portletID
	 * 
	 * @return the windowID that corresponds to the portletID.
     */
    public String getWindowID(PortletID portletID);
    
    /**
     * Returns the windowIDs that corresponds to the portletIDs.
     *
	 * @return a Map of PortletID and windowID that correspond to it
     */
    public Map<PortletID, List<String>> getPortletWindowIDs();

	/**
     * Sets the windowID for the portletID.
     *
	 * @param portletWindowIDs Map of PortletID and windowID that correspond to it
     */
    public void setPortletWindowIDs(Map<PortletID, List<String>> portletWindowIDs);

    /**
     * Returns the list of attributes that will be shared.
     *
	 * @return the list of attributes that will be shared.
     */
    public List<String> getRequestSharedAttributes();

	/**
     * Sets the list of attributes that will to be shared.
     *
	 * @param requestSharedAttributes the list of attributes that will to be shared.
     */
    public void setRequestSharedAttributes(List<String> requestSharedAttributes);

    /**
     * Returns the Map of session attributes that are shared.
     *
	 * @return the Map of session attributes that are shared.
     */
	public Map<String, Object> getSharedSessionAttributesPublish();

	/**
     * Sets the Map of session attributes that are shared.
     *
	 * @param sharedSessionAttributes the Map of session attributes that are shared.
     */
	public void setSharedSessionAttributesPublish(Map<String, Object> sharedSessionAttributes);

}
