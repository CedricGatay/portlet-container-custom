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

import com.sun.portal.container.ChannelState;

/**
 * DesktopConstants defines constants that are used in the DesktopServlet.
 */
public interface DesktopConstants {

    public static String PORTLET_CONTENT = "content";
    public static String PORTLET_TITLE = "title";
    public static String PORTLET_WINDOWS = "com.sun.portal.portletcontainer.driver.portletWindows";

	public static final ChannelState CHANNEL_STATE_UNMINIMIZE = new ChannelState("UNMINIMIZE");
	public static final ChannelState CHANNEL_STATE_UNMAXIMIZE = new ChannelState("UNMAXIMIZE");
   
}
