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
 * A <code>GetMarkupResponse</code> encapsulates the render response sent by the
 * aggregation engine to the container.
 **/
public interface GetMarkupResponse extends ContainerResponse {

    /**
     * Returns the markup content of the channel.
     *
     * @return  markup content of the channel
     **/
    public StringBuffer getMarkup();

    /**
     * Sets the markup content of the channel, the markup set must not
     * be <code>/null</code>
     *
     * @param  markup content of the channel
     **/
    public void setMarkup( StringBuffer markup );

    /**
     * Returns the title of the channel as a <code>String</code>.
     *
     * @return  the title 
     **/
    public String getTitle();

    /**
     * Sets the title String of the channel.
     * Setting <code>null</code> means no title available.
     *
     * @param  title    the title of the channel
     **/
    public void setTitle( String title );

    /**
     * Sets the expiration interval (in second) of the content.
     *
     * @param  expiration     the number of second that the content can be cache
     **/
    public void setExpiration( int expiration );

    /**
     * Returns the expiration interval (in second) of the content
     *
     * @return  the expiration interval (in second) of the content
     **/
    public int getExpiration();
    
    /**
     * Returns the cache control object on which various cache settings can be set.
     * valid for the resource returned in this response.
     * 
     * @return  Cache control for the current response.
     */    
    public ChannelCacheControl getChannelCacheControl();  
    
}

