
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Used as a separator with a string literal immediately following it. Otherwise, it will be disregarded.
 */

@Component
public class FirstExpression extends BaseExpression {

	public static final String KEY = "[FIRST]";

	public FirstExpression() {

	}

	@Override
	public void apply(List<StringBuilder> values, DeprecatedAdvancingSource source, final String capturedText) {
		String separatorExpression = source.getBreedingMethod().getSeparator();

		for (StringBuilder value : values) {
			if (separatorExpression != null && separatorExpression.toString().toUpperCase().contains(FirstExpression.KEY)) {
				int start = separatorExpression.toString().toUpperCase().indexOf(FirstExpression.KEY) + FirstExpression.KEY.length();
				int end = separatorExpression.indexOf("[", start);
				if (end == -1) {
					end = separatorExpression.length();
				}
				String literalSeparator = separatorExpression.substring(start, end);

				int index = source.getRootName().indexOf(literalSeparator);
				if (index > -1) {
					String newRootName = source.getRootName().substring(0, index);
					start = value.indexOf(source.getRootName());
					end = start + source.getRootName().length();
					value.replace(start, end, newRootName);
				}
			}

			this.replaceExpressionWithValue(value, "");
		}
	}

	@Override
	public String getExpressionKey() {
		return FirstExpression.KEY;
	}
}
