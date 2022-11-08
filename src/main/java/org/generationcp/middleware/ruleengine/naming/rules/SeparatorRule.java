
package org.generationcp.middleware.ruleengine.naming.rules;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.naming.service.ProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeparatorRule extends OrderedRule<NamingRuleExecutionContext> {

	public static final String KEY = "Separator";

	@Override
	public Object runRule(NamingRuleExecutionContext context) throws RuleException {
		List<String> input = context.getCurrentData();
		DeprecatedAdvancingSource source = context.getAdvancingSource();

		ProcessCodeService processCodeService = context.getProcessCodeService();
		String separatorExpression = source.getBreedingMethod().getSeparator();

		if (separatorExpression == null) {
			separatorExpression = "";
		}

		for (int i = 0; i < input.size(); i++) {
			// some separator expressions perform operations on the root name, so we replace the current input with the result
			input.set(i, processCodeService.applyProcessCode(input.get(i), separatorExpression, source).get(0));
		}

		context.setCurrentData(input);

		return input;
	}

	@Override
	public String getKey() {
		return SeparatorRule.KEY;
	}
}
