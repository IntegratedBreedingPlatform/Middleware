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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.util.Debug;
import org.junit.Before;
import org.junit.Test;

public class GeolocationDaoTest extends IntegrationTestBase {

	private GeolocationDao dao;

	@Before
	public void setUp() throws Exception {
		this.dao = new GeolocationDao();
		this.dao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testGetTrialEnvironmentDetails() throws Exception {
		Set<Integer> environmentIds = new HashSet<Integer>();
		environmentIds.add(5822);
		List<TrialEnvironment> results = this.dao.getTrialEnvironmentDetails(environmentIds);
		Debug.println(0, "testGetTrialEnvironmentDetails(environmentIds=" + environmentIds + ") RESULTS:");
		for (TrialEnvironment env : results) {
			env.print(4);
		}
	}
}
