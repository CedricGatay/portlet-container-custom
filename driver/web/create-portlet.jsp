<%--
  CDDL HEADER START
  The contents of this file are subject to the terms
  of the Common Development and Distribution License
  (the License). You may not use this file except in
  compliance with the License.

  You can obtain a copy of the License at
  http://www.sun.com/cddl/cddl.html and legal/CDDLv1.0.txt
  See the License for the specific language governing
  permission and limitations under the License.

  When distributing Covered Code, include this CDDL
  Header Notice in each file and include the License file
  at legal/CDDLv1.0.txt.
  If applicable, add the following below the CDDL Header,
  with the fields enclosed by brackets [] replaced by
  your own identifying information:
  "Portions Copyrighted [year] [name of copyright owner]"

  Copyright 2009 Sun Microsystems Inc. All Rights Reserved
  CDDL HEADER END
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.sun.portal.portletcontainer.driver.DriverUtil, 
                com.sun.portal.portletcontainer.driver.admin.AdminConstants" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!--Load the resource bundle for the page -->
<fmt:setBundle basename="DesktopMessages" />

<h1 class="portal-content-header" id="create-portlet-content"><fmt:message key="createPortlet"/></h1>

<c:set value="${sessionScope['com.sun.portal.portletcontainer.driver.admin.creationFailed']}" var="msgFail" />
<c:if test="${msgFail != null}" >
    <h2 id="create-failed"><c:out value="${sessionScope['com.sun.portal.portletcontainer.driver.admin.creationFailed']}" escapeXml="false"/></h2>
</c:if>

<c:set value="${sessionScope['com.sun.portal.portletcontainer.driver.admin.creationSucceeded']}" var="msgSuccess" />
<c:if test="${msgSuccess != null}" >
    <h2 id="create-success"><c:out value="${sessionScope['com.sun.portal.portletcontainer.driver.admin.creationSucceeded']}" escapeXml="false"/></h2>
</c:if>

<form id="create-portlet" name="createForm" method="post" action="<%=DriverUtil.getAdminURL(request)%>" >
    <fieldset>
        <c:set var="list" value="${sessionScope['com.sun.portal.portletcontainer.driver.admin.portlets']}" />
        <label for="portletList"><fmt:message key="selectBasePortlet"/></label>
        <select id="portletList" name="<%=AdminConstants.PORTLET_LIST%>">
            <c:forEach items="${list}" var="portlet">
                <option value="<c:out value="${portlet}" />" >
                        <c:out value="${portlet}" />
                </option>
            </c:forEach>
        </select>
        
        <label for="portletWindowName"><fmt:message key="portletWindow"/></label>
        <input id="portletWindowName" type="text" size="30" maxlength="20" name="<%=AdminConstants.PORTLET_WINDOW_NAME%>" value="" />
        
        <label for="title"><fmt:message key="portletTitle"/></label>
        <input id="title" type="text" size="30"  name="<%=AdminConstants.PORTLET_WINDOW_TITLE%>" value="" maxlength="20" />
        
        <input type="Submit" name="<%=AdminConstants.CREATE_PORTLET_WINDOW_SUBMIT%>" id="create" value="<fmt:message key="createPortletWindow"/>"/>
    </fieldset>
</form>