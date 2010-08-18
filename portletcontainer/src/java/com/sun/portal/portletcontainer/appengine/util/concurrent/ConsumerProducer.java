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

import java.util.List;
import java.util.ArrayList;

public class ConsumerProducer {
    
    public static interface Threshold {
        
        public void reachedStopLimit();
        
        public void reachedResumeLimit();
        
    };
    
    private List      queue;
    private int       stopLimit   = 0;
    private int       resumeLimit = 0;
    private Threshold threshold   = null;
    private boolean   reachedLimit = false;
    
    
    public ConsumerProducer() {
        this(0,0,null);
    }
    
    public ConsumerProducer(int stopLimit,int resumeLimit,Threshold threshold) {
        this.stopLimit   = stopLimit;
        this.resumeLimit = resumeLimit;
        this.threshold   = threshold;
        queue = new ArrayList(100);
    }
    
    public synchronized void put(Object o) {
        queue.add(o);
        int size = queue.size();
        if (size==1) {
            notify();
        }
        if (this.threshold!=null && this.stopLimit>0 && size>this.stopLimit) {
            this.threshold.reachedStopLimit();
            reachedLimit = true;
        }
        
    }
    
    public synchronized Object get() {
        int size = queue.size();
        if (size==0)
            try {
                wait();
            } catch (Exception e) {
            }
        
        if (reachedLimit && size<this.resumeLimit) {
            this.threshold.reachedResumeLimit();
            reachedLimit = false;
        }
        
        Object o = queue.get(0);
        queue.remove(0);
        return o;
    }
    
    public boolean isEmpty() {
        return queue.size()==0;
    }
    
    public synchronized void empty() {
        queue.clear();
        reachedLimit = false;
    }
    
}
