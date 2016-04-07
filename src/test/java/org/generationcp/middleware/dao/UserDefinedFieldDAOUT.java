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

/**
 *
 * HQL-JPA Queries are Strings, so once the query was tested to bring the proper result. I want to make sure that the queryString
 * remains unaltered and if by mistake the query is altered then  the test will fail and show that alteration.
 *
 */
public class UserDefinedFieldDAOUT {

	public static final String DUMMY_TYPE = "DUMMY_TYPE";
	public static final String DUMMY_TABLE = "DUMMY_TABLE";
	public static final String CODE1 = "code1";
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
		List<String> excludedNames = newArrayList(CODE1);

		ArgumentCaptor<String> queryStringCaptor = ArgumentCaptor.forClass(String.class);
		Query queryMock = mock(Query.class);
		String expectedQuery =
				"select udf from UserDefinedField udf where udf.ftable=:table and udf.ftype=:ftype and udf.fcode not in (:excludedCodedNames)";
		when(sessionMock.createQuery(queryStringCaptor.capture())).thenReturn(queryMock);

		userDefinedFieldDAO.getByTableAndTypeWithoutList(DUMMY_TABLE, DUMMY_TYPE, excludedNames);

		String usedQuery = queryStringCaptor.getValue();

		assertTrue(usedQuery.equalsIgnoreCase(expectedQuery));

	}


	@Test
	public void getByTableAndTypeWithoutListQueryDoesNotContainListWhenListIsEmpty() throws Exception {

		List<String> excludedNames = newArrayList();

		ArgumentCaptor<String> queryStringCaptor = ArgumentCaptor.forClass(String.class);
		Query queryMock = mock(Query.class);
		String expectedQuery =
				"select udf from UserDefinedField udf where udf.ftable=:table and udf.ftype=:ftype";
		when(sessionMock.createQuery(queryStringCaptor.capture())).thenReturn(queryMock);


		userDefinedFieldDAO.getByTableAndTypeWithoutList(DUMMY_TABLE, DUMMY_TYPE, excludedNames);

		String usedQuery = queryStringCaptor.getValue();

		assertTrue(usedQuery.equalsIgnoreCase(expectedQuery));

	}
}
