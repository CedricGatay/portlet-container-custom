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

package com.sun.portal.portletcontainer.portlet.impl;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;
import com.sun.portal.container.ContainerLogger;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.SecurityRoleRefDescriptor;
import com.sun.portal.portletcontainer.common.PortletContainerConstants;
import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResponse;
import com.sun.portal.portletcontainer.common.descriptor.SharedSessionAttributeDescriptor;

import javax.portlet.PortletRequest;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;
import javax.portlet.PortletContext;
import javax.portlet.PortalContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.Principal;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.ActionRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderResponse;


/**
 * This class provides implementation of the PortletRequest interface.
 */
public abstract class PortletRequestImpl implements PortletRequest {

    // Global variables
    private HttpServletRequest request;
    //TODO: Not in use. Should be removed?
    //private HttpServletResponse response;
    private PortletContainerRequest pcRequest;
    private PortletContainerResponse pcResponse;
    private PortletContext portletContext;
    private PortalContext portalContext;
    private PortletDescriptor portletDescriptor;
    private PortletSession portletSession;

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private static Logger logger = ContainerLogger.getLogger(PortletRequestImpl.class, "PAELogMessages");

    /**
     * Initialize the global variables.
     * <P>
     *
     * @param request The <code>HttpServletRequest</code> of the PAE
     * @param response The <code>HttpServletResponse</code> of the PAE
     * @param pcRequest The <code>PortletContainerRequest</code>
     * @param pcResponse The <code>PortletContainerResponse</code>
     * @param portletContext The <code>PortletContext</code>
     * @param portalContext The <code>PortalContext</code>
     * @param portletDescriptor The <code>PortletDescriptor</code> for the portlet
     * @param scopedAtttributes the Map of attributes
     */
    protected void init(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerRequest pcRequest,
            PortletContainerResponse pcResponse,
            PortletContext portletContext,
            PortalContext portalContext,
            PortletDescriptor portletDescriptor,
            Map<String, Object> scopedAtttributes) {

        this.request = request;
        //this.response = response;
        this.pcRequest = pcRequest;
        this.pcResponse = pcResponse;
        this.portletContext = portletContext;
        this.portalContext = portalContext;
        this.portletDescriptor = portletDescriptor;

        //Sets the portlet name and portlet window name as an attribute on request
        this.setAttribute(PortletRequestConstants.PORTLET_NAME, pcRequest.getPortletName());
        this.setAttribute(PortletRequestConstants.PORTLET_WINDOW_NAME, pcRequest.getPortletWindowName());

        // Sets req and res in attributes
        // Note: in the case of supporting distributed system, set the
        // request violates the spec, since all objects should be
        // serializable. The setAttribute() method may need to do some
        // special checking.
        this.setAttribute(PortletRequestConstants.HTTP_SERVLET_REQUEST, request);
        this.setAttribute(PortletRequestConstants.HTTP_SERVLET_RESPONSE, response);
        // As the CCPP Profile is set as an attribute on ContainerRequest
        // it will be available as an attribute in the PortletRequest
        // Set the attributes obtained from the PortletContainerRequest
        Map<String, Object> pcRequestAttributes = this.pcRequest.getAttributes();
        Set<Map.Entry<String, Object>> pcRequestAttributesEntries = pcRequestAttributes.entrySet();
        for(Map.Entry<String, Object> mapEntry : pcRequestAttributesEntries) {
            this.setAttribute(mapEntry.getKey(), mapEntry.getValue());
        }
		// Set the scoped attributes
		if(scopedAtttributes != null && !scopedAtttributes.isEmpty()) {
			Set<Map.Entry<String, Object>> scopedAttributesEntries = scopedAtttributes.entrySet();
			for(Map.Entry<String, Object> mapEntry : scopedAttributesEntries) {
				this.setAttribute(mapEntry.getKey(), mapEntry.getValue());
			}
		}
    }

    /**
     * Clears the global variables.
     */
    protected void clear() {
        this.request = null;
        //response = null;
        this.pcRequest = null;
        this.pcResponse = null;
        this.portletContext = null;
        this.portalContext = null;
        this.portletDescriptor = null;
		this.portletSession = null;
    }

    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    protected PortletContainerRequest getPortletContainerRequest() {
        return pcRequest;
    }

