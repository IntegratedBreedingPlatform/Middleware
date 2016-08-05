/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Optional;
import com.hazelcast.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.Constants;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.GermplasmDataManagerImpl;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.util.TimerWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
public class DataImportServiceImpl extends Service implements DataImportService {

	private static final Logger LOG = LoggerFactory.getLogger(DataImportServiceImpl.class);
	public static final int MAX_VARIABLE_NAME_LENGTH = 32;
	public static final String ERROR_MISSING_TRIAL_CONDITION = "error.missing.trial.condition";
	public static final String ERROR_PLOT_DOESNT_EXIST = "error.plot.doesnt.exist";
	public static final String ERROR_ENTRY_DOESNT_EXIST = "error.entry.doesnt.exist";
	public static final String ERROR_GID_DOESNT_EXIST = "error.gid.doesnt.exist";
	public static final String ERROR_DUPLICATE_STUDY_NAME = "error.duplicate.study.name";
	public static final String ERROR_DUPLICATE_PSM = "error.duplicate.psm";
	public static final String ERROR_INVALID_VARIABLE_NAME_LENGTH = "error.invalid.variable.name.length";
	public static final String ERROR_INVALID_VARIABLE_NAME_CHARACTERS = "error.invalid.variable.name.characters";
	public static final String ERROR_INVALID_GIDS_FROM_DATA_FILE = "error.invalid.gids";

	private int maxRowLimit = WorkbookParser.DEFAULT_MAX_ROW_LIMIT;

	@Resource
	private OntologyDataManager ontologyDataManager;

	@Resource
	private GermplasmDataManager germplasmDataManager;

	public DataImportServiceImpl() {

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
	 * @param retainValues         if true, values of the workbook items are retained, else they are cleared to conserve memory
	 * @param isDeleteObservations
	 * @return
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int saveDataset(final Workbook workbook, final boolean retainValues, final boolean isDeleteObservations,
			final String programUUID) {

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
			throw new MiddlewareQueryException("Error encountered with saving to database: ", e);
		} finally {
			timerWatch.stop();
		}

		// Send : Map of 3 sub maps, with data to create Dataset
		// Receive int (success/fail)

		try {

			return this.getWorkbookSaver().saveDataset(workbook, variableMap, retainValues, isDeleteObservations, programUUID);

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered with saving to database: ", e);
		} finally {
			timerWatch.stop();
		}
	}

	@Override
	public Workbook parseWorkbook(final File file) throws WorkbookParserException {
		final WorkbookParser parser = new WorkbookParser(this.maxRowLimit);

		// partially parse the file to parse the description sheet only at first
		// Set performValidation to false to disable validation during parsing.
		final Workbook workbook = parser.parseFile(file, false);

		parser.parseAndSetObservationRows(file, workbook, false);

		return workbook;
	}

	@Override
	public Workbook strictParseWorkbook(final File file, final String programUUID) throws WorkbookParserException {
		final WorkbookParser parser = new WorkbookParser(this.maxRowLimit);

		// partially parse the file to parse the description sheet only at first
		return this.strictParseWorkbook(file, parser, parser.parseFile(file, true), this.ontologyDataManager, this.germplasmDataManager,
				programUUID);
	}

	protected Workbook strictParseWorkbook(final File file, final WorkbookParser parser, final Workbook workbook,
			final OntologyDataManager ontologyDataManager, final GermplasmDataManager germplasmDataManager, final String programUUID)
			throws WorkbookParserException {

		// perform validations on the parsed data that require db access
		final List<Message> messages = new LinkedList<Message>();

		if (!this.isTermExists(TermId.ENTRY_NO.getId(), workbook.getFactors(), ontologyDataManager)) {
			messages.add(new Message(DataImportServiceImpl.ERROR_ENTRY_DOESNT_EXIST));
		}

		if (!this.isTermExists(TermId.PLOT_NO.getId(), workbook.getFactors(), ontologyDataManager) && !this
				.isTermExists(TermId.PLOT_NNO.getId(), workbook.getFactors(), ontologyDataManager)) {
			messages.add(new Message(DataImportServiceImpl.ERROR_PLOT_DOESNT_EXIST));
		}

		if (!workbook.isNursery() && !this
				.isTermExists(TermId.TRIAL_INSTANCE_FACTOR.getId(), workbook.getTrialVariables(), ontologyDataManager)) {
			messages.add(new Message(DataImportServiceImpl.ERROR_MISSING_TRIAL_CONDITION));
		}

		messages.addAll(this.validateMeasurementVariableName(workbook.getAllVariables()));

		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}

