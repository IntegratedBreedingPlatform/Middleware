package org.generationcp.middleware.ruleengine.newnaming.expression;

import org.generationcp.middleware.ruleengine.ExpressionUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PaddedSequenceExpression extends SequenceExpression {

	@Override
	public Integer getNumberOfDigits(final StringBuilder container) {
		return ExpressionUtils.getNumberOfDigitsFromKey(org.generationcp.middleware.ruleengine.coding.expression.CodingPaddedSequenceExpression.PATTERN, container);
	}

	@Override
	public String getExpressionKey() {
		return org.generationcp.middleware.ruleengine.coding.expression.CodingPaddedSequenceExpression.PATTERN_KEY;
	}

	@Override
	public Pattern getPattern() {
		return org.generationcp.middleware.ruleengine.coding.expression.CodingPaddedSequenceExpression.PATTERN;
	}


}
