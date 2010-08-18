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


package com.sun.portal.portletcontainer.admin;

import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import java.io.File;
import org.w3c.dom.Document;

/**
 * PortletRegistryReaderImpl reads the specified registry xml file into a
 * DOM Document. The registry xml files can be
 * portlet-app-registry.xml
 * portlet-window-registry.xml
 * portlet-window-preference-registry.xml.
 *
 */
public abstract class PortletRegistryReaderImpl implements PortletRegistryReader {
    
    private File file;
    
    public PortletRegistryReaderImpl(String registryLocation, String filename) {
        file = new File(registryLocation + File.separator + filename);
    }
    
    private Document getDocument() throws PortletRegistryException {
        if(file.exists()) {
            return PortletRegistryHelper.readFile(file);
        }
        return null;
    }
    
    /**
     * Reads the specified registry xml file in to the appropriate Portlet Registry Object.
     *
     * @return a <code>PortletRegistryObject</code>, that represents the registry xml file.
     */
    public PortletRegistryObject getPortletRegistryObject() throws PortletRegistryException {
		PortletRegistryObject portletRegistryObject = create();
		if(PropertiesContext.persistToFile()) {
			portletRegistryObject.read(getDocument());
		} else {
			portletRegistryObject.read(null);
		}
		return portletRegistryObject;
    }
    
    /**
     * Creates specific Portlet Registry Object.
     *
     * @return a specific <code>PortletRegistryObject</code>.
     */
    protected abstract PortletRegistryObject create();
}
