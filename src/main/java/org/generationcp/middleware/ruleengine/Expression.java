package org.generationcp.middleware.ruleengine;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;

import java.util.List;

public interface Expression {

	void apply(List<StringBuilder> values, String capturedText, NamingConfiguration namingConfiguration);

	String getExpressionKey();

}
