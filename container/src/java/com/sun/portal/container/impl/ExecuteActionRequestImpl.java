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
 * [Name of File] [ver.__] [Date]
 * 
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
*/

package com.sun.portal.container.impl;

import com.sun.portal.container.ExecuteActionRequest;
import com.sun.portal.container.WindowRequestReader;

import java.util.Map;

public class ExecuteActionRequestImpl extends ContainerRequestImpl implements ExecuteActionRequest {

    private Map<String, String[]> actionParameters;
    private WindowRequestReader windowRequestReader;

    public void setActionParameters( Map<String, String[]> actionParameters ) {
        this.actionParameters = actionParameters;
    }

    public Map<String, String[]> getActionParameters() {
        return actionParameters;
    }

    public void setWindowRequestReader(WindowRequestReader windowRequestReader) {
        this.windowRequestReader = windowRequestReader;
    }
    
    public WindowRequestReader getWindowRequestReader(){
        return this.windowRequestReader;
    }
}

