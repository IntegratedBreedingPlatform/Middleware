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

import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.util.Debug;

/**
 * Contains the primary details of a trait class - id, name and description
 *
 * @author Joyce Avestro
 *
 */
public class StandardVariableReference extends Reference implements Serializable, Comparable<StandardVariableReference> {

	private static final long serialVersionUID = 1L;

	private boolean hasPair;

	public StandardVariableReference(Integer id, String name) {
		super.setId(id);
		super.setName(name);
	}

	public StandardVariableReference(Integer id, String name, String description) {
		super.setId(id);
		super.setName(name);
		super.setDescription(description);
	}

	/**
	 * @return the hasPair
	 */
	public boolean isHasPair() {
		return this.hasPair;
	}

	/**
	 * @param hasPair the hasPair to set
	 */
	public void setHasPair(boolean hasPair) {
		this.hasPair = hasPair;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StandardVariableReference [id=");
		builder.append(this.getId());
		builder.append(", name=");
		builder.append(this.getName());
		builder.append(", description=");
		builder.append(this.getDescription());
		builder.append(", hasPair=");
		builder.append(this.isHasPair());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void print(int indent) {
		Debug.println(indent, "StandardVariableReference: ");
		Debug.println(indent + 3, "Id: " + this.getId());
		Debug.println(indent + 3, "Name: " + this.getName());
		Debug.println(indent + 3, "Description: " + this.getDescription());
		Debug.println(indent + 3, "Has Pair: " + this.isHasPair());
	}

	/*
	 * (non-Javadoc) Sort in ascending order by standard variable name
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(StandardVariableReference compareValue) {
		String compareName = compareValue.getName();
		return this.getName().compareToIgnoreCase(compareName);
	}

}
