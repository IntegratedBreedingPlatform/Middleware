
package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.PlantDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by clarysabel on 1/26/18.
 */
public class ExperimentDaoTest {

	private static final String PROGRAM_UUID = "abcd-12345";
	private final List<Integer> gid1Environments = Arrays.asList(1001, 1002, 1003, 1004, 1005);
	private final List<Integer> gid2Environments = Arrays.asList(1002, 1003, 1004, 1005);
	private final List<Integer> gid3Environments = Arrays.asList(1003, 1004, 1005);
	private final List<Integer> dummyIds = Arrays.asList(101, 102, 103, 104, 105);

	private ExperimentDao experimentDao;
	private Session mockSession;
	private SQLQuery mockQuery;
	private Criteria mockCriteria;

	@Before
	public void beforeEachTest() {
		this.mockSession = Mockito.mock(Session.class);

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(this.mockSession);

		this.mockQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(this.mockSession.createSQLQuery(Matchers.anyString())).thenReturn(this.mockQuery);

		this.mockCriteria = Mockito.mock(Criteria.class);
		Mockito.when(this.mockSession.createCriteria(ExperimentModel.class)).thenReturn(this.mockCriteria);
		Mockito.when(this.mockCriteria.list()).thenReturn(dummyIds);
	}

	@Test
	public void testGetSampledPlants_Ok() {
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

	@Test(expected = MiddlewareQueryException.class)
	public void testGetSampledPlants_ThrowsException() {
		Mockito.when(this.mockSession.createSQLQuery(ExperimentDao.SQL_GET_SAMPLED_PLANTS_BY_STUDY))
				.thenThrow(MiddlewareQueryException.class);
		experimentDao.getSampledPlants(1);
	}

	@Test
	public void testGetEnvironmentsOfGermplasms() {
		this.setupEnvironmentsOfGermplasmMocks();
		final Set<Integer> gids = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
		final Map<Integer, Set<Integer>> environmentsMap =
				this.experimentDao.getEnvironmentsOfGermplasms(gids, ExperimentDaoTest.PROGRAM_UUID);

		final String expectedSql = "SELECT DISTINCT s.dbxref_id, e.nd_geolocation_id " + "FROM nd_experiment e "
				+ "     INNER JOIN stock s ON e.stock_id = s.stock_id AND s.dbxref_id IN (:gids) "
				+ "INNER JOIN project p ON p.project_id = e.project_id and p.program_uuid = :programUUID " + " ORDER BY s.dbxref_id ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.mockQuery).setParameterList(Matchers.eq("gids"), Matchers.eq(gids));
		Mockito.verify(this.mockQuery).setParameter(Matchers.eq("programUUID"), Matchers.eq(ExperimentDaoTest.PROGRAM_UUID));

		Assert.assertEquals(gids, environmentsMap.keySet());
		Assert.assertEquals(new LinkedHashSet<>(this.gid1Environments), environmentsMap.get(1));
		Assert.assertEquals(new LinkedHashSet<>(this.gid2Environments), environmentsMap.get(2));
		Assert.assertEquals(new LinkedHashSet<>(this.gid3Environments), environmentsMap.get(3));
	}

	@Test
	public void testGetEnvironmentsOfGermplasmsWithNullProgramUUID() {
		this.setupEnvironmentsOfGermplasmMocks();
		final Set<Integer> gids = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
		final Map<Integer, Set<Integer>> environmentsMap = this.experimentDao.getEnvironmentsOfGermplasms(gids, null);

		final String expectedSql = "SELECT DISTINCT s.dbxref_id, e.nd_geolocation_id " + "FROM nd_experiment e "
				+ "     INNER JOIN stock s ON e.stock_id = s.stock_id AND s.dbxref_id IN (:gids) " + " ORDER BY s.dbxref_id ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.mockQuery).setParameterList(Matchers.eq("gids"), Matchers.eq(gids));
		Mockito.verify(this.mockQuery, Mockito.never()).setParameter(Matchers.eq("programUUID"),
				Matchers.eq(ExperimentDaoTest.PROGRAM_UUID));

		Assert.assertEquals(gids, environmentsMap.keySet());
		Assert.assertEquals(new LinkedHashSet<>(this.gid1Environments), environmentsMap.get(1));
		Assert.assertEquals(new LinkedHashSet<>(this.gid2Environments), environmentsMap.get(2));
		Assert.assertEquals(new LinkedHashSet<>(this.gid3Environments), environmentsMap.get(3));
	}

	private void setupEnvironmentsOfGermplasmMocks() {
		Mockito.when(this.mockSession.createSQLQuery(Matchers.anyString())).thenReturn(this.mockQuery);

		final List<Object[]> mockQueryResult = new ArrayList<Object[]>();
		for (final Integer env : this.gid1Environments) {
			mockQueryResult.add(new Object[] {1, env});
		}
		for (final Integer env : this.gid2Environments) {
			mockQueryResult.add(new Object[] {2, env});
		}
		for (final Integer env : this.gid3Environments) {
			mockQueryResult.add(new Object[] {3, env});
		}
		Mockito.doReturn(mockQueryResult).when(this.mockQuery).list();
	}

	@Test
	public void testGetEnvironmentsOfGermplasmsWithNoGids() {
		final Set<Integer> gids = new HashSet<>();
		final Map<Integer, Set<Integer>> environmentsMap =
				this.experimentDao.getEnvironmentsOfGermplasms(gids, ExperimentDaoTest.PROGRAM_UUID);

		Mockito.verify(this.mockSession, Mockito.never()).createSQLQuery(Matchers.anyString());
		Assert.assertTrue(environmentsMap.isEmpty());
	}

	@Test
	public void testCountStocksByDatasetId() {
		Mockito.doReturn(new BigInteger("20")).when(this.mockQuery).uniqueResult();
		final int id = 1234;
		final long count = this.experimentDao.countStocksByDatasetId(id);

		final String expectedSql = "SELECT COUNT(DISTINCT e.stock_id) FROM nd_experiment e  " + "WHERE e.project_id = :datasetId";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.mockQuery).setParameter(Matchers.eq("datasetId"), Matchers.eq(id));
		Assert.assertEquals(20L, count);
	}

