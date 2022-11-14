
package org.generationcp.middleware.ruleengine.naming.deprecated.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;

import java.util.List;

@Deprecated
public interface DeprecatedExpression {

	void apply(List<StringBuilder> values, DeprecatedAdvancingSource source, final String capturedText);

	String getExpressionKey();
}
