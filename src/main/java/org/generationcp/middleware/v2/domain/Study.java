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
	
	private Set<VariableType> variableTypes;

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

	public Set<VariableType> getVariableTypes() {
		return variableTypes;
	}

	public void setVariableTypes(Set<VariableType> variableTypes) {
		this.variableTypes = variableTypes;
	}

	public VariableList getConditions() {
		return conditions;
	}

	public void setConditions(VariableList conditions) {
		this.conditions = conditions;
	}

	public void print(int indent) {
		Debug.println(indent, "Study: ");
		Debug.println(indent + 3, "Id: " + getId());
		Debug.println(indent + 3, "Name: " + getName());
	    Debug.println(indent + 3, "Description: " + getDescription());
	    
	    Debug.println(indent + 3, "Variable Types: ");
	    for (VariableType variableType : variableTypes) {
	    	variableType.print(indent + 6);
	    }
	    
	    Debug.println(indent + 3, "Conditions: ");
	    for (Variable condition : conditions.getVariables()) {
	    	condition.print(indent + 6);
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
}
