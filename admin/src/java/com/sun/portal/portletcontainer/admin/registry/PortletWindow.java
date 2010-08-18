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
 

package com.sun.portal.portletcontainer.admin.registry;

import com.sun.portal.portletcontainer.admin.registry.model.PortletWindowRegistryModel;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * PortletWindow represents the PortletWindow Element in portlet-window-registry.xml
 */
public class PortletWindow extends AbstractPortletRegistryElement {
    
    private static Logger logger = Logger.getLogger(PortletWindow.class.getPackage().getName(),
            "PALogMessages");

	private int row;

    public PortletWindow() {
		this.row = -1;
    }
    
    public int getRow() {
		return this.row;
    }
    
	@Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("PortletName:");
        buffer.append(getPortletName());
        buffer.append(";Name:");
        buffer.append(getName());
        buffer.append(";Collections(Map):");
        buffer.append(getMapCollectionTable());
        buffer.append(";Collections(String):");
        buffer.append(getStringCollectionTable());
        return buffer.toString();
    }

	public void setRow(int newRow) {
		this.row = newRow;
	}

	@Override
    protected void populateValues(Element portletWindowTag) {
        // Get the attributes for PortletWindow Tag.
        Map portletWindowAttributes = XMLDocumentHelper.createAttributeTable(portletWindowTag);
        setName((String)portletWindowAttributes.get(NAME_KEY));
        setPortletName((String)portletWindowAttributes.get(PORTLET_NAME_KEY));
        setRemote((String)portletWindowAttributes.get(REMOTE_KEY));
        setLang((String)portletWindowAttributes.get(LANG_KEY));
		setRow(getRowNumber((String)portletWindowAttributes.get(ROW_KEY)));
        super.populateValues(portletWindowTag);
    }
    
    protected void populateValues(PortletWindowRegistryModel portletWindowRegistryModel) {

		setName(portletWindowRegistryModel.getName());
        setPortletName(portletWindowRegistryModel.getPortletName());
		setRemote(portletWindowRegistryModel.getRemote());
		setLang(portletWindowRegistryModel.getLang());
		setRow(portletWindowRegistryModel.getRowNumber());
        super.populateValues(portletWindowRegistryModel.getProperties());
    }

    protected void create(Document document, Element rootTag, int row) {
        // Create PortletWindow tag, set attributes and append it to the document
        Element portletWindowTag = XMLDocumentHelper.createElement(document, PORTLET_WINDOW_TAG);
        portletWindowTag.setAttribute(NAME_KEY, getName());
        portletWindowTag.setAttribute(PORTLET_NAME_KEY, getPortletName());
        portletWindowTag.setAttribute(REMOTE_KEY, getRemote());
        portletWindowTag.setAttribute(LANG_KEY, getLang());
		String rowValue = getStringProperty(ROW_KEY);
		if(rowValue == null) {
			rowValue = String.valueOf(row);
		}
		portletWindowTag.setAttribute(ROW_KEY, rowValue);
        rootTag.appendChild(portletWindowTag);
        // Create Properties tag and append it to the document
        Element propertiesTag = XMLDocumentHelper.createElement(document, PROPERTIES_TAG);
        portletWindowTag.appendChild(propertiesTag);
        createPropertiesTag(document, propertiesTag);
    }

	private int getRowNumber(String rowValue) {
		//Check row value in the attribute of PortletWindow if not present
		// it should be in the Properties
		String rowString = null;
		if(rowValue == null) {
			rowString = getStringProperty(ROW_KEY);
		} else {
			rowString = rowValue;
		}
        int rowNumber = -1;
        if(rowString != null) {
            try {
                rowNumber = Integer.parseInt(rowString);
            } catch (NumberFormatException nfe) {
                logger.log(Level.WARNING, "PSPL_CSPPAM0002", rowString);
            }
        }
        return rowNumber;
	}

	public int compareTo(Object portletWindow) {
		return ((PortletWindow)portletWindow).row - this.row;
	}
}
