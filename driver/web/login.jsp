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

<jsp:include page="header.jsp" />

<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!--Load the resource bundle for the page -->
<fmt:setBundle basename="DesktopMessages" />

<div id="portal-content">
  <h1 class="portal-content-header"><fmt:message key="signIn"/></h1>
  <form id="portal-login" method="POST" name="loginform" action="j_security_check" >
    <fieldset>
      <label for="j_username"><fmt:message key="userName"/></label>
      <input type="text" name="j_username" id="j_username" />
      <label for="j_password"><fmt:message key="password"/></label>
      <input type="password" name="j_password" id="j_password" />
      <input id="submit" type="submit" value="<fmt:message key="signIn"/>" />  
      <input id="reset" type="reset" value="<fmt:message key="clear"/>" />
    </fieldset>
  </form>
</div> <!-- closes portal-content -->
    
</div> <!-- closes portal-page -->
</body>
</html>
