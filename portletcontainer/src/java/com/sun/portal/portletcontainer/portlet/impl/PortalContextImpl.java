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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;


/**
 * The <code>PortalContextImpl</code> class provides a default
 * implementation for the <code>PortalContext</code> interface.
 *
 */

public class PortalContextImpl implements PortalContext {

    public static final String PORTAL_INFO =
        // format: vendorname.majorversion.minorversion
        "OpenPortal Portlet Container/2.0";

    private static List supportedPortletModes = new ArrayList();
    private static List supportedWindowStates = new ArrayList();

    static {
        supportedPortletModes.add(PortletMode.VIEW);
        supportedPortletModes.add(PortletMode.EDIT);
        supportedPortletModes.add(PortletMode.HELP);
        supportedWindowStates.add(WindowState.NORMAL);
        supportedWindowStates.add(WindowState.MINIMIZED);
        supportedWindowStates.add(WindowState.MAXIMIZED);
    }
    
    private Map<String, String> properties;
    private String portalInfo;
    
    public PortalContextImpl() {
        this.properties = new HashMap<String, String>();
		this.properties.put(MARKUP_HEAD_ELEMENT_SUPPORT, "true");
    }

    public PortalContextImpl(Map<String, String> properties) {
        this.properties = properties;
    }    
    
  /**
   * Returns the portal property with the given name, 
   * or a <code>null</code> if there is 
   * no property by that name.
   *
   * @param  name    property name
   *
   * @return  portal property with key <code>name</code>
   *
   * @exception	java.lang.IllegalArgumentException	
   *                      if name is <code>null</code>.
   */
    
    public String getProperty(java.lang.String name) 
	throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("The given property name is null");
        }

        return (String)properties.get(name);
    }
    
  /**
   * Returns all portal property names, or an empty 
   * <code>Enumeration</code> if there are no property names.
   *
   * @return  All portal property names as an 
   *          <code>Enumeration</code> of <code>String</code> objects
   */
    public Enumeration getPropertyNames() {
        return Collections.enumeration(properties.keySet());
    }
        
  /**
   * Returns all supported portlet modes by the portal
   * as an enumertation of <code>PorltetMode</code> objects.
   * <p>
   * The portlet modes must at least include the
   * standard portlet modes <code>EDIT, HELP, VIEW</code>.
   *
   * @return  All supported portal modes by the portal
   *          as an enumertation of <code>PorltetMode</code> objects.
   */
    
    public Enumeration getSupportedPortletModes() {
        return Collections.enumeration(supportedPortletModes);
    }
    
  /**
   * Returns all supported window states by the portal
   * as an enumertation of <code>WindowState</code> objects.
   * <p>
   * The window states must at least include the
   * standard window states <code> MINIMIZED, NORMAL, MAXIMIZED</code>.
   *
   * @return  All supported window states by the portal
   *          as an enumertation of <code>WindowState</code> objects.
   */
    
    public Enumeration getSupportedWindowStates() {
        return Collections.enumeration(supportedWindowStates);
    }

  /**
   * Returns information about the portal like vendor, version, etc.
   * <p>
   * The form of the returned string is <I>servername/versionnumber</I>. For 
   * example, the reference implementation Pluto may return the string 
   * <CODE>Pluto/1.0</CODE>.
   * <p>
   * The portlet container may return other optional information  after the 
   * primary string in parentheses, for example, <CODE>Pluto/1.0 
   * (JDK 1.3.1; Windows NT 4.0 x86)</CODE>.
   * 
   * @return a <CODE>String</CODE> containing at least the portal name and version number
   */
    
    public String getPortalInfo() {
        if(this.portalInfo == null) {
            this.portalInfo = PORTAL_INFO;
        }
        return portalInfo;
    }
    
    protected void setPortalInfo(String portalInfo) {
        this.portalInfo = portalInfo;
    }
}
