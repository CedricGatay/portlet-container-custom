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
package com.sun.portal.portletappengine;

import com.sun.portal.container.ContainerLogger;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * The portlet app engine servlet is the entry point of the portlet
 * application engine.
 * The existing portlet webapplications has 
 * com.sun.portal.portletappengine.PortletAppEngineServlet
 * in their web.xmls.
 * So to maintain backward compatibility, this servlet is retained, but the actual
 * processing happens in com.sun.portal.portletcontainer.appengine.PortletAppEngineServlet
 *
 */
public class PortletAppEngineServlet extends com.sun.portal.portletcontainer.appengine.PortletAppEngineServlet {

    private static Logger logger = ContainerLogger.getLogger(PortletAppEngineServlet.class, "PAELogMessages");

    /*
     * Invoke the init method of the super class
    * <P>
    * @param config The <code>ServletConfig</code> object
    */
    public void init(ServletConfig config) throws ServletException {
        logger.finest("PSPL_PAECSPP0001");
        super.init(config);
    }


    /**
     * Invoke the service method of the super class
     *
     * @param req The <code>HttpServletRequest</code>
     * @param res The <code>HttpServletResponse</code>
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws 
            IOException, ServletException {
        logger.finest("PSPL_PAECSPP0002");
        super.service(request, response);
    }


    /*
     * Invoke the destroy method of the super class
    */
    public void destroy()  {
        logger.finest("PSPL_PAECSPP0003");
        super.destroy();
    }
}