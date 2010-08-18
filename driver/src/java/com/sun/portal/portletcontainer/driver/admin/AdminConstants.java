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

/**
 * AdminConstants contains keys that are in DesktopMessages.properties file.
 */
public interface AdminConstants {
    //Attributes used in the session
    public final String PORTLETS_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.portlets";
    public final String PORTLET_APPLICATIONS_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.portletApplications";
    public final String PORTLET_WINDOWS_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.portletWindows";
    public final String SHOW_WINDOW_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.showWindow";
    public final String HIDE_WINDOW_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.hideWindow";
    public final String THICK_WINDOW_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.thickWindow";
    public final String THIN_WINDOW_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.thinWindow";
    public final String CREATION_SUCCEEDED_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.creationSucceeded";
    public final String CREATION_FAILED_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.creationFailed";
    public final String DEPLOYMENT_SUCCEEDED_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.deploymentSucceeded";
    public final String DEPLOYMENT_FAILED_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.deploymentFailed";
    public final String UNDEPLOYMENT_SUCCEEDED_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.undeploymentSucceeded";
    public final String UNDEPLOYMENT_FAILED_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.undeploymentFailed";
    public final String MODIFY_SUCCEEDED_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.modifySucceeded";
    public final String MODIFY_FAILED_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.modifyFailed";
    public final String NO_WINDOW_DATA_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.noWindowData";
    public final String SELECTED_PORTLET_WINDOW_ATTRIBUTE = "com.sun.portal.portletcontainer.driver.admin.selectedPortletWindow";
    
    public final String PORTLETS_TAB = "portlets";
    public final String ADMIN_TAB = "admin";
    public final String WSRP_TAB = "wsrp";

    public final String DEPLOY_PORTLET = "deploy";
    public final String UNDEPLOY_PORTLET = "undeploy";
    
    // Constants used in Deploy
    public final String HIDE_PORTLET_WINDOW_CHECK = "HidePortletWindowCheck";
	
    // Constants used in Undeploy
    public final String UNDEPLOY_PORTLET_SUBMIT = "UndeploySubmit";
    public final String PORTLETS_TO_UNDEPLOY = "portletsToUndeploy";
    
    // Constants used in Create Portlet Window
    public final String CREATE_PORTLET_WINDOW = "createPortletWindow";
    public final String CREATE_PORTLET_WINDOW_SUBMIT = "CreatePortletSubmit";
    public final String PORTLET_WINDOW_NAME = "portletWindowName";
    public final String PORTLET_WINDOW_TITLE = "title";
    public final String PORTLET_LIST = "portletList";
    
    // Constants used in Modify Portlet Window
    public final String MODIFY_PORTLET_WINDOW = "modifyPortletWindow";
    public final String MODIFY_PORTLET_WINDOW_SUBMIT = "ModifyPortletSubmit";
    public final String PORTLET_WINDOW_LIST = "portletList";
    public final String WIDTH_LIST = "widthList";
    public final String VISIBLE_LIST = "visibleList";
    
    public final String CREATION_SUCCEEDED = "creationSucceeded";
    public final String CREATION_FAILED = "creationFailed";
    public final String DEPLOYMENT_SUCCEEDED = "deploymentSucceeded";
    public final String DEPLOYMENT_FAILED = "deploymentFailed";
    public final String UNDEPLOYMENT_SUCCEEDED = "undeploymentSucceeded";
    public final String UNDEPLOYMENT_FAILED = "undeploymentFailed";
    public final String MODIFY_SUCCEEDED = "modifySucceeded";
    public final String MODIFY_FAILED = "modifyFailed";
    public final String NO_WINDOW_DATA = "noWindowData";
    public final String WAR_NOT_DEPLOYED = "warNotDeployed";
    public final String WAR_NOT_UNDEPLOYED = "warNotUnDeployed";
    public final String INVALID_CHARACTERS = "invalidCharacters";
    public final String NO_BASE_PORTLET = "noBasePortlet";
    public final String INVALID_PORTLET_APP = "invalidPortletApp";
    public final String NO_PORTLET_APP = "noPortletApp";
    public final String NO_BASE_PORTLET_WINDOW = "noBasePortletWindow";
    public final String PORTLET_WINDOW_NAME_ALREADY_EXISTS ="portletWindowNameAlreadyExists";
}
