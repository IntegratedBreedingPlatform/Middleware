/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.service.test;


import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.service.ServiceFactory;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.utils.test.TestNurseryWorkbookUtil;
import org.generationcp.middleware.utils.test.TestWorkbookUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TestDataImportServiceImpl {

    private static ServiceFactory serviceFactory;
    private static DataImportService dataImportService;

    private long startTime;

    @Rule
    public TestName name = new TestName();

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "central");

        serviceFactory = new ServiceFactory(local, central);

        dataImportService = serviceFactory.getDataImportService();

    }

    @Before
    public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }



    @Test
    public void testSaveMultiLocationDataset() throws MiddlewareQueryException {
        List<Workbook> workbooks = TestWorkbookUtil.getTestWorkbooks(5);
        int id = 0;
        for (Workbook workbook : workbooks) {
            //comment these out if you want to let the system generate the dataset names.

            workbook.getStudyDetails().setTrialDatasetName("MyTrial_" + workbook.getStudyDetails().getStudyName());
            workbook.getStudyDetails().setMeasurementDatasetName("MyMeasurement_" + workbook.getStudyDetails().getStudyName());

            id = dataImportService.saveDataset(workbook);
        }
        String name = workbooks.get(0).getStudyDetails() != null ? workbooks.get(0).getStudyDetails().getStudyName() : null;
        Debug.println(0, "Created study: " + id + ", name = " + name);
    }

    @Test
    public void testSaveDataset() throws MiddlewareQueryException {
        Workbook workbook = TestWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        int id = dataImportService.saveDataset(workbook);
        Debug.println(0, "Created study:" + id + ", name = " + workbook.getStudyDetails().getStudyName());

    }

    @Test
    public void testSaveNurseryDataset() throws MiddlewareQueryException {
        Workbook workbook = TestNurseryWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        int id = dataImportService.saveDataset(workbook);
        String name = workbook.getStudyDetails() != null ? workbook.getStudyDetails().getStudyName() : null;
        Debug.println(0, "Created study:" + id + ", name = " + name);
    }

    @Test
    public void testParseWorkbook() throws MiddlewareQueryException, WorkbookParserException {
        // Dan V : changed implem so that template path is located in src/test/resources. no need to change per user to reflect file location

        String fileLocation = this.getClass().getClassLoader().getResource("ricetest2.xls").getFile();
        File file = new File(fileLocation);
        Workbook workbook = dataImportService.parseWorkbook(file);
        workbook.print(0);


        int id = dataImportService.saveDataset(workbook);
        Debug.println(0, "Created study:" + id);
    }


    // Daniel V
    // added new tests to cover validation scenarios for strict parsing of workbook

    @Test
    public void testParseWorkbookWrongDescriptionSheet() {
        testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5808WrongDescriptionSheetName.xls", "error.missing.sheet.description", "Unable to detect wrong description sheet");
    }

    @Test
    public void testParseWorkbookWrongObservationSheet() {
        testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5808WrongObservationSheetName.xls", "error.missing.sheet.observation", "Unable to detect wrong observation sheet");
    }

    @Test
    public void testParseWorkbookWithEntryWrongPMS() {
        testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5800ScenarioWithEntryWrongPSM.xls", "error.entry.doesnt.exist", "Unable to detect invalid entry");
    }

    @Test
    public void testParseWorkbookNoEntry() {
        testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5800ScenarioNoEntry.xls", "error.entry.doesnt.exist", "Unable to detect invalid entry");
    }

    @Test
    public void testParseWorkbookWithEntryWrongCategory() {
        testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5800ScenarioWithEntryWrongCategory.xls", "error.entry.doesnt.exist", "Unable to detect invalid entry");
    }

    @Test
    public void testParseWorkbookWithNoTrialNonNursery() {
        testFileAgainstExpectedErrorCondition("org/generationcp/middleware/service/test/GCP5799NonNurseryWorkbookNoTrialEnvironment.xls", "error.missing.trial.condition", "Unable to detect missing trial condition");
    }

    @Test
    public void testParseWorkbookWithNoTrialNursery() {
        String fileLocation = this.getClass().getClassLoader().getResource("org/generationcp/middleware/service/test/GCP5799NurseryWorkbookNoTrialEnvironment.xls").getFile();
        File file = new File(fileLocation);
        try {
            dataImportService.strictParseWorkbook(file);
        } catch (WorkbookParserException e) {
            fail("Unable to correctly parse Nursery workbook with no trial condition");
        } catch (MiddlewareQueryException e) {
            fail("Unexpected exception : " + e.getMessage());
        }
    }

    @Test
    public void testParseWorkbookWithEmptyFields() {
        String fileLocation = this.getClass().getClassLoader().getResource("org/generationcp/middleware/service/test/GCP5802SevenFieldsMissing.xls").getFile();
        File file = new File(fileLocation);
        try {
            dataImportService.strictParseWorkbook(file);
        } catch (WorkbookParserException e) {

            List<Message> messages = e.getErrorMessages();

            assertNotNull(messages);
            // There should be 7 error messages to correspond with the 7 missing fields in the file
            assertSame(messages.size(), 7);

            return;
        } catch (MiddlewareQueryException e) {
            fail("Unexpected exception : " + e.getMessage());
        }

        fail("Unable to detect empty fields");
    }

    protected void testFileAgainstExpectedErrorCondition(String qualifiedFilename, String expectedErrorKey, String errorMessage) {
        String fileLocation = this.getClass().getClassLoader().getResource(qualifiedFilename).getFile();
        try {
            File file = new File(fileLocation);
            dataImportService.strictParseWorkbook(file);
        } catch (WorkbookParserException e) {
            List<Message> messages = e.getErrorMessages();

            assertNotNull(messages);
            assertSame(messages.size(), 1);
            assertEquals(expectedErrorKey, messages.get(0).getMessageKey());
            return;
        } catch (MiddlewareQueryException e) {
            fail("Unexpected exception : " + e.getMessage());
        }

        fail(errorMessage);
    }


    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " s");
    }


    @AfterClass
    public static void tearDown() throws Exception {
        if (serviceFactory != null) {
            serviceFactory.close();
        }
    }
    
    @Test
	public void testCheckIfProjectNameIsExisting() throws Exception {
    	//try to save first then use the name of the saved study
    	Workbook workbook = TestWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        dataImportService.saveDataset(workbook);
        String name = workbook.getStudyDetails() != null ? workbook.getStudyDetails().getStudyName() : null;
        Debug.println(0, "Name: " + name);
		boolean isExisting = dataImportService.checkIfProjectNameIsExisting(name);
		assertTrue(isExisting);
		
		name = "SHOULDNOTEXISTSTUDY";
		Debug.println(0, "Name: " + name);
		isExisting = dataImportService.checkIfProjectNameIsExisting(name);
		assertFalse(isExisting);
	}
    
    @Test
    public void getLocationIdByProjectNameAndDescription() throws MiddlewareQueryException {
    	//try to save first then use the name of the saved study
    	Workbook workbook = TestWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        dataImportService.saveDataset(workbook);
        String name = workbook.getStudyDetails().getStudyName();
        Debug.println(0, "Name: " + name);
		Integer locationId = dataImportService.getLocationIdByProjectNameAndDescription(name,"1");
		assertEquals(locationId.longValue(),1L);        
    }
    
    @Test
    public void testSaveProjectOntology() throws MiddlewareQueryException {
        Workbook workbook = TestWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        int id = dataImportService.saveProjectOntology(workbook);
        Debug.println(0, "Created study:" + id + ", name = " + workbook.getStudyDetails().getStudyName());

    }
    
    @Test
    public void testSaveProjectData() throws MiddlewareQueryException {
        Workbook workbook = TestWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        int studyId = dataImportService.saveProjectOntology(workbook);
        workbook.setStudyId(studyId);
        workbook.setTrialDatasetId(studyId-1);
        workbook.setMeasurementDatesetId(studyId-2); 
        dataImportService.saveProjectData(workbook);
        Debug.println(0, "Saved project data:" + studyId + ", name = " + workbook.getStudyDetails().getStudyName());

    }
    
    @Test
	public void testValidateProjectOntology() throws MiddlewareQueryException {
        Workbook workbook = TestWorkbookUtil.getTestWorkbookWithErrors();
        workbook.print(0);
        Map<String,List<Message>> errors = dataImportService.validateProjectOntology(workbook);
        assertNotNull(errors);
        if(errors!=null) {
        	Debug.println(0, "Errors Identified: ");
        	for(Map.Entry<String,List<Message>> e: errors.entrySet()) {
        		Debug.println(3, e.getKey());
        		for(Message m: e.getValue()) {
        			if(m.getMessageParams()!=null) {
        				Debug.println(5, "Key: " + m.getMessageKey() + " Params: "+ Arrays.asList(m.getMessageParams()));
        			} else {
        				Debug.println(5, "Key: " + m.getMessageKey());
        			}
        		}
        	}
        }
    }
    
    @Test
	public void testValidateProjectData() throws MiddlewareQueryException {
    	String studyName = "validateProjectData_" + new Random().nextInt(10000);
    	int trialNo = 1;
    	Workbook workbook = TestWorkbookUtil.getTestWorkbookForWizard(studyName,trialNo);
        workbook.print(0);
        dataImportService.saveDataset(workbook,true);
        Map<String,List<Message>> errors = dataImportService.validateProjectData(workbook);
        assertNotNull(errors);
        if(errors!=null) {
        	Debug.println(0, "Errors Identified: ");
        	for(Map.Entry<String,List<Message>> e: errors.entrySet()) {
        		Debug.println(3, e.getKey());
        		for(Message m: e.getValue()) {
        			if(m.getMessageParams()!=null) {
        				Debug.println(5, "Key: " + m.getMessageKey() + " Params: "+ Arrays.asList(m.getMessageParams()));
        			} else {
        				Debug.println(5, "Key: " + m.getMessageKey());
        			}
        		}
        	}
        }
    }
}
