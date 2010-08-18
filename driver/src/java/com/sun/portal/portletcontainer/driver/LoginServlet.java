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


package com.sun.portal.portletcontainer.driver;

import com.sun.portal.container.service.Service;
import com.sun.portal.container.service.ServiceManager;
import com.sun.portal.container.service.coordination.ContainerEventService;
import com.sun.portal.portletcontainer.admin.PortletRegistryCache;

import com.sun.portal.portletcontainer.admin.PropertiesContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.xml.namespace.QName;

public class LoginServlet extends HttpServlet {
    
    ServletContext context;
    
    private static Logger logger = Logger.getLogger(DesktopServlet.class.getPackage().getName(),
            "PCDLogMessages");
    
    /**
     * Reads the DriverConfig.properties file.
     * Initializes the Portlet Registry files.
     *
     * @param config the ServletConfig Object
     *
     * @throws javax.servlet.ServletException 
     */
    public void init(ServletConfig config)
    throws ServletException {
        super.init(config);
        context = config.getServletContext();
        PropertiesContext.init();
        PortletRegistryCache.init();
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            doGetPost(request, response);
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGetPost(request, response);
    }
    
    private void doGetPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        QName login = new QName("urn:oasis:names:tc:wsrp:v2:types", "login");
        getContainerEventService().setEvent(login, null, request, response);
        RequestDispatcher rd = request.getRequestDispatcher("/dt");
        rd.forward(request, response);
    }
    
    private ContainerEventService getContainerEventService() {
        return (ContainerEventService)ServiceManager.getServiceManager().getService(Service.CONTAINER_EVENT_SERVICE);
    }
}
