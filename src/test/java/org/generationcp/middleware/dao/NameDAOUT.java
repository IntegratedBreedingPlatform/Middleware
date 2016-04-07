package org.generationcp.middleware.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NameDAOUT {

	public static final String DUMMY_NAME = "DUMMY_NAME";
	public static final String CODE1 = "code1";
	NameDAO nameDAO;

	@Mock
	Session sessionMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		nameDAO = new NameDAO();
		nameDAO.setSession(sessionMock);
	}

	@Test
	public void getNamesByNvalInTypeListWhenTypeListIsEmpty() throws Exception {

		List<String> typeIds = Lists.newArrayList();

		ArgumentCaptor<String> queryStringCaptor = ArgumentCaptor.forClass(String.class);
		Query queryMock = mock(Query.class);
		String expectedQuery = "select names from Name names where names.nval = :name";

		when(sessionMock.createQuery(queryStringCaptor.capture())).thenReturn(queryMock);

		nameDAO.getNamesByNvalInTypeList(DUMMY_NAME, typeIds);

		String usedQuery = queryStringCaptor.getValue();

		assertTrue(usedQuery.equalsIgnoreCase(expectedQuery));

	}


	@Test
	public void getByTableAndTypeListWhenListContainsCodedNames() throws Exception {
		List<String> typeIds = Lists.newArrayList(CODE1);

		ArgumentCaptor<String> queryStringCaptor = ArgumentCaptor.forClass(String.class);
		Query queryMock = mock(Query.class);

		String expectedQuery = "select name from Name name, UserDefinedField udfld  where name.nval = :name and name.typeId = udfld.fldno "
				+ " and udfld.fcode in (:fcodeList)";

		when(sessionMock.createQuery(queryStringCaptor.capture())).thenReturn(queryMock);

		nameDAO.getNamesByNvalInTypeList(DUMMY_NAME, typeIds);

		String usedQuery = queryStringCaptor.getValue();

		assertTrue(usedQuery.equalsIgnoreCase(expectedQuery));

	}
}
