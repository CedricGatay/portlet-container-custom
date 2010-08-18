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

package com.sun.portal.container;

import java.io.Serializable;

/**
 * The <code>EntityID<code>/ class is responsible for creating the unique id
 * associated with a portlet window.
 *
 * The entityId has the following form:
 * <portlet application name>|<portlet name>|<portlet window name>
 *
 */
public class EntityID implements Serializable {
    
    private PortletID portletID;
    private String portletWindowName;
    
    private static final String ENTITY_ID_DELIMITER = "|";
    
    /**
     * Creates a EntityID.
     * Client needs to use set methods to populate the entityID
     *
     */
    public EntityID() {
    }
    
    /**
     * Creates a EntityID with portlet application name and portlet name.
     * This entity id will not be associated with any portlet window.
     *
     * @param portletID the PortletID of the portlet application.
     */
    public EntityID(PortletID portletID) {
        this.portletID = portletID;
    }
    
    /**
     * The Portlet ID contains Portlet Application Name and
     * Portlet Name.
     * The portletID is of the form:
     * <portlet application name>.<portlet name>
     * Returns the Portlet ID that is contained in the EntityID.
     * Returns null, if there is no associated Portlet ID.(Usually Remote Portlet Case) 
     *
     * @return the Portlet ID that is contained in the EntityID or null if
     *          there is no associated Portlet ID(Usually Remote Portlet Case)
     */
    public PortletID getPortletID() {
        return this.portletID;
    }
    
    /**
     * Sets the EntityID prefix.
     *
     * @param entityIDPrefix the EntityID prefix.
     */
    public void setPrefix(String entityIDPrefix) {
        if(entityIDPrefix != null) {
            int index = entityIDPrefix.indexOf(ENTITY_ID_DELIMITER);
            if(index != -1) {
                String portletApplicationName = entityIDPrefix.substring(0,index);
                String portletName = entityIDPrefix.substring(index+1);
                portletID = new PortletID(portletApplicationName, portletName);
            } else {
                throw new IllegalArgumentException("The EntityID Prefix should be of the form PortletApplicationName|PortletName");
            }
        }
    }
    
    /**
     * Return the EntityID prefix. This is a combination of the
     * portlet application name and the portlet name. This value
     * will be normally stored in the portlet registry.
     *
     * @return the EntityID prefix.
     */
    public String getPrefix() {
        return getPrefix(portletID.getPortletApplicationName(), portletID.getPortletName());
    }
    
    /**
     * Returns the name of the Portlet Application
     * returns null, if there is no associated Portlet Application.(Usually Remote Portlet Case)
     *
     * @return the name of the Portlet Application
     */
    public String getPortletApplicationName() {
        if(this.portletID != null) {
            return portletID.getPortletApplicationName();
        }
        return null;
    }
    
    /**
     * Returns the name of the Portlet
     * returns null, if there is no associated Portlet.(Usually Remote Portlet Case)
     *
     * @return the name of the Portlet
     */
    public String getPortletName() {
        if(this.portletID != null) {
            return portletID.getPortletName();
        }
        return null;
    }
    
    /**
     * Returns the name of the Portlet Window
     *
     * @return the name of the Portlet Window
     */
    public String getPortletWindowName() {
        return portletWindowName;
    }
    
    /**
     * Sets the name of the Portlet Window
     *
     * @param portletWindowName the name of the Portlet Window
     */
    public void setPortletWindowName(String portletWindowName) {
        this.portletWindowName = portletWindowName;
    }
    
    /**
     * Returns the String representation of the Entity ID.
     *
     * @return the String representation of the Entity ID.
     */
    public String toString() {
        if(this.portletID != null) {
            return getEntityId(this.portletID, this.portletWindowName);
        } else {
            return this.portletWindowName;
        }
    }
    
    private String getPrefix(String portletAppName, String portletName) {
        String entityIDPrefix = getString(portletAppName, ENTITY_ID_DELIMITER, portletName);
        return entityIDPrefix;
    }
    
    private String getEntityId(PortletID portletID, String portletWindowName) {
        String entityID = getString(getPrefix(portletID.getPortletApplicationName(), 
                                    portletID.getPortletName()), ENTITY_ID_DELIMITER, portletWindowName);
        return entityID;
    }
    
    private static String getString(String... params) {
        StringBuffer buffer = new StringBuffer();
        for(String param: params) {
            buffer.append(param);
        }
        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntityID other = (EntityID) obj;
        if (this.portletID != other.portletID && (this.portletID == null || !this.portletID.equals(other.portletID))) {
            return false;
        }
        if (this.portletWindowName != other.portletWindowName && (this.portletWindowName == null || !this.portletWindowName.equals(other.portletWindowName))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.portletID != null ? this.portletID.hashCode() : 0);
        hash = 17 * hash + (this.portletWindowName != null ? this.portletWindowName.hashCode() : 0);
        return hash;
    }
}
