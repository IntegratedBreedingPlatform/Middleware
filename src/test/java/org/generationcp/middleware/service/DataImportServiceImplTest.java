package org.generationcp.middleware.service;

import com.google.common.base.Optional;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchService;
import org.generationcp.middleware.api.location.LocationService;
import org.generationcp.middleware.data.initializer.StandardVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.util.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

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
	private static final int TEST_VARIABLE_TERM_ID = 1111;
	private static final String TEST_PROPERTY_NAME = "test Property";
	private static final String TEST_SCALE_NAME = "test Scale";
	private static final String TEST_METHOD_NAME = "test Method";
	private static final String TEST_VARIABLE_NAME = "test Variable";
	private static final String EARASP_1_5_PROPERTY = "Ear aspect";
	private static final String EARASP_1_5_METHOD = "EARASP rating";
	private static final String EARASP_1_5_SCALE = "1-5 rating scale";
	private static final String EARASP_1_5_NAME = "EARASP_1_5";
	private static final int EARASP_1_5_TERMID = 20314;
	private static final int INVALID_VARIABLES_COUNT = 5;
	private static final int VALID_VARIABLES_COUNT = 5;

	private static final String STUDY_NAME = "Study 1";
	private static final int TRIAL_NO = 1;
	private static final boolean IS_MULTIPLE_LOCATION = false;
	private static final Integer CREATED_BY = 1;
	private static final int UNSPECIFIED_LOCATION_LOCID = 9999;

	@Mock
	private WorkbookParser parser;

	@Mock
	private OntologyDataManager ontologyDataManager;

	@Mock
	private GermplasmSearchService germplasmSearchService;

	@Mock
	private org.apache.poi.ss.usermodel.Workbook excelWorkbook;

	@Mock
	private File file;

	@Mock
	private TermDataManager termDataManager;

	@Mock
	private LocationService locationService;

	private Workbook workbook;

	@InjectMocks
	private final DataImportServiceImpl dataImportService = new DataImportServiceImpl();

	private static final String[] STRINGS_WITH_INVALID_CHARACTERS = new String[] {"1234", "word@", "_+world=", "!!world!!", "&&&"};

	private static final String[] STRINGS_WITH_VALID_CHARACTERS =
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

		final List<Location> locations = new ArrayList<>();
		final Location unspecifiedLocation = new Location();
		unspecifiedLocation.setLname(Location.UNSPECIFIED_LOCATION);
		unspecifiedLocation.setLocid(UNSPECIFIED_LOCATION_LOCID);
		locations.add(unspecifiedLocation);
		Mockito.when(this.locationService.retrieveLocIdOfUnspecifiedLocation()).thenReturn(String.valueOf(UNSPECIFIED_LOCATION_LOCID));


		final StandardVariable locationVariable = StandardVariableTestDataInitializer.createStandardVariable();
		locationVariable.setId(TermId.LOCATION_ID.getId());
		locationVariable.setName("LOCATION_ID");
		Mockito.when(this.ontologyDataManager.getStandardVariable(TermId.LOCATION_ID.getId(), PROGRAM_UUID)).thenReturn(locationVariable);

		final StandardVariable exptDesignVariable = StandardVariableTestDataInitializer.createStandardVariable();
		exptDesignVariable.setId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		exptDesignVariable.setName("EXPERIMENT DESIGN");
		Mockito.when(this.ontologyDataManager.getStandardVariable(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), PROGRAM_UUID)).thenReturn(exptDesignVariable);
	}

	private void mockStandardVariable(final Integer termId, final String name, final String property, final String scale,
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
	public void testValidateMeasurementVariableNameLengths() {
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
	public void testValidateMeasurementVariableNameLengthsAllShortNames() {
		final List<MeasurementVariable> measurementVariables = this.getShortNamedMeasurementVariables();

		final List<Message> messages = this.dataImportService.validateMeasurmentVariableNameLengths(measurementVariables);

		Assert.assertEquals("messages should be empty", 0, messages.size());
	}

	@Test
	public void testValidateMeasurmentVariableNameCharacters() {
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
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
						true);

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
		Assert.assertFalse(testWorkbook.getConditions().isEmpty());
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

		Mockito.when(this.germplasmSearchService.countSearchGermplasm(this.getGermplasmSearchRequest(gids), PROGRAM_UUID)).thenReturn(Long.valueOf(gids.size()));

		Assert.assertTrue("Should return true because all gids in the list exist in the database",
				this.dataImportService.checkIfAllGidsExistInDatabase(gids, PROGRAM_UUID));

	}

	@Test
	public void testCheckIfAllGidsExistInDatabaseNoGidsExist() {

		final Set<Integer> gids =
				this.dataImportService.extractGidsFromObservations(WorkbookTestDataInitializer.GID, this.workbook.getObservations());

		Mockito.when(this.germplasmSearchService.countSearchGermplasm(this.getGermplasmSearchRequest(gids), PROGRAM_UUID)).thenReturn(0L);

		Assert.assertFalse("Should return false because not all gids in the list exist in the database",
				this.dataImportService.checkIfAllGidsExistInDatabase(gids, PROGRAM_UUID));

	}

	@Test
	public void testCheckForInvalidGidsAllGidsExist() {

		// The count of matched record in germplasm should match the number of observation in data file.
		Mockito.when(this.germplasmSearchService.countSearchGermplasm(ArgumentMatchers.any(GermplasmSearchRequest.class), ArgumentMatchers.eq(PROGRAM_UUID)))
				.thenReturn(Long.valueOf(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS));

		final List<Message> messages = new ArrayList<>();
		this.dataImportService.checkForInvalidGids(this.workbook, messages, PROGRAM_UUID);

		Assert.assertTrue("All gids exist in the database, so no error message should be added in messages list.", messages.isEmpty());

	}

	@Test
	public void testCheckForInvalidGidsDoNotExist() {

		// Retun a number not equal to no of observation to simulate that there are gids that do not exist in the database.
		Mockito.when(this.germplasmSearchService.countSearchGermplasm(ArgumentMatchers.any(GermplasmSearchRequest.class),
			ArgumentMatchers.eq(PROGRAM_UUID))).thenReturn(Long.valueOf(0L));

		final List<Message> messages = new ArrayList<>();
		this.dataImportService.checkForInvalidGids(this.workbook, messages, PROGRAM_UUID);

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
		this.dataImportService.checkForInvalidGids(this.workbook, messages, PROGRAM_UUID);

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

	@Test
	public void testCreateLocationVariable() {

		final MeasurementVariable locationMeasurementVariable = this.dataImportService.createLocationVariable(PROGRAM_UUID);

		Assert.assertEquals(Workbook.DEFAULT_LOCATION_ID_VARIABLE_ALIAS, locationMeasurementVariable.getName());
		Assert.assertEquals(VariableType.ENVIRONMENT_DETAIL, locationMeasurementVariable.getVariableType());
		Assert.assertEquals(String.valueOf(UNSPECIFIED_LOCATION_LOCID), locationMeasurementVariable.getValue());

	}

	@Test
	public void testAddLocationIDVariableIfNotExistsLocationIDIsNotPresentInConditionsAndFactors() {

		final Workbook trialWorkbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
						true);

		final List<MeasurementVariable> measurementVariables = new ArrayList<>();

		removeMeasurementVariableInList(TermId.LOCATION_ID.getId(), trialWorkbook.getConditions());
		removeMeasurementVariableInList(TermId.LOCATION_ID.getId(), trialWorkbook.getFactors());

		this.dataImportService.addLocationIDVariableIfNotExists(trialWorkbook, measurementVariables, PROGRAM_UUID);

		Assert.assertTrue(
				this.dataImportService.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), measurementVariables).isPresent());

	}

	@Test
	public void testAddLocationIDVariableIfNotExistsNoLocationId() {

		final Workbook trialWorkbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
						true);

		removeMeasurementVariableInList(TermId.LOCATION_ID.getId(), trialWorkbook.getConditions());

		final List<MeasurementVariable> measurementVariables = new ArrayList<>();

		this.dataImportService.addLocationIDVariableIfNotExists(trialWorkbook, measurementVariables, PROGRAM_UUID);

		Assert.assertTrue(
				this.dataImportService.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), measurementVariables).isPresent());

	}

	@Test
	public void testAddLocationIDVariableIfNotExistsLocationIDIsAlreadyPresentInConditions() {

		final Workbook trialWorkbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
						true);

		final List<MeasurementVariable> measurementVariables = new ArrayList<>();

		this.dataImportService.addLocationIDVariableIfNotExists(trialWorkbook, measurementVariables, PROGRAM_UUID);

		Assert.assertFalse(
				this.dataImportService.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), measurementVariables).isPresent());

	}

	@Test
	public void testAddLocationIDVariableIfNotExistsLocationIDIsAlreadyPresentInFactors() {

		final Workbook trialWorkbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
						true);

		final Optional<MeasurementVariable> locationIdFromCondition =
				this.dataImportService.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), trialWorkbook.getConditions());
		trialWorkbook.getFactors().add(locationIdFromCondition.get());

		removeMeasurementVariableInList(TermId.LOCATION_ID.getId(), trialWorkbook.getConditions());

		final List<MeasurementVariable> measurementVariables = new ArrayList<>();

		this.dataImportService.addLocationIDVariableIfNotExists(trialWorkbook, measurementVariables, PROGRAM_UUID);

		Assert.assertFalse(
				this.dataImportService.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), measurementVariables).isPresent());

	}

	@Test
	public void testRemoveLocationNameVariableIfExists() {

		final Workbook trialWorkbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
						true);

		final Optional<MeasurementVariable> locationNameFromCondition =
				this.dataImportService.findMeasurementVariableByTermId(TermId.TRIAL_LOCATION.getId(), trialWorkbook.getConditions());
		trialWorkbook.getFactors().add(locationNameFromCondition.get());

		this.dataImportService.removeLocationNameVariableIfExists(trialWorkbook);

		Assert.assertFalse(
				this.dataImportService.findMeasurementVariableByTermId(TermId.TRIAL_LOCATION.getId(), trialWorkbook.getConditions())
						.isPresent());
		Assert.assertFalse(this.dataImportService.findMeasurementVariableByTermId(TermId.TRIAL_LOCATION.getId(), trialWorkbook.getFactors())
				.isPresent());

	}

	@Test
	public void testAssignLocationVariableWithUnspecifiedLocationIfEmptyVariableValueIsNotEmpty() {

		final Workbook trialWorkbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
						true);

		this.dataImportService.assignLocationVariableWithUnspecifiedLocationIfEmpty(trialWorkbook.getConditions());

		final Optional<MeasurementVariable> locationIdFromConditions =
				this.dataImportService.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), trialWorkbook.getConditions());
		Assert.assertEquals(String.valueOf(1), locationIdFromConditions.get().getValue());

	}

	@Test
	public void testAssignLocationVariableWithUnspecifiedLocationIfEmptyVariableValueIsEmpty() {

		final Workbook trialWorkbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
						true);

		final Optional<MeasurementVariable> locationIdFromConditions =
				this.dataImportService.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), trialWorkbook.getConditions());
		locationIdFromConditions.get().setValue("");

		this.dataImportService.assignLocationVariableWithUnspecifiedLocationIfEmpty(trialWorkbook.getConditions());

		Assert.assertEquals(String.valueOf(UNSPECIFIED_LOCATION_LOCID), locationIdFromConditions.get().getValue());

	}

	@Test
	public void testAssignLocationIdVariableToEnvironmentDetailSectionConditions() {

		final Workbook trialWorkbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
						true);

		this.dataImportService.assignLocationIdVariableToEnvironmentDetailSection(trialWorkbook);

		final Optional<MeasurementVariable> locationIdFromConditions =
				this.dataImportService.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), trialWorkbook.getConditions());

		Assert.assertTrue(locationIdFromConditions.isPresent());
		Assert.assertEquals(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0), locationIdFromConditions.get().getLabel());
		Assert.assertEquals(PhenotypicType.TRIAL_ENVIRONMENT, locationIdFromConditions.get().getRole());
		Assert.assertEquals(VariableType.ENVIRONMENT_DETAIL, locationIdFromConditions.get().getVariableType());

	}

	@Test
	public void testAssignLocationIdVariableToEnvironmentDetailSectionFactors() {

		final Workbook trialWorkbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
						true);

		final Optional<MeasurementVariable> locationIdFromConditions =
				this.dataImportService.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), trialWorkbook.getConditions());
		trialWorkbook.getFactors().add(locationIdFromConditions.get());
		trialWorkbook.getConditions().remove(locationIdFromConditions.get());

		this.dataImportService.assignLocationIdVariableToEnvironmentDetailSection(trialWorkbook);

		final Optional<MeasurementVariable> locationIdFromFactors =
				this.dataImportService.findMeasurementVariableByTermId(TermId.LOCATION_ID.getId(), trialWorkbook.getFactors());

		Assert.assertTrue(locationIdFromFactors.isPresent());
		Assert.assertEquals(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0), locationIdFromFactors.get().getLabel());
		Assert.assertEquals(PhenotypicType.TRIAL_ENVIRONMENT, locationIdFromFactors.get().getRole());
		Assert.assertEquals(VariableType.ENVIRONMENT_DETAIL, locationIdFromFactors.get().getVariableType());

	}

	@Test
	public void testAddExptDesignVariableIfNotExists() {
		final Workbook trialWorkbook = WorkbookTestDataInitializer
			.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
				true);

		List<MeasurementVariable> measurementVariables = new ArrayList<>();

		removeMeasurementVariableInList(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getConditions());
		removeMeasurementVariableInList(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getFactors());

		this.dataImportService.addExptDesignVariableIfNotExists(trialWorkbook, measurementVariables, PROGRAM_UUID);

		final Optional<MeasurementVariable> exptDesignVariable = this.dataImportService.findMeasurementVariableByTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), measurementVariables);
		Assert.assertTrue(exptDesignVariable.isPresent());

		measurementVariables = new ArrayList<>();
		trialWorkbook.getConditions().add(exptDesignVariable.get());
		Assert.assertFalse(this.dataImportService.findMeasurementVariableByTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), measurementVariables).isPresent());
	}

	@Test
	public void testGetExperimentalDesignIdValueNull() throws WorkbookParserException{
		Assert.assertEquals(String.valueOf(TermId.EXTERNALLY_GENERATED.getId()), this.dataImportService.getExperimentalDesignIdValue(""));
	}

	@Test
	public void testGetExperimentalDesignIdValueWithError() {
		final Term term = new Term(TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), "RCBD", "RCBD");
		try {
			this.dataImportService.getExperimentalDesignIdValue(term.getName());
			Assert.fail("Should Throw An Exception");
		} catch (WorkbookParserException e){
			Mockito.verify(this.termDataManager).getTermByName(term.getName());
		}
	}

	@Test
	public void processExperimentalDesignNotExisting() throws WorkbookParserException{
		final Workbook trialWorkbook = WorkbookTestDataInitializer
			.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
				true);

		removeMeasurementVariableInList(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getConditions());
		removeMeasurementVariableInList(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getFactors());

		this.dataImportService.processExperimentalDesign(trialWorkbook, PROGRAM_UUID, null);
		final MeasurementVariable exptDesignVariable = this.dataImportService.findMeasurementVariableByTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getConditions()).get();
		Assert.assertEquals(String.valueOf(TermId.EXTERNALLY_GENERATED.getId()), exptDesignVariable.getValue());
	}

	@Test
	public void processExperimentalDesignPresentInConditions() throws WorkbookParserException{
		final Term term = new Term(TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), "RCBD", "RCBD");
		Mockito.when(this.termDataManager.getTermByName(term.getName())).thenReturn(term);
		final Workbook trialWorkbook = WorkbookTestDataInitializer
			.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
				true);
		removeMeasurementVariableInList(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getFactors());
		this.dataImportService.addExptDesignVariableIfNotExists(trialWorkbook, trialWorkbook.getConditions(), PROGRAM_UUID);

		MeasurementVariable exptDesignVariable = this.dataImportService.findMeasurementVariableByTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getConditions()).get();
		exptDesignVariable.setValue(null);

		this.dataImportService.processExperimentalDesign(trialWorkbook, PROGRAM_UUID, term.getName());
		exptDesignVariable = this.dataImportService.findMeasurementVariableByTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getConditions()).get();
		Assert.assertEquals(String.valueOf(term.getId()), exptDesignVariable.getValue());
	}

	@Test
	public void processExperimentalDesignPresentInFactors() throws WorkbookParserException{
		final Term term = new Term(TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), "RCBD", "RCBD");
		Mockito.when(this.termDataManager.getTermByName(term.getName())).thenReturn(term);
		final Workbook trialWorkbook = WorkbookTestDataInitializer
			.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, new StudyTypeDto("T"), STUDY_NAME, TRIAL_NO,
				true);
		removeMeasurementVariableInList(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getConditions());
		this.dataImportService.addExptDesignVariableIfNotExists(trialWorkbook, trialWorkbook.getFactors(), PROGRAM_UUID);

		MeasurementVariable exptDesignVariable = this.dataImportService.findMeasurementVariableByTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getFactors()).get();
		exptDesignVariable.setValue(null);

		this.dataImportService.processExperimentalDesign(trialWorkbook, PROGRAM_UUID, term.getName());
		exptDesignVariable = this.dataImportService.findMeasurementVariableByTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), trialWorkbook.getConditions()).get();
		Assert.assertEquals(String.valueOf(term.getId()), exptDesignVariable.getValue());
	}

	@Test
	public void testGetExperimentalDesignIdValueWithNoError() {
		final Term term = new Term(TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), "RCBD", "RCBD");
		Mockito.when(this.termDataManager.getTermByName(term.getName())).thenReturn(term);
		try {
			this.dataImportService.getExperimentalDesignIdValue(term.getName());
			Mockito.verify(this.termDataManager).getTermByName(term.getName());
		} catch (WorkbookParserException e){
			Assert.fail("Should NOT Throw An Exception");
		}
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

		testWorkbook.setFactors(new ArrayList<>(Arrays.asList(
				new MeasurementVariable(TEST_VARIABLE_NAME, "", TEST_SCALE_NAME, TEST_METHOD_NAME, TEST_PROPERTY_NAME, "", "", ""))));
		testWorkbook.setConditions(new ArrayList<>(Arrays.asList(
				new MeasurementVariable(TEST_VARIABLE_NAME, "", TEST_SCALE_NAME, TEST_METHOD_NAME, TEST_PROPERTY_NAME, "", "", ""))));
		testWorkbook.setConstants(new ArrayList<>(Arrays.asList(
				new MeasurementVariable(TEST_VARIABLE_NAME, "", TEST_SCALE_NAME, TEST_METHOD_NAME, TEST_PROPERTY_NAME, "", "", ""))));
		testWorkbook.setVariates(new ArrayList<>(Arrays.asList(
				new MeasurementVariable(TEST_VARIABLE_NAME, "", TEST_SCALE_NAME, TEST_METHOD_NAME, TEST_PROPERTY_NAME, "", "", ""))));

		final List<MeasurementRow> observations = new ArrayList<>();
		final List<MeasurementData> dataList = new ArrayList<>();
		final MeasurementData variateCategorical = new MeasurementData(TEST_VARIABLE_NAME, withOutOfBoundsData ? "7" : "6");
		dataList.add(variateCategorical);
		final MeasurementRow row = new MeasurementRow();

		row.setDataList(dataList);
		observations.add(row);

		testWorkbook.setObservations(observations);

		final StudyDetails studyDetails = new StudyDetails();
		final StudyTypeDto studyTypeDto = new StudyTypeDto();
		studyTypeDto.setId(6);
		studyDetails.setStudyType(studyTypeDto);
		testWorkbook.setStudyDetails(studyDetails);

		return testWorkbook;

	}

	private List<MeasurementVariable> initializeTestMeasurementVariables() {
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
		final List<MeasurementVariable> measurementVariables = new ArrayList<>();

		for (int i = 0; i < DataImportServiceImplTest.STRINGS_WITH_INVALID_CHARACTERS.length; i++) {
			final MeasurementVariable mv = new MeasurementVariable();
			mv.setName(DataImportServiceImplTest.STRINGS_WITH_INVALID_CHARACTERS[i]);
			measurementVariables.add(mv);
		}
		return measurementVariables;
	}

	private List<MeasurementVariable> getValidNamedMeasurementVariables() {
		final List<MeasurementVariable> measurementVariables = new ArrayList<>();

		for (int i = 0; i < DataImportServiceImplTest.STRINGS_WITH_VALID_CHARACTERS.length; i++) {
			final MeasurementVariable mv = new MeasurementVariable();
			mv.setName(DataImportServiceImplTest.STRINGS_WITH_VALID_CHARACTERS[i]);
			measurementVariables.add(mv);
		}
		return measurementVariables;
	}

	private void removeMeasurementVariableInList(final int termId, final List<MeasurementVariable> measurementVariables) {
		final Iterator<MeasurementVariable> iterator = measurementVariables.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getTermId() == termId) {
				iterator.remove();
			}
		}

	}

	private GermplasmSearchRequest getGermplasmSearchRequest(Set<Integer> gids) {
		GermplasmSearchRequest searchRequest = new GermplasmSearchRequest();
		searchRequest.setGids(new ArrayList<>(gids));

		return searchRequest;
	}

}
