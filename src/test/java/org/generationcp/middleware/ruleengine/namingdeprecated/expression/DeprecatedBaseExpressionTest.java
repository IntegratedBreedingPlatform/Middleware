package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import junit.framework.Assert;
import org.junit.Test;

public class DeprecatedBaseExpressionTest {
    private DeprecatedBaseExpression unitUnderTest = new DeprecatedFirstExpression();

    @Test
    public void testReplaceProcessCodeWithValue() {
        StringBuilder builder = new StringBuilder("ABC" + unitUnderTest.getExpressionKey());

        unitUnderTest.replaceExpressionWithValue(builder, "D");

        Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABCD", builder.toString());
    }

    @Test
    public void testReplaceProcessCodeWithNullVariable() {
        StringBuilder builder = new StringBuilder("ABC" + unitUnderTest.getExpressionKey());

        String nullVariable = null;
        unitUnderTest.replaceExpressionWithValue(builder, nullVariable);

        Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABC", builder.toString());
    }
}
