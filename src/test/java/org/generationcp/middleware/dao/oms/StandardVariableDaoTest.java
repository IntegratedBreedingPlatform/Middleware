package org.generationcp.middleware.dao.oms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermSummary;
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
	
	private static final int PLANT_HEIGHT_ID = 18020, GRAIN_YIELD_ID = 18000;
	
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
	public void testGetStandardVariableSummary() throws MiddlewareQueryException {
		
		StandardVariableDao dao = new StandardVariableDao(factory.getSessionProviderForCentral().getSession());
		
		//Load summary from the view
		StandardVariableSummary summary = dao.getStandardVariableSummary(PLANT_HEIGHT_ID);	
		Assert.assertNotNull(summary);

		//Load details using existing method
		StandardVariable details = manager.getStandardVariable(PLANT_HEIGHT_ID);
		Assert.assertNotNull(details);
		
		//Make sure that the summary data loaded from view matches with details data loaded using the usual method.
		assertVariableDataMatches(summary, details);	
	}
	
	
	@Test
	public void testGetStarndardVariableSummaries() throws MiddlewareQueryException {
		
		StandardVariableDao dao = new StandardVariableDao(factory.getSessionProviderForCentral().getSession());		
		List<StandardVariableSummary> starndardVariableSummaries = dao.getStarndardVariableSummaries(Arrays.asList(GRAIN_YIELD_ID, PLANT_HEIGHT_ID));		
		Assert.assertEquals(2, starndardVariableSummaries.size());
	}
	
	private void assertVariableDataMatches(StandardVariableSummary summary, StandardVariable details) {
		Assert.assertEquals(summary.getId(), new Integer(details.getId()));
		Assert.assertEquals(summary.getName(), details.getName());
		Assert.assertEquals(summary.getDescription(), details.getDescription());
		
		assertTermDataMatches(summary.getProperty(), details.getProperty());
		assertTermDataMatches(summary.getMethod(), details.getMethod());
		assertTermDataMatches(summary.getScale(), details.getScale());
		assertTermDataMatches(summary.getIsA(), details.getIsA());
		assertTermDataMatches(summary.getDataType(), details.getDataType());
		assertTermDataMatches(summary.getStoredIn(), details.getStoredIn());
		
		Assert.assertEquals(summary.getPhenotypicType(), details.getPhenotypicType());
	}
	
	private void assertTermDataMatches(TermSummary termSummary, Term termDetails) {
		Assert.assertEquals(termSummary.getId(), new Integer(termDetails.getId()));
		Assert.assertEquals(termSummary.getName(), termDetails.getName());
		Assert.assertEquals(termSummary.getDefinition(), termDetails.getDefinition());
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		if (factory != null) {
			factory.close();
		}
	}

}
