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
import java.util.TreeSet;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.Constants;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.util.TimerWatch;
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
	public static final String PERSON_ID_VALUES = "PERSON_ID_VALUES";
	public static final String LOCATION_ID_VALUES = "LOCATION_ID_VALUES";
	public static final String GERMPLASM_ID_VALUES = "GERMPLASM_ID_VALUES";

	private LocationDAO locationDAO = new LocationDAO();
	private PersonDAO personDAO = new PersonDAO();
	private GermplasmDAO germplasmDAO = new GermplasmDAO();

	public DataImportServiceImpl() {
		super();

	}

	public DataImportServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	/**
	 * Saves a Dataset from a Workbook into the database via 1. Saving new Ontology Variables (column headers) 2. Saving data
	 * <p/>
	 */
	@Override
	public int saveDataset(final Workbook workbook, final String programUUID) {
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
	public int saveDataset(final Workbook workbook, final boolean retainValues, final boolean isDeleteObservations, final String programUUID) {

		Map<String, ?> variableMap = null;
		final TimerWatch timerWatch = new TimerWatch("saveDataset (grand total)");
		try {

			final boolean isUpdate = workbook.getStudyDetails() != null && workbook.getStudyDetails().getId() != null;
			if (isUpdate) {
				this.getWorkbookSaver().saveWorkbookVariables(workbook);
				this.getWorkbookSaver().removeDeletedVariablesAndObservations(workbook);
			}
			variableMap = this.getWorkbookSaver().saveVariables(workbook, programUUID);

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered with saving to database: ", e, DataImportServiceImpl.LOG);

		} finally {
			timerWatch.stop();
		}

		try {

			final int studyId = this.getWorkbookSaver().saveDataset(workbook, variableMap, retainValues, isDeleteObservations, programUUID);

			return studyId;

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered with saving to database: ", e, DataImportServiceImpl.LOG);

		} finally {
			timerWatch.stop();
		}

		return 0;
	}

	@Override
	public Workbook parseWorkbook(final File file) throws WorkbookParserException {
		final WorkbookParser parser = new WorkbookParser();

		// partially parse the file to parse the description sheet only at first
		final Workbook workbook = parser.parseFile(file, false);

		parser.parseAndSetObservationRows(file, workbook);

		return workbook;
	}

	@Override
	public Workbook strictParseWorkbook(final File file, final String programUUID) throws WorkbookParserException {
		final WorkbookParser parser = new WorkbookParser();

		final OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(this.getSessionProvider());

		// partially parse the file to parse the description sheet only at first
		return this.strictParseWorkbook(file, parser, parser.parseFile(file, true), ontology, programUUID);
	}

	protected Workbook strictParseWorkbook(final File file, final WorkbookParser parser, final Workbook workbook,
			final OntologyDataManager ontology, final String programUUID) throws WorkbookParserException {

		// perform validations on the parsed data that require db access
		final List<Message> messages = new LinkedList<Message>();

		if (!this.isEntryExists(ontology, workbook.getFactors())) {
			messages.add(new Message(DataImportServiceImpl.ERROR_ENTRY_DOESNT_EXIST));
		}

		if (!this.isPlotExists(ontology, workbook.getFactors())) {
			messages.add(new Message(DataImportServiceImpl.ERROR_PLOT_DOESNT_EXIST));
		}

		if (!workbook.isNursery() && !this.isTrialInstanceNumberExists(ontology, workbook.getTrialVariables())) {
			messages.add(new Message(DataImportServiceImpl.ERROR_MISSING_TRIAL_CONDITION));
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

	protected List<Message> validateMeasurementVariableName(final List<MeasurementVariable> allVariables) {
		final List<Message> messages = new ArrayList<Message>();

		messages.addAll(this.validateMeasurmentVariableNameLengths(allVariables));
		messages.addAll(this.validateMeasurmentVariableNameCharacters(allVariables));

		return messages;
	}

	protected List<Message> validateMeasurmentVariableNameLengths(final List<MeasurementVariable> variableList) {
		final List<Message> messages = new ArrayList<Message>();

		for (final MeasurementVariable mv : variableList) {
			if (mv.getName().length() > DataImportServiceImpl.MAX_VARIABLE_NAME_LENGTH) {
				messages.add(new Message(DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_LENGTH, mv.getName()));
			}
		}

		return messages;
	}

	protected List<Message> validateMeasurmentVariableNameCharacters(final List<MeasurementVariable> variableList) {
		final List<Message> messages = new ArrayList<Message>();

		for (final MeasurementVariable mv : variableList) {
			if (!mv.getName().matches("^[^0-9][\\w\\d%]*$")) {
				messages.add(new Message(DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_CHARACTERS, mv.getName()));
			}
		}

		return messages;
	}

	private List<Message> checkForEmptyRequiredVariables(final Workbook workbook) {
		final List<Message> returnVal = new ArrayList<Message>();

		final List<MeasurementVariable> requiredMeasurementVariables = this.retrieveRequiredMeasurementVariables(workbook);

		int i = 1;

		for (final MeasurementRow measurementRow : workbook.getObservations()) {
			for (final MeasurementData measurementData : measurementRow.getDataList()) {
				for (final MeasurementVariable requiredMeasurementVariable : requiredMeasurementVariables) {
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

	private List<MeasurementVariable> retrieveRequiredMeasurementVariables(final Workbook workbook) {
		// current implem leverages the setting of the required variable in earlier checks
		final List<MeasurementVariable> returnVal = new ArrayList<MeasurementVariable>();

		for (final MeasurementVariable measurementVariable : workbook.getAllVariables()) {
			if (measurementVariable.isRequired()) {
				returnVal.add(measurementVariable);
			}
		}

		return returnVal;
	}

	private void checkForDuplicateStudyName(final OntologyDataManager ontology, final Workbook workbook, final List<Message> messages,
			final String programUUID) throws WorkbookParserException {

		final String studyName = workbook.getStudyDetails().getStudyName();
		final String locationDescription = this.getLocationDescription(ontology, workbook, programUUID);
		final Integer locationId = this.getLocationIdByProjectNameAndDescriptionAndProgramUUID(studyName, locationDescription, programUUID);

		// same location and study
		if (locationId != null) {
			messages.add(new Message(DataImportServiceImpl.ERROR_DUPLICATE_STUDY_NAME));
		} else {
			final boolean isExisting = this.checkIfProjectNameIsExistingInProgram(studyName, programUUID);
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

	private void checkForDuplicateVariableNames(final OntologyDataManager ontologyDataManager, final Workbook workbook,
			final List<Message> messages, final String programUUID) throws WorkbookParserException {
		final List<List<MeasurementVariable>> workbookVariables = new ArrayList<List<MeasurementVariable>>();
		workbookVariables.add(workbook.getConditions());
		workbookVariables.add(workbook.getFactors());
		workbookVariables.add(workbook.getConstants());
		workbookVariables.add(workbook.getVariates());
		final Map<String, MeasurementVariable> variableNameMap = new HashMap<String, MeasurementVariable>();
		for (final List<MeasurementVariable> variableList : workbookVariables) {

			for (final MeasurementVariable measurementVariable : variableList) {
				if (variableNameMap.containsKey(measurementVariable.getName())) {
					messages.add(new Message("error.duplicate.local.variable", measurementVariable.getName()));
				} else {
					variableNameMap.put(measurementVariable.getName(), measurementVariable);
				}

				final Integer varId =
						ontologyDataManager.getStandardVariableIdByPropertyScaleMethod(measurementVariable.getProperty(),
								measurementVariable.getScale(), measurementVariable.getMethod());

				if (varId == null) {

					final Set<StandardVariable> variableSet =
							ontologyDataManager.findStandardVariablesByNameOrSynonym(measurementVariable.getName(), programUUID);

					for (final StandardVariable variable : variableSet) {
						messages.add(new Message("error.import.existing.standard.variable.name", measurementVariable.getName(), variable
								.getProperty().getName(), variable.getScale().getName(), variable.getMethod().getName()));
					}

				}
			}
		}

		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}
	}

	private void checkForDuplicatePSMCombo(final Workbook workbook, final List<Message> messages) throws
	WorkbookParserException {
		Map<String, List<MeasurementVariable>> stdVarMap = this.checkForDuplicates(workbook.getNonVariateVariables(), false);
		this.addErrorForDuplicates(messages, stdVarMap);
		stdVarMap = this.checkForDuplicates(workbook.getVariateVariables(), true);
		this.addErrorForDuplicates(messages, stdVarMap);
		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}
	}

	private void checkForDuplicatePSMCombo(final Workbook workbook, final Map<String, List<Message>> errors) {
		Map<String, List<MeasurementVariable>> stdVarMap = this.checkForDuplicates(workbook.getNonVariateVariables(), false);
		this.addErrorForDuplicates(errors, stdVarMap);
		stdVarMap = this.checkForDuplicates(workbook.getVariateVariables(), true);
		this.addErrorForDuplicates(errors, stdVarMap);
	}

	private Map<String, List<MeasurementVariable>> checkForDuplicates(final List<MeasurementVariable> workbookVariables,
			final boolean isVariate) {
		final Map<String, List<MeasurementVariable>> stdVarMap = new LinkedHashMap<String, List<MeasurementVariable>>();
		for (final MeasurementVariable measurementVariable : workbookVariables) {
			// need to retrieve standard variable because of synonyms
			final Integer standardVariableId =
					this.getOntologyDataManager().getStandardVariableIdByPropertyScaleMethod(measurementVariable.getProperty(),
							measurementVariable.getScale(), measurementVariable.getMethod());
			final String key;
			if (standardVariableId != null) {
				key = Integer.toString(standardVariableId);
			} else {
				key =
						measurementVariable.getProperty().toLowerCase() + "-" + measurementVariable.getScale().toLowerCase() + "-"
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

	private void addErrorForDuplicates(final Map<String, List<Message>> errors, final Map<String, List<MeasurementVariable>> map) {
		for (final String key : map.keySet()) {
			final List<MeasurementVariable> vars = map.get(key);
			if (vars.size() > 1) {
				// has duplicate
				final StringBuilder duplicates = new StringBuilder();
				String delimiter = "";
				for (final MeasurementVariable measurementVariable : vars) {
					duplicates.append(delimiter);
					delimiter = ", ";
					duplicates.append(measurementVariable.getName());
				}
				for (final MeasurementVariable measurementVariable : vars) {
					this.initializeIfNull(errors, measurementVariable.getName() + ":" + measurementVariable.getTermId());
					errors.get(measurementVariable.getName() + ":" + measurementVariable.getTermId()).add(
							new Message(ERROR_DUPLICATE_PSM, duplicates.toString()));
				}
			}
		}
	}

	private void addErrorForDuplicates(final List<Message> errors, final Map<String, List<MeasurementVariable>> map) {
		for (final String key : map.keySet()) {
			final List<MeasurementVariable> vars = map.get(key);
			if (vars.size() > 1) {
				// has duplicate
				final StringBuilder duplicates = new StringBuilder();
				String delimiter = "";
				for (final MeasurementVariable measurementVariable : vars) {
					duplicates.append(delimiter);
					delimiter = ", ";
					duplicates.append(measurementVariable.getName());
				}
				errors.add(new Message(ERROR_DUPLICATE_PSM, duplicates.toString()));
			}
		}
	}

	private void checkForInvalidLabel(final Workbook workbook, final List<Message> messages) throws WorkbookParserException {
		final List<MeasurementVariable> variableList = new ArrayList<MeasurementVariable>();
		variableList.addAll(workbook.getFactors());
		variableList.addAll(workbook.getConditions());

		for (final MeasurementVariable measurementVariable : variableList) {
			final PhenotypicType type = PhenotypicType.getPhenotypicTypeForLabel(measurementVariable.getLabel(), false);
			if (type == PhenotypicType.VARIATE) {
				messages.add(new Message("error.import.invalid.label", measurementVariable.getName(), measurementVariable.getLabel()));
			}
		}

		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}
	}

	// for single location
	private String getLocationDescription(final OntologyDataManager ontology, final Workbook workbook, final String programUUID) {

		// check if single location
		// it means the location is defined in the description sheet)
		String trialInstanceNumber = this.getTrialInstanceNumberFromMeasurementVariables(ontology, workbook.getConditions());
		if (trialInstanceNumber != null) {
			return trialInstanceNumber;
		}

		// check if multi-location
		// it means the location is defined in the observation sheet
		trialInstanceNumber = this.getTrialInstanceNumberFromMeasurementRows(ontology, workbook.getObservations(), workbook.getTrialFactors());

		if (workbook.isNursery()) {
			return "1";
		}
		return null;
	}

	private Integer getStudyId(final String name, final String programUUID) {
		return this.getProjectId(name, programUUID, TermId.IS_STUDY);
	}

	private Integer getProjectId(final String name, final String programUUID, final TermId relationship) {
		return this.getDmsProjectDao().getProjectIdByNameAndProgramUUID(name, programUUID, relationship);
	}

	protected Boolean isEntryExists(final OntologyDataManager ontology, final List<MeasurementVariable> list) {
		for (final MeasurementVariable mvar : list) {
			final Integer varId =
					ontology.getStandardVariableIdByPropertyScaleMethod(mvar.getProperty(), mvar.getScale(), mvar.getMethod());

			if (varId != null && varId == TermId.ENTRY_NO.getId()) {
				mvar.setRequired(true);
				return true;
			}
		}

		return false;
	}

	protected Boolean isPlotExists(final OntologyDataManager ontology, final List<MeasurementVariable> list) {
		for (final MeasurementVariable mvar : list) {
			final Integer varId =
					ontology.getStandardVariableIdByPropertyScaleMethod(mvar.getProperty(), mvar.getScale(), mvar.getMethod());

			if (varId != null && (varId == TermId.PLOT_NO.getId() || varId == TermId.PLOT_NNO.getId())) {
				mvar.setRequired(true);
				return true;
			}

		}
		return false;
	}

	protected Boolean isTrialInstanceNumberExists(final OntologyDataManager ontology, final List<MeasurementVariable> list) {
		final MeasurementVariable var = this.getTrialInstanceNumberMeasurementVariable(ontology, list);
		if (var != null) {
			var.setRequired(true);
			return true;
		}
		return false;
	}

	private String getTrialInstanceNumberFromMeasurementRows(final OntologyDataManager ontology,
			final List<MeasurementRow> measurementRows, final List<MeasurementVariable> trialFactors) {

		// get first row - should contain the study location
		final MeasurementRow row = measurementRows.get(0);
		final MeasurementVariable var = this.getTrialInstanceNumberMeasurementVariable(ontology, trialFactors);
		if (var != null) {
			return row.getMeasurementDataValue(var.getName());
		}

		return null;
	}

	private String getTrialInstanceNumberFromMeasurementVariables(final OntologyDataManager ontology, final List<MeasurementVariable> list) {
		final MeasurementVariable mvar = this.getTrialInstanceNumberMeasurementVariable(ontology, list);
		if (mvar != null) {
			return mvar.getValue();
		}
		return null;
	}

	private MeasurementVariable getTrialInstanceNumberMeasurementVariable(final OntologyDataManager ontology,
			final List<MeasurementVariable> list) {
		for (final MeasurementVariable mvar : list) {
			final Integer varId =
					ontology.getStandardVariableIdByPropertyScaleMethod(mvar.getProperty(), mvar.getScale(), mvar.getMethod());
			if (varId != null && TermId.TRIAL_INSTANCE_FACTOR.getId() == varId) {
				return mvar;
			}
		}
		return null;
	}

	@Override
	public boolean checkIfProjectNameIsExistingInProgram(final String name, final String programUUID) {
		return this.getDmsProjectDao().checkIfProjectNameIsExistingInProgram(name, programUUID);
	}

	@Override
	public Integer getLocationIdByProjectNameAndDescriptionAndProgramUUID(final String projectName, final String locationDescription,
			final String programUUID) {
		return this.getGeolocationDao().getLocationIdByProjectNameAndDescriptionAndProgramUUID(projectName, locationDescription,
				programUUID);
	}

	@Override
	public Map<String, List<Message>> validateProjectOntology(final Workbook workbook, final String programUUID) {
		final Map<String, List<Message>> errors = new HashMap<String, List<Message>>();

		final OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(this.getSessionProvider());

		if (!this.isEntryExists(ontology, workbook.getFactors())) {
			this.initializeIfNull(errors, Constants.MISSING_ENTRY);
			// DMV : TODO change implem so that backend is agnostic to UI when
			// determining messages
			errors.get(Constants.MISSING_ENTRY).add(new Message("error.entry.doesnt.exist.wizard"));
		}

		if ((workbook.getImportType() == null || workbook.getImportType() == DataSetType.PLOT_DATA.getId())
				&& !this.isPlotExists(ontology, workbook.getFactors())) {
			this.initializeIfNull(errors, Constants.MISSING_PLOT);
			// DMV : TODO change implem so that backend is agnostic to UI when determining messages
			errors.get(Constants.MISSING_PLOT).add(new Message("error.plot.doesnt.exist.wizard"));
		}

		if (!workbook.isNursery() && !this.isTrialInstanceNumberExists(ontology, workbook.getTrialVariables())) {
			this.initializeIfNull(errors, Constants.MISSING_TRIAL);
			errors.get(Constants.MISSING_TRIAL).add(new Message(DataImportServiceImpl.ERROR_MISSING_TRIAL_CONDITION));
		}

		this.checkForDuplicatePSMCombo(workbook, errors);

		return errors;
	}

	private <T> void initializeIfNull(final Map<String, List<T>> errors, final String key) {
		if (errors.get(key) == null) {
			errors.put(key, new ArrayList<T>());
		}
	}

	@Override
	public int saveProjectOntology(final Workbook workbook, final String programUUID) {

		final TimerWatch timerWatch = new TimerWatch("saveProjectOntology (grand total)");
		int studyId = 0;

		try {

			studyId = this.getWorkbookSaver().saveProjectOntology(workbook, programUUID);

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered with importing project ontology: ", e, DataImportServiceImpl.LOG);

		} finally {
			timerWatch.stop();
		}

		return studyId;
	}

	@Override
	public int saveProjectData(final Workbook workbook, final String programUUID) {

		final TimerWatch timerWatch = new TimerWatch("saveProjectData (grand total)");

		try {

			this.getWorkbookSaver().saveProjectData(workbook, programUUID);

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered in importing observations: ", e, DataImportServiceImpl.LOG);
			return 0;
		} finally {
			timerWatch.stop();
		}

		return 1;
	}

	@Override
	public Map<String, List<Message>> validateProjectData(final Workbook workbook, final String programUUID) {
		final Map<String, List<Message>> errors = new HashMap<String, List<Message>>();
		final OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(this.getSessionProvider());
		this.checkForExistingTrialInstance(ontology, workbook, errors, programUUID);

		// the following code is a workaround versus the current state management in the ETL Wizard
		// to re-set the "required" fields to true for checking later on

		this.isPlotExists(ontology, workbook.getFactors());
		this.isEntryExists(ontology, workbook.getFactors());
		if (!workbook.isNursery()) {
			this.isTrialInstanceNumberExists(ontology, workbook.getTrialVariables());
		}

		final List<Message> requiredVariableValueErrors = this.checkForEmptyRequiredVariables(workbook);

		if (!requiredVariableValueErrors.isEmpty()) {
			errors.put(Constants.OBSERVATION_DATA_ERRORS, requiredVariableValueErrors);
		}

		if (errors.isEmpty()) {
			// check invalid records only when there are no more errors in the workbook
			errors.put(Constants.OBSERVATION_DATA_ERRORS, this.checkForInvalidRecordsOfControlledVariables(workbook, programUUID));
		}

		return errors;
	}

	@Override
	public List<Message> checkForInvalidRecordsOfControlledVariables(Workbook workbook, String programUUID) {
		List<Message> returnVal = new ArrayList<Message>();

		Map<String, Integer> controlledVariablesMap = this.retrieveControlledVariablesMap(workbook);
		Map<String, Set<Integer>> idsToVerifyMap = this.initializeIdsToVerifyMap();
		Map<String, Set<String>> invalidValuesMap = this.initializeInvalidValuesMap();

		for (MeasurementRow measurementRow : workbook.getObservations()) {
			for (MeasurementData measurementData : measurementRow.getDataList()) {
				Integer variableId = controlledVariablesMap.get(measurementData.getLabel());
				if (variableId != null && !StringUtils.isEmpty(measurementData.getValue())) {
					this.addVariableValueToTheAppropriateMap(variableId, measurementData.getValue(), idsToVerifyMap, invalidValuesMap);
				}
			}
		}

		this.verifyRecordIdsIfExistingElseAddToInvalidList(idsToVerifyMap,invalidValuesMap,programUUID);
		boolean hasAdditionalErrors = this.addErrorMessagesForInvalidValues(returnVal, invalidValuesMap);

		if (hasAdditionalErrors) {
			workbook.setInvalidValuesMap(invalidValuesMap);
		} else {
			workbook.setInvalidValuesMap(null);
		}
		return returnVal;
	}

	private boolean addErrorMessagesForInvalidValues(List<Message> returnVal, Map<String, Set<String>> invalidValuesMap) {
		boolean hasAdditionalErrors = false;
		for (String key : invalidValuesMap.keySet()) {
			Set<String> invalidValues = invalidValuesMap.get(key);
			if (!invalidValues.isEmpty()) {
				hasAdditionalErrors = true;
				String messageKey = null;
				if (key.equals(PERSON_ID_VALUES)) {
					messageKey = "import.missing.person.ids";
				} else if (key.equals(LOCATION_ID_VALUES)) {
					messageKey = "import.missing.location.ids";
				} else if (key.equals(GERMPLASM_ID_VALUES)) {
					messageKey = "import.missing.germplasm.ids";
				}
				returnVal.add(new Message(messageKey, StringUtils.join(invalidValues, ", ")));
			}
		}
		return hasAdditionalErrors;
	}
	private void verifyRecordIdsIfExistingElseAddToInvalidList(Map<String, Set<Integer>> idsToVerifyMap, Map<String, Set<String>> invalidValuesMap, String programUUID) {
		this.verifyPersonIdsIfExisting(idsToVerifyMap.get(PERSON_ID_VALUES), invalidValuesMap.get(PERSON_ID_VALUES));
		this.verifyLocationIdsIfExisting(idsToVerifyMap.get(LOCATION_ID_VALUES),invalidValuesMap.get(LOCATION_ID_VALUES),programUUID);
		this.verifyGermplasmIdsIfExisting(idsToVerifyMap.get(GERMPLASM_ID_VALUES), invalidValuesMap.get(GERMPLASM_ID_VALUES));
	}


	private void verifyGermplasmIdsIfExisting(Set<Integer> gidsToVerify, Set<String> invalidGids) {
		this.germplasmDAO.setSession(this.sessionProvider.getSession());
		List<Integer> existingGermplasmIds = this.germplasmDAO.getExistingGIDs(new ArrayList<>(gidsToVerify));
		for (Integer gid : gidsToVerify) {
			if (!existingGermplasmIds.contains(gid)) {
				invalidGids.add(Integer.toString(gid));
			}
		}
	}

	private void verifyPersonIdsIfExisting(Set<Integer> personIdsToVerify, Set<String> invalidPersonIds) {
		this.personDAO.setSession(this.sessionProvider.getSession());
		List<Integer> existingPersonIds = this.personDAO.getExistingPersonIds(new ArrayList<>(personIdsToVerify));
		for (Integer personId : personIdsToVerify) {
			if (!existingPersonIds.contains(personId)) {
				invalidPersonIds.add(Integer.toString(personId));
			}
		}

	}

	private void verifyLocationIdsIfExisting(Set<Integer> locationIdsToVerify, Set<String> invalidLocationIds, String programUUID) {
		this.locationDAO.setSession(this.sessionProvider.getSession());
		List<Integer> existingLocationIds = this.locationDAO.getExistingLocationIds(new ArrayList<>(locationIdsToVerify), programUUID);
		for (Integer locationId : locationIdsToVerify) {
			if (!existingLocationIds.contains(locationId)) {
				invalidLocationIds.add(Integer.toString(locationId));
			}
		}
	}

	private Map<String, Set<Integer>> initializeIdsToVerifyMap() {
		Map<String, Set<Integer>> idsToVerifyMap = new HashMap<>();
		idsToVerifyMap.put(PERSON_ID_VALUES, new TreeSet<Integer>());
		idsToVerifyMap.put(LOCATION_ID_VALUES, new TreeSet<Integer>());
		idsToVerifyMap.put(GERMPLASM_ID_VALUES, new TreeSet<Integer>());
		return idsToVerifyMap;
	}

	private Map<String, Set<String>> initializeInvalidValuesMap() {
		Map<String, Set<String>> invalidValuesMap = new HashMap<>();
		invalidValuesMap.put(PERSON_ID_VALUES, new TreeSet<String>());
		invalidValuesMap.put(LOCATION_ID_VALUES, new TreeSet<String>());
		invalidValuesMap.put(GERMPLASM_ID_VALUES, new TreeSet<String>());
		return invalidValuesMap;
	}

	private void addVariableValueToTheAppropriateMap(Integer id, String value, Map<String, Set<Integer>> idsToVerifyMap,
			Map<String, Set<String>> invalidValuesMap) {
		if (TermId.PI_ID.getId() == id || TermId.COOPERATOOR_ID.getId() == id) {
			if (!NumberUtils.isNumber(value)) {
				invalidValuesMap.get(PERSON_ID_VALUES).add(value);
			} else {
				idsToVerifyMap.get(PERSON_ID_VALUES).add(Integer.parseInt(value));
			}
		} else if (TermId.LOCATION_ID.getId() == id) {
			if (!NumberUtils.isNumber(value)) {
				invalidValuesMap.get(LOCATION_ID_VALUES).add(value);
			} else {
				idsToVerifyMap.get(LOCATION_ID_VALUES).add(Integer.parseInt(value));
			}
		} else if (TermId.GID.getId() == id) {
			if (!NumberUtils.isNumber(value)) {
				invalidValuesMap.get(GERMPLASM_ID_VALUES).add(value);
			} else {
				idsToVerifyMap.get(GERMPLASM_ID_VALUES).add(Integer.parseInt(value));
			}
		}
	}

	/**
	 * Controlled variables are the following:
	 *
	 * PI_ID(8110) and COOPERATOR_ID(8372) referring to Persons.personid, LOCATION_ID(8190) referring to Location.locid and GID(8240)
	 * referring to Germplsm.gid
	 *
	 */
	protected Map<String, Integer> retrieveControlledVariablesMap(Workbook workbook) {
		Map<String, Integer> controlledVariablesMap = new LinkedHashMap<>();
		for (MeasurementVariable measurementVariable : workbook.getAllVariables()) {
			if (measurementVariable.getTermId() == TermId.PI_ID.getId() || measurementVariable.getTermId() == TermId.COOPERATOOR_ID.getId()
					|| measurementVariable.getTermId() == TermId.LOCATION_ID.getId()
					|| measurementVariable.getTermId() == TermId.GID.getId()) {
				controlledVariablesMap.put(measurementVariable.getName(), measurementVariable.getTermId());
			}
		}
		return controlledVariablesMap;
	}

	private void checkForExistingTrialInstance(final OntologyDataManager ontology, final Workbook workbook,
			final Map<String, List<Message>> errors, final String programUUID) {

		final String studyName = workbook.getStudyDetails().getStudyName();
		String trialInstanceNumber;
		if (workbook.isNursery()) {
			trialInstanceNumber = "1";
			final Integer locationId =
					this.getLocationIdByProjectNameAndDescriptionAndProgramUUID(studyName, trialInstanceNumber, programUUID);
			// same location and study
			if (locationId != null) {
				this.initializeIfNull(errors, Constants.GLOBAL);
				errors.get(Constants.GLOBAL).add(new Message("error.duplicate.trial.instance", trialInstanceNumber));
			}
		} else {
			// get local variable name of the trial instance number
			String trialInstanceHeader = null;
			final List<MeasurementVariable> trialFactors = workbook.getTrialFactors();
			for (final MeasurementVariable mvar : trialFactors) {
				final Integer varId =
						ontology.getStandardVariableIdByPropertyScaleMethod(mvar.getProperty(), mvar.getScale(), mvar.getMethod());
				if (varId != null) {
					final StandardVariable svar = ontology.getStandardVariable(varId, programUUID);
					if (svar.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
						trialInstanceHeader = mvar.getName();
						break;
					}
				}
			}
			// get and check if trialInstanceNumber already exists
			final Set<String> locationIds = new LinkedHashSet<String>();

			// TODO MODIFY THIS IF NECESSARY
			int maxNumOfIterations = 100000;
			final int observationCount = workbook.getObservations().size();
			if (observationCount < maxNumOfIterations) {
				maxNumOfIterations = observationCount;
			}
			final List<String> duplicateTrialInstances = new ArrayList<String>();
			final boolean isMeansDataImport =
					workbook.getImportType() != null && workbook.getImportType() == DataSetType.MEANS_DATA.getId();
			for (int i = 0; i < maxNumOfIterations; i++) {
				final MeasurementRow row = workbook.getObservations().get(i);
				trialInstanceNumber = row.getMeasurementDataValue(trialInstanceHeader);
				if (locationIds.add(trialInstanceNumber)) {
					final Integer locationId =
							this.getLocationIdByProjectNameAndDescriptionAndProgramUUID(studyName, trialInstanceNumber, programUUID);
					// same location and study
					if (locationId != null) {
						duplicateTrialInstances.add(trialInstanceNumber);
					}
				}
			}
			boolean hasDuplicateTrialInstances = false;
			if (!duplicateTrialInstances.isEmpty() && workbook.getStudyDetails().getId() != null) {
				// check import type first
				final List<Integer> variateIds = new ArrayList<Integer>();
				// check at least one variate
				variateIds.add(workbook.getVariates().get(0).getTermId());
				final int numberOfVariatesData =
						this.getPhenotypeDao().countVariatesDataOfStudy(
								isMeansDataImport ? workbook.getMeansDatasetId() : workbook.getMeasurementDatesetId(), variateIds);
				if (numberOfVariatesData > 0) {
					hasDuplicateTrialInstances = true;
				}
			}
			if (hasDuplicateTrialInstances) {
				this.initializeIfNull(errors, Constants.GLOBAL);
				final StringBuilder trialInstanceNumbers = new StringBuilder();
				for (final String trialInstanceNo : duplicateTrialInstances) {
					trialInstanceNumbers.append(trialInstanceNo);
					trialInstanceNumbers.append(",");
				}
				errors.get(Constants.GLOBAL).add(
						new Message("error.duplicate.trial.instance", trialInstanceNumbers.toString().substring(0,
								trialInstanceNumbers.toString().length() - 1)));
			}

		}
	}

	@Override
	public void discardMissingRecords(Workbook workbook) {
		Map<String, Set<String>> invalidValuesMap = workbook.getInvalidValuesMap();
		Map<String, Integer> controlledVariablesMap = this.retrieveControlledVariablesMap(workbook);
		for (MeasurementRow measurementRow : workbook.getObservations()) {
			for (MeasurementData measurementData : measurementRow.getDataList()) {
				Integer variableId = controlledVariablesMap.get(measurementData.getLabel());
				if (variableId != null && !StringUtils.isEmpty(measurementData.getValue())) {
					this.discardMissingRecords(measurementData, variableId, invalidValuesMap);
				}
			}
		}
	}

	private void discardMissingRecords(MeasurementData measurementData, Integer variableId, Map<String, Set<String>> invalidValuesMap) {
		if (variableId == TermId.PI_ID.getId() || variableId == TermId.COOPERATOOR_ID.getId()) {
			if (invalidValuesMap.get(PERSON_ID_VALUES).contains(measurementData.getValue())) {
				measurementData.setValue("");
			}
		} else if (variableId == TermId.LOCATION_ID.getId()) {
			if (invalidValuesMap.get(LOCATION_ID_VALUES).contains(measurementData.getValue())) {
				measurementData.setValue("");
			}
		} else if (variableId == TermId.GID.getId()) {
			if (invalidValuesMap.get(GERMPLASM_ID_VALUES).contains(measurementData.getValue())) {
				measurementData.setValue("");
			}
		}
	}

}
