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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.utils.test.Debug;
import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class IMSPojosSimpleTest extends IntegrationTestBase {

	@Test
	public void testLot() throws Exception {
		Query query = this.sessionProvder.getSession().createQuery("FROM Lot");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Lot);
			Assert.assertTrue(obj != null);
			Lot holder = (Lot) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

	@Test
	public void testTransaction() throws Exception {
		Query query = this.sessionProvder.getSession().createQuery("FROM Transaction");
		query.setMaxResults(5);
		List results = query.list();

		for (Object obj : results) {
			Assert.assertTrue(obj instanceof Transaction);
			Assert.assertTrue(obj != null);
			Transaction holder = (Transaction) obj;
			Debug.println(IntegrationTestBase.INDENT, holder);
		}
	}

}
