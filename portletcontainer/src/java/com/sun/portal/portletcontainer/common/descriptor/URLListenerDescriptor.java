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

package com.sun.portal.portletcontainer.common.descriptor;

import com.sun.portal.container.ContainerLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.portlet.PortletURLGenerationListener;
import org.w3c.dom.Element;

/**
 * The listener element used to declare listeners for this portlet application.
 * Used in: portlet-app
 *
 * Java content class for listenerType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/C:/Sun/jwsdp-1.6/jaxb/bin/portlet-app_2_0.xsd line 541)
 * <p>
 * <pre>
 * &lt;complexType name="listenerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="display" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="listener-class" type="{http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd}fully-qualified-classType/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 */
public class URLListenerDescriptor {
    
    private Map<String, String> descriptionMap;
    private Map<String, String> displayNameMap;
    private PortletURLGenerationListener listener;
    
    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PortletDescriptor.class, "PCCLogMessages");

    public URLListenerDescriptor() {
    }
    
    public void load( Element listenerElement, String namespaceURI){
        //load descriptions
        descriptionMap = PortletXMLDocumentHelper.getDescriptionMap(listenerElement);

        //load displaynames
        displayNameMap = PortletXMLDocumentHelper.getDisplayNameMap(listenerElement);

        //load listener-class
        String listenerClassName = PortletXMLDocumentHelper.getChildTextTrim(listenerElement, 
                PortletDescriptorConstants.URL_LISTENER_CLASS);
        try {
            Class listenerClass = Thread.currentThread().getContextClassLoader().loadClass(listenerClassName);
            logger.log(Level.INFO, "PSPL_PCCCSPPCCD0018", listenerClassName);
            listener = (PortletURLGenerationListener) (listenerClass.newInstance());
        } catch (ClassNotFoundException cnfe) {
            logger.log(getLogRecord(listenerClassName, cnfe));
        } catch (IllegalAccessException iae) {
            logger.log(getLogRecord(listenerClassName, iae));
        } catch (InstantiationException ie) {
            logger.log(getLogRecord(listenerClassName, ie));
        }
    }
    
    /**
     * Gets the instance of the listener as specified in listener-class property.
     *
     * @return {@link java.lang.String}
     */
    public PortletURLGenerationListener getListener(){
        return listener;
    }
    
    public List getDescriptions(){
        return new ArrayList<String>(descriptionMap.values());
    }
    
    /**
     * Gets the value of default description, if any
     *
     * @return  {@link java.lang.String}
     */
    public String getDescription(){
        return PortletXMLDocumentHelper.getDescription(descriptionMap);
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
     * descriptions. Empty <code>Map</code> will be returned
     * if not defined.
     */
    public Map getDescriptionMap() {
        return descriptionMap;
    }
    
    private LogRecord getLogRecord(String message, Throwable cause) {
        LogRecord logRecord = new LogRecord(Level.WARNING, "PSPL_PCCCSPPCCD0017");
        logRecord.setLoggerName(logger.getName());
        logRecord.setParameters(new String[] { message} );
        logRecord.setThrown(cause);
        return logRecord;
    }
}
