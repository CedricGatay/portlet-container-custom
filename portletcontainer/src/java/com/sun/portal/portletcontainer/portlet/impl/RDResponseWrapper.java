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

package com.sun.portal.portletcontainer.portlet.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.portlet.ActionResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * RDResponseWrapper is a wrapped HttpServletResponse object that has methods
 * common to all Portlet Response Wrapper Objects.
 */
public class RDResponseWrapper extends HttpServletResponseWrapper {

	private HttpServletResponse response;
	private PortletResponse portletResponse;
    private boolean isInclude;
	private String lifecyclePhase;
    private PSServletOutputStream psServletOutputStream;

    public RDResponseWrapper(HttpServletResponse response,
		PortletResponse portletResponse, String lifecyclePhase,
		boolean isInclude) {

		super(response);
		this.response = response;
		this.portletResponse = portletResponse;
        this.isInclude = isInclude;
		this.lifecyclePhase = lifecyclePhase;
    }
    
	@Override
	public void addCookie(Cookie cookie) {
		if (!isInclude) {
			portletResponse.addProperty(cookie);
		}
	}

	@Override
	public void addDateHeader(String name, long date) {
		addHeader(name, String.valueOf(date));
	}

	@Override
	public void addHeader(String name, String value) {
		if (!isInclude) {
			if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
				this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

	            portletResponse.addProperty(name, value);
			}
		}
	}

	@Override
	public void addIntHeader(String name, int value) {
		addHeader(name, String.valueOf(value));
	}

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

	@Override
	public String encodeURL(String url) {
		return portletResponse.encodeURL(url);
	}

	@Override
	public String encodeUrl(String url) {
		return portletResponse.encodeURL(url);
	}

	@Override
	public void flushBuffer() throws IOException {
		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			((MimeResponse)portletResponse).flushBuffer();
		}
	}

	@Override
	public int getBufferSize() {
		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			return ((MimeResponse)portletResponse).getBufferSize();

		} else {
			return 0;
		}
	}

	@Override
	public String getCharacterEncoding() {
		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			return ((MimeResponse)portletResponse).getCharacterEncoding();

		} else {
			return null;
		}
	}

	@Override
	public String getContentType() {
		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			return ((MimeResponse)portletResponse).getContentType();

		} else {
			return null;
		}
	}

	@Override
	public Locale getLocale() {
		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			return ((MimeResponse)portletResponse).getLocale();

		} else {
			return null;
		}
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {

		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			if (psServletOutputStream == null) {
				psServletOutputStream = new PSServletOutputStream(
					((MimeResponse)portletResponse).getPortletOutputStream());
			}
	        return psServletOutputStream;

		} else {
			return new PSServletOutputStreamIgnored();
		}
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			return ((MimeResponse)portletResponse).getWriter();

		} else {
			return new PrintWriter(new PSServletOutputStreamIgnored());
		}
	}

	@Override
	public boolean isCommitted() {

		if (this.lifecyclePhase.equals(PortletRequest.ACTION_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.EVENT_PHASE)) {

			if(isInclude) {
				return true;
			} else {
				return false;
			}
		} else {
			return ((MimeResponse)portletResponse).isCommitted();
		}
	}

	@Override
	public void reset() {
		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			((MimeResponse)portletResponse).reset();
		}
	}

	@Override
	public void resetBuffer() {
		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			((MimeResponse)portletResponse).resetBuffer();
		}
	}

    @Override
    public void sendError(int sc) throws IOException {
        //no-op
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        //no-op
    }

	@Override
	public void sendRedirect(String location) throws IOException {
		if (!isInclude) {
			if (this.lifecyclePhase.equals(PortletRequest.ACTION_PHASE)) {
				((ActionResponse)portletResponse).sendRedirect(location);
			}
		}
	}

	@Override
	public void setBufferSize(int size) {
		if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
			this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

			((MimeResponse)portletResponse).setBufferSize(size);
		}
	}

	@Override
	public void setCharacterEncoding(String encoding) {
		if (!isInclude) {
			if (this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {
				response.setCharacterEncoding(encoding);
			}
		}
	}

	@Override
	public void setContentLength(int length) {
		if (!isInclude) {
			if (this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {
				response.setContentLength(length);
			}
		}
	}

	@Override
	public void setContentType(String contentType) {
		if (!isInclude) {
			if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
				this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

				((MimeResponse)portletResponse).setContentType(contentType);
			}
		}
	}

	@Override
	public void setDateHeader(String name, long date) {
		setHeader(name, String.valueOf(date));
	}

	@Override
	public void setHeader(String name, String value) {
		if (!isInclude) {
			if (this.lifecyclePhase.equals(PortletRequest.RENDER_PHASE) ||
				this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {

				((MimeResponse)portletResponse).setProperty(name, value);
			}
		}
	}

	@Override
	public void setIntHeader(String name, int value) {
		setHeader(name, String.valueOf(value));
	}

	@Override
	public void setLocale(Locale locale) {
		if (!isInclude) {
			if (this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {
				response.setLocale(locale);
			}
		}
	}

	@Override
	public void setStatus(int sc) {
		if (!isInclude) {
			if (this.lifecyclePhase.equals(PortletRequest.RESOURCE_PHASE)) {
				response.setStatus(sc);
			}
		}
	}

	@Override
	public void setStatus(int sc, String msg) {
		setStatus(sc);
	}
}
