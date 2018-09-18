package org.generationcp.middleware.dao;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.pojos.ListDataProject;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class PlantDaoTest {
	
	@Mock
	private Session mockSession;
	
	@Mock
	private SQLQuery mockQuery;
	
	private PlantDao plantDao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.plantDao = new PlantDao();
		this.plantDao.setSession(this.mockSession);
		Mockito.when(this.mockSession.createSQLQuery(Matchers.anyString())).thenReturn(this.mockQuery);
		Mockito.when(this.mockQuery.addScalar(Matchers.anyString())).thenReturn(this.mockQuery);
	}
	
	@Test
	public void testGetMaxSequenceNumberQuery() {
		final List<Integer> gids = Arrays.asList(11, 22, 33, 44, 55);
		this.plantDao.getMaxSequenceNumber(gids);
		
		final String sql ="SELECT st.dbxref_id as gid," + " max(IF(           convert("
				+ " SUBSTRING_INDEX(SAMPLE_NAME, ':', -1),               SIGNED) = 0,           0,"
				+ " SUBSTRING_INDEX(SAMPLE_NAME, ':', -1))*1) AS max_sequence_no"
				+ " FROM nd_experiment nde "
				+ " INNER JOIN stock st ON st.stock_id = nde.stock_id "
				+ " INNER JOIN plant pl ON pl.nd_experiment_id = nde.nd_experiment_id"
				+ " INNER JOIN sample s ON s.plant_id = pl.plant_id WHERE st.dbxref_id IN (:gids)"
				+ " GROUP BY st.dbxref_id;";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(mockSession).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(sql, sqlCaptor.getValue());
		Mockito.verify(mockQuery).addScalar(Matchers.eq("gid"), Matchers.refEq(new StringType()));
		Mockito.verify(mockQuery).addScalar(Matchers.eq("max_sequence_no"), Matchers.refEq(new IntegerType()));
		Mockito.verify(mockQuery).setParameterList("gids", gids);
	}

}
