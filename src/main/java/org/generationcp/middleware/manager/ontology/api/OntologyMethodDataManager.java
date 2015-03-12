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
package org.generationcp.middleware.manager.ontology.api;

import org.generationcp.middleware.domain.oms.Method;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import java.util.List;

/**
 * This is the API for retrieving ontology scale data.
 * 
 * 
 */
public interface OntologyMethodDataManager {

    /**
     * Gets the method with the given id.
     *
     * @param id the id to match
     * @return the matching method
     * @throws MiddlewareQueryException the middleware query exception
     */
    Method getMethod(int id) throws MiddlewareQueryException;


    /**
     * Gets the all methods from Central and Local.
     *
     * @return All the methods
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getAllMethods() throws MiddlewareQueryException;

    /**
     * Adds a method. If the method is already found in the local database, it simply retrieves the record found.
     * @param method to be added
     * @throws MiddlewareQueryException the middleware query exception
     */
    void addMethod(Method method) throws MiddlewareQueryException;

    /**
     * Updates the given method.
     * This searches for the id. If it exists, the entry in the database is replaced with the new value.
     * @param method The Method to update
     * @throws MiddlewareQueryException the middleware query exception
     * @throws MiddlewareException the middleware exception
     */
    void updateMethod(Method method) throws MiddlewareQueryException, MiddlewareException;

    /**
     * Delete method.
     *
     * @param id the cv term id
     * @throws MiddlewareQueryException the middleware query exception
     */
    void deleteMethod(int id) throws MiddlewareQueryException;

}
