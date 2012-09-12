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
