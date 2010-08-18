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

import java.io.IOException;
import javax.servlet.ServletOutputStream;

/**
 * PSServletOutputStreamIgnored class is a wrapper that ignores
 * all calls to the output stream. This is used in case no response
 * needs to be written to the stream.
 *
 */
public class PSServletOutputStreamIgnored extends ServletOutputStream {
    
    public PSServletOutputStreamIgnored() {
    }

    public void close() {
        //no-op
    }
    
    public void flush() {
        //no-op
    }
    
    public void write(byte[] b) throws IOException {
        //no-op
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
        //no-op
    }
    
    public void write(int b) throws IOException {
        //no-op
    }
    
    public void print(boolean b) {
        //no-op
    }
    
    public void print(char c) {
        //no-op
    }
    
    public void print(double d) {
        //no-op
    }
    
    public void print(float f) {
        //no-op
    }
    
    public void print(int i) {
        //no-op
    }
    
    public void print(long l) {
        //no-op
    }
    
    public void print(String s) {
        //no-op
    }
    
    public void println() {
        //no-op
    }
    
    public void println(boolean b) {
        //no-op
    }
    
    public void println(char c) {
        //no-op
    }
    
    public void println(double d) {
        //no-op
    }
    
    public void println(float f) {
        //no-op
    }
    
    public void println(int i) {
        //no-op
    }
    
    public void println(long l) {
        //no-op
    }
    
    public void println(String s) {
        //no-op
    }
}
