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

package org.generationcp.middleware.operation.parser;

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.util.PoiUtil;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkbookParser {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbookParser.class);

	public static final int DESCRIPTION_SHEET = 0;
	public static final int OBSERVATION_SHEET = 1;

	public static final int STUDY_NAME_ROW_INDEX = 0;
	public static final int STUDY_TITLE_ROW_INDEX = 1;
	public static final int PMKEY_ROW_INDEX = 2;
	public static final int OBJECTIVE_ROW_INDEX = 3;
	public static final int START_DATE_ROW_INDEX = 4;
	public static final int END_DATE_ROW_INDEX = 5;
	public static final int STUDY_TYPE_ROW_INDEX = 6;

	public static final int STUDY_DETAILS_LABEL_COLUMN_INDEX = 0;
	public static final int STUDY_DETAILS_VALUE_COLUMN_INDEX = 1;
	public static final String PMKEY_LABEL = "PMKEY";

	private static final String DESCRIPTION = "DESCRIPTION";
	private static final String PROPERTY = "PROPERTY";
	private static final String SAMPLE_LEVEL = "SAMPLE LEVEL";
	private static final String LABEL = "LABEL";
	private static final String VALUE = "VALUE";
	private static final String DATA_TYPE = "DATA TYPE";
	private static final String METHOD = "METHOD";
	private static final String SCALE = "SCALE";

	public static final int DEFAULT_MAX_ROW_LIMIT = 10000;

	private int currentRowZeroBased;

	private List<Message> errorMessages = new ArrayList<>();
	protected boolean hasIncorrectDatatypeValue = false;
	private int maxRowLimit = DEFAULT_MAX_ROW_LIMIT;

	private org.generationcp.middleware.domain.etl.Workbook currentWorkbook;
	public static final String[] DEFAULT_EXPECTED_VARIABLE_HEADERS = new String[] {WorkbookParser.DESCRIPTION, WorkbookParser.PROPERTY,
			WorkbookParser.SCALE, WorkbookParser.METHOD, WorkbookParser.DATA_TYPE, WorkbookParser.VALUE, WorkbookParser.LABEL};
	public static final String[] EXPECTED_VARIATE_HEADERS = new String[] {WorkbookParser.DESCRIPTION, WorkbookParser.PROPERTY,
			WorkbookParser.SCALE, WorkbookParser.METHOD, WorkbookParser.DATA_TYPE, "", WorkbookParser.SAMPLE_LEVEL};
	public static final String[] EXPECTED_VARIATE_HEADERS_2 = new String[] {WorkbookParser.DESCRIPTION, WorkbookParser.PROPERTY,
			WorkbookParser.SCALE, WorkbookParser.METHOD, WorkbookParser.DATA_TYPE, WorkbookParser.VALUE, WorkbookParser.SAMPLE_LEVEL};
	public static final String[] EXPECTED_CONSTANT_HEADERS = new String[] {WorkbookParser.DESCRIPTION, WorkbookParser.PROPERTY,
			WorkbookParser.SCALE, WorkbookParser.METHOD, WorkbookParser.DATA_TYPE, WorkbookParser.VALUE, WorkbookParser.SAMPLE_LEVEL};
	public static final String[] EXPECTED_CONSTANT_HEADERS_2 = new String[] {WorkbookParser.DESCRIPTION, WorkbookParser.PROPERTY,
			WorkbookParser.SCALE, WorkbookParser.METHOD, WorkbookParser.DATA_TYPE, WorkbookParser.VALUE, ""};
	public static final String[] EXPECTED_FACTOR_HEADERS = new String[] {WorkbookParser.DESCRIPTION, WorkbookParser.PROPERTY,
			WorkbookParser.SCALE, WorkbookParser.METHOD, WorkbookParser.DATA_TYPE, "NESTED IN", WorkbookParser.LABEL};
	public static final String[] EXPECTED_FACTOR_HEADERS_2 = new String[] {WorkbookParser.DESCRIPTION, WorkbookParser.PROPERTY,
			WorkbookParser.SCALE, WorkbookParser.METHOD, WorkbookParser.DATA_TYPE, "", WorkbookParser.LABEL};

	public enum Section {
		CONDITION, FACTOR, CONSTANT, VARIATE
	}

	public WorkbookParser() {

	}

	public WorkbookParser(int maxRowLimit) {
		this.maxRowLimit = maxRowLimit;
	}

	/**
	 * Load the Excel file and convert it to appropriate Excel workbook format (.xlsx (XSSFWorkbook) or .xls (HSSFWorkbook))
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Workbook loadFileToExcelWorkbook(File file) throws WorkbookParserException {

		Workbook excelWorkbook = null;

		try {
			excelWorkbook = convertExcelFileToProperFormat(file);
		} catch (FileNotFoundException e) {
			throw new WorkbookParserException("File not found " + e.getMessage(), e);
		} catch (IOException e) {
			throw new WorkbookParserException("Error accessing file " + e.getMessage(), e);
		}

		if (excelWorkbook instanceof XSSFWorkbook) {
			int maxLimit = 65000;
			Boolean overLimit = PoiUtil.isAnySheetRowsOverMaxLimit(file.getAbsolutePath(), maxLimit);
			if (overLimit) {
				WorkbookParserException workbookParserException = new WorkbookParserException("");
				workbookParserException
						.addMessage(new Message("error.file.is.too.large", new DecimalFormat("###,###,###").format(maxLimit)));
				throw workbookParserException;
			}
		}

		return excelWorkbook;
	}

	protected Workbook convertExcelFileToProperFormat(File file) throws IOException {

		InputStream inp = new FileInputStream(file);
		InputStream inp2 = new FileInputStream(file);

		Workbook excelWorkbook;

		try {
			excelWorkbook = new HSSFWorkbook(inp);
		} catch (OfficeXmlFileException ee) {
			excelWorkbook = new XSSFWorkbook(inp2);
		} finally {
			inp.close();
			inp2.close();
		}

		return excelWorkbook;
	}

	public org.generationcp.middleware.domain.etl.Workbook parseFile(Workbook excelWorkbook, boolean performValidation) throws WorkbookParserException {
		return this.parseFile(excelWorkbook, performValidation, true);
	}

	/**
	 * Parses given file and transforms it into a Workbook
	 * 
	 * @param file
	 * @return workbook
	 * @throws org.generationcp.middleware.exceptions.WorkbookParserException
	 */
	public org.generationcp.middleware.domain.etl.Workbook parseFile(Workbook excelWorkbook, boolean performValidation, boolean isReadVariate)
			throws WorkbookParserException {

		this.currentWorkbook = new org.generationcp.middleware.domain.etl.Workbook();
		this.currentRowZeroBased = 0;
		this.errorMessages = new LinkedList<Message>();
		this.setHasIncorrectDatatypeValue(false);

		// validation Descriptin and Observation Sheets
		this.validateExistenceOfSheets(excelWorkbook);

		// throw an exception here if
		if (!this.errorMessages.isEmpty() && performValidation) {
			throw new WorkbookParserException(this.errorMessages);
		}

		this.currentWorkbook.setStudyDetails(this.readStudyDetails(excelWorkbook));
		this.currentWorkbook.setConditions(this.readMeasurementVariables(excelWorkbook, Section.CONDITION.toString()));
		this.currentWorkbook.setFactors(this.readMeasurementVariables(excelWorkbook, Section.FACTOR.toString()));
		this.currentWorkbook.setConstants(this.readMeasurementVariables(excelWorkbook, Section.CONSTANT.toString()));
		if (isReadVariate) {
			this.currentWorkbook.setVariates(this.readMeasurementVariables(excelWorkbook, Section.VARIATE.toString()));
		}

		if (!this.errorMessages.isEmpty() && performValidation) {
			throw new WorkbookParserException(this.errorMessages);
		}

		return this.currentWorkbook;
	}

	protected void validateExistenceOfSheets(Workbook wb) throws WorkbookParserException {
		try {
			Sheet sheet1 = wb.getSheetAt(WorkbookParser.DESCRIPTION_SHEET);

			if (sheet1 == null || sheet1.getSheetName() == null || !"Description".equals(sheet1.getSheetName())) {
				this.errorMessages.add(new Message("error.missing.sheet.description"));
			}
		} catch (IllegalArgumentException e) {
			WorkbookParser.LOG.debug(e.getMessage(), e);
			this.errorMessages.add(new Message("error.missing.sheet.description"));
		} catch (Exception e) {
			throw new WorkbookParserException("Error encountered with parseFile(): " + e.getMessage(), e);
		}

		try {
			Sheet sheet2 = wb.getSheetAt(WorkbookParser.OBSERVATION_SHEET);

			if (sheet2 == null || sheet2.getSheetName() == null || !"Observation".equals(sheet2.getSheetName())) {
				this.errorMessages.add(new Message("error.missing.sheet.observation"));
			}
		} catch (IllegalArgumentException e) {
			WorkbookParser.LOG.debug(e.getMessage(), e);
			this.errorMessages.add(new Message("error.missing.sheet.observation"));
		} catch (Exception e) {
			throw new WorkbookParserException("Error encountered with parseFile(): " + e.getMessage(), e);
		}
	}

	public void parseAndSetObservationRows(Workbook excelWorkbook, org.generationcp.middleware.domain.etl.Workbook workbook, boolean discardInvalidValues)
			throws WorkbookParserException {

			this.currentRowZeroBased = 0;
			workbook.setObservations(this.readObservations(excelWorkbook, workbook, discardInvalidValues));

	}

	public void removeObsoleteColumnsInExcelWorkbook(final org.apache.poi.ss.usermodel.Workbook excelWorkbook, final List<String> obsoleteVariableNames) {

		// Get the Observation sheet
		Sheet observationSheet = excelWorkbook.getSheetAt(WorkbookParser.OBSERVATION_SHEET);

		// The first row is the header that contains column names
		Row headerRow = observationSheet.getRow(observationSheet.getFirstRowNum());

		for (int columnIndex = 0; columnIndex <= headerRow.getLastCellNum(); columnIndex++ ) {
			Cell cell = headerRow.getCell(columnIndex);
			if (cell != null) {
				String columnName = cell.getStringCellValue();
				if (obsoleteVariableNames.contains(columnName)) {
					// Delete the column of the obsolete variable.
					PoiUtil.deleteColumn(observationSheet, columnIndex);
					// Decrement the column index since we deleted a column
					columnIndex--;
				}
			}

		}


	}

	protected StudyDetails readStudyDetails(Workbook wb) {

		// get study details
		String study =
				WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.STUDY_NAME_ROW_INDEX,
						WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		String title =
				WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.STUDY_TITLE_ROW_INDEX,
						WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		String pmKey =
				WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.PMKEY_ROW_INDEX,
						WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		String pmKeyLabel =
				WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.PMKEY_ROW_INDEX,
						WorkbookParser.STUDY_DETAILS_LABEL_COLUMN_INDEX);
		int rowAdjustMent = 0;
		if (pmKeyLabel != null && !pmKeyLabel.trim().equals(WorkbookParser.PMKEY_LABEL)) {
			rowAdjustMent++;
		}
		String objective =
				WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.OBJECTIVE_ROW_INDEX - rowAdjustMent,
						WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		String startDateStr =
				WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET,
						WorkbookParser.START_DATE_ROW_INDEX - rowAdjustMent, WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		String endDateStr =
				WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, WorkbookParser.END_DATE_ROW_INDEX - rowAdjustMent,
						WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);

		// determine study type
		String studyType =
				WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET,
						WorkbookParser.STUDY_TYPE_ROW_INDEX - rowAdjustMent, WorkbookParser.STUDY_DETAILS_VALUE_COLUMN_INDEX);
		StudyType studyTypeValue = StudyType.getStudyType(studyType);

		// GCP-6991 and GCP-6992
		if (study == null || StringUtils.isEmpty(study)) {
			this.errorMessages.add(new Message("error.blank.study.name"));
		}

		if (title == null || StringUtils.isEmpty(title)) {
			this.errorMessages.add(new Message("error.blank.study.title"));
		}

		SimpleDateFormat dateFormat = Util.getSimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);
		Date startDate = null;
		Date endDate = null;

		if (startDateStr != null && startDateStr.length() != 0 && startDateStr.length() != 8) {
			this.errorMessages.add(new Message("error.start.date.invalid"));
		} else {
			try {
				if (startDateStr != null && !"".equals(startDateStr)) {
					startDate = dateFormat.parse(startDateStr);
				}
			} catch (ParseException e) {
				this.errorMessages.add(new Message("error.start.date.invalid"));
			}
		}
		if (endDateStr != null && endDateStr.length() != 0 && endDateStr.length() != 8) {
			this.errorMessages.add(new Message("error.end.date.invalid"));
		} else {
			try {
				if (endDateStr != null && !"".equals(endDateStr)) {
					endDate = dateFormat.parse(endDateStr);
				}
			} catch (ParseException e) {
				this.errorMessages.add(new Message("error.end.date.invalid"));
			}

		}

		if (startDate != null && endDate != null && startDate.after(endDate)) {
			this.errorMessages.add(new Message("error.start.is.after.end.date"));
		}

		if (startDate == null && endDate != null) {
			this.errorMessages.add(new Message("error.date.startdate.required"));
		}

		Date currentDate = Util.getCurrentDate();
		if (startDate != null && startDate.after(currentDate)) {
			this.errorMessages.add(new Message("error.start.is.after.current.date"));
		}

		if (studyTypeValue == null) {
			studyTypeValue = StudyType.N;
		}

		StudyDetails studyDetails =
				new StudyDetails(study, title, pmKey, objective, startDateStr, endDateStr, studyTypeValue, 0, null, null);

		while (!WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 8)) {
			this.currentRowZeroBased++;
		}
		return studyDetails;
	}

	protected List<MeasurementVariable> readMeasurementVariables(Workbook wb, String name) throws WorkbookParserException {
		List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		try {

			// Cannot have more than one empty row in the description worksheet.
			if (WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 8)) {
				this.currentRowZeroBased++;
			}

			if (WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 8)) {
				this.errorMessages.add(new Message("error.to.many.empty.rows", name, Integer.toString(this.currentRowZeroBased - 1), Integer
						.toString(this.currentRowZeroBased)));
				return Collections.<MeasurementVariable>emptyList();
			}

			// Check if headers are correct

			String[] expectedHeaders = null;
			String[] expectedHeaders2 = null;

			if (Section.FACTOR.toString().equals(name)) {
				expectedHeaders = WorkbookParser.EXPECTED_FACTOR_HEADERS;
				expectedHeaders2 = WorkbookParser.EXPECTED_FACTOR_HEADERS_2;

			} else if (Section.VARIATE.toString().equals(name)) {
				expectedHeaders = WorkbookParser.EXPECTED_VARIATE_HEADERS;
				expectedHeaders2 = WorkbookParser.EXPECTED_VARIATE_HEADERS_2;

			} else if (Section.CONSTANT.toString().equals(name)) {
				expectedHeaders = WorkbookParser.EXPECTED_CONSTANT_HEADERS;
				expectedHeaders2 = WorkbookParser.EXPECTED_CONSTANT_HEADERS_2;

			} else {
				expectedHeaders = WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS;
			}

			boolean valid = this.checkHeadersValid(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, expectedHeaders);
			if (!valid && expectedHeaders2 != null) {
				valid = this.checkHeadersValid(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, expectedHeaders2);
			}
			if (!valid && expectedHeaders != WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS) {
				valid =
						this.checkHeadersValid(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased,
								WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS);
			}

			if (!valid) {
				throw new WorkbookParserException("Incorrect headers for " + name);
			}

			// If file is still valid (after checking headers), proceed
			this.extractMeasurementVariablesForSection(wb, name, measurementVariables);

			return measurementVariables;
		} catch (Exception e) {
			throw new WorkbookParserException(e.getMessage(), e);
		}
	}

	protected void extractMeasurementVariablesForSection(Workbook workbook, String sectionName, List<MeasurementVariable> measurementVariables) {

		// Moving to the next line is necessary as at this point one is on the previous row.
		this.currentRowZeroBased++;

		// Cannot have more than one empty row in the description worksheet.
		if (WorkbookParser.rowIsEmpty(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 8)) {
			this.currentRowZeroBased++;
		}

		if (WorkbookParser.rowIsEmpty(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 8)) {
			this.errorMessages.add(new Message("error.to.many.empty.rows", sectionName, Integer.toString(this.currentRowZeroBased - 1), Integer
					.toString(this.currentRowZeroBased)));
			return;
		}

		// capture empty sections, and return to avoid spillover
		String value = WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 0);

		for (Section section : Section.values()) {
			if (value.equalsIgnoreCase(section.toString())) {
				return;
			}
		}

		while (!WorkbookParser.rowIsEmpty(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 8)) {


			final Integer displayRowNumber = this.currentRowZeroBased + 1;

			// GCP-5802
			MeasurementVariable measurementVariableFromParsedData =
					new MeasurementVariable(WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 0),
							WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 1),
							WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 3),
							WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 4),
							WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 2),
							WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 5),
							WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 6),
							WorkbookParser.getCellStringValue(workbook, WorkbookParser.DESCRIPTION_SHEET, this.currentRowZeroBased, 7));

			this.validateRequiredFields(measurementVariableFromParsedData, displayRowNumber);
			this.validateDataTypeIfNecessary(measurementVariableFromParsedData, displayRowNumber);
			this.validateRequiredFieldsForNonVariateVariables(sectionName, measurementVariableFromParsedData, displayRowNumber);
			this.validateLabelBasedOnSection(sectionName, measurementVariableFromParsedData, displayRowNumber);
			this.assignRoleBasedOnSectionName(sectionName, measurementVariableFromParsedData, displayRowNumber);
			this.assignVariableType(sectionName, measurementVariableFromParsedData, this.currentWorkbook);

			measurementVariables.add(measurementVariableFromParsedData);

			this.currentRowZeroBased++;
		}
	}

	protected void assignVariableType(final String name, final MeasurementVariable measurementVariable, final
			org.generationcp.middleware.domain.etl.Workbook workbook) {

		// NOTE: Explicitly setting variable type
		if (Section.CONSTANT.toString().equals(name) && workbook != null) {
			StudyType studyType = workbook.getStudyDetails().getStudyType();

			if (Objects.equals(studyType, StudyType.N)) {
				measurementVariable.setVariableType(VariableType.NURSERY_CONDITION);
			} else if (Objects.equals(studyType, StudyType.T)) {
				measurementVariable.setVariableType(VariableType.TRIAL_CONDITION);
			}
		} else {
			measurementVariable.setVariableType(OntologyDataHelper.mapFromPhenotype(measurementVariable.getRole(), measurementVariable.getProperty()));
		}

	}

	protected void assignRoleBasedOnSectionName(final String sectionName, final MeasurementVariable measurementVariable, final Integer rowNumber) {

		if (Section.VARIATE.toString().equals(sectionName) || Section.CONSTANT.toString().equals(sectionName)) {
			measurementVariable.setRole(PhenotypicType.VARIATE);
		} else {
			measurementVariable.setRole(PhenotypicType.getPhenotypicTypeForLabel(measurementVariable.getLabel()));
		}

	}

	protected void validateLabelBasedOnSection(final String sectionName, final MeasurementVariable measurementVariable, final Integer rowNumber) {

		if (Section.FACTOR.toString().equals(sectionName) || Section.CONDITION.toString().equals(sectionName)) {
			PhenotypicType role = PhenotypicType.getPhenotypicTypeForLabel(measurementVariable.getLabel());
			if (role == null || role == PhenotypicType.VARIATE) {
				this.errorMessages.add(new Message("error.invalid.field.label", Integer.toString(rowNumber)));
			}

		}

	}

	protected void validateRequiredFieldsForNonVariateVariables(final String sectionName, final MeasurementVariable measurementVariable, final Integer rowNumber) {

		// The Label of measurementVariable is required if it's in Factor, Condition and Constants section
		if (!Section.VARIATE.toString().equals(sectionName) && StringUtils.isEmpty(measurementVariable.getLabel())) {
			this.errorMessages.add(new Message("error.missing.field.label", Integer.toString(rowNumber)));
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

		if (!hasIncorrectDatatypeValue()) {
			validateDataType(measurementVariable, rowNumber);
		}

	}

	protected void validateDataType(final MeasurementVariable measurementVariable, final Integer rowNumber) {

		if (StringUtils.isEmpty(measurementVariable.getDataType())) {
			this.errorMessages.add(new Message("error.missing.field.datatype", Integer.toString(rowNumber)));
			setHasIncorrectDatatypeValue(true);
		} else if (DataType.getByCode(measurementVariable.getDataType()) == null) {
			this.errorMessages.add(new Message("error.unsupported.datatype"));
			setHasIncorrectDatatypeValue(true);
		}

	}

	/**
	 * Validation to check if the Observation sheet has row content.
	 * 
	 * @param excelWorkbook
	 * @throws WorkbookParserException
	 */
	protected void validateExistenceOfObservationRecords(Workbook excelWorkbook) throws WorkbookParserException {

		Integer lastRowNum = this.getLastRowNumber(excelWorkbook, WorkbookParser.OBSERVATION_SHEET);

		if (lastRowNum == 0) {
			List<Message> messages = new ArrayList<Message>();
			Message message = new Message("error.observation.no.records");
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
	protected void validateMaximumLimitOfObservationRecords(Workbook excelWorkbook) throws WorkbookParserException {

		Integer lastRowNum = this.getLastRowNumber(excelWorkbook, WorkbookParser.OBSERVATION_SHEET);

		if (lastRowNum > this.getMaxRowLimit()) {
			List<Message> messages = new ArrayList<Message>();
			Message message =
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
	protected Integer getLastRowNumber(Workbook excelWorkbook, int sheetIndex) {

		Sheet observationSheet = excelWorkbook.getSheetAt(sheetIndex);
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
	protected List<MeasurementVariable> checkIfWorkbookVariablesMatchedTheHeadersInObservation(Workbook excelWorkbook,
			org.generationcp.middleware.domain.etl.Workbook workbook) throws WorkbookParserException {

		List<MeasurementVariable> variables = new ArrayList<>();

		// validate headers and set header labels
		List<MeasurementVariable> factors = workbook.getFactors();
		List<MeasurementVariable> variates = workbook.getVariates();

		for (int col = 0; col < factors.size() + variates.size(); col++) {
			String columnName = WorkbookParser.getCellStringValue(excelWorkbook, WorkbookParser.OBSERVATION_SHEET, this.currentRowZeroBased, col);
			if (col < factors.size()) {

				if (columnName == null || !factors.get(col).getName().equalsIgnoreCase(columnName)) {
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
	protected List<MeasurementRow> readObservations(Workbook excelWorkbook, org.generationcp.middleware.domain.etl.Workbook workbook,
			boolean discardInvalidValues) throws WorkbookParserException {
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();

		this.validateExistenceOfObservationRecords(excelWorkbook);
		this.validateMaximumLimitOfObservationRecords(excelWorkbook);

		Integer lastRowNum = this.getLastRowNumber(excelWorkbook, WorkbookParser.OBSERVATION_SHEET);

		try {

			List<MeasurementVariable> variables = this.checkIfWorkbookVariablesMatchedTheHeadersInObservation(excelWorkbook, workbook);

			this.currentRowZeroBased++;

			while (this.currentRowZeroBased <= lastRowNum) {

				// skip over blank rows in the observation sheet
				if (WorkbookParser.rowIsEmpty(excelWorkbook, WorkbookParser.OBSERVATION_SHEET, this.currentRowZeroBased, variables.size())) {
					this.currentRowZeroBased++;
					continue;
				}

				List<MeasurementData> dataList =
						this.convertSheetRowToDataList(this.currentRowZeroBased, excelWorkbook, discardInvalidValues, variables);

				// danielv -- made use of new constructor to make it clear that only the measurement data is needed at this point. The other
				// values are computed later on in the process
				observations.add(new MeasurementRow(dataList));

				this.currentRowZeroBased++;
			}

			return observations;

		} catch (Exception e) {
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
	protected List<MeasurementData> convertSheetRowToDataList(int rowNumber, Workbook excelWorkbook, boolean discardInvalidValues,
			List<MeasurementVariable> variables) {
		List<MeasurementData> dataList = new ArrayList<MeasurementData>();

		for (int col = 0; col < variables.size(); col++) {

			String data = WorkbookParser.getCellStringValue(excelWorkbook, WorkbookParser.OBSERVATION_SHEET, rowNumber, col);
			MeasurementVariable measurementVariable = variables.get(col);
			MeasurementData measurementData = new MeasurementData(measurementVariable.getName(), data);
			measurementData.setMeasurementVariable(measurementVariable);

			if (discardInvalidValues && !measurementData.isCategoricalValueValid()
					&& measurementVariable.getRole() == PhenotypicType.VARIATE) {
				measurementData.setValue("");
			}

			dataList.add(measurementData);
		}
		return dataList;
	}

	protected static String getCellStringValue(Workbook wb, Integer sheetNumber, Integer rowNumber, Integer columnNumber) {
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
	protected boolean checkHeadersValid(Workbook workbook, int sheetNumber, int row, String[] expectedHeaders) {

		for (int i = 0; i < expectedHeaders.length; i++) {
			// a plus is added to the column count, since the first column is the name of the group; e.g., FACTOR, CONDITION, ETC
			String cellValue = WorkbookParser.getCellStringValue(workbook, sheetNumber, row, i + 1);
			if (StringUtils.isEmpty(cellValue) && !StringUtils.isEmpty(expectedHeaders[i])) {
				return false;
			} else if (!StringUtils.isEmpty(cellValue) && !expectedHeaders[i].equals(cellValue)) {
				return false;
			}
		}

		return true;
	}

	private static Boolean rowIsEmpty(Workbook wb, Integer sheet, Integer row, int len) {
		Integer col = 0;
		for (col = 0; col < len; col++) {
			String value = WorkbookParser.getCellStringValue(wb, sheet, row, col);
			if (value != null && !"".equals(value)) {
				return false;
			}
			col++;
		}
		return true;
	}

	public static String getCellStringValue(Workbook wb, Cell cell) {
		if (cell == null) {
			return null;
		}
		FormulaEvaluator formulaEval = wb.getCreationHelper().createFormulaEvaluator();
		DataFormatter formatter = new DataFormatter();
		return formatter.formatCellValue(cell, formulaEval);
	}

	public List<Message> getErrorMessages() {
		return this.errorMessages;
	}

	public void setErrorMessages(List<Message> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public int getMaxRowLimit() {
		return this.maxRowLimit;
	}

	public void setMaxRowLimit(int maxRowLimit) {
		this.maxRowLimit = maxRowLimit;
	}

	public boolean hasIncorrectDatatypeValue() {
		return hasIncorrectDatatypeValue;
	}

	public void setHasIncorrectDatatypeValue(boolean hasIncorrectDatatypeValue) {
		this.hasIncorrectDatatypeValue = hasIncorrectDatatypeValue;
	}

}
