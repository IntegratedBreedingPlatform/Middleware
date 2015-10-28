package org.generationcp.middleware.manager.ontology.api;

import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.exceptions.MiddlewareException;

public interface OntologyCommonDAO {

	List<Integer> getAllPropertyIdsWithClassAndVariableType(String[] classes, String[] variableTypes) throws MiddlewareException;

	Map<Integer, Property> getPropertiesWithCropOntologyAndTraits(Boolean fetchAll, List propertyIds, boolean filterObsolete) throws MiddlewareException;
}
