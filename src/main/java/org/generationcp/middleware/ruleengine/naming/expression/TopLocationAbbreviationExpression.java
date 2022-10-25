
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TopLocationAbbreviationExpression extends BaseExpression {

	public static final String KEY = "[TLABBR]";

	public TopLocationAbbreviationExpression() {
	}

	@Override
	public <T extends AbstractAdvancingSource> void apply(final List<StringBuilder> values, final T source, final String capturedText) {
		for (final StringBuilder container : values) {
			final String rootName = source.getRootName();
			final String labbr = source.getLocationAbbreviation() != null ? source.getLocationAbbreviation() : "";
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
