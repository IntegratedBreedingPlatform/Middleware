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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.util.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class ProjectPropertyDaoTest extends IntegrationTestBase {

	private ProjectPropertyDao dao;

	@Before
	public void setUp() throws Exception {
		this.dao = new ProjectPropertyDao();
		this.dao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testGetStandardVariableIdsWithTypeByPropertyNames() throws Exception {
		Map<String, VariableType> expectedStdVarWithTypeMap = this.createVarNameWithTypeMapTestData();

		List<String> propertyNames = new ArrayList<String>();
		propertyNames.addAll(expectedStdVarWithTypeMap.keySet());

		Map<String, Map<Integer, VariableType>> results = this.dao.getStandardVariableIdsWithTypeByPropertyNames(propertyNames);

		Debug.println(0, "testGetStandardVariableIdsByPropertyNames(propertyNames=" + propertyNames + ") RESULTS:");
		for (String name : propertyNames) {
			Map<Integer, VariableType> actualStdVarIdWithTypeMap = results.get(name);
			Debug.println(0, "    Header = " + name + ", Terms = " + actualStdVarIdWithTypeMap);
			if (actualStdVarIdWithTypeMap != null) {
				Assert.assertTrue(actualStdVarIdWithTypeMap.containsValue(expectedStdVarWithTypeMap.get(name)));
			}
		}
	}

	private Map<String, VariableType> createVarNameWithTypeMapTestData() {
		Map<String, VariableType> varNameWithTypeMap = new HashMap<String, VariableType>();
		varNameWithTypeMap.put("TRIAL_INSTANCE", VariableType.ENVIRONMENT_DETAIL);
		varNameWithTypeMap.put("ENTRY_NO", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("DESIGNATION", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("GID", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("CROSS", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("PLOT_NO", VariableType.EXPERIMENTAL_DESIGN);
		varNameWithTypeMap.put("REP_NO", VariableType.EXPERIMENTAL_DESIGN);
		varNameWithTypeMap.put("SITE_SOIL_PH", VariableType.TRAIT);
		return varNameWithTypeMap;
	}
}
