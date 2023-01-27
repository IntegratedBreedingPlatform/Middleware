
package org.generationcp.middleware.ruleengine.stockid;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.ruleengine.RuleException;
import org.springframework.stereotype.Component;

/**
 * A rule implementation that defines the logic for processing stock notation within the context of generation of stock IDs
 */
@Component
public class StockNotationNumberRule extends StockIDOrderedRule {

	static final String KEY = "NOTATION";

	@Override
	public Object runRule(final StockIDGenerationRuleExecutionContext context) throws RuleException {

		try {
			final Integer notationNumber =
					context.getLotService().getCurrentNotationNumberForBreederIdentifier(context.getBreederIdentifier()) + 1;
			context.setNotationNumber(notationNumber);
		} catch (MiddlewareQueryException e) {
			throw new RuleException(e.getMessage(), e);
		}

		final Integer currentNotationNumber = context.getNotationNumber();
		context.getStockIDGenerationBuilder().append(currentNotationNumber);

		return currentNotationNumber;
	}

	@Override
	public String getKey() {
		return StockNotationNumberRule.KEY;
	}
}
