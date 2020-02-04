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

package org.generationcp.middleware.operation.saver;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.PhenotypeExceptionDto;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.PhenotypeException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.WorkbookBuilder;
import org.generationcp.middleware.operation.transformer.etl.ExperimentValuesTransformer;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.TimerWatch;
import org.generationcp.middleware.util.Util;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ASsumptions - can be added to validations
// Mandatory fields: workbook.studyDetails.studyName
// template must not contain exact same combo of property-scale-method


public class WorkbookSaver extends Saver {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbookSaver.class);

	private static final String TRIALHEADERS = "trialHeaders";
	private static final String TRIALVARIABLETYPELIST = "trialVariableTypeList";
	private static final String TRIALVARIABLES = "trialVariables";
	private static final String EFFECTVARIABLE = "effectVariables";
	private static final String TRIALMV = "trialMV";
	private static final String EFFECTMV = "effectMV";
	private static final String HEADERMAP = "headerMap";
	private static final String VARIABLETYPEMAP = "variableTypeMap";
	private static final String MEASUREMENTVARIABLEMAP = "measurementVariableMap";
	public static final String ENVIRONMENT = "-ENVIRONMENT";
	public static final String PLOTDATA = "-PLOTDATA";

	private DaoFactory daoFactory;

	@Resource
	private DataSetBuilder dataSetBuilder;

	@Resource
	private WorkbookBuilder workbookBuilder;

	@Resource
	private StudyDataManager studyDataManager;

	public WorkbookSaver() {

	}

	public WorkbookSaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	/**
	 * This method transforms Variable data from a Fieldbook presented as an XLS
	 * style Workbook. - Variables new to the ontology are created and persisted
	 * - Columns and rows are transformed into entities suitables for
	 * persistence
	 * <p>
	 * Note : the result of this process is suitable for Dataset Creation
	 *
	 * @param workbook
	 * @return Map<String>, ?> : a map of 3 sub-maps containing
	 * Strings(headers), VariableTypeLists and Lists of
	 * MeasurementVariables
	 * @throws Exception
	 */

	@SuppressWarnings("rawtypes")
	public Map saveVariables(final Workbook workbook, final String programUUID) throws Exception {
		// make sure to reset all derived variables
		workbook.reset();

		// Create Maps, which we will fill with transformed Workbook Variable
		// Data
		final Map<String, List<String>> headerMap = new HashMap<>();
		final Map<String, VariableTypeList> variableTypeMap = new HashMap<>();
		final Map<String, List<MeasurementVariable>> measurementVariableMap = new HashMap<>();

		// GCP-6091 start
		final List<MeasurementVariable> trialMV = workbook.getTrialVariables();
		final List<String> trialHeaders = workbook.getTrialHeaders();
		final VariableTypeList trialVariables = this.getVariableTypeListTransformer().transform(workbook.getTrialConditions(), programUUID);
		final List<MeasurementVariable> trialFactors = workbook.getTrialFactors();
		VariableTypeList trialVariableTypeList = null;
		if (trialFactors != null && !trialFactors.isEmpty()) {// multi-location
			trialVariableTypeList = this.getVariableTypeListTransformer().transform(trialFactors, trialVariables.size() + 1, programUUID);
			trialVariables.addAll(trialVariableTypeList);
		}
		// GCP-6091 end
		trialVariables.addAll(this.getVariableTypeListTransformer()
			.transform(workbook.getTrialConstants(), trialVariables.size() + 1, programUUID));

		final VariableTypeList effectVariables =
			this.getVariableTypeListTransformer().transform(workbook.getNonTrialFactors(), programUUID);
		effectVariables
			.addAll(this.getVariableTypeListTransformer().transform(workbook.getVariates(), effectVariables.size() + 1, programUUID));

		// -- headers
		headerMap.put(WorkbookSaver.TRIALHEADERS, trialHeaders);
		// -- variableTypeLists
		variableTypeMap.put(WorkbookSaver.TRIALVARIABLETYPELIST, trialVariableTypeList);
		variableTypeMap.put(WorkbookSaver.TRIALVARIABLES, trialVariables);
		variableTypeMap.put(WorkbookSaver.EFFECTVARIABLE, effectVariables);
		// -- measurementVariables
		measurementVariableMap.put(WorkbookSaver.TRIALMV, trialMV);

		final List<MeasurementVariable> effectMV = workbook.getMeasurementDatasetVariables();
		measurementVariableMap.put(WorkbookSaver.EFFECTMV, effectMV);

		// load 3 maps into a super Map
		final Map<String, Map<String, ?>> variableMap = new HashMap<>();
		variableMap.put(WorkbookSaver.HEADERMAP, headerMap);
		variableMap.put(WorkbookSaver.VARIABLETYPEMAP, variableTypeMap);
		variableMap.put(WorkbookSaver.MEASUREMENTVARIABLEMAP, measurementVariableMap);
		return variableMap;
	}

	/**
	 * Dataset creation and persistence for Fieldbook upload
	 * <p>
	 * NOTE IMPORTANT : This step will fail if the Fieldbook has not had new
	 * Variables processed and new ontology terms created.
	 *
	 * @param workbook
	 * @param variableMap : a map of 3 sub-maps containing Strings(headers),
	 *                    VariableTypeLists and Lists of MeasurementVariables
	 * @return int (success/fail)
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int saveDataset(
		final Workbook workbook, final Map<String, ?> variableMap, final boolean retainValues,
		final boolean isDeleteObservations, final String programUUID, final CropType crop) throws Exception {

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList ,
		// Maps of Lists of MeasurementVariable
		final Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get(WorkbookSaver.HEADERMAP);
		final Map<String, VariableTypeList> variableTypeMap =
			(Map<String, VariableTypeList>) variableMap.get(WorkbookSaver.VARIABLETYPEMAP);
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
			(Map<String, List<MeasurementVariable>>) variableMap.get(WorkbookSaver.MEASUREMENTVARIABLEMAP);

		// unpack maps
		// Strings
		final List<String> trialHeaders = headerMap.get(WorkbookSaver.TRIALHEADERS);
		// VariableTypeLists
		final VariableTypeList trialVariableTypeList = variableTypeMap.get(WorkbookSaver.TRIALVARIABLETYPELIST);
		final VariableTypeList trialVariables = variableTypeMap.get(WorkbookSaver.TRIALVARIABLES);
		final VariableTypeList effectVariables = variableTypeMap.get(WorkbookSaver.EFFECTVARIABLE);
		// Lists of measurementVariables
		final List<MeasurementVariable> trialMV = measurementVariableMap.get(WorkbookSaver.TRIALMV);
		final List<MeasurementVariable> effectMV = measurementVariableMap.get(WorkbookSaver.EFFECTMV);

		// TODO : Review code and see whether variable validation and possible
		// dataset creation abort is a good idea (rebecca)

		final int studyId;
		if (!(workbook.getStudyDetails() != null && workbook.getStudyDetails().getId() != null)) {
			studyId = this.createStudyIfNecessary(workbook, true, programUUID, crop);
		} else {
			studyId = workbook.getStudyDetails().getId();
		}
		final Integer environmentDatasetId = this.createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables, programUUID);

		this.saveOrUpdateTrialObservations(crop, environmentDatasetId, workbook);

		final Integer plotDatasetId =
			this.createPlotDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables, programUUID);
		this.createStocksIfNecessary(plotDatasetId, workbook, effectVariables, trialHeaders);

		if (!retainValues) {
			// clean up some variable references to save memory space before
			// saving the measurement effects
			workbook.reset();
			workbook.setConditions(null);
			workbook.setConstants(null);
			workbook.setFactors(null);
			workbook.setStudyDetails(null);
			workbook.setVariates(null);
		} else {
			workbook.getStudyDetails().setId(studyId);
			workbook.setTrialDatasetId(environmentDatasetId);
			workbook.setMeasurementDatesetId(plotDatasetId);
		}

		this.createMeasurementEffectExperiments(crop, plotDatasetId, environmentDatasetId, effectVariables, workbook.getObservations(), trialHeaders);

		return studyId;
	}


	// Used in Design Import
	public void savePlotDataset(final Workbook workbook, final Map<String, ?> variableMap, final String programUUID, final CropType crop) {

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList ,
		// Maps of Lists of MeasurementVariable
		final Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get(WorkbookSaver.HEADERMAP);
		final Map<String, VariableTypeList> variableTypeMap =
			(Map<String, VariableTypeList>) variableMap.get(WorkbookSaver.VARIABLETYPEMAP);
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
			(Map<String, List<MeasurementVariable>>) variableMap.get(WorkbookSaver.MEASUREMENTVARIABLEMAP);

		final List<MeasurementVariable> trialMV = measurementVariableMap.get(WorkbookSaver.TRIALMV);

		// VariableTypeLists
		final VariableTypeList trialVariableTypeList = variableTypeMap.get(WorkbookSaver.TRIALVARIABLETYPELIST);
		final VariableTypeList trialVariables = variableTypeMap.get(WorkbookSaver.TRIALVARIABLES);
		final List<String> trialHeaders = headerMap.get(WorkbookSaver.TRIALHEADERS);
		final VariableTypeList effectVariables = variableTypeMap.get(WorkbookSaver.EFFECTVARIABLE);

		final Integer environmentDatasetId = this.workbookBuilder.getTrialDataSetId(workbook.getStudyDetails().getId());
		final Integer plotDatasetId = this.workbookBuilder.getMeasurementDataSetId(workbook.getStudyDetails().getId());
		final int studyId = workbook.getStudyDetails().getId();

		// TODO: IBP-3389 Check if we can remove the code that resets/delete trial experiments
//		this.getExperimentDestroyer().deleteExperimentsByStudy(plotDatasetId);
//		this.resetTrialObservations(workbook.getTrialObservations());
//
//		final ExperimentModel studyExperiment =
//			this.getExperimentDao().getExperimentsByProjectIds(Arrays.asList(studyId)).get(0);
//		this.getExperimentDao().saveOrUpdate(studyExperiment);
//
//		// delete trial observations
//		this.getExperimentDestroyer().deleteExperimentsByStudy(environmentDatasetId);

		this.saveOrUpdateTrialObservations(crop, environmentDatasetId, workbook);

		this.createStocksIfNecessary(plotDatasetId, workbook, effectVariables, trialHeaders);
		this.createMeasurementEffectExperiments(crop, plotDatasetId, environmentDatasetId, effectVariables, workbook.getObservations(), trialHeaders);

	}

	public void removeDeletedVariablesAndObservations(final Workbook workbook) {
		for (final MeasurementRow measurementRow : workbook.getTrialObservations()) {
			this.removeDeletedVariablesInObservations(workbook.getConstants(), workbook.getTrialObservations());
			this.removeDeletedVariablesInObservations(measurementRow.getMeasurementVariables(), workbook.getTrialObservations());
		}
		this.removeDeletedVariablesInObservations(workbook.getFactors(), workbook.getObservations());
		this.removeDeletedVariablesInObservations(workbook.getVariates(), workbook.getObservations());
		this.removeDeletedVariables(workbook.getConditions());
		this.removeDeletedVariables(workbook.getFactors());
		this.removeDeletedVariables(workbook.getVariates());
		this.removeDeletedVariables(workbook.getConstants());

	}

	private void removeDeletedVariablesInObservations(
		final List<MeasurementVariable> variableList,
		final List<MeasurementRow> observations) {
		final List<Integer> deletedList = new ArrayList<>();
		if (variableList != null) {
			for (final MeasurementVariable var : variableList) {
				if (var.getOperation() != null && var.getOperation().equals(Operation.DELETE)) {
					deletedList.add(Integer.valueOf(var.getTermId()));
				}
			}
		}
		if (observations != null) {
			for (final Integer deletedTermId : deletedList) {
				// remove from measurement rows
				int index = 0;
				int varIndex = 0;
				boolean found = false;
				for (final MeasurementRow row : observations) {
					if (index == 0) {
						for (final MeasurementData mData : row.getDataList()) {
							if (mData.getMeasurementVariable().getTermId() == deletedTermId.intValue()) {
								found = true;
								break;
							}
							varIndex++;
						}
					}
					if (found) {
						row.getDataList().remove(varIndex);
					} else {
						break;
					}
					index++;
				}
			}
		}
	}

	private void removeDeletedVariables(final List<MeasurementVariable> variableList) {
		if (variableList != null) {
			final Iterator<MeasurementVariable> itrMVariable = variableList.iterator();
			while (itrMVariable.hasNext()) {
				final MeasurementVariable mVariable = itrMVariable.next();
				if (mVariable.getOperation() != null && mVariable.getOperation().equals(Operation.DELETE)) {
					itrMVariable.remove();
				}
			}
		}
	}

	public void resetTrialObservations(final List<MeasurementRow> trialObservations) {
		for (final MeasurementRow row : trialObservations) {
			row.setExperimentId(0);
			row.setLocationId(0);
			row.setStockId(0);
			for (final MeasurementData data : row.getDataList()) {
				data.setPhenotypeId(null);
			}
		}
	}

	public void saveOrUpdateTrialObservations(
		final CropType crop, final int trialDatasetId, final Workbook workbook) {

		final Map<Integer, Integer> instanceNumberEnvironmentIdsMap = new HashMap<>();
		// Extract the trial environments from plot observations
		for (final MeasurementRow row : workbook.getObservations()) {
			final Integer instanceNumber = this.getTrialInstanceNumber(row);
			if (!instanceNumberEnvironmentIdsMap.containsKey(instanceNumber)) {
				// TODO IBP-3389 handle environment conditions (last parameter)
				final Integer environmentId = this.createTrialExperiment(crop, trialDatasetId, instanceNumber, new VariableList());
				instanceNumberEnvironmentIdsMap.put(instanceNumber, environmentId);
			}
			row.setLocationId(instanceNumberEnvironmentIdsMap.get(instanceNumber));
		}
	}


	protected void assignExptDesignAsExternallyGeneratedDesignIfEmpty(final VariableList variableList) {
		final Variable exptDesignVariable = variableList.findById(TermId.EXPERIMENT_DESIGN_FACTOR);

		if (exptDesignVariable != null) {
			if (StringUtils.isEmpty(exptDesignVariable.getValue())) {
				exptDesignVariable.setValue(String.valueOf(TermId.EXTERNALLY_GENERATED.getId()));
			}
		}
	}

	protected void assignLocationVariableWithUnspecifiedLocationIfEmptyOrInvalid(
		final VariableList variableList, final List<Location> locations) {
		final Variable locationIdVariable = variableList.findById(TermId.LOCATION_ID);

		if (locationIdVariable != null) {
			final List<Integer> locationId = new ArrayList<>();
			boolean locationIdExists = false;

			if (!StringUtils.isEmpty(locationIdVariable.getValue())) {
				locationId.add(Integer.valueOf(locationIdVariable.getValue()));
				locationIdExists = (daoFactory.getLocationDAO().getByIds(locationId).size() > 0) ? true : false;
			}
			if (StringUtils.isEmpty(locationIdVariable.getValue()) || !locationIdExists) {
				String unspecifiedLocationLocId = "";

				if (!locations.isEmpty()) {
					unspecifiedLocationLocId = String.valueOf(locations.get(0).getLocid());
				}
				locationIdVariable.setValue(unspecifiedLocationLocId);
			}
		}

	}

	void setVariableListValues(final VariableList variableList, final List<MeasurementVariable> measurementVariables) {
		if (measurementVariables != null) {
			for (final MeasurementVariable mvar : measurementVariables) {
				final Variable variable = variableList.findById(mvar.getTermId());
				if (variable != null && variable.getValue() == null) {
					variable.setValue(mvar.getValue());
				}
				this.setCategoricalVariableValues(mvar, variable);
			}
		}
	}

	// Sets the value of categorical variables to the key of the possible value
	// instead of its name
	void setCategoricalVariableValues(final MeasurementVariable mvar, final Variable variable) {
		if (variable != null && mvar.getPossibleValues() != null && !mvar.getPossibleValues().isEmpty()) {
			for (final ValueReference possibleValue : mvar.getPossibleValues()) {
				if (possibleValue.getName().equalsIgnoreCase(mvar.getValue())) {
					variable.setValue(possibleValue.getKey());
					break;
				}
			}
		}
	}

	private Integer getTrialInstanceNumber(final MeasurementRow row) {
		for (final MeasurementData data : row.getDataList()) {
			if (data.getMeasurementVariable().getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
				return Integer.valueOf(data.getValue());
			}
		}
		return null;
	}

	private String getStockFactor(final VariableList stockVariables) {
		if (stockVariables != null && stockVariables.getVariables() != null) {
			for (final Variable variable : stockVariables.getVariables()) {
				if (TermId.ENTRY_NO.getId() == variable.getVariableType().getStandardVariable().getId()) {
					return variable.getValue();
				}
			}
		}
		return null;
	}

	private String getTrialInstanceNumber(final VariableList trialVariables) {
		if (trialVariables != null && trialVariables.getVariables() != null) {
			for (final Variable variable : trialVariables.getVariables()) {
				if (TermId.TRIAL_INSTANCE_FACTOR.getId() == variable.getVariableType().getStandardVariable().getId()) {
					return variable.getValue();
				}
			}
		}
		return null;

	}

	private String generateTrialDatasetName(final String studyName) {
		return studyName + ENVIRONMENT;
	}

	private String generatePlotDatasetName(final String studyName) {
		return studyName + PLOTDATA;
	}

	private String generateMeansDatasetName(final String studyName) {
		return studyName + "-MEANS";
	}

	private ExperimentValues createTrialExperimentValues(final VariableList variates, final Integer instanceNumber) {
		final ExperimentValues value = new ExperimentValues();
		value.setVariableList(variates);
		value.setObservationUnitNo(instanceNumber);
		return value;
	}

	private int createStudyIfNecessary(
		final Workbook workbook, final boolean saveStudyExperiment,
		final String programUUID, final CropType crop) throws Exception {
		final TimerWatch watch = new TimerWatch("find study");

		Integer studyId = null;
		if (workbook.getStudyDetails() != null) {
			studyId = this.getDmsProjectDao().getProjectIdByNameAndProgramUUID(workbook.getStudyDetails().getStudyName(), programUUID);
		}

		if (studyId == null) {
			watch.restart("transform variables for study");
			final List<MeasurementVariable> studyMV = workbook.getStudyVariables();
			final VariableTypeList studyVariables =
				this.getVariableTypeListTransformer().transform(workbook.getStudyConditions(), programUUID);
			studyVariables.addAll(this.getVariableTypeListTransformer()
				.transform(workbook.getStudyConstants(), studyVariables.size() + 1, programUUID));

			final StudyValues studyValues = this.getStudyValuesTransformer().transform(studyMV, studyVariables);

			watch.restart("save study");

			//Recover the studyTypeDto if the id is null. Is necessary to save it in the project table.
			if (null == workbook.getStudyDetails().getStudyType().getId()) {
				final StudyTypeDto studyTypeDto =
					this.studyDataManager.getStudyTypeByName(workbook.getStudyDetails().getStudyType().getName());
				workbook.getStudyDetails().setStudyType(studyTypeDto);
			}

			final DmsProject study = this.getStudySaver()
				.saveStudy(crop, (int) workbook.getStudyDetails().getParentFolderId(), studyVariables, studyValues, saveStudyExperiment,
					programUUID, workbook.getStudyDetails().getStudyType(), workbook.getStudyDetails().getDescription(),
					workbook.getStudyDetails().getStartDate(), workbook.getStudyDetails().getEndDate(),
					workbook.getStudyDetails().getObjective(), workbook.getStudyDetails().getStudyName(),
					workbook.getStudyDetails().getCreatedBy());

			studyId = study.getProjectId();
		}
		watch.stop();

		return studyId;
	}

	private int createTrialDatasetIfNecessary(
		final Workbook workbook, final int studyId, final List<MeasurementVariable> trialMV,
		final VariableTypeList trialVariables, final String programUUID) {
		final TimerWatch watch = new TimerWatch("find trial dataset");
		String trialName = workbook.getStudyDetails().getTrialDatasetName();
		Integer datasetId = null;
		if (trialName == null || "".equals(trialName)) {

			final List<DataSet> dataSetsByType = this.studyDataManager.getDataSetsByType(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
			if (dataSetsByType != null && !CollectionUtils.isEmpty(dataSetsByType)) {
				datasetId = dataSetsByType.get(0).getId();
			}

			if (datasetId == null) {
				final String studyName = workbook.getStudyDetails().getStudyName();
				trialName = this.generateTrialDatasetName(studyName);
			}
		}

		if (datasetId == null) {
			watch.restart("transform trial dataset values");
			final String trialDescription = !workbook.getStudyDetails().getDescription().isEmpty() ?
				this.generateTrialDatasetName(workbook.getStudyDetails().getDescription()) :
				trialName;
			final DatasetValues trialValues = this.getDatasetValuesTransformer()
				.transform(trialName, trialDescription, trialMV, trialVariables);

			watch.restart("save trial dataset");
			final DmsProject trial =
				this.getDatasetProjectSaver().addDataSet(studyId, trialVariables, trialValues, programUUID, DatasetTypeEnum.SUMMARY_DATA.getId());
			datasetId = trial.getProjectId();
		}

		watch.stop();
		return datasetId;
	}

	private Integer createTrialExperiment(
		final CropType crop, final int trialProjectId, final Integer instanceNumber, final VariableList trialVariates) {
		final TimerWatch watch = new TimerWatch("save trial experiments");
		final ExperimentValues trialDatasetValues = this.createTrialExperimentValues(trialVariates, instanceNumber);
		// TODO IBP-3389 Add logic for default experiment design and unspecified location id
		final ExperimentModel experimentModel =
			this.getExperimentModelSaver().addExperiment(crop, trialProjectId, ExperimentType.TRIAL_ENVIRONMENT, trialDatasetValues);
		watch.stop();
		return experimentModel.getNdExperimentId();
	}

	private int createPlotDatasetIfNecessary(
		final Workbook workbook, final int studyId,
		final List<MeasurementVariable> effectMV, final VariableTypeList effectVariables, final VariableTypeList trialVariables,
		final String programUUID) {
		final TimerWatch watch = new TimerWatch("find plotdata dataset");

		String datasetName = workbook.getStudyDetails().getMeasurementDatasetName();
		Integer datasetId = null;

		if (datasetName == null || "".equals(datasetName)) {
			final List<DataSet> dataSetsByType = this.studyDataManager.getDataSetsByType(studyId, DatasetTypeEnum.PLOT_DATA.getId());
			if (dataSetsByType != null && !CollectionUtils.isEmpty(dataSetsByType)) {
				datasetId = dataSetsByType.get(0).getId();
			}

			if (datasetId == null) {
				final String studyName = workbook.getStudyDetails().getStudyName();
				datasetName = this.generatePlotDatasetName(studyName);
			}
		}

		if (datasetId == null) {
			watch.restart("transform measurement effect dataset");
			final String datasetDescription = !workbook.getStudyDetails().getDescription().isEmpty() ?
				this.generatePlotDatasetName(workbook.getStudyDetails().getDescription()) :
				datasetName;
			final DatasetValues datasetValues = this.getDatasetValuesTransformer()
				.transform(datasetName, datasetDescription, effectMV, effectVariables);

			watch.restart("save measurement effect dataset");
			// fix for GCP-6436 start
			final VariableTypeList datasetVariables = this.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);
			// no need to add occ as it is already added in trialVariables
			// fix for GCP-6436 end
			final DmsProject dataset =
				this.getDatasetProjectSaver().addDataSet(studyId, datasetVariables, datasetValues, programUUID, DatasetTypeEnum.PLOT_DATA.getId());
			datasetId = dataset.getProjectId();
		}

		watch.stop();
		return datasetId;
	}

	public void createStocksIfNecessary(
		final int datasetId, final Workbook workbook, final VariableTypeList effectVariables,
		final List<String> trialHeaders) {
		final Map<String, Integer> stockMap = this.getStockModelBuilder().getStockMapForDataset(datasetId);

		List<Integer> variableIndexesList = new ArrayList<>();
		// we get the indexes so that in the next rows we dont need to compare
		// anymore per row
		if (workbook.getObservations() != null && !workbook.getObservations().isEmpty()) {
			final MeasurementRow row = workbook.getObservations().get(0);
			variableIndexesList = this.getVariableListTransformer().transformStockIndexes(row, effectVariables, trialHeaders);
		}

		if (workbook.getObservations() != null) {
			final Session activeSession = this.getActiveSession();
			final FlushMode existingFlushMode = activeSession.getFlushMode();
			activeSession.setFlushMode(FlushMode.MANUAL);
			try {
				for (final MeasurementRow row : workbook.getObservations()) {

					final VariableList stock = this.getVariableListTransformer()
						.transformStockOptimize(variableIndexesList, row, effectVariables, trialHeaders);
					final String stockFactor = this.getStockFactor(stock);
					Integer stockId = stockMap.get(stockFactor);

					if (stockId == null) {
						stockId = this.getStockSaver().saveStock(stock);
						stockMap.put(stockFactor, stockId);
					} else {
						this.getStockSaver().saveOrUpdateStock(stock, stockId);
					}
					row.setStockId(stockId);
				}
				activeSession.flush();
			} finally {
				if (existingFlushMode != null) {
					activeSession.setFlushMode(existingFlushMode);
				}
			}
		}

	}

	// TODO IBP-3389 See if this can be consolidated with createMeansExperiments
	private void createMeasurementEffectExperiments(
		final CropType crop, final int plotDatasetId, final Integer environmentDatasetId, final VariableTypeList effectVariables,
		final List<MeasurementRow> observations, final List<String> trialHeaders) {

		final TimerWatch watch = new TimerWatch("saving stocks and measurement effect data (total)");
		final TimerWatch rowWatch = new TimerWatch("for each row");

		// observation values start at row 2
		int i = 2;

		final ExperimentValuesTransformer experimentValuesTransformer = this.getExperimentValuesTransformer();
		final ExperimentModelSaver experimentModelSaver = this.getExperimentModelSaver();
		Map<Integer, PhenotypeExceptionDto> exceptions = null;
		final Session activeSession = this.getActiveSession();
		final FlushMode existingFlushMode = activeSession.getFlushMode();
		final Map<Integer, Integer> instanceNumberEnvironmentIdMap = this.daoFactory.getEnvironmentDao().getEnvironmentsByDataset(environmentDatasetId).stream()
			.collect(Collectors.toMap(ExperimentModel::getObservationUnitNo, ExperimentModel::getNdExperimentId));
		try {
			activeSession.setFlushMode(FlushMode.MANUAL);
			if (observations != null) {
				for (final MeasurementRow row : observations) {
					rowWatch.restart("saving row " + i++);
					final ExperimentValues experimentValues = experimentValuesTransformer.transform(row, effectVariables, trialHeaders, instanceNumberEnvironmentIdMap);
					try {
						experimentModelSaver.addExperiment(crop, plotDatasetId, ExperimentType.PLOT, experimentValues);
					} catch (final PhenotypeException e) {
						WorkbookSaver.LOG.error(e.getMessage(), e);
						if (exceptions == null) {
							exceptions = e.getExceptions();
						} else {
							for (final Integer standardVariableId : e.getExceptions().keySet()) {
								final PhenotypeExceptionDto exception = e.getExceptions().get(standardVariableId);
								if (exceptions.get(standardVariableId) == null) {
									// add exception
									exceptions.put(standardVariableId, exception);
								} else {
									// add invalid values to the existing map of
									// exceptions for each phenotype
									for (final String invalidValue : exception.getInvalidValues()) {
										exceptions.get(standardVariableId).getInvalidValues().add(invalidValue);
									}
								}
							}
						}
					}
				}
			}
			activeSession.flush();
		} finally {
			if (existingFlushMode != null) {
				activeSession.setFlushMode(existingFlushMode);
			}
		}

		rowWatch.stop();
		watch.stop();

		if (exceptions != null) {
			throw new PhenotypeException(exceptions);
		}
	}

	private boolean isTrialFactorInDataset(final VariableTypeList list) {

		for (final DMSVariableType var : list.getVariableTypes()) {
			if (TermId.TRIAL_INSTANCE_FACTOR.getId() == var.getStandardVariable().getId()) {
				return true;
			}
		}
		return false;

	}

	protected VariableTypeList propagateTrialFactorsIfNecessary(
		final VariableTypeList effectVariables,
		final VariableTypeList trialVariables) {

		final VariableTypeList newList = new VariableTypeList();

		if (!this.isTrialFactorInDataset(effectVariables) && trialVariables != null) {
			int index = 1;
			for (final DMSVariableType var : trialVariables.getVariableTypes()) {
				if (var.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					var.setRank(index);
					newList.add(var);
					index++;
				}
			}

			effectVariables.allocateRoom(newList.size());
		}
		newList.addAll(effectVariables);

		return newList;
	}

	private Integer getMeansDataset(final Integer studyId) {
		Integer id = null;
		final List<DmsProject> datasets = this.getDmsProjectDao()
			.getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.MEANS_DATA.getId());
		if (datasets != null && !datasets.isEmpty()) {
			id = datasets.get(0).getProjectId();
		}
		return id;
	}

	/**
	 * Saves project ontology creating entries in the following tables: project and projectprop tables
	 *
	 * @param workbook
	 * @return study id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int saveProjectOntology(final Workbook workbook, final String programUUID, final CropType crop) throws Exception {

		final Map<String, ?> variableMap = this.saveVariables(workbook, programUUID);
		workbook.setVariableMap(variableMap);

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList ,
		// Maps of Lists of MeasurementVariable
		final Map<String, VariableTypeList> variableTypeMap =
			(Map<String, VariableTypeList>) variableMap.get(WorkbookSaver.VARIABLETYPEMAP);
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
			(Map<String, List<MeasurementVariable>>) variableMap.get(WorkbookSaver.MEASUREMENTVARIABLEMAP);

		// unpack maps
		final VariableTypeList trialVariables = new VariableTypeList();
		// addAll instead of assigning directly to avoid changing the state of
		// the object
		trialVariables.addAll(variableTypeMap.get(WorkbookSaver.TRIALVARIABLES));
		final VariableTypeList effectVariables = new VariableTypeList();
		// addAll instead of assigning directly to avoid changing the state of
		// the object
		effectVariables.addAll(variableTypeMap.get(WorkbookSaver.EFFECTVARIABLE));
		final List<MeasurementVariable> trialMV = measurementVariableMap.get(WorkbookSaver.TRIALMV);
		final List<MeasurementVariable> effectMV = measurementVariableMap.get(WorkbookSaver.EFFECTMV);

		// locationId and experiment are not yet needed here
		final int studyId = this.createStudyIfNecessary(workbook, false, programUUID, crop);
		final int trialDatasetId = this.createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables, programUUID);
		int measurementDatasetId = 0;
		int meansDatasetId = 0;

		if (workbook.getImportType() != null && workbook.getImportType().intValue() == DatasetTypeEnum.MEANS_DATA.getId()) {
			meansDatasetId = this.createMeansDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables, programUUID);
		} else {
			measurementDatasetId =
				this.createPlotDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables,
					programUUID);
		}

		workbook.getStudyDetails().setId(studyId);
		workbook.populateDatasetIds(trialDatasetId, measurementDatasetId, meansDatasetId);

		if (WorkbookSaver.LOG.isDebugEnabled()) {
			WorkbookSaver.LOG.debug("studyId = " + studyId);
			WorkbookSaver.LOG.debug("trialDatasetId = " + trialDatasetId);
			WorkbookSaver.LOG.debug("measurementDatasetId = " + measurementDatasetId);
			WorkbookSaver.LOG.debug("meansDatasetId = " + meansDatasetId);
		}

		return studyId;
	}

	/**
	 * Saves experiments creating entries in the following tables:
	 * nd_experiment, nd_experimentprop, stock, stockprop,
	 * and phenotype
	 *
	 * @param workbook
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void saveProjectData(final Workbook workbook, final String programUUID, final CropType crop) throws Exception {

		final int studyId = workbook.getStudyDetails().getId();
		final int trialDatasetId = workbook.getTrialDatasetId();
		final int measurementDatasetId = workbook.getMeasurementDatesetId() != null ? workbook.getMeasurementDatesetId() : 0;
		final int meansDatasetId = workbook.getMeansDatasetId() != null ? workbook.getMeansDatasetId() : 0;

		final boolean isMeansDataImport =
			workbook.getImportType() != null && workbook.getImportType().intValue() == DatasetTypeEnum.MEANS_DATA.getId();

		Map<String, ?> variableMap = workbook.getVariableMap();
		if (variableMap == null || variableMap.isEmpty()) {
			variableMap = this.saveVariables(workbook, programUUID);
		}

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList ,
		// Maps of Lists of MeasurementVariable
		final Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get(WorkbookSaver.HEADERMAP);
		final Map<String, VariableTypeList> variableTypeMap =
			(Map<String, VariableTypeList>) variableMap.get(WorkbookSaver.VARIABLETYPEMAP);
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
			(Map<String, List<MeasurementVariable>>) variableMap.get(WorkbookSaver.MEASUREMENTVARIABLEMAP);

		// unpack maps
		final List<String> trialHeaders = headerMap.get(WorkbookSaver.TRIALHEADERS);
		final VariableTypeList trialVariableTypeList = variableTypeMap.get(WorkbookSaver.TRIALVARIABLETYPELIST);
		final VariableTypeList trialVariables = variableTypeMap.get(WorkbookSaver.TRIALVARIABLES);
		final VariableTypeList effectVariables = variableTypeMap.get(WorkbookSaver.EFFECTVARIABLE);
		final List<MeasurementVariable> trialMV = measurementVariableMap.get(WorkbookSaver.TRIALMV);
		this.removeConstantsVariables(effectVariables, workbook.getConstants());


		// create stock and stockprops and associate to observations
		int datasetId = measurementDatasetId;
		if (isMeansDataImport) {
			datasetId = meansDatasetId;
		}
		this.createStocksIfNecessary(datasetId, workbook, effectVariables, trialHeaders);

		// create trial experiments if not yet existing
		final boolean hasExistingStudyExperiment = this.checkIfHasExistingStudyExperiment(studyId);
		final boolean hasExistingTrialExperiments = this.checkIfHasExistingExperiments(trialDatasetId);
		if (!hasExistingStudyExperiment) {
			// 1. study experiment
			final StudyValues values = new StudyValues();
			this.getStudySaver().saveStudyExperiment(crop, studyId, values);
		}
		// create trial experiments if not yet existing
		if (!hasExistingTrialExperiments) {
			// 2. trial experiments
			this.saveOrUpdateTrialObservations(crop, trialDatasetId, workbook);
		}
		if (isMeansDataImport) {
			// 3. means experiments
			this.createMeansExperiments(crop, meansDatasetId, trialDatasetId, effectVariables, workbook.getObservations(), trialHeaders);
		} else {
			// 3. measurement experiments
			this.createMeasurementEffectExperiments(crop, measurementDatasetId, trialDatasetId, effectVariables, workbook.getObservations(), trialHeaders);
		}
	}

	// The constants are not needed in the creation of stocks, means
	// experiments, and measurement effects experiments so we need to remove it
	void removeConstantsVariables(final VariableTypeList effectVariables, final List<MeasurementVariable> constants) {

		final List<DMSVariableType> variableTypes = new ArrayList<>();
		for (final DMSVariableType varType : effectVariables.getVariableTypes()) {
			boolean isConstant = false;
			for (final MeasurementVariable mvar : constants) {
				if (varType.getId() == mvar.getTermId()) {
					isConstant = true;
					break;
				}
			}
			if (!isConstant) {
				variableTypes.add(varType);
			}
		}
		effectVariables.setVariableTypes(variableTypes);

	}

	private boolean checkIfHasExistingStudyExperiment(final int studyId) {
		final Integer experimentId = this.getExperimentDao().getExperimentIdByProjectId(studyId);
		return experimentId != null;
	}

	private boolean checkIfHasExistingExperiments(final Integer projectId) {
		final List<Integer> experimentIds = this.daoFactory.getEnvironmentDao().getEnvironmentIds(projectId);
		return experimentIds != null && !experimentIds.isEmpty();
	}

	private VariableList createDefaultEnvironmentVariableList(final String programUUID) {
		final VariableList list = new VariableList();

		final DMSVariableType variableType = new DMSVariableType(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0),
			PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0),
			this.getStandardVariableBuilder().create(TermId.TRIAL_INSTANCE_FACTOR.getId(), programUUID), 1);
		final Variable variable = new Variable(variableType, "1");
		list.add(variable);

		return list;
	}

	public void saveWorkbookVariables(final Workbook workbook) throws ParseException {

		final int parentFolderId = (int) workbook.getStudyDetails().getParentFolderId();

		final DmsProject study = this.getDmsProjectDao().getById(workbook.getStudyDetails().getId());
		study.setParent(this.getDmsProjectDao().getById(parentFolderId));
		Integer trialDatasetId = workbook.getTrialDatasetId();
		Integer measurementDatasetId = workbook.getMeasurementDatesetId();
		if (workbook.getTrialDatasetId() == null || workbook.getMeasurementDatesetId() == null) {
			final Integer studyId = study.getProjectId();
			measurementDatasetId = this.workbookBuilder.getMeasurementDataSetId(studyId);
			trialDatasetId = this.workbookBuilder.getTrialDataSetId(studyId);
		}
		final DmsProject trialDataset = this.getDmsProjectDao().getById(trialDatasetId);
		final DmsProject measurementDataset = this.getDmsProjectDao().getById(measurementDatasetId);

		this.saveProjectProperties(workbook);

		final String description = workbook.getStudyDetails().getDescription();
		final String startDate = workbook.getStudyDetails().getStartDate();
		final String endDate = workbook.getStudyDetails().getEndDate();
		final String objective = workbook.getStudyDetails().getObjective();
		final String createdBy = workbook.getStudyDetails().getCreatedBy();

		this.updateStudyDetails(description + WorkbookSaver.ENVIRONMENT, trialDataset, objective);
		this.updateStudyDetails(description, startDate, endDate, study, objective, createdBy);
		this.updateStudyDetails(description + WorkbookSaver.PLOTDATA, measurementDataset, objective);
	}

	public void saveProjectProperties(final Workbook workbook) {
		final Integer studyId = workbook.getStudyDetails().getId();
		final Integer trialDatasetId = workbook.getTrialDatasetId();
		final Integer measurementDatasetId = workbook.getMeasurementDatesetId();

		final DmsProject study = this.getDmsProjectDao().getById(studyId);
		final DmsProject trialDataset = this.getDmsProjectDao().getById(trialDatasetId);
		final DmsProject measurementDataset = this.getDmsProjectDao().getById(measurementDatasetId);

		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getConditions(), false);
		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getConstants(), true);
		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getVariates(), false);
		this.getProjectPropertySaver().saveFactors(measurementDataset, workbook.getFactors());
	}

	private void updateStudyDetails(
		final String description, final String startDate, final String endDate, final DmsProject study,
		final String objective, final String createdBy) throws ParseException {

		if (study.getCreatedBy() == null) {
			study.setCreatedBy(createdBy);
		}

		if (startDate != null && startDate.contains("-")) {
			study.setStartDate(Util.convertDate(startDate, Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT));
		} else {
			study.setStartDate(startDate);
		}
		study.setStudyUpdate(Util.getCurrentDateAsStringValue(Util.DATE_AS_NUMBER_FORMAT));

		if (endDate != null && endDate.contains("-")) {
			study.setEndDate(Util.convertDate(endDate, Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT));
		} else {
			study.setEndDate(endDate);
		}

		this.updateStudyDetails(description, study, objective);
	}

	private void updateStudyDetails(final String description, final DmsProject study, final String objective) {
		study.setDescription(description);
		study.setObjective(objective);
		this.getDmsProjectDao().merge(study);
	}

	private int createMeansDatasetIfNecessary(
		final Workbook workbook, final int studyId, final List<MeasurementVariable> effectMV,
		final VariableTypeList effectVariables, final VariableTypeList trialVariables, final String programUUID) {

		final TimerWatch watch = new TimerWatch("find means dataset");
		Integer datasetId = this.getMeansDataset(studyId);

		if (datasetId == null) {
			watch.restart("transform means dataset");
			final String datasetName = this.generateMeansDatasetName(workbook.getStudyDetails().getStudyName());
			final String datasetDescription = this.generateMeansDatasetName(workbook.getStudyDetails().getDescription());
			final DatasetValues datasetValues = this.getDatasetValuesTransformer()
				.transform(datasetName, datasetDescription, effectMV, effectVariables);

			watch.restart("save means dataset");
			final VariableTypeList datasetVariables = this.getMeansData(effectVariables, trialVariables);
			final DmsProject dataset =
				this.getDatasetProjectSaver().addDataSet(studyId, datasetVariables, datasetValues, programUUID, DatasetTypeEnum.MEANS_DATA.getId());
			datasetId = dataset.getProjectId();
		}

		watch.stop();
		return datasetId;
	}

	private VariableTypeList getMeansData(final VariableTypeList effectVariables, final VariableTypeList trialVariables) {

		final VariableTypeList newList = new VariableTypeList();
		int rank = 1;
		for (final DMSVariableType var : trialVariables.getVariableTypes()) {
			var.setRank(rank++);
			newList.add(var);
		}
		for (final DMSVariableType var : effectVariables.getVariableTypes()) {
			var.setRank(rank++);
			newList.add(var);
		}
		return newList;
	}


	private void createMeansExperiments(
		final CropType crop, final int meansDatasetId, final Integer environmentDatasetId, final VariableTypeList effectVariables,
		final List<MeasurementRow> observations, final List<String> trialHeaders) {

		final TimerWatch watch = new TimerWatch("saving means data (total)");
		final TimerWatch rowWatch = new TimerWatch("for each row");

		// observation values start at row 2
		int i = 2;

		final ExperimentValuesTransformer experimentValuesTransformer = this.getExperimentValuesTransformer();
		final ExperimentModelSaver experimentModelSaver = this.getExperimentModelSaver();
		Map<Integer, PhenotypeExceptionDto> exceptions = null;
		final Map<Integer, Integer> instanceNumberEnvironmentIdMap = this.daoFactory.getEnvironmentDao().getEnvironmentsByDataset(environmentDatasetId).stream()
			.collect(Collectors.toMap(ExperimentModel::getObservationUnitNo, ExperimentModel::getNdExperimentId));
		if (observations != null) {
			for (final MeasurementRow row : observations) {
				rowWatch.restart("saving row " + i++);
				final ExperimentValues experimentValues = experimentValuesTransformer.transform(row, effectVariables, trialHeaders, instanceNumberEnvironmentIdMap);
				// TODO IBP-3389 See if below is relevant to means dataset
//				final VariableList trialVariates = trialVariatesMap.get((int) row.getLocationId());
//				if (trialVariates != null) {
//					experimentValues.getVariableList().addAll(trialVariates);
//				}
				try {
					experimentModelSaver.addExperiment(crop, meansDatasetId, ExperimentType.AVERAGE, experimentValues);
				} catch (final PhenotypeException e) {
					WorkbookSaver.LOG.error(e.getMessage(), e);
					if (exceptions == null) {
						exceptions = e.getExceptions();
					} else {
						for (final Integer standardVariableId : e.getExceptions().keySet()) {
							final PhenotypeExceptionDto exception = e.getExceptions().get(standardVariableId);
							if (exceptions.get(standardVariableId) == null) {
								// add exception
								exceptions.put(standardVariableId, exception);
							} else {
								// add invalid values to the existing map of
								// exceptions for each phenotype
								for (final String invalidValue : exception.getInvalidValues()) {
									exceptions.get(standardVariableId).getInvalidValues().add(invalidValue);
								}
							}
						}
					}
				}
			}
		}
		rowWatch.stop();
		watch.stop();

		if (exceptions != null) {
			throw new PhenotypeException(exceptions);
		}
	}
}
