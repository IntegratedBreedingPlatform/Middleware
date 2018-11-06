package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.ontology.VariableType;

import java.util.List;

public interface DatasetService {

	long countPhenotypes(Integer datasetId, List<Integer> traitIds);

	long countPhenotypesByInstance(Integer datasetId, Integer instanceId);

	void addVariable(Integer datasetId, Integer variableId, VariableType type, String alias);

	void removeVariables(Integer datasetId, List<Integer> variableIds);

}
