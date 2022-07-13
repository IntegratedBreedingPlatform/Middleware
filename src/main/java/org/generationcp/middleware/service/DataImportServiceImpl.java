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

import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchService;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.Constants;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.operation.saver.WorkbookSaver;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.util.StringUtil;
import org.generationcp.middleware.util.TimerWatch;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class DataImportServiceImpl extends Service implements DataImportService {

	private static final Logger LOG = LoggerFactory.getLogger(DataImportServiceImpl.class);
	private static final int MAX_VARIABLE_NAME_LENGTH = 32;
	private static final String ERROR_MISSING_STUDY_CONDITION = "error.missing.study.condition";
	private static final String ERROR_PLOT_DOESNT_EXIST = "error.plot.doesnt.exist";
	private static final String ERROR_ENTRY_DOESNT_EXIST = "error.entry.doesnt.exist";
	static final String ERROR_GID_DOESNT_EXIST = "error.gid.doesnt.exist";
	private static final String ERROR_DUPLICATE_STUDY_NAME = "error.duplicate.study.name";
	private static final String ERROR_DUPLICATE_PSM = "error.duplicate.psm";
	static final String ERROR_INVALID_VARIABLE_NAME_LENGTH = "error.invalid.variable.name.length";
	static final String ERROR_INVALID_VARIABLE_NAME_CHARACTERS = "error.invalid.variable.name.characters";
	static final String ERROR_INVALID_GIDS_FROM_DATA_FILE = "error.invalid.gids";
	private static final String LOCATION_ID_DOESNT_EXISTS = "error.location.id.doesnt.exists";

	private int maxRowLimit = WorkbookParser.DEFAULT_MAX_ROW_LIMIT;

	@Resource
	private OntologyDataManager ontologyDataManager;

	@Resource
	private GermplasmSearchService germplasmSearchService;

	@Resource
	private LocationDataManager locationDataManager;

	@Resource
	private TermDataManager termDataManager;

	@Resource
	private WorkbookSaver workbookSaver;

	private DaoFactory daoFactory;

	public DataImportServiceImpl() {

	}

	public DataImportServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	/**
	 * Saves a Dataset from a Workbook into the database via 1. Saving new
	 * Ontology Variables (column headers) 2. Saving data
	 * <p/>
	 */
	@Override
	public int saveDataset(final Workbook workbook, final String programUUID, final CropType crop) {
		return this.saveDataset(workbook, false, false, programUUID, crop);
	}

	/**
	 * Transaction 1 : Transform Variables and save new Ontology Terms Send :
	 * xls workbook Return : Map of 3 sub maps with transformed allVariables
	 * (ontology fully loaded) - here is how it was loaded : -- headers :
	 * Strings // headerMap.put("trialHeaders", trialHeaders); // --
	 * variableTypeLists (VariableTypeList) //
	 * variableTypeMap.put("trialVariableTypeList", trialVariableTypeList); //
	 * variableTypeMap.put("trialVariables", trialVariables); //
	 * variableTypeMap.put("effectVariables", effectVariables); // --
	 * measurementVariables (List<MeasurementVariable>) //
	 * measurementVariableMap.put("trialMV", trialMV); //
	 * measurementVariableMap.put("effectMV", effectMV);
	 *
	 * @param workbook
	 * @param retainValues         if true, values of the workbook items are retained, else they
	 *                             are cleared to conserve memory
	 * @param isDeleteObservations
	 * @param crop
	 * @return
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int saveDataset(
		final Workbook workbook, final boolean retainValues, final boolean isDeleteObservations,
		final String programUUID, final CropType crop) {

		Map<String, ?> variableMap = null;
		final TimerWatch timerWatch = new TimerWatch("saveDataset (grand total)");
		try {

			final boolean isUpdate = workbook.getStudyDetails() != null && workbook.getStudyDetails().getId() != null;
			if (isUpdate) {
				this.workbookSaver.saveWorkbookVariables(workbook);
				this.workbookSaver.removeDeletedVariablesAndObservations(workbook);
			}
			variableMap = this.workbookSaver.saveVariables(workbook, programUUID);

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered with saving to database: ", e);
		} finally {
			timerWatch.stop();
		}

		// Send : Map of 3 sub maps, with data to create Dataset
		// Receive int (success/fail)

		try {

			return this.workbookSaver.saveDataset(workbook, variableMap, retainValues, isDeleteObservations, programUUID, crop);

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered with saving to database: ", e);
		} finally {
			timerWatch.stop();
		}
	}

	@Override
	public Workbook parseWorkbook(final File file, final Integer currentIbdbUserId) throws WorkbookParserException {
		final WorkbookParser parser = new WorkbookParser(this.maxRowLimit);

		final org.apache.poi.ss.usermodel.Workbook excelWorkbook = parser.loadFileToExcelWorkbook(file);

		// partially parse the file to parse the description sheet only at first
		// Set performValidation to false to disable validation during parsing.
		final Workbook workbook = parser.parseFile(excelWorkbook, false, currentIbdbUserId.toString());

		parser.parseAndSetObservationRows(excelWorkbook, workbook, false);

		return workbook;
	}

	@Override
	public Workbook parseWorkbookDescriptionSheet(final org.apache.poi.ss.usermodel.Workbook excelWorkbook, final Integer currentIbdbUserId)
		throws WorkbookParserException {
		final WorkbookParser parser = new WorkbookParser(this.maxRowLimit);
		// Only parses the description sheet.
		final Workbook workbook = parser.parseFile(excelWorkbook, false, currentIbdbUserId.toString());
		this.setMeasurementIdIfNecessary(workbook.getAllVariables());
		return workbook;
	}

	@Override
	public Workbook strictParseWorkbook(final File file, final String programUUID, final Integer currentIbdbUserId)
		throws WorkbookParserException {
		final WorkbookParser parser = new WorkbookParser(this.maxRowLimit);

		final org.apache.poi.ss.usermodel.Workbook excelWorkbook = parser.loadFileToExcelWorkbook(file);

		// partially parse the file to parse the description sheet only at first
		return this.strictParseWorkbook(file, parser, parser.parseFile(excelWorkbook, true, currentIbdbUserId.toString()), programUUID);
	}

	protected Workbook strictParseWorkbook(final File file, final WorkbookParser parser, final Workbook workbook, final String programUUID)
		throws WorkbookParserException {

		// perform validations on the parsed data that require db access
		final List<Message> messages = new LinkedList<>();

		final Set<Integer> factorsTermIds = this.getTermIdsOfMeasurementVariables(workbook.getFactors());
		final Set<Integer> trialVariablesTermIds = this.getTermIdsOfMeasurementVariables(workbook.getTrialVariables());

		if (factorsTermIds.contains(TermId.TRIAL_LOCATION.getId()) && !factorsTermIds.contains(TermId.LOCATION_ID.getId())) {
			messages.add(new Message(DataImportServiceImpl.LOCATION_ID_DOESNT_EXISTS));
		}

		if (!factorsTermIds.contains(TermId.ENTRY_NO.getId())) {
			messages.add(new Message(DataImportServiceImpl.ERROR_ENTRY_DOESNT_EXIST));
		}

		if (!factorsTermIds.contains(TermId.PLOT_NO.getId()) && !factorsTermIds.contains(TermId.PLOT_NNO.getId())) {
			messages.add(new Message(DataImportServiceImpl.ERROR_PLOT_DOESNT_EXIST));
		}

		if (!trialVariablesTermIds.contains(TermId.TRIAL_INSTANCE_FACTOR.getId())) {
			messages.add(new Message(DataImportServiceImpl.ERROR_MISSING_STUDY_CONDITION));
		}

		messages.addAll(this.validateMeasurementVariableName(workbook.getAllVariables()));

		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}

		final org.apache.poi.ss.usermodel.Workbook excelWorkbook = parser.loadFileToExcelWorkbook(file);

		// this version of the workbookparser method is also capable of throwing
		// a workbookparserexception with a list of messages
		// containing validation errors inside
		parser.parseAndSetObservationRows(excelWorkbook, workbook, false);

		this.setRequiredFields(workbook);

		// separated the validation of observations from the parsing so that it
		// can be used even in other parsing implementations (e.g., the
		// one for Wizard style)
		messages.addAll(this.checkForEmptyRequiredVariables(workbook));

		this.checkForInvalidGids(workbook, messages, programUUID);

		// moved checking below as this needs to parse the contents of the
		// observation sheet for multi-locations
		this.checkForDuplicateStudyName(workbook, messages, programUUID);

		// GCP-6253
		this.checkForDuplicateVariableNames(workbook, messages, programUUID);

		this.checkForDuplicatePSMCombo(workbook, messages);

		this.checkForInvalidLabel(workbook, messages);

		if (this.checkForOutOfBoundsData(workbook, programUUID)) {
			workbook.setHasOutOfBoundsData(true);
		}

		return workbook;
	}

	@Override
	public Workbook parseWorkbook(
		final File file, final String programUUID, final boolean discardInvalidValues,
		final WorkbookParser workbookParser, final Integer currentIbdbUserId) throws WorkbookParserException {

		final org.apache.poi.ss.usermodel.Workbook excelWorkbook = workbookParser.loadFileToExcelWorkbook(file);

		// Parse the description sheet only at first
		final Workbook workbook = workbookParser.parseFile(excelWorkbook, false, currentIbdbUserId.toString());

		// Location Name is mandatory when creating a new study, so we need to automatically
		// add Location Name variable if is not available in the imported workbook.
		this.addLocationIDVariableIfNotExists(workbook, workbook.getConditions(), programUUID);
		this.assignLocationVariableWithUnspecifiedLocationIfEmpty(workbook.getConditions());
		this.assignLocationIdVariableToEnvironmentDetailSection(workbook);

		this.removeLocationNameVariableIfExists(workbook);

		// Remove obsolete factors, conditions, constants and traits in the
		// workbook if there's any
		final List<String> obsoleteVariableNames = this.removeObsoloteVariablesInWorkbook(workbook, programUUID);

		workbookParser.removeObsoleteColumnsInExcelWorkbook(excelWorkbook, obsoleteVariableNames);

		// Populate possible values for categorical variates
		this.populatePossibleValuesForCategoricalVariates(workbook.getVariates(), programUUID);

		// Parse the observation sheet
		workbookParser.parseAndSetObservationRows(excelWorkbook, workbook, discardInvalidValues);

		return workbook;
	}

	@Override
	public void addLocationIDVariableIfNotExists(
		final Workbook workbook, final List<MeasurementVariable> measurementVariables,
		final String programUUID) {

		final List<MeasurementVariable> combinedVariables = new ArrayList<>();
		combinedVariables.addAll(workbook.getConditions());
		combinedVariables.addAll(workbook.getFactors());

		final boolean locationIdExists = this.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), combinedVariables).isPresent();

		// If LOCATION_ID variable is not existing in both Condition and Factors Section of workbook
		// Automatically add LOCATION_ID variable as it is required in creating a new Study.
		if (!locationIdExists) {
			final MeasurementVariable locationIdVariable = this.createLocationVariable(programUUID);
			measurementVariables.add(locationIdVariable);
		}
	}

	@Override
	public void addExptDesignVariableIfNotExists(
		final Workbook workbook, final List<MeasurementVariable> measurementVariables,
		final String programUUID) {

		final List<MeasurementVariable> combinedVariables = new ArrayList<>();
		combinedVariables.addAll(workbook.getConditions());
		combinedVariables.addAll(workbook.getFactors());

		final Optional<MeasurementVariable> exptDesignExists = this.findMeasurementVariableByTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), combinedVariables);

		if (!exptDesignExists.isPresent()) {
			final MeasurementVariable exptDesignVar = this.createMeasurementVariable(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
				String.valueOf(TermId.EXTERNALLY_GENERATED.getId()), Operation.ADD, PhenotypicType.TRIAL_ENVIRONMENT, programUUID);
			this.asssignMeasurementVariableToEnvironmentDetail(exptDesignVar);
			measurementVariables.add(exptDesignVar);
		}
	}

	@Override
	public void removeLocationNameVariableIfExists(final Workbook workbook) {

		final List<MeasurementVariable> workbookConditions = workbook.getConditions();
		final List<MeasurementVariable> workbookFactors = workbook.getFactors();

		// If the workbook file contains LOCATION_NAME variable, we should not save it in the database.
		// We only need to save LOCATION_ID varible.
		final Optional<MeasurementVariable> locationNameInCondition =
			this.findMeasurementVariableByTermId(TermId.TRIAL_LOCATION.getId(), workbookConditions);
		if (locationNameInCondition.isPresent()) {
			workbookConditions.remove(locationNameInCondition.get());
		}
		final Optional<MeasurementVariable> locationNameFactors =
			this.findMeasurementVariableByTermId(TermId.TRIAL_LOCATION.getId(), workbookFactors);
		if (locationNameFactors.isPresent()) {
			workbookFactors.remove(locationNameFactors.get());
		}

	}

	@Override
	public void assignLocationVariableWithUnspecifiedLocationIfEmpty(final List<MeasurementVariable> measurementVariables) {

		final Optional<MeasurementVariable> locationIdMeasurementVariable =
			this.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), measurementVariables);
		if (locationIdMeasurementVariable.isPresent() && StringUtils.isEmpty(locationIdMeasurementVariable.get().getValue())) {
			locationIdMeasurementVariable.get().setValue(this.locationDataManager.retrieveLocIdOfUnspecifiedLocation());
		}

	}

	@Override
	public void assignLocationIdVariableToEnvironmentDetailSection(final Workbook workbook) {

		final Optional<MeasurementVariable> locationIdVariableInFactors =
			this.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), workbook.getFactors());
		final Optional<MeasurementVariable> locationIdVariableInConditions =
			this.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), workbook.getConditions());
		if (locationIdVariableInFactors.isPresent()) {
			this.asssignMeasurementVariableToEnvironmentDetail(locationIdVariableInFactors.get());

		}
		if (locationIdVariableInConditions.isPresent()) {
			this.asssignMeasurementVariableToEnvironmentDetail(locationIdVariableInConditions.get());
		}

	}

	@Override
	public void processExperimentalDesign(final Workbook workbook, final String programUUID, final String exptDesignValueFromObsSheet)
		throws WorkbookParserException {
		final Optional<MeasurementVariable> exptDesignVarInConditions =
			this.findMeasurementVariableByTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), workbook.getConditions());
		if(exptDesignVarInConditions.isPresent()) {
			final MeasurementVariable exptDesignVar = exptDesignVarInConditions.get();
			if (exptDesignVar.getValue() != null && !exptDesignVar.getValue().isEmpty()) {
				exptDesignVar.setValue(this.getExperimentalDesignIdValue(exptDesignVar.getValue()));
			} else {
				exptDesignVar.setValue(this.getExperimentalDesignIdValue(exptDesignValueFromObsSheet));
			}
		}

		final Optional<MeasurementVariable> exptDesignVarInFactors =
			this.findMeasurementVariableByTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), workbook.getFactors());
		if(exptDesignVarInFactors.isPresent()) {
			final MeasurementVariable exptDesignVar = exptDesignVarInFactors.get();
			if(!exptDesignVarInConditions.isPresent()) {
				exptDesignVar.setValue(this.getExperimentalDesignIdValue(exptDesignValueFromObsSheet));
				exptDesignVar.setVariableType(VariableType.ENVIRONMENT_DETAIL);
				workbook.getConditions().add(exptDesignVar);
			}
			workbook.getFactors().remove(exptDesignVarInFactors.get());
		}

		if(!exptDesignVarInConditions.isPresent() && !exptDesignVarInFactors.isPresent()) {
			final MeasurementVariable exptDesignVar = this.createMeasurementVariable(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
				String.valueOf(TermId.EXTERNALLY_GENERATED.getId()), Operation.ADD, PhenotypicType.TRIAL_ENVIRONMENT, programUUID);
			exptDesignVar.setVariableType(VariableType.ENVIRONMENT_DETAIL);
			workbook.getConditions().add(exptDesignVar);
		}

	}

	@Override
	public void addEntryTypeVariableIfNotExists(final Workbook workbook,
		final String programUUID) {

		final Optional<MeasurementVariable> entryTypeFactor = this.findMeasurementVariableByTermId(TermId.ENTRY_TYPE.getId(), workbook.getEntryDetails());
		final Optional<List<MeasurementRow>> rowsWithoutTermId = this.findMeasurementRowWithoutTermId(TermId.ENTRY_TYPE.getId(), workbook.getObservations());

		// ENTRY_TYPE Not existing in Factors and in Observation
		if (!entryTypeFactor.isPresent() && rowsWithoutTermId.isPresent()) {
			final MeasurementVariable entryType = this.createMeasurementVariable(TermId.ENTRY_TYPE.getId(), null, Operation.ADD, PhenotypicType.ENTRY_DETAIL, programUUID);
			workbook.getFactors().add(entryType);
			final MeasurementData data = new MeasurementData(entryType.getLabel(), String.valueOf(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId()), false, entryType.getDataType(), entryType);
			workbook.getObservations().forEach(row->row.getDataList().add(data));
		}
	}

	String getExperimentalDesignIdValue(final String experimentalDesign) throws WorkbookParserException{
		if(experimentalDesign != null && !experimentalDesign.isEmpty()) {
			final Term exptDesignValue = this.termDataManager.getTermByName(experimentalDesign);
			if(exptDesignValue != null) {
				return String.valueOf(exptDesignValue.getId());
			}
			throw new WorkbookParserException("Invalid value for Experimental Design");
		}
		return String.valueOf(TermId.EXTERNALLY_GENERATED.getId());
	}

	protected void asssignMeasurementVariableToEnvironmentDetail(final MeasurementVariable measurementVariable) {

		measurementVariable.setLabel(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0));
		measurementVariable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
		measurementVariable.setVariableType(VariableType.ENVIRONMENT_DETAIL);

	}

	protected MeasurementVariable createLocationVariable(final String programUUID) {

		// Creates a LOCATION_ID Variable with default value of "Unspecified Location".
		// This variable will be added to an imported study with no LOCATION_ID Variable specified in the file.
		final String unspecifiedLocationId = this.locationDataManager.retrieveLocIdOfUnspecifiedLocation();
		final MeasurementVariable variable =
			this.createMeasurementVariable(TermId.LOCATION_ID.getId(), unspecifiedLocationId, Operation.ADD,
				PhenotypicType.TRIAL_ENVIRONMENT, programUUID);
		variable.setName(Workbook.DEFAULT_LOCATION_ID_VARIABLE_ALIAS);
		variable.setVariableType(VariableType.ENVIRONMENT_DETAIL);

		return variable;

	}

	protected MeasurementVariable createMeasurementVariable(
		final int idToCreate, final String value, final Operation operation,
		final PhenotypicType role, final String programUUID) {
		final StandardVariable stdvar = this.ontologyDataManager.getStandardVariable(Integer.valueOf(idToCreate), programUUID);
		stdvar.setPhenotypicType(role);
		final MeasurementVariable var =
			new MeasurementVariable(idToCreate, stdvar.getName(), stdvar.getDescription(), stdvar.getScale().getName(),
				stdvar.getMethod().getName(), stdvar.getProperty().getName(), stdvar.getDataType().getName(), value,
				stdvar.getPhenotypicType().getLabelList().get(0));
		var.setRole(role);
		var.setDataTypeId(stdvar.getDataType().getId());
		var.setFactor(false);
		var.setOperation(operation);
		return var;

	}

	/**
	 * Remove obsolete variables (factors, conditions, constants and variates)
	 * in Workbook. Returns the list of all obsolete variable names that are
	 * removed.
	 *
	 * @param workbook
	 * @param programUUID
	 * @return
	 */
	protected List<String> removeObsoloteVariablesInWorkbook(final Workbook workbook, final String programUUID) {

		final List<String> obsoleteVariableNames = new ArrayList<>();

		obsoleteVariableNames.addAll(this.removeObsoleteMeasurementVariables(workbook.getConditions(), programUUID));
		obsoleteVariableNames.addAll(this.removeObsoleteMeasurementVariables(workbook.getFactors(), programUUID));
		obsoleteVariableNames.addAll(this.removeObsoleteMeasurementVariables(workbook.getConstants(), programUUID));
		obsoleteVariableNames.addAll(this.removeObsoleteMeasurementVariables(workbook.getVariates(), programUUID));

		return obsoleteVariableNames;

	}

	/**
	 * Remove obsolete variables in specified measuremnt variable list. Returns
	 * the list of all obsolete variable names that are removed.
	 *
	 * @param measurementVariables
	 * @param programUUID
	 * @return
	 */
	protected List<String> removeObsoleteMeasurementVariables(
		final List<MeasurementVariable> measurementVariables,
		final String programUUID) {

		final List<String> obsoleteVariableNames = new ArrayList<>();
		final Iterator<MeasurementVariable> iterator = measurementVariables.iterator();

		while (iterator.hasNext()) {

			final MeasurementVariable measurementVariable = iterator.next();

			final StandardVariable standardVariable = this.ontologyDataManager
				.findStandardVariableByTraitScaleMethodNames(measurementVariable.getProperty(), measurementVariable.getScale(),
					measurementVariable.getMethod(), programUUID);

			if (standardVariable != null && standardVariable.isObsolete()) {
				obsoleteVariableNames.add(measurementVariable.getName());
				iterator.remove();
			}

		}

		return obsoleteVariableNames;

	}

	@Override
	public void populatePossibleValuesForCategoricalVariates(final List<MeasurementVariable> variates, final String programUUID) {

		for (final MeasurementVariable measurementVariable : variates) {

			final StandardVariable standardVariable = this.ontologyDataManager
				.findStandardVariableByTraitScaleMethodNames(measurementVariable.getProperty(), measurementVariable.getScale(),
					measurementVariable.getMethod(), programUUID);

			if (standardVariable != null && standardVariable.getDataType().getId() == DataType.CATEGORICAL_VARIABLE.getId()) {

				final List<ValueReference> possibleValues = new ArrayList<>();
				if (standardVariable.getEnumerations() != null && !standardVariable.getEnumerations().isEmpty()) {
					for (final Enumeration enumeration : standardVariable.getEnumerations()) {
						possibleValues.add(new ValueReference(enumeration.getId(), enumeration.getName(), enumeration.getDescription()));
					}

				}
				measurementVariable.setPossibleValues(possibleValues);
				measurementVariable.setDataTypeId(standardVariable.getDataType().getId());
			}
		}
	}

	@Override
	public boolean checkForOutOfBoundsData(final Workbook workbook, final String programUUID) {

		final Map<String, List<String>> termIdValidValuesMap = this.getValidValuesMapForCategoricalVariates(workbook, programUUID);

		for (final MeasurementRow measurementRow : workbook.getObservations()) {
			for (final Entry<String, List<String>> entry : termIdValidValuesMap.entrySet()) {
				final MeasurementData measurementData = measurementRow.getMeasurementData(entry.getKey());
				if (measurementData != null && !StringUtil.containsIgnoreCase(entry.getValue(), measurementData.getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	protected Map<String, List<String>> getValidValuesMapForCategoricalVariates(final Workbook workbook, final String programUUID) {

		// Get the categorical variates and extract their valid values (possible
		// values)
		final Map<String, List<String>> variableValidValues = new HashMap<>();

		for (final MeasurementVariable measurementVariable : workbook.getVariates()) {
			final Integer stdVariableId = this.ontologyDataManager
				.getStandardVariableIdByPropertyScaleMethod(measurementVariable.getProperty(), measurementVariable.getScale(),
					measurementVariable.getMethod());
			if (stdVariableId != null) {
				final StandardVariable stdVariable = this.ontologyDataManager.getStandardVariable(stdVariableId, programUUID);
				if (stdVariable.getDataType().getId() == DataType.CATEGORICAL_VARIABLE.getId()) {
					final List<String> validValues = new ArrayList<>();
					for (final Enumeration enumeration : stdVariable.getEnumerations()) {
						validValues.add(enumeration.getName());
					}
					variableValidValues.put(measurementVariable.getName(), validValues);
				}

			}
		}
		return variableValidValues;
	}

	protected List<Message> validateMeasurementVariableName(final List<MeasurementVariable> allVariables) {
		final List<Message> messages = new ArrayList<>();

		messages.addAll(this.validateMeasurmentVariableNameLengths(allVariables));
		messages.addAll(this.validateMeasurmentVariableNameCharacters(allVariables));

		return messages;
	}

	protected List<Message> validateMeasurmentVariableNameLengths(final List<MeasurementVariable> variableList) {
		final List<Message> messages = new ArrayList<>();

		for (final MeasurementVariable mv : variableList) {
			if (mv.getName().length() > DataImportServiceImpl.MAX_VARIABLE_NAME_LENGTH) {
				messages.add(new Message(DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_LENGTH, mv.getName()));
			}
		}

		return messages;
	}

	protected List<Message> validateMeasurmentVariableNameCharacters(final List<MeasurementVariable> variableList) {
		final List<Message> messages = new ArrayList<>();

		for (final MeasurementVariable mv : variableList) {
			if (!mv.getName().matches("^[^0-9][\\w\\d%]*$")) {
				messages.add(new Message(DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_CHARACTERS, mv.getName()));
			}
		}

		return messages;
	}

	private List<Message> checkForEmptyRequiredVariables(final Workbook workbook) {
		final List<Message> returnVal = new ArrayList<>();

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
		// current implem leverages the setting of the required variable in
		// earlier checks
		final List<MeasurementVariable> returnVal = new ArrayList<>();

		for (final MeasurementVariable measurementVariable : workbook.getAllVariables()) {
			if (measurementVariable.isRequired()) {
				returnVal.add(measurementVariable);
			}
		}

		return returnVal;
	}

	private void checkForDuplicateStudyName(final Workbook workbook, final List<Message> messages, final String programUUID)
		throws WorkbookParserException {

		final String studyName = workbook.getStudyDetails().getStudyName();
		final String locationDescription = this.getLocationDescription(workbook);
		final Integer locationId = this.getLocationIdByProjectNameAndDescriptionAndProgramUUID(studyName, locationDescription, programUUID);

		// same location and study
		if (locationId != null) {
			messages.add(new Message(DataImportServiceImpl.ERROR_DUPLICATE_STUDY_NAME));
		} else {
			final boolean isExisting = this.checkIfProjectNameIsExistingInProgram(studyName, programUUID);
			if (isExisting) {
				messages.add(new Message(DataImportServiceImpl.ERROR_DUPLICATE_STUDY_NAME));
			}
			// else we will create a new study or append the data sets to the
			// existing study
		}

		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}
	}

	private void checkForDuplicateVariableNames(final Workbook workbook, final List<Message> messages, final String programUUID)
		throws WorkbookParserException {
		final List<List<MeasurementVariable>> workbookVariables = new ArrayList<>();
		workbookVariables.add(workbook.getConditions());
		workbookVariables.add(workbook.getFactors());
		workbookVariables.add(workbook.getConstants());
		workbookVariables.add(workbook.getVariates());
		final Map<String, MeasurementVariable> variableNameMap = new HashMap<>();
		for (final List<MeasurementVariable> variableList : workbookVariables) {

			for (final MeasurementVariable measurementVariable : variableList) {
				if (variableNameMap.containsKey(measurementVariable.getName())) {
					messages.add(new Message("error.duplicate.local.variable", measurementVariable.getName()));
				} else {
					variableNameMap.put(measurementVariable.getName(), measurementVariable);
				}

				final Integer varId = this.ontologyDataManager
					.getStandardVariableIdByPropertyScaleMethod(measurementVariable.getProperty(), measurementVariable.getScale(),
						measurementVariable.getMethod());

				if (varId == null) {

					final Set<StandardVariable> variableSet =
						this.ontologyDataManager.findStandardVariablesByNameOrSynonym(measurementVariable.getName(), programUUID);

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
		Map<String, List<MeasurementVariable>> stdVarMap = this.checkForDuplicates(workbook.getNonVariateVariables());
		this.addErrorForDuplicates(messages, stdVarMap);
		stdVarMap = this.checkForDuplicates(workbook.getVariateVariables());
		this.addErrorForDuplicates(messages, stdVarMap);
		if (!messages.isEmpty()) {
			throw new WorkbookParserException(messages);
		}
	}

	private void checkForDuplicatePSMCombo(final Workbook workbook, final Map<String, List<Message>> errors) {
		Map<String, List<MeasurementVariable>> stdVarMap = this.checkForDuplicates(workbook.getNonVariateVariables());
		this.addErrorForDuplicates(errors, stdVarMap);
		stdVarMap = this.checkForDuplicates(workbook.getVariateVariables());
		this.addErrorForDuplicates(errors, stdVarMap);
		stdVarMap = this.checkForDuplicates(workbook.getEntryDetails());
		this.addErrorForDuplicates(errors, stdVarMap);
	}

	private Map<String, List<MeasurementVariable>> checkForDuplicates(
		final List<MeasurementVariable> workbookVariables) {
		final Map<String, List<MeasurementVariable>> stdVarMap = new LinkedHashMap<>();
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
				vars = new ArrayList<>();
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
						.add(new Message(DataImportServiceImpl.ERROR_DUPLICATE_PSM, duplicates.toString()));
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
				errors.add(new Message(DataImportServiceImpl.ERROR_DUPLICATE_PSM, duplicates.toString()));
			}
		}
	}

	private void checkForInvalidLabel(final Workbook workbook, final List<Message> messages) throws WorkbookParserException {
		final List<MeasurementVariable> variableList = new ArrayList<>();
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
	private String getLocationDescription(final Workbook workbook) {

		// check if single location
		// it means the location is defined in the description sheet)
		String trialInstanceNumber = this.getTrialInstanceNumberFromMeasurementVariables(workbook.getConditions());
		if (trialInstanceNumber != null) {
			return trialInstanceNumber;
		}

		// check if multi-location
		// it means the location is defined in the observation sheet
		trialInstanceNumber = this.getTrialInstanceNumberFromMeasurementRows(workbook.getObservations(), workbook.getTrialFactors());
		if (trialInstanceNumber != null) {
			return trialInstanceNumber;
		}

		return "1";
	}

	Set<Integer> getTermIdsOfMeasurementVariables(final List<MeasurementVariable> measurementVariables) {

		final HashSet<Integer> termIds = new HashSet<>();

		for (final MeasurementVariable mvar : measurementVariables) {

			final Integer varId = this.ontologyDataManager
				.getStandardVariableIdByPropertyScaleMethod(mvar.getProperty(), mvar.getScale(), mvar.getMethod());
			if (varId != null) {
				termIds.add(varId);
			}
		}

		return termIds;
	}

	private String getTrialInstanceNumberFromMeasurementRows(
		final List<MeasurementRow> measurementRows,
		final List<MeasurementVariable> trialFactors) {

		// get first row - should contain the study location
		final MeasurementRow row = measurementRows.get(0);
		final Optional<MeasurementVariable> result =
			this.findMeasurementVariableByTermId(TermId.TRIAL_INSTANCE_FACTOR.getId(), trialFactors);
		if (result.isPresent()) {
			return row.getMeasurementDataValue(result.get().getName());
		}

		return null;
	}

	private String getTrialInstanceNumberFromMeasurementVariables(final List<MeasurementVariable> list) {
		final Optional<MeasurementVariable> result = this.findMeasurementVariableByTermId(TermId.TRIAL_INSTANCE_FACTOR.getId(), list);
		if (result.isPresent()) {
			return result.get().getValue();
		}
		return null;
	}

	@Override
	public Optional<MeasurementVariable> findMeasurementVariableByTermId(final int termId, final List<MeasurementVariable> list) {
		for (final MeasurementVariable mvar : list) {

			final Integer varId;

			if (mvar.getTermId() != 0) {
				varId = mvar.getTermId();
			} else {
				varId = this.ontologyDataManager
					.getStandardVariableIdByPropertyScaleMethod(mvar.getProperty(), mvar.getScale(), mvar.getMethod());
			}

			if (varId != null && termId == varId.intValue()) {
				return Optional.of(mvar);
			}
		}
		return Optional.absent();
	}

	/**
	 * If the term is found in the list, this resets the required field of
	 * MeasurementVariable to true.
	 *
	 * @param termId
	 * @param list
	 */
	void setRequiredField(final int termId, final List<MeasurementVariable> list) {
		final Optional<MeasurementVariable> result = this.findMeasurementVariableByTermId(termId, list);
		if (result.isPresent()) {
			result.get().setRequired(true);
		}
	}

	@Override
	public void checkForInvalidGids(final Workbook workbook, final List<Message> messages, final String programUUID) {

		final Optional<MeasurementVariable> gidResult = this.findMeasurementVariableByTermId(TermId.GID.getId(), workbook.getFactors());
		if (gidResult.isPresent()) {

			final String gidLabel = gidResult.get().getName();
			final Set<Integer> gids = this.extractGidsFromObservations(gidLabel, workbook.getObservations());

			if (!this.checkIfAllObservationHasGidAndNumeric(gidLabel, workbook.getObservations()) || !this
				.checkIfAllGidsExistInDatabase(gids, programUUID)) {
				messages.add(new Message(DataImportServiceImpl.ERROR_INVALID_GIDS_FROM_DATA_FILE));
			}

		} else {

			messages.add(new Message(DataImportServiceImpl.ERROR_GID_DOESNT_EXIST));

		}

	}

	/**
	 * Returns true if the gids in the list are all existing records in the
	 * database.
	 *
	 * @param gids
	 * @return
	 */
	boolean checkIfAllGidsExistInDatabase(final Set<Integer> gids, final String programUUID) {
		GermplasmSearchRequest searchRequest = new GermplasmSearchRequest();
		searchRequest.setGids(new ArrayList<>(gids));
		final long matchedGermplasmCount = this.germplasmSearchService.countSearchGermplasm(searchRequest, programUUID);

		// If the number of gids in the list matched the count of germplasm
		// records matched in the database, it means
		// that all gids searched have existing records in the database.
		return gids.size() == matchedGermplasmCount;

	}

	/**
	 * Returns true if all observation from data file contains gid value and all
	 * values are numeric.
	 *
	 * @param observations
	 * @return
	 */
	boolean checkIfAllObservationHasGidAndNumeric(final String gidLabel, final List<MeasurementRow> observations) {

		for (final MeasurementRow observationRow : observations) {
			final String gidString = observationRow.getMeasurementDataValue(gidLabel);
			if (StringUtils.isBlank(gidString) || !StringUtils.isNumeric(gidString)) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Gets a list of gids extracted from observation.
	 *
	 * @return
	 */
	Set<Integer> extractGidsFromObservations(final String gidLabel, final List<MeasurementRow> observations) {
		final Set<Integer> gids = new HashSet<>();
		for (final MeasurementRow observationRow : observations) {
			final String gidString = observationRow.getMeasurementDataValue(gidLabel);
			if (StringUtils.isNotBlank(gidString) && StringUtils.isNumeric(gidString)) {
				gids.add(Integer.valueOf(gidString));
			}
		}
		return gids;
	}

	@Override
	public boolean checkIfProjectNameIsExistingInProgram(final String name, final String programUUID) {
		return this.daoFactory.getDmsProjectDAO().checkIfProjectNameIsExistingInProgram(name, programUUID);
	}

	@Override
	public Integer getLocationIdByProjectNameAndDescriptionAndProgramUUID(
		final String projectName, final String locationDescription,
		final String programUUID) {
		return this.daoFactory.getGeolocationDao()
			.getLocationIdByProjectNameAndDescriptionAndProgramUUID(projectName, locationDescription, programUUID);
	}

	@Override
	public Map<String, List<Message>> validateProjectOntology(final Workbook workbook, final String programUUID) {
		final Map<String, List<Message>> errors = new HashMap<>();

		final Set<Integer> factorsTermIds = this.getTermIdsOfMeasurementVariables(workbook.getFactors());
		final Set<Integer> trialVariablesTermIds = this.getTermIdsOfMeasurementVariables(workbook.getTrialVariables());
		final Set<Integer> entryDetailsTermIds = this.getTermIdsOfMeasurementVariables(workbook.getEntryDetails());

		// DMV : TODO change implem so that backend is agnostic to UI when
		// determining messages
		if (!entryDetailsTermIds.contains(TermId.ENTRY_NO.getId())) {
			this.initializeIfNull(errors, Constants.MISSING_ENTRY_NO);
			errors.get(Constants.MISSING_ENTRY_NO).add(new Message("error.entryno.doesnt.exist.wizard"));
		}

		if (workbook.getImportType() != null && workbook.getImportType().intValue() == DatasetTypeEnum.PLOT_DATA.getId() && !entryDetailsTermIds.contains(TermId.ENTRY_TYPE.getId())) {
			this.initializeIfNull(errors, Constants.MISSING_ENTRY_TYPE);
			errors.get(Constants.MISSING_ENTRY_TYPE).add(new Message("error.entrytype.doesnt.exist.wizard"));
		}

		if (!factorsTermIds.contains(TermId.GID.getId())) {
			this.initializeIfNull(errors, Constants.MISSING_GID);
			errors.get(Constants.MISSING_GID).add(new Message("error.gid.doesnt.exist.wizard"));
		}

		if ((workbook.getImportType() == null || workbook.getImportType() == DatasetTypeEnum.PLOT_DATA.getId()) && !factorsTermIds
			.contains(TermId.PLOT_NO.getId()) && !factorsTermIds.contains(TermId.PLOT_NNO.getId())) {
			this.initializeIfNull(errors, Constants.MISSING_PLOT);
			errors.get(Constants.MISSING_PLOT).add(new Message("error.plot.doesnt.exist.wizard"));
		}

		if (!trialVariablesTermIds.contains(TermId.TRIAL_INSTANCE_FACTOR.getId())) {
			this.initializeIfNull(errors, Constants.MISSING_TRIAL);
			errors.get(Constants.MISSING_TRIAL).add(new Message(DataImportServiceImpl.ERROR_MISSING_STUDY_CONDITION));
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
	public int saveProjectOntology(final Workbook workbook, final String programUUID, final CropType crop) {

		final TimerWatch timerWatch = new TimerWatch("saveProjectOntology (grand total)");
		int studyId = 0;

		try {

			studyId = this.workbookSaver.saveProjectOntology(workbook, programUUID, crop);

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered with importing project ontology: ", e, DataImportServiceImpl.LOG);

		} finally {
			timerWatch.stop();
		}

		return studyId;
	}

	@Override
	public int saveProjectData(final Workbook workbook, final String programUUID, final CropType crop) {

		final TimerWatch timerWatch = new TimerWatch("saveProjectData (grand total)");

		try {

			this.workbookSaver.saveProjectData(workbook, programUUID, crop);

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
		final Map<String, List<Message>> errors = new HashMap<>();

		// Check that trial instance values are not yet existing and are numeric
		this.validateTrialInstanceValue(workbook, errors, programUUID);

		// the following code is a workaround versus the current state
		// management in the ETL Wizard
		// to re-set the "required" fields to true for checking later on
		this.setRequiredFields(workbook);

		final List<Message> requiredVariableValueErrors = this.checkForEmptyRequiredVariables(workbook);

		if (!requiredVariableValueErrors.isEmpty()) {
			errors.put(Constants.OBSERVATION_DATA_ERRORS, requiredVariableValueErrors);
		}

		return errors;
	}

	protected void setRequiredFields(final Workbook workbook) {

		this.setRequiredField(TermId.PLOT_NO.getId(), workbook.getFactors());
		this.setRequiredField(TermId.PLOT_NNO.getId(), workbook.getFactors());
		this.setRequiredField(TermId.ENTRY_NO.getId(), workbook.getFactors());
		this.setRequiredField(TermId.GID.getId(), workbook.getFactors());
		this.setRequiredField(TermId.TRIAL_INSTANCE_FACTOR.getId(), workbook.getTrialVariables());
	}

	private void validateTrialInstanceValue(final Workbook workbook, final Map<String, List<Message>> errors, final String programUUID) {

		final String studyName = workbook.getStudyDetails().getStudyName();

		// get local variable name of the trial instance number
		String trialInstanceHeader = null;
		MeasurementVariable trialInstanceVariable = null;
		final List<MeasurementVariable> trialVariables = workbook.getTrialFactors();
		trialVariables.addAll(workbook.getConditions());
		for (final MeasurementVariable mvar : trialVariables) {
			final Integer varId = this.ontologyDataManager
				.getStandardVariableIdByPropertyScaleMethod(mvar.getProperty(), mvar.getScale(), mvar.getMethod());
			if (varId != null) {
				final StandardVariable svar = this.ontologyDataManager.getStandardVariable(varId, programUUID);
				if (svar.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					trialInstanceVariable = mvar;
					trialInstanceHeader = mvar.getName();
					break;
				}
			}
		}
		final Set<String> instancesFromWorkbook = new LinkedHashSet<>();
		int maxNumOfIterations = 100000;
		final int observationCount = workbook.getObservations().size();
		if (observationCount < maxNumOfIterations) {
			maxNumOfIterations = observationCount;
		}
		final List<String> existingInstances = new ArrayList<>();
		for (int i = 0; i < maxNumOfIterations; i++) {
			final MeasurementRow row = workbook.getObservations().get(i);
			final String trialInstanceNumber = row.getMeasurementDataValue(trialInstanceHeader);
			final Optional<Message> errorMessage = Util.validateVariableValues(trialInstanceVariable, trialInstanceNumber);
			if (errorMessage.isPresent()) {
				errors.putIfAbsent(Constants.INVALID_TRIAL, new ArrayList<>());
				errors.get(Constants.INVALID_TRIAL).add(errorMessage.get());
			}
			if (instancesFromWorkbook.add(trialInstanceNumber)) {
				final Integer locationId =
					this.getLocationIdByProjectNameAndDescriptionAndProgramUUID(studyName, trialInstanceNumber, programUUID);
				// same location and study
				if (locationId != null) {
					existingInstances.add(trialInstanceNumber);
				}
			}
		}
		// Raise error if any of the instances to be imported already has experiments for given dataset type
		if (!existingInstances.isEmpty() && workbook.getStudyDetails().getId() != null) {
			final List<String> instancesWithExperiments = new ArrayList<>();
			final boolean isMeansDataImport = workbook.getImportType() != null && workbook.getImportType() == DatasetTypeEnum.MEANS_DATA.getId();
			final Map<String, Long> instanceExperimentsMap = this.daoFactory.getExperimentDao()
				.countObservationsPerInstance(isMeansDataImport ? workbook.getMeansDatasetId() : workbook.getMeasurementDatesetId());
			for (final String existingInstance : existingInstances) {
				if (instanceExperimentsMap.containsKey(existingInstance) && instanceExperimentsMap.get(existingInstance) > 0) {
					instancesWithExperiments.add(existingInstance);
				}
			}

			if (!instancesWithExperiments.isEmpty()) {
				this.initializeIfNull(errors, Constants.GLOBAL);
				errors.get(Constants.GLOBAL).add(new Message(
					"error.duplicate.trial.instance",
					StringUtils.join(instancesWithExperiments, ",")));
			}
		}

	}

	public int getMaxRowLimit() {
		return this.maxRowLimit;
	}

	public void setMaxRowLimit(final int maxRowLimit) {
		this.maxRowLimit = maxRowLimit;
	}

	private void setMeasurementIdIfNecessary(final List<MeasurementVariable> measurementVariables) {
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			if (measurementVariable.getTermId() == 0) {
				final Integer measurementVariableId = this.ontologyDataManager.getStandardVariableIdByPropertyScaleMethod(measurementVariable.getProperty(), measurementVariable.getScale(), measurementVariable.getMethod());
				measurementVariable.setTermId(measurementVariableId);
			}
		}
	}

	/**
	 * @param termId
	 * @param rowList
	 * @return Optional List MeasurementRow without termId
	 */
	private Optional<List<MeasurementRow>> findMeasurementRowWithoutTermId(final Integer termId, final List<MeasurementRow> rowList) {
		final List<MeasurementRow> rowWithoutTermid = new ArrayList<>();
		for (final MeasurementRow row: rowList) {
			final boolean hasTermId = row.getDataList().stream().filter(measurementData -> measurementData.getMeasurementVariable().getTermId() == termId).collect(Collectors.toList()).size() > 0;
			if (!hasTermId) {
				rowWithoutTermid.add(row);
			}
		}
		if (Util.isEmpty(rowWithoutTermid)) {
			return Optional.absent();
		} else {
			return Optional.of(rowWithoutTermid);
		}
	}
}
