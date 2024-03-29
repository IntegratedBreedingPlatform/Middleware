package org.generationcp.middleware.service.impl.dataset;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.location.LocationDTO;
import org.generationcp.middleware.api.location.LocationService;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeDTO;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dataset.PlotDatasetPropertiesDTO;
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
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
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
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.service.impl.study.StudyEntryGermplasmDescriptorColumns;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.service.impl.study.StudyInstanceServiceImpl;
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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by clarysabel on 10/22/18.
 */
@Transactional
public class DatasetServiceImpl implements DatasetService {

	public static final String DATE_FORMAT = "YYYYMMDD HH:MM:SS";

	private static final String LOCATION_NAME = "LOCATION_NAME";
	private static final String LOCATION_ID = "LOCATION_ID";
	private static final String OBS_UNIT_ID = "OBS_UNIT_ID";

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
		VariableType.SELECTION_METHOD.getId(),
		VariableType.GERMPLASM_ATTRIBUTE.getId(),
		VariableType.GERMPLASM_PASSPORT.getId());

	private static final List<Integer> PLOT_COLUMNS_FACTOR_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.GERMPLASM_DESCRIPTOR.getId(),
		VariableType.ENTRY_DETAIL.getId(),//
		VariableType.EXPERIMENTAL_DESIGN.getId(),
		VariableType.TREATMENT_FACTOR.getId(),
		VariableType.OBSERVATION_UNIT.getId(),
		VariableType.GERMPLASM_ATTRIBUTE.getId(),
		VariableType.GERMPLASM_PASSPORT.getId());

	public static final List<Integer> ENVIRONMENT_DATASET_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.ENVIRONMENT_DETAIL.getId(),
		VariableType.ENVIRONMENT_CONDITION.getId());

	public static final List<Integer> OBSERVATION_DATASET_VARIABLE_TYPES = Lists.newArrayList(
		VariableType.OBSERVATION_UNIT.getId(),
		VariableType.TRAIT.getId(),
		VariableType.SELECTION_METHOD.getId(),
		VariableType.GERMPLASM_DESCRIPTOR.getId(),
		VariableType.ENTRY_DETAIL.getId());

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

	private static final List<Integer> EXP_DESIGN_VARIABLES =
			Arrays.asList(8170, 8135, 8131, 8842, 8132, 8133, 8134, 8136, 8137, 8138, 8139, 8142, 8165, 8831, 8411, 8412, 8413);

	private DaoFactory daoFactory;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private OntologyVariableService ontologyVariableService;

	@Autowired
	private ProgramService programService;

	@Autowired
	private StudyService studyService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private DerivedVariableService derivedVariableService;

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	@Autowired
	private PedigreeDataManager pedigreeDataManager;

	@Autowired
	private LocationService locationService;

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
		final DatasetDTO datasetDTO = this.daoFactory.getDmsProjectDAO().getDataset(observationSetId);

		// Analysis Summary Variables
		if (DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId() == datasetDTO.getDatasetTypeId()) {
			final List<MeasurementVariable> columns = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(observationSetId,
				Collections.singletonList(VariableType.ANALYSIS_SUMMARY.getId()));
			// Sort by TermId to group related summary statics variables together
			columns.sort(Comparator.comparing(MeasurementVariable::getTermId));
			this.addVariableColumn(studyId, columns, TermId.LOCATION_ID.getId(), 0);
			//Set alias for LOCATION_ID to LOCATION_NAME
			columns.get(0).setAlias(LOCATION_NAME);
			this.addVariableColumn(studyId, columns, TermId.TRIAL_INSTANCE_FACTOR.getId(), 0);
			return columns;
		} else if (DatasetTypeEnum.MEANS_DATA.getId() == datasetDTO.getDatasetTypeId()) {
			final List<MeasurementVariable> columns = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(observationSetId,
				MEANS_VARIABLE_TYPES);
			this.addVariableColumn(studyId, columns, TermId.OBS_UNIT_ID.getId(), 2);
			this.addVariableColumn(studyId, columns, TermId.TRIAL_INSTANCE_FACTOR.getId(), 0);
			return columns;
		} else if (DatasetTypeEnum.SUMMARY_DATA.getId() == datasetDTO.getDatasetTypeId()) {
			return this.daoFactory.getDmsProjectDAO().getObservationSetVariables(observationSetId, ENVIRONMENT_DATASET_VARIABLE_TYPES);
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
		final List<MeasurementVariable> attributes = new ArrayList<>();
		final List<MeasurementVariable> passports = new ArrayList<>();
		final Map<Integer, MeasurementVariable> entryDetails = new LinkedHashMap<>();
		final List<MeasurementVariable> otherVariables = new ArrayList<>();

		this.daoFactory.getDmsProjectDAO().getObservationSetVariables(observationSetIdSupplier.get(), PLOT_COLUMNS_FACTOR_VARIABLE_TYPES)
			.forEach(variable -> {
				if (VariableType.GERMPLASM_DESCRIPTOR == variable.getVariableType()) {
					descriptors.add(variable);
				} else if (VariableType.GERMPLASM_ATTRIBUTE == variable.getVariableType()) {
					attributes.add(variable);
				} else if (VariableType.GERMPLASM_PASSPORT == variable.getVariableType()) {
					passports.add(variable);
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

		descriptors.sort(Comparator.comparing(descriptor -> StudyEntryGermplasmDescriptorColumns.getRankByTermId(descriptor.getTermId())));
		sortedColumns.addAll(descriptors);

		if (addStockIdColumn) {
			sortedColumns.add(this.addTermIdColumn(TermId.STOCK_ID, VariableType.GERMPLASM_DESCRIPTOR, null, true));
		}

		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.getDatasetNameTypes(observationSetIdSupplier.get());
		germplasmNameTypeDTOs.sort(Comparator.comparing(GermplasmNameTypeDTO::getCode));
		sortedColumns.addAll(germplasmNameTypeDTOs.stream().map(germplasmNameTypeDTO ->
				new MeasurementVariable(germplasmNameTypeDTO.getCode(), germplasmNameTypeDTO.getDescription(), germplasmNameTypeDTO.getId(),
					null,
					germplasmNameTypeDTO.getCode(), true)) //
			.collect(Collectors.toSet()));

		passports.sort(Comparator.comparing(MeasurementVariable::getName));
		sortedColumns.addAll(passports);
		attributes.sort(Comparator.comparing(MeasurementVariable::getName));
		sortedColumns.addAll(attributes);

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
		this.addVariableColumn(studyId, sortedColumns, TermId.TRIAL_INSTANCE_FACTOR.getId(), 0);
		return sortedColumns;
	}

	private void addVariableColumn(final Integer studyId, final List<MeasurementVariable> sortedColumns, final Integer termId,
		final int positionColumn) {
		final DmsProject environmentDataset =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);
		final CVTerm cvTerm = this.daoFactory.getCvTermDao().getById(termId);
		final Optional<ProjectProperty> variableAlias =
			this.daoFactory.getProjectPropertyDAO().getByProjectId(environmentDataset.getProjectId()).stream()
				.filter(prop -> termId == prop.getVariableId()).findFirst();

		final Multimap<Integer, VariableType> variableTypeMultimap =
			this.ontologyVariableService.getVariableTypesOfVariables(Arrays.asList(termId));

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setName(cvTerm.getName());
		measurementVariable.setAlias(variableAlias.isPresent() ? variableAlias.get().getAlias() : cvTerm.getName());
		measurementVariable.setTermId(termId);
		measurementVariable.setFactor(true);
		measurementVariable.setVariableType(variableTypeMultimap.get(termId).iterator().next());
		sortedColumns.add(positionColumn, measurementVariable);
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
		this.filterDatasets(datasetDTOList, studyId, datasetTypeIds, false);
		return datasetDTOList;
	}

	@Override
	public List<DatasetDTO> getDatasetsWithVariables(final Integer studyId, final Set<Integer> datasetTypeIds) {
		final List<DatasetDTO> datasetDTOList = new ArrayList<>();
		this.filterDatasets(datasetDTOList, studyId, datasetTypeIds, true);
		return datasetDTOList;
	}

	private void filterDatasets(final List<DatasetDTO> filtered, final Integer parentId, final Set<Integer> datasetTypeIds,
		final boolean addVariables) {

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
				if (addVariables) {
					final List<Integer> variableTypes = this.resolveVariableTypes(datasetDTO.getDatasetTypeId());
					final List<MeasurementVariable> variables = this.daoFactory.getDmsProjectDAO()
						.getObservationSetVariables(datasetId, variableTypes);
					datasetDTO.setVariables(variables);
				}
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
		this.daoFactory.getPhenotypeExternalReferenceDAO().deleteByProjectIdAndVariableIds(datasetId, variableIds);
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
		final Integer loggedInUser = this.userService.getCurrentlyLoggedInUserId();

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

		final Integer observableId = phenotype.getObservableId();
		this.resolveObservationStatus(variableId, phenotype);

		final Phenotype savedRecord = this.daoFactory.getPhenotypeDAO().save(phenotype);

		// Also update the status of phenotypes of the same observation unit for variables using it as input variable
		this.updateDependentPhenotypesAsOutOfSync(observableId, Sets.newHashSet(observationUnitId));

		this.alignObservationValues(observation, savedRecord);

		return observation;
	}

	private void alignObservationValues(final ObservationDto observation, final Phenotype savedRecord) {
		observation.setObservationId(savedRecord.getPhenotypeId());
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		observation.setCreatedDate(dateFormat.format(savedRecord.getCreatedDate()));
		observation.setUpdatedDate(dateFormat.format(savedRecord.getUpdatedDate()));
		observation.setStatus(savedRecord.getValueStatus() != null ? savedRecord.getValueStatus().getName() : null);
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

		phenotype.setUpdatedDate(new Date());
		phenotype.setUpdatedBy(this.userService.getCurrentlyLoggedInUserId());
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
				.updateOutOfSyncPhenotypes(observationUnitIds, Sets.newHashSet(targetVariableIds),
					this.userService.getCurrentlyLoggedInUserId());
		}

	}

	@Override
	public void updateOutOfSyncPhenotypes(final Set<Integer> targetVariableIds, final Set<Integer> observationUnitIds) {
		if (!targetVariableIds.isEmpty()) {
			this.daoFactory.getPhenotypeDAO().updateOutOfSyncPhenotypes(observationUnitIds, targetVariableIds,
				this.userService.getCurrentlyLoggedInUserId());
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
		this.addPreFilteredGids(searchDTO.getFilter());

		// TODO: fix me! This is a workaround until the implementation of this ticket https://ibplatform.atlassian.net/browse/IBP-5837
		final DmsProject dataset = this.daoFactory.getDmsProjectDAO().getById(datasetId);
		// There is no need of germplasm descriptors data if it's a summary statistics dataset
		if (DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId() == dataset.getDatasetType().getDatasetTypeId()) {
			searchDTO.setGenericGermplasmDescriptors(new ArrayList<>());
		}

		final List<ObservationUnitRow> list = this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitTable(searchDTO, pageable);
		if (searchDTO.getGenericGermplasmDescriptors().stream().anyMatch(this::hasParentGermplasmDescriptors)) {
			final Set<Integer> gids =
				list.stream().filter(s -> s.getGid() != null).map(ObservationUnitRow::getGid).collect(Collectors.toSet());
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
		this.addPreFilteredGids(searchDTO.getFilter());
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
			? datasetId : project.getParent().getProjectId();
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

			final Integer observationSetId;
			if (DatasetTypeEnum.PLOT_DATA.getId() == project.getDatasetType().getDatasetTypeId()) {
				observationSetId = datasetId;
			} else {
				final DmsProject plotDataset = this.daoFactory.getDmsProjectDAO().getById(datasetId).getParent();
				observationSetId = plotDataset.getProjectId();
			}

			final List<MeasurementVariableDto> passportAndAttributes =
				this.daoFactory.getProjectPropertyDAO().getVariablesForDataset(observationSetId,
					VariableType.GERMPLASM_ATTRIBUTE.getId(), VariableType.GERMPLASM_PASSPORT.getId());
			searchDTO.setPassportAndAttributes(passportAndAttributes);

			final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.getDatasetNameTypes(observationSetId);

			searchDTO.setNameTypes(germplasmNameTypeDTOs.stream()
				.map(germplasmNameTypeDTO ->
					new MeasurementVariableDto(germplasmNameTypeDTO.getId(), germplasmNameTypeDTO.getCode()))
				.collect(Collectors.toList()));
		}
	}

	@Override
	public List<ObservationUnitRow> getAllObservationUnitRows(final int studyId, final int datasetId, final Set<String> visibleColumns) {
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
		searchDTO.setVisibleColumns(visibleColumns);
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
		final Integer datasetId, final List<Integer> instanceIds, final Boolean draftMode) {
		return this.daoFactory.getObservationUnitsSearchDAO().countObservationUnitsForDataset(datasetId, instanceIds, draftMode, null);
	}

	@Override
	public long countFilteredObservationUnitsForDataset(
		final Integer datasetId, final List<Integer> instanceIds, final Boolean draftMode,
		final ObservationUnitsSearchDTO.Filter filter) {
		// FIXME: It was implemented pre-filters to FEMALE and MALE Parents by NAME or GID.
		//  Is need a workaround solution to implement filters into the query if possible.
		this.addPreFilteredGids(filter);
		return this.daoFactory.getObservationUnitsSearchDAO().countObservationUnitsForDataset(datasetId, instanceIds, draftMode, filter);
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
	public void rejectDatasetDraftData(final Integer datasetId, final Set<Integer> instanceIds) {
		final List<Phenotype> phenotypes = this.daoFactory.getPhenotypeDAO().getDatasetDraftData(datasetId, instanceIds);
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

		final List<Phenotype> phenotypes = this.daoFactory.getPhenotypeDAO().getDatasetDraftData(datasetId, new HashSet<>());

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
	public void acceptDatasetDraftData(final Integer studyId, final Integer datasetId, final Set<Integer> instanceIds) {

		final List<Phenotype> draftPhenotypes = this.daoFactory.getPhenotypeDAO().getDatasetDraftData(datasetId, instanceIds);

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
	public void acceptDraftDataAndSetOutOfBoundsToMissing(final Integer studyId, final Integer datasetId, final Set<Integer> instanceIds) {
		final List<Phenotype> draftPhenotypes = this.daoFactory.getPhenotypeDAO().getDatasetDraftData(datasetId, instanceIds);

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
		this.addPreFilteredGids(searchDTO.getFilter());

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
		final Integer newCategoricalValueId = paramDTO.getNewCategoricalValueId();
		final String variableId = paramDTO.getObservationUnitsSearchDTO().getFilter().getVariableId().toString();
		final Boolean draftMode = paramDTO.getObservationUnitsSearchDTO().getDraftMode();

		final List<ObservationUnitRow> observationUnitsByVariable =
			this.getObservationUnitsByVariable(datasetId, paramDTO.getObservationUnitsSearchDTO(), studyId);

		final List<Phenotype> phenotypes = new ArrayList<>();
		final List<Integer> phenotypeIdsToUpdate = new ArrayList<>();
		final Set<Integer> observationUnitIdsToUpdate = new HashSet<>();
		if (!observationUnitsByVariable.isEmpty()) {
			for (final ObservationUnitRow observationUnitRow : observationUnitsByVariable) {
				final ObservationUnitData observationUnitData = observationUnitRow.getVariables().get(variableId);
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
					phenotypeIdsToUpdate.add(phenotype.getPhenotypeId());
				} else {
					final ObservationDto observationDto =
						new ObservationDto(observationUnitData.getVariableId(), newValue, newCategoricalValueId, null,
							Util.getCurrentDateAsStringValue(), Util.getCurrentDateAsStringValue(),
							observationUnitRow.getObservationUnitId(), newCategoricalValueId, newValue);
					phenotype = this.createPhenotype(observationDto, draftMode);
				}
				phenotypes.add(phenotype);
				observationUnitIdsToUpdate.add(observationUnitRow.getObservationUnitId());
			}
		}

		if (!draftMode && !org.springframework.util.CollectionUtils.isEmpty(observationUnitIdsToUpdate)) {
			this.updateDependentPhenotypesAsOutOfSync(paramDTO.getObservationUnitsSearchDTO().getFilter().getVariableId(),
				observationUnitIdsToUpdate);
		}
		if (!org.springframework.util.CollectionUtils.isEmpty(phenotypeIdsToUpdate)) {
			this.updatePhenotypes(phenotypeIdsToUpdate, paramDTO.getObservationUnitsSearchDTO().getFilter().getVariableId(),
				newCategoricalValueId, newValue, draftMode);
		}
	}

	@Override
	public void deleteVariableValues(final Integer studyId, final Integer datasetId, final ObservationUnitsSearchDTO searchDTO) {
		searchDTO.getFilter().setVariableHasValue(true);

		final String variableId = searchDTO.getFilter().getVariableId().toString();
		final Boolean draftMode = searchDTO.getDraftMode();

		final List<ObservationUnitRow> observationUnitsByVariable =
			this.getObservationUnitsByVariable(datasetId, searchDTO, studyId);

		final List<Integer> phenotypeIdsToDelete = new ArrayList<>();
		final Set<Integer> observationUnitIdsToUpdate = new HashSet<>();
		final List<Integer> phenotypeIdsToUpdate = new ArrayList<>();
		if (!observationUnitsByVariable.isEmpty()) {
			for (final ObservationUnitRow observationUnitRow : observationUnitsByVariable) {
				final ObservationUnitData observationUnitData = observationUnitRow.getVariables().get(variableId);
				Phenotype phenotype = null;

				if (observationUnitData != null) {
					phenotype = this.daoFactory.getPhenotypeDAO().getById(observationUnitData.getObservationId());
				}

				if (phenotype != null) {
					if (draftMode) {
						if (phenotype.getcValueId() == null && StringUtils.isEmpty(phenotype.getValue())) {
							phenotypeIdsToDelete.add(phenotype.getPhenotypeId());
						} else {
							phenotypeIdsToUpdate.add(phenotype.getPhenotypeId());
						}
					} else {
						if (phenotype.getDraftCValueId() == null && StringUtils.isEmpty(phenotype.getDraftValue())) {
							phenotypeIdsToDelete.add(phenotype.getPhenotypeId());
						} else {
							phenotypeIdsToUpdate.add(phenotype.getPhenotypeId());
						}
						observationUnitIdsToUpdate.add(observationUnitRow.getObservationUnitId());
					}
				}
			}

			if (!org.springframework.util.CollectionUtils.isEmpty(phenotypeIdsToDelete)) {
				this.daoFactory.getPhenotypeDAO().deletePhenotypes(phenotypeIdsToDelete);
			}
			if (!org.springframework.util.CollectionUtils.isEmpty(phenotypeIdsToUpdate)) {
				this.updatePhenotypes(phenotypeIdsToUpdate, searchDTO.getFilter().getVariableId(), null, null, draftMode);
			}
			if (!draftMode && !org.springframework.util.CollectionUtils.isEmpty(observationUnitIdsToUpdate)) {
				this.updateDependentPhenotypesAsOutOfSync(searchDTO.getFilter().getVariableId(), observationUnitIdsToUpdate);
			}
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

	private List<ObservationUnitRow> getObservationUnitsByVariable(
		final Integer datasetId, final ObservationUnitsSearchDTO observationUnitsSearchDTO, final Integer studyId) {
		this.addPreFilteredGids(observationUnitsSearchDTO.getFilter());

		this.updateSearchDto(studyId, datasetId, observationUnitsSearchDTO);
		final List<ObservationUnitRow> observationUnitsByVariable =
			this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitsByVariable(observationUnitsSearchDTO);
		return observationUnitsByVariable;
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
	public void importEnvironmentVariableValues(final Integer studyId, final Integer datasetId, final Table<String, String, String> table) {
		final List<MeasurementVariable> measurementVariableList =
				this.getDatasetMeasurementVariablesByVariableType(datasetId,
						Arrays.asList(VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENVIRONMENT_CONDITION.getId()));

		if (!measurementVariableList.isEmpty()) {
			final Map<String, MeasurementVariable> mappedVariables = getMappedVariablesMap(measurementVariableList);

			final Map<String, ObservationUnitRow> trialInstanceMap =
					this.getEnvironmentObservationUnitRows(measurementVariableList, studyId, datasetId);

			final List<String> observationUnitIds = new ArrayList<>();
			final Map<String, String> observationUnitIdTrialInstanceNoMap = new HashMap<>();
			final List<Integer> instanceIds = new ArrayList<>();
			for(final String instanceNo: trialInstanceMap.keySet()) {
				if(MapUtils.isNotEmpty(table.row(instanceNo))) {
					instanceIds.add(trialInstanceMap.get(instanceNo).getInstanceId());
					final String obsUnitId = trialInstanceMap.get(instanceNo).getVariables().get(OBS_UNIT_ID).getValue();
					observationUnitIds.add(obsUnitId);
					observationUnitIdTrialInstanceNoMap.put(obsUnitId, instanceNo);
				}
			}

			final Map<String, Geolocation> geolocationMap = this.daoFactory.getGeolocationDao().findInDataSet(datasetId).stream()
					.collect(Collectors.toMap(geolocation -> geolocation.getDescription(), Function.identity()));

			final Map<String, ObservationUnitRow> currentData =
					this.daoFactory.getExperimentDao().getObservationUnitsAsMap(datasetId, measurementVariableList,
							observationUnitIds);

			final Map<String, Map<Integer, GeolocationProperty>> instanceNoPropertyMap = getInstanceNoPropertyMap(instanceIds);
			final Map<String, Integer> locationNameLocationIdMap = getLocationNameLocationIdMap(table);


			boolean hasGeolocationMetaDataChanges = false;
			for (final String observationUnitId : observationUnitIds) {
				final String trialInstanceNumber = observationUnitIdTrialInstanceNoMap.get(observationUnitId);
				final Set<Phenotype> phenotypes = new HashSet<>();
				final ObservationUnitRow currentRow = currentData.get(observationUnitId);
				final Set<Integer> updatedVariableIds = new HashSet<>();

				for (final String variableName : table.columnKeySet()) {
					final String importedVariableValue = table.get(trialInstanceNumber, variableName);
					// Ignore empty strings and LOCATION_ID, Location value will be set from LOCATION_NAME
					if (StringUtils.isNotBlank(importedVariableValue) && !LOCATION_ID.equalsIgnoreCase(variableName)) {
						final MeasurementVariable measurementVariable = mappedVariables.get(variableName);
						if (StudyInstanceServiceImpl.GEOLOCATION_METADATA.contains(measurementVariable.getTermId())) {
							hasGeolocationMetaDataChanges = true;
							this.mapGeolocationMetaData(geolocationMap.get(trialInstanceNumber), measurementVariable.getTermId(), importedVariableValue, updatedVariableIds);
						} else if (TermId.LOCATION_ID.getId() == measurementVariable.getTermId()) {
							final GeolocationProperty locationProp = instanceNoPropertyMap.get(trialInstanceNumber).get(TermId.LOCATION_ID.getId());
							final String newLoc = locationNameLocationIdMap.get(importedVariableValue.toUpperCase()).toString();
							locationProp.setValue(newLoc);
							if (!newLoc.equals(locationProp.getValue())) {
								updatedVariableIds.add(measurementVariable.getTermId());
							}
							this.daoFactory.getGeolocationPropertyDao().save(locationProp);
						} else if (!EXP_DESIGN_VARIABLES.contains(measurementVariable.getTermId()) && measurementVariable.getVariableType().getId().equals(VariableType.ENVIRONMENT_DETAIL.getId())) {
							this.saveEnvironmentDetailValue(measurementVariable, geolocationMap.get(trialInstanceNumber), instanceNoPropertyMap.get(trialInstanceNumber).get(measurementVariable.getTermId()),
									importedVariableValue, updatedVariableIds);
						} else if (measurementVariable.getVariableType().getId().equals(VariableType.ENVIRONMENT_CONDITION.getId())) {
							// Save environment conditions
							this.saveImportedVariableValue(null, observationUnitId, phenotypes,
									currentRow, importedVariableValue, measurementVariable, false, updatedVariableIds);
						}
					}
				}
				if (CollectionUtils.isNotEmpty(updatedVariableIds)) {
					this.updateDependentPhenotypesStatusByGeolocation(
							geolocationMap.get(trialInstanceNumber).getLocationId(), new ArrayList<>(updatedVariableIds));
				}
			}

			// Save Geolocation metadata changes
			if (hasGeolocationMetaDataChanges) {
				for (final Geolocation geolocation: geolocationMap.values()) {
					this.daoFactory.getGeolocationDao().save(geolocation);
				}
			}
		}
	}

	private static Map<String, MeasurementVariable> getMappedVariablesMap(final List<MeasurementVariable> measurementVariableList) {
		final Map<String, MeasurementVariable> mappedVariables = new HashMap<>();
		measurementVariableList.forEach(measurementVariable -> {
			mappedVariables.putIfAbsent(measurementVariable.getName(), measurementVariable);
			mappedVariables.putIfAbsent(measurementVariable.getAlias(), measurementVariable);
		});
		return mappedVariables;
	}

	private Map<String, Integer> getLocationNameLocationIdMap(final Table<String, String, String> table) {
		final List<String> locationNames = new ArrayList<>();
		Map<String, Integer> locationNameLocationIdMap = new HashMap<>();
		if (table.columnKeySet().contains(LOCATION_NAME)) {
			for (final String observationUnitId : table.rowKeySet()) {
				locationNames.add(table.get(observationUnitId, LOCATION_NAME));
			}
			if (!org.springframework.util.CollectionUtils.isEmpty(locationNames)) {
				final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
				locationSearchRequest.setLocationNames(locationNames);
				locationNameLocationIdMap = this.locationService.searchLocations(locationSearchRequest, null, null)
						.stream().collect(Collectors.toMap(loc -> loc.getName().toUpperCase(), LocationDTO::getId));
			}
		}
		return locationNameLocationIdMap;
	}

	private Map<String, Map<Integer, GeolocationProperty>> getInstanceNoPropertyMap(final List<Integer> instanceIds) {
		final List<GeolocationProperty> geolocationProperties = this.daoFactory.getGeolocationPropertyDao()
				.getByGeolocationByGeolocationIds(instanceIds);
		final Map<String, Map<Integer, GeolocationProperty>> instanceNoPropertyMap = new HashMap<>();
		for(final GeolocationProperty property: geolocationProperties) {
			final String instanceNumber = property.getGeolocation().getDescription();
			instanceNoPropertyMap.putIfAbsent(instanceNumber, new HashMap<>());
			instanceNoPropertyMap.get(instanceNumber).put(property.getTypeId(), property);
		}
		return instanceNoPropertyMap;
	}

	@Override
	public Table<String, Integer, Integer> importDataset(final Integer datasetId, final Table<String, String, String> table,
		final Boolean draftMode, final Boolean allowDateAndCharacterBlankValue) {

		final Table<String, Integer, Integer> observationDbIdsTable = HashBasedTable.create();

		final List<MeasurementVariable> measurementVariableList =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, DatasetServiceImpl.MEASUREMENT_VARIABLE_TYPES);

		if (!measurementVariableList.isEmpty()) {
			final Map<String, MeasurementVariable> mappedVariables = getMappedVariablesMap(measurementVariableList);
			final List<String> observationUnitIds = new ArrayList<>(table.rowKeySet());

			final Map<String, ObservationUnitRow> currentData =
				this.daoFactory.getExperimentDao().getObservationUnitsAsMap(datasetId, measurementVariableList,
					observationUnitIds);

			final Map<Integer, List<Integer>> formulasMap = this.getTargetsByInput(measurementVariableList);

			for (final Object observationUnitId : table.rowKeySet()) {
				final Set<Phenotype> phenotypes = new HashSet<>();
				final ObservationUnitRow currentRow = currentData.get(observationUnitId);

				for (final String variableName : table.columnKeySet()) {
					final String importedVariableValue = table.get(observationUnitId, variableName);

					final MeasurementVariable measurementVariable = mappedVariables.get(variableName);

					// If allowDateAndCharacterBlankValue is true, allow to import blank value of Date and Character datatypes,
					// otherwise, just ignore blank values.
					if ((allowDateAndCharacterBlankValue && isDateOrCharacterDataType(measurementVariable)) || StringUtils
						.isNotBlank(importedVariableValue)) {
						this.saveImportedVariableValue(observationDbIdsTable, (String) observationUnitId, phenotypes,
								currentRow, importedVariableValue, measurementVariable, draftMode, new HashSet<>());
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

		final DmsProject environmentDataset =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);
		final List<MeasurementVariable> studyVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(
			studyId,
			Lists.newArrayList(VariableType.STUDY_DETAIL.getId()));

		final ObservationUnitsSearchDTO searchDTO = new ObservationUnitsSearchDTO();
		searchDTO.setDatasetId(datasetId);
		searchDTO.setInstanceIds(instanceIds);
		searchDTO.setEnvironmentDetails(this.findAdditionalEnvironmentFactors(environmentDataset.getProjectId()));
		searchDTO.setEnvironmentConditions(this.getEnvironmentConditionVariableNames(environmentDataset.getProjectId()));
		searchDTO.setEnvironmentDatasetId(environmentDataset.getProjectId());
		this.updateSearchDto(studyId, datasetId, searchDTO);

		final List<ObservationUnitRow> observationUnits =
			this.daoFactory.getObservationUnitsSearchDAO().getObservationUnitTable(searchDTO, null);
		this.addStudyVariablesToUnitRows(observationUnits, studyVariables);

		if (searchDTO.getGenericGermplasmDescriptors().stream().anyMatch(this::hasParentGermplasmDescriptors)) {
			final Set<Integer> gids = observationUnits.stream().map(s -> s.getGid()).collect(Collectors.toSet());
			this.addParentsFromPedigreeTable(gids, observationUnits);
		}

		return observationUnits.stream()
			.collect(Collectors.groupingBy(ObservationUnitRow::getInstanceId, LinkedHashMap::new, Collectors.toList()));
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
	public void updatePlotDatasetProperties(final Integer studyId, final PlotDatasetPropertiesDTO plotDatasetPropertiesDTO,
		final String programUUID) {
		final DmsProject plotDataset =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.PLOT_DATA.getId()).get(0);
		final List<Integer> descriptorPropertyIds = plotDataset.getProperties()
			.stream()
			.filter(projectProperty -> projectProperty.getTypeId() != null &&
				projectProperty.getVariableId() != null &&
				VariableType.GERMPLASM_DESCRIPTOR.getId().equals(projectProperty.getTypeId()) ||
				VariableType.GERMPLASM_ATTRIBUTE.getId().equals(projectProperty.getTypeId()) ||
				VariableType.GERMPLASM_PASSPORT.getId().equals(projectProperty.getTypeId()))
			.map(ProjectProperty::getVariableId)
			.collect(Collectors.toList());

		final List<Integer> newPropertyVariableIds = plotDatasetPropertiesDTO.getVariableIds()
			.stream()
			.filter(variableId -> !descriptorPropertyIds.contains(variableId))
			.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(newPropertyVariableIds)) {
			final AtomicInteger nextRank =
				new AtomicInteger(this.daoFactory.getProjectPropertyDAO().getNextRank(plotDataset.getProjectId()));

			final VariableFilter variableFilter = new VariableFilter();
			variableFilter.setProgramUuid(programUUID);
			newPropertyVariableIds.forEach(variableFilter::addVariableId);
			this.ontologyVariableDataManager.getWithFilter(variableFilter)
				.stream()
				.forEach(variable -> {
					Integer typeId = null;
					// get first value because germplasm attributes/passport are not combinables with other types
					if (!org.springframework.util.CollectionUtils.isEmpty(variable.getVariableTypes())) {
						typeId = variable.getVariableTypes().iterator().next().getId();
					}

					final String alias = StringUtils.isEmpty(variable.getAlias()) ? variable.getName() : variable.getAlias();
					final ProjectProperty projectProperty =
						new ProjectProperty(plotDataset, typeId, null, nextRank.getAndIncrement(),
							variable.getId(), alias);
					this.daoFactory.getProjectPropertyDAO().save(projectProperty);
				});
		}

		final List<GermplasmNameTypeDTO> existingNameTypes = this.getDatasetNameTypes(plotDataset.getProjectId());
		final List<Integer> nameTypeIds = existingNameTypes.stream().map(GermplasmNameTypeDTO::getId)
			.collect(Collectors.toList());

		final List<Integer> newNameTypeIds = plotDatasetPropertiesDTO.getNameTypeIds().stream()
			.filter(nameTypeId -> !nameTypeIds.contains(nameTypeId)).collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(newNameTypeIds)) {
			final AtomicInteger nextRank =
				new AtomicInteger(this.daoFactory.getProjectPropertyDAO().getNextRank(plotDataset.getProjectId()));
			this.daoFactory.getUserDefinedFieldDAO().getByFldnos(new HashSet(newNameTypeIds))
				.stream()
				.forEach(userDefinedField -> {
					final ProjectProperty projectProperty =
						new ProjectProperty(plotDataset, nextRank.getAndIncrement(), ((UserDefinedField) userDefinedField).getFldno(),
							((UserDefinedField) userDefinedField).getFcode());
					this.daoFactory.getProjectPropertyDAO().save(projectProperty);
				});
		}

		final List<Integer> removeVariableIds = descriptorPropertyIds.stream()
			.filter(variableId -> !plotDatasetPropertiesDTO.getVariableIds().contains(variableId)).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(removeVariableIds)) {
			this.daoFactory.getProjectPropertyDAO().deleteProjectVariables(plotDataset.getProjectId(), removeVariableIds);
		}

		final List<Integer> nameTypeIdsToRemove = nameTypeIds.stream()
			.filter(nameTypeId -> !plotDatasetPropertiesDTO.getNameTypeIds().contains(nameTypeId)).collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(nameTypeIdsToRemove)) {
			this.daoFactory.getProjectPropertyDAO().deleteProjectNameTypes(plotDataset.getProjectId(), nameTypeIdsToRemove);
		}
	}

	private void saveEnvironmentDetailValue(final MeasurementVariable measurementVariable, final Geolocation geolocation, GeolocationProperty property,
											String value, final Set<Integer> updatedVariableIds) {
		final BigInteger categoricalValueId = this.getCategoricalValueId(value, measurementVariable);
		value = getDateValueIfNecessary(value, measurementVariable);

		if (property == null) {
			property = new GeolocationProperty();
			property.setRank(1);
			property.setType(measurementVariable.getTermId());
			property.setGeolocation(geolocation);
		}
		value = categoricalValueId == null ? value : categoricalValueId.toString();
		if (!value.equals(property.getValue())) {
			updatedVariableIds.add(measurementVariable.getTermId());
		}
		property.setValue(value);
		this.daoFactory.getGeolocationPropertyDao().save(property);
	}

	private void saveImportedVariableValue(final Table<String, Integer, Integer> observationDbIdsTable, final String observationUnitId,
										   final Set<Phenotype> phenotypes, final ObservationUnitRow currentRow, String importedVariableValue,
										   final MeasurementVariable measurementVariable, final boolean draftMode, final Set<Integer> updatedVariableIds) {
		final BigInteger categoricalValueId = this.getCategoricalValueId(importedVariableValue, measurementVariable);
		importedVariableValue = getDateValueIfNecessary(importedVariableValue, measurementVariable);

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
			updatedVariableIds.add(measurementVariable.getTermId());

		} else if (observationUnitData.getObservationId() != null
				&& importedVariableValue.equalsIgnoreCase(observationUnitData.getValue())
				&& Boolean.TRUE.equals(draftMode)) {
			/*Phenotype exists and imported value is equal to value => Erase draft data*/
			phenotype =
					this.updatePhenotype(observationUnitData.getObservationId(), observationUnitData.getCategoricalValueId(),
							observationUnitData.getValue(), null, null, draftMode);
		} else if (observationUnitData.getObservationId() != null) {
			if (draftMode) {
				/*imported value is different to stored value*/
				phenotype =
						this.updatePhenotype(observationUnitData.getObservationId(), observationUnitData.getCategoricalValueId(),
								observationUnitData.getValue(), categoricalValue, importedVariableValue, draftMode);
			} else {
				if (!importedVariableValue.equalsIgnoreCase(observationUnitData.getValue())) {
					updatedVariableIds.add(measurementVariable.getTermId());
				}
				phenotype =
						this.updatePhenotype(observationUnitData.getObservationId(), categoricalValue,
								importedVariableValue, null, null, draftMode);
			}
		}

		if (phenotype != null) {
			phenotypes.add(phenotype);
		}
		if (observationDbIdsTable != null) {
			// We need to return the observationDbIds (mapped in a table by observationUnitId and variableId) of the created/updated observations.
			observationDbIdsTable
					.put(observationUnitId, observationUnitData.getVariableId(), phenotype.getPhenotypeId());
		}
	}

	private static String getDateValueIfNecessary(String importedVariableValue, final MeasurementVariable measurementVariable) {
		if (measurementVariable.getDataTypeId() == TermId.DATE_VARIABLE.getId()) {
			// In case the date is in yyyy-MM-dd format, try to parse it as number format yyyyMMdd
			final String parsedDate =
					Util.tryConvertDate(importedVariableValue, Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT);
			if (parsedDate != null) {
				importedVariableValue = parsedDate;
			}
		}
		return importedVariableValue;
	}

	private static BigInteger getCategoricalValueId(final String importedVariableValue, final MeasurementVariable measurementVariable) {
		BigInteger categoricalValueId = null;
		if (measurementVariable.getDataTypeId() == TermId.CATEGORICAL_VARIABLE.getId()) {
			for (final ValueReference possibleValue : measurementVariable.getPossibleValues()) {
				if (importedVariableValue.equalsIgnoreCase(possibleValue.getName())) {
					categoricalValueId = BigInteger.valueOf(possibleValue.getId());
					break;
				}
			}
		}
		return categoricalValueId;
	}

	private void updatePhenotypes(final List<Integer> phenotypeIds, final Integer variableId, final Integer newCValueId,
		final String newValue, final Boolean draftMode) {
		final Formula formula = this.daoFactory.getFormulaDAO().getByTargetVariableId(variableId);
		final boolean isDerivedTrait = formula != null;
		final Integer cvalueId = Integer.valueOf(0).equals(newCValueId) ? null : newCValueId;
		final Integer loggedInUser = this.userService.getCurrentlyLoggedInUserId();
		this.daoFactory.getPhenotypeDAO().updatePhenotypes(phenotypeIds, cvalueId, newValue, draftMode, isDerivedTrait, loggedInUser);
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
	 *                                   FIXME IBP-2694
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

		phenotype.setUpdatedDate(new Date());
		phenotype.setUpdatedBy(this.userService.getCurrentlyLoggedInUserId());
		phenotypeDao.update(phenotype);
		return phenotype;
	}

	private Phenotype createPhenotype(final ObservationDto observation, final Boolean draftMode) {
		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());

		final Integer loggedInUser = this.userService.getCurrentlyLoggedInUserId();
		phenotype.setCreatedBy(loggedInUser);
		phenotype.setUpdatedBy(loggedInUser);

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
		this.alignObservationValues(observation, savedRecord);
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
	public FilteredPhenotypesInstancesCountDTO countFilteredInstancesAndObservationUnits(
		final Integer datasetId, final ObservationUnitsSearchDTO observationUnitsSearchDTO) {
		this.addPreFilteredGids(observationUnitsSearchDTO.getFilter());
		return this.daoFactory.getObservationUnitsSearchDAO()
			.countFilteredInstancesAndObservationUnits(datasetId, observationUnitsSearchDTO);
	}

	private boolean shouldAddStockIdColumn(final Integer studyId) {
		final TransactionsSearchDto transactionsSearchDto = new TransactionsSearchDto();
		transactionsSearchDto.setTransactionStatus(Arrays.asList(0, 1));
		transactionsSearchDto.setPlantingStudyIds(Arrays.asList(studyId));
		return this.daoFactory.getTransactionDAO().countSearchTransactions(transactionsSearchDto) > 0;
	}

	private void addParentsFromPedigreeTable(final Set<Integer> gids, final List<ObservationUnitRow> list) {

		if (CollectionUtils.isEmpty(gids)) {
			return;
		}

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
					new ObservationUnitData(TermId.MALE_PARENT_GID.getId(),
						germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN));
				observationUnitRow.getVariables().put(
					TermId.MALE_PARENT_NAME.name(),
					new ObservationUnitData(TermId.MALE_PARENT_NAME.getId(), germplasm.getPreferredName().getNval()));
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
		if (filter != null) {
			final Set<String> textKeys = filter.getFilteredTextValues().keySet();
			if (textKeys.contains(String.valueOf(TermId.FEMALE_PARENT_GID.getId())) ||
				textKeys.contains(String.valueOf(TermId.FEMALE_PARENT_NAME.getId())) ||
				textKeys.contains(String.valueOf(TermId.MALE_PARENT_GID.getId())) ||
				textKeys.contains(String.valueOf(TermId.MALE_PARENT_NAME.getId()))
			) {
				filter.setPreFilteredGids(this.daoFactory.getObservationUnitsSearchDAO().addPreFilteredGids(filter));
			}
		}
	}

	private Map<String, ObservationUnitRow> getEnvironmentObservationUnitRows(
			final List<MeasurementVariable> environmentVariables, final Integer studyId, final Integer datasetId) {
		final ObservationUnitsSearchDTO searchDTO = new ObservationUnitsSearchDTO();
		final List<MeasurementVariableDto> environmentDetails = new ArrayList<>();
		final List<MeasurementVariableDto> environmentConditions = new ArrayList<>();
		for (final MeasurementVariable variable: environmentVariables) {
			if (VariableType.ENVIRONMENT_DETAIL.getId().equals(variable.getVariableType().getId())) {
				environmentDetails.add(new MeasurementVariableDto(variable.getTermId(), variable.getName()));
			} else if (VariableType.ENVIRONMENT_CONDITION.getId().equals(variable.getVariableType().getId())) {
				environmentConditions.add(new MeasurementVariableDto(variable.getTermId(), variable.getName()));
			}
		}
		searchDTO.setEnvironmentDetails(environmentDetails);
		searchDTO.setEnvironmentConditions(environmentConditions);
		searchDTO.setEnvironmentDatasetId(datasetId);
		final PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE);
		final List<org.generationcp.middleware.service.api.dataset.ObservationUnitRow> rows = this
				.getObservationUnitRows(studyId, datasetId, searchDTO, pageRequest);
		return rows.stream().collect(Collectors.toMap(row -> String.valueOf(row.getTrialInstance()), Function.identity()));
	}

	@Override
	public List<GermplasmNameTypeDTO> getDatasetNameTypes(final Integer datasetId) {
		return this.daoFactory.getUserDefinedFieldDAO().getDatasetNameTypes(datasetId);
	}

	private void mapGeolocationMetaData(final Geolocation geolocation, final int variableId, final String value, final Set<Integer> updatedVariableIds) {
		boolean updated = false;
		if (TermId.LATITUDE.getId() == variableId) {
			final Double dValue = !StringUtils.isBlank(value) ? Double.valueOf(value) : null;
			updated = dValue != geolocation.getLatitude();
			geolocation.setLatitude(dValue);
		} else if (TermId.LONGITUDE.getId() == variableId) {
			final Double dValue = !StringUtils.isBlank(value) ? Double.valueOf(value) : null;
			updated = dValue != geolocation.getLongitude();
			geolocation.setLongitude(dValue);
		} else if (TermId.GEODETIC_DATUM.getId() == variableId) {
			updated = value != geolocation.getGeodeticDatum();
			geolocation.setGeodeticDatum(value);
		} else if (TermId.ALTITUDE.getId() == variableId) {
			final Double dValue = !StringUtils.isBlank(value) ? Double.valueOf(value) : null;
			updated = dValue != geolocation.getAltitude();
			geolocation.setAltitude(dValue);
		}

		if (updated) {
			updatedVariableIds.add(variableId);
		}
	}

}
