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


import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
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
	 */
	StandardVariable findStandardVariableByTraitScaleMethodNames(String property, String scale, String method) throws MiddlewareQueryException;
	
	/**
	 * Retrieve method given the traitId
	 * 
	 * @param traitId
	 * @return List<Term>
	 * @throws MiddlewareQueryException
	 */
	List<Term> getMethodsForTrait(Integer traitId) throws MiddlewareQueryException;
	
	/**
	 * Retrieve scales given the traitId
	 * 
	 * @param traitId
	 * @return List<Term>
	 * @throws MiddlewareQueryException
	 */
	List<Term> getScalesForTrait(Integer traitId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of Term entries based on the given CvId. The CvId can be CvId.PROPERTIES, CvId.METHODS, CvId.SCALES, CvId.VARIABLES.
	 * 
	 * This can be used to get all scales, all traits, all trait methods, all properties, all methods and all variables.
	 * 
	 * @param cvId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<Term> getAllTermsByCvId(CvId cvId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of Term entries based on the given CvId filtered by start and number of records. The CvId can be CvId.PROPERTIES, CvId.METHODS, CvId.SCALES, CvId.VARIABLES.
	 * 
	 * 
	 * This can be used to get all scales, all traits, all trait methods, all properties, all methods and all variables.
	 * 
	 * @param cvId
	 * @param start
	 * @param numOfRows
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<Term> getAllTermsByCvId(CvId cvId, int start, int numOfRows) throws MiddlewareQueryException;
	
	
	/**
	 * Returns the count of entries based on the given CvId. The CvId can be CvId.PROPERTIES, CvId.METHODS, CvId.SCALES, CvId.VARIABLES.
	 * 
	 * This can be used to count all scales, all traits, all trait methods, all properties, all methods and  all variables.
	 * 
	 * @param cvId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	long countTermsByCvId(CvId cvId) throws MiddlewareQueryException; 
	
	/**
	 * Returns Term based on the given name and cvid.  
	 * 
	 * @param name, cvId
	 * @return Term
	 * @throws MiddlewareQueryException
	 * */
	Term findTermByName(String name, CvId cvId) throws MiddlewareQueryException;
	
	/**
	 * Adds a new Term to the database. 
	 * Creates a new cvterm entry in the local database. 
	 * Returns a negative id.
	 * 
	 * @param name
	 * @param definition
	 * @param cvId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	Term addTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of Term entries based on possible data types
	 * 
	 * @return list of data type Term objects 
	 * @throws MiddlewareQueryException
	 */
	List<Term> getDataTypes() throws MiddlewareQueryException;
	
}
