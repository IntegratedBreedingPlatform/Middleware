package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.Configuration;
import org.generationcp.middleware.service.api.ConfigurationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationServiceImplTest extends IntegrationTestBase {

	private ConfigurationService configurationService;

	@Before
	public void setUp() {
		if (this.configurationService == null) {
			this.configurationService = new ConfigurationServiceImpl(this.sessionProvder);
		}
	}

	@Test
	public void testGetConfiguration() {
		Configuration configuration = this.configurationService.getConfiguration("a.b.c");
		Assert.assertNotNull(configuration);
	}
}
