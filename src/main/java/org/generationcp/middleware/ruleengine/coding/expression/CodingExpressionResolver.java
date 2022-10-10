package org.generationcp.middleware.ruleengine.coding.expression;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.ruleengine.Expression;
import org.generationcp.middleware.ruleengine.util.ExpressionHelper;
import org.generationcp.middleware.ruleengine.util.ExpressionHelperCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CodingExpressionResolver {

	@Resource
	private CodingExpressionFactory factory;

	public List<String> resolve(final String currentInput, final String processCode, final NamingConfiguration namingConfiguration) {
		final List<String> newNames = new ArrayList<>();

		if (processCode == null) {
			return newNames;
		}

		final List<StringBuilder> builders = new ArrayList<>();
		builders.add(new StringBuilder(currentInput + processCode));

		ExpressionHelper.evaluateExpression(processCode, ExpressionHelper.PROCESS_CODE_PATTERN, new ExpressionHelperCallback() {

			@Override
			public void evaluateCapturedExpression(final String capturedText, final String originalInput, final int start, final int end) {
				final Expression expression = CodingExpressionResolver.this.factory.lookup(capturedText);

				// It's possible for the expression to add more elements to the builders variable.
				if (expression != null) {
					expression.apply(builders, capturedText, namingConfiguration);
				}
			}
		});

		for (final StringBuilder builder : builders) {
			newNames.add(builder.toString());
		}

		return newNames;
	}

}
