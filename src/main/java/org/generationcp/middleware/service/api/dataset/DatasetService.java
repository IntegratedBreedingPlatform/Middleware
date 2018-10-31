package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.etl.MeasurementVariable;

import java.util.List;

public interface DatasetService {
	
	long countPhenotypes(Integer datasetId, List<Integer> traitIds);

	List<MeasurementVariable> getSubObservationSetColumns(Integer subObservationSetId);

	Integer generateSubObservationDataset(Integer studyId, String datasetName, Integer datasetTypeId, List<Integer> instanceIds,
			Integer observationUnitVariableId, Integer numberOfSubObservationUnits);

}
