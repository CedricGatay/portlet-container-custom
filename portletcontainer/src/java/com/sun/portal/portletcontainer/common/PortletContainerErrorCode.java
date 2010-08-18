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

import com.sun.portal.container.ErrorCode;


/**
 * ErrorCode that must have localized message strings
 **/
public class PortletContainerErrorCode extends ErrorCode {

    public static final PortletContainerErrorCode NO_ERROR =
                    new PortletContainerErrorCode( "NO_ERROR" );

    public static final PortletContainerErrorCode READONLY_ERROR =
                    new PortletContainerErrorCode( "READONLY_ERROR" );

    public static final PortletContainerErrorCode UNSUPPORTED_MODE =
                    new PortletContainerErrorCode( "UNSUPPORTED_MODE" );

    public static final PortletContainerErrorCode UNSUPPORTED_STATE =
                    new PortletContainerErrorCode( "UNSUPPORTED_STATE" );

    public static final PortletContainerErrorCode PORTLET_UNAVAILABLE =
                    new PortletContainerErrorCode( "PORTLET_UNAVAILABLE" );

    public static final PortletContainerErrorCode SECURITY_VIOLATION =
                    new PortletContainerErrorCode( "SECURITY_VIOLATION" );

    public static final PortletContainerErrorCode MISC_ERROR =
                    new PortletContainerErrorCode( "MISC_ERROR" );

    public PortletContainerErrorCode(String errorCodeKey) {
        super(errorCodeKey);
    }

}
