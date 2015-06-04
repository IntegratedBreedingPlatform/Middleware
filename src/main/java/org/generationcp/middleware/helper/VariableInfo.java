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

import org.generationcp.middleware.util.Debug;

public class VariableInfo {

	private String localName;

	private String localDescription;

	private int rank;

	private int stdVariableId;

	private String treatmentLabel;

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

	public void print(int indent) {
		Debug.println(indent, "VariableInfo:");
		Debug.println(indent + 3, "stdVariableId: " + this.stdVariableId);
		Debug.println(indent + 3, "localName: " + this.localName);
		Debug.println(indent + 3, "localDescription: " + this.localDescription);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableInfo [localName=");
		builder.append(this.localName);
		builder.append(", localDescription=");
		builder.append(this.localDescription);
		builder.append(", stdVariableId=");
		builder.append(this.stdVariableId);
		builder.append("]");
		return builder.toString();
	}

}
