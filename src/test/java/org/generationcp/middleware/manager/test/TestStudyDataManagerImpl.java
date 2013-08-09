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

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.FactorType;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Stocks;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestStudyDataManagerImpl {

	private static final Integer STUDY_ID = 10010;
	private static final Integer DATASET_ID = 10045;

	private static ManagerFactory factory;
	private static StudyDataManager manager;
	private static OntologyDataManager ontologyManager;
	
	private long startTime;
	
	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setUp() throws Exception {
		DatabaseConnectionParameters local = new DatabaseConnectionParameters(
				"testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters(
				"testDatabaseConfig.properties", "central");
		factory = new ManagerFactory(local, central);
		manager = factory.getNewStudyDataManager();
		ontologyManager = factory.getNewOntologyDataManager();
	}

	@Before
	public void beforeEachTest() {
		startTime = System.nanoTime();
	}
	
	@After
	public void afterEachTest() {
		System.out.println(name.getMethodName() + ": Elapsed Time=" + (System.nanoTime() - startTime) + " ns");
	}
	
	@Test
	public void testGetStudyDetails() throws Exception {
		Study study = manager.getStudy(STUDY_ID);
		assertNotNull(study);
		System.out.println("ID: " + study.getId());
		System.out.println("Name: " + study.getName());
		System.out.println("Title:" + study.getTitle());
		System.out.println("PI: " + study.getPrimaryInvestigator());
		System.out.println("Start Date:" + study.getStartDate());
		System.out.println("Creation Date: " + study.getCreationDate());
	}

	@Test
	public void testGetAllStudyFactor() throws Exception {
		System.out.println("testGetFactorDetails");
		int studyId = 10010;
		VariableTypeList factors = manager.getAllStudyFactors(studyId);
		assertNotNull(factors);
		Assert.assertTrue(factors.getVariableTypes().size() > 0);
		System.out.println("FACTORS RETRIEVED " + factors.getVariableTypes().size());
		factors.print(0);
	}

	@Test
	public void testGetAllStudyVariates() throws Exception {
		System.out.println("testGetVariates");
		int studyId = 10010;
		VariableTypeList variates = manager.getAllStudyVariates(studyId);
		assertNotNull(variates);
		Assert.assertTrue(variates.getVariableTypes().size() > 0);
		variates.print(0);
	}

	@Test
	public void testGetStudiesByFolder() throws Exception {
		int folderId = 1030;
		StudyResultSet resultSet = manager.searchStudies(new ParentFolderStudyQueryFilter(folderId), 5);
		System.out.println("testGetStudiesByFolder(" + folderId + "): " + resultSet.size());
		Assert.assertTrue(resultSet.size() > 0);
		while (resultSet.hasMore()) {
			StudyReference studyRef = resultSet.next();
			System.out.println(studyRef);
		}
	}
	
	@Test
	public void testSearchStudiesForName() throws Exception {
		System.out.println("testSearchStudiesForName");
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
	    
		filter.setName("FooFoo"); //INVALID: Not a study, should not find any studies
		StudyResultSet resultSet = manager.searchStudies(filter, 10);
		Assert.assertTrue(resultSet.size() == 0);
		
		filter.setName("RYT2000WS"); //VALID: is a study
		
		resultSet = manager.searchStudies(filter, 10);
		System.out.println("INPUT: " + filter);
		System.out.println("Size: " + resultSet.size());
		while (resultSet.hasMore()) {
			System.out.println("\t" + resultSet.next());
			System.out.flush();
		}
	}
	
	@Test
	public void testSearchStudiesForStartDate() throws Exception {
		System.out.println("testSearchStudiesForStartDate");
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
	    filter.setStartDate(20050119);
		
		StudyResultSet resultSet = manager.searchStudies(filter, 10);
		System.out.println("INPUT: " + filter);
		System.out.println("Size: " + resultSet.size());
		while (resultSet.hasMore()) {
			System.out.println("\t" + resultSet.next());
			System.out.flush();
		}
	}
	
	@Test
	public void testSearchStudiesForSeason() throws Exception {
		Season seasons[] = { Season.GENERAL, Season.DRY, Season.WET };
		
		System.out.println("testSearchStudiesForSeason");
		
		for (Season season : seasons) {
			
		    System.out.println("Season: " + season);
			BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
			filter.setSeason(season);
			StudyResultSet resultSet = manager.searchStudies(filter, 10);
			System.out.println("Size: " + resultSet.size());
			while (resultSet.hasMore()) {
				System.out.println("\t" + resultSet.next());
				System.out.flush();
			}
		}
	}

	@Test
	public void testSearchStudiesForCountry() throws Exception {
		System.out.println("testSearchStudies");
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
	    
		filter.setCountry("Republic of the Philippines");
		
		StudyResultSet resultSet = manager.searchStudies(filter, 10);
		System.out.println("INPUT: " + filter);
		System.out.println("Size: " + resultSet.size());
		while (resultSet.hasMore()) {
			System.out.println("\t" + resultSet.next());
			System.out.flush();
		}
	}
	
	@Test
	public void testSearchStudiesForAll() throws Exception {
		System.out.println("testSearchStudiesForAll");
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
	    filter.setStartDate(20050119);
		filter.setName("RYT2000WS"); //VALID: is a study
		filter.setCountry("Republic of the Philippines");
		filter.setSeason(Season.DRY);
		
		StudyResultSet resultSet = manager.searchStudies(filter, 10);
		System.out.println("INPUT: " + filter);
		System.out.println("Size: " + resultSet.size());
		while (resultSet.hasMore()) {
			System.out.println("\t" + resultSet.next());
			System.out.flush();
		}
	}

	@Test
	public void testGetRootFolders() throws Exception {
		List<FolderReference> rootFolders = manager
				.getRootFolders(Database.CENTRAL);
		assertNotNull(rootFolders);
		assert (rootFolders.size() > 0);
		System.out.println("testGetRootFolders(): " + rootFolders.size());
		for (FolderReference node : rootFolders) {
			System.out.println("   " + node);
		}
	}

	@Test
	public void testGetChildrenOfFolder() throws Exception {
		List<Integer> folderIds = Arrays.asList(1000, 2000);
		for (Integer folderId : folderIds){
			List<Reference> childrenNodes = manager.getChildrenOfFolder(folderId);
			assertNotNull(childrenNodes);
			assert (childrenNodes.size() > 0);
			System.out.println("testGetChildrenOfFolder(folderId="+folderId+"): " + childrenNodes.size());
			for (Reference node : childrenNodes) {
				System.out.println("   " + node);
			} 
		}
	}

	@Test
	public void testGetDatasetNodesByStudyId() throws Exception {
		Integer studyId = 10010;
		List<DatasetReference> datasetReferences = manager
				.getDatasetReferences(studyId);
		assertNotNull(datasetReferences);
		assert (datasetReferences.size() > 0);
		System.out.println("testGetDatasetNodesByStudyId(): "
				+ datasetReferences.size());
		for (DatasetReference node : datasetReferences) {
			System.out.println("   " + node);
		}
	}

	@Test
	public void testSearchStudiesByGid() throws Exception {
		System.out.println("testSearchStudiesByGid");
		Integer gid = 70125;
		GidStudyQueryFilter filter = new GidStudyQueryFilter(gid);
		StudyResultSet resultSet = manager.searchStudies(filter, 50);
		Assert.assertNotNull(resultSet);
		System.out.println("Study Count: " + resultSet.size());
		while (resultSet.hasMore()) {
			StudyReference studyRef = resultSet.next();
			System.out.println(studyRef);
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
		
		VariableList locationVariableList = createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1", "prop2");
		studyValues.setLocationId(manager.addTrialEnvironment(locationVariableList));
		
		VariableList germplasmVariableList = createGermplasm("unique name", "1000", "name", "2000", "prop1", "prop2");
		studyValues.setGermplasmId(manager.addStock(germplasmVariableList));

		StudyReference studyRef = manager.addStudy(parentStudyId, typeList, studyValues);

		Assert.assertTrue(studyRef.getId() < 0);
		System.out.println("testAddStudy(): " + studyRef);
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

		Assert.assertTrue(studyRef.getId() < 0);
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
	public void testCountExperiments() throws Exception {
		System.out.println("Dataset Experiment Count: "
				+ manager.countExperiments(DATASET_ID));
	}

	@Test
	public void testGetExperiments() throws Exception {
		for (int i = 0; i < 2; i++) {
			List<Experiment> experiments = manager.getExperiments(DATASET_ID,
					50 * i, 50);
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
		//Parent study, assign a parent study id value, if none exists in db, 
		//you may create a dummy one. or you may run testAddStudy first to create
		//the study
		int parentStudyId = -1;
		
		VariableTypeList typeList = new VariableTypeList();
		VariableList variableList = new VariableList();
		Variable variable;
		
		//please make sure that the study name is unique and does not exist in the db.
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
		System.out.println("Dataset added : " + datasetReference);
		
	}
	
	@Test
	public void testAddDatasetWithNoDataType() throws Exception {
		System.out.println("Test addDatasetWithNoCoreValues");
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
		//Parent study, assign a parent study id value, if none exists in db, 
		//you may create a dummy one. or you may run testAddStudy first to create
		//the study
		int parentStudyId = -1;
		
		VariableTypeList typeList = new VariableTypeList();
		VariableList variableList = new VariableList();
		Variable variable;
		
		//please make sure that the study name is unique and does not exist in the db.
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
		System.out.println("Dataset added : " + datasetReference);
		
		DataSet dataSet = manager.getDataSet(datasetReference.getId());
		System.out.println("Original Dataset");
		dataSet.print(3);
		
		VariableType variableType = new VariableType();
		variableType.setLocalName("Dog");
		variableType.setLocalDescription("Man's best friend");
		variableType.setStandardVariable(ontologyManager.getStandardVariable(8240));
		variableType.setRank(99);
		manager.addDataSetVariableType(dataSet.getId(), variableType);
		
		dataSet = manager.getDataSet(datasetReference.getId());
		System.out.println("Modified Dataset");
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
		System.out.println("Test SetExperimentValue");
		StudyReference studyRef = this.addTestStudy();
		DatasetReference datasetRef = this.addTestDataset(studyRef.getId());
		addTestExperiments(datasetRef.getId(), 4);
		List<Experiment> experiments = manager.getExperiments(datasetRef.getId(), 0, 2);
		
		printExperiments("Original", datasetRef.getId());
		for (Experiment experiment: experiments) {
			manager.setExperimentValue(experiment.getId(), 18000, "666");
			manager.setExperimentValue(experiment.getId(), 18050, "19010");
			manager.setExperimentValue(experiment.getId(), 8200, "4");
		}
		printExperiments("Modified", datasetRef.getId());
	}
	
	@Test
	public void testGetTrialEnvironmentsInDataset() throws Exception {
		System.out.println("Test getTrialEnvironmentsInDataset");
		TrialEnvironments trialEnvironments = manager.getTrialEnvironmentsInDataset(10085);
		trialEnvironments.print(0);
	}
	
	@Test
	public void testGetStocksInDataset() throws Exception {
		System.out.println("Test getStocksInDataset");
		Stocks stocks = manager.getStocksInDataset(10085);
		stocks.print(0);
	}
	
	private void printExperiments(String title, int datasetId) throws Exception {
		System.out.println(title);
		List<Experiment> experiments = manager.getExperiments(datasetId, 0, 4);
		for (Experiment experiment: experiments) {
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
		System.out.println("testGetFactorsByProperty (dataset=" + datasetId + ", property=" + propertyId);
		DataSet dataset = manager.getDataSet(datasetId);
		VariableTypeList factors = dataset.getFactorsByProperty(propertyId);
		if (factors != null && factors.getVariableTypes() != null && factors.getVariableTypes().size() > 0) {
			for (VariableType factor : factors.getVariableTypes()) {
				factor.print(0);
			}
		} else {
			System.out.println("NO FACTORS FOUND FOR DATASET = " + datasetId + " WITH PROPERTY = " + propertyId);
		}
	}
	
	@Test
	public void testGetFactorsByFactorType() throws Exception {
		FactorType factorType = FactorType.DATASET;
		int datasetId = 10087;
		System.out.println("testGetFactorsByFactorType (dataset=" + datasetId + ", factorType=" + factorType + ")");
		DataSet dataset = manager.getDataSet(datasetId);
		VariableTypeList factors = dataset.getFactorsByFactorType(factorType);
		if (factors != null && factors.getVariableTypes() != null && factors.getVariableTypes().size() > 0) {
			for (VariableType factor : factors.getVariableTypes()) {
				factor.print(0);
			}
		} else {
			System.out.println("NO FACTORS FOUND FOR DATASET = " + datasetId + " WITH FACTOR TYPE = " + factorType);
		}
	}
	
	@Test
	public void testGetDataSetsByType() throws Exception {
		int studyId = 10010;
		DataSetType dataSetType = DataSetType.MEANS_DATA;
		System.out.println("testGetDataSetsByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
		List<DataSet> datasets = manager.getDataSetsByType(studyId, dataSetType);
		for (DataSet dataset : datasets) {
			System.out.println("Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
		}
		
		studyId = 10080;
		dataSetType = DataSetType.MEANS_DATA;
		System.out.println("testGetDataSetsByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
		datasets = manager.getDataSetsByType(studyId, dataSetType);
		for (DataSet dataset : datasets) {
			System.out.println("Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
		}
		
		System.out.println("Display data set type in getDataSet");
		DataSet dataSet = manager.getDataSet(10087);
		System.out.println("DataSet = " + dataSet.getId() + ", name = " + dataSet.getName() + ", description = " + dataSet.getDescription() + ", type = " + dataSet.getDataSetType()	);
	}
	
	@Test
	public void testFindOneDataSetByType() throws Exception {
		int studyId = 10010;
		DataSetType dataSetType = DataSetType.MEANS_DATA;
		System.out.println("testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
		DataSet dataset = manager.findOneDataSetByType(studyId, dataSetType);
	    if (dataset != null) {
		    System.out.println("Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
	    }
		
		studyId = 10080;
		dataSetType = DataSetType.MEANS_DATA;
		System.out.println("testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
		dataset = manager.findOneDataSetByType(studyId, dataSetType);
		if (dataset != null) {
			System.out.println("Dataset" + dataset.getId() + "-" + dataset.getName() + "-" + dataset.getDescription());
		}
		
		dataSetType = DataSetType.SUMMARY_DATA;
		System.out.println("testFindOneDataSetByType(studyId = " + studyId + ", dataSetType = " + dataSetType + ")");
		dataset = manager.findOneDataSetByType(studyId, dataSetType);
		Assert.assertTrue(dataset == null);
	}
	
	@Test
	public void testCountExperimentsByTrialEnvironmentAndVariate() throws Exception {
		long count = manager.countExperimentsByTrialEnvironmentAndVariate(10070, 20870);
		System.out.println("Count of Experiments By TE and Variate: " + count);
	}
	
	@Test
	public void testCountStocks() throws Exception {
		long count = manager.countStocks(10087, 10081, 18190);
		System.out.println("Test CountStocks: " + count);
	}
	
	@Test
	public void testDeleteDataSet() throws Exception {
		StudyReference studyRef = this.addTestStudy();
		DatasetReference datasetRef = this.addTestDataset(studyRef.getId());
		this.addTestExperiments(datasetRef.getId(), 10);
		
		System.out.println("Test Delete DataSet: " + datasetRef.getId());
		manager.deleteDataSet(datasetRef.getId());
	}
	
	@Test
	public void testDeleteExperimentsByLocation() throws Exception {
		StudyReference studyRef = this.addTestStudyWithNoLocation();
		DatasetReference datasetRef = this.addTestDatasetWithLocation(studyRef.getId());
		int locationId = this.addTestExperimentsWithLocation(datasetRef.getId(), 10);
		int locationId2 = this.addTestExperimentsWithLocation(datasetRef.getId(), 10);
		
		System.out.println("Test Delete ExperimentsByLocation: " + datasetRef.getId() + ", " + locationId);
		System.out.println("Location id of " + locationId2 + " will NOT be deleted");
		manager.deleteExperimentsByLocation(datasetRef.getId(), locationId);
	}
	
	@Test
	public void testGetLocalNameByStandardVariableId() throws Exception {
	    Integer projectId = 10085; 
	    Integer standardVariableId = 8230;
	    String localName = manager.getLocalNameByStandardVariableId(projectId, standardVariableId);
	    System.out.println("testGetLocalNameByStandardVariableId("+projectId+", "+standardVariableId+"): " + localName);	    
	}
	
	@Test
	public void testGetAllTrialEnvironments() throws Exception {
		System.out.println("testGetAllTrialEnvironemnts");
		TrialEnvironments environments = manager.getAllTrialEnvironments();
		System.out.println("SIZE=" + environments.size());
		environments.print(1);
	}
	
	@Test
	public void testGetPropertiesForTrialEnvironments() throws Exception {
		List<Integer> environmentIds = Arrays.asList(5770, 10081, -1);
		System.out.println("testGetPropertiesForTrialEnvironments = " + environmentIds);
		List<TrialEnvironmentProperty> properties = manager.getPropertiesForTrialEnvironments(environmentIds);
		System.out.println("SIZE=" + properties.size());
		for (TrialEnvironmentProperty property : properties) {
			property.print(0);
		}
	}
	
	@Test
	public void testGetStudiesForTrialEnvironments() throws Exception {
		List<Integer> environmentIds = Arrays.asList(5770, 10081);
		System.out.println("testGetStudiesForTrialEnvironments = " + environmentIds);
		List<StudyReference> studies = manager.getStudiesForTrialEnvironments(environmentIds);
		System.out.println("SIZE=" + studies.size());
		for (StudyReference study : studies) {
			study.print(1);
		}
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

	private VariableList createTrialEnvironment(String name, String latitude, String longitude, String data, String altitude, String property1, String property2) throws Exception{
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
	
	private VariableList createGermplasm(String name, String gid, String designation, String code, String property1, String property2) throws Exception{
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
		
		VariableList locationVariableList = createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1", "prop2");
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
		//Parent study, assign a parent study id value, if none exists in db, 
		
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
		//Parent study, assign a parent study id value, if none exists in db, 
		
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
		VariableList locationVariableList = createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1", "prop2");
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
}
