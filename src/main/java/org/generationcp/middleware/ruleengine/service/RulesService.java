
package org.generationcp.middleware.ruleengine.service;

import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.RuleExecutionContext;

public interface RulesService {

	public Object runRules(RuleExecutionContext context) throws RuleException;
}
