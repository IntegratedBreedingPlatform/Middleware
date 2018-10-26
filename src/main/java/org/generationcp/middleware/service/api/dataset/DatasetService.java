package org.generationcp.middleware.service.api.dataset;

import java.util.List;

public interface DatasetService {
	
	long countPhenotypes(Integer datasetId, List<Integer> traitIds);

	Integer generateSubObservationDataset(Integer studyId, String datasetName, Integer datasetTypeId, List<Integer> instanceIds,
			Integer observationUnitVariableId, Integer numberOfSubObservationUnits);

}
