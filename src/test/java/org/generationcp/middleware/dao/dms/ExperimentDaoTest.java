package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.domain.sample.PlantDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by clarysabel on 1/26/18.
 */
public class ExperimentDaoTest {

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

}
