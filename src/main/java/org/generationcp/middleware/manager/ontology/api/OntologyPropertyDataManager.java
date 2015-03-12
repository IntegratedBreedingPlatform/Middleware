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

import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import java.util.List;

/**
 * This is the API for retrieving ontology scale data.
 * 
 * 
 */
public interface OntologyPropertyDataManager {

    /**
     * Given the termId, retrieve the Property POJO.
     *
     * @param id the term id having cvId = Property
     * @return property
     * @throws MiddlewareQueryException the middleware query exception
     */
    Property getProperty(int id) throws MiddlewareQueryException;

    /**
     * Get all properties
     * @return property
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Property> getAllProperties() throws MiddlewareQueryException;

    /**
     * Get all properties by className
     * @return property
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Property> getAllPropertiesWithClass(String className) throws MiddlewareQueryException;

    /**
     * Adds a new property to the database.
     * This is new method which ignores isA flat relationship to define single class per property
     *
     * @param property to be added
     * @throws MiddlewareQueryException the middleware query exception
     */
    void addProperty(Property property) throws MiddlewareQueryException, MiddlewareException;

    /**
     * Adds a new property to the database.
     * This is new method which ignores isA flat relationship to define single class per property
     * @param property to be updated
     * @throws MiddlewareQueryException the middleware query exception
     */
    void updateProperty(Property property) throws MiddlewareQueryException, MiddlewareException;

    /**
     * Adds a new property to the database.
     * This is new method which ignores isA flat relationship to define single class per property
     * @param propertyId of property
     * @throws MiddlewareQueryException the middleware query exception
     */
    void deleteProperty(Integer propertyId) throws MiddlewareQueryException, MiddlewareException;

}
