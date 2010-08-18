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


package com.sun.portal.portletcontainer.driver.portletwindow;

import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletType;
import com.sun.portal.container.PortletWindowContext;
import com.sun.portal.container.PortletWindowContextException;
import com.sun.portal.portletcontainer.invoker.InvokerException;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PortletWindowInvokerUtils is a utility class used by the PortletWindowInvoker
 *
 */
public class PortletWindowInvokerUtils {
    private static Logger logger = Logger.getLogger(PortletWindowInvokerUtils.class.getPackage().getName(),
            "PCDLogMessages");

    /**
     * Returns the entityID.
     *
     * It is stored in the portlet registry during deployment time.
     */
    public static EntityID getEntityID(PortletWindowContext pwc, String portletWindowName) 
    throws InvokerException {
        try {
            EntityID entityId = pwc.getEntityID(portletWindowName);
            return entityId;
        } catch (PortletWindowContextException pwce) {
            throw new InvokerException(
                    "PortletWindowInvokerUtils.getEntityID():"
                    + " couldn't get entityIDPrefix for portlet window " + portletWindowName,
                    pwce);
        }
    }
    
    /**
     * Gets the userInfoMap property for a portlet window. It is stored in
     * the registry during deployment time.
     */
    public static Map getUserInfoMap(PortletWindowContext pwc, String portletWindowName) 
    throws InvokerException {
        Map userInfoMap = null;
        try {
            String portletName = pwc.getPortletName(portletWindowName);
            userInfoMap = pwc.getUserInfoMap(portletName);
        } catch (PortletWindowContextException pwce) {
            throw new InvokerException(
                    "PortletWindowInvokerUtils.getUserInfoMap():"
                    + " couldn't get roleMap for portlet window " + portletWindowName,
                    pwce);
        }
        return userInfoMap;
    }
    
    /**
     * Gets the roleMap property for a portlet window. It is stored in
     * the registry during deployment time.
     */
    public static Map getRoleMap(PortletWindowContext pwc, String portletWindowName) 
    throws InvokerException {
        Map roleMap = null;
        try {
            String portletName = pwc.getPortletName(portletWindowName);
            roleMap = pwc.getRoleMap(portletName);
        } catch (PortletWindowContextException pwce) {
            throw new InvokerException(
                    "PortletWindowInvokerUtils.getRoleMap():"
                    + " couldn't get roleMap for portlet window " + portletWindowName,
                    pwce);
        }
        return roleMap;
    }
    
    /**
     * Returns the title of a portlet window. It is stored in
     * the registry during deployment time.
     */
    public static String getPortletWindowTitle(PortletWindowContext pwc, String portletWindowName) 
    throws InvokerException {
        String title = null;
        try {
            title = pwc.getPortletWindowTitle(portletWindowName, pwc.getLocaleString());
        } catch (PortletWindowContextException pwce) {
            throw new InvokerException(
                    "PortletWindowInvokerUtils.getPortletWindowTitle():"
                    + " couldn't get title for portlet window " + portletWindowName,
                    pwce);
        }
        return title;
    }
    
    /**
     * Returns the title of a portlet. It is stored in
     * the registry during deployment time.
     */
    public static String getPortletTitle(String portletWindowName, PortletWindowContext pwc) 
    throws InvokerException {
        String title = null;
        try {
            String portletName = pwc.getPortletName(portletWindowName);
            String localeString = pwc.getLocaleString();
            title = pwc.getTitle(portletName, localeString);
            if(title == null) {
                logger.log(Level.SEVERE, "PSPCD_CSPPD0021", new String[] { portletName, localeString });
            }
        } catch (PortletWindowContextException pwce) {
            throw new InvokerException(
                    "PortletWindowInvokerUtils.getPortletTitle():"
                    + " couldn't get title for portlet " + portletWindowName,
                    pwce);
        }
        return title;
    }
}

