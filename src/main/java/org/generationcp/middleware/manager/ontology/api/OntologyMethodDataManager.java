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

import org.generationcp.middleware.domain.ontology.Method;

/**
 * This is the API for retrieving {@link Method}
 *
 */
public interface OntologyMethodDataManager {

	/**
	 * Gets the method with the given id. Return null if obsolete and filterObsolete is true.
	 *
	 * @param id the id to match
	 * @param filterObsolete the flag to determine if obsolete method will be filtered
	 * @return the matching method
	 */
	Method getMethod(int id, boolean filterObsolete);

	/**
	 * Gets the all methods from Central and Local.
	 *
	 * @return All the methods
	 */
	List<Method> getAllMethods();

	/**
	 * Adds a method. If the method is already found in the local database, it simply retrieves the record found.
	 *
	 * @param method to be added
	 */
	void addMethod(Method method);

	/**
	 * Updates the given method. This searches for the id. If it exists, the entry in the database is replaced with the new value.
	 *
	 * @param method The Method to update
	 */
	void updateMethod(Method method);

	/**
	 * Delete method.
	 *
	 * @param id the cv term id
	 */
	void deleteMethod(int id);

}
