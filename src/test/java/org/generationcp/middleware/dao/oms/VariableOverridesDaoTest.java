
package org.generationcp.middleware.dao.oms;

import java.util.UUID;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class VariableOverridesDaoTest extends MiddlewareIntegrationTest {

	private static VariableOverridesDao dao;

	private static Integer variableId = 18000; // Grain_yield from test db
	private static String programId = UUID.randomUUID().toString();

	@BeforeClass
	public static void setUp() throws Exception {
		VariableOverridesDaoTest.dao = new VariableOverridesDao();
		VariableOverridesDaoTest.dao.setSession(MiddlewareIntegrationTest.sessionUtil.getCurrentSession());
	}

	@Test
	public void testSaveTermProgramProperty() throws Exception {
		VariableOverrides overrides =
				VariableOverridesDaoTest.dao.save(VariableOverridesDaoTest.variableId,
						VariableOverridesDaoTest.programId, "My Alias", "0", "100");
		VariableOverridesDaoTest.dao.flush();
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
