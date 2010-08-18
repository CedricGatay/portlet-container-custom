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

package com.sun.portal.portletcontainer.driver;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryContext;
import com.sun.portal.portletcontainer.admin.registry.PortletRegistryConstants;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import com.sun.portal.portletcontainer.invoker.WindowInvokerConstants;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;

/**
 * PortletWindowDataImpl provides concrete implementation of PortletWindowData interface
 */
public class PortletWindowDataImpl implements PortletWindowData, Comparable, Serializable {

    public static final long serialVersionUID = 1L;
    private String requestURL;
    private String portletWindowName;
    private String portletName;
    private String title;
    private StringBuffer content;
    private boolean view;
    private boolean edit;
    private boolean help;
    private Integer rowNumber;
    private String width;
    private String currentMode;
    private String currentWindowState;
    
    public PortletWindowDataImpl() {
    }
    
    public void init(HttpServletRequest request, PortletRegistryContext portletRegistryContext,
            String portletWindowName) throws PortletRegistryException {
        String portletNameTemp = portletRegistryContext.getPortletName(portletWindowName);
        setPortletName(portletNameTemp);
        setPortletWindowName(portletWindowName);
        setRequestURL(request.getRequestURL());
        setView(portletRegistryContext.hasView(portletNameTemp));
        setEdit(portletRegistryContext.hasEdit(portletNameTemp));
        setHelp(portletRegistryContext.hasHelp(portletNameTemp));
        setRowNumber(portletRegistryContext.getRowNumber(portletWindowName));
        setWidth(portletRegistryContext.getWidth(portletWindowName));
    }
    
    public void setPortletName(String portletName){
        this.portletName = portletName;        
    }
    
    public String getPortletName(){
        return portletName;
    }
    
    public void setPortletWindowName(String portletWindowName){
        this.portletWindowName = portletWindowName;        
    }

    public void setRequestURL(StringBuffer requestURL){
        this.requestURL = requestURL.toString();        
    }
    
    public String getRequestURL(){
        return requestURL;
    }

    
    public String getPortletWindowName() {
        return this.portletWindowName;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public StringBuffer getContent() {
        return this.content;
    }
    
    public void setContent(StringBuffer content) {
        this.content = content;
    }
    
    public boolean isView() {
        return this.view;
    }
    
    protected void setView(boolean view) {
        this.view = view;
    }
    
    public String getViewURL() {
        return getPortletModeURL(ChannelMode.VIEW.toString());
    }
    
    public boolean isEdit() {
        return this.edit;
    }
    
    protected void setEdit(boolean edit) {
        this.edit = edit;
    }
    
    public String getEditURL() {
        return getPortletModeURL(ChannelMode.EDIT.toString());
    }
    
    public boolean isHelp() {
        return this.help;
    }
    
    protected void setHelp(boolean help) {
        this.help = help;
    }
    
    public String getHelpURL() {
        return getPortletModeURL(ChannelMode.HELP.toString());
    }
    
    public boolean isNormalized() {
        if(ChannelState.NORMAL.toString().equals(getCurrentWindowState()))
            return true;
        return false;
    }

	@Deprecated
    public String getNormalizedURL() {
        return getPortletWindowStateURL(ChannelState.NORMAL.toString());
    }

    public String getUnMinimizedURL() {
        return getPortletWindowStateURL(DesktopConstants.CHANNEL_STATE_UNMINIMIZE.toString());
	}

    public String getUnMaximizedURL() {
        return getPortletWindowStateURL(DesktopConstants.CHANNEL_STATE_UNMAXIMIZE.toString());
	}

    public boolean isMaximized() {
        if(ChannelState.MAXIMIZED.toString().equals(getCurrentWindowState()))
            return true;
        return false;
    }
    
    public String getMaximizedURL() {
        return getPortletWindowStateURL(ChannelState.MAXIMIZED.toString());
    }
    
    public boolean isMinimized() {
        if(ChannelState.MINIMIZED.toString().equals(getCurrentWindowState()))
            return true;
        return false;
    }
    
    public String getMinimizedURL() {
        return getPortletWindowStateURL(ChannelState.MINIMIZED.toString());
    }
    
    public String getCurrentMode() {
        return this.currentMode;
    }
    
    public void setCurrentMode(ChannelMode currentMode) {
        if(currentMode == null) {
            this.currentMode = ChannelMode.VIEW.toString();
        } else {
            this.currentMode = currentMode.toString();
        }
    }
    
    public String getCurrentWindowState() {
        return this.currentWindowState;
    }
    
    public void setCurrentWindowState(ChannelState currentWindowState) {
        if(currentWindowState == null) {
            this.currentWindowState = ChannelState.NORMAL.toString();
        } else {
            this.currentWindowState = currentWindowState.toString();
        }
    }
    
    public String getRemoveURL() {
        StringBuffer processURL = new StringBuffer(getRequestURL());
        
        processURL.append("?").append(WindowInvokerConstants.DRIVER_ACTION).append("=").append(WindowInvokerConstants.RENDER)
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_MODE_KEY).append("=").append(getCurrentMode())
        .append("&").append(WindowInvokerConstants.PORTLET_REMOVE_KEY).append("=").append("true")
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_KEY).append("=").append(getPortletWindowName());
        return processURL.toString();
    }
    
    protected void setRowNumber(Integer rowNumber){
        this.rowNumber = rowNumber;
    }
    
    protected Integer getRowNumber() {
        return this.rowNumber;
    }
    
    protected void setWidth(String width){
        this.width = width;
    }
    
    public boolean isThin() {
        if(width != null && width.equals(PortletRegistryConstants.WIDTH_THIN))
            return true;
        return false;
    }
    
    public boolean isThick() {
        if(width != null && width.equals(PortletRegistryConstants.WIDTH_THICK))
            return true;
        return false;
    }
    
    private String getPortletModeURL(String portletMode) {
        StringBuffer processURL = new StringBuffer(getRequestURL());
        
        processURL.append("?").append(WindowInvokerConstants.DRIVER_ACTION).append("=").append(WindowInvokerConstants.RENDER)
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_MODE_KEY).append("=").append(portletMode)
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_STATE_KEY).append("=").append(getCurrentWindowState())
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_KEY).append("=").append(getPortletWindowName());
        return processURL.toString();
    }
    
    private String getPortletWindowStateURL(String portletWindowState) {
        StringBuffer processURL = new StringBuffer(getRequestURL());
        
        processURL.append("?").append(WindowInvokerConstants.DRIVER_ACTION).append("=").append(WindowInvokerConstants.RENDER)
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_MODE_KEY).append("=").append(getCurrentMode())
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_STATE_KEY).append("=").append(portletWindowState)
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_KEY).append("=").append(getPortletWindowName());
        return processURL.toString();
    }
    
    public int compareTo(Object o) {
        Integer otherRowNumber = ((PortletWindowDataImpl)o).getRowNumber();
        int value = getRowNumber().compareTo(otherRowNumber);
        return value;
    }
   
    public boolean equals(Object o){
        return super.equals(o);
    }

	public int hashCode() {
		assert false : "hashCode not designed";
		return 42; // any arbitrary constant will do
   }

}
