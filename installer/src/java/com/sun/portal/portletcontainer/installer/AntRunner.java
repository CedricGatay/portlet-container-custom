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

package com.sun.portal.portletcontainer.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger ;

public class AntRunner
{

    private static Logger logger = Logger.getLogger(AntRunner.class.getPackage().getName(),
            "PCSDKLogMessages");
    private static Logger configuratorLogger = Logger.getLogger("com.sun.portal.portletcontainer.configurator",
            "PCSDKLogMessages");
    private static final String space = " ";
    /**
     * Run a target in an ant file.
     *
     * @param appserverInstallDir the Application Server installation directory
     * @param antfile the name of the ant file.
     * @param targetname the target you want to run
     * @param propertyFileName the property file to use when executing ant
     * @return
     */
    public static void runAnt(String appserverInstallDir, String antfile,
            String targetname, String propertyFileName, String logfileName) throws Exception
    {
        String antHome = null;
        String asant = null;
        String antLauncher = null;

        if (System.getProperty("os.name").indexOf("Windows") != -1) {

            antHome = appserverInstallDir + File.separator +
                        "lib" + File.separator +
                        "ant";

            antLauncher = antHome + File.separator
                            + "lib" + File.separator +
                            "ant-launcher.jar";

            //if appserver installdir has spaces, deal with it
            //surround the entire command with quotes
            if(appserverInstallDir.indexOf(' ') != -1) {
                    antHome = "\"" + antHome + "\"";
            }

            if(appserverInstallDir.indexOf(' ') != -1) {
                    antLauncher = "\"" + antLauncher + "\"";
            }

            String asJdk = getAppserverJDK(appserverInstallDir, true);
            String asJava = asJdk + File.separator
                     + "bin" + File.separator
                     + "java.exe";

             if(asJdk.indexOf(' ') != -1) {
                    asJava = "\"" + asJava + "\"";
             }

            String javaHome = asJdk;
            if(asJdk.indexOf(' ') != -1) {
                javaHome = "\"" + asJdk + "\"";
            }

              asant = asJava + space
                     + "-classpath" + space
                     + antLauncher  + space
                     + "-Dant.home="  + antHome + space
                     + "-Djava.home=" + javaHome + space
                     + "org.apache.tools.ant.launch.Launcher" + space
                    ;
         }//windows
         else {
            asant =  appserverInstallDir + File.separator + "bin" +
                    File.separator + "asant ";
        }
        executeAntTask(antfile, targetname, asant, propertyFileName, logfileName);
    }

    /**
     * Executes the ant task provided the ant command. This can be directly called
     * if the Ant home is known, by constructing the ant command.
     *
     * @param antfile The ANT build script file
     * @param targetName The name of the ANT target to execute.
     * @param antCommand The string containing the actual ANT command that needs to be executed.
     * @param propertyFileName The complete path of the property file which is passed as an argument to the ant command.
     * @param logfileName The name of the log file to be created which contains the output from the ANT command.
     */
    public static void executeAntTask(String antfile, String targetName,
            String antCommand, String propertyFileName, String logfileName) throws Exception {

        String antfilePath = new File(antfile).getPath();

        if(System.getProperty("os.name").indexOf("Windows") != -1 &&
                antfilePath.indexOf(" ") != -1) {
            antfilePath = "\"" + antfilePath + "\"";
        }

		String noConfig = "";
		if (System.getProperty("os.name").indexOf("Windows") == -1) {
			noConfig = "--noconfig";
		}
        String arguments =
                "-f" + space +
                 antfilePath
                 + space +
                "-propertyfile " + propertyFileName + space +
                targetName;

        Runtime runtime = Runtime.getRuntime();
        String command = antCommand + space + noConfig + space + arguments;
        configuratorLogger.log(Level.INFO, "command-log", command);
        Process process = runtime.exec(command);
        BufferedReader reader = null;
        File logFile = null;
        PrintWriter logWriter = null;
        String line;
        try {
            reader = new BufferedReader(new InputStreamReader( process.getInputStream()));
            logFile = new File(logfileName);
            logWriter = new PrintWriter(new FileWriter(logFile));
            while((line = reader.readLine()) != null) {
                logWriter.println(line);
            }
        } catch(IOException ioe) {
            logger.log(Level.SEVERE, "error-writing-log", ioe);
        } finally {
            if(logWriter != null) {
                logWriter.close();
            }
            if(reader != null) {
                reader.close();
            }
        }


        BufferedReader errorReader = null;
        try{
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            logFile = new File(logfileName+".err");
            logWriter = new PrintWriter(new FileWriter(logFile));
            while((line = errorReader.readLine()) != null) {
                logWriter.println(line);
            }
        } catch (IOException ioe) {
           logger.log(Level.SEVERE, "error-writing-log", ioe);
        } finally {
            logWriter.close();
            errorReader.close();
        }
    }

    /**
     * This method returns the JAVA_HOME for the JDK
     * used by the appserver
     */
     public static String getAppserverJDK(String installDir, boolean isWindows) {
        BufferedReader bf = null;
        InputStream in = null;
        String javaHome = "";
        try {
            String asenv="";
            if(!isWindows)
                asenv = installDir + File.separator + "config" + File.separator +
                        "asenv.conf";
            else
             asenv = installDir + File.separator + "config" + File.separator +
                     "asenv.bat";

            in = new FileInputStream(asenv);

            bf = new BufferedReader(new InputStreamReader(in));
            String line = bf.readLine();
            while(line != null) {
                if(line.indexOf("AS_JAVA") != -1) {
                    int pos = line.indexOf("=");
                    if (pos > 0) {
                        String lhs = (line.substring(0, pos)).trim();
                        String rhs = (line.substring(pos + 1)).trim();

                        if (isWindows) {    //trim off the "set "
                            lhs = (lhs.substring(3)).trim();
                        }

                        if (!isWindows) {      // take the quotes out
                            pos = rhs.indexOf("\"");
                            if(pos != -1) {
                                rhs = (rhs.substring(pos+1)).trim();
                                pos = rhs.indexOf("\"");
                                if(pos != -1)
                                rhs = ( rhs.substring(0, pos)).trim();
                            }

                        }
                        javaHome = rhs;
                        break;
                    }
                }
                line = bf.readLine();
             }
        } catch(Exception e) {
           return installDir + File.separator + "java";
        } finally {
            if(bf != null) {
                try {
                    bf.close();
                } catch (IOException ex) {
                   // Ignore this
                }
            }
            if(in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    // Ignore this
                }
            }
        }
        return javaHome;
     }

}