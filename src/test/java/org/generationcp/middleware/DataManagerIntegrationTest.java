package org.generationcp.middleware;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ManagerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base class with some common functionality required for Middleware data manager integration
 * tests (i.e. tests that require actual workbehch, central and local databse connections).
 */
public class DataManagerIntegrationTest extends MiddlewareIntegrationTest {
	
	protected static ManagerFactory managerFactory;
	
	static {
		
		HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(
				sessionUtil.getSessionFactory());

		managerFactory = new ManagerFactory();
		managerFactory.setSessionProvider(sessionProvider);
		managerFactory.setDatabaseName(connectionParameters.getDbName());
	}
	
	@BeforeClass
	public static void setUpSuper() throws FileNotFoundException, ConfigException, URISyntaxException, IOException {
		//common superclass setUp
	}
	
	@AfterClass
	public static void tearDownSuper() throws Exception {
		//common superclss tearDown
	}
}
