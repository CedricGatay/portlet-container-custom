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


package com.sun.portal.portletcontainer.common.descriptor;

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.PortletID;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The portlet descriptor is the descriptor for a portlet.
 * <P>
 * The PortletDescriptor loads all the descriptors for a portlet into
 * memory, it delegates the loading of each descriptor to the sub
 * descriptor classe.
 * <P>
 * The PortletDescriptor class provides member methods to get to all
 * sub descriptors, the portlet name, portlet class name, as well as
 * the description, if available. The following get**() methods are defined:
 * <UL>
 *   <LI><code>getPortletName()</code>
 *   <LI><code>getPortletClassName()</code>
 *   <LI><code>getDescriptions()</code>
 *   <LI><code>getDisplayName()</code>
 *   <LI><code>getDisplayNames()</code>
 *   <LI><code>getDisplayNameMap()</code>
 *   <LI><code>getSupportedLocales()</code>
 *   <LI><code>getPortletPreferencesDescriptor()</code>
 *   <LI><code>getInitParamDescriptors()</code>
 *   <LI><code>getSupportsDescriptors()</code>
 *   <LI><code>getSupportsDescriptorMap()</code>
 *   <LI><code>getSupportedPortletModes</code>
 *   <LI><code>getCacheExpiration</code>
 *   <LI><code>getPortletInfoDescriptor</code>
 *   <LI><code>getSecurityRoleRefDescriptors</code>
 *   <LI><code>getContainerRuntimeOptionDescriptors</code>
 *   <LI><code>getDeploymentExtensionDescriptors</code>
 *</UL>
 */
public class PortletDescriptor {
    
    // Global Variables
    private String id;
    private String portletAppName;
    private String portletName;
    private PortletID portletID;
    private String portletClassName;
    private String resourceBundle;
    private List<String> descriptions;
    private List<String> displayNames;
    private Map<String, String> descriptionMap;
    private Map<String, String> displayNameMap;
    private List<String> supportedLocales;
    private PortletPreferencesDescriptor portletPreferencesDescriptor;
    private List<InitParamDescriptor> initParamDescriptors ;
    private List<SupportsDescriptor> supportsDescriptors;
    private String cacheExpiration;
    private String cacheScope;
    private PortletInfoDescriptor infoDescriptor;
    private List<SecurityRoleRefDescriptor> secRoleRefDescriptors;
    
