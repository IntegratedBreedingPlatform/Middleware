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
package org.generationcp.middleware.domain.dms;

import java.io.Serializable;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.util.Debug;

/** 
 * Contains the details of a Variable - type and value.
 */
public class Variable  implements Serializable, Comparable<Variable> {

	private static final long serialVersionUID = 1L;

	private VariableType variableType;
	
	private String value;
	
	private Integer phenotypeId;

	public Variable() { }
	
	public Variable(Integer phenotypeId, VariableType variableType, String value) {
		this.phenotypeId = phenotypeId;
		this.variableType = variableType;
		this.value = value;
		if (variableType == null) throw new RuntimeException();
	}
	
	public Variable(VariableType variableType, String value) {
		this.variableType = variableType;
		this.value = value;
		if (variableType == null) throw new RuntimeException();
	}
	
	public Variable(Integer phenotypeId, VariableType variableType, Double value) {
		this.phenotypeId = phenotypeId;
		this.variableType = variableType;
		if (value != null) {
			this.value = Double.toString(value);
		}
		if (variableType == null) throw new RuntimeException();
	}

	public Variable(VariableType variableType, Double value) {
		this.variableType = variableType;
		if (value != null) {
			this.value = Double.toString(value);
		}
		if (variableType == null) throw new RuntimeException();
	}

	public Variable(Integer phenotypeId, VariableType variableType, Integer value) {
		this.phenotypeId = phenotypeId;
		this.variableType = variableType;
		if (value != null) {
			this.value = Integer.toString(value);
		}
		if (variableType == null) throw new RuntimeException();
	}

	public Variable(VariableType variableType, Integer value) {
		this.variableType = variableType;
		if (value != null) {
			this.value = Integer.toString(value);
		}
		if (variableType == null) throw new RuntimeException();
	}

	public VariableType getVariableType() {
		return variableType;
	}

	public void setVariableType(VariableType variableType) {
		this.variableType = variableType;
		if (variableType == null) throw new RuntimeException();
	}
	
	public void setVariableType(VariableType variableType, boolean hasVariableType) {
		this.variableType = variableType;
		if(hasVariableType){
			if (variableType == null) 
				throw new RuntimeException();
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDisplayValue() {
		String value = this.value;
		if (variableType.getStandardVariable().hasEnumerations()) {
		    try{
		        Enumeration enumeration = variableType.getStandardVariable().findEnumerationById(Integer.parseInt(value));
		        if (enumeration != null) { 
		        	if(variableType.getStandardVariable().getDataType()!=null &&
		        	   variableType.getStandardVariable().getDataType().getId()==TermId.CATEGORICAL_VARIABLE.getId()) {
		        		
		        		//GCP-5536 - get description instead
		        		value = enumeration.getDescription();
		        	} else {
		        		value = enumeration.getName();
		        	}
		        }
		        else if(variableType.getStandardVariable().getDataType()!=null &&
		        	variableType.getStandardVariable().getDataType().getId()==TermId.CATEGORICAL_VARIABLE.getId()) {
	        		
	        		Integer overridingId = variableType.getStandardVariable().getOverridenEnumerations().get(Integer.parseInt(value));
	        		if (overridingId != null) {
	        			enumeration = variableType.getStandardVariable().findEnumerationById(overridingId);
	        		}
	        		
	        		if (enumeration != null){
	        			value = enumeration.getDescription();
	        		}
		        }
		    }catch(NumberFormatException e){
		        // Ignore, just return the value
		    }
		}
	    if (value == null){
            value = "";
        } 
	    return value;
	}
	
	public String getActualValue() {
		String value = this.value;
		if (variableType.getStandardVariable().hasEnumerations()) {
		    try{
		        Enumeration enumeration = variableType.getStandardVariable().findEnumerationById(Integer.parseInt(value));
		        if (enumeration != null) { 
		        	value = enumeration.getName();
		        }
		    }catch(NumberFormatException e){
		        // Ignore, just return the value
		    }
		}
	    if (value == null){
            value = "";
        } 
	    return value;
	}

	public void print(int indent) {
		Debug.println(indent, "Variable: " );
		
		if (variableType == null) {
			Debug.println(indent + 3, "VariableType: null");
		}
		else {
		    Debug.println(indent + 3, "VariableType: " + variableType.getId() + " [" + variableType.getLocalName() + "]");
		}
		Debug.println(indent + 3, "Value: " + value);
	}
	
	public int hashCode() {
		return variableType.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Variable)) return false;
		Variable other = (Variable) obj;
		return other.getVariableType().equals(getVariableType()) &&
			   equals(other.getValue(), getValue());
	}
	
	private boolean equals(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == null) return false;
		if (s2 == null) return false;
		return s1.equals(s2);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Variable [variableType=");
		builder.append(variableType);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	// Sort in ascending order by rank
	public int compareTo(Variable compareValue) { 
        int compareRank = ((Variable) compareValue).getVariableType().getRank(); 
        return Integer.valueOf(getVariableType().getRank()).compareTo(compareRank);
 	}

	public Integer getPhenotypeId() {
		return phenotypeId;
	}

	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}
	
}
