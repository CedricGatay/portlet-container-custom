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

package com.sun.portal.portletcontainer.appengine.filter;

import com.sun.portal.portletcontainer.common.descriptor.FilterDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.InitParamDescriptor;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.portlet.PortletContext;
import javax.portlet.filter.FilterConfig;

/**
 *
 */
public class FilterConfigImpl implements FilterConfig {
    
    private PortletContext context;
    private FilterDescriptor filterDescriptor;
    private Map<String, String> initParamMap = null;
    
    public FilterConfigImpl(PortletContext context, FilterDescriptor filterDescriptor) {
        this.context = context;
        this.filterDescriptor = filterDescriptor;
        initParamsMap();
    }

    public PortletContext getPortletContext() {
        return context;
    }

    public Enumeration getInitParameterNames() {
        HashMap map = new HashMap(initParamMap);
        return Collections.enumeration(map.keySet());
    }

    public String getFilterName() {
        return filterDescriptor.getFilterName();
    }

    public String getInitParameter(String name) {
        return initParamMap.get(name);
    }
    
    /**
     * construct an internal map to store the init parameters
     */
    private void initParamsMap() {

	if (this.filterDescriptor.getInitParamDescriptors() != null) {
            initParamMap = new HashMap(this.filterDescriptor.getInitParamDescriptors().size());
            for (InitParamDescriptor ipd : this.filterDescriptor.getInitParamDescriptors()) {
		initParamMap.put(ipd.getParamName(), ipd.getParamValue());
	    }
	}else{
            initParamMap = Collections.emptyMap();
        }
    }

}
