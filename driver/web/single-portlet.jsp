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

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!--Load the resource bundle for the page -->
<fmt:setBundle basename="DesktopMessages" />

<c:set var="portlet" value="${requestScope.PortletWindow}"/>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><c:out value="${portlet.title}" escapeXml="false"/></title>
        <link rel="stylesheet" href="css/screen.css" type="text/css" media="screen" />
    </head>
    <body>

        <div class="portlet">

        <div class="portlet-header">
  
            <h2 class="portlet-title"><c:out value="${portlet.title}" escapeXml="false"/></h2>
    
            <ul class="portlet-options">
    
                <li>
                    <a href="${portlet.helpURL}">
                        <img src="images/help_button.gif" alt="<fmt:message key="help"/>" title="<fmt:message key="help"/>" />
                    </a>
                </li>


                <li>
                    <a href="${portlet.editURL}">
                        <img src="images/edit_button.gif" alt="<fmt:message key="edit"/>" title="<fmt:message key="edit"/>" />
                    </a>
                </li>


                <li>
                    <a href="${portlet.viewURL}">
                        <img src="images/view_button.gif" alt="<fmt:message key="view"/>" title="<fmt:message key="view"/>" />
                    </a>
                </li>

            </ul>
    
        </div> <!-- closes portlet-header -->
  
        <div class="portlet-content">
            <c:out value="${portlet.content}" escapeXml="false"/>
        </div>
    </body>
</html>