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


package com.sun.portal.portletcontainer.invoker;

import com.sun.portal.container.ChannelMode;
import com.sun.portal.container.ChannelURLFactory;
import com.sun.portal.container.ChannelURLType;
import com.sun.portal.container.Container;
import com.sun.portal.container.ContainerException;
import com.sun.portal.container.ContentException;
import com.sun.portal.container.EntityID;
import com.sun.portal.container.ErrorCode;
import com.sun.portal.container.ChannelState;
import com.sun.portal.container.ContainerFactory;
import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.ContainerRequest;
import com.sun.portal.container.ContainerResponse;
import com.sun.portal.container.ContainerType;
import com.sun.portal.container.ContainerUtil;
import com.sun.portal.container.ExecuteActionRequest;
import com.sun.portal.container.ExecuteActionResponse;
import com.sun.portal.container.GetMarkupRequest;
import com.sun.portal.container.GetMarkupResponse;
import com.sun.portal.container.GetResourceRequest;
import com.sun.portal.container.GetResourceResponse;
import com.sun.portal.container.PortletID;
import com.sun.portal.container.PortletType;
import com.sun.portal.container.PortletWindowContext;
import com.sun.portal.container.PortletWindowContextException;
import com.sun.portal.container.WindowRequestReader;
import com.sun.portal.container.PortletWindowContextAbstractFactory;
import com.sun.portal.container.PortletWindowContextFactory;
import com.sun.portal.portletcontainer.ccpp.DefaultCCPPProfile;
import com.sun.portal.portletcontainer.invoker.util.InvokerUtil;
import com.sun.portal.portletcontainer.invoker.util.PortletWindowRules;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.ccpp.Profile;
import javax.ccpp.ProfileFactory;
import javax.ccpp.ValidationMode;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;


/**
 * This class is responsible for rendering the portlet markup fragments.
 * It retrieves the portlet markup fragments by delegating the portlet
 * execution to the a container implementation.
 * 
 */
public abstract class WindowInvoker {
    
    private static final Profile DEFAULT_CCPP_PROFILE = new DefaultCCPPProfile();
    private static final String CCPP_RI_PROFILE_FACTORY = "com.sun.ccpp.ProfileFactoryImpl"; 
    private static final String ERROR_CODE = "errorCode";
    private static final String WINDOW_INVOKER_RB_NAME = "WindowInvoker";
    
    public static final List localParamKeyList = initParamKeyList();
    
    private String title = "";
    private HttpServletRequest origRequest;
    private HttpServletResponse origResponse;
    private PortletWindowContext portletWindowContext;
    private String portletWindowName;
    private ChannelMode portletWindowMode;
    private ChannelState portletWindowState;
    private ServletContext servletContext;
    private ResponseProperties responseProperties;
	private List<EntityID> eventUpdatedPortlets;
	private Map<EntityID, ChannelState> eventUpdatedPortletsState;
    private static ResourceBundle windowInvokerRB;
    
    //---
    // Debug logger
    //---
    private static Logger logger = ContainerLogger.getLogger(WindowInvoker.class, "PCCTXLogMessages");
    
    //------------------------------------------------------------------
    //
    // Abstract methods to be implemented by the sub-class
    //
    //-----------------------------------------------------------------
    abstract public List getRoleList(HttpServletRequest request) throws InvokerException ;
    
    abstract public Map getUserInfoMap(HttpServletRequest request) throws InvokerException ;
    
    abstract public EntityID getEntityID(HttpServletRequest request) throws InvokerException;
    
    abstract public WindowRequestReader getWindowRequestReader() throws InvokerException;

    abstract public Container getContainer();
    
    abstract public ChannelURLFactory getPortletWindowURLFactory(
            String desktopURLPrefix,
            HttpServletRequest request)
            throws InvokerException;
    
    abstract public boolean isMarkupSupported(String contentType,
            String locale,
            ChannelMode mode,
            ChannelState state)
            throws InvokerException;
    abstract public String getDefaultTitle() throws InvokerException;
    
    //***************************************************************** //
    // MAIN METHODS FOR GETTING render
    //
    //******************************************************************
    
    /**
     * Initializes the WindowInvoker.
     * <p>
     *
     * @param servletContext The ServletContext object.
     * @param request The HTTP request object.
     * @param response The HTTP response object.
     * @throws com.sun.portal.portletcontainer.invoker.InvokerException 
     */
    public void init(ServletContext servletContext, HttpServletRequest request,
            HttpServletResponse response)
            throws InvokerException {
        this.origRequest = request;
        this.origResponse = response;
        this.servletContext = servletContext;
        try {
            PortletWindowContextAbstractFactory afactory = new PortletWindowContextAbstractFactory();
            PortletWindowContextFactory factory = afactory.getPortletWindowContextFactory();
            this.portletWindowContext = factory.getPortletWindowContext(request);
            this.responseProperties = new ResponseProperties();
        } catch (PortletWindowContextException  pwce){
            throw new InvokerException("Initialization of WindowInvoker failed" , pwce);
        }
    }
    
    
    public String getPortletWindowName() {
        return this.portletWindowName;
    }
    
