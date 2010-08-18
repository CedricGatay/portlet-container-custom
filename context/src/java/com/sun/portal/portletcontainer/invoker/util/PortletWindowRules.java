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


package com.sun.portal.portletcontainer.invoker.util;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class PortletWindowRules  {
    
    /**
     * In this window state only the portlet window titlebar is shown
     */
    public final static int MINIMIZE = 1;
    
    /**
     * In this window state the portlet window is displayed normal.
     */
    public final static int NORMAL = 2;
    
    /**
     * In this window state the portlet window is displayed maximized
     */
    public final static int MAXIMIZE = 3;
    
    /**
     * Window state not defined.
     */
    public final static int NOT_DEFINED = -1;
    
    
    private static final Map windowStateMap = getWindowStateMap();
    private static final Map windowStateReverseMap = getWindowStateReverseMap();
    
    
    private static List portletWindowModeALL = new ArrayList();
    private static List portletWindowModeV = new ArrayList();
    private static List windowStateMN = new ArrayList();
    private static List windowStateALL = new ArrayList();
    
    static {
        portletWindowModeALL.add(ChannelMode.VIEW);
        portletWindowModeALL.add(ChannelMode.EDIT);
        portletWindowModeALL.add(ChannelMode.HELP);
        
        portletWindowModeV.add(ChannelMode.VIEW);
        
        // A list containing the MAX window state, to be used as
        // allowable window state when in HELP or EDIT mode.
        windowStateMN.add(ChannelState.MAXIMIZED);
        windowStateMN.add(ChannelState.NORMAL);
        
        windowStateALL.add(ChannelState.NORMAL);
        windowStateALL.add(ChannelState.MAXIMIZED);
        windowStateALL.add(ChannelState.MINIMIZED);
        
    }
    
    private static Map getWindowStateMap() {
        Map windowStateMap = new HashMap();
        
        windowStateMap.put(
                new Integer(MINIMIZE),
                ChannelState.MINIMIZED);
        windowStateMap.put(
                new Integer(MAXIMIZE),
                ChannelState.MAXIMIZED);
        windowStateMap.put(
                new Integer(NORMAL),
                ChannelState.NORMAL);
        return windowStateMap;
        
    }
    
    private static Map getWindowStateReverseMap() {
        
        Map windowStateReverseMap = new HashMap();
        windowStateReverseMap.put(
                ChannelState.MINIMIZED,
                new Integer(MINIMIZE));
        windowStateReverseMap.put(
                ChannelState.MAXIMIZED,
                new Integer(MAXIMIZE));
        windowStateReverseMap.put(
                ChannelState.NORMAL,
                new Integer(NORMAL));
        
        return windowStateReverseMap;
    }
    
    
    
    public static List getAllowablePortletWindowModes(ChannelMode mode,
            boolean authless) {
        
        
        if ( mode == null) {
            throw new IllegalArgumentException(
                    "mode passed is null in getAllowablePortletWindowModes.");
        }
        if(authless){
            if ( mode.equals(ChannelMode.VIEW)) {
                //
                // Authless user can't change to any other mode.
                //
                return portletWindowModeV;
            } else if ( mode.equals(ChannelMode.EDIT)) {
                //
                // should never be here, as authless is not allowed
                // to be in edit mode.
                throw new AssertionError(
                        "Authless should not be in edit mode ever.");
            }
        }
        // All portlet modes are allowed if the not authless
        return portletWindowModeALL;
    }
    
    
    public static List getDefaultAllowableWindowStates(ChannelMode mode) {
        
        if ( mode == null) {
            throw new RuntimeException(
                    "mode passed is null in getAllowableWindowStates.");
        }
        return windowStateALL;
        
    }
    
    
    /**
     * Validate window state for the portlet window Mode passed in. For Edit and
     * Help mode, window state minimized is not allowed.
     */
    public static boolean validateWindowStateChange(ChannelMode portletWindowMode,
            ChannelState windowState) {
        boolean validState = true;
        
        if ( portletWindowMode == null || windowState == null) {
            return true;
        }
        
        return validState;
    }
    
    /**
     * Convert the window state representation used by the provider layer to
     * the representation understood by the container layer
     */
    public static List mapToStandards(int[] portalWindowStates) {
        List newList = new ArrayList();
        for (int i=0; portalWindowStates!= null &&
                i < portalWindowStates.length; i++) {
            ChannelState winState = (ChannelState)windowStateMap.get(
                    new Integer(portalWindowStates[i]));
            if ( winState != null) {
                newList.add(winState);
            }
        }
        return newList;
        
    }
    /**
     * Convert the window state representation used by the provider layer to
     * the representation understood by the container layer
     */
    public static ChannelState mapToStandards(int portalWindowState) {
        ChannelState winState = (ChannelState)windowStateMap.get(
                new Integer(portalWindowState));
        return winState;
    }
    
    /**
     * Convert the window state representation used by the container layer to
     * the representation understood by the provider layer
     */
    public static int mapToPortletWindow(ChannelState windowState) {
        int winState = ((Integer)windowStateReverseMap.get(
                windowState)).intValue();
        return winState;
    }
    
    
    public static ChannelState getDefaultWindowState(ChannelMode portletWindowMode) {
        if ( ChannelMode.VIEW.equals(portletWindowMode)) {
            return ChannelState.NORMAL;
        } else if ( ChannelMode.HELP.equals(portletWindowMode)) {
            return ChannelState.MAXIMIZED;
        }
        if ( ChannelMode.EDIT.equals(portletWindowMode)) {
            return ChannelState.MAXIMIZED;
        }
        return ChannelState.NORMAL;
    }
}
