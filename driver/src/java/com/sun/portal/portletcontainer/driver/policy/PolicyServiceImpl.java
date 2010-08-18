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

package com.sun.portal.portletcontainer.driver.policy;

import com.sun.portal.container.service.policy.EventPolicy;
import com.sun.portal.container.service.policy.PolicyService;
import com.sun.portal.container.service.policy.PublicRenderParameterPolicy;
import com.sun.portal.container.service.ServiceAdapter;
import com.sun.portal.container.service.policy.ContainerEventPolicy;
import com.sun.portal.portletcontainer.admin.PortletRegistryHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * PolicyServiceImpl provides concrete implementation for the PolicyService.
 *
 */
public class PolicyServiceImpl extends ServiceAdapter implements PolicyService {
    
    private static final String DESCRIPTION = "Responsible for providing policy information of event and public render parameter that is effect";
    private static String PORTLET_POLICY_FILE = "portlet-policy.xml";
    
    private static Logger logger = Logger.getLogger(PolicyServiceImpl.class.getPackage().getName(),
            "PCDLogMessages");

    private EventPolicy eventPolicy;
    private ContainerEventPolicy containerEventPolicy;
    private PublicRenderParameterPolicy publicRenderParameterPolicy;
    
    @Override
    public void init(ServletContext context) {
        PortletPolicyDescriptor portletPolicyDescriptor = null;
        String portletPolicyFile = null;
        InputStream inputStream = null;
        try {
            portletPolicyFile = PortletRegistryHelper.getConfigFileLocation() + File.separator + PORTLET_POLICY_FILE;
            inputStream = new FileInputStream(portletPolicyFile);
            PortletPolicyReader descriptorReader = new PortletPolicyReader(null);
            portletPolicyDescriptor = descriptorReader.loadPortletPolicyDescriptor(inputStream);
        } catch (Exception ex) {
            if(logger.isLoggable(Level.WARNING)) {
                LogRecord logRecord = new LogRecord(Level.SEVERE, "PSPCD_CSPPD0041");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new Object[] { portletPolicyFile });
                logRecord.setThrown(ex);
                logger.log(logRecord);
            }
        } finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ignored) {}
        }
        this.eventPolicy = new EventPolicyImpl(portletPolicyDescriptor);
        this.publicRenderParameterPolicy = new PublicRenderParameterPolicyImpl(portletPolicyDescriptor);
        this.containerEventPolicy = new ContainerEventPolicyImpl(portletPolicyDescriptor);
    }
    
    @Override
    public String getName() {
        return POLICY_SERVICE;
    }
    
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
    
    @Override
    public void destroy() {
        this.eventPolicy = null;
        this.publicRenderParameterPolicy = null;
        this.containerEventPolicy = null;
    }
    
    public EventPolicy getEventPolicy() {
        return eventPolicy;
    }
    
    public ContainerEventPolicy getContainerEventPolicy() {
        return this.containerEventPolicy;
    }

    public PublicRenderParameterPolicy getPublicRenderParameterPolicy() {
        return publicRenderParameterPolicy;
    }

    public boolean renderPortletsInParallel(HttpServletRequest request) {
        // In Portlet Driver the portlets will always be rendered in serial as of now.
        return false;
    }
}
