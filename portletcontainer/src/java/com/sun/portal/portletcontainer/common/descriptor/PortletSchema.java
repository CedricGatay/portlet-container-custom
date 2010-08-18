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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * PortletSchema class is responsible for creating Schema Objects for
 * portlet schemas corresponding to version 1.0 and version 2.0
 * 
 */
public class PortletSchema {

    // Create a logger for this class
    private static Logger logger = ContainerLogger.getLogger(PortletSchema.class,
                                                             "PCCLogMessages");
    
    private static final String PORTLET_V1_SCHEMA_FILE = "portlet-app_1_0.xsd";
    private static final String PORTLET_V2_SCHEMA_FILE = "portlet-app_2_0.xsd";
    private static Map<String, Schema> schemas = new HashMap();
    static {
        generateSchema(PortletDescriptorConstants.V1, PORTLET_V1_SCHEMA_FILE);
        generateSchema(PortletDescriptorConstants.V2, PORTLET_V2_SCHEMA_FILE);
    }

    /**
     * Returns the Schema Object based on the version of the Portlet.
     * A Schema object is an immutable memory representation of schema. 
     * A Schema instance can be shared with many different parser instances, 
     * even if they are running in different threads.
     * 
     * @param version the version of the portlet.xml
     * 
     * @return the Schema Object based on the version of the Portlet
     * 
     */
    public static Schema getSchema(String version) {
        if (schemas != null) {
            return schemas.get(version);
        }
        return null;
    }

    private static void generateSchema(String version, String schemaFilename) {
        InputStream schemaStream = null;
        try {
            schemaStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(schemaFilename);
            
            SchemaFactory factory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // Set the resource resolver to redirect the references to external resources
            factory.setResourceResolver(new PortletLSResourceResolver());
            // load a WXS schema, represented by a Schema instance
            Source schemaFile = new StreamSource(schemaStream);
            Schema schema = factory.newSchema(schemaFile);
            schemas.put(version, schema);
        } catch (Exception ex) {
            if(logger.isLoggable(Level.SEVERE)) {
                LogRecord record = new LogRecord(Level.SEVERE, "PSPL_PCCCSPPCCD0019");
                record.setParameters(new String[] {version, schemaFilename});
                record.setThrown(ex);
                record.setLoggerName(logger.getName());
                logger.log(record);
            }
        }finally{
            try{
                if(schemaStream!=null){
                    schemaStream.close();
                }
            }catch(Exception e){
                
            }
        }
    }
}
