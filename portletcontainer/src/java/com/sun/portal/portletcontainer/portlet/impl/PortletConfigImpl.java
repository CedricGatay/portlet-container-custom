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

import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

import com.sun.portal.portletcontainer.common.descriptor.InitParamDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletInfoDescriptor;
import java.util.ArrayList;
import javax.xml.namespace.QName;

/**
 * The <code>PortletConfigImpl</code> class provides a default
 * implementation for the <code>PortletConfig</code> interface.
 *
 */

public class PortletConfigImpl implements PortletConfig {
    
    private String portletName;
    private PortletContext portletContext;
    private PortletDescriptor portletDescriptor;
    private PortletAppDescriptor portletAppDescriptor;
    private Map paramsMap = new HashMap();
    
    public PortletConfigImpl(PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor,
            String portletName,
            PortletContext portletContext) {
        
        this.portletName = portletName;
        this.portletContext = portletContext;
        this.portletDescriptor = portletDescriptor;
        this.portletAppDescriptor = portletAppDescriptor;
        initParamsMap();
    }
    
    /**
     * construct an internal map to store the init parameters
     */
    private void initParamsMap() {
        
        if (this.portletDescriptor != null && this.portletDescriptor.getInitParamDescriptors()!=null) {
            for (InitParamDescriptor ipd : this.portletDescriptor.getInitParamDescriptors()) {
                paramsMap.put(ipd.getParamName(), ipd.getParamValue());
            }
        }
    }
    
    /**
     * Returns a String containing the value of the named initialization parameter,
     * or null if the parameter does not exist.
     *
     * @param name	a <code>String</code> specifying the name
     *			of the initialization parameter
     *
     * @return		a <code>String</code> containing the value
     *			of the initialization parameter
     *
     * @exception	java.lang.IllegalArgumentException
     *                      if name is <code>null</code>.
     */
    public String getInitParameter(java.lang.String name)
    throws IllegalArgumentException {
        
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return (String)paramsMap.get(name);
    }
    
    /**
     * Returns the names of the portlet initialization parameters as an
     * <code>Enumeration</code> of String objects, or an empty <code>Enumeration</code> if the
     * portlet has no initialization parameters.
     *
     * @return		an <code>Enumeration</code> of <code>String</code>
     *			objects containing the names of the portlet
     *			initialization parameters, or an empty <code>Enumeration</code> if the
     *                    portlet has no initialization parameters.
     */
    public Enumeration getInitParameterNames() {
        return Collections.enumeration(paramsMap.keySet());
    }
    
    /**
     * Returns the <code>PortletContext</code> of the portlet application
     * the portlet is in.
     *
     * @return   a <code>PortletContext</code> object, used by the
     *           caller to interact with its portlet container
     *
     * @see PortletContext
     */
    public PortletContext getPortletContext() {
        return this.portletContext;
    }
    
    /**
     * Returns the name of the portlet.
     * <P>
     * The name may be provided via server administration, assigned in the
     * portlet application deployment descriptor with the <code>portlet-name</code>
     * tag.
     *
     * @return   the portlet name
     */
    public String getPortletName() {
        
        return this.portletName;
    }
    
