package org.generationcp.middleware.ruleengine.coding.expression;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CodingExpressionFactoryTest {

	private static final String KEY = "[SEQUENCE]";

	private final CodingExpressionFactory factory = new CodingExpressionFactory();

	@Before
	public void init() {

		this.factory.init();
		this.factory.addExpression(new SequenceExpression());
	}

	@Test
	public void testLookup() {
		Assert.assertNotNull(this.factory.lookup(CodingExpressionFactoryTest.KEY));
		Assert.assertNull(this.factory.lookup(""));

	}

}
