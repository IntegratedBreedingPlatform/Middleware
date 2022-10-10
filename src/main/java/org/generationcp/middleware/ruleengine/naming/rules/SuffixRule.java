
package org.generationcp.middleware.ruleengine.naming.rules;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.naming.service.ProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SuffixRule extends OrderedRule<NamingRuleExecutionContext> {

	public static final String KEY = "Suffix";

	@Override
	public Object runRule(NamingRuleExecutionContext context) throws RuleException {
		// append a suffix string onto each element of the list - in place

		ProcessCodeService processCodeService = context.getProcessCodeService();
		AdvancingSource advancingSource = context.getAdvancingSource();
		String suffix = advancingSource.getBreedingMethod().getSuffix();

		if (suffix == null) {
			suffix = "";
		}

		List<String> input = context.getCurrentData();

		for (int i = 0; i < input.size(); i++) {
			input.set(i, processCodeService.applyProcessCode(input.get(i), suffix, advancingSource).get(0));
		}

		context.setCurrentData(input);

		return input;
	}

	@Override
	public String getKey() {
		return SuffixRule.KEY;
	}
}
