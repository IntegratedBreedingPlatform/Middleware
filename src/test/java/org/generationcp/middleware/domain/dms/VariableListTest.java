package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.junit.Assert;
import org.junit.Test;

public class VariableListTest {
	VariableList variableList = new VariableList();

	@Test
	public void testFindByLocalNameWhereVariableIsFound() {
		final Variable variable = DMSVariableTestDataInitializer.createVariable();
		this.variableList.add(variable);
		final Variable foundVariable = this.variableList.findByLocalName(variable.getVariableType().getLocalName());
		Assert.assertNotNull(foundVariable);
	}

	@Test
	public void testFindByLocalNameWhereVariableIsNotFound() {
		final Variable variable = DMSVariableTestDataInitializer.createVariable();
		this.variableList.add(variable);
		final Variable foundVariable = this.variableList.findByLocalName("LOCATION_NAME");
		Assert.assertNull(foundVariable);
	}
}
