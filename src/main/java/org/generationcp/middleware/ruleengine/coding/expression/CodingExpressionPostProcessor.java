package org.generationcp.middleware.ruleengine.coding.expression;

import org.generationcp.middleware.ruleengine.Expression;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class CodingExpressionPostProcessor implements BeanPostProcessor {

	private CodingExpressionFactory codingExpressionFactory;

	@Override
	public Object postProcessBeforeInitialization(final Object o, final String s) {
		return o;
	}

	@Override
	public Object postProcessAfterInitialization(final Object o, final String s) {
		if (o instanceof BaseCodingExpression) {
			codingExpressionFactory.addExpression((Expression) o);
		}
		return o;
	}

	public void setCodingExpressionFactory(final CodingExpressionFactory codingExpressionFactory) {
		this.codingExpressionFactory = codingExpressionFactory;
	}
}
