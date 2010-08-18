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
 

package com.sun.portal.portletcontainer.portlet.impl;

/**
 * This class uses a thread local object to store the WindowID for the 
 * current request.
 * @deprecated 
 * <P>
 * @see PortletRequestImpl#init
 */
public class WindowIDThreadLocal {
    private static ThreadLocal<String> windowIDThreadLocal = new ThreadLocal<String>();  

    private WindowIDThreadLocal() {
		// nothing, cannot be called
    }

    /**
     * Gets the window id.
     * <P>
     * @return the window id.
     */
    public static String get() {
      return windowIDThreadLocal.get();
    }

    /**
     * Sets the window id.
     * <P>
     * @param windowID The window id
     */
    public static void set(String windowID) {
		windowIDThreadLocal.set(windowID);
    }
}
