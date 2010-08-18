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

public class URLHelper {
    public static String escapeURL(String urlString) {
        String escapedUrl = urlString.replaceAll("&", "&amp;");
        escapedUrl = escapedUrl.replaceAll("<", "&lt;");
        escapedUrl = escapedUrl.replaceAll(">", "&gt;");
        escapedUrl = escapedUrl.replaceAll("\'", "&#039;");
        escapedUrl = escapedUrl.replaceAll("\"", "&#034;");
        return escapedUrl;
    }   
}