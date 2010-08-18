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
 

package com.sun.portal.portletcontainer.driver.portletwindow;

/**
 * This class defines the window states that can possibly be returned from the
 * <code>ContainerProvider.getWindowState(channel)</code> method.
 *
 * The window state informs a client of a provider object what it can expect to
 * be returned from the ContainerProvider's getWindowState() method and what can be
 * set through the ContainerProvider's setWindowState() method.
 */

public interface PortletWindowStates {
  
  /**
   * In this window state only the provider titlebar is shown
   */
  public final static int MINIMIZE = 1;
  
  /**
   * In this window state the provider is displayed normal.
   */
  public final static int NORMAL = 2;

  /**
   * In this window state the provider is displayed maximized
   */
  public final static int MAXIMIZE = 3;

  /**
   * Window state not defined.
   */
  public final static int NOT_DEFINED = -1;

}  
