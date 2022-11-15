
package org.generationcp.middleware.ruleengine.newnaming.expression;

import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BracketsExpression extends BaseExpression {

	public static final String KEY = "[BRACKETS]";

	public BracketsExpression() {
	}

	@Override
	public <T extends AbstractAdvancingSource> void apply(final List<StringBuilder> values, final T source, final String capturedText) {
		for (final StringBuilder container : values) {

			final String newRootName = source.getRootName();

			if (source.getRootNameType() != null && this.isCrossNameType(source.getRootNameType())) {

				// if root name already has parentheses
				if (newRootName.charAt(0) != '(' || newRootName.charAt(newRootName.length() - 1) != ')') {
					this.replaceExpressionWithValue(container, ")");
					container.insert(0, "(");
				}
			} else {
				this.replaceExpressionWithValue(container, "");
			}
		}
	}

	protected boolean isCrossNameType(final Integer nameTypeId) {
		return GermplasmNameType.CROSS_NAME.getUserDefinedFieldID() == nameTypeId
				|| GermplasmNameType.ALTERNATE_CROSS_NAME.getUserDefinedFieldID() == nameTypeId;
	}

	@Override
	public String getExpressionKey() {
		return BracketsExpression.KEY;
	}
}
