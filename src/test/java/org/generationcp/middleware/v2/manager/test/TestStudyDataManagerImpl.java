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

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.pojos.FactorDetails;
import org.generationcp.middleware.v2.pojos.ObservationDetails;
import org.generationcp.middleware.v2.pojos.StudyDetails;
import org.generationcp.middleware.v2.pojos.VariableDetails;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStudyDataManagerImpl{

	private static final int STUDY_ID = 10010;
	
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
    	int studyId = 10015;
    	List<FactorDetails> factorDetails = manager.getFactorDetails(studyId);
    	assertNotNull(factorDetails);
    	printVariableDetails(studyId, factorDetails);
    }

    @Test
    public void testGetObservationDetails() throws Exception {
    	System.out.println("testGetObservationDetails");
    	int studyId = 10015;
    	List<ObservationDetails> observationDetails = manager.getObservationDetails(studyId);
    	assertNotNull(observationDetails);
    	printVariableDetails(studyId, observationDetails);
    }
    
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
    

}
