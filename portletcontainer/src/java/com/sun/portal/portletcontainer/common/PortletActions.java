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
 

package com.sun.portal.portletcontainer.common;

import java.util.ArrayList;
import java.util.List;

/**
  * The PortletActions class holds the types of action and phase.
  */
public class PortletActions {

    public static final String ACTION = "ACTION";
    public static final String RENDER = "RENDER";
    public static final String EVENT = "EVENT";
    public static final String RESOURCE = "RESOURCE";
    public static final String ACTION_PHASE = "ACTION_PHASE";
    public static final String RENDER_PHASE = "RENDER_PHASE";
    public static final String EVENT_PHASE = "EVENT_PHASE";
    public static final String RESOURCE_PHASE = "RESOURCE_PHASE";
    
    private static final List<String> LIFECYCLE_NAMES = new ArrayList<String>(4);
    private static final List<String> LIFECYCLE_PHASES = new ArrayList<String>(4);
    
    static{
        LIFECYCLE_NAMES.add(ACTION);
        LIFECYCLE_NAMES.add(RENDER);
        LIFECYCLE_NAMES.add(EVENT);
        LIFECYCLE_NAMES.add(RESOURCE);
        //Possible phases
        LIFECYCLE_PHASES.add(ACTION_PHASE);
        LIFECYCLE_PHASES.add(RENDER_PHASE);
        LIFECYCLE_PHASES.add(EVENT_PHASE);
        LIFECYCLE_PHASES.add(RESOURCE_PHASE);
    }
    
    public static boolean isValidLifecycleName(String lifecycle){
        return LIFECYCLE_NAMES.contains(lifecycle);
    }

    public static boolean isValidLifecyclePhase(String lifecycle){
        return LIFECYCLE_PHASES.contains(lifecycle);
    }
}
