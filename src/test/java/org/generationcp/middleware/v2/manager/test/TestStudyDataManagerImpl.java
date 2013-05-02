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
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.domain.AbstractNode;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DatasetNode;
import org.generationcp.middleware.v2.domain.FactorDetails;
import org.generationcp.middleware.v2.domain.FolderNode;
import org.generationcp.middleware.v2.domain.ObservationDetails;
import org.generationcp.middleware.v2.domain.StudyDetails;
import org.generationcp.middleware.v2.domain.StudyNode;
import org.generationcp.middleware.v2.domain.StudyQueryFilter;
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
		DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
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
    public void testGetObservationDetails() throws Exception {
    	System.out.println("testGetObservationDetails");
    	int studyId = 10010;
    	List<ObservationDetails> observationDetails = manager.getObservations(studyId);
    	assertNotNull(observationDetails);
    	Assert.assertTrue(observationDetails.size() > 0);
    	printVariableDetails(studyId, observationDetails);
    }

    @Test
    public void testSearchStudies() throws Exception {
    	System.out.println("testSearchStudies");
    	StudyQueryFilter filter = new StudyQueryFilter();
       	//filter.setStartDate(20050119);
       	//filter.setName("BULU"); //INVALID: Not a study, should not be returned
       	//filter.setName("2002WS-CHA"); //VALID: is a study
    	//filter.setCountry("Republic of the Philippines");
    	filter.setSeason(Season.DRY);
    	//filter.setSeason(Season.GENERAL); //do nothing for GENERAL SEASON
    	//filter.setSeason(Season.WET); //currently has no data
    	filter.setStart(0);
    	filter.setNumOfRows(10);
    	List<StudyNode> studies = manager.searchStudies(filter);
    	System.out.println("INPUT: " + filter);
    	for (StudyNode study : studies) {
    		System.out.println("\t" + study.getId() + " - " + study.getName());
    	}
    }

	@Test
	public void testGetRootFolders() throws Exception {
		List<FolderNode> rootFolders = manager.getRootFolders(Database.CENTRAL);
		assertNotNull(rootFolders);
		assert (rootFolders.size() > 0);
		System.out.println("testGetRootFolders(): " + rootFolders.size());
		for (FolderNode node : rootFolders) {
			System.out.println("   " + node);
		}
	}

	@Test
	public void testGetChildrenOfFolder() throws Exception {
		Integer folderId = 1000;
		List<AbstractNode> childrenNodes = manager.getChildrenOfFolder(
				folderId, Database.CENTRAL);
		assertNotNull(childrenNodes);
		assert (childrenNodes.size() > 0);
		System.out
				.println("testGetChildrenOfFolder(): " + childrenNodes.size());
		for (AbstractNode node : childrenNodes) {
			System.out.println("   " + node);
		}
	}

	@Test
	public void testGetDatasetNodesByStudyId() throws Exception {
		Integer studyId = 10010;
		List<DatasetNode> datasetNodes = manager.getDatasetNodesByStudyId(
				studyId, Database.CENTRAL);
		assertNotNull(datasetNodes);
		assert (datasetNodes.size() > 0);
		System.out.println("testGetDatasetNodesByStudyId(): "
				+ datasetNodes.size());
		for (DatasetNode node : datasetNodes) {
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
				System.out.println("Study- " + study.getId() + " - " + study.getName());
			}
		} else {
			System.out.println("No Studies with GID " + gid + " found");
		}
	}


   //================================  helper methods =============================
   private <T extends VariableDetails> void printVariableDetails(int studyId, List<T> details) {
	   if (details != null && details.size() > 0) {
		   System.out.println("NUMBER OF VARIABLES = " + details.size());
		   for (VariableDetails detail : details) {
			   System.out.println("\nFACTOR " + detail.getId() + " (study = " + detail.getStudyId() + ")");
			   System.out.println("\tNAME = " + detail.getName());
			   System.out.println("\tDESCRIPTION = " + detail.getDescription());
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
		Integer projectKey = 1; 
		String title = "Test Study Title";
		String objective = "Test Study Objective"; 
		Integer primaryInvestigator = 1;
		String type = CVTermId.STUDY_TYPE.getId().toString();
       Integer startDate = (int) System.currentTimeMillis();; 
       Integer endDate = (int) System.currentTimeMillis();; 
       Integer user = 1; 
       Integer status = 1; 
       Integer hierarchy = 1000; // parent id 
       Integer creationDate = (int) System.currentTimeMillis();
		
		Study study = new Study(studyId, name, projectKey, title, objective,
				primaryInvestigator, type, startDate, endDate, user, status,
				hierarchy, creationDate);
		study = manager.addStudy(study);

		assert(study.getId() < 0);
		System.out.println("testAddStudy(): " + study);
	}

	@Test
	public void testAddStudyDetails() throws Exception {
		Integer studyId = 0;
		String name = "Test Study 2";
		String title = "Test Study Title";
		String objective = "Test Study Objective"; 
		Integer primaryInvestigator = 1;
		String type = CVTermId.STUDY_TYPE.getId().toString();
        Integer startDate = (int) System.currentTimeMillis();; 
        Integer endDate = (int) System.currentTimeMillis();; 
        Integer user = 1; 
        Integer status = 1; 
        Integer creationDate = (int) System.currentTimeMillis();
		
		StudyDetails studyDetails = new StudyDetails(studyId, name,
				title, objective, primaryInvestigator, type, startDate,
				endDate, user, status, creationDate);
		studyDetails = manager.addStudyDetails(studyDetails);

		assert(studyDetails.getId() < 0);
		System.out.println("testAddStudyDetails(): " + studyDetails);
	}

	@Test
	public void getDataSet() throws Exception {
		DataSet dataSet = manager.getDataSet(DATASET_ID);
		dataSet.getStudy().print(0);
	}
	
	public void testAddDataSet() throws Exception {
		//get a dataset test data from central
		Integer datasetId = 10015;
		DataSet dataset = manager.getDataSet(datasetId);
		
		//save the dataset to local (copied from central)
		DataSet newDataset = manager.addDataSet(dataset);
		
		//assert that the old and new dataset are the same, except for their ids
		System.out.println("ORIGINAL DATASET");
		dataset.print(0);
		System.out.println("NEWLY CREATED DATASET");
		newDataset.print(0);
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		if (factory != null) {
			factory.close();
		}
	}

	
}
