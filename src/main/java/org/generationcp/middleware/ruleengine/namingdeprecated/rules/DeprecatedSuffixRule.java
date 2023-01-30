
package org.generationcp.middleware.ruleengine.namingdeprecated.rules;

import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.namingdeprecated.service.DeprecatedProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Deprecated
@Component
public class DeprecatedSuffixRule extends DeprecatedNamingOrderedRule {

	public static final String KEY = "Suffix";

	@Override
	public Object runRule(DeprecatedNamingRuleExecutionContext context) throws RuleException {
		// append a suffix string onto each element of the list - in place

		DeprecatedProcessCodeService processCodeService = context.getProcessCodeService();
		DeprecatedAdvancingSource advancingSource = context.getAdvancingSource();
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
		return DeprecatedSuffixRule.KEY;
	}
}
