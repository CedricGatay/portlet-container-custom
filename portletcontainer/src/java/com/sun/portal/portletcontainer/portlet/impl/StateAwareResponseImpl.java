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
import com.sun.portal.container.PortletEvent;
import com.sun.portal.container.PortletID;
import com.sun.portal.container.service.EventHolder;
import com.sun.portal.container.service.PortletDescriptorHolder;
import com.sun.portal.container.service.PortletDescriptorHolderFactory;
import com.sun.portal.container.service.PublicRenderParameterHolder;
import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResponse;
import com.sun.portal.portletcontainer.common.descriptor.EventDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.PortletRequest;
import javax.portlet.StateAwareResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

/**
 * This class provides implementation of the StateAwareResponse interface.
 */
public abstract class StateAwareResponseImpl extends PortletResponseImpl implements StateAwareResponse {

    private PortletRequest portletRequest;
    private static Logger logger = ContainerLogger.getLogger(StateAwareResponseImpl.class,
            "PAELogMessages");

    /**
     * Initialize the global variables.
     * <P>
     *
     * @param request          The <code>HttpServletRequest</code> of the PAE
     * @param response          The <code>HttpServletResponse</code> of the PAE
     * @param pcRequest     The <code>PortletContainerRequest</code>
     * @param pcResponse     The <code>PortletContainerResponse</code>
     * @param portletRequest         The <code>PortletRequest</code>
     * @param portletAppDescriptor  The <code>PortletAppDescriptor</code> for the portlet application
     * @param portletDescriptor  The <code>PortletDescriptor</code> for the portlet
     */
    protected void init(HttpServletRequest request,
            HttpServletResponse response,
            PortletContainerRequest pcRequest,
            PortletContainerResponse pcResponse,
            PortletRequest portletRequest,
            PortletAppDescriptor portletAppDescriptor,
            PortletDescriptor portletDescriptor) {
        super.init(request, response, pcRequest, pcResponse, portletRequest,
                portletAppDescriptor, portletDescriptor);
        this.portletRequest = portletRequest;
    }

    /**
     * Clears the global variables.
     */
    @Override
    protected void clear() {
		super.clear();
        this.portletRequest = null;
    }

