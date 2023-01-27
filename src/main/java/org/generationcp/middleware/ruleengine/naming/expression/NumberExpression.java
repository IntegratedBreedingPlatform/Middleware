
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NumberExpression extends BaseExpression implements Expression {

	public static final String KEY = "[NUMBER]";

	public NumberExpression() {

	}

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {
		if (advancingSource.isForceUniqueNameGeneration()) {
			for (final StringBuilder container : values) {
				this.replaceExpressionWithValue(container, "(" + (advancingSource.getCurrentMaxSequence() + 1) + ")");

			}

			return;
		}

		if (advancingSource.isBulkingMethod()) {
			for (final StringBuilder container : values) {
				if (advancingSource.getPlantsSelected() != null && advancingSource.getPlantsSelected() > 1) {
					final Integer newValue = advancingSource.getPlantsSelected();
					this.replaceExpressionWithValue(container, newValue != null ? newValue.toString() : "");
				} else {
					this.replaceExpressionWithValue(container, "");
				}
			}
		} else {
			final List<StringBuilder> newNames = new ArrayList<>();
			int startCount = 1;

			if (advancingSource.getCurrentMaxSequence() > -1) {
				startCount = advancingSource.getCurrentMaxSequence() + 1;
			}

			for (final StringBuilder value : values) {
				if (advancingSource.getPlantsSelected() != null && advancingSource.getPlantsSelected() > 0) {

					for (int i = startCount; i < startCount + advancingSource.getPlantsSelected(); i++) {
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
		return NumberExpression.KEY;
	}

}
