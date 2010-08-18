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
 * [Name of File] [ver.__] [Date]
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */

package com.sun.portal.container;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The <CODE>PortalPropertiesHolder</CODE> defines methods that
 * can be used by the portlet container to provide the
 * portlets vendor specific data.
 */
public interface PortalPropertiesHolder {

	/**
	 * Returns a map of attributes that will be available via getAttribute
	 * method of PortletRequest
	 *
	 * @param request the HttpServletRequest object
	 * @param response the HttpServletResponse object
	 *
	 * @return a map of attributes
	 */
	public Map<String, Object> getAttributes(HttpServletRequest request,
		HttpServletResponse response);

	/**
	 * Returns a map of properties that will be available via getProperty*
	 * methods of PortletRequest
	 *
	 * @param request the HttpServletRequest object
	 * @param response the HttpServletResponse object
	 * 
	 * @return a map of attributes
	 */
	public Map<String, String> getProperties(HttpServletRequest request,
		HttpServletResponse response);

}

