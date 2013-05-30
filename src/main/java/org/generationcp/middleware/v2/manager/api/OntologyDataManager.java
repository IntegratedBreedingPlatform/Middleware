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

package org.generationcp.middleware.v2.manager.api;


import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.Term;

/**
 * This is the API for retrieving ontology data from the CHADO schema.
 * 
 * 
 */
public interface OntologyDataManager {

	/**
	 * Retrieves a Term record given its id. This can also be used to retrieve traits, methods and scales. 
	 * 
	 * @param termId
	 * @return
	 * @throws MiddlewareQueryException
	 */
    public Term getTermById(int termId) throws MiddlewareQueryException;
    
    /**
     * Retrieves a StandardVariable given its id
     * 
     * @param stdVariableId
     * @return
     * @throws MiddlewareQueryException
     */
	public StandardVariable getStandardVariable(int stdVariableId) throws MiddlewareQueryException; 
	
	/**
	 * @param nameOrSynonym
	 * @return
	 * @throws MiddlewareQueryException
	 */
	Set<StandardVariable> findStandardVariablesByNameOrSynonym(String nameOrSynonym) throws MiddlewareQueryException;
	
	/**
	 * Adds a StandardVariable to the database.  
	 * Must provide the property, method, scale, dataType, and storedIn info.
	 * Otherwise, it will throw an exception.
	 * 
	 * @param stdVariable
	 * @throws MiddlewareQueryException
	 */
	public void addStandardVariable(StandardVariable stdVariable) throws MiddlewareQueryException;

	
	/**
	 * Adds a new Method to the database. 
	 * Creates a new cvterm entry in the local database. 
	 * Returns a negative id.
	 * 
	 * @param name
	 * @param definition
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public Term addMethod(String name, String definition) throws MiddlewareQueryException;
	


}
