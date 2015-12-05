
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmList;
import org.hibernate.Criteria;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class GermplasmListDAOTest extends IntegrationTestBase {

	private static GermplasmListDAO dao;
	private static final String TEST_GERMPLASM_LIST_NAME = "TestGermplasmListName";
	private static final String TEST_GERMPLASM_LIST_DESC = "TestGermplasmListDesc";
	private static final long TEST_GERMPLASM_LIST_DATE = 20141103;
	private static final String TEST_GERMPLASM_LIST_TYPE_LST = "LST";
	private static final int TEST_GERMPLASM_LIST_USER_ID = 1;
	private static final Integer STATUS_ACTIVE = 0;
	private static final Integer STATUS_DELETED = 9;
	public static List<String> EXCLUDED_GERMPLASM_LIST_TYPES = new ArrayList<String>();
	
	static{
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("NURSERY");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("TRIAL");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("CHECK");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("ADVANCED");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("CROSSES");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("FOLDER");
	}
	
	@Before
	public void setUp() throws Exception {
		GermplasmListDAOTest.dao = new GermplasmListDAO();
		GermplasmListDAOTest.dao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testHideSnapshotListTypes() {
		GermplasmListDAO dao = new GermplasmListDAO();
		Criteria criteria = Mockito.mock(Criteria.class);
		dao.hideSnapshotListTypes(criteria);
		List<SimpleExpression> restrictedList = dao.getRestrictedSnapshopTypes();
		// this should ensure that the snapshot list types are added int he criteria object
		for (SimpleExpression restricted : restrictedList) {
			Mockito.verify(criteria, Mockito.times(1)).add(restricted);
		}

	}

	@Test
	public void testGetRestrictedSnapshopTypes() {
		GermplasmListDAO dao = new GermplasmListDAO();
		List<SimpleExpression> restrictedList = dao.getRestrictedSnapshopTypes();
		Assert.assertEquals("Should have 5 restricted snapshot types", 5, restrictedList.size());
	}

	@Test
	public void testCountByName() throws Exception {

		GermplasmList list =
				GermplasmListDAOTest.saveGermplasm(GermplasmListDAOTest.createGermplasmListTestData(
						GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC,
						GermplasmListDAOTest.TEST_GERMPLASM_LIST_DATE, GermplasmListDAOTest.TEST_GERMPLASM_LIST_TYPE_LST,
						GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE));
		Assert.assertEquals("There should be one germplasm list with name " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, 1,
				GermplasmListDAOTest.dao.countByName(GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, Operation.EQUAL));

		list.setStatus(GermplasmListDAOTest.STATUS_DELETED);
		GermplasmListDAOTest.saveGermplasm(list);
		Assert.assertEquals("There should be no germplasm list with name " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, 0,
				GermplasmListDAOTest.dao.countByName(GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, Operation.EQUAL));

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void TestGetGermplasmListTypes(){
		List<String> germplasmListTypes = GermplasmListDAOTest.dao.getGermplasmListTypes();
		for(String listType: GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES){
			Assert.assertFalse(listType + " should not be in the Results Array", germplasmListTypes.contains(listType));
		}
	}

	private static GermplasmList saveGermplasm(GermplasmList list) throws MiddlewareQueryException {
		GermplasmList newList = GermplasmListDAOTest.dao.saveOrUpdate(list);
		return newList;
	}

	private static GermplasmList createGermplasmListTestData(String name, String description, long date, String type, int userId, int status)
			throws MiddlewareQueryException {
		GermplasmList list = new GermplasmList();
		list.setName(name);
		list.setDescription(description);
		list.setDate(date);
		list.setType(type);
		list.setUserId(userId);
		list.setStatus(status);
		list.setProgramUUID(UUID.randomUUID().toString());
		return list;
	}

}
