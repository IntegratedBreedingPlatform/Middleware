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

import javax.annotation.Nonnull;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.util.Debug;

public class MeasurementData {

	public static final String MISSING_VALUE = "missing";
	public boolean isCustomCategoricalValue;
	private String label;
	private String value;
	private String cValueId;
	private boolean isEditable;
	private String dataType;
	private Integer phenotypeId;
	private MeasurementVariable measurementVariable;
	private boolean isAccepted;
	private String oldValue;

	// used to map this object to what is actually saved in the database after saving
	private Variable variable;

	public MeasurementData() {
	}

	public MeasurementData(final MeasurementData data) {
		this.label = data.label;
		this.value = data.value;
		this.isEditable = data.isEditable;
		this.dataType = data.dataType;
		this.phenotypeId = data.phenotypeId;
		this.cValueId = data.cValueId;
		this.measurementVariable = data.measurementVariable;
	}

	public MeasurementData(final String label, final String value) {
		super();
		this.label = label;
		this.value = value;
	}

	public MeasurementData(final String label, final String value, final boolean isEditable, final String dataType) {
		super();
		this.label = label;
		this.value = value;
		this.isEditable = isEditable;
		this.dataType = dataType;
	}

	public MeasurementData(final String label, final String value, final boolean isEditable, final String dataType, final Integer valueId) {
		this(label, value, isEditable, dataType);
		if (valueId != null) {
			this.cValueId = valueId.toString();
		}
	}

	public MeasurementData(final String label, final String value, final boolean isEditable, final String dataType,
			final MeasurementVariable mvar) {
		super();
		this.label = label;
		this.value = value;
		this.isEditable = isEditable;
		this.dataType = dataType;
		this.measurementVariable = mvar;
	}

