package org.generationcp.middleware.service.impl.dataset;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.impl.study.MeasurementVariableServiceImpl;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.service.impl.study.StudyServiceImpl;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by clarysabel on 10/22/18.
 */
@Transactional
public class DatasetServiceImpl implements DatasetService {
	public static final String DATE_FORMAT = "YYYYMMDD HH:MM:SS";

	protected static final List<Integer> SUBOBS_COLUMNS_VARIABLE_TYPES = Lists.newArrayList( //
		VariableType.GERMPLASM_DESCRIPTOR.getId(), //
		VariableType.TRAIT.getId(), //
		VariableType.SELECTION_METHOD.getId(), //
		VariableType.OBSERVATION_UNIT.getId());

	protected static final List<Integer> ENVIRONMENT_VARIABLE_TYPES = Lists.newArrayList( //
			VariableType.ENVIRONMENT_DETAIL.getId(),
			VariableType.STUDY_CONDITION.getId());

	protected static final List<Integer> PLOT_COLUMNS_VARIABLE_TYPES = Lists.newArrayList( //
		VariableType.GERMPLASM_DESCRIPTOR.getId(), //
		VariableType.EXPERIMENTAL_DESIGN.getId(), //
		VariableType.TREATMENT_FACTOR.getId(), //
		VariableType.OBSERVATION_UNIT.getId());

	protected static final List<Integer> DATASET_VARIABLE_TYPES = Lists.newArrayList( //
		VariableType.OBSERVATION_UNIT.getId(), //
		VariableType.TRAIT.getId(), //
		VariableType.SELECTION_METHOD.getId());

	protected static final List<Integer> MEASUREMENT_VARIABLE_TYPES = Lists.newArrayList( //
			VariableType.TRAIT.getId(), //
			VariableType.SELECTION_METHOD.getId());

	protected static final List<Integer> STANDARD_ENVIRONMENT_FACTORS = Lists.newArrayList( //
			TermId.LOCATION_ID.getId(), TermId.EXPERIMENT_DESIGN_FACTOR.getId());

	private DaoFactory daoFactory;

	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private MeasurementVariableService measurementVariableService;

	@Autowired
	ResourceBundleMessageSource messageSource;

	@Autowired
	private StudyService studyService;

