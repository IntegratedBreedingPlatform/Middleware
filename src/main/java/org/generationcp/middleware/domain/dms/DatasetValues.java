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

package org.generationcp.middleware.domain.dms;

/**
 * Contains the name, description, type and variables of a dataset.
 */
public class DatasetValues {

	private String name;

	private String description;

	private VariableList variables;

	public DatasetValues() {
	}

	public DatasetValues(final String name, final String description) {
		this.name = name;
		this.description = description;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public VariableList getVariables() {
		return this.variables;
	}

	public void addVariable(final Variable variable) {
		if (this.variables == null) {
			this.variables = new VariableList();
		}
		this.variables.add(variable);
	}

	public void setVariables(final VariableList variables) {
		this.variables = variables;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(this.getEntityName() + " [name=");
		builder.append(this.getName());
		builder.append(", description=");
		builder.append(this.getDescription());
		builder.append(", VariableList=");
		builder.append(this.getVariables().toString());
		builder.append("]");
		return builder.toString();
	}

	public String getEntityName() {
		return "DatasetValues";
	}
}
