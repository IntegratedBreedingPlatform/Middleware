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

import java.io.Serializable;

import org.generationcp.middleware.util.Debug;

/**
 * Contains the details of a variable type - local name, local description and rank.
 */
public class VariableType implements Serializable, Comparable<VariableType> {

	private static final long serialVersionUID = 1L;

	private String localName;

	private String localDescription;

	private int rank;

	private StandardVariable standardVariable;

	private String treatmentLabel;
	
	private PhenotypicType role;

	public VariableType() {
	}

	public VariableType(String localName, String localDescription, StandardVariable standardVariable, int rank) {
		this.localName = localName;
		this.localDescription = localDescription;
		this.standardVariable = standardVariable;
		this.rank = rank;
		this.role = standardVariable.getPhenotypicType();
	}

	public int getId() {
		return this.standardVariable.getId();
	}

	public String getLocalName() {
		return this.localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getLocalDescription() {
		return this.localDescription;
	}

	public void setLocalDescription(String localDescription) {
		this.localDescription = localDescription;
	}

	public int getRank() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public StandardVariable getStandardVariable() {
		return this.standardVariable;
	}

	public void setStandardVariable(StandardVariable standardVariable) {
		this.standardVariable = standardVariable;
	}

	public String getTreatmentLabel() {
		return this.treatmentLabel;
	}

	public void setTreatmentLabel(String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
	}

	public PhenotypicType getRole() {
		return role;
	}

	public void setRole(PhenotypicType role) {
		this.role = role;
	}

	public void print(int indent) {
		Debug.println(indent, "Variable Type: ");
		indent += 3;
		Debug.println(indent, "localName: " + this.localName);
		Debug.println(indent, "localDescription: " + this.localDescription);
		Debug.println(indent, "rank: " + this.rank);
		this.standardVariable.print(indent);
	}

	@Override
	public int hashCode() {
		return this.standardVariable.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VariableType)) {
			return false;
		}
		VariableType other = (VariableType) obj;
		return other.getId() == this.getId();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableType [");
		builder.append(", localName=");
		builder.append(this.localName);
		builder.append(", localDescription=");
		builder.append(this.localDescription);
		builder.append(", rank=");
		builder.append(this.rank);
		builder.append(", standardVariable=");
		builder.append(this.standardVariable);
		builder.append("]");
		return builder.toString();
	}

	@Override
	// Sort in ascending order by rank
	public int compareTo(VariableType compareValue) {
		int compareRank = compareValue.getRank();
		return Integer.valueOf(this.rank).compareTo(compareRank);
	}

}
