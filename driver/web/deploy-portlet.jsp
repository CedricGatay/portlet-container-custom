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
<%@page import="com.sun.portal.portletcontainer.driver.admin.AdminConstants" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!--Load the resource bundle for the page -->
<fmt:setBundle basename="DesktopMessages" />

<script language="JavaScript">
function addRolesTextbox(checkObj)
{
  var rolesDivObj = document.getElementById("roles");
  if(checkObj.checked) {
     rolesDivObj.style.display = "block";
  } else {
     rolesDivObj.style.display = "none";
  }
}
</script>
<noscript>Enable Javascript</noscript>

<h1 class="portal-content-header"><fmt:message key="deployPortlets"/></h1>

<c:set value="${sessionScope['com.sun.portal.portletcontainer.driver.admin.deploymentFailed']}" var="msgFail" />
<c:if test="${msgFail != null}" >
  <h2 id="deploy-failed"><c:out value="${sessionScope['com.sun.portal.portletcontainer.driver.admin.deploymentFailed']}" escapeXml="false"/></h2>
</c:if>

<c:set value="${sessionScope['com.sun.portal.portletcontainer.driver.admin.deploymentSucceeded']}" var="msgSuccess" />
<c:if test="${msgSuccess != null}" >
  <h2 id="deploy-success"><c:out value="${sessionScope['com.sun.portal.portletcontainer.driver.admin.deploymentSucceeded']}" escapeXml="false"/></h2>
</c:if>

<form id="deploy-portlet" METHOD="POST" name="deployForm" enctype="multipart/form-data"  action="upload" >
  <fieldset>
    <label for="filename"><fmt:message key="selectWar"/></label>
    <input id="filename" type="file" name="filename"  size="50" />
    <table>
    	<tr>
           <td scope="col">
               <input id="portletWindowCheck" type="checkbox" name="HidePortletWindowCheck" />
           </td>
           <td scope="col">
               <label for="portletWindowCheck"><fmt:message key="hidePortletWindow"/></label>
           </td>
        </tr>
    	<tr>
           <td scope="col">
               <input id="rolescheck" type="checkbox" name="addRolesCheck" onClick="addRolesTextbox(this)" onKeyPress="addRolesTextbox(this)"/>
           </td>
           <td scope="col">
               <label for="rolescheck"><fmt:message key="addRoles"/></label>
           </td>
        </tr>
    </table>
    <div id="roles" style="display:none">
       <label for="rolefilename"><fmt:message key="selectRoles"/></label>
       <input id="rolefilename" type="file" name="rolefilename"  size="50" />
    </div>
    <input type="submit" name="Submit" value="<fmt:message key="deploy"/>" id="deploy" />
  </fieldset>
</form>
