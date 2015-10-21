
package org.generationcp.middleware.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
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

	public static final int INVALID_VARIABLES_COUNT = 5;
	public static final int VALID_VARIABLES_COUNT = 5;
	@Mock
	private WorkbookParser parser;

	@Mock
	private Workbook workbook;

	@Mock
	private OntologyDataManager ontology;

	@Mock
	private File file;

	@Mock
	protected HibernateSessionProvider sessionProvider;

	@Mock
	private ServiceDaoFactory serviceDaoFactory;

	@Mock
	private LocationDAO locationDAO;

	@Mock
	private PersonDAO personDAO;

	@Mock
	private GermplasmDAO germplasmDAO;

	@InjectMocks
	private DataImportServiceImpl dataImportService = new DataImportServiceImpl();

	public static final String[] STRINGS_WITH_INVALID_CHARACTERS = new String[] {"1234", "word@", "_+world=", "!!world!!", "&&&"};
	public static final String[] STRINGS_WITH_VALID_CHARACTERS = new String[] {"i_am_groot", "hello123world", "%%bangbang",
			"something_something", "zawaruldoisbig"};
	private static final String PROGRAM_UUID = "123456789";

	private static final List<String> PI_IDS_TEST_DATA = Arrays.asList("1", "2", "3", "4", "", "5", null, "6", "7", "8");
	private static final List<String> COOPERATOR_IDS_TEST_DATA = Arrays.asList("11", "12", "13", "", null, "14", null, "15", "16", "17");
	private static final List<String> LOCATION_IDS_TEST_DATA = Arrays.asList("21", "22", "23", "24", "", "", null, "25", "26", "27");
	private static final List<String> GERMPLASM_IDS_TEST_DATA = Arrays.asList("", "31", "32", "", "33", "34", null, "35", "36", "37");

	private static final String MISSING_LOCATION_IDS_ERROR_KEY = "import.missing.location.ids";
	private static final String MISSING_PERSON_IDS_ERROR_KEY = "import.missing.person.ids";
	private static final String MISSING_GERMPLASM_IDS_ERROR_KEY = "import.missing.germplasm.ids";

	private static final String TRIAL_INSTANCE = "Trial instance";
	private static final String NUMBER = "Number";
	private static final String ENUMERATED = "Enumerated";
	private static final String GERMPLASM_ENTRY = "Germplasm entry";
	private static final String FIELD_PLOT = "Field plot";
	private static final String PERSON = "Person";
	private static final String PERSON_ID = "Person id";
	private static final String CONDUCTED = "Conducted";
	private static final String LOCATION = "Location";
	private static final String LOCATION_ID = "Location id";
	private static final String ASSIGNED = "Assigned";
	private static final String GERMPLASM_ID = "Germplasm id";

	@Before
	public void setup() {

		Mockito.when(this.serviceDaoFactory.getPersonDAO()).thenReturn(this.personDAO);
		Mockito.when(this.serviceDaoFactory.getLocationDAO()).thenReturn(this.locationDAO);
		Mockito.when(this.serviceDaoFactory.getGermplasmDAO()).thenReturn(this.germplasmDAO);

	}

	@Test
	public void testStrictParseWorkbookWithGreaterThan32VarNames() throws Exception {
		DataImportServiceImpl moleDataImportService = Mockito.spy(this.dataImportService);

		// we just need to test if isTrialInstanceNumberExists works, so lets mock out other dataImportService calls for the moment
		Mockito.when(this.workbook.isNursery()).thenReturn(true);

		// tip! do note that spy-ed object still calls the real method, may
		// cause changing internal state as side effect
		Mockito.when(
				moleDataImportService.isEntryExists(this.ontology,
						this.workbook.getFactors())).thenReturn(true);
		Mockito.when(
				moleDataImportService.isPlotExists(this.ontology,
						this.workbook.getFactors())).thenReturn(true);
		Mockito.when(
				moleDataImportService.isTrialInstanceNumberExists(
						this.ontology, this.workbook.getTrialVariables()))
				.thenReturn(true);

		Mockito.when(this.workbook.getAllVariables()).thenReturn(
				this.initializeTestMeasurementVariables());

		try {
			moleDataImportService.strictParseWorkbook(this.file, this.parser, this.workbook, this.ontology,
					DataImportServiceImplTest.PROGRAM_UUID);
			Assert.fail("We expects workbookParserException to be thrown");
		} catch (WorkbookParserException e) {

			Mockito.verify(moleDataImportService).validateMeasurementVariableName(this.workbook.getAllVariables());

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

	@Test
	public void testCheckForInvalidRecordsOfControlledVariables() {
		Workbook workbookWithControlledVariables = this.createWorkbookWithControlledVariablesTestData();

		// test where all non empty controlled variables are VALID values
		List<String> personIds = new ArrayList<>(PI_IDS_TEST_DATA);
		personIds.addAll(COOPERATOR_IDS_TEST_DATA);
		List<Integer> existingPersonIds = this.getAllNumericValues(personIds);

		Mockito.doReturn(existingPersonIds).when(this.personDAO).getExistingPersonIds(Mockito.anyList());
		Mockito.doReturn(this.getAllNumericValues(LOCATION_IDS_TEST_DATA)).when(this.locationDAO).getExistingLocationIds(Mockito.anyList(),
				Mockito.anyString());
		Mockito.doReturn(this.getAllNumericValues(GERMPLASM_IDS_TEST_DATA)).when(this.germplasmDAO).getExistingGIDs(Mockito.anyList());

		List<Message> returnVal =
				this.dataImportService.checkForInvalidRecordsOfControlledVariables(workbookWithControlledVariables, PROGRAM_UUID);

		Assert.assertTrue(returnVal.isEmpty());
	}

	private List<Integer> getAllNumericValues(List<String> list) {
		List<Integer> numericValues = new ArrayList<>();
		for (String value : list) {
			if (!StringUtils.isEmpty(value) && NumberUtils.isNumber(value)) {
				numericValues.add(Integer.parseInt(value));
			}
		}
		return numericValues;
	}

	private Workbook createWorkbookWithControlledVariablesTestData() {
		Workbook workbookWithControlledVariables = new Workbook();
		workbookWithControlledVariables.setFactors(this.createFactorsWithControlledVariablesTestData());
		workbookWithControlledVariables.setObservations(this.createObservationsWithControlledVariablesTestData());
		return workbookWithControlledVariables;
	}

	private List<MeasurementVariable> createFactorsWithControlledVariablesTestData() {
		List<MeasurementVariable> factors = new ArrayList<>();
		// required variables
		factors.add(this.createMeasurementVariableTestData(TermId.TRIAL_INSTANCE_FACTOR.getId(), TermId.TRIAL_INSTANCE_FACTOR.toString()));
		factors.add(this.createMeasurementVariableTestData(TermId.ENTRY_NO.getId(), TermId.ENTRY_NO.toString()));
		factors.add(this.createMeasurementVariableTestData(TermId.PLOT_NO.getId(), TermId.PLOT_NO.toString()));
		// controlled variables
		factors.add(this.createMeasurementVariableTestData(TermId.PI_ID.getId(), TermId.PI_ID.toString()));
		factors.add(this.createMeasurementVariableTestData(TermId.COOPERATOOR_ID.getId(), TermId.COOPERATOOR_ID.toString()));
		factors.add(this.createMeasurementVariableTestData(TermId.LOCATION_ID.getId(), TermId.LOCATION_ID.toString()));
		factors.add(this.createMeasurementVariableTestData(TermId.GID.getId(), TermId.GID.toString()));
		return factors;
	}

	private MeasurementVariable createMeasurementVariableTestData(int termId, String name) {
		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(termId);
		measurementVariable.setName(name);
		return measurementVariable;
	}

	private List<MeasurementRow> createObservationsWithControlledVariablesTestData() {
		List<MeasurementRow> measurementRows = new ArrayList<>();
		for (int rowNum = 0; rowNum < 10; rowNum++) {
			measurementRows.add(this.createMeasurementRowsTestData(rowNum));
		}
		return measurementRows;
	}

	private MeasurementRow createMeasurementRowsTestData(int rowNum) {
		MeasurementRow measurementRow = new MeasurementRow();
		String sampleNumber = Integer.toString(rowNum);
		measurementRow.setDataList(this.createDataListTestData(sampleNumber, sampleNumber, PI_IDS_TEST_DATA.get(rowNum),
				COOPERATOR_IDS_TEST_DATA.get(rowNum), LOCATION_IDS_TEST_DATA.get(rowNum), GERMPLASM_IDS_TEST_DATA.get(rowNum)));
		return measurementRow;
	}

	private List<MeasurementData> createDataListTestData(String entryNo, String plotNo, String piId, String cooperatorId, String locationId,
			String gid) {
		List<MeasurementData> measurementData = new ArrayList<>();
		measurementData.add(this.createMeasurementDataTestData(TermId.TRIAL_INSTANCE_FACTOR.toString(), "1"));
		measurementData.add(this.createMeasurementDataTestData(TermId.ENTRY_NO.toString(), entryNo));
		measurementData.add(this.createMeasurementDataTestData(TermId.PLOT_NO.toString(), plotNo));
		measurementData.add(this.createMeasurementDataTestData(TermId.PI_ID.toString(), piId));
		measurementData.add(this.createMeasurementDataTestData(TermId.COOPERATOOR_ID.toString(), cooperatorId));
		measurementData.add(this.createMeasurementDataTestData(TermId.LOCATION_ID.toString(), locationId));
		measurementData.add(this.createMeasurementDataTestData(TermId.GID.toString(), gid));
		return measurementData;
	}

	private MeasurementData createMeasurementDataTestData(String label, String value) {
		MeasurementData measurementData = new MeasurementData();
		measurementData.setLabel(label);
		measurementData.setValue(value);
		return measurementData;
	}

	@Test
	public void testCheckForInvalidRecordsOfControlledVariablesWithMissingValues() {
		Workbook workbookWithControlledVariables = this.createWorkbookWithControlledVariablesTestData();

		// test where all non empty controlled variables are INVALID values (MISSING)
		List<Integer> emptyList = new ArrayList<>();

		Mockito.doReturn(emptyList).when(this.personDAO).getExistingPersonIds(Mockito.anyList());
		Mockito.doReturn(emptyList).when(this.locationDAO).getExistingLocationIds(Mockito.anyList(), Mockito.anyString());
		Mockito.doReturn(emptyList).when(this.germplasmDAO).getExistingGIDs(Mockito.anyList());

		List<Message> returnVal =
				this.dataImportService.checkForInvalidRecordsOfControlledVariables(workbookWithControlledVariables, PROGRAM_UUID);

		Assert.assertFalse(returnVal.isEmpty());
		for (Message message : returnVal) {
			if (MISSING_LOCATION_IDS_ERROR_KEY.equals(message.getMessageKey())) {
				List<String> nonEmptyLocationIds = this.getAllNonEmptyValues(LOCATION_IDS_TEST_DATA);
				Assert.assertEquals(StringUtils.join(new TreeSet<>(nonEmptyLocationIds), ", "),
						message.getMessageParams()[0]);
			} else if (MISSING_PERSON_IDS_ERROR_KEY.equals(message.getMessageKey())) {
				List<String> nonEmptyPiIds = this.getAllNonEmptyValues(PI_IDS_TEST_DATA);
				List<String> nonEmptyCooperatorIds = this.getAllNonEmptyValues(COOPERATOR_IDS_TEST_DATA);
				Set<String> nonEmptyPersonIds = new TreeSet<>(nonEmptyPiIds);
				nonEmptyPersonIds.addAll(nonEmptyCooperatorIds);
				Assert.assertEquals(StringUtils.join(nonEmptyPersonIds, ", "), message.getMessageParams()[0]);
			} else if (MISSING_GERMPLASM_IDS_ERROR_KEY.equals(message.getMessageKey())) {
				List<String> nonEmptyGermplasmIds = this.getAllNonEmptyValues(GERMPLASM_IDS_TEST_DATA);
				Assert.assertEquals(StringUtils.join(new TreeSet<>(nonEmptyGermplasmIds), ", "),
						message.getMessageParams()[0]);
			} else {
				Assert.fail("We're only expecting errors related to missing location ids, person ids and germplasm ids");
			}

		}
	}

	private List<String> getAllNonEmptyValues(List<String> list) {
		List<String> nonEmptyValues = new ArrayList<>();
		for (String value : list) {
			if (!StringUtils.isEmpty(value)) {
				nonEmptyValues.add(value);
			}
		}
		return nonEmptyValues;
	}

	@Test
	public void testDiscardMissingRecords() {
		// test data
		Map<String, Set<String>> invalidValuesMap = this.createInvalidValuesMapTestData();
		Map<String, Set<String>> validValuesMap = this.createValidValuesMapTestData();
		this.createValidValuesMapTestData();
		Workbook workbookWithControlledVariables = this.createWorkbookWithControlledVariablesTestData();
		workbookWithControlledVariables.setInvalidValuesMap(invalidValuesMap);
		// call method to test
		this.dataImportService.discardMissingRecords(workbookWithControlledVariables);
		// assert statements
		Map<String, Integer> controlledVariablesMap =
				this.dataImportService.retrieveControlledVariablesMap(workbookWithControlledVariables);
		for (MeasurementRow measurementRow : workbookWithControlledVariables.getObservations()) {
			for (MeasurementData measurementData : measurementRow.getDataList()) {
				Integer variableId = controlledVariablesMap.get(measurementData.getLabel());
				if (variableId != null && (variableId == TermId.PI_ID.getId() || variableId == TermId.COOPERATOOR_ID.getId())) {
					if (!StringUtils.isEmpty(measurementData.getValue())) {
						Assert.assertTrue(validValuesMap.get(DataImportServiceImpl.PERSON_ID_VALUES).contains(measurementData.getValue()));
						Assert.assertFalse(
								invalidValuesMap.get(DataImportServiceImpl.PERSON_ID_VALUES).contains(measurementData.getValue()));
					}
				} else if (variableId != null && variableId == TermId.LOCATION_ID.getId()) {
					if (!StringUtils.isEmpty(measurementData.getValue())) {
						Assert.assertTrue(
								validValuesMap.get(DataImportServiceImpl.LOCATION_ID_VALUES).contains(measurementData.getValue()));
						Assert.assertFalse(
								invalidValuesMap.get(DataImportServiceImpl.LOCATION_ID_VALUES).contains(measurementData.getValue()));
					}
				} else if (variableId != null && variableId == TermId.GID.getId()) {
					if (!StringUtils.isEmpty(measurementData.getValue())) {
						Assert.assertTrue(
								validValuesMap.get(DataImportServiceImpl.GERMPLASM_ID_VALUES).contains(measurementData.getValue()));
						Assert.assertFalse(
								invalidValuesMap.get(DataImportServiceImpl.GERMPLASM_ID_VALUES).contains(measurementData.getValue()));
					}
				}
			}
		}
	}

	private Map<String, Set<String>> createValidValuesMapTestData() {
		Map<String, Set<String>> validValuesMap = new HashMap<>();
		validValuesMap.put(DataImportServiceImpl.PERSON_ID_VALUES, this.createValidPersonIdsTestData());
		validValuesMap.put(DataImportServiceImpl.LOCATION_ID_VALUES, this.createValidLocationIdsTestData());
		validValuesMap.put(DataImportServiceImpl.GERMPLASM_ID_VALUES, this.createValidGermplasmIdsTestData());
		return validValuesMap;
	}

	private Set<String> createValidGermplasmIdsTestData() {
		Set<String> validValues = new HashSet<>();
		validValues.add(GERMPLASM_IDS_TEST_DATA.get(7));
		validValues.add(GERMPLASM_IDS_TEST_DATA.get(8));
		validValues.add(GERMPLASM_IDS_TEST_DATA.get(9));
		return validValues;
	}

	private Set<String> createValidLocationIdsTestData() {
		Set<String> validValues = new HashSet<>();
		validValues.add(LOCATION_IDS_TEST_DATA.get(7));
		validValues.add(LOCATION_IDS_TEST_DATA.get(8));
		validValues.add(LOCATION_IDS_TEST_DATA.get(9));
		return validValues;
	}

	private Set<String> createValidPersonIdsTestData() {
		Set<String> validValues = new HashSet<>();
		validValues.add(PI_IDS_TEST_DATA.get(7));
		validValues.add(PI_IDS_TEST_DATA.get(8));
		validValues.add(PI_IDS_TEST_DATA.get(9));
		validValues.add(COOPERATOR_IDS_TEST_DATA.get(7));
		validValues.add(COOPERATOR_IDS_TEST_DATA.get(8));
		validValues.add(COOPERATOR_IDS_TEST_DATA.get(9));
		return validValues;
	}

	private Map<String, Set<String>> createInvalidValuesMapTestData() {
		Map<String, Set<String>> invalidValuesMap = new HashMap<>();
		invalidValuesMap.put(DataImportServiceImpl.PERSON_ID_VALUES, this.createInvalidPersonIdsTestData());
		invalidValuesMap.put(DataImportServiceImpl.LOCATION_ID_VALUES, this.createInvalidLocationIdsTestData());
		invalidValuesMap.put(DataImportServiceImpl.GERMPLASM_ID_VALUES, this.createInvalidGermplasmIdsTestData());
		return invalidValuesMap;
	}

	private Set<String> createInvalidPersonIdsTestData() {
		Set<String> invalidValues = new HashSet<>();
		invalidValues.add(PI_IDS_TEST_DATA.get(0));
		invalidValues.add(PI_IDS_TEST_DATA.get(1));
		invalidValues.add(PI_IDS_TEST_DATA.get(2));
		invalidValues.add(PI_IDS_TEST_DATA.get(3));
		invalidValues.add(PI_IDS_TEST_DATA.get(5));
		invalidValues.add(COOPERATOR_IDS_TEST_DATA.get(0));
		invalidValues.add(COOPERATOR_IDS_TEST_DATA.get(1));
		invalidValues.add(COOPERATOR_IDS_TEST_DATA.get(2));
		invalidValues.add(COOPERATOR_IDS_TEST_DATA.get(5));
		return invalidValues;
	}

	private Set<String> createInvalidLocationIdsTestData() {
		Set<String> invalidValues = new HashSet<>();
		invalidValues.add(LOCATION_IDS_TEST_DATA.get(0));
		invalidValues.add(LOCATION_IDS_TEST_DATA.get(1));
		invalidValues.add(LOCATION_IDS_TEST_DATA.get(2));
		invalidValues.add(LOCATION_IDS_TEST_DATA.get(3));
		return invalidValues;
	}

	private Set<String> createInvalidGermplasmIdsTestData() {
		Set<String> invalidValues = new HashSet<>();
		invalidValues.add(GERMPLASM_IDS_TEST_DATA.get(1));
		invalidValues.add(GERMPLASM_IDS_TEST_DATA.get(2));
		invalidValues.add(GERMPLASM_IDS_TEST_DATA.get(4));
		invalidValues.add(GERMPLASM_IDS_TEST_DATA.get(5));
		return invalidValues;
	}

	@Test
	public void testCheckForInvalidRecordsOfControlledVariablesFromAutoImport() {
		Workbook workbookWithControlledVariables = this.createWorkbookWithControlledVariablesWithoutIdTestData();
		this.setupGetStandardVariableIdByPropertyScaleMethodMocks();

		// test where all non empty controlled variables are VALID values
		List<String> personIds = new ArrayList<>(PI_IDS_TEST_DATA);
		personIds.addAll(COOPERATOR_IDS_TEST_DATA);
		List<Integer> existingPersonIds = this.getAllNumericValues(personIds);

		Mockito.doReturn(existingPersonIds).when(this.personDAO).getExistingPersonIds(Mockito.anyList());
		Mockito.doReturn(this.getAllNumericValues(LOCATION_IDS_TEST_DATA)).when(this.locationDAO).getExistingLocationIds(Mockito.anyList(),
				Mockito.anyString());
		Mockito.doReturn(this.getAllNumericValues(GERMPLASM_IDS_TEST_DATA)).when(this.germplasmDAO).getExistingGIDs(Mockito.anyList());

		List<Message> returnVal =
				this.dataImportService.checkForInvalidRecordsOfControlledVariables(workbookWithControlledVariables, PROGRAM_UUID);

		Assert.assertTrue(returnVal.isEmpty());
	}

	@Test
	public void testCheckForInvalidRecordsOfControlledVariablesWithMissingValuesFromAutoImport() {
		Workbook workbookWithControlledVariables = this.createWorkbookWithControlledVariablesWithoutIdTestData();
		this.setupGetStandardVariableIdByPropertyScaleMethodMocks();

		// test where all non empty controlled variables are INVALID values (MISSING)
		List<Integer> emptyList = new ArrayList<>();

		Mockito.doReturn(emptyList).when(this.personDAO).getExistingPersonIds(Mockito.anyList());
		Mockito.doReturn(emptyList).when(this.locationDAO).getExistingLocationIds(Mockito.anyList(), Mockito.anyString());
		Mockito.doReturn(emptyList).when(this.germplasmDAO).getExistingGIDs(Mockito.anyList());

		List<Message> returnVal =
				this.dataImportService.checkForInvalidRecordsOfControlledVariables(workbookWithControlledVariables, PROGRAM_UUID);

		Assert.assertFalse(returnVal.isEmpty());
		for (Message message : returnVal) {
			if (MISSING_LOCATION_IDS_ERROR_KEY.equals(message.getMessageKey())) {
				List<String> nonEmptyLocationIds = this.getAllNonEmptyValues(LOCATION_IDS_TEST_DATA);
				Assert.assertEquals(StringUtils.join(new TreeSet<>(nonEmptyLocationIds), ", "), message.getMessageParams()[0]);
			} else if (MISSING_PERSON_IDS_ERROR_KEY.equals(message.getMessageKey())) {
				List<String> nonEmptyPiIds = this.getAllNonEmptyValues(PI_IDS_TEST_DATA);
				List<String> nonEmptyCooperatorIds = this.getAllNonEmptyValues(COOPERATOR_IDS_TEST_DATA);
				Set<String> nonEmptyPersonIds = new TreeSet<>(nonEmptyPiIds);
				nonEmptyPersonIds.addAll(nonEmptyCooperatorIds);
				Assert.assertEquals(StringUtils.join(nonEmptyPersonIds, ", "), message.getMessageParams()[0]);
			} else if (MISSING_GERMPLASM_IDS_ERROR_KEY.equals(message.getMessageKey())) {
				List<String> nonEmptyGermplasmIds = this.getAllNonEmptyValues(GERMPLASM_IDS_TEST_DATA);
				Assert.assertEquals(StringUtils.join(new TreeSet<>(nonEmptyGermplasmIds), ", "), message.getMessageParams()[0]);
			} else {
				Assert.fail("We're only expecting errors related to missing location ids, person ids and germplasm ids");
			}

		}
	}

	private void setupGetStandardVariableIdByPropertyScaleMethodMocks() {
		Mockito.doReturn(TermId.TRIAL_INSTANCE_FACTOR.getId()).when(this.ontology)
		.getStandardVariableIdByPropertyScaleMethod(TRIAL_INSTANCE, NUMBER, ENUMERATED);
		Mockito.doReturn(TermId.ENTRY_NO.getId()).when(this.ontology).getStandardVariableIdByPropertyScaleMethod(GERMPLASM_ENTRY, NUMBER,
				ENUMERATED);
		Mockito.doReturn(TermId.PLOT_NO.getId()).when(this.ontology).getStandardVariableIdByPropertyScaleMethod(FIELD_PLOT, NUMBER,
				ENUMERATED);
		Mockito.doReturn(TermId.PI_ID.getId()).when(this.ontology).getStandardVariableIdByPropertyScaleMethod(PERSON, PERSON_ID, ASSIGNED);
		Mockito.doReturn(TermId.COOPERATOOR_ID.getId()).when(this.ontology).getStandardVariableIdByPropertyScaleMethod(PERSON, PERSON_ID,
				CONDUCTED);
		Mockito.doReturn(TermId.LOCATION_ID.getId()).when(this.ontology).getStandardVariableIdByPropertyScaleMethod(LOCATION, LOCATION_ID,
				ASSIGNED);
		Mockito.doReturn(TermId.GID.getId()).when(this.ontology).getStandardVariableIdByPropertyScaleMethod(GERMPLASM_ID, GERMPLASM_ID,
				ASSIGNED);

	}

	private Workbook createWorkbookWithControlledVariablesWithoutIdTestData() {
		Workbook workbookWithControlledVariables = new Workbook();
		workbookWithControlledVariables.setFactors(this.createFactorsWithControlledVariablesWithoutIdTestData());
		workbookWithControlledVariables.setObservations(this.createObservationsWithControlledVariablesTestData());
		return workbookWithControlledVariables;
	}

	private List<MeasurementVariable> createFactorsWithControlledVariablesWithoutIdTestData() {
		List<MeasurementVariable> factors = new ArrayList<>();
		// required variables
		factors.add(this.createMeasurementVariableWithoutIdTestData(TermId.TRIAL_INSTANCE_FACTOR.toString(),TRIAL_INSTANCE,NUMBER,ENUMERATED));
		factors.add(this.createMeasurementVariableWithoutIdTestData(TermId.ENTRY_NO.toString(),GERMPLASM_ENTRY,NUMBER,ENUMERATED));
		factors.add(this.createMeasurementVariableWithoutIdTestData(TermId.PLOT_NO.toString(),FIELD_PLOT,NUMBER,ENUMERATED));
		// controlled variables
		factors.add(this.createMeasurementVariableWithoutIdTestData(TermId.PI_ID.toString(),PERSON,PERSON_ID,ASSIGNED));
		factors.add(this.createMeasurementVariableWithoutIdTestData(TermId.COOPERATOOR_ID.toString(),PERSON,PERSON_ID,CONDUCTED));
		factors.add(this.createMeasurementVariableWithoutIdTestData(TermId.LOCATION_ID.toString(),LOCATION,LOCATION_ID,ASSIGNED));
		factors.add(this.createMeasurementVariableWithoutIdTestData(TermId.GID.toString(),GERMPLASM_ID,GERMPLASM_ID,ASSIGNED));
		return factors;
	}

	private MeasurementVariable createMeasurementVariableWithoutIdTestData(String name, String property, String scale, String method) {
		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setName(name);
		measurementVariable.setProperty(property);
		measurementVariable.setScale(scale);
		measurementVariable.setMethod(method);
		return measurementVariable;
	}
}
