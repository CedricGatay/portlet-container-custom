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

import java.util.Locale;

/**
 * The <CODE>ChannelState</CODE> class represents
 * the possible window states that a channel window can assume.
 * <P>
 * This class defines a standard set of the most basic channel window states.
 * Additional window states may be defined by calling the constructorof
 * this class.
 */

public class ChannelState {
    /**
     * The standard "one-of many" window state on a page.
     * <p>
     * The string value for this state is "normal".
     */
    public final static ChannelState NORMAL = new ChannelState("normal");
    
    /**
     * In this window state the channel is displayed maximized
     * which means that it is the only channel shown on the page.
     * <p>
     * The string value for this state is "maximized".
     */
    public final static ChannelState MAXIMIZED = new ChannelState("maximized");
    
    /**
     * In this window state the channel is displayed minimzed
     * which means that only the channel title is shown
     * <p>
     * The string value for this state is "minimized".
     */
    public final static ChannelState MINIMIZED = new ChannelState("minimized");
    
    
    
    private String name;
    
    private ChannelState() {
    };
    
    /**
     * Creates a new window state with the given name.<br>
     * Upper case letters in the name will be converted to
     * lower case letters.
     *
     * @param name The name of the channel mode
     */
    public ChannelState(String name) {
        if (name==null) {
            throw new IllegalArgumentException("WindowState name can not be NULL");
        }
        this.name = name.toLowerCase(Locale.ENGLISH);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public boolean equals(Object object) {
        if(object != null && (object instanceof ChannelState)){
            return this.name.equals(((ChannelState) object).name);
        }else{
            return false;
        }
    }
}

