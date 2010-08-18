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
 

package com.sun.portal.container;


import com.sun.portal.container.service.EventHolder;
import com.sun.portal.container.service.PublicRenderParameterHolder;
import com.sun.portal.container.service.policy.DistributionType;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.portlet.PortletPreferences;

import javax.servlet.http.HttpServletRequest;


/**
 * The <code>PortletWindowContext</code> provides information pertaining to the container and
 * the portlet registry files.
 * This includes information about the locale, user, portlet registry contents.
 */

public interface PortletWindowContext {
    
    /**
     * Initialize this portlet window context.
     * 
     * The request object must contain the service specific data to allow
     * the underlying implementation to be initialized. This data might be
     * cookies, HTTP headers, etc.
     * The details of how the request object is used is
     * specific to the implementation of this interface.
     * 
     * @param request Request object containing service specific data.
     */
    public void init(HttpServletRequest request);
    
    //
    // service
    //
    
    /**
     * Get the Desktop URL. The Desktop URL is the absolute URL used
     * to access the Desktop application. For example:
     * <code>http://SERVER:PORT/ps/desktop</code>.
     *
     * The request object parameter is included to facilitate implementations.
     * It may be used to build the Desktop URL by supplying the
     * server, port, and protocol of the request. It is not required
     * that the request object be utilizied to generate the Desktop URL.
     *
     * @param request Request object containing the protocol, server, port, etc.
     * information required to build the Desktop URL. If the implementation
     * does not use the request object, this parameter may be null.
     * 
     * @return String representation of the URL.
     *
     */
    public String getDesktopURL(HttpServletRequest request) ;
    
    /**
     * Get the Desktop URL. The Desktop URL is the absolute URL used
     * to access the Desktop application. For example:
     * <code>http://SERVER:PORT/ps/desktop?queryString</code>.
     *
     * query String passed in as an argument will be appended as query string to the
     * desktopURL.
     *
     * This method is equivalent to calling <code>getDesktopURL()</code> if
     * query is null and escape flag false.
     *
     * @param request Request object containing the protocol, server, port, etc.
     * information required to build the Desktop URL. If the implementation
     * does not use the request object, this parameter may be null.
     * @param query String. queryString to be appended to URL.
     * @param escape Boolean. flag specifying whether to escape the url string
     * specific to the client using a client specific encoder.
     * @return String representation of the URL.
     *
     * @see #getDesktopURL(HttpServletRequest request)
     */
    public String getDesktopURL(HttpServletRequest request, String query, boolean escape);
    
    
    
    /**
     * Get the string form of the locale.
     *
     * The various locale suffixes are combined together using the
     * underscore character ("_") as:<br>
     * LANG_COUNTRY_VARIANT
     * <br><br>
     * If a particular suffix is not present, then it is ommited from the string.
     * An example for a language setting of "en", and country setting of "US"
     * is "en_US".
     *
     * @return String representation of the locale.
     */
    public String getLocaleString() ;
    
    /**
     * Get the content type.
     *
     * This value is used to determine if a provider is able to produce content
     * for the client's device.
     *
     * @return a <code>String</code> value
     */
    public String getContentType() ;
    
    /**
     * Encodes a URL.
     *
     * Rewrite the URL to include the session id.
     *
	 * @param url The URL to be encoded.
	 * 
	 * @return the encoded URL
     */
    public String encodeURL(String url);
    
    /**
     * Get if the current Desktop session is non-authenticated (authless).
     * 
     * @param request Request object .
     * @return <code>true</code> if the current Desktop session is an authless
     * session otherwise <code>false</code>.
     */
    public boolean isAuthless(HttpServletRequest request);
    
    
    /**
     * Get the authentication type.
     *
     * The method returns a string denoting the authentication type that was
     * used for the current session. No assumptions can be made about the
     * format of the returned string.
     *
     * @return a <code>String</code>, the authentication type.
     */
    public String getAuthenticationType();
    
    /**
     * Gets the user representation.
     *
     * @return User representation.
     */
    public String getUserRepresentation() ;
    
    
    //
    // config
    //
    
