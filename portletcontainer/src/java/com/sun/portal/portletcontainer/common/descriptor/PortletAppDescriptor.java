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

import com.sun.portal.container.PortletID;
import com.sun.portal.container.service.EventHolder;
import com.sun.portal.container.service.PublicRenderParameterHolder;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.portlet.PortletURLGenerationListener;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * The Portlet Application Descriptor is the main entry point of accessing 
 * the portlet deployment descriptor. 
 * <P>
 * The PortletAppDescriptor loads the portlet app descriptors into memory, 
 * it delegates the loading of sub descriptors to the individual descriptor 
 * classes. The load() method is used to load the portlet app descriptor.
 * Note that the load() method also loads the immediate children of the
 * portlet app descriptor' by calling their load() methods. 
 * <P>
 * The PortletAppDescriptor class provides member methods to get to its
 * sub descriptors, as well as portlet app name and description. 
 * The following get**() methods are available:
 * <UL>
 *   <LI><code>getPortletsDescriptor()</code>
 *   <LI><code>getPortletAppName()</code>
 *   <LI><code>getUserAttributeDescriptors()</code>
 *   <LI><code>getFilterDescriptors()</code>
 *   <LI><code>getFilterMappingDescriptors()</code>
 *   <LI><code>getPortletAppDescription()</code>
 *</UL>
 *
 */
public class PortletAppDescriptor {

    private List<String> descriptions;
    private List<String> displayNames;
    private PortletsDescriptor portletsDescriptor;
    private SecurityConstraintDescriptor secConstraintDescriptor;
    private List<UserAttributeDescriptor> userAttributeDescriptors;
    private List<FilterDescriptor> filterDescriptors;
    private List<FilterMappingDescriptor> filterMappingDescriptors;
    private List<EventDescriptor> eventDescriptors;
    private Map<QName, EventDescriptor> eventDescriptorMap;
    private Map<PortletID, List<EventHolder>> processingEventMap;
    private Map<PortletID, List<EventHolder>> publishingEventMap;
    private List<PublicRenderParameterDescriptor> publicRPDescriptors;
    private Map<PortletID, List<PublicRenderParameterHolder>> publicRenderParametersMap;
    private List<CustomPortletModeDecriptor> customPortletModeDecriptors;
    private List<CustomWindowStateDecriptor> customWindowStateDecriptors;
    private List<URLListenerDescriptor> urlListenerDescriptors;
    private Map<String, String[]> containerRuntimeOptions;    
    private String defaultNameSpace;
    private String version;
    private String id;
    private String portletAppName;
    private List<String> servletURLPatterns;
    
    public PortletAppDescriptor(String portletAppName) {
        this.portletAppName = portletAppName;
    }