		// this version of the workbookparser method is also capable of throwing a workbookparserexception with a list of messages
		// containing validation errors inside
		parser.parseAndSetObservationRows(file, workbook, false);

		this.setRequiredFields(ontologyDataManager, workbook);

		// separated the validation of observations from the parsing so that it can be used even in other parsing implementations (e.g., the
		// one for Wizard style)
		messages.addAll(this.checkForEmptyRequiredVariables(workbook));

		this.checkForInvalidGids(workbook, messages);

		// moved checking below as this needs to parse the contents of the observation sheet for multi-locations
		this.checkForDuplicateStudyName(ontologyDataManager, workbook, messages, programUUID);

		// GCP-6253
		this.checkForDuplicateVariableNames(ontologyDataManager, workbook, messages, programUUID);

		this.checkForDuplicatePSMCombo(workbook, messages);

		this.checkForInvalidLabel(workbook, messages);

		if (this.checkForOutOfBoundsData(workbook, programUUID)) {
			workbook.setHasOutOfBoundsData(true);
		}

		return workbook;
	}

	@Override
	public Workbook parseWorkbook(File file, String programUUID, boolean discardInvalidValues, WorkbookParser workbookParser)
			throws WorkbookParserException {

		// partially parse the file to parse the description sheet only at first
		final Workbook workbook = workbookParser.parseFile(file, false);

		this.populatePossibleValuesForCategoricalVariates(workbook.getVariates(), programUUID);

		workbookParser.parseAndSetObservationRows(file, workbook, discardInvalidValues);

		return workbook;
	}

	@Override
	public void populatePossibleValuesForCategoricalVariates(List<MeasurementVariable> variates, String programUUID) {

		for (MeasurementVariable measurementVariable : variates) {

			StandardVariable standardVariable = this.ontologyDataManager
					.findStandardVariableByTraitScaleMethodNames(measurementVariable.getProperty(), measurementVariable.getScale(),
							measurementVariable.getMethod(), programUUID);

			if (standardVariable != null && standardVariable.getDataType().getId() == DataType.CATEGORICAL_VARIABLE.getId()) {

				List<ValueReference> possibleValues = new ArrayList<>();
				if (standardVariable.getEnumerations() != null && !standardVariable.getEnumerations().isEmpty()) {
					for (Enumeration enumeration : standardVariable.getEnumerations()) {
						possibleValues.add(new ValueReference(enumeration.getId(), enumeration.getName(), enumeration.getDescription()));
					}

				}
				measurementVariable.setPossibleValues(possibleValues);
				measurementVariable.setDataTypeId(standardVariable.getDataType().getId());
			}
		}
	}

	@Override
	public boolean checkForOutOfBoundsData(Workbook workbook, final String programUUID) {

		Map<String, List<String>> termIdValidValuesMap =
				this.getValidValuesMapForCategoricalVariates(this.ontologyDataManager, workbook, programUUID);

		for (MeasurementRow measurementRow : workbook.getObservations()) {
			for (Entry<String, List<String>> entry : termIdValidValuesMap.entrySet()) {
				MeasurementData measurementData = measurementRow.getMeasurementData(entry.getKey());
				if (measurementData != null && !this.containsIgnoreCase(entry.getValue(), measurementData.getValue())) {
					return true;
				}

			}

		}

		return false;

	}

	private boolean containsIgnoreCase(List<String> list, String searchFor) {
		for (String item : list) {
			if (item.equalsIgnoreCase(searchFor)) {
				return true;
			}
		}
		return false;
	}

	protected Map<String, List<String>> getValidValuesMapForCategoricalVariates(final OntologyDataManager ontologyDataManager,
			Workbook workbook, final String programUUID) {

		// Get the categorical variates and extract their valid values (possible values)
		Map<String, List<String>> variableValidValues = new HashMap<>();

		for (MeasurementVariable measurementVariable : workbook.getVariates()) {
			Integer stdVariableId = ontologyDataManager
					.getStandardVariableIdByPropertyScaleMethod(measurementVariable.getProperty(), measurementVariable.getScale(),
							measurementVariable.getMethod());
			if (stdVariableId != null) {
				StandardVariable stdVariable = ontologyDataManager.getStandardVariable(stdVariableId, programUUID);
				if (stdVariable.getDataType().getId() == DataType.CATEGORICAL_VARIABLE.getId()) {
					List<String> validValues = new ArrayList<>();
					for (Enumeration enumeration : stdVariable.getEnumerations()) {
						validValues.add(enumeration.getName());
					}
					variableValidValues.put(measurementVariable.getName(), validValues);
				}

			}
		}
		return variableValidValues;
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
					if (measurementData.getLabel().equals(requiredMeasurementVariable.getName()) && StringUtils
							.isEmpty(measurementData.getValue())) {
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

				final Integer varId = ontologyDataManager
						.getStandardVariableIdByPropertyScaleMethod(measurementVariable.getProperty(), measurementVariable.getScale(),
								measurementVariable.getMethod());

				if (varId == null) {

					final Set<StandardVariable> variableSet =
							ontologyDataManager.findStandardVariablesByNameOrSynonym(measurementVariable.getName(), programUUID);

					for (final StandardVariable variable : variableSet) {
						messages.add(new Message("error.import.existing.standard.variable.name", measurementVariable.getName(),
								variable.getProperty().getName(), variable.getScale().getName(), variable.getMethod().getName()));
					}

				}
			}
		}

		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}
	}

	private void checkForDuplicatePSMCombo(final Workbook workbook, final List<Message> messages) throws WorkbookParserException {
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
			final Integer standardVariableId = this.getOntologyDataManager()
					.getStandardVariableIdByPropertyScaleMethod(measurementVariable.getProperty(), measurementVariable.getScale(),
							measurementVariable.getMethod());
			final String key;
			if (standardVariableId != null) {
				key = Integer.toString(standardVariableId);
			} else {
				key = measurementVariable.getProperty().toLowerCase() + "-" + measurementVariable.getScale().toLowerCase() + "-"
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
					errors.get(measurementVariable.getName() + ":" + measurementVariable.getTermId())
							.add(new Message(ERROR_DUPLICATE_PSM, duplicates.toString()));
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
		trialInstanceNumber =
				this.getTrialInstanceNumberFromMeasurementRows(ontology, workbook.getObservations(), workbook.getTrialFactors());

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

	protected boolean isTermExists(final int termId, final List<MeasurementVariable> measurementVariables,
			final OntologyDataManager ontology) {

		for (final MeasurementVariable mvar : measurementVariables) {

			final Integer varId =
					ontology.getStandardVariableIdByPropertyScaleMethod(mvar.getProperty(), mvar.getScale(), mvar.getMethod());

			if (varId != null && varId.intValue() == termId) {
				return true;
			}
		}

		return false;

	}

	private String getTrialInstanceNumberFromMeasurementRows(final OntologyDataManager ontology, final List<MeasurementRow> measurementRows,
			final List<MeasurementVariable> trialFactors) {

		// get first row - should contain the study location
		final MeasurementRow row = measurementRows.get(0);
		final Optional<MeasurementVariable> result =
				findMeasurementVariableByTermId(TermId.TRIAL_INSTANCE_FACTOR.getId(), ontology, trialFactors);
		if (result.isPresent()) {
			return row.getMeasurementDataValue(result.get().getName());
		}

		return null;
	}

	private String getTrialInstanceNumberFromMeasurementVariables(final OntologyDataManager ontology,
			final List<MeasurementVariable> list) {
		final Optional<MeasurementVariable> result = findMeasurementVariableByTermId(TermId.TRIAL_INSTANCE_FACTOR.getId(), ontology, list);
		if (result.isPresent()) {
			return result.get().getValue();
		}
		return null;
	}

	Optional<MeasurementVariable> findMeasurementVariableByTermId(final int termId, final OntologyDataManager ontology,
			final List<MeasurementVariable> list) {
		for (final MeasurementVariable mvar : list) {
			final Integer varId =
					ontology.getStandardVariableIdByPropertyScaleMethod(mvar.getProperty(), mvar.getScale(), mvar.getMethod());
			if (varId != null && termId == varId.intValue()) {
				return Optional.of(mvar);
			}
		}
		return Optional.absent();
	}

	/**
	 * If the term is found in the list, this resets the required field of MeasurementVariable to true.
	 *
	 * @param termId
	 * @param ontology
	 * @param list
	 */
	void setRequiredField(final int termId, final OntologyDataManager ontology, final List<MeasurementVariable> list) {
		final Optional<MeasurementVariable> result = this.findMeasurementVariableByTermId(termId, ontology, list);
		if (result.isPresent()) {
			result.get().setRequired(true);
		}
	}

	@Override
	public void checkForInvalidGids(final Workbook workbook, final List<Message> messages) {

		Optional<MeasurementVariable> gidResult =
				findMeasurementVariableByTermId(TermId.GID.getId(), this.ontologyDataManager, workbook.getFactors());
		if (gidResult.isPresent()) {

			String gidLabel = gidResult.get().getName();
			Set<Integer> gids = extractGidsFromObservations(gidLabel, workbook.getObservations());

			if (!checkIfAllObservationHasGid(gidLabel, workbook.getObservations()) || !checkIfAllGidsExistInDatabase(
					this.germplasmDataManager, gids)) {
				messages.add(new Message(DataImportServiceImpl.ERROR_INVALID_GIDS_FROM_DATA_FILE));
			}

		} else {

			messages.add(new Message(DataImportServiceImpl.ERROR_GID_DOESNT_EXIST));

		}

	}

	/**
	 * Returns true if the gids in the list are all existing records in the database.
	 *
	 * @param germplasmDataManager
	 * @param gids
	 * @return
	 */
	boolean checkIfAllGidsExistInDatabase(final GermplasmDataManager germplasmDataManager, final Set<Integer> gids) {

		long matchedGermplasmCount = germplasmDataManager.countMatchGermplasmInList(gids);

		// If the number of gids in the list matched the count of germplasm records matched in the database, it means
		// that all gids searched have existing records in the database.
		if (gids.size() == matchedGermplasmCount) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Returns true if all observation from data file contains gid value.
	 *
	 * @param observations
	 * @return
	 */
	boolean checkIfAllObservationHasGid(final String gidLabel, final List<MeasurementRow> observations) {

		for (MeasurementRow observationRow : observations) {
			String gidString = observationRow.getMeasurementDataValue(gidLabel);
			if (StringUtil.isNullOrEmpty(gidString)) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Gets a list of gids extracted from observation.
	 *
	 * @param workbook
	 * @return
	 */
	Set<Integer> extractGidsFromObservations(final String gidLabel, final List<MeasurementRow> observations) {
		Set<Integer> gids = new HashSet<>();
		for (MeasurementRow observationRow : observations) {
			String gidString = observationRow.getMeasurementDataValue(gidLabel);
			if (StringUtils.isNotBlank(gidString)) {
				gids.add(Integer.valueOf(gidString));
			}
		}
		return gids;
	}

	@Override
	public boolean checkIfProjectNameIsExistingInProgram(final String name, final String programUUID) {
		return this.getDmsProjectDao().checkIfProjectNameIsExistingInProgram(name, programUUID);
	}

	@Override
	public Integer getLocationIdByProjectNameAndDescriptionAndProgramUUID(final String projectName, final String locationDescription,
			final String programUUID) {
		return this.getGeolocationDao()
				.getLocationIdByProjectNameAndDescriptionAndProgramUUID(projectName, locationDescription, programUUID);
	}

	@Override
	public Map<String, List<Message>> validateProjectOntology(final Workbook workbook, final String programUUID) {
		final Map<String, List<Message>> errors = new HashMap<String, List<Message>>();

		final OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(this.getSessionProvider());

		// DMV : TODO change implem so that backend is agnostic to UI when determining messages

		if (!this.isTermExists(TermId.ENTRY_NO.getId(), workbook.getFactors(), ontology)) {
			this.initializeIfNull(errors, Constants.MISSING_ENTRY);
			errors.get(Constants.MISSING_ENTRY).add(new Message("error.entry.doesnt.exist.wizard"));
		}

		if (!this.isTermExists(TermId.GID.getId(), workbook.getFactors(), ontology)) {
			this.initializeIfNull(errors, Constants.MISSING_GID);
			errors.get(Constants.MISSING_GID).add(new Message("error.gid.doesnt.exist.wizard"));
		}

		if ((workbook.getImportType() == null || workbook.getImportType() == DataSetType.PLOT_DATA.getId()) && (
				!this.isTermExists(TermId.PLOT_NO.getId(), workbook.getFactors(), ontology) && !this
						.isTermExists(TermId.PLOT_NNO.getId(), workbook.getFactors(), ontology))) {
			this.initializeIfNull(errors, Constants.MISSING_PLOT);
			errors.get(Constants.MISSING_PLOT).add(new Message("error.plot.doesnt.exist.wizard"));
		}

		if (!workbook.isNursery() && !this.isTermExists(TermId.TRIAL_INSTANCE_FACTOR.getId(), workbook.getTrialVariables(), ontology)) {
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
		this.setRequiredFields(ontology, workbook);

		final List<Message> requiredVariableValueErrors = this.checkForEmptyRequiredVariables(workbook);

		if (!requiredVariableValueErrors.isEmpty()) {
			errors.put(Constants.OBSERVATION_DATA_ERRORS, requiredVariableValueErrors);
		}

		return errors;
	}

	protected void setRequiredFields(final OntologyDataManager ontology, final Workbook workbook) {

		this.setRequiredField(TermId.PLOT_NO.getId(), ontology, workbook.getFactors());
		this.setRequiredField(TermId.PLOT_NNO.getId(), ontology, workbook.getFactors());
		this.setRequiredField(TermId.ENTRY_NO.getId(), ontology, workbook.getFactors());
		this.setRequiredField(TermId.GID.getId(), ontology, workbook.getFactors());

		if (!workbook.isNursery()) {
			this.setRequiredField(TermId.TRIAL_INSTANCE_FACTOR.getId(), ontology, workbook.getTrialVariables());
		}

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
				final int numberOfVariatesData = this.getPhenotypeDao()
						.countVariatesDataOfStudy(isMeansDataImport ? workbook.getMeansDatasetId() : workbook.getMeasurementDatesetId(),
								variateIds);
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
				errors.get(Constants.GLOBAL).add(new Message("error.duplicate.trial.instance",
						trialInstanceNumbers.toString().substring(0, trialInstanceNumbers.toString().length() - 1)));
			}

		}
	}

	public int getMaxRowLimit() {
		return this.maxRowLimit;
	}

	public void setMaxRowLimit(int value) {

		if (value > 0) {
			this.maxRowLimit = value;
		}

	}

}
