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

package com.sun.portal.container;

import java.util.Map;

/**
 * A <code>ExecuteActionRequest</code> encapsulates the action request sent by the
 * aggregation engine to the container.
 **/
public interface ExecuteActionRequest extends ContainerRequest {

    /**
     * Set the action parameters map.
     *
     * @param  actionParameters    the map that holds the action parameters
     **/
    public void setActionParameters( Map<String, String[]> actionParameters );
    
    /**
     * Returns a <code>Map</code> of action parameters, or <code>null</code>
     * if there is no action parameter.  These are parameters
     * for executing an action.
     *
     * @return  the action parameter map
     **/
    public Map<String, String[]> getActionParameters();

    /**
     * Sets the WindowRequestReader 
     *
     * @param  windowRequestReader   the WindowRequestReader impl to be used. 
     */
    public void setWindowRequestReader(WindowRequestReader windowRequestReader);
    
    /**
     * Returns the instance of WindowRequestReader used by the 
     * caller to help container manage parameters
     *
     * @return instance of WindowRequestReader used by caller 
     **/
    public WindowRequestReader getWindowRequestReader();
}
