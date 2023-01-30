package org.generationcp.middleware.ruleengine.coding;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleExecutionNamespace;

public abstract class CodingOrderedRule extends OrderedRule<CodingRuleExecutionContext> {

	@Override
	public RuleExecutionNamespace getRuleExecutionNamespace() {
		return RuleExecutionNamespace.CODING;
	}

}
