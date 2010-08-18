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

package com.sun.portal.portletcontainer.admin.deployment;

import com.sun.portal.portletcontainer.admin.PortletRegistryHelper;
import com.sun.portal.portletcontainer.admin.ServerType;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;

import com.sun.portal.portletcontainer.warupdater.PortletWarUpdaterUtil;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.handler.HandlerCollection;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class which implements the WebAppDeployer interface to provide the deployment
 * and undeployment functionality for Portlets on Jetty container.
 *
 *
 */
public class JettyWebAppDeployer implements WebAppDeployer {

    private static HandlerCollection handlerCollection;
	private static Map<String, WebAppContext> contexts = new HashMap<String, WebAppContext>();
    private static Logger logger = Logger.getLogger(JettyWebAppDeployer.class.getPackage().getName(),
            "PALogMessages");

    private String autoDeployDirectory;

    public JettyWebAppDeployer() {
		autoDeployDirectory = ServerType.getAutodeployDir();
    }

    public static void setHandlerCollection(HandlerCollection hc) {
        handlerCollection = hc;
		redeploy();
    }

    private static void redeploy() {
        try {
            String warDirName = PortletRegistryHelper.getWarFileLocation();
            
            File warDir = new File(warDirName);
            String[] warFilenames = warDir.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".war");
                }
            });

            JettyWebAppDeployer dep = new JettyWebAppDeployer();
            for (String warFilename : warFilenames) {
                dep.deploy(warFilename);
            }
        } catch (Exception ex) {
			logger.log(Level.INFO, "PSPL_CSPPAM0043", ex.toString());
        }
    }

    public boolean deploy(String warFilename) throws WebAppDeployerException {
        if (handlerCollection == null) {
            throw new IllegalArgumentException(
					"contextHandlerCollection should have been initialized in Jetty config");
        }
		boolean success = false;

		String contextName = warFilename;
        if (contextName.endsWith(".war")) {
            contextName = contextName.substring(0, contextName.length() - 4);
        }

        try {
            String warFileLocation = PortletRegistryHelper.getWarFileLocation();

            WebAppContext ctx = new WebAppContext();
            ctx.setContextPath("/" + contextName);
            ctx.setWar(warFileLocation + File.separator + warFilename);
            handlerCollection.addHandler(ctx);
            ctx.start();
			contexts.put(contextName, ctx);
			success = true;

        } catch (PortletRegistryException ex) {
            throw new WebAppDeployerException("cannot get WAR file location", ex);
        } catch (Exception ex) {
            throw new WebAppDeployerException("cannot start deployed WAR", ex);
        }

        return success;
    }

    public boolean undeploy(String warFilename) throws WebAppDeployerException {
        String contextName = warFilename;
        if (contextName.endsWith(".war")) {
            contextName = contextName.substring(0, contextName.length() - 4);
        }

		boolean success = false;
        WebAppContext ctx = contexts.get(contextName);
        if (ctx == null) {
            throw new WebAppDeployerException("no portlet deployed under context '" + contextName + "'");
        }

        try {
            ctx.stop();
            handlerCollection.removeHandler(ctx);
            contexts.remove(contextName);
			success = true;
        } catch (Exception ex) {
            throw new WebAppDeployerException("cannot stop deployed WAR", ex);
        }
        return success;
    }

}
