package org.generationcp.middleware.ruleengine;

public enum RuleExecutionNamespace {

	CODING("coding"),
	NAMING("naming"),
	STOCK("stockid");

	private final String name;

	RuleExecutionNamespace(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