    /**
     * Loads the portlet app descriptor into memory.
     * <P>
     * This class only loads the top level elements, it delegates the load 
     * action into sub descriptor classes.
     * <P>
     * @param root Element of the deployment descriptor document
     * @param namespaceURI the namespace URI for the portlet
     * @throws com.sun.portal.portletcontainer.common.descriptor.DeploymentDescriptorException 
     */
    public void load(Element root, String namespaceURI) throws DeploymentDescriptorException {

        //load portlet version
        if(root.hasAttribute(PortletDescriptorConstants.VERSION)){
            version = root.getAttribute(PortletDescriptorConstants.VERSION);
        }
        
        //load portlet app id
        id = PortletXMLDocumentHelper.getId(root);
        
        //Load custom portlet mode
		List customPortletModeElements = 
                PortletXMLDocumentHelper.getChildElements(root, PortletDescriptorConstants.CUSTOM_PORTLET_MODE);
        if(customPortletModeElements.size()>0){
            customPortletModeDecriptors = new ArrayList<CustomPortletModeDecriptor>(customPortletModeElements.size());
            for (int i = 0; i < customPortletModeElements.size(); i++) {
                Element cpmElement = (Element) customPortletModeElements.get(i);
                CustomPortletModeDecriptor cpmDisc = new CustomPortletModeDecriptor();
                cpmDisc.load(cpmElement, namespaceURI);
                customPortletModeDecriptors.add(cpmDisc);
            }//end of for
        }//end of if
        
        //Load custom-window-state
		List customWindowStateElements = 
            PortletXMLDocumentHelper.getChildElements(root, PortletDescriptorConstants.CUSTOM_WINDOW_STATE);
        if(customWindowStateElements.size()>0){
            customWindowStateDecriptors = new ArrayList<CustomWindowStateDecriptor>(customWindowStateElements.size());
            for (int i = 0; i < customWindowStateElements.size(); i++) {
                Element cwsElement = (Element) customWindowStateElements.get(i);
                CustomWindowStateDecriptor cwsDisc = new CustomWindowStateDecriptor();
                cwsDisc.load(cwsElement, namespaceURI);
                customWindowStateDecriptors.add(cwsDisc);
            }//end of for
        }//end of if
        
        
		List<Element> descriptionElements = PortletXMLDocumentHelper.getChildElements(root, PortletDescriptorConstants.DESCRIPTION);
        if(descriptionElements.size()>0){
            descriptions = new ArrayList<String>(descriptionElements.size());
            for (int i = 0; i < descriptionElements.size(); i++) {
                descriptions.add( PortletXMLDocumentHelper.getTextTrim(descriptionElements.get(i)) );
            }
        }

		List displayNameElements = PortletXMLDocumentHelper.getChildElements(root, PortletDescriptorConstants.DISPLAY_NAME);
        if(displayNameElements.size()>0){
            displayNames = new ArrayList<String>(displayNameElements.size());
            for (int i = 0; i < displayNameElements.size(); i++) {
                Element displayNameElement = (Element) displayNameElements.get(i);
                displayNames.add( PortletXMLDocumentHelper.getTextTrim(displayNameElement) );
            }
        }

        //Following element is available for ver > 1.0
        defaultNameSpace = PortletXMLDocumentHelper.getChildTextTrim(root, PortletDescriptorConstants.DEFAULT_NAMESPACE);
        if(defaultNameSpace==null){
            defaultNameSpace = XMLConstants.NULL_NS_URI;
        }
        
		portletsDescriptor = new PortletsDescriptor(portletAppName);
		portletsDescriptor.load(root, namespaceURI, defaultNameSpace);
 
		// loads user attribute descriptors
		NodeList userAttrElements = root.getElementsByTagName(PortletDescriptorConstants.USER_ATTRIBUTE);
        if(userAttrElements.getLength()>0){
            userAttributeDescriptors = new ArrayList<UserAttributeDescriptor>(userAttrElements.getLength());
            for (int i = 0; i < userAttrElements.getLength(); i++) {
                Element userAttrElement = (Element) userAttrElements.item(i);
                UserAttributeDescriptor userAttrDescriptor = new UserAttributeDescriptor();
                userAttrDescriptor.load( userAttrElement, namespaceURI );
                userAttributeDescriptors.add( userAttrDescriptor );
            }
        }
        
		// loads security-constraint
		Element secConstraintElement = PortletXMLDocumentHelper.getChildElement(root, PortletDescriptorConstants.SECURITY_CONSTRAINT);
		if (secConstraintElement != null) {
            secConstraintDescriptor = new SecurityConstraintDescriptor();
            secConstraintDescriptor.load( secConstraintElement, namespaceURI );
        }
        
        //Following elements are available for ver > 1.0
        ////////////////////////////////////////////
        // loads filter & filterMapping descriptors
        ///////////////////////////////////////////
        NodeList filterElements = root.getElementsByTagName(PortletDescriptorConstants.FILTER);
        NodeList filterMappingElements = root.getElementsByTagName(PortletDescriptorConstants.FILTER_MAPPING);
        //Lazy initialization of filterDescriptors and filterMappingDescriptors
        //initialization is done only when there is at least one filter declaration.
        if(filterElements.getLength()>0 & filterMappingElements.getLength()>0){
            filterDescriptors = new ArrayList<FilterDescriptor>(filterElements.getLength());
            filterMappingDescriptors = new ArrayList<FilterMappingDescriptor>(filterMappingElements.getLength());
            for(int i=0; i<filterElements.getLength(); i++){
                Element filterElement = (Element) filterElements.item(i);
                FilterDescriptor filterDescriptor = new FilterDescriptor();
                filterDescriptor.load(filterElement, namespaceURI);
                filterDescriptors.add(filterDescriptor);
            }
            
            //Load filter mapping now
            for(int i=0; i<filterMappingElements.getLength(); i++){
                Element filterMappingElement = (Element) filterMappingElements.item(i);
                FilterMappingDescriptor filterMappingDescriptor = new FilterMappingDescriptor();
                filterMappingDescriptor.load(filterMappingElement, namespaceURI);
                filterMappingDescriptors.add(filterMappingDescriptor);
            }//end of for            
        }//end of if

        // load default event namespace
        
        // loads event-definition descriptors
        NodeList eventElements = root.getElementsByTagName(PortletDescriptorConstants.EVENT_DEFINITION);
        //Lazy initialization of eventDescriptors
        //initialization is done only when there is at least one event declaration.
        if(eventElements.getLength()>0){
            eventDescriptors = new ArrayList<EventDescriptor>(eventElements.getLength());
            eventDescriptorMap = new HashMap<QName, EventDescriptor>(eventElements.getLength());
            for(int i=0; i<eventElements.getLength(); i++){
                Element eventElement = (Element) eventElements.item(i);
                EventDescriptor eventDescriptor = new EventDescriptor();
                eventDescriptor.load(eventElement, namespaceURI, defaultNameSpace);
                eventDescriptors.add(eventDescriptor);
                if(eventDescriptor.getQName() != null) {
                    eventDescriptorMap.put(eventDescriptor.getQName(), eventDescriptor);
                }
            }
            // Using event definitions and event qnames, create a Map that has portlet ID and list of event holders
            processingEventMap = getEventMap(getPortletsDescriptor().getPortletProcessingEvents());
            publishingEventMap = getEventMap(getPortletsDescriptor().getPortletPublishingEvents());
        }
        
        // loads public-render-parameter descriptors
        NodeList publicRPElements = root.getElementsByTagName(PortletDescriptorConstants.PUBLIC_RENDER_PARAMETER);
        //Lazy initialization of publicRPDescriptors
        //initialization is done only when there is at least one public-render-parameter declaration.
        if(publicRPElements.getLength()>0){
            publicRPDescriptors = new ArrayList<PublicRenderParameterDescriptor>(publicRPElements.getLength());
            for(int i=0; i<publicRPElements.getLength(); i++){
                Element publicRPElement = (Element) publicRPElements.item(i);
                PublicRenderParameterDescriptor publicRPDescriptor = 
                        new PublicRenderParameterDescriptor();
                publicRPDescriptor.load(publicRPElement, namespaceURI, defaultNameSpace);
                publicRPDescriptors.add(publicRPDescriptor);
            } 
            
            // Using public render parameter definitions and public render parameters identifiers
            // create a Map that has portlet ID and list of public render parameter holders
            Map<PortletID, List<String>> publicRenderParametersIdentifiers = getPortletsDescriptor().getPortletPublicRenderParameterIdentifiers();
            publicRenderParametersMap = new HashMap<PortletID, List<PublicRenderParameterHolder>>(publicRenderParametersIdentifiers.size());
            Set<Map.Entry<PortletID, List<String>>> entries = publicRenderParametersIdentifiers.entrySet();
            for(Map.Entry<PortletID, List<String>> mapEntry : entries) {
                List<String> identifiers = mapEntry.getValue();
                List<PublicRenderParameterHolder> publicRenderParameterHolders = new ArrayList<PublicRenderParameterHolder>(identifiers.size());
                for(PublicRenderParameterDescriptor publicRPDescriptor: publicRPDescriptors) {
                    if(identifiers.contains(publicRPDescriptor.getIdentifier())) {
                        publicRenderParameterHolders.add(publicRPDescriptor.getPublicRenderParameterHolder());
                    }
                }
                publicRenderParametersMap.put(mapEntry.getKey(), publicRenderParameterHolders);
            }
        }

        // loads url listener descriptors
        NodeList listenerElements = root.getElementsByTagName(PortletDescriptorConstants.URL_LISTENER);
        //Lazy initialization of listener descriptors
        //initialization is done only when there is at least one listener declaration.
        if(listenerElements.getLength()>0){
            urlListenerDescriptors = new ArrayList<URLListenerDescriptor>(listenerElements.getLength());
            for(int i=0; i<listenerElements.getLength(); i++){
                Element listenerElement = (Element) listenerElements.item(i);
                URLListenerDescriptor urlListenerDescriptor = new URLListenerDescriptor();
                urlListenerDescriptor.load(listenerElement, namespaceURI);
                urlListenerDescriptors.add(urlListenerDescriptor);
            }
        }
        
        // loads container-runtime-option descriptors
        containerRuntimeOptions = PortletXMLDocumentHelper.getRuntimeOptions(root);
        
    }

