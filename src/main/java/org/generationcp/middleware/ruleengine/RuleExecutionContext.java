
package org.generationcp.middleware.ruleengine;

import java.util.List;

/**
 * Base interface for context objects used to store state for rule executions
 */
public interface RuleExecutionContext {

	List<String> getExecutionOrder();

	Object getRuleExecutionOutput();

	RuleExecutionNamespace getRuleExecutionNamespace();

}
