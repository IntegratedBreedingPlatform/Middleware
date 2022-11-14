
package org.generationcp.middleware.ruleengine.naming.deprecated.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@Component
public class DeprecatedNumberExpression extends DeprecatedBaseExpression implements DeprecatedExpression {

	public static final String KEY = "[NUMBER]";

	public DeprecatedNumberExpression() {

	}

	@Override
	public void apply(final List<StringBuilder> values, final DeprecatedAdvancingSource source, final String capturedText) {
		if (source.isForceUniqueNameGeneration()) {
			for (final StringBuilder container : values) {
				this.replaceExpressionWithValue(container, "(" + (source.getCurrentMaxSequence() + 1) + ")");

			}

			return;
		}

		if (source.isBulk()) {
			for (final StringBuilder container : values) {
				if (source.getPlantsSelected() != null && source.getPlantsSelected() > 1) {
					final Integer newValue = source.getPlantsSelected();
					this.replaceExpressionWithValue(container, newValue != null ? newValue.toString() : "");
				} else {
					this.replaceExpressionWithValue(container, "");
				}
			}
		} else {
			final List<StringBuilder> newNames = new ArrayList<>();
			int startCount = 1;

			if (source.getCurrentMaxSequence() > -1) {
				startCount = source.getCurrentMaxSequence() + 1;
			}

			for (final StringBuilder value : values) {
				if (source.getPlantsSelected() != null && source.getPlantsSelected() > 0) {

					for (int i = startCount; i < startCount + source.getPlantsSelected(); i++) {
						final StringBuilder newName = new StringBuilder(value);
						this.replaceExpressionWithValue(newName, String.valueOf(i));
						newNames.add(newName);
					}
				} else {
					this.replaceExpressionWithValue(value, "");
					newNames.add(value);
				}
			}

			values.clear();
			values.addAll(newNames);
		}
	}

	@Override
	public String getExpressionKey() {
		return DeprecatedNumberExpression.KEY;
	}

}
