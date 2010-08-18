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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * PayloadObjectInputStream loads the class based on a specified ClassLoader
 * rather than the system default.
 * <p>
 * This is useful when PortletAppEngine loads the class that is in the webapp.
 *
 */
//TODO - Not in use, to be removed
public class PayloadObjectInputStream extends ObjectInputStream {
    
    private ClassLoader classLoader;
    
    /**
     * Constructs a new PayloadObjectInputStream.
     *
     * @param classLoader  the ClassLoader from which classes should be loaded
     * @param inputStream  the InputStream that contains the payload.
     * @throws IOException in case of an I/O error
     */
    public PayloadObjectInputStream(ClassLoader classLoader, InputStream inputStream)
    throws IOException {
        super(inputStream);
        this.classLoader = classLoader;
    }
    
    /**
     * Resolve a class using the specified ClassLoader or 
     * the super ClassLoader.
     *
     * @param  objectStreamClass an instance of class ObjectStreamClass
     * @return  a Class object corresponding to objectStreamClass
     * @throws IOException in case of an I/O error
     * @throws ClassNotFoundException if the Class cannot be found
     */
    protected Class resolveClass(ObjectStreamClass objectStreamClass)
    throws IOException, ClassNotFoundException {
        
        Class clazz = Class.forName(objectStreamClass.getName(), false, classLoader);
        
        if (clazz != null) {
            // the classloader knows this class
            return clazz;
        } else {
            // classloader does ot know this class, hence delegate it to the superclass
            return super.resolveClass(objectStreamClass);
        }
    }
}