	@Test
	public void testGetExperimentIdsByStockIds() {
		final Collection<Integer> stockIds = Arrays.asList(11, 22, 33);
		final List<Integer> returnedIds = this.experimentDao.getExperimentIdsByStockIds(stockIds);
		Mockito.verify(this.mockCriteria).add(Matchers.refEq(Restrictions.in("stock.stockId", stockIds)));
		Mockito.verify(this.mockCriteria).setProjection(Matchers.refEq(Projections.property("ndExperimentId")));
		Assert.assertEquals(dummyIds, returnedIds);
	}

	@Test
	public void testGetExperimentIdByLocationIdStockId() {
		final int expectedValue = 1111;
		Mockito.doReturn(expectedValue).when(this.mockQuery).uniqueResult();
		final int projectId = 2022;
		final int locationId = 3033;
		final int stockId = 4044;
		final int experimentId = this.experimentDao.getExperimentIdByLocationIdStockId(projectId, locationId, stockId);
		final String expectedSql = "SELECT exp.nd_experiment_id " + "FROM nd_experiment exp " + " WHERE exp.project_id = " + projectId
				+ " AND exp.nd_geolocation_id = " + locationId + " AND exp.type_id = 1170 " + " AND exp.stock_id = " + stockId;
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Assert.assertEquals(expectedValue, experimentId);
	}

