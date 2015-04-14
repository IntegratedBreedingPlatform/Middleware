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
     * @throws MiddlewareException*
     */
    List<Term> getAllTraitClass() throws MiddlewareException;

    /**
     * Returns term by id
     */
    Term getTermById(Integer termId) throws MiddlewareException;

    /**
     * Returns term by name and cvId
     */
    Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareException;

    /**
     * Checks if term has referred to other term.
     */
    boolean isTermReferred(int termId) throws MiddlewareException;

    /**
     * This function defines total observations carried from this variable.
     * @param variableId variable id to get observations
     * @return Total observations
     * @throws MiddlewareException
     */
    Integer getVariableObservations(int variableId) throws MiddlewareException;

    /**
     *
     * @param childClassName class name to be added under
     */
    Term addTraitClass(String childClassName, Integer parentClassId) throws MiddlewareException;

    /**
     *
     * @param termId to be deleted
     */
    void removeTraitClass(Integer termId) throws MiddlewareException;
}
