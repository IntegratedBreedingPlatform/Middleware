package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.dms.DatasetDTO;

import java.util.List;
import java.util.Set;

/**
 * Created by clarysabel on 10/22/18.
 */
public interface DatasetService {

	long countPhenotypes(final Integer datasetId, final List<Integer> traitIds);

	Integer generateSubObservationDataset(final Integer studyId, final String datasetName, final Integer datasetTypeId,
		final List<Integer> instanceIds, final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits);

	List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds);

}