	@Test
	public void testDeleteExperimentsByIds() {
		final List<Integer> experimentIds = Arrays.asList(11, 22, 33);
		this.experimentDao.deleteExperimentsByIds(experimentIds);

		Mockito.verify(this.mockSession).flush();
		final String deletePhenotypeSql =
				"DELETE pheno FROM nd_experiment e" + "  LEFT JOIN phenotype pheno ON pheno.nd_experiment_id = e.nd_experiment_id"
						+ "  where e.nd_experiment_id in (" + StringUtils.join(experimentIds, ",") + ") ";
		final String deleteExperimentSql = "delete e, eprop " + "from nd_experiment e "
				+ "left join nd_experimentprop eprop on eprop.nd_experiment_id = e.nd_experiment_id " + "where e.nd_experiment_id in ("
				+ StringUtils.join(experimentIds, ",") + ") ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession, Mockito.times(2)).createSQLQuery(sqlCaptor.capture());
		final List<String> queries = sqlCaptor.getAllValues();
		Assert.assertEquals(deletePhenotypeSql, queries.get(0));
		Assert.assertEquals(deleteExperimentSql, queries.get(1));
		Mockito.verify(this.mockQuery, Mockito.times(2)).executeUpdate();
	}

	@Test
	public void testDeleteExperimentsByStudy() {
		final int studyId = 1234;
		this.experimentDao.deleteExperimentsByStudy(studyId);

		Mockito.verify(this.mockSession).flush();
		final String deletePhenotypeSql = "DELETE pheno FROM nd_experiment e"
				+ "  LEFT JOIN phenotype pheno ON pheno.nd_experiment_id = e.nd_experiment_id" + "  WHERE e.project_id = :datasetId ";
		final String deleteExperimentSql = "DELETE e, eprop " + "FROM nd_experiment e "
				+ "LEFT JOIN nd_experimentprop eprop ON eprop.nd_experiment_id = e.nd_experiment_id " + "WHERE e.project_id = :datasetId ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession, Mockito.times(2)).createSQLQuery(sqlCaptor.capture());
		final List<String> queries = sqlCaptor.getAllValues();
		Assert.assertEquals(deletePhenotypeSql, queries.get(0));
		Assert.assertEquals(deleteExperimentSql, queries.get(1));
		Mockito.verify(this.mockQuery, Mockito.times(2)).setParameter("datasetId", studyId);
		Mockito.verify(this.mockQuery, Mockito.times(2)).executeUpdate();
	}

	@Test
	public void testDeleteTrialExperimentsOfStudy() {
		final int studyId = 1234;
		this.experimentDao.deleteTrialExperimentsOfStudy(studyId);

		Mockito.verify(this.mockSession).flush();
		final String deletePhenotypeSql = "DELETE pheno FROM nd_experiment e"
				+ "  LEFT JOIN phenotype pheno ON pheno.nd_experiment_id = e.nd_experiment_id" + "  WHERE e.project_id = :datasetId ";
		final String deleteExperimentSql = "DELETE g, gp, e, eprop " + "FROM nd_geolocation g "
				+ "LEFT JOIN nd_geolocationprop gp on g.nd_geolocation_id = gp.nd_geolocation_id "
				+ "LEFT join nd_experiment e on g.nd_geolocation_id = e.nd_geolocation_id "
				+ "LEFT JOIN nd_experimentprop eprop ON eprop.nd_experiment_id = e.nd_experiment_id " + "WHERE e.project_id = :datasetId ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession, Mockito.times(2)).createSQLQuery(sqlCaptor.capture());
		final List<String> queries = sqlCaptor.getAllValues();
		Assert.assertEquals(deletePhenotypeSql, queries.get(0));
		Assert.assertEquals(deleteExperimentSql, queries.get(1));
		Mockito.verify(this.mockQuery, Mockito.times(2)).setParameter("datasetId", studyId);
		Mockito.verify(this.mockQuery, Mockito.times(2)).executeUpdate();
	}

