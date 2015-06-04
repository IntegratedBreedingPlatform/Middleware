
package org.generationcp.middleware.dao.oms;

import java.util.UUID;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.pojos.oms.VariableProgramOverrides;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class VariableProgramOverridesDaoTest extends MiddlewareIntegrationTest {

	private static VariableProgramOverridesDao dao;

	private static Integer variableId = 18000; // Grain_yield from test db
	private static String programId = UUID.randomUUID().toString();

	@BeforeClass
	public static void setUp() throws Exception {
		VariableProgramOverridesDaoTest.dao = new VariableProgramOverridesDao();
		VariableProgramOverridesDaoTest.dao.setSession(MiddlewareIntegrationTest.sessionUtil.getCurrentSession());
	}

	@Test
	public void testSaveTermProgramProperty() throws Exception {
		VariableProgramOverrides overrides =
				VariableProgramOverridesDaoTest.dao.save(VariableProgramOverridesDaoTest.variableId,
						VariableProgramOverridesDaoTest.programId, "My Alias", "0", "100");
		VariableProgramOverridesDaoTest.dao.flush();
		VariableProgramOverrides dbOverrides =
				VariableProgramOverridesDaoTest.dao.getByVariableAndProgram(VariableProgramOverridesDaoTest.variableId,
						VariableProgramOverridesDaoTest.programId);
		Assert.assertEquals(overrides.getAlias(), dbOverrides.getAlias());
		Assert.assertEquals(overrides.getMinValue(), dbOverrides.getMinValue());
		Assert.assertEquals(overrides.getMaxValue(), dbOverrides.getMaxValue());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		VariableProgramOverridesDaoTest.dao.setSession(null);
		VariableProgramOverridesDaoTest.dao = null;
	}
}
