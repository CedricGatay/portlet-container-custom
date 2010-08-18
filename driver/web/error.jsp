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
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="header.jsp" flush="true"/>

<!--Load the resource bundle for the page -->
<fmt:setBundle basename="DesktopMessages" />

<div id="portal-content">
  <h1 class="portal-content-header"><fmt:message key="errorOcurred"/></h1>
  <p><fmt:message key="invalid"/><a href="authorized"><fmt:message key="toReturn"/>&nbsp;</a><fmt:message key="toRetry"/></p>
</div> <!-- closes portal-content -->

</div> <!-- closes portal-page -->
</body>
</html>