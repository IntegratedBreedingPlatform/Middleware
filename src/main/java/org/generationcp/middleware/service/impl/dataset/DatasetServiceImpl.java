package org.generationcp.middleware.service.impl.dataset;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;


/**
 * Created by clarysabel on 10/22/18.
 */
@Transactional
public class DatasetServiceImpl implements DatasetService {

	public static final String DATE_FORMAT = "YYYYMMDD HH:MM:SS";

	protected static final String[] FIXED_DESIGN_FACTORS =
		{"REP_NO", "PLOT_NO", "BLOCK_NO", "ROW", "COL", "FIELDMAP COLUMN", "FIELDMAP RANGE"};

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
	
	protected static final List<Integer> STANDARD_ENVIRONMENT_FACTORs = Lists.newArrayList( //
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
		final Integer observationUnitId, final Integer observationId, final Integer categoricalValueId, final String value) {
		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();

		final Phenotype phenotype = phenotypeDao.getById(observationId);
		phenotype.setValue(value);
		phenotype.setcValue(categoricalValueId != null && categoricalValueId != 0 ? categoricalValueId : null);
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
	public DatasetDTO getDataset(final Integer datasetId) {
		final DatasetDTO datasetDTO = this.daoFactory.getDmsProjectDAO().getDataset(datasetId);
		if (datasetDTO != null) {
			datasetDTO.setInstances(this.daoFactory.getDmsProjectDAO().getDatasetInstances(datasetId));
			datasetDTO.setVariables(
				this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, DatasetServiceImpl.DATASET_VARIABLE_TYPES));
		}

		return datasetDTO;
	}


