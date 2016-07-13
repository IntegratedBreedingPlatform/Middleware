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

/**
 * This is the API for retrieving ontology scale data.
 *
 *
 */
public interface OntologyPropertyDataManager {

	/**
	 * Given the termId, retrieve the Property POJO. Return null if obsolete and filterObsolete is true.
	 *
	 * @param id the term id having cvId = Property
	 * @param filterObsolete flag to determine if obsolete property will be filtered
	 * @return {@link Property}
	 */
	Property getProperty(int id, boolean filterObsolete);

	/**
	 * Get all properties
	 */
	List<Property> getAllProperties();

	List<Property> getAllPropertiesWithClassAndVariableType(String[] classes, String[] variableTypes);

	/**
	 * Adds a new property to the database. This is new method which ignores isA flat relationship to define single class per property
	 *
	 * @param property to be added
	 */
	void addProperty(Property property);

	/**
	 * Updates the given property. This searches for the id. If it exists, the entry in the database is replaced with the new value.
	 *
	 * @param property The Method to update
	 */
	void updateProperty(Property property);

	/**
	 * Delete method.
	 *
	 * @param propertyId the cv term id
	 */
	void deleteProperty(Integer propertyId);

}
