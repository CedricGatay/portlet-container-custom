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


package com.sun.portal.portletcontainer.context.window.impl;

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.EntityID;
import com.sun.portal.container.PortletLang;
import com.sun.portal.container.PortletType;
import com.sun.portal.container.PortletWindowContext;
import com.sun.portal.container.PortletWindowContextException;
import com.sun.portal.container.service.EventHolder;
import com.sun.portal.container.service.PortletDescriptorHolder;
import com.sun.portal.container.service.PortletDescriptorHolderFactory;
import com.sun.portal.container.service.PublicRenderParameterHolder;
import com.sun.portal.container.service.policy.DistributionType;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryContext;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryContextAbstractFactory;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryContextFactory;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import com.sun.portal.portletcontainer.invoker.WindowInvokerConstants;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * The <code>PortletWindowContextImpl</code> class provides a default
 * implementation for the <code>PortletWindowContext</code> interface.
 */
public class PortletWindowContextImpl implements PortletWindowContext {
    
    private PortletRegistryContext portletRegistryContext;
    private HttpServletRequest request;
    private static final String ENC = "UTF-8";
    private static final String PORTLET_HANDLE_PREF_NAME = "portletHandle";
    private static final String IS_WSRP_REQ="is.wsrp.request";
    private static Logger logger = ContainerLogger.getLogger(PortletWindowContextImpl.class,
            "PCCTXLogMessages");

    private static List<String> roles = Arrays.asList( "role1", "role2", "role3",
                                                        "role4", "role5", "role6",
                                                        "role7", "role8", "role9");
    private String userRepresentation = null;
    
    public PortletWindowContextImpl() {
    }
    
    public PortletWindowContextImpl(String userID) {
        this.userRepresentation = userID;
    }
    
    public void init(HttpServletRequest request) {
        this.request = request;
        try {
            PortletRegistryContextAbstractFactory afactory = new PortletRegistryContextAbstractFactory();
            PortletRegistryContextFactory factory = afactory.getPortletRegistryContextFactory();
            this.portletRegistryContext = factory.getPortletRegistryContext();
        } catch (PortletRegistryException pre) {
            logger.log(Level.SEVERE,"PSPL_PCCTXCSPPCI0012", pre);
        }
    }
    
