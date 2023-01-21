package org.generationcp.middleware.ruleengine.namingdeprecated.rules;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleExecutionNamespace;

@Deprecated
public abstract class DeprecatedNamingOrderedRule extends OrderedRule<DeprecatedNamingRuleExecutionContext> {

	@Override
	public RuleExecutionNamespace getRuleExecutionNamespace() {
		return RuleExecutionNamespace.NAMING;
	}

}
