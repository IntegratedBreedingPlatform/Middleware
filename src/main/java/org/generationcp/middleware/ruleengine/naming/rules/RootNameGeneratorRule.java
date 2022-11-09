
package org.generationcp.middleware.ruleengine.naming.rules;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.naming.expression.RootNameExpression;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RootNameGeneratorRule extends OrderedRule<NamingRuleExecutionContext> {

	public static final String KEY = "RootNameGenerator";

	@Override
	public Object runRule(NamingRuleExecutionContext context) throws RuleException {

		RootNameExpression rootNameExpression = new RootNameExpression();
		DeprecatedAdvancingSource advancingSource = context.getAdvancingSource();

		List<StringBuilder> builders = new ArrayList<>();
		builders.add(new StringBuilder());
		rootNameExpression.apply(builders, advancingSource, null);

		List<String> input = context.getCurrentData();

		String name = builders.get(0).toString();

		input.add(name);

		context.setCurrentData(input);

		return input;
	}

	@Override
	public String getKey() {
		return RootNameGeneratorRule.KEY;
	}
}