    /**
     * Returns the name of the portlet application.
     * <P>
     * @return name of the portlet application.
     */
    public String getName() {
        return this.portletAppName;
    }

    /**
     * Returns the PortletsDescriptor.
     * <P>
     * @return PortletsDescriptor
     */
    public PortletsDescriptor getPortletsDescriptor() {
        return portletsDescriptor;
    }

    /**
     * Returns the UserAttributeDescriptors.
     * <P>
     * @return <code>List</code> of UserAttributeDescriptors.
     */
    public List<UserAttributeDescriptor> getUserAttributeDescriptors() {
        if(userAttributeDescriptors == null){
            return Collections.emptyList();
        }
        return userAttributeDescriptors;
    }

    /**
     * Returns the filterDescriptors.
     * <P>
     * @return <code>List</code> of FilterDescriptor.
     */
    public List<FilterDescriptor> getFilterDescriptors() {
        if(filterDescriptors == null){
            return Collections.emptyList();
        }
        return filterDescriptors;
    }
    
    /**
     * Returns the filterMappingDescriptors.
     * <P>
     * @return <code>List</code> of FilterMappingDescriptor.
     */
    public List<FilterMappingDescriptor> getFilterMappingDescriptors() {
        if(filterMappingDescriptors == null){
            return Collections.emptyList();
        }
        return filterMappingDescriptors;
    }
    
