package org.generationcp.middleware.service.api.dataset;

import com.google.common.collect.Table;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatasetService {

	long countPhenotypes(final Integer datasetId, final List<Integer> variableIds);

	long countPhenotypesByInstance(Integer datasetId, Integer instanceId);

	void addVariable(Integer datasetId, Integer variableId, VariableType type, String alias);

	void removeVariables(Integer datasetId, List<Integer> variableIds);

	boolean isValidObservationUnit(Integer datasetId, Integer observationUnitId);

	Phenotype getPhenotype(Integer observationUnitId, Integer observationId);

	ObservationDto addPhenotype(ObservationDto observation);

	ObservationDto updatePhenotype(
		Integer observationUnitId, Integer observationId, Integer categoricalValueId, String value);

	List<MeasurementVariable> getSubObservationSetColumns(Integer subObservationSetId);

	DatasetDTO generateSubObservationDataset(final Integer studyId, final String datasetName, final Integer datasetTypeId,
		final List<Integer> instanceIds, final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits,
		final Integer parentId);

	List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds);

	DatasetDTO getDataset(final Integer datasetId);

	int countTotalObservationUnitsForDataset(final int datasetId, final int instanceId);

	List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final int datasetId, final Integer instanceId, final Integer pageNumber, final Integer pageSize,
		final String sortBy, final String sortOrder);

	Boolean isDatasetNameAvailable(final String name, final String programUUID);

	Integer getNumberOfChildren (final Integer parentId);

	List<StudyInstance> getDatasetInstances(Integer datasetId);

	void deletePhenotype(final Integer phenotypeId);

	Map<String, ObservationUnitRow> getObservationUnitsAsMap(final int datasetId,
			final List<MeasurementVariable> selectionMethodsAndTraits, final List<String> observationUnitIds);

	void importDataset(final Integer datasetId, final Table<String, String, String> table);

	List<MeasurementVariable> getDatasetMeasurementVariables(Integer datasetId);
	
	List<MeasurementVariable> getAllDatasetVariables(Integer studyId, Integer datasetId);

	void deleteDataset(int datasetId);
	
	Map<Integer, List<ObservationUnitRow>> getInstanceIdToObservationUnitRowsMap(
			final int studyId, final int datasetId, final List<Integer> instanceIds);

	List<MeasurementVariable> getMeasurementVariables(final Integer projectId, final List<Integer> variableTypes);

}
