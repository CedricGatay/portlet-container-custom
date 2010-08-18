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
<%@page import="com.sun.portal.portletcontainer.driver.DriverUtil" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!--Load the resource bundle for the page -->
<fmt:setBundle basename="DesktopMessages" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="portletDriver"/></title>
    <link rel="stylesheet" href="css/screen.css" type="text/css" media="screen" />
    <c:set var="list" value="${sessionScope['com.sun.portal.portletcontainer.markupHeaders']}" />
    <c:forEach items="${list}" var="portlet">
        <c:out value="${portlet}" escapeXml="false" />
    </c:forEach>
</head>
<body>

<div id="portal-page">

<div id="portal-header">
    <h1><a href="<%=DriverUtil.getPortletsURL(request)%>"><img src="images/logo.gif" alt="<fmt:message key="portletDriver"/>" /></a></h1>
    <ul id="portal-options">
        <li><a href="<%=DriverUtil.getPortletsURL(request)%>" class="first-child"><fmt:message key="home"/></a></li>
        <% 
        java.security.Principal user = request.getUserPrincipal();
        
        //if(user == null || (user != null && session.isNew())) {
        if(user == null) {
        %>
        <li><a href="authorized"><fmt:message key="signIn"/></a></li>        
        <%
        } else {
        %>
        <li><a href="logout.jsp"><fmt:message key="signOut"/></a></li>
        <%
        }
        %>
        <li><a href="support.jsp"><fmt:message key="support"/></a></li>
    </ul>
</div> <!-- closes portal-header -->