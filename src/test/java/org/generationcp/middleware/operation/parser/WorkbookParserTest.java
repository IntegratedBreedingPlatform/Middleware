
package org.generationcp.middleware.operation.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.operation.parser.WorkbookParser.Section;
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

}
