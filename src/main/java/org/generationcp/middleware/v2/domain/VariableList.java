package org.generationcp.middleware.v2.domain;

import java.util.ArrayList;
import java.util.List;

public class VariableList {

	private List<Variable> variables = new ArrayList<Variable>();
	
	public void add(Variable variable) {
		variables.add(variable);
	}

	public Variable findById(int id) {
		if (variables != null) {
			for (Variable variable : variables) {
				if (variable.getVariableType().getId() == id) {
					return variable;
				}
			}
		}
		return null;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableList [variables=");
		builder.append(variables);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
