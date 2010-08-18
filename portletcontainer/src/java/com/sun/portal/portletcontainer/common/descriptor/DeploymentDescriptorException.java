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

import java.io.PrintStream;
import java.io.PrintWriter;

public class DeploymentDescriptorException extends Exception {

  protected Throwable wrapped = null;

  /**
   * Constructs a new exception with the specified message, indicating an
   * error in the provider as happened.<br><br>
   *
   * @param msg The descriptive message.
   */
  public DeploymentDescriptorException(String msg) {
      super(msg);
  }

  /**
   * Constructs a new exception with the specified message, and the original
   * <code>exception</code> or <code>error</code>, indicating an error in the
   * deployment descriptor as happened.<br><br>
   *
   * @param msg The descriptive message.
   * @param e The original <code>exception</code> or <code>error</code>.
   */
  public DeploymentDescriptorException(String msg, Throwable e) {
      super(msg);
      wrapped = e;
  }

  public Throwable getWrapped() {
      return wrapped;
  }

  public String toString() {
      StringBuffer b = new StringBuffer();

      b.append(super.toString());
      if (getWrapped() != null) {
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

