package org.generationcp.middleware.dao;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NamingConfigurationDAOTest extends IntegrationTestBase {

	private NamingConfigurationDAO namingConfigurationDAO;

	@Before
	public void setUp() throws Exception {
		this.namingConfigurationDAO = new NamingConfigurationDAO(this.sessionProvder.getSession());
	}

	@Test
	public void testGetByName() {
		final NamingConfiguration code1 = this.namingConfigurationDAO.getByName("CODE 1");
		Assert.assertNotNull(code1);
		final NamingConfiguration code2 = this.namingConfigurationDAO.getByName("CODE 2");
		Assert.assertNotNull(code2);
		final NamingConfiguration code3 = this.namingConfigurationDAO.getByName("CODE 3");
		Assert.assertNotNull(code3);
	}

}
