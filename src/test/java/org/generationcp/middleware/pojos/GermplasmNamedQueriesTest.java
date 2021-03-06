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

import java.math.BigInteger;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.utils.test.Debug;
import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class GermplasmNamedQueriesTest extends IntegrationTestBase {

	@Test
	public void testGetGermplasmByPrefName() {
		String name = "IR 64";
		Query query = this.sessionProvder.getSession().getNamedQuery(Germplasm.GET_BY_PREF_NAME);
		query.setParameter("name", name);
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
	public void testGetAllGermplasm() {
		Query query = this.sessionProvder.getSession().getNamedQuery(Germplasm.GET_ALL);
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
	public void testCountAllGermplasm() {
		Query query = this.sessionProvder.getSession().getNamedQuery(Germplasm.COUNT_ALL);
		Long result = (Long) query.uniqueResult();
		Assert.assertTrue(result != null);
	}

	@Test
	public void testCountGermplasmByPrefName() {
		String name = "IR 64";
		Query query = this.sessionProvder.getSession().createSQLQuery(Germplasm.COUNT_BY_PREF_NAME);
		query.setString("name", name);
		BigInteger result = (BigInteger) query.uniqueResult();
		Assert.assertTrue(result != null);
	}

}
