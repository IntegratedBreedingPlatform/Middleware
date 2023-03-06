/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class DataImportServiceImplITTest extends IntegrationTestBase {

	public static final int CURRENT_IBDB_USER_ID = 1;
	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private FieldbookService fieldbookService;

	// TODO need setup
	private GeolocationDao geolocationDao;

	private static final String PROGRAM_UUID = "123456789";
	private final String cropPrefix = "ABCD";
	private CropType cropType;
	private GermplasmDAO germplasmDAO;

	private LocationDAO locationDAO;

	@Before
	public void setUp() {
		this.geolocationDao = new GeolocationDao(this.sessionProvder.getSession());

		this.cropType = new CropType();
		this.cropType.setPlotCodePrefix(this.cropPrefix);

		this.germplasmDAO = new GermplasmDAO(this.sessionProvder.getSession());
		this.locationDAO = new LocationDAO(this.sessionProvder.getSession());
	}

	@Ignore
	@Test
	public void testSaveMultiLocationDataset() {
		final List<Workbook> workbooks = WorkbookTestDataInitializer.getTestWorkbooks(5, 10);
		int id = 0;
		for (final Workbook workbook : workbooks) {
			// comment these out if you want to let the system generate the
			// dataset names.
			final int randomNumber = new Random().nextInt(10000);
			workbook.getStudyDetails().setTrialDatasetName(
					"MultiLocationStudy_" + workbook.getStudyDetails().getStudyName() + randomNumber);
			workbook.getStudyDetails().setMeasurementDatasetName(
					"MultiLocationMeasurement_" + workbook.getStudyDetails().getStudyName() + randomNumber);

			id = this.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplITTest.PROGRAM_UUID,
					this.cropType);
		}
		final String name = workbooks.get(0).getStudyDetails() != null
				? workbooks.get(0).getStudyDetails().getStudyName() : null;
		Debug.println(IntegrationTestBase.INDENT, "Created study: " + id + ", name = " + name);
	}
	@Ignore
	@Test
	public void testSaveStudyDataset() {
		Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(10, new StudyTypeDto("T"));

		final int id = this.dataImportService.saveDataset(workbook, true, false,
				DataImportServiceImplITTest.PROGRAM_UUID, this.cropType);

		final Workbook createdWorkbook = this.fieldbookService.getStudyDataSet(id);

		workbook = WorkbookTestDataInitializer.getTestWorkbook(10, new StudyTypeDto("T"));

		Assert.assertEquals(
				"Expected " + workbook.getTrialConditions().size() + " of records for study conditions but got "
						+ createdWorkbook.getTrialConditions().size(),
				workbook.getTrialConditions().size(), createdWorkbook.getTrialConditions().size());
		Assert.assertTrue("Expected the same study conditions retrieved but found a different condition.",
				WorkbookTestDataInitializer.areStudyVariablesSame(workbook.getTrialConditions(),
						createdWorkbook.getTrialConditions()));
		Assert.assertEquals(
				"Expected " + workbook.getTrialConstants().size() + " of records for study constants but got "
						+ createdWorkbook.getTrialConstants().size(),
				workbook.getTrialConstants().size(), createdWorkbook.getTrialConstants().size());
		Assert.assertTrue("Expected the same study constants retrieved but found a different constant.",
				WorkbookTestDataInitializer.areStudyVariablesSame(workbook.getTrialConstants(),
						createdWorkbook.getTrialConstants()));
	}

	@Ignore
	@Test
	public void testSaveNurseryDataset() {
		Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(10, new StudyTypeDto("N"));

		final int id = this.dataImportService.saveDataset(workbook, true, false,
				DataImportServiceImplITTest.PROGRAM_UUID, this.cropType);

		final Workbook createdWorkbook = this.fieldbookService.getStudyDataSet(id);

		workbook = WorkbookTestDataInitializer.getTestWorkbook(10, new StudyTypeDto("T"));

		Assert.assertEquals(
				"Expected " + workbook.getTrialConditions().size() + " of records for study conditions but got "
						+ createdWorkbook.getTrialConditions().size(),
				workbook.getTrialConditions().size(), createdWorkbook.getTrialConditions().size());
		Assert.assertTrue("Expected the same study conditions retrieved but found a different condition.",
				WorkbookTestDataInitializer.areStudyVariablesSame(workbook.getTrialConditions(),
						createdWorkbook.getTrialConditions()));
		Assert.assertEquals(
				"Expected " + workbook.getTrialConstants().size() + " of records for study constants but got "
						+ createdWorkbook.getTrialConstants().size(),
				workbook.getTrialConstants().size(), createdWorkbook.getTrialConstants().size());
		Assert.assertTrue("Expected the same study constants retrieved but found a different constant.",
				WorkbookTestDataInitializer.areStudyVariablesSame(workbook.getTrialConstants(),
						createdWorkbook.getTrialConstants()));
	}

	@Ignore
	@Test
	public void testAddStudyEnvironmentToStudy() {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(4, new StudyTypeDto("T"));

		final int id = this.dataImportService.saveDataset(workbook, true, false,
				DataImportServiceImplITTest.PROGRAM_UUID, this.cropType);

		Workbook createdWorkbook = this.fieldbookService.getStudyDataSet(id);

		final int noOfOrigTrialInstances = createdWorkbook.getTrialObservations().size();

		WorkbookTestDataInitializer.addNewEnvironment(createdWorkbook);

		this.dataImportService.saveDataset(createdWorkbook, true, false, DataImportServiceImplITTest.PROGRAM_UUID,
				this.cropType);

		createdWorkbook = this.fieldbookService.getStudyDataSet(id);

		Assert.assertEquals(
			"Expected " + (noOfOrigTrialInstances + 1) + " instances but got " + createdWorkbook.getTrialObservations().size()
				+ " instead.", noOfOrigTrialInstances + 1, createdWorkbook.getTrialObservations().size());
	}

	@Ignore
	@Test
	public void testDeletionOfExperimentPropAndStockProp() {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(10, new StudyTypeDto("N"));

		final int id = this.dataImportService.saveDataset(workbook, DataImportServiceImplITTest.PROGRAM_UUID,
				this.cropType);

		Workbook createdWorkbook = this.fieldbookService.getStudyDataSet(id);

		WorkbookTestDataInitializer.deleteExperimentPropVar(createdWorkbook);

		this.dataImportService.saveDataset(createdWorkbook, DataImportServiceImplITTest.PROGRAM_UUID, this.cropType);

		createdWorkbook = this.fieldbookService.getStudyDataSet(id);

		Assert.assertNotNull("Expected successful retrieval of workbook.", createdWorkbook);
	}

	@Ignore
	@Test
	public void testParseWorkbook() throws WorkbookParserException {
		// Dan V : changed implem so that template path is located in
		// src/test/resources. no need to change per user to reflect file
		// location

		final String fileLocation = Objects.requireNonNull(this.getClass().getClassLoader().getResource("ricetest2.xls")).getFile();
		final File file = new File(fileLocation);
		final Workbook workbook = this.dataImportService.parseWorkbook(file, CURRENT_IBDB_USER_ID);
		workbook.print(IntegrationTestBase.INDENT);

		final int id = this.dataImportService.saveDataset(workbook, DataImportServiceImplITTest.PROGRAM_UUID,
				this.cropType);
		Debug.println(IntegrationTestBase.INDENT, "Created study:" + id);
	}

	@Ignore
	@Test
	public void testParseWorkbookDescriptionSheet() throws WorkbookParserException {
		final String fileLocation = Objects.requireNonNull(this.getClass().getClassLoader().getResource("ricetest2.xls")).getFile();
		final File file = new File(fileLocation);
		final WorkbookParser parser = new WorkbookParser(WorkbookParser.DEFAULT_MAX_ROW_LIMIT);
		final org.apache.poi.ss.usermodel.Workbook excelWorkbook = parser.loadFileToExcelWorkbook(file);
		final Workbook workbook = this.dataImportService.parseWorkbookDescriptionSheet(excelWorkbook, CURRENT_IBDB_USER_ID);
		Assert.assertNotNull(workbook.getConditions());
		Assert.assertNotNull(workbook.getConstants());
		Assert.assertNotNull(workbook.getFactors());
		Assert.assertNotNull(workbook.getVariates());
		Assert.assertNull(workbook.getObservations());
	}

	// Daniel V
	// added new tests to cover validation scenarios for strict parsing of
	// workbook

	@Ignore
	@Test
	public void testParseWorkbookWrongDescriptionSheet() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5808WrongDescriptionSheetName.xls",
				"error.missing.sheet.description", "Unable to detect wrong description sheet");
	}

	@Ignore
	@Test
	public void testParseWorkbookWrongObservationSheet() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5808WrongObservationSheetName.xls",
				"error.missing.sheet.observation", "Unable to detect wrong observation sheet");
	}

	@Ignore
	@Test
	public void testParseWorkbookWithEntryWrongPMS() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5800ScenarioWithEntryWrongPSM.xls",
				"error.entry.doesnt.exist", "Unable to detect invalid entry");
	}

	@Ignore
	@Test
	public void testParseWorkbookNoEntry() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5800ScenarioNoEntry.xls", "error.entry.doesnt.exist",
				"Unable to detect invalid entry");
	}

	@Ignore
	@Test
	public void testParseWorkbookWithEntryWrongCategory() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5800ScenarioWithEntryWrongCategory.xls",
				"error.entry.doesnt.exist", "Unable to detect invalid entry");
	}

	@Ignore
	@Test
	public void testParseWorkbookWithNoStudyNonNursery() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5799NonNurseryWorkbookNoStudyEnvironment.xls",
				"error.missing.study.condition", "Unable to detect missing trial condition");
	}

	@Ignore
	@Test
	public void testParseWorkbookWithNoStudyNursery() {
		final String fileLocation = Objects.requireNonNull(this.getClass().getClassLoader()
			.getResource("org/generationcp/middleware/service/test/GCP5799NurseryWorkbookNoStudyEnvironment.xls"))
				.getFile();
		final File file = new File(fileLocation);
		try {
			this.dataImportService.strictParseWorkbook(file, DataImportServiceImplITTest.PROGRAM_UUID, CURRENT_IBDB_USER_ID);
		} catch (final WorkbookParserException e) {
			Assert.fail("Unable to correctly parse Nursery workbook with no trial condition");
		} catch (final MiddlewareException e) {
			Assert.fail("Unexpected exception : " + e.getMessage());
		}
	}

	@Ignore
	@Test
	public void testParseWorkbookWithEmptyFields() {
		final String fileLocation = Objects.requireNonNull(
			this.getClass().getClassLoader().getResource("org/generationcp/middleware/service/test/GCP5802SevenFieldsMissing.xls")).getFile();
		final File file = new File(fileLocation);
		try {
			this.dataImportService.strictParseWorkbook(file, DataImportServiceImplITTest.PROGRAM_UUID, CURRENT_IBDB_USER_ID);
		} catch (final WorkbookParserException e) {

			final List<Message> messages = e.getErrorMessages();

			Assert.assertNotNull(messages);
			// There should be 7 error messages to correspond with the 7 missing
			// fields in the file
			Assert.assertSame(7, messages.size());

			return;
		} catch (final MiddlewareException e) {
			Assert.fail("Unexpected exception : " + e.getMessage());
		}

		Assert.fail("Unable to detect empty fields");
	}

	protected void testFileAgainstExpectedErrorCondition(final String qualifiedFilename, final String expectedErrorKey,
			final String errorMessage) {
		final String fileLocation = Objects.requireNonNull(this.getClass().getClassLoader().getResource(qualifiedFilename)).getFile();
		try {
			final File file = new File(fileLocation);
			this.dataImportService.strictParseWorkbook(file, DataImportServiceImplITTest.PROGRAM_UUID, CURRENT_IBDB_USER_ID);
		} catch (final WorkbookParserException e) {
			final List<Message> messages = e.getErrorMessages();

			Assert.assertNotNull(messages);
			Assert.assertEquals(1, messages.size());
			Assert.assertEquals(expectedErrorKey, messages.get(0).getMessageKey());
			return;
		} catch (final MiddlewareException e) {
			Assert.fail("Unexpected exception : " + e.getMessage());
		}

		Assert.fail(errorMessage);
	}

	@Ignore
	@Test
	public void testCheckIfProjectNameIsExisting() throws Exception {
		// try to save first then use the name of the saved study
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook();
		workbook.print(IntegrationTestBase.INDENT);
		this.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplITTest.PROGRAM_UUID,
				this.cropType);
		String name = workbook.getStudyDetails() != null ? workbook.getStudyDetails().getStudyName() : null;
		Debug.println(IntegrationTestBase.INDENT, "Name: " + name);
		boolean isExisting = this.dataImportService.checkIfProjectNameIsExistingInProgram(name,
				DataImportServiceImplITTest.PROGRAM_UUID);
		Assert.assertTrue(isExisting);

		name = "SHOULDNOTEXISTSTUDY";
		Debug.println(IntegrationTestBase.INDENT, "Name: " + name);
		isExisting = this.dataImportService.checkIfProjectNameIsExistingInProgram(name,
				DataImportServiceImplITTest.PROGRAM_UUID);
		Assert.assertFalse(isExisting);
	}

	@Ignore
	@Test
	public void getLocationIdByProjectNameAndDescription() {
		// try to save first then use the name of the saved study
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook();
		workbook.print(IntegrationTestBase.INDENT);
		this.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplITTest.PROGRAM_UUID,
				this.cropType);
		final String name = workbook.getStudyDetails().getStudyName();
		Debug.println(IntegrationTestBase.INDENT, "Name: " + name);
		final Integer locationId = this.dataImportService.getLocationIdByProjectNameAndDescriptionAndProgramUUID(name,
				"1", DataImportServiceImplITTest.PROGRAM_UUID);
		Assert.assertNotNull(locationId);
		final Geolocation geolocation = this.geolocationDao.getById(locationId);
		Assert.assertNotNull(geolocation);
		Assert.assertEquals(locationId, geolocation.getLocationId());
		Assert.assertEquals("1", geolocation.getDescription());
	}

	@Ignore
	@Test
	public void testSaveProjectOntology() {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook();
		workbook.print(IntegrationTestBase.INDENT);
		final int id = this.dataImportService.saveProjectOntology(workbook, DataImportServiceImplITTest.PROGRAM_UUID,
				this.cropType);
		Debug.println(IntegrationTestBase.INDENT,
				"Created study:" + id + ", name = " + workbook.getStudyDetails().getStudyName());

	}

	@Ignore
	@Test
	public void testSaveProjectData() {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook();
		workbook.print(IntegrationTestBase.INDENT);
		final int studyId = this.dataImportService.saveProjectOntology(workbook,
				DataImportServiceImplITTest.PROGRAM_UUID, this.cropType);
		workbook.getStudyDetails().setId(studyId);
		workbook.setTrialDatasetId(studyId - 1);
		workbook.setMeasurementDatesetId(studyId - 2);
		this.dataImportService.saveProjectData(workbook, DataImportServiceImplITTest.PROGRAM_UUID, this.cropType);
		Debug.println(IntegrationTestBase.INDENT,
				"Saved project data:" + studyId + ", name = " + workbook.getStudyDetails().getStudyName());

	}

	@Ignore
	@Test
	public void testValidateProjectOntology() {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbookWithErrors();
		workbook.print(IntegrationTestBase.INDENT);
		final Map<String, List<Message>> errors = this.dataImportService.validateProjectOntology(workbook,
				DataImportServiceImplITTest.PROGRAM_UUID);
		Assert.assertNotNull(errors);
		Debug.println(IntegrationTestBase.INDENT, "Errors Identified: ");
		for (final Map.Entry<String, List<Message>> e : errors.entrySet()) {
			Debug.println(IntegrationTestBase.INDENT + 2, e.getKey());
			for (final Message m : e.getValue()) {
				if (m.getMessageParams() != null) {
					Debug.println(IntegrationTestBase.INDENT + 4,
							"Key: " + m.getMessageKey() + " Params: " + Arrays.asList(m.getMessageParams()));
				} else {
					Debug.println(IntegrationTestBase.INDENT + 4, "Key: " + m.getMessageKey());
				}
			}
		}
	}

	@Ignore
	@Test
	public void testValidateProjectData() {
		final String studyName = "validateProjectData_" + new Random().nextInt(10000);
		final int studyNo = 1;
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbookForWizard(studyName, studyNo);
		workbook.print(IntegrationTestBase.INDENT);
		this.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplITTest.PROGRAM_UUID,
				this.cropType);
		final Map<String, List<Message>> errors = this.dataImportService.validateProjectData(workbook,
				DataImportServiceImplITTest.PROGRAM_UUID);
		Assert.assertNotNull(errors);
		Debug.println(IntegrationTestBase.INDENT, "Errors Identified: ");
		for (final Map.Entry<String, List<Message>> e : errors.entrySet()) {
			Debug.println(IntegrationTestBase.INDENT + 2, e.getKey());
			for (final Message m : e.getValue()) {
				if (m.getMessageParams() != null) {
					Debug.println(IntegrationTestBase.INDENT + 4,
							"Key: " + m.getMessageKey() + " Params: " + Arrays.asList(m.getMessageParams()));
				} else {
					Debug.println(IntegrationTestBase.INDENT + 4, "Key: " + m.getMessageKey());
				}
			}
		}
	}

	@Test
	public void testValidateProjectDataWithError() {
		final String studyName = "validateProjectData_" + new Random().nextInt(10000);
		final int studyNo = 1;
		final WorkbookTestDataInitializer testData = new WorkbookTestDataInitializer();
		testData.setGermplasmDao(this.germplasmDAO);

		for(final Location location : testData.createLocationData()) {
			this.locationDAO.saveOrUpdate(location);
		}

		final Workbook workbook = testData.setUpWorkbook(studyName, studyNo);
		workbook.print(IntegrationTestBase.INDENT);
		this.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplITTest.PROGRAM_UUID,
				this.cropType);

		final Workbook workbook2 = testData.setUpWorkbook(studyName, studyNo);
		workbook.print(IntegrationTestBase.INDENT);

		final Map<String, List<Message>> errors = this.dataImportService.validateProjectData(workbook,
				DataImportServiceImplITTest.PROGRAM_UUID);
		Assert.assertFalse(errors.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "Errors Identified: ");
		for (final Map.Entry<String, List<Message>> e : errors.entrySet()) {
			Debug.println(IntegrationTestBase.INDENT + 2, e.getKey());
			for (final Message m : e.getValue()) {
				Debug.println(IntegrationTestBase.INDENT + 4,
						"Key: " + m.getMessageKey() + " Params: " + Arrays.asList(m.getMessageParams()));
				Assert.assertEquals(m.getMessageKey(), "error.duplicate.trial.instance");
				Assert.assertTrue(Arrays.asList(m.getMessageParams()).contains("1"));
			}
		}

	}
}
