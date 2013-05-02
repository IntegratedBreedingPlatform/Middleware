package org.generationcp.middleware.v2.domain;

import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.v2.util.Debug;

public class Study {

    private int id;
	
	private String name;
	
	private String description;
	
	private Set<DataSet> dataSets = new HashSet<DataSet>();
	
	private VariableList conditions;
	
	private VariableTypeList variableTypes;
	
	private VariableList constants;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Set<DataSet> getDataSets() {
		return dataSets;
	}

	public void setDataSets(Set<DataSet> dataSets) {
		this.dataSets = dataSets;
	}

	public VariableTypeList getVariableTypes() {
		return variableTypes;
	}

	public void setVariableTypes(VariableTypeList variableTypes) {
		this.variableTypes = variableTypes;
	}

	public VariableList getConditions() {
		return conditions;
	}

	public void setConditions(VariableList conditions) {
		this.conditions = conditions;
	}

	public VariableList getConstants() {
		return constants;
	}

	public void setConstants(VariableList constants) {
		this.constants = constants;
	}
	
	public void print(int indent) {
		Debug.println(indent, "Study: ");
		Debug.println(indent + 3, "Id: " + getId());
		Debug.println(indent + 3, "Name: " + getName());
	    Debug.println(indent + 3, "Description: " + getDescription());
	    
	    Debug.println(indent + 3, "Variable Types: ");
	    for (VariableType variableType : variableTypes.getVariableTypes()) {
	    	variableType.print(indent + 6);
	    }
	    
	    Debug.println(indent + 3, "Conditions: ");
	    for (Variable condition : conditions.getVariables()) {
	    	condition.print(indent + 6);
	    }
	    
	    Debug.println(indent + 3, "Constants: ");
	    for (Variable constant : constants.getVariables()) {
	    	constant.print(indent + 6);
	    }
	    
	    Debug.println(indent + 3, "Data Sets: ");
	    for (DataSet dataSet : dataSets) {
	    	dataSet.print(indent + 6);
	    }
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Study)) return false;
		Study other = (Study) obj;
		return getId() == other.getId();
	}

	public void addDataSet(DataSet dataSet) {
		dataSets.add(dataSet);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Study [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", dataSets=");
		builder.append(dataSets);
		builder.append(", conditions=");
		builder.append(conditions);
		builder.append(", variableTypes=");
		builder.append(variableTypes);
		builder.append(", constants=");
		builder.append(constants);
		builder.append("]");
		return builder.toString();
	}
	
	
}
