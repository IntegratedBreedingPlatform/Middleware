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

import com.google.common.base.Optional;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableOverridesDto;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.oms.VariableOverrides;

import java.util.List;

/**
 * This is the API for retrieving ontology variable data.
 * TODO migrate progressively to {@link OntologyVariableService}
 */
public interface OntologyVariableDataManager {

	/**
	 *
	 * @param variableFilter have filter data that needs to be applied
	 * @return List<Variable>
	 */
	@Deprecated
	List<Variable> getWithFilter(VariableFilter variableFilter);

	/**
	 * Return list of variable by Ids with details based on programUuid
	 * @param variableIds Ids to be filtered
	 * @param programUuid as the unique id of the program
	 * @return List<Variable>
	 */
	List<Variable> getVariablesByIds(List<Integer> variableIds, String programUuid);

	/**
	 * Return variable by Id with details based on programUuid. After the first read, the variable is cached in memory. Don't filter
	 * obsolete terms if filterObsolete is false.
	 *
	 * @param programUuid as the unique id of the program
	 * @param id as the variable Id
	 * @param filterObsolete as flag if obsolete terms will be filtered
	 * @return the requested {@link Variable}
	 */
	Variable getVariable(String programUuid, Integer id, boolean filterObsolete);

	/**
	 * @param variableInfo
	 */
	void addVariable(OntologyVariableInfo variableInfo);

	/**
	 *
	 * @param summaryList
	 * @param hiddenFields
	 */
	void processTreatmentFactorHasPairValue(List<Variable> summaryList, List<Integer> hiddenFields);

	/**
	 * @param variableInfo
	 */
	void updateVariable(OntologyVariableInfo variableInfo);

	/**
	 * This function will delete the variable and related data
	 * @param variableId variable Id to be deleted
	 */
	void deleteVariable(Integer variableId);

	/** This function will retrieve categorical name values.
	 *
	 * @param programUuid as the unique id of the program
	 * @param variableId variable id to retrieve value
	 * @param categoricalValueId categorical value id to retrieve categorical value
	 * @param removeBraces
	 * @return categorical value
	 */
	String retrieveVariableCategoricalNameValue(String programUuid, Integer variableId, Integer categoricalValueId, boolean removeBraces);

	/**
	 * This function will give boolean flag if variable is used in any study or not
	 *
	 * @param variableId variableId for which to retrieve usage flag
	 * @return boolean return true if variable is used else false
	 */
  	boolean isVariableUsedInStudy(int variableId);

	boolean areVariablesUsedInStudy(List<Integer> variablesIds);

	boolean areVariablesUsedInAttributes(List<Integer> variablesIds);

	List<VariableOverrides> getVariableOverridesByVariableIds(List<Integer> variableIds);

	List<VariableOverridesDto> getVariableOverridesByAliasAndProgram(String alias, String programUuid);

	List<VariableType> getVariableTypes(Integer variableId);

	Optional<DataType> getDataType(Integer variableId);

	void deleteVariablesFromCache(List<Integer> variablesIds);

	void fillVariableUsage(Variable variable);

	List<Variable> searchAttributeVariables(String query, List<Integer> variableTypeIds, String programUUID);

	boolean hasUsage(int variableId);

	boolean hasVariableAttributeGermplasmDeleted(int variableId);
}
