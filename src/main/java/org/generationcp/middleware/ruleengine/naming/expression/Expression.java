
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;

import java.util.List;

public interface Expression {

	<T extends AbstractAdvancingSource> void apply(List<StringBuilder> values, T source, final String capturedText);

	String getExpressionKey();

}
