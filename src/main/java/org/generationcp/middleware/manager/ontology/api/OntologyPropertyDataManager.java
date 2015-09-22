/*******************************************************************************
 * 
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager.ontology.api;

import java.util.List;

import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

/**
 * This is the API for retrieving ontology scale data.
 *
 *
 */
public interface OntologyPropertyDataManager {

	/**
	 * Given the termId, retrieve the Property POJO. Return null if obsolete.
	 *
	 * @param id the term id having cvId = Property
	 * @return {@link Property}
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Property getProperty(int id) throws MiddlewareException;

	/**
	 * Given the termId, retrieve the Property POJO. Return null if obsolete and filterObsolete is true.
	 *
	 * @param id the term id having cvId = Property
	 * @param filterObsolete flag to determine if obsolete property will be filtered
	 * @return {@link Property}
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Property getProperty(int id, boolean filterObsolete) throws MiddlewareException;

	/**
	 * Get all properties
	 * 
	 * @throws MiddlewareException the middleware query exception
	 */
	List<Property> getAllProperties() throws MiddlewareException;

	/**
	 * Get all properties by className
	 * 
	 * @return property
	 * @throws MiddlewareException the middleware query exception
	 */
	List<Property> getAllPropertiesWithClass(String className) throws MiddlewareException;

	public List<Property> getAllPropertiesWithClassAndVariableType(String[] classes, String[] variableTypes) throws MiddlewareException;

	/**
	 * Get all properties given list of classnames
	 *
	 * @return property
	 * @throws MiddlewareException the middleware query exception
	 */
	List<Property> getAllPropertiesWithClass(String[] classes) throws MiddlewareException;

	/**
	 * Adds a new property to the database. This is new method which ignores isA flat relationship to define single class per property
	 *
	 * @param property to be added
	 * @throws MiddlewareException the middleware query exception
	 */
	void addProperty(Property property) throws MiddlewareException;

	/**
	 * Updates the given property. This searches for the id. If it exists, the entry in the database is replaced with the new value.
	 * 
	 * @param property The Method to update
	 * @throws MiddlewareException the middleware exception
	 */
	void updateProperty(Property property) throws MiddlewareException;

	/**
	 * Delete method.
	 *
	 * @param propertyId the cv term id
	 * @throws MiddlewareException the middleware query exception
	 */
	void deleteProperty(Integer propertyId) throws MiddlewareException;

}
