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

package org.generationcp.middleware.dao;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LocationDAOTest extends IntegrationTestBase {

	private LocationDAO dao;
	private static final int EXISTING_LOCATION_ID = 1;


	@Before
	public void setUp() throws Exception {
		if (this.dao == null) {
			this.dao = new LocationDAO();
			this.dao.setSession(this.sessionProvder.getSession());
		}
	}

	@Test
	public void testGetExistingLocationIds() {
		List<Integer> locationIdsToVerify = Arrays.asList(new Integer[] {12345678, 8765432, EXISTING_LOCATION_ID});
		List<Integer> existingLocationIds = this.dao.getExistingLocationIds(locationIdsToVerify, null);
		Assert.assertFalse(existingLocationIds.isEmpty());
		Assert.assertEquals(1, existingLocationIds.size());
		Assert.assertEquals(EXISTING_LOCATION_ID, existingLocationIds.get(0).intValue());
	}
}
