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

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.etl.WorkbookTest;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.utils.test.Debug;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DataImportServiceImplTestIT extends DataManagerIntegrationTest {

	private static DataImportService dataImportService;

	private static FieldbookService fieldbookService;

	private static GeolocationDao geolocationDao;

	private static final String PROGRAM_UUID = "123456789";

	@BeforeClass
	public static void setUp() throws Exception {
		DataImportServiceImplTestIT.dataImportService = DataManagerIntegrationTest.managerFactory.getDataImportService();
		DataImportServiceImplTestIT.fieldbookService = DataManagerIntegrationTest.managerFactory.getFieldbookMiddlewareService();
		DataImportServiceImplTestIT.geolocationDao = new GeolocationDao();
		Session session = MiddlewareIntegrationTest.sessionUtil.getCurrentSession();
		DataImportServiceImplTestIT.geolocationDao.setSession(session);
	}

	@Test
	public void testSaveMultiLocationDataset() throws MiddlewareQueryException {
		List<Workbook> workbooks = WorkbookTest.getTestWorkbooks(5, 10);
		int id = 0;
		for (Workbook workbook : workbooks) {
			// comment these out if you want to let the system generate the dataset names.
			int randomNumber = new Random().nextInt(10000);
			workbook.getStudyDetails()
					.setTrialDatasetName("MultiLocationTrial_" + workbook.getStudyDetails().getStudyName() + randomNumber);
			workbook.getStudyDetails().setMeasurementDatasetName(
					"MultiLocationMeasurement_" + workbook.getStudyDetails().getStudyName() + randomNumber);

			id = DataImportServiceImplTestIT.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID);
		}
		String name = workbooks.get(0).getStudyDetails() != null ? workbooks.get(0).getStudyDetails().getStudyName() : null;
		Debug.println(MiddlewareIntegrationTest.INDENT, "Created study: " + id + ", name = " + name);
	}

	@Test
	public void testSaveTrialDataset() throws MiddlewareException {
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);

		int id = DataImportServiceImplTestIT.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID);

		Workbook createdWorkbook = DataImportServiceImplTestIT.fieldbookService.getTrialDataSet(id);

		WorkbookTest.setTestWorkbook(null);
		workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);

		Assert.assertEquals("Expected " + workbook.getTrialConditions().size() + " of records for trial conditions but got "
				+ createdWorkbook.getTrialConditions().size(), workbook.getTrialConditions().size(), createdWorkbook.getTrialConditions()
				.size());
		Assert.assertTrue("Expected the same trial conditions retrieved but found a different condition.",
				WorkbookTest.areTrialVariablesSame(workbook.getTrialConditions(), createdWorkbook.getTrialConditions()));
		Assert.assertEquals("Expected " + workbook.getTrialConstants().size() + " of records for trial constants but got "
				+ createdWorkbook.getTrialConstants().size(), workbook.getTrialConstants().size(), createdWorkbook.getTrialConstants()
				.size());
		Assert.assertTrue("Expected the same trial constants retrieved but found a different constant.",
				WorkbookTest.areTrialVariablesSame(workbook.getTrialConstants(), createdWorkbook.getTrialConstants()));
	}

	@Test
	public void testSaveNurseryDataset() throws MiddlewareException {
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.N);

		int id = DataImportServiceImplTestIT.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID);

		Workbook createdWorkbook = DataImportServiceImplTestIT.fieldbookService.getNurseryDataSet(id);

		WorkbookTest.setTestWorkbook(null);
		workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);

		Assert.assertEquals("Expected " + workbook.getTrialConditions().size() + " of records for trial conditions but got "
				+ createdWorkbook.getTrialConditions().size(), workbook.getTrialConditions().size(), createdWorkbook.getTrialConditions()
				.size());
		Assert.assertTrue("Expected the same trial conditions retrieved but found a different condition.",
				WorkbookTest.areTrialVariablesSame(workbook.getTrialConditions(), createdWorkbook.getTrialConditions()));
		Assert.assertEquals("Expected " + workbook.getTrialConstants().size() + " of records for trial constants but got "
				+ createdWorkbook.getTrialConstants().size(), workbook.getTrialConstants().size(), createdWorkbook.getTrialConstants()
				.size());
		Assert.assertTrue("Expected the same trial constants retrieved but found a different constant.",
				WorkbookTest.areTrialVariablesSame(workbook.getTrialConstants(), createdWorkbook.getTrialConstants()));
	}

	@Test
	public void testAddTrialEnvironmentToTrial() throws MiddlewareException {
		Workbook workbook = WorkbookTest.getTestWorkbook(4, StudyType.T);

		int id = DataImportServiceImplTestIT.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID);

		Workbook createdWorkbook = DataImportServiceImplTestIT.fieldbookService.getTrialDataSet(id);

		int noOfOrigTrialInstances = createdWorkbook.getTrialObservations().size();

		WorkbookTest.addNewEnvironment(createdWorkbook);

		DataImportServiceImplTestIT.dataImportService.saveDataset(createdWorkbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID);

		createdWorkbook = DataImportServiceImplTestIT.fieldbookService.getTrialDataSet(id);

		Assert.assertTrue("Expected " + (noOfOrigTrialInstances + 1) + " instances but got "
				+ createdWorkbook.getTrialObservations().size() + " instead.", noOfOrigTrialInstances + 1 == createdWorkbook
				.getTrialObservations().size());
	}

	@Test
	public void testDeletionOfExperimentPropAndStockProp() throws MiddlewareException {
		WorkbookTest.setTestWorkbook(null);
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.N);

		int id = DataImportServiceImplTestIT.dataImportService.saveDataset(workbook, DataImportServiceImplTestIT.PROGRAM_UUID);

		Workbook createdWorkbook = DataImportServiceImplTestIT.fieldbookService.getNurseryDataSet(id);

		WorkbookTest.deleteExperimentPropVar(createdWorkbook);

		DataImportServiceImplTestIT.dataImportService.saveDataset(createdWorkbook, DataImportServiceImplTestIT.PROGRAM_UUID);

		createdWorkbook = DataImportServiceImplTestIT.fieldbookService.getNurseryDataSet(id);

		Assert.assertNotNull("Expected successful retrieval of workbook.", createdWorkbook);
	}

	@Test
	public void testParseWorkbook() throws MiddlewareQueryException, WorkbookParserException {
		// Dan V : changed implem so that template path is located in src/test/resources. no need to change per user to reflect file
		// location

		String fileLocation = this.getClass().getClassLoader().getResource("ricetest2.xls").getFile();
		File file = new File(fileLocation);
		Workbook workbook = DataImportServiceImplTestIT.dataImportService.parseWorkbook(file);
		workbook.print(MiddlewareIntegrationTest.INDENT);

		int id = DataImportServiceImplTestIT.dataImportService.saveDataset(workbook, DataImportServiceImplTestIT.PROGRAM_UUID);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Created study:" + id);
	}

	// Daniel V
	// added new tests to cover validation scenarios for strict parsing of workbook

	@Test
	public void testParseWorkbookWrongDescriptionSheet() {
		this.testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5808WrongDescriptionSheetName.xls",
				"error.missing.sheet.description", "Unable to detect wrong description sheet");
	}

	@Test
	public void testParseWorkbookWrongObservationSheet() {
		this.testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5808WrongObservationSheetName.xls",
				"error.missing.sheet.observation", "Unable to detect wrong observation sheet");
	}

	@Test
	public void testParseWorkbookWithEntryWrongPMS() {
		this.testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5800ScenarioWithEntryWrongPSM.xls",
				"error.entry.doesnt.exist", "Unable to detect invalid entry");
	}

	@Test
	public void testParseWorkbookNoEntry() {
		this.testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5800ScenarioNoEntry.xls",
				"error.entry.doesnt.exist", "Unable to detect invalid entry");
	}

	@Test
	public void testParseWorkbookWithEntryWrongCategory() {
		this.testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5800ScenarioWithEntryWrongCategory.xls",
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
		String fileLocation =
				this.getClass().getClassLoader()
						.getResource("org/generationcp/middleware/service/test/GCP5799NurseryWorkbookNoTrialEnvironment.xls").getFile();
		File file = new File(fileLocation);
		try {
			DataImportServiceImplTestIT.dataImportService.strictParseWorkbook(file, DataImportServiceImplTestIT.PROGRAM_UUID);
		} catch (WorkbookParserException e) {
			Assert.fail("Unable to correctly parse Nursery workbook with no trial condition");
		} catch (MiddlewareException e) {
			Assert.fail("Unexpected exception : " + e.getMessage());
		}
	}

	@Test
	public void testParseWorkbookWithEmptyFields() {
		String fileLocation =
				this.getClass().getClassLoader().getResource("org/generationcp/middleware/service/test/GCP5802SevenFieldsMissing.xls")
						.getFile();
		File file = new File(fileLocation);
		try {
			DataImportServiceImplTestIT.dataImportService.strictParseWorkbook(file, DataImportServiceImplTestIT.PROGRAM_UUID);
		} catch (WorkbookParserException e) {

			List<Message> messages = e.getErrorMessages();

			Assert.assertNotNull(messages);
			// There should be 7 error messages to correspond with the 7 missing fields in the file
			Assert.assertSame(messages.size(), 7);

			return;
		} catch (MiddlewareException e) {
			Assert.fail("Unexpected exception : " + e.getMessage());
		}

		Assert.fail("Unable to detect empty fields");
	}

	protected void testFileAgainstExpectedErrorCondition(String qualifiedFilename, String expectedErrorKey, String errorMessage) {
		String fileLocation = this.getClass().getClassLoader().getResource(qualifiedFilename).getFile();
		try {
			File file = new File(fileLocation);
			DataImportServiceImplTestIT.dataImportService.strictParseWorkbook(file, DataImportServiceImplTestIT.PROGRAM_UUID);
		} catch (WorkbookParserException e) {
			List<Message> messages = e.getErrorMessages();

			Assert.assertNotNull(messages);
			Assert.assertTrue(messages.size() == 1);
			Assert.assertEquals(expectedErrorKey, messages.get(0).getMessageKey());
			return;
		} catch (MiddlewareException e) {
			Assert.fail("Unexpected exception : " + e.getMessage());
		}

		Assert.fail(errorMessage);
	}

	@Test
	public void testCheckIfProjectNameIsExisting() throws Exception {
		// try to save first then use the name of the saved study
		Workbook workbook = WorkbookTest.getTestWorkbook();
		workbook.print(MiddlewareIntegrationTest.INDENT);
		DataImportServiceImplTestIT.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID);
		String name = workbook.getStudyDetails() != null ? workbook.getStudyDetails().getStudyName() : null;
		Debug.println(MiddlewareIntegrationTest.INDENT, "Name: " + name);
		boolean isExisting =
				DataImportServiceImplTestIT.dataImportService.checkIfProjectNameIsExistingInProgram(name,
						DataImportServiceImplTestIT.PROGRAM_UUID);
		Assert.assertTrue(isExisting);

		name = "SHOULDNOTEXISTSTUDY";
		Debug.println(MiddlewareIntegrationTest.INDENT, "Name: " + name);
		isExisting =
				DataImportServiceImplTestIT.dataImportService.checkIfProjectNameIsExistingInProgram(name,
						DataImportServiceImplTestIT.PROGRAM_UUID);
		Assert.assertFalse(isExisting);
	}

	@Test
	public void getLocationIdByProjectNameAndDescription() throws MiddlewareQueryException {
		// try to save first then use the name of the saved study
		Workbook workbook = WorkbookTest.getTestWorkbook();
		workbook.print(MiddlewareIntegrationTest.INDENT);
		DataImportServiceImplTestIT.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID);
		String name = workbook.getStudyDetails().getStudyName();
		Debug.println(MiddlewareIntegrationTest.INDENT, "Name: " + name);
		Integer locationId =
				DataImportServiceImplTestIT.dataImportService.getLocationIdByProjectNameAndDescriptionAndProgramUUID(name, "1",
						DataImportServiceImplTestIT.PROGRAM_UUID);
		Assert.assertNotNull(locationId);
		Geolocation geolocation = DataImportServiceImplTestIT.geolocationDao.getById(locationId);
		Assert.assertNotNull(geolocation);
		Assert.assertEquals(locationId, geolocation.getLocationId());
		Assert.assertEquals("1", geolocation.getDescription());
	}

	@Test
	public void testSaveProjectOntology() throws MiddlewareQueryException {
		Workbook workbook = WorkbookTest.getTestWorkbook();
		workbook.print(MiddlewareIntegrationTest.INDENT);
		int id = DataImportServiceImplTestIT.dataImportService.saveProjectOntology(workbook, DataImportServiceImplTestIT.PROGRAM_UUID);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Created study:" + id + ", name = " + workbook.getStudyDetails().getStudyName());

	}

	@Test
	public void testSaveProjectData() throws MiddlewareQueryException {
		Workbook workbook = WorkbookTest.getTestWorkbook();
		workbook.print(MiddlewareIntegrationTest.INDENT);
		int studyId = DataImportServiceImplTestIT.dataImportService.saveProjectOntology(workbook, DataImportServiceImplTestIT.PROGRAM_UUID);
		workbook.setStudyId(studyId);
		workbook.setTrialDatasetId(studyId - 1);
		workbook.setMeasurementDatesetId(studyId - 2);
		DataImportServiceImplTestIT.dataImportService.saveProjectData(workbook, DataImportServiceImplTestIT.PROGRAM_UUID);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Saved project data:" + studyId + ", name = "
				+ workbook.getStudyDetails().getStudyName());

	}

	@Test
	public void testValidateProjectOntology() throws MiddlewareException {
		Workbook workbook = WorkbookTest.getTestWorkbookWithErrors();
		workbook.print(MiddlewareIntegrationTest.INDENT);
		Map<String, List<Message>> errors = DataImportServiceImplTestIT.dataImportService.
				validateProjectOntology(workbook,PROGRAM_UUID);
		Assert.assertNotNull(errors);
		if (errors != null) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "Errors Identified: ");
			for (Map.Entry<String, List<Message>> e : errors.entrySet()) {
				Debug.println(MiddlewareIntegrationTest.INDENT + 2, e.getKey());
				for (Message m : e.getValue()) {
					if (m.getMessageParams() != null) {
						Debug.println(MiddlewareIntegrationTest.INDENT + 4,
								"Key: " + m.getMessageKey() + " Params: " + Arrays.asList(m.getMessageParams()));
					} else {
						Debug.println(MiddlewareIntegrationTest.INDENT + 4, "Key: " + m.getMessageKey());
					}
				}
			}
		}
	}

	@Test
	public void testValidateProjectData() throws MiddlewareException {
		String studyName = "validateProjectData_" + new Random().nextInt(10000);
		int trialNo = 1;
		Workbook workbook = WorkbookTest.getTestWorkbookForWizard(studyName, trialNo);
		workbook.print(MiddlewareIntegrationTest.INDENT);
		DataImportServiceImplTestIT.dataImportService.saveDataset(workbook, true, false, DataImportServiceImplTestIT.PROGRAM_UUID);
		Map<String, List<Message>> errors =
				DataImportServiceImplTestIT.dataImportService.validateProjectData(workbook, DataImportServiceImplTestIT.PROGRAM_UUID);
		Assert.assertNotNull(errors);
		if (errors != null) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "Errors Identified: ");
			for (Map.Entry<String, List<Message>> e : errors.entrySet()) {
				Debug.println(MiddlewareIntegrationTest.INDENT + 2, e.getKey());
				for (Message m : e.getValue()) {
					if (m.getMessageParams() != null) {
						Debug.println(MiddlewareIntegrationTest.INDENT + 4,
								"Key: " + m.getMessageKey() + " Params: " + Arrays.asList(m.getMessageParams()));
					} else {
						Debug.println(MiddlewareIntegrationTest.INDENT + 4, "Key: " + m.getMessageKey());
					}
				}
			}
		}
	}
}
