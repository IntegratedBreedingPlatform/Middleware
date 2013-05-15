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

package org.generationcp.middleware.v2.manager.test;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DataSetType;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.DatasetValues;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.ExperimentValues;
import org.generationcp.middleware.v2.domain.FactorType;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Reference;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.v2.domain.StudyValues;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.search.StudyResultSet;
import org.generationcp.middleware.v2.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.v2.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.v2.search.filter.ParentFolderStudyQueryFilter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStudyDataManagerImpl {

	private static final Integer STUDY_ID = 10010;
	private static final Integer DATASET_ID = 10045;

	private static ManagerFactory factory;
	private static StudyDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception {
		DatabaseConnectionParameters local = new DatabaseConnectionParameters(
				"testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters(
				"testDatabaseConfig.properties", "central");
		factory = new ManagerFactory(local, central);
		manager = factory.getNewStudyDataManager();
	}

	@Test
	public void getGetStudyDetails() throws Exception {
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
		int folderId = 1000;
		StudyResultSet resultSet = manager.searchStudies(new ParentFolderStudyQueryFilter(folderId), 5);
		System.out.println("testGetStudiesByFolder(" + folderId + "): " + resultSet.size());
		Assert.assertTrue(resultSet.size() > 0);
		while (resultSet.hasMore()) {
			StudyReference studyRef = resultSet.next();
			System.out.println(studyRef);
		}
	}

	@Test
	public void testSearchStudies() throws Exception {
		System.out.println("testSearchStudies");
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
	    //filter.setStartDate(20050119);
		//filter.setName("BULU"); //INVALID: Not a study, should not be returned
		filter.setName("RYT2000WS"); //VALID: is a study
		//filter.setCountry("Republic of the Philippines");
		//filter.setCountry("Republic of India");
		//filter.setSeason(Season.DRY);
		//filter.setSeason(Season.GENERAL); //do nothing for GENERAL SEASON
		//filter.setSeason(Season.WET);
		
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
		/* SQL SCRIPT TO VERIFY RESULTS
		Set @TermId_IS_STUDY = 1145;
		Set @TermId_HAS_PARENT_FOLDER = 1140;
		Set @folderId = 1000; -- 2000
		
		SELECT DISTINCT subject.project_id, subject.name,  (CASE WHEN (type_id = @TermId_IS_STUDY) THEN 1 ELSE 0 END) AS is_study
		FROM project subject
		INNER JOIN project_relationship pr on subject.project_id = pr.subject_project_id
		WHERE (pr.type_id = @TermId_HAS_PARENT_FOLDER or pr.type_id = @TermId_IS_STUDY) 
		    AND pr.object_project_id = @folderId
		ORDER BY name;
		*/

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

		Variable variable = createVariable(TermId.STUDY_NAME.getId(), "Study Name " + new Random().nextInt(10000), TermId.STUDY_NAME_STORAGE, 1);
		typeList.add(variable.getVariableType());
		variableList.add(variable);
		
		variable = createVariable(TermId.STUDY_TITLE.getId(), "Study Description", TermId.STUDY_TITLE_STORAGE, 2);
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
	public void testAddDataSet() throws Exception {
		//Parent study, assign a parent study id value, if none exists in db, 
		//you may create a dummy one. or you may run testAddStudy first to create
		//the study
		int parentStudyId = -1;
		
		VariableTypeList typeList = new VariableTypeList();
		VariableList variableList = new VariableList();
		Variable variable;
		
		//please make sure that the study name is unique and does not exist in the db.
		variable = createVariable(TermId.DATASET_NAME.getId(), "My Dataset Name " + new Random().nextInt(10000), TermId.DATASET_NAME_STORAGE, 1);
		typeList.add(variable.getVariableType());
		updateVariableType(variable.getVariableType(), "DATASET_NAME", "Dataset name (local)");
		variableList.add(variable);
		
		variable = createVariable(TermId.DATASET_TITLE.getId(), "My Dataset Description", TermId.DATASET_TITLE_STORAGE, 2);
		typeList.add(variable.getVariableType());
		updateVariableType(variable.getVariableType(), "DATASET_TITLE", "Dataset title (local)");
		variableList.add(variable);
		
		variable = createVariable(TermId.DATASET_TYPE.getId(), "10070", TermId.DATASET_INFO_STORAGE, 3);
		typeList.add(variable.getVariableType());
		updateVariableType(variable.getVariableType(), "DATASET_TYPE", "Dataset type (local)");
		variableList.add(variable);
		
		DatasetValues datasetValues = new DatasetValues();
		datasetValues.setVariableList(variableList);

		DatasetReference datasetReference = manager.addDataSet(parentStudyId, typeList, datasetValues);
		System.out.println("Dataset added : " + datasetReference);
		
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
		manager.addExperiment(dataSetId, experimentValues);
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
		FactorType factorType = FactorType.GERMPLASM;
		int datasetId = 10015;
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

	@AfterClass
	public static void tearDown() throws Exception {
		if (factory != null) {
			factory.close();
		}
	}
	
	
	private Variable createVariable(int termId, String value, TermId storedInTerm, int rank) {
		StandardVariable stVar = new StandardVariable();
		stVar.setId(termId);
		stVar.setStoredIn(new Term(storedInTerm.getId(), "", ""));
		VariableType vtype = new VariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		return var;
	}
	
	private void updateVariableType(VariableType type, String name, String description) {
		type.setLocalName(name);
		type.setLocalDescription(description);
	}

	private VariableList createTrialEnvironment(String name, String latitude, String longitude, String data, String altitude, String property1, String property2){
		VariableList variableList = new VariableList();
		variableList.add(createVariable(1, name, TermId.TRIAL_INSTANCE_STORAGE, 1));
		variableList.add(createVariable(2, latitude, TermId.LATITUDE_STORAGE, 2));
		variableList.add(createVariable(3, longitude, TermId.LONGITUDE_STORAGE, 3));
		variableList.add(createVariable(4, data, TermId.DATUM_STORAGE, 4));
		variableList.add(createVariable(5, altitude, TermId.ALTITUDE_STORAGE, 5));
		variableList.add(createVariable(6, property1, TermId.TRIAL_ENVIRONMENT_INFO_STORAGE, 6));
		variableList.add(createVariable(7, property2, TermId.TRIAL_ENVIRONMENT_INFO_STORAGE, 7));
		return variableList;
	}
	
	private VariableList createGermplasm(String name, String gid, String designation, String code, String property1, String property2){
		VariableList variableList = new VariableList();
		variableList.add(createVariable(1, name, TermId.ENTRY_NUMBER_STORAGE, 1));
		variableList.add(createVariable(2, gid, TermId.ENTRY_GID_STORAGE, 2));
		variableList.add(createVariable(3, designation, TermId.ENTRY_DESIGNATION_STORAGE, 3));
		variableList.add(createVariable(4, code, TermId.ENTRY_CODE_STORAGE, 4));
		variableList.add(createVariable(6, property1, TermId.GERMPLASM_ENTRY_STORAGE, 5));
		variableList.add(createVariable(7, property2, TermId.GERMPLASM_ENTRY_STORAGE, 6));
		return variableList;

	}

}