    /**
     * Returns the eventDescriptors.
     * <P>
     * @return <code>List</code> of EventDescriptor.
     */
    public List<EventDescriptor> getEventDescriptors() {
        if(eventDescriptors == null){
            return Collections.emptyList();
        }
        return eventDescriptors;
    }
  
    /**
     * Returns the eventDescriptor for a QName.
     * <P>
     * @return the EventDescriptor for a QName.
     */
    public EventDescriptor getEventDescriptor(QName qName) {
        if(eventDescriptorMap == null || qName == null){
            return null;
        }
        return eventDescriptorMap.get(qName);
    }
  
    /**
     * Returns the Map of the events that will be supported for processing
     * This checks whether supported-event-processing is present in the
     * event-definition, if yes includes it, else ignores it.
     * The key is the portlet Id and the value is the List of the event holders that contains
     * QNames, aliases and value type
     * <P>
     * @return Map of events that will be supported for processing
     */
    public Map<PortletID, List<EventHolder>> getProcessingEvents() {
        if(processingEventMap == null) {
            return Collections.emptyMap();
        }
        return processingEventMap;
    }
    
    /**
     * Returns the Map of the events that will be supported for processing.
     * This includes all those declared as supported-event-processing.
     * The key is the portlet Id and the value is the List of the event holders that contains
     * QNames, aliases and value type
     * <P>
     * @return Map of events that will be supported for processing
     */
    public Map<PortletID, List<QName>> getSupportedProcessingEvents() {
        return getPortletsDescriptor().getPortletProcessingEvents();
    }
    
