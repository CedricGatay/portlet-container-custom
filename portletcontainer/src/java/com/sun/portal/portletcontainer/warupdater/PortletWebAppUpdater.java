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

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Iterator;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Comment;
import org.xml.sax.SAXException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sun.portal.portletcontainer.common.PortletContainerUtil;


/**
 * PortletWebAppUpdater is responsible for updating the web.xml file
 * in the PortletApplication war file.
 * <p/>
 * The addWebAppParam() method takes the InputStream of the web.xml file
 * and checks for the listener classes and PAE servlet associated with
 * the PortletContainer. If any of these elements are found in the Document,
 * it will remove them and readd them.
 * <p/>
 * The removeWebAppParam() method takes the InputStream of the web.xml file
 * and removes the listener classes, context Params, PAE servlet associated
 * with the portletcontainer.
 * <p/>
 * The isWebAppUpdated() method checks if any of the elements associated with
 * the portletContainer are present in the web.xml document and returns true/false
 * based on the results.
 */
public class PortletWebAppUpdater {

    private static Logger logger = PortletWarUpdaterUtil.getLogger(PortletWebAppUpdater.class,
            "PWULogMessages");

    private static final String PAE_SERVLET_NAME = "PortletAppEngineServlet";
    private static final String PAE_SERVLET_MAPPING = "/servlet/PortletAppEngineServlet";
    private static final String SERVLET_CLASS_NAME = "com.sun.portal.portletcontainer.appengine.PortletAppEngineServlet";
    private static final String PAE_FILTER_NAME = "PortletAppEngineFilter";
    private static final String FILTER_CLASS_NAME = "com.sun.portal.portletcontainer.appengine.PortletAppEngineFilter";

    //properties key from PortletDeployConfig.properties
    private static final String LOAD_ON_STARTUP_PROPERTY = "portlet.loadOnStartup";
    private static final String VENDOR_PORTLET_XML_PREFIX = "vendorPortletXML";
    private static final List contextParams = new ArrayList();
    static {
        contextParams.add("request_response_factory.minSizeParam");
        contextParams.add("request_response_factory.maxSizeParam");
        contextParams.add("request_response_factory.partitionParam");
    }
    
    private static final String CONTEXT_PARAM = "context-param";
    private static final String PARAM_NAME = "param-name";
    private static final String PARAM_VALUE = "param-value";
    private static final String LISTENER = "listener";
    private static final String LISTENER_CLASS = "listener-class";
    private static final String SERVLET = "servlet";
    private static final String SERVLET_NAME = "servlet-name";
    private static final String SERVLET_CLASS = "servlet-class";
    private static final String LOAD_ON_STARTUP = "load-on-startup";
    private static final String SERVLET_MAPPING = "servlet-mapping";
    private static final String FILTER = "filter";
    private static final String FILTER_NAME = "filter-name";
    private static final String FILTER_CLASS = "filter-class";
    private static final String FILTER_MAPPING = "filter-mapping";
    private static final String DISPATCHER_NAME = "dispatcher";
    private static final String DISPATCHER_TYPE = "INCLUDE";
    private static final String URL_PATTERN = "url-pattern";
    private static final String SECURITY_ROLE = "security-role";
    private static final String ROLE_NAME = "role-name";
    private static final String TAGLIB = "taglib";
    private static final String JSP_CONFIG = "jsp-config";
    private static final String TAGLIB_URI = "taglib-uri";
    private static final String TAGLIB_LOCATION = "taglib-location";
    private static final String taglibURI_1 = "http://java.sun.com/portlet";
    private static final String taglibLocation_1 = "/WEB-INF/sun-portlet.tld";
    private static final String taglibURI_2 = "http://java.sun.com/portlet_2_0";
    private static final String taglibLocation_2 = "/WEB-INF/sun-portlet_2_0.tld";
    private static final String contextParamComment = "Init Parameters";
    private static final String servletComment = "PortletAppEngine Servlet";
    private static final String filterComment = "PortletAppEngine Filter";
    private static final String servletMappingComment = "PAE Servlet Mapping";
    private static final String filterMappingComment = "PAE Filter Mapping";
    private static final String taglibComment_1 = "taglib for portlet v1.0";
    private static final String taglibComment_2 = "taglib for portlet v2.0";
    private static List elementBeforeContextParam = new ArrayList();
    private static List elementBeforeFilter = new ArrayList();
    private static List elementBeforeFilterMapping = new ArrayList();
    private static List elementBeforeListener = new ArrayList();
    private static List elementBeforeTaglib = new ArrayList();
    private static List elementBeforeServletMapping = new ArrayList();
    static {
        elementBeforeContextParam.add("icon");
        elementBeforeContextParam.add("display-name");
        elementBeforeContextParam.add("description");
        elementBeforeContextParam.add("distributable");
        elementBeforeContextParam.add("context-param");

        elementBeforeFilter.add("filter");

        elementBeforeFilterMapping.add("filter-mapping");

        elementBeforeListener.add("listener");

        elementBeforeServletMapping.add("servlet");

        elementBeforeTaglib.add("servlet-mapping");
        elementBeforeTaglib.add("session-config");
        elementBeforeTaglib.add("mime-mapping");
        elementBeforeTaglib.add("welcome-file-list");
        elementBeforeTaglib.add("error-page");
    }
    private static final String WEB_APP_VERSION = "version";
    private static final String WEB_APP_2_4 = "2.4";
    private static final String WEB_APP_2_5 = "2.5";
	private static final String LINE_BREAK = "/n";

