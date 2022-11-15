
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationAbbreviationExpression extends BaseExpression {

	public static final String KEY = "[LABBR]";

	public LocationAbbreviationExpression() {
	}

	@Override
	public <T extends AbstractAdvancingSource> void apply(final List<StringBuilder> values, final T source, final String capturedText) {
		for (final StringBuilder container : values) {
			final String newValue = source.getLocationAbbreviation();
			this.replaceExpressionWithValue(container, newValue);
		}
	}

	@Override
	public String getExpressionKey() {
		return LocationAbbreviationExpression.KEY;
	}
}
