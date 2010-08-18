/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.portal.portletcontainer.common.descriptor;

import com.sun.portal.container.service.EventHolder;
import com.sun.portal.container.service.SharedSessionAttributeHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author dg154973
 */
public class SharedSessionAttributeDescriptor {

    private String id;
    private SharedSessionAttributeHolder sharedSessionAttributeHolder;

    /**
     * Loads <shared-session-attribute> tag
	 *
     * @param sharedSessionAttributeElement
	 * @param namespaceURI
     */
    public void load(Element sharedSessionAttributeElement, String namespaceURI){

        //load descriptions
        Map descriptionMap = PortletXMLDocumentHelper.getDescriptionMap(sharedSessionAttributeElement);

		String name = PortletXMLDocumentHelper.getChildTextTrim(sharedSessionAttributeElement, PortletDescriptorConstants.NAME);

        //load value-type
        String valueType = PortletXMLDocumentHelper.getChildTextTrim(sharedSessionAttributeElement, PortletDescriptorConstants.VALUE_TYPE);

        sharedSessionAttributeHolder = new SharedSessionAttributeHolder(descriptionMap, name, valueType);
    }

    /**
     * Returns the SharedSessionAttributeHolder associated with the descriptor
     *
     * @return the SharedSessionAttributeHolder associated with the descriptor
     */
    public SharedSessionAttributeHolder getSharedSessionAttributeHolder() {
        return this.sharedSessionAttributeHolder;
    }

    /**
     * Gets the value of the id property.
     *
     * @return String
     */
    public String getId(){
        return id;
    }

    /**
     * Gets the list of all descriptions associated with all languages
     *
     * @return  {@link java.lang.String}
     */
    public List<String> getDescriptions(){
        return new ArrayList<String>(getDescriptionMap().values());
    }

    /**
     * Get the value of description property for default lang
     *
     * @return String
     */
    public String getDescription(){
        return PortletXMLDocumentHelper.getDescription(getDescriptionMap());
    }

    /**
     * Get the value of description property for a
     * specific xml:lng
     *
     * @param lang
     * @return String
     */
    public String getDescription(String lang){
        return getDescriptionMap().get(lang);
    }

    /**
     * Returns descriptions in a Map.
     * <P>
     * @return <code>Map</code> of lang/description  pairs of the
     * event descriptions. Empty <code>Map</code> will be returned
     * if not defined.
     */
    public Map<String, String> getDescriptionMap() {
        return sharedSessionAttributeHolder.getDescriptionMap();
    }

    /**
     * Gets the value of the valueType property.
     *
     *
     * @return {@link java.lang.String}
     */
    public String getValueType(){
        return sharedSessionAttributeHolder.getValueType();
    }

    /**
     * Gets the value of the name property.
     *
     * @return String
     */
    public String getName(){
        return sharedSessionAttributeHolder.getName();
    }

}