    //Added by v 2.0
    private Map<String, String[]> containerRTOptions;
    private List<QName> supportedProcessingEvents;
    private List<QName> supportedPublishingEvents;
    private List<String> supportedPublicRenderParameterIdentifiers;
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PortletDescriptor.class, "PCCLogMessages");
    
    public PortletDescriptor(String portletAppName) {
        this.portletAppName = portletAppName;
    }
    
    /**
     * Loads the portlet descriptor and sub descriptors into memory.
     * <P>
     * The portlet sub descriptors includes:
     * <UL>
     *   <LI><code>SupportsDescriptor()</code>
     *   <LI><code>PortletPreferencesDescriptor()</code>
     *   <LI><code>InitParamDescriptor()</code>
     *   <LI><code>PortletInfoDescriptor()</code>
     * </UL>
     * @param The portlet descriptor Element
     * @see SupportsDescriptor
     * @see PortletPreferencesDescriptor
     * @see InitParamDescriptor
     * @see PortletInfoDescriptor
     */
    public void load( Element portletElement, String namespaceURI, String defaultNameSpace ) throws
            DeploymentDescriptorException {
        
        //load id attribute
        id = PortletXMLDocumentHelper.getId(portletElement);
        
        //load portlet name
        portletName = PortletXMLDocumentHelper.getChildTextTrim(portletElement, PortletDescriptorConstants.PORTLET_NAME);
        if ( portletName == null ) {
            logger.warning( "PSPL_PCCCSPPCCD0005");
        }
        
        portletID = new PortletID(this.portletAppName, portletName);
        
        //load portlet class name
        portletClassName = PortletXMLDocumentHelper.getChildTextTrim(portletElement, PortletDescriptorConstants.PORTLET_CLASS);
        if ( portletClassName == null) {
            logger.warning( "PSPL_PCCCSPPCCD0006");
        }
        
        NodeList descriptionElements =
                portletElement.getElementsByTagName(PortletDescriptorConstants.DESCRIPTION);
        String description;
        if(descriptionElements.getLength()>0){
            descriptionMap = new HashMap<String, String>(descriptionElements.getLength());            
            descriptions = new ArrayList<String>(descriptionElements.getLength());
            for (int i = 0; i < descriptionElements.getLength(); i++) {
                Element descriptionElement = (Element)descriptionElements.item(i);
                description = PortletXMLDocumentHelper.getTextTrim(descriptionElement);
                descriptions.add(description);
                Map descAttributes = PortletXMLDocumentHelper.getAttributeTable(descriptionElement);
                Iterator dait = descAttributes.entrySet().iterator();
                String lang;
                if(dait.hasNext()) {
                    while(dait.hasNext()) {
                        Map.Entry entry = (Map.Entry) dait.next();
                        lang = (String)entry.getKey();
                        if (lang.equals(PortletDescriptorConstants.XML_LANG_ATTR)) {
                            descriptionMap.put((String)entry.getValue(), description);
                        }
                    }
                } else {
                    lang = Locale.getDefault().toString().replace('_', '-');
                    descriptionMap.put(lang, description);
                }
            }
        }
        
        NodeList displayNameElements = portletElement.getElementsByTagName(PortletDescriptorConstants.DISPLAY_NAME);
        String displayName;
        if(displayNameElements.getLength()>0){
            displayNames = new ArrayList<String>(displayNameElements.getLength());
            displayNameMap = new HashMap<String, String>(displayNameElements.getLength());
            for (int i = 0; i < displayNameElements.getLength(); i++) {
                Element displayNameElement = (Element)displayNameElements.item(i);
                displayName = PortletXMLDocumentHelper.getTextTrim(displayNameElement);
                displayNames.add(displayName);
                Map displayNameAttributes = PortletXMLDocumentHelper.getAttributeTable(displayNameElement);
                Iterator dnit = displayNameAttributes.entrySet().iterator();
                String lang;
                if(dnit.hasNext()) {
                    while(dnit.hasNext()) {
                        Map.Entry entry = (Map.Entry) dnit.next();
                        lang = (String)entry.getKey();
                        if (lang.equals(PortletDescriptorConstants.XML_LANG_ATTR)) {
                            displayNameMap.put((String)entry.getValue(), displayName);
                        }
                    }
                } else {
                    lang = Locale.getDefault().toString().replace('_', '-');
                    displayNameMap.put(lang, displayName);
                }
            }
        }
        NodeList supportedLocaleElements =
                portletElement.getElementsByTagName(PortletDescriptorConstants.SUPPORTED_LOCALE);
        if(supportedLocaleElements.getLength()>0){
            supportedLocales = new ArrayList<String>(supportedLocaleElements.getLength());
            for (int i = 0; i < supportedLocaleElements.getLength(); i++) {
                Element supportedLocaleElement = (Element)supportedLocaleElements.item(i);
                supportedLocales.add( PortletXMLDocumentHelper.getTextTrim(supportedLocaleElement) );
            }
        }
        
        // loads supports descriptor
        NodeList supportsElements =
                portletElement.getElementsByTagName(PortletDescriptorConstants.SUPPORTS);
        if ( supportsElements.getLength()==0 ) {
            logger.warning("PSPL_PCCCSPPCCD0008");
        } else {
            supportsDescriptors = new ArrayList<SupportsDescriptor>(supportsElements.getLength());
            for (int i = 0; i < supportsElements.getLength(); i++) {
                Element supportsElement = (Element)supportsElements.item(i);
                SupportsDescriptor supportsDescriptor = new SupportsDescriptor();
                supportsDescriptor.load( supportsElement, namespaceURI );
                supportsDescriptors.add( supportsDescriptor );
            }
        }
        
        // loads preferences descriptor
        Element pPreferencesElement =
                PortletXMLDocumentHelper.getChildElement(portletElement, PortletDescriptorConstants.PORTLET_PREFERENCES);
        if ( pPreferencesElement != null ) {
            portletPreferencesDescriptor = new PortletPreferencesDescriptor(portletName);
            portletPreferencesDescriptor.load( pPreferencesElement, namespaceURI );
            
        }
        // loads init param descriptor
        NodeList initParamElements =
                portletElement.getElementsByTagName(PortletDescriptorConstants.INIT_PARAM);
        if(initParamElements.getLength()>0){
            initParamDescriptors = new ArrayList<InitParamDescriptor>(initParamElements.getLength());
            for (int i = 0; i < initParamElements.getLength(); i++) {
                Element initParamElement = (Element)initParamElements.item(i);
                InitParamDescriptor initParamDescriptor = new InitParamDescriptor();
                initParamDescriptor.load( initParamElement, namespaceURI );
                initParamDescriptors.add( initParamDescriptor );
            }
        }
        
        // load expiration cache
        cacheExpiration = PortletXMLDocumentHelper.getChildTextTrim(portletElement, PortletDescriptorConstants.EXPIRATION_CACHE);

        // load cache scope
        setCachingScope(PortletXMLDocumentHelper.getChildTextTrim(portletElement, PortletDescriptorConstants.CACHE_SCOPE));
        
        // loads info descriptor
        Element infoElement =
                PortletXMLDocumentHelper.getChildElement(portletElement, PortletDescriptorConstants.PORTLET_INFO);
        if (infoElement != null) {
            infoDescriptor = new PortletInfoDescriptor();
            infoDescriptor.load( infoElement, namespaceURI );
        }
        
        // loads security role ref descriptors
        NodeList secRoleElements =
                portletElement.getElementsByTagName(PortletDescriptorConstants.SECURITY_ROLE_REF);
        
        if(secRoleElements.getLength()>0){
            secRoleRefDescriptors = new ArrayList<SecurityRoleRefDescriptor>(secRoleElements.getLength());
            for (int i = 0; i < secRoleElements.getLength(); i++) {
                Element secRoleElement = (Element)secRoleElements.item(i);
                SecurityRoleRefDescriptor secRoleRefDescriptor = new SecurityRoleRefDescriptor();
                secRoleRefDescriptor.load( secRoleElement, namespaceURI );
                secRoleRefDescriptors.add( secRoleRefDescriptor );
            }
        }
        
        // loads resource bundle
        resourceBundle = PortletXMLDocumentHelper.getChildTextTrim(portletElement, PortletDescriptorConstants.RESOURCE_BUNDLE);
        
        //load supported processing event
        //supportedProcessingEvents = PortletXMLDocumentHelper.getSupportedEventChildElementsQName(portletElement, defaultNameSpace, PortletDescriptorConstants.SUPPORTED_PROCESSING_EVENT);
        NodeList supportedProcessingEventElements = portletElement.getElementsByTagName(PortletDescriptorConstants.SUPPORTED_PROCESSING_EVENT);
        if(supportedProcessingEventElements.getLength() > 0){
            supportedProcessingEvents = new ArrayList<QName>(supportedProcessingEventElements.getLength());
            for(int i=0; i<supportedProcessingEventElements.getLength(); i++){
                Element supportedProcessingEventElement = (Element) supportedProcessingEventElements.item(i);
                QName qname = getQName(supportedProcessingEventElement, defaultNameSpace);
                if(qname != null) {
                    supportedProcessingEvents.add(qname);
                }
            }
        }

        //load supported publishing event
        //supportedPublishingEvents = PortletXMLDocumentHelper.getSupportedEventChildElementsQName(portletElement, defaultNameSpace, PortletDescriptorConstants.SUPPORTED_PUBLISHING_EVENT);
        NodeList supportedPublishingEventElements = portletElement.getElementsByTagName(PortletDescriptorConstants.SUPPORTED_PUBLISHING_EVENT);
        if(supportedPublishingEventElements.getLength() > 0){
            supportedPublishingEvents = new ArrayList<QName>(supportedPublishingEventElements.getLength());
            for(int i=0; i<supportedPublishingEventElements.getLength(); i++){
                Element supportedPublishingEventElement = (Element) supportedPublishingEventElements.item(i);
                QName qname = getQName(supportedPublishingEventElement, defaultNameSpace);
                if(qname != null) {
                    supportedPublishingEvents.add(qname);
                }
            }
        }
        
        //load supported public render parameter
        supportedPublicRenderParameterIdentifiers = PortletXMLDocumentHelper.getChildElementsText(portletElement, PortletDescriptorConstants.SUPPORTED_PUBLIC_RENDER_PARAMETER);
        
        //load container runtime options
        containerRTOptions = PortletXMLDocumentHelper.getRuntimeOptions(portletElement);
    }
    
    /**
     * Returns portlet name.
     * <P>
     * @return <code>String</code> of the portlet name.
     */
    public String getPortletName() {
        return portletName;
    }
    
    /**
     * Returns portlet class name.
     * <P>
     * @return <code>String</code> of the portlet class name.
     */
    public String getClassName() {
        return portletClassName;
    }
    
    /**
     * Returns the description as a <code>String</code>. If there's more
     * than one descriptions are defined, returns the first one.
     * <P>
     * @return <code>String</code> of the description. The return
     * value could be null if no description is defined.
     */
    public String getDescription() {
        String description = null;
        if ( descriptions !=null && !descriptions.isEmpty() ) {
            description = (String)descriptions.get(0);
        }
        return description;
    }
    
    /**
     * Returns portlet descriptions in a List.
     * <P>
     * @return <code>List</code> of <code>String</code>s of the
     * portlet descriptions. Empty <code>List</code> will be returned
     * if not defined.
     */
    public List<String> getDescriptions() {
        if(descriptions == null) {
            return Collections.emptyList();
        }
        return descriptions;
    }
    
    /**
     * Returns portlet descriptions in a Map.
     * <P>
     * @return <code>Map</code> of lang/description  pairs of the
     * portlet descriptions. Empty <code>Map</code> will be returned
     * if not defined.
     */
    public Map<String, String> getDescriptionMap() {
        if(descriptionMap == null) {
            return Collections.emptyMap();
        }
        return descriptionMap;
    }
    
    /**
     * Returns portlet display names in a List.
     * <P>
     * @return <code>List</code> of <code>String</code>s of the
     * portlet display names. Empty <code>List</code> will be returned
     * if not defined.
     */
    public List getDisplayNames() {
        if(displayNames == null) {
            return Collections.emptyList();
        }
        return displayNames;
    }
    
    /**
     * Returns portlet display names in a Map
     * <P>
     * @return <code>Map</code> of lang/display name pairs of the
     * portlet display names. Empty <code>Map</code> will be returned
     * if not defined.
     */
    public Map getDisplayNameMap() {
        if(displayNameMap == null) {
            return Collections.emptyMap();
        }
        return displayNameMap;
    }
    
    /**
     * Returns the supported locales in a List.
     * <P>
     * @return <code>List</code> of <code>String</code>s of the
     * supported locales. Empty <code>List</code> will be returned
     * if not defined.
     */
    public List<String> getSupportedLocales() {
        if(supportedLocales == null) {
            return Collections.emptyList();
        }
        return supportedLocales;
    }
    
    /**
     * Returns the portlet preferences descriptor.
     * <P>
     * @return <code>PortletPreferencesDescriptor</code>
     */
    public PortletPreferencesDescriptor getPortletPreferencesDescriptor() {
        return portletPreferencesDescriptor;
    }
    
    /**
     * Returns the init param descriptors.
     * <P>
     * @return <code>List</code> of <code>InitParamDescriptor</code>s.
     */
    public List<InitParamDescriptor> getInitParamDescriptors() {
        if(initParamDescriptors == null) {
            return Collections.emptyList();
        }
        return initParamDescriptors;
    }
    
    /**
     * Returns the supports descriptors.
     * <P>
     * @return <code>List</code> of <code>SupportsDescriptor</code>s.
     */
    public List<SupportsDescriptor> getSupportsDescriptors() {
        if(supportsDescriptors == null) {
            return Collections.emptyList();
        }
        return supportsDescriptors;
    }
    
    /**
     * Returns the supported mime types.
     * <P>
     * @return <code>List</code> of supported mime types.
     */
    public List<String> getSupportedMimeTypes() {
        List<String> mimeTypeList = new ArrayList<String>();
        Iterator iterator = supportsDescriptors.iterator();
        while ( iterator.hasNext()) {
            SupportsDescriptor supportsDescriptor =
                    (SupportsDescriptor)iterator.next();
            mimeTypeList.add( supportsDescriptor.getMimeType() );
        }
        return mimeTypeList;
    }
    
    /**
     * Returns the supported portlet modes of a specified mime type.
     * <P>
     * @return <code>List</code> of supported portlet modes.
     */
    public List<String> getSupportedPortletModes( String mime ) {
        
        List<String> supportList = null;
        boolean stop = false;
        Iterator iterator = supportsDescriptors.iterator();
        while ( iterator.hasNext() && !stop ) {
            SupportsDescriptor supportsDescriptor =
                    (SupportsDescriptor)iterator.next();
            String mimeType = supportsDescriptor.getMimeType();
            if ( mimeType.toLowerCase(Locale.ENGLISH).equals("*/*")) {
                supportList = supportsDescriptor.getPortletModes();
                stop = true;
            } else if ( mimeType.toLowerCase(Locale.ENGLISH).equals("text/*") &&
                    mime.toLowerCase(Locale.ENGLISH).startsWith("text/") ) {
                supportList = supportsDescriptor.getPortletModes();
                stop = true;
            } else if (supportsDescriptor.getMimeType().equals( mime ) ) {
                supportList = supportsDescriptor.getPortletModes();
                stop = true;
            }
        }
        if(supportList == null) {
            return Collections.emptyList();
        }
        return supportList;
    }
    
    /**
     * Returns the cache expiration.
     * <P>
     * @return <code>int</code> of expiration number. Returns -999 if
     * expiration is not defined. A value of -1 means cache is never
     * expired, a value of 0 means cache is disabled.
     * 
     * Note that WSRP specifically need information whether expiration cache is
     * declared or not and that is why -999 is returned if no expiration cache 
     * is defined.
     */
    public int getCacheExpiration() {
        int retValue = PortletDescriptorConstants.EXPIRATION_CACHE_NOT_DEFINED;
        if ( cacheExpiration != null ) {
            try {
                retValue = Integer.parseInt(cacheExpiration);
            } catch ( NumberFormatException ne ) {
                logger.log( Level.WARNING, "PSPL_PCCCSPPCCD0009", portletName);
            }
        }
        return retValue;
        
    }

    /**
     * Returns the cache expiration scope .
     * if is not defined, "private" is returned.
     * Although this is added in portlet 2.0 but default value is 
     * valid even for portlet 1.0.
     * <P>
     * @return String text within <scope> element
     */
    public String getCachingScope() {
        return cacheScope;        
    }

    // Generates thq QName for the supported event element that can be either
    // supported-processing-event or supported-publishing-event
    private QName getQName(Element supportedEventElement, String defaultNameSpace) {
        // Since it can be either qname or name, first load qname, if qname is not present
        // load name and create qname with defaultnamespace
        QName qname = PortletXMLDocumentHelper.generateQName(PortletXMLDocumentHelper.getChildElement(supportedEventElement, PortletDescriptorConstants.QNAME), defaultNameSpace);
        if(qname == null){
            String name = PortletXMLDocumentHelper.getChildTextTrim(supportedEventElement, PortletDescriptorConstants.NAME);
            if(name != null){
                qname = new QName(defaultNameSpace, name);
            }
        }
        return qname;
    }
    
    /**
     * Sets the correct value of cache scope
     * Allowed values are "private" indicating that the content should not be shared
     * across users and "public" indicating that the content may be shared across users.
     * The default value if not present is "private".
     */
    private void setCachingScope(String scope){
       if(scope != null &&
           ((scope.equalsIgnoreCase(PortletDescriptorConstants.PRIVATE_CACHING_SCOPE))     
            || (scope.equalsIgnoreCase(PortletDescriptorConstants.PUBLIC_CACHING_SCOPE)))) {
            cacheScope = scope;
        }else{
            cacheScope = PortletDescriptorConstants.PRIVATE_CACHING_SCOPE;
        }
    }
    
    
    /**
     * Returns the portlet info descriptor.
     * <P>
     * @return <code>PortletInfoDescriptor</code>
     */
    public PortletInfoDescriptor getPortletInfoDescriptor() {
        return infoDescriptor;
    }
    
    /**
     * Returns the security role ref descriptors.
     * <P>
     * @return <code>List</code> of <code>SecurityRoleRefDescriptor</code>s.
     */
    public List<SecurityRoleRefDescriptor> getSecurityRoleRefDescriptors() {
        if(secRoleRefDescriptors == null) {
            return Collections.emptyList();
        }
        return secRoleRefDescriptors;
    }
    
    /**
     * Returns the name of the resource bundle. Clients
     * should call this method only if <code>getType</code>() returns
     * RESOURCE_TYPE_RB.
     * <P>
     * @return <code>String</code> of the resource bundle.
     */
    public String getResourceBundle() {
        return resourceBundle;
    }
    
    /**
     * Returns the container runtime option map.
     * <P>
     * @return <code>Map</code> of ContainerRuntimeOption.
     */
    public Map<String, String[]> getContainerRuntimeOptions() {
        if(containerRTOptions == null) {
            return Collections.emptyMap();
        }
        return containerRTOptions;
    }
    
    /**
     * Returns a particular runtime option
     * @param optionName option name 
     * @return option value
     */
    public String[] getContainerRuntimeOption(String optionName){
        return containerRTOptions.get(optionName);
    }    
    
    /**
     * Returns the supported processing event list.
     * <P>
     * @return <code>List</code> of supported processing event.
     */
    public List<QName> getSupportedProcessingEvents() {
        if(supportedProcessingEvents == null) {
            return Collections.emptyList();
        }
        return supportedProcessingEvents;
    }
    
    /**
     * Returns the supported publishing event list.
     * <P>
     * @return <code>List</code> of supported publishing event.
     */
    public List<QName> getSupportedPublishingEvents() {
        if(supportedPublishingEvents == null) {
            return Collections.emptyList();
        }
        return supportedPublishingEvents;
    }
    
    /**
     * Returns the supported public render parameters list.
     * <P>
     * @return <code>List</code> of supported public render parameters.
     */
    public List<String> getSupportedPublicRenderParameterIdentifiers() {
        if(supportedPublicRenderParameterIdentifiers == null) {
            return Collections.emptyList();
        }
        return this.supportedPublicRenderParameterIdentifiers;
    }
    
    /**
     * Returns portlet id.
     * <P>
     * @return <code>String</code> portlet id.
     */
    public String getId() {
        return id;
    }
            
    /**
     * Returns the portlet ID. The Portlet ID is a combination of
     * Portlet application name and portlet name.
     * <P>
     * @return the portlet ID.
     */
    public PortletID getPortletID() {
        return portletID;
    }

    /**
     * The toString method.
     * <P>
     * @return the <code>String</code> representation of the portlet
     * descriptor.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "PortletDescriptor ");
        
        sb.append( " portlet name [" );
        if ( portletName != null ) {
            sb.append( portletName );
        } else {
            sb.append( "NULL" );
        }
        sb.append("]");
        
        sb.append( " portlet class [" );
        if ( portletClassName != null ) {
            sb.append( portletClassName );
        } else {
            sb.append( "NULL" );
        }
        sb.append("]");
        
        sb.append( " portlet descriptions [" );
        Iterator iterator0 = descriptions.iterator();
        while ( iterator0.hasNext() ) {
            sb.append( (String)iterator0.next() );
        }
        sb.append( " ]" );
        
        sb.append( " portlet display names [" );
        Iterator iterator3 = displayNames.iterator();
        while ( iterator3.hasNext() ) {
            sb.append( (String)iterator3.next() );
        }
        sb.append( " ]" );
        
        sb.append( " portlet supported locales [" );
        Iterator iterator5 = supportedLocales.iterator();
        while ( iterator5.hasNext() ) {
            sb.append( (String)iterator5.next() );
        }
        sb.append( " ]" );
        
        Iterator iterator = initParamDescriptors.iterator();
        while ( iterator.hasNext() ) {
            InitParamDescriptor initParamDescriptor = (InitParamDescriptor)iterator.next();
            sb.append( initParamDescriptor.toString() );
            sb.append( "\n");
        }
        
        Iterator iterator1 = supportsDescriptors.iterator();
        while ( iterator1.hasNext() ) {
            SupportsDescriptor supportsDescriptor = (SupportsDescriptor)iterator1.next();
            sb.append( supportsDescriptor.toString() );
            sb.append( "\n");
        }
        
        if ( cacheExpiration != null ) {
            sb.append( cacheExpiration );
        }
        
        if ( infoDescriptor != null ) {
            sb.append( infoDescriptor.toString() );
        }
        
        if ( portletPreferencesDescriptor != null ) {
            sb.append( portletPreferencesDescriptor.toString() );
        }
        
        Iterator iterator2 = secRoleRefDescriptors.iterator();
        while ( iterator2.hasNext() ) {
            SecurityRoleRefDescriptor secRoleRefDescriptor = (SecurityRoleRefDescriptor)iterator2.next();
            sb.append( secRoleRefDescriptor.toString() );
            sb.append( "\n");
        }
        
        if (resourceBundle != null) {
            sb.append( " resource bundle [" );
            sb.append( resourceBundle );
            sb.append( "]" );
        }
        
        return (sb.toString());
    }
}
