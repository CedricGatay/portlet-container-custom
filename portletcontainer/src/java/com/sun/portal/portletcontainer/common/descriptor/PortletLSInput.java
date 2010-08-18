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

import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;

/**
 * PortletLSInput class represents the input source present in the
 * application.
 *
 */
public class PortletLSInput implements LSInput {
    
    private InputStream schemaStream;
    private String systemId;
    private String baseURI;
    private String publicId;
    
    public PortletLSInput() {
    }

    public Reader getCharacterStream() {
        return null;
    }

    public void setCharacterStream(Reader characterStream) {
    }

    public InputStream getByteStream() {
        return this.schemaStream;
    }

    public void setByteStream(InputStream byteStream) {
        this.schemaStream = byteStream;
    }

    public String getStringData() {
        return null;
    }

    public void setStringData(String stringData) {
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getBaseURI() {
        return this.baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public String getEncoding() {
        return null;
    }

    public void setEncoding(String encoding) {
    }

    public boolean getCertifiedText() {
        return false;
    }

    public void setCertifiedText(boolean certifiedText) {
    }
    
}
