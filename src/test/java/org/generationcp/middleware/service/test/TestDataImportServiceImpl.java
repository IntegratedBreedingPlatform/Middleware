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
package org.generationcp.middleware.service.test;

import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.service.ServiceFactory;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.utils.test.TestWorkbookUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestDataImportServiceImpl {
	
	private static ServiceFactory serviceFactory;
	private static ManagerFactory managerFactory;
	private static DataImportService dataImportService;
	private static StudyDataManager studyManager;
	
	private long startTime;
	
	@Rule
	public TestName name = new TestName();
	
	@BeforeClass
	public static void setUp() throws Exception {
		DatabaseConnectionParameters local = new DatabaseConnectionParameters(
				"testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters(
				"testDatabaseConfig.properties", "central");
		
		serviceFactory = new ServiceFactory(local, central);
		managerFactory = new ManagerFactory(local, central);
		
		dataImportService = serviceFactory.getDataImportService();
		studyManager = managerFactory.getNewStudyDataManager();

	}
	
	@Before
	public void beforeEachTest() {
		startTime = System.nanoTime();
	}
	
	@Test
	public void testSaveDataset() throws MiddlewareQueryException{
		Workbook workbook = TestWorkbookUtil.getTestWorkbook();
		workbook.print(0);
		int id = dataImportService.saveDataset(workbook);
		System.out.println("Created study:" + id);
		
//		studyManager.getStudy(id);
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		System.out.println("#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
	}
	
	
	@AfterClass
	public static void tearDown() throws Exception {
		if (serviceFactory != null) {
			serviceFactory.close();
		}
	}

}
