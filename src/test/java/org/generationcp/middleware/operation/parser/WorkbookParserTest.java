
package org.generationcp.middleware.operation.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.operation.parser.WorkbookParser.Section;
import org.generationcp.middleware.util.Message;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class WorkbookParserTest {

	private static final String ALEU_COL_1_5 = "AleuCol_1_5";
	private static final String PLOT_NO = "PLOT_NO";
	private static final String ENTRY_NO = "ENTRY_NO";
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public final static String[] INCORRECT_CONDITION_HEADERS = new String[] {"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE",
			"VALUE", "PLOT"};
	public final static String[] INCORRECT_FACTOR_HEADERS = new String[] {"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE123",
			"VALUE", "LABEL"};
	public final static String[] INCORRECT_CONSTANT_HEADERS = new String[] {"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE",
			"VALUE123", "SAMPLE LEVEL"};
	public final static String[] INCORRECT_VARIATE_HEADERS = new String[] {"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE",
			"VALUE", "SAMPLE LEVEL123"};

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Mock
	private File file;

	@InjectMocks
	private WorkbookParser workbookParser;

	@Rule
	public TestName name = new TestName();
	private long startTime;
	public static final int TARGET_ROW_NUMBER = 100;

	@Before
	public void beforeEachTest() {

		List<Message> errorMessages = new ArrayList<>();
		this.workbookParser.setErrorMessages(errorMessages);
		this.workbookParser.setHasIncorrectDatatypeValue(false);

		this.startTime = System.nanoTime();
	}

	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - this.startTime;
		this.LOG.debug("+++++ Test: " + this.getClass().getSimpleName() + "." + this.name.getMethodName() + " took " + (double) elapsedTime
				/ 1000000 + " ms = " + (double) elapsedTime / 1000000000 + " s +++++");
	}

	@Test
	public void testDefaultHeadersForConditionHeaders() throws Exception {
		this.testCorrectSectionHeaders(Section.CONDITION, WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS);
	}

	@Test
	public void testOldFieldbookExportForFactorHeaders_Format1() throws Exception {
		this.testCorrectSectionHeaders(Section.FACTOR, WorkbookParser.EXPECTED_FACTOR_HEADERS);
	}

	@Test
	public void testOldFieldbookExportForFactorHeaders_Format2() throws Exception {
		this.testCorrectSectionHeaders(Section.FACTOR, WorkbookParser.EXPECTED_FACTOR_HEADERS_2);
	}

	@Test
	public void testNewFieldbookExportFactorHeaders() throws Exception {
		this.testCorrectSectionHeaders(Section.FACTOR, WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS);
	}

	@Test
	public void testNewFieldbookExportConstantHeaders() throws Exception {
		this.testCorrectSectionHeaders(Section.CONSTANT, WorkbookParser.EXPECTED_CONSTANT_HEADERS);
	}

	@Test
	public void testOldFieldbookExportConstantHeaders() throws Exception {
		this.testCorrectSectionHeaders(Section.CONSTANT, WorkbookParser.EXPECTED_CONSTANT_HEADERS_2);
	}

	@Test
	public void testDefaultHeadersForConstantHeaders() throws Exception {
		this.testCorrectSectionHeaders(Section.CONSTANT, WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS);
	}

	@Test
	public void testNewFieldbookExportVariateHeaders() throws Exception {
		this.testCorrectSectionHeaders(Section.VARIATE, WorkbookParser.EXPECTED_VARIATE_HEADERS_2);
	}

	@Test
	public void testOldFieldbookExportVariateHeaders() throws Exception {
		this.testCorrectSectionHeaders(Section.VARIATE, WorkbookParser.EXPECTED_VARIATE_HEADERS);
	}

	@Test
	public void testDefaultHeadersForVariateHeaders() throws Exception {
		this.testCorrectSectionHeaders(Section.VARIATE, WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS);
	}

	@Test
	public void testInCorrectConditionHeadersValidated() throws Exception {
		this.testIncorrectSectionHeadersValidated(Section.CONDITION, WorkbookParserTest.INCORRECT_CONDITION_HEADERS);
	}

	@Test
	public void testInCorrectFactorHeadersValidated() throws Exception {
		this.testIncorrectSectionHeadersValidated(Section.FACTOR, WorkbookParserTest.INCORRECT_FACTOR_HEADERS);
	}

	@Test
	public void testInCorrectConstantHeadersValidated() throws Exception {
		this.testIncorrectSectionHeadersValidated(Section.CONSTANT, WorkbookParserTest.INCORRECT_CONSTANT_HEADERS);
	}

	@Test
	public void testInCorrectVariateHeadersValidated() throws Exception {
		this.testIncorrectSectionHeadersValidated(Section.VARIATE, WorkbookParserTest.INCORRECT_VARIATE_HEADERS);
	}

	private void testCorrectSectionHeaders(Section section, String[] headerArray) throws IOException, WorkbookParserException {
		WorkbookParser moleWorkbookParser = Mockito.spy(this.workbookParser);

		Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(section.toString(), headerArray);

		this.setupHeaderValidationMocks(moleWorkbookParser, sampleWorkbook, section);

		moleWorkbookParser.parseFile(this.file, true);
		Mockito.verify(moleWorkbookParser).checkHeadersValid(sampleWorkbook, 0, 0, headerArray);
	}

	private void testIncorrectSectionHeadersValidated(Section section, String[] headerArray) throws IOException, WorkbookParserException {
		WorkbookParser moleWorkbookParser = Mockito.spy(this.workbookParser);

		String sectionName = section.toString();
		Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(sectionName, headerArray);

		this.setupHeaderValidationMocks(moleWorkbookParser, sampleWorkbook, section);

		try {
			moleWorkbookParser.parseFile(this.file, true);
			Assert.fail("Validation exception should have been thrown");
		} catch (WorkbookParserException e) {
			String errorMessage = "Incorrect headers for " + sectionName;
			Assert.assertTrue("Should have thrown validation exception but did not", errorMessage.equals(e.getMessage()));
		}
	}

	private void setupHeaderValidationMocks(WorkbookParser moleWorkbookParser, Workbook sampleWorkbook, Section section)
			throws IOException, WorkbookParserException {
		// mock / skip other parsing logic and validations
		Mockito.doReturn(sampleWorkbook).when(moleWorkbookParser).getCorrectWorkbook(this.file);
		Mockito.doNothing().when(moleWorkbookParser).validateExistenceOfSheets(sampleWorkbook);
		Mockito.doReturn(new StudyDetails()).when(moleWorkbookParser).readStudyDetails(sampleWorkbook);

		// only interested in specific section
		for (Section aSection : Section.values()) {
			if (!aSection.equals(section)) {
				Mockito.doReturn(new ArrayList<MeasurementVariable>()).when(moleWorkbookParser)
						.readMeasurementVariables(sampleWorkbook, aSection.toString());
			}
		}

		// when processing variate section, do not read actual measurement vars after validating headers
		Mockito.doNothing()
				.when(moleWorkbookParser)
				.extractMeasurementVariablesForSection(Matchers.any(Workbook.class), Matchers.any(String.class),
						Matchers.anyListOf(MeasurementVariable.class));
	}

	private Workbook createWorkbookWithSectionHeaders(String sectionName, String[] headerArray) {
		Workbook sampleWorkbook = new HSSFWorkbook();
		Sheet firstSheet = sampleWorkbook.createSheet();

		Row row = firstSheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(sectionName);

		for (int i = 0; i < headerArray.length; i++) {
			cell = row.createCell(i + 1);
			cell.setCellValue(headerArray[i]);
		}

		return sampleWorkbook;
	}

	@Test
	public void testExtractMeasurementVariablesForSection() {
		Section section = Section.CONDITION;
		String[] headers = WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS;
		Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(section.toString(), headers);
		this.addSectionVariableDetailsToWorkbook(sampleWorkbook, this.createVariableDetailsListTestData(section, headers));
		List<MeasurementVariable> measurementVariables = new ArrayList<>();
		List<Message> errorMessages = new ArrayList<>();
		this.workbookParser.setErrorMessages(errorMessages);
		this.workbookParser.extractMeasurementVariablesForSection(sampleWorkbook, section.toString(), measurementVariables);
		Assert.assertTrue("There should be no error after extracting the measurement variables", errorMessages.isEmpty());
	}

	/**
	 * Test to extract measurement data for constant section.
	 */
	@Test
	public void testExtractMeasurementVariablesForConstant() {
		Section section = Section.CONSTANT;
		String[] headers = WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS;
		Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(section.toString(), headers);
		this.addSectionVariableDetailsToWorkbook(sampleWorkbook, this.createVariableDetailsListTestData(section, headers));
		List<MeasurementVariable> measurementVariables = new ArrayList<>();
		List<Message> errorMessages = new ArrayList<>();
		this.workbookParser.setErrorMessages(errorMessages);
		this.workbookParser.extractMeasurementVariablesForSection(sampleWorkbook, section.toString(), measurementVariables);
		Assert.assertTrue("There should be no error after extracting the measurement variables", errorMessages.isEmpty());
	}

	@Test
	public void testExtractMeasurementVariablesForSectionWithEmptyVariableDetails() {
		Section section = Section.CONDITION;
		String[] headers = WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS;
		Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(section.toString(), headers);
		this.addSectionVariableDetailsToWorkbook(sampleWorkbook, this.createVariableDetailsListTestData(section, headers));
		// add row with errors for testing
		int rowWithErrorInSheet = 6;
		String[] invalidVariableDetails = new String[headers.length + 1];
		this.fillVariableDetails(invalidVariableDetails, "", "", "", "", "", "", "WITH ERROR", "");
		this.addRowInExistingSheet(sampleWorkbook.getSheetAt(0), rowWithErrorInSheet, invalidVariableDetails);

		List<MeasurementVariable> measurementVariables = new ArrayList<>();
		List<Message> errorMessages = this.workbookParser.getErrorMessages();
		this.workbookParser.extractMeasurementVariablesForSection(sampleWorkbook, section.toString(), measurementVariables);

		// assertions
		Assert.assertEquals("There should be 7 errors after extracting the measurement variables", 7, errorMessages.size());
		int errorIndex = 0;
		int rowWithError = rowWithErrorInSheet + 1;
		for (Message message : errorMessages) {
			switch (errorIndex) {
				case 0:
					Assert.assertEquals("error.missing.field.name", message.getMessageKey());
					break;
				case 1:
					Assert.assertEquals("error.missing.field.description", message.getMessageKey());
					break;
				case 2:
					Assert.assertEquals("error.missing.field.property", message.getMessageKey());
					break;
				case 3:
					Assert.assertEquals("error.missing.field.scale", message.getMessageKey());
					break;
				case 4:
					Assert.assertEquals("error.missing.field.method", message.getMessageKey());
					break;
				case 5:
					Assert.assertEquals("error.missing.field.datatype", message.getMessageKey());
					break;
				case 6:
					Assert.assertEquals("error.missing.field.label", message.getMessageKey());
					break;
				default:
					break;
			}
			Assert.assertEquals("Error should be found in row " + rowWithError, rowWithError,
					Integer.parseInt(message.getMessageParams()[0]));
			errorIndex++;
		}
	}

	@Test
	public void testExtractMeasurementVariablesForSectionWithIncorrectDataTypeAndLabel() {
		Section section = Section.FACTOR;
		String[] headers = WorkbookParser.DEFAULT_EXPECTED_VARIABLE_HEADERS;
		Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(section.toString(), headers);
		this.addSectionVariableDetailsToWorkbook(sampleWorkbook, this.createVariableDetailsListTestData(section, headers));
		// add row with errors for testing
		int rowWithErrorInSheet = 6;
		String[] invalidVariableDetails = new String[headers.length + 1];
		this.fillVariableDetails(invalidVariableDetails, "NAME " + rowWithErrorInSheet, "DESCRIPTION " + rowWithErrorInSheet, "PROPERTY"
				+ rowWithErrorInSheet, "SCALE" + rowWithErrorInSheet, "METHOD" + rowWithErrorInSheet, "Numeric", "WITH ERROR",
				"Invalid label");
		this.addRowInExistingSheet(sampleWorkbook.getSheetAt(0), rowWithErrorInSheet, invalidVariableDetails);

		List<MeasurementVariable> measurementVariables = new ArrayList<>();
		List<Message> errorMessages = new ArrayList<>();
		this.workbookParser.setErrorMessages(errorMessages);
		this.workbookParser.extractMeasurementVariablesForSection(sampleWorkbook, section.toString(), measurementVariables);

		// assertions
		Assert.assertEquals("There should be 2 errors after extracting the measurement variables", 2, errorMessages.size());
		int errorIndex = 0;
		int rowWithError = rowWithErrorInSheet + 1;
		for (Message message : errorMessages) {
			switch (errorIndex) {
				case 0:
					Assert.assertEquals("error.unsupported.datatype", message.getMessageKey());
					break;
				case 1:
					Assert.assertEquals("error.invalid.field.label", message.getMessageKey());
					Assert.assertEquals("Error should be found in row " + rowWithError, rowWithError,
							Integer.parseInt(message.getMessageParams()[0]));
					break;
				default:
					break;
			}
			errorIndex++;
		}
	}

	@Test
	public void testAssignVariableTypeConstantSection() {


		final org.generationcp.middleware.domain.etl.Workbook workbook = new org.generationcp.middleware.domain.etl.Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		this.workbookParser.assignVariableType(Section.CONSTANT.name(), measurementVariable, workbook);

		// If the Section is CONSTANT and the study is Trial, the variable type should be TRIAL_CONDITION
		Assert.assertEquals(VariableType.TRIAL_CONDITION, measurementVariable.getVariableType());

		studyDetails.setStudyType(StudyType.N);
		this.workbookParser.assignVariableType(Section.CONSTANT.name(), measurementVariable, workbook);

		// If the Section is CONSTANT and the study is Nursery, the variable type should be NURSERY_CONDITION
		Assert.assertEquals(VariableType.NURSERY_CONDITION, measurementVariable.getVariableType());

	}

	@Test
	public void testAssignVariableTypeSectionIsNotConstant() {


		final org.generationcp.middleware.domain.etl.Workbook workbook = new org.generationcp.middleware.domain.etl.Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable measurementVariable = new MeasurementVariable();

		// If the Section is not CONSTANT the VariableType should be derived from the variable's PhenotypicType

		measurementVariable.setRole(PhenotypicType.GERMPLASM);
		this.workbookParser.assignVariableType(Section.FACTOR.name(), measurementVariable, workbook);

		// If the variable's role is GERMPLASM, the VariableType should be GERMPLASM_DESCRIPTOR
		Assert.assertEquals(VariableType.GERMPLASM_DESCRIPTOR, measurementVariable.getVariableType());

		measurementVariable.setRole(PhenotypicType.TRIAL_DESIGN);
		this.workbookParser.assignVariableType(Section.FACTOR.name(), measurementVariable, workbook);

		// If the variable's role is TRIAL_DESIGN, the VariableType should be EXPERIMENTAL_DESIGN
		Assert.assertEquals(VariableType.EXPERIMENTAL_DESIGN, measurementVariable.getVariableType());

	}

	@Test
	public void testAssignRoleBasedOnSectionNameVariateSectionVariableIsInVariateOrConstantSection() {

		final MeasurementVariable variateMeasurementVariable = new MeasurementVariable();
		this.workbookParser.assignRoleBasedOnSectionName(Section.VARIATE.name(), variateMeasurementVariable, TARGET_ROW_NUMBER);

		// If the variable is in VARIATE section, its role (PhenotypicType) should be always be VARIATE.
		Assert.assertEquals(PhenotypicType.VARIATE, variateMeasurementVariable.getRole());

		final MeasurementVariable constantMeasurementVariable = new MeasurementVariable();
		this.workbookParser.assignRoleBasedOnSectionName(Section.CONSTANT.name(), constantMeasurementVariable, TARGET_ROW_NUMBER);

		// If the variable is in VARIATE section, its role (PhenotypicType) should be always be VARIATE.
		Assert.assertEquals(PhenotypicType.VARIATE, constantMeasurementVariable.getRole());

	}

	@Test
	public void testAssignRoleBasedOnSectionNameVariateSectionVariableIsInFactorOrConditionSection() {

		final MeasurementVariable variateMeasurementVariable = new MeasurementVariable();
		variateMeasurementVariable.setLabel(PhenotypicType.DATASET.getLabelList().get(0));
		this.workbookParser.assignRoleBasedOnSectionName(Section.FACTOR.name(), variateMeasurementVariable, TARGET_ROW_NUMBER);

		// If the variable is in FACTOR section, its role (PhenotypicType) should be based on the variable's Label.
		Assert.assertEquals(PhenotypicType.DATASET, variateMeasurementVariable.getRole());

		final MeasurementVariable constantMeasurementVariable = new MeasurementVariable();
		constantMeasurementVariable.setLabel(PhenotypicType.GERMPLASM.getLabelList().get(0));
		this.workbookParser.assignRoleBasedOnSectionName(Section.CONDITION.name(), constantMeasurementVariable, TARGET_ROW_NUMBER);

		// If the variable is in CONDITION section, its role (PhenotypicType) should be based on the variable's Label.
		Assert.assertEquals(PhenotypicType.GERMPLASM, constantMeasurementVariable.getRole());

	}



	@Test
	public void testValidateRequiredFieldsForNonVariateVariablesVariableSection() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setLabel("");

		this.workbookParser.validateRequiredFieldsForNonVariateVariables(Section.VARIATE.name(), measurementVariable, TARGET_ROW_NUMBER);
		List<Message> messages = this.workbookParser.getErrorMessages();

		// Label is not required for Variable Section, so message list should be empty
		Assert.assertTrue(messages.isEmpty());
	}

	@Test
	public void testValidateRequiredFieldsForNonVariateVariablesConditionSection() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setLabel("");

		this.workbookParser.validateRequiredFieldsForNonVariateVariables(Section.CONDITION.name(), measurementVariable, TARGET_ROW_NUMBER);
		List<Message> messages = this.workbookParser.getErrorMessages();

		// Label is required for Condition Section, so message list should not be empty
		Assert.assertFalse(messages.isEmpty());
		Assert.assertEquals("error.missing.field.label", messages.get(0).getMessageKey());

	}

	@Test
	public void testValidateRequiredFieldsForNonVariateVariablesFactorSection() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setLabel("");

		this.workbookParser.validateRequiredFieldsForNonVariateVariables(Section.FACTOR.name(), measurementVariable, TARGET_ROW_NUMBER);
		List<Message> messages = this.workbookParser.getErrorMessages();

		// Label is required for Factor Section, so message should not be empty
		Assert.assertFalse(messages.isEmpty());
		Assert.assertEquals("error.missing.field.label", messages.get(0).getMessageKey());

	}

	@Test
	public void testValidateRequiredFieldsForNonVariateVariablesConstantSection() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setLabel("");

		this.workbookParser.validateRequiredFieldsForNonVariateVariables(Section.CONSTANT.name(), measurementVariable, TARGET_ROW_NUMBER);
		List<Message> messages = this.workbookParser.getErrorMessages();

		// Label is required for Constant Section, so message should not be empty
		Assert.assertFalse(messages.isEmpty());
		Assert.assertEquals("error.missing.field.label", messages.get(0).getMessageKey());

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

		List<Message> messages = this.workbookParser.getErrorMessages();

		Assert.assertTrue("Expecting an empty message list since the required fields have values",messages.isEmpty());


	}

	@Test
	public void testValidateRequiredFieldsAllRequiredFieldIsBlank() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		this.workbookParser.validateRequiredFields(measurementVariable, TARGET_ROW_NUMBER);

		List<Message> messages = this.workbookParser.getErrorMessages();

		Assert.assertEquals(5, messages.size());

		Assert.assertEquals("error.missing.field.name", messages.get(0).getMessageKey());
		Assert.assertEquals(Integer.toString(TARGET_ROW_NUMBER), messages.get(0).getMessageParams()[0]);
		Assert.assertEquals("error.missing.field.description", messages.get(1).getMessageKey());
		Assert.assertEquals(Integer.toString(TARGET_ROW_NUMBER), messages.get(1).getMessageParams()[0]);
		Assert.assertEquals("error.missing.field.property", messages.get(2).getMessageKey());
		Assert.assertEquals(Integer.toString(TARGET_ROW_NUMBER), messages.get(2).getMessageParams()[0]);
		Assert.assertEquals("error.missing.field.scale", messages.get(3).getMessageKey());
		Assert.assertEquals(Integer.toString(TARGET_ROW_NUMBER), messages.get(3).getMessageParams()[0]);
		Assert.assertEquals("error.missing.field.method", messages.get(4).getMessageKey());
		Assert.assertEquals(Integer.toString(TARGET_ROW_NUMBER), messages.get(4).getMessageParams()[0]);

	}


	@Test
	public void testValidateDataTypeIfNecessaryDatatypeIsCorrect() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataType("N");

		this.workbookParser.setHasIncorrectDatatypeValue(false);
		this.workbookParser.validateDataType(measurementVariable, TARGET_ROW_NUMBER);

		List<Message> messages = this.workbookParser.getErrorMessages();

		// The datatype is correct so the message list should be empty
		Assert.assertTrue(messages.isEmpty());
		Assert.assertFalse(workbookParser.hasIncorrectDatatypeValue());

	}

	@Test
	public void testValidateDataTypeIfNecessaryIncorrectDatatypeHasNotYetDetected() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataType("A");

		this.workbookParser.setHasIncorrectDatatypeValue(false);
		this.workbookParser.validateDataType(measurementVariable, TARGET_ROW_NUMBER);

		List<Message> messages = this.workbookParser.getErrorMessages();

		// The datatype A is not supported so the message list should not be empty
		Message message = messages.get(0);
		Assert.assertNotNull(message);
		Assert.assertEquals("error.unsupported.datatype", message.getMessageKey());
		Assert.assertNull(message.getMessageParams());
		Assert.assertTrue(workbookParser.hasIncorrectDatatypeValue());

	}

	@Test
	public void testValidateDataTypeIfNecessaryIncorrectDatatypeHasBeenDetected() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataType("A");

		this.workbookParser.setHasIncorrectDatatypeValue(true);
		this.workbookParser.validateDataTypeIfNecessary(measurementVariable, TARGET_ROW_NUMBER);

		List<Message> messages = this.workbookParser.getErrorMessages();

		// Expecting the returned error messages as empty.
		// Datatype validation logic should only be called if invalid datatype hasn't been detected.
		Assert.assertTrue(messages.isEmpty());
		Assert.assertTrue(workbookParser.hasIncorrectDatatypeValue());

	}


	@Test
	public void testValidateDataTypeMissingDataTypeValue() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataType("");

		this.workbookParser.validateDataType(measurementVariable, TARGET_ROW_NUMBER);

		List<Message> messages = this.workbookParser.getErrorMessages();

		Message message = messages.get(0);
		Assert.assertNotNull(message);
		Assert.assertEquals("error.missing.field.datatype", message.getMessageKey());
		Assert.assertEquals(Integer.toString(TARGET_ROW_NUMBER), message.getMessageParams()[0]);

	}

	@Test
	public void testValidateDataTypeInvalidDataTypeValue() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataType("A");

		this.workbookParser.validateDataType(measurementVariable, TARGET_ROW_NUMBER);

		List<Message> messages = this.workbookParser.getErrorMessages();

		Message message = messages.get(0);
		Assert.assertNotNull(message);
		Assert.assertEquals("error.unsupported.datatype", message.getMessageKey());
		Assert.assertNull(message.getMessageParams());

	}


	/**
	 * Tests ensures that the read measurement variables validates two contiguous empty rows.
	 */
	@Test
	public void testReadMeasurementVariablesEmptyRowValidation() throws Exception {
		final Workbook mockWorkbook = Mockito.mock(Workbook.class);
		final WorkbookParser workbookParser = new WorkbookParser();
		final List<MeasurementVariable> readMeasurementVariables = workbookParser.readMeasurementVariables(mockWorkbook, "CONDITION");

		Assert.assertTrue("Since the work book is empty we should have an empty list of measurement variables.",
				readMeasurementVariables.isEmpty());
		Assert.assertEquals("We must have one error message in the parser error list", 1, workbookParser.getErrorMessages().size());

	}

	/**
	 * Tests ensures that the extract measurement variables validates two contiguous empty rows.
	 */
	@Test
	public void testExtractMeasurementVariablesEmptyRowValidation() throws Exception {
		final Workbook mockWorkbook = Mockito.mock(Workbook.class);
		final WorkbookParser workbookParser = new WorkbookParser();
		workbookParser.extractMeasurementVariablesForSection(mockWorkbook, "VARIATES", Collections.<MeasurementVariable>emptyList());

		Assert.assertEquals("We must have one error message in the parser error list", 1, workbookParser.getErrorMessages().size());

	}

	@Test
	public void testExtractVariantsWithSelectionSuccess() {
		Section section = Section.VARIATE;
		String[] headers = WorkbookParser.EXPECTED_VARIATE_HEADERS_2;
		Workbook sampleWorkbook = this.createWorkbookWithSectionHeaders(section.toString(), headers);
		// this.addSectionVariableDetailsToWorkbook(sampleWorkbook, this.createVariableDetailsListTestData(section, headers));

		int rowWithSelectionsDataInSheet = 1;
		String[] validVariatsDetails = new String[headers.length + 1];

		this.fillVariableDetails(validVariatsDetails, "NPSEL_Local", "Number of plants selected - counted (number)", "Selections",
				"Number", "New_Counted", "N", "", "PLOT");
		this.addRowInExistingSheet(sampleWorkbook.getSheetAt(0), rowWithSelectionsDataInSheet, validVariatsDetails);

		List<MeasurementVariable> measurementVariables = new ArrayList<>();
		List<Message> errorMessages = new ArrayList<>();
		this.workbookParser.setErrorMessages(errorMessages);
		this.workbookParser.extractMeasurementVariablesForSection(sampleWorkbook, section.toString(), measurementVariables);

		System.out.println("Error Messages:" + errorMessages.size());

		HSSFSheet sheet = (HSSFSheet) sampleWorkbook.getSheetAt(0);

		Assert.assertEquals(1, measurementVariables.size());

		MeasurementVariable mv = measurementVariables.get(0);

		HSSFRow row = sheet.getRow(1);

		Assert.assertEquals(mv.getName(), row.getCell(0).getStringCellValue());
		Assert.assertEquals(mv.getDescription(), row.getCell(1).getStringCellValue());
		Assert.assertEquals(mv.getProperty(), row.getCell(2).getStringCellValue());
		Assert.assertEquals(mv.getScale(), row.getCell(3).getStringCellValue());
		Assert.assertEquals(mv.getMethod(), row.getCell(4).getStringCellValue());
		Assert.assertEquals(mv.getDataType(), row.getCell(5).getStringCellValue());
		Assert.assertEquals(mv.getValue(), row.getCell(6).getStringCellValue());
		Assert.assertEquals(mv.getLabel(), row.getCell(7).getStringCellValue());

		// Assert variable type based on property name
		Assert.assertEquals(mv.getVariableType(), VariableType.SELECTION_METHOD);
	}

	@Test
	public void testValidateExistenceOfObservationRecordsNoRecords() {

		Workbook excelWorkbook = this.createTestExcelWorkbook(false, false);
		try {
			this.workbookParser.validateExistenceOfObservationRecords(excelWorkbook);
			Assert.fail("validateExistenceOfObservationRecords should throw an exception");
		} catch (WorkbookParserException e) {
			Assert.assertEquals("error.observation.no.records", e.getErrorMessages().get(0).getMessageKey());
		}
	}

	@Test
	public void testValidateExistenceOfObservationRecordsWithRecords() {

		Workbook excelWorkbook = this.createTestExcelWorkbook(true, false);
		try {
			this.workbookParser.validateExistenceOfObservationRecords(excelWorkbook);
		} catch (WorkbookParserException e) {
			Assert.fail("The Excel workbook has records so exception should not be thrown");
		}
	}

	@Test
	public void testValidateMaximumLimitOfObservationRecordsMaximumRecordsIsReached() {

		Workbook excelWorkbook = this.createTestExcelWorkbook(true, true);

		try {
			this.workbookParser.validateMaximumLimitOfObservationRecords(excelWorkbook);
			Assert.fail("The Excel workbook has exceed the limit of rows, exception should be thrown");
		} catch (WorkbookParserException e) {
			Assert.assertEquals("error.observation.over.maximum.limit", e.getErrorMessages().get(0).getMessageKey());
		}
	}

	@Test
	public void testValidateMaximumLimitOfObservationRecordsLimitNotReached() {

		Workbook excelWorkbook = this.createTestExcelWorkbook(true, false);

		try {
			this.workbookParser.validateMaximumLimitOfObservationRecords(excelWorkbook);
		} catch (WorkbookParserException e) {
			Assert.fail("The Excel workbook has a few records, there should be no exception thrown");
		}
	}

	@Test
	public void testCheckIfWorkbookVariablesMatchedTheHeadersInObservationVariableAndHeadersMatched() {

		org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);

		try {
			this.workbookParser.checkIfWorkbookVariablesMatchedTheHeadersInObservation(excelWorkbook, workbook);
		} catch (WorkbookParserException e) {
			Assert.fail("The workbook variables and observation headers in Excel workbook should match");
		}
	}

	@Test
	public void testCheckIfWorkbookVariablesMatchedTheHeadersInObservationVariableAndHeadersDidNotMatch() {

		org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);
		// Add a variate so that variables won't match with the observation's headers
		workbook.getVariates().add(new MeasurementVariable("PH", "", "", "", "", "", "", ""));

		try {
			this.workbookParser.checkIfWorkbookVariablesMatchedTheHeadersInObservation(excelWorkbook, workbook);
			Assert.fail("The workbook variables and observation headers in Excel workbook should not match, exception must be thrown ");
		} catch (WorkbookParserException e) {
			Assert.assertEquals("Incorrect header for observations.", e.getMessage());
		}
	}

	@Test
	public void testConvertSheetRowToDataListDiscardInvalidValues() {

		org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);
		List<MeasurementVariable> allVariables = new LinkedList<>();
		allVariables.addAll(workbook.getFactors());
		allVariables.addAll(workbook.getVariates());

		List<MeasurementData> result = this.workbookParser.convertSheetRowToDataList(1, excelWorkbook, true, allVariables);
		MeasurementRow row = new MeasurementRow(result);

		Assert.assertEquals("1", row.getMeasurementData(TRIAL_INSTANCE).getValue());
		Assert.assertEquals("1", row.getMeasurementData(ENTRY_NO).getValue());
		Assert.assertEquals("1", row.getMeasurementData(PLOT_NO).getValue());

		// ALEU_COL_1_5's value in Excel is "6" but since it is invalid data and the user chose to discard the invalid values,
		// it should be set to empty
		Assert.assertEquals("", row.getMeasurementData(ALEU_COL_1_5).getValue());
	}

	@Test
	public void testConvertSheetRowToDataListKeepInvalidValues() {

		org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);
		List<MeasurementVariable> allVariables = new LinkedList<>();
		allVariables.addAll(workbook.getFactors());
		allVariables.addAll(workbook.getVariates());

		List<MeasurementData> result = this.workbookParser.convertSheetRowToDataList(1, excelWorkbook, false, allVariables);
		MeasurementRow row = new MeasurementRow(result);

		Assert.assertEquals("1", row.getMeasurementData(TRIAL_INSTANCE).getValue());
		Assert.assertEquals("1", row.getMeasurementData(ENTRY_NO).getValue());
		Assert.assertEquals("1", row.getMeasurementData(PLOT_NO).getValue());
		Assert.assertEquals("6", row.getMeasurementData(ALEU_COL_1_5).getValue());
	}

	@Test
	public void testReadObservationsWithoutInvalidValues() throws WorkbookParserException {

		org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, false);

		List<MeasurementRow> result = this.workbookParser.readObservations(excelWorkbook, workbook, true);

		Assert.assertEquals(1, result.size());

		Assert.assertEquals("1", result.get(0).getMeasurementData(TRIAL_INSTANCE).getValue());
		Assert.assertEquals("1", result.get(0).getMeasurementData(ENTRY_NO).getValue());
		Assert.assertEquals("1", result.get(0).getMeasurementData(PLOT_NO).getValue());
		Assert.assertEquals("1", result.get(0).getMeasurementData(ALEU_COL_1_5).getValue());

	}

	@Test
	public void testReadObservationsWithInvalidValues() throws WorkbookParserException {

		org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);

		List<MeasurementRow> result = this.workbookParser.readObservations(excelWorkbook, workbook, true);

		Assert.assertEquals(1, result.size());

		Assert.assertEquals("1", result.get(0).getMeasurementData(TRIAL_INSTANCE).getValue());
		Assert.assertEquals("1", result.get(0).getMeasurementData(ENTRY_NO).getValue());
		Assert.assertEquals("1", result.get(0).getMeasurementData(PLOT_NO).getValue());

		// ALEU_COL_1_5's value in Excel is "6" but since it is invalid data and the user chose to discard the invalid values,
		// it should be set to empty
		Assert.assertEquals("", result.get(0).getMeasurementData(ALEU_COL_1_5).getValue());

	}

	@Test
	public void testReadObservationsKeepTheInvalidValues() throws WorkbookParserException {

		org.generationcp.middleware.domain.etl.Workbook workbook = this.createTestWorkbook();
		Workbook excelWorkbook = this.createTestExcelWorkbookFromWorkbook(workbook, true);

		List<MeasurementRow> result = this.workbookParser.readObservations(excelWorkbook, workbook, false);

		Assert.assertEquals(1, result.size());

		Assert.assertEquals("1", result.get(0).getMeasurementData(TRIAL_INSTANCE).getValue());
		Assert.assertEquals("1", result.get(0).getMeasurementData(ENTRY_NO).getValue());
		Assert.assertEquals("1", result.get(0).getMeasurementData(PLOT_NO).getValue());
		Assert.assertEquals("6", result.get(0).getMeasurementData(ALEU_COL_1_5).getValue());

	}

	private List<String[]> createVariableDetailsListTestData(Section section, String[] headers) {
		List<String[]> variableDetailsList = new ArrayList<>();
		String variableName;
		String description;
		String property;
		String scale;
		String method;
		String dataType;
		String value = null;
		String label = null;
		for (int i = 1; i <= 5; i++) {
			String[] variableDetails = new String[headers.length + 1];
			variableName = "NAME " + i;
			description = "DESCRIPTION " + i;
			property = "PROPERTY" + i;
			scale = "SCALE" + i;
			method = "METHOD" + i;
			if (i % 2 == 0) {
				dataType = "C";
			} else {
				dataType = "N";
			}
			switch (section) {
				case CONDITION:
					value = Integer.toString(i);
					if (i <= 2) {
						label = PhenotypicType.STUDY.getLabelList().get(0);
					} else {
						label = PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0);
					}
					break;
				case FACTOR:
					value = "";
					if (i <= 2) {
						label = PhenotypicType.GERMPLASM.getLabelList().get(0);
					} else {
						label = PhenotypicType.TRIAL_DESIGN.getLabelList().get(0);
					}
					break;
				case CONSTANT:
					value = Integer.toString(i);
					label = PhenotypicType.VARIATE.getLabelList().get(0);
					break;
				case VARIATE:
					value = "";
					label = PhenotypicType.VARIATE.getLabelList().get(0);
					break;
				default:
					break;
			}
			this.fillVariableDetails(variableDetails, variableName, description, property, scale, method, dataType, value, label);
			variableDetailsList.add(variableDetails);
		}
		return variableDetailsList;
	}

	private void fillVariableDetails(String[] variableDetails, String variableName, String description, String property, String scale,
			String method, String dataType, String value, String label) {
		variableDetails[0] = variableName;
		variableDetails[1] = description;
		variableDetails[2] = property;
		variableDetails[3] = scale;
		variableDetails[4] = method;
		variableDetails[5] = dataType;
		variableDetails[6] = value;
		variableDetails[7] = label;
	}

	private void addSectionVariableDetailsToWorkbook(Workbook sampleWorkbook, List<String[]> variableDetailsList) {
		Sheet firstSheet = sampleWorkbook.getSheetAt(0);
		int rowNumber = 1;
		for (String[] variableDetails : variableDetailsList) {
			this.addRowInExistingSheet(firstSheet, rowNumber, variableDetails);
			rowNumber++;
		}
	}

	private void addRowInExistingSheet(Sheet sheet, int rowNumber, String[] data) {
		Row row = sheet.createRow(rowNumber);
		for (int i = 0; i < data.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(data[i]);
		}
	}

	protected Workbook createTestExcelWorkbook(boolean withRecords, boolean overMaximumRowLimit) {
		HSSFWorkbook excelWorkbook = new HSSFWorkbook();
		excelWorkbook.createSheet("Description");
		HSSFSheet observationSheet = excelWorkbook.createSheet("Observation");

		if (withRecords) {
			int numberOfRows = overMaximumRowLimit ? WorkbookParser.DEFAULT_MAX_ROW_LIMIT + 1 : 100;
			for (int i = 0; i <= numberOfRows; i++) {

				// create header row
				if (i == 0) {
					HSSFRow row = observationSheet.createRow(i);
					row.createCell(0).setCellValue(TRIAL_INSTANCE);
					row.createCell(1).setCellValue(ENTRY_NO);
					row.createCell(2).setCellValue(PLOT_NO);
					row.createCell(3).setCellValue("EarASP_1_5");
				} else {
					HSSFRow row = observationSheet.createRow(i);
					row.createCell(0).setCellValue(i);
					row.createCell(1).setCellValue(i);
					row.createCell(2).setCellValue(i);
					row.createCell(3).setCellValue(i);
				}

			}
		}

		return excelWorkbook;

	}

	private Workbook createTestExcelWorkbookFromWorkbook(org.generationcp.middleware.domain.etl.Workbook workbook, boolean withInvalidValues) {

		HSSFWorkbook excelWorkbook = new HSSFWorkbook();
		excelWorkbook.createSheet("Description");
		HSSFSheet observationSheet = excelWorkbook.createSheet("Observation");

		List<MeasurementVariable> allVariables = new LinkedList<>();
		allVariables.addAll(workbook.getFactors());
		allVariables.addAll(workbook.getVariates());

		HSSFRow row1 = observationSheet.createRow(0);
		for (int i = 0; i < allVariables.size(); i++) {
			HSSFCell cell = row1.createCell(i);
			cell.setCellValue(allVariables.get(i).getName());
		}

		HSSFRow row2 = observationSheet.createRow(1);
		for (int i = 0; i < allVariables.size(); i++) {
			HSSFCell cell = row2.createCell(i);

			if (allVariables.get(i).getDataTypeId() == DataType.CATEGORICAL_VARIABLE.getId()) {
				cell.setCellValue(withInvalidValues ? "6" : "1");
			} else {
				cell.setCellValue("1");
			}

		}

		return excelWorkbook;
	}

	private org.generationcp.middleware.domain.etl.Workbook createTestWorkbook() {
		org.generationcp.middleware.domain.etl.Workbook workbook = new org.generationcp.middleware.domain.etl.Workbook();

		List<MeasurementVariable> factors = new LinkedList<>();
		List<MeasurementVariable> variates = new LinkedList<>();

		factors.add(new MeasurementVariable(TRIAL_INSTANCE, "", "", "", "", "", "", ""));
		factors.add(new MeasurementVariable(ENTRY_NO, "", "", "", "", "", "", ""));
		factors.add(new MeasurementVariable(PLOT_NO, "", "", "", "", "", "", ""));

		MeasurementVariable categorical = new MeasurementVariable(ALEU_COL_1_5, "", "", "", "", "", "", "");
		categorical.setPossibleValues(this.createPossibleValues());
		categorical.setDataTypeId(DataType.CATEGORICAL_VARIABLE.getId());
		categorical.setRole(PhenotypicType.VARIATE);
		factors.add(categorical);

		workbook.setFactors(factors);
		workbook.setVariates(variates);

		return workbook;
	}

	private List<ValueReference> createPossibleValues() {
		List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(new ValueReference(1, "1", ""));
		possibleValues.add(new ValueReference(2, "2", ""));
		possibleValues.add(new ValueReference(3, "3", ""));
		possibleValues.add(new ValueReference(4, "4", ""));
		possibleValues.add(new ValueReference(5, "5", ""));
		return possibleValues;
	}
}
