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
package org.generationcp.middleware.manager.api;


import java.util.Set;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

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
     * Retrieves a the standardVariableId given the property, scale and method Ids
     * 
     * @param propertyId, scaleId, methodId
     * @return
     * @throws MiddlewareQueryException
     */
	public Integer getStandadardVariableIdByPropertyScaleMethod(Integer propertyId,Integer scaleId, Integer methodId) throws MiddlewareQueryException; 

	
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
	Term addMethod(String name, String definition) throws MiddlewareQueryException;
	
	/**
	 * @param id
	 * @return
	 * @throws MiddlewareQueryException
	 */
	Term findMethodById(int id) throws MiddlewareQueryException;
	
	/**
	 * @param name
	 * @return
	 * @throws MiddlewareQueryException
	 */
	Term findMethodByName(String name) throws MiddlewareQueryException;
	
	/**
	 * Retrieves the StandardVariable given the property, scale and method names
	 * 
	 * @param property, scale, method
	 * @return StandardVariable
	 * @throws MiddlewareQueryException
	 * */
	StandardVariable findStandardVariableByTraitScaleMethodNames(String property, String scale, String method) throws MiddlewareQueryException;
}
