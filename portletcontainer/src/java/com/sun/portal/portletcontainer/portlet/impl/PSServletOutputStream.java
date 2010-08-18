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
import java.io.PrintStream;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * PSServletOutputStream class is a wrapper that delegates the call to the
 * Outputstream which can be either RenderResponse's Output Stream or
 * ResourceResponse's OutputStream
 *
 */
public class PSServletOutputStream extends ServletOutputStream {
    
    private PrintStream printStream;
    
    public PSServletOutputStream(OutputStream outStream) {
        printStream = new PrintStream(outStream);
    }
    
    // for all methods of the ServletOutputStream/OutputStream
    // delegate to the printStream
    
    public void close() {
        printStream.close();
    }
    
    public void flush() {
        printStream.flush();
    }
    
    public void write(byte[] b) throws IOException {
        printStream.write(b);
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
        printStream.write(b, off, len);
    }
    
    public void write(int b) throws IOException {
        printStream.write(b);
    }
    
    public void print(boolean b) {
        printStream.print(b);
    }
    
    public void print(char c) {
        printStream.print(c);
    }
    
    public void print(double d) {
        printStream.print(d);
    }
    
    public void print(float f) {
        printStream.print(f);
    }
    
    public void print(int i) {
        printStream.print(i);
    }
    
    public void print(long l) {
        printStream.print(l);
    }
    
    public void print(String s) {
        printStream.print(s);
    }
    
    public void println() {
        printStream.println();
    }
    
    public void println(boolean b) {
        printStream.println(b);
    }
    
    public void println(char c) {
        printStream.println(c);
    }
    
    public void println(double d) {
        printStream.println(d);
    }
    
    public void println(float f) {
        printStream.println(f);
    }
    
    public void println(int i) {
        printStream.println(i);
    }
    
    public void println(long l) {
        printStream.println(l);
    }
    
    public void println(String s) {
        printStream.println(s);
    }
}
