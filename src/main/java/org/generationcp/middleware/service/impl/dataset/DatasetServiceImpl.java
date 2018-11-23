package org.generationcp.middleware.service.impl.dataset;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.operation.transformer.etl.MeasurementVariableTransformer;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitImportResult;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.generationcp.middleware.service.impl.study.DesignFactors;
import org.generationcp.middleware.service.impl.study.GermplasmDescriptors;
import org.generationcp.middleware.util.Util;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by clarysabel on 10/22/18.
 */
@Transactional
public class DatasetServiceImpl implements DatasetService {

	public static final String DATE_FORMAT = "YYYYMMDD HH:MM:SS";

	private static final Logger LOG = LoggerFactory.getLogger(DatasetServiceImpl.class);
	public static final String[] FIXED_GERMPLASM_DESCRIPTOR = {"GID", "DESIGNATION", "ENTRY_NO", "ENTRY_TYPE", "ENTRY_CODE", "OBS_UNIT_ID"};
	public static final String[] FIXED_DESIGN_FACTORS =
		{"REP_NO", "PLOT_NO", "BLOCK_NO", "ROW", "COL", "FIELDMAP COLUMN", "FIELDMAP RANGE"};

	public static final ArrayList<Integer> SUBOBS_COLUMNS_VARIABLE_TYPES = Lists.newArrayList( //
		VariableType.GERMPLASM_DESCRIPTOR.getId(), //
		VariableType.TRAIT.getId(), //
		VariableType.OBSERVATION_UNIT.getId());

	public static final ArrayList<Integer> PLOT_COLUMNS_VARIABLE_TYPES = Lists.newArrayList( //
		VariableType.GERMPLASM_DESCRIPTOR.getId(), //
		VariableType.OBSERVATION_UNIT.getId());

	public static final ArrayList<Integer> DATASET_VARIABLE_TYPES = Lists.newArrayList( //
		VariableType.OBSERVATION_UNIT.getId(), //
		VariableType.TRAIT.getId(), //
		VariableType.SELECTION_METHOD.getId(),
		VariableType.OBSERVATION_UNIT.getId());


	private static final String DATA_TYPE_NUMERIC = "Numeric";

	private DaoFactory daoFactory;

	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private MeasurementVariableService measurementVariableService;

	@Autowired
	private GermplasmDescriptors germplasmDescriptors;

	@Autowired
	private DesignFactors designFactors;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private MeasurementVariableTransformer measurementVariableTransformer;

	@Autowired
	ResourceBundleMessageSource messageSource;