    /**
     * Get the roles the user is in.
     *
     * This method returns the List of strings where
     * each string corresponds to the role the user is in.
     *
     * @return List of roles the user is in.
     */
    public List<String> getRoles();
    
    /**
     * Get the user information for the user.
     *
     * This method returns the Map where eack key corresponds to 
     * a user information and the value corresponds to the value of the
     * user information.
     *
     * @return List of user information for the user.
     */
    public Map<String, String> getUserInfo();
    
    /**
     * Called by the portlet container to get the value for the
     * property.
     * The implementation may chose to handle the property differently
     * based on whether the user is authless or not.
     *  If the authless is true, the property may be returned from the cookie
     *  If the authless is false, the property may be returned from the user's session
     * <p>
     *
     * @return the value for the property
     *
     * @param name the name for the property
     */
    public Object getProperty(String name);
    
    /**
     * Called by the portlet container to set the key and value.
     * The implementation may chose to handle the property differently
     * based on whether the user is authless or not.
     *  If the authless is true, the property may be set in the cookie
     *  If the authless is false, the property may be set in the user's session
     * <p>
     *
     * @param name the name for the property
     * @param value the value for the property
     */
    public void setProperty(String name, Object value);
    
    // These are the methods of PortletRegistryContext interface.
    
    /**
     * Returns the markup types for the portlet as specified in portlet.xml
     *
     * @param portletName the name of the portlet
     *
     * @return a <code>List</code> of the markup types.
     */
    public List<String> getMarkupTypes(String portletName) throws PortletWindowContextException;
    
    /**
     * Returns the description for a portlet for a locale  as specified in portlet.xml
     *
     * @param portletName the name of the portlet
     * @param desiredLocale the locale
     *
     * @return a <code>String</code>, the description
     */
    public String getDescription(String portletName, String desiredLocale) throws PortletWindowContextException;
    
    /**
     * Returns the short title for a portlet for a locale  as specified in portlet.xml
     *
     * @param portletName the name of the portlet
     * @param desiredLocale the locale
     *
     * @return a <code>String</code>, the short title
     */
    public String getShortTitle(String portletName, String desiredLocale) throws PortletWindowContextException;
    
    /**
     * Returns the title for a portlet for a locale as specified in portlet.xml
     *
     * @param portletName the name of the portlet
     * @param desiredLocale the locale
     *
     * @return a <code>String</code>, the title
     */
    public String getTitle(String portletName, String desiredLocale) throws PortletWindowContextException;
    
    /**
     * Returns the keywords for a portlet for a locale as specified in portlet.xml
     *
     * @param portletName the name of the portlet
     * @param desiredLocale the locale
     *
     * @return a <code>List</code> of keywords
     */
    public List<String> getKeywords(String portletName, String desiredLocale) throws PortletWindowContextException;
    
    /**
     * Returns the displayname for a portlet for a locale as specified in portlet.xml
     *
     * @param portletName the name of the portlet
     * @param desiredLocale the locale
     *
     * @return a <code>String</code>, the displayname
     */
    public String getDisplayName(String portletName, String desiredLocale) throws PortletWindowContextException;
    
    /**
     * Returns the role map for a portlet specified during deploying of the portlet.
     *
     * @param portletName the name of the portlet
     *
     * @return a <code>Map</code> of the roles
     */
    public Map<String, String> getRoleMap(String portletName) throws PortletWindowContextException;
    
    /**
     * Returns the userinfo map for a portlet specified during deploying of the portlet
     *
     * @param portletName the name of the portlet
     *
     * @return a <code>Map</code> of the user information
     */
    public Map<String, String> getUserInfoMap(String portletName) throws PortletWindowContextException;
    
    /**
     * Returns the portletName associated with the portlet window
     *
     * @param portletWindowName the name of the portlet window
     *
     * @return a <code>String</code>, the portlet name.
     */
    public String getPortletName(String portletWindowName) throws PortletWindowContextException;

