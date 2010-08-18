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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



/**
 * The Portlets Descriptor is the entry point of accessing portlet
 * descriptors.
 * <P>
 * The PortletsDescriptor loads all the portlet descriptors into memory,
 * it delegates the loading of each portlet descriptor to the
 * <code>PortletDescriptor</code> classe.
 * <P>
 * The PortletsDescriptor class provides member methods to get to all
 * portlet descriptors, the portlet names, as well as to a specified portlet
 * description. The following get**() methods are available:
 * <UL>
 *   <LI><code>getPortletDescriptors()</code>
 *   <LI><code>getPortletNames()</code>
 *   <LI><code>getPortletDescriptor()</code>
 *</UL>
 *
 */
public class PortletsDescriptor {
    
    // Global variables
    private String portletAppName;
    private List<PortletDescriptor> portletDescriptors;
    private List<String> portletNames;
    private List<PortletID> portletIDs;
    
    // Used to get the portlets processing/publishing
    private Map<PortletID, List<QName>> processingEvents;
    private Map<PortletID, List<QName>> publishingEvents;
    
    // Used to get the portlets supporting public render parameters
    private Map<PortletID, List<String>> publicRenderParameterIdentifiers;
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PortletsDescriptor.class, "PCCLogMessages");
    
    public PortletsDescriptor(String portletAppName) {
        this.portletAppName = portletAppName;
    }
    
    /**
     * Loads the portlets descriptor into memory.
     * <P>
     * This class only loads the top level elements, it delegates the load
     * action into portlet descriptor class.
     * <P>
     * @param The root Element of the deployment descriptor document
     */
    public void load(Element element, String namespaceURI, String defaultEventNameSpace) throws
            DeploymentDescriptorException {
        NodeList portletElements = element.getElementsByTagName(PortletDescriptorConstants.PORTLET);
        if ( portletElements.getLength() == 0 ) {
            logger.warning("PSPL_PCCCSPPCCD0010");
        } else{
            portletDescriptors = new ArrayList<PortletDescriptor>(portletElements.getLength());
            portletNames = new ArrayList<String>(portletElements.getLength());
            portletIDs = new ArrayList<PortletID>(portletElements.getLength());
            processingEvents = new LinkedHashMap<PortletID, List<QName>>();
            publishingEvents = new LinkedHashMap<PortletID, List<QName>>();
            publicRenderParameterIdentifiers = new LinkedHashMap<PortletID, List<String>>();
        }
        
        for (int i = 0; i < portletElements.getLength(); i++) {
            Element portletElement = (Element)portletElements.item(i);
            PortletDescriptor portletDescriptor = new PortletDescriptor(this.portletAppName);
            portletDescriptor.load( portletElement, namespaceURI, defaultEventNameSpace );
            portletDescriptors.add( portletDescriptor );
            portletNames.add( portletDescriptor.getPortletName() );
            PortletID portletID = new PortletID(this.portletAppName, portletDescriptor.getPortletName());
            portletIDs.add( portletID );
            if(portletDescriptor.getSupportedProcessingEvents() != null 
                    && !portletDescriptor.getSupportedProcessingEvents().isEmpty()){
                processingEvents.put(portletID, portletDescriptor.getSupportedProcessingEvents());
            }
            if(portletDescriptor.getSupportedPublishingEvents() != null
                    && !portletDescriptor.getSupportedPublishingEvents().isEmpty()){
                publishingEvents.put(portletID, portletDescriptor.getSupportedPublishingEvents());
            }
            if(portletDescriptor.getSupportedPublicRenderParameterIdentifiers() != null 
                    && !portletDescriptor.getSupportedPublicRenderParameterIdentifiers().isEmpty()){
                publicRenderParameterIdentifiers.put(portletID, portletDescriptor.getSupportedPublicRenderParameterIdentifiers());
            }
        }
    }
    
    /**
     * Returns the portlet names.
     * <P>
     * @return A List of portlet names
     */
    public List<String> getPortletNames() {
        return portletNames;
    }
    
    /**
     * Returns the portlet IDs. The Portlet ID is a combination of
     * Portlet application name and portlet name.
     * <P>
     * @return A List of portlet Ids
     */
    public List<PortletID> getPortletIDs() {
        return portletIDs;
    }
    
    /**
     * Returns the Map of the events that will be processed.
     * The key is the portlet ID and the value is the List of the event names
     * that it supports processing
     * <P>
     * @return Map of events that will be processed.
     */
    protected Map<PortletID, List<QName>> getPortletProcessingEvents() {
        return this.processingEvents;
    }
    
    /**
     * Returns the Map of the events that will be published.
     * The key is the portlet ID and the value is the List of the event names
     * that it supports publishing
     * <P>
     * @return Map of events that will be published.
     */
    protected Map<PortletID, List<QName>> getPortletPublishingEvents() {
        return this.publishingEvents;
    }
    
    /**
     * Returns the Map of the public render parameters that will be supported
     * The key is the portlet ID and the value is the List of the public render parameter identifiers
     * <P>
     * @return Map of public render parameters that will be supported
     */
    public Map<PortletID, List<String>> getPortletPublicRenderParameterIdentifiers() {
        return this.publicRenderParameterIdentifiers;
    }
    
    /**
     * Returns the portlet descriptors.
     * <P>
     * @return A List of <code>PortletDescriptor</code>s
     */
    public List<PortletDescriptor> getPortletDescriptors() {
        return portletDescriptors;
    }
    
    /**
     * Returns the specified portlet descriptor.
     * <P>
     * @param portletName The portlet name
     * @return A <code>PortletDescriptor</code>. The returned value could be
     * null if the specified portlet descriptor is not found.
     */
    public PortletDescriptor getPortletDescriptor( String portletName ) {
        PortletDescriptor portlet = null;
        boolean stop = false;
        Iterator iterator = portletDescriptors.iterator();
        while ( iterator.hasNext() && !stop ) {
            PortletDescriptor portletDescriptor = (PortletDescriptor)iterator.next();
            if (portletDescriptor.getPortletName().equals( portletName ) ) {
                portlet = portletDescriptor;
                stop = true;
            }
        }
        
        return portlet;
    }
    
    /**
     * The toString method.
     * <P>
     * @return the <code>String</code> representation of the portlets
     * descriptor.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "PortletsDescriptor [");
        
        Iterator iterator = portletDescriptors.iterator();
        while ( iterator.hasNext() ) {
            PortletDescriptor portletDescriptor = (PortletDescriptor)iterator.next();
            sb.append( portletDescriptor.toString() );
            sb.append( "\n");
        }
        sb.append("]");
        sb.append( "\n");
        return sb.toString();
    }
}
