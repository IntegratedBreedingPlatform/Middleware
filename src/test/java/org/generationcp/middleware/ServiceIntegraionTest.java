package org.generationcp.middleware;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.ServiceFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base class with some common functionality required for Middleware services integration
 * tests (i.e. tests that require actual workbehch, central and local databse connections).
 */
public class ServiceIntegraionTest extends MiddlewareIntegrationTest {
	protected static ServiceFactory serviceFactory;
	
	static {
		
		HibernateSessionProvider sessionProviderForLocal = new HibernateSessionPerThreadProvider(
				localSessionUtil.getSessionFactory());
		HibernateSessionProvider sessionProviderForCentral = new HibernateSessionPerThreadProvider(
				centralSessionUtil.getSessionFactory());

		serviceFactory = new ServiceFactory();
		serviceFactory.setSessionProviderForCentral(sessionProviderForCentral);
		serviceFactory.setSessionProviderForLocal(sessionProviderForLocal);		
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