    /**
     * Returns a list of portlet windows based on PortletType and DistributionType. 
     * The possible values for PortletType are PortletType.ALL, PortletType.LOCAL 
     * and PortletType.REMOTE. 
     * The possible values for DistributionType are DistributionType.ALL_PORTLETS, 
     * DistributionType.ALL_PORTLETS_ON_PAGE and DistributionType.VISIBLE_PORTLETS_ON_PAGE. 
     *
     * @param portletType the <code>PortletType</code>
     * @param distributionType the <code>DistributionType</code>
     *
     * @return a <code>List</code> of the portlet windows
     */
    public List<EntityID> getPortletWindows(PortletType portletType, DistributionType distributionType) throws PortletWindowContextException;
    
    /**
     * Returns the entityID for a portlet window
     *
     * @param portletWindowName the name of the portlet window
     *
     * @return a <code>EntityID</code>, the entity Id.
     */
    public EntityID getEntityID(String portletWindowName) throws PortletWindowContextException;
    
    /**
     * Returns the producerEntityID for a portlet window.
     * Available only for remote portlets.
     *
     * @param portletWindowName the name of the portlet window
     *
     * @return a <code>String</code>, the producer entity Id.
     */
    public String getProducerEntityID(String portletWindowName) throws PortletWindowContextException;
    
    /**
     * Returns the consumerId for a portlet window.
     * Available only for remote portlets.
     *
     * @param portletWindowName the name of the portlet window
     *
     * @return a <code>String</code>, the consumer Id.
     */
    public String getConsumerID(String portletWindowName) throws PortletWindowContextException;  
    
    /**
     * Returns the portletID for a portlet window.
     * Available only for remote portlets.
     *
     * @param portletWindowName the name of the portlet window
     *
     * @return a <code>String</code>, the portlet Id.
     */
    public String getPortletID(String portletWindowName) throws PortletWindowContextException;  
                 
    /**
     * Returns the portlet handle for a portlet window.
     * Available only for remote portlets.
     *
     * @param portletWindowName the name of the portlet window
     *
     * @return a <code>String</code>, the portlet handle.
     */
    public String getPortletHandle(String portletWindowName) throws PortletWindowContextException;  

    /**
     * Sets the portlet handle for a portlet window.
     * Available only for remote portlets.
     *
     * @param portletWindowName the name of the portlet window
     * @param portletHandle handle to the portlet from Producer
     *
     */
    public void setPortletHandle(String portletWindowName, String portletHandle) throws PortletWindowContextException;  

    /**
     * Returns the title for a portlet window based on the locale.
     *
     * @param portletWindowName the name of the portlet window
     * @param desiredLocale the locale
     *
     * @return a <code>String</code>, the title
     */
    public String getPortletWindowTitle(String portletWindowName, String desiredLocale) throws PortletWindowContextException;
    
    /**
     * Returns the preferences for a portlet window and a user name
     *
     * @param portletWindowName the name of the portlet window
     * @param bundle the ResourceBundle for the locale that may contain attributes for
     *               preference name and value
     * @param isReadOnly specifies whether it is allowed to store the preferences in the
     *                   returned Preferences Object. 
     *  if isReadOnly is true, it will not be allowed to store the preferences in the returned
     *  Preferences object. if false, storing will be allowed.
     *
     * @return a <code>PortletPreferences</code>, the preferences.
     */
    public PortletPreferences getPreferences(String portletWindowName, ResourceBundle bundle, boolean isReadOnly) throws PortletWindowContextException;
   
    /**
     * Write out any modified data to the persistent store, if necessary.
     *
     */
    public void store() throws PortletWindowContextException;
   
    /**
     * Returns a <code>PortletLang</code> indicating the language of the portlet.
     *
     * @param portletWindowName the portlet window name
     * @return <code>PortletLang</code> indicating the language of the portlet
     * @throws <code>com.sun.portal.container.PortletWindowContextException</code>
     */
    public PortletLang getPortletLang(String portletWindowName) throws PortletWindowContextException;

