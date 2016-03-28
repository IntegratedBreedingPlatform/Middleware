package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.pojos.UserDefinedField;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDefinedFieldDAOUT {

	public static final String DUMMY_TYPE = "DUMMY_TYPE";
	public static final String DUMMY_TABLE = "DUMMY_TABLE";
	UserDefinedFieldDAO userDefinedFieldDAO;
	@Mock
	Session sessionMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		userDefinedFieldDAO = new UserDefinedFieldDAO();
		userDefinedFieldDAO.setSession(sessionMock);
	}

	@Test
	public void getByTableAndTypeWithoutListQueryIsCompleteWhenListIsNotEmpty() throws Exception {
		int item1 = 1;
		List<Integer> excludedIds = newArrayList(item1);

		ArgumentCaptor<String> queryStringCaptor = ArgumentCaptor.forClass(String.class);
		Query queryMock = mock(Query.class);
		String expectedQuery =
				"select udf from UserDefinedField udf where udf.ftable=:table and udf.ftype=:ftype and udf.fldno not in (:excludedIds)";
		when(sessionMock.createQuery(queryStringCaptor.capture())).thenReturn(queryMock);

		userDefinedFieldDAO.getByTableAndTypeWithoutList(DUMMY_TABLE, DUMMY_TYPE, excludedIds);

		String usedQuery = queryStringCaptor.getValue();

		assertTrue(usedQuery.equalsIgnoreCase(expectedQuery));

	}


	@Test
	public void getByTableAndTypeWithoutListQueryDoesNotContainListWhenListIsEmpty() throws Exception {

		List<Integer> excludedIds = newArrayList();

		ArgumentCaptor<String> queryStringCaptor = ArgumentCaptor.forClass(String.class);
		Query queryMock = mock(Query.class);
		String expectedQuery =
				"select udf from UserDefinedField udf where udf.ftable=:table and udf.ftype=:ftype";
		when(sessionMock.createQuery(queryStringCaptor.capture())).thenReturn(queryMock);


		userDefinedFieldDAO.getByTableAndTypeWithoutList(DUMMY_TABLE, DUMMY_TYPE, excludedIds);

		String usedQuery = queryStringCaptor.getValue();

		assertTrue(usedQuery.equalsIgnoreCase(expectedQuery));

	}
}
