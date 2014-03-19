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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Person;
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
        Integer termId = fieldbookService.getStandardVariableIdByPropertyScaleMethodRole(
                                    property, scale, method, PhenotypicType.GERMPLASM);
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
    public void testSaveNurseryAdvanceGermplasmListCimmytWheat() throws MiddlewareQueryException {
        Map<Germplasm, List<Name>> germplasms = new HashMap<Germplasm, List<Name>>();
        Map<Germplasm, GermplasmListData> listData = new HashMap<Germplasm, GermplasmListData>();
        GermplasmList germplasmList = createGermplasmsCimmytWheat(germplasms, listData);
        
        Integer listId = fieldbookService.saveNurseryAdvanceGermplasmList(germplasms, listData, germplasmList);

        assertTrue(listId != null && listId < 0);
        
        Debug.println(3, "Germplasm List Added: ");
        Debug.println(6, germplasmList.toString());
        Debug.println(3, "Germplasms Added: ");
        for (Germplasm germplasm: germplasms.keySet()){
            Debug.println(6, germplasm.toString());
        }
        
    }
    

    @Test
    public void testSaveNurseryAdvanceGermplasmListOtherCrop() throws MiddlewareQueryException {
        Map<Germplasm, List<Name>> germplasms = new HashMap<Germplasm, List<Name>>();
        Map<Germplasm, GermplasmListData> listData = new HashMap<Germplasm, GermplasmListData>();
        GermplasmList germplasmList = createGermplasmsOtherCrop(germplasms, listData);
        
        Integer listId = fieldbookService.saveNurseryAdvanceGermplasmList(germplasms, listData, germplasmList);

        assertTrue(listId != null && listId < 0);
        
        Debug.println(3, "Germplasm List Added: ");
        Debug.println(6, germplasmList.toString());
        Debug.println(3, "Germplasms Added: ");
        for (Germplasm germplasm: germplasms.keySet()){
            Debug.println(6, germplasm.toString());
        }
        
    }
    
    @Test
    public void testGetDistinctStandardVariableValues() throws Exception {
    	int stdVarId = 8250;
    	List<ValueReference> list = fieldbookService.getDistinctStandardVariableValues(stdVarId);
    	for (ValueReference ref : list) {
    		Debug.println(1, ref.toString());
    	}
    	stdVarId = 8135;
    	list = fieldbookService.getDistinctStandardVariableValues(stdVarId);
    	for (ValueReference ref : list) {
    		Debug.println(1, ref.toString());
    	}
    	stdVarId = 8170;
    	list = fieldbookService.getDistinctStandardVariableValues(stdVarId);
    	for (ValueReference ref : list) {
    		Debug.println(1, ref.toString());
    	}
    	stdVarId = 8191;
    	list = fieldbookService.getDistinctStandardVariableValues(stdVarId);
    	for (ValueReference ref : list) {
    		Debug.println(1, ref.toString());
    	}
    	stdVarId = 8192;
    	list = fieldbookService.getDistinctStandardVariableValues(stdVarId);
    	for (ValueReference ref : list) {
    		Debug.println(1, ref.toString());
    	}
    	stdVarId = 8193;
    	list = fieldbookService.getDistinctStandardVariableValues(stdVarId);
    	for (ValueReference ref : list) {
    		Debug.println(1, ref.toString());
    	}
    	stdVarId = 8194;
    	list = fieldbookService.getDistinctStandardVariableValues(stdVarId);
    	for (ValueReference ref : list) {
    		Debug.println(1, ref.toString());
    	}
    	stdVarId = 8007;
    	list = fieldbookService.getDistinctStandardVariableValues(stdVarId);
    	for (ValueReference ref : list) {
    		Debug.println(1, ref.toString());
    	}
    }
    
    private GermplasmList createGermplasmsCimmytWheat(
            Map<Germplasm, List<Name>> germplasms, Map<Germplasm, GermplasmListData> listData) {
        
        int NUMBER_OF_ENTRIES = 3;
        
        GermplasmList germList = createGermplasmList();
        
        for (int i=0; i< NUMBER_OF_ENTRIES; i++){
            Germplasm g = createGermplasm();

            List<Name> names = new ArrayList<Name>();
            Name n = createGermplasmName(i);
            n.setTypeId(GermplasmNameType.UNRESOLVED_NAME.getUserDefinedFieldID());
            n.setNstat(Integer.valueOf(0));
            names.add(n);

            n = createGermplasmName(i);
            n.setTypeId(GermplasmNameType.CIMMYT_SELECTION_HISTORY.getUserDefinedFieldID());
            n.setNstat(Integer.valueOf(1));
            names.add(n);

            n = createGermplasmName(i);
            n.setTypeId(GermplasmNameType.CIMMYT_WHEAT_PEDIGREE.getUserDefinedFieldID());
            n.setNstat(Integer.valueOf(0));
            names.add(n);

            germplasms.put(g, names);
            
            GermplasmListData germplasmListData = this.createGermplasmListData();
            listData.put(g, germplasmListData);

        }
        
        return germList;
        
    }
        
    private GermplasmList createGermplasmsOtherCrop(
            Map<Germplasm, List<Name>> germplasms, Map<Germplasm, GermplasmListData> listData) {
        
        int NUMBER_OF_ENTRIES = 3;
        
        GermplasmList germList = createGermplasmList();
        
        for (int i=0; i< NUMBER_OF_ENTRIES; i++){
            Germplasm g = createGermplasm();

            List<Name> names = new ArrayList<Name>();
            names.add(createGermplasmName(1));
            germplasms.put(g, names);

            GermplasmListData germplasmListData = createGermplasmListData();
            listData.put(g, germplasmListData);
        }
        
        return germList;        
    }
    
    private GermplasmList createGermplasmList(){
        String name = "Test List #1_" + "_" + (int) Math.random()*100;
        GermplasmList germList = new GermplasmList(null, name, Long.valueOf(20140206)
                , "LST", Integer.valueOf(1), name + " Description", null, 1);
        return germList;
    }
    
    private Germplasm createGermplasm(){
        Germplasm g = new Germplasm();
        g.setGdate(Integer.valueOf(Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(new Date()))));
        g.setGnpgs(Integer.valueOf(0));
        g.setGpid1(Integer.valueOf(0));
        g.setGpid2(Integer.valueOf(0));
        g.setGrplce(Integer.valueOf(0));
        g.setLocationId(Integer.valueOf(9000));
        g.setMethodId(Integer.valueOf(1));
        g.setMgid(Integer.valueOf(1));
        g.setUserId(Integer.valueOf(1));
        g.setReferenceId(Integer.valueOf(1));
        return g;
    }
    
    private Name createGermplasmName(int i){
        Name n = new Name();
        n.setLocationId(Integer.valueOf(9000));
        n.setNdate(Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(new Date())));
        n.setNval("Germplasm_" + i + "_" + (int) Math.random()*100);
        n.setReferenceId(Integer.valueOf(1));
        n.setTypeId(Integer.valueOf(1));
        n.setNstat(Integer.valueOf(0));
        n.setUserId(Integer.valueOf(1));
        return n;
    }
    
    private GermplasmListData createGermplasmListData(){
        return  new GermplasmListData(null, null, Integer.valueOf(2), 1, "EntryCode", "SeedSource",
                "Germplasm Name 3", "GroupName", 0, 99992);
    }
    
    
    @Test
    public void testGetAllNurseryTypes() throws MiddlewareQueryException {
            List<ValueReference> nurseryTypes = fieldbookService.getAllNurseryTypes();
            
            for (ValueReference type : nurseryTypes){
                Debug.println(3, type.toString());
            }
            Debug.println(3, "NUMBER OF RECORDS: " + nurseryTypes.size());
    }
    
    @Test
    public void testGetAllPersons() throws MiddlewareQueryException {
            List<Person> persons = fieldbookService.getAllPersons();
            
            for (Person person : persons){
                Debug.println(3, person.toString());
            }
            Debug.println(3, "NUMBER OF RECORDS: " + persons.size());
            Debug.println(3, "Plots with Plants Selected: " + fieldbookService.countPlotsWithPlantsSelectedofNursery(-146));
    }
    
    @Test
    public void testCountPlotsWithPlantsSelectedofNursery() throws MiddlewareQueryException {
        Workbook workbook = TestNurseryWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        int id = dataImportService.saveDataset(workbook);
        Debug.println(3, "Plots with Plants Selected: " + fieldbookService.countPlotsWithPlantsSelectedofNursery(id));
    }
    
    @Test
    public void testGetNurseryVariableSettings() throws MiddlewareQueryException {
        Workbook workbook = TestNurseryWorkbookUtil.getTestWorkbook();
        workbook.print(0);
        int id = dataImportService.saveDataset(workbook);
        workbook = fieldbookService.getNurseryVariableSettings(id);
        workbook.print(0);
    }

    @Test
    public void testAddFieldLocation() throws MiddlewareQueryException {
        String fieldName = "Test Field JUnit";
        Integer parentLocationId = 17649;
        int result = fieldbookService.addFieldLocation(fieldName, parentLocationId);
        Debug.println(3, "Added: Location with id = " + result);
    }
    
    @Test
    public void testAddBlockLocation() throws MiddlewareQueryException {
        String blockName = "Test Block JUnit";
        Integer parentFieldId = -11;
        int result = fieldbookService.addBlockLocation(blockName, parentFieldId);
        Debug.println(3, "Added: Location with id = " + result);
    }
    
    
    
    
    
    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime 
                + " ns = " + ((double) elapsedTime / 1000000000) + " s");
    }


    @AfterClass
    public static void tearDown() throws Exception {
        if (serviceFactory != null) {
            serviceFactory.close();
        }
    }
}
