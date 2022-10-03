
package org.generationcp.middleware.ruleengine.naming.impl;

import java.util.HashMap;
import java.util.Map;

import org.generationcp.middleware.ruleengine.naming.expression.Expression;

public class ProcessCodeFactory {

	private Map<String, Expression> expressionMap;

	public void init() {

		this.expressionMap = new HashMap<>();

	}

	/**
	 * @param pattern
	 * @return the first Expression that match the pattern
	 */
	public Expression lookup(final String pattern) {
		if (this.expressionMap.containsKey(pattern)) {
			return this.expressionMap.get(pattern);
		}
		for (final String key : this.expressionMap.keySet()) {
			if (key != null && pattern.matches(key)) {
				final Expression expression = this.expressionMap.get(key);
				this.expressionMap.put(pattern, expression); // memorize
				return expression;
			}
		}
		return null;
	}

	public void addExpression(final Expression expression) {
		this.expressionMap.put(expression.getExpressionKey(), expression);
	}
}
