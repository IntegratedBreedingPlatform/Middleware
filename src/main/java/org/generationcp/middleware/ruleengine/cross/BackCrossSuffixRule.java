
package org.generationcp.middleware.ruleengine.cross;

import org.generationcp.middleware.ruleengine.ProcessCodeOrderedRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.PedigreeDataManagerImpl;
import org.generationcp.middleware.ruleengine.RuleExecutionNamespace;
import org.springframework.stereotype.Component;

/**
 * Created by Daniel Villafuerte on 6/6/2015.
 */
@Component
public class BackCrossSuffixRule extends ProcessCodeOrderedRule<CrossingRuleExecutionContext> {

	static final String KEY = "BACKCROSSSUFFIX";
	static final String MALE_RECURRENT_SUFFIX = "M";
	static final String FEMALE_RECURRENT_SUFFIX = "F";
	static final String PROCESS_CODE = "[BC]";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Object runRule(final CrossingRuleExecutionContext context) throws RuleException {
		try {
			final int computation = context.getPedigreeDataManager().calculateRecurrentParent(context.getMaleGid(), context.getFemaleGid());

			String output = context.getCurrentCrossName() == null ? "" : context.getCurrentCrossName();
			if (PedigreeDataManagerImpl.FEMALE_RECURRENT == computation) {
				output += FEMALE_RECURRENT_SUFFIX;
			} else if (PedigreeDataManagerImpl.MALE_RECURRENT == computation) {
				output += MALE_RECURRENT_SUFFIX;
			}

			context.setCurrentCrossName(output);

			return output;
		} catch (MiddlewareQueryException e) {
			throw new RuleException(e.getMessage(), e);
		}

	}

	@Override
	public String getProcessCode() {
		return PROCESS_CODE;
	}

	@Override
	public RuleExecutionNamespace getRuleExecutionNamespace() {
		return RuleExecutionNamespace.NAMING;
	}

}
