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

	public static int DEFAULT_MAX_ROW_LIMIT = 10000;

	private int currentRow;
	private List<Message> errorMessages = new ArrayList<>();
	private boolean hasIncorrectDatatypeValue = false;
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
	 * Added handling for parsing the file if its xls or xlsx
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	protected Workbook getCorrectWorkbook(File file) throws IOException, WorkbookParserException {
		InputStream inp = new FileInputStream(file);
		InputStream inp2 = new FileInputStream(file);
		Workbook wb;
		try {
			wb = new HSSFWorkbook(inp);
		} catch (OfficeXmlFileException ee) {
			WorkbookParser.LOG.debug(ee.getMessage(), ee);
			int maxLimit = 65000;
			Boolean overLimit = PoiUtil.isAnySheetRowsOverMaxLimit(file.getAbsolutePath(), maxLimit);
			if (overLimit) {
				WorkbookParserException workbookParserException = new WorkbookParserException("");
				workbookParserException
						.addMessage(new Message("error.file.is.too.large", new DecimalFormat("###,###,###").format(maxLimit)));
				throw workbookParserException;
			} else {
				wb = new XSSFWorkbook(inp2);
			}

		} finally {
			inp.close();
			inp2.close();
		}
		return wb;
	}

	public org.generationcp.middleware.domain.etl.Workbook parseFile(File file, boolean performValidation) throws WorkbookParserException {
		return this.parseFile(file, performValidation, true);
	}

	/**
	 * Parses given file and transforms it into a Workbook
	 * 
	 * @param file
	 * @return workbook
	 * @throws org.generationcp.middleware.exceptions.WorkbookParserException
	 */
	public org.generationcp.middleware.domain.etl.Workbook parseFile(File file, boolean performValidation, boolean isReadVariate)
			throws WorkbookParserException {

		this.currentWorkbook = new org.generationcp.middleware.domain.etl.Workbook();
		Workbook wb;

		this.currentRow = 0;
		this.errorMessages = new LinkedList<Message>();
		this.hasIncorrectDatatypeValue = false;

		try {

			wb = this.getCorrectWorkbook(file);

			// validation Descriptin and Observation Sheets
			this.validateExistenceOfSheets(wb);

			// throw an exception here if
			if (!this.errorMessages.isEmpty() && performValidation) {
				throw new WorkbookParserException(this.errorMessages);
			}

			this.currentWorkbook.setStudyDetails(this.readStudyDetails(wb));
			this.currentWorkbook.setConditions(this.readMeasurementVariables(wb, Section.CONDITION.toString()));
			this.currentWorkbook.setFactors(this.readMeasurementVariables(wb, Section.FACTOR.toString()));
			this.currentWorkbook.setConstants(this.readMeasurementVariables(wb, Section.CONSTANT.toString()));
			if (isReadVariate) {
				this.currentWorkbook.setVariates(this.readMeasurementVariables(wb, Section.VARIATE.toString()));
			}

			if (!this.errorMessages.isEmpty() && performValidation) {
				throw new WorkbookParserException(this.errorMessages);
			}

		} catch (FileNotFoundException e) {
			throw new WorkbookParserException("File not found " + e.getMessage(), e);
		} catch (IOException e) {
			throw new WorkbookParserException("Error accessing file " + e.getMessage(), e);
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

	public void parseAndSetObservationRows(File file, org.generationcp.middleware.domain.etl.Workbook workbook, boolean discardInvalidValues)
			throws WorkbookParserException {
		try {
			Workbook wb = this.getCorrectWorkbook(file);

			this.currentRow = 0;
			workbook.setObservations(this.readObservations(wb, workbook, discardInvalidValues));
		} catch (IOException e) {
			throw new WorkbookParserException("Error accessing file " + e.getMessage(), e);
		}
	}

	protected StudyDetails readStudyDetails(Workbook wb) throws WorkbookParserException {

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

		while (!WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 8)) {
			this.currentRow++;
		}
		return studyDetails;
	}

	protected List<MeasurementVariable> readMeasurementVariables(Workbook wb, String name) throws WorkbookParserException {
		List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		try {

			// Cannot have more than one empty row in the description worksheet.
			if (WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 8)) {
				this.currentRow++;
			}

			if (WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 8)) {
				this.errorMessages.add(new Message("error.to.many.empty.rows", name, Integer.toString(this.currentRow - 1), Integer
						.toString(this.currentRow)));
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

			boolean valid = this.checkHeadersValid(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, expectedHeaders);
			if (!valid && expectedHeaders2 != null) {
				valid = this.checkHeadersValid(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, expectedHeaders2);
			}
			if (!valid && expectedHeaders != WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS) {
				valid =
						this.checkHeadersValid(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow,
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

	protected void extractMeasurementVariablesForSection(Workbook wb, String name, List<MeasurementVariable> measurementVariables) {

		// Moving to the next line is necessary as at this point one is on the previous row.
		this.currentRow++;
		// Cannot have more than one empty row in the description worksheet.
		if (WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 8)) {
			this.currentRow++;
		}

		if (WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 8)) {
			this.errorMessages.add(new Message("error.to.many.empty.rows", name, Integer.toString(this.currentRow - 1), Integer
					.toString(this.currentRow)));
			return;
		}

		// capture empty sections, and return to avoid spillover
		String value = WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 0);

		for (Section section : Section.values()) {
			if (value.equalsIgnoreCase(section.toString())) {
				return;
			}
		}

		while (!WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 8)) {

			// GCP-5802
			MeasurementVariable var =
					new MeasurementVariable(WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 0),
							WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 1),
							WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 3),
							WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 4),
							WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 2),
							WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 5),
							WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 6),
							WorkbookParser.getCellStringValue(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 7));

			if (StringUtils.isEmpty(var.getName())) {
				this.errorMessages.add(new Message("error.missing.field.name", Integer.toString(this.currentRow + 1)));
			}

			if (StringUtils.isEmpty(var.getDescription())) {
				this.errorMessages.add(new Message("error.missing.field.description", Integer.toString(this.currentRow + 1)));
			}

			if (StringUtils.isEmpty(var.getProperty())) {
				this.errorMessages.add(new Message("error.missing.field.property", Integer.toString(this.currentRow + 1)));
			}

			if (StringUtils.isEmpty(var.getScale())) {
				this.errorMessages.add(new Message("error.missing.field.scale", Integer.toString(this.currentRow + 1)));
			}

			if (StringUtils.isEmpty(var.getMethod())) {
				this.errorMessages.add(new Message("error.missing.field.method", Integer.toString(this.currentRow + 1)));
			}

			this.validateDataType(var, this.currentRow + 1);

			// Validate variable should have label except variates
			if (!Section.VARIATE.toString().equals(name) && StringUtils.isEmpty(var.getLabel())) {
				this.errorMessages.add(new Message("error.missing.field.label", Integer.toString(this.currentRow + 1)));
			}

			if (Section.VARIATE.toString().equals(name) || Section.CONSTANT.toString().equals(name)) {
				var.setRole(PhenotypicType.VARIATE);
			} else {
				PhenotypicType role = PhenotypicType.getPhenotypicTypeForLabel(var.getLabel());
				if (role == null || role == PhenotypicType.VARIATE) {
					this.errorMessages.add(new Message("error.invalid.field.label", Integer.toString(this.currentRow + 1)));
				} else {
					var.setRole(role);
				}
			}

			// NOTE: Explicitly setting variable type
			if (Section.CONSTANT.toString().equals(name) && this.currentWorkbook != null) {
				StudyType studyType = this.currentWorkbook.getStudyDetails().getStudyType();

				if (Objects.equals(studyType, StudyType.N)) {
					var.setVariableType(VariableType.NURSERY_CONDITION);
				} else if (Objects.equals(studyType, StudyType.T)) {
					var.setVariableType(VariableType.TRIAL_CONDITION);
				}
			} else {
				var.setVariableType(OntologyDataHelper.mapFromPhenotype(var.getRole(), var.getProperty()));
			}

			measurementVariables.add(var);

			this.currentRow++;
		}
	protected void validateDataType(final MeasurementVariable measurementVariable, final Integer rowNumber) {

		if (StringUtils.isEmpty(measurementVariable.getDataType())) {
			this.errorMessages.add(new Message("error.missing.field.datatype", Integer.toString(rowNumber)));
		} else if (!this.hasIncorrectDatatypeValue && DataType.getByCode(measurementVariable.getDataType()) == null) {
			this.hasIncorrectDatatypeValue = true;
			this.errorMessages.add(new Message("error.unsupported.datatype"));
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
			String columnName = WorkbookParser.getCellStringValue(excelWorkbook, WorkbookParser.OBSERVATION_SHEET, this.currentRow, col);
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

			this.currentRow++;

			while (this.currentRow <= lastRowNum) {

				// skip over blank rows in the observation sheet
				if (WorkbookParser.rowIsEmpty(excelWorkbook, WorkbookParser.OBSERVATION_SHEET, this.currentRow, variables.size())) {
					this.currentRow++;
					continue;
				}

				List<MeasurementData> dataList =
						this.convertSheetRowToDataList(this.currentRow, excelWorkbook, discardInvalidValues, variables);

				// danielv -- made use of new constructor to make it clear that only the measurement data is needed at this point. The other
				// values are computed later on in the process
				observations.add(new MeasurementRow(dataList));

				this.currentRow++;
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

}
