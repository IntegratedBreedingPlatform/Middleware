/*******************************************************************************
 *
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import java.math.BigInteger;

import org.generationcp.middleware.domain.oms.TermId;
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

public class StockDaoTest {
	
	@Mock
	private Session session;
	
	@Mock
	private SQLQuery query;

	private StockDao dao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		this.dao = new StockDao();
		this.dao.setSession(this.session);
		Mockito.when(this.session.createSQLQuery(Matchers.anyString())).thenReturn(query);
	}

	@Test
	public void testGetStocks() {
		int projectId = 178;
		this.dao.getStocks(projectId);
		
		final String expectedSql = "SELECT DISTINCT s.* FROM nd_experiment e  " +
			"   INNER JOIN stock s ON e.stock_id = s.stock_id " +
			"   WHERE e.project_id = :projectId ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(query).setParameter("projectId", projectId);
	}
	
	@Test
	public void testCountStocks() {
		Mockito.doReturn(new BigInteger("100")).when(this.query).uniqueResult();
		final int variateId = 5168;
		final int trialEnvironmentId = 2;
		final int datasetId = 1;
		final long count = this.dao.countStocks(datasetId, trialEnvironmentId, variateId);
		
		final String expectedSql = "select count(distinct e.stock_id) "
				+ "from nd_experiment e, phenotype p "
				+ "where e.nd_experiment_id = p.nd_experiment_id  "
				+ "  and e.nd_geolocation_id = " + trialEnvironmentId + "  and p.observable_id = " + variateId
				+ "  and e.project_id = " + datasetId;
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Assert.assertEquals(100L, count);
	}
	
	@Test
	public void testCountStockObservations() {
		Mockito.doReturn(new BigInteger("100")).when(this.query).uniqueResult();
		final int datasetId = 1;
		final String nonEditableFactors = TermId.CROSS.getId() + "," + TermId.STOCKID;
		final long count = this.dao.countStockObservations(datasetId, nonEditableFactors);
		
		final String expectedSql = "SELECT COUNT(sp.stockprop_id) " +
			"FROM nd_experiment e " +
			"INNER JOIN stockProp sp ON sp.stock_id = e.stock_id WHERE e.project_id = " + datasetId +
			" AND sp.type_id NOT IN (" + nonEditableFactors + ")";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Assert.assertEquals(100, count);
	}
	
	@Test
	public void testFindInDataSet() {
		int datasetId = 178;
		this.dao.findInDataSet(datasetId);
		
		final String expectedSql =  "SELECT DISTINCT e.stock_id" + " FROM nd_experiment e "
				+ " WHERE e.project_id = :projectId ORDER BY e.stock_id";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(query).setParameter("projectId", datasetId);
	}
	
	@Test
	public void testGetStudiesByGid() {
		final int gid = 123;
		final int start = 10;
		final int numOfRows = 500;
		this.dao.getStudiesByGid(gid, start, numOfRows);
		final String expectedSql =  "select distinct p.project_id, p.name, p.description, "
				+ "st.study_type_id, st.label, st.name, st.visible, st.cvterm_id, p.program_uuid "
				+ "FROM stock s "
				+ "LEFT JOIN nd_experiment e on e.stock_id = s.stock_id "
				+ "LEFT JOIN project_relationship pr ON pr.subject_project_id = e.project_id "
				+ "LEFT JOIN project p ON pr.object_project_id = p.project_id "
				+ "INNER JOIN study_type st ON p.study_type_id = st.study_type_id "
				+ " WHERE s.dbxref_id = " + gid + " AND p.deleted = 0";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(query).setFirstResult(start);
		Mockito.verify(query).setMaxResults(numOfRows);
	}
	
	@Test
	public void testCountStudiesByGid() {
		Mockito.doReturn(new BigInteger("100")).when(this.query).uniqueResult();
		final int gid = 123;
		final long count = this.dao.countStudiesByGid(gid);
		
		final String expectedSql = "select count(distinct p.project_id) " + "FROM stock s "
				+ "LEFT JOIN nd_experiment e on e.stock_id = s.stock_id "
				+ "LEFT JOIN project_relationship pr ON pr.subject_project_id = e.project_id "
				+ "LEFT JOIN project p ON pr.object_project_id = p.project_id " + "WHERE s.dbxref_id = " + gid
				+ " AND p.deleted = 0";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Assert.assertEquals(100L, count);
	}
}
