/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.dms;

import java.io.Serializable;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.util.Debug;

/**
 * Contains the details of a Variable - type and value.
 */
public class Variable implements Serializable, Comparable<Variable> {

	private static final long serialVersionUID = 1L;

	private DMSVariableType variableType;

	private String value;

	private Integer phenotypeId;

	private boolean isCustomValue;

	public Variable() {
	}

	public Variable(Integer phenotypeId, DMSVariableType variableType, String value) {
		this.phenotypeId = phenotypeId;
		this.variableType = variableType;
		this.value = value;
		if (variableType == null) {
			throw new RuntimeException();
		}
	}

	public Variable(DMSVariableType variableType, String value) {
		this.variableType = variableType;
		this.value = value;
		if (variableType == null) {
			throw new RuntimeException();
		}
	}

	public Variable(Integer phenotypeId, DMSVariableType variableType, Double value) {
		this.phenotypeId = phenotypeId;
		this.variableType = variableType;
		if (value != null) {
			this.value = Double.toString(value);
		}
		if (variableType == null) {
			throw new RuntimeException();
		}
	}

	public Variable(DMSVariableType variableType, Double value) {
		this.variableType = variableType;
		if (value != null) {
			this.value = Double.toString(value);
		}
		if (variableType == null) {
			throw new RuntimeException();
		}
	}

	public Variable(Integer phenotypeId, DMSVariableType variableType, Integer value) {
		this.phenotypeId = phenotypeId;
		this.variableType = variableType;
		if (value != null) {
			this.value = Integer.toString(value);
		}
		if (variableType == null) {
			throw new RuntimeException();
		}
	}

	public Variable(DMSVariableType variableType, Integer value) {
		this.variableType = variableType;
		if (value != null) {
			this.value = Integer.toString(value);
		}
		if (variableType == null) {
			throw new RuntimeException();
		}
	}

	public DMSVariableType getVariableType() {
		return this.variableType;
	}

	public void setVariableType(DMSVariableType variableType) {
		this.variableType = variableType;
		if (variableType == null) {
			throw new RuntimeException();
		}
	}

	public void setVariableType(DMSVariableType variableType, boolean hasVariableType) {
		this.variableType = variableType;
		if (hasVariableType) {
			if (variableType == null) {
				throw new RuntimeException();
			}
		}
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayValue() {
		String value = this.value;
		if (this.variableType.getStandardVariable().hasEnumerations()) {
			try {
				Enumeration enumeration = this.variableType.getStandardVariable()
						.findEnumerationById(Integer.parseInt(value));
				if (enumeration != null) {
					if (this.variableType.getStandardVariable().getDataType() != null
							&& this.variableType.getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE
									.getId()) {

						// GCP-5536 - get description instead
						value = enumeration.getDescription();
					} else {
						value = enumeration.getName();
					}
				} else if (this.variableType.getStandardVariable().getDataType() != null
						&& this.variableType.getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE
								.getId()) {

					Integer overridingId = null;

					if (this.variableType.getStandardVariable().getOverridenEnumerations() != null) {
						overridingId = this.variableType.getStandardVariable()
								.getOverridenEnumerations().get(Integer.parseInt(value));
					}

					if (overridingId != null) {
						enumeration = this.variableType.getStandardVariable().findEnumerationById(
								overridingId);
					}

					if (enumeration != null) {
						value = enumeration.getDescription();
					}
				}
			} catch (NumberFormatException e) {
				// Ignore, just return the value
			}
		}
		if (value == null) {
			value = "";
		}
		return value;
	}

	public String getActualValue() {
		String value = this.value;
		if (this.variableType.getStandardVariable().hasEnumerations()) {
			try {
				Enumeration enumeration = this.variableType.getStandardVariable()
						.findEnumerationById(Integer.parseInt(value));
				if (enumeration != null) {
					value = enumeration.getName();
				}
			} catch (NumberFormatException e) {
				// Ignore, just return the value
			}
		}
		if (value == null) {
			value = "";
		}
		return value;
	}

	/**
	 * When the name of the enumeration is saved as value, this method returns the id of the enumeration
	 * @return
	 */
	public String getIdValue() {
		String value = this.value;
		StandardVariable standardVariable = this.variableType.getStandardVariable();
		if (value != null && standardVariable.hasEnumerations()) {
			Enumeration enumerationByName = standardVariable.findEnumerationByName(value);
			if (enumerationByName != null) {
				value = String.valueOf(enumerationByName.getId());
			}
		}
		return value;
	}

	public void print(int indent) {
		Debug.println(indent, "Variable: ");

		if (this.variableType == null) {
			Debug.println(indent + 3, "VariableType: null");
		} else {
			Debug.println(indent + 3, "VariableType: " + this.variableType.getId() + " ["
					+ this.variableType.getLocalName() + "]");
		}
		Debug.println(indent + 3, "Value: " + this.value);
	}

	@Override
	public int hashCode() {
		return this.variableType.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Variable)) {
			return false;
		}
		Variable other = (Variable) obj;
		return other.getVariableType().equals(this.getVariableType())
				&& this.stringEquals(other.getValue(), this.getValue());
	}

	private boolean stringEquals(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}
		if (s1 == null) {
			return false;
		}
		if (s2 == null) {
			return false;
		}
		return s1.equals(s2);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Variable [variableType=");
		builder.append(this.variableType);
		builder.append(", value=");
		builder.append(this.value);
		builder.append("]");
		return builder.toString();
	}

	@Override
	// Sort in ascending order by rank
	public int compareTo(Variable compareValue) {
		int compareRank = compareValue.getVariableType().getRank();
		return Integer.valueOf(this.getVariableType().getRank()).compareTo(compareRank);
	}

	public Integer getPhenotypeId() {
		return this.phenotypeId;
	}

	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}

	public boolean isCustomValue() {
		return this.isCustomValue;
	}

	public void setCustomValue(boolean isCustomValue) {
		this.isCustomValue = isCustomValue;
	}

}
