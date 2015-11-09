
package org.generationcp.middleware.operation.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
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

	@Before
	public void beforeEachTest() {
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
		Workbook sampleWorkbook =
				this.createWorkbookWithSectionHeaders(section.toString(), headers);
		this.addSectionVariableDetailsToWorkbook(sampleWorkbook,
				this.createVariableDetailsListTestData(section, headers));
		List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();
		List<Message> errorMessages = new ArrayList<Message>();
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
		this.addRowOfSectionVariableDetailsToSheet(sampleWorkbook.getSheetAt(0), rowWithErrorInSheet, invalidVariableDetails);

		List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();
		List<Message> errorMessages = new ArrayList<Message>();
		this.workbookParser.setErrorMessages(errorMessages);
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
		this.fillVariableDetails(invalidVariableDetails, "NAME " + rowWithErrorInSheet, "DESCRIPTION " + rowWithErrorInSheet,
				"PROPERTY" + rowWithErrorInSheet, "SCALE" + rowWithErrorInSheet, "METHOD" + rowWithErrorInSheet, "Numeric", "WITH ERROR",
				"Invalid label");
		this.addRowOfSectionVariableDetailsToSheet(sampleWorkbook.getSheetAt(0), rowWithErrorInSheet, invalidVariableDetails);

		List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();
		List<Message> errorMessages = new ArrayList<Message>();
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
	

	/**
	 * Tests ensures that the read measurement variables validates two contiguous empty rows.
	 */
	@Test
	public void testReadMeasurementVariablesEmptyRowValidation() throws Exception {
		final Workbook mockWorkbook = Mockito.mock(Workbook.class);
		final WorkbookParser workbookParser = new WorkbookParser();
		final List<MeasurementVariable> readMeasurementVariables = workbookParser.readMeasurementVariables(mockWorkbook, "CONDITION");
		
		Assert.assertTrue("Since the work book is empty we should have an empty list of measurement variables.", readMeasurementVariables.isEmpty());
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

	private List<String[]> createVariableDetailsListTestData(Section section, String[] headers) {
		List<String[]> variableDetailsList = new ArrayList<>();
		String variableName = null;
		String description = null;
		String property = null;
		String scale = null;
		String method = null;
		String dataType = null;
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
			this.addRowOfSectionVariableDetailsToSheet(firstSheet, rowNumber, variableDetails);
			rowNumber++;
		}
	}

	private void addRowOfSectionVariableDetailsToSheet(Sheet sheet, int rowNumber, String[] variableDetails) {
		Row row = sheet.createRow(rowNumber);
		for (int i = 0; i < variableDetails.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(variableDetails[i]);
		}
	}

}
