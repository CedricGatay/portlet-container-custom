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
 * A <code>GetResourceRequest</code> encapsulates the resource request sent by the
 * aggregation engine to the container.
 **/
public interface GetResourceRequest extends ContainerRequest {

    /**
     * Set the resource parameters map.
     *
     * @param  resourceParameters    the map that holds the resource parameters
     **/
    public void setResourceParameters( Map<String, String[]> resourceParameters  );
    
    /**
     * Returns a <code>Map</code> of resource parameters, or <code>null</code>
     * if there is no resource parameter.  These are parameters
     * for serving a resource.
     *
     * @return  the resource parameter map
     **/
    public Map<String, String[]> getResourceParameters();

    
    /**
     * Sets the resource ID
     * @param resourceID
     **/ 
    public void setResourceID(String resourceID);
    
    /**
     * Gets the resource ID set by <CODE>setResourceID</CODE>
     * @return String resource ID set by <CODE>setResourceID</CODE>
     **/
    public String getResourceID();
    
    /**
     * Sets Cache Level
     * @param cacheLevel
     **/ 
    public void setCacheLevel(String cacheLevel);
    
    /**
     * Gets the cache level set by <CODE>setCacheLevel</CODE>
     * 
     * @return String cache level
     */
    public String getCacheLevel();
    
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

