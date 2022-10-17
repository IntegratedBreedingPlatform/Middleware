package org.generationcp.middleware.ruleengine.coding.expression;

import org.generationcp.middleware.ruleengine.Expression;
import org.generationcp.middleware.ruleengine.ExpressionUtils;

import java.util.regex.Pattern;

public abstract class BaseCodingExpression implements Expression {

	void replaceRegularExpressionKeyWithValue(final StringBuilder container, final String value) {
		ExpressionUtils.replaceRegularExpressionKeyWithValue(this.getPattern(), container, value);
	}

	abstract Pattern getPattern();

}
