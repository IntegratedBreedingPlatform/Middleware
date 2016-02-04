
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GermplasmGroupingServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	private GermplasmDAO germplasmDAO;

	@Before
	public void setUp() throws Exception {
		if (this.germplasmDAO == null) {
			this.germplasmDAO = new GermplasmDAO();
			this.germplasmDAO.setSession(this.sessionProvder.getSession());
		}
	}

	@Test
	public void testIntegration() {
		// TODO integration testing with wheat DB with historic germplasm data loaded. Test gid 165.
		Germplasm germplasm = this.germplasmDataManager.getGermplasmByGID(165);
		GermplasmGroupingServiceImpl groupingService = new GermplasmGroupingServiceImpl(this.germplasmDAO);
		groupingService.markFixed(germplasm, true, true);
		Assert.fail("No assertions yet.");
	}
}
