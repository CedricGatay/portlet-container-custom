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

import com.sun.portal.portletcontainer.appengine.util.concurrent.LockWithMemory;

public class ReusableThread implements Runnable, PartitionObject {
    private ThreadPool     ownerPool;
    private Thread         thread;
    private boolean        running   = true;
    private LockWithMemory begin     = new LockWithMemory();
    private LockWithMemory end       = new LockWithMemory();
    private Runnable       runnable  = null;
    private boolean        executing = false;
    private int            partition;
    private boolean        reuse     = true;


    public ReusableThread() {
        this(null);
    }

    ReusableThread(ThreadPool ownerPool) {
        this.ownerPool = ownerPool;
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    void setReuseThread(boolean b) {
        reuse = b;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public int getPartition() {
        return this.partition;
    }

    void setRunnable(Runnable runnable) {
        this.runnable = runnable;
        executing = false;
    }

    public void start() {
        begin.signal();
        executing = true;
    }

    public void join() {
        join(0,0);
    }

    public void join(long timeout) {
        join(timeout,0);
    }

    public void join(long timeout,int nanos) {
        if (this.runnable!=null) {
            end.waitFor(timeout,nanos);
        }
    }

    public boolean isAlive() {
        return executing;
    }

    void finish() {
        running = false;
        begin.signal();
    }

    public void run() {
        try {
            while (running) {
                try {
                    begin.waitFor();
                    if (running) {
                        try {
                            if (this.runnable!=null) {
                                this.runnable.run();
                            }
                        }
                        catch (Throwable ex1) {
                            System.out.println("ReusableThread.run(), ex1: "+ex1);
                        }
                        running = reuse;
                        end.signal();
                        executing = false;
                        if (this.ownerPool!=null) {
                            this.ownerPool.releaseThread(this);
                        }
                    }
                }
                catch (Exception ex2) {
                    System.out.println("ReusableThread.run(), ex2: "+ex2);
                }
            }
        }
        catch (Exception ex3) {
            System.out.println("ReusableThread.run(), ex3: "+ex3);
        }
    }

}

