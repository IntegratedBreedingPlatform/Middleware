/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.ims;

import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.utils.test.Debug;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class IMSPojosSimpleTest extends DataManagerIntegrationTest {

	private static Session session;

	@BeforeClass
	public static void setUp() throws Exception {
		IMSPojosSimpleTest.session = DataManagerIntegrationTest.managerFactory.getSessionProvider().getSession();
	}

	@Test
	public void testLot() throws Exception {
		Query query = IMSPojosSimpleTest.session.createQuery("FROM Lot");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Lot);
			Assert.assertTrue(obj != null);
			Lot holder = (Lot) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, holder);
		}
	}

	@Test
	public void testTransaction() throws Exception {
		Query query = IMSPojosSimpleTest.session.createQuery("FROM Transaction");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Transaction);
			Assert.assertTrue(obj != null);
			Transaction holder = (Transaction) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, holder);
		}
	}

	@Test
	public void testPerson() throws Exception {
		Query query = IMSPojosSimpleTest.session.createQuery("FROM Person");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Person);
			Assert.assertTrue(obj != null);
			Person holder = (Person) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, holder);
		}
	}
}
