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

import java.io.File;
import java.io.InputStream;

/**
 * The VendorPortletXML interface defines methods that are invoked during
 * portlet deployment and portlet initialization. The implementation is
 * responsible for handling vendor specific portlet xml file.
 * 
 */
public interface VendorPortletXML {
    /**
     * This is invoked when the portlet application is deployed. The implementation
     * is responsible for any processing.
     * 
     * @param warFile the Portlet Web Application.
     * @param vendorPortletXMLStream the vendor portlet xml as stream
     * @param schemaLocation the location of the schema for the vendor portlet xml
     * @param validate true if the vendor portlet xml needs to 
     * 
     * @return The object that represents the vendor portlet xml.
     *
     * @throws java.lang.Exception
     */
    Object processDeploy(File warFile, InputStream vendorPortletXMLStream, String schemaLocation, boolean validate) throws Exception;

    /**
     * This is invoked when the portlet application is undeployed. The implementation
     * is responsible for any processing.
     * 
     * @param vendorPortletXMLStream the vendor portlet xml as stream
     * 
     * @throws java.lang.Exception
     */
    void processUndeploy(InputStream vendorPortletXMLStream) throws Exception;

    /**
     * This is invoked when the portlet application is initialized. The implementation
     * must return an iobject that represents the vendor portlet xml.
     * 
     * @param vendorPortletXMLStream the vendor portlet xml as stream
     * 
     * @return The object that represents the vendor portlet xml.
     * @throws java.lang.Exception
     */
    Object loadPortletExtensionDescriptor(InputStream vendorPortletXMLStream) throws Exception;
}