    public void setPortletWindowName(String portletWindowName) {
        this.portletWindowName = portletWindowName;
    }
    
    protected HttpServletRequest getOriginalRequest() {
        return this.origRequest;
    }
    
    protected HttpServletResponse getOriginalResponse() {
        return this.origResponse;
    }
    
    protected ServletContext getServletContext() {
        return this.servletContext;
    }
    
    public ChannelMode getPortletWindowMode() {
        return this.portletWindowMode;
    }
    
    public void setPortletWindowMode(ChannelMode portletWindowMode) {
        this.portletWindowMode = portletWindowMode;
    }
    
    public ChannelState getPortletWindowState() {
        return this.portletWindowState;
    }
    
    public void setPortletWindowState(ChannelState portletWindowState) {
        this.portletWindowState = portletWindowState;
    }
    
    /**
     * Gets the content for the portletWindow based on mode from the underlying
     * container. This method is called to get content for VIEW, EDIT and
     * and HELP mode .
     *
     * This method sets all the necessary attributes in the ContainerRequest
     * and Container Response and calls the configured container to get
     * the content for the portlet.
     *
     *
     * @param request An HttpServletRequest that contains
     * information related to this request for content.
     * @param response An HttpServletResponse that allows the portlet window context
     * to influence the overall response for the page (besides generating the content).
     * @return StringBuffer holding the content.
     * @exception InvokerException If there was an error generating the
     * content.
     */
    
    
    public StringBuffer render(HttpServletRequest request,
            HttpServletResponse response) throws InvokerException {
        
        StringBuffer markupText = null;
        
        
        ErrorCode errorCode = readErrorCode(request);
        if (errorCode != null) {
            //
            // First, check if request is to report errors that processAction
            // might have ran in to, prior to render() request
            // Or due to explicit invocation of ErrorURL.
            logger.log(Level.FINE, "PSPL_PCCTXCSPPCI0001", errorCode);
            markupText = getErrorMessageContent(errorCode, request.getLocale());
            // Since the error message is being sent, set the default title for this portlet
            setTitle(getDefaultTitle());
        } else {
            //
            // Not an error case, get the normal content
            //
            
            try {
                markupText = getPortletContent(request, response);
            } catch(WindowException we) {
                logger.log(Level.SEVERE, "PSPL_PCCTXCSPPCI0006", we.getMessage());
                markupText =  getErrorMessageContent(we.getErrorCode(), request.getLocale());
            } catch(InvokerException ie) {
                logger.log(Level.SEVERE, "PSPL_PCCTXCSPPCI0006", ie.getMessage());
                markupText =  getErrorMessageContent(WindowErrorCode.CONTAINER_EXCEPTION, request.getLocale());
            }
        }
        
        return markupText;
    }

    public Profile getCCPPProfile(HttpServletRequest request) {
        Profile profile = (Profile)request.getAttribute("CCPPProfile_OSPC");

        if (profile == null) {
            ProfileFactory profileFactory = ProfileFactory.getInstance();

            if (profileFactory == null) {
                try{
                    Class ccppri_ProfileFactoryImpl = 
                            Class.forName(CCPP_RI_PROFILE_FACTORY);
                    Method method = ccppri_ProfileFactoryImpl.getDeclaredMethod("getInstance");
                    profileFactory = (ProfileFactory)method.invoke(ccppri_ProfileFactoryImpl);

                    ProfileFactory.setInstance(profileFactory);
                    profile = profileFactory.newProfile(
                            request, ValidationMode.VALIDATIONMODE_NONE);

                }catch(Exception e){
                    if(logger.isLoggable(Level.FINEST)) {
                        logger.log(Level.FINEST, "PSPL_PCCTXCSPPCI0020", e);
                    }
                }
            }else{
                    profile = profileFactory.newProfile(
                            request, ValidationMode.VALIDATIONMODE_NONE);                
            }

            if (profile == null) {
                profile = DEFAULT_CCPP_PROFILE;
            }
            request.setAttribute("CCPPProfile_OSPC", profile);
        }

        return profile;
    }

