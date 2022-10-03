package org.generationcp.middleware.ruleengine.coding.expression;

import org.generationcp.middleware.ruleengine.ExpressionUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PaddedSequenceExpression extends SequenceExpression {

	private static final String PADSEQ_BASE = "PADSEQ";
	public static final String PATTERN_KEY = "\\[" + PADSEQ_BASE + "(\\.[0-9]+)*\\]";
	public static final Pattern PATTERN = Pattern.compile(PATTERN_KEY);

	@Override
	public Integer getNumberOfDigits(final StringBuilder container) {
		return ExpressionUtils.getNumberOfDigitsFromKey(PATTERN, container);
	}

	@Override
	public String getExpressionKey() {
		return PaddedSequenceExpression.PATTERN_KEY;
	}

	@Override
	public Pattern getPattern() {
		return PaddedSequenceExpression.PATTERN;
	}

}
