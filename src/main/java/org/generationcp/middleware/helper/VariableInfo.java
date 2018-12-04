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

	public void setLocalName(final String localName) {
		this.localName = localName;
	}

	public String getLocalDescription() {
		return this.localDescription;
	}

	public void setLocalDescription(final String localDescription) {
		this.localDescription = localDescription;
	}

	public int getStdVariableId() {
		return this.stdVariableId;
	}

	public void setStdVariableId(final int stdVariableId) {
		this.stdVariableId = stdVariableId;
	}

	public int getRank() {
		return this.rank;
	}

	public void setRank(final int rank) {
		this.rank = rank;
	}

	public String getTreatmentLabel() {
		return this.treatmentLabel;
	}

	public void setTreatmentLabel(final String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
	}

	public PhenotypicType getRole() {
		return this.role;
	}

	public void setRole(final PhenotypicType role) {
		this.role = role;
	}

	public VariableType getVariableType() {
		return this.variableType;
	}

	public void setVariableType(final VariableType variableType) {
		this.variableType = variableType;
	}

	@Override
	public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("VariableInfo [localName=");
        builder.append(this.localName);
        builder.append(", localDescription=");
        builder.append(this.localDescription);
        builder.append(", stdVariableId=");
        builder.append(this.stdVariableId);
        builder.append(", treatmentLabel=");
        builder.append(this.treatmentLabel);
        builder.append(", role=");
        builder.append(this.role);
        builder.append(", rank=");
        builder.append(this.rank);
        builder.append(", variableType=");
        builder.append(this.variableType);
        builder.append("]");
        return builder.toString();
	}
}