    /**
     * Returns the Map of the events that will be supported for processing
     * This checks whether supported-event-publishing is present in the
     * event-definition, if yes includes it, else ignores it.
     * The key is the portlet Id and the value is the List of the event holders that contains
     * QNames, aliases and value type
     * <P>
     * @return Map of events that will be supported for processing
     */
    public Map<PortletID, List<EventHolder>> getPublishingEvents() {
        if(publishingEventMap == null) {
            return Collections.emptyMap();
        }
        return publishingEventMap;
    }
    
    /**
     * Returns the publicRenderParameterDescriptors.
     * <P>
     * @return <code>List</code> of PublicRenderParameterDescriptor.
     */
    public List<PublicRenderParameterDescriptor> getPublicRenderParameterDescriptors() {
        if(publicRPDescriptors == null){
            return Collections.emptyList();
        }
        return publicRPDescriptors;
    }
    
    /**
     * Returns the Map of the public render parameters that will be supported
     * The key is the portlet Id and the value is the List of the public render parameters holders that contains
     * identifier and QNames
     * <P>
     * @return Map of public render parameters that will be supported
     */
    public Map<PortletID, List<PublicRenderParameterHolder>> getPublicRenderParameters() {
        if(publicRenderParametersMap == null) {
            return Collections.emptyMap();
        }
        return publicRenderParametersMap;
    }
    
    /**
     * Returns the public render parameter identifier associated with public render parameter qname
     * <P>
     * @param publicRenderParameterQName the QName of the public render parameter
     *
     * @return the public render parameter identifier associated with public render parameter qname
     */
    public String getPublicRenderParameterIdentifier(QName publicRenderParameterQName) {
        List<PublicRenderParameterDescriptor> publicRenderParameterDescriptors = 
                getPublicRenderParameterDescriptors();
        for(PublicRenderParameterDescriptor 
                publicRenderParameterDescriptor : publicRenderParameterDescriptors) {
              if(publicRenderParameterDescriptor.getQName().equals(publicRenderParameterQName)) {
                  return publicRenderParameterDescriptor.getIdentifier();
              }
        }
        return null;
    }
    
    /**
     * Returns the public render parameter qname associated with public render parameter identifier
     * <P>
     * @param publicRenderParameterIdentifier the public render parameter identifier
     *
     * @return the public render parameter qname associated with public render parameter identifier
     */
    public QName getPublicRenderParameterQName(String publicRenderParameterIdentifier) {
        List<PublicRenderParameterDescriptor> publicRenderParameterDescriptors = 
                getPublicRenderParameterDescriptors();
        for(PublicRenderParameterDescriptor 
                publicRenderParameterDescriptor : publicRenderParameterDescriptors) {
              if(publicRenderParameterDescriptor.getIdentifier().equals(publicRenderParameterIdentifier)) {
                  return publicRenderParameterDescriptor.getQName();
              }
        }
        return null;
    }
    
    /**
     * Returns the app description as a <code>String</code>. If there's more
     * than one descriptions are defined, returns the first one. 
     * <P>
     * @return <code>String</code> of the description. The return
     * value could be null if no description is defined.
     */
    public String getPortletAppDescription() {
        String description = null;
        if ( !descriptions.isEmpty() ) {
            description = (String)descriptions.get(0);
        } 
        return description;
    }

    /**
     * Returns the portlet app descriptions.
     * <P>
     * @return <code>List</code> of the <code>String</code>s. The returned value could 
     * be empty list if description is not defined.
     */
    public List<String> getPortletAppDescriptions() {
        if(descriptions == null) {
            return Collections.emptyList();
        }
        return descriptions;
    }

