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

package com.sun.portal.portletcontainer.warupdater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PortletWarUpdaterUtil contains utility methods like copyfiles, creating directories.
 *
 */
public class PortletWarUpdaterUtil {
    
    private static Logger logger = getLogger(PortletWarUpdaterUtil.class, "PWULogMessages");
    private static String DEBUG_NAME = "debug";

    /**
     * Convienence method to copy a file from a source to a destination.
     * Overwrite is prevented, and the last modified is kept.
     *
     * @throws IOException
     */
    public static void copyFile(String sourceFile, String destFile) throws IOException {
        copyFile(new File(sourceFile), new File(destFile), false, true);
    }
    
    
    /**
     * Method to copy a file from a source to a
     * destination specifying if
     * source files may overwrite newer destination files and the
     * last modified time of <code>destFile</code> file should be made equal
     * to the last modified time of <code>sourceFile</code>.
     *
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File destFile,
            boolean overwrite, boolean preserveLastModified)
            throws IOException {
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "PSPL_CSPPCWU0007",
                    new String[] { sourceFile.getAbsolutePath(), destFile.getAbsolutePath() });
        }
        
        if (overwrite || !destFile.exists() ||
                destFile.lastModified() < sourceFile.lastModified()) {
            
            if (destFile.exists() && destFile.isFile()) {
                destFile.delete();
            }
            
            // ensure that parent dir of dest file exists!
            File parent = new File(destFile.getParent());
            if (!parent.exists()) {
                parent.mkdirs();
            }
            
            FileInputStream in = new FileInputStream(sourceFile);
            FileOutputStream out = new FileOutputStream(destFile);
            
            byte[] buffer = new byte[8 * 1024];
            int count = 0;
            do {
                out.write(buffer, 0, count);
                count = in.read(buffer, 0, buffer.length);
            } while (count != -1);
            
            in.close();
            out.close();
            
            
            if (preserveLastModified) {
                destFile.setLastModified(sourceFile.lastModified());
            }
        }
    }


    /**
     * Method to copy a file from a source inputstream to a
     * destination.
     *
     * @throws IOException
     */
    public static void copyFile(InputStream sourceInputStream, File destFile)
            throws IOException {

		if (destFile.exists() && destFile.isFile()) {
			destFile.delete();
		}

		// ensure that parent dir of dest file exists!
		File parent = new File(destFile.getParent());
		if (!parent.exists()) {
			parent.mkdirs();
		}

		FileOutputStream out = new FileOutputStream(destFile);

		byte[] buffer = new byte[8 * 1024];
		int count = 0;
		do {
			out.write(buffer, 0, count);
			count = sourceInputStream.read(buffer, 0, buffer.length);
		} while (count != -1);

		sourceInputStream.close();
		out.close();

    }
    
    public static boolean makeDir(String dirName) {
        File dir = new File(dirName);
        if (dir.exists()) {
            return true;
        } else {
            return dir.mkdirs();
        }
    }
    
    /**
     * Returns the name of the war file denoted by the input pathname. 
     * This is just the last name in the pathname's name sequence.
     *
     * @param warFile warfile name including path.
     *
     * @return the name of the war file denoted by the input pathname.
     */
    public static String getWarName(String warFile) {
        String warNameOnly;
        int index = warFile.lastIndexOf(File.separator);
        if(index == -1) {
            // Check for both "/" and "\"
            index = warFile.lastIndexOf("\\");
            if(index == -1) {
                index = warFile.lastIndexOf("/");
            }
        }
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "PSPL_CSPPCWU0010",
                    new String[] { warFile, File.separator, String.valueOf(index) });
        }
        if(index != -1) {
            warNameOnly = warFile.substring(index+1);
        } else {
            warNameOnly = warFile;
        }
        logger.log(Level.FINER, "PSPL_CSPPCWU0002", warNameOnly);
        return warNameOnly;
    }
    
    
    /**
     * Returns Extension (such as .zip or .war) of the file along with '.'. 
     * If file name is not having extension, it returns a blank string.
     *
     * @param fileName name of the fie.
     *
     * @return Extension (such as .zip or .war) of the file.
     */
    public static String getFileSuffix(String fileName) {
        String ext = "";
        int index = fileName.lastIndexOf(".");
        
        if(index != -1) {
            ext = fileName.substring(index);
        } 
        return ext;
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
     * Derives the package name from the class object and prepends it
     * with "debug".
     *
     * @param cls a class object
     * @return the package name prepended with 'debug'.
     */
    private static String getName(Class cls) {
        Package pkg = cls.getPackage();
        String packageName = (pkg == null) ? "com.sun.portal.portletcontainer.warupdater" : pkg.getName();
        StringBuffer csuffix = new StringBuffer();
        csuffix.append(DEBUG_NAME);
        csuffix.append(".");
        csuffix.append(packageName);
        return csuffix.toString();
    }
    
    public static void main(String args[]){
        System.out.println("Suffix for abc.zyz is " + getFileSuffix("abc.xyz"));
        System.out.println("Suffix for 123 is " +getFileSuffix("123"));
    }
}
