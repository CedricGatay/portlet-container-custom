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

import com.sun.portal.container.ContainerLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

/**
 * The <code>PortletSessionImpl</code> class provides a default
 * implementation for the <code>PortletSession</code> interface.
 *
 */

public class PortletSessionImpl implements PortletSession {

    // constants
    // fabricated attribute name: javax.portlet.p.<WindowID>?<ATTRIBUTE_NAME>
    private static final String PORTLET_SCOPE_ATTR_PREFIX = "javax.portlet.p.";
    private static int LENGTH_OF_PORTLET_SCOPE_ATTR_PREFIX = PORTLET_SCOPE_ATTR_PREFIX.length(); //16
	private static final String QUESTION = "?";

    //private data members
    private HttpSession httpSession;
    private PortletContext portletContext;
    private String windowID;

    private static Logger logger = ContainerLogger.getLogger(PortletSessionImpl.class, "PAELogMessages");
    
    public PortletSessionImpl(HttpSession session, 
			PortletContext portletContext, String windowID) {

        this.httpSession = session;
        this.portletContext = portletContext;
        this.windowID = windowID;
    }

    /**
     * Returns the object bound with the specified name in this session
     * under the <code>PORTLET_SCOPE</code>, or <code>null</code> if no
     * object is bound under the name in that scope.
     *
     * @param name		a string specifying the name of the object
     *
     * @return			the object with the specified name for
     *                            the <code>PORTLET_SCOPE</code>.
     *
     * @exception java.lang.IllegalStateException	if this method is called on an
     *					invalidated session.
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public Object getAttribute(String name) throws IllegalArgumentException {

        return getAttribute(name, PORTLET_SCOPE);
    }

    /**
     * Returns the object bound with the specified name in this session,
     * or <code>null</code> if no object is bound under the name in the given scope.
     *
     * @param name		a string specifying the name of the object
     * @param scope               session scope of this attribute
     *
     * @return			the object with the specified name
     *
     * @exception java.lang.IllegalStateException	if this method is called on an
     *					invalidated session
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public Object getAttribute(String name, int scope) throws IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("The given attribute name is null");
        }
		String encodedName = getEncodedAttrName(name, scope);
        Object value = httpSession.getAttribute(encodedName);
		if(logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, "PSPL_PAECSPPI0046", 
				new String[] { encodedName, String.valueOf(scope) });
		}
		return value;
    }


    /**
     * Returns an <code>Enumeration</code> of String objects containing the names of
     * all the objects bound to this session under the <code>PORTLET_SCOPE</code>, or an
     * empty <code>Enumeration</code> if no attributes are available.
     *
     * @return			an <code>Enumeration</code> of
     *				<code>String</code> objects specifying the
     *				names of all the objects bound to
     *				this session, or an empty <code>Enumeration</code>
     *                if no attributes are available.
     *
     * @exception java.lang.IllegalStateException	if this method is called on an
     *					invalidated session
     */

    public Enumeration getAttributeNames() {

        return getAttributeNames(PORTLET_SCOPE);
    }

