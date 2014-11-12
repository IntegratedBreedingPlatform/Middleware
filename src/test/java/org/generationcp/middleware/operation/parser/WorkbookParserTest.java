package org.generationcp.middleware.operation.parser;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.generationcp.middleware.operation.parser.WorkbookParser.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkbookParserTest {
	
	public final static String[] INCORRECT_CONDITION_HEADERS = new String[]{"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE", "VALUE", "PLOT"};
	public final static String[] INCORRECT_FACTOR_HEADERS = new String[]{"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE123", "VALUE", "LABEL"};
	public final static String[] INCORRECT_CONSTANT_HEADERS = new String[]{"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE", "VALUE123", "SAMPLE LEVEL"};
	public final static String[] INCORRECT_VARIATE_HEADERS = new String[]{"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE", "VALUE", "SAMPLE LEVEL123"};
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Mock
	private File file;
	
	@InjectMocks
	private WorkbookParser workbookParser;
	
	@Rule
	public TestName name = new TestName();	
	private long startTime;
	
	@Before
	public void beforeEachTest() {
		startTime = System.nanoTime();
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		LOG.debug("+++++ Test: " + getClass().getSimpleName() + "." + name.getMethodName() + " took "
				+ ((double) elapsedTime / 1000000) + " ms = "
				+ ((double) elapsedTime / 1000000000) + " s +++++");
	}

	@Test
	public void testDefaultHeadersForConditionHeaders() throws Exception {
		testCorrectSectionHeaders(Section.CONDITION, DEFAULT_EXPECTED_VARIABLE_HEADERS);
	}
	
	@Test
	public void testOldFieldbookExportForFactorHeaders_Format1() throws Exception {
		testCorrectSectionHeaders(Section.FACTOR, EXPECTED_FACTOR_HEADERS);
	}
	
	@Test
	public void testOldFieldbookExportForFactorHeaders_Format2() throws Exception {
		testCorrectSectionHeaders(Section.FACTOR, EXPECTED_FACTOR_HEADERS_2);
	}
	
	@Test
	public void testNewFieldbookExportFactorHeaders() throws Exception {
		testCorrectSectionHeaders(Section.FACTOR, DEFAULT_EXPECTED_VARIABLE_HEADERS);
	}
	
	@Test
	public void testNewFieldbookExportConstantHeaders() throws Exception {
		testCorrectSectionHeaders(Section.CONSTANT, EXPECTED_CONSTANT_HEADERS);
	}
	
	@Test
	public void testOldFieldbookExportConstantHeaders() throws Exception {
		testCorrectSectionHeaders(Section.CONSTANT, EXPECTED_CONSTANT_HEADERS_2);
	}
	
	@Test
	public void testDefaultHeadersForConstantHeaders() throws Exception {
		testCorrectSectionHeaders(Section.CONSTANT, DEFAULT_EXPECTED_VARIABLE_HEADERS);
	}
	
	@Test
	public void testNewFieldbookExportVariateHeaders() throws Exception {
		testCorrectSectionHeaders(Section.VARIATE, EXPECTED_VARIATE_HEADERS_2);
	}
	
	@Test
	public void testOldFieldbookExportVariateHeaders() throws Exception {
		testCorrectSectionHeaders(Section.VARIATE, EXPECTED_VARIATE_HEADERS);
	}
	
	@Test
	public void testDefaultHeadersForVariateHeaders() throws Exception {
		testCorrectSectionHeaders(Section.VARIATE, DEFAULT_EXPECTED_VARIABLE_HEADERS);
	}
	
	@Test
	public void testInCorrectConditionHeadersValidated() throws Exception {
		testIncorrectSectionHeadersValidated(Section.CONDITION, INCORRECT_CONDITION_HEADERS);
	}
	
	@Test
	public void testInCorrectFactorHeadersValidated() throws Exception {
		testIncorrectSectionHeadersValidated(Section.FACTOR, INCORRECT_FACTOR_HEADERS);
	}
	
	@Test
	public void testInCorrectConstantHeadersValidated() throws Exception {
		testIncorrectSectionHeadersValidated(Section.CONSTANT, INCORRECT_CONSTANT_HEADERS);
	}
	
	@Test
	public void testInCorrectVariateHeadersValidated() throws Exception {
		testIncorrectSectionHeadersValidated(Section.VARIATE, INCORRECT_VARIATE_HEADERS);
	}


	private void testCorrectSectionHeaders(Section section, String[] headerArray) throws IOException, WorkbookParserException {
		WorkbookParser moleWorkbookParser = spy(workbookParser);
		
		Workbook sampleWorkbook = createWorkbookWithSectionHeaders(section.toString(), headerArray);
		
		setupHeaderValidationMocks(moleWorkbookParser, sampleWorkbook, section);
		
		moleWorkbookParser.parseFile(file, true);
		verify(moleWorkbookParser).checkHeadersValid(sampleWorkbook, 0, 0, headerArray);
	}
	
	
	
	
	private void testIncorrectSectionHeadersValidated(Section section, String[] headerArray) throws IOException, WorkbookParserException {
		WorkbookParser moleWorkbookParser = spy(workbookParser);
		
		String sectionName = section.toString();
		Workbook sampleWorkbook = createWorkbookWithSectionHeaders(sectionName, headerArray);
		
		setupHeaderValidationMocks(moleWorkbookParser, sampleWorkbook, section);
		
		try {
			moleWorkbookParser.parseFile(file, true);
			fail("Validation exception should have been thrown");
		} catch (WorkbookParserException e) {
			String errorMessage = "Incorrect headers for " + sectionName;
			assertTrue("Should have thrown validation exception but did not", errorMessage.equals(e.getMessage()));
		}
	}

	private void setupHeaderValidationMocks(WorkbookParser moleWorkbookParser,
			Workbook sampleWorkbook, Section section) throws IOException, WorkbookParserException {
		// mock / skip other parsing logic and validations
		doReturn(sampleWorkbook).when(moleWorkbookParser).getCorrectWorkbook(file);
		doNothing().when(moleWorkbookParser).validateExistenceOfSheets(sampleWorkbook);
		doReturn(new StudyDetails()).when(moleWorkbookParser).readStudyDetails(sampleWorkbook);
		
		// only interested in specific section
		for (Section aSection : Section.values()){
			if (!aSection.equals(section)){
				doReturn(new ArrayList<MeasurementVariable>()).when(moleWorkbookParser).readMeasurementVariables(sampleWorkbook, aSection.toString());
			}
		}
		
		// when processing variate section, do not read actual measurement vars after validating headers
		doNothing().when(moleWorkbookParser).extractMeasurementVariablesForSection(any(Workbook.class), 
				any(String.class), anyListOf(MeasurementVariable.class));
	}



	private Workbook createWorkbookWithSectionHeaders(String sectionName, String[] headerArray) {
		Workbook sampleWorkbook = new HSSFWorkbook();
		Sheet firstSheet = sampleWorkbook.createSheet();
		
		Row row = firstSheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(sectionName);

		for (int i=0; i < headerArray.length; i++){		
			cell = row.createCell(i+1);
			cell.setCellValue(headerArray[i]);
		}
		
		return sampleWorkbook;
	}
	

	
}
