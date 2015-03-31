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
package org.generationcp.middleware.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.etl.WorkbookTest;
import org.generationcp.middleware.domain.etl.WorkbookTest2;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DataImportServiceImplTestIT extends DataManagerIntegrationTest {

    private static DataImportService dataImportService;
    
    private static FieldbookService fieldbookService;
    
    private static final String PROGRAM_UUID = "123456789";

    @BeforeClass
    public static void setUp() throws Exception {
        dataImportService = managerFactory.getDataImportService();
        fieldbookService = managerFactory.getFieldbookMiddlewareService();
    }

    @Test
    public void testSaveMultiLocationDataset() throws MiddlewareQueryException {
        List<Workbook> workbooks = WorkbookTest2.getTestWorkbooks(5);
        int id = 0;
        for (Workbook workbook : workbooks) {
            //comment these out if you want to let the system generate the dataset names.

            workbook.getStudyDetails().setTrialDatasetName("MyTrial_" + workbook.getStudyDetails().getStudyName());
            workbook.getStudyDetails().setMeasurementDatasetName("MyMeasurement_" + workbook.getStudyDetails().getStudyName());

            id = dataImportService.saveDataset(workbook, PROGRAM_UUID);
        }
        String name = workbooks.get(0).getStudyDetails() != null ? workbooks.get(0).getStudyDetails().getStudyName() : null;
        Debug.println(INDENT, "Created study: " + id + ", name = " + name);
    }

    @Test
    public void testSaveTrialDataset() throws MiddlewareQueryException {
        Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);
                
        int id = dataImportService.saveDataset(workbook, PROGRAM_UUID);
                
        Workbook createdWorkbook = fieldbookService.getTrialDataSet(id);
        
        WorkbookTest.setTestWorkbook(null);
        workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);
        
        assertEquals("Expected " + workbook.getTrialConditions().size() + " of records for trial conditions but got " 
        		+ createdWorkbook.getTrialConditions().size(), workbook.getTrialConditions().size(), 
        		createdWorkbook.getTrialConditions().size());
        assertTrue("Expected the same trial conditions retrieved but found a different condition.", 
        		WorkbookTest.areTrialVariablesSame(workbook.getTrialConditions(), createdWorkbook.getTrialConditions()));
        assertEquals("Expected " + workbook.getTrialConstants().size() + " of records for trial constants but got " 
        		+ createdWorkbook.getTrialConstants().size(), workbook.getTrialConstants().size(), 
        		createdWorkbook.getTrialConstants().size());
        assertTrue("Expected the same trial constants retrieved but found a different constant.", 
        		WorkbookTest.areTrialVariablesSame(workbook.getTrialConstants(), createdWorkbook.getTrialConstants()));
    }

	@Test
    public void testSaveNurseryDataset() throws MiddlewareQueryException {
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.N);
                
        int id = dataImportService.saveDataset(workbook, PROGRAM_UUID);
                
        Workbook createdWorkbook = fieldbookService.getNurseryDataSet(id);
        
        WorkbookTest.setTestWorkbook(null);
        workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);
                
        assertEquals("Expected " + workbook.getTrialConditions().size() + " of records for trial conditions but got " 
        		+ createdWorkbook.getTrialConditions().size(), workbook.getTrialConditions().size(), 
        		createdWorkbook.getTrialConditions().size());
        assertTrue("Expected the same trial conditions retrieved but found a different condition.", 
        		WorkbookTest.areTrialVariablesSame(workbook.getTrialConditions(), createdWorkbook.getTrialConditions()));
        assertEquals("Expected " + workbook.getTrialConstants().size() + " of records for trial constants but got " 
        		+ createdWorkbook.getTrialConstants().size(), workbook.getTrialConstants().size(), 
        		createdWorkbook.getTrialConstants().size());
        assertTrue("Expected the same trial constants retrieved but found a different constant.", 
        		WorkbookTest.areTrialVariablesSame(workbook.getTrialConstants(), createdWorkbook.getTrialConstants()));
    }
	
	@Test
    public void testAddTrialEnvironmentToTrial() throws MiddlewareQueryException {
        Workbook workbook = WorkbookTest.getTestWorkbook(4, StudyType.T);
                
        int id = dataImportService.saveDataset(workbook, PROGRAM_UUID);
        
        Workbook createdWorkbook = fieldbookService.getTrialDataSet(id);
        
        int noOfOrigTrialInstances = createdWorkbook.getTrialObservations().size();
        
        WorkbookTest.addNewEnvironment(createdWorkbook);
                
        dataImportService.saveDataset(createdWorkbook, PROGRAM_UUID);
        
        createdWorkbook = fieldbookService.getTrialDataSet(id);
        
        assertTrue("Expected " + (noOfOrigTrialInstances + 1) + " instances but got " + createdWorkbook.getTrialObservations().size() + " instead." , 
        		(noOfOrigTrialInstances + 1) == createdWorkbook.getTrialObservations().size());
    }

	@Test
	public void testDeletionOfExperimentPropAndStockProp() throws MiddlewareQueryException {
		WorkbookTest.setTestWorkbook(null);
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.N);
		
		int id = dataImportService.saveDataset(workbook, PROGRAM_UUID);
		
		Workbook createdWorkbook = fieldbookService.getNurseryDataSet(id);
		
		WorkbookTest.deleteExperimentPropVar(createdWorkbook);
		
		dataImportService.saveDataset(createdWorkbook, PROGRAM_UUID);
		
		createdWorkbook = fieldbookService.getNurseryDataSet(id);
		
		assertNotNull("Expected successful retrieval of workbook.", createdWorkbook);
	}
	
    @Test
    public void testParseWorkbook() throws MiddlewareQueryException, WorkbookParserException {
        // Dan V : changed implem so that template path is located in src/test/resources. no need to change per user to reflect file location

        String fileLocation = this.getClass().getClassLoader().getResource("ricetest2.xls").getFile();
        File file = new File(fileLocation);
        Workbook workbook = dataImportService.parseWorkbook(file);
        workbook.print(INDENT);


        int id = dataImportService.saveDataset(workbook, PROGRAM_UUID);
        Debug.println(INDENT, "Created study:" + id);
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
            dataImportService.strictParseWorkbook(file,PROGRAM_UUID);
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
            dataImportService.strictParseWorkbook(file,PROGRAM_UUID);
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
            dataImportService.strictParseWorkbook(file,PROGRAM_UUID);
        } catch (WorkbookParserException e) {
            List<Message> messages = e.getErrorMessages();

            assertNotNull(messages);
            assertTrue(messages.size() == 1);
            assertEquals(expectedErrorKey, messages.get(0).getMessageKey());
            return;
        } catch (MiddlewareQueryException e) {
            fail("Unexpected exception : " + e.getMessage());
        }

        fail(errorMessage);
    }

    @Test
	public void testCheckIfProjectNameIsExisting() throws Exception {
    	//try to save first then use the name of the saved study
    	Workbook workbook = WorkbookTest2.getTestWorkbook();
        workbook.print(INDENT);
        dataImportService.saveDataset(workbook, PROGRAM_UUID);
        String name = workbook.getStudyDetails() != null ? workbook.getStudyDetails().getStudyName() : null;
        Debug.println(INDENT, "Name: " + name);
		boolean isExisting = dataImportService.checkIfProjectNameIsExistingInProgram(name,PROGRAM_UUID);
		assertTrue(isExisting);
		
		name = "SHOULDNOTEXISTSTUDY";
		Debug.println(INDENT, "Name: " + name);
		isExisting = dataImportService.checkIfProjectNameIsExistingInProgram(name,PROGRAM_UUID);
		assertFalse(isExisting);
	}
    
    @Test
    public void getLocationIdByProjectNameAndDescription() throws MiddlewareQueryException {
    	//try to save first then use the name of the saved study
    	Workbook workbook = WorkbookTest2.getTestWorkbook();
        workbook.print(INDENT);
        dataImportService.saveDataset(workbook, PROGRAM_UUID);
        String name = workbook.getStudyDetails().getStudyName();
        Debug.println(INDENT, "Name: " + name);
		Integer locationId = dataImportService.
				getLocationIdByProjectNameAndDescriptionAndProgramUUID(
						name,"1",PROGRAM_UUID);
		assertEquals(locationId.longValue(),1L);        
    }
    
    @Test
    public void testSaveProjectOntology() throws MiddlewareQueryException {
        Workbook workbook = WorkbookTest2.getTestWorkbook();
        workbook.print(INDENT);
        int id = dataImportService.saveProjectOntology(workbook, PROGRAM_UUID);
        Debug.println(INDENT, "Created study:" + id + ", name = " + workbook.getStudyDetails().getStudyName());

    }
    
    @Test
    public void testSaveProjectData() throws MiddlewareQueryException {
        Workbook workbook = WorkbookTest2.getTestWorkbook();
        workbook.print(INDENT);
        int studyId = dataImportService.saveProjectOntology(workbook, PROGRAM_UUID);
        workbook.setStudyId(studyId);
        workbook.setTrialDatasetId(studyId-1);
        workbook.setMeasurementDatesetId(studyId-2); 
        dataImportService.saveProjectData(workbook,PROGRAM_UUID);
        Debug.println(INDENT, "Saved project data:" + studyId + ", name = " + workbook.getStudyDetails().getStudyName());

    }
    
    @Test
	public void testValidateProjectOntology() throws MiddlewareQueryException {
        Workbook workbook = WorkbookTest2.getTestWorkbookWithErrors();
        workbook.print(INDENT);
        Map<String,List<Message>> errors = dataImportService.validateProjectOntology(workbook);
        assertNotNull(errors);
        if(errors!=null) {
        	Debug.println(INDENT, "Errors Identified: ");
        	for(Map.Entry<String,List<Message>> e: errors.entrySet()) {
        		Debug.println(INDENT+2, e.getKey());
        		for(Message m: e.getValue()) {
        			if(m.getMessageParams()!=null) {
        				Debug.println(INDENT+4, "Key: " + m.getMessageKey() + " Params: "+ Arrays.asList(m.getMessageParams()));
        			} else {
        				Debug.println(INDENT+4, "Key: " + m.getMessageKey());
        			}
        		}
        	}
        }
    }
    
    @Test
	public void testValidateProjectData() throws MiddlewareQueryException {
    	String studyName = "validateProjectData_" + new Random().nextInt(10000);
    	int trialNo = 1;
    	Workbook workbook = WorkbookTest2.getTestWorkbookForWizard(studyName,trialNo);
        workbook.print(INDENT);
        dataImportService.saveDataset(workbook,true,false, PROGRAM_UUID);
        Map<String,List<Message>> errors = dataImportService.validateProjectData(workbook,PROGRAM_UUID);
        assertNotNull(errors);
        if(errors!=null) {
        	Debug.println(INDENT, "Errors Identified: ");
        	for(Map.Entry<String,List<Message>> e: errors.entrySet()) {
        		Debug.println(INDENT+2, e.getKey());
        		for(Message m: e.getValue()) {
        			if(m.getMessageParams()!=null) {
        				Debug.println(INDENT+4, "Key: " + m.getMessageKey() + " Params: "+ Arrays.asList(m.getMessageParams()));
        			} else {
        				Debug.println(INDENT+4, "Key: " + m.getMessageKey());
        			}
        		}
        	}
        }
    }
}