    /**
     * Checks the input render parameter map for the following.
     * 1. The parameter map should not be null
     * 2. The keys in the map must be of type String
     * 3. The values in the map must be of type String[]
     * IllegalArgumentException is thrown if any of the above is true.
     *
     * @param parameters input render parameter map
     */
    protected void checkRenderParameterMap(Map<String, String[]> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("The passed in map should not be null");
        }
        //Check if all the params are Strings and corresponding values are String[]
        Iterator keys = parameters.entrySet().iterator();
        while (keys.hasNext()) {
            Map.Entry entry = (Map.Entry) keys.next();
            Object key = entry.getKey();
            if (!(key instanceof String)) {
                throw new IllegalArgumentException("The keys in the map must be of type String");
            }
            Object value = entry.getValue();
            if (!(value instanceof String[])) {
                throw new IllegalArgumentException("The values in the map must be of type String[]");
            }
        }
    }

    /**
     * If the value is null, Sets the key in the deleted render parameter list and
     * and returns the List.
     *
     * @param  key   key of the render parameter
     * @param  value  value of the render parameter
     * @param deletedRenderParameters the deleted render parameter list of either ActionResponse or EventResponse
     *
     * @return List of deleted render parameters that contains render parameters to be deleted.
     */
    protected List<String> getDeletedRenderParameterList(String key,
            String value,
            List<String> deletedRenderParameters) {
        if (value == null) {
            if (deletedRenderParameters == null || deletedRenderParameters.size() == 0) {
                deletedRenderParameters = new ArrayList<String>();
            }
            deletedRenderParameters.add(key);
        }
        return deletedRenderParameters;
    }

    /**
     * Sets the key and value in the input render parameter map.
     * and returns the Map.
     * If the value is null, the key is deleted from the map.
     *
     * @param  key     key of the render parameter
     * @param  value  value of the render parameter
     * @param renderMap the render parameter map of either ActionResponse or EventResponse
     *
     * @return render parameter map that has the key and value.
     */
    protected Map<String, String[]> getRenderParameterMap(String key,
            String value,
            Map<String, String[]> renderMap) {
        // If the value is null, delete the parameter
        if (value == null) {
            if (renderMap != null && renderMap.size() != 0) {
                renderMap.remove(key);
            }
        } else {
            String[] values = new String[1];
            values[0] = value;
            renderMap = getRenderParameterMap(key, values, renderMap);
        }
        return renderMap;
    }

    /**
     * Sets the key and values in the input render parameter map
     * and returns the Map.
     * It checks whether the key or values is null and if null, throws
     * IllegalArgumentException.
     *
     * @param key key of the render parameter
     * @param values values of the render parameter
     * @param renderMap the render parameter map of either ActionResponse or EventResponse
     *
     * @return render parameter map that has the key and values.
     */
    protected Map<String, String[]> getRenderParameterMap(String key,
            String[] values,
            Map<String, String[]> renderMap) {
        if (key == null || values == null) {
            throw new IllegalArgumentException("Key or value argument should not be null.");
        }

		Map<String, String[]> modifiableRenderMap;
        if (renderMap == null || renderMap.size() == 0) {
            modifiableRenderMap = new HashMap();
        } else {
			modifiableRenderMap = new HashMap<String, String[]>(renderMap);
        }
		modifiableRenderMap.put(key, values);
        return modifiableRenderMap;
    }

    /**
     * Publishes an Event with the given payload.
     * <p>
     * The object type of the value must be compliant with the specified event
     * type in the portlet deployment descriptor.
     * <p>
     * The value must have a valid JAXB binding and be serializable.
     *
     * @param name
     *            the event name to publish, must not be <code>null</code>
     * @param value
     *            the value of this event, must not be <code>null</code> and
     *            must have a valid JAXB binding and be serializable.
     *
     * @exception java.lang.IllegalArgumentException
     *                if name or value is <code>null</code>, the value is not
     *                serializable, the value has not a valid JAXB binding, the
     *                object type of the value is not the same as specified in
     *                the portlet deployment descriptor for this event name.
     * @since 2.0
     */
    public void setEvent(QName name, Serializable value) {
        setEventQueue(name, value);
    }

    /**
     * Publishes an Event with the given payload in the default namespace.
     * <p>
     * The name is treated as local part of the event QName and the namespace
     * is either taken from the <code>default-event-namespace</code> element
     * in the portlet deployment descriptor, or if this element is not provided
     * the XML default namespace XMLConstants.NULL_NS_URI is used.
     * <p>
     * The object type of the value must be compliant with the specified event
     * type in the portlet deployment descriptor.
     * <p>
     * The value must have a valid JAXB binding and be serializable.
     *
     * @param name
     *            the local part of the event name to publish, must not be <code>null</code>
     * @param value
     *            the value of this event, must have a valid JAXB binding and
     *            be serializable, or <code>null</code>.
     *
     * @exception java.lang.IllegalArgumentException
     *                if name is <code>null</code>, the value is not
     *                serializable, the value has not a valid JAXB binding, the
     *                object type of the value is not the same as specified in
     *                the portlet deployment descriptor for this event name.
     * @since 2.0
     */
    public void setEvent(String name, Serializable value) {
        if (name == null) {
            throw new IllegalArgumentException("The event name must not be null");
        }
        QName qname = new QName(getPortletAppDescriptor().getDefaultNamespace(),
                name);
        setEventQueue(qname, value);
    }

    private void setEventQueue(QName qname, Serializable value) {
        if (qname == null) {
            throw new IllegalArgumentException("The event name must not be null");
        }
        //Event may optionally have a value
        if (value != null && !(value instanceof Serializable)) {
            throw new IllegalArgumentException("The event value is not serializable");
        }
    
        EventDescriptor eventDescriptor = getPortletAppDescriptor().getEventDescriptor(qname);
        // Check whether the event name is defined in the event-definition, if not ignore the event
        if (eventDescriptor == null) {
            logger.log(Level.WARNING, "PSPL_PAECSPPI0031", qname);
            return;
        }
        EventHolder eventHolder = null;
        try {
            PortletDescriptorHolder portletDescriptorHolder = PortletDescriptorHolderFactory.getPortletDescriptorHolder();
            eventHolder = portletDescriptorHolder.verifySupportedPublishingEvent(getPortletContainerRequest().getEntityID(),
                    eventDescriptor.getEventHolder());
        } catch (Exception ex) {
            logger.log(Level.WARNING, "PSPL_PAECSPPI0030", ex);
        }
        if (eventHolder == null) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "PSPL_PAECSPPI0032",
                        new Object[]{qname, getPortletContainerRequest().getEntityID()});
            }
            return;
        }
        // The value is optional
        if(value != null) {
            // Get the value-type for the event
            String valueType = eventHolder.getValueType();
            String inputValueType = value.getClass().getName();
            if (!inputValueType.equals(valueType)) {
                throw new IllegalArgumentException("The object type of the value is " + inputValueType + ". But the value-type in portlet deployment descriptor is " + valueType + " for the event " + qname);
            }
        }
        PortletEvent event = new PortletEvent(eventHolder, qname, value);
        setEventQueue(event);
    }

    /**
     * Checks if the input name is a public render parameter.
     * 
     * @param name a <code>String</code> specifying the name of the
     * public render parameter.
     * 
     * @return true if the name is a public render parameter
     * 
     * @exception  java.lang.IllegalArgumentException
     *                            if name is <code>null</code>.
     */
    protected boolean checkIfPublicRenderParameter(String name) {
        if(name == null){
            throw new IllegalArgumentException("The public render parameter to be removed cannot be null");
        }
        boolean found = false;
        Map<PortletID, List<PublicRenderParameterHolder>> publicRenderParameters = getPortletAppDescriptor().getPublicRenderParameters();
        PortletID portletID = getPortletContainerRequest().getEntityID().getPortletID();
        List<PublicRenderParameterHolder> publicRenderParameterHolders = publicRenderParameters.get(portletID);
        if(publicRenderParameterHolders != null) {
            for(PublicRenderParameterHolder publicRenderParameterHolder: publicRenderParameterHolders) {
                if(name.equals(publicRenderParameterHolder.getIdentifier())) {
                    found = true;
                    break;
                }
            }
        }
		if(!found) {
			logger.log(Level.WARNING, "PSPL_PAECSPPI0042", name);
		}
        return found;
    }

    protected abstract void setEventQueue(PortletEvent event);
}
