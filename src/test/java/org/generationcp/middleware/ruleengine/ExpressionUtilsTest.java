package org.generationcp.middleware.ruleengine;

import org.generationcp.middleware.ruleengine.naming.expression.Expression;
import org.generationcp.middleware.ruleengine.naming.expression.FirstExpression;
import org.generationcp.middleware.ruleengine.naming.expression.PaddedSequenceExpression;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class ExpressionUtilsTest {

	private final Expression unitUnderTest = new FirstExpression();

	@Test
	public void testReplaceProcessCodeWithValue() {
		final String key = this.unitUnderTest.getExpressionKey();
		final StringBuilder builder = new StringBuilder("ABC" + key);

		ExpressionUtils.replaceExpressionWithValue(key, builder, "D");

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABCD", builder.toString());
	}

	@Test
	public void testReplaceProcessCodeWithNullValue() {
		final String key = this.unitUnderTest.getExpressionKey();
		final StringBuilder builder = new StringBuilder("ABC" + key);

		final String nullVariable = null;
		ExpressionUtils.replaceExpressionWithValue(key, builder, nullVariable);

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABC", builder.toString());
	}

	@Test
	public void testReplaceRegularExpressionProcessCodeWithNullValue() {
		final PaddedSequenceExpression expression = new PaddedSequenceExpression();
		final StringBuilder builder = new StringBuilder("ABC" + "[PADSEQ.3]");

		final String nullVariable = null;
		ExpressionUtils.replaceRegularExpressionKeyWithValue(Pattern.compile(expression.getExpressionKey()), builder, nullVariable);

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABC", builder.toString());
	}

	@Test
	public void testReplaceRegularExpressionProcessCodeWithValue() {
		final PaddedSequenceExpression expression = new PaddedSequenceExpression();
		final StringBuilder builder = new StringBuilder("ABC" + "[PADSEQ.3]");

		final String value = "023";
		ExpressionUtils.replaceRegularExpressionKeyWithValue(Pattern.compile(expression.getExpressionKey()), builder, value);

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABC" + value, builder.toString());
	}

	@Test
	public void testGetNumberOfDigitsFromKey() {
		final PaddedSequenceExpression expression = new PaddedSequenceExpression();
		final Pattern pattern = Pattern.compile(expression.getExpressionKey());
		// When no digit is specified
		Assert.assertEquals(ExpressionUtils.DEFAULT_LENGTH, ExpressionUtils.getNumberOfDigitsFromKey(pattern,  new StringBuilder("ABC" + "[PADSEQ]XYZ")));
		// Check that regex matching is case-insensitive
		Assert.assertEquals(ExpressionUtils.DEFAULT_LENGTH, ExpressionUtils.getNumberOfDigitsFromKey(pattern,  new StringBuilder("ABC" + "[padseq]XYZ")));
		// With digit specified
		Assert.assertEquals(7, ExpressionUtils.getNumberOfDigitsFromKey(pattern,  new StringBuilder("ABC" + "[PADSEQ.7]XYZ")).intValue());
		// Regex not matched
		Assert.assertEquals(0, ExpressionUtils.getNumberOfDigitsFromKey(pattern,  new StringBuilder("ABC" + "[SEQUENCE]")).intValue());
	}

}