    public static List<String> getRoles(InputStream in) throws IOException, PortletWarUpdaterException {
        Document doc = readFile(in);
        Element root = getRootElement(doc);
        List<String> roles = new ArrayList<String>();
        List sRoleElements = WebXMLDocumentHelper.getChildElements(root,
                SECURITY_ROLE);
        for (int i = 0; i < sRoleElements.size(); i++) {
            Element sRoleElement = (Element) sRoleElements.get(i);
            Element roleName = WebXMLDocumentHelper.getChildElement(sRoleElement,
                    ROLE_NAME);
            if (roleName != null) {
                String role = WebXMLDocumentHelper.getTextTrim(roleName);
                if (role != null && role.length() != 0) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    protected static File addWebAppParam(InputStream in,
            Properties configProps, String portletAppName) throws IOException, PortletWarUpdaterException {
        Document doc = readFile(in);
        Element root = getRootElement(doc);

        // check if the web.xml is already updated, if it is remove
        // the updates first and then readd them.
        boolean updated = isWebAppUpdated(root, configProps);
        if (updated) {
            try {
                removeElements(root, doc, configProps);
            } catch (PortletWarUpdaterException pe) {
                throw new PortletWarUpdaterException("errorIllegalAdd", pe);
            }
        }

        try {
            String version = getWebAppVersion(root);
            logger.log(Level.FINER, "PSPL_CSPPCWU0012", version);
            if ((version != null) && ((version.equals(WEB_APP_2_4)) || (version.equals(WEB_APP_2_5)))) {
                addElementsAsPer2_4Schema(root, doc, configProps);
            } else {
                addElements(root, doc, configProps);
            }
        } catch (DOMException de) {
            throw new PortletWarUpdaterException("errorIllegalAdd", de);
        }

        return createUpdatedFile(doc);
    }

    protected static File removeWebAppParam(InputStream in,
            Properties configProps) throws IOException, PortletWarUpdaterException {
        Document doc = readFile(in);
        Element root = getRootElement(doc);
        try {
            removeElements(root, doc, configProps);
        } catch (DOMException de) {
            throw new PortletWarUpdaterException("errorIllegalAdd", de);
        }
        return createUpdatedFile(doc);
    }

    protected static boolean isWebAppUpdated(InputStream in,
            Properties configProps) throws PortletWarUpdaterException {
        Document doc = readFile(in);
        Element root = getRootElement(doc);

        return isWebAppUpdated(root, configProps);
    }

    private static boolean isWebAppUpdated(Element root,
            Properties configProps) throws PortletWarUpdaterException {
        boolean updated = false;

        List children = WebXMLDocumentHelper.getElementList(root);
        Iterator itr = children.iterator();
        while (itr.hasNext()) {
            Element child = (Element) itr.next();
            if (child.getTagName().equals(CONTEXT_PARAM)) {
                String param = WebXMLDocumentHelper.getChildTextTrim(child,
                        PARAM_NAME);
                if (contextParams.contains(param)) {
                    updated = true;
                    return updated;
                }
            } else if (child.getTagName().equals(SERVLET)) {
                String param = WebXMLDocumentHelper.getChildTextTrim(child,
                        SERVLET_NAME);
                if (param.equals(PAE_SERVLET_NAME)) {
                    updated = true;
                    return updated;
                }
            }
        }
        return updated;
    }

    protected static Document readFile(InputStream webStream) throws PortletWarUpdaterException {
        try {
            return PortletContainerUtil.getDocumentBuilder().parse(webStream);
        } catch (ParserConfigurationException pce) {
            throw new PortletWarUpdaterException(pce);
        } catch (SAXException saxe) {
            throw new PortletWarUpdaterException(saxe);
        } catch (IOException ioe) {
            throw new PortletWarUpdaterException(ioe);
        }
    }

    protected static Element getRootElement(Document document) {
        if (document != null) {
            return document.getDocumentElement();
        }
        return null;
    }

    protected static synchronized File createUpdatedFile(Document document)
            throws IOException, PortletWarUpdaterException {
        // create a temp file
        File newWebXMLFile;
        newWebXMLFile = File.createTempFile("web", ".xml");
        newWebXMLFile.deleteOnExit();

        try {
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
			setOutputDoctype(document, transformer);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                    "5");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(newWebXMLFile);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException tce) {
            throw new PortletWarUpdaterException(tce);
        } catch (TransformerException te) {
            throw new PortletWarUpdaterException(te);
        }
        return newWebXMLFile;
    }

	/** Use the same DOCTYPE declaration as given in the DOC document */
	private static void setOutputDoctype(Document document, Transformer transformer) {
        DocumentType doctype = document.getDoctype();
        if(doctype != null) {
			String id = doctype.getSystemId();
			if(id == null || id.length() == 0)
				return;
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, id);
			
			id = doctype.getPublicId();
			if(id == null || id.length() == 0)
				return;
 
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, id);
		}
    }
  