    /**
     * Returns an <code>Enumeration</code> of String objects containing the names of
     * all the objects bound to this session in the given scope, or an
     * empty <code>Enumeration</code> if no attributes are available in the
     * given scope.
     *
     * @param scope               session scope of the attribute names
     *
     * @return			an <code>Enumeration</code> of
     *				<code>String</code> objects specifying the
     *				names of all the objects bound to
     *				this session, or an empty <code>Enumeration</code>
     *                            if no attributes are available in the given scope.
     *
     * @exception java.lang.IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public Enumeration getAttributeNames(int scope) {

        Enumeration attrNames = httpSession.getAttributeNames();
        ArrayList attrs = new ArrayList();

        if (scope == APPLICATION_SCOPE) {
            while (attrNames.hasMoreElements()) {
                String attrName = (String) attrNames.nextElement();
                if (!attrName.startsWith(PORTLET_SCOPE_ATTR_PREFIX)) {
                    attrs.add(attrName);
                }
            }
        } else {
            // PORTLET_SCOPE
            while (attrNames.hasMoreElements()) {
                String attrName = (String) attrNames.nextElement();
                if (isTargetPortletID(attrName)) {
                    attrs.add(getDecodedAttrName(attrName));
                }
            }
        }
        return Collections.enumeration(attrs);
    }


    /**
     * Returns the time when this session was created, measured in
     * milliseconds since midnight January 1, 1970 GMT.
     *
     * @return				a <code>long</code> specifying
     * 					when this session was created,
     *					expressed in
     *					milliseconds since 1/1/1970 GMT
     *
     * @exception java.lang.IllegalStateException	if this method is called on an
     *					invalidated session
     */
    public long getCreationTime() {

        return httpSession.getCreationTime();
    }

    /**
     * Returns a string containing the unique identifier assigned to this session.
     *
     * @return				a string specifying the identifier
     *					assigned to this session
     */
    public String getId() {

        return httpSession.getId();
    }

    /**
     * Returns the last time the client sent a request associated with this session,
     * as the number of milliseconds since midnight January 1, 1970 GMT.
     *
     * <p>Actions that your portlet takes, such as getting or setting
     * a value associated with the session, do not affect the access
     * time.
     *
     * @return				a <code>long</code>
     *					representing the last time
     *					the client sent a request associated
     *					with this session, expressed in
     *					milliseconds since 1/1/1970 GMT
     */
    public long getLastAccessedTime() {

        return httpSession.getLastAccessedTime();
    }

    /**
     * Returns the maximum time interval, in seconds, for which the portlet container
     * keeps this session open between client accesses. After this interval,
     * the portlet container invalidates the session.  The maximum time
     * interval can be set
     * with the <code>setMaxInactiveInterval</code> method.
     * A negative time indicates the session should never timeout.
     *
     * @return		an integer specifying the number of
     *			seconds this session remains open
     *			between client requests
     *
     * @see		#setMaxInactiveInterval
     */
    public int getMaxInactiveInterval() {

        return httpSession.getMaxInactiveInterval();
    }

    /**
     * Invalidates this session (all scopes) and unbinds any objects bound to it.
     * <p>
     * Invalidating the portlet session will result in invalidating the underlying
     * <code>HttpSession</code>
     *
     * @exception java.lang.IllegalStateException	if this method is called on a
     *					session which has already been invalidated
     */
    public void invalidate() {

        httpSession.invalidate();
    }

    /**
     * Returns true if the client does not yet know about the session or
     * if the client chooses not to join the session.
     *
     * @return 				<code>true</code> if the
     *					server has created a session,
     *					but the client has not joined yet.
     *
     * @exception java.lang.IllegalStateException	if this method is called on a
     *					session which has already been invalidated
     *
     */
    public boolean isNew() {

        return httpSession.isNew();
    }

