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
 

package com.sun.portal.portletcontainer.appengine.util.concurrent;

/**
 * Semaphore provides synchronized access to shared objects.
 *
 */
public class Semaphore {
    private int maxConcurrentUsers;
    private int currentUsers;
  //TODO: Not in use. Should be removed? 
    //private ConsumerProducer queue;
    
    /**
     *
     * @param maxConcurrentUsers if 0 means no restrictions on
     *        number of concurrent users.
     *
     */
    public Semaphore(int maxConcurrentUsers) {
        if (maxConcurrentUsers<0) {
            throw new IllegalArgumentException("Semaphore <init> - maxConcurrentUsers has to be 0 or greater");
        }
        this.maxConcurrentUsers = maxConcurrentUsers;
        currentUsers = 0;
    }
    
    /**
     * Returns the maximum number of concurrent users
     *
     * @return the maximum number of concurrent users
     *
     */
    public int getMaxConcurrentUsers() {
        return this.maxConcurrentUsers;
    }
    
    /**
     * Returns the current number of concurrent users
     *
     * @return the current number of concurrent users
     *
     */
    public int getCurrentUsers() {
        return currentUsers;
    }
    
    /**
     * Using sWait as wait is an Object method.
     *
     */
    public void sWait() throws InterruptedException {
        if (this.maxConcurrentUsers>0) {
            boolean go;
            synchronized (this) {
                currentUsers++;
                if (currentUsers>this.maxConcurrentUsers) {
                    wait();
                }
            }
        }
    }
    
    /**
     * Using sSignal for consistency with sWait.
     *
     */
    public void sSignal() {
        if (this.maxConcurrentUsers>0) {
            synchronized (this) {
                if(currentUsers>0) {
                    currentUsers--;
                }
                notify();
            }
        }
    }
    
}

