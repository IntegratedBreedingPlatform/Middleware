package org.generationcp.middleware.service.api.dataset;

import java.util.List;

import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.dms.Phenotype;

public interface DatasetService {

	long countPhenotypes(Integer datasetId, List<Integer> traitIds);

	void addVariable(Integer datasetId, Integer variableId, VariableType type, String alias);
	
	void removeVariables(Integer datasetId, List<Integer> variableIds);
	
	boolean isValidObservationUnit(Integer datasetId, Integer observationUnitId);
	
	ObservationDto addPhenotype(ObservationDto observation);

	Phenotype updatePhenotype(
		Integer observationUnitId, Integer observationId, Integer categoricalValueId, String value);

}
