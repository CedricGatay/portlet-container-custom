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
 * The <code>PortletLang</code> class represents the possible languages to write portlets.
 * <P>
 * This class defines a standard set of the languages for portlet.
 * Additional portlet languages may be defined by calling the constructor
 * of this class.
 */
public class PortletLang {

    /**
     * This represents the JAVA portlets
     * <p>
     * The string value for this type is "JAVA".
     */
    public static final PortletLang JAVA = new PortletLang("JAVA");

    /**
     * This represents the Ruby On Rails portlets
     * <p>
     * The string value for this type is "ROR".
     */
    public static final PortletLang ROR = new PortletLang("ROR");

    private String lang;

    private PortletLang() {
    }

    /**
     * Creates a new PortletLang with the given name.<br>
     * Lower case letters in the language name will be converted to
     * upper case letters.
     * 
     * @param lang Language in which the portlet is written
     */
    public PortletLang(String lang) {
        if (lang == null) {
            throw new IllegalArgumentException("PortletLang language can not be NULL");
        }
        this.lang = lang.toUpperCase(Locale.ENGLISH);
    }

    @Override
    public String toString() {
        return lang;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PortletLang other = (PortletLang) obj;
        if (this.lang != other.lang && (this.lang == null || !this.lang.equals(other.lang))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.lang != null ? this.lang.hashCode() : 0);
        return hash;
    }
}