	private static void addElements(Element root, Document doc,
            Properties configProps) throws DOMException {
        List children = WebXMLDocumentHelper.getElementList(root);
        int size = children.size();

        int newSize = size + contextParams.size() + 1;
        List newChildren = new ArrayList(newSize);

        int i;
        int j = 0;

        boolean addContextParam = false;
        for (i = 0; i < size; i++) {
            Element child = (Element) children.get(i);
            if (elementBeforeContextParam.contains(child.getTagName())) {
                newChildren.add(child);
            } else {
                newChildren.addAll(genContextParamElements(doc, configProps));
                addContextParam = true;
                break;
            }
        }
        if (!addContextParam) {
            newChildren.addAll(genContextParamElements(doc, configProps));
        }

        boolean addFilterParam = false;
        for (j = i; j < size; j++) {
            Element child = (Element) children.get(j);
            if (elementBeforeFilter.contains(child.getTagName())) {
                newChildren.add(child);
            } else {
                newChildren.addAll(genFilterElements(doc, configProps));
                addFilterParam = true;
                break;
            }
        }
        if (!addFilterParam) {
            newChildren.addAll(genFilterElements(doc, configProps));
        }

        boolean addFilterMappingParam = false;
        for (j = i; j < size; j++) {
            Element child = (Element) children.get(j);
            if (elementBeforeFilterMapping.contains(child.getTagName())) {
                newChildren.add(child);
            } else {
                newChildren.addAll(genFilterMappingElements(doc));
                addFilterMappingParam = true;
                break;
            }
        }
        if (!addFilterMappingParam) {
            newChildren.addAll(genFilterMappingElements(doc));
        }

        boolean addServletParam = false;
        for (j = i; j < size; j++) {
            Element child = (Element) children.get(j);
            if (elementBeforeListener.contains(child.getTagName())) {
                newChildren.add(child);
            } else {
                newChildren.addAll(genServletElements(doc, configProps));
                addServletParam = true;
                break;
            }
        }
        if (!addServletParam) {
            newChildren.addAll(genServletElements(doc, configProps));
        }

        boolean addServletMappingParam = false;
        for (i = j; i < size; i++) {
            Element child = (Element) children.get(i);
            if (elementBeforeServletMapping.contains(child.getTagName())) {
                newChildren.add(child);
            } else {
                newChildren.addAll(genServletMappingElements(doc));
                addServletMappingParam = true;
                break;
            }
        }
        if (!addServletMappingParam) {
            newChildren.addAll(genServletMappingElements(doc));
        }

        boolean addTaglibParam = false;
        for (j = i; j < size; j++) {
            Element child = (Element) children.get(j);
            if (elementBeforeTaglib.contains(child.getTagName())) {
                newChildren.add(child);
            } else {
                newChildren.addAll(genTaglibElements(doc));
                addTaglibParam = true;
                break;
            }
        }


        if (!addTaglibParam) {
            newChildren.addAll(genTaglibElements(doc));
        }

        for (i = j; i < size; i++) {
            newChildren.add(children.get(i));
        }
        Iterator it = WebXMLDocumentHelper.getElementList(root).iterator();
        List list = new ArrayList();
        //need to copy names to avoid simultaneous update of iterator
        while (it.hasNext()) {
            list.add((Element) it.next());
        }
        for (int count = 0; count < list.size(); count++) {
            //using removeChild and not removeChildren to make sure
            //next call to the same doesn't fails in case multiple
            //child has same name
            root.removeChild((Node) list.get(count));
        }
        WebXMLDocumentHelper.addContent(root, newChildren);
    }

