/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.portal.portletcontainer.admin;

import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;

/**
 *
 * @author Deepak
 */
public interface PortletRegistryReader {

    /**
     * Reads the specified registry in to the appropriate Portlet Registry Object.
     *
     * @return a <code>PortletRegistryObject</code>, that represents the registry.
     */
    public PortletRegistryObject getPortletRegistryObject() throws PortletRegistryException;
}
