package org.generationcp.middleware.manager.test;


import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.NumericDataElement;
import org.generationcp.middleware.pojos.NumericRange;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.generationcp.middleware.pojos.Variate;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStudyDataManagerImpl
{
	private static ManagerFactory factory;
	private static StudyDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception 
	{
		DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
		factory = new ManagerFactory(local, central);
		manager = factory.getStudyDataManager();
	}
	
	@Test
	public void testGetGIDSByPhenotypicData() throws Exception
	{
		NumericRange range = new NumericRange(new Double(2000), new Double(3000));
		TraitCombinationFilter combination = new TraitCombinationFilter(new Integer(1003), new Integer(9), new Integer(30), range);
		List<TraitCombinationFilter> filters = new ArrayList<TraitCombinationFilter>();
		filters.add(combination);
		
		//TraitCombinationFilter combination1 = new TraitCombinationFilter(new Integer(1007), new Integer(266), new Integer(260), new Double(5));
		//filters.add(combination1);
		//TraitCombinationFilter combination2 = new TraitCombinationFilter(new Integer(1007), new Integer(266), new Integer(260), "5");
		//filters.add(combination2);
		
		List<Integer> results = manager.getGIDSByPhenotypicData(filters, 0, 10);
		System.out.println("RESULTS:");
		for(Integer gid: results)
			System.out.println(gid);
	}
	

	@Test
	public void testFindStudyByNameUsingLike() throws Exception{
		List<Study> studyList = manager.findStudyByName("IRTN%", 0, 5, Operation.LIKE, Database.CENTRAL);
		Assert.assertTrue(studyList != null);
		Assert.assertTrue(!studyList.isEmpty());
		
		for(Study study : studyList){
			System.out.println(study);
		}
	}

	@Test
	public void testFindStudyByNameUsingEqual() throws Exception{
		List<Study> studyList = manager.findStudyByName("PEATSOIL", 0, 5, Operation.EQUAL, Database.CENTRAL);
		Assert.assertTrue(studyList != null);
		Assert.assertTrue(!studyList.isEmpty());
		
		for(Study study : studyList){
			System.out.println(study);
		}
	}

	@Test
	public void testGetStudyByID() throws Exception{
		List<Study> studyList = manager.getStudyByID(new Integer(714));
		Assert.assertTrue(studyList != null);
		Assert.assertTrue(!studyList.isEmpty());
		
		for(Study study : studyList){
			System.out.println(study);
		}
	}

	@Test
	public void testGetAllTopLevelStudies() throws Exception {
		List<Study> topLevelStudies = manager.getAllTopLevelStudies(0, 10, Database.LOCAL);
		System.out.println("TOP LEVEL STUDIES: " + topLevelStudies.size());
		for (Study study : topLevelStudies) {
			System.out.println(study);
		}
	}
	
	@Test
	public void testGetStudiesByParentFolderID() throws Exception {
		List<Study> studies = manager.getStudiesByParentFolderID(640, 0, 100);
		System.out.println("STUDIES BY PARENT FOLDER: " + studies.size());
		for (Study study : studies) {
			System.out.println(study);
		}
	}

	@Test
	public void testGetFactorsByStudyID() throws Exception{
		List<Factor> factors = manager.getFactorsByStudyID(new Integer(430));
		Assert.assertTrue(factors != null);
		Assert.assertTrue(!factors.isEmpty());
		
		for(Factor factor : factors){
			System.out.println(factor);
		}
	}
	
	@Test
	public void testGetVariatesByStudyID() throws Exception{
		List<Variate> variates = manager.getVariatesByStudyID(new Integer(430));
		Assert.assertTrue(variates != null);
		Assert.assertTrue(!variates.isEmpty());
		
		for(Variate variate : variates){
			System.out.println(variate);
		}
	}

	@Test
	public void testGetEffectsByStudyID() throws Exception{
		List<StudyEffect> studyEffects = manager.getEffectsByStudyID(new Integer(430));
		Assert.assertTrue(studyEffects != null);
		Assert.assertTrue(!studyEffects.isEmpty());
		
		for(StudyEffect studyEffect : studyEffects){
			System.out.println(studyEffect);
		}
	}
	
	@Test
	public void testGetRepresentationByStudyID() throws Exception{
		List<Representation> representations = manager.getRepresentationByEffectID(new Integer(430));
		Assert.assertTrue(representations != null);
		Assert.assertTrue(!representations.isEmpty());
		
		for(Representation representation : representations){
			System.out.println(representation);
		}
	}
	
	@Test
	public void testGetFactorsByRepresentationId() throws Exception {
		List<Factor> factors = manager.getFactorsByRepresentationId(1176);
		System.out.println("FACTORS BY REPRESENTATION: " + factors.size());
		for (Factor factor : factors) {
			System.out.println(factor);
		}
	}
	
	@Test
	public void testGetOunitIDsByRepresentationId() throws Exception {
		List<Integer> ounitIDs = manager.getOunitIDsByRepresentationId(1176);
		System.out.println("OUNIT IDS BY REPRESENTATION: " + ounitIDs.size());
		System.out.println(ounitIDs);
	}
	
	@Test
	public void testGetVariatesByRepresentationId() throws Exception {
		List<Variate> variates = manager.getVariatesByRepresentationId(1176);
		System.out.println("VARIATES BY REPRESENTATION: " + variates.size());
		for (Variate variate: variates) {
			System.out.println(variate);
		}
	}
	
	@Test
	public void testGetNumericDataValuesByOunitIdList() throws Exception {
		List<Integer> ounitIdList = new ArrayList<Integer>();
		ounitIdList.add(447201);
		ounitIdList.add(447202);
		ounitIdList.add(447203);
		ounitIdList.add(447204);
		ounitIdList.add(447205);
		List<NumericDataElement> dataElements = manager.getNumericDataValuesByOunitIdList(ounitIdList);
		System.out.println("NUMERIC DATA VALUES BY OUNITIDLIST: " + dataElements.size());
		for (NumericDataElement data : dataElements) {
			System.out.println(data);
		}
	}
	
	@AfterClass
	public static void tearDown() throws Exception 
	{
		factory.close();
	}

}
