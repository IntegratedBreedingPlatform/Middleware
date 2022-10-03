package org.generationcp.middleware.ruleengine;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;

import java.util.List;

public interface Expression {

	public void apply(List<StringBuilder> values, final String capturedText, final NamingConfiguration namingConfiguration);

	public String getExpressionKey();

}
