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

import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.util.Debug;

import java.util.List;

public class MeasurementVariable {

	private int termId;

	private String name;

	/**
	 * Some of the old code may still fill the name with the alias
	 * The explicit field is preferred in the new code
	 */
	private String alias;

	private String description;

	private String scale;

	private Integer scaleId;

	private String method;

	private String property;

	private String dataType;

	private String value;

	private String label;

	private boolean isFactor;

	private Integer dataTypeId;

	private List<ValueReference> possibleValues;

	private String possibleValuesString;

	/**
	 * For consistency with the old code, we keep minRange and maxRange to be either scale range when it's present or variable range
	 */
	private Double minRange;

	private Double maxRange;

	private Double scaleMinRange;

	private Double scaleMaxRange;

	private Double variableMinRange;

	private Double variableMaxRange;

	private boolean required;

	private String treatmentLabel;

	private Operation operation;

	private PhenotypicType role;

	private VariableType variableType;

	private FormulaDto formula;

	private String cropOntology;

	private boolean isSystemVariable;

	public MeasurementVariable() {
	}

	public MeasurementVariable(final Integer termId) {
		this.termId = termId;
	}

	public MeasurementVariable(final String name, final Integer termId, final Integer dataTypeId) {
		this.name = name;
		this.termId = termId;
		this.dataTypeId = dataTypeId;
	}

	public MeasurementVariable(final String name, final String description, final Integer termId, final Integer dataTypeId, final String alias, boolean isFactor) {
		this.name = name;
		this.description = description;
		this.termId = termId;
		this.dataTypeId = dataTypeId;
		this.alias = alias;
		this.isFactor = isFactor;
	}

