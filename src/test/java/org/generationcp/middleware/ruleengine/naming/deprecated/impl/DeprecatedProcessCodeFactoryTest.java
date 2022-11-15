package org.generationcp.middleware.ruleengine.naming.deprecated.impl;

import org.generationcp.middleware.ruleengine.naming.deprecated.expression.DeprecatedFirstExpression;
import org.generationcp.middleware.ruleengine.naming.deprecated.expression.DeprecatedSeasonExpression;
import org.generationcp.middleware.ruleengine.naming.deprecated.expression.DeprecatedSequenceExpression;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeprecatedProcessCodeFactoryTest {

	private static final String KEY1 = "[SEQUENCE]";
	private static final String KEY2 = "[FIRST]";
	private static final String[] KEYS = {KEY1, KEY2};

	private final DeprecatedProcessCodeFactory factory = new DeprecatedProcessCodeFactory();

	@Before
	public void init() {

		this.factory.init();
		this.factory.addExpression(new DeprecatedSeasonExpression());
		this.factory.addExpression(new DeprecatedSequenceExpression());
		this.factory.addExpression(new DeprecatedFirstExpression());
	}

	@Test
	public void testLookup() {
		for (final String key : DeprecatedProcessCodeFactoryTest.KEYS) {
			Assert.assertNotNull(this.factory.lookup(key));
			Assert.assertNull(this.factory.lookup(""));

		}

	}

}
