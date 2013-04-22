package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class Variable {

	private VariableType variableType;
	
	private String value;

	public VariableType getVariableType() {
		return variableType;
	}

	public void setVariableType(VariableType variableType) {
		this.variableType = variableType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void print(int indent) {
		Debug.println(indent, "Variable: " );
		variableType.print(indent + 3);
		Debug.println(indent + 3, "Value: " + value);
	}
}
