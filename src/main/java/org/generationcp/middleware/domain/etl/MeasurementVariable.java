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

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.util.Debug;

import java.util.List;

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
	
	private int storedIn;
	
	private boolean isFactor;
	
	private Integer dataTypeId;
	
	private List<ValueReference> possibleValues;
	
	private String possibleValuesString;
	
	private Double minRange;
	
	private Double maxRange;

        private boolean required;
        
        private String treatmentLabel;
    
        private Operation operation;
        
	public MeasurementVariable() {
	}

	public MeasurementVariable(String name, String description, String scale,
			String method, String property, String dataType, String value, String label) {
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

    public MeasurementVariable(int termId, String name, String description, String scale,
            String method, String property, String dataType, String value, String label) {
        this(name, description, scale, method, property, dataType, value, label);
        this.termId = termId;
    }
    
	public MeasurementVariable(String name, String description, String scale,
			String method, String property, String dataType, String value, String label,
			Double minRange, Double maxRange) {
		this(name, description, scale, method, property, dataType, value, label);
		this.minRange = minRange;
		this.maxRange = maxRange;
	}

    public MeasurementVariable(int termId, String name, String description, String scale,
            String method, String property, String dataType, String value, String label, 
            Double minRange, Double maxRange) {
        this(name, description, scale, method, property, dataType, value, label, minRange, maxRange);
        this.termId = termId;
    }
	
	public int getTermId() {
        return termId;
    }
    
    public void setTermId(int termId) {
        this.termId = termId;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		if(label==null) {
			label = "";
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MeasurementVariable [termId=");
        builder.append(termId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", scale=");
        builder.append(scale);
        builder.append(", method=");
        builder.append(method);
        builder.append(", property=");
        builder.append(property);
        builder.append(", dataType=");
        builder.append(dataType);
        builder.append(", value=");
        builder.append(value);
        builder.append(", label=");
        builder.append(label);
        builder.append(", storedIn=");
        builder.append(storedIn);
        builder.append(", isFactor=");
        builder.append(isFactor);
        builder.append("]");
        return builder.toString();
    }
	
	public void print(int indent) {
		Debug.println(indent, "MeasurementVariable: ");
		Debug.println(indent + 3, "Name: " + name);
	    Debug.println(indent + 3, "Description: " + description);
		Debug.println(indent + 3, "Scale: " + scale);
	    Debug.println(indent + 3, "Method: " + method);
	    Debug.println(indent + 3, "Property: " + property);
	    Debug.println(indent + 3, "Data Type: " + dataType);
	    Debug.println(indent + 3, "Value: " + value);
	    Debug.println(indent + 3, "Label: " + label);
	}

	public boolean isFactor() {
		return isFactor;
	}

	public void setFactor(boolean isFactor) {
		this.isFactor = isFactor;
	}

	public int getStoredIn() {
	    return storedIn;
	}
	
	public void setStoredIn(int storedIn){
	    this.storedIn = storedIn;
	}

	/**
	 * @return the dataTypeId
	 */
	public Integer getDataTypeId() {
		return dataTypeId;
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
		return possibleValues;
	}

	/**
	 * @param possibleValues the possibleValues to set
	 */
	public void setPossibleValues(List<ValueReference> possibleValues) {
		
		this.possibleValues = possibleValues;
		
		StringBuilder sb = new StringBuilder();
		
		if (possibleValues != null) {
			for (ValueReference ref : possibleValues){
				sb.append(ref.getDescription() + "|");
			}
		}
		
		this.setPossibleValuesString(sb.toString());
	}

	/**
	 * @return the minRange
	 */
	public Double getMinRange() {
		return minRange;
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
		return maxRange;
	}

	/**
	 * @param maxRange the maxRange to set
	 */
	public void setMaxRange(Double maxRange) {
		this.maxRange = maxRange;
	}
	
	public String getDataTypeDisplay() {
	    //datatype ids: 1120, 1125, 1128, 1130
		if(dataTypeId == null && dataType != null) {
			return dataType;
		}
		else if (dataTypeId == TermId.CHARACTER_VARIABLE.getId() || dataTypeId == TermId.TIMESTAMP_VARIABLE.getId() || 
	            dataTypeId == TermId.CHARACTER_DBID_VARIABLE.getId() || dataTypeId == TermId.CATEGORICAL_VARIABLE.getId()) {
	        return "C";
	    } else {
	        return "N";
	    }
	}

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

	public String getTreatmentLabel() {
		return treatmentLabel;
	}

	public void setTreatmentLabel(String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
	}

    /**
     * @return the operation
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(Operation operation) {
        this.operation = operation;
    }
    
    public MeasurementVariable copy() {
		MeasurementVariable var = new MeasurementVariable();
	    var.setTermId(termId);
	    var.setName(name);
		var.setDescription(description);
		var.setScale(scale);
		var.setMethod(method);
		var.setProperty(property);
		var.setDataType(dataType);
		var.setValue(value);
		var.setLabel(label);
		var.setStoredIn(storedIn);
		var.setFactor(isFactor);
		var.setDataTypeId(dataTypeId);
		var.setPossibleValues(possibleValues);
		var.setMinRange(minRange);
		var.setMaxRange(maxRange);
		var.setRequired(required);
	    var.setTreatmentLabel(treatmentLabel);
	    var.setOperation(operation);		
		return var;
	}

	public String getPossibleValuesString() {
		return possibleValuesString;
	}

	public void setPossibleValuesString(String possibleValuesString) {
		this.possibleValuesString = possibleValuesString;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		 if (this == obj)
	         return true;
	      if (obj == null)
	         return false;
	      if (getClass() != obj.getClass())
	         return false;
	      MeasurementVariable other = (MeasurementVariable) obj;
	      if (termId != other.termId)
	         return false;
	      
	      return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
        result = prime * result + termId;
        return result;
	}

}