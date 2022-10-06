
package org.generationcp.middleware.ruleengine;

import java.util.List;

/**
 * An abstract class used to define common state regarding sequential rule execution
 */
public abstract class OrderedRuleExecutionContext implements RuleExecutionContext {

	private final List<String> executionOrder;
	private int executionIndex;

	public OrderedRuleExecutionContext(List<String> executionOrder) {
		this.executionOrder = executionOrder;
	}

	public int getCurrentExecutionIndex() {
		return this.executionIndex;
	}

	@Override
	public List<String> getExecutionOrder() {
		return this.executionOrder;
	}

	public void setCurrentExecutionIndex(int index) {
		this.executionIndex = index;
	}
}
