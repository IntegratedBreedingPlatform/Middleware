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

package org.generationcp.middleware.helper;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.util.Debug;

public class VariableInfo {

	private String localName;

	private String localDescription;

	private int rank;

	private int stdVariableId;

	private String treatmentLabel;
	
	private PhenotypicType role;

	private VariableType variableType;

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

	public int getStdVariableId() {
		return this.stdVariableId;
	}

	public void setStdVariableId(int stdVariableId) {
		this.stdVariableId = stdVariableId;
	}

	public int getRank() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
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

	public VariableType getVariableType() {
		return variableType;
	}

	public void setVariableType(VariableType variableType) {
		this.variableType = variableType;
	}

	@Override
	public String toString() {
		return "VariableInfo{" +
				"localName='" + localName + '\'' +
				", localDescription='" + localDescription + '\'' +
				", rank=" + rank +
				", stdVariableId=" + stdVariableId +
				", treatmentLabel='" + treatmentLabel + '\'' +
				", role=" + role +
				", variableType=" + variableType +
				'}';
	}

	public void print(int indent) {
		Debug.println(indent, "VariableInfo:");
		Debug.println(indent + 3, "localName: " + this.localName);
		Debug.println(indent + 3, "localDescription: " + this.localDescription);
		Debug.println(indent + 3, "rank: " + this.rank);
		Debug.println(indent + 3, "stdVariableId: " + this.stdVariableId);
		Debug.println(indent + 3, "treatmentLabel: " + this.treatmentLabel);
		Debug.println(indent + 3, "role: " + this.role);
		Debug.println(indent + 3, "variableType: " + this.variableType);
	}
}
