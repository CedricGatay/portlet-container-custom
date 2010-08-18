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
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/*
 * The existing portlet webapplications has
 * com.sun.portal.portletappengine.LoadServletContextLsnrServlet
 * in their web.xmls.
 * So to maintain backward compatibility, this servlet is retained and it does not
 * perform any function.
 *
 */

public class LoadServletContextLsnrServlet extends HttpServlet {
    
    private static Logger logger = ContainerLogger.getLogger(PortletAppEngineServlet.class, "PAELogMessages");
    
    public void init(ServletConfig servletConfig) throws ServletException {
        logger.finest("PSPL_PAECSPP0004");
        
    }
}
