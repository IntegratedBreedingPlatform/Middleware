/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.oms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.util.Debug;

/**
 * Contains the primary details of a trait property - id, name, description, plus the list of standard variables
 *
 * @author Joyce Avestro
 *
 */
public class PropertyReference extends Reference implements Serializable, Comparable<PropertyReference> {

	private static final long serialVersionUID = 1L;

	private List<StandardVariableReference> standardVariables;

	public PropertyReference(Integer id, String name) {
		super.setId(id);
		super.setName(name);
		this.standardVariables = new ArrayList<>();
	}

	public PropertyReference(Integer id, String name, String description) {
		this(id, name);
		super.setDescription(description);
	}

	/**
	 * @return the standardVariables
	 */
	public List<StandardVariableReference> getStandardVariables() {
		return this.standardVariables;
	}

	/**
	 * @param standardVariables the standardVariables to set
	 */
	public void setStandardVariables(List<StandardVariableReference> standardVariables) {
		this.standardVariables = standardVariables;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PropertyReference [id=");
		builder.append(this.getId());
		builder.append(", name=");
		builder.append(this.getName());
		builder.append(", description=");
		builder.append(this.getDescription());
		builder.append(", standardVariables=");
		builder.append(this.standardVariables);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void print(int indent) {
		Debug.println(indent, "PropertyReference: ");
		Debug.println(indent + 3, "Id: " + this.getId());
		Debug.println(indent + 3, "Name: " + this.getName());
		Debug.println(indent + 3, "Description: " + this.getDescription());
		Debug.println(indent + 3, "Standard Variables : ");
		for (StandardVariableReference variable : this.standardVariables) {
			variable.print(indent + 6);
		}
		if (this.standardVariables.isEmpty()) {
			Debug.println(indent + 6, "None");
		}
	}

	/*
	 * (non-Javadoc) Sort in ascending order by property name
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PropertyReference compareValue) {
		String compareName = compareValue.getName();
		return this.getName().compareToIgnoreCase(compareName);
	}

}
