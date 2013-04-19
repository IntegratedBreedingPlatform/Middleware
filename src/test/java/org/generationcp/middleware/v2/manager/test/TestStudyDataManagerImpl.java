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

import junit.framework.Assert;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.pojos.AbstractNode;
import org.generationcp.middleware.v2.pojos.CVTermId;
import org.generationcp.middleware.v2.pojos.DatasetNode;
import org.generationcp.middleware.v2.pojos.DmsDataset;
import org.generationcp.middleware.v2.pojos.FactorDetails;
import org.generationcp.middleware.v2.pojos.FolderNode;
import org.generationcp.middleware.v2.pojos.ObservationDetails;
import org.generationcp.middleware.v2.pojos.ProjectProperty;
import org.generationcp.middleware.v2.pojos.ProjectRelationship;
import org.generationcp.middleware.v2.pojos.StudyDetails;
import org.generationcp.middleware.v2.pojos.StudyNode;
import org.generationcp.middleware.v2.pojos.StudyQueryFilter;
import org.generationcp.middleware.v2.pojos.VariableDetails;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStudyDataManagerImpl {

	private static final Integer STUDY_ID = 10010;

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
	}

    @Test
    public void testGetFactorDetails() throws Exception {
    	System.out.println("testGetFactorDetails");
    	int studyId = 10010;
    	List<FactorDetails> factorDetails = manager.getFactorDetails(studyId);
    	assertNotNull(factorDetails);
    	Assert.assertTrue(factorDetails.size() > 0);
    	printVariableDetails(studyId, factorDetails);
    }

    @Test
    public void testGetObservationDetails() throws Exception {
    	System.out.println("testGetObservationDetails");
    	int studyId = 10010;
    	List<ObservationDetails> observationDetails = manager.getObservationDetails(studyId);
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
	public void testAddDmsDataset() throws Exception {
		Integer datasetId = 0;
		String name = "Test Dataset";
		String description = "Test Description";
		List<ProjectProperty> properties = null;
		List<ProjectRelationship> relatedTos = null;
		List<ProjectRelationship> relatedBys = null;

		DmsDataset dataset = new DmsDataset(datasetId, name, description, properties, relatedTos, relatedBys);
		dataset = manager.addDmsDataset(dataset);
		
		assert(dataset.getProjectId() < 0);
		System.out.println("testAddDmsDataset(): " + dataset);
	}

	@Test
	public void testAddStudy() throws Exception {
		Integer studyId = 0;
		String name = "Test Study";
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
		
		Study study = new Study(studyId, name, projectKey, title, objective, primaryInvestigator, type, startDate, endDate, user, status, hierarchy, creationDate);		
		study = manager.addStudy(study);

		assert(study.getId() < 0);
		System.out.println("testAddStudy(): " + study);
	}

	
	@AfterClass
	public static void tearDown() throws Exception {
		factory.close();
	}

}
