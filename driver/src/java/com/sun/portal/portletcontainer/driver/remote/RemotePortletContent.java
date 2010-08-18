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

package com.sun.portal.portletcontainer.driver.remote;

import com.sun.portal.portletcontainer.context.registry.PortletRegistryContext;
import com.sun.portal.portletcontainer.driver.PortletContent;
import com.sun.portal.portletcontainer.invoker.InvokerException;
import com.sun.portal.portletcontainer.invoker.WindowInvoker;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RemotePortletContent is responsible for getting the portlet content from
 * remote portlet and execting action on these portlet. It delegates the calls to
 * WSRPWindowProvider.
 */
public class RemotePortletContent extends PortletContent{

    static final long serialVersionUID = 2L;
    public RemotePortletContent(ServletContext context, HttpServletRequest request,
            HttpServletResponse response) throws InvokerException {
        super(context, request, response);
    }

    protected WindowInvoker getWindowInvoker(ServletContext context,
            HttpServletRequest request,
            HttpServletResponse response)
            throws InvokerException  {
        WindowInvoker windowInvoker = null;
        try{
            Class invokerClass = Class.forName("com.sun.portal.wsrp.consumer.wsrpinvoker.WSRPWindowProvider");
            if(invokerClass!=null){
                windowInvoker = (WindowInvoker)(invokerClass.newInstance());
                windowInvoker.init(context, request, response);
            }
        }catch(Exception e){
            throw new InvokerException("WSRP is not available");
        }
        return windowInvoker;
    }


}
