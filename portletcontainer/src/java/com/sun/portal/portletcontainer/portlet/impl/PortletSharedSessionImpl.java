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
import com.sun.portal.container.ContainerUtil;
import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.descriptor.SharedSessionAttributeDescriptor;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.PortletContext;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;

/**
 * The <code>PortletSharedSessionImpl</code> class provides
 * the functionality of sharing session attributes.
 *
 */
public class PortletSharedSessionImpl extends PortletSessionImpl {

    private static Logger logger = ContainerLogger.getLogger(PortletSharedSessionImpl.class, "PAELogMessages");

	private PortletContainerRequest pcRequest;

	public PortletSharedSessionImpl(HttpSession session,
			PortletContext portletContext, String windowID,
			PortletContainerRequest pcRequest) {

		super(session, portletContext, windowID);
		this.pcRequest = pcRequest;
    }

	protected void updatePortletContainerRequest(PortletContainerRequest pcRequest) {
		this.pcRequest = pcRequest;
	}

	@Override
	public Object getAttribute(String name, int scope) {
		Object value = super.getAttribute(name, scope);
		SharedSessionAttributeDescriptor sharedSessionAttributeDescriptor
				= getSharedSessionAttributeDescriptor(name, scope);
		if(sharedSessionAttributeDescriptor != null) {
			value = this.pcRequest.getSharedSessionAttribute(name);
			value = getDeserializedValue(sharedSessionAttributeDescriptor.getValueType(), value);
		}
		return value;
	}

	@Override
	public void removeAttribute(String name, int scope) {
		super.removeAttribute(name, scope);
		SharedSessionAttributeDescriptor sharedSessionAttributeDescriptor
				= getSharedSessionAttributeDescriptor(name, scope);
		if(sharedSessionAttributeDescriptor != null) {
			this.pcRequest.removeSharedSessionAttribute(name);
		}
	}

	@Override
	public void setAttribute(String name, Object value, int scope) {
		super.setAttribute(name, value, scope);
		SharedSessionAttributeDescriptor sharedSessionAttributeDescriptor
				= getSharedSessionAttributeDescriptor(name, scope);
		if(sharedSessionAttributeDescriptor != null) {
			//shared session attribute must be serializable
			if(value == null) {
				removeAttribute(name, scope);
			} else {
				if (!(value instanceof Serializable)) {
					throw new IllegalArgumentException("The shared session attribute value is not serializable");
				}
				this.pcRequest.addSharedSessionAttribute(name,
						getSerializableValue(name, value));
			}
		}
	}

	//shared session attribute is supported only for APPLICATION_SCOPE
	private SharedSessionAttributeDescriptor
			getSharedSessionAttributeDescriptor(String name, int scope) {
		if(scope != APPLICATION_SCOPE) {
			return null;
		}
		List<SharedSessionAttributeDescriptor> sharedSessionAttributeDescriptors
				 = this.pcRequest.getSharedSessionAttributeDescriptors();
		if(sharedSessionAttributeDescriptors != null) {
			for(SharedSessionAttributeDescriptor sharedSessionAttributeDescriptor
					: sharedSessionAttributeDescriptors) {
				if(name.equals(sharedSessionAttributeDescriptor.getName())) {
					return sharedSessionAttributeDescriptor;
				}
			}
		}
		return null;
	}

	private Serializable getSerializableValue(String name, Object value)  {
		Serializable serializedValue = null;
		QName qname = new QName(name);
		try {
			serializedValue = ContainerUtil.serializeJavaToXml(qname, (Serializable) value);
		} catch (Exception ex) {
			if(logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "PSPL_PAECSPPI0048",
						new String[] { name, ex.toString() });
			}
		}
		return serializedValue;
	}

	private Serializable getDeserializedValue(String valueType, Object value) {
		Serializable deserializedValue = null;
		try {
			deserializedValue = ContainerUtil.deserializeXmlToJava(valueType, (Serializable) value);
		} catch (Exception ex) {
			if(logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "PSPL_PAECSPPI0049",
						new String[] { valueType, ex.toString() });
			}
		}
		return deserializedValue;
	}
}
