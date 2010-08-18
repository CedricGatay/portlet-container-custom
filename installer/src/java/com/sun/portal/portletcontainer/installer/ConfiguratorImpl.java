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

package com.sun.portal.portletcontainer.installer;

import com.sun.appserv.addons.AddonException;
import com.sun.appserv.addons.AddonVersion;
import com.sun.appserv.addons.ConfigurationContext;
import com.sun.appserv.addons.Configurator;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfiguratorImpl implements Configurator{
    private PortletContainerConfigurator configurator;
    private static Logger logger = Logger.getLogger(ConfiguratorImpl.class.getPackage().getName(),
                                    "PCSDKLogMessages");

    /** Creates a new instance of ConfiguratorImpl */
    public ConfiguratorImpl() {
        configurator = new PortletContainerConfigurator();
    }

    public void configure(ConfigurationContext configurationContext) throws AddonException {
        try{
            File appServerHome = configurationContext.getInstallationContext().getInstallationDirectory();
            configurator.install(appServerHome.getAbsolutePath(),
                    configurationContext.getDomainDirectory().getAbsolutePath());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "configuration-failed", e);
            throw new AddonException(e);
        }
    }

    public void unconfigure(ConfigurationContext configurationContext) throws AddonException {
        try{
            File appServerHome = configurationContext.getInstallationContext().getInstallationDirectory();
            configurator.uninstall(appServerHome.getAbsolutePath(),
                    configurationContext.getDomainDirectory().getAbsolutePath());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "configuration-failed", e);
            throw new AddonException(e);
        }
    }

    public void disable(ConfigurationContext configurationContext) throws AddonException {
    }

    public void enable(ConfigurationContext configurationContext) throws AddonException {
    }

    public void upgrade(ConfigurationContext configurationContext, AddonVersion addonVersion) throws AddonException {
    }
}

