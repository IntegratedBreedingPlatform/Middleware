package org.generationcp.middleware.ruleengine.naming.rules;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleExecutionNamespace;

public abstract class NamingOrderedRule extends OrderedRule<NamingRuleExecutionContext> {

	@Override
	public RuleExecutionNamespace getRuleExecutionNamespace() {
		return RuleExecutionNamespace.NAMING;
	}

}
