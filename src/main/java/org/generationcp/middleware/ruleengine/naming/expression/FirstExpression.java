
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
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
	public <T extends AbstractAdvancingSource> void apply(final List<StringBuilder> values, final T source, final String capturedText) {
		final String separatorExpression = source.getBreedingMethod().getSeparator();

		for (final StringBuilder value : values) {
			if (separatorExpression != null && separatorExpression.toUpperCase().contains(FirstExpression.KEY)) {
				int start = separatorExpression.toUpperCase().indexOf(FirstExpression.KEY) + FirstExpression.KEY.length();
				int end = separatorExpression.indexOf("[", start);
				if (end == -1) {
					end = separatorExpression.length();
				}
				final String literalSeparator = separatorExpression.substring(start, end);

				final int index = source.getRootName().indexOf(literalSeparator);
				if (index > -1) {
					final String newRootName = source.getRootName().substring(0, index);
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
