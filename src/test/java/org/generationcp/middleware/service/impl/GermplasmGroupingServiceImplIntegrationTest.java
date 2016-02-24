
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
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

	private NameDAO nameDAO;

	private MethodDAO methodDAO;

	@Before
	public void setUp() throws Exception {
		if (this.germplasmDAO == null) {
			this.germplasmDAO = new GermplasmDAO();
			this.germplasmDAO.setSession(this.sessionProvder.getSession());
		}

		if (this.nameDAO == null) {
			this.nameDAO = new NameDAO();
			this.nameDAO.setSession(this.sessionProvder.getSession());
		}

		if (this.methodDAO == null) {
			this.methodDAO = new MethodDAO();
			this.methodDAO.setSession(this.sessionProvder.getSession());
		}
	}

	@Test
	public void testIntegration() {
		Germplasm germplasm = this.germplasmDataManager.getGermplasmByGID(1);
		GermplasmGroupingServiceImpl groupingService = new GermplasmGroupingServiceImpl(this.germplasmDAO, this.nameDAO, this.methodDAO);
		groupingService.markFixed(germplasm, true, false);
		// Fake statement for debugging.
		Assert.assertTrue(true);
	}
}
