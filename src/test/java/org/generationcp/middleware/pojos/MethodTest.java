package org.generationcp.middleware.pojos;


import org.junit.Assert;
import org.junit.Test;

public class MethodTest {

	@Test
	public void testIsGenerative() {
		Method method = new Method();
		// null
		Assert.assertFalse(method.isGenerative());

		// empty
		method.setMtype("");
		Assert.assertFalse(method.isGenerative());

		// not GEN
		method.setMtype("NOTGEN");
		Assert.assertFalse(method.isGenerative());

		// GEN with leading, trailing whitespace
		method.setMtype("  GEN  ");
		Assert.assertTrue(method.isGenerative());

		// GEN
		method.setMtype("GEN");
		Assert.assertTrue(method.isGenerative());
	}
}
