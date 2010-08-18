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

package com.sun.portal.portletcontainer.appengine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;

/**
 * StringServletOutputStream provides OutputStream required by
 * PortletAppEngine. It uses ByteArrayOutputStream.
 * 
 */
public class StringServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream byteArrayOutputStream;
    
    public StringServletOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    @Override
    public void write(int b) throws IOException {
        this.byteArrayOutputStream.write(b);
    }

    public byte[] toByteArray() {
        return this.byteArrayOutputStream.toByteArray();
    }

    public String toString(String characterEncoding) throws UnsupportedEncodingException {
        return this.byteArrayOutputStream.toString(characterEncoding);
    }
    
    public void reset() {
        this.byteArrayOutputStream.reset();
    }

    public int size() {
        return this.byteArrayOutputStream.size();
    }

}
