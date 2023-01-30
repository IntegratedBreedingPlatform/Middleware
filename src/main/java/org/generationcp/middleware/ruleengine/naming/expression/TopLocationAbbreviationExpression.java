
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TopLocationAbbreviationExpression extends BaseExpression {

	public static final String KEY = "[TLABBR]";

	public TopLocationAbbreviationExpression() {
	}

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {
		for (final StringBuilder container : values) {
			final String rootName = advancingSource.getRootName();
			final String labbr = advancingSource.getLocationAbbreviation() != null ? advancingSource.getLocationAbbreviation() : "";
			if (rootName != null && rootName.endsWith("T")) {
                this.replaceExpressionWithValue(container, "TOP" + labbr);
			} else {
				this.replaceExpressionWithValue(container, labbr);
			}
		}
	}

	@Override
	public String getExpressionKey() {
		return TopLocationAbbreviationExpression.KEY;
	}
}
