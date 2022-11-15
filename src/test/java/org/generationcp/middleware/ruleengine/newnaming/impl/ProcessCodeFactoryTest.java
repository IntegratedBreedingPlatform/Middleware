package org.generationcp.middleware.ruleengine.newnaming.impl;

import org.generationcp.middleware.ruleengine.newnaming.expression.FirstExpression;
import org.generationcp.middleware.ruleengine.newnaming.expression.SeasonExpression;
import org.generationcp.middleware.ruleengine.newnaming.expression.SequenceExpression;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProcessCodeFactoryTest {

	private static final String KEY1 = "[SEQUENCE]";
	private static final String KEY2 = "[FIRST]";
	private static final String[] KEYS = {KEY1, KEY2};

	private final ProcessCodeFactory factory = new ProcessCodeFactory();

	@Before
	public void init() {

		this.factory.init();
		this.factory.addExpression(new SeasonExpression());
		this.factory.addExpression(new SequenceExpression());
		this.factory.addExpression(new FirstExpression());
	}

	@Test
	public void testLookup() {
		for (final String key : ProcessCodeFactoryTest.KEYS) {
			Assert.assertNotNull(this.factory.lookup(key));
			Assert.assertNull(this.factory.lookup(""));

		}

	}

}
