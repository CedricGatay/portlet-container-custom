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

<jsp:include page="header.jsp" flush="true" />

<jsp:include page="tabs.jsp" flush="true">
  <jsp:param name="selectedTab" value="wsrp" />
</jsp:include>
    
<div id="portal-content">

  <c:if test="${layout==null}">
      <c:set var="layout" value="1" scope="session" />
  </c:if>
  <c:if test="${param.layout!=null}">
    <c:set var="layout" value="${param.layout}" scope="session" />
  </c:if>
   
  <jsp:include page="change-layout.jsp" flush="true" />
  
  <c:choose>
    <c:when test='${layout == "1"}'>
      <jsp:include page="layout-1.jsp" flush="true"/>
    </c:when>
    <c:when test='${layout == "2"}'>
      <jsp:include page="layout-2.jsp" flush="true"/>
    </c:when>
    <c:otherwise>
      <jsp:include page="layout-3.jsp" flush="true"/>
    </c:otherwise>
  </c:choose>

</div> <!-- closes portal-content -->
  
</div> <!-- closes portal-page -->
</body>
</html>