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
import org.generationcp.middleware.domain.oms.TermId;
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
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestFieldbookServiceImpl extends TestOutputFormatter{
        
    private static ServiceFactory serviceFactory;
    private static FieldbookService fieldbookService;
    private static DataImportService dataImportService;

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

    @Test
    public void testGetAllLocalNurseryDetails() throws MiddlewareQueryException {

        List<StudyDetails> nurseryStudyDetails = fieldbookService.getAllLocalNurseryDetails();
        for (StudyDetails study : nurseryStudyDetails){
            study.print(INDENT);
        }
        Debug.println(INDENT, "#RECORDS: " + nurseryStudyDetails.size());

    }


    @Test
    public void testGetAllLocalTrialStudyDetails() throws MiddlewareQueryException {
        List<StudyDetails> studyDetails = fieldbookService.getAllLocalTrialStudyDetails();
        for (StudyDetails study : studyDetails){
            study.print(INDENT);
        }
        Debug.println(INDENT, "#RECORDS: " + studyDetails.size());

    }
    
    @Test
    public void testGetFieldMapCountsOfTrial() throws MiddlewareQueryException{
        List<Integer> trialIds = new ArrayList<Integer>();
        trialIds.add(Integer.valueOf(1)); 
        List<FieldMapInfo> fieldMapCount = fieldbookService.getFieldMapInfoOfTrial(trialIds);
        for (FieldMapInfo fieldMapInfo : fieldMapCount) {
            fieldMapInfo.print(INDENT);
        }
        //assertTrue(fieldMapCount.getEntryCount() > 0);      
    }
    
    @Test
    public void testGetFieldMapCountsOfNursery() throws MiddlewareQueryException{
        List<Integer> nurseryIds = new ArrayList<Integer>();
        nurseryIds.add(Integer.valueOf(5734)); 
        List<FieldMapInfo> fieldMapCount = fieldbookService.getFieldMapInfoOfNursery(nurseryIds);
        for (FieldMapInfo fieldMapInfo : fieldMapCount) {
            fieldMapInfo.print(INDENT);
        }
       // assertTrue(fieldMapCount.getEntryCount() > 0);      
    }

    @Test
    public void testGetAllFieldMapsInBlockByTrialInstanceId() throws MiddlewareQueryException{
        List<FieldMapInfo> fieldMapCount = fieldbookService.getAllFieldMapsInBlockByTrialInstanceId(-2, -1);
        for (FieldMapInfo fieldMapInfo : fieldMapCount) {
            fieldMapInfo.print(INDENT);
        }
        //assertTrue(fieldMapCount.getEntryCount() > 0);      
    }
    

    @Test
    public void testGetAllLocations() throws MiddlewareQueryException {
        List<Location> locations = fieldbookService.getAllLocations();
        Debug.printObjects(locations);
    }

    @Test
    public void testGetAllBreedingLocations() throws MiddlewareQueryException {
        List<Location> locations = fieldbookService.getAllBreedingLocations();
        Debug.printObjects(locations);
    }

    @Test
    public void testGetAllSeedLocations() throws MiddlewareQueryException {
        List<Location> locations = fieldbookService.getAllSeedLocations();
        Debug.printObjects(locations);
    }

    @Test
    public void testGetNurseryDataSet() throws MiddlewareQueryException {
        Workbook workbook = TestNurseryWorkbookUtil.getTestWorkbook();
        workbook.print(INDENT);
        int id = dataImportService.saveDataset(workbook);
        workbook = fieldbookService.getNurseryDataSet(id);
        workbook.print(INDENT);
    }
    
    @Test
    public void testSaveMeasurementRows() throws MiddlewareQueryException {

        // Assumption: there is at least 1 local nursery stored in the database
        int id = fieldbookService.getAllLocalNurseryDetails().get(0).getId();
//    	int id = -167;
        Workbook workbook = fieldbookService.getNurseryDataSet(id);
        workbook.print(INDENT);
        
        List<MeasurementRow> observations = workbook.getObservations();
        for (MeasurementRow observation : observations){
            List<MeasurementData> fields = observation.getDataList();
            for (MeasurementData field : fields){
                try {
                    //Debug.println(INDENT, "Original: " + field.toString());
                    if (field.getValue() != null){
                        field.setValue(Double.valueOf(Double.valueOf(field.getValue()) + 1).toString());
                        field.setValue(Integer.valueOf(Integer.valueOf(field.getValue()) + 1).toString());
                        //Debug.println(INDENT, "Updated: " + field.toString());
                    }
                } catch (NumberFormatException e){
                    // Ignore. Update only numeric values
                }
            }
        }
        
//        MeasurementVariable bhtrait = new MeasurementVariable(22448, "BH-LOCAL", "BH-LOCAL DESC", null, null, null, null, null, null);
//        bhtrait.setOperation(Operation.ADD);
//        bhtrait.setStoredIn(TermId.OBSERVATION_VARIATE.getId());
//        workbook.getVariates().add(bhtrait);
//        for (MeasurementVariable var : workbook.getVariates()) {
//        	if (var.getTermId() == 8390) { //NOTES trait
//        		var.setName("LOCAL " + var.getName());
//        		var.setDescription("LOCAL " + var.getDescription());
//        		var.setOperation(Operation.UPDATE);
//        	}
//        }
//        for (MeasurementVariable var : workbook.getConditions()) {
//        	if (var.getTermId() == -160) {
//        		var.setOperation(Operation.DELETE);
//        	}
//        	else if (var.getTermId() == 8100) {
//        		var.setOperation(Operation.UPDATE);
//        		var.setName("INVESTIGATOR_NAME");
//        		var.setDescription("INVESTIGATOR_DESCRIPTION");
//        	}
//        }
        
        fieldbookService.saveMeasurementRows(workbook);
        Workbook workbook2 = fieldbookService.getNurseryDataSet(id);
        workbook2.print(INDENT);
        assertFalse(workbook.equals(workbook2));
    }
    
    @Test
    public void testGetStandardVariableIdByPropertyScaleMethodRole() throws MiddlewareQueryException {
        String property = "Germplasm entry";
        String scale = "Number";
        String method = "Enumerated";
        Integer termId = fieldbookService.getStandardVariableIdByPropertyScaleMethodRole(
                                    property, scale, method, PhenotypicType.GERMPLASM);
        Debug.println(INDENT, termId.toString());
        assertEquals((Integer) 8230, termId);
    }

    @Test
    public void testGetAllBreedingMethods() throws MiddlewareQueryException {
        List<Method> methods = fieldbookService.getAllBreedingMethods(false);
        assertFalse(methods.isEmpty());
        Debug.printObjects(INDENT, methods);
    }

    @Test
    public void testSaveNurseryAdvanceGermplasmListCimmytWheat() throws MiddlewareQueryException {
        Map<Germplasm, List<Name>> germplasms = new HashMap<Germplasm, List<Name>>();
        Map<Germplasm, GermplasmListData> listData = new HashMap<Germplasm, GermplasmListData>();
        GermplasmList germplasmList = createGermplasmsCimmytWheat(germplasms, listData);
        
        Integer listId = fieldbookService.saveNurseryAdvanceGermplasmList(germplasms, listData, germplasmList);

        assertTrue(listId != null && listId < 0);
        
        Debug.println(INDENT, "Germplasm List Added: ");
        Debug.println(INDENT*2, germplasmList.toString());
        Debug.println(INDENT, "Germplasms Added: ");
        Debug.printObjects(INDENT*2, new ArrayList<Germplasm>(germplasms.keySet()));
    }
    
    @Test
    public void testSaveNurseryAdvanceGermplasmListOtherCrop() throws MiddlewareQueryException {
        Map<Germplasm, List<Name>> germplasms = new HashMap<Germplasm, List<Name>>();
        Map<Germplasm, GermplasmListData> listData = new HashMap<Germplasm, GermplasmListData>();
        GermplasmList germplasmList = createGermplasmsOtherCrop(germplasms, listData);
        
        Integer listId = fieldbookService.saveNurseryAdvanceGermplasmList(germplasms, listData, germplasmList);

        assertTrue(listId != null && listId < 0);
        
        Debug.println(INDENT, "Germplasm List Added: ");
        Debug.println(INDENT*2, germplasmList.toString());
        Debug.println(INDENT, "Germplasms Added: ");
        Debug.printObjects(INDENT*2, new ArrayList<Germplasm>(germplasms.keySet()));
    }

    @Test
    public void testGetDistinctStandardVariableValues() throws Exception {
    	int stdVarId = 8250;
    	getDistinctStandardVariableValues(stdVarId);
    	stdVarId = 8135;
        getDistinctStandardVariableValues(stdVarId);
    	stdVarId = 8170;
        getDistinctStandardVariableValues(stdVarId);
    	stdVarId = 8191;
        getDistinctStandardVariableValues(stdVarId);
    	stdVarId = 8192;
        getDistinctStandardVariableValues(stdVarId);
    	stdVarId = 8193;
        getDistinctStandardVariableValues(stdVarId);
    	stdVarId = 8194;
        getDistinctStandardVariableValues(stdVarId);
    	stdVarId = 8007;
        getDistinctStandardVariableValues(stdVarId);
    }

    private void getDistinctStandardVariableValues(int stdVarId) throws MiddlewareQueryException{
        List<ValueReference> list = fieldbookService.getDistinctStandardVariableValues(stdVarId);
        Debug.println(INDENT, "StandardVariable ID: " + stdVarId);
        Debug.printObjects(INDENT+2, list);
        Debug.println("");
    }
    
    private GermplasmList createGermplasmsCimmytWheat(
            Map<Germplasm, List<Name>> germplasms, Map<Germplasm, GermplasmListData> listData) {
        
        int numberOfEntries = 3;
        
        GermplasmList germList = createGermplasmList();
        
        for (int i=0; i< numberOfEntries; i++){
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
        
        int numberOfEntries = 3;
        
        GermplasmList germList = createGermplasmList();
        
        for (int i=0; i< numberOfEntries; i++){
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
        String name = "Test List #1_" + "_" + (int) (Math.random()*100);
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
        n.setNval("Germplasm_" + i + "_" + (int) (Math.random()*100));
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
            Debug.printObjects(INDENT, nurseryTypes);
    }
    
    @Test
    public void testGetAllPersons() throws MiddlewareQueryException {
            List<Person> persons = fieldbookService.getAllPersons();
            Debug.printObjects(INDENT, persons);
            //Debug.println(INDENT, "Plots with Plants Selected: " + fieldbookService.countPlotsWithPlantsSelectedofNursery(-146));
    }
    
    @Test
    public void testCountPlotsWithPlantsSelectedofNursery() throws MiddlewareQueryException {
        Workbook workbook = TestNurseryWorkbookUtil.getTestWorkbook();
        workbook.print(INDENT);
        int id = dataImportService.saveDataset(workbook);
        //Debug.println(INDENT, "Plots with Plants Selected: " + fieldbookService.countPlotsWithPlantsSelectedofNursery(id));
    }
    
    @Test
    public void testGetNurseryVariableSettings() throws MiddlewareQueryException {
        Workbook workbook = TestNurseryWorkbookUtil.getTestWorkbook();
        workbook.print(INDENT);
        int id = dataImportService.saveDataset(workbook);
        workbook = fieldbookService.getStudyVariableSettings(id, true);
        workbook.print(INDENT);
    }
    
    @Test
    public void testGetBlockId() throws MiddlewareQueryException {
    	int datasetId = -167;
    	String trialInstance = "1";
    	System.out.println(fieldbookService.getBlockId(datasetId, trialInstance));
    }

    @Test
    public void testAddFieldLocation() throws MiddlewareQueryException {
        String fieldName = "Test Field JUnit";
        Integer parentLocationId = 17649;
        int result = fieldbookService.addFieldLocation(fieldName, parentLocationId, -1);
        Debug.println(INDENT, "Added: Location with id = " + result);
    }
    
    @Test
    public void testAddBlockLocation() throws MiddlewareQueryException {
        String blockName = "Test Block JUnit";
        Integer parentFieldId = -11;
        int result = fieldbookService.addBlockLocation(blockName, parentFieldId, -1);
        Debug.println(INDENT, "Added: Location with id = " + result);
    }
    
    @Test
    public void testGetStudyType() throws MiddlewareQueryException  {
    	int studyId = -74;
    	TermId studyType = fieldbookService.getStudyType(studyId);
    	System.out.println("STUDY TYPE IS " + studyType.name());
    }
    
    @Test
    public void testGetFolderNameById() throws MiddlewareQueryException  {
        String folderName = fieldbookService.getFolderNameById(1);
        System.out.println("Folder Name is: " + folderName);
    }
    
    @Test
    public void testCheckIfStudyHasFieldmap()	throws MiddlewareQueryException {
    	int studyId = -12;
    	System.out.println("RESULT1 = " + fieldbookService.checkIfStudyHasFieldmap(studyId));
    	studyId = -18;
    	System.out.println("RESULT2 = " + fieldbookService.checkIfStudyHasFieldmap(studyId));
    }
    
    @Test
    public void testCheckIfStudyHasMeasurementData() throws MiddlewareQueryException {
        Workbook workbook = TestNurseryWorkbookUtil.getTestWorkbook();
        workbook.print(INDENT);
        int id = dataImportService.saveDataset(workbook);
        workbook = fieldbookService.getNurseryDataSet(id);
        List<Integer> variateIds =  new ArrayList<Integer>();
        variateIds.add(new Integer(21980));
        variateIds.add(new Integer(21981));
        
        boolean hasMeasurementData = fieldbookService.checkIfStudyHasMeasurementData(workbook.getMeasurementDatesetId(), variateIds);
        System.out.println(hasMeasurementData);
    }
    
    @Test
    public void testDeleteObservationsOfStudy() throws MiddlewareQueryException {
        Workbook workbook = TestNurseryWorkbookUtil.getTestWorkbook();
        workbook.print(INDENT);
        int id = dataImportService.saveDataset(workbook);
        workbook = fieldbookService.getNurseryDataSet(id);
        
        fieldbookService.deleteObservationsOfStudy(workbook.getMeasurementDatesetId());
        workbook.print(INDENT);
    }
    
    @Test
    public void testGetProjectIdByName() throws Exception {
    	String name = "ROOT STUDY";
    	System.out.println("ID IS " + fieldbookService.getProjectIdByName(name));
    }
    
    @Test
    public void testGetGidsByName() throws Exception {
    	String name = "CG7";
    	System.out.println("GIDS = " + fieldbookService.getGermplasmIdsByName(name));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (serviceFactory != null) {
            serviceFactory.close();
        }
    }

}
