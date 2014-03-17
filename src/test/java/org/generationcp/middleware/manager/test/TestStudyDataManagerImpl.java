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

package org.generationcp.middleware.manager.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.domain.workbench.StudyNode;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.utils.test.TestNurseryWorkbookUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestStudyDataManagerImpl{

    private static final Integer       STUDY_ID   = 10010;
    private static final Integer       DATASET_ID = 10045;

    private static ManagerFactory      factory;
    private static StudyDataManager    manager;
    private static OntologyDataManager ontologyManager;

    private long                       startTime;

    @Rule
    public TestName                    name       = new TestName();

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties",
                "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getNewStudyDataManager();
        ontologyManager = factory.getNewOntologyDataManager();
    }

    @Before
    public void beforeEachTest() {
        startTime = System.nanoTime();
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
    }

    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = "
                + ((double) elapsedTime / 1000000000) + " s");
    }

    @Test
    public void testGetStudyDetails() throws Exception {
        Study study = manager.getStudy(STUDY_ID);
        assertNotNull(study);
        Debug.println(0, "ID: " + study.getId());
        Debug.println(0, "Name: " + study.getName());
        Debug.println(0, "Title:" + study.getTitle());
        Debug.println(0, "PI: " + study.getPrimaryInvestigator());
        Debug.println(0, "Start Date:" + study.getStartDate());
        Debug.println(0, "Creation Date: " + study.getCreationDate());
        Debug.println(0, "Study status: " + study.getStatus());
        Debug.println(0, "Study type: " + study.getType());
    }

    @Test
    public void testGetStudyConditions() throws Exception {
        Study study = manager.getStudy(STUDY_ID);
        assertNotNull(study);
        VariableList vList = study.getConditions();
        for (Variable v : vList.getVariables()) {
            Debug.print(0, "name[" + v.getVariableType().getStandardVariable().getName() + "]=");
            Debug.println(0, v.getDisplayValue());
        }
    }

    @Test
    public void testGetAllStudyFactor() throws Exception {
        Debug.println(0, "testGetFactorDetails");
        int studyId = 10010;
        VariableTypeList factors = manager.getAllStudyFactors(studyId);
        assertNotNull(factors);
        assertTrue(factors.getVariableTypes().size() > 0);
        Debug.println(0, "FACTORS RETRIEVED " + factors.getVariableTypes().size());
        factors.print(0);
    }

    @Test
    public void testGetAllStudyVariates() throws Exception {
        int studyId = 10010;
        VariableTypeList variates = manager.getAllStudyVariates(studyId);
        assertNotNull(variates);
        assertTrue(variates.getVariableTypes().size() > 0);
        variates.print(0);
    }

    @Test
    public void testGetStudiesByFolder() throws Exception {
        int folderId = 1030;
        StudyResultSet resultSet = manager.searchStudies(new ParentFolderStudyQueryFilter(folderId), 5);
        Debug.println(0, "testGetStudiesByFolder(" + folderId + "): " + resultSet.size());
        assertTrue(resultSet.size() > 0);
        while (resultSet.hasMore()) {
            StudyReference studyRef = resultSet.next();
            Debug.println(0, studyRef.toString());
        }
    }

    @Test
    public void testSearchStudiesForName() throws Exception {
        Debug.println(0, "testSearchStudiesForName");
        BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();

        filter.setName("FooFoo"); // INVALID: Not a study, should not find any
                                  // studies
        StudyResultSet resultSet = manager.searchStudies(filter, 10);
        assertTrue(resultSet.size() == 0);

        filter.setName("RYT2000WS"); // VALID: is a study

        resultSet = manager.searchStudies(filter, 10);
        Debug.println(0, "INPUT: " + filter);
        Debug.println(0, "Size: " + resultSet.size());
        while (resultSet.hasMore()) {
            Debug.println(0, "\t" + resultSet.next());
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

        Debug.println(0, "INPUT: " + filter);
        Debug.println(0, "Size: " + resultSet.size());
        while (resultSet.hasMore()) {
            Debug.println(0, "\t" + resultSet.next());
            System.out.flush();
        }
        // long before = resultSet.size();
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
            Debug.println(0, "Season: " + season);
            BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
            filter.setSeason(season);
            StudyResultSet resultSet = manager.searchStudies(filter, 10);
            Debug.println(0, "Size: " + resultSet.size());
            while (resultSet.hasMore()) {
                Debug.println(0, "\t" + resultSet.next());
                System.out.flush();
            }
        }
    }

    @Test
    public void testSearchStudiesForCountry() throws Exception {
        BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();

        filter.setCountry("Republic of the Philippines");

        StudyResultSet resultSet = manager.searchStudies(filter, 10);
        Debug.println(0, "INPUT: " + filter);
        Debug.println(0, "Size: " + resultSet.size());
        while (resultSet.hasMore()) {
            Debug.println(0, "\t" + resultSet.next());
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
        Debug.println(0, "INPUT: " + filter);
        Debug.println(0, "Size: " + resultSet.size());
        while (resultSet.hasMore()) {
            Debug.println(0, "\t" + resultSet.next());
            System.out.flush();
        }
    }

    @Test
    public void testGetRootFolders() throws Exception {
        List<FolderReference> rootFolders = manager.getRootFolders(Database.CENTRAL);
        assertNotNull(rootFolders);
        assert (rootFolders.size() > 0);
        Debug.println(0, "testGetRootFolders(): " + rootFolders.size());
        for (FolderReference node : rootFolders) {
            Debug.println(0, "   " + node);
        }
    }

    @Test
    public void testGetChildrenOfFolder() throws Exception {
        List<Integer> folderIds = Arrays.asList(1000, 2000);
        for (Integer folderId : folderIds) {
            List<Reference> childrenNodes = manager.getChildrenOfFolder(folderId);
            assertNotNull(childrenNodes);
            assert (childrenNodes.size() > 0);
            Debug.println(0, "testGetChildrenOfFolder(folderId=" + folderId + "): " + childrenNodes.size());
            for (Reference node : childrenNodes) {
                Debug.println(0, "   " + node);
            }
        }
    }

    @Test
    public void testGetDatasetNodesByStudyId() throws Exception {
        Integer studyId = 10010;
        List<DatasetReference> datasetReferences = manager.getDatasetReferences(studyId);
        assertNotNull(datasetReferences);
        assert (datasetReferences.size() > 0);
        Debug.println(0, "Dataset Nodes By Study Id Count: " + datasetReferences.size());
        for (DatasetReference node : datasetReferences) {
            Debug.println(0, "   " + node);
        }
    }

    @Test
    public void testSearchStudiesByGid() throws Exception {
        Integer gid = 2434138;
        GidStudyQueryFilter filter = new GidStudyQueryFilter(gid);
        StudyResultSet resultSet = manager.searchStudies(filter, 50);
        assertNotNull(resultSet);
        Debug.println(0, "Study Count: " + resultSet.size());
        while (resultSet.hasMore()) {
            StudyReference studyRef = resultSet.next();
            Debug.println(0, studyRef.toString());
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

        StudyReference studyRef = manager.addStudy(parentStudyId, typeList, studyValues);

        assertTrue(studyRef.getId() < 0);
        Debug.println(0, "testAddStudy(): " + studyRef);
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

        StudyReference studyRef = manager.addStudy(parentStudyId, typeList, studyValues);

        assertTrue(studyRef.getId() < 0);
        Study study = manager.getStudy(studyRef.getId());
        study.print(0);
    }

    @Test
    public void testGetDataSet() throws Exception {
        for (int i = 10015; i <= 10075; i += 10) {
            DataSet dataSet = manager.getDataSet(i);
            dataSet.print(0);
        }
    }

    @Test
    public void testGetDataSetOfSorghum() throws Exception { // GCP-4986
        int dataSetId = -4; // Local sorghum
        DataSet dataSet = manager.getDataSet(dataSetId);
        dataSet.print(0);
        List<Experiment> experiments = manager.getExperiments(dataSetId, 0, (int) manager.countExperiments(dataSetId));
        Debug.println(0, " Experiments: " + experiments.size());

        Debug.println(0, " Variables.getDisplayValue(): " + experiments.size());
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

            for (Variable variable : variables)
                if (!("GID".equals(variable.getVariableType().getLocalName().trim()))) {
                    String value = variable.getDisplayValue();
                    Debug.println(0, "Data Type is "
                            + variable.getVariableType().getStandardVariable().getDataType().getName());
                    Debug.println(0, "\t" + experiment.getId() + "  :  "
                            + variable.getVariableType().getStandardVariable().getName() + "  :  " + value);
                }
        }
    }

    @Test
    public void testCountExperiments() throws Exception {
        Debug.println(0, "Dataset Experiment Count: " + manager.countExperiments(DATASET_ID));
    }

    @Test
    public void testGetExperiments() throws Exception {
        for (int i = 0; i < 2; i++) {
            List<Experiment> experiments = manager.getExperiments(DATASET_ID, 50 * i, 50);
            for (Experiment experiment : experiments) {
                experiment.print(0);
            }
        }
    }

    @Test
    public void testGetExperimentsWithAverage() throws Exception {
        List<Experiment> experiments = manager.getExperiments(5803, 0, 50);
        for (Experiment experiment : experiments) {
            experiment.print(0);
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

        DatasetReference datasetReference = manager.addDataSet(parentStudyId, typeList, datasetValues);
        Debug.println(0, "Dataset added : " + datasetReference);

    }

    @Test
    public void testAddDatasetWithNoDataType() throws Exception {
        Debug.println(0, "Test addDatasetWithNoCoreValues");
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

        DatasetReference dataSetRef = manager.addDataSet(studyRef.getId(), typeList, datasetValues);

        DataSet dataSet = manager.getDataSet(dataSetRef.getId());
        dataSet.print(0);
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

        DatasetReference datasetReference = manager.addDataSet(parentStudyId, typeList, datasetValues);
        Debug.println(0, "Dataset added : " + datasetReference);

        DataSet dataSet = manager.getDataSet(datasetReference.getId());
        Debug.println(0, "Original Dataset");
        dataSet.print(3);

        VariableType variableType = new VariableType();
        variableType.setLocalName("Dog");
        variableType.setLocalDescription("Man's best friend");
        variableType.setStandardVariable(ontologyManager.getStandardVariable(8240));
        variableType.setRank(99);
        manager.addDataSetVariableType(dataSet.getId(), variableType);

        dataSet = manager.getDataSet(datasetReference.getId());
        Debug.println(0, "Modified Dataset");
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
        Debug.println(0, "Test getTrialEnvironmentsInDataset");
        TrialEnvironments trialEnvironments = manager.getTrialEnvironmentsInDataset(10085);
        trialEnvironments.print(0);
    }

    @Test
    public void testGetStocksInDataset() throws Exception {
        Stocks stocks = manager.getStocksInDataset(10085);
        stocks.print(0);
    }

    private void printExperiments(String title, int datasetId) throws Exception {
        Debug.println(0, title);
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
        Debug.println(0, "testGetFactorsByProperty (dataset=" + datasetId + ", property=" + propertyId);
        DataSet dataset = manager.getDataSet(datasetId);
        VariableTypeList factors = dataset.getFactorsByProperty(propertyId);
        if (factors != null && factors.getVariableTypes() != null && factors.getVariableTypes().size() > 0) {
            for (VariableType factor : factors.getVariableTypes()) {
                factor.print(0);
            }
        } else {
            Debug.println(0, "NO FACTORS FOUND FOR DATASET = " + datasetId + " WITH PROPERTY = " + propertyId);
        }
    }

    @Test
    public void testGetFactorsByPhenotypicType() throws Exception {
        PhenotypicType phenotypicType = PhenotypicType.DATASET;
        int datasetId = 10087;
        Debug.println(0, "testGetFactorsByPhenotypicType (dataset=" + datasetId + ", role=" + phenotypicType + ")");
        DataSet dataset = manager.getDataSet(datasetId);
        if (dataset != null) {
            VariableTypeList factors = dataset.getFactorsByPhenotypicType(phenotypicType);

            if (factors != null && factors.getVariableTypes() != null && factors.getVariableTypes().size() > 0) {
                for (VariableType factor : factors.getVariableTypes()) {
                    factor.print(0);
                }
            } else {
                Debug.println(0, "NO FACTORS FOUND FOR DATASET = " + datasetId + " WITH FACTOR TYPE = "
                        + phenotypicType);
            }
        } else {
            Debug.println(0, "DATASET = " + datasetId + " NOT FOUND. ");
        }
    }

    @Test
    public void testGetDataSetsByType() throws Exception {
        int studyId = 10010;
        DataSetType dataSetType = DataSetType.MEANS_DATA;
        Debug.println(0, "testGetDataSetsByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
        List<DataSet> datasets = manager.getDataSetsByType(studyId, dataSetType);
        for (DataSet dataset : datasets) {
            Debug.println(0, "Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
        }

        studyId = 10080;
        dataSetType = DataSetType.MEANS_DATA;
        Debug.println(0, "testGetDataSetsByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
        datasets = manager.getDataSetsByType(studyId, dataSetType);
        for (DataSet dataset : datasets) {
            Debug.println(0, "Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
        }

        Debug.println(0, "Display data set type in getDataSet");
        DataSet dataSet = manager.getDataSet(10087);
        Debug.println(0, "DataSet = " + dataSet.getId() + ", name = " + dataSet.getName() + ", description = "
                + dataSet.getDescription() + ", type = " + dataSet.getDataSetType());
    }

    @Test
    public void testFindOneDataSetByType() throws Exception {
        int studyId = 10010;
        DataSetType dataSetType = DataSetType.MEANS_DATA;
        Debug.println(0, "testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
        DataSet dataset = manager.findOneDataSetByType(studyId, dataSetType);
        if (dataset != null) {
            Debug.println(0, "Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
        }

        studyId = 10080;
        dataSetType = DataSetType.MEANS_DATA;
        Debug.println(0, "testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
        dataset = manager.findOneDataSetByType(studyId, dataSetType);
        if (dataset != null) {
            Debug.println(0, "Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
        }

        dataSetType = DataSetType.SUMMARY_DATA;
        Debug.println(0, "testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
        dataset = manager.findOneDataSetByType(studyId, dataSetType);
        assertNull(dataset);
    }

    @Test
    public void testCountExperimentsByTrialEnvironmentAndVariate() throws Exception {
        long count = manager.countExperimentsByTrialEnvironmentAndVariate(10070, 20870);
        Debug.println(0, "Count of Experiments By TE and Variate: " + count);
    }

    @Test
    public void testCountStocks() throws Exception {
        long count = manager.countStocks(10087, 10081, 18190);
        Debug.println(0, "Test CountStocks: " + count);
    }

    @Test
    public void testDeleteDataSet() throws Exception {
        StudyReference studyRef = this.addTestStudy();
        DatasetReference datasetRef = this.addTestDataset(studyRef.getId());
        this.addTestExperiments(datasetRef.getId(), 10);

        Debug.println(0, "Test Delete DataSet: " + datasetRef.getId());
        manager.deleteDataSet(datasetRef.getId());
    }

    @Test
    public void testDeleteExperimentsByLocation() throws Exception {
        StudyReference studyRef = this.addTestStudyWithNoLocation();
        DatasetReference datasetRef = this.addTestDatasetWithLocation(studyRef.getId());
        int locationId = this.addTestExperimentsWithLocation(datasetRef.getId(), 10);
        int locationId2 = this.addTestExperimentsWithLocation(datasetRef.getId(), 10);

        Debug.println(0, "Test Delete ExperimentsByLocation: " + datasetRef.getId() + ", " + locationId);
        Debug.println(0, "Location id of " + locationId2 + " will NOT be deleted");
        manager.deleteExperimentsByLocation(datasetRef.getId(), locationId);
    }

    @Test
    public void testGetLocalNameByStandardVariableId() throws Exception {
        Integer projectId = 10085;
        Integer standardVariableId = 8230;
        String localName = manager.getLocalNameByStandardVariableId(projectId, standardVariableId);
        Debug.println(0, "testGetLocalNameByStandardVariableId(" + projectId + ", " + standardVariableId + "): "
                + localName);
    }

    @Test
    public void testGetAllStudyDetails() throws Exception {

        List<StudyDetails> nurseryStudyDetails = manager.getAllStudyDetails(Database.LOCAL, StudyType.N);

        Debug.println(0, "testGetAllStudyDetails(Database.LOCAL, StudyType.N)");
        for (StudyDetails study : nurseryStudyDetails) {
            study.print(3);
        }
    }

    @Test
    public void testGetAllNurseryAndTrialStudyNodes() throws Exception {
        List<StudyNode> studyNodes = manager.getAllNurseryAndTrialStudyNodes();
        for (StudyNode study : studyNodes) {
            study.print(3);
        }
        Debug.println(3, "Number of Records: " + studyNodes.size());
    }

    @Test
    public void testCountProjectsByVariable() throws Exception {
        int variableId = 8050;
        long count = manager.countProjectsByVariable(variableId);
        Debug.println(0, "countProjectsByVariable on " + variableId + " = " + count);
    }

    @Test
    public void testCountExperimentsByProjectPropVariable() throws Exception {
        int variableId = 8050;
        int storedInId = 1010;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(0, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByProjectVariable() throws Exception {
        int variableId = 8005;
        int storedInId = 1011;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(0, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByExperimentPropVariable() throws Exception {
        int variableId = 8200;
        int storedInId = 1030;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(0, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByGeolocationVariable() throws Exception {
        int variableId = 8170;
        int storedInId = 1021;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(0, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByGeolocationPropVariable() throws Exception {
        int variableId = 8370;
        int storedInId = 1020;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(0, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByStockVariable() throws Exception {
        int variableId = 8230;
        int storedInId = 1041;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(0, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByStockPropVariable() throws Exception {
        int variableId = 8255;
        int storedInId = 1040;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(0, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @Test
    public void testCountExperimentsByPhenotypeVariable() throws Exception {
        int variableId = 18000;
        int storedInId = 1043;
        long count = manager.countExperimentsByVariable(variableId, storedInId);
        Debug.println(0, "countExperimentsByVariable on " + variableId + ", " + storedInId + " = " + count);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (factory != null) {
            factory.close();
        }
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

        return manager.addStudy(parentStudyId, typeList, studyValues);
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

        return manager.addStudy(parentStudyId, typeList, studyValues);
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

        return manager.addDataSet(studyId, typeList, datasetValues);
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

        return manager.addDataSet(studyId, typeList, datasetValues);
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
        Debug.println(0, "Name: " + name);
        boolean isExisting = manager.checkIfProjectNameIsExisting(name);
        assertTrue(isExisting);

        name = "SHOULDNOTEXISTSTUDY";
        Debug.println(0, "Name: " + name);
        isExisting = manager.checkIfProjectNameIsExisting(name);
        assertFalse(isExisting);
    }

    @Test
    public void testGetFieldMapCountsOfTrial() throws MiddlewareQueryException{
        List<Integer> trialIdList = new ArrayList<Integer>();
        trialIdList.addAll(Arrays.asList(Integer.valueOf(-4)));  
        List<FieldMapInfo> fieldMapInfos = manager.getFieldMapInfoOfStudy(trialIdList, StudyType.T);
        for (FieldMapInfo fieldMapInfo : fieldMapInfos) {
            Debug.println(0, fieldMapInfo.getFieldbookName());
            if (fieldMapInfo.getDatasets() != null){
                Debug.println(0, fieldMapInfo.getDatasets().toString());
            }
        }
        //assertTrue(fieldMapCount.getEntryCount() > 0);
    }

    @Test
    public void testGetParentFolder() throws MiddlewareQueryException{
    	DmsProject proj = manager.getParentFolder(10010);
    	if(proj==null)
    		System.out.println("Parent is null");
    	else
    		System.out.println("Parent is NOT null");
    }
    
    @Test
    public void testGetFieldMapCountsOfNursery() throws MiddlewareQueryException {
        List<Integer> nurseryIdList = new ArrayList<Integer>();
        nurseryIdList.addAll(Arrays.asList(Integer.valueOf(-1)));  
        List<FieldMapInfo> fieldMapInfos = manager.getFieldMapInfoOfStudy(nurseryIdList, StudyType.N);
        for (FieldMapInfo fieldMapInfo : fieldMapInfos) {
            Debug.println(0, fieldMapInfo.getFieldbookName());
            if (fieldMapInfo.getDatasets() != null){
                Debug.println(0, fieldMapInfo.getDatasets().toString());
            }
        }
        //assertTrue(fieldMapCount.getEntryCount() > 0);
    }
    
    @Test
    public void testGetGeolocationPropValue() throws MiddlewareQueryException {
        String value = manager.getGeolocationPropValue(Database.LOCAL, TermId.LOCATION_ID.getId(), -1);
        Debug.println(0, value);
    }
    
    @Test
    public void testSaveFieldMapProperties() throws MiddlewareQueryException {
        List<Integer> trialIdList = new ArrayList<Integer>();
        trialIdList.add(new Integer(-186));
         
        int geolocationId = -123; //please specify the geolocation id used by the trial 
        List<FieldMapInfo> info = manager.getFieldMapInfoOfStudy(trialIdList, StudyType.T);
        //info.setBlockName("Block Name 1");
        if (info != null) {
            info.get(0).setFieldbookId(-186);
        }
        //info.setColumnsInBlock(7);
        //info.setRangesInBlock(8);
        //info.setPlantingOrder(1);
        int columnCount = 1, rangeCount = 1;
        /*
        for (FieldMapLabel label : info.getFieldMapLabels()) {
            label.setColumn(columnCount);
            label.setRange(rangeCount++);
            if (rangeCount > 8) {
                columnCount++;
                rangeCount = 1;
            }
            if (columnCount > 7) {
                break;
            }
        }*/
        //TODO
        manager.saveOrUpdateFieldmapProperties(info, "");
    }
    
    @Test
    public void testGetStudyDetailsWithPaging() throws MiddlewareQueryException {
    	Debug.println(0, "testGetStudyDetailsWithPaging");
    	Debug.println(0, "List of Nurseries");
    	List<StudyDetails> nlist = manager.getStudyDetails(StudyType.N, 0,Integer.MAX_VALUE);
        for (StudyDetails s : nlist) {
            Debug.println(0, s.toString());
        }
        Debug.println(0, "List of Trials");
        List<StudyDetails> tlist = manager.getStudyDetails(StudyType.T, 0,Integer.MAX_VALUE);
        for (StudyDetails s : tlist) {
            Debug.println(0, s.toString());
        }
        Debug.println(0, "List of Trials and Nurseries");
        List<StudyDetails> slist = manager.getNurseryAndTrialStudyDetails(0,Integer.MAX_VALUE);
        for (StudyDetails s : slist) {
            Debug.println(0, s.toString());
        }
        Debug.println(0, "List ALL Trials and Nurseries");
        List<StudyDetails> list = manager.getAllNurseryAndTrialStudyDetails();
        for (StudyDetails s : list) {
            Debug.println(0, s.toString());
        }
        Debug.println(0, String.valueOf(manager.countAllNurseryAndTrialStudyDetails()));
        Debug.println(0, "List ALL Trials and Nurseries");
        list = manager.getAllNurseryAndTrialStudyDetails();
        for (StudyDetails s : list) {
            Debug.println(0, s.toString());
        }
        Debug.println(0, String.valueOf(manager.countAllNurseryAndTrialStudyDetails()));
        
        Debug.println(0, "List ALL Trials");
        list = manager.getAllStudyDetails(StudyType.T);
        for (StudyDetails s : list) {
            Debug.println(0, s.toString());
        }
        Debug.println(0, String.valueOf(manager.countAllStudyDetails(StudyType.T)));
        
        Debug.println(0, "List ALL Nurseries");
        list = manager.getAllStudyDetails(StudyType.T);
        for (StudyDetails s : list) {
            Debug.println(0, s.toString());
        }
        Debug.println(0, String.valueOf(manager.countAllStudyDetails(StudyType.N)));
        
    }
    
    @Test
    public void testGetFolderTree() throws MiddlewareQueryException {
        List<FolderReference> tree = manager.getFolderTree();
        System.out.println("GetFolderTree Test");
        printFolderTree(tree, 1);
    }
    
    private void printFolderTree(List<FolderReference> tree, int tab) {
        if (tree != null && tree.size() > 0) {
            for (FolderReference folder : tree) {
                for (int i = 0; i < tab; i++) {
                    System.out.print("\t");
                }
                System.out.println(folder.getId() + " - " + folder.getName());
                printFolderTree(folder.getSubFolders(), tab+1);
            }
        }
    }
    
}
