
package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Deprecated
@Component
public class DeprecatedLocationAbbreviationExpression extends DeprecatedBaseExpression {

	public static final String KEY = "[LABBR]";

	public DeprecatedLocationAbbreviationExpression() {
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
		return DeprecatedLocationAbbreviationExpression.KEY;
	}
}
