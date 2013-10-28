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


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.util.Debug;
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
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
		startTime = System.nanoTime();
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
	}
	
	@Test
	public void testGetAllTrialEnvironments() throws Exception {
		TrialEnvironments environments = manager.getAllTrialEnvironments();
		environments.print(1);
		Debug.println(0, "SIZE=" + environments.size());
	}
	
	@Test
	public void testGetTrialEnvironments() throws Exception {
		long count = manager.countAllTrialEnvironments();
		Integer batchSizeInt = 1000;
		int iterations = ((Double) Math.ceil(count / batchSizeInt.doubleValue())).intValue();
		
		for (int i = 0; i < iterations; i++){
			int start = i * batchSizeInt;
			TrialEnvironments environments = manager.getTrialEnvironments(start, batchSizeInt);
			environments.print(1);
			Debug.println(0, "start=" + start + ", size=" + environments.size());
		}
	}
	
	@Test
	public void testCountAllTrialEnvironments() throws Exception {
		long count = manager.countAllTrialEnvironments();
		Debug.println(0, "SIZE=" + count);
	}
	
	@Test
	public void testGetPropertiesForTrialEnvironments() throws Exception {
		List<Integer> environmentIds = Arrays.asList(5770, 10081, -1);
		Debug.println(0, "testGetPropertiesForTrialEnvironments = " + environmentIds);
		List<TrialEnvironmentProperty> properties = manager.getPropertiesForTrialEnvironments(environmentIds);
		Debug.println(0, "SIZE=" + properties.size());
		for (TrialEnvironmentProperty property : properties) {
			property.print(0);
		}
	}
	
	@Test
	public void testGetStudiesForTrialEnvironments() throws Exception {
		List<Integer> environmentIds = Arrays.asList(5770, 10081);
		Debug.println(0, "testGetStudiesForTrialEnvironments = " + environmentIds);
		List<StudyReference> studies = manager.getStudiesForTrialEnvironments(environmentIds);
		Debug.println(0, "SIZE=" + studies.size());
		for (StudyReference study : studies) {
			study.print(1);
		}
	}
	
    @Test
    public void testGetTraitsForNumericVariates() throws Exception {
        List<Integer> environmentIds = Arrays.asList(10081, 10082, 10083, 10084, 10085, 10086, 10087); //Rice
        List<NumericTraitInfo> result = manager.getTraitsForNumericVariates(environmentIds);
        Debug.println(0, "testGetTraitsForNumericVariates(): " + result.size());
        for (NumericTraitInfo trait : result){
            trait.print(4);
        }
    }
    
    @Test
    public void testGetTraitsForCharacterVariates() throws Exception {
        List<Integer> environmentIds = Arrays.asList( 10040, 10050, 10060, 10070); //Rice
        List<CharacterTraitInfo> result = manager.getTraitsForCharacterVariates(environmentIds);
        Debug.println(0, "testGetTraitsForCharacterVariates(): " + result.size());
        for (CharacterTraitInfo trait : result){
            trait.print(4);
        }
    }
   
    @Test
    public void testGetTraitsForCategoricalVariates() throws Exception {
        List<Integer> environmentIds = Arrays.asList(10010, 10020, 10030, 10040, 10050, 10060, 10070); //Rice
        List<CategoricalTraitInfo> result = manager.getTraitsForCategoricalVariates(environmentIds);
        for (CategoricalTraitInfo trait : result) {
            //Debug.println(0, trait);
            trait.print(4);
        }
        Debug.println(0, "testGetTraitsForCategoricalVariates(): " + result.size());
    }
    
    @Test
    public void testGetEnvironmentsForGermplasmPairs() throws Exception {
    	
        List<GermplasmPair> pairs = new ArrayList<GermplasmPair>();

        // Case 1: Central - Central
        pairs.add(new GermplasmPair(2434138, 1356114));
        //pairs.add(new GermplasmPair(1317670, 383601));
        
        
        // Case 2: Local - Local
        pairs.add(new GermplasmPair(-1, -2));
        
        // Case 3: Central - Local
        
        
        List<GermplasmPair> result = manager.getEnvironmentsForGermplasmPairs(pairs);
        for (GermplasmPair pair : result) {
        	pair.print(4);
        }
        Debug.println(0, "testGetEnvironmentsForGermplasmPairs(): " + result.size());
    }
    
    @Test
    public void testGetObservationsForTraitOnGermplasms() throws Exception {
        /*
        List<Integer> traitIds = Arrays.asList(22564, 22073, 21735, 20848, 18000);
        List<Integer> germplasmIds = Arrays.asList(39, 1709, 1000);
        List<Integer> environmentIds = Arrays.asList(5770, 10085, 5748, -1, -6);
        */
    	
        List<Integer> traitIds = Arrays.asList(18020,18180,18190,18200);
        List<Integer> germplasmIds = Arrays.asList(1709);
        List<Integer> environmentIds = Arrays.asList(10081,10084,10085,10086);

        List<Observation> result = manager.getObservationsForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);
       
        for (Observation observation : result) {
            observation.print(4);
        }
        Debug.println(0, "testGetObservationsForTraitOnGermplasms(): " + result.size());
    }
  
    @Test
    public void testGetObservationsForTraits() throws Exception {
        List<Integer> traitIds = Arrays.asList(22564, 22073, 21735, 20848, 18000);
        List<Integer> environmentIds = Arrays.asList(5770, 10085, 5748, -1, -6);

        /*List<Observation> result = manager.getObservationsForTraits(traitIds, environmentIds);
       
        Debug.println(0, "testGetObservationsForTraits(): " + result.size());
        for (Observation observation : result) {
            observation.print(4);
        }
        Debug.println(0, "testGetObservationsForTraits(): " + result.size());
        */
        
        //new
        List<Observation> result1 = new ArrayList<Observation>();
        List<Observation> result = manager.getObservationsForTraits(traitIds, environmentIds,0,2);
        Debug.println(0, "new testGetObservationsForTraits(): " + result.size());
        for (Observation observation : result) {
            observation.print(4);
        }
        result1.addAll(result);
        
        result = manager.getObservationsForTraits(traitIds, environmentIds,2,2);
        Debug.println(0, "new testGetObservationsForTraits(): " + result.size());
        for (Observation observation : result) {
            observation.print(4);
        }
        result1.addAll(result);
        
        result = manager.getObservationsForTraits(traitIds, environmentIds,0,4);
        Debug.println(0, "new testGetObservationsForTraits(): " + result.size());
        for (Observation observation : result) {
            observation.print(4);
        }
        //compare combination of first 2 and new 4 observations 
        assertEquals(result1, result);
        
        result = manager.getObservationsForTraits(traitIds, environmentIds,299,2);
        Debug.println(0, "new testGetObservationsForTraits(): " + result.size());
        //should return 2 records from local given there are 299 central records
        assertEquals(2, result.size());
        for (Observation observation : result) {
            observation.print(4);
        }
        //compare with old
        List<Observation> oldResult = manager.getObservationsForTraits(traitIds, environmentIds);
        Debug.println(0, "RESULT SIZE of old testGetObservationsForTraits(): " + oldResult.size());
        //get last 2 (should be from local)
        oldResult = oldResult.subList(oldResult.size()-2, oldResult.size());
        Debug.println(0, "Size: " + oldResult.size());
        assertEquals(2, oldResult.size());
        for (Observation observation : oldResult) {
            observation.print(4);
        }
    }

    @Test
    public void testGetObservationsForTrait() throws Exception {
        int traitId = 22574;
    	List<Integer> environmentIds = Arrays.asList(5771, 5772, 5773, 5774, 5775, 5776); //Rice
        
    	List<TraitObservation> result = manager.getObservationsForTrait(traitId, environmentIds);
        Debug.println(0, "testGetObservationsForTrait(): " + result.size());
        for (TraitObservation trait : result){
            Debug.println(0, "    " + trait);
        }
    }
	
	@Test
	public void testGetEnvironmentsForTraits() throws Exception {
    	List<Integer> traitIds = new ArrayList<Integer>();
    	
    	traitIds.add(22006);
    	traitIds.add(22485);
    	
		Debug.println(0, "testGetEnvironmentForTraits");
		TrialEnvironments environments = manager.getEnvironmentsForTraits(traitIds);
		Debug.println(0, "SIZE=" + environments.size());
		environments.print(1);
	}
  
	@AfterClass
	public static void tearDown() throws Exception {
		if (factory != null) {
			factory.close();
		}
	}
	
	
}
