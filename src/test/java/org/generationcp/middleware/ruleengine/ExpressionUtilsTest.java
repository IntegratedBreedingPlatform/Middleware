package org.generationcp.middleware.ruleengine;

import org.generationcp.middleware.ruleengine.namingdeprecated.expression.DeprecatedExpression;
import org.generationcp.middleware.ruleengine.namingdeprecated.expression.DeprecatedFirstExpression;
import org.generationcp.middleware.ruleengine.namingdeprecated.expression.DeprecatedPaddedSequenceExpression;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class ExpressionUtilsTest {

	private final DeprecatedExpression unitUnderTest = new DeprecatedFirstExpression();

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
		final DeprecatedPaddedSequenceExpression expression = new DeprecatedPaddedSequenceExpression();
		final StringBuilder builder = new StringBuilder("ABC" + "[PADSEQ.3]");

		final String nullVariable = null;
		ExpressionUtils.replaceRegularExpressionKeyWithValue(Pattern.compile(expression.getExpressionKey()), builder, nullVariable);

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABC", builder.toString());
	}

	@Test
	public void testReplaceRegularExpressionProcessCodeWithValue() {
		final DeprecatedPaddedSequenceExpression expression = new DeprecatedPaddedSequenceExpression();
		final StringBuilder builder = new StringBuilder("ABC" + "[PADSEQ.3]");

		final String value = "023";
		ExpressionUtils.replaceRegularExpressionKeyWithValue(Pattern.compile(expression.getExpressionKey()), builder, value);

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABC" + value, builder.toString());
	}

	@Test
	public void testGetNumberOfDigitsFromKey() {
		final DeprecatedPaddedSequenceExpression expression = new DeprecatedPaddedSequenceExpression();
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
