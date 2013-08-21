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


import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestCrossStudyDataManagerImpl {

	private static ManagerFactory factory;
	private static CrossStudyDataManager manager;
	
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
		manager = factory.getCrossStudyDataManager();
	}

	@Before
	public void beforeEachTest() {
		startTime = System.nanoTime();
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		System.out.println("#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
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
	
    @Test
    public void testGetTraitsForNumericVariates() throws Exception {
        List<Integer> environmentIds = Arrays.asList(10081, 10082, 10083, 10084, 10085, 10086, 10087); //Rice
        List<NumericTraitInfo> result = manager.getTraitsForNumericVariates(environmentIds);
        System.out.println("testGetTraitsForNumericVariates(): " + result.size());
        for (NumericTraitInfo trait : result){
            trait.print(4);
        }
    }
    
    @Test
    public void testGetTraitsForCharacterVariates() throws Exception {
        List<Integer> environmentIds = Arrays.asList( 10040, 10050, 10060, 10070); //Rice
        List<CharacterTraitInfo> result = manager.getTraitsForCharacterVariates(environmentIds);
        System.out.println("testGetTraitsForCharacterVariates(): " + result.size());
        for (CharacterTraitInfo trait : result){
            trait.print(4);
        }
        System.out.println("testGetTraitsForCharacterVariates(): " + result.size());
    }
   
    @Test
    public void testGetTraitsForCategoricalVariates() throws Exception {
        List<Integer> environmentIds = Arrays.asList( 10040, 10050, 10060, 10070); //Rice
        List<CategoricalTraitInfo> result = manager.getTraitsForCategoricalVariates(environmentIds);
        System.out.println("testGetTraitsForCategoricalVariates(): " + result.size());
        for (CategoricalTraitInfo trait : result){
            trait.print(4);
        }
    }
   

	
	@AfterClass
	public static void tearDown() throws Exception {
		if (factory != null) {
			factory.close();
		}
	}
	
	
}
