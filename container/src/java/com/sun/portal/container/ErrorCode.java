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
 * [Name of File] [ver.__] [Date]
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */

package com.sun.portal.container;

/**
 * The <code>ErrorCode</code> that can be used by implementations
 * to report back more specific information
 * about the error.
 **/
public class ErrorCode {

    private String errorCodeKey = null;

    public ErrorCode(String errorCodeKey) {
        if (errorCodeKey == null) {
            throw new IllegalArgumentException("Null error code");
        }
        this.errorCodeKey = errorCodeKey;
    }

    @Override
    public String toString() {
        return this.errorCodeKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof ErrorCode)) {
            ErrorCode errorCodeObj = (ErrorCode) obj;
            if (errorCodeObj.errorCodeKey.equals(this.errorCodeKey)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        assert false : "hashCode not designed";
        return 42; // any arbitrary constant will do
    }
}