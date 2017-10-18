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

import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.util.Debug;

/**
 * Contains the details of a study - id, conditions and constants.
 */

public class Study implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private VariableList conditions;

	private VariableList constants;
	
	private String programUUID;

	public Study() {
	}

	public Study(int id, VariableList conditions, VariableList constants) {
		this.id = id;
		this.conditions = conditions;
		this.constants = constants;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.getDisplayValue(TermId.STUDY_NAME);
	}

	public String getTitle() {
		return this.getDisplayValue(TermId.STUDY_TITLE);
	}

	public String getObjective() {
		return this.getDisplayValue(TermId.STUDY_OBJECTIVE);
	}

	public Integer getPrimaryInvestigator() {
		return this.getDisplayValueAsInt(TermId.PI_ID);
	}

	public StudyType getType() {
		return StudyType.getStudyType(this.getDisplayValue(TermId.STUDY_TYPE));
	}

	public Integer getStartDate() {
		return this.getDisplayValueAsInt(TermId.START_DATE);
	}

	public Integer getEndDate() {
		return this.getDisplayValueAsInt(TermId.END_DATE);
	}

	public Integer getUser() {
		return this.getDisplayValueAsInt(TermId.STUDY_UID);
	}

	public Integer getCreationDate() {
		return this.getDisplayValueAsInt(TermId.CREATION_DATE);
	}

	public String getDisplayValue(TermId termId) {
		String value = null;
		Variable variable = this.conditions.findById(termId);
		if (variable == null) {
			variable = this.constants.findById(termId);
		}
		if (variable != null) {
			value = variable.getActualValue();
		}
		return value;
	}

	public Integer getDisplayValueAsInt(TermId termId) {
		Integer value = null;
		String strValue = this.getDisplayValue(termId);
		if (strValue != null && !"".equals(strValue)) {
			value = Integer.parseInt(strValue);
		}
		return value;
	}

	public VariableList getConditions() {
		return this.conditions.sort();
	}

	public void setConditions(VariableList conditions) {
		this.conditions = conditions;
	}

	public VariableTypeList getConditionVariableTypes() {
		return this.conditions.getVariableTypes().sort();
	}

	public VariableList getConstants() {
		return this.constants.sort();
	}

	public void setConstants(VariableList constants) {
		this.constants = constants;
	}

	public VariableTypeList getConstantVariableTypes() {
		return this.constants.getVariableTypes().sort();
	}

	public void print(int indent) {
		Debug.println(indent, "Study: ");
		Debug.println(indent + 3, "Id: " + this.getId());
		Debug.println(indent + 3, "Name: " + this.getName());
		Debug.println(indent + 3, "Title: " + this.getTitle());

		Debug.println(indent + 3, "Conditions: ");
		for (Variable condition : this.conditions.getVariables()) {
			condition.print(indent + 6);
		}

		Debug.println(indent + 3, "Constants: ");
		for (Variable constant : this.constants.getVariables()) {
			constant.print(indent + 6);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.conditions == null ? 0 : this.conditions.hashCode());
		result = prime * result + (this.constants == null ? 0 : this.constants.hashCode());
		result = prime * result + this.id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Study)) {
			return false;
		}
		Study other = (Study) obj;
		return this.getId() == other.getId();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Study [id=");
		builder.append(this.id);
		builder.append(", conditions=");
		builder.append(this.conditions);
		builder.append(", constants=");
		builder.append(this.constants);
		builder.append("]");
		return builder.toString();
	}

	public VariableTypeList getVariableTypesByPhenotypicType(PhenotypicType pheotypicType) {
		VariableTypeList filteredFactors = new VariableTypeList();
		VariableTypeList factors = this.getConditionVariableTypes();
		if (factors != null && factors.getVariableTypes() != null) {
			for (DMSVariableType factor : factors.getVariableTypes()) {
				if (factor.getStandardVariable().getPhenotypicType() == pheotypicType) {
					filteredFactors.add(factor);
				}
			}
		}
		return filteredFactors.sort();
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(String programUUID) {
		this.programUUID = programUUID;
	}
	
	
}
