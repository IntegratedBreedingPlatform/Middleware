
package org.generationcp.middleware.dao;

import java.util.HashSet;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.h2h.GermplasmLocationInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class BreedersQueryDaoIntegrationTest extends IntegrationTestBase {

	private static BreedersQueryDao dao;

	@Before
	public void setUp() {
		BreedersQueryDaoIntegrationTest.dao = new BreedersQueryDao(this.sessionProvder.getSession());
	}

	// FIXME I assume some Rice corp data in central schema for my test
	// assertions. Make me independent by setting up the data I need first, read
	// that data, assert and then remove the data I created.
	@Test
	public void testGetGermplasmLocationInfoByEnvironmentIds() throws MiddlewareQueryException {
		HashSet<Integer> environmentIds = new HashSet<Integer>();
		environmentIds.add(5794);
		environmentIds.add(5795);
		environmentIds.add(5796);
		environmentIds.add(5880);

		List<GermplasmLocationInfo> result = BreedersQueryDaoIntegrationTest.dao.getGermplasmLocationInfoByEnvironmentIds(environmentIds);
		Assert.assertEquals(89, result.size());
	}

	@Test
	public void testGetEnvironmentIdsForGermplasm() throws MiddlewareQueryException {
		HashSet<Integer> gids = new HashSet<Integer>();
		gids.add(2586617);

		List<Integer> result = BreedersQueryDaoIntegrationTest.dao.getTrialEnvironmentIdsForGermplasm(gids);
		Assert.assertEquals(42, result.size());

		gids = new HashSet<Integer>();

		result = BreedersQueryDaoIntegrationTest.dao.getTrialEnvironmentIdsForGermplasm(gids);
		Assert.assertEquals(0, result.size());
	}

}
