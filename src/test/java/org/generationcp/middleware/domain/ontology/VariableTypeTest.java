
package org.generationcp.middleware.domain.ontology;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class VariableTypeTest {

	@Test
	public void testGetReservedVariableTypes() {
		final List<VariableType> reservedVariableTypes = VariableType.getReservedVariableTypes();
		Assert.assertNotNull(reservedVariableTypes);
		Assert.assertEquals(2, reservedVariableTypes.size());
		Assert.assertTrue(reservedVariableTypes.contains(VariableType.ANALYSIS));
		Assert.assertTrue(reservedVariableTypes.contains(VariableType.ANALYSIS_SUMMARY));
	}

}
