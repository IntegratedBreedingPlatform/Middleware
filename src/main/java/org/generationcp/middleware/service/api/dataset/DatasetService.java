package org.generationcp.middleware.service.api.dataset;

/**
 * Created by clarysabel on 10/22/18.
 */
public interface DatasetService {

	Integer generateSubObservationDataset(Integer studyId, String datasetName, Integer datasetTypeId, Integer[] instanceIds,
			Integer observationUnitVariableId, Integer numberOfSubObservationUnits);

}
