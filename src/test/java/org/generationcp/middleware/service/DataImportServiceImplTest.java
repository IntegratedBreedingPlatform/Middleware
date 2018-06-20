package org.generationcp.middleware.service;

import com.google.common.base.Optional;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.util.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class DataImportServiceImplTest {

	private static final String PROGRAM_UUID = "123456789";
	public static final int TEST_VARIABLE_TERM_ID = 1111;
	public static final String TEST_PROPERTY_NAME = "test Property";
	public static final String TEST_SCALE_NAME = "test Scale";
	public static final String TEST_METHOD_NAME = "test Method";
	public static final String TEST_VARIABLE_NAME = "test Variable";
	private static final String EARASP_1_5_PROPERTY = "Ear aspect";
	private static final String EARASP_1_5_METHOD = "EARASP rating";
	private static final String EARASP_1_5_SCALE = "1-5 rating scale";
	private static final String EARASP_1_5_NAME = "EARASP_1_5";
	private static final int EARASP_1_5_TERMID = 20314;
	public static final int INVALID_VARIABLES_COUNT = 5;
	public static final int VALID_VARIABLES_COUNT = 5;

	private static final String STUDY_NAME = "Study 1";
	private static final int TRIAL_NO = 1;
	private static final boolean IS_MULTIPLE_LOCATION = false;
	public static final Integer CREATED_BY = 1;

	@Mock
	private WorkbookParser parser;

	@Mock
	private OntologyDataManager ontologyDataManager;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private org.apache.poi.ss.usermodel.Workbook excelWorkbook;

	@Mock
	private File file;

	private Workbook workbook;

	@InjectMocks
	private final DataImportServiceImpl dataImportService = new DataImportServiceImpl();

	public static final String[] STRINGS_WITH_INVALID_CHARACTERS = new String[] {"1234", "word@", "_+world=", "!!world!!", "&&&"};

	public static final String[] STRINGS_WITH_VALID_CHARACTERS =
			new String[] {"i_am_groot", "hello123world", "%%bangbang", "something_something", "zawaruldoisbig"};

	@Before
	public void init() {

		this.workbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("N"), STUDY_NAME, TRIAL_NO,
						IS_MULTIPLE_LOCATION);

		this.mockStandardVariable(TEST_VARIABLE_TERM_ID, TEST_VARIABLE_NAME, TEST_PROPERTY_NAME, TEST_SCALE_NAME, TEST_METHOD_NAME,
				PROGRAM_UUID);
		this.mockStandardVariable(TermId.PLOT_NO.getId(), WorkbookTestDataInitializer.PLOT, WorkbookTestDataInitializer.FIELD_PLOT,
				WorkbookTestDataInitializer.NESTED_NUMBER, WorkbookTestDataInitializer.ENUMERATED, PROGRAM_UUID);
		this.mockStandardVariable(TermId.GID.getId(), WorkbookTestDataInitializer.GID, WorkbookTestDataInitializer.GERMPLASM_ID,
				WorkbookTestDataInitializer.DBID, WorkbookTestDataInitializer.ASSIGNED, PROGRAM_UUID);
		this.mockStandardVariable(TermId.ENTRY_NO.getId(), WorkbookTestDataInitializer.ENTRY, WorkbookTestDataInitializer.GERMPLASM_ENTRY,
				WorkbookTestDataInitializer.NUMBER, WorkbookTestDataInitializer.ENUMERATED, PROGRAM_UUID);
		this.mockStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), WorkbookTestDataInitializer.STUDY_NAME,
				WorkbookTestDataInitializer.TRIAL, WorkbookTestDataInitializer.NUMBER, WorkbookTestDataInitializer.ENUMERATED,
				PROGRAM_UUID);

		this.mockStandardVariable(EARASP_1_5_TERMID, EARASP_1_5_NAME, EARASP_1_5_PROPERTY, EARASP_1_5_SCALE, EARASP_1_5_METHOD,
				PROGRAM_UUID);

	}

	protected void mockStandardVariable(final Integer termId, final String name, final String property, final String scale,
			final String method, final String programUUID) {

		Mockito.when(this.ontologyDataManager.getStandardVariableIdByPropertyScaleMethod(property, scale, method)).thenReturn(termId);
		Mockito.when(this.ontologyDataManager.getStandardVariable(termId, programUUID))
				.thenReturn(this.createTestCategoricalStandardVariable(name));
		Mockito.when(this.ontologyDataManager.findStandardVariableByTraitScaleMethodNames(property, scale, method, programUUID))
				.thenReturn(this.createTestCategoricalStandardVariable(name));

	}

	@Test
	public void testStrictParseWorkbookWithGreaterThan32VarNames() throws Exception {

		// Add variables with long names
		this.workbook.getAllVariables().addAll(this.initializeTestMeasurementVariables());

		try {
			dataImportService.strictParseWorkbook(this.file, this.parser, workbook, DataImportServiceImplTest.PROGRAM_UUID);
			Assert.fail("We expect workbookParserException to be thrown");
		} catch (final WorkbookParserException e) {

			final String[] errorTypes = {DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_LENGTH,
					DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_CHARACTERS};
			for (final Message error : e.getErrorMessages()) {

				Assert.assertTrue(
						"All errors should contain either ERROR_INVALID_VARIABLE_NAME_CHARACTERS or ERROR_INVALID_VARIABLE_NAME_LENGTH",
						Arrays.asList(errorTypes).contains(error.getMessageKey()));
			}
		}
	}

	@Test
	public void testValidateMeasurementVariableNameLengths() throws Exception {
		final List<MeasurementVariable> measurementVariables = this.initializeTestMeasurementVariables();

		final List<Message> messages = this.dataImportService.validateMeasurmentVariableNameLengths(measurementVariables);

		Assert.assertEquals("we should only have 5 variables with > 32 char length", DataImportServiceImplTest.INVALID_VARIABLES_COUNT,
				messages.size());

		for (final Message message : messages) {
			Assert.assertTrue("returned messages should only contain the variables with names > 32",
					message.getMessageParams()[0].length() > 32);
		}
	}

	@Test
	public void testValidateMeasurementVariableNameLengthsAllShortNames() throws Exception {
		final List<MeasurementVariable> measurementVariables = this.getShortNamedMeasurementVariables();

		final List<Message> messages = this.dataImportService.validateMeasurmentVariableNameLengths(measurementVariables);

		Assert.assertEquals("messages should be empty", 0, messages.size());
	}

	@Test
	public void testValidateMeasurmentVariableNameCharacters() throws Exception {
		final List<MeasurementVariable> measurementVariables = this.getValidNamedMeasurementVariables();
		measurementVariables.addAll(this.getInvalidNamedMeasurementVariables());

		final List<Message> messages = this.dataImportService.validateMeasurmentVariableNameCharacters(measurementVariables);

		Assert.assertEquals("we should only have messages same size with the STRINGS_WITH_INVALID_CHARACTERS count",
				DataImportServiceImplTest.STRINGS_WITH_INVALID_CHARACTERS.length, messages.size());

		for (final Message message : messages) {
			Assert.assertTrue("returned messages should contain the names from the set of invalid strings list",
					Arrays.asList(DataImportServiceImplTest.STRINGS_WITH_INVALID_CHARACTERS).contains(message.getMessageParams()[0]));
		}
	}

	@Test
	public void testCheckForOutOfBoundsDataWithValidData() {

		final Workbook testWorkbook = this.createTestWorkbook(true);

		Assert.assertTrue(this.dataImportService.checkForOutOfBoundsData(testWorkbook, PROGRAM_UUID));

	}

	@Test
	public void testCheckForOutOfBoundsDataWithInvalidData() {

		final Workbook testWorkbook = this.createTestWorkbook(false);

		Assert.assertFalse(this.dataImportService.checkForOutOfBoundsData(testWorkbook, PROGRAM_UUID));

	}

	@Test
	public void testFindMeasurementVariableByTermIdMeasurementVariableIsFound() {

		final Optional<MeasurementVariable> result =
				this.dataImportService.findMeasurementVariableByTermId(TermId.ENTRY_NO.getId(), this.workbook.getFactors());

		Assert.assertTrue("Measurement variable is found, so the value is present", result.isPresent());
		Assert.assertNotNull(result.get());
		Assert.assertEquals(TermId.ENTRY_NO.getId(), result.get().getTermId());
	}

	@Test
	public void testFindMeasurementVariableByTermIdMeasurementVariableIsNotFound() {

		final Optional<MeasurementVariable> result =
				this.dataImportService.findMeasurementVariableByTermId(TermId.BREEDING_METHOD_CODE.getId(), this.workbook.getFactors());

		Assert.assertFalse("No measurement variable found, so the value is not present", result.isPresent());

	}

	@Test
	public void testSetRequiredField() {

		this.dataImportService.setRequiredField(TermId.ENTRY_NO.getId(), this.workbook.getFactors());

		final Optional<MeasurementVariable> result =
				this.dataImportService.findMeasurementVariableByTermId(TermId.ENTRY_NO.getId(), this.workbook.getFactors());

		if (result.isPresent()) {
			Assert.assertEquals(TermId.ENTRY_NO.getId(), result.get().getTermId());
			Assert.assertTrue("The variable's required field should be set to true", result.get().isRequired());
		} else {
			Assert.fail("The variable entry_no should be found because it exists in the list");
		}

	}

	@Test
	public void testSetRequiredFieldsForStudy() {

		final Workbook trialWorkbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO, true);

		this.dataImportService.setRequiredFields(trialWorkbook);

		final Optional<MeasurementVariable> optionalPlotNo =
				dataImportService.findMeasurementVariableByTermId(TermId.PLOT_NO.getId(), trialWorkbook.getFactors());
		final Optional<MeasurementVariable> optionalEntryNo =
				dataImportService.findMeasurementVariableByTermId(TermId.ENTRY_NO.getId(), trialWorkbook.getFactors());
		final Optional<MeasurementVariable> optionalGid =
				dataImportService.findMeasurementVariableByTermId(TermId.GID.getId(), trialWorkbook.getFactors());
		final Optional<MeasurementVariable> optionalTrialInstance =
				dataImportService.findMeasurementVariableByTermId(TermId.TRIAL_INSTANCE_FACTOR.getId(), trialWorkbook.getTrialVariables());
		final Optional<MeasurementVariable> optionalPlotNNo =
				dataImportService.findMeasurementVariableByTermId(TermId.PLOT_NNO.getId(), trialWorkbook.getFactors());

		Assert.assertTrue(optionalPlotNo.get().isRequired());
		Assert.assertTrue(optionalEntryNo.get().isRequired());
		Assert.assertTrue(optionalGid.get().isRequired());
		Assert.assertTrue(optionalTrialInstance.get().isRequired());
		Assert.assertFalse(optionalPlotNNo.isPresent());

	}

	@Test
	public void testExtractGidsFromObservations() {

		final Set<Integer> result =
				this.dataImportService.extractGidsFromObservations(WorkbookTestDataInitializer.GID, this.workbook.getObservations());

		Assert.assertEquals(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, result.size());

		for (final Integer gid : result) {
			Assert.assertNotNull("Gid should not be null", gid);
		}

	}

	@Test
	public void testCheckIfAllObservationHasGidAndNumericTrue() {

		Assert.assertTrue(this.dataImportService
				.checkIfAllObservationHasGidAndNumeric(WorkbookTestDataInitializer.GID, this.workbook.getObservations()));

	}

	@Test
	public void testParseWorkbookWithDiscardInvalidValuesIsTrue() throws WorkbookParserException {

		final Workbook testWorkbook = this.createTestWorkbook(true);

		Mockito.when(this.parser.loadFileToExcelWorkbook(this.file)).thenReturn(this.excelWorkbook);
		Mockito.when(this.parser.parseFile(excelWorkbook, false, CREATED_BY.toString())).thenReturn(testWorkbook);

		this.dataImportService.parseWorkbook(this.file, PROGRAM_UUID, true, this.parser, CREATED_BY);

		Mockito.verify(this.parser).parseAndSetObservationRows(excelWorkbook, testWorkbook, true);

		Assert.assertFalse("Make sure the possible values of categorical variates is populated",
				testWorkbook.getVariates().get(0).getPossibleValues().isEmpty());
		Assert.assertEquals("Make sure the datatype of categorical variates is CATEGORICAL_VARIBLE", DataType.CATEGORICAL_VARIABLE.getId(),
				testWorkbook.getVariates().get(0).getDataTypeId());
	}

	@Test
	public void testParseWorkbookWithDiscardInvalidValuesIsFalse() throws WorkbookParserException {

		final Workbook testWorkbook = this.createTestWorkbook(true);

		Mockito.when(this.parser.loadFileToExcelWorkbook(this.file)).thenReturn(this.excelWorkbook);
		Mockito.when(this.parser.parseFile(this.excelWorkbook, false, CREATED_BY.toString())).thenReturn(testWorkbook);

		this.dataImportService.parseWorkbook(this.file, PROGRAM_UUID, false, this.parser, CREATED_BY);

		Mockito.verify(this.parser).parseAndSetObservationRows(excelWorkbook, testWorkbook, false);

		Assert.assertFalse("Make sure the possible values of categorical variates is populated",
				testWorkbook.getVariates().get(0).getPossibleValues().isEmpty());
		Assert.assertEquals("Make sure the datatype of categorical variates is CATEGORICAL_VARIBLE", DataType.CATEGORICAL_VARIABLE.getId(),
				testWorkbook.getVariates().get(0).getDataTypeId());

	}

	@Test
	public void testParseWorkbookWithObsoleteVariables() throws WorkbookParserException {

		final Workbook testWorkbook = this.createTestWorkbook(false);

		// Add obsolete variable to workbook's factors, conditions, constants and variates list.
		final StandardVariable obsoleteStandardVariable = new StandardVariable();
		obsoleteStandardVariable.setObsolete(true);

		Mockito.when(this.ontologyDataManager
				.findStandardVariableByTraitScaleMethodNames(TEST_PROPERTY_NAME, TEST_SCALE_NAME, TEST_METHOD_NAME, PROGRAM_UUID))
				.thenReturn(obsoleteStandardVariable);

		Mockito.when(this.parser.loadFileToExcelWorkbook(this.file)).thenReturn(this.excelWorkbook);
		Mockito.when(this.parser.parseFile(excelWorkbook, false, CREATED_BY.toString())).thenReturn(testWorkbook);

		this.dataImportService.parseWorkbook(this.file, PROGRAM_UUID, false, this.parser, CREATED_BY);

		Mockito.verify(this.parser).parseAndSetObservationRows(excelWorkbook, testWorkbook, false);

		// Expecting workbook's factors, conditions, constants and variates list are empty because
		// they only contained obsolete variables.
		Assert.assertTrue(testWorkbook.getFactors().isEmpty());
		Assert.assertTrue(testWorkbook.getConditions().isEmpty());
		Assert.assertTrue(testWorkbook.getConstants().isEmpty());
		Assert.assertTrue(testWorkbook.getVariates().isEmpty());

	}

	@Test
	public void testCheckIfAllObservationHasGidAndNumericGidIsBlank() {

		final List<MeasurementRow> observations = this.workbook.getObservations();

		// Set the GID to null of one observation to simulate blank gid in data file.
		final MeasurementRow row = observations.get(0);
		final MeasurementData measurementData = row.getMeasurementData(WorkbookTestDataInitializer.GID);
		measurementData.setValue(null);

		Assert.assertFalse(this.dataImportService
				.checkIfAllObservationHasGidAndNumeric(WorkbookTestDataInitializer.GID, this.workbook.getObservations()));

		measurementData.setValue("");

		Assert.assertFalse(this.dataImportService
				.checkIfAllObservationHasGidAndNumeric(WorkbookTestDataInitializer.GID, this.workbook.getObservations()));

		measurementData.setValue(" ");

		Assert.assertFalse(this.dataImportService
				.checkIfAllObservationHasGidAndNumeric(WorkbookTestDataInitializer.GID, this.workbook.getObservations()));

	}

	@Test
	public void testCheckIfAllObservationHasGidAndNumericGidIsNotNumeric() {

		final List<MeasurementRow> observations = this.workbook.getObservations();

		// Set the GID to null of one observation to simulate blank gid in data file.
		final MeasurementRow row = observations.get(0);
		final MeasurementData measurementData = row.getMeasurementData(WorkbookTestDataInitializer.GID);
		measurementData.setValue("123AAA");

		Assert.assertFalse("Should return false because the GID contains non numeric characters", this.dataImportService
				.checkIfAllObservationHasGidAndNumeric(WorkbookTestDataInitializer.GID, this.workbook.getObservations()));

	}

	@Test
	public void testCheckIfAllGidsExistInDatabaseAllGidsExist() {

		final Set<Integer> gids =
				this.dataImportService.extractGidsFromObservations(WorkbookTestDataInitializer.GID, this.workbook.getObservations());

		Mockito.when(this.germplasmDataManager.countMatchGermplasmInList(gids)).thenReturn(Long.valueOf(gids.size()));

		Assert.assertTrue("Should return true because all gids in the list exist in the database",
				this.dataImportService.checkIfAllGidsExistInDatabase(gids));

	}

	@Test
	public void testCheckIfAllGidsExistInDatabaseNoGidsExist() {

		final Set<Integer> gids =
				this.dataImportService.extractGidsFromObservations(WorkbookTestDataInitializer.GID, this.workbook.getObservations());

		Mockito.when(this.germplasmDataManager.countMatchGermplasmInList(gids)).thenReturn(0L);

		Assert.assertFalse("Should return false because not all gids in the list exist in the database",
				this.dataImportService.checkIfAllGidsExistInDatabase(gids));

	}

	@Test
	public void testCheckForInvalidGidsAllGidsExist() {

		// The count of matched record in germplasm should match the number of observation in data file.
		Mockito.when(this.germplasmDataManager.countMatchGermplasmInList(Matchers.anySet()))
				.thenReturn(Long.valueOf(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS));

		final List<Message> messages = new ArrayList<>();
		this.dataImportService.checkForInvalidGids(this.workbook, messages);

		Assert.assertTrue("All gids exist in the database, so no error message should be added in messages list.", messages.isEmpty());

	}

	@Test
	public void testCheckForInvalidGidsDoNotExist() {

		// Retun a number not equal to no of observation to simulate that there are gids that do not exist in the database.
		Mockito.when(this.germplasmDataManager.countMatchGermplasmInList(Matchers.anySet())).thenReturn(Long.valueOf(0L));

		final List<Message> messages = new ArrayList<>();
		this.dataImportService.checkForInvalidGids(this.workbook, messages);

		Assert.assertTrue(!messages.isEmpty());
		// Make sure that invalid gids error message is added to the messages list.
		Assert.assertEquals(DataImportServiceImpl.ERROR_INVALID_GIDS_FROM_DATA_FILE, messages.get(0).getMessageKey());

	}

	@Test
	public void testCheckForInvalidGidVariableNotInFile() {

		// Remove the GID variable to simulate that GID doesnt exist in the data file
		final Iterator<MeasurementVariable> iterator = this.workbook.getFactors().iterator();
		while (iterator.hasNext()) {
			if (Objects.equals(iterator.next().getName(), WorkbookTestDataInitializer.GID)) {
				iterator.remove();
			}
		}

		final List<Message> messages = new ArrayList<>();
		this.dataImportService.checkForInvalidGids(this.workbook, messages);

		Assert.assertTrue(!messages.isEmpty());
		// Make sure that gid doesnt exist error message is added to the messages list.
		Assert.assertEquals(DataImportServiceImpl.ERROR_GID_DOESNT_EXIST, messages.get(0).getMessageKey());
	}

	@Test
	public void testPopulatePossibleValuesForCategoricalVariatesStandardVariableIsCategorical() {

		final List<MeasurementVariable> variates = new ArrayList<>();

		final MeasurementVariable testMeasurementVariable =
				new MeasurementVariable(EARASP_1_5_NAME, "", EARASP_1_5_SCALE, EARASP_1_5_METHOD, EARASP_1_5_PROPERTY, "", "C", "VARIATE");
		variates.add(testMeasurementVariable);

		this.dataImportService.populatePossibleValuesForCategoricalVariates(variates, PROGRAM_UUID);

		Assert.assertFalse(testMeasurementVariable.getPossibleValues().isEmpty());
		Assert.assertEquals(DataType.CATEGORICAL_VARIABLE.getId(), testMeasurementVariable.getDataTypeId());
	}

	@Test
	public void testPopulatePossibleValuesForCategoricalVariatesStandardVariableIsNotCategorical() {

		final StandardVariable testStandardVariable = this.createTestCategoricalStandardVariable(EARASP_1_5_NAME);
		testStandardVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numeric variable", ""));
		Mockito.when(this.ontologyDataManager.getStandardVariable(EARASP_1_5_TERMID, PROGRAM_UUID)).thenReturn(testStandardVariable);

		final List<MeasurementVariable> variates = new ArrayList<>();

		final MeasurementVariable testMeasurementVariable =
				new MeasurementVariable(EARASP_1_5_NAME, "", EARASP_1_5_SCALE, EARASP_1_5_METHOD, EARASP_1_5_PROPERTY, "", "C", "VARIATE");
		variates.add(testMeasurementVariable);

		this.dataImportService.populatePossibleValuesForCategoricalVariates(variates, "");

		Assert.assertNull(testMeasurementVariable.getPossibleValues());
		Assert.assertNull(testMeasurementVariable.getDataTypeId());
	}

	@Test
	public void testGetTermIdsOfMeasurementVariables() {

		final Set<Integer> termIds = this.dataImportService.getTermIdsOfMeasurementVariables(this.workbook.getFactors());

		Assert.assertTrue("The termid of entry no should be in the list because it's in the ontology",
				termIds.contains(TermId.ENTRY_NO.getId()));
		Assert.assertTrue("The termid of plot no should be in the list because it's in the ontology",
				termIds.contains(TermId.PLOT_NO.getId()));
		Assert.assertTrue("The termid of gid should be in the list because it's in the ontology", termIds.contains(TermId.GID.getId()));
		Assert.assertFalse("The termid of desig should not be in the list because it's not in the ontology",
				termIds.contains(TermId.DESIG.getId()));
		Assert.assertFalse("The termid of trial instance not be in the list because it's not in the factors list",
				termIds.contains(TermId.TRIAL_INSTANCE_FACTOR.getId()));

	}

	@Test
	public void testRemoveObsoloteVariablesInWorkbookVariablesAreObsolote() {

		final Workbook testWorkbook = this.createTestWorkbook(false);

		// Add obsolete variable to workbook's factors, conditions, constants and variates list.
		final StandardVariable obsoleteStandardVariable = new StandardVariable();
		obsoleteStandardVariable.setObsolete(true);

		Mockito.when(this.ontologyDataManager
				.findStandardVariableByTraitScaleMethodNames(TEST_PROPERTY_NAME, TEST_SCALE_NAME, TEST_METHOD_NAME, PROGRAM_UUID))
				.thenReturn(obsoleteStandardVariable);

		final List<String> obsoloteVariableNames = this.dataImportService.removeObsoloteVariablesInWorkbook(testWorkbook, PROGRAM_UUID);

		// Expecting workbook's factors, conditions, constants and variates list are empty because
		// they only contained obsolete variables.
		Assert.assertTrue(testWorkbook.getFactors().isEmpty());
		Assert.assertTrue(testWorkbook.getConditions().isEmpty());
		Assert.assertTrue(testWorkbook.getConstants().isEmpty());
		Assert.assertTrue(testWorkbook.getVariates().isEmpty());

		// The obsolete variable names list should contain the name of obsolete variable names that were removed
		Assert.assertTrue(obsoloteVariableNames.contains(TEST_VARIABLE_NAME));

	}

	@Test
	public void testRemoveObsoloteVariablesInWorkbookVariablesAreNotObsolote() {

		final Workbook testWorkbook = this.createTestWorkbook(false);

		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setObsolete(false);

		Mockito.when(this.ontologyDataManager
				.findStandardVariableByTraitScaleMethodNames(TEST_PROPERTY_NAME, TEST_SCALE_NAME, TEST_METHOD_NAME, PROGRAM_UUID))
				.thenReturn(standardVariable);

		final List<String> obsoloteVariableNames = this.dataImportService.removeObsoloteVariablesInWorkbook(testWorkbook, PROGRAM_UUID);

		// Expecting workbook's factors, conditions, constants and variates list are not empty because
		// they contain non-obsolete variables
		Assert.assertFalse(testWorkbook.getFactors().isEmpty());
		Assert.assertFalse(testWorkbook.getConditions().isEmpty());
		Assert.assertFalse(testWorkbook.getConstants().isEmpty());
		Assert.assertFalse(testWorkbook.getVariates().isEmpty());

		// The obsolete variable names list should be empty because no obsolete variable was removed.
		Assert.assertTrue(obsoloteVariableNames.isEmpty());

	}

	@Test
	public void testRemoveObsoleteMeasurementVariableVariableIsObsolete() {

		// Create an 'AD' measurement variable
		final MeasurementVariable adObsolete = new MeasurementVariable("AD", "", "days", "Computed", "Anthesis time.", "N", "", "VARIATE");

		// Create an 'AD' standard variable and tag as obsolete
		final StandardVariable adStandardVariable = new StandardVariable();
		adStandardVariable.setObsolete(true);

		Mockito.when(this.ontologyDataManager
				.findStandardVariableByTraitScaleMethodNames(adObsolete.getProperty(), adObsolete.getScale(), adObsolete.getMethod(),
						PROGRAM_UUID)).thenReturn(adStandardVariable);

		final List<MeasurementVariable> measurementVariables = new ArrayList<>();
		measurementVariables.add(adObsolete);

		final List<String> obsoloteVariableNames =
				this.dataImportService.removeObsoleteMeasurementVariables(measurementVariables, PROGRAM_UUID);

		// Expecting that measurementVariables list is empty.
		// The added variable should be deleted from the list because it is obsolete.
		Assert.assertTrue(measurementVariables.isEmpty());

		// The obsolete variable names list should contain the name of obsolete variable that was removed
		Assert.assertTrue(obsoloteVariableNames.contains(adObsolete.getName()));

	}

	@Test
	public void testRemoveObsoleteMeasurementVariable() {

		// Create an 'AD' measurement variable
		final MeasurementVariable adObsolete = new MeasurementVariable("AD", "", "days", "Computed", "Anthesis time.", "N", "", "VARIATE");

		// Create an 'AD' standard variable
		final StandardVariable adStandardVariable = new StandardVariable();
		adStandardVariable.setObsolete(false);

		Mockito.when(this.ontologyDataManager
				.findStandardVariableByTraitScaleMethodNames(adObsolete.getProperty(), adObsolete.getScale(), adObsolete.getMethod(),
						PROGRAM_UUID)).thenReturn(adStandardVariable);

		final List<MeasurementVariable> measurementVariables = new ArrayList<>();
		measurementVariables.add(adObsolete);

		this.dataImportService.removeObsoleteMeasurementVariables(measurementVariables, PROGRAM_UUID);

		// Expecting that measurementVariables list has 1 item.
		// The added variable should not be deleted from the list because it is not obsolete.
		Assert.assertEquals(1, measurementVariables.size());
		Assert.assertEquals(adObsolete, measurementVariables.get(0));

	}

	private StandardVariable createTestCategoricalStandardVariable(final String name) {
		final StandardVariable stdVar = new StandardVariable();
		stdVar.setName(name);
		stdVar.setDataType(new Term(DataType.CATEGORICAL_VARIABLE.getId(), "", ""));
		final List<Enumeration> enumerations = new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			enumerations.add(new Enumeration(i, String.valueOf(i), "", 0));
		}
		stdVar.setEnumerations(enumerations);
		return stdVar;
	}

	private Workbook createTestWorkbook(final boolean withOutOfBoundsData) {

		final Workbook testWorkbook = new Workbook();

		testWorkbook.setFactors(new ArrayList<MeasurementVariable>(Arrays.asList(
				new MeasurementVariable(TEST_VARIABLE_NAME, "", TEST_SCALE_NAME, TEST_METHOD_NAME, TEST_PROPERTY_NAME, "", "", ""))));
		testWorkbook.setConditions(new ArrayList<MeasurementVariable>(Arrays.asList(
				new MeasurementVariable(TEST_VARIABLE_NAME, "", TEST_SCALE_NAME, TEST_METHOD_NAME, TEST_PROPERTY_NAME, "", "", ""))));
		testWorkbook.setConstants(new ArrayList<MeasurementVariable>(Arrays.asList(
				new MeasurementVariable(TEST_VARIABLE_NAME, "", TEST_SCALE_NAME, TEST_METHOD_NAME, TEST_PROPERTY_NAME, "", "", ""))));
		testWorkbook.setVariates(new ArrayList<MeasurementVariable>(Arrays.asList(
				new MeasurementVariable(TEST_VARIABLE_NAME, "", TEST_SCALE_NAME, TEST_METHOD_NAME, TEST_PROPERTY_NAME, "", "", ""))));

		final List<MeasurementRow> observations = new ArrayList<>();
		final List<MeasurementData> dataList = new ArrayList<>();
		final MeasurementData variateCategorical = new MeasurementData(TEST_VARIABLE_NAME, withOutOfBoundsData ? "7" : "6");
		dataList.add(variateCategorical);
		final MeasurementRow row = new MeasurementRow();

		row.setDataList(dataList);
		observations.add(row);

		testWorkbook.setObservations(observations);

		return testWorkbook;

	}

	protected List<MeasurementVariable> initializeTestMeasurementVariables() {
		final List<MeasurementVariable> measurementVariables = this.getShortNamedMeasurementVariables();

		// 5 long names
		for (int i = 0; i < DataImportServiceImplTest.INVALID_VARIABLES_COUNT; i++) {
			final MeasurementVariable mv = new MeasurementVariable();

			mv.setName("NUM_" + i + "_MEASUREMENT_VARIABLE_WITH_NAME_UP_TO_THIRTY_TWO_CHARACTERS");
			measurementVariables.add(mv);
		}

		// also add those invalid variables to add to the main test
		measurementVariables.addAll(this.getInvalidNamedMeasurementVariables());

		return measurementVariables;
	}

	private List<MeasurementVariable> getShortNamedMeasurementVariables() {
		final List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		// 5 short names
		for (int i = 0; i < DataImportServiceImplTest.VALID_VARIABLES_COUNT; i++) {
			final MeasurementVariable mv = new MeasurementVariable();
			mv.setName("NUM_" + i + "_SHORT");
			measurementVariables.add(mv);
		}
		return measurementVariables;
	}

	private List<MeasurementVariable> getInvalidNamedMeasurementVariables() {
		final List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		for (int i = 0; i < DataImportServiceImplTest.STRINGS_WITH_INVALID_CHARACTERS.length; i++) {
			final MeasurementVariable mv = new MeasurementVariable();
			mv.setName(DataImportServiceImplTest.STRINGS_WITH_INVALID_CHARACTERS[i]);
			measurementVariables.add(mv);
		}
		return measurementVariables;
	}

	private List<MeasurementVariable> getValidNamedMeasurementVariables() {
		final List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		for (int i = 0; i < DataImportServiceImplTest.STRINGS_WITH_VALID_CHARACTERS.length; i++) {
			final MeasurementVariable mv = new MeasurementVariable();
			mv.setName(DataImportServiceImplTest.STRINGS_WITH_VALID_CHARACTERS[i]);
			measurementVariables.add(mv);
		}
		return measurementVariables;
	}

}
