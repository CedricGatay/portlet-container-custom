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

package com.sun.portal.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The <code>ContainerFactory</code> is responsible for returning appropriate Container implementation
 *
 */
public class ContainerFactory {
    
    //
    // static instance holding container instances
    //
    private static Map<ContainerType, Container> containers = 
            new ConcurrentHashMap<ContainerType, Container>();
    

    private ContainerFactory() {
    }

    /**
     * Sets the Container implementation for the Container. If the container is null,
     * removes the container implementation.
     * 
     * @param containerType the type of the Container
     * @param container the Container implementation.
     */
    public static void setContainer(ContainerType containerType, Container container) {
        if(containerType != null) {
            if(container != null) {
                containers.put(containerType, container);
            } else {
                containers.remove(containerType);
            }
        }
    }

    /**
     * Returns the Container implementation that is associated with the type of the Container
     *
     * @param containerType the type of the Container
     *
     * @return the Container implementation that is associated with the type of the Container
     */
    public static Container getContainer(ContainerType containerType) {
        return containers.get(containerType);
    }
}
