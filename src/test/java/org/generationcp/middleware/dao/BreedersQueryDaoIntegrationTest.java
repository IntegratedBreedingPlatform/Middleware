package org.generationcp.middleware.dao;

import java.util.HashSet;
import java.util.List;

import org.junit.Assert;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.h2h.GermplasmLocationInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.junit.BeforeClass;
import org.junit.Test;

public class BreedersQueryDaoIntegrationTest extends MiddlewareIntegrationTest {
	
	private static BreedersQueryDao dao;
	
	@BeforeClass
	public static void setUp() {
		dao = new BreedersQueryDao(centralSessionUtil.getCurrentSession());
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
		
		List<GermplasmLocationInfo> result = dao.getGermplasmLocationInfoByEnvironmentIds(environmentIds);
		Assert.assertEquals(89, result.size());
	}

}