    protected PortletContainerResponse getPortletContainerResponse() {
        return pcResponse;
    }

    protected PortletContext getPortletContext() {
        return portletContext;
    }

    protected PortletDescriptor getPortletDescriptor() {
        return portletDescriptor;
    }

	public String getPortletName() {
		return getPortletDescriptor().getPortletName();
	}

    /**
     * Returns true, if the given window state is valid
     * to be set for this portlet in the portletContext
     * of the current request.
     *
     * @param state    window state to check
     * @return true, if it is valid for this portlet
     *            entity in this request to change to the
     *            given window state
     * @see ActionRequest#setWindowState
     */
    public boolean isWindowStateAllowed(WindowState state) {

        boolean retVal = false;
        ChannelState newState = new ChannelState(state.toString());

        if (getPortletContainerRequest().getAllowableWindowStates().contains(newState)) {
            retVal = true;
        }

        return retVal;

    }

    /**
     * Returns true, if the given portlet mode is a valid
     * one to set for this portlet in the portletContext
     * of the current request.
     *
     * @param portletMode    portlet mode to check
     * @return true, if it is valid for this portlet
     *            entity in this request to change to the
     *            given portlet mode
     * @see ActionRequest#setPortletMode
     */

    public boolean isPortletModeAllowed(PortletMode portletMode) {

        boolean isAllowed = false;

        ChannelMode newMode = new ChannelMode(portletMode.toString());

        // check if the new mode is allowed in the portal
        if (getPortletContainerRequest().getAllowablePortletWindowModes().contains(newMode)) {
            if (portletMode.equals(PortletMode.VIEW)) {
                isAllowed = true;
            }
            // check if portlet mode is allowed in the container
            else if (getPortletDescriptor() != null) {

                String mimeType = getResponseContentType();
                List supportedPortletModes = getPortletDescriptor().getSupportedPortletModes(mimeType);

                if (supportedPortletModes != null) {
                    for (int i = 0; i < supportedPortletModes.size()
                    && !isAllowed; i++) {
                        String mode = (String) supportedPortletModes.get(i);
                        if (portletMode.toString().equalsIgnoreCase(mode)) {
                            isAllowed = true;
                        }
                    }
                }
            }
        }
        if(!isAllowed && logger.isLoggable(Level.WARNING)) {
            logger.log(Level.WARNING, "PSPL_PAECSPPI0041", portletMode.toString());
        }
        return isAllowed;
    }

    /**
     * Returns the preferences object associated with the portlet.
     * <p>
     * @return the portlet preferences
     */
    public PortletPreferences getPreferences() {
        return getPortletContainerRequest().getPortletPreferences();
    }

    /**
     * Returns the current portlet session or, if there is no current session,
     * creates one and returns the new session.
     *
     * Creating a new portlet session will result in creating
     * a new <code>HttpSession</code> on which the portlet session is
     * based on.
     *
     * @return the portlet session
     */
    public PortletSession getPortletSession() {
        return getPortletSession(true);
    }

    /**
     * Returns the current portlet session or, if there is no current session
     * and the given flag is <CODE>true</CODE>, it creates one and returns
     * the new session.
     *
     * <P>
     * If the given flag is <CODE>false</CODE> and there is no current
     * portlet session, this method returns <CODE>null</CODE>.
     *
     * Creating a new portlet session will result in creating
     * a new <code>HttpSession</code> on which the portlet session is based on.
     *
     * @param create
     *               <CODE>true</CODE> to create a news session, <BR>
     *               <CODE>false</CODE> to return <CODE>null</CODE> of there
     *               is no current session
     * @return the portlet session
     */
    public PortletSession getPortletSession(boolean create) {

        HttpSession session = null;
        try {
            session = createSession(getHttpServletRequest(), create);
        } catch (IllegalStateException ise) {
            if(logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "PSPL_PAECSPPI0037", ise.toString());
            }
        }

