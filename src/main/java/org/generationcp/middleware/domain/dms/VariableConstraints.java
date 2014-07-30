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

import org.generationcp.middleware.util.Debug;

/** 
 * Contains the min and max constraints of a variable.
 */
public class VariableConstraints implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Integer minValueId; // the cvtermprop id of min constraint- to identify if from local or central
    
	private Double minValue;
	
    private Integer maxValueId; // the cvtermprop id of max constraint - to identify if from local or central
    
	private Double maxValue;
	
	public VariableConstraints() { }

    public VariableConstraints(Double minValue, Double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public VariableConstraints(Integer minValueId, Integer maxValueId, Double minValue, Double maxValue) {
        this.minValueId = minValueId;
        this.maxValueId = maxValueId;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	
    public Integer getMinValueId() {
        return minValueId;
    }

    
    public void setMinValueId(Integer id) {
        this.minValueId = id;
    }

    
    public Integer getMaxValueId() {
        return maxValueId;
    }
    
    public void setMaxValueId(Integer id) {
        this.maxValueId = id;
    }

    public void print(int indent) {
        Debug.println(indent, "VariableConstraints: ");
        Debug.println(indent + 3, "minValueId: " + minValueId);
        Debug.println(indent + 3, "minValue: " + minValue);
        Debug.println(indent + 3, "maxValueId: " + maxValueId);
		Debug.println(indent + 3, "maxValue: " + maxValue);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableConstraints [minValueId=");
		builder.append(minValueId);
        builder.append(", minValue=");
		builder.append(minValue);
        builder.append(", maxValueId=");
        builder.append(maxValueId);
        builder.append(", maxValue=");
        builder.append(maxValue);
		builder.append("]");
		return builder.toString();
	}
	
	
}
