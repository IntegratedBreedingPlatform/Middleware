package org.generationcp.middleware.ruleengine.coding.expression;

import org.junit.Assert;
import org.junit.Test;

public class BaseCodingExpressionTest {
	private static final String SEQUENCE_CODE = "[SEQUENCE]";

	private final CodingSequenceExpression expression = new CodingSequenceExpression();

	@Test
	public void testReplaceProcessCodeWithValue() {
		final StringBuilder builder = new StringBuilder("ABC" + SEQUENCE_CODE);

		this.expression.replaceRegularExpressionKeyWithValue(builder, "D");

		Assert.assertEquals("BaseCodingExpression unable to replace the process code with the new value", "ABCD", builder.toString());
	}

	@Test
	public void testReplaceProcessCodeWithNullVariable() {
		final StringBuilder builder = new StringBuilder("ABC" + SEQUENCE_CODE);

		final String nullVariable = null;
		this.expression.replaceRegularExpressionKeyWithValue(builder, nullVariable);

		Assert.assertEquals("BaseCodingExpression unable to replace the process code with the new value", "ABC", builder.toString());
	}
}
