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

package com.sun.portal.container.service.policy;

import java.util.Locale;

/**
 * The DistributionType class represents the possible event distribution types.
 * <P>
 * This class defines a standard set of the distribution types.
 * Additional distribution types may be defined by calling the constructor
 * of this class.
 */
public class DistributionType {
    
    /**
     * This represents all portlets that is deployed.
     * <p>
     * The string value for this type is "ALL_PORTLETS".
     */
    public final static DistributionType ALL_PORTLETS = new DistributionType("ALL_PORTLETS");
    
    /**
     * This represents all portlets on a page. This includes both visible and hidden portlets.
     * <p>
     * The string value for this mode is "ALL_PORTLETS_ON_PAGE".
     */
    public final static DistributionType ALL_PORTLETS_ON_PAGE = new DistributionType("ALL_PORTLETS_ON_PAGE");
    
    /**
     * This represents all visible portlets on a page.
     * <p>
     * The string value for this mode is "VISIBLE_PORTLETS_ON_PAGE".
     */
    public final static DistributionType VISIBLE_PORTLETS_ON_PAGE = new DistributionType("VISIBLE_PORTLETS_ON_PAGE");
    
    /**
     * This is used in case the events should not be distributed to any portlets.
     * <p>
     * The string value for this mode is "NO_PORTLETS".
     */
    public final static DistributionType NO_PORTLETS = new DistributionType("NO_PORTLETS");
    
    private String name;
    
    private DistributionType() {
    }
    
    /**
     * Creates a new channel mode with the given name.<br>
     * Lower case letters in the name will be converted to
     * upper case letters.
     *
     * @param name The name of the channel mode
     */
    public DistributionType(String name) {
        if (name == null) {
            throw new IllegalArgumentException("DistributionType name can not be NULL");
        }
        this.name = name.toUpperCase(Locale.ENGLISH);
    }
    
    public String toString() {
        return this.name;
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public boolean equals(Object object) {
        if(object != null){
            return this.name.equals(((DistributionType)object).name);
        }else{
            return false;
        }
    }
}

