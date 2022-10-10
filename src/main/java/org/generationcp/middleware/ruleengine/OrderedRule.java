
package org.generationcp.middleware.ruleengine;

import java.util.List;

/**
 * An abstract class that represent rules that are expected to be executed in a pre-defined sequence. Defines an implementation used by the
 * rule engine for retrieving the next rule within the sequence
 */
public abstract class OrderedRule<T extends OrderedRuleExecutionContext> implements Rule<T> {

	@Override
	public String getNextRuleStepKey(T context) {
		List<String> sequenceOrder = context.getExecutionOrder();
		int executionIndex = context.getCurrentExecutionIndex();

		// increment to the next rule in the sequence
		executionIndex++;
		if (executionIndex < sequenceOrder.size()) {
			String nextKey = sequenceOrder.get(executionIndex);
			context.setCurrentExecutionIndex(executionIndex);
			return nextKey;
		} else {
			return null;
		}
	}
}
