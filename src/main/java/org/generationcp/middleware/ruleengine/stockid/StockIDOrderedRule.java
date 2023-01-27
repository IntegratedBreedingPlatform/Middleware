package org.generationcp.middleware.ruleengine.stockid;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleExecutionNamespace;

public abstract class StockIDOrderedRule extends OrderedRule<StockIDGenerationRuleExecutionContext> {

	@Override
	public RuleExecutionNamespace getRuleExecutionNamespace() {
		return RuleExecutionNamespace.STOCK;
	}

}
