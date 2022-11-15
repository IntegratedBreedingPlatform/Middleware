
package org.generationcp.middleware.ruleengine.namingdeprecated.impl;

import org.generationcp.middleware.ruleengine.namingdeprecated.expression.DeprecatedExpression;
import org.generationcp.middleware.ruleengine.namingdeprecated.service.DeprecatedProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.generationcp.middleware.ruleengine.util.ExpressionHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Service
@Transactional
public class DeprecatedProcessCodeServiceImpl implements DeprecatedProcessCodeService {

	@Resource
	private DeprecatedProcessCodeFactory factory;

	@Override
	public List<String> applyProcessCode(final String currentInput, final String processCode, final DeprecatedAdvancingSource source) {
		final List<String> newNames = new ArrayList<String>();

		if (processCode == null) {
			return newNames;
		}

		final List<StringBuilder> builders = new ArrayList<StringBuilder>();
		builders.add(new StringBuilder(currentInput + processCode));

		ExpressionHelper.evaluateExpression(processCode, ExpressionHelper.PROCESS_CODE_PATTERN,
			(capturedText, originalInput, start, end) -> {
				final DeprecatedExpression expression = DeprecatedProcessCodeServiceImpl.this.factory.lookup(capturedText);

				// It's possible for the expression to add more elements to the builders variable.
				if (expression != null) {
					expression.apply(builders, source, capturedText);
				}
			});

		for (final StringBuilder builder : builders) {
			newNames.add(builder.toString());
		}

		return newNames;
	}

}
