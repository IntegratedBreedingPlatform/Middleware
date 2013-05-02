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
import org.generationcp.middleware.v2.domain.CvTerm;

/**
 * This is the API for retrieving ontology data from the CHADO schema.
 * 
 * 
 */
public interface OntologyDataManager {

	/**
	 * Retrieves a CvTerm record given its id. 
	 * 
	 * @param cvTermId
	 * @return
	 * @throws MiddlewareQueryException
	 */
    public CvTerm getCvTermById(int cvTermId) throws MiddlewareQueryException;
    

}
