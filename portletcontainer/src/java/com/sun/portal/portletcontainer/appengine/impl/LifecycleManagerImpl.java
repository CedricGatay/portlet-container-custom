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


package com.sun.portal.portletcontainer.appengine.impl;

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.portletcontainer.appengine.PortletAppEngineUtils;
import java.io.IOException;
import javax.portlet.PortalContext;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PreferencesValidator;
import javax.portlet.UnavailableException;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.portal.portletcontainer.appengine.LifecycleManager;
import com.sun.portal.portletcontainer.appengine.LifecycleManagerException;
import com.sun.portal.portletcontainer.common.descriptor.DeploymentDescriptorException;
import com.sun.portal.portletcontainer.common.descriptor.DeploymentDescriptorReader;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletPreferencesDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletsDescriptor;
import com.sun.portal.portletcontainer.portlet.impl.PortalContextImpl;
import com.sun.portal.portletcontainer.portlet.impl.PortletConfigImpl;
import com.sun.portal.portletcontainer.portlet.impl.PortletContextImpl;
import com.sun.portal.portletcontainer.appengine.filter.FilterChainFactory;
import com.sun.portal.portletcontainer.portlet.impl.PortletRequestConstants;
import java.util.logging.LogRecord;
import javax.portlet.filter.FilterChain;
import javax.servlet.ServletConfig;

/**
 * The lifecycle manager impl class is the implementation of lifecycle
 * manager. It does the real work of creating objects maintained by the
 * lifecycle manager.
 * <P>
 * There are three objects the lifecycle manager maintains:
 * <UL>
 *    <LI>The deployment descriptor
 *    <LI>The portlet context
 *    <LI>The portlet objects
 * </UL>
 * The clients can call the following interface to get these three objects in the
 * lifecycle of the portlet application:
 * <UL>
 *    <LI><code>getPortletAppDescriptor()</code>: gets the deployment descriptor
 *    <LI><code>getPortlet(String portletName)</code>: gets the specific portlet
 *    <LI><code>getPortletContext(InputStream in)</code>: gets the portlet context
 *    <LI><code>getPreferencesValidator(String portletName)</code>:
 * gets the preferences validator if it is defined in the deployment descriptor
 * </UL>
 */
public class LifecycleManagerImpl implements LifecycleManager {
    
    private PortletAppDescriptor portletAppDescriptor;
    private Map<String, Object> portletExtensionDescriptors;
    private PortletContext portletContext;
    private Map<String, Portlet> portlets = new HashMap<String, Portlet>();
    private Map<String, PortletConfig> portletConfigs = new HashMap<String, PortletConfig>();
    private Map<String, PreferencesValidator> validators = new HashMap<String, PreferencesValidator>();
    private PortalContext portalContext;
    private FilterChainFactory filterChainFactory = null;
    
    private static Logger logger = ContainerLogger.getLogger(LifecycleManagerImpl.class, "PAELogMessages");
    /**
     * Initialize the portlet context object and the deployment descriptor
     * object.
     * <P>
     * @param config The servlet config
     */
    public LifecycleManagerImpl(ServletConfig config) {
		ServletContext context = config.getServletContext();
        //reads in portlet deployment descriptor
        InputStream portletXmlStream =
                context.getResourceAsStream("/WEB-INF/portlet.xml");
        
        //reads in web deployment descriptor
        InputStream webXmlStream =
                context.getResourceAsStream("/WEB-INF/web.xml");
        
        portletAppDescriptor = initPortletAppDescriptor(context, portletXmlStream, webXmlStream);
        
        //Close the streams as it is no longer required
        try {
            portletXmlStream.close();
            webXmlStream.close();
        } catch (IOException ex) {
            // Ignored
        }
        
        // get vendor portlet xml streams
        portletExtensionDescriptors = initPortletExtensionDescriptor(context);
        //creates portal context
        portalContext = initPortalContext();
        
        if ( portletAppDescriptor != null ) {
            
            //creates portlet context
            portletContext = initPortletContext( context );
            
			//Set ServletConfig as an attribute 
			portletContext.setAttribute(PortletRequestConstants.SERVLET_CONFIG, 
					config);
			
            //create portlets
            createPortlets();
            
            //create FilterChainFactory
            filterChainFactory = new FilterChainFactory(portletAppDescriptor, portletContext);
        }
        
    }
    
    /*
     * Initialize the portlet context object.
     * <P>
     */
    private PortletContext initPortletContext( ServletContext context ) {
        return new PortletContextImpl(context, portletAppDescriptor);
    }
    
