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


package com.sun.portal.portletcontainer.appengine;

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.container.PortletEvent;
import com.sun.portal.container.PortletID;
import com.sun.portal.container.service.PortletDescriptorHolder;
import com.sun.portal.container.service.PortletDescriptorHolderFactory;
import com.sun.portal.portletcontainer.appengine.impl.LifecycleManagerImpl;
import com.sun.portal.portletcontainer.appengine.impl.RequestResponseFactoryImpl;
import com.sun.portal.portletcontainer.common.PortletActions;
import com.sun.portal.portletcontainer.common.PortletContainerActionRequest;
import com.sun.portal.portletcontainer.common.PortletContainerActionResponse;
import com.sun.portal.portletcontainer.common.PortletContainerCacheControl;
import com.sun.portal.portletcontainer.common.PortletContainerConstants;
import com.sun.portal.portletcontainer.common.PortletContainerErrorCode;
import com.sun.portal.portletcontainer.common.PortletContainerEventRequest;
import com.sun.portal.portletcontainer.common.PortletContainerEventResponse;
import com.sun.portal.portletcontainer.common.PortletContainerRenderRequest;
import com.sun.portal.portletcontainer.common.PortletContainerRenderResponse;
import com.sun.portal.portletcontainer.common.PortletContainerRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResourceRequest;
import com.sun.portal.portletcontainer.common.PortletContainerResourceResponse;
import com.sun.portal.portletcontainer.common.PortletContainerResponse;
import com.sun.portal.portletcontainer.common.PortletContainerUtil;
import com.sun.portal.portletcontainer.common.PreferencesValidatorSetter;
import com.sun.portal.portletcontainer.common.descriptor.DeploymentExtensionDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.EventDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletDescriptorConstants;
import com.sun.portal.portletcontainer.common.descriptor.SecurityConstraintDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.SharedSessionAttributeDescriptor;
import com.sun.portal.portletcontainer.portlet.impl.PortletRequestConstants;
import com.sun.portal.portletcontainer.portlet.impl.PortletResourceBundle;
import com.sun.portal.portletcontainer.portlet.impl.PortletsResources;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.portlet.PreferencesValidator;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;
import javax.portlet.WindowStateException;
import javax.portlet.filter.FilterChain;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.EventPortlet;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.xml.namespace.QName;

/**
 * The portlet app engine servlet is the entry point of the portlet
 * application engine.
 * <p/>
 * The portlet app engine receives requests from
 * the portlet container through the Request Dispatcher, and then
 * deligates the work to the following components:
 * <UL>
 * <LI> Lifecycle manager: which allow create and retrieve
 * portlet instances
 * <LI> Request/response factory: generates portlet
 * request/response objects
 * </UL>
 * Once retrieves the target portlet, the portlet app engine servlet will
 * executes the actions and sets the result in the response and return
 * back to portlet container.
 */
public class PortletAppEngineServlet extends HttpServlet {
    
    public static final String CONFIDENTIAL = "CONFIDENTIAL";
    public static final String INTEGRAL = "INTEGRAL";
    
    // Global variables
    private LifecycleManager lifecycleManager;
    private RequestResponseFactory requestResponseFactory;
    private PortletAppDescriptor portletAppDescriptor;
    private ServletContext context;
    private PortletsResources portletsResources;
    
