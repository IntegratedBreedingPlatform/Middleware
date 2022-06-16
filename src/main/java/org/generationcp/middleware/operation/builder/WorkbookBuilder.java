/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.builder;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.TreatmentVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.NonEditableFactors;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WorkbookBuilder extends Builder {

	private static final List<Integer> CHARACTER_TYPE_TERM_IDS = Arrays.asList(TermId.CHARACTER_VARIABLE.getId(),
		TermId.TIMESTAMP_VARIABLE.getId(), TermId.CHARACTER_DBID_VARIABLE.getId(),
		TermId.CATEGORICAL_VARIABLE.getId(), TermId.PERSON_DATA_TYPE.getId(), TermId.LOCATION_DATA_TYPE.getId(),
		TermId.STUDY_DATA_TYPE.getId(), TermId.DATASET_DATA_TYPE.getId(), TermId.GERMPLASM_LIST_DATA_TYPE.getId(),
		TermId.BREEDING_METHOD_DATA_TYPE.getId());

	public static final List<Integer> EXPERIMENTAL_DESIGN_VARIABLES = Arrays.asList(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
		TermId.NUMBER_OF_REPLICATES.getId(), TermId.BLOCK_SIZE.getId(), TermId.BLOCKS_PER_REPLICATE.getId(),
		TermId.PERCENTAGE_OF_REPLICATION.getId(),
		TermId.REPLICATIONS_MAP.getId(), TermId.NO_OF_REPS_IN_COLS.getId(), TermId.NO_OF_ROWS_IN_REPS.getId(),
		TermId.NO_OF_COLS_IN_REPS.getId(), TermId.NO_OF_CROWS_LATINIZE.getId(), TermId.NO_OF_CCOLS_LATINIZE.getId(),
		TermId.NO_OF_CBLKS_LATINIZE.getId(), TermId.EXPT_DESIGN_SOURCE.getId(), TermId.NBLKS.getId(),
		TermId.CHECK_PLAN.getId(), TermId.CHECK_INTERVAL.getId(), TermId.CHECK_START.getId());

	private static final Logger LOG = LoggerFactory.getLogger(WorkbookBuilder.class);

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private DataSetBuilder dataSetBuilder;

	private DaoFactory daoFactory;

	public WorkbookBuilder() {

	}

	public WorkbookBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public void loadObservations(final Workbook workbook, final List<Integer> instanceNumbers, final List<Integer> repNumbers) {
		final Integer dataSetId = workbook.getMeasurementDatesetId();
		final VariableTypeList variables = this.dataSetBuilder.getVariableTypes(dataSetId);
		final List<Experiment> experiments =
			this.studyDataManager.getExperimentsWithGidAndCross(dataSetId, instanceNumbers, repNumbers);
		final Map<Integer, String> samples = this.getExperimentSampleMap(workbook.getStudyDetails().getId());
		// Do not rely on workbook variates, instead query the latest record from DB
		final List<MeasurementVariable> selectionsAndTraits = this.daoFactory.getDmsProjectDAO()
			.getObservationSetVariables(dataSetId, Arrays.asList(VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId()));

		this.addFactorToWorkbookIfNotPresent(workbook, TermId.GID);
		// Forcing to add CROSS variable because we need cross values to show them in Advance Study > REVIEW ADVANCED LINES
		this.addFactorToWorkbookIfNotPresent(workbook, TermId.CROSS);

		workbook.setObservations(this.buildObservations(experiments, variables.getVariates(), workbook.getFactors(), selectionsAndTraits,
			workbook.getConditions(), samples));
	}

	private void addFactorToWorkbookIfNotPresent(final Workbook workbook, final TermId termId) {
		final boolean factorPresent =
			workbook.getFactors().stream().anyMatch(measurementVariable -> measurementVariable.getTermId() == termId.getId());
		if (!factorPresent) {
			workbook.getFactors().add(new MeasurementVariable(termId.getId()));
		}
	}

	public Workbook create(final int id) {

		final Monitor monitor = MonitorFactory.start("Build Workbook");

		final Workbook workbook = new Workbook();

		/**
		 * 1. Get the dataset id 2. Count total no. of experiments of the
		 * dataset 3. getExperiments 4. Per experiment, transform it to
		 * MeasurementRow a. MeasurementRow (list of MeasurementData) b.
		 * MeasurementData label (Experiment > VariableList > Variable >
		 * localName), value (Experiment > VariableList > Variable), datatype
		 * (Experiment > VariableList > Variable > VariableType >
		 * StandardVariable), iseditable (true for variates, else, false)
		 */

		// DA
		final StudyDetails studyDetails = this.studyDataManager.getStudyDetails(id);

		// DA getDMSProject
		final Study study = this.getStudyBuilder().createStudy(id);

		// DA if name not conventional
		// FIXME : this heavy id fetch pattern needs changing
		final int dataSetId = this.getMeasurementDataSetId(id);
		// validation, bring inline
		this.checkMeasurementDataset(Integer.valueOf(dataSetId));
		workbook.setMeasurementDatesetId(dataSetId);

		// Variables required to get Experiments (?)
		VariableTypeList variables = this.dataSetBuilder.getVariableTypes(dataSetId);

		// FIXME : this heavy id fetch pattern needs changing
		final DmsProject trialDataSetProject = this.dataSetBuilder.getTrialDataset(study.getId());
		final DataSet trialDataSet = this.dataSetBuilder.build(trialDataSetProject.getProjectId());
		workbook.setTrialDatasetId(trialDataSet.getId());

		final VariableList conditionVariables = study.getConditions();
		final VariableList constantVariables = study.getConstants();
		final VariableList trialConstantVariables = this.getTrialConstants(trialDataSet);

		// the trialEnvironmentVariables are filtered from the TrialDataset
		final VariableList trialEnvironmentVariables = this.getTrialEnvironmentVariableList(trialDataSet);
		// FIXME : I think we are reducing to traits, but difficult to
		// understand
		variables = this.removeTrialDatasetVariables(variables, trialEnvironmentVariables);
		final Set<MeasurementVariable> conditions = this.buildConditionVariables(conditionVariables, trialEnvironmentVariables);
		final List<MeasurementVariable> factors = this.buildFactors(variables);
		final List<MeasurementVariable> entryDetails = this.buildEntryDetails(variables);
		final Set<MeasurementVariable> constants = this.buildStudyMeasurementVariables(constantVariables, false, true);
		constants.addAll(this.buildStudyMeasurementVariables(trialConstantVariables, false, false));
		final List<MeasurementVariable> variates = this.buildVariates(variables, new ArrayList<>(constants));
		final List<MeasurementVariable> expDesignVariables = new ArrayList<>();

		this.populateBreedingMethodPossibleValues(variates);

		final List<TreatmentVariable> treatmentFactors = this.buildTreatmentFactors(variables);
		final List<ProjectProperty> projectProperties = trialDataSetProject.getProperties();

		final Map<Integer, VariableType> projectPropRoleMapping = this
			.generateProjectPropertyRoleMap(projectProperties);

		final GeolocationPropertyDao geolocationPropertyDao = this.daoFactory.getGeolocationPropertyDao();
		for (final ProjectProperty projectProperty : projectProperties) {
			// FIXME DA IN A LOOP
			final StandardVariable stdVariable = this.getStandardVariableBuilder()
				.create(projectProperty.getVariableId(), study.getProgramUUID());

			final int stdVariableId = stdVariable.getId();

			Double minRange = null, maxRange = null;
			if (stdVariable.getConstraints() != null) {
				minRange = stdVariable.getConstraints().getMinValue();
				maxRange = stdVariable.getConstraints().getMaxValue();
			}

			final VariableType varType = projectPropRoleMapping.get(stdVariableId);
			if (varType != null) {
				stdVariable.setPhenotypicType(varType.getRole());


				if (WorkbookBuilder.EXPERIMENTAL_DESIGN_VARIABLES.contains(stdVariableId)) {
					String value = projectProperty.getValue();
					// During import of study, experiment design values are not set in ProjectProperty so we resolve them from GeolocationProperty
					if (value == null && VariableType.ENVIRONMENT_DETAIL.equals(varType)) {
						value = geolocationPropertyDao.getGeolocationPropValue(stdVariableId, id);
					}

					final MeasurementVariable measurementVariable =
						this.createMeasurementVariable(stdVariable, projectProperty, value, minRange, maxRange, varType);

					expDesignVariables.add(measurementVariable);
					this.setValueInCondition(new ArrayList<>(conditions), value, stdVariableId);
				}
			}
		}

		workbook.setStudyDetails(studyDetails);
		workbook.setFactors(factors);
		workbook.setEntryDetails(entryDetails);
		workbook.setVariates(variates);
		workbook.setConditions(new ArrayList<>(conditions));
		workbook.setConstants(new ArrayList<>(constants));
		workbook.setTreatmentFactors(treatmentFactors);
		workbook.setExperimentalDesignVariables(expDesignVariables);

		final List<MeasurementRow> trialObservations = this.getTrialObservations(workbook);
		workbook.setTrialObservations(trialObservations);
		WorkbookBuilder.LOG.debug(StringUtils.EMPTY + monitor.stop() + ". This instance was for studyId: " + id);

		return workbook;
	}

	private void populateBreedingMethodPossibleValues(final List<MeasurementVariable> variates) {
		final Monitor monitor = MonitorFactory.start("OpenTrial.bms.middleware.WorkbookBuilder.populateBreedingMethodPossibleValues");

		try {
			final CVTerm breedingMethodProperty = this.daoFactory.getCvTermDao().getById(TermId.BREEDING_METHOD_PROP.getId());
			List<ValueReference> possibleBreedingMethodValues = null;
			for (final MeasurementVariable variable : variates) {
				if (variable.getProperty().equals(breedingMethodProperty.getName())) {
					if (possibleBreedingMethodValues == null) {
						// Query only once on first match and reuse for
						// subsequent matches.
						possibleBreedingMethodValues = this.getAllBreedingMethods();
					}
					variable.setPossibleValues(possibleBreedingMethodValues);
				}
			}
		} finally {
			monitor.stop();
		}
	}

	protected Set<MeasurementVariable> buildConditionVariables(
		final VariableList studyConditionVariables,
		final VariableList trialEnvironmentVariables) {
		// we set roles here (study, trial, variate) which seem to match the
		// dataset : reconcile - we might be over-categorizing
		final Set<MeasurementVariable> conditions = this.buildStudyMeasurementVariables(studyConditionVariables, true,
			true);
		conditions.addAll(this.buildStudyMeasurementVariables(trialEnvironmentVariables, true, false));
		return conditions;
	}

	private List<MeasurementRow> getTrialObservations(final Workbook workbook) {
		final List<MeasurementRow> trialObservations;
		trialObservations = this.dataSetBuilder.buildCompleteDataset(workbook.getTrialDatasetId()).getObservations();
		return trialObservations;
	}

	protected void checkMeasurementDataset(final Integer dataSetId) {
		// if study has no measurementDataset, throw an error as it is an
		// invalid template
		if (dataSetId == null || dataSetId.equals(0)) {
			throw new MiddlewareQueryException(
				ErrorCode.STUDY_FORMAT_INVALID.getCode(),
				"The term you entered is invalid");
		}
	}

	private void setValueInCondition(final List<MeasurementVariable> conditions, final String value, final int id) {
		if (conditions != null && !conditions.isEmpty()) {
			for (final MeasurementVariable condition : conditions) {
				if (condition.getTermId() == id) {
					condition.setValue(value);
					break;
				}
			}
		}
	}

	private Map<Integer, VariableType> generateProjectPropertyRoleMap(final List<ProjectProperty> projectProperties) {
		final Map<Integer, VariableType> projPropRoleMap = new HashMap<>();
		for (final ProjectProperty projectProp : projectProperties) {
			if (VariableType.getById(projectProp.getTypeId()) != null) {
				final VariableType varType = VariableType.getById(projectProp.getTypeId());
				projPropRoleMap.put(projectProp.getVariableId(), varType);
			}
		}
		return projPropRoleMap;
	}

	public Workbook createStudyVariableSettings(final int id) {
		final Workbook workbook = new Workbook();
		final Study study = this.getStudyBuilder().createStudy(id);
		Integer dataSetId = null, trialDatasetId = null;

		final StudyDetails studyDetails = this.studyDataManager.getStudyDetails(id);
		workbook.setStudyDetails(studyDetails);

		final DataSet plotDataset = this.studyDataManager.findOneDataSetByType(id, DatasetTypeEnum.PLOT_DATA.getId());
		if (plotDataset != null) {
			dataSetId = plotDataset.getId();
		}

		final DataSet dataset = this.studyDataManager.findOneDataSetByType(id, DatasetTypeEnum.SUMMARY_DATA.getId());
		if (dataset != null) {
			trialDatasetId = dataset.getId();
		}

		this.checkMeasurementDataset(dataSetId);

		workbook.setMeasurementDatesetId(dataSetId);
		workbook.setTrialDatasetId(trialDatasetId);

		VariableTypeList variables = null;
		if (dataSetId != null) {
			variables = this.dataSetBuilder.getVariableTypes(dataSetId);
		}

		final List<MeasurementVariable> factors = this.buildFactors(variables);
		List<MeasurementVariable> variates = this.buildVariates(variables);
		final Set<MeasurementVariable> conditions = this.buildStudyMeasurementVariables(study.getConditions(), true, true);
		final Set<MeasurementVariable> constants = this.buildStudyMeasurementVariables(study.getConstants(), false, true);
		final List<TreatmentVariable> treatmentFactors = this.buildTreatmentFactors(variables);
		final List<MeasurementVariable> entryDetails = this.buildEntryDetails(variables);
		if (dataSetId != null) {
			this.setTreatmentFactorValues(treatmentFactors, dataSetId);
		}
		final DmsProject dmsProject = this.dataSetBuilder.getTrialDataset(id);
		final List<MeasurementVariable> experimentalDesignVariables = new ArrayList<>();
		final List<ProjectProperty> projectProperties = dmsProject != null ? dmsProject.getProperties()
			: new ArrayList<ProjectProperty>();
		final Map<Integer, VariableType> projectPropRoleMapping = this
			.generateProjectPropertyRoleMap(projectProperties);

		/**
		 * TODO Extract common code with
		 * {@link WorkbookBuilder#create(int, StudyType)}
		 */

		for (final ProjectProperty projectProperty : projectProperties) {
			boolean isConstant = false;
			final StandardVariable stdVariable = this.getStandardVariableBuilder()
				.create(projectProperty.getVariableId(), study.getProgramUUID());
			final VariableType varType = projectPropRoleMapping.get(stdVariable.getId());
			if (varType != null) {
				stdVariable.setPhenotypicType(varType.getRole());

				if (PhenotypicType.TRIAL_ENVIRONMENT == varType.getRole()
					|| PhenotypicType.VARIATE == varType.getRole()) {

					Double minRange = null, maxRange = null;
					if (stdVariable.getConstraints() != null) {
						minRange = stdVariable.getConstraints().getMaxValue();
						maxRange = stdVariable.getConstraints().getMaxValue();
					}

					String value = null;
					if (PhenotypicType.TRIAL_ENVIRONMENT == varType.getRole()) {
						value = projectProperty.getValue();
						if (value == null) {
							value = StringUtils.EMPTY;
						}
					} else if (PhenotypicType.VARIATE == varType.getRole()) {// TODO traits
						// constants, no need to retrieve the value if it's a trial study
						isConstant = true;
						value = StringUtils.EMPTY;
					}

					if (value != null) {
						final MeasurementVariable measurementVariable = this.createMeasurementVariable(stdVariable,
							projectProperty, value, minRange, maxRange, varType);
						if (WorkbookBuilder.EXPERIMENTAL_DESIGN_VARIABLES.contains(stdVariable.getId())) {
							experimentalDesignVariables.add(measurementVariable);
						} else if (isConstant) {
							constants.add(measurementVariable);
						} else {
							conditions.add(measurementVariable);
						}
					}
				}
			}
		}

		variates = this.removeConstantsFromVariates(variates, new ArrayList<>(constants));
		workbook.setFactors(factors);
		workbook.setVariates(variates);
		workbook.setConditions(new ArrayList<>(conditions));
		workbook.setConstants(new ArrayList<>(constants));
		workbook.setTreatmentFactors(treatmentFactors);
		workbook.setEntryDetails(entryDetails);
		workbook.setExperimentalDesignVariables(experimentalDesignVariables);
		return workbook;
	}

	protected MeasurementVariable createMeasurementVariable(
		final StandardVariable stdVariable,
		final ProjectProperty projectProperty, final String value, final Double minRange, final Double maxRange,
		final VariableType varType) {
		final MeasurementVariable measurementVariable = new MeasurementVariable(stdVariable.getId(),
			projectProperty.getAlias(), stdVariable.getDescription(), stdVariable.getScale().getName(),
			stdVariable.getMethod().getName(), stdVariable.getProperty().getName(),
			stdVariable.getDataType().getName(), value, StringUtils.EMPTY, minRange, maxRange);
		measurementVariable.setFactor(true);
		measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
		measurementVariable.setPossibleValues(
			this.getMeasurementVariableTransformer().transformPossibleValues(stdVariable.getEnumerations()));
		measurementVariable.setRole(varType.getRole());
		measurementVariable.setVariableType(varType);
		return measurementVariable;
	}

	private List<MeasurementRow> buildObservations(
		final List<Experiment> experiments, final VariableTypeList variateTypes,
		final List<MeasurementVariable> factorList, final List<MeasurementVariable> variateList,
		final List<MeasurementVariable> conditionList, final Map<Integer, String> samplesMap) {

		final List<MeasurementRow> observations = new ArrayList<>();
		for (final Experiment experiment : experiments) {
			final int experimentId = experiment.getId();
			final VariableList factors = experiment.getFactors();
			final VariableList variates = this.getCompleteVariatesInExperiment(experiment, variateTypes);
			final List<MeasurementData> measurementDataList = new ArrayList<>();

			for (final MeasurementVariable condition : conditionList) {
				for (final Variable variable : factors.getVariables()) {
					if (condition.getTermId() == variable.getVariableType().getStandardVariable().getId()
						&& variable.getVariableType().getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {

						final boolean isEditable = NonEditableFactors.isEditable(variable.getVariableType().getStandardVariable().getId());
						final String dataType = this.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId());

						final MeasurementData measurementData =
							new MeasurementData(variable.getVariableType().getLocalName(), variable.getValue(), isEditable, dataType,
								condition);

						measurementDataList.add(measurementData);
						break;
					}
				}
			}
			this.addMeasurementDataForFactors(factorList, experiment, factors, measurementDataList);

			measurementDataList.add(this.getMeasurementDataWithSample(samplesMap, experimentId));

			this.populateMeasurementData(variateList, variates, measurementDataList);

			final MeasurementRow measurementRow = new MeasurementRow(measurementDataList);
			measurementRow.setExperimentId(experimentId);
			measurementRow.setLocationId(experiment.getLocationId());

			observations.add(measurementRow);
		}

		return observations;
	}

	void addMeasurementDataForFactors(
		final List<MeasurementVariable> factorList, final Experiment experiment, final VariableList factors,
		final List<MeasurementData> measurementDataList) {
		for (final MeasurementVariable factor : factorList) {
			boolean found = false;
			for (final Variable variable : factors.getVariables()) {
				if (factor.getTermId() == variable.getVariableType().getStandardVariable().getId()) {
					found = true;
					if (variable.getVariableType().getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR
						.getId()
						|| PhenotypicType.TRIAL_ENVIRONMENT != variable.getVariableType().getRole()) {
						final boolean isEditable = NonEditableFactors
							.isEditable(variable.getVariableType().getStandardVariable().getId());
						final String dataType = this
							.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId());
						final String localName = variable.getVariableType().getLocalName();

						final MeasurementData measurementData;

						if (variable.getVariableType().getStandardVariable().getDataType()
							.getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
							final Integer id = NumberUtils.isNumber(variable.getValue())
								? Integer.valueOf(variable.getValue()) : null;

							measurementData = new MeasurementData(localName, variable.getActualValue(), isEditable,
								dataType, id, factor);
						} else {
							measurementData = new MeasurementData(localName, variable.getValue(), isEditable, dataType,
								factor);
						}
						measurementDataList.add(measurementData);
						break;
					}
				}
			}
			if (!found) {
				final boolean isEditable = NonEditableFactors.isEditable(factor.getTermId());
				final String dataType = this.getDataType(factor.getDataTypeId());
				final MeasurementData measurementData;

				if (factor.getTermId() == TermId.OBS_UNIT_ID.getId()) {
					final String obsUnitId = experiment.getObsUnitId();
					measurementData = new MeasurementData(factor.getName(), obsUnitId, isEditable, dataType, factor);
				} else {
					measurementData = new MeasurementData(factor.getName(), null, isEditable, dataType, factor);
				}
				measurementDataList.add(measurementData);
			}
		}
	}

	/**
	 * This method set a MeasurementData with the value of the samples. Is
	 * necessary because the SAMPLES TermId is not a real it was created in the
	 * code to set the column SAMPLES on MeasurementData.
	 *
	 * @param samplesMap
	 * @param experimentId
	 * @return MeasurementData
	 */
	protected MeasurementData getMeasurementDataWithSample(
		final Map<Integer, String> samplesMap,
		final int experimentId) {
		final MeasurementVariable measurementVariable = new MeasurementVariable();

		measurementVariable.setTermId(TermId.SAMPLES.getId());
		measurementVariable.setName(String.valueOf(TermId.SAMPLES.getId()));
		measurementVariable.setLabel(measurementVariable.getName());
		measurementVariable.setFactor(true);
		measurementVariable.setDataTypeId(DataType.CHARACTER_VARIABLE.getId());
		measurementVariable.setPossibleValues(new ArrayList<ValueReference>());
		final String sampleValue = samplesMap.get(experimentId);
		return new MeasurementData(String.valueOf(TermId.SAMPLES.getId()), sampleValue, false, "C",
			measurementVariable);

	}

	/**
	 * This method recovered the SAMPLES value by ExperimentId Key. Is necessary
	 * because the SAMPLES represent not existing TermId, so is the only way to
	 * recover this data.
	 *
	 * @param studyDbId
	 * @return
	 */
	private Map<Integer, String> getExperimentSampleMap(final Integer studyDbId) {
		return this.studyDataManager.getExperimentSampleMap(studyDbId);
	}

	protected void populateMeasurementData(
		final List<MeasurementVariable> variateList, final VariableList variates,
		final List<MeasurementData> measurementDataList) {
		for (final MeasurementVariable variate : variateList) {
			boolean found = false;

			for (final Variable variable : variates.getVariables()) {
				if (variate.getTermId() == variable.getVariableType().getStandardVariable().getId()) {
					found = true;
					final MeasurementData measurementData = new MeasurementData(
						variable.getVariableType().getLocalName(), variable.getValue(), true,
						this.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
						variate);
					measurementData.setMeasurementDataId(variable.getVariableDataId());
					measurementData.setAccepted(true);
					if (this.isCategoricalVariate(variable) && !variable.isCustomValue()
						&& NumberUtils.isNumber(variable.getValue())) {
						// we set the cValue id if the isCustomValue flag is
						// false, since this is an id of the valid value
						// we check if its a number to be sure
						measurementData.setcValueId(variable.getValue());
					}
					//FIXME get this information along with the variable to avoid going back to the database inside these loops
					this.setValueStatusToMeasurementData(variable, measurementData);
					measurementDataList.add(measurementData);
					break;
				}
			}
			if (!found) {
				final MeasurementData measurementData = new MeasurementData(variate.getName(), null, true,
					this.getDataType(variate.getDataTypeId()), variate);
				measurementDataList.add(measurementData);
			}
		}
	}

	private void setValueStatusToMeasurementData(final Variable variable, final MeasurementData measurementData) {
		final Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getById(variable.getVariableDataId());
		if (phenotype != null) {
			measurementData.setValueStatus(phenotype.getValueStatus());
		}
	}

	protected boolean isCategoricalVariate(final Variable variable) {
		final StandardVariable stdVar = variable.getVariableType().getStandardVariable();
		return PhenotypicType.VARIATE == stdVar.getPhenotypicType()
			&& stdVar.getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId();
	}

	private List<ValueReference> getAllBreedingMethods() {
		final List<ValueReference> list = new ArrayList<>();
		final List<Method> methodList = this.getGermplasmDataManager().getAllMethodsNotGenerative();

		Collections.sort(methodList, new Comparator<Method>() {

			@Override
			public int compare(final Method o1, final Method o2) {
				final String methodName1 = o1.getMname().toUpperCase();
				final String methodName2 = o2.getMname().toUpperCase();

				// ascending order
				return methodName1.compareTo(methodName2);
			}

		});

		if (!methodList.isEmpty()) {
			for (final Method method : methodList) {
				if (method != null) {
					list.add(new ValueReference(method.getMid(), method.getMname() + " - " + method.getMcode(),
						method.getMname() + " - " + method.getMcode()));
				}
			}
		}
		return list;
	}

	private String getDataType(final int dataTypeId) {
		// datatype ids: 1120, 1125, 1128, 1130
		if (WorkbookBuilder.CHARACTER_TYPE_TERM_IDS.contains(dataTypeId)) {
			return "C";
		} else {
			return "N";
		}
	}

	private Set<MeasurementVariable> buildStudyMeasurementVariables(
		final VariableList variableList, final boolean isFactor,
		final boolean isStudy) {
		final Set<MeasurementVariable> measurementVariableLists =
			this.getMeasurementVariableTransformer().transform(variableList, isFactor, isStudy);
		this.setMeasurementVarRoles(measurementVariableLists, isFactor, isStudy);
		return measurementVariableLists;
	}

	protected void setMeasurementVarRoles(
		final Set<MeasurementVariable> measurementVariableLists, final boolean isFactor,
		final boolean isStudy) {
		final PhenotypicType role;
		if (!isFactor) {
			// is factor == false, then always variate phenotype
			role = PhenotypicType.VARIATE;
		} else if (isStudy) {
			// if factor and is study
			role = PhenotypicType.STUDY;
		} else {
			// if factor and is not study
			role = PhenotypicType.TRIAL_ENVIRONMENT;
		}
		for (final MeasurementVariable var : measurementVariableLists) {
			var.setRole(role);
		}
	}

	List<TreatmentVariable> buildTreatmentFactors(final VariableTypeList variables) {
		final List<TreatmentVariable> treatmentFactors = new ArrayList<>();
		List<MeasurementVariable> factors;
		final Map<String, VariableTypeList> treatmentMap = new HashMap<>();
		if (variables != null && variables.getFactors() != null
			&& !variables.getFactors().getVariableTypes().isEmpty()) {
			for (final DMSVariableType variable : variables.getFactors().getVariableTypes()) {
				if (variable.getTreatmentLabel() != null
					&& !variable.getTreatmentLabel().isEmpty()) {
					VariableTypeList list = treatmentMap.get(variable.getTreatmentLabel());
					if (list == null) {
						list = new VariableTypeList();
						treatmentMap.put(variable.getTreatmentLabel(), list);
					}
					list.add(variable);
				}
			}

			final Set<String> keys = treatmentMap.keySet();
			for (final String key : keys) {
				factors = this.getMeasurementVariableTransformer().transform(treatmentMap.get(key), false);
				final TreatmentVariable treatment = new TreatmentVariable();
				for (final MeasurementVariable factor : factors) {
					if (factor.getName().equals(key)) {
						treatment.setLevelVariable(factor);
					} else {
						treatment.setValueVariable(factor);
					}
				}
				treatmentFactors.add(treatment);
			}
		}

		return treatmentFactors;
	}

	private List<MeasurementVariable> buildFactors(final VariableTypeList variables) {
		List<MeasurementVariable> factors = new ArrayList<>();
		final VariableTypeList factorList = new VariableTypeList();
		if (variables != null && variables.getFactors() != null
			&& !variables.getFactors().getVariableTypes().isEmpty()) {

			for (final DMSVariableType variable : variables.getFactors().getVariableTypes()) {
				if (PhenotypicType.TRIAL_DESIGN == variable.getRole() || PhenotypicType.GERMPLASM == variable.getRole()
					|| PhenotypicType.TRIAL_ENVIRONMENT == variable.getRole()) {

					factorList.add(variable);
				}
			}
			factors = this.getMeasurementVariableTransformer().transform(factorList, true);
		}
		return factors;
	}

	private List<MeasurementVariable> buildEntryDetails(final VariableTypeList variables) {
		if (variables != null) {
			return variables.getVariableTypes().stream()
				.filter(variableType -> variableType.getVariableType() == VariableType.ENTRY_DETAIL)
				.map(variableType -> this.getMeasurementVariableTransformer().transform(variableType, false, false))
				.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	private List<MeasurementVariable> removeConstantsFromVariates(
		final List<MeasurementVariable> variates,
		final List<MeasurementVariable> constants) {
		final List<MeasurementVariable> newVariates = new ArrayList<>();
		if (variates != null && !variates.isEmpty()) {
			for (final MeasurementVariable variate : variates) {
				boolean found = false;
				if (constants != null && !constants.isEmpty()) {
					for (final MeasurementVariable constant : constants) {
						if (variate.getTermId() == constant.getTermId()) {
							found = true;
						}
					}
				}
				if (!found) {
					newVariates.add(variate);
				}
			}
		}
		return newVariates;
	}

	private List<MeasurementVariable> buildVariates(final VariableTypeList variables) {
		return this.buildVariates(variables, null);
	}

	private List<MeasurementVariable> buildVariates(
		final VariableTypeList variables,
		final List<MeasurementVariable> constants) {
		List<MeasurementVariable> variates = new ArrayList<>();
		final VariableTypeList filteredVariables;

		if (variables != null && variables.getVariates() != null
			&& !variables.getVariates().getVariableTypes().isEmpty()) {
			final List<String> constantHeaders = new ArrayList<>();
			if (constants != null) {
				for (final MeasurementVariable constant : constants) {
					constantHeaders.add(constant.getName());
				}
				filteredVariables = new VariableTypeList();
				for (final DMSVariableType variable : variables.getVariableTypes()) {
					if (!constantHeaders.contains(variable.getLocalName())) {
						filteredVariables.add(variable);
					}
				}
			} else {
				filteredVariables = variables;
			}

			if (!filteredVariables.isEmpty()) {
				variates = this.getMeasurementVariableTransformer().transform(filteredVariables.getVariates(), false);
			}
		}

		return variates;
	}

	private VariableList getCompleteVariatesInExperiment(
		final Experiment experiment,
		final VariableTypeList variateTypes) {
		final VariableList vlist = new VariableList();

		for (final DMSVariableType vType : variateTypes.getVariableTypes()) {
			boolean found = false;

			// added for optimization
			final String key = Integer.toString(vType.getId());
			final Variable var = experiment.getVariatesMap().get(key);
			if (var != null) {
				vlist.add(var);
				found = true;
			}
			if (!found) {
				vlist.add(new Variable(vType, (String) null));
			}
		}

		return vlist;
	}

	protected VariableList getTrialEnvironmentVariableList(final DataSet trialDataset) {
		final VariableTypeList typeList = trialDataset.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		final VariableList list = new VariableList();
		for (final DMSVariableType type : typeList.getVariableTypes()) {
			list.add(new Variable(type, (String) null));
		}
		return list;
	}

	protected VariableList getTrialConstants(final DataSet trialDataSet) {
		final VariableTypeList typeList = trialDataSet.getVariableTypes().getVariates();

		final VariableList list = new VariableList();
		for (final DMSVariableType type : typeList.getVariableTypes()) {
			list.add(new Variable(type, (String) null));
		}
		return list;
	}

	protected VariableTypeList removeTrialDatasetVariables(
		final VariableTypeList variables,
		final VariableList toBeDeleted) {
		final List<Integer> trialList = new ArrayList<>();
		if (toBeDeleted != null && !toBeDeleted.isEmpty()) {
			for (final Variable variable : toBeDeleted.getVariables()) {
				trialList.add(variable.getVariableType().getStandardVariable().getId());
			}
		}

		final VariableTypeList list = new VariableTypeList();
		if (variables != null) {
			for (final DMSVariableType type : variables.getVariableTypes()) {
				if (!trialList.contains(type.getStandardVariable().getId())) {
					list.add(type);
				}
			}
		}
		return list;
	}

	public int getMeasurementDataSetId(final int studyId) {
		// Get dataset reference by dataset type
		final DatasetReference datasetRef = this.studyDataManager.findOneDataSetReferenceByType(
			studyId,
			DatasetTypeEnum.PLOT_DATA.getId());
		if (datasetRef != null) {
			return datasetRef.getId();
		} else {
			return 0;
		}
	}

	public int getTrialDataSetId(final int studyId) {
		final DataSet dataset = this.studyDataManager.findOneDataSetByType(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
		if (dataset != null) {
			return dataset.getId();
		} else {
			return 0;
		}
	}

	public List<MeasurementRow> buildDatasetObservations(
		final List<Experiment> experiments,
		final VariableTypeList variateTypes, final List<MeasurementVariable> factorList,
		final List<MeasurementVariable> variateList) {

		final List<MeasurementRow> observations = new ArrayList<>();
		for (final Experiment experiment : experiments) {
			final int experimentId = experiment.getId();
			final VariableList factors = experiment.getFactors();
			final VariableList variates = this.getCompleteVariatesInExperiment(experiment, variateTypes);

			final List<MeasurementData> measurementDataList = this.getMeasurementDataListFromFactors(experiment,
				factorList, factors);

			this.populateMeasurementData(variateList, variates, measurementDataList);

			final MeasurementRow measurementRow = new MeasurementRow(measurementDataList);
			measurementRow.setExperimentId(experimentId);
			measurementRow.setLocationId(experiment.getLocationId());

			observations.add(measurementRow);
		}

		return observations;
	}

	private List<MeasurementData> getMeasurementDataListFromFactors(
		final Experiment experiment, final List<MeasurementVariable> factorList,
		final VariableList factors) {
		final List<MeasurementData> measurementDataList = new ArrayList<>();

		for (final MeasurementVariable factor : factorList) {
			MeasurementData measurementData = this.getMeasurementDataFromFactorVariables(factors, factor);
			if (measurementData == null) {
				final boolean isEditable = NonEditableFactors.isEditable(factor.getTermId());
				String value = null;
				if (factor.getTermId() == TermId.OBS_UNIT_ID.getId()) {
					value = experiment.getObsUnitId();
				}
				measurementData = new MeasurementData(factor.getName(), value, isEditable,
					this.getDataType(factor.getDataTypeId()), null, factor);
			}
			measurementDataList.add(measurementData);
		}
		return measurementDataList;
	}

	private MeasurementData getMeasurementDataFromFactorVariables(final VariableList factors, final MeasurementVariable factor) {
		MeasurementData measurementData = null;
		for (final Variable variable : factors.getVariables()) {
			measurementData = this.getMeasurementDataFromVariable(factor, variable);
			if (measurementData != null) {
				break;
			}
		}
		return measurementData;
	}

	private MeasurementData getMeasurementDataFromVariable(final MeasurementVariable factor, final Variable variable) {
		final DMSVariableType variableType = variable.getVariableType();
		final StandardVariable standardVariable = variableType.getStandardVariable();

		if (factor.getTermId() == standardVariable.getId()) {
			final boolean isEditable = NonEditableFactors.isEditable(standardVariable.getId());

			final int standardVariableDataTypeId = standardVariable.getDataType().getId();
			final String value = variable.getValue();

			// BMS-2155 make sure that the value for EXP_DESIGN factor returned
			// is the ID and not the name
			if (standardVariableDataTypeId == TermId.CATEGORICAL_VARIABLE.getId()
				&& standardVariable.getId() != TermId.EXPERIMENT_DESIGN_FACTOR.getId()) {
				final Integer id = value != null && NumberUtils.isNumber(value) ? Integer.valueOf(value) : null;
				final MeasurementData measurementData = new MeasurementData(variableType.getLocalName(), variable.getDisplayValue(), isEditable,
					this.getDataType(standardVariableDataTypeId), id, factor);
				measurementData.setMeasurementDataId(variable.getVariableDataId());
				return measurementData;
			}

			final MeasurementData measurementData = new MeasurementData(variableType.getLocalName(), value, isEditable,
				this.getDataType(standardVariableDataTypeId), factor);
			measurementData.setMeasurementDataId(variable.getVariableDataId());
			return measurementData;
		}
		return null;
	}

	public void setTreatmentFactorValues(
		final List<TreatmentVariable> treatmentVariables,
		final int measurementDatasetId) {

		for (final TreatmentVariable treatmentVariable : treatmentVariables) {
			final List<String> values = this.daoFactory.getExperimentPropertyDao().getTreatmentFactorValues(
				treatmentVariable.getLevelVariable().getTermId(), treatmentVariable.getValueVariable().getTermId(),
				measurementDatasetId);
			treatmentVariable.setValues(values);
		}
	}

}