    /**
     * Gets the resource bundle for the given locale based on the
     * resource bundle defined in the deployment descriptor
     * with <code>resource-bundle</code> tag or the inlined resources
     * defined in the deployment descriptor.
     *
     * @param    locale    the locale for which to retrieve the resource bundle
     *
     * @return   the resource bundle for the given locale
     *
     */
    public ResourceBundle getResourceBundle(Locale locale)
    throws MissingResourceException {
        
        String rbName;
        ResourceBundle rb = null;
        
        // check if the portlet descriptor is null
        if (this.portletDescriptor == null) {
            // cannot obtain name of the resource class and the
            // name of the key for the resource
            throw new
                    MissingResourceException("PortletConfigImpl.getResourceBundle: missing portlet descriptor", null, null);
        } else {
            rbName = this.portletDescriptor.getResourceBundle();
        }
        
        PortletInfoDescriptor pid = this.portletDescriptor.getPortletInfoDescriptor();
        Object values[] = new Object[]{"","",""};
        boolean valuesDefinedInline[] = new boolean[]{ false, false, false };
        
        if (pid != null) {
            if (pid.getTitle() != null) {
                values[0] = pid.getTitle();
                valuesDefinedInline[0] = true;
            }
            if (pid.getShortTitle() != null) {
                values[1] = pid.getShortTitle();
                valuesDefinedInline[1] = true;
            }
            if (pid.getKeywords() != null) {
                List keywords = pid.getKeywords();
                
                String[] strArray = new String[keywords.size()];
                values[2] = (String[]) keywords.toArray(strArray);
                valuesDefinedInline[2] = true;
            }
        }
        
        try {
            if (rbName != null) {
                rb = ResourceBundle.getBundle(rbName, locale, Thread.currentThread().getContextClassLoader());
            }
        } catch (MissingResourceException me) {
            //falls through
        }
        rb = (ResourceBundle) new PortletResourceBundle(rb, rbName, values, valuesDefinedInline);
        
        return rb;
    }
    
  /**
   * Returns the names of the public render parameters supported by the portlet
   * as an <code>Enumeration</code> of <code>String</code> objects, 
   * or an empty <code>Enumeration</code> if the 
   * portlet has not defined public render parameters.
   * <p>
   * Public render parameters are defined in the portlet deployment descriptor
   * with the <code>supported-public-render-parameter</code> element.    
   *
   * @return		an <code>Enumeration</code> of <code>String</code> 
   *			objects containing the names of the public 
   *			render parameters, or an empty <code>Enumeration</code> if the 
   *                    portlet has not defined support for any public render parameters
   *                    in the portlet deployment descriptor.
   * @since 2.0 
   */
    public Enumeration<String> getPublicRenderParameterNames() {
        List<String> publicRenderParameters =
                this.portletDescriptor.getSupportedPublicRenderParameterIdentifiers();
        if(publicRenderParameters != null) {
            return Collections.enumeration(publicRenderParameters);
        }
        List<String> emptyList = Collections.emptyList();
        return Collections.enumeration(emptyList);
    }
    
    
    /**
     * Returns the default namespace for events and public render parameters.
     * This namespace is defined in the portlet deployment descriptor
     * with the <code>default-namespace</code> element.
     * <p>
     * If no default namespace is defined in the portlet deployment
     * descriptor this methods returns the XML default namespace
     * <code>XMLConstants.NULL_NS_URI</code>.
     *
     * @return the default namespace defined in the portlet deployment
     *         descriptor, or <code>XMLConstants.NULL_NS_URI</code> is non is
     *         defined.
     * @since 2.0
     */
    public String getDefaultNamespace() {
        return this.portletAppDescriptor.getDefaultNamespace();
    }