    /*
     * Initialize the portal context object.
     * <P>
     */
    private PortalContext initPortalContext() {
        return new PortalContextImpl();
    }
    
    /*
     * Initialize the deployment descriptor object.
     * <P>
     */
    private PortletAppDescriptor initPortletAppDescriptor(ServletContext context,
            InputStream portletXmlStream, InputStream webXmlStream) {
        String portletAppName = PortletAppEngineUtils.getPortletAppName(context);
        logger.log(Level.INFO, "PSPL_PAECSPPAI0015", portletAppName);
        DeploymentDescriptorReader reader = new DeploymentDescriptorReader(context);
        
        try {
            portletAppDescriptor = reader.getPortletAppDescriptor(portletAppName, portletXmlStream, false);
            reader.readWebAppDescriptor(portletAppDescriptor, webXmlStream);
        } catch (DeploymentDescriptorException de) {
            // do nothing, null will be returned
        }
        return portletAppDescriptor;
    }
    
    /*
     * Initialize the vendor deployment descriptor objects.
     * <P>
     */
    private Map<String, Object> initPortletExtensionDescriptor(ServletContext context) {
        DeploymentDescriptorReader reader = new DeploymentDescriptorReader(context);
        
        try {
            portletExtensionDescriptors = reader.getPortletExtensionDescriptors(context);
        } catch (DeploymentDescriptorException de) {
            // do nothing, null will be return
        }
        return portletExtensionDescriptors;
    }
    
    public PortletAppDescriptor getPortletAppDescriptor() {
        return portletAppDescriptor;
    }
    
    public Object getPortletExtensionDescriptor(String vendorPortletXMLName) {
        return portletExtensionDescriptors.get(vendorPortletXMLName);
    }
    
    public Portlet getPortlet( String portletName ) throws LifecycleManagerException {
        Portlet p = portlets.get(portletName);
        
        if (p == null) {
            logger.log(Level.SEVERE, "PSPL_PAECSPPAI0009", portletName);
            throw new LifecycleManagerException("Cannot get the portlet:" + portletName);
        }
        
        return p;
    }
    
    public PreferencesValidator getPreferencesValidator( String portletName ) {
        return validators.get(portletName);
    }
    
    public void removePortlet( String portletName ) {
        Portlet p = portlets.get( portletName );
        if ( p != null ) {
            synchronized ( portlets ) {
                portlets.put( portletName, null );
            }
        }
    }
    
    public PortletConfig getPortletConfig( String portletName ) {
        PortletConfig config = portletConfigs.get(portletName);
        
        if (config == null) {
            logger.log(Level.WARNING, "PSPL_PAECSPPAI0014", portletName);
        }
        
        return config;
    }
    
    public void removePortletConfig( String portletName ) {
        PortletConfig config = portletConfigs.get( portletName );
        if ( config != null ) {
            synchronized ( portletConfigs ) {
                portletConfigs.put( portletName, null );
            }
        }
        
    }
    
    public PortletContext getPortletContext() {
        return portletContext;
    }
    
    public PortalContext getPortalContext() {
        return portalContext;
    }
    
    /**
     * Clears the portlet context object and the deployment descriptor object.
     * Calls the <code>destroy()</code> method for each portlet object and
     * remove it from the portlet object map.
     */
    public void destroy() {
        portletAppDescriptor = null;
        portletContext = null;
        
        //calls portlets' destroy method
        for (Iterator i = portlets.keySet().iterator(); i.hasNext(); ) {
            String portletName = (String)i.next();
            Portlet p = portlets.get(portletName);
            try {
                p.destroy();
            } catch ( RuntimeException re ) {
				if(logger.isLoggable(Level.INFO)) {
					logger.log(Level.INFO, "PSPL_PAECSPPAI0010",
						new Object[] { portletName, re.getMessage()});
				}
            }
        }
        portlets.clear();
        portletConfigs.clear();
        validators.clear();
        filterChainFactory.clear();
    }
    