    public String getDesktopURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        return requestURL.toString();
    }
    
    public String getDesktopURL(HttpServletRequest request, String query, boolean escape) {
        StringBuffer urlBuffer = new StringBuffer(getDesktopURL(request));
        if (query != null && query.length() != 0) {
            urlBuffer.append("?").append(query);
        }
        String url = urlBuffer.toString();
        if ( escape ) {
            try {
                url = URLEncoder.encode(url, ENC);
            } catch (UnsupportedEncodingException ex) {
                //ignore
            }
        }
        return url;
    }
    
    public String getLocaleString() {
        Locale locale = request.getLocale();
        return locale.toString();
    }
    
    public String getContentType() {
        String contentType = "text/html";
        return contentType;
    }
    
    public String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, ENC);
        } catch (UnsupportedEncodingException usee) {
            return url;
        }
    }
    
    public boolean isAuthless(HttpServletRequest request) {
        // For now authless can also edit , hence return false
        return false;
    }
    
    public String getAuthenticationType() {
        return request.getAuthType();
    }
    
    public String getUserRepresentation() {
        // The order in which this is suppose to be done
        // 1. Check if userRepresentation is explicity set , If yes use it always
        // 2. If user ID is null, Get from request - getPrincipal
        // 3. If userRepresentation is null, see if wsrp is sending it (in case of resource URL) 
        // 4. else retun null.
        if(userRepresentation == null){
            Principal principal = request.getUserPrincipal();
            if(principal != null) {
                userRepresentation = principal.getName();
            }else{
                userRepresentation = request.getParameter("wsrp.userID");
            }
        }
        return userRepresentation;
    }
    
    public Object getProperty(String name) {
        Object value = null;
        if(request != null) {
            HttpSession session = request.getSession(false);
            if(session != null)
                value = session.getAttribute(name);
        }
        return value;
    }
    
    public void setProperty(String name, Object value) {
        if(request != null) {
            request.getSession(true).setAttribute(name, value);
        }
    }
    
    public List<String> getRoles() {
        //Check if any in the roles is in role
        List<String> currentRoles = new ArrayList();
        for(String role : roles) {
            if(this.request.isUserInRole(role)) {
                currentRoles.add(role);
            }
        }
        return currentRoles;
    }
    
    public Map<String, String> getUserInfo() {
        //TODO
        return Collections.EMPTY_MAP;
    }
    
    public List<String> getMarkupTypes(String portletName) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getMarkupTypes(portletName);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public String getDescription(String portletName, String desiredLocale) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getDescription(portletName, desiredLocale);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public String getShortTitle(String portletName, String desiredLocale) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getShortTitle(portletName, desiredLocale);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public String getTitle(String portletName, String desiredLocale) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getTitle(portletName, desiredLocale);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public List<String> getKeywords(String portletName, String desiredLocale) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getKeywords(portletName, desiredLocale);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public String getDisplayName(String portletName, String desiredLocale) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getDisplayName(portletName, desiredLocale);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public String getPortletName(String portletWindowName) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getPortletName(portletWindowName);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public List<EntityID> getPortletWindows(PortletType portletType, DistributionType distributionType)
    throws PortletWindowContextException {
        List<EntityID> portletList = new ArrayList();
        try {
            List<String> portlets = null;
            if(DistributionType.ALL_PORTLETS.equals(distributionType)) {
                portlets = getAllPortletWindows(portletType);
            } else if(DistributionType.ALL_PORTLETS_ON_PAGE.equals(distributionType)){
                portlets = getAvailablePortletWindows(portletType);
            } else if(DistributionType.VISIBLE_PORTLETS_ON_PAGE.equals(distributionType)) {
                portlets = getVisiblePortletWindows(portletType);
            }
            if(portlets != null) {
                for(String portletWindowName: portlets){
                    portletList.add(getEntityID(portletWindowName));
                }
            }
        } catch (PortletWindowContextException pre) {
            logger.log(Level.SEVERE,"PSPL_PCCTXCSPPCI0011", pre);
        }
        return portletList;
    }
    
    private List<String> getVisiblePortletWindows(PortletType portletType) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getVisiblePortletWindows(portletType);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    private List<String> getAvailablePortletWindows(PortletType portletType) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getAllPortletWindows(portletType);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    private List<String> getAllPortletWindows(PortletType portletType) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getAllPortletWindows(portletType);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public EntityID getEntityID(String portletWindowName) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getEntityId(portletWindowName);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public String getPortletWindowTitle(String portletWindowName, String locale) throws PortletWindowContextException {
        try {
            //locale ignore for now.
            return portletRegistryContext.getPortletWindowTitle(portletWindowName);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public Map<String, String> getRoleMap(String portletWindowName) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getRoleMap(portletWindowName);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public Map<String, String> getUserInfoMap(String portletWindowName) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getUserInfoMap(portletWindowName);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public PortletPreferences getPreferences(String portletWindowName, ResourceBundle bundle, boolean isReadOnly) throws PortletWindowContextException {
        String userId = checkUserID();
        return new PortletPreferencesImpl(this.request, this.portletRegistryContext,
                getEntityID(portletWindowName), userId, bundle, isReadOnly );
    }
    
    private String checkUserID() {
        String userId = getUserRepresentation();
        if(userId == null){
            userId = WindowInvokerConstants.AUTHLESS_USER_ID;
        }if(isWSRPRequest()){
            return Base64.encode(userId);
        }
        return userId;
    }
    
    private boolean isWSRPRequest(){
        Object isWSRPReq = request.getAttribute(IS_WSRP_REQ);
        return isWSRPReq == null ? false : true;
    }
    
    public EventHolder verifySupportedPublishingEvent(EntityID portletEntityId, EventHolder eventHolder) {
        PortletDescriptorHolder portletDescriptorHolder = getPortletDescriptorHolder();
        if(portletDescriptorHolder == null)
            return null;
        return portletDescriptorHolder.verifySupportedPublishingEvent(portletEntityId, eventHolder);
    }
    
    public List<EventHolder> getSupportedPublishingEventHolders(EntityID portletEntityId) {
        PortletDescriptorHolder portletDescriptorHolder = getPortletDescriptorHolder();
        if(portletDescriptorHolder == null)
            return null;
        return portletDescriptorHolder.getSupportedPublishingEventHolders(portletEntityId);
    }

    public EventHolder verifySupportedProcessingEvent(EntityID portletEntityId, EventHolder eventHolder) {
        PortletDescriptorHolder portletDescriptorHolder = getPortletDescriptorHolder();
        if(portletDescriptorHolder == null)
            return null;
        return portletDescriptorHolder.verifySupportedProcessingEvent(portletEntityId, eventHolder);
    }
    
    public List<EventHolder> getSupportedProcessingEventHolders(EntityID portletEntityId) {
        PortletDescriptorHolder portletDescriptorHolder = getPortletDescriptorHolder();
        if(portletDescriptorHolder == null)
            return null;
        return portletDescriptorHolder.getSupportedProcessingEventHolders(portletEntityId);
    }

    public Map<String, String> verifySupportedPublicRenderParameters(EntityID portletEntityId, List<PublicRenderParameterHolder> publicRenderParameterHolders) {
        PortletDescriptorHolder portletDescriptorHolder = getPortletDescriptorHolder();
        if(portletDescriptorHolder == null)
            return Collections.emptyMap();
        return portletDescriptorHolder.verifySupportedPublicRenderParameters(portletEntityId, publicRenderParameterHolders);
    }
    
    public List<PublicRenderParameterHolder> getSupportedPublicRenderParameterHolders(EntityID portletEntityId, Map<String, String[]> renderParameters) {
        PortletDescriptorHolder portletDescriptorHolder = getPortletDescriptorHolder();
        if(portletDescriptorHolder == null)
            return Collections.emptyList();
        return portletDescriptorHolder.getSupportedPublicRenderParameterHolders(portletEntityId, renderParameters);
    }

    private PortletDescriptorHolder getPortletDescriptorHolder() {
        PortletDescriptorHolder portletDescriptorHolder = null;
        try {
            portletDescriptorHolder = PortletDescriptorHolderFactory.getPortletDescriptorHolder();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "PSPL_PCCTXCSPPCI0010", ex);
            return null;
        }
        return portletDescriptorHolder;
    }
    
    public String getPortletID(String portletWindowName) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getPortletID(portletWindowName);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public String getConsumerID(String portletWindowName) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getConsumerID(portletWindowName);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public String getPortletHandle(String portletWindowName) throws PortletWindowContextException {
        PortletPreferences prefs = getPreferences(portletWindowName, null, true);
        return (String)prefs.getValue(PORTLET_HANDLE_PREF_NAME, null);
    }
    
    public void setPortletHandle(String portletWindowName, String portletHandle) throws PortletWindowContextException {
        PortletPreferences prefs = getPreferences(portletWindowName, null, false);
        try {
            prefs.setValue(PORTLET_HANDLE_PREF_NAME, portletHandle);
            prefs.store();
        } catch (PortletException pe) {
            throw new PortletWindowContextException(pe);
        } catch (IOException ioe) {
            throw new PortletWindowContextException(ioe);
        }
    }
    
    public String getProducerEntityID(String portletWindowName) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getProducerEntityID(portletWindowName);
        } catch (PortletRegistryException pre){
            throw new PortletWindowContextException(pre.getMessage());
        }
    }
    
    public PortletLang getPortletLang(String portletWindowName) throws PortletWindowContextException {
        try {
            return portletRegistryContext.getPortletLang(portletWindowName);
        } catch (PortletRegistryException pre) {
            throw new PortletWindowContextException(pre.getMessage());
        }
    }

    //TODO
    public void store() throws PortletWindowContextException {
        
    }
}
