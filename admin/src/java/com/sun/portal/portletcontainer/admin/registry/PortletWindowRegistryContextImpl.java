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

import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletLang;
import com.sun.portal.container.PortletType;
import com.sun.portal.portletcontainer.admin.PortletRegistryElement;
import com.sun.portal.portletcontainer.admin.PortletRegistryHelper;
import com.sun.portal.portletcontainer.admin.PortletRegistryObject;
import com.sun.portal.portletcontainer.admin.PortletRegistryReader;
import com.sun.portal.portletcontainer.admin.PortletRegistryWriter;
import com.sun.portal.portletcontainer.admin.PropertiesContext;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import java.util.ArrayList;
import java.util.List;

/**
 * PortletWindowRegistryContextImpl is a concrete implementation of the
 * PortletWindowRegistryContext interface.
 */
public class PortletWindowRegistryContextImpl implements PortletWindowRegistryContext {
    
    PortletRegistryObject portletWindowRegistry;
    
    public PortletWindowRegistryContextImpl() throws PortletRegistryException {
        String registryLocation = PortletRegistryHelper.getRegistryLocation();
        PortletRegistryReader portletWindowRegistryReader = new PortletWindowRegistryReader(registryLocation);
        portletWindowRegistry = portletWindowRegistryReader.getPortletRegistryObject();
    }
    
    private List<String> getAllPortletWindows(PortletType portletType, boolean allPortlets) throws PortletRegistryException {
        List<PortletRegistryElement> portletWindows = portletWindowRegistry.getRegistryElements();
        List<String> visiblePortletWindows = new ArrayList<String>();
        if(portletType == null){
            portletType = PortletType.ALL;
        }

		for(PortletRegistryElement portletWindow : portletWindows) {
            String portletWindowName = portletWindow.getName();
            boolean isRemote = isRemote(portletWindowName);
            if(portletType.equals(PortletType.ALL)
            || (!isRemote && portletType.equals(PortletType.LOCAL))
            || (isRemote && (portletType.equals(PortletType.REMOTE)))){
                if(allPortlets || isVisible(portletWindowName)) {
                    visiblePortletWindows.add(portletWindowName);
                }
            }
        }
        return visiblePortletWindows;
    }
    
    public List<String> getVisiblePortletWindows(PortletType portletType) throws PortletRegistryException {
        // Return only visible portlet windows
        return getAllPortletWindows(portletType, false);
    }
    
    public List<String> getAllPortletWindows(PortletType portletType) throws PortletRegistryException {
        // Return all portlet windows
        return getAllPortletWindows(portletType, true);
    }
    
    public boolean isVisible(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        String visibleValue = portletRegistryElement.getStringProperty(PortletRegistryTags.VISIBLE_KEY);
        return (PortletRegistryConstants.VISIBLE_TRUE.equals(visibleValue));
    }
    
    public EntityID getEntityId(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        return getEntityId(portletRegistryElement, portletWindowName);
    }
    
    private EntityID getEntityId(PortletRegistryElement portletRegistryElement, String portletWindowName) throws PortletRegistryException {
        String entityIDPrefix = getEntityIdPrefix(portletRegistryElement);
        EntityID entityID = new EntityID();
        entityID.setPrefix(entityIDPrefix);
        entityID.setPortletWindowName(portletWindowName);
        return entityID;
    }
    
    public List<EntityID> getEntityIds() throws PortletRegistryException {
        List<PortletRegistryElement> portletWindows = portletWindowRegistry.getRegistryElements();
        List<EntityID> entityIds = new ArrayList<EntityID>();

		for(PortletRegistryElement portletWindow : portletWindows) {
            String portletWindowName = portletWindow.getName();
            entityIds.add(getEntityId(portletWindow, portletWindowName));
        }
        return entityIds;
    }

    private void setEntityIdPrefix(PortletRegistryElement portletRegistryElement, String entityID) throws PortletRegistryException {
        portletRegistryElement.setStringProperty(PortletRegistryTags.ENTITY_ID_PREFIX_KEY, entityID);
    }
    
    private void setWidth(PortletRegistryElement portletRegistryElement, String width) throws PortletRegistryException {
        portletRegistryElement.setStringProperty(PortletRegistryTags.WIDTH_KEY, width);
    }
    
    private void setVisible(PortletRegistryElement portletRegistryElement, boolean value) throws PortletRegistryException {
        portletRegistryElement.setStringProperty(PortletRegistryTags.VISIBLE_KEY, Boolean.toString(value));
    }
    
