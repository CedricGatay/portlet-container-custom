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

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.portletcontainer.admin.PortletRegistryObject;
import com.sun.portal.portletcontainer.admin.PortletRegistryElement;
import com.sun.portal.portletcontainer.admin.PortletRegistryHelper;
import com.sun.portal.portletcontainer.admin.PropertiesContext;
import com.sun.portal.portletcontainer.admin.registry.model.PortletAppRegistryModel;
import com.sun.portal.portletcontainer.admin.registry.model.PortletDataPersistenceHelper;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * PortletAppRegistry represents the PortletAppRegistry Element in portlet-app-registry.xml
 */
public class PortletAppRegistry implements PortletRegistryTags, PortletRegistryObject {

    private String version;
    private Map<String, PortletRegistryElement> portletAppTable;
    private List<PortletRegistryElement> portletAppList;

	private static Logger logger = ContainerLogger.getLogger(PortletAppRegistry.class, "PALogMessages");
    
    public PortletAppRegistry() {
        portletAppTable = new LinkedHashMap<String, PortletRegistryElement>();
        portletAppList = new ArrayList<PortletRegistryElement>();
    }

    public void read(Document document) throws PortletRegistryException {
		if(document != null || PropertiesContext.persistToFile()) {
			Element root = PortletRegistryHelper.getRootElement(document);
			if(root != null)
				populate(root);
		} else {
			populate(PortletDataPersistenceHelper.getPortletAppRegistryModels());
		}
    }
    
    public void addRegistryElement(PortletRegistryElement portletApp) {
        portletAppTable.put(portletApp.getName(), portletApp);
        portletAppList.add(portletApp);
    }
    
    public PortletRegistryElement getRegistryElement(String name){
        return portletAppTable.get(name);
    }
    
    public List<PortletRegistryElement> getRegistryElements() {
        return this.portletAppList;
    }

    public boolean removeRegistryElement(PortletRegistryElement portletApp) {
        portletAppTable.remove(portletApp.getName());
		if(!PropertiesContext.persistToFile()) {
			PortletDataPersistenceHelper.removePortletAppRegistryModel(portletApp.getName());
		}
        return portletAppList.remove(portletApp);
    }

    private void populate(Element root){
        // Get the attributes for PortletAppRegistry Tag.
        Map portletAppRegistryAttributes = XMLDocumentHelper.createAttributeTable(root);
        setVersion((String)portletAppRegistryAttributes.get(VERSION_KEY));
        // Get a list of PortletApp tags and populate values from it.
        List portletAppTags = XMLDocumentHelper.createElementList(root);
        int numOfPortletAppTags = portletAppTags.size();
        PortletApp portletApp;
        for(int i=0; i<numOfPortletAppTags; i++) {
            Element portletAppTag = (Element)portletAppTags.get(i);
            portletApp = new PortletApp();
            portletApp.populateValues(portletAppTag);
            addRegistryElement(portletApp);
        }
		if(logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, "PSPL_CSPPAM0044", portletAppTable);
		}
    }

	private void populate(List<PortletAppRegistryModel> portletAppRegistryModels) {
        PortletApp portletApp;
        for(PortletAppRegistryModel portletAppRegistryModel : portletAppRegistryModels) {
            portletApp = new PortletApp();
            portletApp.populateValues(portletAppRegistryModel);
            addRegistryElement(portletApp);
        }
		if(logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, "PSPL_CSPPAM0044", portletAppTable);
		}
    }

    public String getVersion() {
        if(this.version == null)
            return PortletRegistryConstants.VERSION;
        return this.version;
    }
    
    public void setVersion(String version){
        this.version = version;
    }
    
    public void write(Document document) {
		if(PropertiesContext.persistToFile()) {
			Element rootTag = XMLDocumentHelper.createElement(document, PORTLET_APP_REGISTRY_TAG);
			//Add the atribute to the child
			rootTag.setAttribute(VERSION_KEY, getVersion());
			document.appendChild(rootTag);
			for(PortletRegistryElement portletRegistryElement : getRegistryElements()) {
				PortletApp portletApp = (PortletApp)portletRegistryElement;
				portletApp.create(document, rootTag);
			}
		} else {
			for(PortletRegistryElement portletApp : getRegistryElements()) {
				PortletDataPersistenceHelper.updatePortletAppRegistryModel(portletApp);
			}
		}
    }
}
