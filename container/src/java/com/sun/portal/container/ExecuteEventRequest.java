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
 * A <code>ExecuteEventRequest</code> encapsulates the event request sent by the
 * aggregation engine to the container.
 **/
public interface ExecuteEventRequest extends ContainerRequest {

    /**
     * Returns a <code>Map</code> of event parameters, or <code>null</code>
     * if there is no event parameter.  These are parameters
     * that are used during eventing.
     *
     * @return  the event parameter map
     **/
    public Map<String, String[]> getEventParameters();

    /**
     * Sets the event parameters map.
     *
     * @param  eventParameters   the map that holds the event parameters
     **/
    public void setEventParameters( Map<String, String[]> eventParameters );
}
