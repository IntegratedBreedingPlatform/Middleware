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

import java.util.List;

import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;

/**
 * This is the API for retrieving ontology variable data.
 *
 */
public interface OntologyVariableDataManager {

	/**
	 *
	 * @param variableFilter have filter data that needs to be applied
	 * @return List<Variable>
	 */
	List<Variable> getWithFilter(VariableFilter variableFilter);

	/**
	 * Return variable by Id with details based on programUuid. After the first read, the variable is cached in memory. Don't filter
	 * obsolete terms if filterObsolete is false.
	 *
	 * @param programUuid as the unique id of the program
	 * @param id as the variable Id
	 * @param filterObsolete as flag if obsolete terms will be filtered
	 * @param calculateVariableUsage will populate variable usage into the studies and observation fields fields in {@link Variable} object.
	 *        If variable usage is not calcualted the value of the studies and observation field is set to -1 which indicated unknow value.
	 * @return the requested {@link Variable}
	 */
	Variable getVariable(String programUuid, Integer id, boolean filterObsolete, boolean calculateVariableUsage);

	/**
	 * @param OntologyVariableInfo
	 */
	void addVariable(OntologyVariableInfo variableInfo);

	/**
	 *
	 * @param summaryList
	 * @param hiddenFields
	 */
	void processTreatmentFactorHasPairValue(List<Variable> summaryList, List<Integer> hiddenFields);

	/**
	 * @param OntologyVariableInfo
	 */
	void updateVariable(OntologyVariableInfo variableInfo);

	/**
	 * @param id
	 */
	void deleteVariable(Integer id);

	/**
	 * This function defines total observations carried from this variable.
	 *
	 * @param variableId variable id to get observations
	 * @return Total observations
	 */
	Integer getVariableObservations(int variableId);

	/**
	 * This function defines total studies taken from this variable.
	 *
	 * @param variableId variable id to get observations
	 * @return Total studies
	 */
	Integer getVariableStudies(int variableId);
}
