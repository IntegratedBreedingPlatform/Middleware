package org.generationcp.middleware.api.ontology;

import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;

import java.util.List;
import java.util.Map;

/**
 * Replaces {@link org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager}
 */
public interface OntologyVariableService {

	Map<Integer, Variable> getVariablesWithFilterById(VariableFilter variableFilter);

	List<Integer> createAnalysisVariables(List<Integer> variableIds, List<String> analysisNames, String variableType);
}
