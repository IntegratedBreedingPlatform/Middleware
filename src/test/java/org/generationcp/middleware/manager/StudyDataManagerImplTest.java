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

package org.generationcp.middleware.manager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.StudyTestDataUtil;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Stocks;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.domain.workbench.StudyNode;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.FieldMapDataUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class StudyDataManagerImplTest extends DataManagerIntegrationTest {

    private static final Integer STUDY_ID   = 10010;
    private static final Integer DATASET_ID = 10045;
    private static final Integer ROOT_STUDY_FOLDER = 1;

    private static StudyDataManager manager;
    private static OntologyDataManager ontologyManager;
    private static Project commonTestProject;
    private static WorkbenchTestDataUtil workbenchTestDataUtil;
    private static final int LEVEL = 1;
    private static final int NAME_TYPE = 7;

    @BeforeClass
    public static void setUp() throws Exception {
        manager = managerFactory.getNewStudyDataManager();
        ontologyManager = managerFactory.getNewOntologyDataManager();
        workbenchTestDataUtil = WorkbenchTestDataUtil.getInstance();
        workbenchTestDataUtil.setUpWorkbench();
        commonTestProject = workbenchTestDataUtil.getCommonTestProject();
    }

    @Test
    public void testGetStudy() throws Exception {
        Study study = manager.getStudy(STUDY_ID);
        assertNotNull(study);
        Debug.println(INDENT, "ID: " + study.getId());
        Debug.println(INDENT, "Name: " + study.getName());
        Debug.println(INDENT, "Title: " + study.getTitle());
        Debug.println(INDENT, "PI: " + study.getPrimaryInvestigator());
        Debug.println(INDENT, "Start Date:" + study.getStartDate());
        Debug.println(INDENT, "Creation Date: " + study.getCreationDate());
        Debug.println(INDENT, "Study status: " + study.getStatus());
        Debug.println(INDENT, "Study type: " + study.getType());
    }

    @Test
    public void testGetStudyConditions() throws Exception {
        Study study = manager.getStudy(STUDY_ID);
        assertNotNull(study);
        VariableList vList = study.getConditions();
        for (Variable v : vList.getVariables()) {
            Debug.print(0, "name[" + v.getVariableType().getStandardVariable().getName() + "]=");
            Debug.println(INDENT, v.getDisplayValue());
        }
    }

    @Test
    public void testGetAllStudyFactor() throws Exception {
        Debug.println(INDENT, "testGetFactorDetails");
        int studyId = 10010;
        VariableTypeList factors = manager.getAllStudyFactors(studyId);
        assertNotNull(factors);
        assertTrue(factors.getVariableTypes().size() > 0);
        Debug.println(INDENT, "FACTORS RETRIEVED " + factors.getVariableTypes().size());
        factors.print(INDENT);
    }

    @Test
    public void testGetAllStudyVariates() throws Exception {
        int studyId = 10010;
        VariableTypeList variates = manager.getAllStudyVariates(studyId);
        assertNotNull(variates);
        assertTrue(variates.getVariableTypes().size() > 0);
        variates.print(INDENT);
    }

    @Test
    public void testGetStudiesByFolder() throws Exception {
        int folderId = 1030;
        StudyResultSet resultSet = manager.searchStudies(new ParentFolderStudyQueryFilter(folderId), 5);
        Debug.println(INDENT, "testGetStudiesByFolder(" + folderId + "): " + resultSet.size());
        assertTrue(resultSet.size() > 0);
        while (resultSet.hasMore()) {
            StudyReference studyRef = resultSet.next();
            Debug.println(INDENT, studyRef.toString());
        }
    }

    @Test
    public void testSearchStudiesForName() throws Exception {
        Debug.println(INDENT, "testSearchStudiesForName");
        BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();

        filter.setName("FooFoo"); // INVALID: Not a study, should not find any
                                  // studies
        StudyResultSet resultSet = manager.searchStudies(filter, 10);
        assertTrue(resultSet.size() == 0);

        filter.setName("RYT2000WS"); // VALID: is a study

        resultSet = manager.searchStudies(filter, 10);
        Debug.println(INDENT, "INPUT: " + filter);
        Debug.println(INDENT, "Size: " + resultSet.size());
        while (resultSet.hasMore()) {
            Debug.println(INDENT, "\t" + resultSet.next());
            System.out.flush();
        }
        /*
         * to test deleted study, run in mysql: update projectprop set value =
         * 12990 where type_id = 8006 and project_id = (select project_id from
         * project where name = 'RYT2000WS') then uncomment the test below
         */
        // Assert.assertTrue(resultSet.size() == 0);
    }

    @Test
    public void testSearchStudiesForStartDate() throws Exception {
        BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
        filter.setStartDate(20050119);

        StudyResultSet resultSet = manager.searchStudies(filter, 10);

        Debug.println(INDENT, "INPUT: " + filter);
        Debug.println(INDENT, "Size: " + resultSet.size());
        while (resultSet.hasMore()) {
            Debug.println(INDENT, "\t" + resultSet.next());
            System.out.flush();
        }
        /*
         * to test deleted study, uncomment line above, then run in mysql:
         * update projectprop set value = 12990 where type_id = 8006 and
         * project_id = 5739 Note: 5739 is one of the project_id returned then
         * uncomment the test below
         */
        // Assert.assertTrue(resultSet.size() == before-1);
    }

    @Test
    public void testSearchStudiesForSeason() throws Exception {
        Season seasons[] = { Season.GENERAL, Season.DRY, Season.WET };
        for (Season season : seasons) {
            Debug.println(INDENT, "Season: " + season);
            BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
            filter.setSeason(season);
            StudyResultSet resultSet = manager.searchStudies(filter, 10);
            Debug.println(INDENT, "Size: " + resultSet.size());
            while (resultSet.hasMore()) {
                Debug.println(INDENT, "\t" + resultSet.next());
                System.out.flush();
            }
        }
    }

    @Test
    public void testSearchStudiesForCountry() throws Exception {
        BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();

        filter.setCountry("Republic of the Philippines");

        StudyResultSet resultSet = manager.searchStudies(filter, 10);
        Debug.println(INDENT, "INPUT: " + filter);
        Debug.println(INDENT, "Size: " + resultSet.size());
        while (resultSet.hasMore()) {
            Debug.println(INDENT, "\t" + resultSet.next());
            System.out.flush();
        }
    }

    @Test
    public void testSearchStudiesForAll() throws Exception {
        BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
        filter.setStartDate(20050119);
        filter.setName("RYT2000WS"); // VALID: is a study
        filter.setCountry("Republic of the Philippines");
        filter.setSeason(Season.DRY);

        StudyResultSet resultSet = manager.searchStudies(filter, 10);
        Debug.println(INDENT, "INPUT: " + filter);
        Debug.println(INDENT, "Size: " + resultSet.size());
        while (resultSet.hasMore()) {
            Debug.println(INDENT, "\t" + resultSet.next());
            System.out.flush();
        }
    }

    @Test
    public void testGetRootFolders() throws Exception {    	
    	List<FolderReference> rootFolders = manager.getRootFolders(commonTestProject.getUniqueID());
        assertNotNull(rootFolders);
        //this should contain the nursery and trial templates
        assertFalse(rootFolders.isEmpty());
        
        StudyTestDataUtil studyTestDataUtil = StudyTestDataUtil.getInstance();
    	String uniqueId = commonTestProject.getUniqueID();
    	studyTestDataUtil.createFolderTestData(uniqueId);
    	studyTestDataUtil.createStudyTestData(uniqueId);
    	
    	rootFolders = manager.getRootFolders(commonTestProject.getUniqueID());
        assertNotNull(rootFolders);
        assertFalse(rootFolders.isEmpty());
        
    	Debug.println(INDENT, "testGetRootFolders(): " + rootFolders.size());
        for (FolderReference node : rootFolders) {
            Debug.println(INDENT, "   " + node);
            assertEquals(node.getParentFolderId(),ROOT_STUDY_FOLDER);
        }
    }

    @Test
    public void testGetChildrenOfFolder() throws Exception {
    	StudyTestDataUtil studyTestDataUtil = StudyTestDataUtil.getInstance();
    	String uniqueId = commonTestProject.getUniqueID();
    	studyTestDataUtil.createFolderTestData(uniqueId);
    	
        List<Integer> folderIds = Arrays.asList(25000, 1);
        for (Integer folderId : folderIds) {
        	Debug.println(INDENT, " folderId = " + folderId);
        	List<Reference> childrenNodes = manager.getChildrenOfFolder(folderId, commonTestProject.getUniqueID());
            assertNotNull(childrenNodes);
            assertTrue(childrenNodes.size() > 0);
            Debug.println(INDENT, "testGetChildrenOfFolder(folderId=" + folderId + "): " + childrenNodes.size());
            for (Reference node : childrenNodes) {
                Debug.println(INDENT, "   " + node);
            }
        }
    }

    @Test
    public void testGetDatasetNodesByStudyId() throws Exception {
        Integer studyId = 10010;
        List<DatasetReference> datasetReferences = manager.getDatasetReferences(studyId);
        assertNotNull(datasetReferences);
        assertTrue(datasetReferences.size() > 0);
        Debug.println(INDENT, "Dataset Nodes By Study Id Count: " + datasetReferences.size());
        for (DatasetReference node : datasetReferences) {
            Debug.println(INDENT, "   " + node);
        }
    }

    @Test
    public void testSearchStudiesByGid() throws Exception {
        Integer gid = 2434138;
        GidStudyQueryFilter filter = new GidStudyQueryFilter(gid);
        StudyResultSet resultSet = manager.searchStudies(filter, 50);
        assertNotNull(resultSet);
        Debug.println(INDENT, "Study Count: " + resultSet.size());
        while (resultSet.hasMore()) {
            StudyReference studyRef = resultSet.next();
            Debug.println(INDENT, studyRef.toString());
        }
    }

    @Test
    public void testAddStudy() throws Exception {

        int parentStudyId = 1;

        VariableTypeList typeList = new VariableTypeList();

        VariableList variableList = new VariableList();

        Variable variable = createVariable(TermId.STUDY_NAME.getId(), "Study Name " + new Random().nextInt(10000), 1);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        variable = createVariable(TermId.STUDY_TITLE.getId(), "Study Description", 2);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        StudyValues studyValues = new StudyValues();
        studyValues.setVariableList(variableList);

        VariableList locationVariableList = createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1",
                "prop2");
        studyValues.setLocationId(manager.addTrialEnvironment(locationVariableList));

        VariableList germplasmVariableList = createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
        studyValues.setGermplasmId(manager.addStock(germplasmVariableList));

        StudyReference studyRef = manager.addStudy(parentStudyId, typeList, studyValues, commonTestProject.getUniqueID());
        
        assertNotNull(studyRef.getId());
        assertTrue(studyRef.getId() != 0);
        Debug.println(INDENT, "testAddStudy(): " + studyRef);
    }

    @Test
    public void testAddStudyWithNoLocation() throws Exception {

        int parentStudyId = 1;

        VariableTypeList typeList = new VariableTypeList();

        VariableList variableList = new VariableList();

        Variable variable = createVariable(TermId.STUDY_NAME.getId(), "Study Name " + new Random().nextInt(10000), 1);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        variable = createVariable(TermId.STUDY_TITLE.getId(), "Study Description", 2);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        typeList.add(createVariableType(TermId.GID.getId(), "GID", "gid", 3));

        StudyValues studyValues = new StudyValues();
        studyValues.setVariableList(variableList);

        studyValues.setLocationId(null);

        VariableList germplasmVariableList = createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
        studyValues.setGermplasmId(manager.addStock(germplasmVariableList));

        StudyReference studyRef = manager.addStudy(parentStudyId, typeList, studyValues, null);

        assertTrue(studyRef.getId() < 0);
        Study study = manager.getStudy(studyRef.getId());
        study.print(INDENT);
    }

    @Test
    public void testGetDataSet() throws Exception {
        for (int i = 10015; i <= 10075; i += 10) {
            DataSet dataSet = manager.getDataSet(i);
            dataSet.print(INDENT);
        }
    }

    @Test
    public void testGetDataSetOfSorghum() throws Exception { // GCP-4986
        int dataSetId = -4; // Local sorghum
        DataSet dataSet = manager.getDataSet(dataSetId);
        dataSet.print(INDENT);
        List<Experiment> experiments = manager.getExperiments(dataSetId, 0, (int) manager.countExperiments(dataSetId));
        Debug.println(INDENT, " Experiments: " + experiments.size());

        Debug.println(INDENT, " Variables.getDisplayValue(): " + experiments.size());
        for (Experiment experiment : experiments) {
            List<Variable> variables = new ArrayList<Variable>();

            VariableList factors = experiment.getFactors();
            if (factors != null) {
                variables.addAll(factors.getVariables());
            }

            VariableList variates = experiment.getVariates();
            if (variates != null) {
                variables.addAll(variates.getVariables());
            }

            for (Variable variable : variables) {
                if (!("GID".equals(variable.getVariableType().getLocalName().trim()))) {
                    String value = variable.getDisplayValue();
                    Debug.println(INDENT, "Data Type is "
                            + variable.getVariableType().getStandardVariable().getDataType().getName());
                    Debug.println(INDENT, "\t" + experiment.getId() + "  :  "
                            + variable.getVariableType().getStandardVariable().getName() + "  :  " + value);
                }
            }
        }
    }

    @Test
    public void testCountExperiments() throws Exception {
        Debug.println(INDENT, "Dataset Experiment Count: " + manager.countExperiments(DATASET_ID));
    }

    @Test
    public void testGetExperiments() throws Exception {
        for (int i = 0; i < 2; i++) {
            List<Experiment> experiments = manager.getExperiments(DATASET_ID, 50 * i, 50);
            for (Experiment experiment : experiments) {
                experiment.print(INDENT);
            }
        }
    }

    @Test
    public void testGetExperimentsWithAverage() throws Exception {
        List<Experiment> experiments = manager.getExperiments(5803, 0, 50);
        for (Experiment experiment : experiments) {
            experiment.print(INDENT);
        }
    }
    
    @Test
    public void testGetExperimentsWithTrialEnvironments() throws Exception {
    	List<Experiment> experiments = manager.getExperimentsWithTrialEnvironment(5803, 5803, 0, 50);
        for (Experiment experiment : experiments) {
            experiment.print(INDENT);
        }
    }

    @Test
    public void testAddDataSet() throws Exception {
        // Parent study, assign a parent study id value, if none exists in db,
        // you may create a dummy one. or you may run testAddStudy first to
        // create
        // the study
        int parentStudyId = -1;

        VariableTypeList typeList = new VariableTypeList();
        VariableList variableList = new VariableList();
        Variable variable;

        // please make sure that the study name is unique and does not exist in
        // the db.
        variable = createVariable(TermId.DATASET_NAME.getId(), "My Dataset Name " + new Random().nextInt(10000), 1);
        typeList.add(variable.getVariableType());
        updateVariableType(variable.getVariableType(), "DATASET_NAME", "Dataset name (local)");
        variableList.add(variable);

        variable = createVariable(TermId.DATASET_TITLE.getId(), "My Dataset Description", 2);
        typeList.add(variable.getVariableType());
        updateVariableType(variable.getVariableType(), "DATASET_TITLE", "Dataset title (local)");
        variableList.add(variable);

        variable = createVariable(TermId.DATASET_TYPE.getId(), "10070", 3);
        typeList.add(variable.getVariableType());
        updateVariableType(variable.getVariableType(), "DATASET_TYPE", "Dataset type (local)");
        variableList.add(variable);

        DatasetValues datasetValues = new DatasetValues();
        datasetValues.setVariables(variableList);

        DatasetReference datasetReference = manager.addDataSet(parentStudyId, typeList, datasetValues, null);
        Debug.println(INDENT, "Dataset added : " + datasetReference);

    }

    @Test
    public void testAddDatasetWithNoDataType() throws Exception {
        Debug.println(INDENT, "Test addDatasetWithNoCoreValues");
        StudyReference studyRef = this.addTestStudy();
        VariableTypeList typeList = new VariableTypeList();

        DatasetValues datasetValues = new DatasetValues();
        datasetValues.setName("No Datatype dataset" + new Random().nextInt(10000));
        datasetValues.setDescription("whatever ds");

        VariableType variableType = createVariableType(18000, "Grain Yield", "whatever", 4);
        typeList.add(variableType);

        variableType = createVariableType(18050, "Disease Pressure", "whatever", 5);
        typeList.add(variableType);

        variableType = createVariableType(8200, "Plot No", "whatever", 6);
        typeList.add(variableType);

        DatasetReference dataSetRef = manager.addDataSet(studyRef.getId(), typeList, datasetValues, null);

        DataSet dataSet = manager.getDataSet(dataSetRef.getId());
        dataSet.print(INDENT);
    }

    @Test
    public void testAddDataSetVariableType() throws Exception {
        // Parent study, assign a parent study id value, if none exists in db,
        // you may create a dummy one. or you may run testAddStudy first to
        // create
        // the study
        int parentStudyId = -1;

        VariableTypeList typeList = new VariableTypeList();
        VariableList variableList = new VariableList();
        Variable variable;

        // please make sure that the study name is unique and does not exist in
        // the db.
        variable = createVariable(TermId.DATASET_NAME.getId(), "My Dataset Name " + new Random().nextInt(10000), 1);
        typeList.add(variable.getVariableType());
        updateVariableType(variable.getVariableType(), "DATASET_NAME", "Dataset name (local)");
        variableList.add(variable);

        variable = createVariable(TermId.DATASET_TITLE.getId(), "My Dataset Description", 2);
        typeList.add(variable.getVariableType());
        updateVariableType(variable.getVariableType(), "DATASET_TITLE", "Dataset title (local)");
        variableList.add(variable);

        variable = createVariable(TermId.DATASET_TYPE.getId(), "10070", 3);
        typeList.add(variable.getVariableType());
        updateVariableType(variable.getVariableType(), "DATASET_TYPE", "Dataset type (local)");
        variableList.add(variable);

        DatasetValues datasetValues = new DatasetValues();
        datasetValues.setVariables(variableList);

        DatasetReference datasetReference = manager.addDataSet(parentStudyId, typeList, datasetValues, null);
        Debug.println(INDENT, "Dataset added : " + datasetReference);

        DataSet dataSet = manager.getDataSet(datasetReference.getId());
        Debug.println(INDENT, "Original Dataset");
        dataSet.print(3);

        VariableType variableType = new VariableType();
        variableType.setLocalName("Dog");
        variableType.setLocalDescription("Man's best friend");
        variableType.setStandardVariable(ontologyManager.getStandardVariable(8240));
        variableType.setRank(99);
        manager.addDataSetVariableType(dataSet.getId(), variableType);

        dataSet = manager.getDataSet(datasetReference.getId());
        Debug.println(INDENT, "Modified Dataset");
        dataSet.print(3);

    }

    @Test
    public void testAddExperiment() throws Exception {
        List<Experiment> experiments = manager.getExperiments(10015, 0, /* 1093 */1);
        int dataSetId = -1;
        ExperimentValues experimentValues = new ExperimentValues();
        List<Variable> varList = new ArrayList<Variable>();
        varList.addAll(experiments.get(0).getFactors().getVariables());
        varList.addAll(experiments.get(0).getVariates().getVariables());
        VariableList list = new VariableList();
        list.setVariables(varList);
        experimentValues.setVariableList(list);
        experimentValues.setGermplasmId(-1);
        experimentValues.setLocationId(-1);
        manager.addExperiment(dataSetId, ExperimentType.PLOT, experimentValues);
    }

    @Test
    public void testSetExperimentValue() throws Exception {
        StudyReference studyRef = this.addTestStudy();
        DatasetReference datasetRef = this.addTestDataset(studyRef.getId());
        addTestExperiments(datasetRef.getId(), 4);
        List<Experiment> experiments = manager.getExperiments(datasetRef.getId(), 0, 2);

        printExperiments("Original", datasetRef.getId());
        for (Experiment experiment : experiments) {
            manager.setExperimentValue(experiment.getId(), 18000, "666");
            manager.setExperimentValue(experiment.getId(), 18050, "19010");
            manager.setExperimentValue(experiment.getId(), 8200, "4");
        }
        printExperiments("Modified", datasetRef.getId());
    }

    @Test
    public void testGetTrialEnvironmentsInDataset() throws Exception {
        Debug.println(INDENT, "Test getTrialEnvironmentsInDataset");
        TrialEnvironments trialEnvironments = manager.getTrialEnvironmentsInDataset(10085);
        trialEnvironments.print(INDENT);
    }

    @Test
    public void testGetStocksInDataset() throws Exception {
        Stocks stocks = manager.getStocksInDataset(10085);
        stocks.print(INDENT);
    }

    private void printExperiments(String title, int datasetId) throws Exception {
        Debug.println(INDENT, title);
        List<Experiment> experiments = manager.getExperiments(datasetId, 0, 4);
        for (Experiment experiment : experiments) {
            experiment.print(3);
        }
    }

    @Test
    public void testAddTrialEnvironment() throws Exception {
        VariableList variableList = createTrialEnvironment("loc desc", "1.1", "2.2", "datum", "3.3", "prop1", "prop2");
        manager.addTrialEnvironment(variableList);
    }

    @Test
    public void testAddGermplasm() throws Exception {
        VariableList variableList = createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
        manager.addStock(variableList);
    }

    @Test
    public void testGetFactorsByProperty() throws Exception {
        int propertyId = 2205;
        int datasetId = 10015;
        Debug.println(INDENT, "testGetFactorsByProperty (dataset=" + datasetId + ", property=" + propertyId);
        DataSet dataset = manager.getDataSet(datasetId);
        VariableTypeList factors = dataset.getFactorsByProperty(propertyId);
        if (factors != null && factors.getVariableTypes() != null && factors.getVariableTypes().size() > 0) {
            for (VariableType factor : factors.getVariableTypes()) {
                factor.print(INDENT);
            }
        } else {
            Debug.println(INDENT, "NO FACTORS FOUND FOR DATASET = " + datasetId + " WITH PROPERTY = " + propertyId);
        }
    }

    @Test
    public void testGetFactorsByPhenotypicType() throws Exception {
        PhenotypicType phenotypicType = PhenotypicType.DATASET;
        int datasetId = 10087;
        Debug.println(INDENT, "testGetFactorsByPhenotypicType (dataset=" + datasetId + ", role=" + phenotypicType + ")");
        DataSet dataset = manager.getDataSet(datasetId);
        if (dataset != null) {
            VariableTypeList factors = dataset.getFactorsByPhenotypicType(phenotypicType);

            if (factors != null && factors.getVariableTypes() != null && factors.getVariableTypes().size() > 0) {
                for (VariableType factor : factors.getVariableTypes()) {
                    factor.print(INDENT);
                }
            } else {
                Debug.println(INDENT, "NO FACTORS FOUND FOR DATASET = " + datasetId + " WITH FACTOR TYPE = "
                        + phenotypicType);
            }
        } else {
            Debug.println(INDENT, "DATASET = " + datasetId + " NOT FOUND. ");
        }
    }

    @Test
    public void testGetDataSetsByType() throws Exception {
        int studyId = 10010;
        DataSetType dataSetType = DataSetType.MEANS_DATA;
        Debug.println(INDENT, "testGetDataSetsByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
        List<DataSet> datasets = manager.getDataSetsByType(studyId, dataSetType);
        for (DataSet dataset : datasets) {
            Debug.println(INDENT, "Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
        }

        studyId = 10080;
        dataSetType = DataSetType.MEANS_DATA;
        Debug.println(INDENT, "testGetDataSetsByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
        datasets = manager.getDataSetsByType(studyId, dataSetType);
        for (DataSet dataset : datasets) {
            Debug.println(INDENT, "Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
        }

        Debug.println(INDENT, "Display data set type in getDataSet");
        DataSet dataSet = manager.getDataSet(10087);
        Debug.println(INDENT, "DataSet = " + dataSet.getId() + ", name = " + dataSet.getName() + ", description = "
                + dataSet.getDescription() + ", type = " + dataSet.getDataSetType());
    }

    @Test
    public void testFindOneDataSetByType() throws Exception {
        int studyId = 10010;
        DataSetType dataSetType = DataSetType.MEANS_DATA;
        Debug.println(INDENT, "testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
        DataSet dataset = manager.findOneDataSetByType(studyId, dataSetType);
        if (dataset != null) {
            Debug.println(INDENT, "Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
        }

        studyId = 10080;
        dataSetType = DataSetType.MEANS_DATA;
        Debug.println(INDENT, "testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
        dataset = manager.findOneDataSetByType(studyId, dataSetType);
        if (dataset != null) {
            Debug.println(INDENT, "Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
        }

        dataSetType = DataSetType.SUMMARY_DATA;
        Debug.println(INDENT, "testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
        dataset = manager.findOneDataSetByType(studyId, dataSetType);
        assertNull(dataset);
    }

    @Test
    public void testCountExperimentsByTrialEnvironmentAndVariate() throws Exception {
        long count = manager.countExperimentsByTrialEnvironmentAndVariate(10070, 20870);
        Debug.println(INDENT, "Count of Experiments By TE and Variate: " + count);
    }

    @Test
    public void testCountStocks() throws Exception {
        long count = manager.countStocks(10087, 10081, 18190);
        Debug.println(INDENT, "Test CountStocks: " + count);
    }

    @Test
    public void testDeleteDataSet() throws Exception {
        StudyReference studyRef = this.addTestStudy();
        DatasetReference datasetRef = this.addTestDataset(studyRef.getId());
        this.addTestExperiments(datasetRef.getId(), 10);

        Debug.println(INDENT, "Test Delete DataSet: " + datasetRef.getId());
        manager.deleteDataSet(datasetRef.getId());
    }

    @Test
    public void testDeleteExperimentsByLocation() throws Exception {
        StudyReference studyRef = this.addTestStudyWithNoLocation();
        DatasetReference datasetRef = this.addTestDatasetWithLocation(studyRef.getId());
        int locationId = this.addTestExperimentsWithLocation(datasetRef.getId(), 10);
        int locationId2 = this.addTestExperimentsWithLocation(datasetRef.getId(), 10);

        Debug.println(INDENT, "Test Delete ExperimentsByLocation: " + datasetRef.getId() + ", " + locationId);
        Debug.println(INDENT, "Location id of " + locationId2 + " will NOT be deleted");
        manager.deleteExperimentsByLocation(datasetRef.getId(), locationId);
    }

    @Test
    public void testGetLocalNameByStandardVariableId() throws Exception {
        Integer projectId = 10085;
        Integer standardVariableId = 8230;
        String localName = manager.getLocalNameByStandardVariableId(projectId, standardVariableId);
        Debug.println(INDENT, "testGetLocalNameByStandardVariableId(" + projectId + ", " + standardVariableId + "): "
                + localName);
    }

    @Test
    public void testGetAllStudyDetails() throws Exception {
        List<StudyDetails> nurseryStudyDetails = manager.getAllStudyDetails(
        		StudyType.N,commonTestProject.getUniqueID());
        Debug.println(INDENT, "testGetAllStudyDetails(StudyType.N, "+commonTestProject.getUniqueID()+")");
        Debug.printFormattedObjects(INDENT, nurseryStudyDetails);
    }

    @Test
    public void testGetAllNurseryAndTrialStudyNodes() throws Exception {
        List<StudyNode> studyNodes = manager.getAllNurseryAndTrialStudyNodes(
        		commonTestProject.getUniqueID());
        Debug.printFormattedObjects(INDENT, studyNodes);
    }

    @Test
    public void testCountProjectsByVariable() throws Exception {
        int variableId = 8050;
        long count = manager.countProjectsByVariable(variableId);
        Debug.println(INDENT, "countProjectsByVariable on " + variableId + " = " + count);
    }

    @Test
    public void testCountExperimentsByProjectPropVariable() throws Exception {
        int variableId = 8050;
        int storedInId = 1010;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByProjectVariable() throws Exception {
        int variableId = 8005;
        int storedInId = 1011;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByExperimentPropVariable() throws Exception {
        int variableId = 8200;
        int storedInId = 1030;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByGeolocationVariable() throws Exception {
        int variableId = 8170;
        int storedInId = 1021;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByGeolocationPropVariable() throws Exception {
        int variableId = 8370;
        int storedInId = 1020;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByStockVariable() throws Exception {
        int variableId = 8230;
        int storedInId = 1041;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByStockPropVariable() throws Exception {
        int variableId = 8255;
        int storedInId = 1040;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByPhenotypeVariable() throws Exception {
        int variableId = 18000;
        int storedInId = 1043;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(INDENT, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    private Variable createVariable(int termId, String value, int rank) throws Exception {
        StandardVariable stVar = ontologyManager.getStandardVariable(termId);

        VariableType vtype = new VariableType();
        vtype.setStandardVariable(stVar);
        vtype.setRank(rank);
        Variable var = new Variable();
        var.setValue(value);
        var.setVariableType(vtype);
        return var;
    }

    private VariableType createVariableType(int termId, String name, String description, int rank) throws Exception {
        StandardVariable stdVar = ontologyManager.getStandardVariable(termId);

        VariableType vtype = new VariableType();
        vtype.setLocalName(name);
        vtype.setLocalDescription(description);
        vtype.setRank(rank);
        vtype.setStandardVariable(stdVar);

        return vtype;
    }

    private void updateVariableType(VariableType type, String name, String description) {
        type.setLocalName(name);
        type.setLocalDescription(description);
    }

    private VariableList createTrialEnvironment(String name, String latitude, String longitude, String data,
            String altitude, String property1, String property2) throws Exception {
        VariableList variableList = new VariableList();
        variableList.add(createVariable(8170, name, 0));
        variableList.add(createVariable(8191, latitude, 0));
        variableList.add(createVariable(8192, longitude, 0));
        variableList.add(createVariable(8193, data, 0));
        variableList.add(createVariable(8194, altitude, 0));
        variableList.add(createVariable(8135, property1, 0));
        variableList.add(createVariable(8180, property2, 0));
        variableList.add(createVariable(8195, "999", 0));
        return variableList;
    }

    private VariableList createGermplasm(String name, String gid, String designation, String code, String property1,
            String property2) throws Exception {
        VariableList variableList = new VariableList();
        variableList.add(createVariable(8230, name, 1));
        variableList.add(createVariable(8240, gid, 2));
        variableList.add(createVariable(8250, designation, 3));
        variableList.add(createVariable(8300, code, 4));
        variableList.add(createVariable(8255, property1, 5));
        variableList.add(createVariable(8377, property2, 6));
        return variableList;
    }

    private StudyReference addTestStudy() throws Exception {
        int parentStudyId = 1;

        VariableTypeList typeList = new VariableTypeList();

        VariableList variableList = new VariableList();

        Variable variable = createVariable(TermId.STUDY_NAME.getId(), "Study Name " + new Random().nextInt(10000), 1);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        variable = createVariable(TermId.STUDY_TITLE.getId(), "Study Description", 2);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        StudyValues studyValues = new StudyValues();
        studyValues.setVariableList(variableList);

        VariableList locationVariableList = createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1",
                "prop2");
        studyValues.setLocationId(manager.addTrialEnvironment(locationVariableList));

        VariableList germplasmVariableList = createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
        studyValues.setGermplasmId(manager.addStock(germplasmVariableList));

        return manager.addStudy(parentStudyId, typeList, studyValues, null);
    }

    private StudyReference addTestStudyWithNoLocation() throws Exception {
        int parentStudyId = 1;

        VariableTypeList typeList = new VariableTypeList();

        VariableList variableList = new VariableList();

        Variable variable = createVariable(TermId.STUDY_NAME.getId(), "Study Name " + new Random().nextInt(10000), 1);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        variable = createVariable(TermId.STUDY_TITLE.getId(), "Study Description", 2);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        StudyValues studyValues = new StudyValues();
        studyValues.setVariableList(variableList);

        VariableList germplasmVariableList = createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
        studyValues.setGermplasmId(manager.addStock(germplasmVariableList));

        return manager.addStudy(parentStudyId, typeList, studyValues, null);
    }

    private DatasetReference addTestDataset(int studyId) throws Exception {
        // Parent study, assign a parent study id value, if none exists in db,

        VariableTypeList typeList = new VariableTypeList();

        DatasetValues datasetValues = new DatasetValues();
        datasetValues.setName("My Dataset Name " + new Random().nextInt(10000));
        datasetValues.setDescription("My Dataset Description");
        datasetValues.setType(DataSetType.MEANS_DATA);

        VariableType variableType = createVariableType(18000, "Grain Yield", "whatever", 4);
        typeList.add(variableType);

        variableType = createVariableType(18050, "Disease Pressure", "whatever", 5);
        typeList.add(variableType);

        variableType = createVariableType(8200, "Plot No", "whatever", 6);
        typeList.add(variableType);

        return manager.addDataSet(studyId, typeList, datasetValues, null);
    }

    private DatasetReference addTestDatasetWithLocation(int studyId) throws Exception {
        // Parent study, assign a parent study id value, if none exists in db,

        VariableTypeList typeList = new VariableTypeList();

        DatasetValues datasetValues = new DatasetValues();
        datasetValues.setName("My Dataset Name " + new Random().nextInt(10000));
        datasetValues.setDescription("My Dataset Description");
        datasetValues.setType(DataSetType.MEANS_DATA);

        VariableType variableType = createVariableType(18000, "Grain Yield", "whatever", 4);
        typeList.add(variableType);

        variableType = createVariableType(18050, "Disease Pressure", "whatever", 5);
        typeList.add(variableType);

        variableType = createVariableType(8200, "Plot No", "whatever", 6);
        typeList.add(variableType);

        variableType = createVariableType(8195, "Site Code", "whatever", 7);
        typeList.add(variableType);

        return manager.addDataSet(studyId, typeList, datasetValues, null);
    }

    public void addTestExperiments(int datasetId, int numExperiments) throws Exception {
        DataSet dataSet = manager.getDataSet(datasetId);
        for (int i = 0; i < numExperiments; i++) {
            ExperimentValues experimentValues = new ExperimentValues();
            VariableList varList = new VariableList();
            varList.add(createVariable(dataSet, 18000, "99"));
            varList.add(createVariable(dataSet, 18050, "19000"));
            varList.add(createVariable(dataSet, 8200, "3"));

            experimentValues.setVariableList(varList);
            experimentValues.setGermplasmId(-1);
            experimentValues.setLocationId(-1);
            manager.addExperiment(datasetId, ExperimentType.PLOT, experimentValues);
        }
    }

    public int addTestExperimentsWithLocation(int datasetId, int numExperiments) throws Exception {
        VariableList locationVariableList = createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1",
                "prop2");
        int locationId = manager.addTrialEnvironment(locationVariableList);

        DataSet dataSet = manager.getDataSet(datasetId);
        for (int i = 0; i < numExperiments; i++) {
            ExperimentValues experimentValues = new ExperimentValues();
            VariableList varList = new VariableList();
            varList.add(createVariable(dataSet, 18000, "99"));
            varList.add(createVariable(dataSet, 18050, "19000"));
            varList.add(createVariable(dataSet, 8200, "3"));

            experimentValues.setVariableList(varList);
            experimentValues.setGermplasmId(-1);
            experimentValues.setLocationId(locationId);
            manager.addExperiment(datasetId, ExperimentType.PLOT, experimentValues);
        }
        return locationId;
    }

    private Variable createVariable(DataSet dataSet, int stdVarId, String value) {
        Variable variable = new Variable();
        variable.setValue(value);
        variable.setVariableType(dataSet.getVariableTypes().findById(stdVarId));
        return variable;
    }

    public void testCheckIfProjectNameIsExisting() throws Exception {
        Study study = manager.getStudy(10010);
        String name = study.getName();
        Debug.println(INDENT, "Name: " + name);
        boolean isExisting = manager.checkIfProjectNameIsExistingInProgram(name,
        		commonTestProject.getUniqueID());
        assertTrue(isExisting);

        name = "SHOULDNOTEXISTSTUDY";
        Debug.println(INDENT, "Name: " + name);
        isExisting = manager.checkIfProjectNameIsExistingInProgram(name,
        		commonTestProject.getUniqueID());
        assertFalse(isExisting);
    }

    @Test
    public void testGetFieldMapCountsOfTrial() throws MiddlewareQueryException{
        List<Integer> trialIdList = new ArrayList<Integer>();
        trialIdList.addAll(Arrays.asList(Integer.valueOf(-4)));  
        List<FieldMapInfo> fieldMapInfos = manager.getFieldMapInfoOfStudy(trialIdList, StudyType.T, LEVEL, NAME_TYPE);
        for (FieldMapInfo fieldMapInfo : fieldMapInfos) {
            Debug.println(INDENT, fieldMapInfo.getFieldbookName());
            if (fieldMapInfo.getDatasets() != null){
                Debug.println(INDENT, fieldMapInfo.getDatasets().toString());
            }
        }
        //assertTrue(fieldMapCount.getEntryCount() > 0);
    }

    @Test
    public void testGetParentFolder() throws MiddlewareQueryException{
    	DmsProject proj = manager.getParentFolder(10010);
    	if(proj==null) {
            Debug.println(INDENT, "Parent is null");
        } else {
            Debug.println(INDENT, "Parent is NOT null");
        }
    }
    
    @Test
    public void testGetFieldMapCountsOfNursery() throws MiddlewareQueryException {
        List<Integer> nurseryIdList = new ArrayList<Integer>();
        
        //REPLACED BY THIS TO MAKE THE JUNIT WORK - Get the first nursery from the db
        List<StudyDetails> studyDetailsList = manager.getAllNurseryAndTrialStudyDetails(
        		commonTestProject.getUniqueID());
        if (studyDetailsList != null && studyDetailsList.size() > 0) {
            for (StudyDetails study : studyDetailsList) {
                if (study.getStudyType() == StudyType.N) {
                    nurseryIdList.add(study.getId());
                    break;
                }
            }
        }
        
        if (nurseryIdList.size() > 0) {
            
            List<FieldMapInfo> fieldMapInfos = manager.getFieldMapInfoOfStudy(nurseryIdList, StudyType.N, LEVEL, NAME_TYPE);
            for (FieldMapInfo fieldMapInfo : fieldMapInfos) {
                Debug.println(INDENT, fieldMapInfo.getFieldbookName());
                if (fieldMapInfo.getDatasets() != null){
                    Debug.println(INDENT, fieldMapInfo.getDatasets().toString());
                }
            }
            //assertTrue(fieldMapCount.getEntryCount() > 0);
        }
    }
    
    @Test
    public void testGetGeolocationPropValue() throws MiddlewareQueryException {
        String value = manager.getGeolocationPropValue(TermId.LOCATION_ID.getId(), -1);
        Debug.println(INDENT, value);
    }
    
    @Test
    public void testSaveFieldMapProperties() throws MiddlewareQueryException {
        List<Integer> trialIdList = new ArrayList<Integer>();

        //REPLACED BY THIS TO MAKE THE JUNIT WORK
        List<StudyDetails> studyDetailsList = manager.getAllNurseryAndTrialStudyDetails(
        		commonTestProject.getUniqueID());
        if (studyDetailsList != null && studyDetailsList.size() > 0){
            for (StudyDetails study : studyDetailsList){
                if (study.getStudyType() == StudyType.T){
                    trialIdList.add(study.getId());
                    break;
                }
            }
        }
         
        List<FieldMapInfo> info = manager.getFieldMapInfoOfStudy(trialIdList, StudyType.T, LEVEL, NAME_TYPE);

        manager.saveOrUpdateFieldmapProperties(info, -1, false);
    }
    
    @Test
    public void testGetAllNurseryAndTrialStudyDetails() throws MiddlewareQueryException {
    	Debug.println(INDENT, "testGetStudyDetailsWithPaging");
        Debug.println(INDENT, "List ALL Trials and Nurseries");
        List<StudyDetails> list = manager.getAllNurseryAndTrialStudyDetails(
        		commonTestProject.getUniqueID());
        for (StudyDetails s : list) {
            Debug.println(INDENT, s.toString());
        }
        Debug.println(INDENT, String.valueOf(manager.countAllNurseryAndTrialStudyDetails(
        		commonTestProject.getUniqueID())));
        Debug.println(INDENT, "List ALL Trials and Nurseries");
        list = manager.getAllNurseryAndTrialStudyDetails(commonTestProject.getUniqueID());
        for (StudyDetails s : list) {
            Debug.println(INDENT, s.toString());
        }
        Debug.println(INDENT, String.valueOf(manager.countAllNurseryAndTrialStudyDetails(
        		commonTestProject.getUniqueID())));
        
        Debug.println(INDENT, "List ALL Trials");
        list = manager.getAllStudyDetails(StudyType.T,commonTestProject.getUniqueID());
        for (StudyDetails s : list) {
            Debug.println(INDENT, s.toString());
        }
        Debug.println(INDENT, String.valueOf(manager.countAllStudyDetails(StudyType.T,
        		commonTestProject.getUniqueID())));
        
        Debug.println(INDENT, "List ALL Nurseries");
        list = manager.getAllStudyDetails(StudyType.T,commonTestProject.getUniqueID());
        for (StudyDetails s : list) {
            Debug.println(INDENT, s.toString());
        }
        Debug.println(INDENT, String.valueOf(manager.countAllStudyDetails(StudyType.N,
        		commonTestProject.getUniqueID())));
        
    }
    
    @Test
    public void testGetFolderTree() throws MiddlewareQueryException {
        List<FolderReference> tree = manager.getFolderTree();
        Debug.println(INDENT, "GetFolderTree Test");
        printFolderTree(tree, 1);
    }
    
    @Test
    public void testGetPhenotypeIdsByLocationAndPlotNo() throws Exception{
    	
    	
    	List<Integer> cvTermIds = new ArrayList<Integer>();
    	
    	DataSet dataSet = manager.getDataSet(-9999);
    	
    	if (dataSet==null) {
            return;
        }
    	
		for (VariableType vType: dataSet.getVariableTypes().getVariates().getVariableTypes()){
			cvTermIds.add(vType.getStandardVariable().getId());
		}
    		
    	List<Object[]> value = manager.getPhenotypeIdsByLocationAndPlotNo(-26, -14, 101, cvTermIds);
    	
    	assertNotNull(value);
    	
    	Debug.println(INDENT, "getPhenotypeIdsByLocationAndPlotNo Test");
    	for (Object[] val :value){
    		Debug.println(val.toString());
    	}
    	
    	
    }
    
    
    @Test
    public void testSaveOrUpdatePhenotypeOutliers() throws Exception{
    	
    	List<PhenotypeOutlier> outliers = new ArrayList<PhenotypeOutlier>();
    	PhenotypeOutlier phenotypeOutlier = new PhenotypeOutlier();
    	phenotypeOutlier.setPhenotypeId(1);
    	phenotypeOutlier.setValue("hello");
    	
    	outliers.add(phenotypeOutlier);
    	
    	try{
    		manager.saveOrUpdatePhenotypeOutliers(outliers);
    	}catch(Exception e){
    	
    	}
    	
    	Debug.println(INDENT, "testSavePhenotypeOutlier Test");
    	
    	
    }
    
    private void printFolderTree(List<FolderReference> tree, int tab) {
        if (tree != null && tree.size() > 0) {
            for (FolderReference folder : tree) {
                for (int i = 0; i < tab; i++) {
                	Debug.print(0, "\t");
                }
                Debug.println(INDENT, folder.getId() + " - " + folder.getName());
                printFolderTree(folder.getSubFolders(), tab+1);
            }
        }
    }
        
    @Test
    public void testUpdateFieldMapWithBlockInformationWhenBlockIdIsNotNull() {
    	LocationDataManager locationDataManager = Mockito.mock(LocationDataManager.class);
    	
    	FieldmapBlockInfo fieldMapBlockInfo = new FieldmapBlockInfo(FieldMapDataUtil.BLOCK_ID, 
    			FieldMapDataUtil.ROWS_IN_BLOCK, FieldMapDataUtil.RANGES_IN_BLOCK, 
    			FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, FieldMapDataUtil.PLANTING_ORDER, 
    			FieldMapDataUtil.MACHINE_ROW_CAPACITY, false, null, FieldMapDataUtil.FIELD_ID);
    	
    	StudyDataManagerImpl localManager = ((StudyDataManagerImpl) manager);
    	
    	List<FieldMapInfo> infos = FieldMapDataUtil.createFieldMapInfoList(true);
    	
    	localManager.setLocationDataManager(locationDataManager);
    		
    	try {
    		Mockito.when(locationDataManager.getBlockInformation(FieldMapDataUtil.BLOCK_ID)).thenReturn(fieldMapBlockInfo);
    		localManager.updateFieldMapWithBlockInformation(infos, fieldMapBlockInfo, false);
    		
    		FieldMapTrialInstanceInfo trialInstance = infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);
    		
    		Assert.assertEquals("Expected " + FieldMapDataUtil.ROWS_IN_BLOCK + " but got " + trialInstance.getRowsInBlock() + " instead.", 
    				FieldMapDataUtil.ROWS_IN_BLOCK, trialInstance.getRowsInBlock().intValue());
    		Assert.assertEquals("Expected " + FieldMapDataUtil.RANGES_IN_BLOCK + " but got " + trialInstance.getRangesInBlock() + " instead.", 
    				FieldMapDataUtil.RANGES_IN_BLOCK, trialInstance.getRangesInBlock().intValue());
    		Assert.assertEquals("Expected " + FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT + " but got " + trialInstance.getRowsPerPlot() + " instead.", 
    				FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, trialInstance.getRowsPerPlot().intValue());
    		Assert.assertEquals("Expected " + FieldMapDataUtil.PLANTING_ORDER + " but got " + trialInstance.getPlantingOrder() + " instead.", 
    				FieldMapDataUtil.PLANTING_ORDER, trialInstance.getPlantingOrder().intValue());
    		Assert.assertEquals("Expected " + FieldMapDataUtil.MACHINE_ROW_CAPACITY + " but got " + trialInstance.getMachineRowCapacity() + " instead.", 
    				FieldMapDataUtil.MACHINE_ROW_CAPACITY, trialInstance.getMachineRowCapacity().intValue());
    	} catch (MiddlewareQueryException e) {
    		Assert.fail("Expected mocked value to be returned but used the original call for getBlockInformation instead.");
    	}
    }
    
    @Test
    public void testUpdateFieldMapWithBlockInformationWhenBlockIdIsNull() {
    	LocationDataManager locationDataManager = Mockito.mock(LocationDataManager.class);
    	
    	FieldmapBlockInfo fieldMapBlockInfo = new FieldmapBlockInfo(FieldMapDataUtil.BLOCK_ID, 
    			FieldMapDataUtil.ROWS_IN_BLOCK, FieldMapDataUtil.RANGES_IN_BLOCK, 
    			FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, FieldMapDataUtil.PLANTING_ORDER, 
    			FieldMapDataUtil.MACHINE_ROW_CAPACITY, false, null, FieldMapDataUtil.FIELD_ID);
    	
    	StudyDataManagerImpl localManager = ((StudyDataManagerImpl) manager);
    	
    	List<FieldMapInfo> infos = FieldMapDataUtil.createFieldMapInfoList(true);
    	FieldMapTrialInstanceInfo trialInstance = infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);
    	trialInstance.setBlockId(null);
    	
    	localManager.setLocationDataManager(locationDataManager);
    		
    	try {
    		Mockito.when(locationDataManager.getBlockInformation(FieldMapDataUtil.BLOCK_ID)).thenReturn(fieldMapBlockInfo);
    		localManager.updateFieldMapWithBlockInformation(infos, fieldMapBlockInfo, false);
    		
    		Assert.assertNull("Expected null but got " + trialInstance.getRowsInBlock() + " instead.", trialInstance.getRowsInBlock());
    		Assert.assertNull("Expected null but got " + trialInstance.getRangesInBlock() + " instead.", trialInstance.getRangesInBlock());
    		Assert.assertNull("Expected null but got " + trialInstance.getRowsPerPlot() + " instead.", trialInstance.getRowsPerPlot());
    		Assert.assertNull("Expected null but got " + trialInstance.getPlantingOrder() + " instead.", trialInstance.getPlantingOrder());
    		Assert.assertNull("Expected null but got " + trialInstance.getMachineRowCapacity() + " instead.", trialInstance.getMachineRowCapacity());
    	} catch (MiddlewareQueryException e) {
    		Assert.fail("Expected mocked value to be returned but used the original call for getBlockInformation instead.");
    	}
    }
    public void testGetStudyType() {
    	try {
			Assert.assertEquals("Study type returned did not match.", StudyType.BON, manager.getStudyType(STUDY_ID));
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
    }
    
    @Test
    public void testGetStudyTypeNullEdgeCase() {
    	try {
    		final int PRESUMABLY_NON_EXISTENT_STUDY_ID = -1000000;
    		Assert.assertNull("Expected null return value but was non null.", manager.getStudyType(PRESUMABLY_NON_EXISTENT_STUDY_ID));
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
    }
    
    @Test 
    public void testDeleteProgramStudies() {
    	StudyTestDataUtil studyTestDataUtil = StudyTestDataUtil.getInstance();
    	String uniqueId = commonTestProject.getUniqueID();
    	try {
			studyTestDataUtil.createFolderTestData(uniqueId);
			studyTestDataUtil.createStudyTestData(uniqueId);
	    	studyTestDataUtil.createStudyTestDataWithActiveStatus(uniqueId);
	    	
	    	List<FolderReference> programStudiesAndFolders = 
	    			studyTestDataUtil.getLocalRootFolders(commonTestProject.getUniqueID());
	    	assertEquals("Current Program with programUUID "+commonTestProject.getUniqueID()+
	    			" should return 3 children",
	    			3,programStudiesAndFolders.size());
	    	manager.deleteProgramStudies(commonTestProject.getUniqueID());
	    	programStudiesAndFolders = 
	    			studyTestDataUtil.getLocalRootFolders(commonTestProject.getUniqueID());
	    	assertEquals("Current Program with programUUID "+commonTestProject.getUniqueID()+
	    			" should return no children",
	    			0,programStudiesAndFolders.size());
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
    	
    }
    
    @Test
    public void testGetStudyDetails() throws MiddlewareQueryException {
    	List<StudyDetails> studyDetailsList = manager.getStudyDetails(
    			StudyType.N, commonTestProject.getUniqueID(), -1, -1);
    	assertNotNull(studyDetailsList);
    }
    
    @Test
    public void testGetNurseryAndTrialStudyDetails() throws MiddlewareQueryException {
    	List<StudyDetails> studyDetailsList = manager.getNurseryAndTrialStudyDetails(
    			commonTestProject.getUniqueID(), -1, -1);
    	assertNotNull(studyDetailsList);
    }
    
    @Test
    public void testGetStudyDetails_ByTypeAndId() throws MiddlewareQueryException {
    	DmsProject study = StudyTestDataUtil.getInstance().createStudyTestDataWithActiveStatus(
    			commonTestProject.getUniqueID());
    	StudyDetails studyDetails = manager.getStudyDetails(
    			StudyType.T, study.getProjectId());
    	assertNotNull("Study should not be null", studyDetails);
    	assertEquals("Study should have the id "+study.getProjectId(), 
    			study.getProjectId(), studyDetails.getId());
    	assertEquals("Study should have the programUUID "+commonTestProject.getUniqueID(), 
    			commonTestProject.getUniqueID(), studyDetails.getProgramUUID());
    	assertEquals("Study should be a trial", 
    			StudyType.T, studyDetails.getStudyType());
    }
}
