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
	 * Retrieves a Term record given its id. 
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

}