        if (session != null) {

            String sessionId = (String) getHttpServletRequest().getAttribute(PortletContainerConstants.HTTP_SESSION_ID);
            Boolean sessionInvalid = (Boolean) getHttpServletRequest().getAttribute(PortletContainerConstants.SESSION_INVALID);

            if (!session.getId().equals(sessionId)
            && Boolean.TRUE.equals(sessionInvalid)) {
                // new user login, need to invalidate the existing
                // HttpSession object and create a new one
                if(logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "PSPL_PAECSPPI0039",
                            new String[] { session.getId(), sessionId, String.valueOf(sessionInvalid) } );
                }
				if(getHttpServletRequest().isRequestedSessionIdValid()) {
					session.invalidate();
				}
                session = createSession(getHttpServletRequest(), create);
            }

            if (session != null) {
                if(this.portletSession == null) {
                    this.portletSession = createPortletSession(
							session, this.getPortletContext(), getWindowID());
					if(logger.isLoggable(Level.FINE)) {
						logger.log(Level.FINE, "PSPL_PAECSPPI0040", getWindowID());
					}
                }
            } else {
                logger.log(Level.WARNING, "PSPL_PAECSPPI0038");
            }
        }
		if(logger.isLoggable(Level.FINE)) {
			logger.log(Level.FINE, "PSPL_PAECSPPI0044", this.portletSession);
		}
		getPortletContainerRequest().setHttpSession(session);
		return this.portletSession;
    }

    /**
     * Returns the name of the authentication scheme used to protect the
     * connection between client and portal,
     * for example, <code>BASIC_AUTH</code>, <code>CLIENT_CERT_AUTH</code>,
     * a custom one or <code>null</code> if there was no authentication.
     *
     * @return      one of the static members <code>BASIC_AUTH</code>,
     *		    <code>FORM_AUTH</code>, <code>CLIENT_CERT_AUTH</code>,
     *              <code>DIGEST_AUTH</code> (suitable for == comparison)
     *              indicating the authentication scheme, a custom
     *              one, or
     *              <code>null</code> if the request was
     *              not authenticated.
     */
    public String getAuthType() {
        return getHttpServletRequest().getAuthType();
    }

    /**
     * Returns the login of the user making this request, if the user
     * has been authenticated, or null if the user has not been authenticated.
     *
     * @return    a <code>String</code> specifying the login
     *            of the user making this request, or <code>null</code
     *            if the user login is not known
     *
     */
    public String getRemoteUser() {
        return getPortletContainerRequest().getUserID();
    }

    /**
     * Returns a java.security.Principal object containing the name of the
     * current authenticated user.
     *
     * @return        a <code>java.security.Principal</code> containing
     *            the name of the user making this request;
     *            <code>null</code> if the user has not been
     *            authenticated
     */
    public Principal getUserPrincipal() {
        Principal userPrincipal = getPortletContainerRequest().getUserPrincipal();

        if (userPrincipal == null) {
	        userPrincipal = getHttpServletRequest().getUserPrincipal();
        }
        return userPrincipal;
    }

    /**
     * Returns a boolean indicating whether the authenticated user is
     * included in the specified logical "role".  Roles and role
     * membership
     * can be defined using deployment descriptors.  If the user has
     * not been authenticated, the method returns <code>false</code>.
     *
     * @param role    a <code>String</code> specifying the name
     *                of the role
     *
     * @return    a <code>boolean</code> indicating whether
     *            the user making this request belongs to a given role;
     *            <code>false</code> if the user has not been
     *            authenticated
     */
    public boolean isUserInRole(String role) {
        boolean isInRole = false;
        List<SecurityRoleRefDescriptor> securityRoleRefDescriptors =
            getPortletDescriptor().getSecurityRoleRefDescriptors();

        List<String> portalSupportedRoles = getPortletContainerRequest().getRoles();

        if (portalSupportedRoles != null) {

            // Loop through security-role-refs for role names and links,
            // and compare the role-link with the roles supported from
            // the portal side.
            for (int i = 0; i < securityRoleRefDescriptors.size() && !isInRole; i++) {
                SecurityRoleRefDescriptor refDescriptor = securityRoleRefDescriptors.get(i);

                String roleName = refDescriptor.getRoleName();
                String roleLink = refDescriptor.getRoleLink();

                if (!role.equalsIgnoreCase(roleName)) {
                    continue;
                }
                for (int j = 0; j < portalSupportedRoles.size() && !isInRole; j++) {
                    String portalSupportedRole = portalSupportedRoles.get(j);

                    if (roleLink != null) {
                        if (portalSupportedRole.equalsIgnoreCase(roleLink)) {
                            isInRole = true;
                        }
                    } else if (portalSupportedRole.equalsIgnoreCase(roleName)) {
                        isInRole = true;
                    }
                }
            }
        }

        return isInRole;
    }

    /**
     *
     * Returns the value of the named attribute as an <code>Object</code>,
     * or <code>null</code> if no attribute of the given name exists.
     *
     *
     * <p>Attribute names should follow the same conventions as package
     * names. This specification reserves names matching <code>java.*</code>,
     * and <code>javax.*</code>.
     *
     * In a distributed portlet web application the <code>Object</code>
     * needs to be serializable.
     *
     * @param name    a <code>String</code> specifying the name of
     *            the attribute
     *
     * @return    an <code>Object</code> containing the value
     *            of the attribute, or <code>null</code> if
     *            the attribute does not exist
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public Object getAttribute(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "Attribute name should not be null.");
        }

        if (name.equals(PortletRequest.USER_INFO)) {
            return pcRequest.getUserInfo();
        }
        // Since CCPP_PROFILE is set as an attribute,
        // no special check is required
        return getHttpServletRequest().getAttribute(name);
    }

    /**
     * Returns an <code>Enumeration</code> containing the
     * names of the attributes available to this request.
     * This method returns an empty <code>Enumeration</code>
     * if the request has no attributes available to it.
     *
     *
     * @return    an <code>Enumeration</code> of strings
     *            containing the names
     *            of the request's attributes, or an empty
     *                    <code>Enumeration</code> if the request
     *                    has no attributes available to it.
     *
     */
    public Enumeration<String> getAttributeNames() {
		return getHttpServletRequest().getAttributeNames();
    }

    /**
     * Returns the value of a request parameter as a <code>String</code>,
     * or <code>null</code> if the parameter does not exist. Request parameters
     * are extra information sent with the request. The returned parameter
     * are "x-www-form-urlencoded" decoded.
     * <p>
     * Only parameters targeted to the current portlet are accessible.
     *
     * <p>This method should only be used when the
     * parameter has only one value. If the parameter might have
     * more than one value, use {@link #getParameterValues}.
     *
     * <p>If this method is used with a multivalued
     * parameter, the value returned is equal to the first value
     * in the array returned by <code>getParameterValues</code>.
     *
     *
     * @param name    a <code>String</code> specifying the
     *                name of the parameter
     *
     * @return    a <code>String</code> representing the
     *            single value of the parameter
     *
     * @see       #getParameterValues
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     *
     */
    public String getParameter(String name) {
        Map<String, String[]> map = getParameterMap();
        String retVal = null;

        if (name == null) {
            throw new IllegalArgumentException(
                    "Parameter name should not be null.");
        }

        if (map != null) {
            String[] values = map.get(name);
			if(values == null && this.pcRequest != null) {
				// if the name is prepended with namespace
				values = map.get(this.pcRequest.getNamespace() + name);
			}
            if(values != null) {
                retVal = values[0];
            }
        }

        return retVal;
    }

    /**
     *
     * Returns an <code>Enumeration</code> of <code>String</code>
     * objects containing the names of the parameters contained
     * in this request. If the request has
     * no parameters, the method returns an
     * empty <code>Enumeration</code>.
     *
     * @return    an <code>Enumeration</code> of <code>String</code>
     *            objects, each <code>String</code> containing
     *            the name of a request parameter; or an
     *            empty <code>Interator</code> if the
     *            request has no parameters
     *
     */
    public Enumeration<String> getParameterNames() {
        HashMap map = new HashMap(getParameterMap());
        return Collections.enumeration(map.keySet());
    }

    /**
     * Returns an array of <code>String</code> objects containing
     * all of the values the given request parameter has, or
     * <code>null</code> if the parameter does not exist.
     * The returned parameters are "x-www-form-urlencoded" decoded.
     * <p>If the parameter has a single value, the array has a length
     * of 1.
     *
     * @param name    a <code>String</code> containing the name of
     *                the parameter whose value is requested
     *
     * @return    an array of <code>String</code> objects
     *            containing the parameter's values
     *
     * @see       #getParameter
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public String[] getParameterValues(String name) {
        Map<String, String[]> map = getParameterMap();
        String[] retVals = null;

        if (name == null) {
            throw new IllegalArgumentException(
                    "Parameter name should not be null.");
        }

        if (map != null) {
            String[] values = map.get(name);
            if (values != null) {
                retVals = values;
            }
        }

        return retVals;
    }

    /**
     * Returns the preferred Locale in which the portal will accept content.
     * The Locale may be based on the Accept-Language header of the client.
     *
     */
    public Locale getLocale() {
        return getPortletContainerRequest().getLocale();
    }

    /**
     * Returns an Enumeration of Locale objects indicating, in decreasing
     * order starting with the preferred locale in which the portal will
     * accept content for this request.
     * The Locales may be based on the Accept-Language header of the client.
     *
     * @return  an Enumeration of Locales, in decreasing order, in which
     *           the portal will accept content for this request
     */

    public Enumeration<Locale> getLocales() {
        List localeList = new ArrayList(1);
        localeList.add(getPortletContainerRequest().getLocale());

        return Collections.enumeration(localeList);
    }

    /**
     * Returns the name of the scheme used to make this request.
     * For example, <code>http</code>, <code>https</code>, or <code>ftp</code>.
     * Different schemes have different rules for constructing URLs,
     * as noted in RFC 1738.
     *
     * @return		a <code>String</code> containing the name
     *			of the scheme used to make this request
     */

    public String getScheme() {
        return getHttpServletRequest().getScheme();
    }

    /**
     * Returns the host name of the server that received the request.
     *
     * @return		a <code>String</code> containing the name
     *			of the server to which the request was sent
     */

    public String getServerName() {
        return getHttpServletRequest().getServerName();
    }

    /**
     * Returns the port number on which this request was received.
     *
     * @return		an integer specifying the port number
     */

    public int getServerPort() {
        return getHttpServletRequest().getServerPort();
    }

    /**
     * Returns a boolean indicating whether this request was made
     * using a secure portlet window between client and portal, such as HTTPS.
     *
     * @return  true, if the request was made using a secure portlet window.
     */
    public boolean isSecure() {
        return getHttpServletRequest().isSecure();
    }

    /**
     *
     * Stores an attribute in this request.
     *
     * <p>Attribute names should follow the same conventions as
     * package names. Names beginning with <code>java.*</code>,
     * <code>javax.*</code>, and <code>com.sun.*</code>, are
     * reserved for use by Sun Microsystems.
     * <br> If the value passed into this method is <code>null</code>,
     * the effect is the same as calling {@link #removeAttribute}.
     *
     * @param name        a <code>String</code> specifying
     *                    the name of the attribute
     *
     * @param o           the <code>Object</code> to be stored
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public void setAttribute(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "Can not set attribue with null name.");
        }
		getHttpServletRequest().setAttribute(name, value);
    }

    /**
     *
     * Removes an attribute from this request.  This method is not
     * generally needed as attributes only persist as long as the request
     * is being handled.
     *
     * <p>Attribute names should follow the same conventions as
     * package names. Names beginning with <code>java.*</code>,
     * <code>javax.*</code>, and <code>com.sun.*</code>, are
     * reserved for use by Sun Microsystems.
     *
     *
     * @param name        a <code>String</code> specifying
     *                    the name of the attribute to remove
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public void removeAttribute(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "Attribute name should not be null.");
        }
		getHttpServletRequest().removeAttribute(name);
    }

    /**
     * Returns the portal preferred content type of the response.
     *
     * The content type only includes the MIME type, not the
     * character set.
     * <p>
     * Only content types that the portlet has defined in its
     * deployment descriptor are valid return values for
     * this method call. If the portlet has defined
     * <code>'*'</code> or <code>'* / *'</code> as supported content
     * types, these may also be valid return values.
     *
     * @return prefered MIME type of the response
     */
    public String getResponseContentType() {
        String responseContentType = getPortletContainerRequest().getResponseContentType();
        if (responseContentType == null || responseContentType.length() == 0) {
            responseContentType = DEFAULT_CONTENT_TYPE;
        }
        return responseContentType;
    }

    /**
     * Gets a list of content types which the portal accepts for the response.
     * This list is ordered with the most preferable types listed first.
     * <p>
     * The content type only includes the MIME type, not the
     * character set.
     * <p>
     * Only content types that the portlet has defined in its
     * deployment descriptor are valid return values for
     * this method call. If the portlet has defined
     * <code>'*'</code> or <code>'* / *'</code> as supported content
     * types, these may also be valid return values.
     *
     * @return ordered list of MIME types for the response
     */
    public Enumeration<String> getResponseContentTypes() {
        List contentTypes = getPortletContainerRequest().getAllowableContentTypes();
        List mimeTypes = getPortletDescriptor().getSupportedMimeTypes();
        List returnList = new ArrayList();
        boolean stop = false;

        for (int i = 0; i < contentTypes.size() && !stop; i++) {
            String contentType = ((String) contentTypes.get(i)).toLowerCase(Locale.ENGLISH);

            for (int j = 0; j < mimeTypes.size() && !stop; j++) {
                String mimeType = ((String) mimeTypes.get(j)).toLowerCase(Locale.ENGLISH);

                if (mimeType.equals("*/*")) {
                    returnList = contentTypes;
                    stop = true;
                } else if (contentType.equals(mimeType)) {
                    returnList.add(contentType);
                } else if (mimeType.equals("text/*")
                && contentType.startsWith("text/")) {
                    returnList.add(contentType);
                }
            }
        }

        return Collections.enumeration(returnList);
    }

    /**
     *
     * Returns the value of the specified client request property
     * as a <code>String</code>. If the request did not include a property
     * of the specified name, this method returns <code>null</code>.
     *
     * A portlet can access portal/portlet-container specific properties
     * through this method and, if available, the
     * headers of the HTTP client request.
     * <p>
     * This method should only be used if the
     * property has only one value. If the property might have
     * more than one value, use {@link #getProperties}.
     * <p>
     * If this method is used with a multivalued
     * parameter, the value returned is equal to the first value
     * in the enumeration returned by <code>getProperties</code>.
     *
     * @param name        a <code>String</code> specifying the
     *                    property name
     *
     * @return            a <code>String</code> containing the
     *                    value of the requested
     *                    property, or <code>null</code>
     *                    if the request does not
     *                    have a property of that name
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */

    public String getProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "Property name should not be null.");
        }
        // Check if this name is present in the HTTP Servlet Request Header
        // If yes, return it else return it from the properties
        String value = getHttpServletRequest().getHeader(name);
        if(value == null) {
            Map<String, List<String>> properties = this.pcRequest.getProperties();
            if(properties != null && properties.size() != 0) {
                List<String> values = properties.get(name);
                if(values != null) {
                    value = values.get(0);
                }
            }
        }
        return value;
    }

    /**
     *
     * Returns all the values of the specified request property
     * as a <code>Enumeration</code> of <code>String</code> objects.
     *
     * <p>If the request did not include any propertys
     * of the specified name, this method returns an empty
     * <code>Enumeration</code>.
     * The property name is case insensitive. You can use
     * this method with any request property.
     *
     * @param name	a <code>String</code> specifying the
     *			property name
     *
     * @return		a <code>Enumeration</code> containing
     *                  the values of the requested property. If
     *                  the request does not have any properties of
     *                  that name return an empty Enumeration.
     *
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public Enumeration<String> getProperties(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "Property name should not be null.");
        }
        // Check if this name is present in the HTTP Servlet Request Headers
        // If yes, return it else return it from the properties
        Enumeration<String> headers = getHttpServletRequest().getHeaders(name);
        if(!headers.hasMoreElements()) {
            Map<String, List<String>> properties = this.pcRequest.getProperties();
            if(properties != null && properties.size() != 0) {
                List<String> values = properties.get(name);
                if(values != null) {
                    return Collections.enumeration(values);
                }
            }
            List<String> emptyList = Collections.emptyList();
            return Collections.enumeration(emptyList);
        }
        return headers;
    }

    /**
     *
     * Returns a <code>Enumeration</code> of all the property names
     * this request contains. If the request has no
     * properties, this method returns an empty enumeration.
     *
     *
     * @return			an enumeration of all the
     *				property names sent with this
     *				request; if the request has
     *				no propertys, an empty enumeration
     */
    public Enumeration<String> getPropertyNames() {
        // Merge the header names and thee properties names
        Enumeration<String> headerNames = getHttpServletRequest().getHeaderNames();
        Map<String, Object> map = new HashMap();
        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            map.put(name, getHttpServletRequest().getHeader(name));
        }
        Map<String, List<String>> properties = this.pcRequest.getProperties();
        if(properties != null && properties.size() != 0) {
            map.putAll(properties);
        }
        return Collections.enumeration(map.keySet());
    }

    /**
     * Returns the portletContext of the calling portal.
     *
     * @return the portletContext of the calling portal
     */
    public PortalContext getPortalContext() {
        //Set the Portal Information set by the portal
        ((PortalContextImpl)this.portalContext).setPortalInfo(
                        this.pcRequest.getPortalInfo());
        return this.portalContext;
    }

    /**
     *
     * Returns the portletContext path which is the path prefix associated with the deployed
     * portlet application. If the portlet application is rooted at the
     * base of the web server URL namespace (also known as "default" portletContext),
     * this path must be an empty string. Otherwise, it must be the path the
     * portlet application is rooted to, the path must start with a '/' and
     * it must not end with a '/' character.
     * <p>
     * To encode a URL the {@link PortletResponse#encodeURL} method must be used.
     *
     * @return a <code>String</code> specifying the
     *           portion of the request URL that indicates the portletContext
     *           of the request
     * @see RenderResponse#encodeURL
     */

    public String getContextPath() {
        String contextPath = (String)this.request.getAttribute("javax.servlet.include.context_path");
        if (contextPath == null || contextPath.length() == 0) {
            contextPath = this.portletContext.getPortletContextName();
            if (contextPath == null || contextPath.length() == 0) {
                return "/";
            } else {
                return "/" + contextPath;
            }
        }
        return contextPath;
    }

    /**
     *
     * Returns the session ID indicated in the client request.
     * This session ID may not be a valid one, it may be an old
     * one that has expired or has been invalidated.
     * If the client request
     * did not specify a session ID, this method returns
     * <code>null</code>.
     *
     * @return		a <code>String</code> specifying the session
     *			ID, or <code>null</code> if the request did
     *			not specify a session ID
     *
     * @see		#isRequestedSessionIdValid
     *
     */
    public String getRequestedSessionId() {
        return getHttpServletRequest().getRequestedSessionId();
    }

    /**
     *
     * Checks whether the requested session ID is still valid.
     *
     * @return <code>true</code> if this
     * 				request has an id for a valid session
     * 				in the current session portletContext;
     * 				<code>false</code> otherwise
     * @see #getRequestedSessionId
     * @see #getPortletSession
     */
    public boolean isRequestedSessionIdValid() {
        return getHttpServletRequest().isRequestedSessionIdValid();
    }

    /**
     * Returns the portlet window ID. The portlet window ID is
     * unique for this portlet window and is constant for the lifetime
     * of the portlet window.
     * <p>
     * This ID is the same that is used by the portlet container for
     * scoping the portlet-scope session attributes.
     *
     * @since 2.0
     * @return  the portlet window ID
     */
    public String getWindowID() {
        return this.pcRequest.getWindowID();
    }

    /**
     * Returns an array containing all of the Cookie properties.
     * <p>
     * This method returns <code>null</code> if no cookies exist.
     *
     * @since 2.0
     * @return  array of cookie properties, or
     *          <code>null</code> if no cookies exist.
     * @see PortletResponse#addProperty(Cookie)
     */
    public Cookie[] getCookies() {
        // Return the cookies from the HttpServletRequest and also
        // the cookies that has been set in PortletContainerRequest
        List<Cookie> cookieProperties = this.pcResponse.getCookieProperties();
        Cookie requestCookies[] = request.getCookies();
		int requestCookieLength = 0;
		if(requestCookies != null) {
			requestCookieLength = requestCookies.length;
		}

        if(cookieProperties != null && cookieProperties.size() != 0) {
            Cookie[] cookies = new Cookie[requestCookieLength + cookieProperties.size()];
            for(int i=0; i<requestCookieLength; i++){
                cookies[i] = requestCookies[i];
            }
            int j = requestCookieLength;
            for(Cookie cookie: cookieProperties) {
                cookies[j++] = cookie;
            }
            return cookies;
        } else {
            return requestCookies;
        }
    }

    /**
     * Returns either PrivateParameterMap or PublicParameterMap based on the boolean
     * publicParametersOnly.
     * If publicParametersOnly is true, returns PublicParameterMap
     * If publicParametersOnly is false, returns PrivateParameterMap
     * @param parameters the render parameters
     * @param publicParametersOnly indicates whether public or private parameter map is needed
     *
     * @return PublicParameterMap is publicParametersOnly is true, else returns PrivateParameterMap
     */
    protected Map<String, String[]> getPublicPrivateParameterMap(Map<String, String[]> parameters,
            boolean publicParametersOnly) {
        if(parameters != null) {
            List<String> publicRenderParameters = getSupportedPublicRenderParameters();
            // Walkthrough the parameters and remove the public render parameters from it
            Map<String, String[]> renderParameters = new HashMap<String, String[]>(parameters.size());
            Set entrySet = parameters.entrySet();
            Iterator<Map.Entry> itr = entrySet.iterator();
            while(itr.hasNext()) {
                Map.Entry<String, String[]> mapEntry = itr.next();
                String parameter = mapEntry.getKey();
                if(publicParametersOnly) {
                    if(publicRenderParameters.contains(parameter)) {
                        renderParameters.put(parameter, mapEntry.getValue());
                    }
                } else {
                    if(!publicRenderParameters.contains(parameter)) {
                        renderParameters.put(parameter, mapEntry.getValue());
                    }
                }
            }
            return Collections.unmodifiableMap(renderParameters);
        } else {
            return Collections.emptyMap();
        }
    }

    private HttpSession createSession(HttpServletRequest request, boolean create) {
        HttpSession session = null;
        // Synchronize only if create is true
        if(create) {
            synchronized(request) {
               session = request.getSession(true);
            }
        } else {
            session = request.getSession(false);
        }
        //Hack to test for invalid session
        if(session != null && session.getId().indexOf("Null") != -1) {
            return null;
        }
		if(logger.isLoggable(Level.FINER)) {
			logger.log(Level.FINER, "PSPL_PAECSPPI0045", session);
		}
        return session;
    }

    // Returns a list of Public Render Parameters supported(i.e identifiers)
    private List<String> getSupportedPublicRenderParameters() {
        List<String> publicRenderParameters =
                this.portletDescriptor.getSupportedPublicRenderParameterIdentifiers();
        if(publicRenderParameters != null) {
            return publicRenderParameters;
        } else {
            return Collections.emptyList();
        }
    }

	private PortletSession createPortletSession(HttpSession session, PortletContext portletContext, String windowID) {
		PortletSession newPortletSession = null;
		if(hasSharedSessionAttributes()) {
			if(logger.isLoggable(Level.FINE)) {
				logger.log(Level.FINE, "PSPL_PAECSPPI0053", windowID);
			}
			newPortletSession = new PortletSharedSessionImpl(
					session, portletContext, windowID, getPortletContainerRequest());
		} else {
			newPortletSession = new PortletSessionImpl(
					session, portletContext, windowID);
		}
		return newPortletSession;
	}

	private boolean hasSharedSessionAttributes() {
		List<SharedSessionAttributeDescriptor> sharedSessionAttributeDescriptors
				 = getPortletContainerRequest().getSharedSessionAttributeDescriptors();
		if(sharedSessionAttributeDescriptors != null
				&& !sharedSessionAttributeDescriptors.isEmpty()) {
			return true;
		}
		return false;
	}
}
