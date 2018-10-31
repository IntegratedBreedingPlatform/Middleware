package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.dms.DatasetDTO;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.ontology.VariableType;

public interface DatasetService {
	
	long countPhenotypes(Integer datasetId, List<Integer> traitIds);
	
	void addVariable(Integer datasetId, Integer variableId, VariableType type, String alias);

	Integer generateSubObservationDataset(Integer studyId, String datasetName, Integer datasetTypeId, List<Integer> instanceIds,
			Integer observationUnitVariableId, Integer numberOfSubObservationUnits);

	List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds);
	
	Boolean isDatasetNameAvailable(final String name, final String programUUID);

	Integer getNumberOfChildren (final Integer parentId);

}
