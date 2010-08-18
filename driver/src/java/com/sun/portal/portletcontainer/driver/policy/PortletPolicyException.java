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

package com.sun.portal.portletcontainer.driver.policy;

/**
 * PortletPolicyException may be thrown when portlet policy is being parsed.
 * 
 */
public class PortletPolicyException extends Exception {
    
    /**
     * Creates a new instance of <code>PortletPolicyException</code> without detail message.
     */
    public PortletPolicyException() {
    }
    
    
    /**
     * Constructs an instance of <code>PortletPolicyException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PortletPolicyException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>PortletPolicyException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PortletPolicyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
