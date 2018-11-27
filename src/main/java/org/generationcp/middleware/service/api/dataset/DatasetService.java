package org.generationcp.middleware.service.api.dataset;

import com.google.common.collect.Table;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatasetService {

	long countPhenotypes(final Integer datasetId, final List<Integer> traitIds);

	long countPhenotypesByInstance(Integer datasetId, Integer instanceId);

	void addVariable(Integer datasetId, Integer variableId, VariableType type, String alias);

	void removeVariables(Integer datasetId, List<Integer> variableIds);

	boolean isValidObservationUnit(Integer datasetId, Integer observationUnitId);

	boolean isValidObservation(Integer observationUnitId, Integer observationId);

	ObservationDto addPhenotype(ObservationDto observation);

	ObservationDto updatePhenotype(
		Integer observationUnitId, Integer observationId, Integer categoricalValueId, String value);

	List<MeasurementVariable> getSubObservationSetColumns(Integer subObservationSetId);

	DatasetDTO generateSubObservationDataset(final Integer studyId, final String datasetName, final Integer datasetTypeId,
		final List<Integer> instanceIds, final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits,
		final Integer parentId);

	List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds);

	DatasetDTO getDataset(final Integer studyId, final Integer datasetId);

	List<ObservationUnitRow> getObservationUnitRows(
		int studyId, Integer datasetId, Integer instanceId, Integer pageNumber, Integer pageSize,
		String sortedColumnTermId, String sortOrder);

	int countTotalObservationUnitsForDataset(final int datasetId, final int instanceId);

	Boolean isDatasetNameAvailable(final String name, final String programUUID);

	Integer getNumberOfChildren (final Integer parentId);
	
	void deletePhenotype(final Integer phenotypeId);

	Map<String, ObservationUnitRow> getObservationUnitsAsMap(final int datasetId,
			final List<MeasurementVariable> selectionMethodsAndTraits, final List<String> observationUnitIds);

	void importDataset(final Integer datasetId, final Table<String, String, String> table);

	ObservationUnitImportResult previewImportDataset(final Integer datasetId, final Table<String, String, String> table);

	List<MeasurementVariable> getDatasetMeasurementVariables(Integer datasetId);
}