    private static void addElementsAsPer2_4Schema(Element root,
            Document doc, Properties configProps) throws DOMException {

        List children = WebXMLDocumentHelper.getElementList(root);
        int size = children.size();

        int newSize = size + contextParams.size() + 30; // for filter and servlet elements
        List newChildren = new ArrayList(newSize);

        //The elements in the web_app2.4 web.xml can be in any order.
        int i = 0;
        boolean addTaglibParam = false;
        //copy all the elements to new list
        for (i = 0; i < size; i++) {
            Element child = (Element) children.get(i);
            if (child.getTagName().equals(JSP_CONFIG)) {
                Element newChild = (Element) child.cloneNode(true);

                WebXMLDocumentHelper.insertContent(newChild,
                        genTaglibElements(doc));

                newChildren.add(newChild);
                addTaglibParam = true;
            } else {
                newChildren.add(child);
            }
        }
        //copy the new context-param elements
        newChildren.addAll(genContextParamElements(doc, configProps));

        //copy the new filter elements
        newChildren.addAll(genFilterElements(doc, configProps));

        //copy the new filter-mapping element
        newChildren.addAll(genFilterMappingElements(doc));

        //copy the new servlet elements
        newChildren.addAll(genServletElements(doc, configProps));

        //copy the new servlet-mapping element
        newChildren.addAll(genServletMappingElements(doc));

        //if jsp-config element was not already present, create new
        if (!addTaglibParam) {
            //create a new jsp-config element with  new taglib element
            Element child = doc.createElement(JSP_CONFIG);
            WebXMLDocumentHelper.addContent(child, genTaglibElements(doc));
            newChildren.add(child);
        }

        Iterator it = WebXMLDocumentHelper.getElementList(root).iterator();
        List list = new ArrayList();
        //need to copy names to avoid simultaneous update of iterator
        while (it.hasNext()) {
            list.add((Element) it.next());
        }
        for (int count = 0; count < list.size(); count++) {
            //using removeChild and not removeChildren to make sure
            //next call to the same doesn't fails in case multiple
            //child has same name
            root.removeChild((Node) list.get(count));
        }
        //set the new content to root
        WebXMLDocumentHelper.addContent(root, newChildren);
    }

