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

import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.util.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExperimentPropertyDaoTest extends IntegrationTestBase {

	private ExperimentPropertyDao dao;

	@Before
	public void setUp() throws Exception {
		this.dao = new ExperimentPropertyDao();
		this.dao.setSession(this.sessionProvder.getSession());
	}

	// FIXME: hardcoded project id
	@Test
	public void testGetFieldMapLabels() throws Exception {
		int projectId = 5734; // 5790;
		List<FieldMapDatasetInfo> datasets = this.dao.getFieldMapLabels(projectId);

		if (datasets == null) {
			Debug.println(0, "testGetFieldMapLabels(projectId=" + projectId + ") RESULTS: NULL/Empty");
			return;
		}

		Debug.println(0, "testGetFieldMapLabels(projectId=" + projectId + ") RESULTS:");
		for (FieldMapDatasetInfo dataset : datasets) {
			Debug.println(3, dataset.toString());
		}
		Assert.assertFalse(datasets.isEmpty());
	}
}