	public MeasurementData(final String label, final String value, final boolean isEditable, final String dataType, final Integer valueId,
			final MeasurementVariable mvar) {
		this(label, value, isEditable, dataType, mvar);
		if (valueId != null) {
			this.cValueId = valueId.toString();
		}
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	@Nonnull
	public String getValue() {
		return this.value == null || "null".equalsIgnoreCase(this.value) ? "" : this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getPhenotypeId() {
		if (this.phenotypeId == null && this.variable != null) {
			return this.variable.getPhenotypeId();
		}
		return this.phenotypeId;
	}

	public void setPhenotypeId(final Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("MeasurementData [label=");
		builder.append(this.label);
		builder.append(", value=");
		builder.append(this.value);
		builder.append(", isEditable=");
		builder.append(this.isEditable);
		builder.append(", dataType=");
		builder.append(this.dataType);
		builder.append(", phenotypeId=");
		builder.append(this.phenotypeId);
		builder.append("]");
		return builder.toString();
	}

	public void print(final int indent) {
		Debug.println(indent, "MeasurementData: ");
		Debug.println(indent + 3, "Label: " + this.label);
		Debug.println(indent + 3, "Value: " + this.value);
	}

	public boolean isEditable() {
		return this.isEditable;
	}

	public void setEditable(final boolean isEditable) {
		this.isEditable = isEditable;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(final String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the measurementVariable
	 */
	public MeasurementVariable getMeasurementVariable() {
		return this.measurementVariable;
	}

	/**
	 * @param measurementVariable the measurementVariable to set
	 */
	public void setMeasurementVariable(final MeasurementVariable measurementVariable) {
		this.measurementVariable = measurementVariable;
	}

	public String getcValueId() {
		return this.cValueId;
	}

	public void setcValueId(final String cValueId) {
		this.cValueId = cValueId;
	}

	public String getDisplayValue() {
		return this.getDisplayValue(true);
	}

	public String getDisplayValue(boolean showDescription) {
		if (this.getMeasurementVariable() != null && this.getMeasurementVariable().getPossibleValues() != null
				&& !this.getMeasurementVariable().getPossibleValues().isEmpty()) {

			if (NumberUtils.isNumber(this.value)) {
				final List<ValueReference> possibleValues = this.getMeasurementVariable().getPossibleValues();
				for (final ValueReference possibleValue : possibleValues) {
					if (possibleValue.getId().equals(Double.valueOf(this.value).intValue())) {
						return showDescription ? possibleValue.getDescription() : possibleValue.getName();
					}
				}
			}
			// this would return the value from the db
			return this.value == null ? "" : this.value;
		} else {
			if (this.getMeasurementVariable() != null && this.getMeasurementVariable().getDataTypeDisplay() != null
					&& "N".equalsIgnoreCase(this.getMeasurementVariable().getDataTypeDisplay())) {
				if (this.value != null && !"".equalsIgnoreCase(this.value) && !"null".equalsIgnoreCase(this.value)) {
					if (MeasurementData.MISSING_VALUE.equalsIgnoreCase(this.value)) {
						return MeasurementData.MISSING_VALUE;
					}
					final int intVal = Double.valueOf(this.value).intValue();
					final double doubleVal = Double.valueOf(this.value);
					if (intVal == doubleVal) {
						this.value = Integer.toString(intVal);
						return this.value;
					} else {
						return this.value;
					}
				}
			} else {
				return this.value;
			}
		}
		return "";
	}

	public CategoricalDisplayValue getDisplayValueForCategoricalData() {
		if (null == this.value || "".equals(this.value)) {
			return new CategoricalDisplayValue("", "", "", false);
		} else if (NumberUtils.isNumber(this.value)) {
			final List<ValueReference> possibleValues = this.getMeasurementVariable().getPossibleValues();
			for (final ValueReference possibleValue : possibleValues) {
				if (possibleValue.getId().equals(Double.valueOf(this.value).intValue())) {

					// if measurement data is a factor, show original description else, get the modified display description
					final String displayDescription =
							this.getMeasurementVariable().isFactor() ? possibleValue.getDescription() : possibleValue
									.getDisplayDescription();

					return new CategoricalDisplayValue(this.value, possibleValue.getName(), displayDescription);
				}
			}
		}

		return new CategoricalDisplayValue(this.value, this.value, this.value, false);
	}

	public MeasurementData copy() {
		final MeasurementData data = new MeasurementData(this.label, this.value, this.isEditable, this.dataType, this.measurementVariable);
		data.setPhenotypeId(this.phenotypeId);
		data.setcValueId(this.cValueId);
		data.setCustomCategoricalValue(this.isCustomCategoricalValue);
		data.setAccepted(this.isAccepted);
		return data;
	}

	public MeasurementData copy(final MeasurementVariable oldVar) {
		final MeasurementData data = new MeasurementData(this.label, this.value, this.isEditable, this.dataType, oldVar);
		data.setPhenotypeId(this.phenotypeId);
		data.setcValueId(this.cValueId);
		return data;
	}

	public boolean isCustomCategoricalValue() {
		return this.isCustomCategoricalValue;
	}

	public void setCustomCategoricalValue(final boolean isCustomCategoricalValue) {
		this.isCustomCategoricalValue = isCustomCategoricalValue;
	}

	public boolean isAccepted() {
		return this.isAccepted;
	}

	public void setAccepted(final boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

	public boolean isCategoricalDisplayAcceptedValidValues() {
		return this.isDisplayAcceptedValidValues();
	}

	public boolean isDisplayAcceptedValidValues() {
		if (this.getMeasurementVariable() != null && this.getMeasurementVariable().getDataTypeId() == TermId.CATEGORICAL_VARIABLE.getId()
				&& this.getMeasurementVariable().getPossibleValues() != null) {
			final String displayValue = this.getDisplayValue();
			if (displayValue != null && !displayValue.equalsIgnoreCase("") && !MeasurementData.MISSING_VALUE.equals(displayValue)) {
				for (final ValueReference valRef : this.getMeasurementVariable().getPossibleValues()) {
					if (valRef.getDescription().equalsIgnoreCase(displayValue)) {
						return false;
					}
				}
				return true;
			}
		} else if (this.getMeasurementVariable() != null
				&& this.getMeasurementVariable().getDataTypeId() == TermId.NUMERIC_VARIABLE.getId()) {
			final String displayValue = this.getDisplayValue();
			if (displayValue != null && !displayValue.equalsIgnoreCase("") && !MeasurementData.MISSING_VALUE.equals(displayValue)) {
				if (this.getMeasurementVariable().getMinRange() != null && this.getMeasurementVariable().getMaxRange() != null) {

					if (!NumberUtils.isNumber(displayValue)) {
						return false;
					} else {
						final Double numericValue = Double.valueOf(displayValue);
						return numericValue > this.getMeasurementVariable().getMaxRange()
								|| numericValue < this.getMeasurementVariable().getMinRange();

					}

				}
				return false;
			}
		}
		return false;
	}

	public Variable getVariable() {
		return this.variable;
	}

	public void setVariable(final Variable variable) {
		this.variable = variable;
	}

	public boolean isCategorical() {
		return this.getMeasurementVariable().getDataTypeId().equals(TermId.CATEGORICAL_VARIABLE.getId());
	}

	public boolean isNumeric() {
		return this.getMeasurementVariable().getDataTypeId().equals(TermId.NUMERIC_VARIABLE.getId());
	}

	public String getOldValue() {
		return this.oldValue;
	}

	/**
	 * Use this to store the original value of the measurement data from importing measurements or data retrieved from the database, so that
	 * even if the MeasurementData.value is changed, you can still recover the old value.
	 * 
	 * @param value
	 */
	public void setOldValue(String value) {
		this.oldValue = value;
	}

}
