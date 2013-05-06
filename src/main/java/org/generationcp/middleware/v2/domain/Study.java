package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class Study {

    private int id;
	
	private String name;
	
	private String description;
	
	private VariableList conditions;
	
	private VariableTypeList conditionVariableTypes;

	private VariableList constants;
	
	private VariableTypeList constantVariableTypes;
	
	public Study(){
	}

	public Study(int id, String name, String description, 
			VariableList conditions,
			VariableTypeList conditionVariableTypes, 
			VariableList constants,
			VariableTypeList constantVariableTypes) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.conditions = conditions;
		this.conditionVariableTypes = conditionVariableTypes;
		this.constants = constants;
		this.constantVariableTypes = constantVariableTypes;
	}

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
	
	public VariableList getConditions() {
		return conditions;
	}

	public void setConditions(VariableList conditions) {
		this.conditions = conditions;
	}


	public VariableTypeList getConditionVariableTypes() {
		return conditionVariableTypes;
	}

	public void setConditionVariableTypes(VariableTypeList conditionVariableTypes) {
		this.conditionVariableTypes = conditionVariableTypes;
	}
	public VariableList getConstants() {
		return constants;
	}

	public void setConstants(VariableList constants) {
		this.constants = constants;
	}
	
	public VariableTypeList getConstantVariableTypes() {
		return constantVariableTypes;
	}

	public void setConstantVariableTypes(VariableTypeList constantVariableTypes) {
		this.constantVariableTypes = constantVariableTypes;
	}
	
	public void print(int indent) {
		Debug.println(indent, "Study: ");
		Debug.println(indent + 3, "Id: " + getId());
		Debug.println(indent + 3, "Name: " + getName());
	    Debug.println(indent + 3, "Description: " + getDescription());
	    
	    Debug.println(indent + 3, "Conditions: ");
	    for (Variable condition : conditions.getVariables()) {
	    	condition.print(indent + 6);
	    }
	    
	    Debug.println(indent + 3, "Condition Variable Types: ");
	    for (VariableType variableType : conditionVariableTypes.getVariableTypes()) {
	    	variableType.print(indent + 6);
	    }

	    Debug.println(indent + 3, "Constants: ");
	    for (Variable constant : constants.getVariables()) {
	    	constant.print(indent + 6);
	    }
	    
	    Debug.println(indent + 3, "Constant Variable Types: ");
	    for (VariableType variableType : constantVariableTypes.getVariableTypes()) {
	    	variableType.print(indent + 6);
	    }

	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Study)) return false;
		Study other = (Study) obj;
		return getId() == other.getId();
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
		builder.append(", conditions=");
		builder.append(conditions);
		builder.append(", conditionVariableTypes=");
		builder.append(conditionVariableTypes);
		builder.append(", constants=");
		builder.append(constants);
		builder.append(", constantVariableTypes=");
		builder.append(constantVariableTypes);
		builder.append("]");
		return builder.toString();
	}
}
