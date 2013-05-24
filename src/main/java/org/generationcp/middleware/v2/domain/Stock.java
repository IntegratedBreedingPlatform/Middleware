package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class Stock {

	private int id;
	private VariableList variables;
	
	public Stock(int id, VariableList variables) {
		this.id = id;
		this.variables = variables;
	}
	
	public int getId() {
		return id;
	}

	public boolean containsValueByLocalName(String localName, String value) {
		return variables.containsValueByLocalName(localName, value);
	}

	public void print(int indent) {
		Debug.println(indent, "Stock " + id);
		variables.print(indent + 3);
	}
}
