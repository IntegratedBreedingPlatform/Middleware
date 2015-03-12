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

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import java.util.List;

/**
 * This is the API for retrieving ontology scale data.
 * 
 * 
 */
public interface OntologyBasicDataManager {

    /**
     * Return All Trait Classes
     * * @return List<Term>
     * @throws MiddlewareQueryException*
     */
    List<Term> getAllTraitClass() throws MiddlewareQueryException;

    /**
     * Returns all dataTypes used by Scale
     */
    List<Term> getDataTypes() throws MiddlewareQueryException;


    Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareQueryException;

    boolean isTermReferred(int termId) throws MiddlewareQueryException;
}
