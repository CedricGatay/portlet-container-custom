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

import com.sun.portal.container.service.EventHolder;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
/**
 * The <code>PortletEvent</code> class represents an event that the portlet has received
 * in the event processing phase.
 *
 */
public class PortletEvent {
    
    private EventHolder eventHolder;
    private QName eventQName;
    private Serializable eventPayload;
    
    private static Logger logger = ContainerLogger.getLogger(PortletEvent.class,
            "CLogMessages");

    public PortletEvent(EventHolder eventHolder, QName eventQName, Serializable value) {
        this.eventHolder = eventHolder;
        this.eventQName = eventQName;
		try {
			this.eventPayload = ContainerUtil.serializeJavaToXml(eventQName, value);
		} catch (Exception ex) {
			logger.log(getLogRecord("PSC_CSPCS019", ex));
		}
    }
    
    public PortletEvent(EventHolder eventHolder, QName eventName, JAXBElement value) {
        this.eventQName = eventName;
        this.eventHolder = eventHolder;
        Serializable xmlData = null;
        if(value != null) {
            try {
                Class clazz = value.getClass();
                JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
                Marshaller marshaller = jaxbContext.createMarshaller();
                Writer out = new StringWriter();
                marshaller.marshal(value, out);
                xmlData = out.toString();
            } catch(JAXBException jaxbe) {
                logger.log(getLogRecord("PSC_CSPCS019", jaxbe));
            }
        }
        
        this.eventPayload = xmlData;
        
    }
	
    public PortletEvent(EventHolder eventHolder, QName eventName, Element value) {
        this.eventQName = eventName;
        this.eventHolder = eventHolder;
        Serializable xmlData = null;
        if(value != null) {
            try {
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer trans = tf.newTransformer();
                StringWriter sw = new StringWriter();
                trans.transform(new DOMSource(value), new StreamResult(sw));
                xmlData = sw.toString();
            } catch (TransformerConfigurationException e){
                logger.log(getLogRecord("PSC_CSPCS019", e));
            }catch (TransformerException e1){
                logger.log(getLogRecord("PSC_CSPCS019", e1));
            }
        }        
        this.eventPayload = xmlData;        
    }
    /**
     * Get the EventHolder.
     *
     * @return  EventHolder associated with this event.
     */
    public EventHolder getEventHolder() {
        return this.eventHolder;
    }
    
    /**
     * Get the event QName.
     *
     * @return  the QName of the event, never <code>null</code>.
     */
    public QName getQName() {
        return this.eventQName;
    }
    
    /**
     * sets the event QName.
     * 
     * @param eventQName the QName of the event
     */
    public void setQName(QName eventQName) {
        this.eventQName = eventQName;
    }
    
    /**
     * Get the local part of the event name.
     *
     * @return  the local part of the event, never <code>null</code>.
     */
    public String getName() {
        return this.eventQName.getLocalPart();
    }
    
    /**
     * Get the event payload.
     *
     * @return  event payload, must be serializable.
     *          May return <code>null</code> if this event has no payload.
     */
    public Serializable getValue() {
        Serializable value = null;
		try {
			value = ContainerUtil.deserializeXmlToJava(getValueType(), this.eventPayload);
		} catch (Exception ex) {
			logger.log(getLogRecord("PSC_CSPCS020", ex));
		}
        return value;
    }

    /**
     * Get event payload in DOM Element format.
     *
     * @return  Element    
     */
    public Element getElementValue(){
        Element docElement = null;
        ByteArrayInputStream bais = null;
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            bais = new ByteArrayInputStream(
                    ((String)this.eventPayload).getBytes("UTF-8"));
            Document doc = parser.parse(bais);
            docElement =  doc.getDocumentElement();
        } catch (SAXException ex){
            logger.log(getLogRecord("PSC_CSPCS024", ex));
        } catch (ParserConfigurationException pcex){
            logger.log(getLogRecord("PSC_CSPCS024", pcex));
        } catch (IOException ioex){
            logger.log(getLogRecord("PSC_CSPCS024", ioex));
        } finally{
            try{
                if(bais != null){
                    bais.close();
                }
            }catch(Exception ignore){}
        }
        
        return docElement;
    }

    /**
     * Get the type of the event value. It will be a fully
     * qualified class type. This may be null, if there is no
     * value associated with the event.
     *
     * @return  the type of the event value.
     */
    public String getValueType() {
        return this.eventHolder.getValueType();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PortletEvent other = (PortletEvent) obj;
        if (this.eventHolder != other.eventHolder 
                && (this.eventHolder == null || !this.eventHolder.equals(other.eventHolder))) {
            return false;
        }
        if (this.eventQName != other.eventQName 
                && (this.eventQName == null || !this.eventQName.equals(other.eventQName))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.eventHolder != null ? this.eventHolder.hashCode() : 0);
        hash = 17 * hash + (this.eventQName != null ? this.eventQName.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( "PortletEvent[");
        
        sb.append( this.eventQName );
        sb.append("]");
        
        return sb.toString();
    }

    private LogRecord getLogRecord(String code, Exception cause){
        LogRecord record = new LogRecord(Level.SEVERE, code);
        record.setThrown(cause);
        record.setParameters(new Object[] { getValueType(), getQName()} );
        record.setLoggerName(logger.getName());
        return record;
    }
}