    /*
     * Creates a portlet object using the portlet class name defined in the
     * deployment descriptor.
     */
    private Portlet createPortlet( String portletName,
            PortletDescriptor portletDescriptor )
            throws LifecycleManagerException {
        
        String portletClassName = portletDescriptor.getClassName();
        Portlet portlet;
        PreferencesValidator validator = null;
        
        if (portletClassName == null) {
            throw new LifecycleManagerException("LifecycleManagerImpl.getPortlet(), the portlet class name is not defined for portlet: " + portletName);
        } else {
            logger.log(Level.FINE, "PSPL_PAECSPPAI0011", portletClassName );
        }
        
        try {
            Class portletClass = Thread.currentThread().getContextClassLoader().loadClass(portletClassName);
            portlet = (Portlet)(portletClass.newInstance());
            PortletPreferencesDescriptor pPrefDescriptor =
                    portletDescriptor.getPortletPreferencesDescriptor();
            if ( pPrefDescriptor != null ) {
                validator =
                        instantiatePreferencesValidator(pPrefDescriptor.getPreferencesValidatorName());
                if ( validator != null ) {
                    validators.put(portletName, validator);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            throw new LifecycleManagerException(cnfe);
        } catch (IllegalAccessException iae) {
            throw new LifecycleManagerException(iae);
        } catch (ClassCastException cce) {
            throw new LifecycleManagerException(cce);
        } catch (InstantiationException ie) {
            throw new LifecycleManagerException(ie);
        }
        
        return portlet;
        
    }
    
    /*
     * Go through the deployment descriptor and reads all the portlets,
     * create and initialized them at one shot.
     * <P>
     * This method is called from the init() method of this class, which
     * is called only once. By creating all the portlet instances
     * at one shot, we avoid the synchronization issue about putting and
     * getting one portlet instance from different threads, we also don't
     * need to worry about the potential of creating multiple instances of
     * a portlet in differnt threads.
     * <P>
     * There may be concern if there's a lot of portlets defined in the
     * deployment descriptor, and if all of them are generated from the
     * beginning and kept in memory, there is potential memory problem.
     */
    private void createPortlets() {
        PortletsDescriptor portletsDescriptor = null;
        if ( portletAppDescriptor != null ) {
            portletsDescriptor = portletAppDescriptor.getPortletsDescriptor();
        }
        if ( portletsDescriptor != null ) {
            List<String> portletNames = portletsDescriptor.getPortletNames();
            for (String portletName : portletNames) {
                PortletDescriptor portletDescriptor = portletsDescriptor.getPortletDescriptor( portletName );
                if ( portletDescriptor != null ) {
                    PortletConfig config = new PortletConfigImpl(portletAppDescriptor, portletDescriptor, portletName, portletContext);
                    try {
                        Portlet p = createPortlet( portletName, portletDescriptor );
                        p.init( config );
                        portlets.put( portletName, p );
                        portletConfigs.put( portletName, config );
                        logger.log(Level.INFO, "PSPL_PAECSPPAI0012",portletName );
                    } catch ( LifecycleManagerException lcme ) {
                        if(logger.isLoggable(Level.SEVERE)) {
                            logger.log(getLogRecord(portletName, lcme));
                        }
                    } catch ( UnavailableException ue ) {
                        if(logger.isLoggable(Level.SEVERE)) {
                            logger.log(getLogRecord(portletName, ue));
                        }
                    } catch ( PortletException pe ) {
                        if(logger.isLoggable(Level.SEVERE)) {
                            logger.log(getLogRecord(portletName, pe));
                        }
                    } catch (Throwable t) {
                        if(logger.isLoggable(Level.SEVERE)) {
                            logger.log(getLogRecord(portletName, t));
                        }
                    }
                }
            }
        }
    }
    
    /*
     * Instantiates the validator class for this portlet. Returns null
     * if having problem instantiates the class.
     */
    private PreferencesValidator instantiatePreferencesValidator( String validatorName ) {
        
        PreferencesValidator validator = null;
        
        if ( validatorName != null ) {
            try {
                //validator = (PreferencesValidator)(Class.forName(validatorName).newInstance());
                Class validatorClass = Thread.currentThread().getContextClassLoader().loadClass(validatorName);
                validator = (PreferencesValidator)(validatorClass.newInstance());
            } catch (ClassNotFoundException cnfe) {
                // will return null
            } catch (IllegalAccessException iae) {
                // will return null
            } catch (ClassCastException cce) {
                // will return null
            } catch (InstantiationException ie) {
                // will return null
            } catch (SecurityException se) {
                // will return null
            }
        }
        
        return validator;
    }
    
    public FilterChain getFilterChain(String portletName, Portlet p, String lifeCycle){
        return filterChainFactory.getPortletFilterChain(portletName, p, lifeCycle);
    }
    
    private LogRecord getLogRecord(String parameter, Throwable cause){
        LogRecord logRecord = new LogRecord(Level.SEVERE, "PSPL_PAECSPPAI0013");
        logRecord.setParameters(new String[] { parameter });
        logRecord.setThrown(cause);
        logRecord.setLoggerName(logger.getName());
        return logRecord;
    }
}
