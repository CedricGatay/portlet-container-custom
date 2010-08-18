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

package com.sun.portal.container;

import java.util.Locale;

/**
 * The ChannelURLType class represents the possible types of Channel URL.
 * <P>
 * This class defines a standard set of the Channel URL types.
 * Additional Channel URL types may be defined by calling the constructor
 * of this class.
 */
public class ChannelURLType {
    /**
     * This represents the Action URL type
     * <p>
     * The string value for this type is "ACTION".
     */
    public final static ChannelURLType ACTION = new ChannelURLType("ACTION");
    
    /**
     * This represents the Render URL type
     * <p>
     * The string value for this mode is "RENDER".
     */
    public final static ChannelURLType RENDER = new ChannelURLType("RENDER");
    
    /**
     * This represents the Resource URL type
     * <p>
     * The string value for this mode is "RESOURCE".
     */
    public final static ChannelURLType RESOURCE = new ChannelURLType("RESOURCE");
    
    private String name;
    
    private ChannelURLType() {
    }
    
    /**
     * Creates a new channel mode with the given name.<br>
     * Lower case letters in the name will be converted to
     * upper case letters.
     *
     * @param name The name of the channel mode
     */
    public ChannelURLType(String name) {
        if (name == null) {
            throw new IllegalArgumentException("ChannelURLType name can not be NULL");
        }
        this.name = name.toUpperCase(Locale.ENGLISH);
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
        if(object != null){
            return this.name.equals(((ChannelURLType)object).name);
        }else{
            return false;
        }
    }
}
