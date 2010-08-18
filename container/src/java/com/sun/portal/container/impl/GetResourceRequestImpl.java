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

import com.sun.portal.container.GetResourceRequest;
import com.sun.portal.container.WindowRequestReader;

import java.util.Map;

/**
 * This class implements GetResourceRequest interface
 **/
public class GetResourceRequestImpl extends ContainerRequestImpl implements GetResourceRequest {
    
    private Map<String, String[]> resourceParameters;
    private String resourceID;
    private String cacheLevel;
    private String eTag;
    private WindowRequestReader windowRequestReader;
    
    public Map<String, String[]> getResourceParameters() {
        return this.resourceParameters;
    }
    
    public void setResourceParameters( Map<String, String[]> resourceParameters ) {
        this.resourceParameters = resourceParameters;
    }
    
    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }
    
    public String getResourceID() {
        return resourceID;
    }
    
    public void setCacheLevel(String cacheLevel) {
        this.cacheLevel = cacheLevel;
    }
    
    public String getCacheLevel() {
        return cacheLevel;
    }
    
    public String getETag() {
        return eTag;
    }
    
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public void setWindowRequestReader(WindowRequestReader windowRequestReader) {
        this.windowRequestReader = windowRequestReader;
    }
    
    public WindowRequestReader getWindowRequestReader(){
        return this.windowRequestReader;
    }
}
