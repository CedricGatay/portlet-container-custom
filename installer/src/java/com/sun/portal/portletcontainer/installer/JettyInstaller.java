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

import java.io.File;
import java.util.Properties;

/**
 *  Jetty specific implementation of the Installer interface.
 */
public class JettyInstaller implements Installer {

    private static final String ANT_TARGET = "deploy-on-jetty";

    public JettyInstaller() {
    }

    public String getPCHome(String jettyHome, String webappsDir) {
        return jettyHome + File.separator + PORTLET_CONTAINER_HOME;
    }

    public String getAntTarget() {
        return ANT_TARGET;
    }

    public void updateProperties(String jettyHome, String webappsDir, Properties properties) {
        properties.setProperty(SERVER_HOME, jettyHome);
    }

    public String getUninstallAntTarget() {
        return "uninstall-pc-on-jetty";
    }
}

