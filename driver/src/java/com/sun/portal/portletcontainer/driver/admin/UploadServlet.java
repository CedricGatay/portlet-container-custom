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

import com.sun.portal.portletcontainer.admin.PortletRegistryHelper;
import com.sun.portal.portletcontainer.admin.deployment.WebAppDeployerException;
import com.sun.portal.portletcontainer.admin.registry.PortletRegistryConstants;
import com.sun.portal.portletcontainer.admin.registry.PortletRegistryTags;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import com.sun.portal.portletcontainer.driver.DesktopMessages;
import com.sun.portal.portletcontainer.warupdater.PortletWarUpdaterUtil;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 * UploadServlet is responsible for uploading the portlet war file
 */
public class UploadServlet extends HttpServlet {
    
    private ServletContext context;
    
    private long maxUploadSize;
    
    private static Logger logger = Logger.getLogger(UploadServlet.class.getPackage().getName(), "PCDLogMessages");
    
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        super.init(config);
        maxUploadSize = config.getInitParameter("MAX_UPLOAD_SIZE") == null ? 10000000 : Integer.parseInt(config.getInitParameter("MAX_UPLOAD_SIZE"));
        context = config.getServletContext();
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Initialize DesktopMessages' Resource Bundle
        DesktopMessages.init(request);
        try {
            uploadFile(request, response);
        } catch (PortletRegistryException pre) {
            logger.log(Level.SEVERE,"PSPCD_CSPPD0029",pre);
        } catch (FileUploadException e) {
            logger.log(Level.SEVERE,"PSPCD_CSPPD0029",e);
        } finally {
            RequestDispatcher reqd = context.getRequestDispatcher("/admin.jsp");
            reqd.forward(request, response);            
        }
    }
   
    /* This method below is for use with commons-fileupload version 1.1
     */
      private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws FileUploadException, IOException, PortletRegistryException {
     
        HttpSession session = AdminUtils.getClearedSession(request);
     
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(maxUploadSize);
     
        // Parse the request
        List fileItems = upload.parseRequest(request);
        Iterator itr = fileItems.iterator();
        String[] fileNames = new String[2];
        int i = 0;
		boolean hidePortletWindow = false;
        while (itr.hasNext()) {
            FileItem fileItem = (FileItem) itr.next();
            if (fileItem.isFormField())  {
				String name = fileItem.getFieldName();
				if(AdminConstants.HIDE_PORTLET_WINDOW_CHECK.equals(name)) {
					hidePortletWindow = true;
				}
			} else {
                fileNames[i] = processFileItem(fileItem);
                i++;
            }
        } 
        deployPortlet(fileNames, hidePortletWindow, session);
        // refresh portlet list
        AdminUtils.refreshList(request);
    }
    
    // First item is portlet war, second item is roles file
    private void deployPortlet(String[] fileNames, boolean hidePortletWindow, HttpSession session) throws PortletRegistryException {
        String warFileName = fileNames[0];
        if(warFileName == null || !warFileName.endsWith(".war")) {
            session.setAttribute(AdminConstants.DEPLOYMENT_FAILED_ATTRIBUTE, 
                    DesktopMessages.getLocalizedString(AdminConstants.INVALID_PORTLET_APP));
        } else {
            PortletAdminData portletAdminData = PortletAdminDataFactory.getPortletAdminData();
            boolean success = false;
            StringBuffer messageBuffer = new StringBuffer();
            try {
                // If already deployed. Unregister it before deploying
                if(isPortletDeployed(warFileName)) {
                    try {
                        portletAdminData.undeploy(getWarName(warFileName), false);
                    } catch (Exception ex) {
                        //ignored
                    }
                }
				Properties portletWindow = new Properties();
				if(hidePortletWindow) {
					portletWindow.setProperty(PortletRegistryTags.VISIBLE_KEY, PortletRegistryConstants.VISIBLE_FALSE);
				} else {
					portletWindow.setProperty(PortletRegistryTags.VISIBLE_KEY, PortletRegistryConstants.VISIBLE_TRUE);
				}
                success = portletAdminData.deploy(warFileName, fileNames[1], null, portletWindow, true);
                messageBuffer.append(DesktopMessages.getLocalizedString(AdminConstants.DEPLOYMENT_SUCCEEDED));
            } catch (Exception ex) {
                success = false;
                if(ex instanceof WebAppDeployerException){
                    Object[] tokens = {PortletRegistryHelper.getUpdatedAbsoluteWarFileName(warFileName)};
                    messageBuffer.append(DesktopMessages.getLocalizedString(AdminConstants.WAR_NOT_DEPLOYED, tokens));
                } else {
                    messageBuffer.append(DesktopMessages.getLocalizedString(AdminConstants.DEPLOYMENT_FAILED));
                    messageBuffer.append(".");
                    messageBuffer.append(ex.getMessage());
                    // Undeploy only when deploy fails for reasons other than war deployment
                    try {
                        portletAdminData.undeploy(getWarName(warFileName), true);
                    } catch (Exception ex1) {
                        // ignored
                    }
                }
            }
            if (success) {
                session.setAttribute(AdminConstants.DEPLOYMENT_SUCCEEDED_ATTRIBUTE, messageBuffer.toString());
            } else {
                session.setAttribute(AdminConstants.DEPLOYMENT_FAILED_ATTRIBUTE, messageBuffer.toString());
            }
            new File(warFileName).delete();
        }
    }
    
    private String processFileItem(FileItem fi) throws FileUploadException {
        
        // On some browsers fi.getName() will return the full path to the file
        // the client select this can cause problems
        // so the following is a workaround.
        try {
            String fileName = fi.getName();
            if(fileName == null || fileName.trim().length() == 0) {
                return null;
            }
            fileName = FilenameUtils.getName(fileName);
            
            File fNew = File.createTempFile("opc", ".tmp");
            fNew.deleteOnExit();
            fi.write(fNew);
            
            File finalFileName = new File(fNew.getParent() + File.separator + fileName);
            if (fNew.renameTo(finalFileName)) {
                return finalFileName.getAbsolutePath();
            } else {
                // unable to rename, copy the contents of the file instead
                PortletWarUpdaterUtil.copyFile(fNew, finalFileName, true, false);
                return finalFileName.getAbsolutePath();
            }
            
        } catch (Exception e) {
            throw new FileUploadException(e.getMessage());
        }
    }
    
    private String getWarName(String warFileName) {
        String warName = PortletWarUpdaterUtil.getWarName(warFileName);
        String regexp = WarFileFilter.WAR_EXTENSION + "$";
        return warName.replaceFirst(regexp, "");
    }

    private boolean isPortletDeployed(String warFileName) 
        throws PortletRegistryException{
        
        String filename = PortletRegistryHelper.getWarFileLocation() + 
                File.separator + PortletWarUpdaterUtil.getWarName(warFileName);
        return (new File(filename)).exists();
    }
}
