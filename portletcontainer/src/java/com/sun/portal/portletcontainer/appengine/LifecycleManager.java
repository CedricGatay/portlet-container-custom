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
 

package com.sun.portal.portletcontainer.appengine;

import javax.portlet.Portlet;
import javax.portlet.PortletContext;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PreferencesValidator;
import javax.portlet.filter.FilterChain;

import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;

/**
 * The lifecycle manager maintains the life cycle of portlets and 
 * other portlet container objects running inside the portlet application 
 * engine. <code>LifecycleManager</code> is a interface that is used by the 
 * <code>PortletAppEngineServlet</code>. 
 **/
public interface LifecycleManager {

    // Key for LifeCycleManager object in servlet context
    public static final String LIFECYCLE_MANAGER = "lifecycle_manager";
    
    /**
     * Gets the Portlet App Descriptor object. 
     * <P>
     * The <code>PortletAppDescriptor</code> contains the Portlet configuration 
     * information defined in the portlet desployment descriptor,that is,
     * the portlet.xml file.
     * <P>
     * The <code>PortletAppDescriptor</code> stores all the descriptors associated
     * with Portlet Applications. The descriptors include:
     * <P>
     * <UL>
     *   <LI>PortletsDescriptor</LI>
     *   <LI>PortletPreferencesDescriptor</LI>
     *   <LI>SecurityRolesDescriptor</LI>
     *   <LI>SecurityConstraintsDescriptor</LI>
     * </UL>
     *
     * @return the PortletAppDescriptor object
     **/
    public PortletAppDescriptor getPortletAppDescriptor();  

    /**
     * Gets the Descriptor object that corresponds to the vendor portlet.xml
     * <P>
     * The Descriptor object contains the configuration 
     * information defined in the vendor's portlet descriptor, for example in 
     * case of Sun, it will be sun-portlet.xml.
     * <P>
     *
     * @param vendorPortletXMLName name of the vendor portlet xml
     * @return the the Descriptor object that corresponds to the vendor portlet.xml
     **/
    public Object getPortletExtensionDescriptor(String vendorPortletXMLName);  

    /**
     * Returns the portlet of the specified portletName.
     * <P>
     * @param portletName The portlet name
     * @return javax.portlet.Portlet a portlet object.
     * @exception LifecycleManagerException if an error occurs in
     * getting the portlet object, or when instantiating the portlet
     * object.
     **/
    public Portlet getPortlet( String portletName ) throws LifecycleManagerException; 

    /**
     * Removes the portlet of the specified portletName from the
     * lifecycle manager.
     *
     * <P>
     * @param portletName The portlet name
     **/
    public void removePortlet( String portletName );

    /**
     * Returns the portlet config of the specified portletName.
     * <P>
     * @param portletName The portlet name
     * @return javax.portlet.PortletConfig a portlet config
     * object. Returns null if not found.
     **/
    public PortletConfig getPortletConfig( String portletName );

    /**
     * Returns the preferences validator if it is defined in the 
     * descriptor.
     * <P>
     * @param portletName The portlet name
     * @return javax.portlet.PreferencesValidator portlet validator 
     * object. Returns null if not found.
     **/
    public PreferencesValidator getPreferencesValidator(String portletName);
    
    /**
     * Removes the portlet config of the specified portletName from the
     * lifecycle manager.
     *
     * <P>
     * @param portletName The portlet name
     **/
    public void removePortletConfig( String portletName );

    /**
     * Returns the PortletContext object.
     * <P>
     * @return A javax.portlet.PortletContext object, used by the caller 
     * to interact with its portlet application.
     **/
    public PortletContext getPortletContext();

    /**
     * Returns the PortalContext object.
     * <P>
     * @return A javax.portlet.PortalContext object, used by the caller 
     * to query for portal information.
     **/
    public PortalContext getPortalContext();

    /**
     * Returns the FilterChain object corresponding to the 
     * specified portlet and lifecycle method.
     * <P>
     * @param portletName 
     * @param p 
     * @param lifeCycle 
     * @return javax.portlet.filter.FilterChain
     */
    public FilterChain getFilterChain(String portletName, Portlet p, String lifeCycle);
}
