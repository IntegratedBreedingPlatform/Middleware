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
import java.util.Iterator;
import java.util.Set;

import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.util.Debug;

/**
 * Contains the details of a variable type - local name, local description and rank.
 */
public class DMSVariableType implements Serializable, Comparable<DMSVariableType> {

	private static final long serialVersionUID = 1L;

	private String localName;

	private String localDescription;

	private int rank;

	private StandardVariable standardVariable;

	private String treatmentLabel;
	
	private PhenotypicType role;

	private VariableType variableType;

	public DMSVariableType() {
	}

	public DMSVariableType(String localName, String localDescription, StandardVariable standardVariable, int rank) {
		this.localName = localName;
		this.localDescription = localDescription;
		this.standardVariable = standardVariable;
		this.role = standardVariable.getPhenotypicType();

		// Setting first variable type if exist
		final Set<VariableType> variableTypes = standardVariable.getVariableTypes();
		if (variableTypes != null && !variableTypes.isEmpty()) {
			final Iterator<VariableType> iterator = variableTypes.iterator();
			this.variableType = iterator.next();
		}

		this.rank = rank;
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

	public VariableType getVariableType() {
		return variableType;
	}

	//NOTE: We also add variable type to associated standard variable.
	public void setVariableType(VariableType variableType) {
		this.variableType = variableType;
	}

	/**
	 * This will set variable type if null based on role and property name.
	 */
	public void setVariableTypeIfNull(){

		if(this.getVariableType() != null) {
			return;
		}

		if(this.getRole() == null){
			return;
		}

		StandardVariable standardVariable = this.getStandardVariable();
		String propertyName = standardVariable.getProperty().getName();
		this.setVariableType(OntologyDataHelper.mapFromPhenotype(this.getRole(), propertyName));
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
		if (!(obj instanceof DMSVariableType)) {
			return false;
		}
		DMSVariableType other = (DMSVariableType) obj;
		return other.getId() == this.getId();
	}

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DMSVariableType [localName=");
        builder.append(this.localName);
        builder.append(", localDescription=");
        builder.append(this.localDescription);
        builder.append(", rank=");
        builder.append(this.rank);
        builder.append(", standardVariable=");
        builder.append(this.standardVariable);
        builder.append(", role=");
        builder.append(this.role);
        builder.append(", treatmentLabel=");
        builder.append(this.treatmentLabel);
        builder.append(", variableType=");
        builder.append(this.variableType);
        builder.append("]");
        return builder.toString();
    }

	public void print(int indent) {
		Debug.println(indent, "DMS Variable Type: ");
		Debug.println(indent + 3, "localName: " + this.localName);
		Debug.println(indent + 3, "localDescription: " + this.localDescription);
		Debug.println(indent + 3, "rank: " + this.rank);
		Debug.println(indent + 3, "standardVariable: " + this.standardVariable);
		Debug.println(indent + 3, "treatmentLabel: " + this.treatmentLabel);
		Debug.println(indent + 3, "role: " + this.role);
		Debug.println(indent + 3, "variableType: " + this.variableType);
	}

	@Override
	// Sort in ascending order by rank
	public int compareTo(DMSVariableType compareValue) {
		int compareRank = compareValue.getRank();
		return Integer.valueOf(this.rank).compareTo(compareRank);
	}

}
