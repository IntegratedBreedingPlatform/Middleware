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

import org.generationcp.middleware.domain.oms.OntologyVariable;
import org.generationcp.middleware.domain.oms.OntologyVariableInfo;
import org.generationcp.middleware.domain.oms.OntologyVariableSummary;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import java.util.List;

/**
 * This is the API for retrieving ontology variable data.
 *
 */
public interface OntologyVariableDataManager {

    /**
     * Return All Variables
     * * @return List<OntologyVariableSummary>
     * @throws MiddlewareQueryException*
     */
    List<OntologyVariableSummary> getAllVariables() throws MiddlewareQueryException;

    /**
     * Return variable by Id
     * * @return OntologyVariable
     * @throws MiddlewareQueryException*
     */
    OntologyVariable getVariable(Integer id) throws MiddlewareQueryException, MiddlewareException;

    /**
     * @throws MiddlewareQueryException*
     * * @throws MiddlewareException*
     */
    void addVariable(OntologyVariableInfo variableInfo) throws MiddlewareQueryException, MiddlewareException;

    /**
     * @throws MiddlewareQueryException*
     * * @throws MiddlewareException*
     */
    void updateVariable(OntologyVariableInfo variableInfo) throws MiddlewareQueryException, MiddlewareException;

    /**
     * @throws MiddlewareQueryException*
     * * @throws MiddlewareException*
     */
    void deleteVariable(Integer id) throws MiddlewareQueryException, MiddlewareException;
}
