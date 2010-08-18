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

public class ObjectPool {

    private Pool pool;

    // maxSize = 0 --> NullObjectPool (create objects with every obtain)
    // partitions = 1 --> SimpleObjectPool (a single internal pool, all obtains are synch on the same monitor)
    // partitions > 1 --> ParallelObjectPool (#partitions# of internal pool, each partition has its own monitor)
    //                    partitions should be prime number (3,5,7,11,13,17,19,....)

    public ObjectPool(ObjectManager objectManager,int minSize,int maxSize,boolean overflow,int partitions) {
        if (maxSize==0) {
            pool = new NullObjectPool(objectManager);
        }
        else
        if (partitions==1) {
            pool = new SimpleObjectPool(objectManager,minSize,maxSize,overflow);
        }
        else {
            pool = new ParallelObjectPool(objectManager,minSize,maxSize,overflow,partitions);
        }
    }

    public Pool getPool() {
        return pool;
    }

}