    private static List genContextParamElements(Document doc,
            Properties configProps) {

        List allContextParams = createVendorPortletXMLParams(configProps);
        allContextParams.addAll(0, contextParams);
        List contextParamElements = new ArrayList(allContextParams.size());

        Comment comment = doc.createComment(contextParamComment);

        for (int i = 0; i < allContextParams.size(); i++) {
            Element contextParam = doc.createElement(CONTEXT_PARAM);
            Element paramName = doc.createElement(PARAM_NAME);
            WebXMLDocumentHelper.addContent(paramName, doc,
                    (String) allContextParams.get(i));
            Element paramValue = doc.createElement(PARAM_VALUE);
            WebXMLDocumentHelper.addContent(paramValue, doc,
                    configProps.getProperty((String) allContextParams.get(i)));
            WebXMLDocumentHelper.addContent(contextParam, paramName);
            WebXMLDocumentHelper.addContent(contextParam, paramValue);
            if (i == 0) {
                paramName.getParentNode().insertBefore(comment, paramName);
            }
            contextParamElements.add(contextParam);
        }

        return contextParamElements;
    }

    // add the vendor portlet xml related properties as context init parameters
    private static List createVendorPortletXMLParams(Properties configProps) {
        List parameters = new ArrayList();
        if(configProps != null) {
            Enumeration e = configProps.propertyNames();
            while(e.hasMoreElements()) {
                String key = (String)e.nextElement();
                if(key.startsWith(VENDOR_PORTLET_XML_PREFIX)) {
                    parameters.add(key);
                }
            }
        }
        return parameters;
    }
            
    private static List genServletElements(Document doc,
            Properties configProps) {

        List servletElements = new ArrayList();

        Comment comment = doc.createComment(servletComment);
        Element servletElement = doc.createElement(SERVLET);
        Element servletNameElement = doc.createElement(SERVLET_NAME);
        WebXMLDocumentHelper.addContent(servletNameElement, doc,
                PAE_SERVLET_NAME);

        Element servletClassElement = doc.createElement(SERVLET_CLASS);
        WebXMLDocumentHelper.addContent(servletClassElement, doc,
                SERVLET_CLASS_NAME);
        String value = configProps.getProperty(LOAD_ON_STARTUP_PROPERTY);
        boolean loadOnStartup = true;
        Element loadOnStartupElement = null;
        if ("false".equalsIgnoreCase(value)) {
            loadOnStartup = false;
        }
        if(loadOnStartup) {
            loadOnStartupElement = doc.createElement(LOAD_ON_STARTUP);
            WebXMLDocumentHelper.addContent(loadOnStartupElement, doc, "1");
        }

        WebXMLDocumentHelper.addContent(servletElement, servletNameElement);
        WebXMLDocumentHelper.addContent(servletElement, servletClassElement);
        if (loadOnStartup && loadOnStartupElement != null) {
            WebXMLDocumentHelper.addContent(servletElement, loadOnStartupElement);
        }
        servletNameElement.getParentNode().insertBefore(comment,
                servletNameElement);
        servletElements.add(servletElement);

        return servletElements;
    }

    private static List genServletMappingElements(Document doc) {

        List servletMappingElements = new ArrayList();
        Comment comment = doc.createComment(servletMappingComment);
        Element servletMappingElement = doc.createElement(SERVLET_MAPPING);
        Element servletNameElement = doc.createElement(SERVLET_NAME);
        WebXMLDocumentHelper.addContent(servletNameElement, doc,
                PAE_SERVLET_NAME);
        Element urlPatternElement = doc.createElement(URL_PATTERN);
        WebXMLDocumentHelper.addContent(urlPatternElement, doc,
                PAE_SERVLET_MAPPING);
        WebXMLDocumentHelper.addContent(servletMappingElement,
                servletNameElement);
        WebXMLDocumentHelper.addContent(servletMappingElement, urlPatternElement);
        servletNameElement.getParentNode().insertBefore(comment,
                servletNameElement);
        servletMappingElements.add(servletMappingElement);
        return servletMappingElements;
    }

