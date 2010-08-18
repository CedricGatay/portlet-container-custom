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
 * A PublicDescriptorHolderException is thrown when an unrecoverable error
 * occurs during the processing of event definition or public render parameter definitions.
 *
 **/
public class PublicDescriptorHolderException extends Exception {
    
    /**
     * Creates a new instance of <code>PublicDescriptorHolderException</code> without detail message.
     */
    public PublicDescriptorHolderException() {
    }
    
    
    /**
     * Constructs an instance of <code>PublicDescriptorHolderException</code> with the specified detail message.
     *
     * @param message the detail message.
     */
    public PublicDescriptorHolderException(String message) {
        super(message);
    }

    /**
     * Constructs an instance of <code>PublicDescriptorHolderException</code> with the specified detail 
     * message associated with the cause.
     * @param message the detailed message
     * @param cause the cause associated with the message
     */
    public PublicDescriptorHolderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an instance of <code>PublicDescriptorHolderException</code> with the specified cause.
     * @param cause the cause of the exception
     */
    public PublicDescriptorHolderException(Throwable cause) {
        super(cause);
    }
}
