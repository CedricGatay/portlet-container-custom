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
 * The <CODE>ChannelMode</CODE> class is represents
 * the possible modes that a channel can assume.
 * <P>
 * This class defines a standard set of the most basic channel modes.
 * Additional channel modes may be defined by calling the constructor
 * of this class.
 */
public class ChannelMode {
    /**
     * The standard "one-of many" channel view on a page.
     * <P>
     * This mode must be implemented by the channel.
     * <p>
     * The string value for this mode is "view".
     */
    public final static ChannelMode VIEW = new ChannelMode("view");
    
    /**
     * This mode allows the channel to capture user-specific
     * parameterization, which leads to a personalized view of the
     * channel.
     * <P>
     * This mode may be implemented by the channel.
     * <p>
     * The string value for this mode is "edit".
     */
    public final static ChannelMode EDIT = new ChannelMode("edit");
    
    /**
     * A channel should provide useful online help in this mode.
     * This may be a short description or a multi-page instruction on
     * how to use the channel.
     * <P>
     * This mode may be implemented by the channel.
     * <p>
     * The string value for this mode is "help".
     */
    public final static ChannelMode HELP = new ChannelMode("help");
    
    /**
     * Allows the channel to bring its own configuration
     * screen if required. Only a user with administrator privileges should
     * be able to call a channel in this mode.
     * <P>
     * This mode may be implemented by the channel, but the channel
     * cannot be sure that this mode is called, as the channel
     * container it is running in may not support this mode.
     * <p>
     * The string value for this mode is "config".
     */
    public final static ChannelMode CONFIG = new ChannelMode("config");
    
    
    
    private String name;
    
    private ChannelMode() {
    }
    
    /**
     * Creates a new channel mode with the given name.<br>
     * Upper case letters in the name will be converted to
     * lower case letters.
     *
     * @param name The name of the channel mode
     */
    public ChannelMode(String name) {
        if (name==null) {
            throw new IllegalArgumentException("ChannelMode name can not be NULL");
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
        if(object != null && (object instanceof ChannelMode)){
            return this.name.equals(((ChannelMode)object).name);
        }else{
            return false;
        }
    }
}

