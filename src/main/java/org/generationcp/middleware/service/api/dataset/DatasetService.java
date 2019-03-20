package org.generationcp.middleware.service.api.dataset;

import com.google.common.collect.Table;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatasetService {

	long countPhenotypes(Integer datasetId, List<Integer> variableIds);

	long countPhenotypesByInstance(Integer datasetId, Integer instanceId);

	void addVariable(Integer datasetId, Integer variableId, VariableType type, String alias);

	void removeVariables(Integer datasetId, List<Integer> variableIds);

	boolean isValidObservationUnit(Integer datasetId, Integer observationUnitId);

	Phenotype getPhenotype(Integer observationUnitId, Integer observationId);

	ObservationDto addPhenotype(ObservationDto observation);

	ObservationDto updatePhenotype(Integer observationId, ObservationDto observationDto);

	List<MeasurementVariable> getSubObservationSetColumns(Integer subObservationSetId, Boolean draftMode);

	List<MeasurementVariable> getSubObservationSetVariables(Integer subObservationSetId);

	DatasetDTO generateSubObservationDataset(
		Integer studyId, String datasetName, Integer datasetTypeId,
		List<Integer> instanceIds, Integer observationUnitVariableId, Integer numberOfSubObservationUnits,
		Integer parentId);

	List<DatasetDTO> getDatasets(Integer studyId, Set<Integer> datasetTypeIds);

	/*
	 * If variable is input variable to formula, update the phenotypes status as "OUT OF SYNC" for given observation unit
	 */
	void updateDependentPhenotypesStatus(Integer variableId, Integer observationUnitId);

	DatasetDTO getDataset(Integer datasetId);

	Integer countAllObservationUnitsForDataset(final Integer datasetId, final Integer instanceId, final Boolean draftMode);

	long countFilteredObservationUnitsForDataset(Integer datasetId, Integer instanceId, final Boolean draftMode,
		ObservationUnitsSearchDTO.Filter filter);

	List<ObservationUnitRow> getObservationUnitRows(int studyId, int datasetId, ObservationUnitsSearchDTO searchDTO);

	List<ObservationUnitRow> getAllObservationUnitRows(int studyId, int datasetId);

	Boolean isDatasetNameAvailable(final String name, final int studyId);

	Integer getNumberOfChildren(Integer parentId);

	List<StudyInstance> getDatasetInstances(Integer datasetId);

	void deletePhenotype(Integer phenotypeId);

	List<MeasurementVariableDto> getVariables(Integer datasetId, VariableType variableType);

	void acceptDraftData(Integer datasetId);

	Map<String, ObservationUnitRow> getObservationUnitsAsMap(
		int datasetId,
		List<MeasurementVariable> selectionMethodsAndTraits, List<String> observationUnitIds);

	void importDataset(Integer datasetId, Table<String, String, String> table, Boolean draftMode);

	List<MeasurementVariable> getDatasetMeasurementVariables(Integer datasetId);

	void deleteDataset(int datasetId);

	Map<Integer, List<ObservationUnitRow>> getInstanceIdToObservationUnitRowsMap(
		int studyId, int datasetId, List<Integer> instanceIds);

	Map<String, Long> countObservationsGroupedByInstance(Integer datasetId);

	List<MeasurementVariable> getMeasurementVariables(Integer projectId, List<Integer> variableTypes);

	void rejectDraftData(Integer datasetId);

	Boolean checkOutOfBoundDraftData(Integer datasetId);

	void setValuesToMissing(Integer datasetId);
}