    /**
     * Removes the object bound with the specified name under
     * the <code>PORTLET_SCOPE</code> from
     * this session. If the session does not have an object
     * bound with the specified name, this method does nothing.
     *
     * @param name   the name of the object to be
     *               removed from this session in the
     *               <code> PORTLET_SCOPE</code>.
     *
     * @exception java.lang.IllegalStateException
     *                   if this method is called on a
     *                   session which has been invalidated
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public void removeAttribute(String name) throws IllegalArgumentException {

        removeAttribute(name, PORTLET_SCOPE);
    }

    /**
     * Removes the object bound with the specified name and the given scope from
     * this session. If the session does not have an object
     * bound with the specified name, this method does nothing.
     *
     * @param name   the name of the object to be
     *               removed from this session
     * @param scope  session scope of this attribute
     *
     * @exception java.lang.IllegalStateException
     *                   if this method is called on a
     *                   session which has been invalidated
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public void removeAttribute(String name, int scope) throws IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("The given attribute name is null");
        }
		String encodedName = getEncodedAttrName(name, scope);
		if(logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, "PSPL_PAECSPPI0046", 
				new String[] { encodedName, String.valueOf(scope) });
		}
        httpSession.removeAttribute(encodedName);
    }

    /**
     * Binds an object to this session under the <code>PORTLET_SCOPE</code>, using the name specified.
     * If an object of the same name in this scope is already bound to the session,
     * that object is replaced.
     *
     * <p>After this method has been executed, and if the new object
     * implements <code>HttpSessionBindingListener</code>,
     * the container calls
     * <code>HttpSessionBindingListener.valueBound</code>. The container then
     * notifies any <code>HttpSessionAttributeListeners</code> in the web
     * application.
     * <p>If an object was already bound to this session
     * that implements <code>HttpSessionBindingListener</code>, its
     * <code>HttpSessionBindingListener.valueUnbound</code> method is called.
     *
     * <p>If the value is <code>null</code>, this has the same effect as calling
     * <code>removeAttribute()</code>.
     *
     *
     * @param name		the name to which the object is bound under
     *                            the <code>PORTLET_SCOPE</code>;
     *				this cannot be <code>null</code>.
     * @param value		the object to be bound
     *
     * @exception java.lang.IllegalStateException	if this method is called on a
     *					session which has been invalidated
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public void setAttribute(String name, Object value) throws IllegalArgumentException {

        setAttribute(name, value, PORTLET_SCOPE);
    }

    /**
     * Binds an object to this session in the given scope, using the name specified.
     * If an object of the same name in this scope is already bound to the session,
     * that object is replaced.
     *
     * <p>After this method has been executed, and if the new object
     * implements <code>HttpSessionBindingListener</code>,
     * the container calls
     * <code>HttpSessionBindingListener.valueBound</code>. The container then
     * notifies any <code>HttpSessionAttributeListeners</code> in the web
     * application.
     * <p>If an object was already bound to this session
     * that implements <code>HttpSessionBindingListener</code>, its
     * <code>HttpSessionBindingListener.valueUnbound</code> method is called.
     *
     * <p>If the value is <code>null</code>, this has the same effect as calling
     * <code>removeAttribute()</code>.
     *
     *
     * @param name		the name to which the object is bound;
     *				this cannot be <code>null</code>.
     * @param value		the object to be bound
     * @param scope               session scope of this attribute
     *
     * @exception java.lang.IllegalStateException	if this method is called on a
     *					session which has been invalidated
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    public void setAttribute(String name, Object value, int scope) throws IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("The given attribute name is null");
        }
		String encodedName = getEncodedAttrName(name, scope);
        httpSession.setAttribute(encodedName, value);
		if(logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, "PSPL_PAECSPPI0046", 
				new String[] { encodedName, String.valueOf(scope) });
		}
    }

    /**
     * Specifies the time, in seconds, between client requests, before the
     * portlet container invalidates this session. A negative time
     * indicates the session should never timeout.
     *
     * @param interval		An integer specifying the number
     * 				of seconds
     */
    public void setMaxInactiveInterval(int interval) {

        httpSession.setMaxInactiveInterval(interval);
    }

    /**
     * Returns the portlet application context associated with this session.
     *
     * @return   the portlet application context
     */
    public PortletContext getPortletContext() {

        return portletContext;
    }

    /**
     * Returns the Session ID associated with the HttpSession.
     *
     * @return   the http Session Id
     */
    protected String getHttpSessionId() {

        return this.httpSession.getId();
    }

