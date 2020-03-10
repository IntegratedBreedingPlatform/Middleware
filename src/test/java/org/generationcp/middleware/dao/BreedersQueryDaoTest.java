
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.generationcp.middleware.domain.h2h.GermplasmLocationInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * This is the <strong>unit</strong> test for BreedersQueryDao - must not require actual database connection. For
 * <strong>integration</strong> test see {@link BreedersQueryDaoIntegrationTest}.
 *
 */
public class BreedersQueryDaoTest {

	private static BreedersQueryDao dao;
	private static Session session;

	@BeforeClass
	public static void setUp() {
		BreedersQueryDaoTest.session = Mockito.mock(Session.class);
		BreedersQueryDaoTest.dao = new BreedersQueryDao(BreedersQueryDaoTest.session);
	}

	@Test
	public void testGetGermplasmLocationInfoByInstanceIds() throws MiddlewareQueryException {
		HashSet<Integer> instanceIds = new HashSet<Integer>();
		instanceIds.add(5794);
		instanceIds.add(5880);

		SQLQuery mockQuery = Mockito.mock(SQLQuery.class);
		ArrayList<Object[]> mockQueryResult = new ArrayList<Object[]>();

		Object[] resultRow0 =
				new Object[] {5794, 2586617, "ADT (R) 47 (NO SEEDS)", "AGRICULTURAL RESEARCH STATION, PULLA",
						"AGRICULTURAL RESEARCH STATION, PULLA"};
		mockQueryResult.add(resultRow0);

		Object[] resultRow1 = new Object[] {5880, 303724, "ADT (R) 48", "Agricultural Research Institute P.K. 16 Edirne", "Turkey"};
		mockQueryResult.add(resultRow1);

		Mockito.when(mockQuery.list()).thenReturn(mockQueryResult);
		Mockito.when(BreedersQueryDaoTest.session.createSQLQuery(Matchers.anyString())).thenReturn(mockQuery);

		List<GermplasmLocationInfo> result = BreedersQueryDaoTest.dao.getGermplasmLocationInfoByInstanceIds(instanceIds);
		Assert.assertEquals(2, result.size());

		Assert.assertEquals(resultRow0[0], result.get(0).getInstanceId());
		Assert.assertEquals(resultRow0[1], result.get(0).getGid());
		Assert.assertEquals(resultRow0[2], result.get(0).getGermplasmName());
		Assert.assertEquals(resultRow0[3], result.get(0).getLocationName());
		Assert.assertEquals(resultRow0[4], result.get(0).getCountryName());

		Assert.assertEquals(resultRow1[0], result.get(1).getInstanceId());
		Assert.assertEquals(resultRow1[1], result.get(1).getGid());
		Assert.assertEquals(resultRow1[2], result.get(1).getGermplasmName());
		Assert.assertEquals(resultRow1[3], result.get(1).getLocationName());
		Assert.assertEquals(resultRow1[4], result.get(1).getCountryName());

	}

}