    /**
     * Returns the portlet app display names.
     * <P>
     * @return <code>List</code> of <code>String</code>s. The returned value could 
     * be empty list if display name is not defined.
     */
    public List<String> getPortletAppDisplayNames() {
        if(displayNames == null) {
            return Collections.emptyList();
        }
        return displayNames;
    }

    /**
     * Returns the SecurityConstrintDescriptor.
     * <P>
     * @return SecurityConstraintDescriptor
     */
    public SecurityConstraintDescriptor getSecurityConstraintDescriptor() {
        return secConstraintDescriptor;
    }

    /**
     * Returns the container runtime option map.
     * <P>
     * @return <code>Map</code> of ContainerRuntimeOption.
     */
    private Map<String, String[]> getContainerRuntimeOptions() {
        if(containerRuntimeOptions == null){
            return Collections.emptyMap();
        }
        return containerRuntimeOptions;
    }
    
    /**
     * Returns the URL Listener Descriptor. 
     * <P>
     * @return <code>List</code> of URL Listener Descriptor.
     */
    public List<URLListenerDescriptor> getURLListenerDescriptors() {
        if(this.urlListenerDescriptors == null) {
            return Collections.emptyList();
        }
        return this.urlListenerDescriptors;
    }
    
    /**
     * Returns the PortletURLGenerationListeners. Each listener implements 
     * the PortletURLGenerationListener interface.
     * <P>
     * @return <code>List</code> of URL Generation Listeners.
     */
    public List<PortletURLGenerationListener> getURLGenerationListeners() {
        List<PortletURLGenerationListener> portletURLGenerationListeners = new ArrayList();
        if(this.urlListenerDescriptors != null) {
            for(URLListenerDescriptor urlListenerDescriptor: urlListenerDescriptors) {
                portletURLGenerationListeners.add(urlListenerDescriptor.getListener());
            }
        }
        return portletURLGenerationListeners;
    }
    
    /**
     * Returns the container runtime options for a Portlet ID
     *
     * @param portletID the Portlet ID
     *
     * @return <code>Map</code> of ContainerRuntimeOptions for a Portlet ID
     */
    public Map<String, String[]> getContainerRuntimeOptions(PortletID portletID){
        PortletDescriptor portletDescriptor = this.getPortletsDescriptor().getPortletDescriptor(portletID.getPortletName());
        Map<String, String[]> portletRuntimeOptions = portletDescriptor.getContainerRuntimeOptions();
        Map<String, String[]> runtimeOptions = getContainerRuntimeOptions();
        if(portletRuntimeOptions != null) {
            runtimeOptions.putAll(portletRuntimeOptions);
        }
        return runtimeOptions;
    }    

    /**
     * Returns a particular runtime option for a portletID
     * @param portletID the Portlet ID
     * @param optionName option name 
     *
     * @return option the value of container runtime option for a portletID
     */
    public String[] getContainerRuntimeOption(PortletID portletID, String optionName){
        PortletDescriptor portletDescriptor = this.getPortletsDescriptor().getPortletDescriptor(portletID.getPortletName());
        String[] portletRuntimeOption = portletDescriptor.getContainerRuntimeOption(optionName);
        if(portletRuntimeOption == null) {
            portletRuntimeOption = containerRuntimeOptions.get(optionName);
        }
        return portletRuntimeOption;
    }    
    

    public String getDefaultNamespace(){
        return defaultNameSpace;
    }
    
    protected void setURLPatterns(List<String> servletURLPatterns) {
        this.servletURLPatterns = servletURLPatterns;
    }
    
    public List<String> getServletURLPatterns() {
        return servletURLPatterns;
    }
    