    /**
     * Returns a <code>Map</code> of the session attributes in
     * the portlet session scope.
     * <p>
     * The keys are of type <code>String</code> and the values in the
     * returned <code>Map</code> are from type <code>Object</code>.
     * <p>
     * If no session attributes exist this method returns an empty <code>Map</code>.
     *
     * @return     an immutable <code>Map</code> containing the session attributes in the
     *             portlet session scope as keys and attribute values as map values, or an empty <code>Map</code>
     *             if no session attributes exist. The keys in the
     *             map are of type String, the values of type
     *             Object.
     *  @since 2.0
     */
    public Map<String, Object> getAttributeMap() {
        Enumeration<String> attributeNames = getAttributeNames();
        if(attributeNames.hasMoreElements()) {
            Map<String, Object> attributes = new HashMap<String, Object>();
            while(attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                attributes.put(attributeName, getAttribute(attributeName));
            }
            return Collections.unmodifiableMap(attributes);
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Returns a <code>Map</code> of the session attributes in
     * the given session scope.
     * <p>
     * The keys are of type <code>String</code> and the values in the
     * eturned <code>Map</code> are from type <code>Object</code>.
     * <p>
     * If no session attributes exist this method returns an empty <code>Map</code>.
     *
     * @param scope               session scope of this attribute
     *
     * @return     an immutable <code>Map</code> containing the session attributes in the
     *             given scope as keys and attribute values as map values, or an empty <code>Map</code>
     *             if no session attributes exist. The keys in the
     *             map are of type String, the values of type
     *             Object.
     *  @since 2.0
     */
    public Map<String, Object> getAttributeMap(int scope) {
        Enumeration<String> attributeNames = getAttributeNames(scope);
        if(attributeNames.hasMoreElements()) {
            Map<String, Object> attributes = new HashMap<String, Object>();
            while(attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                attributes.put(attributeName, getAttribute(attributeName, scope));
            }
            return Collections.unmodifiableMap(attributes);
        } else {
            return Collections.emptyMap();
        }
    }

	public String getWindowID() {
		return this.windowID;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("PortletSession:{");
		sb.append("WindowID=");
		sb.append(getWindowID());
		sb.append(", HttpSessionID=");
		sb.append(getId());
		sb.append("}");
		return sb.toString();
	}

    private String getEncodedAttrName(String name, int scope) {

        String encodedName = null;

        if (scope == APPLICATION_SCOPE) {
            encodedName = name;
        } else {
            // PORTLET_SCOPE
            StringBuffer sb = new StringBuffer();
            sb.append(PORTLET_SCOPE_ATTR_PREFIX);
            sb.append(getWindowID());
            sb.append(QUESTION);
            sb.append(name);
            encodedName = sb.toString();
        }
        return encodedName;
    }

    // Get <ATTRIBUTE_NAME> from the fabricated attribute name:
    // "javax.portlet.w.<WindowID>?<ATTRIBUTE_NAME>". The assumption
    // here is the encoding of fabricated attribute name hase been
    // checked.
    private String getDecodedAttrName(String attrName) {

        return attrName.substring(attrName.indexOf(QUESTION) + 1);
    }

    // This method does the following two checkings:
    // 1. Check if the encoded PORTLET_SCOPE attribute name is valid.
    //    The correct fabricated attributename shoule be like:
    //    "javax.portlet.w.<WindowID>?<ATTRIBUTE_NAME>" where <WindowID> is the
    //    unique identifier for the portlet window that must not
    //    contain a "?" character.
    // 2. Check if this PORTLET_SCOPE attribute belongs to the current
    //    target window. This can only be done if the attribute name
    //    in correct PORTLET_SCOPE name format.
    private boolean isTargetPortletID(String name) {

        boolean valid = true;

        if (!name.startsWith(PORTLET_SCOPE_ATTR_PREFIX) || (name.indexOf(QUESTION) != name.lastIndexOf(QUESTION))) {
            valid = false;
        } else {
            String portletID = name.substring(LENGTH_OF_PORTLET_SCOPE_ATTR_PREFIX, name.indexOf(QUESTION));
            if (!portletID.equals(getWindowID())) {
                valid = false;
            }
        }
        return valid;
    }

}
