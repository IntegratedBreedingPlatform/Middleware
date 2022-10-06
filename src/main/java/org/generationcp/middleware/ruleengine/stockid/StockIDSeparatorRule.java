
package org.generationcp.middleware.ruleengine.stockid;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.springframework.stereotype.Component;

/**
 * A rule implementation that defines the logic for processing separators within the context of generation of stock IDs
 */
@Component
public class StockIDSeparatorRule extends OrderedRule<StockIDGenerationRuleExecutionContext> {

	static final String KEY = "SEPARATOR";
	public static final String DEFAULT_SEPARATOR = "-";

	@Override
	public Object runRule(final StockIDGenerationRuleExecutionContext context) throws RuleException {
		final String separator =
				StringUtils.isEmpty(context.getSeparator()) ? StockIDSeparatorRule.DEFAULT_SEPARATOR : context.getSeparator();
		context.setSeparator(separator);

		context.getStockIDGenerationBuilder().append(separator);

		return separator;
	}

	@Override
	public String getKey() {
		return StockIDSeparatorRule.KEY;
	}
}