    private String getPortalInfo(Locale locale) {
        ResourceBundle bundle = getWindowInvokerBundle(locale);
        String portalInfo = null;
        if(bundle != null) {
            portalInfo = bundle.getString("PORTAL_INFO");
        }
        return portalInfo;
    }
    
    
    private StringBuffer getPortletContent(HttpServletRequest request,
            HttpServletResponse response)
            throws InvokerException, WindowException {
        
        //
        // We need to know if it is a authless user, because
        // parameters are stored in client properties or
        // in session properties based on whether this use is authless or
        // not.
        
        boolean authless = getPortletWindowContext().isAuthless(request);
        
        
        //
        // Abstract method to be implemented by the derived class
        //
        
        EntityID portletEntityId = getEntityID(request);
        
        //
        // Get current portlet mode
        //
        
        ChannelMode currentPortletWindowMode = getCurrentPortletWindowMode(request);
        
        //
        // Get current window state
        
        
        ChannelState currentWindowState = getCurrentWindowState(request);
        
        
        //
        // Get list of allowed window states
        //
        List allowableWindowStates = getAllowableWindowStates(request, currentPortletWindowMode);
        
        //
        // Get list of allowed portletWindow modes
        //
        List allowablePortletWindowModes =
                PortletWindowRules.getAllowablePortletWindowModes(currentPortletWindowMode, authless);
        
        String processURL = getActionURL(request, currentPortletWindowMode, currentWindowState);
        
        // Request
        GetMarkupRequest getMarkupRequest = 
                      getContainer().createGetMarkUpRequest(request,
                              portletEntityId, 
                              currentWindowState, 
                              currentPortletWindowMode,
                              portletWindowContext, 
                              getPortletWindowURLFactory(processURL, request));
        populateContainerRequest(
                getMarkupRequest,
                request,
                allowableWindowStates,
                allowablePortletWindowModes);

		getMarkupRequest.setPortletNamespaces(getPortletNamespaces(getMarkupRequest));
        // Response
        GetMarkupResponse getMarkupResponse = getContainer().createGetMarkUpResponse(response);
        
        //
        // Call the container interface
        //
        try {
            getContainer().getMarkup(getMarkupRequest, getMarkupResponse);
        } catch (ContainerException ce) {
            // If exception set the default title
            setTitle(getDefaultTitle());
            if(logger.isLoggable(Level.WARNING)){
                LogRecord logRecord = new LogRecord(Level.WARNING, "PSPL_PCCTXCSPPCI0006");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[]{getPortletWindowName()});
                logRecord.setThrown(ce);
                logger.log(logRecord);
            }
            throw new InvokerException(
                    "Container exception", ce);
        } catch (ContentException cte) {
            // If exception set the default title
            setTitle(getDefaultTitle());
            if(logger.isLoggable(Level.WARNING)){
                LogRecord logRecord = new LogRecord(Level.WARNING, "PSPL_PCCTXCSPPCI0006");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[]{getPortletWindowName()});
                logRecord.setThrown(cte);
                logger.log(logRecord);
            }
            throw new WindowException(getErrorCode(cte),
                    "Content Exception",
                    cte);
        }
        
        //
        // save the title so getTitle can return it
        //
        
        setTitle(getMarkupResponse.getTitle());
        
        
        //
        // Process the markup based on the mode.
        //
        
        if (getMarkupResponse.getMarkup() == null) {
            logger.info("PSPL_PCCTXCSPPCI0007");
        }

		setResponseProperties(getMarkupResponse);
        
        return getMarkupResponse.getMarkup();
        
    }
    
    
    //****************************************************************
    //
    // MAIN METHODS FOR processAction
    //
    //***************************************************************
    
    /**
     * Invokes the processAction of the portlet.
     *
     * This method sets all the necessary attributes in the ContainerRequest
     * and Container Response and calls the configured container to invoke
     * the processAction of the portlet.
     *
     *
     * @param request An HttpServletRequest that contains
     * information related to this request for content.
     * @param response An HttpServletResponse that allows the portlet window context
     * to influence the overall response for the page (besides generating the content).
     * @return URL redirect URL
     * @exception InvokerException If there was an error generating the
     * content.
     */
    public URL processAction(HttpServletRequest request,
            HttpServletResponse response) throws InvokerException {
        
        try {
            return processActionInternal(request, response);
        } catch(WindowException we) {
            logger.log(Level.SEVERE, "PSPL_PCCTXCSPPCI0008", we.getMessage());
            return getErrorCodeURL(we.getErrorCode(), request);
        }
    }
    
    public URL processActionInternal(HttpServletRequest request,
            HttpServletResponse response) throws InvokerException,
            WindowException{
        
        URL returnURL = null;
        ChannelMode currentPortletWindowMode = null;
        ChannelMode newPortletWindowMode = null;
        ChannelState currentWindowState = null;
        ChannelState newWindowState = null;
        
        //
        // We need to know if it is a authless user, because
        // parameters are stored in client properties or
        // in session properties based on whether this use is authless or
        // not.
        
        boolean authless = getPortletWindowContext().isAuthless(request);
        
        //
        // Abstract method to be implemented by the derived class
        //
        EntityID portletEntityId = getEntityID(request);
        
        
        //
        // Get current portletWindow mode and window state
        //
        currentPortletWindowMode = getCurrentPortletWindowMode(request);
        
        currentWindowState = getCurrentWindowState(request);
        
        
        //
        // get new portletWindow mode and window state set on the url.
        //
        newWindowState =
                getWindowRequestReader().readNewWindowState(request);
        
        newPortletWindowMode =
                getWindowRequestReader().readNewPortletWindowMode(request);
        
        
        //
        // Process only the window state.
        // Won't process new ChannelMode, just check validity,
        // as an optimization
        // since it might change again after the processAction is called
        
        if (newPortletWindowMode != null) {
            validateModeChange(currentPortletWindowMode,
                    newPortletWindowMode,
                    authless);
            currentPortletWindowMode = newPortletWindowMode;
        }
        if ( newWindowState != null) {
            currentWindowState = processWindowStateChange(request,
                    newWindowState,
                    currentPortletWindowMode,
                    authless);
        }
        
        //
        // See what kind of URL is it
        
        ChannelURLType urlType = getWindowRequestReader().readURLType(request);
        Container c = ContainerFactory.getContainer(ContainerType.PORTLET_CONTAINER);
        String processURL = getActionURL(request, currentPortletWindowMode, currentWindowState);
        // Request
        ExecuteActionRequest executeActionRequest = 
                      getContainer().createExecuteActionRequest(request,
                              portletEntityId, 
                              currentWindowState, 
                              currentPortletWindowMode,
                              portletWindowContext, 
                              getPortletWindowURLFactory(processURL, request),
                              getWindowRequestReader());
        
        List allowableWindowStates = getAllowableWindowStates(request, currentPortletWindowMode);
        List allowablePortletWindowModes = PortletWindowRules.getAllowablePortletWindowModes(currentPortletWindowMode, authless);
        populateContainerRequest(
                executeActionRequest,
                request,
                allowableWindowStates,
                allowablePortletWindowModes);

        executeActionRequest.setPortletNamespaces(getPortletNamespaces(executeActionRequest));
		executeActionRequest.setPortletWindowIDs(getPortletWindowIDs(executeActionRequest));

        // Response
        ExecuteActionResponse executeActionResponse = getContainer().createExecuteActionResponse(response);
        
		//
		// Call the container implementation to executeAction
		//
		try {
			getContainer().executeAction(executeActionRequest, executeActionResponse, urlType );
		} catch (ContainerException ce) {
			if(logger.isLoggable(Level.WARNING)){
				LogRecord logRecord = new LogRecord(Level.WARNING, "PSPL_PCCTXCSPPCI0008");
				logRecord.setLoggerName(logger.getName());
				logRecord.setParameters(new String[]{getPortletWindowName()});
				logRecord.setThrown(ce);
				logger.log(logRecord);
			}
			throw new InvokerException(
					"WindowInvoker.processAction():container exception",
					ce);
		} catch (ContentException cte) {
			if(logger.isLoggable(Level.WARNING)){
				LogRecord logRecord = new LogRecord(Level.WARNING, "PSPL_PCCTXCSPPCI0008");
				logRecord.setLoggerName(logger.getName());
				logRecord.setParameters(new String[]{getPortletWindowName()});
				logRecord.setThrown(cte);
				logger.log(logRecord);
			}
			throw new WindowException(getErrorCode(cte),
					"Content Exception",
					cte);
		}


		//
		// container.executeAction can either return a redirectURL or
		// change in mode,windowstate and new renderparams.
		// Both cases are mutually exclusive.
		//

		returnURL = executeActionResponse.getRedirectURL();


		if (returnURL == null) {
                
			//
			// process mode changes.

			newPortletWindowMode = executeActionResponse.getNewChannelMode();
			if ( newPortletWindowMode != null) {
				validateModeChange(currentPortletWindowMode,
						newPortletWindowMode,
						authless);
				currentPortletWindowMode = newPortletWindowMode;
			}

			//
			// process state changes.
			//

			newWindowState = executeActionResponse.getNewWindowState();
			if ( newWindowState != null) {
				currentWindowState = processWindowStateChange(request,
						newWindowState,
						currentPortletWindowMode,
						authless);
			}
        }
        
        //
        // Now we have new renderParams, new portletWindow mode
        // new window state
        //
        
        if ( returnURL == null) {
            if ( currentPortletWindowMode != null) {
                returnURL = processModeChange(request, currentPortletWindowMode, currentWindowState);
            }

			this.eventUpdatedPortlets = executeActionResponse.getEventUpdatedPortlets();
			this.eventUpdatedPortletsState = executeActionResponse.getEventUpdatedPortletsState();

			setResponseProperties(executeActionResponse);
        }
        
        return returnURL;
    }
    
    
    /**
     * Invokes the serveResource of the portlet.
     *
     * This method sets all the necessary attributes in the ContainerRequest
     * and Container Response and calls the configured container to invoke
     * the serveResource of the portlet.
     *
     *
     * @param request An HttpServletRequest that contains
     * information related to this request for content.
     * @param response An HttpServletResponse that allows the portlet window context
     * to influence the overall response for the page (besides generating the content).

     * @exception InvokerException If there was an error generating the
     * content.
     */
    public void getResources(HttpServletRequest request,
            HttpServletResponse response) throws InvokerException {
        
        try {
            getResourcesInternal(request, response);
        } catch(WindowException we) {
            logger.log(Level.SEVERE, "PSPL_PCCTXCSPPCI0019", we.getMessage());
        }
    }
    
    public void getResourcesInternal(HttpServletRequest request,
            HttpServletResponse response)
            throws InvokerException, WindowException {
        
        boolean authless = getPortletWindowContext().isAuthless(request);
       
        //
        // Abstract method to be implemented by the derived class
        //
        
        EntityID portletEntityId = getEntityID(request);
        
        //
        // Get current portlet mode
        //
        
        ChannelMode currentPortletWindowMode = getCurrentPortletWindowMode(request);
        
        //
        // Get current window state
        
        
        ChannelState currentWindowState = getCurrentWindowState(request);
        
        
        //
        // Get list of allowed window states
        //
        List allowableWindowStates = getAllowableWindowStates(request, currentPortletWindowMode);
        
        //
        // Get list of allowed portletWindow modes
        //
        List allowablePortletWindowModes =
                PortletWindowRules.getAllowablePortletWindowModes(currentPortletWindowMode, authless);
        
        
        String processURL = getActionURL(request, currentPortletWindowMode, currentWindowState);
        
        // Request
        GetResourceRequest getResourceRequest = 
                      getContainer().createGetResourceRequest(request,
                              portletEntityId, 
                              currentWindowState, 
                              currentPortletWindowMode,
                              portletWindowContext, 
                              getPortletWindowURLFactory(processURL, request),
                              getWindowRequestReader());
        populateContainerRequest(
                getResourceRequest,
                request,
                allowableWindowStates,
                allowablePortletWindowModes);
        
        // Response
        GetResourceResponse getResourceResponse = getContainer().createGetResourceResponse(response);
        
        //
        // Call the container interface
        //
        try {
            getContainer().getResources(getResourceRequest, getResourceResponse);
        } catch (ContainerException ce) {
            // If exception set the default title
            setTitle(getDefaultTitle());
            if(logger.isLoggable(Level.WARNING)){
                LogRecord logRecord = new LogRecord(Level.WARNING, "PSPL_PCCTXCSPPCI0019");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[]{getPortletWindowName()});
                logRecord.setThrown(ce);
                logger.log(logRecord);
            }
            throw new InvokerException(
                    "Container exception", ce);
        } catch (ContentException cte) {
            // If exception set the default title
            setTitle(getDefaultTitle());
            if(logger.isLoggable(Level.WARNING)){
                LogRecord logRecord = new LogRecord(Level.WARNING, "PSPL_PCCTXCSPPCI0019");
                logRecord.setLoggerName(logger.getName());
                logRecord.setParameters(new String[]{getPortletWindowName()});
                logRecord.setThrown(cte);
                logger.log(logRecord);
            }
             throw new InvokerException(
                    "Container exception");
        }
		setResponseProperties(getResourceResponse);
        
        InvokerUtil.setResponseProperties(request, response, this.responseProperties);

        try {
            String contentType = getResourceResponse.getContentType();
            if(contentType != null) {
                response.setContentType(contentType);
            }
            StringBuffer buffer = getResourceResponse.getContentAsBuffer();
            byte[] bytes = getResourceResponse.getContentAsBytes();
            if(buffer != null){
                response.getWriter().print(buffer);              
                
            } else if(bytes != null && bytes.length > 0) {        
                response.getOutputStream().write(bytes);    
                
            } else {    
                response.getWriter().print("");       
            }
            
            response.flushBuffer();
            InvokerUtil.clearResponseProperties(this.responseProperties);
        } catch(IOException e){
            throw new InvokerException("Exception in Writing Response",e);
        }
        
    }
    
    /**
     * Populates the ContainerRequest object
     */
    protected void populateContainerRequest(
            ContainerRequest containerRequest,
            HttpServletRequest request,
            List allowableWindowStates,
            List allowablePortletWindowModes) throws InvokerException {

        //
        // allowable window state and mode determines the set of window state
        // and portlet mode the portlet can switch to programmatically
        //

        containerRequest.setAllowableWindowStates(allowableWindowStates);
        containerRequest.setAllowableChannelModes(allowablePortletWindowModes);
        
        //
        // set allowable states, modes and contentTypes.
        //

        String contentType = getPortletWindowContext().getContentType();
        List allowableContentTypes = new ArrayList();
        allowableContentTypes.add(contentType);
        containerRequest.setAllowableContentTypes(allowableContentTypes);

        containerRequest.setRoles(getRoleList(request));

        containerRequest.setUserInfo(getUserInfoMap(request));
        
        containerRequest.setUserID(getUserID(request));
        
        containerRequest.setUserPrincipal(request.getUserPrincipal());
        
        containerRequest.setLocale(request.getLocale());
        
        containerRequest.setPortalInfo(getPortalInfo(request.getLocale()));
		
        containerRequest.setAttribute(PortletRequest.CCPP_PROFILE, getCCPPProfile(request));
    }

	private Map<PortletID, List<String>> getPortletNamespaces(ContainerRequest containerRequest) {

		Map<PortletID, List<String>> portletNamespaces = new HashMap<PortletID, List<String>>();
		try {
			List<EntityID> portletEntityIDs = this.portletWindowContext.getPortletWindows(
				PortletType.LOCAL, ContainerUtil.getEventDistributionType(containerRequest));
			if (portletEntityIDs != null) {
				List<String> namespaces = null;

				for(EntityID portletEntityID : portletEntityIDs) {
					namespaces = portletNamespaces.get(portletEntityID.getPortletID());
					if(namespaces == null) {
						namespaces = new ArrayList<String>();
						portletNamespaces.put(portletEntityID.getPortletID(), namespaces);
					}
					namespaces.add(ContainerUtil.getJavascriptSafeName(portletEntityID.toString()));
				}
			}
		} catch (PortletWindowContextException ex) {
			logger.log(Level.WARNING, "PSPL_PCCTXCSPPCI0021", ex);
		}
		return portletNamespaces;
	}
	
	private Map<PortletID, List<String>> getPortletWindowIDs(ContainerRequest containerRequest) {

		Map<PortletID, List<String>> portletWindowIDs = new HashMap<PortletID, List<String>>();
		try {
			List<EntityID> portletEntityIDs = this.portletWindowContext.getPortletWindows(
				PortletType.LOCAL, ContainerUtil.getEventDistributionType(containerRequest));
			if (portletEntityIDs != null) {
				List<String> windowIDs = null;
				for(EntityID portletEntityID : portletEntityIDs) {
					windowIDs = portletWindowIDs.get(portletEntityID.getPortletID());
					if(windowIDs == null) {
						windowIDs = new ArrayList<String>();
						portletWindowIDs.put(portletEntityID.getPortletID(), windowIDs);
					}
					windowIDs.add(portletEntityID.toString());
				}
			}
		} catch (PortletWindowContextException ex) {
			logger.log(Level.WARNING, "PSPL_PCCTXCSPPCI0021", ex);
		}
		return portletWindowIDs;
	}

    /**
     * Gets the title  for the portletWindow.
     * This method returns the title from the portlet.
     * Portlet uses javax.portlet.title namespace for its title.
     *
     * @return A string title.
     * @exception InvokerException if error occurs when getting the title for
     * the portletWindow.
     */
    public String getTitle() throws InvokerException {
        if (title != null && title.length() != 0) {
            return title;
        } else {
            return getDefaultTitle();
        }
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the response properties  for the portletWindow.
     * This method returns the cookies, headers that were
     * set by the portlet.
     *
     * @return ResponseProperties
     */
    public ResponseProperties getResponseProperties() {
        return this.responseProperties;
    }
    
    /**
     * Gets the portlets that have been updated during eventing.
     *
     * @return portlets that have been updated during eventing.
     */
    public List<EntityID> getEventUpdatedPortlets() {
        return this.eventUpdatedPortlets;
    }

    /**
     * Returns list of portlets along with ChannelState that have been updated
	 * for the portlets during eventing.
	 *
     * @return list of portlets along with channel state that have been updated
	 * during eventing.
     */
	public Map<EntityID,ChannelState> getEventUpdatedPortletsState() {
		return this.eventUpdatedPortletsState;
	}

    public boolean isEditable() throws InvokerException {
        
        //
        // Get handle to portlet description
        //
        //
        if ( getPortletWindowContext().isAuthless(origRequest)) {
            return false;
        }
        
        return isMarkupSupported(getPortletWindowContext().getContentType(),
                getPortletWindowContext().getLocaleString(),
                ChannelMode.EDIT,
                ChannelState.MAXIMIZED);
    }
    
    /**
     * Process window state changes.
     */
    protected ChannelState processWindowStateChange(HttpServletRequest request,
            ChannelState newWindowState,
            ChannelMode portletWindowMode,
            boolean authless) throws InvokerException, WindowException {
        
        
        ChannelState windowState = newWindowState;
        boolean validState = PortletWindowRules.validateWindowStateChange(
                portletWindowMode, newWindowState);
        
        if ( !validState || newWindowState == null) {
            
            windowState = PortletWindowRules.getDefaultWindowState(portletWindowMode);
			if(logger.isLoggable(Level.FINER)) {
				logger.log(Level.FINER, "PSPL_PCCTXCSPPCI0002",
					new Object[]{windowState, portletWindowMode});
			}
        }
        
        return windowState;
    }
    
    /**
     * Assemble the URL to cause the desktop to be rendered with
     * the new mode and window state
     */
    protected URL processModeChange(HttpServletRequest request, ChannelMode portletWindowMode,
            ChannelState portletWindowState) throws InvokerException {
        URL redirectURL = null;
        try {
            redirectURL = new URL(getRenderURL(request, portletWindowMode, portletWindowState));
        } catch (MalformedURLException mue) {
            throw new InvokerException(
                    "WindowInvoker.processModeChange():"
                    + " couldn't generate redirect URL to page for mode "
                    + portletWindowMode.toString(),
                    mue);
        }
        return redirectURL;
    }
    
    private static List initParamKeyList() {
        
        ArrayList localParamKeyList = new ArrayList();
        localParamKeyList.add(WindowInvokerConstants.PORTLET_WINDOW_KEY);
        localParamKeyList.add(WindowInvokerConstants.PORTLET_WINDOW_MODE_KEY);
        return localParamKeyList;
        
    }
    
    /**
     * Used by subclasses to find out if key in the request
     * is reserved by the window invoker
     */
    
    public static boolean isWindowInvokerKey(String key) {
        if (key != null) {
            if ( localParamKeyList.contains(key)) {
                return true;
            }
        }
        return false;
    }
    
    
    //-----------------------------------------------------------------
    // Error Handling Methods
    //-----------------------------------------------------------------
    
    /**
     * Derived implementations can use this method to
     * generate a error url if needed
     * We return the URL based on the current mode
     */
    
    public URL getErrorCodeURL(ErrorCode errorCode, HttpServletRequest request)
    throws InvokerException {
        
        try {
            //
            // Get the URL for the existing mode
            //
            String startURL = getPortletWindowContext().getDesktopURL(request) + "?";
            
            //
            // Append the error code to it
            //
            return new URL(startURL + getErrorCodeParameter() + "=" + errorCode.toString());
            
        } catch (MalformedURLException mue) {
            throw new InvokerException(
                    "WindowInvoker.getErrorCodeURL():couldn't build errorURL",
                    mue);
        }
    }
    

    private String getErrorCodeParameter() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(WindowInvokerConstants.KEYWORD_PREFIX);
        buffer.append(getPortletWindowName());
        buffer.append(ERROR_CODE);
        return buffer.toString();
    }
    //
    // Content rendered for error
    //
    // Content rendered for error
    protected StringBuffer getErrorMessageContent(ErrorCode errorCode) {
        Locale locale = new Locale(getPortletWindowContext().getLocaleString());
        return getErrorMessageContent(errorCode, locale);
    }

    //
    // This method can be overwritten by the
    // derived classes based on different container implementation
    // to spit out more fine grained error codes.
    // Overwriting this method IMPLIES overwriting getErrorMessageContent
    // too. In that case, derived classes should create resource bundle
    // with messages for the each new error code they might return from
    // getErrorCode method, and use the super class method for the rest.
    //
    protected ErrorCode getErrorCode(ContentException ex) {
        ErrorCode code = ex.getErrorCode();
        if( code == null ) {
            return WindowErrorCode.CONTENT_EXCEPTION;
        } else {
            return code;
        }
    }
    
    //
    // Read error code from the request params
    //
    
    protected ErrorCode readErrorCode(HttpServletRequest request) {
        String errorCodeStr = (String)request.getParameter(getErrorCodeParameter());
        
        if ( errorCodeStr != null && errorCodeStr.length() > 0) {
            return new ErrorCode(errorCodeStr);
        } else {
            return null;
        }
    }
    
    
    protected ChannelMode getCurrentPortletWindowMode(HttpServletRequest request) {
        
        ChannelMode currentPortletWindowMode = getPortletWindowMode();
        
        if ( currentPortletWindowMode != null) {
            return currentPortletWindowMode;
        }
        return ChannelMode.VIEW;
    }
    
    
    
    protected ChannelState getCurrentWindowState(HttpServletRequest request)
    throws InvokerException{
        
        ChannelState currentWindowState = getPortletWindowState();
        if ( currentWindowState == null) {
            return PortletWindowRules.getDefaultWindowState(
                    getCurrentPortletWindowMode(request));
        }
        
        return currentWindowState;
    }
    
    protected List getAllowableWindowStates(HttpServletRequest request,
            ChannelMode mode ){
        List allowableWindowStates = null;
        
        allowableWindowStates =
                PortletWindowRules.getDefaultAllowableWindowStates(mode);
        return allowableWindowStates;
    }
    
    public String getActionURL(HttpServletRequest request, ChannelMode portletWindowMode,
            ChannelState portletWindowState) {
        StringBuffer processURL =
                new StringBuffer(getPortletWindowContext().getDesktopURL(request));
        
        processURL.append("?").append(WindowInvokerConstants.DRIVER_ACTION).append("=").append(WindowInvokerConstants.ACTION)
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_MODE_KEY).append("=").append(portletWindowMode.toString())
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_STATE_KEY).append("=").append(portletWindowState.toString())
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_KEY).append("=").append(getPortletWindowName());
        return processURL.toString();
    }
    
    public String getRenderURL(HttpServletRequest request, ChannelMode portletWindowMode,
            ChannelState portletWindowState) {
        StringBuffer processURL =
                new StringBuffer(getPortletWindowContext().getDesktopURL(request));
        
        processURL.append("?").append(WindowInvokerConstants.DRIVER_ACTION).append("=").append(WindowInvokerConstants.RENDER)
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_MODE_KEY).append("=").append(portletWindowMode.toString())
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_STATE_KEY).append("=").append(portletWindowState.toString())
        .append("&").append(WindowInvokerConstants.PORTLET_WINDOW_KEY).append("=").append(getPortletWindowName());
        return processURL.toString();
    }

    private String getUserID(HttpServletRequest request) {
        String userID = null;
        Principal principal = request.getUserPrincipal();
        if(principal != null) {
            userID = principal.getName();
        }
        return userID;
    }

	private void setResponseProperties(ContainerResponse containerResponse) {
        // If headers, cookie have been set by the Portlet add it to the
        // ResponseProperties
        if(containerResponse.getCookieProperties() != null
                || containerResponse.getStringProperties() != null
                || containerResponse.getElementProperties() != null) {
            this.responseProperties.setCookies(containerResponse.getCookieProperties());
            this.responseProperties.setResponseHeaders(containerResponse.getStringProperties());
            this.responseProperties.setMarkupHeaders(containerResponse.getElementProperties());
        }

	}

    /**
     * Throws a WindowException exception if attempt is made to
     * change to a new mode that is not allowed by the portal
     */
    private void validateModeChange(ChannelMode currentMode,
            ChannelMode newMode,
            boolean authless)
            throws WindowException {
        
        
        List allowedList = PortletWindowRules.getAllowablePortletWindowModes(currentMode,
                authless);
        if ( !allowedList.contains(newMode)) {
            throw new WindowException(
                    WindowErrorCode.INVALID_MODE_CHANGE_REQUEST,
                    "Portal doesn't allow changing mode "
                    + " from "
                    + currentMode
                    + " to "
                    + newMode);
        }
        return;
        
    }
    
    /**
     * Gets the <code>PortletWindowContext</code> for the PortletiIndow.
     *
     * @return <code>PortletWindowContext</code>.
     */
    public PortletWindowContext getPortletWindowContext(){
        return this.portletWindowContext;
    }
    
    /**
     * Gets a specified ResourceBundle file for the provider based on User's
     * locale.
     * <p>
     * A provider can specify on-screen strings to be localized in a resource
     * bundle file, as described in the Java <code>ResourceBundle</code> class.
     *
     * @param base a specified <code>ResourceBundle</code> name.
     *
     * @see java.util.ResourceBundle.
     *
     * @return <code>ResourceBundle</code>.
     */
    public ResourceBundle getResourceBundle(String base) {
        Locale locale = new Locale(getPortletWindowContext().getLocaleString());
        return getResourceBundle(base,locale);
    }

    /**
     * Gets a WindowInvoker ResourceBundle file based on locale.
     *
     * @param base a specified <code>ResourceBundle</code> name.
     *
     * @see java.util.ResourceBundle.
     *
     * @return <code>ResourceBundle</code>.
     */
    private ResourceBundle getWindowInvokerBundle(Locale locale) {
        if(windowInvokerRB == null) {
            windowInvokerRB = getResourceBundle(WINDOW_INVOKER_RB_NAME, locale);
        }
        return windowInvokerRB;
    }

    private ResourceBundle getResourceBundle(String base, Locale locale) {
        return ResourceBundle.getBundle(base, locale, getClass().getClassLoader());
    }

    private StringBuffer getErrorMessageContent(ErrorCode errorCode, Locale locale) {
        ResourceBundle bundle = null;
        StringBuffer buffer = new StringBuffer();
        try {
            bundle = getWindowInvokerBundle(locale);
            buffer.append(bundle.getString(errorCode.toString()));
        } catch(MissingResourceException ex) {
            logger.log(Level.FINE,"PSPL_PCCTXCSPPCI0003", ex);
            if(bundle != null) {
                buffer.append(bundle.getString(WindowErrorCode.GENERIC_ERROR.toString()));
                buffer.append(" ");
                buffer.append(errorCode);
            } else {
                buffer.append(WindowErrorCode.GENERIC_ERROR.toString());
                buffer.append(" ");
                buffer.append(errorCode);
            }
        }
        return buffer;
    }

}

