package org.generationcp.middleware.service.api.dataset;

import java.util.List;

/**
 * Created by clarysabel on 10/22/18.
 */
public interface DatasetService {

	Integer generateSubObservationDataset(Integer studyId, String datasetName, Integer datasetTypeId, Integer[] instanceIds,
			Integer observationUnitVariableId, Integer numberOfSubObservationUnits);

	int countTotalObservationUnitsForDataset(final int datasetId, final int instanceId);

	List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final int datasetId, final int instanceId, final int pageNumber, final int pageSize,
		final String sortBy, final String sortOrder);

}
