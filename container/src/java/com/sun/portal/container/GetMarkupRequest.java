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
 * A <code>GetMarkupRequest</code> encapsulates the render request sent by the
 * aggregation engine to the container.
 **/
public interface GetMarkupRequest extends ContainerRequest {

    /**
     * Sets whether the current channel is the target channel of the action in
     * this user request.
     *
     * @param  isTarget   true if the current channel is the target channel
     */
    public void setIsTarget( boolean isTarget );

    /**
     * Returns whether the current channel is the target channel of the action
     * in this user request.
     *
     * @return  true if the current channel is the target channel
     **/
    public boolean getIsTarget();

    /**
     * Returns a <code>Map</code> of render parameters, or <code>null</code>
     * if there is no render parameter.  These are parameters
     * that are used for content generation.
     *
     * @return  the render parameter map
     **/
    public Map<String, String[]> getRenderParameters();

    /**
     * Sets the render parameters map.
     *
     * @param  renderParameters   the map that holds the render parameters
     **/
    public void setRenderParameters( Map<String, String[]> renderParameters );

    /** Returns the ETag as a <code>String</code>
     * 
     *  @return the ETag
     */
    public String getETag();    
    
    /** Sets the ETag value
     * 
     *  @param eTag <code>String</code> containing the ETag value
     */
    public void setETag(String eTag);
}