    private String getEntityIdPrefix(PortletRegistryElement portletRegistryElement) throws PortletRegistryException {
        String entityIDPrefix = portletRegistryElement.getStringProperty(PortletRegistryTags.ENTITY_ID_PREFIX_KEY);
        return entityIDPrefix;
    }
    
    public String getPortletWindowTitle(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        return getPortletWindowTitle(portletRegistryElement);
    }
    
    public void setPortletWindowTitle(String portletWindowName, String title) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        setPortletWindowTitle(portletRegistryElement, title);
    }
    
    private String getPortletWindowTitle(PortletRegistryElement portletRegistryElement) throws PortletRegistryException {
        String title = portletRegistryElement.getStringProperty(PortletRegistryTags.TITLE_KEY);
        return title;
    }
    
    private void setPortletWindowTitle(PortletRegistryElement portletRegistryElement, String title) throws PortletRegistryException {
        portletRegistryElement.setStringProperty(PortletRegistryTags.TITLE_KEY, title);
    }
    
    public void createPortletWindow(String portletName, String portletWindowName) throws PortletRegistryException {
        createPortletWindow(portletName, portletWindowName, null, null);
    }
    
    public void createPortletWindow(String portletName, String portletWindowName, String title, String locale) throws PortletRegistryException {
        PortletRegistryElement existingPortletRegistryElement = portletWindowRegistry.getRegistryElement(portletName);
        PortletRegistryElement newPortletRegistryElement = portletWindowRegistry.getRegistryElement(portletWindowName);
        if(newPortletRegistryElement == null){
            newPortletRegistryElement = new PortletWindow();
            newPortletRegistryElement.setName(portletWindowName);
            newPortletRegistryElement.setPortletName(existingPortletRegistryElement.getPortletName());
            newPortletRegistryElement.setUserName(existingPortletRegistryElement.getUserName());
            // If title is not present use title of the existing portlet window name
            if(title == null){
                title = getPortletWindowTitle(existingPortletRegistryElement);
            }
            setPortletWindowTitle(newPortletRegistryElement, title);
            
            String entityIDPrefix = getEntityIdPrefix(existingPortletRegistryElement);
            setEntityIdPrefix(newPortletRegistryElement, entityIDPrefix);
            setWidth(newPortletRegistryElement, PortletRegistryConstants.WIDTH_THIN);
            setVisible(newPortletRegistryElement, true);
        } else {
            // If the portlet window is already present, update the title
            if(title != null){
                setPortletWindowTitle(newPortletRegistryElement, title);
            }
        }
        appendDocument(newPortletRegistryElement);
    }
    
    public void removePortletWindow(String portletWindowname) throws PortletRegistryException {
        // Remove the portlet window
        // If successful, get the rest and write it
        PortletRegistryElement existingPortletRegistryElement = portletWindowRegistry.getRegistryElement(portletWindowname);
        if(existingPortletRegistryElement != null) {
            portletWindowRegistry.removeRegistryElement(existingPortletRegistryElement);
			if(PropertiesContext.persistToFile()) {
				writeDocument(portletWindowRegistry);
			}
        }
    }
    
    public void removePortletWindows(String portletName) throws PortletRegistryException {
        // Prepare a list of portlet windows that are based on the portletName
        List<PortletRegistryElement> portletWindows = portletWindowRegistry.getRegistryElements();
        // Maintains a list of portlet windows to be removed
        List<PortletRegistryElement> removeablePortletWindows = new ArrayList<PortletRegistryElement>();
        boolean remove = false;
        for(PortletRegistryElement portletWindow : portletWindows) {
            if(portletWindow.getPortletName().equals(portletName)){
                remove = true;
                removeablePortletWindows.add(portletWindow);
            }
        }

		for(PortletRegistryElement portletWindow : removeablePortletWindows) {
            portletWindowRegistry.removeRegistryElement(portletWindow);
        }
        if(remove && PropertiesContext.persistToFile()){
            writeDocument(portletWindowRegistry);
        }
    }
    
    private void showHidePortletWindow(String portletWindowName, String visible) 
			throws PortletRegistryException {
		
        PortletRegistryElement portletRegistryElement = 
				portletWindowRegistry.getRegistryElement(portletWindowName);
		
		if(portletRegistryElement!=null){
			portletRegistryElement.setStringProperty(
					PortletRegistryTags.VISIBLE_KEY, visible);
	        appendDocument(portletRegistryElement);
		}
		
    }
    
    public void showPortletWindow(String portletWindowName, boolean visible) throws PortletRegistryException {
        // Hide the portlet window and write it
        showHidePortletWindow(portletWindowName, String.valueOf(visible));
    }
    
    public String getPortletName(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        String name = portletRegistryElement.getPortletName();
        return name;
    }
    
    public List getPortletWindows(String portletName) throws PortletRegistryException {
        List<PortletRegistryElement> portletWindows = portletWindowRegistry.getRegistryElements();
        List<String> list = new ArrayList<String>();
        for(PortletRegistryElement portletWindow : portletWindows) {
            if(portletWindow.getPortletName().equals(portletName)) {
                list.add(portletWindow.getName());
            }
        }
        return list;
    }
    
    public Integer getRowNumber(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
		PortletWindow portletWindow = (PortletWindow)portletRegistryElement;
        return new Integer(portletWindow.getRow());
    }
    
    public String getWidth(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        String width = portletRegistryElement.getStringProperty(PortletRegistryTags.WIDTH_KEY);
        return width;
    }
    
    public void setWidth(String portletWindowName, String width) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        setWidth(portletRegistryElement, width);
        appendDocument(portletRegistryElement);
    }
    
    public String getPortletID(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        String portletId =
                portletRegistryElement.getStringProperty(PortletRegistryTags.PORTLET_ID);
        return portletId;
    }
    
    public String getConsumerID(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        String consumerId =
                portletRegistryElement.getStringProperty(PortletRegistryTags.CONSUMER_ID);
        return consumerId;
    }
    
    public String getProducerEntityID(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        String producerEntityId =
                portletRegistryElement.getStringProperty(PortletRegistryTags.PRODUCER_ENTITY_ID);
        return producerEntityId;
    }
    
    
    public boolean isRemote(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        boolean isRemote = false;
        if(portletRegistryElement.getRemote()!=null){
            isRemote = Boolean.valueOf(portletRegistryElement.getRemote()).booleanValue();
        }
        return isRemote;
    }
    
    
    private PortletRegistryElement getRegistryElement(String name) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = portletWindowRegistry.getRegistryElement(name);
        if(portletRegistryElement == null)
            throw new PortletRegistryException(name  + " does not exist");
        return portletRegistryElement;
    }
    
    private PortletRegistryWriter getPortletRegistryWriter() throws PortletRegistryException {
        String registryLocation = PortletRegistryHelper.getRegistryLocation();
        return new PortletWindowRegistryWriter(registryLocation);
    }
    
    private void appendDocument(PortletRegistryElement portletRegistryElement) throws PortletRegistryException {
        PortletRegistryWriter portletWindowRegistryWriter = getPortletRegistryWriter();
        List portletWindowElementList = new ArrayList();
        portletWindowElementList.add(portletRegistryElement);
        try {
            portletWindowRegistryWriter.writeDocument(portletWindowElementList, true);
        } catch (Exception e) {
            throw new PortletRegistryException(e);
        }
    }
    
    private void writeDocument(PortletRegistryObject portletWindowRegistry) throws PortletRegistryException {
        PortletRegistryWriter portletWindowRegistryWriter = getPortletRegistryWriter();
        List portletWindowElementList = portletWindowRegistry.getRegistryElements();
        try {
            portletWindowRegistryWriter.writeDocument(portletWindowElementList, false);
        } catch (Exception e) {
            throw new PortletRegistryException(e);
        }
    }
    
    public List<String> getRemotePortletWindows() throws PortletRegistryException {
        List<PortletRegistryElement> portletWindows = portletWindowRegistry.getRegistryElements();
        List<String> remotePortletWindows = new ArrayList<String>();
        for(PortletRegistryElement portletWindow : portletWindows) {
            boolean isRemote = Boolean.valueOf(portletWindow.getRemote()).booleanValue();
            if(isRemote) {
                remotePortletWindows.add(portletWindow.getName());
            }
        }
        return remotePortletWindows;
    }
    
    public PortletLang getPortletLang(String portletWindowName) throws PortletRegistryException {
        PortletRegistryElement portletRegistryElement = getRegistryElement(portletWindowName);
        String lang = portletRegistryElement.getLang();
        PortletLang portletLang = new PortletLang(lang);
        return portletLang;
    } 
}
