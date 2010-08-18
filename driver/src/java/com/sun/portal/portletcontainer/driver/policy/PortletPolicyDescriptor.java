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

package com.sun.portal.portletcontainer.driver.policy;

import com.sun.portal.container.service.policy.DistributionType;
import com.sun.portal.container.service.policy.EventPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * The Portlet Policy Descriptor is the main entry point of accessing
 * the portlet policy descriptor.
 * <P>
 * The PortletPolicyDescriptor loads the portlet policy descriptor into memory
 * and provides methods to access the various information present in the
 * portlet policy xml file.
 *
 */
public class PortletPolicyDescriptor {
    
    private DistributionType eventDistribution;
    private int maxEventGeneration;
    private DistributionType containerEventDistribution;
    private int containerMaxEventGeneration;
    private Map<String, Boolean> containerEventStatus;
    private DistributionType publicRenderParameterPolicyDistribution;
    private String version;

    public PortletPolicyDescriptor() {
    }
    
    /**
     * Loads the portlet policy descriptor into memory.
     * <P>
     *
     * @param root  the portlet policy element
     * @param namespaceURI the namespace of portlet policy element
     * @throws com.sun.portal.portletcontainer.driver.policy.PortletPolicyException if unable to parse the xml file.
     */
    public void load(Element root, String namespaceURI) throws PortletPolicyException {
        //load portlet version
        if(root.hasAttribute(PortletPolicyConstants.VERSION)){
            this.version = root.getAttribute(PortletPolicyConstants.VERSION);
        }
        
        //Load event policy element
        Element eventPolicyElement = PortletPolicyXMLHelper.getChildElement(root, PortletPolicyConstants.EVENT_POLICY);
        if (eventPolicyElement != null) {
            String distributionValue = PortletPolicyXMLHelper.getChildTextTrim(eventPolicyElement, PortletPolicyConstants.EVENT_DISTRIBUTION);
            this.eventDistribution = getDistributionType(distributionValue);
            String maxEventGenerationValue = PortletPolicyXMLHelper.getChildTextTrim(eventPolicyElement, PortletPolicyConstants.MAX_EVENT_GENERATION);
            this.maxEventGeneration = getMaxEventGeneration(maxEventGenerationValue);
        }
        
        //Load container event policy element
        Element containerEventPolicyElement = PortletPolicyXMLHelper.getChildElement(root, PortletPolicyConstants.CONTAINER_EVENT_POLICY);
        containerEventStatus = new HashMap<String, Boolean>();
        if (containerEventPolicyElement != null) {
            String distributionValue = PortletPolicyXMLHelper.getChildTextTrim(containerEventPolicyElement, PortletPolicyConstants.EVENT_DISTRIBUTION);
            this.containerEventDistribution = getDistributionType(distributionValue);
            String maxEventGenerationValue = PortletPolicyXMLHelper.getChildTextTrim(eventPolicyElement, PortletPolicyConstants.MAX_EVENT_GENERATION);
            this.containerMaxEventGeneration = getMaxEventGeneration(maxEventGenerationValue);
            List<Element> eventElements = PortletPolicyXMLHelper.getChildElements(containerEventPolicyElement, PortletPolicyConstants.EVENT);
            for(Element element : eventElements) {
                String name = PortletPolicyXMLHelper.getChildTextTrim(element, PortletPolicyConstants.NAME);
                String status = PortletPolicyXMLHelper.getChildTextTrim(element, PortletPolicyConstants.STATUS);
                if(name != null) {
                    if(PortletPolicyConstants.ENABLED.equals(status)) {
                        containerEventStatus.put(name, Boolean.TRUE);
                    } else {
                        containerEventStatus.put(name, Boolean.FALSE);
                    }
                }
            }
        }

        //Load public render parameter policy element
        Element publicRenderParameterPolicyElement = PortletPolicyXMLHelper.getChildElement(root, PortletPolicyConstants.PUBLIC_RENDER_PARAMETER_POLICY);
        if (publicRenderParameterPolicyElement != null) {
            String distributionValue = PortletPolicyXMLHelper.getChildTextTrim(publicRenderParameterPolicyElement, PortletPolicyConstants.PUBLIC_RENDER_PARAMETER_DISTRIBUTION);
            this.publicRenderParameterPolicyDistribution = getDistributionType(distributionValue);
        }
    }
    
    public DistributionType getEventDistribution() {
        return eventDistribution;
    }
    
    public int getMaxEventGeneration() {
        return maxEventGeneration;
    }
    
    public DistributionType getContainerEventDistribution() {
        return containerEventDistribution;
    }
    
    public int getContainerMaxEventGeneration() {
        return containerMaxEventGeneration;
    }

    public Boolean getContainerEventStatus(String eventName) {
        return this.containerEventStatus.get(eventName);
    }

    public DistributionType getPublicRenderParameterPolicyDistribution() {
        return publicRenderParameterPolicyDistribution;
    }
    
    /**
     * The toString method.
     * <P>
     * @return the <code>String</code> representation of the descriptor.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( "PortletPolicyDescriptor [");
        
        sb.append( " EventPolicy = " );
        sb.append( eventDistribution );
        
        sb.append( " , MaxEventGeneration = " );
        sb.append( maxEventGeneration );
        
        sb.append( " ContainerEventPolicy = " );
        sb.append( containerEventDistribution );
        
        sb.append( " , ContainerMaxEventGeneration = " );
        sb.append( containerMaxEventGeneration );
        
        sb.append( " , ContainerEventStatus = " );
        sb.append( containerEventStatus );
        
        sb.append( " , PublicRenderParameterPolicy = " );
        sb.append( publicRenderParameterPolicyDistribution );
        
        sb.append( " ]");
        return sb.toString();
    }

    private DistributionType getDistributionType(String distributionValue) {
        DistributionType distributionType;
        if(DistributionType.ALL_PORTLETS.toString().equals(distributionValue)) {
            distributionType = DistributionType.ALL_PORTLETS;

        } else if(DistributionType.ALL_PORTLETS_ON_PAGE.toString().equals(distributionValue) ){
            distributionType = DistributionType.ALL_PORTLETS_ON_PAGE;
            
        } else if(DistributionType.VISIBLE_PORTLETS_ON_PAGE.toString().equals(distributionValue)) {
            distributionType = DistributionType.VISIBLE_PORTLETS_ON_PAGE;
            
        } else {
            distributionType = DistributionType.ALL_PORTLETS_ON_PAGE;
        }
        return distributionType;
    }

    private int getMaxEventGeneration(String maxEventGenerationValue) {
        if(maxEventGenerationValue == null) {
            return EventPolicy.DEFAULT_MAX_EVENT_GENERATION;
        }
        int maxEvent;
        try {
            maxEvent = Integer.parseInt(maxEventGenerationValue);
        } catch(NumberFormatException nfe) {
            maxEvent = EventPolicy.DEFAULT_MAX_EVENT_GENERATION;
        }
        return maxEvent;
    }
    
}
