package org.generationcp.middleware.service.impl.dataset;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
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
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.ObservationUnitIDGenerator;
import org.generationcp.middleware.service.api.PedigreeService;
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
import org.generationcp.middleware.service.impl.study.StudyEntryDescriptorColumns;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by clarysabel on 10/22/18.
 */
@Transactional
public class DatasetServiceImpl implements DatasetService {

	public static final String DATE_FORMAT = "YYYYMMDD HH:MM:SS";

	private static final String LOCATION_NAME = "LOCATION_NAME";

	private static final List<Integer> SUBOBS_COLUMNS_ALL_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.GERMPLASM_DESCRIPTOR.getId(),
		VariableType.ENTRY_DETAIL.getId(),//
		VariableType.TRAIT.getId(),
		VariableType.SELECTION_METHOD.getId(),
		VariableType.OBSERVATION_UNIT.getId());

	private static final List<Integer> PLOT_COLUMNS_ALL_VARIABLE_TYPES = Lists.newArrayList( //
		VariableType.GERMPLASM_DESCRIPTOR.getId(),
		VariableType.ENTRY_DETAIL.getId(),//
		VariableType.EXPERIMENTAL_DESIGN.getId(), //
		VariableType.TREATMENT_FACTOR.getId(), //
		VariableType.OBSERVATION_UNIT.getId(), //
		VariableType.TRAIT.getId(), //
		VariableType.SELECTION_METHOD.getId());

	private static final List<Integer> PLOT_COLUMNS_FACTOR_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.GERMPLASM_DESCRIPTOR.getId(),
		VariableType.ENTRY_DETAIL.getId(),//
		VariableType.EXPERIMENTAL_DESIGN.getId(),
		VariableType.TREATMENT_FACTOR.getId(),
		VariableType.OBSERVATION_UNIT.getId());

	protected static final List<Integer> ENVIRONMENT_DATASET_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.ENVIRONMENT_DETAIL.getId(),
		VariableType.ENVIRONMENT_CONDITION.getId());

	protected static final List<Integer> OBSERVATION_DATASET_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.OBSERVATION_UNIT.getId(),
		VariableType.TRAIT.getId(),
		VariableType.SELECTION_METHOD.getId(),
		VariableType.GERMPLASM_DESCRIPTOR.getId(),
		VariableType.ENTRY_DETAIL.getId())//
	;

	protected static final List<Integer> MEANS_DATASET_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.ENVIRONMENT_DETAIL.getId(),
		VariableType.GERMPLASM_DESCRIPTOR.getId(),
		VariableType.ANALYSIS_SUMMARY.getId());

	protected static final List<Integer> SUMMARY_STATISTICS_DATASET_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.ENVIRONMENT_DETAIL.getId(),
		VariableType.ANALYSIS_SUMMARY.getId());

	protected static final List<Integer> MEASUREMENT_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.TRAIT.getId(),
		VariableType.SELECTION_METHOD.getId());

	protected static final List<Integer> MEANS_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.GERMPLASM_DESCRIPTOR.getId(),
		VariableType.ENTRY_DETAIL.getId(),
		VariableType.ANALYSIS.getId(),
		VariableType.TRAIT.getId());

	private static final List<Integer> STANDARD_ENVIRONMENT_FACTORS = Lists.newArrayList(
		TermId.LOCATION_ID.getId(),
		TermId.TRIAL_INSTANCE_FACTOR.getId(),
		TermId.EXPERIMENT_DESIGN_FACTOR.getId());
	private static final String SUM_OF_SAMPLES = "SUM_OF_SAMPLES";

	private DaoFactory daoFactory;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private ProgramService programService;

	@Autowired
	private StudyService studyService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private DerivedVariableService derivedVariableService;

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	@Autowired
	private PedigreeDataManager pedigreeDataManager;

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
	public List<MeasurementVariable> getObservationSetColumns(final Integer studyId, final Integer observationSetId,
		final Boolean draftMode) {
		final DatasetDTO datasetDTO = this.getDataset(observationSetId);
		// Analysis Summary Variables
		if (datasetDTO.getDatasetTypeId().equals(DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId())) {
			final List<MeasurementVariable> columns = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(observationSetId,
				Collections.singletonList(VariableType.ANALYSIS_SUMMARY.getId()));
			// Sort by TermId to group related summary statics variables together
			columns.sort(Comparator.comparing(MeasurementVariable::getTermId));
			this.addVariableColumn(studyId, columns, TermId.LOCATION_ID.getId());
			//Set alias for LOCATION_ID to LOCATION_NAME
			columns.get(0).setAlias(LOCATION_NAME);
			this.addVariableColumn(studyId, columns, TermId.TRIAL_INSTANCE_FACTOR.getId());
			return columns;
		} else if (datasetDTO.getDatasetTypeId().equals(DatasetTypeEnum.MEANS_DATA.getId())) {
			final List<MeasurementVariable> columns = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(observationSetId,
				MEANS_VARIABLE_TYPES);
			this.addVariableColumn(studyId, columns, TermId.TRIAL_INSTANCE_FACTOR.getId());
			return columns;
		} else {
			return this.getObservationsColumns(datasetDTO, studyId, draftMode);
		}
	}

	private List<MeasurementVariable> getObservationsColumns(final DatasetDTO datasetDTO, final Integer studyId, final Boolean draftMode) {
		// TODO get plot dataset even if subobs is not a direct descendant (ie. sub-sub-obs)
		final Supplier<Integer> observationSetIdSupplier;
		final boolean addStockIdColumn;
		final Integer observationSetId = datasetDTO.getDatasetId();
		if (datasetDTO.getDatasetTypeId().equals(DatasetTypeEnum.PLOT_DATA.getId())) {
			//PLOTDATA
			observationSetIdSupplier = () -> observationSetId;
			//STOCK ID
			addStockIdColumn = this.shouldAddStockIdColumn(studyId);
		} else {
			//SUBOBS
			final DmsProject plotDataset = this.daoFactory.getDmsProjectDAO().getById(observationSetId).getParent();
			// TODO get immediate parent columns
			// (ie. Plot subdivided into plant and then into fruits, then immediate parent column would be PLANT_NO)
			observationSetIdSupplier = () -> plotDataset.getProjectId();
			addStockIdColumn = false;
		}

		final List<MeasurementVariable> descriptors = new ArrayList<>();
		final Map<Integer, MeasurementVariable> entryDetails = new LinkedHashMap<>();
		final List<MeasurementVariable> otherVariables = new ArrayList<>();
		this.daoFactory.getDmsProjectDAO().getObservationSetVariables(observationSetIdSupplier.get(), PLOT_COLUMNS_FACTOR_VARIABLE_TYPES)
			.forEach(variable -> {
				if (VariableType.GERMPLASM_DESCRIPTOR == variable.getVariableType()) {
					descriptors.add(variable);
				} else if (VariableType.ENTRY_DETAIL == variable.getVariableType()) {
					entryDetails.put(variable.getTermId(), variable);
				} else {
					otherVariables.add(variable);
				}
			});

		final List<MeasurementVariable> sortedColumns = new ArrayList<>();
		if (entryDetails.containsKey(TermId.ENTRY_NO.getId())) {
			sortedColumns.add(entryDetails.remove(TermId.ENTRY_NO.getId()));
		}
		if (entryDetails.containsKey(TermId.ENTRY_TYPE.getId())) {
			sortedColumns.add(entryDetails.remove(TermId.ENTRY_TYPE.getId()));
		}

		descriptors.sort(Comparator.comparing(descriptor -> StudyEntryDescriptorColumns.getRankByTermId(descriptor.getTermId())));
		sortedColumns.addAll(descriptors);

		if (addStockIdColumn) {
			sortedColumns.add(this.addTermIdColumn(TermId.STOCK_ID, VariableType.GERMPLASM_DESCRIPTOR, null, true));
		}

		sortedColumns.addAll(otherVariables);

		// Virtual columns
		if (this.daoFactory.getSampleDao().countByDatasetId(observationSetId) > 0) {
			// Set the the variable name of this virtual Sample Column to SUM_OF_SAMPLES, to match
			// the Sample field name in observation query.
			sortedColumns.add(this.addTermIdColumn(TermId.SAMPLES, null, SUM_OF_SAMPLES, true));
		}

		sortedColumns.addAll(entryDetails.values());

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
				Lists.newArrayList(Iterables.filter(variateColumns, input -> pendingVariableIds.contains(input.getTermId())
					|| VariableType.OBSERVATION_UNIT.equals(input.getVariableType())
				));
		}

		sortedColumns.addAll(variateColumns);
		this.addVariableColumn(studyId, sortedColumns, TermId.TRIAL_INSTANCE_FACTOR.getId());
		return sortedColumns;
	}

	private void addVariableColumn(final Integer studyId, final List<MeasurementVariable> sortedColumns, final Integer termId) {
		final DmsProject environmentDataset =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);
		final CVTerm cvTerm = this.daoFactory.getCvTermDao().getById(termId);
		final Optional<ProjectProperty> variableAlias =
			this.daoFactory.getProjectPropertyDAO().getByProjectId(environmentDataset.getProjectId()).stream()
				.filter(prop -> termId == prop.getVariableId()).findFirst();

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setName(cvTerm.getName());
		measurementVariable.setAlias(variableAlias.isPresent() ? variableAlias.get().getAlias() : cvTerm.getName());
		measurementVariable.setTermId(termId);
		measurementVariable.setFactor(true);
		sortedColumns.add(0, measurementVariable);
	}

	private MeasurementVariable addTermIdColumn(final TermId termId, final VariableType variableType, final String name,
		final boolean factor) {
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setName(StringUtils.isBlank(name) ? termId.name() : name);
		measurementVariable.setAlias(termId.name());
		measurementVariable.setTermId(termId.getId());
		measurementVariable.setVariableType(variableType);
		measurementVariable.setFactor(factor);
		return measurementVariable;
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
		final CropType crop = this.programService.getProjectByUuid(dmsProject.getProgramUUID()).getCropType();
		for (final ExperimentModel plotObservationUnit : plotObservationUnits) {
			for (int i = 1; i <= numberOfSubObservationUnits; i++) {
				final ExperimentModel experimentModel = new ExperimentModel(plotObservationUnit.getGeoLocation(),
					plotObservationUnit.getTypeId(), subObservationDataset, plotObservationUnit.getStock(), plotObservationUnit, i);
				ObservationUnitIDGenerator.generateObservationUnitIds(crop, Arrays.asList(experimentModel));
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
	public void removeDatasetVariables(final Integer studyId, final Integer datasetId, final List<Integer> variableIds) {
		this.daoFactory.getProjectPropertyDAO().deleteProjectVariables(datasetId, variableIds);
		this.daoFactory.getPhenotypeDAO().deletePhenotypesByProjectIdAndVariableIds(datasetId, variableIds);
		this.daoFactory.getStockDao().deleteStocksForStudyAndVariable(studyId, variableIds);
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
			final List<Integer> targetVariableIds = Lists.transform(formulaList, formula -> formula.getTargetCVTerm().getCvTermId());
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
			final List<Integer> targetVariableIds = Lists.transform(formulaList, formula -> formula.getTargetCVTerm().getCvTermId());
			this.daoFactory.getPhenotypeDAO()
				.updateOutOfSyncPhenotypesByGeolocation(geolocation, Sets.newHashSet(targetVariableIds));
		}
	}

	@Override
	public DatasetDTO getDataset(final Integer datasetId) {
		final DatasetDTO datasetDTO = this.daoFactory.getDmsProjectDAO().getDataset(datasetId);
		if (datasetDTO != null) {
			datasetDTO.setInstances(this.daoFactory.getDmsProjectDAO().getDatasetInstances(datasetId));
			final List<Integer> variableTypes = this.resolveVariableTypes(datasetDTO.getDatasetTypeId());
			datasetDTO.setVariables(
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, variableTypes));
			datasetDTO.setHasPendingData(this.daoFactory.getPhenotypeDAO().countPendingDataOfDataset(datasetId) > 0);
			datasetDTO.setHasOutOfSyncData(this.daoFactory.getPhenotypeDAO().hasOutOfSync(datasetId));
		}

		return datasetDTO;
	}

	private List<Integer> resolveVariableTypes(final Integer datasetTypeId) {
		if (DatasetTypeEnum.SUMMARY_DATA.getId() == datasetTypeId.intValue()) {
			return DatasetServiceImpl.ENVIRONMENT_DATASET_VARIABLE_TYPES;
		} else if (DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId() == datasetTypeId.intValue()) {
			return DatasetServiceImpl.SUMMARY_STATISTICS_DATASET_VARIABLE_TYPES;
		} else if (DatasetTypeEnum.MEANS_DATA.getId() == datasetTypeId.intValue()) {
			return DatasetServiceImpl.MEANS_DATASET_VARIABLE_TYPES;
		}
		return DatasetServiceImpl.OBSERVATION_DATASET_VARIABLE_TYPES;
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

		this.updateSearchDto(studyId, datasetId, searchDTO);
		// FIXME: It was implemented pre-filters to FEMALE and MALE Parents by NAME or GID.
		//  Is need a workaround solution to implement filters into the query if possible.
		if(searchDTO.getFilter() != null){
			this.addPreFilteredGids(searchDTO.getFilter());
		}
		final List<ObservationUnitRow> list = this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitTable(searchDTO, pageable);

		if (searchDTO.getGenericGermplasmDescriptors().stream().anyMatch(this::hasParentGermplasmDescriptors)) {
			final Set<Integer> gids = list.stream().map(s -> s.getGid()).collect(Collectors.toSet());
			this.addParentsFromPedigreeTable(gids, list);
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getObservationUnitRowsAsMapList(
		final int studyId, final int datasetId, final ObservationUnitsSearchDTO searchDTO, final Pageable pageable) {

		this.updateSearchDto(studyId, datasetId, searchDTO);
		// FIXME: It was implemented pre-filters to FEMALE and MALE Parents by NAME or GID.
		//  Is need a workaround solution to implement filters into the query if possible.
		if(searchDTO.getFilter() != null){
			this.addPreFilteredGids(searchDTO.getFilter());
		}
		return this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitTableMapList(searchDTO, pageable);
	}

	private void updateSearchDto(final int studyId, final int datasetId, final ObservationUnitsSearchDTO searchDTO) {
		searchDTO.setDatasetId(datasetId);
		final Map<Integer, String> germplasmDescriptors = this.studyService.getGenericGermplasmDescriptors(studyId);
		searchDTO.setGenericGermplasmDescriptors(Lists.newArrayList(germplasmDescriptors.values()));
		final Map<Integer, String> designFactors = this.studyService.getAdditionalDesignFactors(studyId);
		searchDTO.setAdditionalDesignFactors(Lists.newArrayList(designFactors.values()));

		final DmsProject project = this.daoFactory.getDmsProjectDAO().getById(datasetId);
		final int plotDatasetId = (DatasetTypeEnum.PLOT_DATA.getId() == project.getDatasetType().getDatasetTypeId()
			|| DatasetTypeEnum.ANALYSIS_RESULTS_DATASET_IDS.contains(project.getDatasetType().getDatasetTypeId()))
			?  datasetId : project.getParent().getProjectId();
		final List<MeasurementVariableDto> entryDetails =
			this.daoFactory.getProjectPropertyDAO().getVariablesForDataset(plotDatasetId,
				VariableType.ENTRY_DETAIL.getId());
		searchDTO.setEntryDetails(entryDetails);


		if (project.getDatasetType().getDatasetTypeId() == DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId()) {
			searchDTO.setDatasetVariables(this.daoFactory.getProjectPropertyDAO().getVariablesForDataset(datasetId,
				VariableType.ANALYSIS_SUMMARY.getId()));
		} else if (project.getDatasetType().getDatasetTypeId() == DatasetTypeEnum.MEANS_DATA.getId()) {
			searchDTO.setDatasetVariables(this.daoFactory.getProjectPropertyDAO().getVariablesForDataset(datasetId,
				VariableType.TRAIT.getId(), VariableType.ANALYSIS.getId()));
		} else {
			//for Plot and Subobservation datasets
			searchDTO.setDatasetVariables(this.daoFactory.getProjectPropertyDAO().getVariablesForDataset(datasetId,
				VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId()));
		}
	}

	@Override
	public List<ObservationUnitRow> getAllObservationUnitRows(final int studyId, final int datasetId) {
		final DmsProject environmentDataset =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);
		final List<MeasurementVariable> studyVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(
			studyId,
			Lists.newArrayList(VariableType.STUDY_DETAIL.getId()));

		final ObservationUnitsSearchDTO searchDTO = new ObservationUnitsSearchDTO();
		searchDTO.setDatasetId(datasetId);
		searchDTO.setEnvironmentDetails(this.findAdditionalEnvironmentFactors(environmentDataset.getProjectId()));
		searchDTO.setEnvironmentConditions(this.getEnvironmentConditionVariableNames(environmentDataset.getProjectId()));
		searchDTO.setEnvironmentDatasetId(environmentDataset.getProjectId());
		this.updateSearchDto(studyId, datasetId, searchDTO);

		final List<ObservationUnitRow> observationUnits =
			this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitTable(searchDTO, new PageRequest(0, Integer.MAX_VALUE));
		this.addStudyVariablesToUnitRows(observationUnits, studyVariables);

		if (searchDTO.getGenericGermplasmDescriptors().stream().anyMatch(this::hasParentGermplasmDescriptors)) {
			final Set<Integer> gids = observationUnits.stream().map(s -> s.getGid()).collect(Collectors.toSet());
			this.addParentsFromPedigreeTable(gids, observationUnits);
		}
		return observationUnits;
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
		// FIXME: It was implemented pre-filters to FEMALE and MALE Parents by NAME or GID.
		//  Is need a workaround solution to implement filters into the query if possible.
		if(filter != null){
			this.addPreFilteredGids(filter);
		}
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
				final Collection<Phenotype> selectedPhenotypes = CollectionUtils.select(phenotypes, o -> {
					final Phenotype phenotype = (Phenotype) o;
					return phenotype.getObservableId().equals(measurementVariable.getTermId());
				});

				Collection<Phenotype> possibleValues = null;
				if (measurementVariable.getPossibleValues() != null && !measurementVariable.getPossibleValues().isEmpty()) {
					possibleValues =
						CollectionUtils.collect(measurementVariable.getPossibleValues(), input -> {
							final ValueReference variable = (ValueReference) input;
							return variable.getName();
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
				final Collection<Phenotype> selectedPhenotypes = CollectionUtils.select(draftPhenotypes, o -> {
					final Phenotype phenotype = (Phenotype) o;
					return phenotype.getObservableId().equals(measurementVariable.getTermId());
				});

				Collection<Phenotype> possibleValues = null;
				if (measurementVariable.getPossibleValues() != null && !measurementVariable.getPossibleValues().isEmpty()) {
					possibleValues =
						CollectionUtils.collect(measurementVariable.getPossibleValues(), input -> {
							final ValueReference variable = (ValueReference) input;
							return variable.getName();
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
		this.updateSearchDto(studyId, datasetId, searchDTO);
		// FIXME: It was implemented pre-filters to FEMALE and MALE Parents by NAME or GID.
		//  Is need a workaround solution to implement filters into the query if possible.
		if(searchDTO.getFilter() != null){
			this.addPreFilteredGids(searchDTO.getFilter());
		}

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
		if(paramDTO.getObservationUnitsSearchDTO().getFilter() != null){
			this.addPreFilteredGids(paramDTO.getObservationUnitsSearchDTO().getFilter());
		}
		this.updateSearchDto(studyId, datasetId, paramDTO.getObservationUnitsSearchDTO());
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
			final Map<String, MeasurementVariable> mappedVariables = new HashMap<>();
			measurementVariableList.forEach(measurementVariable -> {
				mappedVariables.putIfAbsent(measurementVariable.getName(), measurementVariable);
				mappedVariables.putIfAbsent(measurementVariable.getAlias(), measurementVariable);
			});
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

					final MeasurementVariable measurementVariable = mappedVariables.get(variableName);

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
					final Optional<ExperimentModel> experimentModelOptional =
						this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitId.toString());

					final ArrayList<Phenotype> datasetPhenotypes = new ArrayList<>(experimentModelOptional.get().getPhenotypes());
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

		final DmsProject environmentDataset =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);
		final List<MeasurementVariable> studyVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(
			studyId,
			Lists.newArrayList(VariableType.STUDY_DETAIL.getId()));

		for (final Integer instanceId : instanceIds) {
			final ObservationUnitsSearchDTO searchDTO = new ObservationUnitsSearchDTO();
			searchDTO.setDatasetId(datasetId);
			searchDTO.setInstanceId(instanceId);
			searchDTO.setEnvironmentDetails(this.findAdditionalEnvironmentFactors(environmentDataset.getProjectId()));
			searchDTO.setEnvironmentConditions(this.getEnvironmentConditionVariableNames(environmentDataset.getProjectId()));
			searchDTO.setEnvironmentDatasetId(environmentDataset.getProjectId());
			this.updateSearchDto(studyId, datasetId, searchDTO);

			final List<ObservationUnitRow> observationUnits =
				this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitTable(searchDTO, null);
			this.addStudyVariablesToUnitRows(observationUnits, studyVariables);
			instanceMap.put(instanceId, observationUnits);

			if (searchDTO.getGenericGermplasmDescriptors().stream().anyMatch(this::hasParentGermplasmDescriptors)) {
				final Set<Integer> gids = observationUnits.stream().map(s -> s.getGid()).collect(Collectors.toSet());
				this.addParentsFromPedigreeTable(gids, observationUnits);
			}
		}
		return instanceMap;
	}

	@Override
	public void replaceObservationUnitEntry(final List<Integer> observationUnitIds, final Integer newEntryId) {
		this.daoFactory.getExperimentDao().updateEntryId(observationUnitIds, newEntryId);
	}

	@Override
	public Long countObservationUnits(final Integer datasetId) {
		return this.daoFactory.getExperimentDao().count(datasetId);
	}

	@Override
	public long countByVariableIdAndValue(final Integer variableId, final String value) {
		return this.daoFactory.getDmsProjectDAO().countByVariableIdAndValue(variableId, value);
	}

	@Override
	public long countObservationsByVariableIdAndValue(final Integer variableId, final String value) {
		return this.daoFactory.getPhenotypeDAO().countByVariableIdAndValue(variableId, value);
	}

	@Override
	public void updateDatasetProperties(final Integer studyId, final List<Integer> variableIds) {
		final DmsProject plotDataset = this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.PLOT_DATA.getId()).get(0);
		final List<Integer> germplasmDescriptorPropertyIds = plotDataset.getProperties()
			.stream()
			.filter(projectProperty ->
				projectProperty.getTypeId().equals(VariableType.GERMPLASM_DESCRIPTOR.getId()) && !projectProperty.getVariableId()
					.equals(TermId.OBS_UNIT_ID.getId()))
			.map(ProjectProperty::getVariableId)
			.collect(Collectors.toList());

		final List<Integer> newPropertyVariableIds = variableIds.stream()
				.filter(variableId -> !germplasmDescriptorPropertyIds.contains(variableId)).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(newPropertyVariableIds)) {
			final AtomicInteger nextRank =
				new AtomicInteger(this.daoFactory.getProjectPropertyDAO().getNextRank(plotDataset.getProjectId()));

			final VariableFilter variableFilter = new VariableFilter();
			newPropertyVariableIds.forEach(variableFilter::addVariableId);
			final List<Variable> variables = this.ontologyVariableDataManager.getWithFilter(variableFilter);
			final Map<Integer, String> variableAliasByIds = variables.stream()
				.collect(Collectors.toMap(Variable::getId,
					variable -> StringUtils.isEmpty(variable.getAlias()) ? variable.getName() : variable.getAlias()));

			newPropertyVariableIds.forEach(variableId -> {
				final ProjectProperty projectProperty =
					new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), null, nextRank.getAndIncrement(),
						variableId, variableAliasByIds.get(variableId));
				this.daoFactory.getProjectPropertyDAO().save(projectProperty);
			});
		}

		final List<Integer> removeVariableIds = germplasmDescriptorPropertyIds.stream()
			.filter(variableId -> !variableIds.contains(variableId)).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(removeVariableIds)) {
			this.daoFactory.getProjectPropertyDAO().deleteProjectVariables(plotDataset.getProjectId(), removeVariableIds);
		}
	}

	void addStudyVariablesToUnitRows(final List<ObservationUnitRow> observationUnits, final List<MeasurementVariable> studyVariables) {
		for (final ObservationUnitRow observationUnitRow : observationUnits) {
			for (final MeasurementVariable measurementVariable : studyVariables) {
				final String key;
				if (!StringUtils.isEmpty(measurementVariable.getAlias())) {
					key = measurementVariable.getAlias();
				} else {
					key = measurementVariable.getName();
				}
				observationUnitRow.getVariables()
					.put(key, new ObservationUnitData(measurementVariable.getValue()));
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

	public void setRoleService(final RoleService roleService) {
		this.roleService = roleService;
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
		final Integer datasetId, final ObservationUnitsSearchDTO observationUnitsSearchDTO) {
		if(observationUnitsSearchDTO.getFilter() != null){
			this.addPreFilteredGids(observationUnitsSearchDTO.getFilter());
		}
		return this.daoFactory.getObservationUnitsSearchDAO().countFilteredInstancesAndPhenotypes(datasetId, observationUnitsSearchDTO);
	}

	private boolean shouldAddStockIdColumn(final Integer studyId) {
		final TransactionsSearchDto transactionsSearchDto = new TransactionsSearchDto();
		transactionsSearchDto.setTransactionStatus(Arrays.asList(0, 1));
		transactionsSearchDto.setPlantingStudyIds(Arrays.asList(studyId));
		return this.daoFactory.getTransactionDAO().countSearchTransactions(transactionsSearchDto) > 0;
	}

	private void addParentsFromPedigreeTable(final Set<Integer> gids, final List<ObservationUnitRow> list) {
		final Integer level = this.crossExpansionProperties.getCropGenerationLevel(this.pedigreeService.getCropName());
		final com.google.common.collect.Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable =
			this.pedigreeDataManager.generatePedigreeTable(gids, level, false);

		list.forEach(observationUnitRow -> {
			final Integer gid = observationUnitRow.getGid();

			final Optional<Germplasm> femaleParent = pedigreeTreeNodeTable.get(gid, ColumnLabels.FGID.getName());
			femaleParent.ifPresent(value -> {
				final Germplasm germplasm = value;
				observationUnitRow.getVariables().put(
					TermId.FEMALE_PARENT_GID.name(),
					new ObservationUnitData(TermId.FEMALE_PARENT_GID.getId(),
						germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN));
				observationUnitRow.getVariables().put(
					TermId.FEMALE_PARENT_NAME.name(),
					new ObservationUnitData(TermId.FEMALE_PARENT_NAME.getId(), germplasm.getPreferredName().getNval()));
			});

			final Optional<Germplasm> maleParent = pedigreeTreeNodeTable.get(gid, ColumnLabels.MGID.getName());
			if (maleParent.isPresent()) {
				final Germplasm germplasm = maleParent.get();
				observationUnitRow.getVariables().put(
					TermId.MALE_PARENT_GID.name(),
					new ObservationUnitData(TermId.MALE_PARENT_GID.getId(),germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN));
				observationUnitRow.getVariables().put(
					TermId.MALE_PARENT_NAME.name(),
					new ObservationUnitData(TermId.MALE_PARENT_NAME.getId(),germplasm.getPreferredName().getNval()));
			}

		});
	}

	private boolean hasParentGermplasmDescriptors(final String germplasmDescriptor) {
		return TermId.FEMALE_PARENT_GID.name().equals(germplasmDescriptor) ||
			TermId.FEMALE_PARENT_NAME.name().equals(germplasmDescriptor) ||
			TermId.MALE_PARENT_GID.name().equals(germplasmDescriptor) ||
			TermId.MALE_PARENT_NAME.name().equals(germplasmDescriptor);
	}

	private void addPreFilteredGids(final ObservationUnitsSearchDTO.Filter filter) {
		Set<String> textKeys = filter.getFilteredTextValues().keySet();
		if(textKeys.contains(String.valueOf(TermId.FEMALE_PARENT_GID.getId())) ||
			textKeys.contains(String.valueOf(TermId.FEMALE_PARENT_NAME.getId())) ||
			textKeys.contains(String.valueOf(TermId.MALE_PARENT_GID.getId())) ||
			textKeys.contains(String.valueOf(TermId.MALE_PARENT_NAME.getId()))
		){
			filter.setPreFilteredGids(this.daoFactory.getObservationUnitsSearchDAO().addPreFilteredGids(filter));
		}
	}
}
