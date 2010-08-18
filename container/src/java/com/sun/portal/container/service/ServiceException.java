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

package com.sun.portal.container.service;

/**
 * A ServiceException is thrown when there is an unrecoverable error
 * occurs when service code gets executed.
 *
 **/
public class ServiceException extends Exception {
    
    /**
     * Creates a new instance of <code>ServiceException</code> without detail message.
     */
    public ServiceException() {
    }
    
    
    /**
     * Constructs an instance of <code>ServiceException</code> with the specified detail message.
     *
     * @param message the detail message.
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Constructs an instance of <code>ServiceException</code> with the specified detail 
     * message associated with the cause.
     * @param message the detailed message
     * @param cause the cause associated with the message
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an instance of <code>ServiceException</code> with the specified cause.
     * @param cause the cause of the exception
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }
}
