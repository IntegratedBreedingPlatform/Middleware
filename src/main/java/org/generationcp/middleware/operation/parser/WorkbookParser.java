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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import org.generationcp.middleware.exceptions.WorkbookParserException;
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

	private int currentRow;
	private List<Message> errorMessages;
	private boolean hasIncorrectDatatypeValue = false;

	// GCP-5815
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

	public void parseAndSetObservationRows(File file, org.generationcp.middleware.domain.etl.Workbook workbook)
			throws WorkbookParserException {
		try {
			Workbook wb = this.getCorrectWorkbook(file);

			this.currentRow = 0;
			workbook.setObservations(this.readObservations(wb, workbook));
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

			while (WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 8)) {
				this.currentRow++;
			}
			// Check if headers are correct

			// GCP-5815
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
		do {
			this.currentRow++;
		} while (WorkbookParser.rowIsEmpty(wb, WorkbookParser.DESCRIPTION_SHEET, this.currentRow, 8));

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

			if (StringUtils.isEmpty(var.getDataType())) {
				this.errorMessages.add(new Message("error.missing.field.datatype", Integer.toString(this.currentRow + 1)));
			} else if (!this.hasIncorrectDatatypeValue && !"N".equals(var.getDataType()) && !"C".equals(var.getDataType())) {
				this.hasIncorrectDatatypeValue = true;
				this.errorMessages.add(new Message("error.unsupported.datatype"));
			}

			if (!Section.VARIATE.toString().equals(name)
					&& StringUtils.isEmpty(var.getLabel())) {
				this.errorMessages.add(new Message("error.missing.field.label",
						Integer.toString(this.currentRow + 1)));
			} else {
				var.setRole(PhenotypicType.VARIATE);
			}

			if (Section.FACTOR.toString().equals(name)
					|| Section.CONDITION.toString().equals(name)) {
				PhenotypicType role = PhenotypicType
						.getPhenotypicTypeForLabel(var.getLabel());
				if (role == null || role == PhenotypicType.VARIATE) {
					this.errorMessages.add(new Message(
							"error.invalid.field.label", Integer
									.toString(this.currentRow + 1)));
				} else {
					var.setRole(role);
				}
			}

			measurementVariables.add(var);

			this.currentRow++;
		}
	}

	private List<MeasurementRow> readObservations(Workbook wb, org.generationcp.middleware.domain.etl.Workbook workbook)
			throws WorkbookParserException {
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();

		// add each row in observations
		Sheet observationSheet = wb.getSheetAt(WorkbookParser.OBSERVATION_SHEET);
		Integer lastRowNum = PoiUtil.getLastRowNum(observationSheet);

		// GCP-7541 limit the observations rows
		Integer maxLimit = 10000;
		if (lastRowNum == 0) {
			List<Message> messages = new ArrayList<Message>();
			Message message = new Message("error.observation.no.records");
			messages.add(message);
			throw new WorkbookParserException(messages);
		} else if (lastRowNum > maxLimit) {
			List<Message> messages = new ArrayList<Message>();
			Message message = new Message("error.observation.over.maximum.limit", new DecimalFormat("###,###,###").format(maxLimit));
			messages.add(message);
			throw new WorkbookParserException(messages);
		}

		try {
			// validate headers and set header labels
			List<MeasurementVariable> factors = workbook.getFactors();
			List<MeasurementVariable> variates = workbook.getVariates();
			List<MeasurementVariable> variables = new ArrayList<MeasurementVariable>();

			for (int col = 0; col < factors.size() + variates.size(); col++) {
				String columnName = WorkbookParser.getCellStringValue(wb, WorkbookParser.OBSERVATION_SHEET, this.currentRow, col);
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

			this.currentRow++;

			while (this.currentRow <= lastRowNum) {
				// skip over blank rows in the observation sheet
				if (WorkbookParser.rowIsEmpty(wb, WorkbookParser.OBSERVATION_SHEET, this.currentRow, factors.size() + variates.size())) {
					this.currentRow++;
					continue;
				}

				List<MeasurementData> dataList = new ArrayList<MeasurementData>();

				for (int col = 0; col < factors.size() + variates.size(); col++) {
					MeasurementData data =
							new MeasurementData(variables.get(col).getName(), WorkbookParser.getCellStringValue(wb,
									WorkbookParser.OBSERVATION_SHEET, this.currentRow, col));

					data.setMeasurementVariable(variables.get(col));

					dataList.add(data);
				}

				// danielv -- made use of new constructor to make it clear that only the measurement data is needed at this point. The other
				// values are computed later on in the process
				observations.add(new MeasurementRow(dataList));
				/*
				 * observations.add(new MeasurementRow(stockId, DEFAULT_GEOLOCATION_ID, measurementData));//note that the locationid will be
				 * replaced inside
				 */
				this.currentRow++;
			}

			return observations;
		} catch (Exception e) {
			throw new WorkbookParserException(e.getMessage(), e);
		}
	}

	protected static String getCellStringValue(Workbook wb, Integer sheetNumber, Integer rowNumber, Integer columnNumber) {
		try {
			Sheet sheet = wb.getSheetAt(sheetNumber);
			Row row = sheet.getRow(rowNumber);
			Cell cell = row.getCell(columnNumber);
			return PoiUtil.getCellStringValue(cell);
		} catch (IllegalStateException e) {
			WorkbookParser.LOG.error(e.getMessage(), e);
			return "";

		} catch (NullPointerException e) {
			WorkbookParser.LOG.error(e.getMessage(), e);
			return "";
		}
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

}
