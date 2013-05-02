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

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.v2.domain.CvTerm;
import org.generationcp.middleware.v2.manager.api.OntologyDataManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestOntologyDataManagerImpl {

	private static final Integer CV_TERM_ID = 1010;
	private static final String CV_TERM_NAME = "Study Information";

	private static ManagerFactory factory;
	private static OntologyDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception {
		DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
		factory = new ManagerFactory(local, central);
		manager = factory.getNewOntologyDataManager();
	}

	@Test
	public void testGetCvTermById() throws Exception {
		CvTerm cvTerm = manager.getCvTermById(CV_TERM_ID);
		
		assertNotNull(cvTerm);
		assert(cvTerm.getId() == CV_TERM_ID);
		assert(cvTerm.getName().equals(CV_TERM_NAME));
		
		System.out.println("testGetCvTermById(): " + cvTerm);
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		if (factory != null) {
			factory.close();
		}
	}

	
}
