package org.generationcp.middleware.v2.domain;

import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.v2.util.Debug;

public class Study {

    private int id;
	
	private String name;
	
	private String description;
	
	private Set<DataSet> dataSets = new HashSet<DataSet>();
	
	private Set<Variable> conditions;

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

	public Set<Variable> getConditions() {
		return conditions;
	}

	public void setConditions(Set<Variable> conditions) {
		this.conditions = conditions;
	}

	public void print(int indent) {
		Debug.println(indent, "Study: ");
		Debug.println(indent + 3, "Id: " + getId());
		Debug.println(indent + 3, "Name: " + getName());
	    Debug.println(indent + 3, "Description: " + getDescription());
	    
	    for (Variable condition : conditions) {
	    	condition.print(indent + 3);
	    }
	    
	    for (DataSet dataSet : dataSets) {
	    	dataSet.print(indent);
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
