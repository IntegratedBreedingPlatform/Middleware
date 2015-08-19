
package org.generationcp.middleware;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.pojos.workbench.CropType.CropEnum;
import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base class with some common functionality required for Middleware data manager integration tests (i.e. tests that require actual
 * workbehch, central and local databse connections).
 *
 * @deprecated use {@link IntegrationTestBase} instead.
 */
@Deprecated
public class DataManagerIntegrationTest extends MiddlewareIntegrationTest {

	protected static ManagerFactory managerFactory;

	static {

		HibernateSessionProvider sessionProvider =
				new HibernateSessionPerThreadProvider(MiddlewareIntegrationTest.sessionUtil.getSessionFactory());

		DataManagerIntegrationTest.managerFactory = new ManagerFactory();
		DataManagerIntegrationTest.managerFactory.setSessionProvider(sessionProvider);
		DataManagerIntegrationTest.managerFactory.setDatabaseName(MiddlewareIntegrationTest.connectionParameters.getDbName());
		DataManagerIntegrationTest.managerFactory.setCropName(CropEnum.RICE.toString());
		DataManagerIntegrationTest.managerFactory.setPedigreeProfile(PedigreeFactory.PROFILE_DEFAULT);
	}

	@BeforeClass
	public static void setUpSuper() throws FileNotFoundException, ConfigException, URISyntaxException, IOException {
		// common superclass setUp
	}

	@AfterClass
	public static void tearDownSuper() throws Exception {
		// common superclss tearDown
	}
}
