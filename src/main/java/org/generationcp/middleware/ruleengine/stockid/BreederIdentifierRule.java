
package org.generationcp.middleware.ruleengine.stockid;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.springframework.stereotype.Component;

/**
 * A rule implementation that defines the logic for processing breeder identifiers within the context of generation of stock IDs
 */
@Component
public class BreederIdentifierRule extends OrderedRule<StockIDGenerationRuleExecutionContext> {

	static final String KEY = "IDENTIFIER";

	@Override
	public Object runRule(final StockIDGenerationRuleExecutionContext context) throws RuleException {
		if (context.getBreederIdentifier() == null) {
			throw new IllegalStateException("User must have supplied breeder identifier at this point");
		}

		context.getStockIDGenerationBuilder().append(context.getBreederIdentifier());

		return context.getBreederIdentifier();
	}

	@Override
	public String getKey() {
		return BreederIdentifierRule.KEY;
	}
}
