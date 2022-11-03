
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;

import java.util.List;

public interface Expression {

	void apply(List<StringBuilder> values, DeprecatedAdvancingSource source, final String capturedText);

	String getExpressionKey();
}