	protected void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final int datasetId, final Integer instanceId, final Integer pageNumber, final Integer pageSize,
		final String sortedColumnTermId, final String sortOrder) {
		final List<MeasurementVariableDto> selectionMethodsAndTraits = this.measurementVariableService.getVariablesForDataset(datasetId,
			VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());
		String sortBy = sortedColumnTermId;
		if (sortedColumnTermId != null) {
			sortBy = this.ontologyDataManager.getTermById(Integer.valueOf(sortedColumnTermId)).getName();
		}

		return this.daoFactory.getExperimentDao().getObservationUnitTable(datasetId, selectionMethodsAndTraits,
			this.findGenericGermplasmDescriptors(studyId), this.findAdditionalDesignFactors(studyId), instanceId,
			pageNumber, pageSize, sortBy, sortOrder);
	}

	private List<String> findGenericGermplasmDescriptors(final int studyId) {

		return this.studyService.getGenericGermplasmDescriptors(studyId);
	}

	private List<String> findAdditionalDesignFactors(final int studyIdentifier) {
		return this.studyService.getAdditionalDesignFactors(studyIdentifier);
	}
	
	List<String> findAdditionalEnvironmentFactors(final List<MeasurementVariable> environmentFactors) {
		final List<String> factors = new ArrayList<>();
		for (final MeasurementVariable variable : environmentFactors) {
			if (!STANDARD_ENVIRONMENT_FACTORs.contains(variable.getTermId())) {
				factors.add(variable.getName());
			}
		}
		return factors;
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

	@Override
	public void deleteDataset(final int datasetId) {

		try {

			this.daoFactory.getDmsProjectDAO().deleteDataset(datasetId);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("error in deleteDataSet " + e.getMessage(), e);
		}
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
	public void importDataset(final Integer datasetId, final Table<String, String, String> table) {
		final List<MeasurementVariable>
			measurementVariableList =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, DatasetServiceImpl.MEASUREMENT_VARIABLE_TYPES);

		if (measurementVariableList.size() > 0) {

			final List<String> observationUnitIds = new ArrayList<>(table.rowKeySet());

			final Map<String, ObservationUnitRow> currentData =
				this.daoFactory.getExperimentDao().getObservationUnitsAsMap(datasetId, measurementVariableList,
					observationUnitIds);

			final Map<MeasurementVariable, List<MeasurementVariable>> formulasMap = this.getVariatesMapUsedInFormulas(measurementVariableList);
			for (final Object observationUnitId : table.rowKeySet()) {
				final Set<Phenotype> phenotypes = new HashSet<>();
				final ObservationUnitRow currentRow = currentData.get(observationUnitId);

				final ExperimentModel experimentModel = this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitId.toString());
				for (final String variableName : table.columnKeySet()) {
					final String importedVariableValue = table.get(observationUnitId, variableName);

					if (importedVariableValue != null && !importedVariableValue.isEmpty()) {
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
							if (importedVariableValue != null) {
								for (final ValueReference possibleValue : measurementVariable.getPossibleValues()) {
									if (importedVariableValue.equalsIgnoreCase(possibleValue.getName())) {
										categoricalValueId = BigInteger.valueOf(possibleValue.getId());
										break;
									}
								}
							}
						}

						final ObservationUnitData observationUnitData = currentRow.getVariables().get(measurementVariable.getName());
						final Integer categoricalValue = categoricalValueId != null ? categoricalValueId.intValue() : null;
						Phenotype phenotype = null;
						if (observationUnitData != null && observationUnitData.getObservationId() != null && !importedVariableValue
							.equalsIgnoreCase(observationUnitData.getValue())) {
							phenotype =
								this.updatePhenotype(observationUnitData.getObservationId(), categoricalValue, importedVariableValue);
						} else if (observationUnitData == null || observationUnitData.getObservationId() == null) {
							final ObservationDto observationDto = new ObservationDto();
							observationDto.setVariableId(measurementVariable.getTermId());

							observationDto.setCategoricalValueId(categoricalValue);
							observationDto.setCreatedDate(Util.getCurrentDateAsStringValue());
							observationDto.setObservationUnitId(experimentModel.getNdExperimentId());
							observationDto.setUpdatedDate(Util.getCurrentDateAsStringValue());
							observationDto.setValue(importedVariableValue);
							if (measurementVariable.getFormula() != null) {
								observationDto.setStatus(Phenotype.ValueStatus.MANUALLY_EDITED.getName());
							} else {
								observationDto.setStatus(null);
							}

							phenotype = this.createPhenotype(observationDto);

						}

						if (phenotype != null) {
							phenotypes.add(phenotype);
						}
					}
				}

				phenotypes.addAll(experimentModel.getPhenotypes());
				this.setMeasurementDataAsOutOfSync(formulasMap, phenotypes);
			}
		}
	}

	@Override
	public List<MeasurementVariable> getDatasetMeasurementVariables(final Integer datasetId) {
		return this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, MEASUREMENT_VARIABLE_TYPES);
	}
	
	@Override
	public List<MeasurementVariable> getAllDatasetVariables(final Integer studyId, final Integer datasetId) {
		// TODO get plot dataset even if subobs is not a direct descendant (ie. sub-sub-obs)
		final DmsProject plotDataset = this.daoFactory.getProjectRelationshipDao()
			.getObjectBySubjectIdAndTypeId(datasetId, TermId.BELONGS_TO_STUDY.getId());
		
		final List<MeasurementVariable> studyVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(studyId,
				Lists.newArrayList(VariableType.STUDY_DETAIL.getId()));

		final DmsProject trialDataset = this.daoFactory.getDmsProjectDAO().getDataSetsByStudyAndProjectProperty(studyId, TermId.DATASET_TYPE.getId(),
				String.valueOf(DataSetType.SUMMARY_DATA.getId())).get(0);
		final List<MeasurementVariable> environmentDetailsVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(trialDataset.getProjectId(), ENVIRONMENT_VARIABLE_TYPES);
		// Experimental Design variables have value at dataset level. Perform sorting to ensure that they come first
		Collections.sort(environmentDetailsVariables, new Comparator<MeasurementVariable>() {
			@Override
			public int compare(MeasurementVariable var1, MeasurementVariable var2) {
				final String value1 = var1.getValue();
				final String value2 = var2.getValue();
		        if (value1 != null && value2 != null)
		            return value1.compareTo(value2);
		        return (value1 == null) ? 1 : -1;
		    }
		});
		final List<MeasurementVariable> environmentConditions = this.daoFactory.getDmsProjectDAO()
				.getObservationSetVariables(trialDataset.getProjectId(), Lists.newArrayList(VariableType.TRAIT.getId()));
		
		final List<MeasurementVariable> plotDataSetColumns =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(plotDataset.getProjectId(), PLOT_COLUMNS_VARIABLE_TYPES);
		final List<MeasurementVariable> subObservationSetColumns =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, SUBOBS_COLUMNS_VARIABLE_TYPES);

		final List<MeasurementVariable> allVariables = new ArrayList<>();
		allVariables.addAll(studyVariables);
		allVariables.addAll(environmentDetailsVariables);
		allVariables.addAll(environmentConditions);
		allVariables.addAll(plotDataSetColumns);
		allVariables.addAll(subObservationSetColumns);
		return allVariables;
	}
	
	@Override
	public Map<Integer, List<ObservationUnitRow>> getInstanceObservationUnitRowsMap(final int studyId, final int datasetId,
			final List<Integer> instanceIds) {
		final Map<Integer, List<ObservationUnitRow>> instanceMap = new HashMap<>();
		final List<MeasurementVariableDto> selectionMethodsAndTraits = this.measurementVariableService.getVariablesForDataset(datasetId,
				VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());
		final List<String> additionalDesignFactors = this.findAdditionalDesignFactors(studyId);
		final List<String> genericGermplasmDescriptors = this.findGenericGermplasmDescriptors(studyId);
//		final List<MeasurementVariable> studyVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(studyId,
//				Lists.newArrayList(VariableType.STUDY_DETAIL.getId()));
		
		final DmsProject trialDataset = this.daoFactory.getDmsProjectDAO().getDataSetsByStudyAndProjectProperty(studyId, TermId.DATASET_TYPE.getId(),
				String.valueOf(DataSetType.SUMMARY_DATA.getId())).get(0);
		final List<MeasurementVariable> environmentDetailsVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(trialDataset.getProjectId(), ENVIRONMENT_VARIABLE_TYPES);
		final List<String> additionalEnvironmentFactors = this.findAdditionalEnvironmentFactors(environmentDetailsVariables);
		
		for (final Integer instanceId : instanceIds) {			
			final List<ObservationUnitRow> observationUnits = this.daoFactory.getExperimentDao().getObservationUnitAllVariableValues(datasetId, selectionMethodsAndTraits,
					genericGermplasmDescriptors, additionalDesignFactors, additionalEnvironmentFactors, instanceId);
			instanceMap.put(instanceId, observationUnits);	
			// TODO add study variables to unit rows	
		}	
		return instanceMap;
	}

	private void setMeasurementDataAsOutOfSync(
		final Map<MeasurementVariable, List<MeasurementVariable>> formulasMap,
		final Set<Phenotype> phenotypes) {
		for (final MeasurementVariable measurementVariable : formulasMap.keySet()) {
			final Phenotype key = this.findPhenotypeByTermId(measurementVariable.getTermId(), phenotypes);
			final List<MeasurementVariable> formulas = formulasMap.get(measurementVariable);
			for (final MeasurementVariable formula : formulas) {
				final Phenotype value = this.findPhenotypeByTermId(formula.getTermId(), phenotypes);
				if (key != null && key.isChanged() && value != null) {
					value.setValueStatus(Phenotype.ValueStatus.OUT_OF_SYNC);
					this.daoFactory.getPhenotypeDAO().saveOrUpdate(value);
				}
			}
		}
	}

	private Phenotype findPhenotypeByTermId(final Integer termId, final Set<Phenotype> phenotypes) {
		return (Phenotype) CollectionUtils.find(phenotypes, new Predicate() {

			@Override
			public boolean evaluate(final Object object) {
				final Phenotype dto = (Phenotype) object;
				return dto.getObservableId().equals(termId);
			}
		});
	}

	private Map<MeasurementVariable, List<MeasurementVariable>> getVariatesMapUsedInFormulas(
		final List<MeasurementVariable> measurementVariables) {
		final Map<MeasurementVariable, List<MeasurementVariable>> map = new HashMap<>();

		final Collection<MeasurementVariable> formulas = CollectionUtils.select(measurementVariables, new Predicate() {

			@Override
			public boolean evaluate(final Object o) {
				final MeasurementVariable measurementVariable = (MeasurementVariable) o;
				return measurementVariable.getFormula() != null;
			}
		});

		if (!formulas.isEmpty()) {
			for (final MeasurementVariable row : measurementVariables) {
				final List<MeasurementVariable> formulasFromCVTermId = this.getFormulasFromCVTermId(row, formulas);
				if (!formulasFromCVTermId.isEmpty()) {
					map.put(row, formulasFromCVTermId);
				}
			}
		}
		return map;
	}

	private List<MeasurementVariable> getFormulasFromCVTermId(
		final MeasurementVariable variable,
		final Collection<MeasurementVariable> measurementVariables) {
		final List<MeasurementVariable> result = new ArrayList<>();
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			if (measurementVariable.getFormula().isInputVariablePresent(variable.getTermId())) {
				result.add(measurementVariable);
			}
		}
		return result;
	}

	private Phenotype updatePhenotype(final Integer observationId, final Integer categoricalValueId, final String value) {
		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();

		final Phenotype phenotype = phenotypeDao.getById(observationId);
		phenotype.setValue(value);
		phenotype.setcValue(categoricalValueId == null || categoricalValueId == 0 ? null : categoricalValueId);
		final Integer observableId = phenotype.getObservableId();
		this.resolveObservationStatus(observableId, phenotype);
		phenotype.setChanged(true);
		phenotypeDao.update(phenotype);
		return phenotype;
	}

	private Phenotype createPhenotype(final ObservationDto observation) {
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

		if (Phenotype.ValueStatus.MANUALLY_EDITED.equals(phenotype.getValueStatus())) {
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
}
