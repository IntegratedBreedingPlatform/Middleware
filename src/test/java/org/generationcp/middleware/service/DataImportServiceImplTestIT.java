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
import java.util.Random;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DataImportServiceImplTestIT extends IntegrationTestBase {

	public static final int CURRENT_IBDB_USER_ID = 1;
	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private FieldbookService fieldbookService;

	// TODO need setup
	private GeolocationDao geolocationDao;

	private static final String PROGRAM_UUID = "123456789";
	private final String cropPrefix = "ABCD";

	@Before
	public void setUp() {
		this.geolocationDao = new GeolocationDao();
		this.geolocationDao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testSaveMultiLocationDataset() throws MiddlewareQueryException {
		final List<Workbook> workbooks = WorkbookTestDataInitializer.getTestWorkbooks(5, 10);
		int id = 0;
		for (final Workbook workbook : workbooks) {
			// comment these out if you want to let the system generate the
			// dataset names.
			final int randomNumber = new Random().nextInt(10000);
			workbook.getStudyDetails().setTrialDatasetName(
					"MultiLocationTrial_" + workbook.getStudyDetails().getStudyName() + randomNumber);
			workbook.getStudyDetails().setMeasurementDatasetName(
					"MultiLocationMeasurement_" + workbook.getStudyDetails().getStudyName() + randomNumber);

			id = this.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID,
					this.cropPrefix);
		}
		final String name = workbooks.get(0).getStudyDetails() != null
				? workbooks.get(0).getStudyDetails().getStudyName() : null;
		Debug.println(IntegrationTestBase.INDENT, "Created study: " + id + ", name = " + name);
	}

	@Test
	public void testSaveTrialDataset() throws MiddlewareException {
		Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(10, StudyType.T);

		final int id = this.dataImportService.saveDataset(workbook, true, false,
				DataImportServiceImplTestIT.PROGRAM_UUID, this.cropPrefix);

		final Workbook createdWorkbook = this.fieldbookService.getTrialDataSet(id);

		workbook = WorkbookTestDataInitializer.getTestWorkbook(10, StudyType.T);

		Assert.assertEquals(
				"Expected " + workbook.getTrialConditions().size() + " of records for trial conditions but got "
						+ createdWorkbook.getTrialConditions().size(),
				workbook.getTrialConditions().size(), createdWorkbook.getTrialConditions().size());
		Assert.assertTrue("Expected the same trial conditions retrieved but found a different condition.",
				WorkbookTestDataInitializer.areTrialVariablesSame(workbook.getTrialConditions(),
						createdWorkbook.getTrialConditions()));
		Assert.assertEquals(
				"Expected " + workbook.getTrialConstants().size() + " of records for trial constants but got "
						+ createdWorkbook.getTrialConstants().size(),
				workbook.getTrialConstants().size(), createdWorkbook.getTrialConstants().size());
		Assert.assertTrue("Expected the same trial constants retrieved but found a different constant.",
				WorkbookTestDataInitializer.areTrialVariablesSame(workbook.getTrialConstants(),
						createdWorkbook.getTrialConstants()));
	}

	@Test
	public void testSaveNurseryDataset() throws MiddlewareException {
		Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(10, StudyType.N);

		final int id = this.dataImportService.saveDataset(workbook, true, false,
				DataImportServiceImplTestIT.PROGRAM_UUID, this.cropPrefix);

		final Workbook createdWorkbook = this.fieldbookService.getNurseryDataSet(id);

		workbook = WorkbookTestDataInitializer.getTestWorkbook(10, StudyType.T);

		Assert.assertEquals(
				"Expected " + workbook.getTrialConditions().size() + " of records for trial conditions but got "
						+ createdWorkbook.getTrialConditions().size(),
				workbook.getTrialConditions().size(), createdWorkbook.getTrialConditions().size());
		Assert.assertTrue("Expected the same trial conditions retrieved but found a different condition.",
				WorkbookTestDataInitializer.areTrialVariablesSame(workbook.getTrialConditions(),
						createdWorkbook.getTrialConditions()));
		Assert.assertEquals(
				"Expected " + workbook.getTrialConstants().size() + " of records for trial constants but got "
						+ createdWorkbook.getTrialConstants().size(),
				workbook.getTrialConstants().size(), createdWorkbook.getTrialConstants().size());
		Assert.assertTrue("Expected the same trial constants retrieved but found a different constant.",
				WorkbookTestDataInitializer.areTrialVariablesSame(workbook.getTrialConstants(),
						createdWorkbook.getTrialConstants()));
	}

	@Test
	public void testAddTrialEnvironmentToTrial() throws MiddlewareException {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(4, StudyType.T);

		final int id = this.dataImportService.saveDataset(workbook, true, false,
				DataImportServiceImplTestIT.PROGRAM_UUID, this.cropPrefix);

		Workbook createdWorkbook = this.fieldbookService.getTrialDataSet(id);

		final int noOfOrigTrialInstances = createdWorkbook.getTrialObservations().size();

		WorkbookTestDataInitializer.addNewEnvironment(createdWorkbook);

		this.dataImportService.saveDataset(createdWorkbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID,
				this.cropPrefix);

		createdWorkbook = this.fieldbookService.getTrialDataSet(id);

		Assert.assertTrue(
				"Expected " + (noOfOrigTrialInstances + 1) + " instances but got "
						+ createdWorkbook.getTrialObservations().size() + " instead.",
				noOfOrigTrialInstances + 1 == createdWorkbook.getTrialObservations().size());
	}

	@Test
	public void testDeletionOfExperimentPropAndStockProp() throws MiddlewareException {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(10, StudyType.N);

		final int id = this.dataImportService.saveDataset(workbook, DataImportServiceImplTestIT.PROGRAM_UUID,
				this.cropPrefix);

		Workbook createdWorkbook = this.fieldbookService.getNurseryDataSet(id);

		WorkbookTestDataInitializer.deleteExperimentPropVar(createdWorkbook);

		this.dataImportService.saveDataset(createdWorkbook, DataImportServiceImplTestIT.PROGRAM_UUID, this.cropPrefix);

		createdWorkbook = this.fieldbookService.getNurseryDataSet(id);

		Assert.assertNotNull("Expected successful retrieval of workbook.", createdWorkbook);
	}

	@Test
	public void testParseWorkbook() throws MiddlewareQueryException, WorkbookParserException {
		// Dan V : changed implem so that template path is located in
		// src/test/resources. no need to change per user to reflect file
		// location

		final String fileLocation = this.getClass().getClassLoader().getResource("ricetest2.xls").getFile();
		final File file = new File(fileLocation);
		final Workbook workbook = this.dataImportService.parseWorkbook(file, CURRENT_IBDB_USER_ID);
		workbook.print(IntegrationTestBase.INDENT);

		final int id = this.dataImportService.saveDataset(workbook, DataImportServiceImplTestIT.PROGRAM_UUID,
				this.cropPrefix);
		Debug.println(IntegrationTestBase.INDENT, "Created study:" + id);
	}

	@Test
	public void testParseWorkbookDescriptionSheet() throws WorkbookParserException {
		final String fileLocation = this.getClass().getClassLoader().getResource("ricetest2.xls").getFile();
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

	@Test
	public void testParseWorkbookWrongDescriptionSheet() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5808WrongDescriptionSheetName.xls",
				"error.missing.sheet.description", "Unable to detect wrong description sheet");
	}

	@Test
	public void testParseWorkbookWrongObservationSheet() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5808WrongObservationSheetName.xls",
				"error.missing.sheet.observation", "Unable to detect wrong observation sheet");
	}

	@Test
	public void testParseWorkbookWithEntryWrongPMS() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5800ScenarioWithEntryWrongPSM.xls",
				"error.entry.doesnt.exist", "Unable to detect invalid entry");
	}

	@Test
	public void testParseWorkbookNoEntry() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5800ScenarioNoEntry.xls", "error.entry.doesnt.exist",
				"Unable to detect invalid entry");
	}

	@Test
	public void testParseWorkbookWithEntryWrongCategory() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5800ScenarioWithEntryWrongCategory.xls",
				"error.entry.doesnt.exist", "Unable to detect invalid entry");
	}

	@Test
	public void testParseWorkbookWithNoTrialNonNursery() {
		this.testFileAgainstExpectedErrorCondition(
				"org/generationcp/middleware/service/test/GCP5799NonNurseryWorkbookNoTrialEnvironment.xls",
				"error.missing.trial.condition", "Unable to detect missing trial condition");
	}

	@Test
	public void testParseWorkbookWithNoTrialNursery() {
		final String fileLocation = this.getClass().getClassLoader()
				.getResource("org/generationcp/middleware/service/test/GCP5799NurseryWorkbookNoTrialEnvironment.xls")
				.getFile();
		final File file = new File(fileLocation);
		try {
			this.dataImportService.strictParseWorkbook(file, DataImportServiceImplTestIT.PROGRAM_UUID, CURRENT_IBDB_USER_ID);
		} catch (final WorkbookParserException e) {
			Assert.fail("Unable to correctly parse Nursery workbook with no trial condition");
		} catch (final MiddlewareException e) {
			Assert.fail("Unexpected exception : " + e.getMessage());
		}
	}

	@Test
	public void testParseWorkbookWithEmptyFields() {
		final String fileLocation = this.getClass().getClassLoader()
				.getResource("org/generationcp/middleware/service/test/GCP5802SevenFieldsMissing.xls").getFile();
		final File file = new File(fileLocation);
		try {
			this.dataImportService.strictParseWorkbook(file, DataImportServiceImplTestIT.PROGRAM_UUID, CURRENT_IBDB_USER_ID);
		} catch (final WorkbookParserException e) {

			final List<Message> messages = e.getErrorMessages();

			Assert.assertNotNull(messages);
			// There should be 7 error messages to correspond with the 7 missing
			// fields in the file
			Assert.assertSame(messages.size(), 7);

			return;
		} catch (final MiddlewareException e) {
			Assert.fail("Unexpected exception : " + e.getMessage());
		}

		Assert.fail("Unable to detect empty fields");
	}

	protected void testFileAgainstExpectedErrorCondition(final String qualifiedFilename, final String expectedErrorKey,
			final String errorMessage) {
		final String fileLocation = this.getClass().getClassLoader().getResource(qualifiedFilename).getFile();
		try {
			final File file = new File(fileLocation);
			this.dataImportService.strictParseWorkbook(file, DataImportServiceImplTestIT.PROGRAM_UUID, CURRENT_IBDB_USER_ID);
		} catch (final WorkbookParserException e) {
			final List<Message> messages = e.getErrorMessages();

			Assert.assertNotNull(messages);
			Assert.assertTrue(messages.size() == 1);
			Assert.assertEquals(expectedErrorKey, messages.get(0).getMessageKey());
			return;
		} catch (final MiddlewareException e) {
			Assert.fail("Unexpected exception : " + e.getMessage());
		}

		Assert.fail(errorMessage);
	}

	@Test
	public void testCheckIfProjectNameIsExisting() throws Exception {
		// try to save first then use the name of the saved study
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook();
		workbook.print(IntegrationTestBase.INDENT);
		this.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID,
				this.cropPrefix);
		String name = workbook.getStudyDetails() != null ? workbook.getStudyDetails().getStudyName() : null;
		Debug.println(IntegrationTestBase.INDENT, "Name: " + name);
		boolean isExisting = this.dataImportService.checkIfProjectNameIsExistingInProgram(name,
				DataImportServiceImplTestIT.PROGRAM_UUID);
		Assert.assertTrue(isExisting);

		name = "SHOULDNOTEXISTSTUDY";
		Debug.println(IntegrationTestBase.INDENT, "Name: " + name);
		isExisting = this.dataImportService.checkIfProjectNameIsExistingInProgram(name,
				DataImportServiceImplTestIT.PROGRAM_UUID);
		Assert.assertFalse(isExisting);
	}

	@Test
	public void getLocationIdByProjectNameAndDescription() throws MiddlewareQueryException {
		// try to save first then use the name of the saved study
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook();
		workbook.print(IntegrationTestBase.INDENT);
		this.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID,
				this.cropPrefix);
		final String name = workbook.getStudyDetails().getStudyName();
		Debug.println(IntegrationTestBase.INDENT, "Name: " + name);
		final Integer locationId = this.dataImportService.getLocationIdByProjectNameAndDescriptionAndProgramUUID(name,
				"1", DataImportServiceImplTestIT.PROGRAM_UUID);
		Assert.assertNotNull(locationId);
		final Geolocation geolocation = this.geolocationDao.getById(locationId);
		Assert.assertNotNull(geolocation);
		Assert.assertEquals(locationId, geolocation.getLocationId());
		Assert.assertEquals("1", geolocation.getDescription());
	}

	@Test
	public void testSaveProjectOntology() throws MiddlewareQueryException {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook();
		workbook.print(IntegrationTestBase.INDENT);
		final int id = this.dataImportService.saveProjectOntology(workbook, DataImportServiceImplTestIT.PROGRAM_UUID,
				this.cropPrefix);
		Debug.println(IntegrationTestBase.INDENT,
				"Created study:" + id + ", name = " + workbook.getStudyDetails().getStudyName());

	}

	@Test
	public void testSaveProjectData() throws MiddlewareQueryException {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook();
		workbook.print(IntegrationTestBase.INDENT);
		final int studyId = this.dataImportService.saveProjectOntology(workbook,
				DataImportServiceImplTestIT.PROGRAM_UUID, this.cropPrefix);
		workbook.getStudyDetails().setId(studyId);
		workbook.setTrialDatasetId(studyId - 1);
		workbook.setMeasurementDatesetId(studyId - 2);
		this.dataImportService.saveProjectData(workbook, DataImportServiceImplTestIT.PROGRAM_UUID, this.cropPrefix);
		Debug.println(IntegrationTestBase.INDENT,
				"Saved project data:" + studyId + ", name = " + workbook.getStudyDetails().getStudyName());

	}

	@Test
	public void testValidateProjectOntology() throws MiddlewareException {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbookWithErrors();
		workbook.print(IntegrationTestBase.INDENT);
		final Map<String, List<Message>> errors = this.dataImportService.validateProjectOntology(workbook,
				DataImportServiceImplTestIT.PROGRAM_UUID);
		Assert.assertNotNull(errors);
		if (errors != null) {
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
	}

	@Test
	public void testValidateProjectData() throws MiddlewareException {
		final String studyName = "validateProjectData_" + new Random().nextInt(10000);
		final int trialNo = 1;
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbookForWizard(studyName, trialNo);
		workbook.print(IntegrationTestBase.INDENT);
		this.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID,
				this.cropPrefix);
		final Map<String, List<Message>> errors = this.dataImportService.validateProjectData(workbook,
				DataImportServiceImplTestIT.PROGRAM_UUID);
		Assert.assertNotNull(errors);
		if (errors != null) {
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
	}
}
