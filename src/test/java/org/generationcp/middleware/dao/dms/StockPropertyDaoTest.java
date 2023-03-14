package org.generationcp.middleware.dao.dms;

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

public class StockPropertyDaoTest {
	
	@Mock
	private Session mockSession;
	
	@Mock
	private SQLQuery mockQuery;
	
	private StockPropertyDao dao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.dao = new StockPropertyDao(this.mockSession);
		Mockito.when(this.mockSession.createSQLQuery(Matchers.anyString())).thenReturn(this.mockQuery);
	}

	@Test
	public void testDeleteStockPropInProjectByTermId() {
		final int projectId = 111;
		final int termId = 222;
		this.dao.deleteStockPropInProjectByTermId(projectId, termId);
		
		Mockito.verify(this.mockSession).flush();
		final String deleteExperimentSql = "DELETE FROM stockprop  WHERE stock_id IN (  SELECT s.stock_id "+
		" FROM stock s " +
		" INNER JOIN nd_experiment nde ON s.stock_id = nde.stock_id "+
		" AND nde.project_id = " + projectId + ") " +
		" AND type_id =" + termId;
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(deleteExperimentSql, sqlCaptor.getValue());
		Mockito.verify(this.mockQuery).executeUpdate();
	}
}
