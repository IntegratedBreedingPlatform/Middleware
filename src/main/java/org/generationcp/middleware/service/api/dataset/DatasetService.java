package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;

import java.util.List;
import java.util.Set;

public interface DatasetService {

	long countPhenotypes(Integer datasetId, List<Integer> traitIds);

	void addVariable(Integer datasetId, Integer variableId, VariableType type, String alias);

	List<MeasurementVariable> getSubObservationSetColumns(Integer subObservationSetId);

	DatasetDTO generateSubObservationDataset(Integer studyId, final Integer parentId, String datasetName, Integer datasetTypeId, List<Integer> instanceIds,
			Integer observationUnitVariableId, Integer numberOfSubObservationUnits);

	List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds);

	int countTotalObservationUnitsForDataset(final int datasetId, final int instanceId);

	List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final int datasetId, final int instanceId, final int pageNumber, final int pageSize,
		final String sortBy, final String sortOrder);
	
	Boolean isDatasetNameAvailable(final String name, final String programUUID);

	Integer getNumberOfChildren (final Integer parentId);

}
