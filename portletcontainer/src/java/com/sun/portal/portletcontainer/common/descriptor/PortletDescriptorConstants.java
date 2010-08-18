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

package com.sun.portal.portletcontainer.common.descriptor;

/**
 * This class defines constants for all the tags in portlet.xml
 */
public interface PortletDescriptorConstants {

    // version values
    public static final String V1 = "1.0";
    public static final String V2 = "2.0";
    
    // Portlet App Descriptor Element Names
    public static final String PORTLET_APP = "portlet-app";
    public static final String PORTLET_APP_NAME = "portlet-app-name";
    public static final String DESCRIPTION = "description";
    public static final String DISPLAY_NAME = "display-name";
    public static final String USER_ATTRIBUTE = "user-attribute";
    public static final String SECURITY_CONSTRAINT = "security-constraint";
    public static final String XML_LANG_ATTR = "xml:lang";
    //Following added during v2.0 development
    public static final String VALUE_TYPE = "value-type";
    public static final String NAME = "name";
    public static final String QNAME = "qname";
    public static final String ALIAS = "alias";
    public static final String WINDOW_STATE = "window-state";    
    public static final String PORTLET_MODE = "portlet-mode";
    public static final String PORTAL_MANAGED = "portal-managed";    
    public static final String CUSTOM_PORTLET_MODE = "custom-portlet-mode";
    public static final String CUSTOM_WINDOW_STATE = "custom-window-state";
    public static final String FILTER_NAME = "filter-name";
    public static final String FILTER_CLASS = "filter-class";
    public static final String LIFECYCLE = "lifecycle";    
    public static final String DEFAULT_XML_LANG = "en";
    public static final String VERSION = "version";
    public static final String ID = "id";
    
    
    //Added for Ver 2.0
    public static final String FILTER = "filter";
    public static final String FILTER_MAPPING = "filter-mapping";
    public static final String EVENT_DEFINITION = "event-definition";
    public static final String PUBLIC_RENDER_PARAMETER = "public-render-parameter";      
    
    
    // Portlet Descriptor Element Names
    public static final String PORTLET = "portlet";
    public static final String PORTLET_NAME = "portlet-name";
    public static final String PORTLET_CLASS = "portlet-class";
    public static final String PORTLET_PREFERENCES = "portlet-preferences";
    public static final String INIT_PARAM = "init-param";
    public static final String SUPPORTED_LOCALE = "supported-locale";
    public static final String EXPIRATION_CACHE = "expiration-cache";
    public static final String CACHE_SCOPE = "cache-scope";
    public static final String PORTLET_INFO = "portlet-info";
    public static final String SUPPORTS = "supports";
    public static final String SECURITY_ROLE_REF = "security-role-ref";
    public static final String DEPLOYMENT_EXTENSION = "deployment-extension";
    public static final String RESOURCE_BUNDLE = "resource-bundle";
    
    public static final int EXPIRATION_CACHE_NOT_DEFINED = -999;
    
    //Added by v 2.0
    public static final String DEFAULT_NAMESPACE = "default-namespace";
    public static final String CONTAINER_RUNTIME_OPTION = "container-runtime-option";
    public static final String SUPPORTED_PROCESSING_EVENT = "supported-processing-event";
    public static final String SUPPORTED_PUBLISHING_EVENT = "supported-publishing-event";
    public static final String SUPPORTED_PUBLIC_RENDER_PARAMETER = "supported-public-render-parameter";
    public static final String IDENTIFIER = "identifier";
    public static final String URL_LISTENER = "listener";
    public static final String URL_LISTENER_CLASS = "listener-class";
    
    // Container Runtime options
    public static final String ESCAPE_XML = "javax.portlet.escapeXml";
    public static final String RENDER_HEADERS = "javax.portlet.renderHeaders";
    public static final String SERVLET_DEFAULT_SESSION_SCOPE = "javax.portlet.servletDefaultSessionScope";
    public static final String ACTION_SCOPED_REQUEST_ATTRIBUTES = "javax.portlet.actionScopedRequestAttributes";
    
    // Security constraint descriptor element names
    public static final String PORTLET_COLLECTION = "portlet-collection";
    public static final String USER_DATA_CONSTRAINT = "user-data-constraint";
    public static final String TRANSPORT_GUARANTEE = "transport-guarantee";
    
    // Security transport-guarantee types
    public static final String CONFIDENTIAL = "CONFIDENTIAL";
    public static final String INTEGRAL = "INTEGRAL";
    public static final String NONE = "NONE";        
    
    // Prefrence Descriptor Element Names
    public static final String PREF_NAME = "name";
    public static final String PREF_VALUE = "value";
    public static final String READ_ONLY = "read-only";
    public static final String MULTI_VALUE = "multi-value";

    // Security Role Ref Element Names
    public static final String ROLE_NAME = "role-name";
    public static final String ROLE_LINK = "role-link";
    
    // Init Param Element Names
    public static final String PARAM_NAME = "name";
    public static final String PARAM_VALUE = "value";

    public static final String PRIVATE_CACHING_SCOPE = "private";
    public static final String PUBLIC_CACHING_SCOPE = "public";
}
