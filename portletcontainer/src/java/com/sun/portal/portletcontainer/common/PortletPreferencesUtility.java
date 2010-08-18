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

import java.util.List;
import java.util.ArrayList;

public class PortletPreferencesUtility {
    
    private static String ARRAY_DELIMITER = "|";
    private static String SPACE = " ";
    private static char ARRAY_DELIMITER_CHAR = ARRAY_DELIMITER.charAt(0);
    private static String ESCAPED_ARRAY_DELIMITER = ARRAY_DELIMITER+ARRAY_DELIMITER;
    private static int ARRAY_DELIMITER_LENGTH = ARRAY_DELIMITER.length();
    private static int ESCAPED_ARRAY_DELIMITER_LENGTH = ESCAPED_ARRAY_DELIMITER.length();
    public static final String NULL_STRING ="@@$$NULL_STRING$$@@";
    private static final String EMPTY_STRING ="@@$$EMPTY_STRING$$@@";
    
    /**
     * Converts the List of preference values by using | character as the
     * delimiter.
     * @param prefs list of preference values
     * @return String that has preference values delimited by |
     */
    public static String getPreferenceString( List prefs ) {
        if( prefs != null ) {
            StringBuffer sb = new StringBuffer( "" );
            for( int i = 0; i < prefs.size(); i++ ) {
                sb.append( ARRAY_DELIMITER );
                sb.append(getPreferenceString((String)prefs.get(i)));
            }
            return sb.toString();
        } else {
            return null;
        }
    }
    
    /**
     * Converts the List of preference values by using | character as the
     * delimiter.
     * @param prefs array of preference values
     * @return String that has preference values delimited by |
     */
    public static String getPreferenceString( String[] prefs ) {
        if( prefs != null ) {
            StringBuffer sb = new StringBuffer( "" );
            for( int i = 0; i < prefs.length; i++ ) {
                sb.append( ARRAY_DELIMITER );
                sb.append(getPreferenceString(prefs[i]));
            }
            return sb.toString();
        } else {
            return null;
        }
    }
    
    // If the value includes |, then its escaped by adding one more | character.
    // Eg: if pref="v1|v2", then the returned value will be "v1||v2"
    private static String getPreferenceString( String pref ) {
        if( pref == null ) {
            pref = NULL_STRING;
        } else if ( pref.length() == 0 ) {
            pref = EMPTY_STRING;
        }
        
        StringBuffer prefBuffer = new StringBuffer(pref);
        // pref should never start with '|' and never end with '|'. In case
        // such a thing happens, its abnormal, so in order for the
        // parsing algorithm to work properly, if pref starts with '|', insert a space
        // and if pref endswith '|' append a space.
        if(pref.startsWith(ARRAY_DELIMITER))
            prefBuffer.insert(0, SPACE);
        if(pref.endsWith(ARRAY_DELIMITER))
            prefBuffer.insert(prefBuffer.length(), SPACE);
        // If the preference value contains |, then add one more |
        int arrayDelimiterIndex = prefBuffer.indexOf(ARRAY_DELIMITER);
        if(arrayDelimiterIndex != -1)  {
            while(arrayDelimiterIndex != -1){
                prefBuffer.replace(arrayDelimiterIndex,
                        arrayDelimiterIndex+ARRAY_DELIMITER_LENGTH,
                        ESCAPED_ARRAY_DELIMITER);
                arrayDelimiterIndex = prefBuffer.indexOf(ARRAY_DELIMITER,arrayDelimiterIndex+ESCAPED_ARRAY_DELIMITER_LENGTH);
            }
        }
        return prefBuffer.toString();
    }
    
    /**
     * Converts the preference value string into a list. Multiple preference
     * values are delimited by '|'.
     * @param pref multiple preference value delimited by '|'.
     * @return List of preference values.
     */
    public static List getPreferenceValues( String pref ) {
        
        ArrayList prefArray = new ArrayList();
        if( pref == null || pref.length() == 0 ) {
            return prefArray;
        }
        
        StringBuffer valueBuffer = new StringBuffer();
        StringBuffer prefBuffer = new StringBuffer(pref);
        boolean start = false;
        
        // pref from DP should always start with '|' and never end with '|'. In case
        // such a thing happens, its abnormal, however just to be nice and not
        // blow things,
        // (a)if pref does not start with '|', insert '|'
        // (b)if pref starts with '||' , insert '| '
        // (c)if pref ends with '|', insert ' |'.
        
        if(pref.startsWith(ARRAY_DELIMITER)) {
            if(pref.startsWith(ESCAPED_ARRAY_DELIMITER)) {
                prefBuffer.insert(0, SPACE);
                prefBuffer.insert(0, ARRAY_DELIMITER);
            }
        } else {
            prefBuffer.insert(0,ARRAY_DELIMITER);
        }
        if(pref.endsWith(ARRAY_DELIMITER)) {
            prefBuffer.insert(prefBuffer.length(), SPACE);
            prefBuffer.insert(prefBuffer.length(), ARRAY_DELIMITER);
        } else {
            prefBuffer.insert(prefBuffer.length(), ARRAY_DELIMITER);
        }
        
        /*
        In order to convert the preferences value back into array
        check for the array delimiter '|'. If if the value contains '||'
        include it. Before adding into the array, replace '||' by '|'
         */
        String modifiedPref = prefBuffer.toString();
        char[] prefCharArray = modifiedPref.toCharArray();
        int modifiedPrefLength = prefCharArray.length;
        for(int i=0;i<prefCharArray.length; i++) {
            if(!start && (prefCharArray[i] == '|')) {
                start = true;
                valueBuffer = new StringBuffer();
            } else if ( start &&
                    (prefCharArray[i] == ARRAY_DELIMITER_CHAR &&
                    i+1 != modifiedPrefLength && prefCharArray[i+1] == ARRAY_DELIMITER_CHAR) ){
                valueBuffer.append(prefCharArray[i]);
                valueBuffer.append(prefCharArray[i+1]);
                i=i+1;
            } else if ( start &&
                    (prefCharArray[i] == ARRAY_DELIMITER_CHAR &&
                    ( i+1 == modifiedPrefLength || prefCharArray[i+1] != ARRAY_DELIMITER_CHAR)) ){
                if( valueBuffer.equals( NULL_STRING ) ) {
                    valueBuffer = null;
                } else if ( valueBuffer.indexOf(EMPTY_STRING) != -1) {
                    valueBuffer = new StringBuffer("");
                } else {
                    replaceEscapedArrayDelimiter(valueBuffer);
                }
                prefArray.add(valueBuffer.toString());
                valueBuffer = new StringBuffer();
            } else if(start) {
                valueBuffer.append(prefCharArray[i]);
            }
        }
        return prefArray;
    }
    
    private static void replaceEscapedArrayDelimiter(StringBuffer valueBuffer) {
        int index = valueBuffer.indexOf(ESCAPED_ARRAY_DELIMITER);
        while(index != -1){
            valueBuffer.replace(index, index+ESCAPED_ARRAY_DELIMITER_LENGTH, ARRAY_DELIMITER);
            index = valueBuffer.indexOf(ESCAPED_ARRAY_DELIMITER, index+ARRAY_DELIMITER_LENGTH);
        }
    }
}

