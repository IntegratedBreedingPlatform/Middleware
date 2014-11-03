/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.domain.etl;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.util.Debug;

import java.util.List;

public class MeasurementData {
	
	private String label;
	
	private String value;
	
	private String cValueId;
	
	private boolean isEditable;
	
	private String dataType;
	
	private Integer phenotypeId;
	
	private MeasurementVariable measurementVariable;
	
	public MeasurementData() {
	}
	
	public MeasurementData(String label, String value) {
		super();
		this.label = label;
		this.value = value;
	}

	public MeasurementData(String label, String value, boolean isEditable, String dataType) {
		super();
		this.label = label;
		this.value = value;
		this.isEditable = isEditable;
		this.dataType = dataType;
	}
	
	public MeasurementData(String label, String value, boolean isEditable, String dataType, Integer valueId) {
		this(label, value, isEditable, dataType);
		if (valueId != null) {
			this.cValueId = valueId.toString();
		}
	}

	public MeasurementData(String label, String value, boolean isEditable, String dataType, MeasurementVariable mvar) {
		super();
		this.label = label;
		this.value = value;
		this.isEditable = isEditable;
		this.dataType = dataType;
		this.measurementVariable = mvar;
	}
	
	public MeasurementData(String label, String value, boolean isEditable, String dataType, Integer valueId, MeasurementVariable mvar) {
		this(label, value, isEditable, dataType, mvar);
		if (valueId != null) {
			this.cValueId = valueId.toString();
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return (value == null ||  "null".equalsIgnoreCase(value)) ? "" : value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Integer getPhenotypeId() {
	    return phenotypeId;
	}
	
	public void setPhenotypeId(Integer phenotypeId) {
	    this.phenotypeId = phenotypeId;
	} 

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MeasurementData [label=");
        builder.append(label);
        builder.append(", value=");
        builder.append(value);
        builder.append(", isEditable=");
        builder.append(isEditable);
        builder.append(", dataType=");
        builder.append(dataType);
        builder.append(", phenotypeId=");
        builder.append(phenotypeId);
        builder.append("]");
        return builder.toString();
    }

	public void print(int indent) {
		Debug.println(indent, "MeasurementData: ");
		Debug.println(indent + 3, "Label: " + label);
	    Debug.println(indent + 3, "Value: " + value);
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the measurementVariable
	 */
	public MeasurementVariable getMeasurementVariable() {
		return measurementVariable;
	}

	/**
	 * @param measurementVariable the measurementVariable to set
	 */
	public void setMeasurementVariable(MeasurementVariable measurementVariable) {
		this.measurementVariable = measurementVariable;
	}


	public String getcValueId() {
		return cValueId;
	}

	public void setcValueId(String cValueId) {
		this.cValueId = cValueId;
	}
	
	public String getDisplayValue() {
		if (this.getMeasurementVariable() != null && this.getMeasurementVariable().getPossibleValues() != null
				&& !this.getMeasurementVariable().getPossibleValues().isEmpty() 
				&& NumberUtils.isNumber(this.value)) {
			
			List<ValueReference> possibleValues = this.getMeasurementVariable().getPossibleValues();
			for (ValueReference possibleValue : possibleValues) {
				if (possibleValue.getId().equals(Double.valueOf(this.value).intValue())) {
					return possibleValue.getDescription();
				}
			}
			return this.value; //this would return the value from the db
		}
		else {
			if(this.getMeasurementVariable() != null && this.getMeasurementVariable().getDataTypeDisplay() != null && this.getMeasurementVariable().getDataTypeDisplay().equalsIgnoreCase("N")){
				if(this.value != null && !this.value.equalsIgnoreCase("") && !this.value.equalsIgnoreCase("null")) {
					int intVal = Double.valueOf(this.value).intValue();
					double doubleVal = Double.valueOf(this.value);
					if(intVal == doubleVal){
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
	
	public MeasurementData copy() {
		MeasurementData data = new MeasurementData(this.label, this.value, this.isEditable, this.dataType, this.measurementVariable);
		data.setPhenotypeId(this.phenotypeId);
		data.setcValueId(this.cValueId);
		return data;
	}
	public MeasurementData copy(MeasurementVariable oldVar) {
		MeasurementData data = new MeasurementData(this.label, this.value, this.isEditable, this.dataType, oldVar);
		data.setPhenotypeId(this.phenotypeId);
		data.setcValueId(this.cValueId);
		return data;
	}
}
