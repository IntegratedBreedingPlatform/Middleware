
package org.generationcp.middleware.ruleengine.naming.deprecated.rules;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.naming.deprecated.service.DeprecatedProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Prefix provided in this Rule refers to the prefix of the 'generated and appended generational tail' of a Germplasm name
 * 
 * Therefore the Prefix follows the existing germplasm name, and can appear to look like a suffix.
 * 
 * Rule Chain = RootName-separator-prefix-count-suffix
 * 
 *
 */

@Deprecated
@Component
public class DeprecatedPrefixRule extends OrderedRule<DeprecatedNamingRuleExecutionContext> {

	public static final String KEY = "Prefix";

	@Override
	public Object runRule(DeprecatedNamingRuleExecutionContext context) throws RuleException {

		// append a separator string onto each element of the list - in place
		List<String> input = context.getCurrentData();

		DeprecatedProcessCodeService processCodeService = context.getProcessCodeService();
		DeprecatedAdvancingSource advancingSource = context.getAdvancingSource();
		String prefix = advancingSource.getBreedingMethod().getPrefix();

		if (prefix == null) {
			prefix = "";
		}

		for (int i = 0; i < input.size(); i++) {
			input.set(i, processCodeService.applyProcessCode(input.get(i), prefix, advancingSource).get(0));
		}

		context.setCurrentData(input);

		return input;
	}

	@Override
	public String getKey() {
		return DeprecatedPrefixRule.KEY;
	}
}
