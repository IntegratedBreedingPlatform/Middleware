package org.generationcp.middleware.operation.parser;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.operation.parser.WorkbookParser.Section;
import org.generationcp.middleware.util.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class WorkbookParserTest {

	private static final String ALEU_COL_1_5 = "AleuCol_1_5";
	private static final String PLOT_NO = "PLOT_NO";
	private static final String ENTRY_NO = "ENTRY_NO";
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String CREATED_BY = "1";

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Mock
	private File file;

	@InjectMocks
	private WorkbookParser workbookParser;

	public static final int TARGET_ROW_NUMBER = 100;

	@Before
	public void beforeEachTest() {
		final List<Message> errorMessages = new ArrayList<>();
		this.workbookParser.setErrorMessages(errorMessages);
		this.workbookParser.setHasIncorrectDatatypeValue(false);
	}

	@Test
	public void testValidateStartDate() {
		this.workbookParser.setErrorMessages(new ArrayList<Message>());
		final Date startDate = this.workbookParser.validateDate("20180503", true, new Message("error.start.date.invalid"));
		Assert.assertNotNull(startDate);
		assertTrue(this.workbookParser.getErrorMessages().isEmpty());
	}

	@Test
	public void testValidateStartDateInvalidFormat() {
		this.workbookParser.setErrorMessages(new ArrayList<Message>());
		final Date startDate = this.workbookParser.validateDate("fdsf", true, new Message("error.start.date.invalid"));
		Assert.assertNull(startDate);
		assertEquals(1, this.workbookParser.getErrorMessages().size());
		assertEquals("error.start.date.invalid", this.workbookParser.getErrorMessages().get(0).getMessageKey());
	}

	@Test
	public void testValidateStartDateBlank() {
		this.workbookParser.setErrorMessages(new ArrayList<Message>());
		final Date startDate = this.workbookParser.validateDate("", true, new Message("error.start.date.invalid"));
		Assert.assertNull(startDate);
		assertEquals(1, this.workbookParser.getErrorMessages().size());
		assertEquals("error.start.date.is.empty", this.workbookParser.getErrorMessages().get(0).getMessageKey());
	}

	@Test
	public void testValidateEndDate() {
		this.workbookParser.setErrorMessages(new ArrayList<Message>());
		final Date endDate = this.workbookParser.validateDate("20180503", false, new Message("error.end.date.invalid"));
		Assert.assertNotNull(endDate);
		assertTrue(this.workbookParser.getErrorMessages().isEmpty());
	}

	@Test
	public void testValidateEndDateInvalidFormat() {
		this.workbookParser.setErrorMessages(new ArrayList<Message>());
		final Date endDate = this.workbookParser.validateDate("fdsf", false, new Message("error.end.date.invalid"));
		Assert.assertNull(endDate);
		assertEquals(1, this.workbookParser.getErrorMessages().size());
		assertEquals("error.end.date.invalid", this.workbookParser.getErrorMessages().get(0).getMessageKey());
	}

	@Test
	public void testCheckHeadersValidTrue() {
		final Section section = Section.STUDY_DETAILS;
		final String sectionName = section.getName();
		final Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(sectionName, WorkbookParser.EXPECTED_VARIABLE_HEADERS);
		assertTrue(this.workbookParser.checkHeadersValid(sampleWorkbook, 0, 0));
	}

	@Test
	public void testCheckHeadersValidFalse() {
		final Section section = Section.STUDY_DETAILS;
		final String sectionName = section.getName();
		final Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(sectionName, new String[] {"Column1", "Column2", "Column3"});
		assertFalse(this.workbookParser.checkHeadersValid(sampleWorkbook, 0, 0));
	}

	@Test
	public void testExtractMeasurementVariablesForSection() {
		final Section section = Section.STUDY_DETAILS;
		final String[] headers = WorkbookParser.EXPECTED_VARIABLE_HEADERS;
		final Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(section.toString(), headers);
		this.addSectionVariableDetailsToWorkbook(sampleWorkbook, this.createVariableDetailsListTestData(section, headers));
		final List<MeasurementVariable> measurementVariables = new ArrayList<>();
		final List<Message> errorMessages = new ArrayList<>();
		this.workbookParser.setErrorMessages(errorMessages);
		this.workbookParser.extractMeasurementVariablesForSection(sampleWorkbook, section, measurementVariables);
		assertTrue("There should be no error after extracting the measurement variables", errorMessages.isEmpty());
	}

	@Test
	public void testExtractMeasurementVariablesForSectionWithEmptyVariableDetails() {
		final Section section = Section.STUDY_DETAILS;
		final String[] headers = WorkbookParser.EXPECTED_VARIABLE_HEADERS;
		final Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(section.toString(), headers);
		this.addSectionVariableDetailsToWorkbook(sampleWorkbook, this.createVariableDetailsListTestData(section, headers));

		// add row with errors for testing
		final int rowWithErrorInSheet = 6;
		final String[] invalidVariableDetails = new String[headers.length + 1];
		this.fillVariableDetails(invalidVariableDetails, "", "", "", "", "", "", "", "", "STUDY");
		this.addRowInExistingSheet(sampleWorkbook.getSheetAt(0), rowWithErrorInSheet, invalidVariableDetails);

		final List<MeasurementVariable> measurementVariables = new ArrayList<>();
		final List<Message> errorMessages = this.workbookParser.getErrorMessages();
		this.workbookParser.extractMeasurementVariablesForSection(sampleWorkbook, section, measurementVariables);

		// assertions
		assertEquals("There should be 6 errors after extracting the measurement variables", 6, errorMessages.size());
		int errorIndex = 0;
		final int rowWithError = rowWithErrorInSheet + 1;
		for (final Message message : errorMessages) {
			switch (errorIndex) {
				case 0:
					assertEquals("error.missing.field.name", message.getMessageKey());
					break;
				case 1:
					assertEquals("error.missing.field.description", message.getMessageKey());
					break;
				case 2:
					assertEquals("error.missing.field.property", message.getMessageKey());
					break;
				case 3:
					assertEquals("error.missing.field.scale", message.getMessageKey());
					break;
				case 4:
					assertEquals("error.missing.field.method", message.getMessageKey());
					break;
				case 5:
					assertEquals("error.missing.field.datatype", message.getMessageKey());
					break;
				default:
					break;
			}
			assertEquals("Error should be found in row " + rowWithError, rowWithError,
				Integer.parseInt(message.getMessageParams()[0]));
			errorIndex++;
		}
	}

	@Test
	public void testExtractMeasurementVariablesForSectionWithIncorrectDataTypeAndLabel() {
		final Section section = Section.STUDY_DETAILS;
		final String[] headers = WorkbookParser.EXPECTED_VARIABLE_HEADERS;
		final Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(section.toString(), headers);
		this.addSectionVariableDetailsToWorkbook(sampleWorkbook, this.createVariableDetailsListTestData(section, headers));
		// add row with errors for testing
		final int rowWithErrorInSheet = 6;
		final String[] invalidVariableDetails = new String[headers.length + 1];
		this.fillVariableDetails(invalidVariableDetails, "NAME " + rowWithErrorInSheet, "DESCRIPTION " + rowWithErrorInSheet,
			"ONTOLOGYID" + rowWithErrorInSheet,
			"PROPERTY" + rowWithErrorInSheet, "SCALE" + rowWithErrorInSheet, "METHOD" + rowWithErrorInSheet, "Numeric", "WITH ERROR",
			"Invalid label");
		this.addRowInExistingSheet(sampleWorkbook.getSheetAt(0), rowWithErrorInSheet, invalidVariableDetails);

		final List<MeasurementVariable> measurementVariables = new ArrayList<>();
		final List<Message> errorMessages = new ArrayList<>();
		this.workbookParser.setErrorMessages(errorMessages);
		this.workbookParser.extractMeasurementVariablesForSection(sampleWorkbook, section, measurementVariables);

		// assertions
		assertEquals("There should be 2 errors after extracting the measurement variables", 1, errorMessages.size());
		int errorIndex = 0;
		final int rowWithError = rowWithErrorInSheet + 1;
		for (final Message message : errorMessages) {
			switch (errorIndex) {
				case 0:
					assertEquals("error.unsupported.datatype", message.getMessageKey());
					break;
				case 1:
					assertEquals("Error should be found in row " + rowWithError, rowWithError,
						Integer.parseInt(message.getMessageParams()[0]));
					break;
				default:
					break;
			}
			errorIndex++;
		}
	}

	@Test
	public void testassignVariableTypeAndRoleBasedOnSectionName() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();

		this.workbookParser.assignVariableTypeAndRoleBasedOnSectionName(Section.STUDY_DETAILS, measurementVariable);
		assertEquals(PhenotypicType.STUDY, measurementVariable.getRole());
		assertEquals(PhenotypicType.STUDY.getLabelList().get(0), measurementVariable.getLabel());
		assertEquals(VariableType.STUDY_DETAIL, measurementVariable.getVariableType());

		this.workbookParser.assignVariableTypeAndRoleBasedOnSectionName(Section.EXPERIMENTAL_DESIGN, measurementVariable);
		assertEquals(PhenotypicType.TRIAL_DESIGN, measurementVariable.getRole());
		assertEquals(PhenotypicType.TRIAL_DESIGN.getLabelList().get(0), measurementVariable.getLabel());
		assertEquals(VariableType.EXPERIMENTAL_DESIGN, measurementVariable.getVariableType());

		this.workbookParser.assignVariableTypeAndRoleBasedOnSectionName(Section.ENVIRONMENT_DETAILS, measurementVariable);
		assertEquals(PhenotypicType.TRIAL_ENVIRONMENT, measurementVariable.getRole());
		assertEquals(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0), measurementVariable.getLabel());
		assertEquals(VariableType.ENVIRONMENT_DETAIL, measurementVariable.getVariableType());

		this.workbookParser.assignVariableTypeAndRoleBasedOnSectionName(Section.ENVIRONMENTAL_CONDITIONS, measurementVariable);
		assertEquals(PhenotypicType.TRIAL_ENVIRONMENT, measurementVariable.getRole());
		assertEquals(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0), measurementVariable.getLabel());
		assertEquals(VariableType.STUDY_CONDITION, measurementVariable.getVariableType());

		this.workbookParser.assignVariableTypeAndRoleBasedOnSectionName(Section.GERMPLASM_DECRIPTORS, measurementVariable);
		assertEquals(PhenotypicType.GERMPLASM, measurementVariable.getRole());
		assertEquals(PhenotypicType.GERMPLASM.getLabelList().get(0), measurementVariable.getLabel());
		assertEquals(VariableType.GERMPLASM_DESCRIPTOR, measurementVariable.getVariableType());

		this.workbookParser.assignVariableTypeAndRoleBasedOnSectionName(Section.TRAIT, measurementVariable);
		assertEquals(PhenotypicType.VARIATE, measurementVariable.getRole());
		assertEquals(PhenotypicType.VARIATE.getLabelList().get(1), measurementVariable.getLabel());
		assertEquals(VariableType.TRAIT, measurementVariable.getVariableType());

		this.workbookParser.assignVariableTypeAndRoleBasedOnSectionName(Section.SELECTIONS, measurementVariable);
		assertEquals(PhenotypicType.VARIATE, measurementVariable.getRole());
		assertEquals(PhenotypicType.VARIATE.getLabelList().get(1), measurementVariable.getLabel());
		assertEquals(VariableType.SELECTION_METHOD, measurementVariable.getVariableType());

	}

	@Test
	public void testValidateRequiredFieldsAllRequiredFieldsHaveValue() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();

		measurementVariable.setName("Test Variable Name");
		measurementVariable.setDescription("Test Description");
		measurementVariable.setProperty("Test Property");
		measurementVariable.setScale("Test Scale");
		measurementVariable.setMethod("Test Method");

		this.workbookParser.validateRequiredFields(measurementVariable, TARGET_ROW_NUMBER);

		final List<Message> messages = this.workbookParser.getErrorMessages();

		assertTrue("Expecting an empty message list since the required fields have values", messages.isEmpty());

	}

	@Test
	public void testValidateRequiredFieldsAllRequiredFieldIsBlank() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		this.workbookParser.validateRequiredFields(measurementVariable, TARGET_ROW_NUMBER);

		final List<Message> messages = this.workbookParser.getErrorMessages();

		assertEquals(5, messages.size());

		assertEquals("error.missing.field.name", messages.get(0).getMessageKey());
		assertEquals(Integer.toString(TARGET_ROW_NUMBER), messages.get(0).getMessageParams()[0]);
		assertEquals("error.missing.field.description", messages.get(1).getMessageKey());
		assertEquals(Integer.toString(TARGET_ROW_NUMBER), messages.get(1).getMessageParams()[0]);
		assertEquals("error.missing.field.property", messages.get(2).getMessageKey());
		assertEquals(Integer.toString(TARGET_ROW_NUMBER), messages.get(2).getMessageParams()[0]);
		assertEquals("error.missing.field.scale", messages.get(3).getMessageKey());
		assertEquals(Integer.toString(TARGET_ROW_NUMBER), messages.get(3).getMessageParams()[0]);
		assertEquals("error.missing.field.method", messages.get(4).getMessageKey());
		assertEquals(Integer.toString(TARGET_ROW_NUMBER), messages.get(4).getMessageParams()[0]);

	}

	@Test
	public void testValidateDataTypeIfNecessaryDatatypeIsCorrect() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataType("N");

		this.workbookParser.setHasIncorrectDatatypeValue(false);
		this.workbookParser.validateDataType(measurementVariable, TARGET_ROW_NUMBER);

		final List<Message> messages = this.workbookParser.getErrorMessages();

		// The datatype is correct so the message list should be empty
		assertTrue(messages.isEmpty());
		assertFalse(this.workbookParser.hasIncorrectDatatypeValue());

	}

	@Test
	public void testValidateDataTypeIfNecessaryIncorrectDatatypeHasNotYetDetected() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataType("A");

		this.workbookParser.setHasIncorrectDatatypeValue(false);
		this.workbookParser.validateDataType(measurementVariable, TARGET_ROW_NUMBER);

		final List<Message> messages = this.workbookParser.getErrorMessages();

		// The datatype A is not supported so the message list should not be empty
		final Message message = messages.get(0);
		Assert.assertNotNull(message);
		assertEquals("error.unsupported.datatype", message.getMessageKey());
		Assert.assertNull(message.getMessageParams());
		assertTrue(this.workbookParser.hasIncorrectDatatypeValue());

	}

	@Test
	public void testValidateDataTypeIfNecessaryIncorrectDatatypeHasBeenDetected() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataType("A");

		this.workbookParser.setHasIncorrectDatatypeValue(true);
		this.workbookParser.validateDataTypeIfNecessary(measurementVariable, TARGET_ROW_NUMBER);

		final List<Message> messages = this.workbookParser.getErrorMessages();

		// Expecting the returned error messages as empty.
		// Datatype validation logic should only be called if invalid datatype hasn't been detected.
		assertTrue(messages.isEmpty());
		assertTrue(this.workbookParser.hasIncorrectDatatypeValue());

	}

	@Test
	public void testValidateDataTypeMissingDataTypeValue() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataType("");

		this.workbookParser.validateDataType(measurementVariable, TARGET_ROW_NUMBER);

		final List<Message> messages = this.workbookParser.getErrorMessages();

		final Message message = messages.get(0);
		Assert.assertNotNull(message);
		assertEquals("error.missing.field.datatype", message.getMessageKey());
		assertEquals(Integer.toString(TARGET_ROW_NUMBER), message.getMessageParams()[0]);

	}

	@Test
	public void testValidateDataTypeInvalidDataTypeValue() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataType("A");

		this.workbookParser.validateDataType(measurementVariable, TARGET_ROW_NUMBER);

		final List<Message> messages = this.workbookParser.getErrorMessages();

		final Message message = messages.get(0);
		Assert.assertNotNull(message);
		assertEquals("error.unsupported.datatype", message.getMessageKey());
		Assert.assertNull(message.getMessageParams());

	}

	@Test
	public void testValidateExistenceOfObservationRecordsNoRecords() {

		final Workbook excelWorkbook = this.createTestExcelWorkbook(false, false);
		try {
			this.workbookParser.validateExistenceOfObservationRecords(excelWorkbook);
			Assert.fail("validateExistenceOfObservationRecords should throw an exception");
		} catch (final WorkbookParserException e) {
			assertEquals("error.observation.no.records", e.getErrorMessages().get(0).getMessageKey());
		}
	}

	@Test
	public void testValidateExistenceOfObservationRecordsWithRecords() {

		final Workbook excelWorkbook = this.createTestExcelWorkbook(true, false);
		try {
			this.workbookParser.validateExistenceOfObservationRecords(excelWorkbook);
		} catch (final WorkbookParserException e) {
			Assert.fail("The Excel workbook has records so exception should not be thrown");
		}
	}

	@Test
	public void testValidateMaximumLimitOfObservationRecordsMaximumRecordsIsReached() {

		final Workbook excelWorkbook = this.createTestExcelWorkbook(true, true);

		try {
			this.workbookParser.validateMaximumLimitOfObservationRecords(excelWorkbook);
			Assert.fail("The Excel workbook has exceed the limit of rows, exception should be thrown");
		} catch (final WorkbookParserException e) {
			assertEquals("error.observation.over.maximum.limit", e.getErrorMessages().get(0).getMessageKey());
		}
	}

	@Test
	public void testValidateMaximumLimitOfObservationRecordsLimitNotReached() {

		final Workbook excelWorkbook = this.createTestExcelWorkbook(true, false);

		try {
			this.workbookParser.validateMaximumLimitOfObservationRecords(excelWorkbook);
		} catch (final WorkbookParserException e) {
			Assert.fail("The Excel workbook has a few records, there should be no exception thrown");
		}
	}

	@Test
	public void testCheckIfWorkbookVariablesMatchedTheHeadersInObservationVariableAndHeadersMatched() {

		final org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		final Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);

		try {
			this.workbookParser.checkIfWorkbookVariablesMatchedTheHeadersInObservation(excelWorkbook, workbook);
		} catch (final WorkbookParserException e) {
			Assert.fail("The workbook variables and observation headers in Excel workbook should match");
		}
	}

	@Test
	public void testCheckIfWorkbookVariablesMatchedTheHeadersInObservationVariableAndHeadersDidNotMatch() {

		final org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		final Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);
		// Add a variate so that variables won't match with the observation's headers
		workbook.getVariates().add(new MeasurementVariable("PH", "", "", "", "", "", "", ""));

		try {
			this.workbookParser.checkIfWorkbookVariablesMatchedTheHeadersInObservation(excelWorkbook, workbook);
			Assert.fail("The workbook variables and observation headers in Excel workbook should not match, exception must be thrown ");
		} catch (final WorkbookParserException e) {
			assertEquals("Incorrect header for observations.", e.getMessage());
		}
	}

	@Test
	public void testConvertSheetRowToDataListDiscardInvalidValues() {

		final org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		final Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);
		final List<MeasurementVariable> allVariables = new LinkedList<>();
		allVariables.addAll(workbook.getFactors());
		allVariables.addAll(workbook.getVariates());

		final List<MeasurementData> result = this.workbookParser.convertSheetRowToDataList(1, excelWorkbook, true, allVariables);
		final MeasurementRow row = new MeasurementRow(result);

		assertEquals(CREATED_BY, row.getMeasurementData(TRIAL_INSTANCE).getValue());
		assertEquals(CREATED_BY, row.getMeasurementData(ENTRY_NO).getValue());
		assertEquals(CREATED_BY, row.getMeasurementData(PLOT_NO).getValue());

		// ALEU_COL_1_5's value in Excel is "6" but since it is invalid data and the user chose to discard the invalid values,
		// it should be set to empty
		assertEquals("", row.getMeasurementData(ALEU_COL_1_5).getValue());
	}

	@Test
	public void testDetermineStudyTypeWithNoSpecifiedType() {
		final Workbook wb = Mockito.mock(Workbook.class);
		final StudyTypeDto studyTypeDto = this.workbookParser.determineStudyType(wb);
		assertEquals(StudyTypeDto.NURSERY_NAME, studyTypeDto.getName());
	}

	@Test
	public void testDetermineStudyType() {
		final Workbook wb = Mockito.mock(Workbook.class);
		final Sheet sheet = Mockito.mock(Sheet.class);
		Mockito.when(wb.getSheetAt(Matchers.anyInt())).thenReturn(sheet);
		final Row row = Mockito.mock(Row.class);
		Mockito.when(sheet.getRow(Matchers.anyInt())).thenReturn(row);
		final Cell cell = Mockito.mock(Cell.class);
		Mockito.when(row.getCell(Matchers.anyInt())).thenReturn(cell);
		Mockito.when(cell.getStringCellValue()).thenReturn(StudyTypeDto.TRIAL_LABEL);
		Mockito.when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_STRING);
		final StudyTypeDto studyTypeDtoTrial = this.workbookParser.determineStudyType(wb);
		assertEquals(StudyTypeDto.TRIAL_NAME, studyTypeDtoTrial.getName());

		Mockito.when(cell.getStringCellValue()).thenReturn(StudyTypeDto.NURSERY_LABEL);
		Mockito.when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_STRING);
		final StudyTypeDto studyTypeDtoNursery = this.workbookParser.determineStudyType(wb);
		assertEquals(StudyTypeDto.NURSERY_NAME, studyTypeDtoNursery.getName());
	}



	@Test
	public void testConvertSheetRowToDataListKeepInvalidValues() {

		final org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		final Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);
		final List<MeasurementVariable> allVariables = new LinkedList<>();
		allVariables.addAll(workbook.getFactors());
		allVariables.addAll(workbook.getVariates());

		final List<MeasurementData> result = this.workbookParser.convertSheetRowToDataList(1, excelWorkbook, false, allVariables);
		final MeasurementRow row = new MeasurementRow(result);

		assertEquals(CREATED_BY, row.getMeasurementData(TRIAL_INSTANCE).getValue());
		assertEquals(CREATED_BY, row.getMeasurementData(ENTRY_NO).getValue());
		assertEquals(CREATED_BY, row.getMeasurementData(PLOT_NO).getValue());
		assertEquals("6", row.getMeasurementData(ALEU_COL_1_5).getValue());
	}

	@Test
	public void testReadObservationsWithoutInvalidValues() throws WorkbookParserException {

		final org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		final Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, false);

		final List<MeasurementRow> result = this.workbookParser.readObservations(excelWorkbook, workbook, true);

		assertEquals(1, result.size());

		assertEquals(CREATED_BY, result.get(0).getMeasurementData(TRIAL_INSTANCE).getValue());
		assertEquals(CREATED_BY, result.get(0).getMeasurementData(ENTRY_NO).getValue());
		assertEquals(CREATED_BY, result.get(0).getMeasurementData(PLOT_NO).getValue());
		assertEquals(CREATED_BY, result.get(0).getMeasurementData(ALEU_COL_1_5).getValue());

	}

	@Test
	public void testReadObservationsWithInvalidValues() throws WorkbookParserException {

		final org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		final Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);

		final List<MeasurementRow> result = this.workbookParser.readObservations(excelWorkbook, workbook, true);

		assertEquals(1, result.size());

		assertEquals(CREATED_BY, result.get(0).getMeasurementData(TRIAL_INSTANCE).getValue());
		assertEquals(CREATED_BY, result.get(0).getMeasurementData(ENTRY_NO).getValue());
		assertEquals(CREATED_BY, result.get(0).getMeasurementData(PLOT_NO).getValue());

		// ALEU_COL_1_5's value in Excel is "6" but since it is invalid data and the user chose to discard the invalid values,
		// it should be set to empty
		assertEquals("", result.get(0).getMeasurementData(ALEU_COL_1_5).getValue());

	}

	@Test
	public void testReadObservationsKeepTheInvalidValues() throws WorkbookParserException {

		final org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		final Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);

		final List<MeasurementRow> result = this.workbookParser.readObservations(excelWorkbook, workbook, false);

		assertEquals(1, result.size());

		assertEquals(CREATED_BY, result.get(0).getMeasurementData(TRIAL_INSTANCE).getValue());
		assertEquals(CREATED_BY, result.get(0).getMeasurementData(ENTRY_NO).getValue());
		assertEquals(CREATED_BY, result.get(0).getMeasurementData(PLOT_NO).getValue());
		assertEquals("6", result.get(0).getMeasurementData(ALEU_COL_1_5).getValue());

	}

	@Test
	public void testRemoveObsoleteColumnsInExcelWorkbook() {

		final Workbook excelWorkbook = new HSSFWorkbook();

		// Create description sheet
		excelWorkbook.createSheet("Description");

		// We're only interested in observation sheet
		final Sheet observationSheet = excelWorkbook.createSheet("Observation");

		// Add column names
		final Row headerRow = observationSheet.createRow(0);

		final String columnTrialInstance = "TRIAL_INSTANCE";
		final String columnPlotNo = "PLOT_NO";
		final String columnPlantHeight = "PlantHeight";
		final String columnEarPH = "EarPH";
		final String columnEarSel = "EarSel";

		final String columnData1 = "DATA1";
		final String columnData2 = "DATA2";
		final String columnData3 = "DATA3";
		final String columnData4 = "DATA4";
		final String columnData5 = "DATA5";

		headerRow.createCell(0).setCellValue(columnTrialInstance);
		headerRow.createCell(1).setCellValue(columnPlotNo);
		headerRow.createCell(2).setCellValue(columnPlantHeight);
		headerRow.createCell(3).setCellValue(columnEarPH);
		headerRow.createCell(4).setCellValue(columnEarSel);

		// Add data
		final Row dataRow = observationSheet.createRow(1);
		dataRow.createCell(0).setCellValue(columnData1);
		dataRow.createCell(1).setCellValue(columnData2);
		dataRow.createCell(2).setCellValue(columnData3);
		dataRow.createCell(3).setCellValue(columnData4);
		dataRow.createCell(4).setCellValue(columnData5);

		// Delete the Trial Instance, Plant Height and EarPH columns
		final List<String> obsoleteVariableToDelete = Arrays.asList(columnTrialInstance, columnPlantHeight, columnEarPH);

		this.workbookParser.removeObsoleteColumnsInExcelWorkbook(excelWorkbook, obsoleteVariableToDelete);

		// Verify the header row
		assertEquals("columnTrialInstance is deleted, so columnPlotNo is now on the first column", columnPlotNo,
			headerRow.getCell(0).getStringCellValue());
		;
		assertEquals("columnPlantHeight and columnEarPH are deleted, so columnEarSel is now on the second column ", columnEarSel,
			headerRow.getCell(1).getStringCellValue());
		Assert.assertNull("No cell should be on the third column", headerRow.getCell(2));
		Assert.assertNull("No cell should be on the fourth column", headerRow.getCell(3));
		Assert.assertNull("No cell should be on the fifth column", headerRow.getCell(4));

		// Verify the data row
		assertEquals("columnTrialInstance data is deleted, so columnPlotNo data is now on the first column", columnData2,
			dataRow.getCell(0).getStringCellValue());
		;
		assertEquals("columnPlantHeight and columnEarPH data are deleted, so columnEarSel data is now on the second column ",
			columnData5, dataRow.getCell(1).getStringCellValue());
		Assert.assertNull("No cell should be on the third column", dataRow.getCell(2));
		Assert.assertNull("No cell should be on the fourth column", dataRow.getCell(3));
		Assert.assertNull("No cell should be on the fifth column", dataRow.getCell(4));

	}

	@Test
	public void testReadMeasurementVariablesNoDescriptionSheet() throws Exception {

		final Workbook excelWorkbook = new HSSFWorkbook();
		excelWorkbook.createSheet("Observation");

		final WorkbookParser workbookParser = new WorkbookParser();
		final List<MeasurementVariable> readMeasurementVariables = workbookParser.readMeasurementVariables(excelWorkbook, Section.STUDY_DETAILS);
		assertTrue("Since the work book has no description sheet, measurement variables should be empty",
			readMeasurementVariables.isEmpty());
	}

	@Test
	public void isDescriptionSheetExists_WithDescriptionSheet_True() throws WorkbookParserException {
		final Workbook excelWorkbook = new HSSFWorkbook();
		excelWorkbook.createSheet("Description");
		final WorkbookParser workbookParser = new WorkbookParser();
		assertTrue(workbookParser.isDescriptionSheetExists(excelWorkbook));
	}

	@Test
	public void isDescriptionSheetExists_NoDescriptionSheet_False() throws WorkbookParserException {
		final Workbook excelWorkbook = new HSSFWorkbook();
		excelWorkbook.createSheet("Observation");
		final WorkbookParser workbookParser = new WorkbookParser();
		assertFalse(workbookParser.isDescriptionSheetExists(excelWorkbook));
	}

	private List<String[]> createVariableDetailsListTestData(final Section section, final String[] headers) {
		final List<String[]> variableDetailsList = new ArrayList<>();

		for (int i = 1; i <= 5; i++) {
			final String[] variableDetails = new String[headers.length + 1];
			final String variableName = "name " + i;
			final String description = "description " + i;
			final String ontologyId = "ontologyid " + i;
			final String property = "property" + i;
			final String scale = "scale" + i;
			final String method = "method" + i;
			final String value = "value" + i;
			final String dataset = "STUDY";
			final String dataType;
			if (i % 2 == 0) {
				dataType = "C";
			} else {
				dataType = "N";
			}
			this.fillVariableDetails(variableDetails, variableName, description, ontologyId, property, scale, method, dataType, value,
				dataset);
			variableDetailsList.add(variableDetails);
		}
		return variableDetailsList;
	}

	private void fillVariableDetails(final String[] variableDetails, final String variableName, final String description,
		final String ontologyId,
		final String property, final String scale, final String method, final String dataType, final String value, final String dataset) {
		variableDetails[0] = variableName;
		variableDetails[1] = description;
		variableDetails[2] = ontologyId;
		variableDetails[3] = property;
		variableDetails[4] = scale;
		variableDetails[5] = method;
		variableDetails[6] = dataType;
		variableDetails[7] = value;
		variableDetails[8] = dataset;
	}

	private void addSectionVariableDetailsToWorkbook(final Workbook sampleWorkbook, final List<String[]> variableDetailsList) {
		final Sheet firstSheet = sampleWorkbook.getSheetAt(0);
		int rowNumber = 1;
		for (final String[] variableDetails : variableDetailsList) {
			this.addRowInExistingSheet(firstSheet, rowNumber, variableDetails);
			rowNumber++;
		}
	}

	private void addRowInExistingSheet(final Sheet sheet, final int rowNumber, final String[] data) {
		final Row row = sheet.createRow(rowNumber);
		for (int i = 0; i < data.length; i++) {
			final Cell cell = row.createCell(i);
			cell.setCellValue(data[i]);
		}
	}

	protected Workbook createTestExcelWorkbook(final boolean withRecords, final boolean overMaximumRowLimit) {
		final HSSFWorkbook excelWorkbook = new HSSFWorkbook();
		excelWorkbook.createSheet("Description");
		final HSSFSheet observationSheet = excelWorkbook.createSheet("Observation");

		if (withRecords) {
			final int numberOfRows = overMaximumRowLimit ? WorkbookParser.DEFAULT_MAX_ROW_LIMIT + 1 : 100;
			for (int i = 0; i <= numberOfRows; i++) {

				// create header row
				if (i == 0) {
					final HSSFRow row = observationSheet.createRow(i);
					row.createCell(0).setCellValue(TRIAL_INSTANCE);
					row.createCell(1).setCellValue(ENTRY_NO);
					row.createCell(2).setCellValue(PLOT_NO);
					row.createCell(3).setCellValue("EarASP_1_5");
				} else {
					final HSSFRow row = observationSheet.createRow(i);
					row.createCell(0).setCellValue(i);
					row.createCell(1).setCellValue(i);
					row.createCell(2).setCellValue(i);
					row.createCell(3).setCellValue(i);
				}

			}
		}

		return excelWorkbook;

	}

	private Workbook createTestExcelWorkbookFromWorkbook(final org.generationcp.middleware.domain.etl.Workbook workbook,
		final boolean withInvalidValues) {

		final HSSFWorkbook excelWorkbook = new HSSFWorkbook();
		excelWorkbook.createSheet("Description");
		final HSSFSheet observationSheet = excelWorkbook.createSheet("Observation");

		final List<MeasurementVariable> allVariables = new LinkedList<>();
		allVariables.addAll(workbook.getFactors());
		allVariables.addAll(workbook.getVariates());

		final HSSFRow row1 = observationSheet.createRow(0);
		for (int i = 0; i < allVariables.size(); i++) {
			final HSSFCell cell = row1.createCell(i);
			cell.setCellValue(allVariables.get(i).getName());
		}

		final HSSFRow row2 = observationSheet.createRow(1);
		for (int i = 0; i < allVariables.size(); i++) {
			final HSSFCell cell = row2.createCell(i);

			if (Objects.equals(allVariables.get(i).getDataTypeId(), DataType.CATEGORICAL_VARIABLE.getId())) {
				cell.setCellValue(withInvalidValues ? "6" : CREATED_BY);
			} else {
				cell.setCellValue(CREATED_BY);
			}

		}

		return excelWorkbook;
	}

	private org.generationcp.middleware.domain.etl.Workbook createTestWorkbook() {
		final org.generationcp.middleware.domain.etl.Workbook workbook = new org.generationcp.middleware.domain.etl.Workbook();

		final List<MeasurementVariable> factors = new LinkedList<>();
		final List<MeasurementVariable> variates = new LinkedList<>();

		factors.add(new MeasurementVariable(TRIAL_INSTANCE, "", "", "", "", "", "", ""));
		factors.add(new MeasurementVariable(ENTRY_NO, "", "", "", "", "", "", ""));
		factors.add(new MeasurementVariable(PLOT_NO, "", "", "", "", "", "", ""));

		final MeasurementVariable categorical = new MeasurementVariable(ALEU_COL_1_5, "", "", "", "", "", "", "");
		categorical.setPossibleValues(this.createPossibleValues());
		categorical.setDataTypeId(DataType.CATEGORICAL_VARIABLE.getId());
		categorical.setRole(PhenotypicType.VARIATE);
		factors.add(categorical);

		workbook.setFactors(factors);
		workbook.setVariates(variates);

		return workbook;
	}

	private List<ValueReference> createPossibleValues() {
		final List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(new ValueReference(1, CREATED_BY, ""));
		possibleValues.add(new ValueReference(2, "2", ""));
		possibleValues.add(new ValueReference(3, "3", ""));
		possibleValues.add(new ValueReference(4, "4", ""));
		possibleValues.add(new ValueReference(5, "5", ""));
		return possibleValues;
	}

	private Workbook createWorkbookWithSectionHeaders(final String sectionName, final String[] headerArray) {
		final Workbook sampleWorkbook = new HSSFWorkbook();
		final Sheet firstSheet = sampleWorkbook.createSheet();

		final Row row = firstSheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(sectionName);

		for (int i = 0; i < headerArray.length; i++) {
			cell = row.createCell(i + 1);
			cell.setCellValue(headerArray[i]);
		}

		return sampleWorkbook;
	}

}
