package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.generationcp.middleware.ruleengine.ExpressionUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Deprecated
@Component
public class DeprecatedPaddedSequenceExpression extends DeprecatedSequenceExpression {

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
