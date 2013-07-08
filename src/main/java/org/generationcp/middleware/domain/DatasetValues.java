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
package org.generationcp.middleware.domain;

/** 
 * Contains the name, description, type and variables of a dataset.
 */
public class DatasetValues {

	private String name;
	
	private String description;
	
	private DataSetType type;
	
	private VariableList variables;
	
	public DatasetValues() { }
	
	public DatasetValues(String name, String description, DataSetType type) {
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(DataSetType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public DataSetType getType() {
		return type;
	}

	public VariableList getVariables() {
		return variables;
	}
	
	public void addVariable(Variable variable) {
		if (variables == null) {
			variables = new VariableList();
		}
		variables.add(variable);
	}

	public void setVariables(VariableList variables) {
		this.variables = variables;
	}
}
