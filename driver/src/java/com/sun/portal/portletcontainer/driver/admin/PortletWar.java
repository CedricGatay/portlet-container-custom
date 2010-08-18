package com.sun.portal.portletcontainer.driver.admin;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortletWar {
    private File warFile;
    
    private File markerFile;
    
    private static Logger logger = Logger.getLogger(PortletWar.class.getPackage().getName(), "PCDLogMessages");
    
    protected static final int NUM_RETRIES = 3;
    
    public PortletWar(String portletWarFileName) {
        this(new File(portletWarFileName));
    }
    
    public PortletWar(File war) {
        warFile = war;
        markerFile = new File(warFile.getAbsolutePath() + WarFileFilter.WAR_DEPLOYED_EXTENSION);
    }
    
    public void deploy() {
        
        if (isWarInValidState(warFile)) {
            
            boolean success = false;
            String absolutePath = warFile.getAbsolutePath();
            try {
                PortletAdminData portletAdminData = PortletAdminDataFactory.getPortletAdminData();
                success = portletAdminData.deploy(absolutePath, true);
                createDeployedMarkerFile();
            } catch (Exception ex) {
                // Deploy to portlet container failed, so
                // undeploy it and cleanup
                logger.log(Level.INFO, "PSPCD_CSPPD0035", ex);
                try {
                    undeploy();
                } catch (Exception ex1) {
                    // ignored
                }
            }
        }
    }
    
    private void createDeployedMarkerFile() {
        try {
            markerFile.createNewFile();
        } catch (IOException e) {
            logger.log(Level.INFO, "PSPCD_CSPPD0037", new String[] { warFile.getAbsolutePath() });
        }
    }
    
    private boolean isWarInValidState(File file) {
        // If you can open the WAR and enumerate the entries without getting an
        // exception
        // we assume that the war is in a valid state and we can start
        // masssaging the war
        // and reading it now.
        // This method will do atleast 3 retries to check this.
        int i = 0;
        while (i < NUM_RETRIES) {
            JarFile war;
            try {
                war = new JarFile(file);
                war.entries();
                war.close();
                return true;
            } catch (IOException e) {
                // Log the message and wait a few seconds
                logger.log(Level.INFO, "PSPCD_CSPPD0036", new String[] { "" + i });
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e1) {
                }
            }
            i++;
        }
        return false;
    }
    
    public boolean isDeployed() {
        // If in the directory of this war file there is a file called
        // warName.deployed
        // then it has already been deployed.
        if (markerFile.exists())
            return true;
        else
            return false;
    }
    
    public void undeploy() throws Exception {
        // Undeploy portlet and then remove the marker file
        PortletAdminData portletAdminData = PortletAdminDataFactory.getPortletAdminData();
        portletAdminData.undeploy(getWarName(), true);
        if (markerFile.exists())
            markerFile.delete();
    }
    
    public boolean warFileExists() {
        return warFile.exists();
    }
    
    public String getWarName() {
        String regexp = WarFileFilter.WAR_EXTENSION + "$";
        return warFile.getName().replaceFirst(regexp, "");
    }
    
    public boolean needsRedeploy() {
        // if the timestamp of the war file is more than the timestamp of the .deploy
        // then this portlet needs redeploy
        if ( warFile.lastModified() > markerFile.lastModified())
            return true;
        else
            return false;
    }
    
    public void redeploy() throws Exception {
        undeploy();
        deploy();
    }
}
