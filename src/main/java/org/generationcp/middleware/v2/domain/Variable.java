package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class Variable {

	private VariableType variableType;
	
	private String value;

	public Variable() { }
	
	public Variable(VariableType variableType, String value) {
		this.variableType = variableType;
		this.value = value;
	}
	
	public Variable(VariableType variableType, Double value) {
		this.variableType = variableType;
		if (value != null) {
			this.value = Double.toString(value);
		}
	}

	public Variable(VariableType variableType, Integer value) {
		this.variableType = variableType;
		if (value != null) {
			this.value = Integer.toString(value);
		}
	}

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
		
		if (variableType == null) {
			Debug.println(indent + 3, "VariableType: null");
		}
		else {
		    Debug.println(indent + 3, "VariableType: " + variableType.getId() + " [" + variableType.getLocalName() + "]");
		}
		Debug.println(indent + 3, "Value: " + value);
	}
	
	public int hashCode() {
		return variableType.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Variable)) return false;
		Variable other = (Variable) obj;
		return other.getVariableType().equals(getVariableType()) &&
			   equals(other.getValue(), getValue());
	}
	
	private boolean equals(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == null) return false;
		if (s2 == null) return false;
		return s1.equals(s2);
	}
}
