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

public class ThreadPool {

    private static class ThreadManager implements ObjectManager {
        private ThreadPool threadPool;

        private ThreadManager(ThreadPool threadPool) {
            this.threadPool = threadPool;
        }

        public Object createObject(Object param) {
            return new ReusableThread(this.threadPool);
        }

        public void destroyObject(Object o) {
            ((ReusableThread)o).finish();
        }
    }

    private ObjectPool _pool;

    public ThreadPool(int minSize,int maxSize,boolean overflow,int partitions) {
        _pool = new ObjectPool(new ThreadManager(this),minSize,maxSize,overflow,partitions);
    }

    // when the runnable ends the thread is automatically returned to the pool
    public ReusableThread obtainThread(Runnable runnable) {
        if (runnable==null) {
            throw new IllegalArgumentException("ThreadPool.obtainThread() - runnable can not be null");
        }
        ReusableThread rt = (ReusableThread) _pool.getPool().obtainObject(null);
        if (rt!=null) {
            rt.setRunnable(runnable);
            rt.setReuseThread(_pool.getPool().doesReuseObjects());
        }
        return rt;
    }

    void releaseThread(ReusableThread thread) {
        _pool.getPool().releaseObject(thread);
    }

    public int getLeased() {
        return _pool.getPool().getLeased();
    }

    public int getMinSize() {
        return _pool.getPool().getMinSize();
    }

    public int getMaxSize() {
        return _pool.getPool().getMaxSize();
    }

    public void setMaxSize(int maxSize) {
        _pool.getPool().setMaxSize(maxSize);
    }

    public void destroy() {
        _pool.getPool().destroy();
    }

    public int[] getPartitionUse() {
        int[]  NO_POOL = new int[0];
        return (_pool.getPool() instanceof ParallelObjectPool) ? ((ParallelObjectPool)_pool.getPool()).getPartitionUse() : NO_POOL;
    }

}

