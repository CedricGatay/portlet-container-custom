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


package com.sun.portal.portletcontainer.admin.registry;


import com.sun.portal.portletcontainer.admin.PortletRegistryCache;
import com.sun.portal.portletcontainer.admin.PortletRegistryElement;
import com.sun.portal.portletcontainer.admin.PortletRegistryFile;
import com.sun.portal.portletcontainer.admin.PortletRegistryObject;
import com.sun.portal.portletcontainer.admin.PortletRegistryReader;
import com.sun.portal.portletcontainer.admin.PortletRegistryWriter;
import com.sun.portal.portletcontainer.admin.PropertiesContext;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import java.util.List;


/**
 * PortletAppRegistryWriter is responsible for updating the portlet-app-registry.xml file
 */
public class PortletAppRegistryWriter extends PortletRegistryWriter {
 
    public PortletAppRegistryWriter(String registryLocation) {
        super(registryLocation, PortletRegistryFile.PORTLET_APP_REGISTRY_FILE);
    }
    
    public void writeDocument(List<PortletRegistryElement> portletAppElementList,
		boolean append) throws PortletRegistryException {
		
		PortletRegistryObject portletAppRegistry = null;
		if(append && PropertiesContext.persistToFile()) {
			PortletRegistryReader portletAppRegistryReader = new PortletAppRegistryReader(registryLocation);
			portletAppRegistry = portletAppRegistryReader.getPortletRegistryObject();
		} else {
			portletAppRegistry = new PortletAppRegistry();
		}
        for(PortletRegistryElement portletApp : portletAppElementList ){
            portletAppRegistry.addRegistryElement(portletApp);
        }
		write(portletAppRegistry);
        PortletRegistryCache.refreshPortletAppRegistryCache(true);
    }
    
}
