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
import java.util.List;

import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.service.ServiceFactory;
import org.generationcp.middleware.service.api.DataImportService;
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
        System.out.println("Created study: " + id + ", name = " + workbooks.get(0).getStudyDetails().getStudyName());
    }

    @Test
    public void testSaveDataset() throws MiddlewareQueryException {
        Workbook workbook = TestWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        int id = dataImportService.saveDataset(workbook);
        System.out.println("Created study:" + id + ", name = " + workbook.getStudyDetails().getStudyName());

    }

    @Test
    public void testSaveNurseryDataset() throws MiddlewareQueryException {
        Workbook workbook = TestNurseryWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        int id = dataImportService.saveDataset(workbook);
        System.out.println("Created study:" + id + ", name = " + workbook.getStudyDetails().getStudyName());
    }

    @Test
    public void testParseWorkbook() throws MiddlewareQueryException, WorkbookParserException {
        // Dan V : changed implem so that template path is located in src/test/resources. no need to change per user to reflect file location

        String fileLocation = this.getClass().getClassLoader().getResource("Population114_Pheno_FB_3.xls").getFile();
        File file = new File(fileLocation);
        Workbook workbook = dataImportService.parseWorkbook(file);
        workbook.print(0);


        int id = dataImportService.saveDataset(workbook);
        System.out.println("Created study:" + id);
    }


    // Daniel V
    // added new tests to cover validation scenarios for strict parsing of workbook

    @Test
    public void testParseWorkbookWrongDescriptionSheet() {
        String fileLocation = this.getClass().getClassLoader().getResource("org/generationcp/middleware/service/test/GCP5808WrongDescriptionSheetName.xls").getFile();
        try {
            File file = new File(fileLocation);
            Workbook workbook = dataImportService.strictParseWorkbook(file);
        } catch (WorkbookParserException e) {
            List<Message> messages = e.getErrorMessages();

            assertNotNull(messages);
            assertTrue(messages.size() == 1);
            assertEquals("error.missing.sheet.description", messages.get(0).getMessageKey());
            return;
        } catch (MiddlewareQueryException e) {
            fail("Unexpected exception : " + e.getMessage());
        }

        fail("Unable to detect wrong description sheet");
    }

    @Test
    public void testParseWorkbookWrongObservationSheet() {
        String fileLocation = this.getClass().getClassLoader().getResource("org/generationcp/middleware/service/test/GCP5808WrongObservationSheetName.xls").getFile();
        try {
            File file = new File(fileLocation);
            Workbook workbook = dataImportService.strictParseWorkbook(file);
        } catch (WorkbookParserException e) {
            List<Message> messages = e.getErrorMessages();

            assertNotNull(messages);
            assertTrue(messages.size() == 1);
            assertEquals("error.missing.sheet.observation", messages.get(0).getMessageKey());
            return;
        } catch (MiddlewareQueryException e) {
            fail("Unexpected exception : " + e.getMessage());
        }

        fail("Unable to detect wrong observation sheet");
    }

    @Test
    public void testParseWorkbookWithEntryWrongPMS() {
        String fileLocation = this.getClass().getClassLoader().getResource("org/generationcp/middleware/service/test/GCP5800ScenarioWithEntryWrongPSM.xls").getFile();
        try {
            File file = new File(fileLocation);
            Workbook workbook = dataImportService.strictParseWorkbook(file);
        } catch (WorkbookParserException e) {
            List<Message> messages = e.getErrorMessages();

            assertNotNull(messages);
            assertTrue(messages.size() == 1);
            assertEquals("error.entry.doesnt.exist", messages.get(0).getMessageKey());
            return;
        } catch (MiddlewareQueryException e) {
            fail("Unexpected exception : " + e.getMessage());
        }

        fail("Unable to detect invalid entry");
    }

    @Test
    public void testParseWorkbookNoEntry() {
        String fileLocation = this.getClass().getClassLoader().getResource("org/generationcp/middleware/service/test/GCP5800ScenarioNoEntry.xls").getFile();
        try {
            File file = new File(fileLocation);
            dataImportService.strictParseWorkbook(file);
        } catch (WorkbookParserException e) {
            List<Message> messages = e.getErrorMessages();

            assertNotNull(messages);
            assertTrue(messages.size() == 1);
            assertEquals("error.entry.doesnt.exist", messages.get(0).getMessageKey());
            return;
        } catch (MiddlewareQueryException e) {
            fail("Unexpected exception : " + e.getMessage());
        }

        fail("Unable to detect invalid entry");
    }

    @Test
    public void testParseWorkbookWithEntryWrongCategory() {
        String fileLocation = this.getClass().getClassLoader().getResource("org/generationcp/middleware/service/test/GCP5800ScenarioWithEntryWrongCategory.xls").getFile();
        try {
            File file = new File(fileLocation);
            dataImportService.strictParseWorkbook(file);
        } catch (WorkbookParserException e) {
            List<Message> messages = e.getErrorMessages();

            assertNotNull(messages);
            assertTrue(messages.size() == 1);
            assertEquals("error.entry.doesnt.exist", messages.get(0).getMessageKey());
            return;
        } catch (MiddlewareQueryException e) {
            fail("Unexpected exception : " + e.getMessage());
        }

        fail("Unable to detect invalid entry");
    }


    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " s");
    }


    @AfterClass
    public static void tearDown() throws Exception {
        if (serviceFactory != null) {
            serviceFactory.close();
        }
    }
}
