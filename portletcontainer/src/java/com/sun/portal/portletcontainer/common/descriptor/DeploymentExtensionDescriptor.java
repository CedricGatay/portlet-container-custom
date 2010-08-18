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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javax.xml.XMLConstants;
import org.w3c.dom.Element;


/**
 * This class loads the sun portlet deployment descriptor(sun-portlet.xml).
 * <P>
 * The schema is at https://portlet-container.dev.java.net/xml/sun-portlet.xsd
 * 
 */
public class DeploymentExtensionDescriptor {

    // Portlet Descriptor Element Names
	private static final String RENDER_URL_PARAMETER_CACHE_DISABLED = "render-url-parameter-cache-disabled";
	private static final String PORTAL_QUERY_PARAMETERS = "portal-query-parameters";
	private static final String SHARED_SESSION_ATTRIBUTE = "shared-session-attribute";
    private static final String NAME = "name";

	private Map<String, List<String>> portalQueryParameters;
	private List<String> renderURLParameterCacheDisabledList;
	private List<SharedSessionAttributeDescriptor> sharedSessionAttributeDescriptors;

    public DeploymentExtensionDescriptor() {
		portalQueryParameters = new HashMap<String, List<String>>();
		renderURLParameterCacheDisabledList = new ArrayList<String>();
    }

    public void load(Element root, String namespaceURI) throws DeploymentDescriptorException {

        List<Element> portletElements =
                PortletXMLDocumentHelper.getChildElements(root, PortletDescriptorConstants.PORTLET);
		
        for (Element portletElement : portletElements) {

            String portletName =
                    PortletXMLDocumentHelper.getChildTextTrim(portletElement,PortletDescriptorConstants.PORTLET_NAME);

            List<Element> portalQueryParameterNameElements =
                    PortletXMLDocumentHelper.getChildElements(portletElement, PORTAL_QUERY_PARAMETERS);
			if(!portalQueryParameterNameElements.isEmpty()) {
				List<String> portalQueryParameterNames = new ArrayList<String>();
				for(Element nameElement : portalQueryParameterNameElements) {
					String name =
							PortletXMLDocumentHelper.getChildTextTrim(nameElement, NAME);
					portalQueryParameterNames.add(name);
				}
				portalQueryParameters.put(portletName, portalQueryParameterNames);
			}
			
            String renderURLParameterCacheDisabled =
                    PortletXMLDocumentHelper.getChildTextTrim(portletElement,RENDER_URL_PARAMETER_CACHE_DISABLED);
			if (renderURLParameterCacheDisabled != null && renderURLParameterCacheDisabled.equals("true")) {
				renderURLParameterCacheDisabledList.add(portletName);
			}
		}

        List<Element> sharedSessionAttributeElements =
                PortletXMLDocumentHelper.getChildElements(root, SHARED_SESSION_ATTRIBUTE);

        if(sharedSessionAttributeElements.size() > 0){
            sharedSessionAttributeDescriptors = new ArrayList<SharedSessionAttributeDescriptor>(sharedSessionAttributeElements.size());
            for(Element sharedSessionAttributeElement: sharedSessionAttributeElements){
                SharedSessionAttributeDescriptor sharedSessionAttributeDescriptor = new SharedSessionAttributeDescriptor();
                sharedSessionAttributeDescriptor.load(sharedSessionAttributeElement, namespaceURI);
                sharedSessionAttributeDescriptors.add(sharedSessionAttributeDescriptor);
            }
        }
	}

    /**
     * Returns the List of portal query parameters for the portlet.
     * <P>
     * @return List of portal query parameters for the portlet.
     */
    public List<String> getPortalQueryParameters(String portletName) {
        return this.portalQueryParameters.get(portletName);
    }

    /**
     * Returns true of the caching of render url parameters is disabled for
	 * a portlet.
     * <P>
     * @return true of the caching of render url parameters is disabled for
	 * a portlet.
     */
    public boolean renderURLParameterCacheDisabled(String portletName) {
        boolean renderURLParameterCacheDisabled = false;

        if (this.renderURLParameterCacheDisabledList.contains(portletName)) {
            renderURLParameterCacheDisabled = true;
        }
        return renderURLParameterCacheDisabled;
    }

	public List<SharedSessionAttributeDescriptor> getSharedSessionAttributeDescriptors() {
		return this.sharedSessionAttributeDescriptors;
	}

    public String toString() {
        StringBuffer sb = new StringBuffer("DeploymentExtensionDescriptor ");

        sb.append("[ render-url-parameter-cache-disabled: ");
        sb.append(renderURLParameterCacheDisabledList);
        sb.append(" ]");
        sb.append("\n");
        sb.append("[ portal-query-parameters:");
        sb.append(portalQueryParameters);
        sb.append(" ]");


        return sb.toString();
    }
}