    private static List genFilterElements(Document doc,
            Properties configProps) {

        List filterElements = new ArrayList();

        Comment comment = doc.createComment(filterComment);
        Element filterElement = doc.createElement(FILTER);
        Element filterNameElement = doc.createElement(FILTER_NAME);
        WebXMLDocumentHelper.addContent(filterNameElement, doc, PAE_FILTER_NAME);

        Element filterClassElement = doc.createElement(FILTER_CLASS);
        WebXMLDocumentHelper.addContent(filterClassElement, doc,
                FILTER_CLASS_NAME);

        WebXMLDocumentHelper.addContent(filterElement, filterNameElement);
        WebXMLDocumentHelper.addContent(filterElement, filterClassElement);
        filterNameElement.getParentNode().insertBefore(comment,
                filterNameElement);
        filterElements.add(filterElement);

        return filterElements;
    }

    private static List genFilterMappingElements(Document doc) {

        List filterMappingElements = new ArrayList();
        Comment comment = doc.createComment(filterMappingComment);
        Element filterMappingElement = doc.createElement(FILTER_MAPPING);
        Element filterNameElement = doc.createElement(FILTER_NAME);
        WebXMLDocumentHelper.addContent(filterNameElement, doc, PAE_FILTER_NAME);
        Element servletNameElement = doc.createElement(SERVLET_NAME);
        WebXMLDocumentHelper.addContent(servletNameElement, doc,
                PAE_SERVLET_NAME);
		Element dispatcherNameElement = null;
		//Do not add dispatcher for if DTD is present in the web.xml, this might by 2.3
		if(doc.getDoctype() == null) {
			dispatcherNameElement = doc.createElement(DISPATCHER_NAME);
			WebXMLDocumentHelper.addContent(dispatcherNameElement, doc,
					DISPATCHER_TYPE);
		}
        WebXMLDocumentHelper.addContent(filterMappingElement, filterNameElement);
        WebXMLDocumentHelper.addContent(filterMappingElement, servletNameElement);
		if(dispatcherNameElement != null) {
			WebXMLDocumentHelper.addContent(filterMappingElement,
					dispatcherNameElement);
		}
        filterNameElement.getParentNode().insertBefore(comment,
                filterNameElement);
        filterMappingElements.add(filterMappingElement);
        return filterMappingElements;
    }

    private static List genTaglibElements(Document doc) {
        List taglibElements = new ArrayList();

        taglibElements.add(getTabLibElement(doc, taglibComment_1, taglibURI_1,
                taglibLocation_1));
        taglibElements.add(getTabLibElement(doc, taglibComment_2, taglibURI_2,
                taglibLocation_2));

        return taglibElements;
    }

    private static Element getTabLibElement(Document doc,
            String comment, String uri, String location) {
        Comment commentElement = doc.createComment(comment);
        Element taglibElement = doc.createElement(TAGLIB);
        Element taglibURIElement = doc.createElement(TAGLIB_URI);
        WebXMLDocumentHelper.addContent(taglibURIElement, doc, uri);
        Element taglibLocationElement = doc.createElement(TAGLIB_LOCATION);
        WebXMLDocumentHelper.addContent(taglibLocationElement, doc, location);
        WebXMLDocumentHelper.addContent(taglibElement, taglibURIElement);
        WebXMLDocumentHelper.addContent(taglibElement, taglibLocationElement);
        taglibURIElement.getParentNode().insertBefore(commentElement,
                taglibURIElement);

        return taglibElement;
    }

