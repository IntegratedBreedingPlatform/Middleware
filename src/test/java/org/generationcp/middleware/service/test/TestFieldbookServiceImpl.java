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


import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.service.ServiceFactory;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.util.Debug;
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

@RunWith(JUnit4.class)
public class TestFieldbookServiceImpl {
    
    private static ServiceFactory serviceFactory;
    private static FieldbookService fieldbookService;
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

        fieldbookService = serviceFactory.getFieldbookService();
        dataImportService = serviceFactory.getDataImportService();
    }

    @Before
    public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }

    @Test
    public void testGetAllLocalNurseryDetails() throws MiddlewareQueryException {

        List<StudyDetails> nurseryStudyDetails = fieldbookService.getAllLocalNurseryDetails();
        for (StudyDetails study : nurseryStudyDetails){
            study.print(3);
        }
        Debug.println(3, "NUMBER OF RECORDS: " + nurseryStudyDetails.size());

    }


    @Test
    public void testGetAllLocalTrialStudyDetails() throws MiddlewareQueryException {
        List<StudyDetails> studyDetails = fieldbookService.getAllLocalTrialStudyDetails();
        for (StudyDetails study : studyDetails){
            study.print(3);
        }
        Debug.println(3, "NUMBER OF RECORDS: " + studyDetails.size());

    }
    
    @Test
    public void testGetFieldMapCountsOfTrial() throws MiddlewareQueryException{
        List<Integer> trialIds = new ArrayList<Integer>();
        trialIds.add(Integer.valueOf(1)); 
        List<FieldMapInfo> fieldMapCount = fieldbookService.getFieldMapInfoOfTrial(trialIds);
        for (FieldMapInfo fieldMapInfo : fieldMapCount) {
            fieldMapInfo.print(3);
        }
        //assertTrue(fieldMapCount.getEntryCount() > 0);      
    }
    
    @Test
    public void testGetFieldMapCountsOfNursery() throws MiddlewareQueryException{
        List<Integer> nurseryIds = new ArrayList<Integer>();
        nurseryIds.add(Integer.valueOf(5734)); 
        List<FieldMapInfo> fieldMapCount = fieldbookService.getFieldMapInfoOfNursery(nurseryIds);
        for (FieldMapInfo fieldMapInfo : fieldMapCount) {
            fieldMapInfo.print(3);
        }
       // assertTrue(fieldMapCount.getEntryCount() > 0);      
    }

    @Test
    public void testGetAllFieldMapsInBlockByTrialInstanceId() throws MiddlewareQueryException{
        List<FieldMapInfo> fieldMapCount = fieldbookService.getAllFieldMapsInBlockByTrialInstanceId(-2, -1);
        for (FieldMapInfo fieldMapInfo : fieldMapCount) {
            fieldMapInfo.print(3);
        }
        //assertTrue(fieldMapCount.getEntryCount() > 0);      
    }
    

    @Test
    public void testGetAllLocations() throws MiddlewareQueryException {
    	List<Location> locations = fieldbookService.getAllLocations();
    	for (Location loc : locations){
    		Debug.println(3, loc.toString());
    	}
    	Debug.println(3, "NUMBER OF RECORDS: " + locations.size());
    }
    
    @Test
    public void testGetNurseryDataSet() throws MiddlewareQueryException {
        Workbook workbook = TestNurseryWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        int id = dataImportService.saveDataset(workbook);
        workbook = fieldbookService.getNurseryDataSet(id);
        
        System.out.println(workbook);
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
}
