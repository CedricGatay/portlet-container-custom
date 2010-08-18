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

import com.sun.portal.container.ContainerLogger;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * PortletLSResourceResolver implements LSResourceResolver interface and is responsible
 * for redirecting the references to external resources in the schema to the resource
 * present in the application.
 *
 */
public class PortletLSResourceResolver implements LSResourceResolver {
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PortletLSResourceResolver.class,
                                                             "PCCLogMessages");

    public PortletLSResourceResolver() {
    }

    public LSInput resolveResource(String type, 
            String namespaceURI, String publicId, String systemId, String baseURI) {
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "PSPL_PCCCSPPCCD0027",
                    new String[] {type, namespaceURI, publicId, systemId, baseURI}  );
        }
        
        if (systemId == null || systemId.lastIndexOf('/') == systemId.length()) {
            return null;
        }
        
        String fileName = systemId.substring(systemId.lastIndexOf('/')+1);                    
        if (fileName == null) {
            fileName = systemId;
        }
        logger.log(Level.FINER, "PSPL_PCCCSPPCCD0028", fileName);
        
        InputStream schemaStream = Thread.currentThread().
                getContextClassLoader().getResourceAsStream(fileName);
        LSInput lsInput = new PortletLSInput();
        lsInput.setBaseURI(baseURI);
        lsInput.setByteStream(schemaStream);
        lsInput.setPublicId(publicId);
        lsInput.setSystemId(systemId);
        return lsInput;
    }
    
}
