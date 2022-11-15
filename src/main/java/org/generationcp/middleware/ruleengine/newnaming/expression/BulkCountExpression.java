
package org.generationcp.middleware.ruleengine.newnaming.expression;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
import org.generationcp.middleware.ruleengine.util.ExpressionHelper;
import org.generationcp.middleware.ruleengine.util.ExpressionHelperCallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCountExpression extends BaseExpression {

	public static final String KEY = "[BCOUNT]";

	public BulkCountExpression() {
	}

	@Override
	public <T extends AbstractAdvancingSource> void apply(final List<StringBuilder> values, final T source, final String capturedText) {
		for (final StringBuilder container : values) {
            final String computedValue;
			if (source.getRootName() != null) {
				final BulkExpressionHelperCallback callback = new BulkExpressionHelperCallback();
				ExpressionHelper.evaluateExpression(source.getRootName(), "-([0-9]*)B", callback);

				final StringBuilder lastBulkCount = callback.getLastBulkCount();

				if (lastBulkCount.length() > 0) {
					computedValue = (Integer.parseInt(lastBulkCount.toString()) + 1) + "B";
				} else {
					computedValue = "-B";
				}
			} else {
                computedValue = "-B";
			}

            this.replaceExpressionWithValue(container, computedValue);
		}
	}

	private static class BulkExpressionHelperCallback implements ExpressionHelperCallback {

		final StringBuilder lastBulkCount = new StringBuilder();

		@Override
		public void evaluateCapturedExpression(final String capturedText, final String originalInput, final int start, final int end) {
			if ("-B".equals(capturedText)) {
				this.lastBulkCount.replace(0, this.lastBulkCount.length(), "1");
			} else {
				final String newCapturedText = capturedText.replaceAll("[-B]*", "");
				if (NumberUtils.isNumber(newCapturedText)) {
					this.lastBulkCount.replace(0, this.lastBulkCount.length(), newCapturedText);
				}
			}
		}

		public StringBuilder getLastBulkCount() {
			return this.lastBulkCount;
		}
	}

	@Override
	public String getExpressionKey() {
		return BulkCountExpression.KEY;
	}
}
