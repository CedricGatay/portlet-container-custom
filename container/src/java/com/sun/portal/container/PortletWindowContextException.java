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
 

package com.sun.portal.container;

/**
 * A <code>PortletWindowContextException</code> is thrown when there are errors related to
 * portlet window.
 */

public class PortletWindowContextException extends Exception {
    
    /**
     * Constructs a new exception with the specified message
     *
     * @param msg The descriptive message.
     */
    public PortletWindowContextException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs a new exception with the specified message, and the original
     * <code>exception</code> or <code>error</code>
     * 
     * @param msg The descriptive message.
     * @param cause The original <code>exception</code> or <code>error</code>.
     */
    public PortletWindowContextException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a new exception with the original
     * 
     * @param cause The original <code>exception</code> or <code>error</code>.
     */
    public PortletWindowContextException(Throwable cause) {
        super(cause);
    }
}