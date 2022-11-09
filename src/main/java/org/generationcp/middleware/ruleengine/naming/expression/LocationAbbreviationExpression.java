
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationAbbreviationExpression extends BaseExpression {

	public static final String KEY = "[LABBR]";

	public LocationAbbreviationExpression() {
	}

	@Override
	public void apply(List<StringBuilder> values, DeprecatedAdvancingSource source, final String capturedText) {
		for (StringBuilder container : values) {
			String newValue = source.getLocationAbbreviation();
			this.replaceExpressionWithValue(container, newValue);
		}
	}

	@Override
	public String getExpressionKey() {
		return LocationAbbreviationExpression.KEY;
	}
}
