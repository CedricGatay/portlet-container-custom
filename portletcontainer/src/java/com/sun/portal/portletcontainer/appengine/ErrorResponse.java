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

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/*
 * This class is a wrapper of the http servlet response for outputing
 * error content to the portlet window.
 */
public class ErrorResponse extends HttpServletResponseWrapper {
    
    private CharArrayWriter buffer = new CharArrayWriter();
    private PrintWriter writer = new PrintWriter(buffer);

    // Constructor
    public ErrorResponse(HttpServletResponse res) {
        super(res);
    }
    
    /**
     * Overrides the <code>getWriter</code>() method of the 
     * <code>HttpServletResponse</code> and returns the 
     * <code>PrintWriter</code> that is instantiated from a
     * <code>CharArrayWritter</code>.
     * <P>
     * @return A <code>PrintWriter</code>
     */
    public PrintWriter getWriter() {
        return writer;
    }
    
    /**
     * Returns the content of the error page as a <code>StringBuffer</code>.
     * <P>
     * @return A <code>StringBuffer</code>
     */
    public StringBuffer getBuffer() {
        return new StringBuffer(buffer.toString());
    }
}
