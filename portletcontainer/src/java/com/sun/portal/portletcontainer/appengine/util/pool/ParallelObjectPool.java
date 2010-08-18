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

import java.util.Random;

public class ParallelObjectPool implements Pool {
    private int           minSize;
    private int           maxSize;
    private int           partitions;
    private Pool[]        pools;
    private boolean       destroyed;
    private int[]        partitionUse;
    private Random        random;
    
    public ParallelObjectPool(ObjectManager objectManager,int minSize,int maxSize,boolean overflow,int partitions) {
        if (partitions<1) {
            throw new IllegalArgumentException("ParallelObjectPool.<init>: partitions<1");
        }
        this.minSize    = minSize;
        this.maxSize    = maxSize;
        this.partitions = partitions;
        pools      = new Pool[partitions];
        
        int partitionMinSize = minSize / partitions;
        int partitionMaxSize = maxSize / partitions;
        int partitionMinRest = minSize % partitions;
        int partitionMaxRest = maxSize % partitions;
        int min = partitionMinSize + (((partitionMinRest--)>0) ? 1 : 0);
        int max = partitionMaxSize + (((partitionMaxRest--)>0) ? 1 : 0);
        for (int i=0;i<partitions;i++) {
            pools[i] = new SimpleObjectPool(objectManager,min,max,overflow);
            min = partitionMinSize + (((partitionMinRest--)>0) ? 1 : 0);
            max = partitionMaxSize + (((partitionMaxRest--)>0) ? 1 : 0);
        }
        partitionUse = new int[partitions];
        random = new Random();
    }
    
    private int calculatePartition() {
        return (this.partitions==1) ? 0 : (int) (Math.abs(random.nextInt()) % this.partitions);
    }
    
    public Object obtainObject(Object param) {
        if (destroyed) {
            throw new IllegalStateException();
        }
        int partition = calculatePartition();
        Pool pool = pools[partition];
        Object o = pool.obtainObject(param);
        if (o==null) {
            for (int i=0;o==null && i<this.partitions;i++) {
                if (i!=partition) {
                    pool = pools[i];
                    o = pool.obtainObject(param);
                    if (o!=null) {
                        partition = i;
                    }
                }
            }
        }
        if (o!=null && (o instanceof PartitionObject)) {
            ((PartitionObject)o).setPartition(partition);
            partitionUse[partition]++;
        }
        return o;
    }
    
    public void releaseObject(Object o) {
        int partition;
        if (o instanceof PartitionObject) {
            partition = ((PartitionObject)o).getPartition();
        } else {
            partition = calculatePartition();
        }
        pools[partition].releaseObject(o);
    }
    
    public int getLeased() {
        int leased = 0;
        for (int i=0;i<pools.length;i++) {
            leased += pools[i].getLeased();
        }
        return leased;
    }
    
    public int getMinSize() {
        return this.minSize;
    }
    
    public int getMaxSize() {
        return this.maxSize;
    }
    
    public int[] getPartitionUse() {
        return (int[]) partitionUse.clone();
    }
    
    
    public void setMaxSize(int maxSize) {
        synchronized (this) {
            if (destroyed) {
                throw new IllegalStateException();
            }
            int partitionMaxSize = maxSize / this.partitions;
            int partitionMaxRest = maxSize % this.partitions;
            int max = partitionMaxSize + (((partitionMaxRest--)>0) ? 1 : 0);
            for (int i=0;i<this.partitions;i++) {
                pools[i].setMaxSize(max);
                max = partitionMaxSize + (((partitionMaxRest--)>0) ? 1 : 0);
            }
        }
    }
    
    public void destroy() {
        synchronized (this) {
            if (destroyed) {
                throw new IllegalStateException();
            }
            destroyed = true;
            for (int i=0;i<this.partitions;i++) {
                pools[i].destroy();
            }
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