	@Test
	public void testGetExperiments_FirstInstance() {
		final Query query = Mockito.mock(Query.class);
		Mockito.when(this.mockSession.createQuery(Matchers.anyString())).thenReturn(query);
		final int projectId = 1011;
		final int start = 1000;
		final int numOfRows = 5000;
		this.experimentDao.getExperiments(projectId, Arrays.asList(TermId.PLOT_EXPERIMENT, TermId.SAMPLE_EXPERIMENT), start, numOfRows,
				true);

		final String sql = "select distinct exp from ExperimentModel as exp "
				+ "left outer join exp.properties as plot with plot.typeId IN (8200,8380) "
				+ "left outer join exp.properties as rep with rep.typeId = 8210 " + "left outer join exp.stock as st "
				+ "where exp.project.projectId =:p_id and exp.typeId in (:type_ids) " + "and exp.geoLocation.description = 1 "
				+ "order by (exp.geoLocation.description * 1) ASC, " + "(plot.value * 1) ASC, " + "(rep.value * 1) ASC, "
				+ "(st.uniqueName * 1) ASC, " + "exp.ndExperimentId ASC";
		Mockito.verify(this.mockSession).createQuery(Matchers.eq(sql));
		Mockito.verify(query).setParameter("p_id", projectId);
		Mockito.verify(query).setParameterList("type_ids",
				Arrays.asList(TermId.PLOT_EXPERIMENT.getId(), TermId.SAMPLE_EXPERIMENT.getId()));
		Mockito.verify(query).setMaxResults(numOfRows);
		Mockito.verify(query).setFirstResult(start);
	}
	
	@Test
	public void testGetExperiments_NotFirstInstance() {
		final Query query = Mockito.mock(Query.class);
		Mockito.when(this.mockSession.createQuery(Matchers.anyString())).thenReturn(query);
		final int projectId = 1011;
		final int start = 1000;
		final int numOfRows = 5000;
		this.experimentDao.getExperiments(projectId, Arrays.asList(TermId.PLOT_EXPERIMENT, TermId.SAMPLE_EXPERIMENT), start, numOfRows,
				false);

		final String sql = "select distinct exp from ExperimentModel as exp "
				+ "left outer join exp.properties as plot with plot.typeId IN (8200,8380) "
				+ "left outer join exp.properties as rep with rep.typeId = 8210 " + "left outer join exp.stock as st "
				+ "where exp.project.projectId =:p_id and exp.typeId in (:type_ids) "
				+ "order by (exp.geoLocation.description * 1) ASC, " + "(plot.value * 1) ASC, " + "(rep.value * 1) ASC, "
				+ "(st.uniqueName * 1) ASC, " + "exp.ndExperimentId ASC";
		Mockito.verify(this.mockSession).createQuery(Matchers.eq(sql));
		Mockito.verify(query).setParameter("p_id", projectId);
		Mockito.verify(query).setParameterList("type_ids",
				Arrays.asList(TermId.PLOT_EXPERIMENT.getId(), TermId.SAMPLE_EXPERIMENT.getId()));
		Mockito.verify(query).setMaxResults(numOfRows);
		Mockito.verify(query).setFirstResult(start);
	}

	@Test
	public void testIsInstanceExistsInDataset() {

		final Random ran = new Random();
		final int datasetId = ran.nextInt();
		final int instanceId = ran.nextInt();

		final SQLQuery query = Mockito.mock(SQLQuery.class);
		Mockito.when(this.mockSession.createSQLQuery(Matchers.anyString())).thenReturn(query);
		Mockito.when(query.uniqueResult()).thenReturn(BigInteger.valueOf(1));
		Assert.assertTrue(this.experimentDao.isInstanceExistsInDataset(datasetId, instanceId));

		Mockito.verify(this.mockSession).createSQLQuery(
			"SELECT COUNT(e.nd_experiment_id) FROM nd_experiment e  WHERE e.project_id = :datasetId and e.nd_geolocation_id = :instanceId");
		Mockito.verify(query).setParameter("datasetId", datasetId);
		Mockito.verify(query).setParameter("instanceId", instanceId);
	}

}
