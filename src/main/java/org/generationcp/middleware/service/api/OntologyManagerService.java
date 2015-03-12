/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import java.util.List;

/**
 * 
 * This is the API for Ontology Browser requirements.
 * 
 */
@SuppressWarnings("unused")
public interface OntologyManagerService {

    /*======================= Classes ================================== */
    /**
     * Return All Trait Classes
     * * @return
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException*
     */
    List<Term> getAllTraitClass() throws MiddlewareQueryException;

}
