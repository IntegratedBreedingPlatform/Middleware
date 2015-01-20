
package org.generationcp.middleware;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.utils.database.DatabaseSetupUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class with common functionality - mainly session factory creation - required for Middleware integration tests (i.e. tests that
 * require actual workbench, central and local database connections).
 */
public class MiddlewareIntegrationTest {

	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	protected static final int INDENT = 3;

	protected static DatabaseConnectionParameters connectionParameters, workbenchConnectionParameters;
	protected static HibernateUtil sessionUtil, workbenchSessionUtil;

	/**
	 * We hold session factories in a static field and initialise them only once for all tests to use as opening a session factory is an
	 * expensive operation.
	 */
	static {
		try {
			Configuration config = new PropertiesConfiguration("testDatabaseConfig.properties");
			if (config.getBoolean("drop.create.dbs")) {
				DatabaseSetupUtil.setupTestDatabases();
			}
			
			connectionParameters = new DatabaseConnectionParameters("testDatabaseConfig.properties", "crop");
			workbenchConnectionParameters = new DatabaseConnectionParameters("testDatabaseConfig.properties", "workbench");

			sessionUtil = new HibernateUtil(connectionParameters);
			workbenchSessionUtil = new HibernateUtil(workbenchConnectionParameters);

		} catch (FileNotFoundException e) {
			Assert.fail(e.getMessage());
		} catch (ConfigException e) {
			Assert.fail(e.getMessage());
		} catch (URISyntaxException e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (ConfigurationException e) {
			Assert.fail(e.getMessage());
		} catch (ConversionException e) {
			Assert.fail("Boolean config for drop.create.dbs in testDatabaseConfig.properties was not formed properly - needs to be true,t,false or f" + e.getMessage());		
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} 
	}

	@Rule
	public TestName name = new TestName();
	private long startTime;

	@Before
	public void beforeEachTest() {
		startTime = System.nanoTime();
	}

	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		LOG.debug("+++++ Test: " + getClass().getSimpleName() + "." + name.getMethodName() + " took " + ((double) elapsedTime / 1000000)
				+ " ms = " + ((double) elapsedTime / 1000000000) + " s +++++");
	}
}
