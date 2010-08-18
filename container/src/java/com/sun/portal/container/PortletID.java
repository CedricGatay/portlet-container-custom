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
 * The <code>PortletID<code>/ class is responsible for creating the unique id
 * for a portlet.
 *
 * The PortletID has the following form:
 * <portlet application name>.<portlet name>
 *
 */
public class PortletID implements Serializable {
    
    private String portletApplicationName;
    private String portletName;
    
    private static final String DOT_SEPARATOR = ".";
    
    private PortletID() {
    }
    
    /**
     * Creates a PortletID with portlet application name and portlet name.
     *
     * @param portletApplicationName the name of the portlet application.
     * @param portletName the name of the portlet.
     */
    public PortletID(String portletApplicationName, String portletName) {
        this.portletApplicationName = portletApplicationName;
        this.portletName = portletName;
    }
    
    /**
     * Returns the name of the Portlet Application
     *
     * @return the name of the Portlet Application
     */
    public String getPortletApplicationName() {
        return portletApplicationName;
    }
    
    /**
     * Returns the name of the Portlet
     *
     * @return the name of the Portlet
     */
    public String getPortletName() {
        return portletName;
    }
    
    /**
     * Returns the String representation of the Portlet ID.
     *
     * @return the String representation of the Portlet ID.
     */
    public String toString() {
        if(this.portletApplicationName != null && this.portletName != null) {
            return getString(this.portletApplicationName, DOT_SEPARATOR, this.portletName);
        } else {
            return "";
        }
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
        final PortletID other = (PortletID) obj;
        if (this.portletApplicationName != other.portletApplicationName && (this.portletApplicationName == null || !this.portletApplicationName.equals(other.portletApplicationName))) {
            return false;
        }
        if (this.portletName != other.portletName && (this.portletName == null || !this.portletName.equals(other.portletName))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.portletApplicationName != null ? this.portletApplicationName.hashCode() : 0);
        hash = 17 * hash + (this.portletName != null ? this.portletName.hashCode() : 0);
        return hash;
    }
    
    
}
