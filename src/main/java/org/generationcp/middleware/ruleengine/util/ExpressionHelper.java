
package org.generationcp.middleware.ruleengine.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
public class ExpressionHelper {

	public static final String PROCESS_CODE_PATTERN = "\\[([^\\]]*)]";

	private ExpressionHelper() {

	}

	public static void evaluateExpression(String input, String sequence, ExpressionHelperCallback callback) {
		Pattern pattern = Pattern.compile(sequence);
		Matcher matcher = pattern.matcher(input);

		while (matcher.find()) {
			String text = matcher.group();
			int start = matcher.start();
			int end = matcher.end();

			callback.evaluateCapturedExpression(text, input, start, end);
		}
	}
}
