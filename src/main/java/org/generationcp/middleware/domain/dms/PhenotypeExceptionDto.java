
package org.generationcp.middleware.domain.dms;

import java.util.Set;

import org.generationcp.middleware.util.Debug;

public class PhenotypeExceptionDto {

	private String localVariableName;

	private String standardVariableName;

	private Integer standardVariableId;

	private Set<String> validValues;

	private Set<String> invalidValues;

	public String getLocalVariableName() {
		return this.localVariableName;
	}

	public void setLocalVariableName(String localVariableName) {
		this.localVariableName = localVariableName;
	}

	public String getStandardVariableName() {
		return this.standardVariableName;
	}

	public void setStandardVariableName(String standardVariableName) {
		this.standardVariableName = standardVariableName;
	}

	public Integer getStandardVariableId() {
		return this.standardVariableId;
	}

	public void setStandardVariableId(Integer standardVariableId) {
		this.standardVariableId = standardVariableId;
	}

	public Set<String> getValidValues() {
		return this.validValues;
	}

	public void setValidValues(Set<String> validValues) {
		this.validValues = validValues;
	}

	public Set<String> getInvalidValues() {
		return this.invalidValues;
	}

	public void setInvalidValues(Set<String> invalidValues) {
		this.invalidValues = invalidValues;
	}

	public void print(int indent) {
		Debug.println(
				indent,
				"Phenotype:[localVariableName=" + this.localVariableName + ", standardVariableName=" + this.standardVariableName
						+ ", standardVariableId=" + this.standardVariableId + ", validValues =" + this.validValues != null ? this.validValues
						.toString() : null + ", invalidValues =" + this.invalidValues != null ? this.invalidValues.toString() : null + "]");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.standardVariableId == null ? 0 : this.standardVariableId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		PhenotypeExceptionDto other = (PhenotypeExceptionDto) obj;
		if (this.standardVariableId == null) {
			if (other.standardVariableId != null) {
				return false;
			}
		} else if (!this.standardVariableId.equals(other.standardVariableId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.invalidValues != null ? this.invalidValues.toString() : null);
		builder.append(" are not valid values of ");
		builder.append(this.localVariableName);
		builder.append(" (Standard Variable: ");
		builder.append(this.standardVariableName);
		builder.append(") .");
		if (this.validValues != null) {
			builder.append(" Valid values are ");
			builder.append(this.validValues.toString());
			builder.append(".");
		} else {
			builder.append(" Valid values are not defined. Please update the variable using Manage Ontologies tool.");
		}
		return builder.toString();
	}

}