    /**
     * The toString method.
     * <P>
     * @return the <code>String</code> representation of the deployment
     * descriptor.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "PortletAppDescriptor [");

	sb.append( " portlet app descriptions [" );
	Iterator iterator0 = descriptions.iterator();
	while ( iterator0.hasNext() ) {
	    sb.append( (String)iterator0.next() );
	}
	sb.append( " ]" );

	sb.append( " portlet app display names [" );
	Iterator iterator2 = displayNames.iterator();
	while ( iterator2.hasNext() ) {
	    sb.append( (String)iterator2.next() );
	}
	sb.append( " ]" );


	sb.append( getPortletsDescriptor().toString() );

	Iterator iterator1 = userAttributeDescriptors.iterator();
	while ( iterator1.hasNext() ) {
	    UserAttributeDescriptor userAttrDescriptor = (UserAttributeDescriptor)iterator1.next();
	    sb.append( userAttrDescriptor.toString() );
	    sb.append( "\n");
	}

        sb.append( "]" );
		sb.append( " security constraint description [" );
        if ( secConstraintDescriptor != null ) {
            sb.append( secConstraintDescriptor.toString() );
        }
        
        sb.append( "]" );

	return sb.toString();
    }

    private Map<PortletID, List<EventHolder>> getEventMap(Map<PortletID, List<QName>> portletEvents) {
        Map eventMap = new HashMap<PortletID, List<EventHolder>>(portletEvents.size());
        Set<Map.Entry<PortletID, List<QName>>> entries = portletEvents.entrySet();
        for(Map.Entry<PortletID, List<QName>> mapEntry : entries) {
            List<QName> publishingProcessingEvents = mapEntry.getValue();
            List<EventHolder> eventHolders = new ArrayList<EventHolder>(publishingProcessingEvents.size());
            for(EventDescriptor eventDescriptor : eventDescriptors) {
                if(doesEventDefinitionContainSupportedEvent(eventDescriptor.getQName(), 
                        publishingProcessingEvents)) {
                    eventHolders.add(eventDescriptor.getEventHolder());
                }
            }
            eventMap.put(mapEntry.getKey(), eventHolders);
        }
        return eventMap;
    }
    
    /*
     The local part of the supportedEvents may end with be a wildcard, 
     a "." character to indicate the portlet is willing to process or publish any event
     whose name starts with the characters before the "." character.
     If it ends with "..", it will match all the events in the hierarchy.
     Eg: if the event-definitions are (1) a.b.c (2)a.b.d (3)a.m.n (4)a.b (5)a.c
     If the supported event is 
     (I)a. -> it matches (4) & (5)
     (II)a.. -> it matches (1), (2), (3), (4) & (5)
     (III)a.b. -> it matches (1) & (2)
     (IV)a.b.. -> it matches (1) & (2)
     (V)a.m. -> it matches (3)
    */
    private boolean doesEventDefinitionContainSupportedEvent(QName eventDescriptorQName, List<QName> supportedEvents) {
        String eventDefinitionName = eventDescriptorQName.getLocalPart();
        // Event Definition should not end in "." character
        if(eventDefinitionName.endsWith(".")) {
            return false;
        }
        for(QName supportedEvent : supportedEvents) {
            String supportedEventName = supportedEvent.getLocalPart();
            if(eventDescriptorQName.getNamespaceURI().equals(supportedEvent.getNamespaceURI())) {
                if (supportedEventName.endsWith("..") && eventDefinitionName.indexOf(".") != -1) {
                    int index = supportedEventName.indexOf("..");
                    String partialEventName = supportedEventName.substring(0, index);
                    if (eventDefinitionName.startsWith(partialEventName)) {
                        return true;
                    }
                } else if (supportedEventName.endsWith(".") && eventDefinitionName.indexOf(".") != -1) {
                    // Match the event names
                    StringTokenizer tokens = new StringTokenizer(supportedEventName, ".");
                    StringTokenizer tokens1 = new StringTokenizer(eventDefinitionName, ".");
                    if (tokens1.countTokens() == tokens.countTokens() + 1) {
                        int index = supportedEventName.lastIndexOf(".");
                        String partialEventName = supportedEventName.substring(0, index);
                        if (eventDefinitionName.startsWith(partialEventName)) {
                            return true;
                        }
                    }

                } else if (eventDefinitionName.equals(supportedEventName)) {
                        return true;
                }
            }
        }
        return false;
    }

    public String getVersion() {
        return version;
    }
}
