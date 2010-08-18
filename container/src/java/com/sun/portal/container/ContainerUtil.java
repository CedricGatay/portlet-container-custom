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
 * [Name of File] [ver.__] [Date]
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */

package com.sun.portal.container;

import com.sun.portal.container.service.Service;
import com.sun.portal.container.service.ServiceManager;
import com.sun.portal.container.service.policy.DistributionType;
import com.sun.portal.container.service.policy.EventPolicy;
import com.sun.portal.container.service.policy.PolicyService;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * ContainerUtil contains utilities that are used in the container.
 * 
 */
public class ContainerUtil {

	/**
	 * Returns javascript safe name for the given name.
	 * 
	 * @param name the name that needs to be javascript safe
	 * 
	 * @return javascript safe name for the given name.
	 */
	public static String getJavascriptSafeName(String name){
		if(name == null){
			return null;
		}
		// replace the characters that makes invalid javascript/css
        name = name.replace('/', '_');
        name = name.replace('.', '_');
        name = name.replace('-', '_');
        name = name.replace(' ', '_');
        return name.replace('|','_');		
	}
	
    public static DistributionType getEventDistributionType(ContainerRequest containerRequest) {
        DistributionType distributionType;
        if(containerRequest.getPolicyManager().getEventPolicy() != null) {
            distributionType = containerRequest.getPolicyManager().getEventPolicy().getDistributionType();
        } else if(getPolicyService() != null && getPolicyService().getEventPolicy() != null ) {
            distributionType = getPolicyService().getEventPolicy().getDistributionType();
        } else {
            distributionType = DistributionType.ALL_PORTLETS_ON_PAGE;
        }
        return distributionType;
    }
    
    public static DistributionType getPublicRenderParameterDistributionType(ContainerRequest containerRequest) {
        DistributionType distributionType;
        if(containerRequest.getPolicyManager().getPublicRenderParameterPolicy() != null) {
            distributionType = containerRequest.getPolicyManager().getPublicRenderParameterPolicy().getDistributionType();
        } else if(getPolicyService() != null && getPolicyService().getPublicRenderParameterPolicy() != null ) {
            distributionType = getPolicyService().getPublicRenderParameterPolicy().getDistributionType();
        } else {
            distributionType = DistributionType.ALL_PORTLETS_ON_PAGE;
        }
        return distributionType;
    }
    
    public static int getMaxEventGeneration() {
        int maxEventGeneration;
        if(getPolicyService() != null && getPolicyService().getEventPolicy() != null ) {
            maxEventGeneration = getPolicyService().getEventPolicy().getMaxGenerationOfEvents();
        } else {
            maxEventGeneration = EventPolicy.DEFAULT_MAX_EVENT_GENERATION;
        }
        return maxEventGeneration;
    }
    
    /**
     * Returns the Service for the serviceName
     * @param serviceName the name of the service
     * @return the Service for the serviceName
     */
    public static Object getService(String serviceName) {
        return ServiceManager.getServiceManager().getService(serviceName);
    }

    public static PolicyService getPolicyService() {
        return (PolicyService)getService(Service.POLICY_SERVICE);
    }
    
    public static Serializable serializeJavaToXml(QName qname, Serializable value) throws Exception {
        Serializable xmlData = null;
        if(value != null) {
			Class clazz = value.getClass();
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Marshaller marshaller = jaxbContext.createMarshaller();
			Writer out = new StringWriter();
			JAXBElement<Serializable> element = new JAXBElement<Serializable>(qname, clazz, value);
			marshaller.marshal(element, out);
			xmlData = out.toString();
        }
        return xmlData;
    }

    public static Serializable deserializeXmlToJava(String valueType, Serializable value) throws Exception {
        Serializable deserializedValue = null;
        if(value != null) {
            XMLStreamReader xml = null;
            if (value instanceof String) {
				xml = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader((String) value));
            }

            if (xml != null) {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				Class<? extends Serializable> clazz = loader.loadClass(valueType).asSubclass(Serializable.class);
				JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				JAXBElement result = unmarshaller.unmarshal(xml, clazz);
				deserializedValue = (Serializable) result.getValue();
            }
        }
        return deserializedValue;
    }
}
