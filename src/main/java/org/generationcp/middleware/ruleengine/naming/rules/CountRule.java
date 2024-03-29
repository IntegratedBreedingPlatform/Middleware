
package org.generationcp.middleware.ruleengine.naming.rules;

import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.naming.service.ProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CountRule extends NamingOrderedRule {

	public static final String KEY = "Count";
	public static final String DEFAULT_COUNT = "[NUMBER]";

	@Override
	public Object runRule(final NamingRuleExecutionContext context) throws RuleException {
		// create counts first - we need a list in case we have a sequence

		final ProcessCodeService service = context.getProcessCodeService();
		final AdvancingSource source = context.getAdvancingSource();

		final List<String> input = context.getCurrentData();

		final List<String> counts = new ArrayList<>();

		for (final String currentInput : input) {
			counts.addAll(service.applyProcessCode(currentInput, source.getBreedingMethod().getCount(), source));
		}

		// store current data in temp before overwriting it with count data, so that it can be restored for another try later on
		context.setTempData(context.getCurrentData());

		if (!counts.isEmpty()) {
			// place the processed name data with count information as current rule execution output
			context.setCurrentData(counts);
			return counts;
		} else {
			return input;
		}

	}

	@Override
	public String getKey() {
		return CountRule.KEY;
	}
}
