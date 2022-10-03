package org.generationcp.middleware.ruleengine.coding;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.coding.expression.CodingExpressionResolver;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CountRule extends OrderedRule<CodingRuleExecutionContext> {

	public static final String KEY = "Count";

	@Resource
	private CodingExpressionResolver codingExpressionResolver;

	@Override
	public Object runRule(final CodingRuleExecutionContext context) throws RuleException {

		final NamingConfiguration namingConfiguration = context.getNamingConfiguration();
		String count = context.getNamingConfiguration().getCount();

		if (count == null) {
			count = "";
		}

		final String resolvedData = codingExpressionResolver.resolve(context.getCurrentData(), count, namingConfiguration).get(0);

		context.setCurrentData(resolvedData);

		return resolvedData;

	}

	@Override
	public String getKey() {
		return CountRule.KEY;
	}
}
