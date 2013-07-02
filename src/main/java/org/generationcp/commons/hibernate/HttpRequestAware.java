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
package org.generationcp.commons.hibernate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An interface for objects that wants to be notified when a request is
 * started/ended.
 * 
 * @author Glenn Marintes
 */
public interface HttpRequestAware {

    /**
     * Implement this method to perform actions needed when a request has
     * started.
     * 
     * @param request
     * @param response
     */
    public void onRequestStarted(HttpServletRequest request, HttpServletResponse response);

    /**
     * Implement this method to perform actions needed when a request has ended.
     * 
     * @param request
     * @param response
     */
    public void onRequestEnded(HttpServletRequest request, HttpServletResponse response);
}
