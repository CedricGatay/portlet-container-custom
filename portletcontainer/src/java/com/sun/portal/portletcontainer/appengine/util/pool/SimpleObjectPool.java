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

import java.util.List;
import java.util.ArrayList;

public class SimpleObjectPool implements Pool {
    private ObjectManager objectManager;
    private List    objectPool;
    private int     minSize;
    private int     maxSize;
    private boolean overflow;
    private int     leased;
    private int     mid;
    private boolean destroyed;

    public SimpleObjectPool(ObjectManager objectManager,int minSize,int maxSize,boolean overflow) {
        this.objectManager = objectManager;
        objectPool    = new ArrayList(maxSize);
        this.minSize       = minSize;
        this.maxSize       = maxSize;
        mid           = (this.minSize+this.maxSize)/2 + 1;
        this.overflow      = overflow;
        leased        = 0;
        destroyed     = false;
        for (int i=0;i<this.minSize;i++) {
            objectPool.add(this.objectManager.createObject(null));
        }
    }

    public synchronized Object obtainObject(Object param) {
        if (destroyed) {
            throw new IllegalStateException();
        }
        Object o = null;
        int size = objectPool.size();
        if (size>0) {
            o = objectPool.remove(size-1); // takes the last one to avoid shifting in the arraylist
            leased++;
        }
        else {
            if (leased<this.maxSize || this.overflow) {
                o = this.objectManager.createObject(param);
                leased++;
            }
        }
        return o;
    }

    public synchronized void releaseObject(Object o) {
        leased--;
        if (destroyed) {
            this.objectManager.destroyObject(o);
        }
        else {
            objectPool.add(o);
            int size = objectPool.size();
            int extra = size - this.maxSize;
            if (extra>0 && leased<mid) {
                for (int i=0;i<extra;i++) {
                    o = objectPool.remove(--size);
                    this.objectManager.destroyObject(o);
                }
            }
        }
    }

    public int getLeased() {
        return leased;
    }

    public int getMinSize() {
        return this.minSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(int maxSize) {
        if (destroyed) {
            throw new IllegalStateException();
        }
        this.maxSize = maxSize;
    }

    public synchronized void destroy() {
        if (destroyed) {
            throw new IllegalStateException();
        }
        destroyed = true;
        for (int i=0;i<objectPool.size();i++) {
            Object o = objectPool.get(i);
            this.objectManager.destroyObject(o);
        }
    }

    public final boolean doesReuseObjects() {
        return true;
    }

    protected void finalize() {
        if (!destroyed) {
            destroy();
        }
    }

}

