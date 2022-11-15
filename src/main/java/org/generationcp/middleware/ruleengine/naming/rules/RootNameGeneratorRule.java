
package org.generationcp.middleware.ruleengine.naming.rules;

import org.generationcp.middleware.ruleengine.OrderedRule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.naming.expression.RootNameExpression;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RootNameGeneratorRule extends OrderedRule<NamingRuleExecutionContext> {

	public static final String KEY = "RootNameGenerator";

	@Override
	public Object runRule(final NamingRuleExecutionContext context) throws RuleException {

		final RootNameExpression rootNameExpression = new RootNameExpression();
		final AdvancingSource advancingSource = context.getAdvancingSource();

		final List<StringBuilder> builders = new ArrayList<>();
		builders.add(new StringBuilder());
		rootNameExpression.apply(builders, advancingSource, null);

		final List<String> input = context.getCurrentData();

		final String name = builders.get(0).toString();

		input.add(name);

		context.setCurrentData(input);

		return input;
	}

	@Override
	public String getKey() {
		return RootNameGeneratorRule.KEY;
	}
}
