package org.generationcp.middleware.dao.oms;

import java.util.Arrays;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class CVTermRelationshipDaoTest {
	private CVTermRelationshipDao dao;
	private Session session;

	@Before
	public void setUp() throws Exception {
		this.session = Mockito.mock(Session.class);
		this.dao = new CVTermRelationshipDao();
		this.dao.setSession(this.session);
	}
	
	@Test
	public void testGetCategoriesReferredInPhenotype() {
		final List<String> categories = Arrays.asList("1", "2", "3", "4", "5");
		final SQLQuery mockQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(this.session.createSQLQuery(Matchers.anyString())).thenReturn(mockQuery);
		Mockito.when(mockQuery.list()).thenReturn(categories);
		final List<String> result = this.dao.getCategoriesReferredInPhenotype(1001);
		Assert.assertNotNull(result);
		Assert.assertEquals(categories.size(), result.size());
		for(String category: categories){
			Assert.assertTrue(result.contains(category));
		}
	}
}
