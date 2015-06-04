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

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmList;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GermplasmListDAOTest extends MiddlewareIntegrationTest {

	private static GermplasmListDAO dao;
	private static final String TEST_GERMPLASM_LIST_NAME = "TestGermplasmListName";
	private static final String TEST_GERMPLASM_LIST_DESC = "TestGermplasmListDesc";
	private static final long TEST_GERMPLASM_LIST_DATE = 20141103;
	private static final String TEST_GERMPLASM_LIST_TYPE_LST = "LST";
	private static final int TEST_GERMPLASM_LIST_USER_ID = 1;
	private static final Integer STATUS_ACTIVE = 0;
	private static final Integer STATUS_DELETED = 9;

	private static Session session;

	@BeforeClass
	public static void setUp() throws Exception {
		GermplasmListDAOTest.dao = new GermplasmListDAO();
		GermplasmListDAOTest.session = MiddlewareIntegrationTest.sessionUtil.getCurrentSession();
		GermplasmListDAOTest.dao.setSession(GermplasmListDAOTest.session);
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

		GermplasmListDAOTest.deleteGermplasm(list);

	}

	private static void deleteGermplasm(GermplasmList list) throws MiddlewareQueryException {
		GermplasmListDAOTest.session.delete(list);
		GermplasmListDAOTest.session.flush();
		GermplasmListDAOTest.session.clear();
	}

	private static GermplasmList saveGermplasm(GermplasmList list) throws MiddlewareQueryException {
		GermplasmList newList = GermplasmListDAOTest.dao.saveOrUpdate(list);
		GermplasmListDAOTest.dao.flush();
		GermplasmListDAOTest.dao.clear();
		return newList;
	}

	private static GermplasmList createGermplasmListTestData(String name, String description, long date, String type, int userId, int status)
			throws MiddlewareQueryException {
		GermplasmList list = new GermplasmList();
		Integer nextId = GermplasmListDAOTest.dao.getNextId("id");
		list.setId(nextId);
		list.setName(name);
		list.setDescription(description);
		list.setDate(date);
		list.setType(type);
		list.setUserId(userId);
		list.setStatus(status);
		return list;
	}

	@AfterClass
	public static void tearDown() {
		GermplasmListDAOTest.dao.setSession(null);
		GermplasmListDAOTest.dao = null;
	}
}
