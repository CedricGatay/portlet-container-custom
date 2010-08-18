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
 * The <code>PublicRenderParameterHolder</code> class holds information about 
 * a particular Public Render Parameter within a portlet webapp.
 * It provides convenience methods to access various information.
 *
 */
public class PublicRenderParameterHolder {
        
    private String identifier;
    private Map<String, String> descriptionMap;
    private QName qname;
    private List<QName> aliases;

    /**
     * Creates a new PublicRenderParameterHolder object.
     *
     * @param identifier the identifier
     * @param descriptionMap the map containing the lang/description pairs of the event descriptions. 
     * @param qname the value of the qname property
     * @param aliases the value of the alias property.
    */     
    public PublicRenderParameterHolder(String identifier, Map descriptionMap, 
            QName qname, List<QName> aliases) {
        this.identifier = identifier;
        this.descriptionMap = descriptionMap;
        if(this.descriptionMap == null){
            this.descriptionMap = Collections.EMPTY_MAP;
        }
        this.qname = qname;
        this.aliases = aliases;
        if(this.aliases == null) {
            this.aliases = Collections.EMPTY_LIST;
        }
    }
    
    
     /**
     * Gets the identifier
     *
     * @return  {@link java.lang.String}
     */     
    public String getIdentifier(){
        return identifier;
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
     * @return  {@link java.lang.String}
     */
    public String getDescription(String lang){
        return descriptionMap.get(lang);
    }
    
    /**
     * Returns descriptions in a Map.
     * <P>
     * @return <code>Map</code> of lang/description  pairs of the
     * public-render-parameter descriptions. 
     * Empty <code>Map</code> will be returned if not defined.
     */
    public Map<String, String> getDescriptionMap() {
        return descriptionMap;
    }
    
    /**
     * Gets the value of the qname or name property.
     *
     * @return {@link javax.xml.namespace.QName}
     */
    public QName getQName(){
        return qname;
    }
    
    /**
     * Gets the value of the alias property.
     *
     * @return List
     */
    public List<QName> getAliases(){
        if(aliases == null) {
            return Collections.emptyList();
        }
        return aliases;
    }    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PublicRenderParameterHolder other = (PublicRenderParameterHolder) obj;
        if (this.identifier != other.identifier && (this.identifier == null || !this.identifier.equals(other.identifier))) {
            return false;
        }
        if (this.qname != other.qname && (this.qname == null || !this.qname.equals(other.qname))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        hash = 17 * hash + (this.qname != null ? this.qname.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( "PublicRenderParameterHolder[ ");
        
        sb.append( " Identifier=" );
        sb.append( this.identifier );
        
        sb.append( ", QName=" );
        sb.append( this.qname );
        sb.append("]");
        
        return sb.toString();
    }
}
