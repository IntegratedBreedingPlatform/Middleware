package org.generationcp.middleware.ruleengine.coding;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.coding.expression.CodingExpressionResolver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CodingSuffixRule extends CodingOrderedRule {

	public static final String KEY = "Suffix";

	@Resource
	private CodingExpressionResolver codingExpressionResolver;

	@Override
	public Object runRule(final CodingRuleExecutionContext context) throws RuleException {

		final NamingConfiguration namingConfiguration = context.getNamingConfiguration();
		String suffix = context.getNamingConfiguration().getSuffix();

		if (suffix == null) {
			suffix = "";
		}

		final String resolvedData = codingExpressionResolver.resolve(context.getCurrentData(), suffix, namingConfiguration).get(0);

		context.setCurrentData(resolvedData);

		return resolvedData;

	}

	@Override
	public String getKey() {
		return CodingSuffixRule.KEY;
	}
}