	public DatasetServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DatasetServiceImpl(final MeasurementVariableService measurementVariableService, final GermplasmDescriptors germplasmDescriptors,
		final DesignFactors designFactors) {
		super();
		this.measurementVariableService = measurementVariableService;
		this.germplasmDescriptors = germplasmDescriptors;
		this.designFactors = designFactors;
	}

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		final Session currentSession = sessionProvider.getSession();
		this.daoFactory = new DaoFactory(sessionProvider);
		this.ontologyVariableDataManager = new OntologyVariableDataManagerImpl(sessionProvider);
	}

	@Override
	public long countPhenotypes(final Integer datasetId, final List<Integer> traitIds) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDataset(datasetId, traitIds);
	}

	@Override
	public long countPhenotypesByInstance(final Integer datasetId, final Integer instanceId) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDatasetAndInstance(datasetId, instanceId);
	}

	@Override
	public List<MeasurementVariable> getSubObservationSetColumns(final Integer subObservationSetId) {
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

		final String cropPrefix = this.workbenchDataManager.getProjectByUuid(study.getProgramUUID()).getCropType().getPlotCodePrefix();

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
		final Variable observationUnitVariable = this.ontologyVariableDataManager.getVariable(study.getProgramUUID(), observationUnitVariableId, false, false);
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
				final ExperimentModel experimentModel = new ExperimentModel(plotObservationUnit.getGeoLocation(), plotObservationUnit.getTypeId(),
					cropPrefix + "P" + RandomStringUtils.randomAlphanumeric(8), subObservationDataset, plotObservationUnit.getStock(),
					plotObservationUnit, i);
				this.daoFactory.getExperimentDao().save(experimentModel);
			}
		}
		return this.getDataset(studyId, dataset.getProjectId());
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

	private List<ProjectProperty> buildDefaultDatasetProperties(final DmsProject study, final DmsProject dmsProject,
		final String datasetName, final Integer datasetTypeId) {
		final List<ProjectProperty> projectProperties = new ArrayList<>();
		final ProjectProperty datasetProperty =
			this.buildDatasetProperty(dmsProject,
				VariableType.STUDY_DETAIL.getId(), TermId.DATASET_NAME.getId(),
				datasetName, null, 1,
				this.ontologyVariableDataManager.getVariable(study.getProgramUUID(), TermId.DATASET_NAME.getId(), false, false));
		final ProjectProperty datasetTitleProperty =
			this.buildDatasetProperty(dmsProject,
				VariableType.STUDY_DETAIL.getId(), TermId.DATASET_TITLE.getId(),
				null, null, 2,
				this.ontologyVariableDataManager.getVariable(study.getProgramUUID(), TermId.DATASET_TITLE.getId(), false, false));
		final ProjectProperty datasetTypeProperty =
			this.buildDatasetProperty(dmsProject,
				VariableType.STUDY_DETAIL.getId(), TermId.DATASET_TYPE.getId(),
				String.valueOf(datasetTypeId), null, 3,
				this.ontologyVariableDataManager.getVariable(study.getProgramUUID(), TermId.DATASET_TYPE.getId(), false, false));
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
		final ProjectProperty projectProperty =
			new ProjectProperty(dmsProject, typeId, value, rank, variableId, (alias == null) ? variable.getName() : alias);
		return projectProperty;
	}

	private List<ProjectRelationship> buildProjectRelationships(final DmsProject parentDataset, final DmsProject childDataset)
		throws MiddlewareQueryException {
		final ProjectRelationship relationship = new ProjectRelationship();
		relationship.setSubjectProject(childDataset);
		relationship.setObjectProject(parentDataset);
		relationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());

		final List<ProjectRelationship> relationships = new ArrayList<ProjectRelationship>();
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
	public boolean isValidObservation(final Integer observationUnitId, final Integer observationId) {
		return this.daoFactory.getPhenotypeDAO().isValidPhenotype(observationUnitId, observationId);
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

		this.resolveObservationStatus(variableId, phenotype);

		final Phenotype savedRecord = this.daoFactory.getPhenotypeDAO().save(phenotype);
		observation.setObservationId(savedRecord.getPhenotypeId());
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		observation.setCreatedDate(dateFormat.format(savedRecord.getCreatedDate()));
		observation.setUpdatedDate(dateFormat.format(savedRecord.getUpdatedDate()));
		observation.setStatus(savedRecord.getValueStatus() != null ? savedRecord.getValueStatus().getName() : null);

		return observation;
	}

	@Override
	public ObservationDto updatePhenotype(
		final Integer observationUnitId, final Integer observationId, final Integer categoricalValueId, final String value) {
		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();

		final Phenotype phenotype = phenotypeDao.getById(observationId);
		phenotype.setValue(value);
		phenotype.setcValue(categoricalValueId == 0 ? null : categoricalValueId);
		final Integer observableId = phenotype.getObservableId();
		this.resolveObservationStatus(observableId, phenotype);

		phenotypeDao.update(phenotype);

		// Also update the status of phenotypes of the same observation unit for variables using it as input variable
		this.updateDependentPhenotypesStatus(observableId, observationUnitId);

		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		final ObservationDto observation = new ObservationDto();
		observation.setObservationId(phenotype.getPhenotypeId());
		observation.setCategoricalValueId(phenotype.getcValueId());
		observation.setStatus(phenotype.getValueStatus() != null ? phenotype.getValueStatus().getName() : null);
		observation.setUpdatedDate(dateFormat.format(phenotype.getUpdatedDate()));
		observation.setCreatedDate(dateFormat.format(phenotype.getCreatedDate()));
		observation.setValue(phenotype.getValue());
		observation.setObservationUnitId(phenotype.getExperiment().getNdExperimentId());
		observation.setVariableId(phenotype.getObservableId());

		return observation;

	}

	void resolveObservationStatus(final Integer variableId, final Phenotype phenotype) {

		final FormulaDAO formulaDAO = this.daoFactory.getFormulaDAO();
		final Formula formula = formulaDAO.getByTargetVariableId(variableId);

		final boolean isDerivedTrait = formula != null;

		if (isDerivedTrait) {
			phenotype.setValueStatus(Phenotype.ValueStatus.MANUALLY_EDITED);
		}
	}

	/*
	 * If variable is input variable to formula, update the phenotypes status as "OUT OF SYNC" for given observation unit
	 */
	void updateDependentPhenotypesStatus(final Integer variableId, final Integer observationUnitId) {
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
	public DatasetDTO getDataset(final Integer studyId, final Integer datasetId) {
		final List<DatasetDTO> datasetDTOList = this.getDatasets(studyId, new TreeSet<Integer>());
		for (final DatasetDTO datasetDto : datasetDTOList)
			if (datasetDto.getDatasetId().equals(datasetId)) {
				final DatasetDTO datasetDTO = datasetDto;
				datasetDTO.setInstances(this.daoFactory.getDmsProjectDAO().getDatasetInstances(datasetId));
				datasetDTO.setVariables(
					this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, DatasetServiceImpl.DATASET_VARIABLE_TYPES));
				return datasetDTO;
			}

		return null;
	}


	protected void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final Integer datasetId, final Integer instanceId, final Integer pageNumber, final Integer pageSize,
		final String sortedColumnTermId, final String sortOrder) {
		final List<MeasurementVariableDto> selectionMethodsAndTraits = this.measurementVariableService.getVariablesForDataset(datasetId,
				VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());
		String sortBy = sortedColumnTermId;
		if (sortedColumnTermId != null) {
			sortBy = this.ontologyDataManager.getTermById(Integer.valueOf(sortedColumnTermId)).getName();
		}
	//TODO delete this
	/*	final ObservationUnitImportResult o = new ObservationUnitImportResult();
		final Map<String, Map<String, String>> observationUnitRows = new HashMap<>();
		final Map<String, String> data = new HashMap<>();
		data.put("Aflatox_M_ppb", "12");
		observationUnitRows.put("839f9d543dd8d", data);
		o.setObservationUnitRows(observationUnitRows);
		this.validateImportDataset(studyId, datasetId, "079878e1-ce3b-48f5-89f3-df7c82c96363", o);
	*/
		return this.daoFactory.getExperimentDao().getObservationUnitTable(datasetId, selectionMethodsAndTraits,
			this.findGenericGermplasmDescriptors(studyId), this.findAdditionalDesignFactors(studyId), instanceId,
			pageNumber, pageSize, sortBy, sortOrder);
	}

	private List<String> findGenericGermplasmDescriptors(final int studyId) {

		final List<String> allGermplasmDescriptors = this.germplasmDescriptors.find(studyId);
		/**
		 * Fixed descriptors are the ones that are NOT stored in stockprop or nd_experimentprop. We dont need additional joins to props
		 * table for these as they are available in columns in main entity (e.g. stock or nd_experiment) tables.
		 */
		final List<String> fixedGermplasmDescriptors =
			Lists.newArrayList(FIXED_GERMPLASM_DESCRIPTOR);
		final List<String> genericGermplasmDescriptors = Lists.newArrayList();

		for (final String gpDescriptor : allGermplasmDescriptors) {
			if (!fixedGermplasmDescriptors.contains(gpDescriptor)) {
				genericGermplasmDescriptors.add(gpDescriptor);
			}
		}
		return genericGermplasmDescriptors;
	}

	private List<String> findAdditionalDesignFactors(final int studyIdentifier) {

		final List<String> allDesignFactors = this.designFactors.find(studyIdentifier);
		/**
		 * Fixed design factors are already being retrieved individually in Measurements query. We are only interested in additional
		 * EXPERIMENTAL_DESIGN and TREATMENT FACTOR variables
		 */
		final List<String> fixedDesignFactors =
			Lists.newArrayList(FIXED_DESIGN_FACTORS);
		final List<String> additionalDesignFactors = Lists.newArrayList();

		for (final String designFactor : allDesignFactors) {
			if (!fixedDesignFactors.contains(designFactor)) {
				additionalDesignFactors.add(designFactor);
			}
		}
		return additionalDesignFactors;
	}

	@Override
	public int countTotalObservationUnitsForDataset(final int datasetId, final int instanceId) {
		return this.daoFactory.getExperimentDao().countTotalObservationUnitsForDataset(datasetId, instanceId);
	}
	
	@Override
	public void deletePhenotype(final Integer phenotypeId) {
		final Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getById(phenotypeId);
		final Integer observableId = phenotype.getObservableId();
		final Integer observationUnitId = phenotype.getExperiment().getNdExperimentId();
		this.daoFactory.getPhenotypeDAO().makeTransient(phenotype);
		
		// Also update the status of phenotypes of the same observation unit for variables using the trait as input variable
		this.updateDependentPhenotypesStatus(observableId, observationUnitId);
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
			final List<MeasurementVariableDto> selectionMethodsAndTraits, final List<String> observationUnitIds) {
		return this.daoFactory.getExperimentDao().getObservationUnitsAsMap(datasetId, selectionMethodsAndTraits,
				observationUnitIds);
	}

	@Override
	public ObservationUnitImportResult validateImportDataset(
		final Integer studyId, final Integer datasetId,
		final String programUUID, final ObservationUnitImportResult observationUnitImportResult) {

		boolean isOverwritten = false;
		final ObservationUnitImportResult result = new ObservationUnitImportResult();
		final Table rows = observationUnitImportResult.getObservationUnitRows();
		result.setObservationUnitRows(rows);

		final List<MeasurementVariableDto> selectionMethodsAndTraits = this.measurementVariableService.getVariablesForDataset(datasetId,
			VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());

		if (selectionMethodsAndTraits.size() > 0) {

			final List<String> observationUnitIds = new ArrayList<>(observationUnitImportResult.getObservationUnitRows().columnKeySet());

			final Map<String, ObservationUnitRow> currentData =
				this.daoFactory.getExperimentDao().getObservationUnitsAsMap(datasetId, selectionMethodsAndTraits,
					observationUnitIds);

			final Integer difference = currentData.values().size() - observationUnitImportResult.getObservationUnitRows().size();
			if (difference != 0) {
				//"xx number of observation units were not found in the dataset you selected. Please review the imported file. Would you like to proceed with the import?"
				final String message = this.messageSource.getMessage("warning.import.not.found", new String[] {difference.toString()}, LocaleContextHolder.getLocale());
				result.getWarnings().add(message);
			}

			for (final Object row : rows.columnKeySet()) {
				final ObservationUnitRow currentRow = currentData.get(row);
				final Map<String, String> variables = rows.column(row);
				for (final String variableName : variables.keySet()) {
					final String importedVariable = variables.get(variableName);
					final ObservationUnitData variable = currentRow.getVariables().get(variableName);

					if (!isOverwritten && variable != null && variable.getValue() != null && !variable.getValue().equalsIgnoreCase(importedVariable)) {
						final String message = this.messageSource.getMessage("warning.import.overwrite.data", new String[] {difference.toString()}, LocaleContextHolder.getLocale());
						result.getWarnings().add(message);
						isOverwritten = true;
					}

					final StandardVariable
						standardVariable = this.ontologyDataManager.getStandardVariable(variable.getVariableId(), programUUID);
					final MeasurementVariable measurementVariable = this.measurementVariableTransformer.transform(standardVariable, false);
					if (!this.isValidValue(measurementVariable, importedVariable)) {
						//The numeric variableName {0} contains an invalid value {1} containing characters. Please check the data file and try again.
						throw new MiddlewareRequestException("", "warning.import.save.invalidCellValue", null);
					}
				}
			}
		}
		return result;
	}

	private boolean isValidValue(
		final MeasurementVariable var, final String value) {
		if (StringUtils.isBlank(value)) {
			return true;
		}
		if (var.getMinRange() != null && var.getMaxRange() != null) {
			return this.validateIfValueIsMissingOrNumber(value.trim());
		} else if (var != null && var.getDataTypeId() != null && var.getDataTypeId() == TermId.DATE_VARIABLE.getId()) {
			return Util.isValidDate(value);
		} else if (StringUtils.isNotBlank(var.getDataType()) && var.getDataType().equalsIgnoreCase(DATA_TYPE_NUMERIC)) {
			return this.validateIfValueIsMissingOrNumber(value.trim());
		}
		return true;
	}

	private boolean validateIfValueIsMissingOrNumber(final String value) {
		if (MeasurementData.MISSING_VALUE.equals(value.trim())) {
			return true;
		}
		return NumberUtils.isNumber(value);
	}

	@Override
	public List<String> importDataset(final Integer datasetId, final ObservationUnitImportResult observationUnitImportResult) {
		return null;
	}

	public void setGermplasmDescriptors(final GermplasmDescriptors germplasmDescriptors) {
		this.germplasmDescriptors = germplasmDescriptors;
	}

	public void setDesignFactors(final DesignFactors designFactors) {
		this.designFactors = designFactors;
	}

	public void setMeasurementVariableService(final MeasurementVariableService measurementVariableService) {
		this.measurementVariableService = measurementVariableService;
	}
}
