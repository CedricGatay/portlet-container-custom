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

package com.sun.portal.portletcontainer.driver.admin;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sun.portal.portletcontainer.admin.PortletRegistryHelper;
import com.sun.portal.portletcontainer.admin.PropertiesContext;
import com.sun.portal.portletcontainer.admin.deployment.WebAppDeployerException;
import com.sun.portal.portletcontainer.admin.registry.PortletRegistryConstants;
import com.sun.portal.portletcontainer.driver.DesktopMessages;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;

/**
 * AdminServlet is a router for admin related requests like deploying/undeploying of portlets
 * and creating of portlet windows.
 */
public class AdminServlet extends HttpServlet {
    
    private static Logger logger = Logger.getLogger(AdminServlet.class.getPackage().getName(),
            "PCDLogMessages");
    private static final String PORTLET_DRIVER_AUTODEPLOY_DIR = PortletRegistryHelper.getAutoDeployLocation();
    
    private ServletContext context;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        context = config.getServletContext();
        // Do not invoke autodeploy is not enabled
        if(PropertiesContext.enableAutodeploy()) {
            DirectoryWatcherTask watcher = new DirectoryWatcherTask(PORTLET_DRIVER_AUTODEPLOY_DIR,
                    new WarFileFilter(), new DirectoryChangedListener() {
                
                public void fileAdded(File file) {
                    if ( file.getName().endsWith(WarFileFilter.WAR_EXTENSION)) {
                        PortletWar portlet = new PortletWar(file);
                        checkAndDeploy(portlet);
                    } else if (file.getName().endsWith(WarFileFilter.WAR_DEPLOYED_EXTENSION)) {
                        String markerFileName = file.getAbsolutePath();
                        String portletWarFileName = markerFileName.replaceFirst(WarFileFilter.WAR_DEPLOYED_EXTENSION+"$", "");
                        PortletWar portlet = new PortletWar(portletWarFileName);
                        
                        if ( !portlet.warFileExists() ) {
                            try {
                                portlet.undeploy();
                            } catch (Exception e) {
                                if(logger.isLoggable(Level.INFO)) {
                                    logger.log(Level.INFO, "PSPCD_CSPPD0031", portlet.getWarName());
                                }
                            }
                        }
                    }
                }
                
                private void checkAndDeploy(PortletWar portlet) {
                    if (!portlet.isDeployed())
                        portlet.deploy();
                    else if (portlet.needsRedeploy()) {
                        try {
                            portlet.redeploy();
                        } catch (Exception e) {
                            if(logger.isLoggable(Level.INFO)) {
                                logger.log(Level.INFO, "PSPCD_CSPPD0031", portlet.getWarName());
                            }
                        }
                    }
                }
                
            });
            
            Timer timer = new Timer();
            long watchInterval = PropertiesContext.getAutodeployDirWatchInterval();
            timer.schedule(watcher, watchInterval, watchInterval);
        }
    }
    
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGetPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGetPost(request, response);
    }
    
    public void doGetPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        DesktopMessages.init(request);
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = AdminUtils.getClearedSession(request);
        PortletAdminData portletAdminData = null;
        try {
            portletAdminData = PortletAdminDataFactory.getPortletAdminData();
        } catch (PortletRegistryException pre) {
            throw new IOException(pre.getMessage());
        }
        AdminUtils.setAttributes(session, portletAdminData);
        
        if(isParameterPresent(request, AdminConstants.CREATE_PORTLET_WINDOW_SUBMIT)) {
            String portletWindowName = request.getParameter(AdminConstants.PORTLET_WINDOW_NAME);
            String portletName = request.getParameter(AdminConstants.PORTLET_LIST);
            String title = request.getParameter(AdminConstants.PORTLET_WINDOW_TITLE);
            if(portletName == null){
                String message = DesktopMessages.getLocalizedString(AdminConstants.NO_BASE_PORTLET);
                session.setAttribute(AdminConstants.CREATION_FAILED_ATTRIBUTE, message);
            } else {
                boolean isValid = validateString(portletWindowName, false);
                boolean isDuplicate = false;
                if(isValid) {
                    // Check if a portlet window already exists with the same name.
                    List<String> portletWindowNames = portletAdminData.getPortletWindowNames();
                    if(portletWindowNames != null) {
                        for(String tempPortletWindowName: portletWindowNames){
                            if(portletWindowName.equals(tempPortletWindowName)) {
                                String message = DesktopMessages.getLocalizedString(AdminConstants.PORTLET_WINDOW_NAME_ALREADY_EXISTS, 
                                        new String[]{portletWindowName});
                                session.setAttribute(AdminConstants.CREATION_FAILED_ATTRIBUTE, message);
                                isDuplicate = true;
                                break;
                            }
                        }
                    }
                }
                
                if(!isDuplicate) {
                    if(isValid) {
                        isValid = validateString(title, true);
                    }
                    StringBuffer messageBuffer = new StringBuffer(DesktopMessages.getLocalizedString(AdminConstants.CREATION_FAILED));
                    if(isValid) {
                        boolean success = false;
                        try {
                            success = portletAdminData.createPortletWindow(portletName, portletWindowName, title);
                        } catch (Exception ex) {
                            messageBuffer.append(".");
                            messageBuffer.append(ex.getMessage());
                        }
                        if(success) {
                            String message = DesktopMessages.getLocalizedString(AdminConstants.CREATION_SUCCEEDED);
                            session.setAttribute(AdminConstants.CREATION_SUCCEEDED_ATTRIBUTE, message);
                            AdminUtils.refreshList(request);
                        } else {
                            session.setAttribute(AdminConstants.CREATION_FAILED_ATTRIBUTE, messageBuffer.toString());
                        }
                    } else {
                        String message = DesktopMessages.getLocalizedString(AdminConstants.INVALID_CHARACTERS);
                        session.setAttribute(AdminConstants.CREATION_FAILED_ATTRIBUTE, message);
                    }
                }
            }
        } else if (isParameterPresent(request, AdminConstants.UNDEPLOY_PORTLET_SUBMIT)) {
            String[] portletsToUndeploy = request.getParameterValues(AdminConstants.PORTLETS_TO_UNDEPLOY);
            if(portletsToUndeploy == null){
                String message = DesktopMessages.getLocalizedString(AdminConstants.NO_PORTLET_APP);
                session.setAttribute(AdminConstants.UNDEPLOYMENT_FAILED_ATTRIBUTE, message);
            } else {
                StringBuffer messageBuffer = new StringBuffer();
                boolean success = false;
                for (int i = 0; i < portletsToUndeploy.length; i++) {
                    String warName = portletsToUndeploy[i];
                    try {
                        success = portletAdminData.undeploy(warName, true);
						//remove from autodeploy location if present
						removeFromAutodeploy(warName);
                    } catch (Exception ex) {
                        success = false;
                        if(ex instanceof WebAppDeployerException){
                            Object[] tokens = {warName+".war"};
                            messageBuffer.append(DesktopMessages.getLocalizedString(AdminConstants.WAR_NOT_UNDEPLOYED, tokens));
                        } else {
                            messageBuffer.append(DesktopMessages.getLocalizedString(AdminConstants.UNDEPLOYMENT_FAILED));
                            messageBuffer.append(".");
                            messageBuffer.append(ex.getMessage());
                        }
                        // If undeploy throws exception, stop undeploying remaining portlets
                        break;
                    }
                }
                
                if (success) {
                    messageBuffer.append(DesktopMessages.getLocalizedString(AdminConstants.UNDEPLOYMENT_SUCCEEDED));
                    session.setAttribute(AdminConstants.UNDEPLOYMENT_SUCCEEDED_ATTRIBUTE, messageBuffer.toString());
                    // refresh portlet list
                    AdminUtils.refreshList(request);
                } else {
                    session.setAttribute(AdminConstants.UNDEPLOYMENT_FAILED_ATTRIBUTE, messageBuffer.toString());
                }
            }
        } else if(isParameterPresent(request, AdminConstants.MODIFY_PORTLET_WINDOW_SUBMIT)) {
            String portletWindowName = request.getParameter(AdminConstants.PORTLET_WINDOW_LIST);
            setSelectedPortletWindow(session, portletWindowName);
            String width = request.getParameter(AdminConstants.WIDTH_LIST);
            String visibleValue = request.getParameter(AdminConstants.VISIBLE_LIST);
            boolean visible;
            if(PortletRegistryConstants.VISIBLE_TRUE.equals(visibleValue)){
                visible = true;
            } else {
                visible = false;
            }
            if(portletWindowName == null){
                String message = DesktopMessages.getLocalizedString(AdminConstants.NO_BASE_PORTLET_WINDOW);
                session.setAttribute(AdminConstants.MODIFY_FAILED_ATTRIBUTE, message);
            } else {
                StringBuffer messageBuffer = new StringBuffer(DesktopMessages.getLocalizedString(AdminConstants.MODIFY_FAILED));
                boolean success = false;
                try {
                    success = portletAdminData.modifyPortletWindow(portletWindowName, width, visible);
                    AdminUtils.setPortletWindowAttributes(session, portletAdminData, portletWindowName);
                } catch (Exception ex) {
                    messageBuffer.append(".");
                    messageBuffer.append(ex.getMessage());
                }
                if(success) {
                    String message = DesktopMessages.getLocalizedString(AdminConstants.MODIFY_SUCCEEDED);
                    session.setAttribute(AdminConstants.MODIFY_SUCCEEDED_ATTRIBUTE, message);
                } else {
                    session.setAttribute(AdminConstants.MODIFY_FAILED_ATTRIBUTE, messageBuffer.toString());
                }
            }
        } else if(isParameterPresent(request, AdminConstants.PORTLET_WINDOW_LIST)) {
            String portletWindowName = request.getParameter(AdminConstants.PORTLET_WINDOW_LIST);
            setSelectedPortletWindow(session, portletWindowName);
            if(portletWindowName == null){
                String message = DesktopMessages.getLocalizedString(AdminConstants.NO_BASE_PORTLET_WINDOW);
                session.setAttribute(AdminConstants.NO_WINDOW_DATA_ATTRIBUTE, message);
            } else {
                StringBuffer messageBuffer = new StringBuffer(DesktopMessages.getLocalizedString(AdminConstants.NO_WINDOW_DATA));
                // Set the attribues for show/hide and thick/thin
                boolean success = false;
                try {
                    AdminUtils.setPortletWindowAttributes(session, portletAdminData, portletWindowName);
                    success = true;
                } catch (Exception ex) {
                    messageBuffer.append(".");
                    messageBuffer.append(ex.getMessage());
                }
                if(!success) {
                    session.setAttribute(AdminConstants.NO_WINDOW_DATA_ATTRIBUTE, messageBuffer.toString());
                }
            }
        } else {
            try {
                AdminUtils.setPortletWindowAttributes(session, portletAdminData, null);
            } catch(Exception ex) {
                StringBuffer messageBuffer = new StringBuffer(DesktopMessages.getLocalizedString(AdminConstants.NO_WINDOW_DATA));
                messageBuffer.append(".");
                messageBuffer.append(ex.getMessage());
                session.setAttribute(AdminConstants.NO_WINDOW_DATA_ATTRIBUTE, messageBuffer.toString());
            }
        }
        
        RequestDispatcher reqd = context.getRequestDispatcher("/admin.jsp");
        reqd.forward(request,response);
    }
    
    private boolean validateString(String name, boolean allowSpaces) {
        if(name == null || name.trim().length() == 0){
            return false;
        }
        String value = name.trim();
        for(int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            if(!Character.isLetterOrDigit(c) && !((c == '_') || (allowSpaces && c == ' '))){
                return false;
            }
        }
        return true;
    }
    
    private boolean isParameterPresent(HttpServletRequest request, String parameter) {
        String name = request.getParameter(parameter);
        return (name == null ? false : true);
    }
    
    private void setSelectedPortletWindow(HttpSession session, String portletWindowName) {
        session.removeAttribute(AdminConstants.SELECTED_PORTLET_WINDOW_ATTRIBUTE);
        session.setAttribute(AdminConstants.SELECTED_PORTLET_WINDOW_ATTRIBUTE, portletWindowName);
    }

	private void removeFromAutodeploy(String warName) {
		String autodeploy = PortletRegistryHelper.getAutoDeployLocation();
		File file = new File(autodeploy, warName+".war");
		if(file.exists()) {
			file.delete();
		}
	}
}
