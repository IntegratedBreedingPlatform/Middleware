package org.generationcp.middleware.dao.dms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.sample.PlantDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Created by clarysabel on 1/26/18.
 */
public class ExperimentDaoTest {
	private static final String PROGRAM_UUID = "abcd-12345";
	private final List<Integer> gid1_environments = Arrays.asList(1001, 1002, 1003, 1004, 1005);
	private final List<Integer> gid2_environments = Arrays.asList(1002, 1003, 1004, 1005);
	private final List<Integer> gid3_environments = Arrays.asList(1003, 1004, 1005);

	private ExperimentDao experimentDao;
	private Session mockSession;
	private SQLQuery mockQuery;

	@Before
	public void beforeEachTest() {
		this.mockSession = Mockito.mock(Session.class);

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(this.mockSession);

		this.mockQuery = Mockito.mock(SQLQuery.class);
	}

	@Test
	public void testGetSampledPlants_Ok () {
		Mockito.when(this.mockSession.createSQLQuery(ExperimentDao.SQL_GET_SAMPLED_PLANTS_BY_STUDY)).thenReturn(this.mockQuery);

		List<Object[]> mockQueryResult = new ArrayList<Object[]>();

		Object[] mockDBRow1 = new Object[] {1, 1, "1"};
		mockQueryResult.add(mockDBRow1);

		Object[] mockDBRow2 = new Object[] {1, 2, "2"};
		mockQueryResult.add(mockDBRow2);

		Object[] mockDBRow3 = new Object[] {2, 3, "1"};
		mockQueryResult.add(mockDBRow3);

		Object[] mockDBRow4 = new Object[] {3, 3, "1"};
		mockQueryResult.add(mockDBRow4);

		Mockito.when(this.mockQuery.list()).thenReturn(mockQueryResult);

		final Map<Integer, List<PlantDTO>> result = experimentDao.getSampledPlants(1);
		assertThat(result.size(), equalTo(3));
		assertThat(result.get(1).size(), equalTo(2));
		assertThat(result.get(2).size(), equalTo(1));
		assertThat(result.get(3).size(), equalTo(1));
	}

	@Test (expected = MiddlewareQueryException.class)
	public void testGetSampledPlants_ThrowsException () {
		Mockito.when(this.mockSession.createSQLQuery(ExperimentDao.SQL_GET_SAMPLED_PLANTS_BY_STUDY)).thenThrow(MiddlewareQueryException.class);
		experimentDao.getSampledPlants(1);
	}
	
	@Test
	public void testGetEnvironmentsOfGermplasms() {
		this.setupEnvironmentsOfGermplasmMocks();
		final Set<Integer> gids = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
		final Map<Integer, Set<Integer>> environmentsMap = this.experimentDao.getEnvironmentsOfGermplasms(gids, ExperimentDaoTest.PROGRAM_UUID);

		final String expectedSql = "SELECT DISTINCT s.dbxref_id, e.nd_geolocation_id " + "FROM nd_experiment e "
				+ "     INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id "
				+ "     INNER JOIN stock s ON es.stock_id = s.stock_id AND s.dbxref_id IN (:gids) "
				+ "INNER JOIN project p ON p.project_id = e.project_id and p.program_uuid = :programUUID " + " ORDER BY s.dbxref_id ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.mockQuery).setParameterList(Matchers.eq("gids"), Matchers.eq(gids));
		Mockito.verify(this.mockQuery).setParameter(Matchers.eq("programUUID"), Matchers.eq(ExperimentDaoTest.PROGRAM_UUID));

		Assert.assertEquals(gids, environmentsMap.keySet());
		Assert.assertEquals(new LinkedHashSet<>(this.gid1_environments), environmentsMap.get(1));
		Assert.assertEquals(new LinkedHashSet<>(this.gid2_environments), environmentsMap.get(2));
		Assert.assertEquals(new LinkedHashSet<>(this.gid3_environments), environmentsMap.get(3));
	}

	@Test
	public void testGetEnvironmentsOfGermplasmsWithNullProgramUUID() {
		this.setupEnvironmentsOfGermplasmMocks();
		final Set<Integer> gids = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
		final Map<Integer, Set<Integer>> environmentsMap = this.experimentDao.getEnvironmentsOfGermplasms(gids, null);

		final String expectedSql = "SELECT DISTINCT s.dbxref_id, e.nd_geolocation_id " + "FROM nd_experiment e "
				+ "     INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id "
				+ "     INNER JOIN stock s ON es.stock_id = s.stock_id AND s.dbxref_id IN (:gids) " + " ORDER BY s.dbxref_id ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.mockQuery).setParameterList(Matchers.eq("gids"), Matchers.eq(gids));
		Mockito.verify(this.mockQuery, Mockito.never()).setParameter(Matchers.eq("programUUID"),
				Matchers.eq(ExperimentDaoTest.PROGRAM_UUID));

		Assert.assertEquals(gids, environmentsMap.keySet());
		Assert.assertEquals(new LinkedHashSet<>(this.gid1_environments), environmentsMap.get(1));
		Assert.assertEquals(new LinkedHashSet<>(this.gid2_environments), environmentsMap.get(2));
		Assert.assertEquals(new LinkedHashSet<>(this.gid3_environments), environmentsMap.get(3));
	}

	@Test
	public void testGetEnvironmentsOfGermplasmsWithNoGids() {
		this.setupEnvironmentsOfGermplasmMocks();
		final Set<Integer> gids = new HashSet<>();
		final Map<Integer, Set<Integer>> environmentsMap = this.experimentDao.getEnvironmentsOfGermplasms(gids, ExperimentDaoTest.PROGRAM_UUID);

		Mockito.verify(this.mockSession, Mockito.never()).createSQLQuery(Matchers.anyString());
		Assert.assertTrue(environmentsMap.isEmpty());
	}
	
	private void  setupEnvironmentsOfGermplasmMocks() {
		final List<Object[]> mockQueryResult = new ArrayList<Object[]>();
		for (final Integer env : this.gid1_environments) {
			mockQueryResult.add(new Object[] {1, env});
		}
		for (final Integer env : this.gid2_environments) {
			mockQueryResult.add(new Object[] {2, env});
		}
		for (final Integer env : this.gid3_environments) {
			mockQueryResult.add(new Object[] {3, env});
		}
		Mockito.doReturn(mockQueryResult).when(this.mockQuery).list();
	}

}
