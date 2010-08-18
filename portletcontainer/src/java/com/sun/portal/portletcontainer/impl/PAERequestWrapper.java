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
 

package com.sun.portal.portletcontainer.impl;


import com.sun.portal.portletcontainer.portlet.impl.PortletRequestConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * The <code>PAERequestWrapper</code> is for wrapper the original
 * HttpServletRequest before passing it to the RequestDispatcher.include().
 * A wrapper is needed because the <code>PortletContainer</code> and the
 * PortletApplicationEngine communicate with each other by setting the
 * <code>PortletContainerRequest</code> and <code>PortletContainerResponse</code>
 * to the request atributes.  Without a wrapper, this mechanism for
 * communicating does not work under multi-threaded environment since
 * each RequestDispatcher.include() call overwrites the attributes of the
 * previous call.  This wrapper only wraps the <code>getAttribute()</code>
 * and <code>setAttribute()</code> methods.
 **/
public class PAERequestWrapper extends HttpServletRequestWrapper {

	private String namespace;
	private List<String> requestSharedAttributes;

	static Set<String> reservedAttributes = new HashSet<String>();

	static {
		reservedAttributes.add(PortletRequestConstants.PORTLET_REQUEST_ATTRIBUTE);
		reservedAttributes.add(PortletRequestConstants.PORTLET_RESPONSE_ATTRIBUTE);
		reservedAttributes.add(PortletRequestConstants.PORTLET_CONFIG_ATTRIBUTE);
		reservedAttributes.add(PortletRequest.LIFECYCLE_PHASE);
	}

    public PAERequestWrapper (HttpServletRequest request, String namespace) {
        super(request);
        this.namespace = namespace;
    }

    @Override
    public Object getAttribute(String name) {
		Object value = super.getAttribute(this.namespace + name);

		if (value == null) {
			value = super.getAttribute(name);
		}

		return value;
    }

    @Override
    public void setAttribute(String name, Object value) {
		if (isReservedParameter(name)) {
			super.setAttribute(name, value);
		}
		else {
			super.setAttribute(this.namespace + name, value);
		}
    }

	@Override
	public Enumeration<String> getAttributeNames() {
		List<String> names = new ArrayList<String>();

		Enumeration<String> attributeNames = super.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			String name = attributeNames.nextElement();

			if (name.startsWith(this.namespace)) {
				names.add(name.substring(this.namespace.length(), name.length()));
			}
		}

		return Collections.enumeration(names);
	}

	@Override
	public void removeAttribute(String name) {
		if (isReservedParameter(name)) {
			super.removeAttribute(name);
		}
		else {
			super.removeAttribute(this.namespace + name);
		}
	}

	protected void setRequestSharedAttributes(List<String> requestSharedAttributes) {
		this.requestSharedAttributes = requestSharedAttributes;
	}

	private boolean isReservedParameter(String name) {
		if (reservedAttributes.contains(name)) {
			return true;
			
		} else if (this.requestSharedAttributes != null) {
			for (String requestSharedAttribute : requestSharedAttributes) {
				if (name.startsWith(requestSharedAttribute)) {
					return true;
				}
			}
		}

		return false;
	}
}
