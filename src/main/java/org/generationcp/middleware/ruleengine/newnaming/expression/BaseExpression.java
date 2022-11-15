package org.generationcp.middleware.ruleengine.newnaming.expression;

import org.generationcp.middleware.ruleengine.ExpressionUtils;

public abstract class BaseExpression implements Expression {

	protected void replaceExpressionWithValue(final StringBuilder container, final String value) {
		ExpressionUtils.replaceExpressionWithValue(this.getExpressionKey(), container, value);
	}
}
