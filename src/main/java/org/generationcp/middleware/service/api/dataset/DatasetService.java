package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.ontology.VariableType;

import java.util.List;
import java.util.Set;

/**
 * Created by clarysabel on 10/22/18.
 */

public interface DatasetService {

	long countPhenotypes(Integer datasetId, List<Integer> traitIds);
	
	void addVariable(Integer datasetId, Integer variableId, VariableType type, String alias);
	
	Integer generateSubObservationDataset(final Integer studyId, final String datasetName, final Integer datasetTypeId,
		final List<Integer> instanceIds, final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits);

	List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds);

	int countTotalObservationUnitsForDataset(final int datasetId, final int instanceId);

	List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final int datasetId, final int instanceId, final int pageNumber, final int pageSize,
		final String sortBy, final String sortOrder);

}
