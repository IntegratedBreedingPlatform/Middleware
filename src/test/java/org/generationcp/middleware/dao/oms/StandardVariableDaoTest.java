package org.generationcp.middleware.dao.oms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class StandardVariableDaoTest {
	
	private static final int VARIABLE_ID = 18020;
	
	private static ManagerFactory factory;
	private static OntologyDataManager manager;

	@Rule
	public TestName name = new TestName();
	
	private long startTime;
	
	@Before
	public void beforeEachTest() {
        Debug.println(0, "***** Begin Test: " + name.getMethodName());
		startTime = System.nanoTime();
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		Debug.println(0, "***** End Test: " + name.getMethodName() + ". Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000) + " ms = " + ((double) elapsedTime/1000000000) + " s\n\n");
	}
	
	@BeforeClass
	public static void once() throws FileNotFoundException, ConfigException, URISyntaxException, IOException {
		DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
		
		factory = new ManagerFactory(local, central);
		manager = factory.getNewOntologyDataManager();
	}
	
	@Test
	public void testgetStandardVariable1() throws MiddlewareQueryException {
		StandardVariableDao dao = new StandardVariableDao(factory.getSessionProviderForCentral().getSession());
		StandardVariableSummary variable = dao.getStandardVariableSummary(VARIABLE_ID);	
		Assert.assertNotNull(variable);
		System.out.println(variable);
	}
	
	@Test
	public void testGetStandardVariable2() throws MiddlewareQueryException {
		StandardVariable variable = manager.getStandardVariable(VARIABLE_ID);
		Assert.assertNotNull(variable);
		System.out.println(variable);							
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		if (factory != null) {
			factory.close();
		}
	}
	

}