    /**
     * Verifies whether the portlet can publish the event.
     * If the portlet supports publishing the event , it should specify
     * in the portlet.xml.
     * <P>
     * 1. If event is specified as supported-publishing-event in the
     *    descriptor, returns the event qname
     * <P>
     * 2. If the event is specified as alias in the descriptor,
     *    returns the qname of the event associated with the alias
     * <P>
     * If neither 1 or 2 is valid returns null
     * @param portletEntityId the entity id of the portlet
     * @param eventHolder the EventHolder that represents the event
     * 
     * @return eventQName, if the event is specified as supported-publishing-event in the descriptor
     *                     or as an alias in the descriptor.
     */
    public EventHolder verifySupportedPublishingEvent(EntityID portletEntityId, EventHolder eventHolder);
    
    /**
     * Returns a list of EventHolder objects supported by the portlet for publishing.
     * The EventHolder object holds information about a Event supported by the portlet
     * for publishing.
     *
     * @param portletEntityId the entity id of the portlet
     *
     * @return list of EventHolder objects supported by the portlet for publishing.
     */
    public List<EventHolder> getSupportedPublishingEventHolders(EntityID portletEntityId);

    /**
     * Verifies whether the portlet can process the event.
     * If the portlet supports processing the event , it should specify
     * in the portlet.xml.
     * <P>
     * 1. If event qname is specified as supported-processing-event in the
     *    descriptor, returns the event qname
     * <P>
     * 2. If the event qname is specified as alias in the descriptor,
     *    returns the qname of the event associated with the alias
     * <P>
     * If neither 1 or 2 is valid returns null
     *
     * @param portletEntityId the entity id of the portlet
     * @param eventHolder the EventHolder that represents the event
     *
     * @return EventHolder, if the event is specified as supported-processing-event in the descriptor
     *                     or as an alias in the descriptor.
     */
    public EventHolder verifySupportedProcessingEvent(EntityID portletEntityId, EventHolder eventHolder);
    
    /**
     * Returns a list of EventHolder objects supported by the portlet for processing.
     * The EventHolder object holds information about a Event supported by the portlet
     * for processing.
     *
     * @param portletEntityId the entity id of the portlet
     *
     * @return list of EventHolder objects supported by the portlet for processing.
     */
    public List<EventHolder> getSupportedProcessingEventHolders(EntityID portletEntityId);

    /**
     * 
     * Verifies whether the portlet can publish or process the public render parameters specified in
     * the list of PublicRenderParameterHolders.
     * If the portlet supports publishing or processing the public render parameter, it should specify
     * in the portlet.xml.
     * <P>
     * 1. If render parameter is specified as supported-public-render-parameter in the
     *    descriptor, returns the qname
     * <P>
     * 2. If the render parameter is specified as alias in the descriptor,
     *    returns the qname of the render parameter associated with the alias
     * <P>
     * 3. If the alias of the PublicRenderParameterHolder contains the qname of the descriptor,
     *    returns the qname
     * <P>
     * If neither 1 or 2 or 3 is valid returns an empty List
     * 
     * @param portletEntityId the entity id of the portlet
     * @param publicRenderParameterHolders List of PublicRenderParameterHolders of the render parameter
     * 
     * @return list of public render parameter identifiers supported by the portlet.
     */
    public Map<String, String> verifySupportedPublicRenderParameters(EntityID portletEntityId, List<PublicRenderParameterHolder> publicRenderParameterHolders);

    /**
     * Returns a list of PublicRenderParameterHolder objects supported by the portlet based
     * on the render parameters. If the render parameters is null, it returns all the
     * PublicRenderParameterHolder objects supported by the portlet
     * The PublicRenderParameterHolder object holds information about a Public Render
     * Parameter supported by the portlet.
     *
     * @param portletEntityId the entity id of the portlet
     * @param renderParameters the render parameters of the portlet
     *
     * @return list of PublicRenderParameterHolder objects supported by the portlet.
     */
    public List<PublicRenderParameterHolder> getSupportedPublicRenderParameterHolders(EntityID portletEntityId, Map<String, String[]> renderParameters);
}
