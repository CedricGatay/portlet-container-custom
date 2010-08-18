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
 * [Name of File] [ver.__] [Date]
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */

package com.sun.portal.container;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A ContentException is thrown when there is an unrecoverable error
 * occurs when a container is trying to obtain the content of a particular
 * channel.
 **/

public class ContentException extends Exception {
    
    protected Throwable wrapped = null;
    protected ErrorCode code = null;
    
    /**
     * Constructs a new exception with the specified message, indicating an
     * error in the provider as happened.<br><br>
     *
     * @param msg The descriptive message.
     */
    public ContentException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs a new exception with the specified message and an <code>error
     * code</code>, indicating an
     * error in the provider as happened.<br><br>
     *
     * @param msg The descriptive message.
     * @param code The error code associate with the exception.
     */
    public ContentException(String msg, ErrorCode code) {
        super(msg);
        this.code = code;
    }
    
    /**
     * Constructs a new exception with the specified message, and the original
     * <code>exception</code>, indicating an error in the
     * container as happened.<br><br>
     *
     * @param msg The descriptive message.
     * @param e The original <code>exception</code>.
     */
    public ContentException(String msg, Throwable e) {
        super(msg);
        wrapped = e;
    }
    
    /**
     * Constructs a new exception with the specified message, the <code>error
     * code</code> and the original <code>exception</code>, indicating an error
     * in the container as happened.<br><br>
     *
     * @param msg The descriptive message.
     * @param code The error code associate with the exception.
     * @param e The original <code>exception</code>.
     */
    public ContentException(String msg, ErrorCode code, Throwable e) {
        super(msg);
        wrapped = e;
        this.code = code;
    }
    
    public Throwable getWrapped() {
        return wrapped;
    }
    
    public ErrorCode getErrorCode() {
        return code;
    }
    
    public String toString() {
        StringBuffer b = new StringBuffer();
        
        b.append(super.toString());
        b.append( " with errorcode " );
        b.append( code );
        if (getWrapped() != null) {
			b.append(" and exception is ");
            b.append(wrapped.toString());
        }
        
        return b.toString();
    }
    
    public void printStackTrace() {
        super.printStackTrace();
        if (getWrapped() != null) {
            wrapped.printStackTrace();
        }
    }
    
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if (getWrapped() != null) {
            wrapped.printStackTrace(s);
        }
    }
    
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (getWrapped() != null) {
            wrapped.printStackTrace(s);
        }
    }
}

