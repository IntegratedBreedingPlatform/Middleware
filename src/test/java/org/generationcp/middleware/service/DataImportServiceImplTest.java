package org.generationcp.middleware.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Optional;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
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

	public static final int INVALID_VARIABLES_COUNT = 5;
	public static final int VALID_VARIABLES_COUNT = 5;
	private static final String STUDY_NAME = "Study 1";
	private static final int TRIAL_NO = 1;
	private static final boolean IS_MULTIPLE_LOCATION = false;
	@Mock
	private WorkbookParser parser;

	@Mock
	private OntologyDataManager ontology;

	@Mock
	private File file;

	private Workbook workbook;

	@InjectMocks
	private DataImportServiceImpl dataImportService;

	public static final String[] STRINGS_WITH_INVALID_CHARACTERS = new String[] {"1234", "word@", "_+world=", "!!world!!", "&&&"};
	public static final String[] STRINGS_WITH_VALID_CHARACTERS =
			new String[] {"i_am_groot", "hello123world", "%%bangbang", "something_something", "zawaruldoisbig"};
	private static final String PROGRAM_UUID = "123456789";

	@Before
	public void init() {

		this.workbook = WorkbookTestDataInitializer
				.createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, StudyType.N, STUDY_NAME, TRIAL_NO,
						IS_MULTIPLE_LOCATION);

		Mockito.when(ontology.getStandardVariableIdByPropertyScaleMethod(WorkbookTestDataInitializer.GERMPLASM_ENTRY,
				WorkbookTestDataInitializer.NUMBER, WorkbookTestDataInitializer.ENUMERATED)).thenReturn(TermId.ENTRY_NO.getId());
		Mockito.when(ontology.getStandardVariableIdByPropertyScaleMethod(WorkbookTestDataInitializer.GERMPLASM_ID,
				WorkbookTestDataInitializer.DBID, WorkbookTestDataInitializer.ASSIGNED)).thenReturn(TermId.GID.getId());
		Mockito.when(ontology.getStandardVariableIdByPropertyScaleMethod(WorkbookTestDataInitializer.FIELD_PLOT,
				WorkbookTestDataInitializer.NESTED_NUMBER, WorkbookTestDataInitializer.ENUMERATED)).thenReturn(TermId.PLOT_NO.getId());

	}

	@Test
	public void testStrictParseWorkbookWithGreaterThan32VarNames() throws Exception {

		// Add variables with long names
		this.workbook.getAllVariables().addAll(this.initializeTestMeasurementVariables());

		try {
			dataImportService.strictParseWorkbook(this.file, this.parser, workbook, this.ontology, DataImportServiceImplTest.PROGRAM_UUID);
			Assert.fail("We expect workbookParserException to be thrown");
		} catch (WorkbookParserException e) {

			final String[] errorTypes = {DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_LENGTH,
					DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_CHARACTERS};
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
	public void testIsTermExistsTrue() {

		Assert.assertTrue("The entry_no is in the factors list, so it should return true.",
				this.dataImportService.isTermExists(TermId.ENTRY_NO.getId(), this.workbook.getFactors(), ontology));

	}

	@Test
	public void testIsTermExistsEmptyVariableList() {

		Assert.assertFalse("There are no variables in the list so it should return false.",
				this.dataImportService.isTermExists(TermId.ENTRY_NO.getId(), new ArrayList<MeasurementVariable>(), ontology));

	}

	@Test
	public void testIsTermExistsVariableDoesntExistInOntology() {

		Mockito.when(ontology.getStandardVariableIdByPropertyScaleMethod(WorkbookTestDataInitializer.GERMPLASM_ENTRY,
				WorkbookTestDataInitializer.NUMBER, WorkbookTestDataInitializer.ENUMERATED)).thenReturn(null);

		Assert.assertFalse("The entry_no variable is not found in the ontology, so it should return false",
				this.dataImportService.isTermExists(TermId.ENTRY_NO.getId(), this.workbook.getFactors(), ontology));

	}

	@Test
	public void testFindMeasurementVariableByTermIdMeasurementVariableIsFound() {

		Optional<MeasurementVariable> result =
				this.dataImportService.findMeasurementVariableByTermId(TermId.ENTRY_NO.getId(), ontology, this.workbook.getFactors());

		Assert.assertTrue("Measurement variable is found, so the value is present", result.isPresent());
		Assert.assertNotNull(result.get());
		Assert.assertEquals(TermId.ENTRY_NO.getId(), result.get().getTermId());
	}

	@Test
	public void testFindMeasurementVariableByTermIdMeasurementVariableIsNotFound() {

		Optional<MeasurementVariable> result = this.dataImportService
				.findMeasurementVariableByTermId(TermId.BREEDING_METHOD_CODE.getId(), ontology, this.workbook.getFactors());

		Assert.assertFalse("No measurement variable found, so the value is not present", result.isPresent());

	}

	@Test
	public void testResetRequiredField() {

		this.dataImportService.resetRequiredField(TermId.ENTRY_NO.getId(), ontology, this.workbook.getFactors());

		Optional<MeasurementVariable> result =
				this.dataImportService.findMeasurementVariableByTermId(TermId.ENTRY_NO.getId(), ontology, this.workbook.getFactors());

		if (result.isPresent()) {
			Assert.assertEquals(TermId.ENTRY_NO.getId(), result.get().getTermId());
			Assert.assertTrue("The variable's required field should be set to true", result.get().isRequired());
		} else {
			Assert.fail("The variable entry_no should be found because it exists in the list");
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

}
