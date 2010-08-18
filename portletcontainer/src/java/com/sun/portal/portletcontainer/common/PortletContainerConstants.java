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


package com.sun.portal.portletcontainer.common;

public class PortletContainerConstants {
    
    // Prefix used for implementation specific attributes
    public static final String PREFIX = "com.sun.portal.portletcontainer.";
    
    //key for portlet locale specific resource info stored as an attribute in ServletContext
    public static final String PORTLET_RESOURCES = PREFIX + "portlet_resources";
    
    //key for portlet locale specific metadata info stored as an attribute in ServletContext
    public static final String PORTLET_METADATA_RESOURCES = PREFIX + "portlet_metadata_resources";
    
    // Prefix used to store render parameters in session
    public static final String RENDER_PARAM_PREFIX = PREFIX + "renderParameters.";

    // Prefix used to store render URL parameters in session
    public static final String RENDER_URL_PARAM_PREFIX = PREFIX + "renderURLParameters.";

    // Prefix used to store scoped attributes in session
    public static final String SCOPED_ATTRIBUTES_PREFIX = PREFIX + "scopedAttributes.";

    // Prefix used to store shared session attributes in session
    public static final String SHARED_SESSION_ATTRIBUTES_PREFIX = PREFIX + "sharedSessionAttributes.";

    //Fired events
    public static final String EVENTS = PREFIX + "events";
    
    //prefix key to hold the title of the portlet that is in the registry
    public static final String PORTLET_TITLE_IN_REGISTRY = PREFIX + "portlet.registry.title.";
    
    // The key to hold the title of the portlet
    public static final String PORTLET_TITLE = PREFIX + "portlet.title";
    
    // The title that is obtained from Generic Portlet
    public static final String TITLE_FROM_GENERIC_PORTLET = PREFIX + "portlet.generic.title";

    //key to hold the session id
    public static String HTTP_SESSION_ID = "javax.portlet.http_session_id";
    
    //key to hold the information about the session validity
    public static final String SESSION_INVALID = "javax.portlet.session_invalid";

    // key to hold a map if portlet preferences should be staged instead of written to the data store	
    public static final String PREFERENCE_STAGING_MAP = PREFIX + "preferenceStagingMap";

}
