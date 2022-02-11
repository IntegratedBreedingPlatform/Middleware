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

package org.generationcp.middleware.pojos;

import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.utils.test.Debug;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class GMSPojosSimpleTest extends IntegrationTestBase {

	private Session session;

	@Before
	public void setUp() throws Exception {
		if (this.session == null) {
			this.session = this.sessionProvder.getSession();
		}
	}

	@Test
	public void testAtributs() {
		Query query = this.session.createQuery("FROM Attribute");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Attribute);
			Assert.assertTrue(obj != null);
			Attribute atributs = (Attribute) obj;
			Debug.println(IntegrationTestBase.INDENT, atributs);
		}
	}

	@Test
	public void testBibrefs() {
		Query query = this.session.createQuery("FROM Bibref");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Bibref);
			Assert.assertTrue(obj != null);
			Bibref holder = (Bibref) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testCntry() {
		Query query = this.session.createQuery("FROM Country");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Country);
			Assert.assertTrue(obj != null);
			Country holder = (Country) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testGeoref() {
		Query query = this.session.createQuery("FROM Georef");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Georef);
			Assert.assertTrue(obj != null);
			Georef holder = (Georef) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testGermplsm() {
		Query query = this.session.createQuery("FROM Germplasm");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Germplasm);
			Assert.assertTrue(obj != null);
			Germplasm holder = (Germplasm) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testLocation() {
		Query query = this.session.createQuery("FROM Location");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Location);
			Assert.assertTrue(obj != null);
			Location holder = (Location) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testLocdes() {
		Query query = this.session.createQuery("FROM Locdes");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Locdes);
			Assert.assertTrue(obj != null);
			Locdes holder = (Locdes) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testMethods() {
		Query query = this.session.createQuery("FROM Method");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Method);
			Assert.assertTrue(obj != null);
			Method holder = (Method) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testNames() {
		Query query = this.session.createQuery("FROM Name");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Name);
			Assert.assertTrue(obj != null);
			Name holder = (Name) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testProgntrs() {
		Query query = this.session.createQuery("FROM Progenitor");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Progenitor);
			Assert.assertTrue(obj != null);
			Progenitor holder = (Progenitor) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testUdflds() {
		Query query = this.session.createQuery("FROM UserDefinedField");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof UserDefinedField);
			Assert.assertTrue(obj != null);
			UserDefinedField holder = (UserDefinedField) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testGermplasmList() {
		Query query = this.session.createQuery("FROM GermplasmList");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof GermplasmList);
			Assert.assertTrue(obj != null);
			GermplasmList holder = (GermplasmList) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testGermplasmListData() {
		Query query = this.session.createQuery("FROM GermplasmListData");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof GermplasmListData);
			Assert.assertTrue(obj != null);
			GermplasmListData holder = (GermplasmListData) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testListDataProperty() {

		// UNCOMMENT TO TEST: (IMPORTANT -- RUN IN RICE DB ONLY)
		Query query = this.session.createQuery("FROM GermplasmListData where id IN (32334, 32335, 32336)");
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof GermplasmListData);
			Assert.assertTrue(obj != null);
		}
	}
}
