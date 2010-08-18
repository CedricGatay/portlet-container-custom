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
 

package com.sun.portal.portlet.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;


/**
 * This class is used to declare scripting variables from a JSP tag. 
 * 
 * This class applicable for Portlet v1.0
 */
public class PortletURLTagExtraInfo extends TagExtraInfo {
    
    public VariableInfo[] getVariableInfo(TagData data) {
        
        VariableInfo[] vInfos = null;
        
        String varName = data.getAttributeString("var");
        
        if (varName != null) {
            vInfos = new VariableInfo[1];                
            vInfos[0] = new VariableInfo(varName,"java.lang.String",true, VariableInfo.AT_END);
        }
        return vInfos;
    }
}
