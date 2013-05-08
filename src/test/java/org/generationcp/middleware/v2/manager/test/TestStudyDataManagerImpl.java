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
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.Assert;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.DatasetValues;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.ExperimentValues;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Reference;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.StudyQueryFilter;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.v2.domain.StudyValues;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
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
		List<Study> studies = manager.getStudiesByFolder(folderId, 0, 5);
		assertNotNull(studies);
		Assert.assertTrue(studies.size() > 0);
		System.out.println("testGetStudiesByFolder(" + folderId + "): "
				+ studies.size());
		for (Study study : studies) {
			System.out.println(study);
		}
	}

	@Test
	public void testCountStudiesByFolder() throws Exception {
		int folderId = 1000;
		long count = manager.countStudiesByFolder(folderId);
		Assert.assertTrue(count > 0);
		System.out.println("testCountStudiesByFolder(" + folderId + "): "
				+ count);
	}

	@Test
	public void testSearchStudies() throws Exception {
		System.out.println("testSearchStudies");
		StudyQueryFilter filter = new StudyQueryFilter();
		// filter.setStartDate(20050119);
		// filter.setName("BULU"); //INVALID: Not a study, should not be
		// returned
		// filter.setName("2002WS-CHA"); //VALID: is a study
		// filter.setCountry("Republic of the Philippines");
		filter.setSeason(Season.DRY);
		// filter.setSeason(Season.GENERAL); //do nothing for GENERAL SEASON
		// filter.setSeason(Season.WET); //currently has no data
		filter.setStart(0);
		filter.setNumOfRows(10);
		List<StudyReference> studies = manager.searchStudies(filter);
		System.out.println("INPUT: " + filter);
		for (StudyReference study : studies) {
			System.out.println("\t" + study.getId() + " - " + study.getName());
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
		Integer folderId = 1000;
		List<Reference> childrenNodes = manager.getChildrenOfFolder(folderId);
		assertNotNull(childrenNodes);
		assert (childrenNodes.size() > 0);
		System.out
				.println("testGetChildrenOfFolder(): " + childrenNodes.size());
		for (Reference node : childrenNodes) {
			System.out.println("   " + node);
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
		Set<Study> studies = manager.searchStudiesByGid(gid);
		if (studies != null && studies.size() > 0) {
			for (Study study : studies) {
				System.out.println("Study- " + study.getId() + " - "
						+ study.getName());
			}
		} else {
			System.out.println("No Studies with GID " + gid + " found");
		}
	}

	@Test
	public void testAddStudy() throws Exception {

		// get a study test data from central
		Integer studyId = 1000;
		int parentStudyId = 1;
		Study study = manager.getStudy(studyId);
		
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
		studyValues.setGermplasmId(-1);
		studyValues.setLocationId(-1);
		
		StudyReference studyRef = manager.addStudy(parentStudyId, study.getConditionVariableTypes(), studyValues);

		assert (studyRef.getId() < 0);
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
		variable = createVariable(TermId.DATASET_NAME.getId(), "My Dataset Name " + new Random().nextInt(10000), TermId.DATASET_INFO_STORAGE, 1);
		typeList.add(variable.getVariableType());
		variableList.add(variable);
		
		variable = createVariable(TermId.DATASET_TITLE.getId(), "My Dataset Description", TermId.DATASET_INFO_STORAGE, 2);
		typeList.add(variable.getVariableType());
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
		VariableList variableList = new VariableList();
		variableList.add(createVariable(1, "loc desc", TermId.TRIAL_INSTANCE_STORAGE, 1));
		variableList.add(createVariable(2, "1.1", TermId.LATITUDE_STORAGE, 2));
		variableList.add(createVariable(3, "2.2", TermId.LONGITUDE_STORAGE, 3));
		variableList.add(createVariable(4, "datum", TermId.DATUM_STORAGE, 4));
		variableList.add(createVariable(5, "3.3", TermId.ALTITUDE_STORAGE, 5));
		variableList.add(createVariable(6, "prop1", TermId.TRIAL_ENVIRONMENT_INFO_STORAGE, 6));
		variableList.add(createVariable(7, "prop2", TermId.TRIAL_ENVIRONMENT_INFO_STORAGE, 7));
		manager.addTrialEnvironment(variableList);
	}
	
	@Test
	public void testAddGermplasm() throws Exception {
		VariableList variableList = new VariableList();
		variableList.add(createVariable(1, "unique name", TermId.ENTRY_NUMBER_STORAGE, 1));
		variableList.add(createVariable(2, "1000", TermId.ENTRY_GID_STORAGE, 2));
		variableList.add(createVariable(3, "name", TermId.ENTRY_DESIGNATION_STORAGE, 3));
		variableList.add(createVariable(4, "2000", TermId.ENTRY_CODE_STORAGE, 4));
		variableList.add(createVariable(6, "prop1", TermId.GERMPLASM_ENTRY_STORAGE, 5));
		variableList.add(createVariable(7, "prop2", TermId.GERMPLASM_ENTRY_STORAGE, 6));
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

}
