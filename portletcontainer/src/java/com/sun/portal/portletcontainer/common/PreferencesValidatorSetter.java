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

import javax.portlet.PreferencesValidator;

/**
 * <code>PreferencesValidatorSetter</code> is an interface class for
 * setting <code>PreferencesValidator</code>.  Known implementing class is
 * the <code>PortletPreferenceImpl</code>.
 *
 * @see com.sun.portal.portlet.portlet.impl.PortletPreferenceImpl
 **/
public interface PreferencesValidatorSetter {

    /**
     * Sets the <code>PreferencesValidator</code> object.
     *
     * @param pv    the PreferencesValidator object to be set
     **/
    public void setPreferencesValidator( PreferencesValidator pv );

}
