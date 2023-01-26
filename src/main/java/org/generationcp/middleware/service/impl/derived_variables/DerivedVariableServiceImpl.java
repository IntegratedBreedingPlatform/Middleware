package org.generationcp.middleware.service.impl.derived_variables;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.VariableDatasetsDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.derived_variables.DerivedVariableService;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DerivedVariableServiceImpl implements DerivedVariableService {

	public static final List<Integer> CALCULATED_VARIABLE_VARIABLE_TYPES = Collections.unmodifiableList(
		Arrays.asList(VariableType.TRAIT.getId(), VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENVIRONMENT_CONDITION.getId()));

	@Autowired
	private FormulaService formulaService;

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private UserService userService;

	private DaoFactory daoFactory;

	public DerivedVariableServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DerivedVariableServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Set<FormulaVariable> getMissingFormulaVariablesInStudy(final int studyId, final int datasetId, final int variableId) {

		final Set<Integer> variableIds = this.extractVariableIdsFromDataset(studyId, datasetId);
		final Set<FormulaVariable> missingFormulaVariablesInStudy = new HashSet<>();
		final Set<FormulaVariable> formulaVariablesOfCalculatedVariable =
			this.formulaService.getAllFormulaVariables(Sets.newHashSet(variableId));
		for (final FormulaVariable formulaVariable : formulaVariablesOfCalculatedVariable) {
			if (!variableIds.contains(formulaVariable.getId())) {
				missingFormulaVariablesInStudy.add(formulaVariable);
			}
		}

		return missingFormulaVariablesInStudy;
	}

	@Override
	public Set<FormulaVariable> getFormulaVariablesInStudy(final Integer studyId, final Integer datasetId) {
		// Get variableIds of all traits, environment detail, environment condition in plot dataset and specified datasetId.
		final Set<Integer> variableIds = new HashSet<>();
		final Integer plotDatasetId =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.PLOT_DATA.getId()).get(0).getProjectId();
		final List<MeasurementVariable> measurementVariables =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(Arrays.asList(plotDatasetId, datasetId),
				CALCULATED_VARIABLE_VARIABLE_TYPES);
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			variableIds.add(measurementVariable.getTermId());
		}
		return this.formulaService.getAllFormulaVariables(variableIds);
	}

	@Override
	public int countCalculatedVariablesInDatasets(final Set<Integer> datasetIds) {
		return this.daoFactory.getDmsProjectDAO().countCalculatedVariablesInDatasets(datasetIds);
	}

	@Override
	public void saveCalculatedResult(
		final String value, final Integer categoricalId, final Integer observationUnitId, final Integer observationId,
		final MeasurementVariable measurementVariable) {

		// Update phenotype if it already exists, otherwise, create new phenotype.
		if (observationId != null) {
			this.updatePhenotype(observationId, categoricalId, value);
		} else {
			final ObservationDto observationDto = new ObservationDto();
			observationDto.setVariableId(measurementVariable.getTermId());
			observationDto.setCategoricalValueId(categoricalId);
			observationDto.setObservationUnitId(observationUnitId);
			observationDto.setValue(value);
			this.createPhenotype(observationDto);
		}

	}

	@Override
	public Map<Integer, MeasurementVariable> createVariableIdMeasurementVariableMapInStudy(final int studyId) {
		final Map<Integer, MeasurementVariable> variableIdMeasurementVariableMap = new HashMap<>();

		final List<DatasetDTO> projects = this.daoFactory.getDmsProjectDAO().getDatasets(studyId);
		final List<Integer> datasetIds = new ArrayList<>();
		for (final DatasetDTO datasetDTO : projects) {
			datasetIds.add(datasetDTO.getDatasetId());
		}
		final List<MeasurementVariable> measurementVariables =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetIds,
				CALCULATED_VARIABLE_VARIABLE_TYPES);
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			variableIdMeasurementVariableMap.put(measurementVariable.getTermId(), measurementVariable);
		}
		return variableIdMeasurementVariableMap;
	}

	Set<Integer> extractVariableIdsFromDataset(final Integer studyId, final Integer datasetId) {

		final Set<Integer> variableIds = new HashSet<>();

		boolean isPlotDataset = false;
		final List<DatasetDTO> projects = this.daoFactory.getDmsProjectDAO().getDatasets(studyId);
		final List<Integer> projectIds = new ArrayList<>();
		for (final DatasetDTO datasetDTO : projects) {
			projectIds.add(datasetDTO.getDatasetId());
			if (datasetDTO.getDatasetId().equals(datasetId)
				&& datasetDTO.getDatasetTypeId().intValue() == DatasetTypeEnum.PLOT_DATA.getId()) {
				isPlotDataset = true;
			}
		}

		final List<MeasurementVariable> measurementVariables;
		if (isPlotDataset) {
			// If dataset is plot dataset, we should get all variables (ENVIRONMENT DETAIL, ENVIRONMENT_CONDITION and TRAIT) across all datasets in study.
			measurementVariables =
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(projectIds,
					CALCULATED_VARIABLE_VARIABLE_TYPES);
		} else {
			// Else, we assume that the dataset is sub-observation, in that case, we should only get the TRAIT variables within the sub-ob dataset.
			measurementVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(Collections.singletonList(datasetId),
				Collections.singletonList(VariableType.TRAIT.getId()));
		}

		for (final MeasurementVariable measurementVariable : measurementVariables) {
			variableIds.add(measurementVariable.getTermId());
		}
		return variableIds;

	}

	@Override
	public Map<Integer, Map<String, List<Object>>> getValuesFromObservations(final int studyId, final List<Integer> datasetTypeIds,
		final Map<Integer, Integer> inputVariableDatasetMap) {
		return this.daoFactory.getExperimentDao().getValuesFromObservations(studyId, datasetTypeIds, inputVariableDatasetMap);
	}

	@Override
	public Map<Integer, VariableDatasetsDTO> createVariableDatasetsMap(final Integer studyId, final Integer datasetId,
		final Integer variableId) {

		final Map<Integer, VariableDatasetsDTO> inputVariableDatasetMap = new HashMap<>();
		final Optional<FormulaDto> formulaDtoOptional = this.formulaService.getByTargetId(variableId);
		final List<Integer> variableIds = new ArrayList<>();

		if (formulaDtoOptional.isPresent()) {
			for (final FormulaVariable formulaVariable : formulaDtoOptional.get().getInputs()) {
				variableIds.add(formulaVariable.getId());
			}
		}

		final Integer plotDatasetId =
			this.datasetService.getDatasets(studyId, Sets.newHashSet(DatasetTypeEnum.PLOT_DATA.getId())).get(0).getDatasetId();
		final List<ProjectProperty> projectProperties;

		// if the calculated variable is executed from a plot dataset, the system should be able to read all input variables added in a study
		if (plotDatasetId.equals(datasetId)) {
			projectProperties = this.daoFactory.getProjectPropertyDAO().getByStudyAndStandardVariableIds(studyId, variableIds);
		} else {
			// But if the calculated variable is executed in a sub observation dataset, the system should only read the input variables
			// contained in the same dataset.
			projectProperties = this.daoFactory.getProjectPropertyDAO().getByProjectIdAndVariableIds(datasetId, variableIds);
		}

		for (final ProjectProperty projectProperty : projectProperties) {
			final Integer projectPropertyVariableId = projectProperty.getVariableId();
			final DmsProject project = projectProperty.getProject();

			// Create new variableDatasetsDto instance if it's not yet in the map.
			if (!inputVariableDatasetMap.containsKey(projectPropertyVariableId)) {
				final VariableDatasetsDTO variableDatasetsDTO = new VariableDatasetsDTO();
				variableDatasetsDTO.setVariableName(projectProperty.getAlias());
				inputVariableDatasetMap.put(projectPropertyVariableId, variableDatasetsDTO);
			}
			final List<DatasetReference> datasets = inputVariableDatasetMap.get(projectPropertyVariableId).getDatasets();
			datasets.add(new DatasetReference(project.getProjectId(), project.getName()));
		}

		return inputVariableDatasetMap;
	}

	private void updatePhenotype(final Integer observationId, final Integer categoricalValueId, final String value) {
		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();
		final Phenotype phenotype = phenotypeDao.getById(observationId);
		phenotype.setValue(value);
		phenotype.setcValue(categoricalValueId == null || categoricalValueId == 0 ? null : categoricalValueId);
		phenotype.setChanged(true);
		phenotype.setValueStatus(null);
		phenotypeDao.update(phenotype);
	}

	private void createPhenotype(final ObservationDto observation) {
		final Integer loggedInUser = this.userService.getCurrentlyLoggedInUserId();
		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setCreatedBy(loggedInUser);
		phenotype.setUpdatedBy(loggedInUser);
		phenotype.setcValue(observation.getCategoricalValueId());
		final Integer variableId = observation.getVariableId();
		phenotype.setObservableId(variableId);
		phenotype.setValue(observation.getValue());
		final Integer observationUnitId = observation.getObservationUnitId();
		phenotype.setExperiment(new ExperimentModel(observationUnitId));
		phenotype.setName(String.valueOf(variableId));
		this.daoFactory.getPhenotypeDAO().save(phenotype);
	}

	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public DaoFactory getDaoFactory() {
		return this.daoFactory;
	}
}
