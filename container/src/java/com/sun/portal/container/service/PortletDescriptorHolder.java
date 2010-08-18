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

package com.sun.portal.container.service;

import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletID;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * The <code>PortletDescriptorHolder</code> class holds information about portlet descriptor of various
 * portlets. Its provides convenience methods to access various information.
 *  The default implementation reads it from the portlet.xml during the initialization of
 * the PortletAppEngineServlet.
 *
 */
public interface PortletDescriptorHolder {
    
    /**
     * Loads the Portlet Deployment descriptor and obtains the Event and
     * Public Render Parameter specific information from it.
     *
     * @param descriptor The Portlet Application Descriptor
     */
    public void load(Object descriptor);
    
    /**
     * Removes the  Event and Public Render Parameter specific information
     * obtained from the Portlet Deployment descriptor.
     *
     * @param descriptor The Portlet Application Descriptor
     */
    public void remove(Object descriptor);
    
    /**
     * Sets the event definition for the portlet application.
     * This data is not persisted. If any error occurs while adding
     * the event definition, the exception is thrown.
     * 
     * @param portletAppName the name of the portlet application
     * @param eventHolder the EventHolder that represents the event
     * 
     * @throws com.sun.portal.container.service.PublicDescriptorHolderException if any error occurs while adding
     * the event definition 
     */
    public void setEventHolder(String portletAppName, EventHolder eventHolder) 
            throws PublicDescriptorHolderException;
    
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
     * @return EventHolder, if the event is specified as supported-publishing-event in the descriptor
     *                     or as an alias in the descriptor.
     */
    public EventHolder verifySupportedPublishingEvent(EntityID portletEntityId, EventHolder eventHolder);
    
    /**
     * Enables the portlet to publish the event.
     * This data is not persisted. If any error occurs while adding
     * the supported publishing event definition, the exception is thrown.
     * 
     * @param portletEntityId the entity id of the portlet
     * @param eventHolder the EventHolder that represents the event
     * 
     * @throws com.sun.portal.container.service.PublicDescriptorHolderException if any error occurs while adding
     * the supported publishing event definition
     */
    public void setSupportedPublishingEvent(EntityID portletEntityId, 
            EventHolder eventHolder)
            throws PublicDescriptorHolderException;
    
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
     * Returns a list of PortletID that will be publishing the event.
     *
     * @param eventQname the qname of the event
     *
     * @return list of portlet Ids that will be publishing the event
     */
    public List<PortletID> getEventPublishingPortlets(QName eventQname);

    /**
     * Returns a Map of all the event publishing portlets with the event qname
	 * as the key and list of PortletID that will be publishing that event.
	 *
     * @return Map of all event publishing portlets.
     */
    public Map<QName, List<PortletID>> getAllEventPublishingPortlets();

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
     * @return eventQName, if the event is specified as supported-processing-event in the descriptor
     *                     or as an alias in the descriptor.
     */
    public EventHolder verifySupportedProcessingEvent(EntityID portletEntityId, 
            EventHolder eventHolder);
    
    /**
     * Enables the portlet to process the event.
     * This data is not persisted. If any error occurs while adding
     * the supported processing event definition, the exception is thrown.
     * 
     * @param portletEntityId the entity id of the portlet
     * @param eventHolder the EventHolder that represents the event
     * 
     * @throws com.sun.portal.container.service.PublicDescriptorHolderException if any error occurs while adding
     * the supported processing event definition
     */
    public void setSupportedProcessingEvent(EntityID portletEntityId, 
            EventHolder eventHolder)
            throws PublicDescriptorHolderException;

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
     * Returns a list of PortletID that will be processing the event.
     *
     * @param eventQname the qname of the event
     *
     * @return list of portlet Ids that will be processing the event
     */
    public List<PortletID> getEventProcessingPortlets(QName eventQname);

    /**
     * Returns a Map of all the event processing portlets with the event qname
	 * as the key and list of PortletID that will be processing that event.
	 *
     * @return Map of all event publishing portlets.
     */
    public Map<QName, List<PortletID>> getAllEventProcessingPortlets();

    /**
     * Sets the public render parameter definition for the portlet application.
     * This data is not persisted. If any error occurs while adding
     * the public render parameter definition, the exception is thrown.
     * 
     * @param portletAppName the name of the portlet application
     * @param publicRenderParameterHolder the PublicRenderParameterHolder that represents the public render parameter definition
     * 
     * @throws com.sun.portal.container.service.PublicDescriptorHolderException if any error occurs while adding
     * the public render parameter definition 
     */
    public void setPublicRenderParameterHolder(String portletAppName, 
            PublicRenderParameterHolder publicRenderParameterHolder)
            throws PublicDescriptorHolderException;
    
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
    public Map<String, String> verifySupportedPublicRenderParameters(EntityID portletEntityId, 
			List<PublicRenderParameterHolder> publicRenderParameterHolders);

    /**
     * Enables the portlet to process/publish the public render parameter.
     * This data is not persisted. If any error occurs while adding
     * the supported public render parameter definition, the exception is thrown.
     * 
     * @param portletEntityId the entity id of the portlet
     * @param publicRenderParameterHolder the PublicRenderParameterHolder that represents the public render parameter
     * 
     * @throws com.sun.portal.container.service.PublicDescriptorHolderException if any error occurs while adding
     * the supported public render parameter definition 
     */
    public void setSupportedPublicRenderParameter(EntityID portletEntityId, 
            PublicRenderParameterHolder publicRenderParameterHolder)
            throws PublicDescriptorHolderException;
    
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
