package org.generationcp.middleware.domain.dms;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.util.Debug;

public class PhenotypeExceptionDto {
	
	private String localVariableName;
	
	private String standardVariableName;
	
	private Integer standardVariableId;
	
	private Set<String> validValues;
	
	private Set<String> invalidValues;
	
	public String getLocalVariableName() {
		return localVariableName;
	}

	public void setLocalVariableName(String localVariableName) {
		this.localVariableName = localVariableName;
	}

	public String getStandardVariableName() {
		return standardVariableName;
	}

	public void setStandardVariableName(String standardVariableName) {
		this.standardVariableName = standardVariableName;
	}

	public Integer getStandardVariableId() {
		return standardVariableId;
	}

	public void setStandardVariableId(Integer standardVariableId) {
		this.standardVariableId = standardVariableId;
	}

	public Set<String> getValidValues() {
		return validValues;
	}

	public void setValidValues(Set<String> validValues) {
		this.validValues = validValues;
	}

	public Set<String> getInvalidValues() {
		return invalidValues;
	}

	public void setInvalidValues(Set<String> invalidValues) {
		this.invalidValues = invalidValues;
	}

	public void print(int indent) {
		Debug.println(indent, "Phenotype:[localVariableName=" + localVariableName + 
				", standardVariableName=" + standardVariableName + 
				", standardVariableId=" + standardVariableId + 
				", validValues =" + validValues!=null?validValues.toString():null +
				", invalidValues =" + invalidValues!=null?invalidValues.toString():null + 
				"]");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((standardVariableId == null) ? 0 : standardVariableId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhenotypeExceptionDto other = (PhenotypeExceptionDto) obj;
		if (standardVariableId == null) {
			if (other.standardVariableId != null)
				return false;
		} else if (!standardVariableId.equals(other.standardVariableId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(invalidValues!=null?invalidValues.toString():null);
		builder.append(" are not valid values of ");
		builder.append(localVariableName);
		builder.append(".");
		if(validValues!=null) {
			builder.append(" Valid values are ");
			builder.append(validValues.toString());
			builder.append(".");
		}
		return builder.toString();
	}
	
	

}
