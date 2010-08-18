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
 

package com.sun.portal.portletcontainer.appengine.util.pool;

public class NullObjectPool implements Pool {
    private ObjectManager objectManager;
    private boolean destroyed;
    private int leased;

    public NullObjectPool(ObjectManager objectManager) {
        this.objectManager = objectManager;
        destroyed     = false;
    }

    public Object obtainObject(Object param) {
        if (destroyed) {
            throw new IllegalStateException();
        }
        leased++;
        return this.objectManager.createObject(param);
    }

    public void releaseObject(Object o) {
        this.objectManager.destroyObject(o);
        leased--;
    }

    public int getLeased() {
        return leased;
    }

    public int getMinSize() {
        return 0;
    }

    public int getMaxSize() {
        return 0;
    }

    public void setMaxSize(int maxSize) {
    }

    public void destroy() {
        destroyed = true;
    }

    public final boolean doesReuseObjects() {
        return false;
    }


}

