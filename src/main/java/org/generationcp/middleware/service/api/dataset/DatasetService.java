package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;

import java.util.List;
import java.util.Set;

public interface DatasetService {

	long countPhenotypes(final Integer datasetId, final List<Integer> traitIds);

	void addVariable(final Integer datasetId, final Integer variableId, final VariableType type, final String alias);

	void removeVariables(Integer datasetId, List<Integer> variableIds);

	List<MeasurementVariable> getSubObservationSetColumns(Integer subObservationSetId);

	DatasetDTO generateSubObservationDataset(final Integer studyId, final String datasetName, final Integer datasetTypeId,
		final List<Integer> instanceIds, final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits,
		final Integer parentId);

	List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds);

	DatasetDTO getDataset(final Integer studyId, final Integer datasetId);

	int countTotalObservationUnitsForDataset(final int datasetId, final int instanceId);

	List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final int datasetId, final int instanceId, final int pageNumber, final int pageSize,
		final String sortBy, final String sortOrder);

	Boolean isDatasetNameAvailable(final String name, final String programUUID);

	Integer getNumberOfChildren (final Integer parentId);


}
