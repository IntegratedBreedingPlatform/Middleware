package org.generationcp.middleware.v2.domain;

import java.util.ArrayList;
import java.util.List;

public class VariableTypeList {

	private List<VariableType> variableTypes = new ArrayList<VariableType>();
	
	public void add(VariableType variableType) {
		variableTypes.add(variableType);
	}
	
	public void addAll(VariableTypeList variableTypes) {
		this.variableTypes.addAll(variableTypes.getVariableTypes());
	}

	public VariableType findById(int id) {
		if (variableTypes != null) {
			for (VariableType variableType : variableTypes) {
				if (variableType.getId() == id) {
					return variableType;
				}
			}
		}
		return null;
	}

	public List<VariableType> getVariableTypes() {
		return variableTypes;
	}

	public void setVariableTypes(List<VariableType> variableTypes) {
		this.variableTypes = variableTypes;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableTypeList [variableTypes=");
		builder.append(variableTypes);
		builder.append("]");
		return builder.toString();
	}

	
	
}
