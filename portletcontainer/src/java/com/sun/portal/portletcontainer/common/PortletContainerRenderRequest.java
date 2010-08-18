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
 

package com.sun.portal.portletcontainer.common;

import com.sun.portal.container.GetMarkupRequest;

import java.util.Map;

public class PortletContainerRenderRequest extends PortletContainerRequest {

  private GetMarkupRequest request;
  private Map<String, String[]> queryParameterMap;

  public PortletContainerRenderRequest( GetMarkupRequest request ) {
      super( request );
      this.request = request;
  }


  /**
   * Returns a <code>Map</code> of the render parameters.
   *
   * @return  the render parameters map.
   **/
  public Map<String, String[]> getRenderParameters( ) {
	  Map<String, String[]> map = request.getRenderParameters();
	  Map<String, String[]> mergedParameterMap = null;
	  if(this.queryParameterMap != null && !this.queryParameterMap.isEmpty()) {
		  mergedParameterMap = PortletContainerUtil.getMergedParameterMap(
			  this.queryParameterMap, map, true);
	  } else {
		  mergedParameterMap = map;
	  }
      return mergedParameterMap;
  }

  /**
   * Returns a <code>Map</code> of the render parameters.
   *
   * @return  the render parameters map.
   **/
  public void setQueryParameters(Map<String, String[]> queryParameterMap) {
      this.queryParameterMap = queryParameterMap;
  }

  /**
   * Returns whether the current portlet window is the target portlet window of the action
   * in this user request.
   *
   * @return  true if the current portlet window is the target portlet window
   **/
  public boolean getIsTarget() {
      return request.getIsTarget();
  }
  
    /**
     * This method provides access to the ETag which can be accessed by the Portlets.
     *
     * @return The ETag as a <CODE>String</CODE>
     */
    public String getETag() {
      return request.getETag();
    }

}
