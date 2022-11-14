package org.generationcp.middleware.ruleengine.naming.deprecated.expression;

import org.generationcp.middleware.ruleengine.ExpressionUtils;

@Deprecated
public abstract class DeprecatedBaseExpression implements DeprecatedExpression {

	protected void replaceExpressionWithValue(final StringBuilder container, final String value) {
		ExpressionUtils.replaceExpressionWithValue(this.getExpressionKey(), container, value);
	}
}
