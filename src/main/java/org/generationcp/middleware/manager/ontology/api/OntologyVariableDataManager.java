/*******************************************************************************
 * 
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager.ontology.api;

import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;

import java.util.List;
import java.util.Set;

/**
 * This is the API for retrieving ontology variable data.
 *
 */
public interface OntologyVariableDataManager {

	/**
	 *
	 * @param variableFilter have filter data that needs to be applied
	 * @return List<Variable>
	 * @throws MiddlewareException
	 */
	List<Variable> getWithFilter(VariableFilter variableFilter) throws MiddlewareException;

	void processTreatmentFactorHasPairValue(List<OntologyVariableSummary> summaryList, List<Integer> hiddenFields) throws MiddlewareException;
	/**
	 * Return variable by Id * @return Variable
	 * 
	 * @throws MiddlewareException*
	 */
	Variable getVariable(String programUuid, Integer id) throws MiddlewareException;

	/**
	 * @throws MiddlewareException*
	 */
	void addVariable(OntologyVariableInfo variableInfo) throws MiddlewareException;

	/**
	 * @throws MiddlewareException*
	 */
	void updateVariable(OntologyVariableInfo variableInfo) throws MiddlewareException;

	/**
	 * @throws MiddlewareException*
	 */
	void deleteVariable(Integer id) throws MiddlewareException;

	/**
	 * This function defines total observations carried from this variable.
	 * 
	 * @param variableId variable id to get observations
	 * @return Total observations
	 * @throws MiddlewareException
	 */
	Integer getVariableObservations(int variableId) throws MiddlewareException;

	/**
	 * This function defines total studies taken from this variable.
	 * 
	 * @param variableId variable id to get observations
	 * @return Total studies
	 * @throws MiddlewareException
	 */
	Integer getVariableStudies(int variableId) throws MiddlewareException;
}
