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

package com.sun.portal.container;

import java.util.logging.Logger;

/**
 * The <code>ContainerLogger</code> is a proxy for the Logger class. All the classes in the Container
 * use this class to get the logger. This ensures that the logger namespace is uniform
 * across the project.
 *
 */
public class ContainerLogger {
    
    private static String DEBUG_NAME = "debug";
    
    private ContainerLogger() {
    }

    /**
     * Returns the Logger for the class object and the associated
     * resource bundle file.
     * Derives the package name from the class object and uses it
     * to create the Logger.
     * 
     * @return the Logger
     * @param logMessages the resource bundle name
     * @param cls a class object
     */
    public static Logger getLogger(Class cls, String logMessages) {
        return Logger.getLogger(getName(cls), logMessages);
    }

    /**
     * Returns the Logger for the specified name.
     * This method is used by those classes that do not follow
     * com.sun.portal namespace. This is used in place if the
     * above method <code>getLogger(Class cls)</code>.
     *
     * @param name the package name
     * @return the Logger
     */
    public static Logger getLogger(String name) {
        return Logger.getLogger(correct(name));
    }

    /**
     * Derives the package name from the class object and prepends it
     * with "debug".
     *
     * @param cls a class object
     * @return the package name prepended with 'debug'.
     */
    private static String getName(Class cls) {
        Package pkg = cls.getPackage();
        String packageName = (pkg == null) ? getDefaultPkgName(cls) : pkg.getName();
        String correctName = correct(packageName);
        return correctName;
    }

    private static String getDefaultPkgName(Class cls) {
        String className = cls.getName();
        String pkgName = DEBUG_NAME;
        int index = -1;
        if (className != null){
            index = className.lastIndexOf(".");
        }
        if (index != -1){
            pkgName = className.substring(0,index);
        }
        return pkgName;
    }

    /**
     * Prepends the name with "debug" if its not present.
     *
     * @param name the package name
     * @return the package name prepended with 'debug'.
     */
    private static String correct(String name) {
        if (name.indexOf(DEBUG_NAME) != 0) {
            StringBuffer csuffix = new StringBuffer();
            csuffix.append(DEBUG_NAME);
            csuffix.append(".");
            csuffix.append(name);
            return csuffix.toString();
        } else {
            return name;
        }
    }
}
