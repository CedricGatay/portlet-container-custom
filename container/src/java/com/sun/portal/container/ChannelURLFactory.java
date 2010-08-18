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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The <CODE>ChannelURLFactory</CODE> is responsible for creating the ChannelURL
 */
public interface ChannelURLFactory {
    
    /**
     * Returns the ChannelURL implementation.
     *
     * @return the ChannelURL implementation.
     */
    public ChannelURL createChannelURL();
    
    /**
     * Returns the encoded url.
     *
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     * @param url the url to be encoded.
     *
     * @return the encoded url.
     */
    public String encodeURL( HttpServletRequest request, HttpServletResponse response, String url );
    
    /**
     * Returns the template used to create the render URL.
     *
     * @return the template used to create the render URL.
     */
    public String getRenderTemplate();
    
    /**
     * the template used to create the action URL.
     *
     * @return the template used to create the action URL.
     */
    public String getActionTemplate();
 
   /**
     * Returns the template used to create the resource URL.
     *
     * @return the template used to create the resource URL.
     */
    public String getResourceTemplate();
 
    /**
     * Returns the security related error URL.
     *
     * @return the security related error URL.
     */
    public String getSecurityErrorURL();
}