    /**
    * Returns the QNames of the publishing events supported by the portlet
    * as an <code>Enumeration</code> of <code>QName</code> objects, 
    * or an empty <code>Enumeration</code> if the 
    * portlet has not defined any publishing events.    
    * <p>
    * Publishing events are defined in the portlet deployment descriptor
    * with the <code>supported-publishing-event</code> element.    
    * <p>
    * Note that this call does not return any events published that have not been
    * declared in the deployment descriptor as supported.
    * 
    * @return		an <code>Enumeration</code> of <code>QName</code> 
    *			objects containing the names of the publishing events, 
    *			or an empty <code>Enumeration</code> if the 
    *                    portlet has not defined any support for publishing events in
    *                    the deployment descriptor.
    * @since 2.0 
    */
    public Enumeration<QName> getPublishingEventQNames() {
        List<QName> supportedPublishingEvents = this.portletDescriptor.getSupportedPublishingEvents();
        if(supportedPublishingEvents != null) {
            return Collections.enumeration(supportedPublishingEvents);
        }
        List<QName> emptyList = Collections.emptyList();
        return Collections.enumeration(emptyList);
    }

  
    /**
    * Returns the QNames of the processing events supported by the portlet
    * as an <code>Enumeration</code> of <code>QName</code> objects, 
    * or an empty <code>Enumeration</code> if the 
    * portlet has not defined any processing events.    
    * <p>
    * Processing events are defined in the portlet deployment descriptor
    * with the <code>supported-processing-event</code> element.    
    * 
    * @return		an <code>Enumeration</code> of <code>QName</code> 
    *			objects containing the names of the processing events, 
    *			or an empty <code>Enumeration</code> if the 
    *                    portlet has not defined any support for processing events in
    *                    the deployment descriptor.
    * @since 2.0 
    */
    public Enumeration<QName> getProcessingEventQNames() {
        List<QName> supportedProcessingEvents = this.portletDescriptor.getSupportedProcessingEvents();
        if(supportedProcessingEvents != null) {
            return Collections.enumeration(supportedProcessingEvents);
        }
        List<QName> emptyList = Collections.emptyList();
        return Collections.enumeration(emptyList);
    }

    /**
    * Returns the locales supported by the portlet
    * as an <code>Enumeration</code> of <code>Locale</code> objects, 
    * or an empty <code>Enumeration</code> if the 
    * portlet has not defined any supported locales.    
    * <p>
    * Supported locales are defined in the portlet deployment descriptor
    * with the <code>supported-locale</code> element.    
    * 
    * @return		an <code>Enumeration</code> of <code>Locale</code> 
    *			objects containing the supported locales, 
    *			or an empty <code>Enumeration</code> if the 
    *                    portlet has not defined any supported locales in
    *                    the deployment descriptor.
    * @since 2.0
    */
    public Enumeration<Locale> getSupportedLocales() {
        List<String> supportedLocaleStrings = this.portletDescriptor.getSupportedLocales();
        if(supportedLocaleStrings != null) {
            List<Locale> supportedLocales = new ArrayList<Locale>(supportedLocaleStrings.size());
            for(String s : supportedLocaleStrings) {
                supportedLocales.add(new Locale(s));
            }
            return Collections.enumeration(supportedLocales);
        }
        List<Locale> emptyList = Collections.emptyList();
        return Collections.enumeration(emptyList);
    }
    
  /**
   * Returns the container container runtime options
   * and values for this portlet.
   * <p>
   * The portlet can set container runtime
   * options in the <code>portlet.xml</code> via the
   * <code>container-runtime-option</code> element with a name and a
   * value on the application and portlet level.<br>
   * If a container runtime option is set on the portlet application 
   * level and on the portlet level with the same name the setting 
   * on the portlet level takes precedence and overwrites the one 
   * set on the portal application level.
   * <p>
   * The map returned from this method will provide the subset the
   * portlet container supports of the options the portlet has specified 
   * in the <code>portlet.xml</code>. Options that the portlet container
   * does not support will not be returned in this map.
   * <p>
   * The map will contain name of the runtime option as key of type String
   * and the runtime options as values of type String array (<code>String[]</code>)
   * with the values specified in the <code>portlet.xml</code> deployment descriptor.
   * 
   * @since 2.0
   *  
   * @return  an immutable <code>Map</code> containing portlet
   *          container runtime options names as keys and the 
   *          container runtime values as map values, or an empty <code>Map</code>
   *          if no portlet container runtime options are set
   *          in the <code>portlet.xml</code> or supported by this portlet container. 
   *          The keys in the map are of type String. The values in the map are of type
   *          String array (<code>String[]</code>).
   */
  public Map<String, String[]> getContainerRuntimeOptions() {
      return this.portletAppDescriptor.getContainerRuntimeOptions(this.portletDescriptor.getPortletID());
  }
}
