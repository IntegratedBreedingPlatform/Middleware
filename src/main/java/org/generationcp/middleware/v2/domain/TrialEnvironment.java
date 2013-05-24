package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class TrialEnvironment {

	private int id;
	private VariableList variables;
	
	public TrialEnvironment(int id, VariableList variables) {
		this.id = id;
		this.variables = variables;
	}
	
	public int getId() {
		return id;
	}

	public boolean containsValueByLocalName(String localName, String value) {
		return variables.containsValueByLocalName(localName, value);
	}
	
	public VariableList getVariables() {
		return variables;
	}
	
	public void print(int indent) {
		Debug.println(indent, "Trial Environment " + id);
		variables.print(indent + 3);
	}
}
