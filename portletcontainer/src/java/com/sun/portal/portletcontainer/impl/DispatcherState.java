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
 

package com.sun.portal.portletcontainer.impl;

/**
 * DispatcherState is the class that keeps the state information about
 * what portlets have called the dispatcher to PAE.  It controls which
 * portlets can safely call the dispatcher to PAE and which portlets must
 * wait.  DispatcherState objects are stored as HttpServletRequest attribute
 * by PortletContainer.  Since there is no synchronizing code in this class,
 * the user (PortletContainer) must make sure it syncs around calls to the
 * class.
 **/
public class DispatcherState {

    private int count;
    private String runningAppName;

    /**
     * Return whether the portlet can safely use dispatcher to PAE.  If the
     * return value is false, the caller should call wait().  It will be woke 
     * up by notifyAll() when all the portlets have exited the critical
     * section.  However, the portlet should call canEnter() again to make
     * sure it is safe to enter the critical section after being waken up.
     *
     * @param appName   application name the portlet belongs to.
     * 
     * @param serializeAll  true if serialization is needed at all time.
     *
     * @return true if the portlet can safely use the dispatcher to PAE,
     *         false otherwise.
     **/
    public boolean canEnter( String appName, boolean serializeAll ) {
        if( serializeAll ) {
            if( runningAppName == null ) {
                // nobody is running!
                runningAppName = appName;
                count = 1;
                return true;
            } else {
                return false;
            }
        } else {
            if( runningAppName == null ) {
                // nobody is running!
                runningAppName = appName;
                count = 1;
                return true;
            } else {
                count++;
                return true;
            }
        }
    }

    /**
     * Return whether the portlet is the last one to exit the critical section
     * for dispatcher to PAE.  If the return value is true, the caller should
     * call wakeThreads() to wake up waiting threads.
     *
     * @return true when it is the last portlet to exit critical section,
     *         false otherwise.
     **/
    public boolean exit( ) {
        count--;
        if( count == 0 ) {
            runningAppName = null;
            return true;
        } else {
            return false;
        }
    }

}
