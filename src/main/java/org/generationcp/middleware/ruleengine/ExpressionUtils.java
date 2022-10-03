package org.generationcp.middleware.ruleengine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExpressionUtils {

	public static final Integer DEFAULT_LENGTH = 3;

	private ExpressionUtils() {
		// utility class
	}

	public static void replaceExpressionWithValue(final String expressionKey, final StringBuilder container, final String value) {
		final int startIndex = container.toString().toUpperCase().indexOf(expressionKey);
		final int endIndex = startIndex + expressionKey.length();

		final String replaceValue = value == null ? "" : value;
		container.replace(startIndex, endIndex, replaceValue);
	}

	public static void replaceRegularExpressionKeyWithValue (final Pattern pattern, final StringBuilder container, final String value) {
		final Matcher matcher = pattern.matcher(container.toString().toUpperCase());
		if (matcher.find()) {
			final String replaceValue = value == null ? "" : value;
			container.replace(matcher.start(), matcher.end(), replaceValue);
		}

	}

	public static Integer getNumberOfDigitsFromKey(final Pattern pattern, final StringBuilder container) {
		final Matcher matcher = pattern.matcher(container.toString().toUpperCase());
		// If pattern is matched but no digit specified, use default number of digits
		Integer numberOfDigits = ExpressionUtils.DEFAULT_LENGTH;
		if (matcher.find()) {
			final String processCode = matcher.group();
			// Look for a digit withing the process code, if present
			final Pattern patternDigit = Pattern.compile("[[0-9]*]");
			final Matcher matcherDigit = patternDigit.matcher(processCode);
			if (matcherDigit.find()) {
				numberOfDigits = Integer.valueOf(matcherDigit.group());
			}
			return numberOfDigits;
		}
		return 0;
	}

}
