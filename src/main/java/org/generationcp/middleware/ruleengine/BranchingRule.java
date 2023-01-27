
package org.generationcp.middleware.ruleengine;

import java.util.List;

/**
 * An abstract class used for defining rules that branch out from the pre-defined sequence. Provides an method that is used to ensure that
 * context state is properly maintained even when branching out from the sequence.
 */
public abstract class BranchingRule<T extends OrderedRuleExecutionContext> extends OrderedRule<T> {

	public void prepareContextForBranchingToKey(final T context, final String targetKey) {
		final List<String> executionOrder = context.getExecutionOrder();
		final int currentExecutionIndex = context.getCurrentExecutionIndex();
		final List<String> previousRuleKeys = executionOrder.subList(0, currentExecutionIndex);
		final int index = previousRuleKeys.lastIndexOf(targetKey);

		if (index != -1) {
			context.setCurrentExecutionIndex(index);
		}

	}
}
