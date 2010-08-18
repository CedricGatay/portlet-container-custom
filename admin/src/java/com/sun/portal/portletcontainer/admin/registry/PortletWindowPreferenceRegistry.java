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
import com.sun.portal.portletcontainer.admin.registry.model.PortletDataPersistenceHelper;
import com.sun.portal.portletcontainer.admin.registry.model.PortletWindowPreferenceRegistryModel;
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
 * PortletWindowPreferenceRegistry represents the PortletWindowPreferenceRegistry Element 
 * in portlet-window-preferences.xml
 */
public class PortletWindowPreferenceRegistry implements PortletRegistryTags, PortletRegistryObject {
    
    private String version;
    private Map<String, PortletRegistryElement> portletWindowPreferenceTable;
    private List<PortletRegistryElement> portletWindowPreferenceList;
    private static Logger logger = ContainerLogger.getLogger(PortletWindowPreferenceRegistry.class, "PALogMessages");
    
    public PortletWindowPreferenceRegistry() {
        portletWindowPreferenceTable = new LinkedHashMap();
        portletWindowPreferenceList = new ArrayList();
    }
    
    public void read(Document document) throws PortletRegistryException {
		if(document != null || PropertiesContext.persistToFile()) {
			Element root = PortletRegistryHelper.getRootElement(document);
			if(root != null)
				populate(root);
		} else {
			populate(PortletDataPersistenceHelper.getPortletWindowPreferenceRegistryModels());
		}
    }
    
    public void addRegistryElement(PortletRegistryElement portletWindowPreference) {
        // The unique key is the combination of portletwindowname and username;
        portletWindowPreferenceTable.put(getUniqueName(portletWindowPreference), portletWindowPreference);
        portletWindowPreferenceList.add(portletWindowPreference);
    }
    
    public PortletRegistryElement getRegistryElement(String name){
        return portletWindowPreferenceTable.get(name);
    }
    
    public List<PortletRegistryElement> getRegistryElements() {
        return this.portletWindowPreferenceList;
    }
    
    public boolean removeRegistryElement(PortletRegistryElement portletWindowPreference) {
        portletWindowPreferenceTable.remove(getUniqueName(portletWindowPreference));
		if(!PropertiesContext.persistToFile()) {
			PortletDataPersistenceHelper.removePortletWindowPreferenceRegistryModel(portletWindowPreference.getName());
		}
        return portletWindowPreferenceList.remove(portletWindowPreference);
    }

    private void populate(Element root){
        // Get the attributes for PortletWindowPreferenceRegistry Tag.
        Map portletWindowPreferencesAttributes = XMLDocumentHelper.createAttributeTable(root);
        setVersion((String)portletWindowPreferencesAttributes.get(PortletRegistryTags.VERSION_KEY));
        // Get a list of PortletWindowPreference tags and populate values from it.
        List portletWindowPrefTags = XMLDocumentHelper.createElementList(root);
        int numOfPortletWindowPrefTags = portletWindowPrefTags.size();
        for(int i=0; i<numOfPortletWindowPrefTags; i++) {
            Element portletWindowPreferenceTag = (Element)portletWindowPrefTags.get(i);
            PortletWindowPreference portletWindowPreference = new PortletWindowPreference();
            portletWindowPreference.populateValues(portletWindowPreferenceTag);
            addRegistryElement(portletWindowPreference);
        }
		if(logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, "PSPL_CSPPAM0046", portletWindowPreferenceTable);
		}
    }
    
	private void populate(List<PortletWindowPreferenceRegistryModel> portletWindowPreferenceRegistryModels) {
        for(PortletWindowPreferenceRegistryModel portletWindowPreferenceRegistryModel : portletWindowPreferenceRegistryModels) {
            PortletWindowPreference portletWindowPreference = new PortletWindowPreference();
            portletWindowPreference.populateValues(portletWindowPreferenceRegistryModel);
            addRegistryElement(portletWindowPreference);
        }
		if(logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, "PSPL_CSPPAM0046", portletWindowPreferenceTable);
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
			Element rootTag = XMLDocumentHelper.createElement(document, PORTLET_WINDOW_PREFERENCE_REGISTRY_TAG);
			//Add the atribute to the child
			rootTag.setAttribute(VERSION_KEY, getVersion());
			document.appendChild(rootTag);
			for(PortletRegistryElement portletRegistryElement : getRegistryElements()) {
				PortletWindowPreference portletWindowPreference = (PortletWindowPreference)portletRegistryElement;
				portletWindowPreference.create(document, rootTag);
			}
		} else {
			for(PortletRegistryElement portletWindowPreference : getRegistryElements()) {
				PortletDataPersistenceHelper.updatePortletWindowPreferenceRegistryModel(portletWindowPreference);
			}
		}
    }

    private String getUniqueName(PortletRegistryElement portletWindowPreference) {
        return portletWindowPreference.getName()+portletWindowPreference.getUserName();
    }
}
