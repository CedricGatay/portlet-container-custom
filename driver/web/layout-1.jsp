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
<%@taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set value="${sessionScope['com.sun.portal.portletcontainer.driver.portletWindows']}" var="map"/>
<c:set var="thinportlets" value='${map["thin"]}' />
<c:set var="thickportlets" value='${map["thick"]}' />

<div id="portal-content-layout">
  
  <!--
  <div id="full-top">
    FULL_TOP
  </div>
  -->
  
  <c:if test="${thickportlets != null}">
      <c:choose>
          <c:when test="${fn:length(thickportlets) == 1}">
              <c:forEach items="${thickportlets}" var="portlet">     
                  <c:choose>
                      <c:when test="${portlet.maximized==true}">
                          <div id="layout-2-thick" style="width:100%">
                            <%@include file="portlet.jsp"%>
                          </div>
                      </c:when>
                      <c:otherwise>
                          <div id="layout-2-thick">
                            <%@include file="portlet.jsp"%>
                          </div>  
                      </c:otherwise>
                  </c:choose>
                  
              </c:forEach>
          </c:when>
          <c:otherwise>
            <div id="layout-2-thick">
              <c:forEach items="${thickportlets}" var="portlet">     
                  <%@include file="portlet.jsp"%>
              </c:forEach>
            </div>
          </c:otherwise>
      </c:choose>
      
  </c:if>
  
  <c:if test="${thinportlets != null}">
      <c:choose>
          <c:when test="${fn:length(thinportlets) == 1}">
              <c:forEach items="${thinportlets}" var="portlet">     
                  <c:choose>
                      <c:when test="${portlet.maximized==true}">
                          <div id="layout-2-thin" style="width:100%">    
                                  <%@include file="portlet.jsp"%>
                          </div>
                      </c:when>
                      <c:otherwise>
                          <div id="layout-2-thin">    
                                  <%@include file="portlet.jsp"%>
                          </div>
                      </c:otherwise>
                  </c:choose>
              </c:forEach>
          </c:when>
          <c:otherwise>
              <div id="layout-2-thin">    
                  <c:forEach items="${thinportlets}" var="portlet"> 
                      <%@include file="portlet.jsp"%>
                  </c:forEach>    
              </div>
          </c:otherwise>
      </c:choose>
  </c:if>
  <!--
  <div id="full-bottom">
    FULL_BOTTOM
  </div>
  -->
  
</div>