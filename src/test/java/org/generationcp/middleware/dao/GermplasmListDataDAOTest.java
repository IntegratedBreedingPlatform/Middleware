
package org.generationcp.middleware.dao;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class GermplasmListDataDAOTest {

	private GermplasmListDataDAO germplasmListDataDAO;
	private Session mockHibernateSession;


	public static final String DUMMY_STRING = "DUMMY STRING";
	public static final Integer TEST_VALUE = 1;

	@Before
	public void beforeTest() {
		this.germplasmListDataDAO = new GermplasmListDataDAO();
		this.mockHibernateSession = Mockito.mock(Session.class);
		this.germplasmListDataDAO.setSession(this.mockHibernateSession);
	}

	@Test
	public void testCountByListId() throws Exception {

		final SQLQuery mockSqlQuery = Mockito.mock(SQLQuery.class);
		when(this.mockHibernateSession.createSQLQuery(anyString())).thenReturn(mockSqlQuery);
		final BigInteger exampleNumberOfListDataEntries = new BigInteger("50");
		when(mockSqlQuery.uniqueResult()).thenReturn(exampleNumberOfListDataEntries);

		final int listId = 1;

		this.germplasmListDataDAO.countByListId(listId);

		verify(mockSqlQuery).setParameter(GermplasmListDataDAO.GERMPLASM_LIST_DATA_LIST_ID_COLUMN, listId);

	}

	@Test
	public void testDeleteByListId() throws Exception {


		final Query mockQuery = Mockito.mock(Query.class);
		when(this.mockHibernateSession.getNamedQuery(GermplasmListData.DELETE_BY_LIST_ID)).thenReturn(mockQuery);
		final int listId = 1;
		this.germplasmListDataDAO.deleteByListId(listId);
		verify(mockQuery).setInteger(GermplasmListDataDAO.GERMPLASM_LIST_DATA_LIST_ID_COLUMN, listId);
		verify(mockQuery).executeUpdate();
	}

	@Test
	public void testGetByIds() throws Exception {

		final int listId = 1;
		final Criteria mockCriteria = Mockito.mock(Criteria.class);
		when(this.mockHibernateSession.createCriteria(GermplasmListData.class)).thenReturn(mockCriteria);

		this.germplasmListDataDAO.getByIds(Collections.singletonList(listId));

		verify(mockCriteria).createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);

		// Simple and Property Expression
		verify(mockCriteria, times(3)).add(Matchers.any(Criterion.class));
		verify(mockCriteria).addOrder(Matchers.any(Order.class));
		verify(mockCriteria).list();

	}

	/**
	 * Basic test to make sure that
	 */
	@Test
	public void testGetByListId() {
		final int listId = 1;
		final Criteria mockCriteria = Mockito.mock(Criteria.class);
		when(this.mockHibernateSession.createCriteria(GermplasmListData.class)).thenReturn(mockCriteria);

		this.germplasmListDataDAO.getByListId(listId);

		verify(mockCriteria).createAlias(GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE,
				GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS);
		verify(mockCriteria).createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);

		// Simple and Property Expression
		verify(mockCriteria, times(3)).add(Matchers.any(Criterion.class));
		verify(mockCriteria).addOrder(Matchers.any(Order.class));
		verify(mockCriteria).list();

	}

	@Test
	public void testGetByListIdAndEntryId() throws Exception {
		final int listId = 1;
		final int entryId = 100;

		final Criteria mockCriteria = Mockito.mock(Criteria.class);
		when(this.mockHibernateSession.createCriteria(GermplasmListData.class)).thenReturn(mockCriteria);

		this.germplasmListDataDAO.getByListIdAndEntryId(listId, entryId);

		verify(mockCriteria).createAlias(GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE,
				GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS);
		verify(mockCriteria).createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);

		// Simple and Property Expression
		verify(mockCriteria, times(4)).add(Matchers.any(Criterion.class));
		verify(mockCriteria).addOrder(Matchers.any(Order.class));
		verify(mockCriteria).uniqueResult();
	}

	@Test
	public void testGetByListIdAndLrecId() throws Exception {
		final int listId = 1;
		final int lrecId = 100;

		final Criteria mockCriteria = Mockito.mock(Criteria.class);
		when(this.mockHibernateSession.createCriteria(GermplasmListData.class)).thenReturn(mockCriteria);

		this.germplasmListDataDAO.getByListIdAndLrecId(listId, lrecId);

		verify(mockCriteria).createAlias(GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE,
				GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS);
		verify(mockCriteria).createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);

		// Simple and Property Expression
		verify(mockCriteria, times(4)).add(Matchers.any(Criterion.class));
		verify(mockCriteria).addOrder(Matchers.any(Order.class));
		verify(mockCriteria).uniqueResult();
	}

	@Test
	public void testGetListDataWithParents(){
		final SQLQuery mockSqlQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(this.mockHibernateSession.createSQLQuery(anyString())).thenReturn(mockSqlQuery);

		List<Object[]> list = new ArrayList<>();
		Object [] objects = {TEST_VALUE,TEST_VALUE,DUMMY_STRING,DUMMY_STRING,DUMMY_STRING,
				TEST_VALUE,DUMMY_STRING,TEST_VALUE,TEST_VALUE,DUMMY_STRING,DUMMY_STRING};

		list.add(objects);

		Mockito.when(mockSqlQuery.list()).thenReturn(list);
		final Integer listId = 1;

		List<GermplasmListData> result = this.germplasmListDataDAO.getListDataWithParents(listId);

		for(GermplasmListData g : result){

			Assert.assertEquals(GermplasmListDataDAOTest.TEST_VALUE,g.getGid());
			Assert.assertEquals(GermplasmListDataDAOTest.TEST_VALUE,g.getEntryId());
			Assert.assertEquals(GermplasmListDataDAOTest.DUMMY_STRING,g.getDesignation());
			Assert.assertEquals(GermplasmListDataDAOTest.DUMMY_STRING,g.getGroupName());
			Assert.assertEquals(GermplasmListDataDAOTest.DUMMY_STRING,g.getFemaleParent());
			Assert.assertEquals(GermplasmListDataDAOTest.TEST_VALUE,g.getFgid());
			Assert.assertEquals(GermplasmListDataDAOTest.DUMMY_STRING,g.getMaleParent());
			Assert.assertEquals(GermplasmListDataDAOTest.TEST_VALUE,g.getMgid());
			Assert.assertEquals(GermplasmListDataDAOTest.TEST_VALUE,g.getGid());
			Assert.assertEquals(GermplasmListDataDAOTest.DUMMY_STRING,g.getSeedSource());
			Assert.assertEquals(GermplasmListDataDAOTest.DUMMY_STRING,g.getBreedingMethodName());

		}

	}

}
