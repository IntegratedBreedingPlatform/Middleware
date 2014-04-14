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

import org.generationcp.middleware.util.Debug;

public class MeasurementData {
	
	private String label;
	
	private String value;
	
	private String cValueId;
	
	private boolean isEditable;
	
	private String dataType;
	
	private Integer phenotypeId;
	
//	private Integer valueId;
	
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
//		this.valueId = valueId;
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
//		this.valueId = valueId;
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

//	/**
//	 * @return the valueId
//	 */
//	public Integer getValueId() {
//		return valueId;
//	}
//
//	/**
//	 * @param valueId the valueId to set
//	 */
//	public void setValueId(Integer valueId) {
//		this.valueId = valueId;
//	}

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
	
}
