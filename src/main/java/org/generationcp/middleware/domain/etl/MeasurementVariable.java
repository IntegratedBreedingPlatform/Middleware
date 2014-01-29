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

public class MeasurementVariable {

	private String name;

	private String description;

	private String scale;

	private String method;
	
	private String property;
	
	private String dataType;
	
	private String value;
	
	private String label;
	
	private boolean isFactor;

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
		builder.append("MeasurementVariable [name=");
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

	
}
