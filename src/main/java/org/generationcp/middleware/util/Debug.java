/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Debug {

    private final static Logger LOG = LoggerFactory.getLogger(Debug.class);

    public static void println(int indent, String s) {
        for (int i = 0; i < indent; i++) {
            LOG.debug(" ");
        }
        LOG.debug(s);
    }

    public static void print(int indent, String s) {
        for (int i = 0; i < indent; i++) {
            LOG.debug(" ");
        }
        LOG.debug(s);
    }
}