	public MeasurementVariable(final String name, final String description, final String scale, final String method, final String property, final String dataType,
			final String value, final String label) {
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

	public MeasurementVariable(final String name, final String description, final String scale, final String method, final String property, final String dataType,
			final String value, final String label, final PhenotypicType role) {
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

	public MeasurementVariable(
		final int termId, final String name, final String description, final String scale, final String method, final String property, final String dataType,
			final String value, final String label) {
		this(name, description, scale, method, property, dataType, value, label);
		this.termId = termId;
	}

	public MeasurementVariable(final String name, final String description, final String scale, final String method, final String property, final String dataType,
			final String value, final String label, final Double minRange, final Double maxRange) {
		this(name, description, scale, method, property, dataType, value, label);
		this.minRange = minRange;
		this.maxRange = maxRange;
	}

	public MeasurementVariable(final String name, final String description, final String scale, final String method, final String property, final String dataType,
			final String value, final String label, final Double minRange, final Double maxRange, final PhenotypicType role) {
		this(name, description, scale, method, property, dataType, value, label, role);
		this.minRange = minRange;
		this.maxRange = maxRange;
	}

	public MeasurementVariable(
		final int termId, final String name, final String description, final String scale, final String method, final String property, final String dataType,
			final String value, final String label, final Double minRange, final Double maxRange) {
		this(name, description, scale, method, property, dataType, value, label, minRange, maxRange);
		this.termId = termId;
	}

	public MeasurementVariable(
		final int termId, final String name, final String description, final String scale, final String method, final String property, final String dataType,
			final String value, final String label, final Double minRange, final Double maxRange, final PhenotypicType role) {
		this(name, description, scale, method, property, dataType, value, label, minRange, maxRange, role);
		this.termId = termId;

	}

	public MeasurementVariable(
		final int termId, final String name, final String description, final String scale, final String method, final String property, final String dataType,
			final String value, final String label, final VariableType variableType) {
		this(termId, name, description, scale, method, property, dataType, value, label);
		this.variableType = variableType;
	}

	public int getTermId() {
		return this.termId;
	}

	public void setTermId(final int termId) {
		this.termId = termId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getScale() {
		return this.scale;
	}

	public void setScale(final String scale) {
		this.scale = scale;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(final String property) {
		this.property = property;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(final String dataType) {
		this.dataType = dataType;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public boolean isSystemVariable() {
		return this.isSystemVariable;
	}

	public void setIsSystemVariable(final boolean system) {
		this.isSystemVariable = system;
	}

	public String getLabel() {
		if (this.label == null) {
			this.label = "";
		}
		if ("".equalsIgnoreCase(this.label) && this.role != null && CollectionUtils.isNotEmpty(this.role.getLabelList())) {
			this.label = this.role.getLabelList().get(0);
		}
		return this.label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("MeasurementVariable [termId=");
		builder.append(this.termId);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", isSystemVariable=");
		builder.append(this.isSystemVariable);
		builder.append(", scale=");
		builder.append(this.scale);
		builder.append(", method=");
		builder.append(this.method);
		builder.append(", property=");
		builder.append(this.property);
		builder.append(", dataType=");
		builder.append(this.dataType);
		builder.append(", role=");
		builder.append(this.role);
		builder.append(", value=");
		builder.append(this.value);
		builder.append(", label=");
		builder.append(this.label);
        builder.append(", isFactor=");
        builder.append(this.isFactor);
        builder.append(", dataTypeId=");
        builder.append(this.dataTypeId);
        builder.append(", possibleValues=");
		builder.append(this.possibleValues);
        builder.append(", possibleValuesString=");
        builder.append(this.possibleValuesString);
        builder.append(", minRange=");
        builder.append(this.minRange);
        builder.append(", maxRange=");
        builder.append(this.maxRange);
        builder.append(", required=");
        builder.append(this.required);
        builder.append(", treatmentLabel=");
        builder.append(this.treatmentLabel);
        builder.append(", operation=");
        builder.append(this.operation);
        builder.append(", variableType=");
        builder.append(this.variableType);
		builder.append("]");
		return builder.toString();
	}

	public void print(final int indent) {
		Debug.println(indent, "MeasurementVariable: ");
		Debug.println(indent + 3, "termId: " + this.termId);
		Debug.println(indent + 3, "Name: " + this.name);
		Debug.println(indent + 3, "Description: " + this.description);
		Debug.println(indent + 3, "Scale: " + this.scale);
		Debug.println(indent + 3, "Method: " + this.method);
		Debug.println(indent + 3, "Property: " + this.property);
		Debug.println(indent + 3, "Data Type: " + this.dataType);
		Debug.println(indent + 3, "Value: " + this.value);
		Debug.println(indent + 3, "Label: " + this.label);
		Debug.println(indent + 3, "isFactor: " + this.isFactor);
		Debug.println(indent + 3, "dataTypeId: " + this.dataTypeId);
		Debug.println(indent + 3, "possibleValues: " + this.possibleValues);
		Debug.println(indent + 3, "possibleValuesString: " + this.possibleValuesString);
		Debug.println(indent + 3, "minRange: " + this.minRange);
		Debug.println(indent + 3, "maxRange: " + this.maxRange);
		Debug.println(indent + 3, "required: " + this.required);
		Debug.println(indent + 3, "treatmentLabel: " + this.treatmentLabel);
		Debug.println(indent + 3, "operation: " + this.operation);
		Debug.println(indent + 3, "role: " + this.role);
		Debug.println(indent + 3, "variableType: " + this.variableType);
	}

	public boolean isFactor() {
		return this.isFactor;
	}

	public void setFactor(final boolean isFactor) {
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
	public void setDataTypeId(final Integer dataTypeId) {
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
	public void setPossibleValues(final List<ValueReference> possibleValues) {

		this.possibleValues = possibleValues;

		final StringBuilder sb = new StringBuilder();

		if (possibleValues != null) {
			for (final ValueReference ref : possibleValues) {
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
	public void setMinRange(final Double minRange) {
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
	public void setMaxRange(final Double maxRange) {
		this.maxRange = maxRange;
	}

	public Double getScaleMinRange() {
		return this.scaleMinRange;
	}

	public void setScaleMinRange(final Double scaleMinRange) {
		this.scaleMinRange = scaleMinRange;
	}

	public Double getScaleMaxRange() {
		return this.scaleMaxRange;
	}

	public void setScaleMaxRange(final Double scaleMaxRange) {
		this.scaleMaxRange = scaleMaxRange;
	}

	public Double getVariableMinRange() {
		return this.variableMinRange;
	}

	public void setVariableMinRange(final Double variableMinRange) {
		this.variableMinRange = variableMinRange;
	}

	public Double getVariableMaxRange() {
		return this.variableMaxRange;
	}

	public void setVariableMaxRange(final Double variableMaxRange) {
		this.variableMaxRange = variableMaxRange;
	}

	/**
	 * Gets the single character code that represents the data type of this variable.
	 *
	 * @return "C" for Categorical
	 * "N" for Numeric
	 * "D" for Date
	 * "T" for Character/Text
	 * If the variable has invalid datatypeid, it will return an empty string.
	 */
	public String getDataTypeCode() {

		final DataType dataTypeEnum = DataType.getById(this.dataTypeId);
		if (dataTypeEnum != null) {
			return dataTypeEnum.getDataTypeCode();
		}
		return "";

	}


	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

	public String getTreatmentLabel() {
		return this.treatmentLabel;
	}

	public void setTreatmentLabel(final String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
	}

	public PhenotypicType getRole() {
		return this.role;
	}

	public void setRole(final PhenotypicType role) {
		this.role = role;
	}

	public VariableType getVariableType() {
		return this.variableType;
	}

	public void setVariableType(final VariableType variableType) {
		this.variableType = variableType;
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
	public void setOperation(final Operation operation) {
		this.operation = operation;
	}

	public MeasurementVariable copy() {
		final MeasurementVariable var = new MeasurementVariable();
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

	public void setPossibleValuesString(final String possibleValuesString) {
		this.possibleValuesString = possibleValuesString;
	}

	public Integer getScaleId() {
		return this.scaleId;
	}

	public void setScaleId(final Integer scaleId) {
		this.scaleId = scaleId;
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final MeasurementVariable other = (MeasurementVariable) obj;
		if (this.termId != 0 && other.termId != 0 && this.termId != other.termId) {
			return false;
		} else if (this.getPSMHashCode() != other.getPSMHashCode()) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		int uniqueHashCode = this.termId;
		if (uniqueHashCode == 0) {
			uniqueHashCode = this.getPSMHashCode();
		}
		result = prime * result + uniqueHashCode;
		return result;
	}

	private int getPSMHashCode() {
		return java.util.Objects.hash(this.property, this.scale, this.method);
	}

	public void setFormula(final FormulaDto formula) {
		this.formula = formula;
	}

	public FormulaDto getFormula() {
		return this.formula;
	}

	public String getCropOntology() {
		return this.cropOntology;
	}

	public void setCropOntology(final String cropOntology) {
		this.cropOntology = cropOntology;
	}
}
