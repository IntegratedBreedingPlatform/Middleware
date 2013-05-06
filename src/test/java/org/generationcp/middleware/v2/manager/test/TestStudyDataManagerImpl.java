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

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.FactorDetails;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Reference;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.domain.VariateDetails;
import org.generationcp.middleware.v2.domain.StudyDetails;
import org.generationcp.middleware.v2.domain.StudyQueryFilter;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.VariableDetails;
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
		StudyDetails studyDetails = manager.getStudyDetails(STUDY_ID);
		assertNotNull(studyDetails);
		System.out.println("ID: " + studyDetails.getId());
		System.out.println("Name: " + studyDetails.getName());
		System.out.println("Title:" + studyDetails.getTitle());
		System.out.println("Start Date:" + studyDetails.getStartDate());
		System.out.println("Creation Date: " + studyDetails.getCreationDate());
	}

	@Test
	public void testGetFactorDetails() throws Exception {
		System.out.println("testGetFactorDetails");
		int studyId = 10010;
		List<FactorDetails> factorDetails = manager.getFactors(studyId);
		assertNotNull(factorDetails);
		Assert.assertTrue(factorDetails.size() > 0);
		printVariableDetails(studyId, factorDetails);
	}

	@Test
	public void testGetVariates() throws Exception {
		System.out.println("testGetVariates");
		int studyId = 10010;
		List<VariateDetails> variates = manager.getVariates(studyId);
		assertNotNull(variates);
		Assert.assertTrue(variates.size() > 0);
		printVariableDetails(studyId, variates);
	}

	@Test
	public void testGetStudiesByFolder() throws Exception {
		int folderId = 1000;
		List<StudyDetails> studies = manager.getStudiesByFolder(folderId, 0, 5);
		assertNotNull(studies);
		Assert.assertTrue(studies.size() > 0);
		System.out.println("testGetStudiesByFolder(" + folderId + "): "
				+ studies.size());
		for (StudyDetails study : studies) {
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
		Set<StudyDetails> studies = manager.searchStudiesByGid(gid);
		if (studies != null && studies.size() > 0) {
			for (StudyDetails study : studies) {
				System.out.println("Study- " + study.getId() + " - "
						+ study.getName());
			}
		} else {
			System.out.println("No Studies with GID " + gid + " found");
		}
	}

	// ================================ helper methods
	// =============================
	private <T extends VariableDetails> void printVariableDetails(int studyId,
			List<T> details) {
		if (details != null && details.size() > 0) {
			System.out.println("NUMBER OF VARIABLES = " + details.size());
			for (VariableDetails detail : details) {
				System.out.println("\nFACTOR " + detail.getId() + " (study = "
						+ detail.getStudyId() + ")");
				System.out.println("\tNAME = " + detail.getName());
				System.out
						.println("\tDESCRIPTION = " + detail.getDescription());
				System.out.println("\tPROPERTY = " + detail.getProperty());
				System.out.println("\tMETHOD = " + detail.getMethod());
				System.out.println("\tSCALE = " + detail.getScale());
				System.out.println("\tDATA TYPE = " + detail.getDataType());
			}

		} else {
			System.out.println("NO VARIABLE FOUND FOR STUDY " + studyId);
		}
	}

	@Test
	public void testAddStudy() throws Exception {
		Integer studyId = 0;
		String name = "Test Study 1";
		String description = "Test Study Title";
		Integer parentId = 1000; // parent id
		Integer projectKey = 1;
		String title = "Test Study Title";
		String objective = "Test Study Objective";
		Integer primaryInvestigator = 1;
		String type = TermId.STUDY_TYPE.getId().toString();
		Integer startDate = (int) System.currentTimeMillis();
		Integer endDate = (int) System.currentTimeMillis();
		Integer user = 1;
		Integer status = 1;
		Integer hierarchy = 1000; // parent id
		Integer creationDate = (int) System.currentTimeMillis();

		// TODO PROPERTIES ProjectKey, Objective, type =
		// CVTermId.STUDY_TYPE.getId().toString(), startDate, endDate, user,
		// status, creationDate
		VariableTypeList variableTypes = new VariableTypeList();
		VariableTypeList conditionVariableTypes = new VariableTypeList();
		VariableTypeList constantVariableTypes = new VariableTypeList();
		VariableList conditions = new VariableList();
		VariableList constants = new VariableList();

		Study study = new Study(studyId, name, description,
				conditions, conditionVariableTypes, constants,
				constantVariableTypes);
		StudyReference studyRef = manager.addStudy(study);

		assert (studyRef.getId() < 0);
		System.out.println("testAddStudy(): " + study);
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
		// get a dataset test data from central
		Integer datasetId = 10015;
		DataSet dataset = manager.getDataSet(datasetId);
		List<Experiment> experiments = manager.getExperiments(datasetId, 0, /* 1093 */
				1);

		// save the dataset to local (copied from central)
		manager.addDataSet(dataset, experiments);

		/*
		 * compare the ff tables in central and local. they must be the same,
		 * except for primary keys/foreign keys >> project, projectprop,
		 * project_relationship, nd_experiment_project >> nd_experiment,
		 * nd_experimentprop >> nd_geolocation, nd_geolocationprop >>
		 * nd_experiment_stock, stock, stockprop >> nd_experiment_phenotype,
		 * phenotype
		 */
	}

	@AfterClass
	public static void tearDown() throws Exception {
		if (factory != null) {
			factory.close();
		}
	}

}
