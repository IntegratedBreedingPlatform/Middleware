
package org.generationcp.middleware.ruleengine.naming.deprecated.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Used as a separator with a string literal immediately following it. Otherwise, it will be disregarded.
 */

@Deprecated
@Component
public class DeprecatedFirstExpression extends DeprecatedBaseExpression {

	public static final String KEY = "[FIRST]";

	public DeprecatedFirstExpression() {

	}

	@Override
	public void apply(List<StringBuilder> values, DeprecatedAdvancingSource source, final String capturedText) {
		String separatorExpression = source.getBreedingMethod().getSeparator();

		for (StringBuilder value : values) {
			if (separatorExpression != null && separatorExpression.toString().toUpperCase().contains(DeprecatedFirstExpression.KEY)) {
				int start = separatorExpression.toString().toUpperCase().indexOf(DeprecatedFirstExpression.KEY) + DeprecatedFirstExpression.KEY.length();
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
		return DeprecatedFirstExpression.KEY;
	}
}
