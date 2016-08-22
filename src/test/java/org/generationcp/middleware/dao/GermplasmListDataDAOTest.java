
package org.generationcp.middleware.dao;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class GermplasmListDataDAOTest extends IntegrationTestBase {
	
	private static final Integer TEST_METHOD_ID = 101;
	private static final String TEST_METHOD_NAME = "Single cross";

	private GermplasmListDataDAO germplasmListDataDAO;
	private Session mockHibernateSession;
	private Session realHibernateSession;
	private GermplasmDAO germplasmDAO;
	private NameDAO nameDAO;
	private GermplasmListDAO germplasmListDAO;

	@Before
	public void beforeTest() {
		this.germplasmListDataDAO = new GermplasmListDataDAO();
		this.germplasmDAO = new GermplasmDAO();
		this.nameDAO = new NameDAO();
		this.germplasmListDAO = new GermplasmListDAO();
		this.mockHibernateSession = Mockito.mock(Session.class);
		this.realHibernateSession = this.sessionProvder.getSession();
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
	public void testGetListDataWithParents() {
		this.setSessionOfDaosToRealHibernateSession();
		
		final Germplasm parentGermplasm = this.createTestParentGermplasmWithPreferredAndNonpreferredNames();
		final Germplasm childGermplasm = this.createTestChildGermplasm(parentGermplasm);
		final GermplasmListData listData = this.createTestListData(childGermplasm);
		final List<GermplasmListData> listDataList = this.germplasmListDataDAO.getListDataWithParents(listData.getList().getId());
		Assert.assertEquals("There should be only 1 list data under the list with id" + listData.getList().getId(), 1, listDataList.size());
		
		for (final GermplasmListData currentGermplasmListData : listDataList) {
			Assert.assertEquals("List data id should be " + listData.getId(), listData.getId(),
					currentGermplasmListData.getId());
			Assert.assertEquals("Entry id should be " + listData.getEntryId(), listData.getEntryId(),
					currentGermplasmListData.getEntryId());
			Assert.assertEquals("Desig should be " + listData.getDesignation(), listData.getDesignation(),
					currentGermplasmListData.getDesignation());
			Assert.assertEquals("Group name should be " + listData.getGroupName(), listData.getGroupName(),
					currentGermplasmListData.getGroupName());
			Assert.assertEquals("Gid should be " + listData.getGid(), listData.getGid(),
					currentGermplasmListData.getGid());
			Assert.assertEquals("Seed source should be " + listData.getSeedSource(), listData.getSeedSource(),
					currentGermplasmListData.getSeedSource());
			Assert.assertEquals("Breeding method name should be " + TEST_METHOD_NAME,
					TEST_METHOD_NAME, currentGermplasmListData.getBreedingMethodName());

			// Check parent germplasm values
			Assert.assertEquals("Female Parent GID should be " + listData.getFgid(), listData.getFgid(),
					currentGermplasmListData.getFgid());
			Assert.assertEquals("Female Parent GID should be " + listData.getMgid(), listData.getMgid(),
					currentGermplasmListData.getMgid());
			Assert.assertEquals("Female Parent designation should be " + parentGermplasm.getPreferredName().getNval(),
					parentGermplasm.getPreferredName().getNval(), currentGermplasmListData.getFemaleParent());
			Assert.assertEquals("Male Parent designation should be " + parentGermplasm.getPreferredName().getNval(),
					parentGermplasm.getPreferredName().getNval(), currentGermplasmListData.getMaleParent());
		}
	}
	
	private void setSessionOfDaosToRealHibernateSession() {
		this.germplasmListDataDAO.setSession(this.realHibernateSession);
		this.germplasmDAO.setSession(this.realHibernateSession);
		this.nameDAO.setSession(this.realHibernateSession);
		this.germplasmListDAO.setSession(this.realHibernateSession);
	}

	private GermplasmListData createTestListData(final Germplasm childGermplasm) {
		final GermplasmList germplasmList = this.createTestList();
		final GermplasmListData listData =
				new GermplasmListDataTestDataInitializer().createGermplasmListData(germplasmList, childGermplasm.getGid(), 1);
		listData.setFgid(childGermplasm.getGpid1());
		listData.setMgid(childGermplasm.getGpid2());
		this.germplasmListDataDAO.save(listData);
		return listData;
	}
	
	private GermplasmList createTestList() {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(null, false);
		this.germplasmListDAO.save(germplasmList);
		return germplasmList;
	}
	
	private Germplasm createTestParentGermplasmWithPreferredAndNonpreferredNames() {
		final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName();
		this.germplasmDAO.save(germplasm);
		final Name preferredName = germplasm.getPreferredName();
		preferredName.setGermplasmId(germplasm.getGid());
		final Name otherName = GermplasmTestDataInitializer.createGermplasmName(germplasm.getGid(), "Other Name ");
		otherName.setNstat(0);
		this.nameDAO.save(preferredName);
		this.nameDAO.save(otherName);
		return germplasm;
	}

	private Germplasm createTestChildGermplasm(final Germplasm parentGermplasm) {
		final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName();
		germplasm.setGpid1(parentGermplasm.getGid());
		germplasm.setGpid2(parentGermplasm.getGid());
		germplasm.setMethodId(TEST_METHOD_ID);
		this.germplasmDAO.save(germplasm);
		return germplasm;
	}

}
