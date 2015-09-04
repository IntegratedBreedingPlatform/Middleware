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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.util.Debug;
import org.junit.Before;
import org.junit.Test;

public class ProjectPropertyDaoTest extends IntegrationTestBase {

	private ProjectPropertyDao dao;

	@Before
	public void setUp() throws Exception {
		this.dao = new ProjectPropertyDao();
		this.dao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testGetStandardVariableIdsByPropertyNames() throws Exception {
		List<String> propertyNames =
				Arrays.asList("ENTRY", "ENTRYNO", "PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET", "LOC", "LOCN", "NURSER", "Plot Number");

		Map<String, Set<Integer>> results = this.dao.getStandardVariableIdsByPropertyNames(propertyNames);

		Debug.println(0, "testGetStandardVariableIdsByPropertyNames(propertyNames=" + propertyNames + ") RESULTS:");
		for (String name : propertyNames) {
			Debug.println(0, "    Header = " + name + ", Terms = " + results.get(name));

			/*
			 * TO VERIFY: SELECT DISTINCT ppValue.value, ppStdVar.id FROM projectprop ppValue INNER JOIN (SELECT project_id, value id FROM
			 * projectprop WHERE type_id = 1070) AS ppStdVar ON ppValue.project_id = ppStdVar.project_id AND ppValue.type_id = 1060 AND
			 * ppValue.value IN (:propertyNames)
			 */
		}
	}
}
