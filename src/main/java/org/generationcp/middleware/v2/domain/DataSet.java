package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class DataSet {

	private int id;
	
	private String name;
	
	private String description;
	
	private int studyId;
	
	private VariableTypeList variableTypes;

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

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public VariableTypeList getVariableTypes() {
		return variableTypes;
	}

	public void setVariableTypes(VariableTypeList variableTypes) {
		this.variableTypes = variableTypes;
	}

	public void print(int indent) {
		Debug.println(indent, "DataSet: ");
		Debug.println(indent + 3, "Id: " + getId());
		Debug.println(indent + 3, "Name: " + getName());
	    Debug.println(indent + 3, "Description: " + getDescription());
	    Debug.println(indent + 3, "Variable Types: ");
	    variableTypes.print(indent + 6);
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof DataSet)) return false;
		DataSet other = (DataSet) obj;
		return getId() == other.getId();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataSet [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", studyId=");
		builder.append(studyId);
		builder.append(", variableTypes=");
		builder.append(variableTypes);
		builder.append("]");
		return builder.toString();
	}

	
}