	public DatasetServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.ontologyVariableDataManager = new OntologyVariableDataManagerImpl(sessionProvider);
		this.measurementVariableService = new MeasurementVariableServiceImpl(sessionProvider.getSession());
		this.studyService = new StudyServiceImpl(sessionProvider);
	}

	@Override
	public long countPhenotypes(final Integer datasetId, final List<Integer> variableIds) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDataset(datasetId, variableIds);
	}

	@Override
	public long countPhenotypesByInstance(final Integer datasetId, final Integer instanceId) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDatasetAndInstance(datasetId, instanceId);
	}

	@SuppressWarnings("Duplicates")
	@Override
	public List<MeasurementVariable> getSubObservationSetColumns(final Integer subObservationSetId, final Boolean draftMode) {
		// TODO get plot dataset even if subobs is not a direct descendant (ie. sub-sub-obs)
		final DmsProject plotDataset = this.daoFactory.getProjectRelationshipDao()
			.getObjectBySubjectIdAndTypeId(subObservationSetId, TermId.BELONGS_TO_STUDY.getId());

		final List<MeasurementVariable> plotDataSetColumns =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(plotDataset.getProjectId(), PLOT_COLUMNS_VARIABLE_TYPES);
		List<MeasurementVariable> subObservationSetColumns =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(subObservationSetId, SUBOBS_COLUMNS_VARIABLE_TYPES);

		// TODO get immediate parent columns
		// (ie. Plot subdivided into plant and then into fruits, then immediate parent column would be PLANT_NO)

		// Filter columns with draft data
		if (Boolean.TRUE.equals(draftMode)) {
			final Set<Integer> pendingVariableIds = this.daoFactory.getPhenotypeDAO().getPendingVariableIds(subObservationSetId);
			subObservationSetColumns =
				Lists.newArrayList(Iterables.filter(subObservationSetColumns, new com.google.common.base.Predicate<MeasurementVariable>() {

					@Override
					public boolean apply(@Nullable final MeasurementVariable input) {
						return pendingVariableIds.contains(input.getTermId())
							|| VariableType.OBSERVATION_UNIT.equals(input.getVariableType());
					}
				}));
		}

		// Virtual columns
		if (this.daoFactory.getSampleDao().countByDatasetId(subObservationSetId) > 0) {
			final MeasurementVariable sampleColumn = new MeasurementVariable();
			sampleColumn.setName(TermId.SAMPLES.name());
			sampleColumn.setAlias(TermId.SAMPLES.name());
			sampleColumn.setTermId(TermId.SAMPLES.getId());
			sampleColumn.setFactor(true);
			plotDataSetColumns.add(sampleColumn);
		}

		// Other edge cases
		// Let's consider it as a special case instead of getting ENVIRONMENT_DETAILS for plot dataset
		final MeasurementVariable trialInstanceCol = new MeasurementVariable();
		trialInstanceCol.setName(ColumnLabels.TRIAL_INSTANCE.getName());
		trialInstanceCol.setAlias(ColumnLabels.TRIAL_INSTANCE.getName());
		trialInstanceCol.setTermId(ColumnLabels.TRIAL_INSTANCE.getTermId().getId());
		trialInstanceCol.setFactor(true);
		plotDataSetColumns.add(0, trialInstanceCol);

		plotDataSetColumns.addAll(subObservationSetColumns);

		return plotDataSetColumns;
	}

	@SuppressWarnings("Duplicates")
	@Override
	public List<MeasurementVariable> getSubObservationSetVariables(final Integer subObservationSetId) {
		// TODO get plot dataset even if subobs is not a direct descendant (ie. sub-sub-obs)
		final DmsProject plotDataset = this.daoFactory.getProjectRelationshipDao()
			.getObjectBySubjectIdAndTypeId(subObservationSetId, TermId.BELONGS_TO_STUDY.getId());

		final List<MeasurementVariable> plotDataSetColumns =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(plotDataset.getProjectId(), PLOT_COLUMNS_VARIABLE_TYPES);
		final List<MeasurementVariable> subObservationSetColumns =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(subObservationSetId, SUBOBS_COLUMNS_VARIABLE_TYPES);

		// TODO get immediate parent columns
		// (ie. Plot subdivided into plant and then into fruits, then immediate parent column would be PLANT_NO)

		plotDataSetColumns.addAll(subObservationSetColumns);

		return plotDataSetColumns;
	}

	@Override
	public DatasetDTO generateSubObservationDataset(final Integer studyId, final String datasetName, final Integer datasetTypeId,
		final List<Integer> instanceIds,
		final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits, final Integer parentId) {

		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);

		final List<DmsProject> plotDatasets = this.daoFactory.getDmsProjectDAO()
			.getDataSetsByStudyAndProjectProperty(studyId, TermId.DATASET_TYPE.getId(), String.valueOf(DataSetType.PLOT_DATA.getId()));

		if (plotDatasets == null || plotDatasets.isEmpty()) {
			throw new MiddlewareException("Study does not have a plot dataset associated to it");
		}

		final DmsProject plotDataset = plotDatasets.get(0);
		final DmsProject parentDataset = this.daoFactory.getDmsProjectDAO().getById(parentId);

		final DmsProject subObservationDataset = new DmsProject();

		final List<ProjectProperty> projectProperties =
			this.buildDefaultDatasetProperties(study, subObservationDataset, datasetName, datasetTypeId);
		final Variable observationUnitVariable = this.ontologyVariableDataManager.getVariable(study.getProgramUUID(), observationUnitVariableId, false);

		projectProperties.add(this.buildDatasetProperty(subObservationDataset, VariableType.OBSERVATION_UNIT.getId(), observationUnitVariableId, null, null, 4, observationUnitVariable));

		subObservationDataset.setName(datasetName);
		subObservationDataset.setDescription(datasetName);
		subObservationDataset.setProgramUUID(study.getProgramUUID());
		subObservationDataset.setDeleted(false);
		subObservationDataset.setLocked(false);
		subObservationDataset.setProperties(projectProperties);
		subObservationDataset.setRelatedTos(this.buildProjectRelationships(parentDataset, subObservationDataset));

		final DmsProject dataset = this.daoFactory.getDmsProjectDAO().save(subObservationDataset);

		// iterate and create new sub-observation units
		final List<ExperimentModel> plotObservationUnits =
			this.daoFactory.getExperimentDao().getObservationUnits(plotDataset.getProjectId(), instanceIds);
		for (final ExperimentModel plotObservationUnit : plotObservationUnits) {
			for (int i = 1; i <= numberOfSubObservationUnits; i++) {
				final ExperimentModel experimentModel = new ExperimentModel(plotObservationUnit.getGeoLocation(),
						plotObservationUnit.getTypeId(), subObservationDataset, plotObservationUnit.getStock(), plotObservationUnit, i);
				this.daoFactory.getExperimentDao().save(experimentModel);
			}
		}
		return this.getDataset(dataset.getProjectId());
	}

	@Override
	public Boolean isDatasetNameAvailable(final String name, final String projectUUID) {
		final Integer dmsProjectId = this.daoFactory.getDmsProjectDAO().getProjectIdByNameAndProgramUUID(name, projectUUID);
		return (dmsProjectId == null);
	}

	@Override
	public Integer getNumberOfChildren(final Integer parentId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetsByParent(parentId).size();
	}

	@Override
	public List<StudyInstance> getDatasetInstances(final Integer datasetId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetInstances(datasetId);
	}

	private List<ProjectProperty> buildDefaultDatasetProperties(final DmsProject study, final DmsProject dmsProject,
		final String datasetName, final Integer datasetTypeId) {
		final List<ProjectProperty> projectProperties = new ArrayList<>();

		final ProjectProperty datasetProperty =
				this.buildDatasetProperty(dmsProject,
						VariableType.STUDY_DETAIL.getId(), TermId.DATASET_NAME.getId(),
						datasetName, null, 1,
						this.ontologyVariableDataManager.getVariable(study.getProgramUUID(), TermId.DATASET_NAME.getId(), false));
		final ProjectProperty datasetTitleProperty =
				this.buildDatasetProperty(dmsProject,
						VariableType.STUDY_DETAIL.getId(), TermId.DATASET_TITLE.getId(),
						null, null, 2,
						this.ontologyVariableDataManager.getVariable(study.getProgramUUID(), TermId.DATASET_TITLE.getId(), false));
		final ProjectProperty datasetTypeProperty =
				this.buildDatasetProperty(dmsProject,
						VariableType.STUDY_DETAIL.getId(), TermId.DATASET_TYPE.getId(),
						String.valueOf(datasetTypeId), null, 3,
						this.ontologyVariableDataManager.getVariable(study.getProgramUUID(), TermId.DATASET_TYPE.getId(), false));
		projectProperties.add(datasetProperty);
		projectProperties.add(datasetTitleProperty);
		projectProperties.add(datasetTypeProperty);
		return projectProperties;
	}

	private ProjectProperty buildDatasetProperty(final DmsProject dmsProject, final Integer typeId,
		final Integer variableId, final String value, final String alias, final Integer rank, final Variable variable) {
		if (!variable.getVariableTypes().contains(VariableType.getById(typeId))) {
			throw new MiddlewareException("Specified type does not match with the list of types associated to the variable");
		}
		return new ProjectProperty(dmsProject, typeId, value, rank, variableId, (alias == null) ? variable.getName() : alias);
	}

	private List<ProjectRelationship> buildProjectRelationships(final DmsProject parentDataset, final DmsProject childDataset)
		throws MiddlewareQueryException {
		final ProjectRelationship relationship = new ProjectRelationship();
		relationship.setSubjectProject(childDataset);
		relationship.setObjectProject(parentDataset);
		relationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());

		final List<ProjectRelationship> relationships = new ArrayList<>();
		relationships.add(relationship);

		return relationships;
	}

	@Override
	public List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds) {
		final List<DatasetDTO> datasetDTOList = new ArrayList<>();
		this.filterDatasets(datasetDTOList, studyId, datasetTypeIds);
		return datasetDTOList;
	}

	private void filterDatasets(final List<DatasetDTO> filtered, final Integer parentId, final Set<Integer> datasetTypeIds) {
		final List<DatasetDTO> datasetDTOs = this.daoFactory.getDmsProjectDAO().getDatasets(parentId);

		for (final DatasetDTO datasetDTO : datasetDTOs) {
			if (datasetTypeIds.isEmpty() || datasetTypeIds.contains(datasetDTO.getDatasetTypeId())) {
				datasetDTO.setHasPendingData(this.daoFactory.getPhenotypeDAO().countPendingDataOfDataset(datasetDTO.getDatasetId()) > 0);
				filtered.add(datasetDTO);
			}
			this.filterDatasets(filtered, datasetDTO.getDatasetId(), datasetTypeIds);
		}
	}

	@Override
	public void addVariable(final Integer datasetId, final Integer variableId, final VariableType type, final String alias) {
		final ProjectPropertyDao projectPropertyDAO = this.daoFactory.getProjectPropertyDAO();
		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setAlias(alias);
		projectProperty.setTypeId(type.getId());
		projectProperty.setVariableId(variableId);
		final DmsProject dataset = new DmsProject();
		dataset.setProjectId(datasetId);
		projectProperty.setProject(dataset);
		projectProperty.setRank(projectPropertyDAO.getNextRank(datasetId));
		projectPropertyDAO.save(projectProperty);
	}

	@Override
	public void removeVariables(final Integer datasetId, final List<Integer> variableIds) {
		this.daoFactory.getProjectPropertyDAO().deleteProjectVariables(datasetId, variableIds);
		this.daoFactory.getPhenotypeDAO().deletePhenotypesByProjectIdAndVariableIds(datasetId, variableIds);
	}

	@Override
	public boolean isValidObservationUnit(final Integer datasetId, final Integer observationUnitId) {
		return this.daoFactory.getExperimentDao().isValidExperiment(datasetId, observationUnitId);
	}

	@Override
	public Phenotype getPhenotype(final Integer observationUnitId, final Integer observationId) {
		return this.daoFactory.getPhenotypeDAO().getPhenotype(observationUnitId, observationId);
	}

	@Override
	public ObservationDto addPhenotype(final ObservationDto observation) {
		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(observation.getCategoricalValueId());
		final Integer variableId = observation.getVariableId();
		phenotype.setObservableId(variableId);
		phenotype.setValue(observation.getValue());
		final Integer observationUnitId = observation.getObservationUnitId();
		phenotype.setExperiment(new ExperimentModel(observationUnitId));
		phenotype.setName(String.valueOf(variableId));

		final Integer observableId = phenotype.getObservableId();
		this.resolveObservationStatus(variableId, phenotype);

		final Phenotype savedRecord = this.daoFactory.getPhenotypeDAO().save(phenotype);

		// Also update the status of phenotypes of the same observation unit for variables using it as input variable
		this.updateDependentPhenotypesStatus(observableId, observationUnitId);

		observation.setObservationId(savedRecord.getPhenotypeId());
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		observation.setCreatedDate(dateFormat.format(savedRecord.getCreatedDate()));
		observation.setUpdatedDate(dateFormat.format(savedRecord.getUpdatedDate()));
		observation.setStatus(savedRecord.getValueStatus() != null ? savedRecord.getValueStatus().getName() : null);

		return observation;
	}

	@Override
	public ObservationDto updatePhenotype(
		final Integer observationId, final ObservationDto observationDto) {
		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();

		final Phenotype phenotype = phenotypeDao.getById(observationId);
		phenotype.setValue(observationDto.getValue());
		phenotype.setcValue(Integer.valueOf(0).equals(observationDto.getCategoricalValueId()) ?
			null : observationDto.getCategoricalValueId());
		phenotype.setDraftValue(observationDto.getDraftValue());
		phenotype.setDraftCValueId(Integer.valueOf(0).equals(observationDto.getDraftCategoricalValueId()) ?
			null : observationDto.getDraftCategoricalValueId());
		final Integer observableId = phenotype.getObservableId();
		this.resolveObservationStatus(observableId, phenotype);

		phenotypeDao.update(phenotype);

		// Also update the status of phenotypes of the same observation unit for variables using it as input variable
		this.updateDependentPhenotypesStatus(observableId, observationDto.getObservationUnitId());

		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		observationDto.setObservationId(phenotype.getPhenotypeId());
		observationDto.setUpdatedDate(dateFormat.format(phenotype.getUpdatedDate()));
		observationDto.setCreatedDate(dateFormat.format(phenotype.getCreatedDate()));
		observationDto.setStatus(phenotype.getValueStatus() != null ? phenotype.getValueStatus().getName() : null);
		observationDto.setVariableId(phenotype.getObservableId());

		return observationDto;

	}

	void resolveObservationStatus(final Integer variableId, final Phenotype phenotype) {

		final boolean isDerivedTrait = this.isDerivedTrait(variableId);

		if (isDerivedTrait) {
			phenotype.setValueStatus(Phenotype.ValueStatus.MANUALLY_EDITED);
		}
	}

	private boolean isDerivedTrait(final Integer variableId) {
		final FormulaDAO formulaDAO = this.daoFactory.getFormulaDAO();
		final Formula formula = formulaDAO.getByTargetVariableId(variableId);

		return formula != null;
	}

	/*
	 * If variable is input variable to formula, update the phenotypes status as "OUT OF SYNC" for given observation unit
	 */
	@Override
	public void updateDependentPhenotypesStatus(final Integer variableId, final Integer observationUnitId) {
		final List<Formula> formulaList = this.daoFactory.getFormulaDAO().getByInputId(variableId);
		if (!formulaList.isEmpty()) {
			final List<Integer> targetVariableIds = Lists.transform(formulaList, new Function<Formula, Integer>() {

				@Override
				public Integer apply(final Formula formula) {
					return formula.getTargetCVTerm().getCvTermId();
				}
			});
			this.daoFactory.getPhenotypeDAO().updateOutOfSyncPhenotypes(observationUnitId, targetVariableIds);
		}

	}

	@Override
	public DatasetDTO getDataset(final Integer datasetId) {
		final DatasetDTO datasetDTO = this.daoFactory.getDmsProjectDAO().getDataset(datasetId);
		if (datasetDTO != null) {
			datasetDTO.setInstances(this.daoFactory.getDmsProjectDAO().getDatasetInstances(datasetId));
			datasetDTO.setVariables(
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, DatasetServiceImpl.DATASET_VARIABLE_TYPES));
			datasetDTO.setHasPendingData(this.daoFactory.getPhenotypeDAO().countPendingDataOfDataset(datasetId) > 0);
		}

		return datasetDTO;
	}


	protected void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final int datasetId, final ObservationUnitsSearchDTO searchDTO) {

		if (searchDTO.getSortedRequest() != null && searchDTO.getSortedRequest().getSortBy() != null) {
			searchDTO.getSortedRequest()
				.setSortBy(this.ontologyDataManager.getTermById(Integer.valueOf(searchDTO.getSortedRequest().getSortBy())).getName());
		}

		searchDTO.setDatasetId(datasetId);
        searchDTO.setGenericGermplasmDescriptors(this.findGenericGermplasmDescriptors(studyId));
		searchDTO.setAdditionalDesignFactors(this.findAdditionalDesignFactors(studyId));

		final List<MeasurementVariableDto> selectionMethodsAndTraits = this.measurementVariableService.getVariablesForDataset(datasetId,
			VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());
		searchDTO.setSelectionMethodsAndTraits(selectionMethodsAndTraits);

		return this.daoFactory.getExperimentDao().getObservationUnitTable(searchDTO);
	}

	@Override
	public List<ObservationUnitRow> getAllObservationUnitRows(final int studyId, final int datasetId) {

		final List<String> designFactors = this.findAdditionalDesignFactors(studyId);
		final List<String> germplasmDescriptors = this.findGenericGermplasmDescriptors(studyId);

		final DmsProject environmentDataset =
			this.daoFactory.getDmsProjectDAO().getDataSetsByStudyAndProjectProperty(studyId, TermId.DATASET_TYPE.getId(),
				String.valueOf(DataSetType.SUMMARY_DATA.getId())).get(0);
		final List<MeasurementVariable> studyVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(
			studyId,
			Lists.newArrayList(VariableType.STUDY_DETAIL.getId()));

		final ObservationUnitsSearchDTO searchDTO =
			new ObservationUnitsSearchDTO(datasetId, null, germplasmDescriptors, designFactors, new ArrayList<MeasurementVariableDto>());
		searchDTO.setEnvironmentDetails(this.findAdditionalEnvironmentFactors(environmentDataset.getProjectId()));
		searchDTO.setEnvironmentConditions(this.getEnvironmentConditionVariableNames(environmentDataset.getProjectId()));
		searchDTO.setEnvironmentDatasetId(environmentDataset.getProjectId());

		final List<ObservationUnitRow> observationUnits = this.daoFactory.getExperimentDao().getObservationUnitTable(searchDTO);
		this.addStudyVariablesToUnitRows(observationUnits, studyVariables);

		return observationUnits;
	}

	private List<String> findGenericGermplasmDescriptors(final int studyId) {

		return this.studyService.getGenericGermplasmDescriptors(studyId);
	}

	private List<String> findAdditionalDesignFactors(final int studyId) {
		return this.studyService.getAdditionalDesignFactors(studyId);
	}

	List<String> getEnvironmentConditionVariableNames(final Integer trialDatasetId) {
		final List<MeasurementVariable> environmentConditions = this.daoFactory.getDmsProjectDAO()
				.getObservationSetVariables(trialDatasetId, Lists.<Integer>newArrayList(VariableType.TRAIT.getId()));
		final List<String> factors = new ArrayList<>();
		for (final MeasurementVariable variable : environmentConditions) {
			factors.add(variable.getName());
		}
		return factors;
	}

	List<String> findAdditionalEnvironmentFactors(final Integer trialDatasetId) {
		final List<MeasurementVariable> environmentDetailsVariables =
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(trialDatasetId, ENVIRONMENT_VARIABLE_TYPES);
		final List<String> factors = new ArrayList<>();
		for (final MeasurementVariable variable : environmentDetailsVariables) {
			if (!STANDARD_ENVIRONMENT_FACTORS.contains(variable.getTermId())) {
				factors.add(variable.getName());
			}
		}
		return factors;
	}

	@Override
	public Integer countTotalObservationUnitsForDataset(
		final Integer datasetId, final Integer instanceId, final Boolean draftMode) {
		return this.daoFactory.getExperimentDao().countObservationUnitsForDataset(datasetId, instanceId, draftMode, null);
	}

	@Override
	public long countFilteredObservationUnitsForDataset(
		final Integer datasetId, final Integer instanceId, final Boolean draftMode,
		final ObservationUnitsSearchDTO.Filter filter) {
		return this.daoFactory.getExperimentDao().countObservationUnitsForDataset(datasetId, instanceId, draftMode, filter);
	}

	@Override
	public void deletePhenotype(final Integer phenotypeId) {
		final Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getById(phenotypeId);
		final Integer observableId = phenotype.getObservableId();
		final ExperimentModel experiment = phenotype.getExperiment();
		final Integer observationUnitId = experiment.getNdExperimentId();

		final List<Phenotype> experimentPhenotypes = experiment.getPhenotypes();
		experimentPhenotypes.remove(phenotype);
		experiment.setPhenotypes(experimentPhenotypes);
		this.daoFactory.getExperimentDao().merge(experiment);

		this.daoFactory.getPhenotypeDAO().makeTransient(phenotype);

		// Also update the status of phenotypes of the same observation unit for variables using the trait as input variable
		this.updateDependentPhenotypesStatus(observableId, observationUnitId);
	}

	@Override
	public void deleteDataset(final int datasetId) {

		try {

			this.daoFactory.getDmsProjectDAO().deleteDataset(datasetId);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("error in deleteDataSet " + e.getMessage(), e);
		}
	}

	@Override
	public List<MeasurementVariableDto> getVariables(final Integer datasetId, final VariableType variableType) {
		return this.measurementVariableService.getVariablesForDataset(datasetId, variableType.getId());
	}

	@Override
	public List<MeasurementVariable> getMeasurementVariables(final Integer projectId, final List<Integer> variableTypes) {
		return this.daoFactory.getDmsProjectDAO().getObservationSetVariables(projectId, variableTypes);
	}

	@Override
	public void rejectDraftData(final Integer datasetId) {
		final List<Phenotype> phenotypes = this.daoFactory.getPhenotypeDAO().getDraftDataOfDataset(datasetId);
		for (final Phenotype phenotype : phenotypes) {
			if (StringUtils.isEmpty(phenotype.getValue())) {
				this.deletePhenotype(phenotype.getPhenotypeId());
			} else {
				this.updatePhenotype(phenotype.getPhenotypeId(), null, null, true);
			}
		}
	}

	@Override
	public void acceptDraftData(final Integer datasetId) {

		final List<Phenotype> phenotypes = this.daoFactory.getPhenotypeDAO().getDraftDataOfDataset(datasetId);
		final List<Phenotype> allPhenotypes = this.daoFactory.getPhenotypeDAO().getPhenotypes(datasetId);

		if (phenotypes.size() > 0) {

			for (final Phenotype phenotype : phenotypes) {
				if (StringUtils.isEmpty(phenotype.getDraftValue())) {
					this.deletePhenotype(phenotype.getPhenotypeId());
				} else {
					this.updatePhenotype(phenotype, phenotype.getDraftCValueId(), phenotype.getDraftValue(), false);
				}
			}

			final List<MeasurementVariable> measurementVariableList =
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, DatasetServiceImpl.MEASUREMENT_VARIABLE_TYPES);

			if (measurementVariableList.size() > 0) {
				final Map<Integer, List<Integer>> formulasMap = this.getTargetsByInput(measurementVariableList);
				this.setMeasurementDataAsOutOfSync(formulasMap,  //
					Sets.newHashSet(phenotypes), //
					Sets.newHashSet(allPhenotypes));
			}
		}
	}

	@Override
	public Boolean checkOutOfBoundDraftData(final Integer datasetId) {

		final List<Phenotype> phenotypes = this.daoFactory.getPhenotypeDAO().getDraftDataOfDataset(datasetId);

		if (phenotypes.size() > 0) {
			final List<MeasurementVariable>
				measurementVariableList =
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, DatasetServiceImpl.MEASUREMENT_VARIABLE_TYPES);

			for (final MeasurementVariable measurementVariable : measurementVariableList) {
				final Collection<Phenotype> selectedPhenotypes = CollectionUtils.select(phenotypes, new Predicate() {

					@Override
					public boolean evaluate(final Object o) {
						final Phenotype phenotype = (Phenotype) o;
						return phenotype.getObservableId().equals(measurementVariable.getTermId());
					}
				});

				Collection<Phenotype> possibleValues = null;
				if (measurementVariable.getPossibleValues() != null && !measurementVariable.getPossibleValues().isEmpty()) {
					possibleValues =
						CollectionUtils.collect(measurementVariable.getPossibleValues(), new Transformer() {

							@Override
							public String transform(final Object input) {
								final ValueReference variable = (ValueReference) input;
								return variable.getName();
							}

						});

				}

				for (final Phenotype phenotype : selectedPhenotypes) {
					if (!ExportImportUtils
						.isValidValue(measurementVariable, phenotype.getDraftValue(), possibleValues)) {
						return Boolean.TRUE;
					}
				}

			}
		}

		return Boolean.FALSE;
	}



	/**
	 *
	 * @param datasetId
	 * @param selectionMethodsAndTraits
	 * @param observationUnitIds
	 * @return A map where the key element is the observation unit id (OBS_UNIT_ID) in nd_experiment table, and value is
	 * 		a observationUnitRow that contains only values for the specified measurement variables
	 */
	@Override
	public Map<String, ObservationUnitRow> getObservationUnitsAsMap(
			final int datasetId,
			final List<MeasurementVariable> selectionMethodsAndTraits, final List<String> observationUnitIds) {
		return this.daoFactory.getExperimentDao().getObservationUnitsAsMap(datasetId, selectionMethodsAndTraits,
				observationUnitIds);
	}

	@Override
	public void importDataset(final Integer datasetId, final Table<String, String, String> table, final Boolean draftMode) {
		final List<MeasurementVariable>
			measurementVariableList =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, DatasetServiceImpl.MEASUREMENT_VARIABLE_TYPES);

		if (measurementVariableList.size() > 0) {

			final List<String> observationUnitIds = new ArrayList<>(table.rowKeySet());

			final Map<String, ObservationUnitRow> currentData =
				this.daoFactory.getExperimentDao().getObservationUnitsAsMap(datasetId, measurementVariableList,
					observationUnitIds);

			final Map<Integer, List<Integer>> formulasMap = this.getTargetsByInput(measurementVariableList);

			for (final Object observationUnitId : table.rowKeySet()) {
				final Set<Phenotype> phenotypes = new HashSet<>();
				final ObservationUnitRow currentRow = currentData.get(observationUnitId);

				// TODO Review performance IBP-2230
				final ExperimentModel experimentModel = this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitId.toString());

				for (final String variableName : table.columnKeySet()) {
					final String importedVariableValue = table.get(observationUnitId, variableName);

					if (StringUtils.isNotBlank(importedVariableValue)) {
						final MeasurementVariable measurementVariable =
							(MeasurementVariable) CollectionUtils.find(measurementVariableList, new Predicate() {

								@Override
								public boolean evaluate(final Object object) {
									final MeasurementVariable variable = (MeasurementVariable) object;
									return variable.getAlias().equalsIgnoreCase(variableName);
								}
							});

						BigInteger categoricalValueId = null;
						if (measurementVariable.getDataTypeId() == TermId.CATEGORICAL_VARIABLE.getId()) {
							for (final ValueReference possibleValue : measurementVariable.getPossibleValues()) {
								if (importedVariableValue.equalsIgnoreCase(possibleValue.getName())) {
									categoricalValueId = BigInteger.valueOf(possibleValue.getId());
									break;
								}
							}

						}

						final ObservationUnitData observationUnitData = currentRow.getVariables().get(measurementVariable.getName());
						final Integer categoricalValue = categoricalValueId != null ? categoricalValueId.intValue() : null;
						Phenotype phenotype = null;
						if ((observationUnitData == null || observationUnitData.getObservationId() == null)) {
							/*Phenotype does not exist*/

							String status = null;
							if (measurementVariable.getFormula() != null) {
								status = Phenotype.ValueStatus.MANUALLY_EDITED.getName();
							}

							final ObservationDto observationDto =
								new ObservationDto(measurementVariable.getTermId(), importedVariableValue, categoricalValue, status,
									Util.getCurrentDateAsStringValue(), Util.getCurrentDateAsStringValue(),
									experimentModel.getNdExperimentId(), categoricalValue, importedVariableValue);

							phenotype = this.createPhenotype(observationDto, draftMode);

						} else if (observationUnitData != null && observationUnitData.getObservationId() != null
							&& importedVariableValue .equalsIgnoreCase(observationUnitData.getValue())
							&& Boolean.TRUE.equals(draftMode)) {
							/*Phenotype exists and imported value is equal to value => Erase draft data*/
							phenotype = this.updatePhenotype(observationUnitData.getObservationId(), null, null, draftMode);
						}
						else if (observationUnitData != null && observationUnitData.getObservationId() != null &&
							!importedVariableValue.equalsIgnoreCase(observationUnitData.getValue())) {
							/*imported value is different to stored value*/
							phenotype =
								this.updatePhenotype(
									observationUnitData.getObservationId(), categoricalValue, importedVariableValue, draftMode);
						}

						if (phenotype != null) {
							phenotypes.add(phenotype);
						}
					}
				}

				if (!draftMode) {
					final ArrayList<Phenotype> datasetPhenotypes = new ArrayList<>(experimentModel.getPhenotypes());
					datasetPhenotypes.addAll(phenotypes);
					this.setMeasurementDataAsOutOfSync(formulasMap, //
						Sets.newHashSet(phenotypes), //
						Sets.newHashSet(datasetPhenotypes));
				}
			}
		}
	}

	@Override
	public List<MeasurementVariable> getDatasetMeasurementVariables(final Integer datasetId) {
		return this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, MEASUREMENT_VARIABLE_TYPES);
	}

	@Override
	public Map<Integer, List<ObservationUnitRow>> getInstanceIdToObservationUnitRowsMap(final int studyId, final int datasetId,
			final List<Integer> instanceIds) {
		final Map<Integer, List<ObservationUnitRow>> instanceMap = new HashMap<>();

		final List<MeasurementVariableDto> selectionMethodsAndTraits = this.measurementVariableService.getVariablesForDataset(datasetId,
				VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());
		final List<String> designFactors = this.findAdditionalDesignFactors(studyId);
		final List<String> germplasmDescriptors = this.findGenericGermplasmDescriptors(studyId);

		final DmsProject environmentDataset = this.daoFactory.getDmsProjectDAO().getDataSetsByStudyAndProjectProperty(studyId, TermId.DATASET_TYPE.getId(),
				String.valueOf(DataSetType.SUMMARY_DATA.getId())).get(0);
		final List<MeasurementVariable> studyVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(studyId,
				Lists.newArrayList(VariableType.STUDY_DETAIL.getId()));

		for (final Integer instanceId : instanceIds) {
			final ObservationUnitsSearchDTO
				searchDTO = new ObservationUnitsSearchDTO(datasetId, instanceId, germplasmDescriptors, designFactors, selectionMethodsAndTraits);
			searchDTO.setEnvironmentDetails(this.findAdditionalEnvironmentFactors(environmentDataset.getProjectId()));
			searchDTO.setEnvironmentConditions(this.getEnvironmentConditionVariableNames(environmentDataset.getProjectId()));
			searchDTO.setEnvironmentDatasetId(environmentDataset.getProjectId());

			final List<ObservationUnitRow> observationUnits = this.daoFactory.getExperimentDao().getObservationUnitTable(searchDTO);
			this.addStudyVariablesToUnitRows(observationUnits, studyVariables);
			instanceMap.put(instanceId, observationUnits);
		}
		return instanceMap;
	}

	void addStudyVariablesToUnitRows(final List<ObservationUnitRow> observationUnits, final List<MeasurementVariable> studyVariables) {
		for(final ObservationUnitRow observationUnitRow: observationUnits) {
			for(final MeasurementVariable measurementVariable: studyVariables) {
				observationUnitRow.getVariables().put(measurementVariable.getName(), new ObservationUnitData(measurementVariable.getValue()));
			}
		}
	}

	private void setMeasurementDataAsOutOfSync(
		final Map<Integer, List<Integer>> targetsByInput,
		final Set<Phenotype> phenotypes,
		final Set<Phenotype> datasetPhenotypes) {

		final Set<Integer> observationUnitIdOutOfSync = new HashSet<>();
		final Set<Integer> targetVariableIdOutOfSync = new HashSet<>();

		for (final Phenotype phenotype : phenotypes) {
			if (phenotype.isChanged()) {
				observationUnitIdOutOfSync.add(phenotype.getExperiment().getNdExperimentId());
				final List<Integer> targetsOutOfSync = targetsByInput.get(phenotype.getObservableId());
				if (targetsOutOfSync != null) {
					targetVariableIdOutOfSync.addAll(targetsOutOfSync);
				}
			}
		}

		for (final Phenotype datasetPhenotype : datasetPhenotypes) {
            if (targetVariableIdOutOfSync.contains(datasetPhenotype.getObservableId())
				&& observationUnitIdOutOfSync.contains(datasetPhenotype.getExperiment().getNdExperimentId())) {

				datasetPhenotype.setValueStatus(Phenotype.ValueStatus.OUT_OF_SYNC);
				this.daoFactory.getPhenotypeDAO().saveOrUpdate(datasetPhenotype);
			}

		}
	}

	private Map<Integer, List<Integer>> getTargetsByInput(
		final List<MeasurementVariable> measurementVariables) {

		final Map<Integer, List<Integer>> targetByInput = new HashMap<>();

		for (final MeasurementVariable input : measurementVariables) {
			for (final MeasurementVariable target : measurementVariables) {
				final FormulaDto formula = target.getFormula();
				if (formula != null && formula.isInputVariablePresent(input.getTermId())) {
					if (!targetByInput.containsKey(input.getTermId())) {
						targetByInput.put(input.getTermId(), new ArrayList<Integer>());
					}
					targetByInput.get(input.getTermId()).add(target.getTermId());
				}
			}
		}

		return targetByInput;
	}

	private Phenotype updatePhenotype(
		final Phenotype phenotype, final Integer categoricalValueId, final String value, final Boolean draftMode) {
		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();

		return this.updatePhenotypeValues(categoricalValueId, value, draftMode, phenotypeDao, phenotype);
	}

	private Phenotype updatePhenotype(
		final Integer observationId, final Integer categoricalValueId, final String value, final Boolean draftMode) {
		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();

		final Phenotype phenotype = phenotypeDao.getById(observationId);
		return this.updatePhenotypeValues(categoricalValueId, value, draftMode, phenotypeDao, phenotype);
	}

	private Phenotype updatePhenotypeValues(
		final Integer categoricalValueId, final String value, final Boolean draftMode, final PhenotypeDao phenotypeDao,
		final Phenotype phenotype) {
		if (draftMode) {
			phenotype.setDraftValue(value);
			phenotype.setDraftCValueId(Integer.valueOf(0).equals(categoricalValueId) ? null : categoricalValueId);
		} else {
			phenotype.setValue(value);
			phenotype.setcValue(Integer.valueOf(0).equals(categoricalValueId) ? null : categoricalValueId);
			phenotype.setDraftValue(null);
			phenotype.setDraftCValueId(null);
		}
		final Integer observableId = phenotype.getObservableId();
		// TODO Review performance IBP-2230
		//  Can we leverage measurementVariable.getFormula() as in importDataset() ?
		this.resolveObservationStatus(observableId, phenotype);
		phenotype.setChanged(true);
		phenotypeDao.update(phenotype);
		return phenotype;
	}

	private Phenotype createPhenotype(final ObservationDto observation, final Boolean draftMode) {
		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());

		final Integer variableId = observation.getVariableId();
		phenotype.setObservableId(variableId);

		final Integer observationUnitId = observation.getObservationUnitId();
		phenotype.setExperiment(new ExperimentModel(observationUnitId));
		phenotype.setName(String.valueOf(variableId));

		if (draftMode) {
			phenotype.setDraftCValueId(observation.getCategoricalValueId());
			phenotype.setDraftValue(observation.getValue());
		} else {
			phenotype.setValue(observation.getValue());
			phenotype.setcValue(observation.getCategoricalValueId());
			this.resolveObservationStatus(variableId, phenotype);
			phenotype.setChanged(true);
		}

		final Phenotype savedRecord = this.daoFactory.getPhenotypeDAO().save(phenotype);
		observation.setObservationId(savedRecord.getPhenotypeId());
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		observation.setCreatedDate(dateFormat.format(savedRecord.getCreatedDate()));
		observation.setUpdatedDate(dateFormat.format(savedRecord.getUpdatedDate()));
		observation.setStatus(savedRecord.getValueStatus() != null ? savedRecord.getValueStatus().getName() : null);

		return phenotype;
	}

	public void setMeasurementVariableService(final MeasurementVariableService measurementVariableService) {
		this.measurementVariableService = measurementVariableService;
	}

	public void setStudyService(final StudyService studyService) {
		this.studyService = studyService;
	}

	@Override
	public Map<String, Long> countObservationsGroupedByInstance(final Integer datasetId) {
		return daoFactory.getExperimentDao().countObservationsPerInstance(datasetId);
	}

}
