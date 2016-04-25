package org.generationcp.middleware.manager.ontology.api;

import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;

public interface OntologyCommonDAO {

	/**
	 * Get all property ids with filtered by class name and variable type
	 * @param classes
	 * @param variableTypes
	 * @return propertyIds
	 * @throws MiddlewareException
	 */
	List<Integer> getAllPropertyIdsWithClassAndVariableType(final String[] classes, final String[] variableTypes) throws MiddlewareException;

	/**
	 * Get all properties with crop ontology ids and traits
	 * @param fetchAll
	 * @param propertyIds
	 * @param filterObsolete
	 * @return map that have property and property id
	 * @throws MiddlewareException
	 */
	Map<Integer, Property> getPropertiesWithCropOntologyAndTraits(final Boolean fetchAll, List propertyIds, final boolean filterObsolete) throws MiddlewareException;

	/**
	 * Get all scales with data type and their properties
	 * @param termIds
	 * @param scaleMap
	 * @param filterObsolete
	 * @return map that have scale and scale id
	 * @throws MiddlewareException
	 */
	Map<Integer, Scale> getScalesWithDataTypeAndProperties(final List<Integer> termIds, final Map<Integer, Scale> scaleMap, final Boolean filterObsolete) throws MiddlewareException;

	/**
	 * Make Filter Clause String by IsFavourites, Method Ids and PropertyIds
	 * @param variableFilter
	 * @param listParameters
	 * @throws MiddlewareException
	 */
	void makeFilterClauseByIsFavouritesMethodIdsAndPropertyIds(VariableFilter variableFilter, Map<String, List<Integer>> listParameters) throws MiddlewareException;

	/**
	 * Make Filter Clause String by Variable Ids and ExcludedVariableIds
	 * @param variableFilter
	 * @param listParameters
	 * @throws MiddlewareException
	 */
	void makeFilterClauseByVariableIdsAndExcludedVariableIds(VariableFilter variableFilter, Map<String, List<Integer>> listParameters) throws MiddlewareException;

	/**
	 * Make Filter Clause String by Scale Ids
	 * @param variableFilter
	 * @param listParameters
	 * @throws MiddlewareException
	 */
	void makeFilterClauseByScaleIds(VariableFilter variableFilter, Map<String, List<Integer>> listParameters) throws MiddlewareException;

	/**
	 * Get Property Ids and Add it to Filter Clause String
	 * @param variableFilter
	 * @param listParameters
	 * @return
	 * @throws MiddlewareException
	 */
	List<Integer> getPropertyIdsAndAddToFilterClause(VariableFilter variableFilter, Map<String, List<Integer>> listParameters) throws MiddlewareException;

	/**
	 * Get Scale Ids and Add it to Filter Clause String
	 * @param variableFilter
	 * @param dataTypeIds
	 * @param listParameters
	 * @return
	 * @throws MiddlewareException
	 */
	List<Integer> getScaleIdsAndAddToFilterClause(VariableFilter variableFilter,List<Integer> dataTypeIds, Map<String, List<Integer>> listParameters) throws MiddlewareException;

	/**
	 * Get Variable Ids and Add it to Filter Clause String
	 * @param variableFilter
	 * @param variableTypeNames
	 * @param listParameters
	 * @return
	 * @throws MiddlewareException
	 */
	List<Integer> getVariableIdsAndAddToFilterClause(VariableFilter variableFilter,List<String> variableTypeNames, Map<String, List<Integer>> listParameters) throws MiddlewareException;

	/**
	 * Fill Variable Map with Method Map, Property Map and Scale Map
	 * @param variableFilter
	 * @param listParameters
	 * @param map
	 * @param methodMap
	 * @param propertyMap
	 * @param scaleMap
	 * @return
	 * @throws MiddlewareException
	 */
	Map<Integer, Variable> fillVariableMapUsingFilterClause(VariableFilter variableFilter, Map<String, List<Integer>> listParameters,
			Map<Integer, Variable> map, Map<Integer, Method> methodMap, Map<Integer, Property> propertyMap, Map<Integer, Scale> scaleMap) throws MiddlewareException;

	/**
	 * Get Relationships of variable
	 * @param methodMap
	 * @param propertyMap
	 * @param scaleMap
	 * @throws MiddlewareException
	 */
	void getVariableRelationships(Map<Integer, Method> methodMap, Map<Integer, Property> propertyMap, Map<Integer, Scale> scaleMap) throws MiddlewareException;

	/**
	 * Get Properties of variable
	 * @param map
	 * @param methodMap
	 * @param propertyMap
	 * @param scaleMap
	 * @throws MiddlewareException
	 */
	void getVariableProperties(Map<Integer, Variable> map, Map<Integer, Method> methodMap, Map<Integer, Property> propertyMap, Map<Integer, Scale> scaleMap)
			throws MiddlewareException;

    /**
     * Get Observation count
     * @param variableId Variable Id
     */
    Integer getVariableObservations(int variableId);

    /**
     * Get Variable used in studies
     * @param variableId Variable Id
     */
    Integer getVariableStudies(int variableId);
}
