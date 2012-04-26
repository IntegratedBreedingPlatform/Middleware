package org.generationcp.middleware.manager.test;


import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.NumericRange;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
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
	
	@AfterClass
	public static void tearDown() throws Exception 
	{
		factory.close();
	}

}
