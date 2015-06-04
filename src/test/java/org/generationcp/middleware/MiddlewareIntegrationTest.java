
package org.generationcp.middleware;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
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

	protected static final Logger LOG = LoggerFactory.getLogger(MiddlewareIntegrationTest.class);
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

			MiddlewareIntegrationTest.workbenchConnectionParameters =
					new DatabaseConnectionParameters("testDatabaseConfig.properties", "workbench");
			MiddlewareIntegrationTest.connectionParameters = new DatabaseConnectionParameters("testDatabaseConfig.properties", "crop");

			MiddlewareIntegrationTest.sessionUtil = new HibernateUtil(MiddlewareIntegrationTest.connectionParameters);
			MiddlewareIntegrationTest.workbenchSessionUtil = new HibernateUtil(MiddlewareIntegrationTest.workbenchConnectionParameters);

		} catch (ConversionException e) {
			Assert.fail("Boolean config for drop.create.dbs in testDatabaseConfig.properties was not formed properly - needs to be true,t,false or f"
					+ e.getMessage());
		} catch (Exception e) {
			MiddlewareIntegrationTest.logExceptionInfoAndFail(e);
		}
	}

	static void logExceptionInfoAndFail(Exception e) {
		String msg = "Exception " + e.getMessage();
		if (e.getCause() != null) {
			msg += " caused by: " + e.getCause().getMessage();
		}
		MiddlewareIntegrationTest.LOG.info(msg);
		Assert.fail(msg);
	}

	@Rule
	public TestName name = new TestName();
	private long startTime;

	@Before
	public void beforeEachTest() {
		this.startTime = System.nanoTime();
	}

	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - this.startTime;
		MiddlewareIntegrationTest.LOG.debug("+++++ Test: " + this.getClass().getSimpleName() + "." + this.name.getMethodName() + " took "
				+ (double) elapsedTime / 1000000 + " ms = " + (double) elapsedTime / 1000000000 + " s +++++");
	}
}
