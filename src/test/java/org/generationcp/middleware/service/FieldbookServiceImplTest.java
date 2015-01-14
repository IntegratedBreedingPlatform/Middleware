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
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.StudyTestDataUtil;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.etl.WorkbookTest;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FieldbookServiceImplTest extends DataManagerIntegrationTest {
        
    private static FieldbookService fieldbookService;
    private static DataImportService dataImportService;

    @BeforeClass
    public static void setUp() throws Exception {
        fieldbookService = managerFactory.getFieldbookMiddlewareService();
        dataImportService = managerFactory.getDataImportService();
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
        Workbook workbook = WorkbookTest.getTestWorkbook();
        workbook.print(INDENT);
        int id = dataImportService.saveDataset(workbook);
        workbook = fieldbookService.getNurseryDataSet(id);
        workbook.print(INDENT);
    }
    
    @Test
    public void testSaveTrialMeasurementRows() throws MiddlewareQueryException {
    	Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);
        workbook.print(INDENT);
                
        int id = dataImportService.saveDataset(workbook);
        Workbook createdWorkbook = fieldbookService.getTrialDataSet(id);
        
        createdWorkbook = WorkbookTest.addEnvironmentAndConstantVariables(createdWorkbook);
        
        List<MeasurementRow> observations = createdWorkbook.getObservations();
        for (MeasurementRow observation : observations){
            List<MeasurementData> fields = observation.getDataList();
            for (MeasurementData field : fields){
                try {
                    if (field.getValue() != null){
                        field.setValue(Double.valueOf(Double.valueOf(field.getValue()) + 1).toString());
                        field.setValue(Integer.valueOf(Integer.valueOf(field.getValue()) + 1).toString());
                    }
                } catch (NumberFormatException e){
                    // Ignore. Update only numeric values
                }
            }
        }        
        
        fieldbookService.saveMeasurementRows(createdWorkbook);
        workbook = fieldbookService.getTrialDataSet(id);
        assertFalse(workbook.equals(createdWorkbook));
        
        assertEquals("Expected " + createdWorkbook.getTrialConditions().size() + " of records for trial conditions but got " 
        		+ workbook.getTrialConditions().size(), createdWorkbook.getTrialConditions().size(), 
        		workbook.getTrialConditions().size());
        assertTrue("Expected the same trial conditions retrieved but found a different condition.", 
        		WorkbookTest.areTrialVariablesSame(createdWorkbook.getTrialConditions(), workbook.getTrialConditions()));
        assertEquals("Expected " + createdWorkbook.getTrialConstants().size() + " of records for trial constants but got " 
        		+ workbook.getTrialConstants().size(), createdWorkbook.getTrialConstants().size(), 
        		workbook.getTrialConstants().size());
        assertTrue("Expected the same trial constants retrieved but found a different constant.", 
        		WorkbookTest.areTrialVariablesSame(createdWorkbook.getTrialConstants(), workbook.getTrialConstants()));
    }
    
    @Test
    public void testSaveNurseryMeasurementRows() throws MiddlewareQueryException {
    	Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.N);
        workbook.print(INDENT);
                
        int id = dataImportService.saveDataset(workbook);
        Workbook createdWorkbook = fieldbookService.getNurseryDataSet(id);
        
        createdWorkbook = WorkbookTest.addEnvironmentAndConstantVariables(createdWorkbook);
        
        List<MeasurementRow> observations = createdWorkbook.getObservations();
        for (MeasurementRow observation : observations){
            List<MeasurementData> fields = observation.getDataList();
            for (MeasurementData field : fields){
                try {
                    if (field.getValue() != null){
                        field.setValue(Double.valueOf(Double.valueOf(field.getValue()) + 1).toString());
                        field.setValue(Integer.valueOf(Integer.valueOf(field.getValue()) + 1).toString());
                    }
                } catch (NumberFormatException e){
                    // Ignore. Update only numeric values
                }
            }
        }        
        
        fieldbookService.saveMeasurementRows(createdWorkbook);
        workbook = fieldbookService.getNurseryDataSet(id);
        assertFalse(workbook.equals(createdWorkbook));
        
        assertEquals("Expected " + createdWorkbook.getTrialConditions().size() + " of records for trial conditions but got " 
        		+ workbook.getTrialConditions().size(), createdWorkbook.getTrialConditions().size(), 
        		workbook.getTrialConditions().size());
        assertTrue("Expected the same trial conditions retrieved but found a different condition.", 
        		WorkbookTest.areTrialVariablesSame(createdWorkbook.getTrialConditions(), workbook.getTrialConditions()));
        assertEquals("Expected " + createdWorkbook.getTrialConstants().size() + " of records for trial constants but got " 
        		+ workbook.getTrialConstants().size(), createdWorkbook.getTrialConstants().size(), 
        		workbook.getTrialConstants().size());
        assertTrue("Expected the same trial constants retrieved but found a different constant.", 
        		WorkbookTest.areTrialVariablesSame(createdWorkbook.getTrialConstants(), workbook.getTrialConstants()));
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
        Workbook workbook = WorkbookTest.getTestWorkbook();
        workbook.print(INDENT);
        int id = dataImportService.saveDataset(workbook);
        //Debug.println(INDENT, "Plots with Plants Selected: " + fieldbookService.countPlotsWithPlantsSelectedofNursery(id));
    }
    
    @Test
    public void testGetNurseryVariableSettings() throws MiddlewareQueryException {
        Workbook workbook = WorkbookTest.getTestWorkbook();
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
        Workbook workbook = WorkbookTest.getTestWorkbook();
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
        Workbook workbook = WorkbookTest.getTestWorkbook();
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
    
    @Test
    public void testGetAllTreatmentFactors() throws Exception {
    	List<Integer> hiddenFields = Arrays.asList(8200,8380,8210,8220,8400,8410,8581,8582);
    	List<StandardVariableReference> variables = fieldbookService.getAllTreatmentLevels(hiddenFields);
    	if (variables != null) {
    		for (StandardVariableReference variable : variables) {
    			System.out.println(variable);
    		}
    	}
    }
    
    @Test
    public void testSaveOrUpdateListDataProject() throws Exception {
    	List<ListDataProject> list = new ArrayList<ListDataProject>();
    	int projectId = -422;
    	Integer originalListId = 1;
    	list.add(new ListDataProject());
    	list.get(0).setCheckType(1);
    	list.get(0).setDesignation("DESIG1");
    	list.get(0).setEntryId(1);
    	list.get(0).setEntryCode("ABC1");
    	list.get(0).setGermplasmId(1);
    	//list.get(0).setGroupName("CROSS1");
    	list.get(0).setSeedSource("SOURCE1");
    	list.get(0).setListDataProjectId(null);
//    	fieldbookService.saveOrUpdateListDataProject(projectId, GermplasmListType.NURSERY, originalListId, list);
//    	fieldbookService.saveOrUpdateListDataProject(projectId, GermplasmListType.TRIAL, originalListId, list);
    	fieldbookService.saveOrUpdateListDataProject(projectId, GermplasmListType.ADVANCED, originalListId, list, 0);
//    	fieldbookService.saveOrUpdateListDataProject(projectId, GermplasmListType.CHECK, null, list);
    }
    
    @Test
    public void testGetGermplasmListsByProjectId() throws Exception {
    	int projectId = -422;
    	System.out.println("NURSERY");
    	List<GermplasmList> lists = fieldbookService.getGermplasmListsByProjectId(projectId, GermplasmListType.NURSERY);
    	for (GermplasmList list : lists) {
    		System.out.println(list);
    	}
    	System.out.println("TRIAL");
    	lists = fieldbookService.getGermplasmListsByProjectId(projectId, GermplasmListType.TRIAL);
    	for (GermplasmList list : lists) {
    		System.out.println(list);
    	}
    	System.out.println("ADVANCED");
    	lists = fieldbookService.getGermplasmListsByProjectId(projectId, GermplasmListType.ADVANCED);
    	for (GermplasmList list : lists) {
    		System.out.println(list);
    	}
    	System.out.println("CHECK");
    	lists = fieldbookService.getGermplasmListsByProjectId(projectId, GermplasmListType.CHECK);
    	for (GermplasmList list : lists) {
    		System.out.println(list);
    	}
    }
    
    @Test
    public void testGetListDataProject() throws Exception {
    	int listId = -31;
    	System.out.println(fieldbookService.getListDataProject(listId));
    }
    
    @Test
    public void testDeleteListDataProjects() throws Exception {
    	int projectId = -422;
    	
    	fieldbookService.deleteListDataProjects(projectId, GermplasmListType.ADVANCED);
    }
    
    @Test
    public void testDeleteStudy() throws Exception {
    	StudyTestDataUtil studyTestDataUtil = StudyTestDataUtil.getInstance();
    	DmsProject testFolder = studyTestDataUtil.createFolderTestData();
    	DmsProject testStudy1 = studyTestDataUtil.createStudyTestData();
    	DmsProject testStudy2 = studyTestDataUtil.createStudyTestDataWithActiveStatus();
    	fieldbookService.deleteStudy(testFolder.getProjectId());
    	fieldbookService.deleteStudy(testStudy1.getProjectId());
    	fieldbookService.deleteStudy(testStudy2.getProjectId());
    	
    	boolean folderExists = false;
    	boolean study1Exists = false;
    	boolean study2Exists = false;
    	List<FolderReference> rootFolders = studyTestDataUtil.getLocalRootFolders();
    	for (FolderReference folderReference : rootFolders) {
			if(folderReference.getId().equals(testFolder.getProjectId())) {
				folderExists = true;
			}
			if(folderReference.getId().equals(testStudy1.getProjectId())) {
				study1Exists = true;
			}
			if(folderReference.getId().equals(testStudy2.getProjectId())) {
				study2Exists = true;
			}
		}
    	assertFalse("Folder should no longer be found",folderExists);
    	assertFalse("Study should no longer be found",study1Exists);
    	assertFalse("Study should no longer be found",study2Exists);
    }
    
}
