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
 * The <code>PortletType</code> class represents the possible portlet types.
 * <P>
 * This class defines a standard set of the portlet types.
 * Additional portlet types may be defined by calling the constructor
 * of this class.
 */
public class PortletType {
    /**
     * This represents the ALL portlets
     * <p>
     * The string value for this type is "ALL".
     */
    public final static PortletType ALL = new PortletType("ALL");
    
    /**
     * This represents only local portlets.
     * <p>
     * The string value for this mode is "LOCAL".
     */
    public final static PortletType LOCAL = new PortletType("LOCAL");
    
    /**
     * This represents only the remote portlets.
     * <p>
     * The string value for this mode is "REMOTE".
     */
    public final static PortletType REMOTE = new PortletType("REMOTE");
    
    private String name;
    
    private PortletType() {
    }
    
    /**
     * Creates a new channel mode with the given name.<br>
     * Lower case letters in the name will be converted to
     * upper case letters.
     *
     * @param name The name of the channel mode
     */
    public PortletType(String name) {
        if (name == null) {
            throw new IllegalArgumentException("PortletType name can not be NULL");
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
            return this.name.equals(((PortletType)object).name);
        }else{
            return false;
        }
    }
}
