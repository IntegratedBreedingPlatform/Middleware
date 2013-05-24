package org.generationcp.middleware.v2.domain;

public class DatasetValues {

	private String name;
	
	private String description;
	
	private DataSetType type;
	
	private VariableList variables;
	
	public DatasetValues() { }
	
	public DatasetValues(String name, String description, DataSetType type) {
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(DataSetType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public DataSetType getType() {
		return type;
	}

	public VariableList getVariables() {
		return variables;
	}
	
	public void addVariable(Variable variable) {
		if (variables == null) {
			variables = new VariableList();
		}
		variables.add(variable);
	}

	public void setVariables(VariableList variables) {
		this.variables = variables;
	}
}