    private static Logger logger = ContainerLogger.getLogger(PortletAppEngineServlet.class, "PAELogMessages");
	private static final String CLIENT_EVENT_OBJECT_VALUE_TYPE = "com.sun.portlet.ClientEvent";
    /*
     * Initializes global variables.
     * <P>
     * @param config The <code>ServletConfig</code> object
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        context = config.getServletContext();
        lifecycleManager = new LifecycleManagerImpl(config);
        context.setAttribute(LifecycleManager.LIFECYCLE_MANAGER, lifecycleManager);
        
        requestResponseFactory = new RequestResponseFactoryImpl(context);
        context.setAttribute(RequestResponseFactory.REQUEST_RESPONSE_FACTORY, requestResponseFactory);
        
        portletAppDescriptor = lifecycleManager.getPortletAppDescriptor();
        try {
            PortletDescriptorHolderFactory.getPortletDescriptorHolder().load(portletAppDescriptor);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
        portletsResources = new PortletsResources(portletAppDescriptor);
        context.setAttribute(PortletContainerConstants.PORTLET_RESOURCES, portletsResources.getPortletResourceMap());
        context.setAttribute(PortletContainerConstants.PORTLET_METADATA_RESOURCES, portletsResources.getPortletMetadataResourceMap());
    }

    /**
     * This method is the meat of the servlet, which basically handles
     * the request for executing a portlet.
     * <p/>
     * This method will do the following tasks:
     * <UL>
     * <LI>Obtains portlet request/response objects from
     * request/response factory
     * <LI>Checks for security violation, if any
     * <LI>Obtains the target portlet object from the lifecycle
     * manager
     * <LI>Obtains the action list that is passed from the request
     * <LI>Calls corresponding portlet methods
     * <LI>Sets corresponding results in the response
     * <LI>Handles exceptions
     * <p/>
     *
     * @param request The <code>HttpServletRequest</code>
     * @param response The <code>HttpServletResponse</code>
     * @throws IOException
     * @throws ServletException 
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        StringWriter stringWriter = new StringWriter(PortletAppEngineConstants.INITIAL_BUFFER_SIZE);
        StringServletOutputStream outputStream = new StringServletOutputStream(
                new ByteArrayOutputStream(PortletAppEngineConstants.INITIAL_BUFFER_SIZE));
        ActionRequest actionRequest = null;
        RenderRequest renderRequest = null;
        ActionResponse actionResponse = null;
        RenderResponse renderResponse = null;
        EventRequest eventRequest = null;
        EventResponse eventResponse = null;
        ResourceRequest resourceRequest = null;
        ResourceResponse resourceResponse = null;
        /*************************************************
         * Setup local variables.
         *************************************************/
        PortletContainerRequest pcRequest = 
			(PortletContainerRequest) request.getAttribute(PortletContainerRequest.PORTLET_CONTAINER_REQUEST);
        PortletContainerResponse pcResponse = 
			(PortletContainerResponse) request.getAttribute(PortletContainerResponse.PORTLET_CONTAINER_RESPONSE);
        String portletName = pcRequest.getPortletName();
        PortletMode portletMode = PortletAppEngineUtils.getPortletMode(pcRequest.getPortletWindowMode());
        WindowState windowState = PortletAppEngineUtils.getWindowState(pcRequest.getWindowState());
        List<String> actionList = pcRequest.getActions();
        String entityID = pcRequest.getEntityID().toString();
        if (logger.isLoggable(Level.FINEST)) {
            StringBuffer actionListBuffer = new StringBuffer();
            for (int j = 0; j < actionList.size(); j++) {
                actionListBuffer.append(actionList.get(j));
                actionListBuffer.append(" | ");
            }
            logger.log(Level.FINEST, "PSPL_PAECSPPA0001", 
                    new Object[]{request.getContextPath(), entityID, portletName, portletMode, 
                    windowState, actionListBuffer});
        }
        /*************************************************
         * Check security constraint
         *************************************************/
        if (checkSecurityConstraint(request, portletName)) {
            /*************************************************
             * Start processing actions.
             *************************************************/
            PortletDescriptor portletDescriptor = getPortletDescriptor(portletName);
            PreferencesValidator validator = lifecycleManager.getPreferencesValidator(portletName);
            Portlet p = null;
            try {
                // gets the portlet
                p = lifecycleManager.getPortlet(portletName);
                // sets portlet config in request attribute
                PortletConfig portletConfig = lifecycleManager.getPortletConfig(portletName);
                request.setAttribute(PortletRequestConstants.PORTLET_CONFIG_ATTRIBUTE, portletConfig);
                logger.log(Level.FINEST, "PSPL_PAECSPPA0002", portletName);
                boolean stop = false;
                // calls portlet actions
                for (int i = 0; i < actionList.size() && !stop; i++) {
                    String action = (String) actionList.get(i);
					setSharedSessionAttributeDescriptors(pcRequest);
                    if (action.equals(PortletActions.ACTION)) {
                        PortletContainerActionRequest pcActionRequest = (PortletContainerActionRequest) pcRequest;
                        PortletContainerActionResponse pcActionResponse = (PortletContainerActionResponse) pcResponse;
                        actionRequest = requestResponseFactory.getActionRequest(request, response, pcActionRequest, pcActionResponse, 
                                lifecycleManager.getPortletContext(), lifecycleManager.getPortalContext(), portletDescriptor);
                        actionResponse = requestResponseFactory.getActionResponse(request, response, pcActionRequest, pcActionResponse, 
                                actionRequest, portletAppDescriptor, portletDescriptor);
                        // Set the lifecycle phase attribute. It allows a portlet to determine the current lifecycle of the request
						setLifecycle(actionRequest, PortletRequest.ACTION_PHASE);
                        setValidator(actionRequest, validator, false);
						setRequestResponseAttributes(request, actionRequest, actionResponse);
                        //////////////////////////////////////////
                        //      Filter entry point              //
                        //////////////////////////////////////////
                        FilterChain filterChain = lifecycleManager.getFilterChain(portletName, p, PortletActions.ACTION_PHASE);
                        if (filterChain != null) {
                            filterChain.doFilter(actionRequest, actionResponse);
                        } else {
                            p.processAction(actionRequest, actionResponse);
                        }
                        //////////////////////////////////////////
                        //      Filter exit point              //
                        //////////////////////////////////////////
                        // Store scoped attributes
                        if(isActionScopedRequestAttributeEnabled(portletDescriptor.getPortletID())) {
                            // Start a new action scope
                            String actionScopeIDValue = generateActionScopeID(portletDescriptor);
                            Map<String, String[]> map = 
                                    new HashMap<String,String[]>(actionResponse.getRenderParameterMap());
                            map.put(PortletRequest.ACTION_SCOPE_ID, new String[] {actionScopeIDValue} );
                            actionResponse.setRenderParameters(map);
                            setScopedAttributes(actionScopeIDValue, portletDescriptor.getPortletID(), pcActionRequest, actionRequest);
                        }
                        if (pcActionResponse.getRedirectURL() != null) {
                            stop = true;
                        }
                        Map<String, String[]> publicRenderParams = 
                                getUpdatedPublicRenderParameters(portletConfig, actionResponse);
                        if(publicRenderParams.size()>0){
                            pcActionResponse.setPublicRenderParameters(publicRenderParams);
                        }
                        
                        // unset validator
                        setValidator(actionRequest, validator, true);
						
						//Set new portlet mode and window state
						pcActionResponse.setNewChannelMode(pcActionResponse.getChannelMode());
 						pcActionResponse.setNewChannelState(pcActionResponse.getChannelState());
                        
                        // 286 Eventing - If the processAction is succesful then only set the event queue
                        Queue<PortletEvent> eventQueue = (Queue) actionRequest.getAttribute(PortletContainerConstants.EVENTS);
                        if (eventQueue != null && eventQueue.size() > 0) {
                            pcActionResponse.setEventQueue(eventQueue);
                        }
						actionRequest.removeAttribute(PortletContainerConstants.EVENTS);
                    } else if (action.equals(PortletActions.EVENT)) {
                        if (p instanceof EventPortlet) {
                            EventPortlet eventPortlet = (EventPortlet) p;
                            PortletContainerEventRequest pcEventRequest = (PortletContainerEventRequest) pcRequest;
                            PortletContainerEventResponse pcEventResponse = (PortletContainerEventResponse) pcResponse;
                            eventRequest = requestResponseFactory.getEventRequest(request, response, pcEventRequest, pcEventResponse, 
                                    lifecycleManager.getPortletContext(), lifecycleManager.getPortalContext(), portletDescriptor);
                            eventResponse = requestResponseFactory.getEventResponse(request, response, pcEventRequest, pcEventResponse,
                                    eventRequest, portletAppDescriptor, portletDescriptor);
                            // Set the lifecycle phase attribute. It allows a portlet to determine the current lifecycle of the request
							setLifecycle(eventRequest, PortletRequest.EVENT_PHASE);
							setValidator(eventRequest, validator, false);
							setRequestResponseAttributes(request, eventRequest, eventResponse);
                            //////////////////////////////////////////
                            //      Filter entry point              //
                            //////////////////////////////////////////
                            FilterChain filterChain = lifecycleManager.getFilterChain(portletName, p, PortletActions.EVENT_PHASE);
                            if (filterChain != null) {
                                filterChain.doFilter(eventRequest, eventResponse);
                            } else {
                                eventPortlet.processEvent(eventRequest, eventResponse);
                            }
                            //////////////////////////////////////////
                            //      Filter exit point              //
                            //////////////////////////////////////////
                           
                            // If the processEvent is successful then only set the event queue
                            Queue<PortletEvent> eventQueue = (Queue) eventRequest.getAttribute(PortletContainerConstants.EVENTS);
                            if (eventQueue != null && eventQueue.size() > 0) {
                                pcEventResponse.setEventQueue(eventQueue);
                            }
							eventRequest.removeAttribute(PortletContainerConstants.EVENTS);
                            // Store scoped attributes
                            if(isActionScopedRequestAttributeEnabled(portletDescriptor.getPortletID())) {
                                String actionScopeIDValue = eventRequest.getParameter(PortletRequest.ACTION_SCOPE_ID);
                                if(actionScopeIDValue == null) {
                                    // Start a new action scope
                                    actionScopeIDValue = generateActionScopeID(portletDescriptor);
                                    Map<String, String[]> map = 
                                            new HashMap<String,String[]>(eventResponse.getRenderParameterMap());
                                    map.put(PortletRequest.ACTION_SCOPE_ID, new String[] {actionScopeIDValue} );
                                    eventResponse.setRenderParameters(map);
                                }
                                setScopedAttributes(actionScopeIDValue, portletDescriptor.getPortletID(), pcEventRequest, eventRequest);
                            }
                            
                            Map<String, String[]> publicRenderParams = 
                                    getUpdatedPublicRenderParameters(portletConfig, eventResponse);
                            if(publicRenderParams.size()>0){
                                pcEventResponse.setPublicRenderParameters(publicRenderParams);
                            }                            
                            
							//Set new portlet mode and window state
							pcEventResponse.setNewChannelMode(pcEventResponse.getChannelMode());
							pcEventResponse.setNewChannelState(pcEventResponse.getChannelState());
                        
                        } else {
                            logger.log(Level.WARNING,"PSPL_PAECSPPA0031",portletName);
                        }
                    } else if (action.equals(PortletActions.RENDER)) {
                        PortletContainerRenderRequest pcRenderRequest = (PortletContainerRenderRequest) pcRequest;
                        PortletContainerRenderResponse pcRenderResponse = (PortletContainerRenderResponse) pcResponse;
                        setCacheParams(pcRenderResponse.getCacheControl(), 
                                portletDescriptor, pcRenderRequest.getETag());
                        renderRequest = requestResponseFactory.getRenderRequest(request, response, pcRenderRequest, pcRenderResponse, 
                                lifecycleManager.getPortletContext(), lifecycleManager.getPortalContext(), portletDescriptor);
                        renderResponse = requestResponseFactory.getRenderResponse(request, response, pcRenderRequest, pcRenderResponse, 
                                renderRequest, portletAppDescriptor, portletDescriptor, stringWriter, outputStream);
                        // Set the lifecycle phase attribute. It allows a portlet to determine the current lifecycle of the request
						setLifecycle(renderRequest, PortletRequest.RENDER_PHASE);
                        setValidator(renderRequest, validator, false);
						setRequestResponseAttributes(request, renderRequest, renderResponse);

                        String title = getPortletTitle(request, pcRenderRequest, entityID, portletName); // use resource bundle
                        // Set the title on PortletRenderRequest
                        renderRequest.setAttribute(PortletContainerConstants.PORTLET_TITLE, title);
                        // Get the title returned from getTitle method of GenericPortlet. This is done by using the
                        // same implementation used in the getTitle method of GenericPortlet. Set this as an attribute
                        // to be used in RenderResponse.setTitle
                        String genericPortletTitle = getGenericPortletTitle(portletConfig, renderRequest);
                        if(genericPortletTitle != null){
                            renderRequest.setAttribute(PortletContainerConstants.TITLE_FROM_GENERIC_PORTLET, genericPortletTitle);
                        }

						handlePortalQueryParameters(portletName, pcRenderRequest);

                        //////////////////////////////////////////
                        //      Filter entry point              //
                        //////////////////////////////////////////
                        FilterChain filterChain = lifecycleManager.getFilterChain(portletName, p, PortletActions.RENDER_PHASE);
                        if (filterChain != null) {
                            filterChain.doFilter(renderRequest, renderResponse);
                        } else {
                            p.render(renderRequest, renderResponse);
                        }
                        //////////////////////////////////////////
                        //      Filter exit point              //
                        //////////////////////////////////////////
						
						// Check for the presence of Client Events
						StringBuffer clientEventDataBuffer = getClientEventData(portletDescriptor, pcRenderRequest);
						// sets values in portlet container response
                        // the output is either in the writer or in the
                        // output stream
                        StringBuffer writerOuput = stringWriter.getBuffer();
                        String streamOutput = outputStream.toString(response.getCharacterEncoding());
						StringBuffer markup;
                        if (writerOuput.length() > 0) {
                            markup = writerOuput;
                        } else if (streamOutput.length() > 0) {
                            markup = new StringBuffer(streamOutput);
                        } else {
                            markup = new StringBuffer("");
                        }
						if(clientEventDataBuffer.length() != 0) {
							markup.insert(0, clientEventDataBuffer);
						} 
						pcRenderResponse.setMarkup(markup);
                        setValidator(renderRequest, validator, true);
                    } else if (action.equals(PortletActions.RESOURCE)) {
                        if (p instanceof ResourceServingPortlet) {
                            //handle resource action
                            ResourceServingPortlet resourceServingPortlet = (ResourceServingPortlet) p;
                            PortletContainerResourceRequest pcResourceRequest = (PortletContainerResourceRequest) pcRequest;
                            PortletContainerResourceResponse pcResourceResponse = (PortletContainerResourceResponse) pcResponse;
                            //Set caching parameters for this request
                            setCacheParams(pcResourceResponse.getCacheControl(),
                                    portletDescriptor, pcResourceRequest.getETag());

                            // Instead of using input HttpServletResponse, use the response set on the 
                            // PCResourceResponse. This is because the input HttpServletResponse is
                            // of type PAEResponseWrapper and cannot be be used for the serverResource feature 
                            // because of some restrictions in RD.include(see ServletSpec)
                            // Hence the original HttpServletResponse set on PCResourceResponse is used.
                            resourceRequest = requestResponseFactory.getResourceRequest(request, 
                                    pcResourceResponse.getHttpServletResponse(), pcResourceRequest, pcResourceResponse, 
                                    lifecycleManager.getPortletContext(), lifecycleManager.getPortalContext(), portletDescriptor);
                            
                            resourceResponse = requestResponseFactory.getResourceResponse(pcResourceRequest.getHttpServletRequest(), 
                                    pcResourceResponse.getHttpServletResponse(), pcResourceRequest, pcResourceResponse, 
                                    resourceRequest, portletAppDescriptor, portletDescriptor, stringWriter, outputStream);
                            // Set the lifecycle phase attribute. It allows a portlet to determine the current lifecycle of the request
							setLifecycle(resourceRequest, PortletRequest.RESOURCE_PHASE);
							setValidator(resourceRequest, validator, false);
							setRequestResponseAttributes(request, resourceRequest, resourceResponse);

                            //////////////////////////////////////////
                            //      Filter entry point              //
                            //////////////////////////////////////////
                            FilterChain filterChain = lifecycleManager.getFilterChain(portletName, p, PortletActions.RESOURCE_PHASE);
                            if (filterChain != null) {
                                filterChain.doFilter(resourceRequest, resourceResponse);
                            } else {
                                resourceServingPortlet.serveResource(resourceRequest, resourceResponse);
                            }
                            //////////////////////////////////////////
                            //      Filter exit point              //
                            //////////////////////////////////////////

                            // sets values in portlet container response
                            // the output is either in the writer or in the
                            // output stream
                            StringBuffer buff = stringWriter.getBuffer();
                            byte[] bytes = outputStream.toByteArray();
                            
                            if (buff.length() > 0) {
                                pcResourceResponse.setContentAsBuffer(buff);
                            } else if (bytes != null && bytes.length > 0) {
                                pcResourceResponse.setContentAsBytes(bytes);
                            } else {
                                pcResourceResponse.setContentAsBuffer(new StringBuffer(""));
                            }
                        } else {
                            logger.log(Level.WARNING,"PSPL_PAECSPPA0030",portletName);
                        }
                    }
                }
            } catch (UnavailableException ue) {
                if (p != null) {
                    p.destroy();
                    lifecycleManager.removePortlet(portletName);
                    lifecycleManager.removePortletConfig(portletName);
                }
                pcResponse.setErrorCode(PortletContainerErrorCode.PORTLET_UNAVAILABLE);
                pcResponse.setException(ue);
                logger.log(Level.WARNING, "PSPL_PAECSPPA0004", ue);
            } catch (PortletSecurityException pse) {
                pcResponse.setErrorCode(PortletContainerErrorCode.SECURITY_VIOLATION);
                pcResponse.setException(pse);
                logger.log(Level.WARNING, "PSPL_PAECSPPA0009", pse);
            } catch (PortletException pe) {
                Throwable cause = pe.getCause();
                if (pe instanceof PortletModeException || cause instanceof PortletModeException) {
                    pcResponse.setErrorCode(PortletContainerErrorCode.UNSUPPORTED_MODE);
                    pcResponse.setException(pe);
                    logger.log(Level.WARNING, "PSPL_PAECSPPA0010", pe);
                } else if (pe instanceof ReadOnlyException || cause instanceof ReadOnlyException) {
                    pcResponse.setErrorCode(PortletContainerErrorCode.READONLY_ERROR);
                    pcResponse.setException(pe);
                    logger.log(Level.WARNING, "PSPL_PAECSPPA0011", pe);
                } else if (pe instanceof WindowStateException || cause instanceof WindowStateException) {
                    pcResponse.setErrorCode(PortletContainerErrorCode.UNSUPPORTED_STATE);
                    pcResponse.setException(pe);
                    logger.log(Level.WARNING, "PSPL_PAECSPPA0012", pe);
                } else if (cause instanceof ServletException) {
                    pcResponse.setErrorCode(PortletContainerErrorCode.MISC_ERROR);
                    pcResponse.setException(pe);
                    logger.log(Level.WARNING, "PSPL_PAECSPPA0036", cause);
                } else {
                    pcResponse.setErrorCode(PortletContainerErrorCode.MISC_ERROR);
                    pcResponse.setException(pe);
                    logger.log(Level.WARNING, "PSPL_PAECSPPA0013", pe);
                }
            } catch (IOException ie) {
                pcResponse.setErrorCode(PortletContainerErrorCode.MISC_ERROR);
                pcResponse.setException(ie);
                logger.log(Level.WARNING, "PSPL_PAECSPPA0014", ie);
            } catch (LifecycleManagerException lme) {
                pcResponse.setErrorCode(PortletContainerErrorCode.PORTLET_UNAVAILABLE);
                pcResponse.setException(lme);
            } catch (RuntimeException re) {
                pcResponse.setErrorCode(PortletContainerErrorCode.MISC_ERROR);
                pcResponse.setException(re);
                logger.log(Level.WARNING, "PSPL_PAECSPPA0015", re);
			}
            /*************************************************
             * Releases objects.
             *************************************************/
            if (actionRequest != null) {
                requestResponseFactory.releaseActionRequest(actionRequest);
            }
            if (renderRequest != null) {
                requestResponseFactory.releaseRenderRequest(renderRequest);
            }
            if (eventRequest != null) {
                requestResponseFactory.releaseEventRequest(eventRequest);
            }
            if (resourceRequest != null) {
                requestResponseFactory.releaseResourceRequest(resourceRequest);
            }
            if (actionResponse != null) {
                requestResponseFactory.releaseActionResponse(actionResponse);
            }
            if (renderResponse != null) {
                requestResponseFactory.releaseRenderResponse(renderResponse);
            }
            if (eventResponse != null) {
                requestResponseFactory.releaseEventResponse(eventResponse);
            }
            if (resourceResponse != null) {
                requestResponseFactory.releaseResourceResponse(resourceResponse);
            }
            // if check security returns false
        } else {
            pcResponse.setErrorCode(PortletContainerErrorCode.SECURITY_VIOLATION);
            logger.log(Level.WARNING, "PSPL_PAECSPPA0005");
        }
    }

	private StringBuffer getClientEventData(PortletDescriptor portletDescriptor, PortletContainerRenderRequest pcRenderRequest) {
		StringBuffer clientEventDataBuffer = new StringBuffer();
		List<QName> supportedPublishingEvents = portletDescriptor.getSupportedPublishingEvents();
		Map<QName,List<String>> publishingEventQnameProcessingEventNamespaces = null;
		for(QName publishingEventQname : supportedPublishingEvents) {
			EventDescriptor eventDescriptor = portletAppDescriptor.getEventDescriptor(publishingEventQname);
			if(eventDescriptor != null && 
                CLIENT_EVENT_OBJECT_VALUE_TYPE.equals(eventDescriptor.getValueType())) {
				if(publishingEventQnameProcessingEventNamespaces == null) {
					publishingEventQnameProcessingEventNamespaces = new HashMap<QName,List<String>>();
				}
				List<PortletID> supportedProcessingPortlets = null;
				try {
					PortletDescriptorHolder portletDescriptorHolder = PortletDescriptorHolderFactory.getPortletDescriptorHolder();
					supportedProcessingPortlets = portletDescriptorHolder.getEventProcessingPortlets(publishingEventQname);
					List<String> processEventNamespaces = new ArrayList<String>();
					for(PortletID supportedProcessingPortlet : supportedProcessingPortlets) {
						processEventNamespaces.add(pcRenderRequest.getNamespace(supportedProcessingPortlet));
					}
					if(!processEventNamespaces.isEmpty()) {
						publishingEventQnameProcessingEventNamespaces.put(publishingEventQname, processEventNamespaces);
					}
				} catch (Exception ex) {
		            logger.log(Level.WARNING, "PSPL_PAECSPPA0007", ex);
				}
			}
		}
		if(publishingEventQnameProcessingEventNamespaces != null) {
			clientEventDataBuffer.append(
				getClientEventDataBuffer(pcRenderRequest.getNamespace(), publishingEventQnameProcessingEventNamespaces));
		}

		return clientEventDataBuffer;
	}
	
	private StringBuffer getClientEventDataBuffer(String publishingEventNamespace,
		Map<QName,List<String>> publishingEventQnameProcessingEventNamespaces) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("<script type=\"text/javascript\">");
		buffer.append(publishingEventNamespace).append("EventQueue = {");
		buffer.append("setEvent : function(qname, values) {");
		buffer.append("var eventQueue = {");
		int i = 0;
		Set<Map.Entry<QName, List<String>>> entries = publishingEventQnameProcessingEventNamespaces.entrySet();
		int entrySize = entries.size();
		for(Map.Entry<QName, List<String>> mapEntry : entries) {
			List<String> processEventNamespaces = new ArrayList<String>();
			for(String processEventNamespace : mapEntry.getValue()) {
				if(processEventNamespace != null) {
					processEventNamespaces.add(processEventNamespace);
				}
			}
			QName qname = mapEntry.getKey();
			int size = processEventNamespaces.size();
			int j = 0;
			buffer.append("\"").append(qname.getNamespaceURI()).append("|").append(qname.getLocalPart()).append("\"");
			buffer.append(":").append("[")	;
			for(String processEventNamespace : processEventNamespaces) {
				buffer.append(processEventNamespace).append("PortletObj");
				j++;
				if(j < size) {
					buffer.append(",");
				}
			}
			buffer.append("]");
			i++;
			if(i < entrySize) {
				buffer.append(",");
			}
		}
		buffer.append("};");

		buffer.append("for(var e in eventQueue){");
		buffer.append("var qnameArray = new Array(); qnameArray = e.split('|');");
		buffer.append("var qnameUri, qnameLocalPart;");
        buffer.append("if(typeof(qname.uri) == 'undefined') { qnameUri = \"\"; }");
        buffer.append("else { qnameUri = qname.uri; }");
        buffer.append("if(typeof(qname.name) == 'undefined') { qnameLocalPart = qname; }");
        buffer.append("else { qnameLocalPart = qname.name; }");
		buffer.append("if(qnameUri == qnameArray[0] && qnameLocalPart == qnameArray[1]){");
		buffer.append("for(i=0; i<eventQueue[e].length; i++){");
		buffer.append("eventQueue[e][i].processEvent(values);");
		buffer.append("}}}}};</script>");
		return buffer;
	}

    /*
     * Returns the <code>PortletDescriptor</code> for given portlet
     * name. Returns null if not defined.
     */
    private PortletDescriptor getPortletDescriptor(String portletName) {
        PortletDescriptor portletDescriptor = null;
        if (portletAppDescriptor != null && portletAppDescriptor.getPortletsDescriptor() != null) {
            portletDescriptor = portletAppDescriptor.getPortletsDescriptor().getPortletDescriptor(portletName);
            if(portletDescriptor == null) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "PSPL_PAECSPPA0003", portletName);
                }
            }
        }
        return portletDescriptor;
    }

	private void setLifecycle(PortletRequest portletRequest, String lifecycle) {
		portletRequest.setAttribute(PortletRequest.LIFECYCLE_PHASE, lifecycle);
	}

	private void setRequestResponseAttributes(HttpServletRequest request, 
		PortletRequest portletRequest, PortletResponse portletResponse) {
		
		request.setAttribute(PortletRequestConstants.PORTLET_REQUEST_ATTRIBUTE,
							portletRequest);
		request.setAttribute(PortletRequestConstants.PORTLET_RESPONSE_ATTRIBUTE,
							portletResponse);
	}

	private void handlePortalQueryParameters(String portletName,
		PortletContainerRenderRequest pcRenderRequest) {
		
		DeploymentExtensionDescriptor sunPortletDescriptor =
			PortletContainerUtil.getSunPortletDescriptor(this.lifecycleManager);

		if(sunPortletDescriptor != null) {
			List<String> portalQueryParameterNames = 
				sunPortletDescriptor.getPortalQueryParameters(portletName);
			if(portalQueryParameterNames != null && !portalQueryParameterNames.isEmpty()) {
				Map<String, String[]> parameterMap = pcRenderRequest.getHttpServletRequest().getParameterMap();
				Map<String, String[]> queryParameterMap = new HashMap<String, String[]>();
				for(String name : portalQueryParameterNames) {
					if(parameterMap.containsKey(name)) {
						queryParameterMap.put(name, parameterMap.get(name));
					}
				}
				pcRenderRequest.setQueryParameters(queryParameterMap);
			}
		}
	}

	private void setSharedSessionAttributeDescriptors(PortletContainerRequest pcRequest) {

		DeploymentExtensionDescriptor sunPortletDescriptor =
			PortletContainerUtil.getSunPortletDescriptor(this.lifecycleManager);

		if(sunPortletDescriptor != null) {
			pcRequest.setSharedSessionAttributeDescriptors(
				sunPortletDescriptor.getSharedSessionAttributeDescriptors());
		}
	}

    /*
     * Sets the preferences validator in the portlet preferences
     */
    private void setValidator(PortletRequest pReq, PreferencesValidator validator, boolean cleanup) {
        if(pReq.getPreferences() instanceof PreferencesValidatorSetter) {
            PreferencesValidatorSetter setter = (PreferencesValidatorSetter) pReq.getPreferences();
            if (setter != null && cleanup) {
                setter.setPreferencesValidator(null);
            }

            if (setter != null && validator != null) {
                setter.setPreferencesValidator(validator);
            }
        }
    }
    
    /*
     * Populate PortletContainerCacheControl associated with 
     * PortletContainerRenderRequest/PortletContainerResourceRequest
     */
    private void setCacheParams(PortletContainerCacheControl pcCacheControl, 
            PortletDescriptor portletDescriptor, String eTag) {
        int expiration = 0;
        if (portletDescriptor != null) {
            expiration = portletDescriptor.getCacheExpiration();
            String scope = portletDescriptor.getCachingScope();
            if(scope != null && 
                    scope.equals(PortletDescriptorConstants.PUBLIC_CACHING_SCOPE)) {
                pcCacheControl.setPublicScope(true);            
            }
            
        }
		if(expiration == PortletDescriptorConstants.EXPIRATION_CACHE_NOT_DEFINED) {
			pcCacheControl.setExpirationTime(0);
		} else {
			pcCacheControl.setExpirationTime(expiration);
		}
        pcCacheControl.setETag(eTag);
        
        
    }

    // If the security constraint is either CONFIDENTIAL or INTEGRAL, check if 
    // the portlet is present in the security-constraint's portlet-collection list
    // If true and if the request is secure then its valid to render it and if the
    // request is not secure then the portlet should not be rendered.
    private boolean checkSecurityConstraint(HttpServletRequest request, String portletName) {
        boolean isValid = true;
        SecurityConstraintDescriptor secConsDescriptor = portletAppDescriptor.getSecurityConstraintDescriptor();
        if (secConsDescriptor != null) {
            String transportGuaranteeType = secConsDescriptor.getTransportGuaranteeType();
            if (CONFIDENTIAL.equals(transportGuaranteeType) || INTEGRAL.equals(transportGuaranteeType)) {
                List<String> portlets = secConsDescriptor.getConstrainedPortlets();
                if(portlets.contains(portletName)
                        && !request.isSecure()) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }
    
	private String getGenericPortletTitle(PortletConfig portletConfig, RenderRequest renderRequest) {
		ResourceBundle bundle = portletConfig.getResourceBundle(renderRequest.getLocale());
		return getTitleFromRB(bundle, null);
	}

    private String getTitleFromPortletDescriptor(String portletName) {
        PortletDescriptor pd = getPortletDescriptor(portletName);
        String title = null;
        if (pd.getPortletInfoDescriptor() != null) {
            title = pd.getPortletInfoDescriptor().getTitle();
        }
        return title;
    }

	private String getTitleFromRB(ResourceBundle bundle, String portletName) {
		String title = null;
        if(bundle != null) {
			// look up for title is enhanced as follows
			// 1) javax.portlet.title.PORTLETNAME
			// 2) javax.portlet.title
			title = getStringFromBundle(bundle, PortletResourceBundle.RB_TITLE + "." + portletName);
			if(title == null) {
				title = getStringFromBundle(bundle, PortletResourceBundle.RB_TITLE);
			}
			if(title == null) {
                logger.log(Level.FINER, "PSPL_PAECSPPA0023", portletName);
            }
        }
        return title;
	}

    private String getTitleFromRB(String portletName, Locale locale) {
        Map<String,Map<String,ResourceBundle>> portletResourceMap = this.portletsResources.getPortletResourceMap();
        ResourceBundle bundle = PortletContainerUtil.getResourceBundle(portletResourceMap, portletName, locale);
		return getTitleFromRB(bundle, portletName);
    }
    
	private String getStringFromBundle(ResourceBundle bundle, String key) {
		String value = null;
		try {
			value = (String) bundle.getString(key);
		} catch (MissingResourceException mre) {
			value = null;
		}
		return value;
	}
    /**
     * The title is obtained in the following sequence
     * (1) Portal Server Preferences, (2) Resource Bundle or (3) portlet.xml
     */
    private String getPortletTitle(HttpServletRequest request, PortletContainerRenderRequest pcRenderRequest,
            String entityID, String portletName) {
        //Get the title from the registry
        String title = (String) request.getAttribute(PortletContainerConstants.PORTLET_TITLE_IN_REGISTRY + entityID);
        // Compare the title obtained from the registry with the title in portlet.xml,
        // if they are same ignore it and use the title from RB.
        // By doing so we can retain the functionality of editing of the title in registry
        // and still have RB preference over portlet.xml title.
        if(title != null && title.length() != 0) {
            String portletDescriptorTitle = getTitleFromPortletDescriptor(portletName);
            if(portletDescriptorTitle != null && portletDescriptorTitle.equals(title)) {
                Locale locale = pcRenderRequest.getLocale();
                String rbTitle = getTitleFromRB(portletName, locale);
                if(rbTitle != null) {
                    title = rbTitle;
                }
            }
        }
        if(title == null || title.length() == 0) {
            logger.fine("PSPL_PAECSPPA0006");
            Locale locale = pcRenderRequest.getLocale();
            title = getTitleFromRB(portletName, locale);
        }
        if (title == null || title.length() == 0) {
			Locale locale = pcRenderRequest.getLocale();
			//Do not get the inline title for non english locale
			if(Locale.getDefault().equals(locale)) {
				logger.info("PSPL_PAECSPPA0008");
				title = getTitleFromPortletDescriptor(portletName);
			}
        }
        return title;
    }
    
    private String generateActionScopeID(PortletDescriptor portletDescriptor) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(portletDescriptor.getPortletID());
        buffer.append(Math.random());
        return buffer.toString();
    }
    
    // Returns true if the container runtime option actionScopedRequestAttribute is
    // set to true
    private boolean isActionScopedRequestAttributeEnabled(PortletID portletID) {
        String[] containerRuntimeOption = this.portletAppDescriptor.getContainerRuntimeOption(portletID, PortletDescriptorConstants.ACTION_SCOPED_REQUEST_ATTRIBUTES);
        if(containerRuntimeOption != null && "true".equals(containerRuntimeOption[0])) {
            return true;
        }
        return false;
    }
    
    // Saves scoped request attributes
    private void setScopedAttributes(String actionScopeIDValue, PortletID portletID, 
            PortletContainerRequest pcRequest, PortletRequest portletRequest) {
        Enumeration<String> attributeNames = portletRequest.getAttributeNames();
        Map<String, Serializable> scopedAttributes = new HashMap<String, Serializable>();
        while(attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            if(!attributeName.startsWith("java.")
                && !attributeName.startsWith("javax.")
                && !attributeName.startsWith(PortletContainerConstants.PREFIX)) {
                Object value = portletRequest.getAttribute(attributeName);
                if(value instanceof Serializable) {
                    scopedAttributes.put(attributeName, (Serializable)value);
                } else {
                    if(logger.isLoggable(Level.WARNING)) {
                        logger.log(Level.WARNING, "PSPL_PAECSPPA0026", value);
                    }
                }
            }
        }
        pcRequest.setScopedAttributes(actionScopeIDValue, scopedAttributes);
    }
    
    /**
     * Obtain public render parameters from render parameters map
     */
    private Map<String, String[]> getUpdatedPublicRenderParameters(PortletConfig config, 
            StateAwareResponse response){
        Map<String, String[]> renderParameters = response.getRenderParameterMap();
        Map<String, String[]> publicParameters = new HashMap<String, String[]>();
        Enumeration<String> publicParameternames = 
                config.getPublicRenderParameterNames();
        while(publicParameternames.hasMoreElements()){
            String key = publicParameternames.nextElement();
            String[] value = renderParameters.get(key);
            if(value!=null){
                publicParameters.put(key, value);
            }
        }
        
        return publicParameters;
    }
    
    /*
     * Cleanup of LifeCycleManager and RequestResponseFactory objects
     * <P>
     */
    public void destroy() {
        ((LifecycleManagerImpl) lifecycleManager).destroy();
        ((RequestResponseFactoryImpl) requestResponseFactory).destroy();
        try {
            PortletDescriptorHolderFactory.getPortletDescriptorHolder().remove(portletAppDescriptor);
        } catch (Exception ex) {
            //ignore
        }
    }
}
