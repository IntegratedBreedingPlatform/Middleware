
package org.generationcp.middleware.ruleengine;

public interface Rule<T extends RuleExecutionContext> {

	Object runRule(T context) throws RuleException;

	String getNextRuleStepKey(T context);

	String getKey();

	RuleExecutionNamespace getRuleExecutionNamespace();

}
