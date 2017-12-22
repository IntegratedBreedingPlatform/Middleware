
package org.generationcp.middleware.dao.dms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ExperimentStockDaoTest {

	private static final String PROGRAM_UUID = "abcd-12345";

	private ExperimentStockDao dao;

	@Mock
	private Session mockSession;

	@Mock
	private SQLQuery mockQuery;

	private final List<Integer> gid1_environments = Arrays.asList(1001, 1002, 1003, 1004, 1005);
	private final List<Integer> gid2_environments = Arrays.asList(1002, 1003, 1004, 1005);
	private final List<Integer> gid3_environments = Arrays.asList(1003, 1004, 1005);

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.dao = new ExperimentStockDao();
		this.dao.setSession(this.mockSession);
		Mockito.doReturn(this.mockQuery).when(this.mockSession).createSQLQuery(Matchers.anyString());

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

	@Test
	public void testGetEnvironmentsOfGermplasms() {
		final Set<Integer> gids = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
		final Map<Integer, Set<Integer>> environmentsMap = this.dao.getEnvironmentsOfGermplasms(gids, ExperimentStockDaoTest.PROGRAM_UUID);

		final String expectedSql = "SELECT DISTINCT s.dbxref_id, e.nd_geolocation_id " + "FROM nd_experiment e "
				+ "     INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id "
				+ "     INNER JOIN stock s ON es.stock_id = s.stock_id AND s.dbxref_id IN (:gids) "
				+ "INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id "
				+ "INNER JOIN project p ON p.project_id = ep.project_id and p.program_uuid = :programUUID " + " ORDER BY s.dbxref_id ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.mockQuery).setParameterList(Matchers.eq("gids"), Matchers.eq(gids));
		Mockito.verify(this.mockQuery).setParameter(Matchers.eq("programUUID"), Matchers.eq(ExperimentStockDaoTest.PROGRAM_UUID));

		Assert.assertEquals(gids, environmentsMap.keySet());
		Assert.assertEquals(new LinkedHashSet<>(this.gid1_environments), environmentsMap.get(1));
		Assert.assertEquals(new LinkedHashSet<>(this.gid2_environments), environmentsMap.get(2));
		Assert.assertEquals(new LinkedHashSet<>(this.gid3_environments), environmentsMap.get(3));
	}

	@Test
	public void testGetEnvironmentsOfGermplasmsWithNullProgramUUID() {
		final Set<Integer> gids = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
		final Map<Integer, Set<Integer>> environmentsMap = this.dao.getEnvironmentsOfGermplasms(gids, null);

		final String expectedSql = "SELECT DISTINCT s.dbxref_id, e.nd_geolocation_id " + "FROM nd_experiment e "
				+ "     INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id "
				+ "     INNER JOIN stock s ON es.stock_id = s.stock_id AND s.dbxref_id IN (:gids) " + " ORDER BY s.dbxref_id ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.mockQuery).setParameterList(Matchers.eq("gids"), Matchers.eq(gids));
		Mockito.verify(this.mockQuery, Mockito.never()).setParameter(Matchers.eq("programUUID"),
				Matchers.eq(ExperimentStockDaoTest.PROGRAM_UUID));

		Assert.assertEquals(gids, environmentsMap.keySet());
		Assert.assertEquals(new LinkedHashSet<>(this.gid1_environments), environmentsMap.get(1));
		Assert.assertEquals(new LinkedHashSet<>(this.gid2_environments), environmentsMap.get(2));
		Assert.assertEquals(new LinkedHashSet<>(this.gid3_environments), environmentsMap.get(3));
	}

	@Test
	public void testGetEnvironmentsOfGermplasmsWithNoGids() {
		final Set<Integer> gids = new HashSet<>();
		final Map<Integer, Set<Integer>> environmentsMap = this.dao.getEnvironmentsOfGermplasms(gids, ExperimentStockDaoTest.PROGRAM_UUID);

		Mockito.verify(this.mockSession, Mockito.never()).createSQLQuery(Matchers.anyString());
		Assert.assertTrue(environmentsMap.isEmpty());
	}

}
