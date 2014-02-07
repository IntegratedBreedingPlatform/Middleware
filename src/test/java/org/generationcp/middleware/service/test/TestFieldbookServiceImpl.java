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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.ServiceFactory;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.utils.test.TestNurseryWorkbookUtil;
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
        workbook.print(0);
    }
    
    @Test
    public void testSaveMeasurementRows() throws MiddlewareQueryException {

        // Assumption: there is at least 1 local nursery stored in the database
        int id = fieldbookService.getAllLocalNurseryDetails().get(0).getId();
        Workbook workbook = fieldbookService.getNurseryDataSet(id);
        workbook.print(0);
        
        List<MeasurementRow> observations = workbook.getObservations();
        for (MeasurementRow observation : observations){
            List<MeasurementData> fields = observation.getDataList();
            for (MeasurementData field : fields){
                try {
                    //Debug.println(4, "Origina: " + field.toString());
                    if (field.getValue() != null){
                        field.setValue(Double.valueOf(Double.valueOf(field.getValue()) + 1).toString());
                        field.setValue(Integer.valueOf(Integer.valueOf(field.getValue()) + 1).toString());
                        //Debug.println(4, "Updated: " + field.toString());
                    }
                } catch (NumberFormatException e){
                    // Ignore. Update only numeric values
                }
            }
        }
        
        fieldbookService.saveMeasurementRows(workbook);
        Workbook workbook2 = fieldbookService.getNurseryDataSet(id);
        workbook2.print(0);
        assertFalse(workbook.equals(workbook2));
    }
    
    @Test
    public void testGetStandardVariableIdByPropertyScaleMethodRole() throws MiddlewareQueryException {
        String property = "Germplasm entry";
        String scale = "Number";
        String method = "Enumerated";
        Integer termId = fieldbookService.getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, PhenotypicType.GERMPLASM);
        Debug.println(0, termId.toString());
        assertEquals((Integer) 8230, termId);
    }

    @Test
    public void testGetAllBreedingMethods() throws MiddlewareQueryException {
        List<Method> methods = fieldbookService.getAllBreedingMethods();
        for (Method method : methods){
            Debug.println(3, method.toString());
        }
        assertFalse(methods.isEmpty());
        Debug.println(3, "#Methods: " + methods.size());
    }

    @Test
    public void testSaveNurseryAdvanceGermplasmList() throws MiddlewareQueryException {
        Map<Germplasm, Name> germplasms = new HashMap<Germplasm, Name>();
        createGermplasms(germplasms);
        
        List<GermplasmListData> listData = new ArrayList<GermplasmListData>();
        GermplasmList germplasmList = createGermplasmList(listData);

        Integer listId = fieldbookService.saveNurseryAdvanceGermplasmList(germplasms, germplasmList);

        assertTrue(listId != null && listId < 0);
        
        Debug.println(3, "Germplasm List Added: ");
        Debug.println(6, germplasmList.toString());
        Debug.println(3, "Germplasms Added: ");
        for (Germplasm germplasm: germplasms.keySet()){
            Debug.println(6, germplasm.toString());
        }
        
    }

    private void createGermplasms(Map<Germplasm, Name> germplasms) {
        int NUMBER_OF_ENTRIES = 3;
        
        for (int i=0; i< NUMBER_OF_ENTRIES; i++){
            Germplasm g = new Germplasm();
            g.setGdate(Integer.valueOf(20140206));
            g.setGnpgs(Integer.valueOf(0));
            g.setGpid1(Integer.valueOf(0));
            g.setGpid2(Integer.valueOf(0));
            g.setGrplce(Integer.valueOf(0));
            g.setLocationId(Integer.valueOf(9000));
            g.setMethodId(Integer.valueOf(1));
            g.setMgid(Integer.valueOf(1));
            g.setUserId(Integer.valueOf(1));
            g.setReferenceId(Integer.valueOf(1));
    
            Name n = new Name();
            n.setLocationId(Integer.valueOf(9000));
            n.setNdate(Integer.valueOf(20140206));
            n.setNval("Germplasm_64_" + i + "_" + (int) Math.random()*100);
            n.setReferenceId(Integer.valueOf(1));
            n.setTypeId(Integer.valueOf(1));
            n.setUserId(Integer.valueOf(1));
            
            germplasms.put(g, n);
        }
    }
    
    private GermplasmList createGermplasmList(List<GermplasmListData> listData){
        String name = "Test List #1_" + "_" + (int) Math.random()*100;
        GermplasmList germList = new GermplasmList(null, name, Long.valueOf(20140206), "LST", Integer.valueOf(1),
                name + " Description", null, 1);
        
        GermplasmListData germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(2), 1, "EntryCode", "SeedSource",
                "Germplasm Name 3", "GroupName", 0, 99992);
        listData.add(germplasmListData);
        
        return germList;

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
