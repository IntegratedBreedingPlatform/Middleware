
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;

import java.util.List;

public interface Expression {

	void apply(List<StringBuilder> values, AdvancingSource source, final String capturedText);

	String getExpressionKey();
}
