
package org.generationcp.middleware.dao.oms;

import java.util.UUID;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VariableOverridesDaoTest extends IntegrationTestBase {

	private static VariableOverridesDao dao;

	private static Integer variableId = 18000; // Grain_yield from test db
	private static String programId = UUID.randomUUID().toString();

	@Before
	public void setUp() throws Exception {
		VariableOverridesDaoTest.dao = new VariableOverridesDao();
		VariableOverridesDaoTest.dao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testSaveTermProgramProperty() throws Exception {
		VariableOverrides overrides =
				VariableOverridesDaoTest.dao.save(VariableOverridesDaoTest.variableId, VariableOverridesDaoTest.programId, "My Alias", "0",
						"100");
		VariableOverrides dbOverrides =
				VariableOverridesDaoTest.dao.getByVariableAndProgram(VariableOverridesDaoTest.variableId,
						VariableOverridesDaoTest.programId);
		Assert.assertEquals(overrides.getAlias(), dbOverrides.getAlias());
		Assert.assertEquals(overrides.getExpectedMin(), dbOverrides.getExpectedMin());
		Assert.assertEquals(overrides.getExpectedMax(), dbOverrides.getExpectedMax());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		VariableOverridesDaoTest.dao.setSession(null);
		VariableOverridesDaoTest.dao = null;
	}
}
