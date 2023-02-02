package org.generationcp.middleware.api.ontology;

import com.google.common.collect.Multimap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;

import java.util.List;
import java.util.Map;

/**
 * Replaces {@link org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager}
 */
public interface OntologyVariableService {

	Map<Integer, Variable> getVariablesWithFilterById(VariableFilter variableFilter);

	MultiKeyMap createAnalysisVariables(AnalysisVariablesImportRequest analysisVariablesImportRequest,
		Map<String, String> variableNameToAliasMap);

	Multimap<Integer, VariableType> getVariableTypesOfVariables(List<Integer> variableIds);

	MultiKeyMap getAnalysisMethodsOfTraits(List<Integer> variableIds, List<Integer> methodIds);

	List<Variable> searchVariables(VariableFilter filter);

}
