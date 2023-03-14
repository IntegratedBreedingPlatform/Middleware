
package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class VariableOverridesDaoTest extends IntegrationTestBase {

	private static VariableOverridesDao dao;

	private static Integer variableId = 51570; // GY_Adj_kgha from test db
	private static String programId = UUID.randomUUID().toString();

	@Before
	public void setUp() throws Exception {
		VariableOverridesDaoTest.dao = new VariableOverridesDao(this.sessionProvder.getSession());
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

}
