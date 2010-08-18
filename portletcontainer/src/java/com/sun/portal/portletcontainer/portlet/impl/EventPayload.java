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

package com.sun.portal.portletcontainer.portlet.impl;

import com.sun.portal.container.ContainerLogger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

/**
 * EventPayload is a wrapper for the actual event payload. This is responsible for
 * writing and reading to/from the serialized stream.
 *
 */
//TODO - Not in use, to be removed
public class EventPayload {
    
    private static Logger logger = ContainerLogger.getLogger(EventPayload.class, "PAELogMessages");
    private QName eventQName;
    private byte[] eventValueBytes;
    private Serializable eventValueObject;
    private boolean serialize;
    
    public EventPayload(QName qname, String valueType) {
        this.eventQName = qname;
        this.serialize = doesValueNeedSerialization(valueType) ;
    }
    
    public void writePayload(Serializable value){
        if(this.serialize) {
            ByteArrayOutputStream baos = null;
            ObjectOutputStream out = null;
            try {
                baos = new ByteArrayOutputStream();
                out = new ObjectOutputStream(baos);
                out.writeObject(value);
                this.eventValueBytes = baos.toByteArray();
            } catch (Exception e) {
                if(logger.isLoggable(Level.SEVERE)){
                    LogRecord record = new LogRecord(Level.SEVERE, "PSPL_PAECSPPA0022");
                    record.setLoggerName(logger.getName());
                    record.setThrown(e);
                    record.setParameters(new Object[] { eventQName });
                    logger.log(record);
                }
            } finally {
                try {
                    if(baos != null)
                        baos.close();
                    if(out != null)
                        out.close();
                } catch (IOException ex) {
                    //ignored
                }
            }
        } else {
            this.eventValueObject = value;
        }
    }
    
    public Serializable getPayload() {
        Serializable value = null;
        if(this.serialize) {
            if(this.eventValueBytes != null) {
                ByteArrayInputStream bais = null;
                ObjectInputStream in = null;
                try {
                    bais = new ByteArrayInputStream(eventValueBytes);
                    in = new PayloadObjectInputStream(Thread.currentThread().getContextClassLoader(), bais);
                    value = (Serializable)in.readObject();
                } catch (Exception e) {
                    if(logger.isLoggable(Level.SEVERE)){
                        LogRecord record = new LogRecord(Level.SEVERE, "PSPL_PAECSPPA0024");
                        record.setLoggerName(logger.getName());
                        record.setThrown(e);
                        record.setParameters(new Object[] { eventQName });
                        logger.log(record);
                    }
                } finally {
                    try {
                        if(bais != null)
                            bais.close();
                        if(in != null)
                            in.close();
                    } catch (IOException ex) {
                        //ignored
                    }
                }
            } else {
                logger.log(Level.SEVERE, "PSPL_PAECSPPA0025", eventQName);
            }
        } else {
            value = this.eventValueObject;
        }
        return value;
    }
    
   // If the object is of primitive type, there is no need for serialization
    private boolean doesValueNeedSerialization(String valueType) {
        if(valueType.startsWith("java.lang")) {
            return false;
        }
        return true;
    }
}
