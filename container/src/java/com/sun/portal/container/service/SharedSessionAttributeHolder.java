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

package com.sun.portal.container.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * The <code>SharedSessionAttributeHolder</code> class holds information about
 * a particular Shared Session Attribute within a portlet webapp.
 * It provides convenience methods to access various information.
 *
 */
public class SharedSessionAttributeHolder {

    private Map<String, String> descriptionMap;
    private String name;
    private String valueType;

    /**
     * Creates a new SharedSessionAttributeHolder object.
     *
     * @param descriptionMap the map containing the lang/description pairs of the event descriptions.
     * @param name the value of the name property
     * @param valueType the type of the value
    */
    public SharedSessionAttributeHolder(Map<String, String> descriptionMap,
            String name, String valueType) {

        this.descriptionMap = descriptionMap;

        if (this.descriptionMap == null) {
            this.descriptionMap = Collections.EMPTY_MAP;
        }

        this.name = name;

        this.valueType = valueType;
    }


     /**
     * Gets the type of the value
     *
     * @return  {@link java.lang.String}
     */
    public String getValueType(){
        return valueType;
    }

     /**
     * Gets all the descriptions
     *
     * @return  {@link java.lang.String}
     */
    public List<String> getDescriptions(){
        return new ArrayList<String>(descriptionMap.values());
    }


    /**
     * Gets the value of description associated with particular xml:lang
     *
     * @param lang the language code
     * @return  {@link java.lang.String}
     */
    public String getDescription(String lang){
        return descriptionMap.get(lang);
    }

    /**
     * Returns descriptions in a Map.
     * <P>
     * @return <code>Map</code> of lang/description  pairs of the
     * event descriptions.
     * Empty <code>Map</code> will be returned if not defined.
     */
    public Map<String, String> getDescriptionMap() {
        return descriptionMap;
    }

    /**
     * Gets the value of the name property.
     *
     * @return String
     */
    public String getName(){
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SharedSessionAttributeHolder other = (SharedSessionAttributeHolder) obj;
        if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( "SharedSessionAttributeHolder[");

        sb.append( this.name );
        sb.append("]");

        return sb.toString();
    }
}
