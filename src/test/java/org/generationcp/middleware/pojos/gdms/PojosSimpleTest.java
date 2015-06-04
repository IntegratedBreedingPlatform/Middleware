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

package org.generationcp.middleware.pojos.gdms;

import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.utils.test.Debug;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class PojosSimpleTest extends DataManagerIntegrationTest {

	private static Session session;

	@BeforeClass
	public static void setUp() throws Exception {
		PojosSimpleTest.session = DataManagerIntegrationTest.managerFactory.getSessionProvider().getSession();
	}

	@Test
	public void testAccMetadataSet() {
		Query query = PojosSimpleTest.session.createQuery("FROM AccMetadataSet");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof AccMetadataSet);
			Assert.assertTrue(obj != null);
			AccMetadataSet element = (AccMetadataSet) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, element);
		}
	}

	@Test
	public void testAlleleValues() {
		Query query = PojosSimpleTest.session.createQuery("FROM AlleleValues");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof AlleleValues);
			Assert.assertTrue(obj != null);
			AlleleValues element = (AlleleValues) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, element);
		}
	}

	@Test
	public void testCharValues() {
		Query query = PojosSimpleTest.session.createQuery("FROM CharValues");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof CharValues);
			Assert.assertTrue(obj != null);
			CharValues element = (CharValues) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, element);
		}
	}

	@Test
	public void testDataset() {
		Query query = PojosSimpleTest.session.createQuery("FROM Dataset");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Dataset);
			Assert.assertTrue(obj != null);
			Dataset element = (Dataset) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, element);
		}
	}

	@Test
	public void testMap() {
		Query query = PojosSimpleTest.session.createQuery("FROM Map");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Map);
			Assert.assertTrue(obj != null);
			Map element = (Map) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, element);
		}
	}

	@Test
	public void testMappingData() {
		Query query = PojosSimpleTest.session.createQuery("FROM MappingData");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof MappingData);
			Assert.assertTrue(obj != null);
			MappingData element = (MappingData) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, element);
		}
	}

	@Test
	public void testMappingPop() {
		Query query = PojosSimpleTest.session.createQuery("FROM MappingPop");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof MappingPop);
			Assert.assertTrue(obj != null);
			MappingPop element = (MappingPop) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, element);
		}
	}

	@Test
	public void testMappingPopValues() {
		Query query = PojosSimpleTest.session.createQuery("FROM MappingPopValues");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof MappingPopValues);
			Assert.assertTrue(obj != null);
			MappingPopValues element = (MappingPopValues) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, element);
		}
	}

	@Test
	public void testMarker() {
		Query query = PojosSimpleTest.session.createQuery("FROM Marker");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Marker);
			Assert.assertTrue(obj != null);
			Marker element = (Marker) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, element);
		}
	}

	@Test
	public void testMarkerMetadataSet() {
		Query query = PojosSimpleTest.session.createQuery("FROM MarkerMetadataSet");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof MarkerMetadataSet);
			Assert.assertTrue(obj != null);
			MarkerMetadataSet element = (MarkerMetadataSet) obj;
			Debug.println(MiddlewareIntegrationTest.INDENT, element);
		}
	}
}
