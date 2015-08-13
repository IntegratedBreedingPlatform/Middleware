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

package org.generationcp.middleware.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.Constants;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.util.TimerWatch;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DataImportServiceImpl extends Service implements DataImportService {

	private static final Logger LOG = LoggerFactory.getLogger(DataImportServiceImpl.class);
	public static final int MAX_VARIABLE_NAME_LENGTH = 32;
	public static final String ERROR_MISSING_TRIAL_CONDITION = "error.missing.trial.condition";
	public static final String ERROR_PLOT_DOESNT_EXIST = "error.plot.doesnt.exist";
	public static final String ERROR_ENTRY_DOESNT_EXIST = "error.entry.doesnt.exist";
	public static final String ERROR_DUPLICATE_STUDY_NAME = "error.duplicate.study.name";
	public static final String ERROR_DUPLICATE_PSM = "error.duplicate.psm";
	public static final String ERROR_INVALID_VARIABLE_NAME_LENGTH = "error.invalid.variable.name.length";
	public static final String ERROR_INVALID_VARIABLE_NAME_CHARACTERS = "error.invalid.variable.name.characters";

	public DataImportServiceImpl() {
		super();
		
	}
	public DataImportServiceImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	/**
	 * Saves a Dataset from a Workbook into the database via 1. Saving new Ontology Variables (column headers) 2. Saving data
	 * <p/>
	 * The operation is performed in two separate transactions in order to force a Hibernate Session Flush, and thereby persist all required
	 * terms to the Ontology Tables
	 */
	@Override
	public int saveDataset(Workbook workbook, String programUUID) throws MiddlewareQueryException {
		return this.saveDataset(workbook, false, false, programUUID);
	}

	/**
	 * Transaction 1 : Transform Variables and save new Ontology Terms Send : xls workbook Return : Map of 3 sub maps with transformed
	 * allVariables (ontology fully loaded) - here is how it was loaded : -- headers : Strings // headerMap.put("trialHeaders",
	 * trialHeaders); // -- variableTypeLists (VariableTypeList) // variableTypeMap.put("trialVariableTypeList", trialVariableTypeList); //
	 * variableTypeMap.put("trialVariables", trialVariables); // variableTypeMap.put("effectVariables", effectVariables); // --
	 * measurementVariables (List<MeasurementVariable>) // measurementVariableMap.put("trialMV", trialMV); //
	 * measurementVariableMap.put("effectMV", effectMV);
	 * 
	 * @param workbook
	 * @param retainValues if true, values of the workbook items are retained, else they are cleared to conserve memory
	 * @param isDeleteObservations
	 * @return
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int saveDataset(Workbook workbook, boolean retainValues, boolean isDeleteObservations, String programUUID)
			throws MiddlewareQueryException {
		
		
		Map<String, ?> variableMap = null;
		TimerWatch timerWatch = new TimerWatch("saveDataset (grand total)");
		try {

			boolean isUpdate = workbook.getStudyDetails() != null && workbook.getStudyDetails().getId() != null;
			if (isUpdate) {
				this.getWorkbookSaver().saveWorkbookVariables(workbook);
				this.getWorkbookSaver().removeDeletedVariablesAndObservations(workbook);
			}
			variableMap = this.getWorkbookSaver().saveVariables(workbook,programUUID);


		} catch (Exception e) {

			this.logAndThrowException("Error encountered with saving to database: ", e, DataImportServiceImpl.LOG);

		} finally {
			timerWatch.stop();
		}

		// Transaction 2 : save data
		// Send : Map of 3 sub maps, with data to create Dataset
		// Receive int (success/fail)
		Transaction trans2 = null;

		try {



			int studyId = this.getWorkbookSaver().saveDataset(workbook, variableMap, retainValues, isDeleteObservations, programUUID);



			return studyId;

		} catch (Exception e) {

			this.logAndThrowException("Error encountered with saving to database: ", e, DataImportServiceImpl.LOG);

		} finally {
			timerWatch.stop();
		}

		return 0;
	}

	@Override
	public Workbook parseWorkbook(File file) throws WorkbookParserException {
		WorkbookParser parser = new WorkbookParser();

		// partially parse the file to parse the description sheet only at first
		Workbook workbook = parser.parseFile(file, false);

		parser.parseAndSetObservationRows(file, workbook);

		return workbook;
	}

	@Override
	public Workbook strictParseWorkbook(File file, String programUUID) throws WorkbookParserException, MiddlewareException {
		WorkbookParser parser = new WorkbookParser();

		OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(this.getSessionProvider());

		// partially parse the file to parse the description sheet only at first
		return this.strictParseWorkbook(file, parser, parser.parseFile(file, true), ontology, programUUID);
	}

	protected Workbook strictParseWorkbook(File file, WorkbookParser parser, Workbook workbook, OntologyDataManager ontology,
			String programUUID) throws MiddlewareException, WorkbookParserException {

		// perform validations on the parsed data that require db access
		List<Message> messages = new LinkedList<Message>();

		if (!this.isEntryExists(ontology, workbook.getFactors())) {
			messages.add(new Message(
					DataImportServiceImpl.ERROR_ENTRY_DOESNT_EXIST));
		}

		if (!this.isPlotExists(ontology, workbook.getFactors())) {
			messages.add(new Message(
					DataImportServiceImpl.ERROR_PLOT_DOESNT_EXIST));
		}

		if (!workbook.isNursery()
				&& !this.isTrialInstanceNumberExists(ontology,
						workbook.getTrialVariables())) {
			messages.add(new Message(
					DataImportServiceImpl.ERROR_MISSING_TRIAL_CONDITION));
		}

		messages.addAll(this.validateMeasurementVariableName(workbook.getAllVariables()));

		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}

		// this version of the workbookparser method is also capable of throwing a workbookparserexception with a list of messages
		// containing validation errors inside
		parser.parseAndSetObservationRows(file, workbook);

		// separated the validation of observations from the parsing so that it can be used even in other parsing implementations (e.g., the
		// one for Wizard style)
		messages.addAll(this.checkForEmptyRequiredVariables(workbook));

		// moved checking below as this needs to parse the contents of the observation sheet for multi-locations
		this.checkForDuplicateStudyName(ontology, workbook, messages, programUUID);

		// GCP-6253
		this.checkForDuplicateVariableNames(ontology, workbook, messages, programUUID);

		this.checkForDuplicatePSMCombo(workbook, messages);

		this.checkForInvalidLabel(workbook, messages);

		return workbook;
	}

	protected List<Message> validateMeasurementVariableName(List<MeasurementVariable> allVariables) {
		List<Message> messages = new ArrayList<Message>();

		messages.addAll(this.validateMeasurmentVariableNameLengths(allVariables));
		messages.addAll(this.validateMeasurmentVariableNameCharacters(allVariables));

		return messages;
	}

	protected List<Message> validateMeasurmentVariableNameLengths(List<MeasurementVariable> variableList) {
		List<Message> messages = new ArrayList<Message>();

		for (MeasurementVariable mv : variableList) {
			if (mv.getName().length() > DataImportServiceImpl.MAX_VARIABLE_NAME_LENGTH) {
				messages.add(new Message(DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_LENGTH, mv.getName()));
			}
		}

		return messages;
	}

	protected List<Message> validateMeasurmentVariableNameCharacters(List<MeasurementVariable> variableList) {
		List<Message> messages = new ArrayList<Message>();

		for (MeasurementVariable mv : variableList) {
			if (!mv.getName().matches("^[^0-9][\\w\\d%]*$")) {
				messages.add(new Message(DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_CHARACTERS, mv.getName()));
			}
		}

		return messages;
	}

	private List<Message> checkForEmptyRequiredVariables(Workbook workbook) {
		List<Message> returnVal = new ArrayList<Message>();

		List<MeasurementVariable> requiredMeasurementVariables = this.retrieveRequiredMeasurementVariables(workbook);

		int i = 1;

		for (MeasurementRow measurementRow : workbook.getObservations()) {
			for (MeasurementData measurementData : measurementRow.getDataList()) {
				for (MeasurementVariable requiredMeasurementVariable : requiredMeasurementVariables) {
					if (measurementData.getLabel().equals(requiredMeasurementVariable.getName())
							&& StringUtils.isEmpty(measurementData.getValue())) {
						returnVal.add(new Message("empty.required.variable", measurementData.getLabel(), Integer.toString(i)));
					}
				}
			}

			i++;
		}

		return returnVal;
	}

	private List<MeasurementVariable> retrieveRequiredMeasurementVariables(Workbook workbook) {
		// current implem leverages the setting of the required variable in earlier checks
		List<MeasurementVariable> returnVal = new ArrayList<MeasurementVariable>();

		for (MeasurementVariable measurementVariable : workbook.getAllVariables()) {
			if (measurementVariable.isRequired()) {
				returnVal.add(measurementVariable);
			}
		}

		return returnVal;
	}

	private void checkForDuplicateStudyName(OntologyDataManager ontology, 
			Workbook workbook, List<Message> messages, String programUUID)
			throws MiddlewareException, WorkbookParserException {

		String studyName = workbook.getStudyDetails().getStudyName();
		String locationDescription = this.getLocationDescription(ontology, workbook,
				programUUID);
		Integer locationId = this.getLocationIdByProjectNameAndDescriptionAndProgramUUID(studyName, locationDescription, programUUID);

		// same location and study
		if (locationId != null) {
			messages.add(new Message(DataImportServiceImpl.ERROR_DUPLICATE_STUDY_NAME));
		} else {
			boolean isExisting = this.checkIfProjectNameIsExistingInProgram(studyName, programUUID);
			// existing and is not a valid study
			if (isExisting && this.getStudyId(studyName, programUUID) == null) {
				messages.add(new Message(DataImportServiceImpl.ERROR_DUPLICATE_STUDY_NAME));
			}
			// else we will create a new study or append the data sets to the existing study
		}

		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}
	}

	private void checkForDuplicateVariableNames(OntologyDataManager ontologyDataManager, 
			Workbook workbook, List<Message> messages, String programUUID)
			throws WorkbookParserException, MiddlewareException {
		List<List<MeasurementVariable>> workbookVariables = new ArrayList<List<MeasurementVariable>>();
		workbookVariables.add(workbook.getConditions());
		workbookVariables.add(workbook.getFactors());
		workbookVariables.add(workbook.getConstants());
		workbookVariables.add(workbook.getVariates());
		Map<String, MeasurementVariable> variableNameMap = new HashMap<String, MeasurementVariable>();
		for (List<MeasurementVariable> variableList : workbookVariables) {

			for (MeasurementVariable measurementVariable : variableList) {
				if (variableNameMap.containsKey(measurementVariable.getName())) {
					messages.add(new Message("error.duplicate.local.variable", measurementVariable.getName()));
				} else {
					variableNameMap.put(measurementVariable.getName(), measurementVariable);
				}

				Integer varId = ontologyDataManager
						.getStandardVariableIdByPropertyScaleMethod(
								measurementVariable.getProperty(),
								measurementVariable.getScale(),
								measurementVariable.getMethod());

				if (varId == null) {

					Set<StandardVariable> variableSet =
							ontologyDataManager.findStandardVariablesByNameOrSynonym(
									measurementVariable.getName(),programUUID);

					for (StandardVariable variable : variableSet) {
						messages.add(new Message(
								"error.import.existing.standard.variable.name",
								measurementVariable.getName(), variable
										.getProperty().getName(), variable
										.getScale().getName(), variable
										.getMethod().getName()));
					}

				}
			}
		}

		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}
	}

	private void checkForDuplicatePSMCombo(Workbook workbook, List<Message> messages) throws MiddlewareQueryException,
			WorkbookParserException {
		Map<String, List<MeasurementVariable>> stdVarMap = this.checkForDuplicates(workbook.getNonVariateVariables(), false);
		this.addErrorForDuplicates(messages, stdVarMap);
		stdVarMap = this.checkForDuplicates(workbook.getVariateVariables(), true);
		this.addErrorForDuplicates(messages, stdVarMap);
		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}
	}

	private void checkForDuplicatePSMCombo(Workbook workbook, Map<String, List<Message>> errors) throws MiddlewareQueryException {
		Map<String, List<MeasurementVariable>> stdVarMap = this.checkForDuplicates(workbook.getNonVariateVariables(), false);
		this.addErrorForDuplicates(errors, stdVarMap);
		stdVarMap = this.checkForDuplicates(workbook.getVariateVariables(), true);
		this.addErrorForDuplicates(errors, stdVarMap);
	}

	private Map<String, List<MeasurementVariable>> checkForDuplicates(List<MeasurementVariable> workbookVariables, boolean isVariate)
			throws MiddlewareQueryException {
		Map<String, List<MeasurementVariable>> stdVarMap = new LinkedHashMap<String, List<MeasurementVariable>>();
		for (MeasurementVariable measurementVariable : workbookVariables) {
			// need to retrieve standard variable because of synonyms
			Integer standardVariableId = this.getOntologyDataManager()
					.getStandardVariableIdByPropertyScaleMethod(
							measurementVariable.getProperty(),
							measurementVariable.getScale(),
							measurementVariable.getMethod());
			String key;
			if (standardVariableId != null) {
				key = Integer.toString(standardVariableId);
			} else {
				key = measurementVariable.getProperty().toLowerCase() + "-"
						+ measurementVariable.getScale().toLowerCase() + "-"
						+ measurementVariable.getMethod().toLowerCase();
			}
			List<MeasurementVariable> vars = stdVarMap.get(key);
			if (vars == null) {
				vars = new ArrayList<MeasurementVariable>();
				stdVarMap.put(key, vars);
			}
			vars.add(measurementVariable);
		}
		return stdVarMap;
	}

	private void addErrorForDuplicates(Map<String, List<Message>> errors, Map<String, List<MeasurementVariable>> map) {
		for (String key : map.keySet()) {
			List<MeasurementVariable> vars = map.get(key);
			if (vars.size() > 1) {
				// has duplicate
				StringBuilder duplicates = new StringBuilder();
				String delimiter = "";
				for (MeasurementVariable measurementVariable : vars) {
					duplicates.append(delimiter);
					delimiter = ", ";
					duplicates.append(measurementVariable.getName());
				}
				for (MeasurementVariable measurementVariable : vars) {
					this.initializeIfNull(errors, measurementVariable.getName() + ":" + measurementVariable.getTermId());
					errors.get(measurementVariable.getName() + ":" + measurementVariable.getTermId()).add(
							new Message(ERROR_DUPLICATE_PSM, duplicates.toString()));
				}
			}
		}
	}

	private void addErrorForDuplicates(List<Message> errors, Map<String, List<MeasurementVariable>> map) {
		for (String key : map.keySet()) {
			List<MeasurementVariable> vars = map.get(key);
			if (vars.size() > 1) {
				// has duplicate
				StringBuilder duplicates = new StringBuilder();
				String delimiter = "";
				for (MeasurementVariable measurementVariable : vars) {
					duplicates.append(delimiter);
					delimiter = ", ";
					duplicates.append(measurementVariable.getName());
				}
				errors.add(new Message(ERROR_DUPLICATE_PSM, duplicates.toString()));
			}
		}
	}

	private void checkForInvalidLabel(Workbook workbook, List<Message> messages) throws MiddlewareQueryException, WorkbookParserException {
		List<MeasurementVariable> variableList = new ArrayList<MeasurementVariable>();
		variableList.addAll(workbook.getFactors());
		variableList.addAll(workbook.getConditions());

		for (MeasurementVariable measurementVariable : variableList) {
			PhenotypicType type = PhenotypicType.getPhenotypicTypeForLabel(measurementVariable.getLabel(), false);
			if (type == PhenotypicType.VARIATE) {
				messages.add(new Message("error.import.invalid.label", measurementVariable.getName(), measurementVariable.getLabel()));
			}
		}

		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}
	}

	// for single location
	private String getLocationDescription(OntologyDataManager ontology,
			Workbook workbook, String programUUID) throws MiddlewareException {

		// check if single location
		// it means the location is defined in the description sheet)
		String trialInstanceNumber = getTrialInstanceNumberFromMeasurementVariables(
				ontology, workbook.getConditions());
		if (trialInstanceNumber != null) {
			return trialInstanceNumber;
		}

		// check if multi-location
		// it means the location is defined in the observation sheet
		trialInstanceNumber = getTrialInstanceNumberFromMeasurementRows(
				ontology, workbook.getObservations(),
				workbook.getTrialFactors());

		if (workbook.isNursery()) {
			return "1";
		}
		return null;
	}

	private Integer getStudyId(String name, String programUUID) throws MiddlewareQueryException {
		return this.getProjectId(name, programUUID, TermId.IS_STUDY);
	}

	private Integer getProjectId(String name, String programUUID, TermId relationship) throws MiddlewareQueryException {
		return this.getDmsProjectDao().getProjectIdByNameAndProgramUUID(name, programUUID, relationship);
	}

	protected Boolean isEntryExists(OntologyDataManager ontology,
			List<MeasurementVariable> list) throws MiddlewareException {
		for (MeasurementVariable mvar : list) {
			Integer varId = ontology
					.getStandardVariableIdByPropertyScaleMethod(
							mvar.getProperty(), mvar.getScale(),
							mvar.getMethod());

			if (varId != null && varId == TermId.ENTRY_NO.getId()) {
				mvar.setRequired(true);
				return true;
			}
		}

		return false;
	}

	protected Boolean isPlotExists(OntologyDataManager ontology,
			List<MeasurementVariable> list) throws MiddlewareException {
		for (MeasurementVariable mvar : list) {
			Integer varId = ontology
					.getStandardVariableIdByPropertyScaleMethod(
							mvar.getProperty(), mvar.getScale(),
							mvar.getMethod());

			if (varId != null
					&& (varId == TermId.PLOT_NO.getId() || varId == TermId.PLOT_NNO
							.getId())) {
				mvar.setRequired(true);
				return true;
			}

		}
		return false;
	}

	protected Boolean isTrialInstanceNumberExists(OntologyDataManager ontology,
			List<MeasurementVariable> list) throws MiddlewareException {
		MeasurementVariable var = getTrialInstanceNumberMeasurementVariable(
				ontology, list);
		if (var != null) {
			var.setRequired(true);
			return true;
		}
		return false;
	}

	private String getTrialInstanceNumberFromMeasurementRows(
			OntologyDataManager ontology, List<MeasurementRow> measurementRows,
			List<MeasurementVariable> trialFactors)
			throws MiddlewareQueryException {

		// get first row - should contain the study location
		MeasurementRow row = measurementRows.get(0);
		MeasurementVariable var = getTrialInstanceNumberMeasurementVariable(
				ontology, trialFactors);
		if (var != null) {
			return row.getMeasurementDataValue(var.getName());
		}

		return null;
	}

	private String getTrialInstanceNumberFromMeasurementVariables(
			OntologyDataManager ontology, List<MeasurementVariable> list)
			throws MiddlewareQueryException {
		MeasurementVariable mvar = getTrialInstanceNumberMeasurementVariable(
				ontology, list);
		if (mvar != null) {
			return mvar.getValue();
		}
		return null;
	}

	private MeasurementVariable getTrialInstanceNumberMeasurementVariable(
			OntologyDataManager ontology, List<MeasurementVariable> list)
			throws MiddlewareQueryException {
		for (MeasurementVariable mvar : list) {
			Integer varId = ontology
					.getStandardVariableIdByPropertyScaleMethod(
							mvar.getProperty(), mvar.getScale(),
							mvar.getMethod());
			if (varId != null && TermId.TRIAL_INSTANCE_FACTOR.getId() == varId) {
				return mvar;
			}
		}
		return null;
	}

	@Override
	public boolean checkIfProjectNameIsExistingInProgram(String name, String programUUID) throws MiddlewareQueryException {
		return this.getDmsProjectDao().checkIfProjectNameIsExistingInProgram(name, programUUID);
	}

	@Override
	public Integer getLocationIdByProjectNameAndDescriptionAndProgramUUID(String projectName, String locationDescription, String programUUID)
			throws MiddlewareQueryException {
		return this.getGeolocationDao().getLocationIdByProjectNameAndDescriptionAndProgramUUID(projectName, locationDescription,
				programUUID);
	}

	@Override
	public Map<String, List<Message>> validateProjectOntology(Workbook workbook, String programUUID) throws MiddlewareException {
		Map<String, List<Message>> errors = new HashMap<String, List<Message>>();

		OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(this.getSessionProvider());

		if (!this.isEntryExists(ontology, workbook.getFactors())) {
			this.initializeIfNull(errors, Constants.MISSING_ENTRY);
			// DMV : TODO change implem so that backend is agnostic to UI when
			// determining messages
			errors.get(Constants.MISSING_ENTRY).add(
					new Message("error.entry.doesnt.exist.wizard"));
		}

		if ((workbook.getImportType() == null || workbook.getImportType() == DataSetType.PLOT_DATA
				.getId())
				&& !this.isPlotExists(ontology, workbook.getFactors())) {
			this.initializeIfNull(errors, Constants.MISSING_PLOT);
			// DMV : TODO change implem so that backend is agnostic to UI when determining messages
			errors.get(Constants.MISSING_PLOT).add(new Message("error.plot.doesnt.exist.wizard"));
		}

		if (!workbook.isNursery()
				&& !this.isTrialInstanceNumberExists(ontology,
						workbook.getTrialVariables())) {
			this.initializeIfNull(errors, Constants.MISSING_TRIAL);
			errors.get(Constants.MISSING_TRIAL).add(new Message(DataImportServiceImpl.ERROR_MISSING_TRIAL_CONDITION));
		}

		this.checkForDuplicatePSMCombo(workbook, errors);

		return errors;
	}

	private <T> void initializeIfNull(Map<String, List<T>> errors, String key) {
		if (errors.get(key) == null) {
			errors.put(key, new ArrayList<T>());
		}
	}

	@Override
	public int saveProjectOntology(Workbook workbook, String programUUID) throws MiddlewareQueryException {
		
		
		TimerWatch timerWatch = new TimerWatch("saveProjectOntology (grand total)");
		int studyId = 0;

		try {


			studyId = this.getWorkbookSaver().saveProjectOntology(workbook, programUUID);


		} catch (Exception e) {

			this.logAndThrowException("Error encountered with importing project ontology: ", e, DataImportServiceImpl.LOG);

		} finally {
			timerWatch.stop();
		}

		return studyId;
	}

	@Override
	public int saveProjectData(Workbook workbook, String programUUID) throws MiddlewareQueryException {
		
		
		TimerWatch timerWatch = new TimerWatch("saveProjectData (grand total)");

		try {


			this.getWorkbookSaver().saveProjectData(workbook, programUUID);


		} catch (Exception e) {

			this.logAndThrowException("Error encountered in importing observations: ", e, DataImportServiceImpl.LOG);
			return 0;
		} finally {
			timerWatch.stop();
		}

		return 1;
	}

	@Override
	public Map<String, List<Message>> validateProjectData(Workbook workbook, String programUUID) 
			throws MiddlewareException {
		Map<String, List<Message>> errors = new HashMap<String, List<Message>>();
		OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(this.getSessionProvider());
		this.checkForExistingTrialInstance(ontology, workbook, errors, programUUID);

		// the following code is a workaround versus the current state management in the ETL Wizard
		// to re-set the "required" fields to true for checking later on

		this.isPlotExists(ontology, workbook.getFactors());
		this.isEntryExists(ontology, workbook.getFactors());
		if (!workbook.isNursery()) {
			this.isTrialInstanceNumberExists(ontology,
					workbook.getTrialVariables());
		}

		List<Message> requiredVariableValueErrors = this.checkForEmptyRequiredVariables(workbook);

		if (!requiredVariableValueErrors.isEmpty()) {
			errors.put(Constants.OBSERVATION_DATA_ERRORS, requiredVariableValueErrors);
		}

		return errors;
	}

	private void checkForExistingTrialInstance(OntologyDataManager ontology, 
			Workbook workbook, Map<String, List<Message>> errors,
			String programUUID) throws MiddlewareException {

		String studyName = workbook.getStudyDetails().getStudyName();
		String trialInstanceNumber;
		if (workbook.isNursery()) {
			trialInstanceNumber = "1";
			Integer locationId = this.getLocationIdByProjectNameAndDescriptionAndProgramUUID(studyName, trialInstanceNumber, programUUID);
			// same location and study
			if (locationId != null) {
				this.initializeIfNull(errors, Constants.GLOBAL);
				errors.get(Constants.GLOBAL).add(new Message("error.duplicate.trial.instance", trialInstanceNumber));
			}
		} else {
			// get local variable name of the trial instance number
			String trialInstanceHeader = null;
			List<MeasurementVariable> trialFactors = workbook.getTrialFactors();
			for (MeasurementVariable mvar : trialFactors) {
				Integer varId = ontology
						.getStandardVariableIdByPropertyScaleMethod(
								mvar.getProperty(), mvar.getScale(),
								mvar.getMethod());
				if (varId != null) {
					StandardVariable svar = ontology.getStandardVariable(varId,programUUID);
					if (svar.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
						trialInstanceHeader = mvar.getName();
						break;
					}
				}
			}
			// get and check if trialInstanceNumber already exists
			Set<String> locationIds = new LinkedHashSet<String>();

			// TODO MODIFY THIS IF NECESSARY
			int maxNumOfIterations = 100000;
			int observationCount = workbook.getObservations().size();
			if (observationCount < maxNumOfIterations) {
				maxNumOfIterations = observationCount;
			}
			List<String> duplicateTrialInstances = new ArrayList<String>();
			boolean isMeansDataImport = workbook.getImportType() != null && workbook.getImportType() == DataSetType.MEANS_DATA.getId();
			for (int i = 0; i < maxNumOfIterations; i++) {
				MeasurementRow row = workbook.getObservations().get(i);
				trialInstanceNumber = row.getMeasurementDataValue(trialInstanceHeader);
				if (locationIds.add(trialInstanceNumber)) {
					Integer locationId =
							this.getLocationIdByProjectNameAndDescriptionAndProgramUUID(studyName, trialInstanceNumber, programUUID);
					// same location and study
					if (locationId != null) {
						duplicateTrialInstances.add(trialInstanceNumber);
					}
				}
			}
			boolean hasDuplicateTrialInstances = false;
			if (!duplicateTrialInstances.isEmpty() && workbook.getStudyId() != null) {
				// check import type first
				List<Integer> variateIds = new ArrayList<Integer>();
				// check at least one variate
				variateIds.add(workbook.getVariates().get(0).getTermId());
				int numberOfVariatesData =
						this.getPhenotypeDao().countVariatesDataOfStudy(
								isMeansDataImport ? workbook.getMeansDatasetId() : workbook.getMeasurementDatesetId(), variateIds);
				if (numberOfVariatesData > 0) {
					hasDuplicateTrialInstances = true;
				}
			}
			if (hasDuplicateTrialInstances) {
				this.initializeIfNull(errors, Constants.GLOBAL);
				StringBuilder trialInstanceNumbers = new StringBuilder();
				for (String trialInstanceNo : duplicateTrialInstances) {
					trialInstanceNumbers.append(trialInstanceNo);
					trialInstanceNumbers.append(",");
				}
				errors.get(Constants.GLOBAL).add(
						new Message("error.duplicate.trial.instance", trialInstanceNumbers.toString().substring(0,
								trialInstanceNumbers.toString().length() - 1)));
			}

		}
	}

}
