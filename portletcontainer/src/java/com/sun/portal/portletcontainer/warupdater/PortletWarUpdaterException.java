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


package com.sun.portal.portletcontainer.warupdater;


import java.io.PrintWriter;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


/**
 * A <code>PortletWarUpdaterException</code> is thrown when there are errors related to
 * display profile and server deploy operations.
 */

public class PortletWarUpdaterException
        extends Exception {
    
    public static final String RESOURCE_BASE = "PortletWarUpdater";
    
    static Locale locale =
            Locale.getDefault();
    
    protected Throwable origEx = null;
    protected String key = null;
    protected Object[] tokens = null;
    
    /*
     * CONSTRUCTORS
     */
    
    /**
     * Constructs an instance of the <code>PortletWarUpdaterException</code> class.
     *
     * @param key key string to index into resource bundle to retieve
     *            localized message
     */
    public PortletWarUpdaterException(String key) {
        super(key);
        this.key = key;
    }
    
    /**
     * Constructs an instance of the <code>PortletWarUpdaterException</code> class.
     *
     * @param key key string to index into resource bundle to retieve
     *            localized message
     * @param tokens array of tokens to be used by the exception message
     */
    public PortletWarUpdaterException(String key, Object[] tokens) {
        super(key);
        this.key = key;
        this.tokens = tokens;
    }
    
    /**
     * Constructs an instance of the <code>PortletWarUpdaterException</code> class.
     *
     * @param key key string to index into resource bundle to retieve
     *            localized message
     * @param t Throwable object provided by the object which is throwing
     */
    public PortletWarUpdaterException(String key,
            Throwable t) {
        super(key);
        origEx = t;
        this.key = key;
    }
    
    /**
     * Constructs an instance of the <code>PortletWarUpdaterException</code> class.
     *
     * @param key key string to index into resource bundle to retieve
     *            localized message
     * @param t Throwable object provided by the object which is throwing
     * @param tokens array of tokens to be used by the exception message
     */
    public PortletWarUpdaterException(String key,
            Throwable t,
            Object[] tokens) {
        super(key);
        origEx = t;
        this.key = key;
        this.tokens = tokens;
    }
    
    /**
     * Constructs an instance of the <code>PortletWarUpdaterException</code> class.
     *
     * @param t Throwable object provided by the object which is throwing
     *  the exception
     */
    public PortletWarUpdaterException(Throwable t) {
        super(t);
        this.key = "";
        origEx = t;
    }
    
    public static void setLocale(Locale loc) {
        locale = loc;
    }
    
    public String getMessage() {
        // non-localized resource bundle
        ResourceBundle rb =
                PropertyResourceBundle.getBundle(RESOURCE_BASE,
                Locale.getDefault());
        return getMessageFromRB(rb, key, tokens);
    }
    
    public Throwable getWrapped() {
        return origEx;
    }
    
    public String getWrappedMessage() {
        String msg = null;
        if (origEx != null) {
            msg = origEx.getMessage();
        } else {
            msg = null;
        }
        return msg;
    }
    
    public String getLocalizedMessage() {
        // localized resource bundle
        ResourceBundle rb =
                PropertyResourceBundle.getBundle(RESOURCE_BASE,
                locale);
        String msg = null;
        try {
            msg = getMessageFromRB(rb, key, tokens);
        } catch (MissingResourceException mrex) {
            msg = key;
        }
        return msg;
    }
    
    private String getMessageFromRB(ResourceBundle rb,
            String key,
            Object[] tokens)
            throws MissingResourceException {
        
        String msg = rb.getString(key);
        
        if (tokens != null && tokens.length > 0) {
            java.text.MessageFormat mf = new java.text.MessageFormat("");
            mf.setLocale(rb.getLocale());
            mf.applyPattern(msg);
            return mf.format(tokens);
        } else {
            return msg;
        }
    }
    
    public void printStackTrace() {
        if (origEx != null) {
            origEx.printStackTrace();
        } else {
            super.printStackTrace();
        }
    }
    
    public void printStackTrace(PrintWriter pw) {
        if (origEx != null) {
            origEx.printStackTrace(pw);
        } else {
            super.printStackTrace(pw);
        }
    }
    
    public String getKey(){
        return key;
    }
    
    public Object[] getTokens(){
        return tokens;
    }
}
