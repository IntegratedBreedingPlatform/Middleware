
package org.generationcp.middleware.ruleengine.namingdeprecated.rules;

import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.namingdeprecated.expression.DeprecatedRootNameExpression;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@Component
public class DeprecatedRootNameGeneratorRule extends DeprecatedNamingOrderedRule {

	public static final String KEY = "RootNameGenerator";

	@Override
	public Object runRule(DeprecatedNamingRuleExecutionContext context) throws RuleException {

		DeprecatedRootNameExpression rootNameExpression = new DeprecatedRootNameExpression();
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
		return DeprecatedRootNameGeneratorRule.KEY;
	}
}
