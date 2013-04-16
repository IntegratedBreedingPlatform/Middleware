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

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.pojos.StudyDetails;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
}
