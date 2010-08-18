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

package com.sun.portal.container.service.policy;

/**
 * The DistributionTypeUtil class is a utility class to convert
 * distribution type String into DistributionType object.
 */
public class DistributionTypeUtil {

	/**
	 * Converts distribution type String into DistributionType object.
	 * 
	 * @param type the distribution type value
	 * 
	 * @return DistributionType object that corresponds to the type
	 */
	public static DistributionType getDistributionType(String type) {
		DistributionType distributionType = new DistributionType(type);
		if (DistributionType.ALL_PORTLETS.equals(distributionType)) {
			return DistributionType.ALL_PORTLETS;
		} else if (DistributionType.ALL_PORTLETS_ON_PAGE.equals(
			distributionType)) {
			return DistributionType.ALL_PORTLETS_ON_PAGE;
		} else {
			return DistributionType.ALL_PORTLETS_ON_PAGE;
		}
	}

}