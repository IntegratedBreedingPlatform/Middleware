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

package org.generationcp.middleware.domain.etl;

import java.util.List;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.util.Debug;

public class MeasurementVariable {

	private int termId;

	private String name;

	private String description;

	private String scale;

	private String method;

	private String property;

	private String dataType;

	private String value;

	private String label;

	private boolean isFactor;

	private Integer dataTypeId;

	private List<ValueReference> possibleValues;

	private String possibleValuesString;

	private Double minRange;

	private Double maxRange;

	private boolean required;

	private String treatmentLabel;

	private Operation operation;
	
	private PhenotypicType role;

	public MeasurementVariable() {
	}

	public MeasurementVariable(String name, String description, String scale, String method, String property, String dataType,
			String value, String label) {
		this.name = name;
		this.description = description;
		this.scale = scale;
		this.method = method;
		this.property = property;
		this.dataType = dataType;
		this.value = value;
		this.label = label;
		this.required = false;
	}
	
	public MeasurementVariable(String name, String description, String scale, String method, String property, String dataType,
			String value, String label, PhenotypicType role) {
		this.name = name;
		this.description = description;
		this.scale = scale;
		this.method = method;
		this.property = property;
		this.dataType = dataType;
		this.value = value;
		this.label = label;
		this.required = false;
		this.role = role;
	}

	public MeasurementVariable(int termId, String name, String description, String scale, String method, String property, String dataType,
			String value, String label) {
		this(name, description, scale, method, property, dataType, value, label);
		this.termId = termId;
	}

	public MeasurementVariable(String name, String description, String scale, String method, String property, String dataType,
			String value, String label, Double minRange, Double maxRange) {
		this(name, description, scale, method, property, dataType, value, label);
		this.minRange = minRange;
		this.maxRange = maxRange;
	}
	
	public MeasurementVariable(String name, String description, String scale, String method, String property, String dataType,
			String value, String label, Double minRange, Double maxRange, PhenotypicType role) {
		this(name, description, scale, method, property, dataType, value, label, role);
		this.minRange = minRange;
		this.maxRange = maxRange;
	}

	public MeasurementVariable(int termId, String name, String description, String scale, String method, String property, String dataType,
			String value, String label, Double minRange, Double maxRange) {
		this(name, description, scale, method, property, dataType, value, label, minRange, maxRange);
		this.termId = termId;
	}
	
	public MeasurementVariable(int termId, String name, String description, String scale, String method, String property, String dataType,
			String value, String label, Double minRange, Double maxRange, PhenotypicType role) {
		this(name, description, scale, method, property, dataType, value, label, minRange, maxRange, role);
		this.termId = termId;
		
	}

	public int getTermId() {
		return this.termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getScale() {
		return this.scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getLabel() {
		if (this.label == null) {
			this.label = "";
		} 
		if("".equalsIgnoreCase(this.label) && this.role != null){
			this.label = this.role.getLabelList().get(0);
	    }
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeasurementVariable [termId=");
		builder.append(this.termId);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", scale=");
		builder.append(this.scale);
		builder.append(", method=");
		builder.append(this.method);
		builder.append(", property=");
		builder.append(this.property);
		builder.append(", dataType=");
		builder.append(this.dataType);
		builder.append(", value=");
		builder.append(this.value);
		builder.append(", label=");
		builder.append(this.label);
		builder.append(", isFactor=");
		builder.append(this.isFactor);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "MeasurementVariable: ");
		Debug.println(indent + 3, "Name: " + this.name);
		Debug.println(indent + 3, "Description: " + this.description);
		Debug.println(indent + 3, "Scale: " + this.scale);
		Debug.println(indent + 3, "Method: " + this.method);
		Debug.println(indent + 3, "Property: " + this.property);
		Debug.println(indent + 3, "Data Type: " + this.dataType);
		Debug.println(indent + 3, "Value: " + this.value);
		Debug.println(indent + 3, "Label: " + this.label);
	}

	public boolean isFactor() {
		return this.isFactor;
	}

	public void setFactor(boolean isFactor) {
		this.isFactor = isFactor;
	}
	
	/**
	 * @return the dataTypeId
	 */
	public Integer getDataTypeId() {
		return this.dataTypeId;
	}

	/**
	 * @param dataTypeId the dataTypeId to set
	 */
	public void setDataTypeId(Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}

	/**
	 * @return the possibleValues
	 */
	public List<ValueReference> getPossibleValues() {
		return this.possibleValues;
	}

	/**
	 * @param possibleValues the possibleValues to set
	 */
	public void setPossibleValues(List<ValueReference> possibleValues) {

		this.possibleValues = possibleValues;

		StringBuilder sb = new StringBuilder();

		if (possibleValues != null) {
			for (ValueReference ref : possibleValues) {
				sb.append(ref.getDescription() + "|");
			}
		}

		this.setPossibleValuesString(sb.toString());
	}

	/**
	 * @return the minRange
	 */
	public Double getMinRange() {
		return this.minRange;
	}

	/**
	 * @param minRange the minRange to set
	 */
	public void setMinRange(Double minRange) {
		this.minRange = minRange;
	}

	/**
	 * @return the maxRange
	 */
	public Double getMaxRange() {
		return this.maxRange;
	}

	/**
	 * @param maxRange the maxRange to set
	 */
	public void setMaxRange(Double maxRange) {
		this.maxRange = maxRange;
	}

	public String getDataTypeDisplay() {
		// datatype ids: 1120, 1125, 1128, 1130
		if (this.dataTypeId == null && this.dataType != null) {
			return this.dataType;
		} else if (this.dataTypeId == TermId.CHARACTER_VARIABLE.getId() || this.dataTypeId == TermId.TIMESTAMP_VARIABLE.getId()
				|| this.dataTypeId == TermId.CHARACTER_DBID_VARIABLE.getId() || this.dataTypeId == TermId.CATEGORICAL_VARIABLE.getId()) {
			return "C";
		} else {
			return "N";
		}
	}

	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getTreatmentLabel() {
		return this.treatmentLabel;
	}

	public void setTreatmentLabel(String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
	}
	
	
	public PhenotypicType getRole() {
		return role;
	}

	
	public void setRole(PhenotypicType role) {
		this.role = role;
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return this.operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public MeasurementVariable copy() {
		MeasurementVariable var = new MeasurementVariable();
		var.setTermId(this.termId);
		var.setName(this.name);
		var.setDescription(this.description);
		var.setScale(this.scale);
		var.setMethod(this.method);
		var.setProperty(this.property);
		var.setDataType(this.dataType);
		var.setValue(this.value);
		var.setLabel(this.label);
		var.setRole(this.role);
		var.setFactor(this.isFactor);
		var.setDataTypeId(this.dataTypeId);
		var.setPossibleValues(this.possibleValues);
		var.setMinRange(this.minRange);
		var.setMaxRange(this.maxRange);
		var.setRequired(this.required);
		var.setTreatmentLabel(this.treatmentLabel);
		var.setOperation(this.operation);
		return var;
	}

	public String getPossibleValuesString() {
		return this.possibleValuesString;
	}

	public void setPossibleValuesString(String possibleValuesString) {
		this.possibleValuesString = possibleValuesString;
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
		MeasurementVariable other = (MeasurementVariable) obj;
		if (this.termId != other.termId) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.termId;
		return result;
	}

}
