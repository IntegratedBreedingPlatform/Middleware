package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class GenericDaoTest {
	
	class TestEntity {		
	}
	
	class TestDao extends GenericDAO<TestEntity, Integer> {		
	}
	
	private TestDao testDao;
	private Session mockSession;
	private Criteria mockCriteria;
	
	@Before
	public void beforeEachTest() {
		testDao = new TestDao();
		mockSession = Mockito.mock(Session.class);
		mockCriteria = Mockito.mock(Criteria.class);
		
		Mockito.when(mockSession.createCriteria(TestEntity.class)).thenReturn(mockCriteria);
		testDao.setSession(mockSession);
	}
	
	/**
	 * If existing max is 1, expect a return value of 2.
	 */
	@Test
	public void testGetNextId1() throws MiddlewareQueryException {
		Mockito.when(mockCriteria.uniqueResult()).thenReturn(1);
		Assert.assertEquals(Integer.valueOf(2), testDao.getNextId("id"));
	}
	
	/**
	 * If there is no existing record, expect a return value of 1.
	 */
	@Test
	public void testGetNextId2() throws MiddlewareQueryException {
		Mockito.when(mockCriteria.uniqueResult()).thenReturn(null);
		Assert.assertEquals(Integer.valueOf(1), testDao.getNextId("id"));
	}

}
