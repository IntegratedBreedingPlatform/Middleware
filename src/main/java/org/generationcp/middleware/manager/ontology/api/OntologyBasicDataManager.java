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
import org.generationcp.middleware.exceptions.MiddlewareException;
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
     * Returns term by id
     */
    Term getTermById(Integer termId) throws MiddlewareQueryException;

    /**
     * Returns term by name and cvId
     */
    Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareQueryException;

    /**
     * Checks if term is referred
     */
    boolean isTermReferred(int termId) throws MiddlewareQueryException;

    /**
     *
     * @param childClassName class name to be added under
     */
    Term addTraitClass(String childClassName, Integer parentClassId) throws MiddlewareQueryException, MiddlewareException;

    /**
     *
     * @param termId to be deleted
     */
    void removeTraitClass(Integer termId) throws MiddlewareQueryException, MiddlewareException;
}
