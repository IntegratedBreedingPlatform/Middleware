package org.generationcp.middleware.service.impl.dataset;

import com.google.common.base.Function;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.ObservationUnitIDGenerator;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.FilteredPhenotypesInstancesCountDTO;
import org.generationcp.middleware.service.api.dataset.InstanceDetailsDTO;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsParamDTO;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.derived_variables.DerivedVariableService;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.impl.study.ObservationUnitIDGeneratorImpl;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by clarysabel on 10/22/18.
 */
@Transactional
public class DatasetServiceImpl implements DatasetService {

	public static final String DATE_FORMAT = "YYYYMMDD HH:MM:SS";

	private static final List<Integer> SUBOBS_COLUMNS_ALL_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.GERMPLASM_DESCRIPTOR.getId(),
		VariableType.TRAIT.getId(),
		VariableType.SELECTION_METHOD.getId(),
		VariableType.OBSERVATION_UNIT.getId());

	private static final List<Integer> PLOT_COLUMNS_ALL_VARIABLE_TYPES = Lists.newArrayList( //
		VariableType.GERMPLASM_DESCRIPTOR.getId(), //
		VariableType.EXPERIMENTAL_DESIGN.getId(), //
		VariableType.TREATMENT_FACTOR.getId(), //
		VariableType.OBSERVATION_UNIT.getId(), //
		VariableType.TRAIT.getId(), //
		VariableType.SELECTION_METHOD.getId());

	private static final List<Integer> PLOT_COLUMNS_FACTOR_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.GERMPLASM_DESCRIPTOR.getId(),
		VariableType.EXPERIMENTAL_DESIGN.getId(),
		VariableType.TREATMENT_FACTOR.getId(),
		VariableType.OBSERVATION_UNIT.getId());

	private static final List<Integer> ENVIRONMENT_DATASET_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.ENVIRONMENT_DETAIL.getId(),
		VariableType.ENVIRONMENT_CONDITION.getId());

	protected static final List<Integer> OBSERVATION_DATASET_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.OBSERVATION_UNIT.getId(),
		VariableType.TRAIT.getId(),
		VariableType.SELECTION_METHOD.getId(),
		VariableType.GERMPLASM_DESCRIPTOR.getId());

	protected static final List<Integer> MEASUREMENT_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.TRAIT.getId(),
		VariableType.SELECTION_METHOD.getId());

	private static final List<Integer> STANDARD_ENVIRONMENT_FACTORS = Lists.newArrayList(
		TermId.LOCATION_ID.getId(),
		TermId.TRIAL_INSTANCE_FACTOR.getId(),
		TermId.EXPERIMENT_DESIGN_FACTOR.getId());
	private static final String SUM_OF_SAMPLES = "SUM_OF_SAMPLES";

	private DaoFactory daoFactory;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private StudyService studyService;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private DerivedVariableService derivedVariableService;

	public DatasetServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public long countObservationsByVariables(final Integer datasetId, final List<Integer> variableIds) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDataset(datasetId, variableIds);
	}

	@Override
	public long countObservationsByInstance(final Integer datasetId, final Integer instanceId) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDatasetAndInstance(datasetId, instanceId);
	}

	@Override
	public List<MeasurementVariable> getObservationSetColumns(final Integer observationSetId, final Boolean draftMode) {
		// TODO get plot dataset even if subobs is not a direct descendant (ie. sub-sub-obs)
		final List<MeasurementVariable> factorColumns;
		final DatasetDTO datasetDTO = this.getDataset(observationSetId);

		if (datasetDTO.getDatasetTypeId().equals(DatasetTypeEnum.PLOT_DATA.getId())) {
			//PLOTDATA
			factorColumns = this.daoFactory.getDmsProjectDAO()
				.getObservationSetVariables(observationSetId, PLOT_COLUMNS_FACTOR_VARIABLE_TYPES);
		} else {
			//SUBOBS
			final DmsProject plotDataset = this.daoFactory.getDmsProjectDAO().getById(observationSetId).getParent();
			// TODO get immediate parent columns
			// (ie. Plot subdivided into plant and then into fruits, then immediate parent column would be PLANT_NO)
			factorColumns =
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(plotDataset.getProjectId(),
					PLOT_COLUMNS_FACTOR_VARIABLE_TYPES);
		}

		List<MeasurementVariable> variateColumns;
		if (datasetDTO.getDatasetTypeId().equals(DatasetTypeEnum.PLOT_DATA.getId())) {
			//PLOTDATA
			variateColumns = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(observationSetId, MEASUREMENT_VARIABLE_TYPES);
		} else {
			//SUBOBS
			variateColumns = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(observationSetId,
				SUBOBS_COLUMNS_ALL_VARIABLE_TYPES);
		}

		// Filter columns with draft data
		if (Boolean.TRUE.equals(draftMode)) {
			final Set<Integer> pendingVariableIds = this.daoFactory.getPhenotypeDAO().getPendingVariableIds(observationSetId);
			variateColumns =
				Lists.newArrayList(Iterables.filter(variateColumns, new com.google.common.base.Predicate<MeasurementVariable>() {

					@Override
					public boolean apply(@Nullable final MeasurementVariable input) {
						return pendingVariableIds.contains(input.getTermId())
							|| VariableType.OBSERVATION_UNIT.equals(input.getVariableType());
					}
				}));
		}

		// Virtual columns
		if (this.daoFactory.getSampleDao().countByDatasetId(observationSetId) > 0) {
			factorColumns.add(this.buildSampleColumn());
		}

		// Other edge cases
		// Let's consider it as a special case instead of getting ENVIRONMENT_DETAILS for plot dataset
		final MeasurementVariable trialInstanceCol = new MeasurementVariable();
		trialInstanceCol.setName(ColumnLabels.TRIAL_INSTANCE.getName());
		trialInstanceCol.setAlias(ColumnLabels.TRIAL_INSTANCE.getName());
		trialInstanceCol.setTermId(ColumnLabels.TRIAL_INSTANCE.getTermId().getId());
		trialInstanceCol.setFactor(true);
		factorColumns.add(0, trialInstanceCol);

		factorColumns.addAll(variateColumns);

		return factorColumns;
	}

	private MeasurementVariable buildSampleColumn() {
		final MeasurementVariable sampleColumn = new MeasurementVariable();
		// Set the the variable name of this virtual Sample Column to SUM_OF_SAMPLES, to match
		// the Sample field name in observation query.
		sampleColumn.setName(SUM_OF_SAMPLES);
		sampleColumn.setAlias(TermId.SAMPLES.name());
		sampleColumn.setTermId(TermId.SAMPLES.getId());
		sampleColumn.setFactor(true);
		return sampleColumn;
	}

	@Override
	public List<MeasurementVariable> getObservationSetVariables(final Integer observationSetId) {

		final DatasetDTO datasetDTO = this.getDataset(observationSetId);

		final List<MeasurementVariable> plotDataSetColumns;
		if (datasetDTO.getDatasetTypeId().equals(DatasetTypeEnum.PLOT_DATA.getId())) {
			plotDataSetColumns =
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetDTO.getDatasetId(), PLOT_COLUMNS_ALL_VARIABLE_TYPES);

		} else {
			final DmsProject plotDataset = this.daoFactory.getDmsProjectDAO().getById(observationSetId).getParent();
			plotDataSetColumns =
				this.daoFactory.getDmsProjectDAO()
					.getObservationSetVariables(plotDataset.getProjectId(), PLOT_COLUMNS_FACTOR_VARIABLE_TYPES);
			final List<MeasurementVariable> subObservationSetColumns =
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(observationSetId, SUBOBS_COLUMNS_ALL_VARIABLE_TYPES);
			plotDataSetColumns.addAll(subObservationSetColumns);
		}

		// TODO get immediate parent columns
		// (ie. Plot subdivided into plant and then into fruits, then immediate parent column would be PLANT_NO)
		return plotDataSetColumns;

	}

	@Override
	public DatasetDTO generateSubObservationDataset(
		final Integer studyId, final String datasetName, final Integer datasetTypeId,
		final List<Integer> instanceIds,
		final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits, final Integer parentId) {

		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);

		final List<DmsProject> plotDatasets =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.PLOT_DATA.getId());

		if (plotDatasets == null || plotDatasets.isEmpty()) {
			throw new MiddlewareException("Study does not have a plot dataset associated to it");
		}

		final DmsProject plotDataset = plotDatasets.get(0);
		final DmsProject parentDataset = this.daoFactory.getDmsProjectDAO().getById(parentId);

		final DmsProject subObservationDataset = new DmsProject();

		final List<ProjectProperty> projectProperties =
			this.buildDefaultDatasetProperties(study, subObservationDataset, datasetName);
		final Variable observationUnitVariable =
			this.ontologyVariableDataManager.getVariable(study.getProgramUUID(), observationUnitVariableId, false);

		projectProperties.add(
			this.buildDatasetProperty(subObservationDataset, VariableType.OBSERVATION_UNIT.getId(), observationUnitVariableId, null, null,
				4, observationUnitVariable));

		subObservationDataset.setName(datasetName);
		subObservationDataset.setDescription(datasetName);
		subObservationDataset.setProgramUUID(study.getProgramUUID());
		subObservationDataset.setDeleted(false);
		subObservationDataset.setLocked(false);
		subObservationDataset.setProperties(projectProperties);
		subObservationDataset.setDatasetType(new DatasetType(datasetTypeId));
		subObservationDataset.setParent(parentDataset);
		subObservationDataset.setStudy(study);

		final DmsProject dataset = this.daoFactory.getDmsProjectDAO().save(subObservationDataset);

		// iterate and create new sub-observation units
		this.saveSubObservationUnits(studyId, instanceIds, numberOfSubObservationUnits, plotDataset, subObservationDataset);
		return this.getDataset(dataset.getProjectId());
	}

	/**
	 * Creates sub-observation units for each plot observation unit
	 */
	void saveSubObservationUnits(
		final Integer studyId, final List<Integer> instanceIds, final Integer numberOfSubObservationUnits,
		final DmsProject plotDataset, final DmsProject subObservationDataset) {
		final List<ExperimentModel> plotObservationUnits =
			this.daoFactory.getExperimentDao().getObservationUnits(plotDataset.getProjectId(), instanceIds);
		final DmsProject dmsProject = this.daoFactory.getDmsProjectDAO().getById(studyId);
		final CropType crop = this.workbenchDataManager.getProjectByUuid(dmsProject.getProgramUUID()).getCropType();
		final ObservationUnitIDGenerator observationUnitIdGenerator = new ObservationUnitIDGeneratorImpl();
		for (final ExperimentModel plotObservationUnit : plotObservationUnits) {
			for (int i = 1; i <= numberOfSubObservationUnits; i++) {
				final ExperimentModel experimentModel = new ExperimentModel(plotObservationUnit.getGeoLocation(),
					plotObservationUnit.getTypeId(), subObservationDataset, plotObservationUnit.getStock(), plotObservationUnit, i);
				observationUnitIdGenerator.generateObservationUnitIds(crop, Arrays.asList(experimentModel));
				this.daoFactory.getExperimentDao().save(experimentModel);
			}
		}
	}

	@Override
	public Boolean isDatasetNameAvailable(final String name, final int studyId) {
		final List<DatasetDTO> datasetDTOs = this.getDatasets(studyId, new HashSet<>());
		for (final DatasetDTO datasetDTO : datasetDTOs) {
			if (datasetDTO.getName().equals(name)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Integer getNumberOfChildren(final Integer parentId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetsByParent(parentId).size();
	}

	@Override
	public List<StudyInstance> getDatasetInstances(final Integer datasetId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetInstances(datasetId);
	}

	private List<ProjectProperty> buildDefaultDatasetProperties(
		final DmsProject study, final DmsProject dmsProject,
		final String datasetName) {
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
		projectProperties.add(datasetProperty);
		projectProperties.add(datasetTitleProperty);
		return projectProperties;
	}

	private ProjectProperty buildDatasetProperty(
		final DmsProject dmsProject, final Integer typeId,
		final Integer variableId, final String value, final String alias, final Integer rank, final Variable variable) {
		if (!variable.getVariableTypes().contains(VariableType.getById(typeId))) {
			throw new MiddlewareException("Specified type does not match with the list of types associated to the variable");
		}
		return new ProjectProperty(dmsProject, typeId, value, rank, variableId, (alias == null) ? variable.getName() : alias);
	}

	@Override
	public List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds) {
		final List<DatasetDTO> datasetDTOList = new ArrayList<>();
		this.filterDatasets(datasetDTOList, studyId, datasetTypeIds);
		return datasetDTOList;
	}

	private void filterDatasets(final List<DatasetDTO> filtered, final Integer parentId, final Set<Integer> datasetTypeIds) {

		final Map<Integer, Long> countOutOfSyncPerDatasetMap =
			this.daoFactory.getPhenotypeDAO().countOutOfSyncDataOfDatasetsInStudy(parentId);
		final List<DatasetDTO> datasetDTOs = this.daoFactory.getDmsProjectDAO().getDatasets(parentId);

		for (final DatasetDTO datasetDTO : datasetDTOs) {
			if (datasetTypeIds.isEmpty() || datasetTypeIds.contains(datasetDTO.getDatasetTypeId())) {
				final Integer datasetId = datasetDTO.getDatasetId();
				datasetDTO.setHasPendingData(this.daoFactory.getPhenotypeDAO().countPendingDataOfDataset(datasetId) > 0);
				datasetDTO.setHasOutOfSyncData(
					countOutOfSyncPerDatasetMap.containsKey(datasetId) ? countOutOfSyncPerDatasetMap.get(datasetDTO.getDatasetId()) > 0 :
						Boolean.FALSE);
				filtered.add(datasetDTO);
			}
		}
	}

	@Override
	public void addDatasetVariable(final Integer datasetId, final Integer variableId, final VariableType type, final String alias) {
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
	public void removeDatasetVariables(final Integer datasetId, final List<Integer> variableIds) {
		this.daoFactory.getProjectPropertyDAO().deleteProjectVariables(datasetId, variableIds);
		this.daoFactory.getPhenotypeDAO().deletePhenotypesByProjectIdAndVariableIds(datasetId, variableIds);
	}

	@Override
	public boolean isValidObservationUnit(final Integer datasetId, final Integer observationUnitId) {
		return this.daoFactory.getExperimentDao().isValidExperiment(datasetId, observationUnitId);
	}

	@Override
	public boolean isValidDatasetId(final Integer datasetId) {
		return this.daoFactory.getDmsProjectDAO().isValidDatasetId(datasetId);
	}

	@Override
	public Phenotype getPhenotype(final Integer observationUnitId, final Integer observationId) {
		return this.daoFactory.getPhenotypeDAO().getPhenotype(observationUnitId, observationId);
	}

	@Override
	public ObservationDto createObservation(final ObservationDto observation) {
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
		this.updateDependentPhenotypesAsOutOfSync(observableId, Sets.newHashSet(observationUnitId));

		observation.setObservationId(savedRecord.getPhenotypeId());
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		observation.setCreatedDate(dateFormat.format(savedRecord.getCreatedDate()));
		observation.setUpdatedDate(dateFormat.format(savedRecord.getUpdatedDate()));
		observation.setStatus(savedRecord.getValueStatus() != null ? savedRecord.getValueStatus().getName() : null);

		return observation;
	}

	@Override
	public ObservationDto updatePhenotype(final Integer observationId, final ObservationDto observationDto) {
		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();

		final Phenotype phenotype = phenotypeDao.getById(observationId);
		phenotype.setValue(observationDto.getValue());
		phenotype.setcValue(Integer.valueOf(0).equals(observationDto.getCategoricalValueId()) ?
			null : observationDto.getCategoricalValueId());
		phenotype.setDraftValue(observationDto.getDraftValue());
		phenotype.setDraftCValueId(Integer.valueOf(0).equals(observationDto.getDraftCategoricalValueId()) ?
			null : observationDto.getDraftCategoricalValueId());
		final Integer observableId = phenotype.getObservableId();

		if (!observationDto.isDraftMode()) {
			this.resolveObservationStatus(observableId, phenotype);
		}

		phenotypeDao.update(phenotype);

		if (!observationDto.isDraftMode()) {
			// Also update the status of phenotypes of the same observation unit for variables using it as input variable
			this.updateDependentPhenotypesAsOutOfSync(observableId, Sets.newHashSet(observationDto.getObservationUnitId()));
		}

		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		observationDto.setObservationId(phenotype.getPhenotypeId());
		observationDto.setUpdatedDate(dateFormat.format(phenotype.getUpdatedDate()));
		observationDto.setCreatedDate(dateFormat.format(phenotype.getCreatedDate()));
		observationDto.setStatus(phenotype.getValueStatus() != null ? phenotype.getValueStatus().getName() : null);
		observationDto.setVariableId(phenotype.getObservableId());

		return observationDto;

	}

	void resolveObservationStatus(final Integer variableId, final Phenotype phenotype) {
		final boolean isDerivedTrait;
		if (phenotype.isDerivedTrait() != null) {
			// Performance optimization when processing many phenotypes
			isDerivedTrait = phenotype.isDerivedTrait();
		} else {
			final Formula formula = this.daoFactory.getFormulaDAO().getByTargetVariableId(variableId);
			isDerivedTrait = formula != null;
		}
		if (isDerivedTrait) {
			phenotype.setValueStatus(Phenotype.ValueStatus.MANUALLY_EDITED);
		}
	}

	@Override
	public void updateDependentPhenotypesAsOutOfSync(final Integer variableId, final Set<Integer> observationUnitIds) {
		final List<Formula> formulaList = this.daoFactory.getFormulaDAO().getByInputId(variableId);
		if (!org.springframework.util.CollectionUtils.isEmpty(formulaList) && !org.springframework.util.CollectionUtils
			.isEmpty(observationUnitIds)) {
			final List<Integer> targetVariableIds = Lists.transform(formulaList, new Function<Formula, Integer>() {

				@Override
				public Integer apply(final Formula formula) {
					return formula.getTargetCVTerm().getCvTermId();
				}
			});
			this.daoFactory.getPhenotypeDAO()
				.updateOutOfSyncPhenotypes(observationUnitIds, Sets.newHashSet(targetVariableIds));

		}

	}

	@Override
	public void updateOutOfSyncPhenotypes(final Set<Integer> targetVariableIds, final Set<Integer> observationUnitIds) {
		if (!targetVariableIds.isEmpty()) {
			this.daoFactory.getPhenotypeDAO().updateOutOfSyncPhenotypes(observationUnitIds, targetVariableIds);
		}

	}

	@Override
	public void updateDependentPhenotypesStatusByGeolocation(final Integer geolocation, final List<Integer> variableIds) {

		final List<Formula> formulaList = this.daoFactory.getFormulaDAO().getByInputIds(variableIds);
		if (!formulaList.isEmpty()) {
			final List<Integer> targetVariableIds = Lists.transform(formulaList, new Function<Formula, Integer>() {

				@Override
				public Integer apply(final Formula formula) {
					return formula.getTargetCVTerm().getCvTermId();
				}
			});
			this.daoFactory.getPhenotypeDAO()
				.updateOutOfSyncPhenotypesByGeolocation(geolocation, Sets.newHashSet(targetVariableIds));
		}
	}

	@Override
	public DatasetDTO getDataset(final Integer datasetId) {
		final DatasetDTO datasetDTO = this.daoFactory.getDmsProjectDAO().getDataset(datasetId);
		if (datasetDTO != null) {
			final DatasetType datasetType = this.daoFactory.getDatasetTypeDao().getById(datasetDTO.getDatasetTypeId());
			datasetDTO.setInstances(this.daoFactory.getDmsProjectDAO().getDatasetInstances(datasetId));
			final List<Integer> variableTypes = DatasetTypeEnum.SUMMARY_DATA.getId() == datasetDTO.getDatasetTypeId() ?
				DatasetServiceImpl.ENVIRONMENT_DATASET_VARIABLE_TYPES : DatasetServiceImpl.OBSERVATION_DATASET_VARIABLE_TYPES;
			datasetDTO.setVariables(
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, variableTypes));
			datasetDTO.setHasPendingData(this.daoFactory.getPhenotypeDAO().countPendingDataOfDataset(datasetId) > 0);
			datasetDTO.setHasOutOfSyncData(this.daoFactory.getPhenotypeDAO().hasOutOfSync(datasetId));
		}

		return datasetDTO;
	}

	@Override
	public DatasetDTO getDatasetByObsUnitDbId(final String observationUnitDbId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetByObsUnitDbId(observationUnitDbId);
	}

	protected void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final int datasetId, final ObservationUnitsSearchDTO searchDTO, final Pageable pageable) {

		this.fillSearchDTO(studyId, datasetId, searchDTO);

		return this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitTable(searchDTO, pageable);
	}

	@Override
	public List<Map<String, Object>> getObservationUnitRowsAsMapList(
		final int studyId, final int datasetId, final ObservationUnitsSearchDTO searchDTO, final Pageable pageable) {

		this.fillSearchDTO(studyId, datasetId, searchDTO);

		return this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitTableMapList(searchDTO, pageable);
	}

	private void fillSearchDTO(final int studyId, final int datasetId, final ObservationUnitsSearchDTO searchDTO) {
		searchDTO.setDatasetId(datasetId);
		searchDTO.setGenericGermplasmDescriptors(this.findGenericGermplasmDescriptors(studyId));
		searchDTO.setAdditionalDesignFactors(this.findAdditionalDesignFactors(studyId));

		final List<MeasurementVariableDto> selectionMethodsAndTraits =
			this.daoFactory.getProjectPropertyDAO().getVariablesForDataset(datasetId,
				VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());
		searchDTO.setSelectionMethodsAndTraits(selectionMethodsAndTraits);
	}

	@Override
	public List<ObservationUnitRow> getAllObservationUnitRows(final int studyId, final int datasetId) {

		final List<String> designFactors = this.findAdditionalDesignFactors(studyId);
		final List<String> germplasmDescriptors = this.findGenericGermplasmDescriptors(studyId);

		final DmsProject environmentDataset =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);
		final List<MeasurementVariable> studyVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(
			studyId,
			Lists.newArrayList(VariableType.STUDY_DETAIL.getId()));

		final List<MeasurementVariableDto> selectionMethodsAndTraits =
			this.daoFactory.getProjectPropertyDAO().getVariablesForDataset(datasetId,
				VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());

		final ObservationUnitsSearchDTO searchDTO =
			new ObservationUnitsSearchDTO(datasetId, null, germplasmDescriptors, designFactors, new ArrayList<>());
		searchDTO.setEnvironmentDetails(this.findAdditionalEnvironmentFactors(environmentDataset.getProjectId()));
		searchDTO.setEnvironmentConditions(this.getEnvironmentConditionVariableNames(environmentDataset.getProjectId()));
		searchDTO.setEnvironmentDatasetId(environmentDataset.getProjectId());
		searchDTO.setSelectionMethodsAndTraits(selectionMethodsAndTraits);

		final List<ObservationUnitRow> observationUnits =
			this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitTable(searchDTO, new PageRequest(0, Integer.MAX_VALUE));
		this.addStudyVariablesToUnitRows(observationUnits, studyVariables);

		return observationUnits;
	}

	private List<String> findGenericGermplasmDescriptors(final int studyId) {

		return this.studyService.getGenericGermplasmDescriptors(studyId);
	}

	private List<String> findAdditionalDesignFactors(final int studyId) {
		return this.studyService.getAdditionalDesignFactors(studyId);
	}

	List<MeasurementVariableDto> getEnvironmentConditionVariableNames(final Integer trialDatasetId) {
		final List<MeasurementVariable> environmentConditions = this.daoFactory.getDmsProjectDAO()
			.getObservationSetVariables(trialDatasetId, Lists.newArrayList(VariableType.ENVIRONMENT_CONDITION.getId()));
		final List<MeasurementVariableDto> factors = new ArrayList<>();
		for (final MeasurementVariable variable : environmentConditions) {
			factors.add(new MeasurementVariableDto(variable.getTermId(), variable.getName()));
		}
		return factors;
	}

	List<MeasurementVariableDto> findAdditionalEnvironmentFactors(final Integer trialDatasetId) {
		final List<MeasurementVariable> environmentDetailsVariables =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(trialDatasetId, Lists.newArrayList(
				VariableType.ENVIRONMENT_DETAIL.getId()));
		final List<MeasurementVariableDto> factors = new ArrayList<>();
		for (final MeasurementVariable variable : environmentDetailsVariables) {
			if (!STANDARD_ENVIRONMENT_FACTORS.contains(variable.getTermId())) {
				factors.add(new MeasurementVariableDto(variable.getTermId(), variable.getName()));
			}
		}
		return factors;
	}

	@Override
	public Integer countAllObservationUnitsForDataset(
		final Integer datasetId, final Integer instanceId, final Boolean draftMode) {
		return this.daoFactory.getObservationUnitsSearchDAO().countObservationUnitsForDataset(datasetId, instanceId, draftMode, null);
	}

	@Override
	public long countFilteredObservationUnitsForDataset(
		final Integer datasetId, final Integer instanceId, final Boolean draftMode,
		final ObservationUnitsSearchDTO.Filter filter) {
		return this.daoFactory.getObservationUnitsSearchDAO().countObservationUnitsForDataset(datasetId, instanceId, draftMode, filter);
	}

	@Override
	public void deletePhenotype(final Integer phenotypeId) {
		this.deletePhenotype(phenotypeId, true);
	}

	// Delete transaction might be part of a batch action so provide the option to not update dependent phenotypes right away (ie. do it in batch later)
	private void deletePhenotype(final Integer phenotypeId, final boolean updateDependentPhenotypes) {
		final Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getById(phenotypeId);
		final ExperimentModel experiment = phenotype.getExperiment();

		final List<Phenotype> experimentPhenotypes = experiment.getPhenotypes();
		experimentPhenotypes.remove(phenotype);
		experiment.setPhenotypes(experimentPhenotypes);
		this.daoFactory.getExperimentDao().merge(experiment);

		this.daoFactory.getPhenotypeDAO().makeTransient(phenotype);

		// Also update the status of phenotypes of the same observation unit for variables using the trait as input variable
		if (updateDependentPhenotypes) {
			final Integer observableId = phenotype.getObservableId();
			final Integer observationUnitId = experiment.getNdExperimentId();
			this.updateDependentPhenotypesAsOutOfSync(observableId, Sets.newHashSet(observationUnitId));
		}
	}

	@Override
	public void deleteDataset(final int datasetId) {

		try {

			this.daoFactory.getDmsProjectDAO().deleteDataset(datasetId);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("error in deleteDataSet " + e.getMessage(), e);
		}
	}

	// TODO consolidate with getObservationSetVariables
	@Override
	public List<MeasurementVariableDto> getDatasetVariablesByType(final Integer datasetId, final VariableType variableType) {
		return this.daoFactory.getProjectPropertyDAO().getVariablesForDataset(datasetId, variableType.getId());
	}

	@Override
	public List<MeasurementVariable> getObservationSetVariables(final Integer projectId, final List<Integer> variableTypes) {
		return this.daoFactory.getDmsProjectDAO().getObservationSetVariables(projectId, variableTypes);
	}

	@Override
	public void rejectDatasetDraftData(final Integer datasetId) {
		final List<Phenotype> phenotypes = this.daoFactory.getPhenotypeDAO().getDatasetDraftData(datasetId);
		for (final Phenotype phenotype : phenotypes) {
			if (StringUtils.isEmpty(phenotype.getValue())) {
				this.deletePhenotype(phenotype.getPhenotypeId(), false);
			} else {
				this.updatePhenotype(phenotype, phenotype.getcValueId(), phenotype.getValue(), null, null, true);
			}
		}
	}

	private void reorganizePhenotypesStatus(
		final Integer studyId, final List<Phenotype> inputPhenotypes) {
		final List<MeasurementVariable> measurementVariableList =
			new ArrayList<>(this.derivedVariableService.createVariableIdMeasurementVariableMapInStudy(studyId).values());

		if (!measurementVariableList.isEmpty()) {
			final Map<Integer, List<Integer>> formulasMap = this.getTargetsByInput(measurementVariableList);
			this.setMeasurementDataAsOutOfSync(
				formulasMap,
				Sets.newHashSet(inputPhenotypes));
		}
	}

	@Override
	public Boolean hasDatasetDraftDataOutOfBounds(final Integer datasetId) {

		final List<Phenotype> phenotypes = this.daoFactory.getPhenotypeDAO().getDatasetDraftData(datasetId);

		if (!phenotypes.isEmpty()) {
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

	@Override
	public void acceptAllDatasetDraftData(final Integer studyId, final Integer datasetId) {

		final List<Phenotype> draftPhenotypes = this.daoFactory.getPhenotypeDAO().getDatasetDraftData(datasetId);

		if (!draftPhenotypes.isEmpty()) {

			for (final Phenotype phenotype : draftPhenotypes) {
				if (StringUtils.isEmpty(phenotype.getDraftValue())) {
					// Set isChanged to true so that the derived traits that depend on it will be tagged as OUT_OF_SYNC later.
					phenotype.setChanged(true);
					this.deletePhenotype(phenotype.getPhenotypeId(), false);
				} else {
					this.updatePhenotype(
						phenotype, phenotype.getDraftCValueId(), phenotype.getDraftValue(), null, null, false);
				}
			}

			this.reorganizePhenotypesStatus(studyId, draftPhenotypes);
		}
	}

	@Override
	public void acceptDraftDataAndSetOutOfBoundsToMissing(final Integer studyId, final Integer datasetId) {
		final List<Phenotype> draftPhenotypes = this.daoFactory.getPhenotypeDAO().getDatasetDraftData(datasetId);

		if (!draftPhenotypes.isEmpty()) {
			final List<MeasurementVariable>
				measurementVariableList =
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, DatasetServiceImpl.MEASUREMENT_VARIABLE_TYPES);

			for (final MeasurementVariable measurementVariable : measurementVariableList) {
				final Collection<Phenotype> selectedPhenotypes = CollectionUtils.select(draftPhenotypes, new Predicate() {

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
					if (!ExportImportUtils.isValidValue(measurementVariable, phenotype.getDraftValue(), possibleValues)) {
						this.updatePhenotype(phenotype, null, Phenotype.MISSING, null, null, false);
					} else {
						this.acceptDraftData(phenotype);
					}
				}

				if (!selectedPhenotypes.isEmpty()) {
					this.reorganizePhenotypesStatus(studyId, draftPhenotypes);
				}
			}
		}
	}

	@Override
	public void acceptDraftDataFilteredByVariable(
		final Integer datasetId,
		final ObservationUnitsSearchDTO searchDTO, final int studyId) {

		final String variableId = searchDTO.getFilter().getVariableId().toString();
		final List<Phenotype> phenotypes = new ArrayList<>();
		this.fillSearchDTO(studyId, datasetId, searchDTO);

		final List<ObservationUnitRow> observationUnitsByVariable =
			this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitsByVariable(searchDTO);

		if (!observationUnitsByVariable.isEmpty()) {

			for (final ObservationUnitRow observationUnitRow : observationUnitsByVariable) {

				final ObservationUnitData observationUnitData = observationUnitRow.getVariables().get(
					variableId);
				Phenotype phenotype = null;
				if (observationUnitData != null) {
					/* TODO IBP-2822
					 *  Approach of IBP-2781 (getWithIsDerivedTrait) won't work here
					 *  because the performance gain of not having to call formulaDao is lost
					 *  with this query that is not as good as getById
					 */
					phenotype = this.daoFactory.getPhenotypeDAO().getById(observationUnitData.getObservationId());
				}

				if (phenotype != null) {
					phenotypes.add(phenotype);
					if (StringUtils.isEmpty(phenotype.getDraftValue())) {
						// Set isChanged to true so that the derived traits that depend on it will be tagged as OUT_OF_SYNC later.
						phenotype.setChanged(true);
						this.deletePhenotype(phenotype.getPhenotypeId(), false);
					} else {
						this.updatePhenotype(phenotype, phenotype.getDraftCValueId(), phenotype.getDraftValue(), null, null, false);
					}
				}
			}
		}

		this.reorganizePhenotypesStatus(studyId, phenotypes);
	}

	@Override
	public void setValueToVariable(final Integer datasetId, final ObservationUnitsParamDTO paramDTO, final Integer studyId) {

		final String newValue = paramDTO.getNewValue();
		final String variableId = paramDTO.getObservationUnitsSearchDTO().getFilter().getVariableId().toString();
		final List<Phenotype> phenotypes = new ArrayList<>();

		this.fillSearchDTO(studyId, datasetId, paramDTO.getObservationUnitsSearchDTO());
		final Boolean draftMode = paramDTO.getObservationUnitsSearchDTO().getDraftMode();
		final List<ObservationUnitRow> observationUnitsByVariable =
			this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitsByVariable(paramDTO.getObservationUnitsSearchDTO());

		if (!observationUnitsByVariable.isEmpty()) {

			for (final ObservationUnitRow observationUnitRow : observationUnitsByVariable) {
				final ObservationUnitData observationUnitData = observationUnitRow.getVariables().get(variableId);
				Phenotype phenotype = null;

				final Integer newCategoricalValueId = paramDTO.getNewCategoricalValueId();

				if (observationUnitData != null) {
					/* TODO IBP-2822
					 *  Approach of IBP-2781 (getWithIsDerivedTrait) won't work here
					 *  because the performance gain of not having to call formulaDao is lost
					 *  with this query that is not as good as getById
					 */
					phenotype = this.daoFactory.getPhenotypeDAO().getById(observationUnitData.getObservationId());
				}

				if (phenotype != null) {
					if (draftMode) {
						this.updatePhenotype(phenotype,
							phenotype.getcValueId(), phenotype.getValue(), newCategoricalValueId, newValue, draftMode);
					} else {
						this.updatePhenotype(phenotype,
							newCategoricalValueId, newValue, phenotype.getDraftCValueId(), phenotype.getDraftValue(), draftMode);
					}
				} else {
					final ObservationDto observationDto =
						new ObservationDto(observationUnitData.getVariableId(), newValue, newCategoricalValueId, null,
							Util.getCurrentDateAsStringValue(), Util.getCurrentDateAsStringValue(),
							observationUnitRow.getObservationUnitId(), newCategoricalValueId, newValue);
					phenotype = this.createPhenotype(observationDto, draftMode);
				}

				phenotypes.add(phenotype);
			}
		}

		if (!draftMode) {
			this.reorganizePhenotypesStatus(studyId, phenotypes);
		}
	}

	@Override
	public boolean allDatasetIdsBelongToStudy(final Integer studyId, final List<Integer> datasetIds) {
		return this.daoFactory.getDmsProjectDAO().allDatasetIdsBelongToStudy(studyId, datasetIds);
	}

	@Override
	public Table<Integer, Integer, Integer> getTrialNumberPlotNumberObservationUnitIdTable(final Integer datasetId,
		final Set<Integer> instanceNumbers, final Set<Integer> plotNumbers) {
		return this.daoFactory.getExperimentDao().getTrialNumberPlotNumberObservationUnitIdTable(datasetId, instanceNumbers, plotNumbers);
	}

	private void acceptDraftData(final Phenotype phenotype) {
		if (StringUtils.isEmpty(phenotype.getDraftValue())) {
			// Set isChanged to true so that the derived traits that depend on it will be tagged as OUT_OF_SYNC later.
			phenotype.setChanged(true);
			this.deletePhenotype(phenotype.getPhenotypeId(), false);
		} else {
			this.updatePhenotype(phenotype, phenotype.getDraftCValueId(), phenotype.getDraftValue(), null, null, false);
		}
	}

	/**
	 * @param datasetId
	 * @param selectionMethodsAndTraits
	 * @param observationUnitIds
	 * @return A map where the key element is the observation unit id (OBS_UNIT_ID) in nd_experiment table, and value is
	 * a observationUnitRow that contains only values for the specified measurement variables
	 */
	@Override
	public Map<String, ObservationUnitRow> getObservationUnitsAsMap(
		final int datasetId,
		final List<MeasurementVariable> selectionMethodsAndTraits, final List<String> observationUnitIds) {
		return this.daoFactory.getExperimentDao().getObservationUnitsAsMap(datasetId, selectionMethodsAndTraits,
			observationUnitIds);
	}

	@Override
	public Table<String, Integer, Integer> importDataset(final Integer datasetId, final Table<String, String, String> table,
		final Boolean draftMode, final Boolean allowDateAndCharacterBlankValue) {

		final Table<String, Integer, Integer> observationDbIdsTable = HashBasedTable.create();

		final List<MeasurementVariable> measurementVariableList =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, DatasetServiceImpl.MEASUREMENT_VARIABLE_TYPES);

		if (!measurementVariableList.isEmpty()) {

			final List<String> observationUnitIds = new ArrayList<>(table.rowKeySet());

			final Map<String, ObservationUnitRow> currentData =
				this.daoFactory.getExperimentDao().getObservationUnitsAsMap(datasetId, measurementVariableList,
					observationUnitIds);

			final Map<Integer, List<Integer>> formulasMap = this.getTargetsByInput(measurementVariableList);

			for (final Object observationUnitId : table.rowKeySet()) {
				final Set<Phenotype> phenotypes = new HashSet<>();
				final ObservationUnitRow currentRow = currentData.get(observationUnitId);

				for (final String variableName : table.columnKeySet()) {
					String importedVariableValue = table.get(observationUnitId, variableName);

					final MeasurementVariable measurementVariable =
						(MeasurementVariable) CollectionUtils.find(measurementVariableList, object -> {
							final MeasurementVariable variable = (MeasurementVariable) object;
							return variable.getAlias().equalsIgnoreCase(variableName);
						});

					// If allowDateAndCharacterBlankValue is true, allow to import blank value of Date and Character datatypes,
					// otherwise, just ignore blank values.
					if ((allowDateAndCharacterBlankValue && isDateOrCharacterDataType(measurementVariable)) || StringUtils
						.isNotBlank(importedVariableValue)) {
						BigInteger categoricalValueId = null;
						if (measurementVariable.getDataTypeId() == TermId.CATEGORICAL_VARIABLE.getId()) {
							for (final ValueReference possibleValue : measurementVariable.getPossibleValues()) {
								if (importedVariableValue.equalsIgnoreCase(possibleValue.getName())) {
									categoricalValueId = BigInteger.valueOf(possibleValue.getId());
									break;
								}
							}
						}
						if (measurementVariable.getDataTypeId() == TermId.DATE_VARIABLE.getId()) {
							// In case the date is in yyyy-MM-dd format, try to parse it as number format yyyyMMdd
							final String parsedDate =
								Util.tryConvertDate(importedVariableValue, Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT);
							if (parsedDate != null) {
								importedVariableValue = parsedDate;
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
									currentRow.getObservationUnitId(), categoricalValue, importedVariableValue);

							phenotype = this.createPhenotype(observationDto, draftMode);

						} else if (observationUnitData.getObservationId() != null
							&& importedVariableValue.equalsIgnoreCase(observationUnitData.getValue())
							&& Boolean.TRUE.equals(draftMode)) {
							/*Phenotype exists and imported value is equal to value => Erase draft data*/
							phenotype =
								this.updatePhenotype(observationUnitData.getObservationId(), observationUnitData.getCategoricalValueId(),
									observationUnitData.getValue(), null, null, draftMode);
						} else if (observationUnitData.getObservationId() != null &&
							!importedVariableValue.equalsIgnoreCase(observationUnitData.getValue())) {
							/*imported value is different to stored value*/
							phenotype =
								this.updatePhenotype(observationUnitData.getObservationId(), observationUnitData.getCategoricalValueId(),
									observationUnitData.getValue(), categoricalValue, importedVariableValue, draftMode);
						}

						if (phenotype != null) {
							phenotypes.add(phenotype);
						}

						// We need to return the observationDbIds (mapped in a table by observationUnitId and variableId) of the created/updated observations.
						observationDbIdsTable
							.put((String) observationUnitId, observationUnitData.getVariableId(), phenotype.getPhenotypeId());
					}
				}

				if (!draftMode) {
					/* TODO improve performance
					 *  Low priority as this flow is not reachable now from BMS
					 *  Import goes to draft data always
					 */
					final ExperimentModel experimentModel = this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitId.toString());

					final ArrayList<Phenotype> datasetPhenotypes = new ArrayList<>(experimentModel.getPhenotypes());
					datasetPhenotypes.addAll(phenotypes);
					this.setMeasurementDataAsOutOfSync(
						formulasMap, //
						Sets.newHashSet(phenotypes));
				}
			}
		}
		return observationDbIdsTable;
	}

	private static boolean isDateOrCharacterDataType(final MeasurementVariable measurementVariable) {
		return DataType.DATE_TIME_VARIABLE.getId().equals(measurementVariable.getDataTypeId()) || DataType.CHARACTER_VARIABLE.getId()
			.equals(measurementVariable.getDataTypeId());
	}

	@Override
	public List<MeasurementVariable> getDatasetMeasurementVariables(final Integer datasetId) {
		return this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, MEASUREMENT_VARIABLE_TYPES);
	}

	@Override
	public List<MeasurementVariable> getDatasetMeasurementVariablesByVariableType(final Integer datasetId,
		final List<Integer> variableTypes) {
		return this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, variableTypes);
	}

	@Override
	public Map<Integer, List<ObservationUnitRow>> getInstanceIdToObservationUnitRowsMap(
		final int studyId, final int datasetId,
		final List<Integer> instanceIds) {
		final Map<Integer, List<ObservationUnitRow>> instanceMap = new LinkedHashMap<>();

		final List<MeasurementVariableDto> selectionMethodsAndTraits =
			this.daoFactory.getProjectPropertyDAO().getVariablesForDataset(datasetId,
				VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());
		final List<String> designFactors = this.findAdditionalDesignFactors(studyId);
		final List<String> germplasmDescriptors = this.findGenericGermplasmDescriptors(studyId);

		final DmsProject environmentDataset =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);
		final List<MeasurementVariable> studyVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(
			studyId,
			Lists.newArrayList(VariableType.STUDY_DETAIL.getId()));

		for (final Integer instanceId : instanceIds) {
			final ObservationUnitsSearchDTO
				searchDTO =
				new ObservationUnitsSearchDTO(datasetId, instanceId, germplasmDescriptors, designFactors, selectionMethodsAndTraits);
			searchDTO.setEnvironmentDetails(this.findAdditionalEnvironmentFactors(environmentDataset.getProjectId()));
			searchDTO.setEnvironmentConditions(this.getEnvironmentConditionVariableNames(environmentDataset.getProjectId()));
			searchDTO.setEnvironmentDatasetId(environmentDataset.getProjectId());

			final List<ObservationUnitRow> observationUnits =
				this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitTable(searchDTO, null);
			this.addStudyVariablesToUnitRows(observationUnits, studyVariables);
			instanceMap.put(instanceId, observationUnits);
		}
		return instanceMap;
	}

	@Override
	public void replaceObservationUnitEntry(final List<Integer> observationUnitIds, final Integer newEntryId) {
		this.daoFactory.getExperimentDao().updateEntryId(observationUnitIds, newEntryId);
	}

	void addStudyVariablesToUnitRows(final List<ObservationUnitRow> observationUnits, final List<MeasurementVariable> studyVariables) {
		for (final ObservationUnitRow observationUnitRow : observationUnits) {
			for (final MeasurementVariable measurementVariable : studyVariables) {
				observationUnitRow.getVariables()
					.put(measurementVariable.getName(), new ObservationUnitData(measurementVariable.getValue()));
			}
		}
	}

	private void setMeasurementDataAsOutOfSync(
		final Map<Integer, List<Integer>> targetsByInput,
		final Set<Phenotype> inputPhenotypes) {

		if (!targetsByInput.isEmpty()) {
			final Set<Integer> observationUnitIdOutOfSync = new HashSet<>();
			final Set<Integer> targetVariableIdOutOfSync = new HashSet<>();

			for (final Phenotype phenotype : inputPhenotypes) {
				if (phenotype.isChanged()) {
					observationUnitIdOutOfSync.add(phenotype.getExperiment().getNdExperimentId());
					final List<Integer> targetsOutOfSync = targetsByInput.get(phenotype.getObservableId());
					if (targetsOutOfSync != null) {
						targetVariableIdOutOfSync.addAll(targetsOutOfSync);
					}
				}
			}

			this.updateOutOfSyncPhenotypes(targetVariableIdOutOfSync, observationUnitIdOutOfSync);
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
						targetByInput.put(input.getTermId(), new ArrayList<>());
					}
					targetByInput.get(input.getTermId()).add(target.getTermId());
				}
			}
		}

		return targetByInput;
	}

	private Phenotype updatePhenotype(
		final Phenotype phenotype, final Integer categoricalValueId, final String value, final Integer draftCategoricalValueId,
		final String draftvalue, final Boolean draftMode) {

		return this.updatePhenotypeValues(categoricalValueId, value, draftCategoricalValueId, draftvalue, phenotype, draftMode);
	}

	private Phenotype updatePhenotype(
		final Integer observationId, final Integer categoricalValueId, final String value, final Integer draftCategoricalValueId,
		final String draftvalue, final Boolean draftMode) {

		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();

		/* TODO IBP-2822
		 *  Approach of IBP-2781 (getWithIsDerivedTrait) won't work here
		 *  because the performance gain of not having to call formulaDao is lost
		 *  with this query that is not as good as getById
		 */
		final Phenotype phenotype = phenotypeDao.getById(observationId);
		return this.updatePhenotypeValues(categoricalValueId, value, draftCategoricalValueId, draftvalue, phenotype, draftMode);
	}

	/**
	 * @param draftMode False if either you are in Accepted view (e.g batch update in accepted view)
	 *                  or you are going to accepted view (e.g accepting draft data)
	 *                  FIXME IBP-2694
	 */
	private Phenotype updatePhenotypeValues(
		final Integer categoricalValueId, final String value, final Integer draftCategoricalValueId, final String draftvalue,
		final Phenotype phenotype, final Boolean draftMode) {

		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();
		phenotype.setDraftValue(draftvalue);
		phenotype.setDraftCValueId(Integer.valueOf(0).equals(draftCategoricalValueId) ? null : draftCategoricalValueId);
		phenotype.setValue(value);
		phenotype.setcValue(Integer.valueOf(0).equals(categoricalValueId) ? null : categoricalValueId);

		if (!draftMode) {
			final Integer observableId = phenotype.getObservableId();
			this.resolveObservationStatus(observableId, phenotype);
			phenotype.setChanged(true); // to set out-of-sync
		}

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
			// FIXME IBP-2822 get ObservationUnitData with IsDerivedTrait to avoid go to the db again
			this.resolveObservationStatus(variableId, phenotype);
			phenotype.setChanged(true); // to set out-of-sync
		}

		final Phenotype savedRecord = this.daoFactory.getPhenotypeDAO().save(phenotype);
		observation.setObservationId(savedRecord.getPhenotypeId());
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		observation.setCreatedDate(dateFormat.format(savedRecord.getCreatedDate()));
		observation.setUpdatedDate(dateFormat.format(savedRecord.getUpdatedDate()));
		observation.setStatus(savedRecord.getValueStatus() != null ? savedRecord.getValueStatus().getName() : null);

		return phenotype;
	}

	public void setStudyService(final StudyService studyService) {
		this.studyService = studyService;
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	@Override
	public Map<String, Long> countObservationsGroupedByInstance(final Integer datasetId) {
		return this.daoFactory.getExperimentDao().countObservationsPerInstance(datasetId);
	}

	@Override
	public List<InstanceDetailsDTO> getInstanceDetails(final Integer datasetId, final Integer studyId) {
		return this.daoFactory.getExperimentDao().getInstanceInformation(datasetId, studyId);
	}

	@Override
	public FilteredPhenotypesInstancesCountDTO countFilteredInstancesAndPhenotypes(
		final Integer datasetId, final ObservationUnitsSearchDTO filter) {
		return this.daoFactory.getObservationUnitsSearchDAO().countFilteredInstancesAndPhenotypes(datasetId, filter);
	}
}
