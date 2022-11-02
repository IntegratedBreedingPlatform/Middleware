package org.generationcp.middleware.ruleengine.coding;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.coding.expression.CodingExpressionResolver;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CodingPrefixRule extends OrderedRule<CodingRuleExecutionContext> {

	public static final String KEY = "Prefix";

	@Resource
	private CodingExpressionResolver codingExpressionResolver;

	@Override
	public Object runRule(final CodingRuleExecutionContext context) throws RuleException {

		final NamingConfiguration namingConfiguration = context.getNamingConfiguration();
		String prefix = context.getNamingConfiguration().getPrefix();

		if (prefix == null) {
			prefix = "";
		}

		final String resolvedData = codingExpressionResolver.resolve(context.getCurrentData(), prefix, namingConfiguration).get(0);

		context.setCurrentData(resolvedData);

		return resolvedData;

	}

	@Override
	public String getKey() {
		return CodingPrefixRule.KEY;
	}
}
