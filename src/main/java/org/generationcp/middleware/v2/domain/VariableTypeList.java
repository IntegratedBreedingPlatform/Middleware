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

	public VariableType findById(TermId termId) {
		return findById(termId.getId());
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

	public void print(int indent) {
		if (variableTypes != null) {
			for (VariableType variableType : variableTypes) {
				variableType.print(indent);
			}
		}
	}

	public VariableTypeList getFactors() {
		VariableTypeList factors = new VariableTypeList();
		if (variableTypes != null) {
			for (VariableType variableType : variableTypes) {
				if (!isVariate(variableType)) {
					factors.add(variableType);
				}
			}
		}
		return factors;
	}

	public VariableTypeList getVariates() {
		VariableTypeList variates = new VariableTypeList();
		if (variableTypes != null) {
			for (VariableType variableType : variableTypes) {
				if (isVariate(variableType)) {
					variates.add(variableType);
				}
			}
		}
		return variates;
	}

	private boolean isVariate(VariableType variableType) {
		return variableType.getStandardVariable().getStoredIn().getId() == TermId.OBSERVATION_VARIATE.getId() ||
			   variableType.getStandardVariable().getStoredIn().getId() == TermId.CATEGORICAL_VARIATE.getId();
	}
	
}
