package org.generationcp.middleware.manager.ontology.api;

import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.exceptions.MiddlewareException;

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
}
