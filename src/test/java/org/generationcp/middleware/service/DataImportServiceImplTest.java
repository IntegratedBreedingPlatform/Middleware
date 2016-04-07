
package org.generationcp.middleware.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.util.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DataImportServiceImplTest {

	private static final String EARASP_1_5_PROPERTY = "Ear aspect";
	private static final String EARASP_1_5_METHOD = "EARASP rating";
	private static final String EARASP_1_5_SCALE = "1-5 rating scale";
	private static final String EARASP_1_5_DEFINITION = "Sample Categorical Variate";
	private static final String EARASP_1_5_NAME = "EARASP_1_5";
	private static final int EARASP_1_5_TERMID = 20314;
	public static final int INVALID_VARIABLES_COUNT = 5;
	public static final int VALID_VARIABLES_COUNT = 5;

	@Mock
	private WorkbookParser parser;

	@Mock
	private Workbook workbook;

	@Mock
	private OntologyDataManager ontologyDataManager;

	@Mock
	private File file;

	@InjectMocks
	private DataImportServiceImpl dataImportService = new DataImportServiceImpl();

	public static final String[] STRINGS_WITH_INVALID_CHARACTERS = new String[] {"1234", "word@", "_+world=", "!!world!!", "&&&"};
	public static final String[] STRINGS_WITH_VALID_CHARACTERS = new String[] {"i_am_groot", "hello123world", "%%bangbang",
			"something_something", "zawaruldoisbig"};

	private static final String PROGRAM_UUID = "123456789";
	private static final String ENTRY = "ENTRY";
	private static final String NUMBER = "NUMBER";
	private static final String ENUMERATED = "ENUMERATED";
	private static final String GERMPLASM_ENTRY = "GERMPLASM_ENTRY";
	private static final String NUMERIC = "NUMERIC";
	private static final String STUDY = "STUDY";
	private static final String NESTED_NUMBER = "NESTED NUMBER";
	private static final String FIELD_PLOT = "FIELD PLOT";
	private static final String NUMERIC_VALUE = "NUMERIC VALUE";
	private static final String PLOT = "PLOT";
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	private static final String TRIAL = "TRIAL";

	@Before
	public void init() {

		Mockito.when(
				this.ontologyDataManager.getStandardVariableIdByPropertyScaleMethod(EARASP_1_5_PROPERTY, EARASP_1_5_SCALE,
						EARASP_1_5_METHOD)).thenReturn(EARASP_1_5_TERMID);

		Mockito.when(this.ontologyDataManager.getStandardVariable(EARASP_1_5_TERMID, PROGRAM_UUID)).thenReturn(
				this.createTestCategoricalStandardVariable(EARASP_1_5_NAME));

		Mockito.when(
				this.ontologyDataManager.findStandardVariableByTraitScaleMethodNames(EARASP_1_5_PROPERTY, EARASP_1_5_SCALE,
						EARASP_1_5_METHOD, PROGRAM_UUID)).thenReturn(this.createTestCategoricalStandardVariable(EARASP_1_5_NAME));

	}

	@Test
	public void testStrictParseWorkbookWithGreaterThan32VarNames() throws Exception {

		Workbook testWorkbook = this.createTestWorkbook();
		testWorkbook.getFactors().addAll(this.initializeTestMeasurementVariables());

		Mockito.when(this.ontologyDataManager.getStandardVariableIdByPropertyScaleMethod(GERMPLASM_ENTRY, NUMBER, ENUMERATED)).thenReturn(
				TermId.ENTRY_NO.getId());
		Mockito.when(this.ontologyDataManager.getStandardVariableIdByPropertyScaleMethod(FIELD_PLOT, NESTED_NUMBER, ENUMERATED))
				.thenReturn(TermId.PLOT_NO.getId());
		Mockito.when(this.ontologyDataManager.getStandardVariableIdByPropertyScaleMethod(TRIAL_INSTANCE, NUMBER, ENUMERATED)).thenReturn(
				TermId.TRIAL_INSTANCE_FACTOR.getId());

		try {
			this.dataImportService.strictParseWorkbook(this.file, this.parser, testWorkbook, this.ontologyDataManager,
					DataImportServiceImplTest.PROGRAM_UUID);
			Assert.fail("We expect workbookParserException to be thrown");
		} catch (WorkbookParserException e) {

			final String[] errorTypes =
					{DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_LENGTH, DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_CHARACTERS};
			for (Message error : e.getErrorMessages()) {
				Assert.assertTrue(
						"All errors should contain either ERROR_INVALID_VARIABLE_NAME_CHARACTERS or ERROR_INVALID_VARIABLE_NAME_LENGTH",
						Arrays.asList(errorTypes).contains(error.getMessageKey()));
			}
		}
	}

	@Test
	public void testValidateMeasurementVariableNameLengths() throws Exception {
		List<MeasurementVariable> measurementVariables = this.initializeTestMeasurementVariables();

		List<Message> messages = this.dataImportService.validateMeasurmentVariableNameLengths(measurementVariables);

		Assert.assertEquals("we should only have 5 variables with > 32 char length", DataImportServiceImplTest.INVALID_VARIABLES_COUNT,
				messages.size());

		for (Message message : messages) {
			Assert.assertTrue("returned messages should only contain the variables with names > 32",
					message.getMessageParams()[0].length() > 32);
		}
	}

	@Test
	public void testValidateMeasurementVariableNameLengthsAllShortNames() throws Exception {
		List<MeasurementVariable> measurementVariables = this.getShortNamedMeasurementVariables();

		List<Message> messages = this.dataImportService.validateMeasurmentVariableNameLengths(measurementVariables);

		Assert.assertEquals("messages should be empty", 0, messages.size());
	}

	@Test
	public void testValidateMeasurmentVariableNameCharacters() throws Exception {
		List<MeasurementVariable> measurementVariables = this.getValidNamedMeasurementVariables();
		measurementVariables.addAll(this.getInvalidNamedMeasurementVariables());

		List<Message> messages = this.dataImportService.validateMeasurmentVariableNameCharacters(measurementVariables);

		Assert.assertEquals("we should only have messages same size with the STRINGS_WITH_INVALID_CHARACTERS count",
				DataImportServiceImplTest.STRINGS_WITH_INVALID_CHARACTERS.length, messages.size());

		for (Message message : messages) {
			Assert.assertTrue("returned messages should contain the names from the set of invalid strings list",
					Arrays.asList(DataImportServiceImplTest.STRINGS_WITH_INVALID_CHARACTERS).contains(message.getMessageParams()[0]));
		}
	}

	@Test
	public void testCheckForOutOfBoundsDataWithValidData() {

		Workbook testWorkbook = this.createTestWorkbook(true);

		Assert.assertTrue(this.dataImportService.checkForOutOfBoundsData(this.ontologyDataManager, testWorkbook, PROGRAM_UUID));

	}

	@Test
	public void testCheckForOutOfBoundsDataWithInvalidData() {

		Workbook testWorkbook = this.createTestWorkbook(false);

		Assert.assertFalse(this.dataImportService.checkForOutOfBoundsData(this.ontologyDataManager, testWorkbook, PROGRAM_UUID));

	}

	@Test
	public void testParseWorkbookWithDiscardInvalidValuesIsTrue() throws WorkbookParserException {

		Workbook testWorkbook = this.createTestWorkbook(true);

		Mockito.when(this.parser.parseFile(this.file, false)).thenReturn(testWorkbook);

		this.dataImportService.parseWorkbook(this.file, PROGRAM_UUID, true, this.ontologyDataManager, this.parser);

		Mockito.verify(this.parser).parseAndSetObservationRows(this.file, testWorkbook, true);

		Assert.assertFalse("Make sure the possible values of categorical variates is populated", testWorkbook.getVariates().get(0)
				.getPossibleValues().isEmpty());
		Assert.assertEquals("Make sure the datatype of categorical variates is CATEGORICAL_VARIBLE", DataType.CATEGORICAL_VARIABLE.getId(),
				testWorkbook.getVariates().get(0).getDataTypeId());
	}

	@Test
	public void testParseWorkbookWithDiscardInvalidValuesIsFalse() throws WorkbookParserException {

		Workbook testWorkbook = this.createTestWorkbook(true);

		Mockito.when(this.parser.parseFile(this.file, false)).thenReturn(testWorkbook);

		this.dataImportService.parseWorkbook(this.file, PROGRAM_UUID, false, this.ontologyDataManager, this.parser);

		Mockito.verify(this.parser).parseAndSetObservationRows(this.file, testWorkbook, false);

		Assert.assertFalse("Make sure the possible values of categorical variates is populated", testWorkbook.getVariates().get(0)
				.getPossibleValues().isEmpty());
		Assert.assertEquals("Make sure the datatype of categorical variates is CATEGORICAL_VARIBLE", DataType.CATEGORICAL_VARIABLE.getId(),
				testWorkbook.getVariates().get(0).getDataTypeId());

	}

	@Test
	public void testPopulatePossibleValuesForCategoricalVariatesStandardVariableIsCategorical() {

		List<MeasurementVariable> variates = new ArrayList<>();

		MeasurementVariable testMeasurementVariable =
				new MeasurementVariable(EARASP_1_5_NAME, "", EARASP_1_5_SCALE, EARASP_1_5_METHOD, EARASP_1_5_PROPERTY, "", "C", "VARIATE");
		variates.add(testMeasurementVariable);

		this.dataImportService.populatePossibleValuesForCategoricalVariates(variates, PROGRAM_UUID, this.ontologyDataManager);

		Assert.assertFalse(testMeasurementVariable.getPossibleValues().isEmpty());
		Assert.assertEquals(DataType.CATEGORICAL_VARIABLE.getId(), testMeasurementVariable.getDataTypeId());
	}

	public void testPopulatePossibleValuesForCategoricalVariatesStandardVariableIsNotCategorical() {

		StandardVariable testStandardVariable = this.createTestCategoricalStandardVariable(EARASP_1_5_NAME);
		testStandardVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numeric variable", ""));
		Mockito.when(this.ontologyDataManager.getStandardVariable(EARASP_1_5_TERMID, PROGRAM_UUID)).thenReturn(testStandardVariable);

		List<MeasurementVariable> variates = new ArrayList<>();

		MeasurementVariable testMeasurementVariable =
				new MeasurementVariable(EARASP_1_5_NAME, "", EARASP_1_5_SCALE, EARASP_1_5_METHOD, EARASP_1_5_PROPERTY, "", "C", "VARIATE");
		variates.add(testMeasurementVariable);

		this.dataImportService.populatePossibleValuesForCategoricalVariates(variates, "", this.ontologyDataManager);

		Assert.assertNull(testMeasurementVariable.getPossibleValues());
		Assert.assertNull(testMeasurementVariable.getDataTypeId());
	}

	private StandardVariable createTestCategoricalStandardVariable(String name) {
		StandardVariable stdVar = new StandardVariable();
		stdVar.setName(name);
		stdVar.setDataType(new Term(DataType.CATEGORICAL_VARIABLE.getId(), "", ""));
		List<Enumeration> enumerations = new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			enumerations.add(new Enumeration(i, String.valueOf(i), "", 0));
		}
		stdVar.setEnumerations(enumerations);
		return stdVar;
	}

	private Workbook createTestWorkbook(boolean withOutOfBoundsData) {

		Workbook testWorkbook = new Workbook();

		List<MeasurementVariable> variates = new ArrayList<>();

		MeasurementVariable categoricalMeasurementVariable =
				new MeasurementVariable(EARASP_1_5_TERMID, EARASP_1_5_NAME, EARASP_1_5_DEFINITION, EARASP_1_5_SCALE, EARASP_1_5_METHOD,
						EARASP_1_5_PROPERTY, NUMERIC, "", "VARIATE");

		variates.add(categoricalMeasurementVariable);

		testWorkbook.setVariates(variates);

		List<MeasurementRow> observations = new ArrayList<>();
		List<MeasurementData> dataList = new ArrayList<>();
		MeasurementData variateCategorical = new MeasurementData(EARASP_1_5_NAME, withOutOfBoundsData ? "7" : "6");
		dataList.add(variateCategorical);
		MeasurementRow row = new MeasurementRow();

		row.setDataList(dataList);
		observations.add(row);

		testWorkbook.setObservations(observations);

		return testWorkbook;
	}

	protected List<MeasurementVariable> initializeTestMeasurementVariables() {
		List<MeasurementVariable> measurementVariables = this.getShortNamedMeasurementVariables();

		// 5 long names
		for (int i = 0; i < DataImportServiceImplTest.INVALID_VARIABLES_COUNT; i++) {
			MeasurementVariable mv = new MeasurementVariable();

			mv.setName("NUM_" + i + "_MEASUREMENT_VARIABLE_WITH_NAME_UP_TO_THIRTY_TWO_CHARACTERS");
			measurementVariables.add(mv);
		}

		// also add those invalid variables to add to the main test
		measurementVariables.addAll(this.getInvalidNamedMeasurementVariables());

		return measurementVariables;
	}

	private List<MeasurementVariable> getShortNamedMeasurementVariables() {
		List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		// 5 short names
		for (int i = 0; i < DataImportServiceImplTest.VALID_VARIABLES_COUNT; i++) {
			MeasurementVariable mv = new MeasurementVariable();
			mv.setName("NUM_" + i + "_SHORT");
			measurementVariables.add(mv);
		}
		return measurementVariables;
	}

	private List<MeasurementVariable> getInvalidNamedMeasurementVariables() {
		List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		for (int i = 0; i < DataImportServiceImplTest.STRINGS_WITH_INVALID_CHARACTERS.length; i++) {
			MeasurementVariable mv = new MeasurementVariable();
			mv.setName(DataImportServiceImplTest.STRINGS_WITH_INVALID_CHARACTERS[i]);
			measurementVariables.add(mv);
		}
		return measurementVariables;
	}

	private List<MeasurementVariable> getValidNamedMeasurementVariables() {
		List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		for (int i = 0; i < DataImportServiceImplTest.STRINGS_WITH_VALID_CHARACTERS.length; i++) {
			MeasurementVariable mv = new MeasurementVariable();
			mv.setName(DataImportServiceImplTest.STRINGS_WITH_VALID_CHARACTERS[i]);
			measurementVariables.add(mv);
		}
		return measurementVariables;
	}

	private Workbook createTestWorkbook() {
		Workbook wb = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		wb.setStudyDetails(studyDetails);
		wb.setFactors(this.createTestFactors());
		return wb;
	}

	private List<MeasurementVariable> createTestFactors() {
		List<MeasurementVariable> list = new ArrayList<>();

		list.add(new MeasurementVariable(TermId.ENTRY_NO.getId(), ENTRY, "The germplasm entry number", NUMBER, ENUMERATED, GERMPLASM_ENTRY,
				NUMERIC, STUDY, ENTRY));
		list.add(new MeasurementVariable(TermId.PLOT_NO.getId(), PLOT, "Plot number ", NESTED_NUMBER, ENUMERATED, FIELD_PLOT, NUMERIC,
				NUMERIC_VALUE, PLOT));
		list.add(new MeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), "TRIAL_INSTANCE", "TRIAL NUMBER", NUMBER, ENUMERATED,
				TRIAL_INSTANCE, NUMERIC, "", TRIAL));
		return list;
	}
}