    private static void removeElements(Element root, Document doc,
            Properties configProps) throws PortletWarUpdaterException {

        List children = WebXMLDocumentHelper.getElementList(root);
        List newchildren = new ArrayList();
        Iterator itr = children.iterator();
		boolean addTagLib = false;

        while (itr.hasNext()) {
            Element child = (Element) itr.next();
            if (child.getTagName().equals(CONTEXT_PARAM)) {
                String param = WebXMLDocumentHelper.getChildTextTrim(child,
                        PARAM_NAME);
                if (!contextParams.contains(param) && !configProps.containsKey(param)) {
                    newchildren.add(child);
                }
            } else if (child.getTagName().equals(SERVLET)) {
                String param = WebXMLDocumentHelper.getChildTextTrim(child,
                        SERVLET_NAME);
                if (!PAE_SERVLET_NAME.equals(param)) {
                    newchildren.add(child);
                }
            } else if (child.getTagName().equals(SERVLET_MAPPING)) {
                String param = WebXMLDocumentHelper.getChildTextTrim(child,
                        SERVLET_NAME);
                if (!PAE_SERVLET_NAME.equals(param)) {
                    newchildren.add(child);
                }
            } else if (child.getTagName().equals(FILTER)) {
                String param = WebXMLDocumentHelper.getChildTextTrim(child,
                        FILTER_NAME);
                if (!PAE_FILTER_NAME.equals(param)) {
                    newchildren.add(child);
                }
            } else if (child.getTagName().equals(FILTER_MAPPING)) {
                String param = WebXMLDocumentHelper.getChildTextTrim(child,
                        FILTER_NAME);
                if (!PAE_FILTER_NAME.equals(param)) {
                    newchildren.add(child);
                }
            } else if (child.getTagName().equals(JSP_CONFIG)) {
                List<Element> taglibs = WebXMLDocumentHelper.getChildElements(child, TAGLIB);
				for(Element taglib : taglibs) {
					String param = WebXMLDocumentHelper.getChildTextTrim(taglib,
							TAGLIB_LOCATION);
					if (taglibLocation_1.equals(param) || taglibLocation_2.equals(param)) {
						child.removeChild(taglib);
						addTagLib = true;
					}
				}
				if(addTagLib) {
					newchildren.add(child);
				}
            } else {
                newchildren.add(child);
            }
        }

		List<Node> nodes = WebXMLDocumentHelper.getElementList(root);
		for(Node node :  nodes) {
			root.removeChild(node);
		}
        removeAll(doc, Node.COMMENT_NODE, null);
        removeAll(doc, Node.TEXT_NODE, LINE_BREAK);

        WebXMLDocumentHelper.addContent(root, newchildren);
    }

    private static String getWebAppVersion(Element root) throws PortletWarUpdaterException {

        String versionValue = null;
        // Get all the attributes of an element in a map
        NamedNodeMap attrs = root.getAttributes();

        // Get number of attributes in the element
        int numAttrs = attrs.getLength();

        // Process each attribute
        for (int i = 0; i < numAttrs; i++) {
            Attr attr = (Attr) attrs.item(i);

            // Get attribute name and value
            String attrName = attr.getNodeName();
            if (attrName.equals(WEB_APP_VERSION)) {
                versionValue = root.getAttribute(WEB_APP_VERSION);
                //if element <web-app> has version attribute , it should have a value 2.4  or above
                if (versionValue == null || (Float.parseFloat(versionValue) < Float.parseFloat(WEB_APP_2_4))) {
                    throw new PortletWarUpdaterException("errorGettingVersionValue");
                }
                break;
            }
        }
        //returns null if attribute version does not exist
        return versionValue;
    }

    protected static void removeAll(Node node, short nodeType,
            String name) {
        if (node.getNodeType() == nodeType) {
			if (name == null || node.getNodeName().equals(name)) {
				node.getParentNode().removeChild(node);
			} else if (LINE_BREAK.equals(name)) {
				node.getParentNode().removeChild(node);
			}
        } else {
            // Visit the children
            NodeList list = node.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                removeAll(list.item(i), nodeType, name);
            }
        }
    }

    public static void main(String[] args) {
        try {
            String webXML = args[0];
            java.io.File webXMLFile = new java.io.File(webXML);
            java.io.InputStream webXMLStream = new java.io.FileInputStream(webXMLFile);
            java.io.InputStream configPropsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("PortletDeployConfig.properties");
            java.util.Properties configProps = new Properties();
            configProps.load(configPropsStream);
            File newWebXMLFile = addWebAppParam(webXMLStream, configProps,
                    "test");
            File tmpFile = new File("/tmp/webnew.xml");
            PortletWarUpdaterUtil.copyFile(newWebXMLFile, tmpFile, true, false);
            System.out.println("File:" + newWebXMLFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}