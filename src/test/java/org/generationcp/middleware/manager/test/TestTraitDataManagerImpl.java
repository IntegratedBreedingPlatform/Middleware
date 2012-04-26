package org.generationcp.middleware.manager.test;


import java.util.List;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.pojos.Scale;
import org.generationcp.middleware.pojos.ScaleContinuous;
import org.generationcp.middleware.pojos.ScaleDiscrete;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.TraitMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTraitDataManagerImpl
{
	private static ManagerFactory factory;
	private static TraitDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception
	{
		DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
		factory = new ManagerFactory(local, central);
		manager = factory.getTraitDataManager();
	}

	@Test
	public void testGetScaleById() throws Exception
	{
		Scale scale = manager.getScaleByID(new Integer(1));
		Assert.assertTrue(scale != null);
		System.out.println(scale);
	}
	
	@Test
	public void testGetAllScales() throws Exception
	{
		List<Scale> scales = manager.getAllScales(0, 5);
		Assert.assertTrue(scales != null);
		Assert.assertTrue(!scales.isEmpty());
		
		System.out.println("RESULTS:");
		for(Scale scale : scales)
		{
			System.out.println(scale);
		}
	}
	
	@Test
	public void testCountAllScales() throws Exception
	{
		System.out.println(manager.countAllScales());
	}
	
	@Test
	public void testGetScaleDiscreteDescription() throws Exception
	{
		String desc = manager.getScaleDiscreteDescription(new Integer(1), "1");
		Assert.assertTrue(desc != null);
		Assert.assertTrue(desc.length() > 0);
		System.out.println(desc);
	}
	
	@Test
	public void testGetDiscreteValuesOfScale() throws Exception
	{
		List<ScaleDiscrete> results = manager.getDiscreteValuesOfScale(new Integer(1));
		Assert.assertTrue(results != null);
		Assert.assertTrue(!results.isEmpty());
		
		System.out.println("RESULTS:");
		for(ScaleDiscrete sd : results)
		{
			System.out.println(sd);
		}
	}
	
	@Test
	public void testGetRangeOfContinuousScale() throws Exception
	{
		ScaleContinuous range = manager.getRangeOfContinuousScale(new Integer(68));
		Assert.assertTrue(range != null);
		System.out.println(range);
	}
	
	@Test
	public void testGetTraitById() throws Exception
	{
		Trait trait = manager.getTraitById(new Integer(305));
		Assert.assertTrue(trait != null);
		System.out.println(trait);
	}
	
	@Test
	public void testGetAllTraits() throws Exception
	{
		List<Trait> traits = manager.getAllTraits(0, 5);
		Assert.assertTrue(traits != null);
		Assert.assertTrue(!traits.isEmpty());
		
		System.out.println("RESULTS:");
		for(Trait trait : traits)
		{
			System.out.println(trait);
		}
	}
	
	@Test
	public void testCountAllTraits() throws Exception
	{
		System.out.println(manager.countAllTraits());
	}
	
	@Test
	public void testGetTraitMethodById() throws Exception
	{
		TraitMethod method = manager.getTraitMethodById(new Integer(1));
		Assert.assertTrue(method != null);
		System.out.println(method);
	}
	
	@Test
	public void testGetAllTraitMethods() throws Exception
	{
		List<TraitMethod> traits = manager.getAllTraitMethods(0, 5);
		Assert.assertTrue(traits != null);
		Assert.assertTrue(!traits.isEmpty());
		
		System.out.println("RESULTS:");
		for(TraitMethod trait : traits)
		{
			System.out.println(trait);
		}
	}
	
	@Test
	public void testCountAllTraitMethods() throws Exception
	{
		System.out.println(manager.countAllTraitMethods());
	}
	
	@Test
	public void testGetTraitMethodsByTraitId() throws Exception
	{
		List<TraitMethod> traits = manager.getTraitMethodsByTraitId(new Integer(1215));
		Assert.assertTrue(traits != null);
		Assert.assertTrue(!traits.isEmpty());
		
		System.out.println("RESULTS:");
		for(TraitMethod trait : traits)
		{
			System.out.println(trait);
		}
	}
	
	@Test
	public void testGetScalesByTraitId() throws Exception
	{
		List<Scale> scales = manager.getScalesByTraitId(new Integer(1215));
		Assert.assertTrue(scales != null);
		Assert.assertTrue(!scales.isEmpty());
		
		System.out.println("RESULTS:");
		for(Scale scale : scales)
		{
			System.out.println(scale);
		}
	}
	
	@AfterClass
	public static void tearDown() throws Exception
	{
		factory.close();
	}

}
