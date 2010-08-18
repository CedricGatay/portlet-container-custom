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
 

package com.sun.portal.portletcontainer.impl;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is used as a wrapper over HttpServletResponse.
 * To avoid any write operation to the original HttpServletResponse
 * this wrapper will override methods which involves writing to
 * response.
 */
public class PAEResponseWrapper extends HttpServletResponseWrapper {
    
    public PAEResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return new PSServletOutputStream();
    }

    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new PSServletOutputStream());
    }

    public void sendError(int sc) throws IOException {
        //Do nothing ...
    }

    public void setStatus(int sc) {
        //Do nothing ...
    }

    public void setStatus(int sc, String sm) {
        //Do nothing ...
    }
    
    //Implementation to avoid writing to actual HttpServletResponse
   static class PSServletOutputStream extends ServletOutputStream {
       
        public PSServletOutputStream() {
            //Do nothing ...
        }
        
        public void write(int b) throws IOException {
            //Nothing to be done ...
        }
    }
}
