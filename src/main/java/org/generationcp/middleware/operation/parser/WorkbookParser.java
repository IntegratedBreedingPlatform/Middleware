/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.operation.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.util.PoiUtil;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class WorkbookParser {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbookParser.class);

	public static final int DESCRIPTION_SHEET = 0;
	public static final int OBSERVATION_SHEET = 1;

	public static final int STUDY_NAME_ROW_INDEX = 0;
	public static final int STUDY_TITLE_ROW_INDEX = 1;
	public static final int OBJECTIVE_ROW_INDEX = 2;
	public static final int START_DATE_ROW_INDEX = 3;
	public static final int END_DATE_ROW_INDEX = 4;
	public static final int STUDY_TYPE_ROW_INDEX = 5;

	public static final int STUDY_DETAILS_LABEL_COLUMN_INDEX = 0;
	public static final int STUDY_DETAILS_VALUE_COLUMN_INDEX = 1;

	private static final String DESCRIPTION = "DESCRIPTION";
	private static final String ONTOLOGY_ID = "ONTOLOGY ID";
	private static final String PROPERTY = "PROPERTY";
	private static final String DATASET = "DATASET";
	private static final String VALUE = "VALUE";
	private static final String DATA_TYPE = "DATA TYPE";
	private static final String METHOD = "METHOD";
	private static final String SCALE = "SCALE";
	private static final String DESCRIPTION_SHEET_NAME = "Description";
	private static final String OBSERVATION_SHEET_NAME = "Observation";

	private static final int NUMBER_OF_COLUMNS = 9;

	public static final int DEFAULT_MAX_ROW_LIMIT = 10000;

	private int rowIndex;

	private List<Message> errorMessages = new ArrayList<>();
	protected boolean hasIncorrectDatatypeValue = false;
	private int maxRowLimit = DEFAULT_MAX_ROW_LIMIT;

	protected static final String[] EXPECTED_VARIABLE_HEADERS =
		new String[] {
			WorkbookParser.DESCRIPTION, WorkbookParser.ONTOLOGY_ID, WorkbookParser.PROPERTY, WorkbookParser.SCALE, WorkbookParser.METHOD,
			WorkbookParser.DATA_TYPE, WorkbookParser.VALUE, WorkbookParser.DATASET};


	public enum Section {
		STUDY_DETAILS("STUDY DETAILS"),
		EXPERIMENTAL_DESIGN("EXPERIMENTAL DESIGN"),
		ENVIRONMENT_DETAILS("ENVIRONMENT DETAILS"),
		ENVIRONMENTAL_CONDITIONS("ENVIRONMENTAL CONDITIONS"),
		GERMPLASM_DECRIPTORS("GERMPLASM DESCRIPTORS"),
		OBSERVATION_UNIT("OBSERVATION UNIT"),
		CONSTANT("CONSTANT"),
		TRAIT("TRAITS"),
		SELECTIONS("SELECTIONS");

		private final String name;

		Section(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public WorkbookParser() {

	}

	public WorkbookParser(final int maxRowLimit) {
		this.maxRowLimit = maxRowLimit;
	}

	/**
	 * Load the Excel file and convert it to appropriate Excel workbook format (.xlsx (XSSFWorkbook) or .xls (HSSFWorkbook))
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Workbook loadFileToExcelWorkbook(final File file) throws WorkbookParserException {

		final Workbook excelWorkbook;

		try {
			excelWorkbook = this.convertExcelFileToProperFormat(file);
		} catch (final FileNotFoundException e) {
			throw new WorkbookParserException("File not found " + e.getMessage(), e);
		} catch (final IOException e) {
			throw new WorkbookParserException("Error accessing file " + e.getMessage(), e);
		}

		if (excelWorkbook instanceof XSSFWorkbook) {
			final int maxLimit = 65000;
			final boolean overLimit = PoiUtil.isAnySheetRowsOverMaxLimit(file.getAbsolutePath(), maxLimit);
			if (overLimit) {
				final WorkbookParserException workbookParserException = new WorkbookParserException("");
				workbookParserException
					.addMessage(new Message("error.file.is.too.large", new DecimalFormat("###,###,###").format(maxLimit)));
				throw workbookParserException;
			}
		}

		return excelWorkbook;
	}

	protected Workbook convertExcelFileToProperFormat(final File file) throws IOException {

		final InputStream inp = new FileInputStream(file);
		final InputStream inp2 = new FileInputStream(file);

		Workbook excelWorkbook;

		try {
			excelWorkbook = new HSSFWorkbook(inp);
		} catch (final OfficeXmlFileException ee) {
			excelWorkbook = new XSSFWorkbook(inp2);
		} finally {
			inp.close();
			inp2.close();
		}

		return excelWorkbook;
	}

	public org.generationcp.middleware.domain.etl.Workbook parseFile(final Workbook excelWorkbook, final boolean performValidation, final
	String createdBy)
		throws WorkbookParserException {
		return this.parseFile(excelWorkbook, performValidation, true, createdBy);
	}

	/**
	 * Parses given file and transforms it into a Workbook
	 *
	 * @param file
	 * @param createdBy
	 * @return workbook
	 * @throws org.generationcp.middleware.exceptions.WorkbookParserException
	 */
	public org.generationcp.middleware.domain.etl.Workbook parseFile(final Workbook excelWorkbook, final boolean performValidation,
		final boolean isReadTraits, final String createdBy) throws WorkbookParserException {

		final org.generationcp.middleware.domain.etl.Workbook workbook = new org.generationcp.middleware.domain.etl.Workbook();

		this.errorMessages = new LinkedList<>();
		this.setHasIncorrectDatatypeValue(false);
		this.validateExistenceOfSheets(excelWorkbook);

		// throw an exception here if
		if (!this.errorMessages.isEmpty() && performValidation) {
			throw new WorkbookParserException(this.errorMessages);
		}

		final List<MeasurementVariable> factors = new ArrayList<>();
		final List<MeasurementVariable> conditions = new ArrayList<>();
		final List<MeasurementVariable> constants = new ArrayList<>();
		final List<MeasurementVariable> traits = new ArrayList<>();


		// Read the study details (metadata: name, objective, start/end date etc..)
		// The first 7 rows are reserved from study details
		workbook.setStudyDetails(this.readStudyDetails(excelWorkbook, createdBy));

		// Assumes the first section Study Details (aka "Study Settings") is in row 8
		this.rowIndex = 7;
		conditions.addAll(this.readMeasurementVariables(excelWorkbook, Section.STUDY_DETAILS.getName()));
		this.incrementDescriptionSheetRowIndex(excelWorkbook); // Skip blank rows between sections
		factors.addAll(this.readMeasurementVariables(excelWorkbook, Section.EXPERIMENTAL_DESIGN.getName()));
		this.incrementDescriptionSheetRowIndex(excelWorkbook); // Skip blank rows between sections
		conditions.addAll(this.readMeasurementVariables(excelWorkbook, Section.ENVIRONMENT_DETAILS.getName()));
		this.incrementDescriptionSheetRowIndex(excelWorkbook); // Skip blank rows between sections
		constants.addAll(this.readMeasurementVariables(excelWorkbook, Section.ENVIRONMENTAL_CONDITIONS.getName()));
		this.incrementDescriptionSheetRowIndex(excelWorkbook); // Skip blank rows between sections
		factors.addAll(this.readMeasurementVariables(excelWorkbook, Section.GERMPLASM_DECRIPTORS.getName()));
		this.incrementDescriptionSheetRowIndex(excelWorkbook); // Skip blank rows between sections
		factors.addAll(this.readMeasurementVariables(excelWorkbook, Section.OBSERVATION_UNIT.getName()));

		if (isReadTraits) {
			this.incrementDescriptionSheetRowIndex(excelWorkbook); // Skip blank rows between sections
			traits.addAll(this.readMeasurementVariables(excelWorkbook, Section.TRAIT.getName()));
			this.incrementDescriptionSheetRowIndex(excelWorkbook); // Skip blank rows between sections
			traits.addAll(this.readMeasurementVariables(excelWorkbook, Section.SELECTIONS.getName()));
		}

		workbook.setFactors(factors);
		workbook.setConditions(conditions);
		workbook.setVariates(traits);
		workbook.setConstants(constants);

		if (!this.errorMessages.isEmpty() && performValidation) {
			throw new WorkbookParserException(this.errorMessages);
		}

		return workbook;
	}

	protected boolean isDescriptionSheetExists(final Workbook wb) {
		final Sheet sheet1 = wb.getSheetAt(WorkbookParser.DESCRIPTION_SHEET);
		if (sheet1 == null || sheet1.getSheetName() == null || !WorkbookParser.DESCRIPTION_SHEET_NAME.equals(sheet1.getSheetName())) {
			return false;
		}
		return true;
	}

	protected void validateExistenceOfSheets(final Workbook wb) throws WorkbookParserException {
		try {
			final Sheet sheet1 = wb.getSheetAt(WorkbookParser.DESCRIPTION_SHEET);

			if (sheet1 == null || sheet1.getSheetName() == null || !WorkbookParser.DESCRIPTION_SHEET_NAME.equals(sheet1.getSheetName())) {
				this.errorMessages.add(new Message("error.missing.sheet.description"));
			}
		} catch (final IllegalArgumentException e) {
			WorkbookParser.LOG.debug(e.getMessage(), e);
			this.errorMessages.add(new Message("error.missing.sheet.description"));
		} catch (final Exception e) {
			throw new WorkbookParserException("Error encountered with parseFile(): " + e.getMessage(), e);
		}

		try {
			final Sheet sheet2 = wb.getSheetAt(WorkbookParser.OBSERVATION_SHEET);

			if (sheet2 == null || sheet2.getSheetName() == null || !WorkbookParser.OBSERVATION_SHEET_NAME.equals(sheet2.getSheetName())) {
				this.errorMessages.add(new Message("error.missing.sheet.observation"));
			}
		} catch (final IllegalArgumentException e) {
			WorkbookParser.LOG.debug(e.getMessage(), e);
			this.errorMessages.add(new Message("error.missing.sheet.observation"));
		} catch (final Exception e) {
			throw new WorkbookParserException("Error encountered with parseFile(): " + e.getMessage(), e);
		}
	}

	public void parseAndSetObservationRows(final Workbook excelWorkbook, final org.generationcp.middleware.domain.etl.Workbook workbook,
		final boolean discardInvalidValues) throws WorkbookParserException {

		this.rowIndex = 0;
		workbook.setObservations(this.readObservations(excelWorkbook, workbook, discardInvalidValues));

	}

	public void removeObsoleteColumnsInExcelWorkbook(final org.apache.poi.ss.usermodel.Workbook excelWorkbook,
		final List<String> obsoleteVariableNames) {

		// Get the Observation sheet
		final Sheet observationSheet = excelWorkbook.getSheetAt(WorkbookParser.OBSERVATION_SHEET);

		// The first row is the header that contains column names
		final Row headerRow = observationSheet.getRow(observationSheet.getFirstRowNum());

		for (int columnIndex = 0; columnIndex <= headerRow.getLastCellNum(); columnIndex++) {
			final Cell cell = headerRow.getCell(columnIndex);
			if (cell != null) {
				final String columnName = cell.getStringCellValue();
				if (obsoleteVariableNames.contains(columnName)) {
					// Delete the column of the obsolete variable.
					PoiUtil.deleteColumn(observationSheet, columnIndex);
					// Decrement the column index since we deleted a column
					columnIndex--;
				}
			}

		}

	}

	protected StudyDetails readStudyDetails(final Workbook wb, final String createdBy) {

		// get study details
		final String study = WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.STUDY_NAME_ROW_INDEX,
			WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		final String description =
			WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.STUDY_TITLE_ROW_INDEX,
				WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		final String objective = WorkbookParser
			.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.OBJECTIVE_ROW_INDEX,
				WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		final String startDateStr = WorkbookParser
			.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.START_DATE_ROW_INDEX,
				WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		final String endDateStr = WorkbookParser
			.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.END_DATE_ROW_INDEX,
				WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		final StudyTypeDto studyTypeValue = this.determineStudyType(wb);

		// GCP-6991 and GCP-6992
		if (study == null || StringUtils.isEmpty(study)) {
			this.errorMessages.add(new Message("error.blank.study.name"));
		}

		if (description == null || StringUtils.isEmpty(description)) {
			this.errorMessages.add(new Message("error.blank.study.title"));
		}

		final Date startDate = this.validateDate(startDateStr, true, new Message("error.start.date.invalid"));
		final Date endDate = this.validateDate(endDateStr, false, new Message("error.end.date.invalid"));

		if (startDate != null && endDate != null && startDate.after(endDate)) {
			this.errorMessages.add(new Message("error.start.is.after.end.date"));
		}

		if (startDate == null && endDate != null) {
			this.errorMessages.add(new Message("error.date.startdate.required"));
		}

		final Date currentDate = Util.getCurrentDate();
		if (startDate != null && startDate.after(currentDate)) {
			this.errorMessages.add(new Message("error.start.is.after.current.date"));
		}

		// Study is not locked by default
		final StudyDetails studyDetails =
			new StudyDetails(study, description, objective, startDateStr, endDateStr, studyTypeValue, 0, null, null, Util
				.getCurrentDateAsStringValue(), createdBy, false);

		return studyDetails;
	}

	StudyTypeDto determineStudyType(final Workbook wb) {
		final String studyTypeName = WorkbookParser
			.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.STUDY_TYPE_ROW_INDEX,
				WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		return new StudyTypeDto(StringUtils.isEmpty(studyTypeName) ? StudyTypeDto.NURSERY_NAME : studyTypeName);
	}

	protected Date validateDate(final String dateString, final boolean isStartDate, final Message errorMessage) {
		final SimpleDateFormat dateFormat = Util.getSimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);
		Date date = null;
		if (dateString != null && dateString.length() != 0 && dateString.length() != 8) {
			this.errorMessages.add(errorMessage);
		} else {
			try {
				if (dateString != null && !"".equals(dateString)) {
					date = dateFormat.parse(dateString);
				}
				if (isStartDate && date == null) {
					this.errorMessages.add(new Message("error.start.date.is.empty"));
				}
			} catch (final ParseException e) {
				this.errorMessages.add(errorMessage);
			}
		}
		return date;
	}

	protected List<MeasurementVariable> readMeasurementVariables(final Workbook wb, final String name) throws WorkbookParserException {
		final List<MeasurementVariable> measurementVariables = new ArrayList<>();

		try {

			// Skip checking description sheet if it is not present in file
			if (!this.isDescriptionSheetExists(wb)) {
				return Collections.<MeasurementVariable>emptyList();
			}

			// Check if headers are correct
			final boolean valid = this.checkHeadersValid(wb, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex);

			if (!valid) {
				throw new WorkbookParserException("Incorrect headers for " + name);
			}

			// If file is still valid (after checking headers), proceed
			this.extractMeasurementVariablesForSection(wb, name, measurementVariables);

			return measurementVariables;
		} catch (final Exception e) {
			throw new WorkbookParserException(e.getMessage(), e);
		}
	}

	protected void extractMeasurementVariablesForSection(final Workbook workbook, final String sectionName,
		final List<MeasurementVariable> measurementVariables) {

		// Moving to the next line is necessary as at this point one is on the previous row.
		this.rowIndex++;

		if (WorkbookParser.rowIsEmpty(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, NUMBER_OF_COLUMNS)) {
			this.errorMessages.add(new Message("error.to.many.empty.rows", sectionName, Integer.toString(this.rowIndex - 1),
				Integer.toString(this.rowIndex)));
			return;
		}

		// capture empty sections, and return to avoid spillover
		final String value = WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, 0);

		for (final Section section : Section.values()) {
			if (value.equalsIgnoreCase(section.toString())) {
				return;
			}
		}

		while (!WorkbookParser.rowIsEmpty(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, NUMBER_OF_COLUMNS)) {

			final Integer displayRowNumber = this.rowIndex + 1;

			final MeasurementVariable measurementVariable = new MeasurementVariable();
			measurementVariable
				.setName(WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, 0));
			measurementVariable
				.setDescription(WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, 1));
			measurementVariable.setCropOntology(
				WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, 2));
			measurementVariable
				.setProperty(WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, 3));
			measurementVariable
				.setScale(WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, 4));
			measurementVariable
				.setMethod(WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, 5));
			measurementVariable
				.setDataType(WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, 6));
			measurementVariable
				.setValue(WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, 7));

			this.validateRequiredFields(measurementVariable, displayRowNumber);
			this.validateDataTypeIfNecessary(measurementVariable, displayRowNumber);
			this.assignVariableTypeAndRoleBasedOnSectionName(sectionName, measurementVariable);

			measurementVariables.add(measurementVariable);

			this.rowIndex++;
		}
	}

	protected void assignVariableTypeAndRoleBasedOnSectionName(final String sectionName, final MeasurementVariable measurementVariable) {
		if (Section.STUDY_DETAILS.getName().equals(sectionName)) {
			measurementVariable.setRole(PhenotypicType.STUDY);
			measurementVariable.setLabel(PhenotypicType.STUDY.getLabelList().get(0));
			measurementVariable.setVariableType(VariableType.STUDY_DETAIL);
		} else if (Section.EXPERIMENTAL_DESIGN.getName().equals(sectionName)) {
			measurementVariable.setRole(PhenotypicType.TRIAL_DESIGN);
			measurementVariable.setLabel(PhenotypicType.TRIAL_DESIGN.getLabelList().get(0));
			measurementVariable.setVariableType(VariableType.EXPERIMENTAL_DESIGN);
		} else if (Section.ENVIRONMENT_DETAILS.getName().equals(sectionName)) {
			measurementVariable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
			measurementVariable.setLabel(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0));
			measurementVariable.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		} else if (Section.ENVIRONMENTAL_CONDITIONS.getName().equals(sectionName)) {
			measurementVariable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
			measurementVariable.setLabel(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0));
			measurementVariable.setVariableType(VariableType.STUDY_CONDITION);
		} else if (Section.GERMPLASM_DECRIPTORS.getName().equals(sectionName)) {
			measurementVariable.setRole(PhenotypicType.GERMPLASM);
			measurementVariable.setLabel(PhenotypicType.GERMPLASM.getLabelList().get(0));
			measurementVariable.setVariableType(VariableType.GERMPLASM_DESCRIPTOR);
		} else if (Section.TRAIT.getName().equals(sectionName)) {
			measurementVariable.setRole(PhenotypicType.VARIATE);
			measurementVariable.setLabel(PhenotypicType.VARIATE.getLabelList().get(1));
			measurementVariable.setVariableType(VariableType.TRAIT);
		} else if (Section.SELECTIONS.toString().equals(sectionName)) {
			measurementVariable.setRole(PhenotypicType.VARIATE);
			measurementVariable.setLabel(PhenotypicType.VARIATE.getLabelList().get(1));
			measurementVariable.setVariableType(VariableType.SELECTION_METHOD);
		}
	}

	protected void validateRequiredFields(final MeasurementVariable measurementVariable, final Integer rowNumber) {

		if (StringUtils.isEmpty(measurementVariable.getName())) {
			this.errorMessages.add(new Message("error.missing.field.name", Integer.toString(rowNumber)));
		}

		if (StringUtils.isEmpty(measurementVariable.getDescription())) {
			this.errorMessages.add(new Message("error.missing.field.description", Integer.toString(rowNumber)));
		}

		if (StringUtils.isEmpty(measurementVariable.getProperty())) {
			this.errorMessages.add(new Message("error.missing.field.property", Integer.toString(rowNumber)));
		}

		if (StringUtils.isEmpty(measurementVariable.getScale())) {
			this.errorMessages.add(new Message("error.missing.field.scale", Integer.toString(rowNumber)));
		}

		if (StringUtils.isEmpty(measurementVariable.getMethod())) {
			this.errorMessages.add(new Message("error.missing.field.method", Integer.toString(rowNumber)));
		}

	}

	protected void validateDataTypeIfNecessary(final MeasurementVariable measurementVariable, final Integer rowNumber) {

		if (!this.hasIncorrectDatatypeValue()) {
			this.validateDataType(measurementVariable, rowNumber);
		}

	}

	protected void validateDataType(final MeasurementVariable measurementVariable, final Integer rowNumber) {

		if (StringUtils.isEmpty(measurementVariable.getDataType())) {
			this.errorMessages.add(new Message("error.missing.field.datatype", Integer.toString(rowNumber)));
			this.setHasIncorrectDatatypeValue(true);
		} else if (DataType.getByCode(measurementVariable.getDataType()) == null) {
			this.errorMessages.add(new Message("error.unsupported.datatype"));
			this.setHasIncorrectDatatypeValue(true);
		}

	}

	/**
	 * Validation to check if the Observation sheet has row content.
	 *
	 * @param excelWorkbook
	 * @throws WorkbookParserException
	 */
	protected void validateExistenceOfObservationRecords(final Workbook excelWorkbook) throws WorkbookParserException {

		final Integer lastRowNum = this.getLastRowNumber(excelWorkbook, WorkbookParser.OBSERVATION_SHEET);

		if (lastRowNum == 0) {
			final List<Message> messages = new ArrayList<>();
			final Message message = new Message("error.observation.no.records");
			messages.add(message);
			throw new WorkbookParserException(messages);
		}

	}

	/**
	 * Validation to check if the Observation sheet has exceed the maximum limit of rows that can be processed by the system.
	 *
	 * @param excelWorkbook
	 * @throws WorkbookParserException
	 */
	protected void validateMaximumLimitOfObservationRecords(final Workbook excelWorkbook) throws WorkbookParserException {

		final Integer lastRowNum = this.getLastRowNumber(excelWorkbook, WorkbookParser.OBSERVATION_SHEET);

		if (lastRowNum > this.getMaxRowLimit()) {
			final List<Message> messages = new ArrayList<>();
			final Message message =
				new Message("error.observation.over.maximum.limit", new DecimalFormat("###,###,###").format(this.getMaxRowLimit()));
			messages.add(message);
			throw new WorkbookParserException(messages);
		}

	}

	/**
	 * Get the last row number of the specified excel workbook and sheet number.
	 *
	 * @param excelWorkbook
	 * @param sheetIndex
	 * @return
	 */
	protected Integer getLastRowNumber(final Workbook excelWorkbook, final int sheetIndex) {

		final Sheet observationSheet = excelWorkbook.getSheetAt(sheetIndex);
		return PoiUtil.getLastRowNum(observationSheet);

	}

	/**
	 * Validation to check if the variables in Description sheet matched the headers in Observation sheet.
	 *
	 * @param excelWorkbook
	 * @param workbook
	 * @return
	 * @throws WorkbookParserException
	 */
	protected List<MeasurementVariable> checkIfWorkbookVariablesMatchedTheHeadersInObservation(final Workbook excelWorkbook,
		final org.generationcp.middleware.domain.etl.Workbook workbook) throws WorkbookParserException {

		final List<MeasurementVariable> variables = new ArrayList<>();

		// validate headers and set header labels
		final List<MeasurementVariable> factors = workbook.getFactors();
		final List<MeasurementVariable> variates = workbook.getVariates();

		for (int col = 0; col < factors.size() + variates.size(); col++) {
			final String columnName =
				WorkbookParser.getCellStringValue(excelWorkbook, WorkbookParser.OBSERVATION_SHEET, this.rowIndex, col);
			if (col < factors.size()) {

				if (!factors.get(col).getName().equalsIgnoreCase(columnName)) {
					throw new WorkbookParserException("Incorrect header for observations.");
				} else {
					variables.add(factors.get(col));
				}

			} else {

				if (columnName == null || !variates.get(col - factors.size()).getName().equalsIgnoreCase(columnName)) {
					throw new WorkbookParserException("Incorrect header for observations.");
				} else {
					variables.add(variates.get(col - factors.size()));
				}

			}
		}

		return variables;
	}

	/**
	 * Parse the Observation sheet rows into a list of MeasurementRow
	 *
	 * @param excelWorkbook
	 * @param workbook
	 * @param discardInvalidValues
	 * @return
	 * @throws WorkbookParserException
	 */
	protected List<MeasurementRow> readObservations(final Workbook excelWorkbook,
		final org.generationcp.middleware.domain.etl.Workbook workbook, final boolean discardInvalidValues)
		throws WorkbookParserException {
		final List<MeasurementRow> observations = new ArrayList<>();

		this.validateExistenceOfObservationRecords(excelWorkbook);
		this.validateMaximumLimitOfObservationRecords(excelWorkbook);

		final Integer lastRowNum = this.getLastRowNumber(excelWorkbook, WorkbookParser.OBSERVATION_SHEET);

		try {

			final List<MeasurementVariable> variables =
				this.checkIfWorkbookVariablesMatchedTheHeadersInObservation(excelWorkbook, workbook);

			this.rowIndex++;

			while (this.rowIndex <= lastRowNum) {

				// skip over blank rows in the observation sheet
				if (WorkbookParser
					.rowIsEmpty(excelWorkbook, WorkbookParser.OBSERVATION_SHEET, this.rowIndex, variables.size())) {
					this.rowIndex++;
					continue;
				}

				final List<MeasurementData> dataList =
					this.convertSheetRowToDataList(this.rowIndex, excelWorkbook, discardInvalidValues, variables);

				// danielv -- made use of new constructor to make it clear that only the measurement data is needed at this point. The other
				// values are computed later on in the process
				observations.add(new MeasurementRow(dataList));

				this.rowIndex++;
			}

			return observations;

		} catch (final Exception e) {
			throw new WorkbookParserException(e.getMessage(), e);
		}
	}

	/**
	 * Parse a specific row in Observation sheet into a list of MeasurementData
	 *
	 * @param rowNumber
	 * @param excelWorkbook
	 * @param discardInvalidValues
	 * @param variables
	 * @return
	 */
	protected List<MeasurementData> convertSheetRowToDataList(final int rowNumber, final Workbook excelWorkbook,
		final boolean discardInvalidValues, final List<MeasurementVariable> variables) {
		final List<MeasurementData> dataList = new ArrayList<>();

		for (int col = 0; col < variables.size(); col++) {

			final String data = WorkbookParser.getCellStringValue(excelWorkbook, WorkbookParser.OBSERVATION_SHEET, rowNumber, col);
			final MeasurementVariable measurementVariable = variables.get(col);
			final MeasurementData measurementData = new MeasurementData(measurementVariable.getName(), data);
			measurementData.setMeasurementVariable(measurementVariable);

			if (discardInvalidValues && !measurementData.isCategoricalValueValid()
				&& measurementVariable.getRole() == PhenotypicType.VARIATE) {
				measurementData.setValue("");
			}

			dataList.add(measurementData);
		}
		return dataList;
	}

	protected static String getCellStringValue(final Workbook wb, final Integer sheetNumber, final Integer rowNumber,
		final Integer columnNumber) {
		final Sheet sheet = wb.getSheetAt(sheetNumber);
		if (sheet != null) {
			final Row row = sheet.getRow(rowNumber);
			if (row != null) {
				final Cell cell = row.getCell(columnNumber);
				return PoiUtil.getCellStringValue(cell);
			}
		}
		return "";
	}

	// GCP-5815
	protected boolean checkHeadersValid(final Workbook workbook, final int sheetNumber, final int row) {

		for (int i = 0; i < EXPECTED_VARIABLE_HEADERS.length; i++) {
			// a plus is added to the column count, since the first column is the name of the group; e.g., FACTOR, CONDITION, ETC
			final String cellValue = WorkbookParser.getCellStringValue(workbook, sheetNumber, row, i + 1);
			if (StringUtils.isEmpty(cellValue) && !StringUtils.isEmpty(EXPECTED_VARIABLE_HEADERS[i])) {
				return false;
			} else if (!StringUtils.isEmpty(cellValue) && !EXPECTED_VARIABLE_HEADERS[i].equals(cellValue)) {
				return false;
			}
		}

		return true;
	}

	private void incrementDescriptionSheetRowIndex(final Workbook workbook) {
		while (WorkbookParser.rowIsEmpty(workbook, WorkbookParser.DESCRIPTION_SHEET, this.rowIndex, NUMBER_OF_COLUMNS)) {
			this.rowIndex++;
		}
	}

	private static boolean rowIsEmpty(final Workbook wb, final Integer sheet, final Integer row, final int len) {
		Integer col;
		for (col = 0; col < len; col++) {
			final String value = WorkbookParser.getCellStringValue(wb, sheet, row, col);
			if (value != null && !"".equals(value)) {
				return false;
			}
			col++;
		}
		return true;
	}

	public static String getCellStringValue(final Workbook wb, final Cell cell) {
		if (cell == null) {
			return null;
		}
		final FormulaEvaluator formulaEval = wb.getCreationHelper().createFormulaEvaluator();
		final DataFormatter formatter = new DataFormatter();
		return formatter.formatCellValue(cell, formulaEval);
	}

	public List<Message> getErrorMessages() {
		return this.errorMessages;
	}

	public void setErrorMessages(final List<Message> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public int getMaxRowLimit() {
		return this.maxRowLimit;
	}

	public void setMaxRowLimit(final int maxRowLimit) {
		this.maxRowLimit = maxRowLimit;
	}

	public boolean hasIncorrectDatatypeValue() {
		return this.hasIncorrectDatatypeValue;
	}

	public void setHasIncorrectDatatypeValue(final boolean hasIncorrectDatatypeValue) {
		this.hasIncorrectDatatypeValue = hasIncorrectDatatypeValue;
	}

}
