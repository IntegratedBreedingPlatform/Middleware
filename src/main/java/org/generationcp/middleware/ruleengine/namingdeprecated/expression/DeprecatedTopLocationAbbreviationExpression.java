
package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Deprecated
@Component
public class DeprecatedTopLocationAbbreviationExpression extends DeprecatedBaseExpression {

	public static final String KEY = "[TLABBR]";

	public DeprecatedTopLocationAbbreviationExpression() {
	}

	@Override
	public void apply(List<StringBuilder> values, DeprecatedAdvancingSource source, final String capturedText) {
		for (StringBuilder container : values) {
			String rootName = source.getRootName();
			String labbr = source.getLocationAbbreviation() != null ? source.getLocationAbbreviation() : "";
			if (rootName != null && rootName.toString().endsWith("T")) {
                this.replaceExpressionWithValue(container, "TOP" + labbr);
			} else {
				this.replaceExpressionWithValue(container, labbr);
			}
		}
	}

	@Override
	public String getExpressionKey() {
		return DeprecatedTopLocationAbbreviationExpression.KEY;
	}
}
