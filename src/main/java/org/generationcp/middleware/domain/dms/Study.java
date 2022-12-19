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


import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.util.Debug;

import java.io.Serializable;
import java.util.Objects;

/**
 * Contains the details of a study - id, conditions and constants.
 */

public class Study implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private String name;

	private VariableList conditions;

	private VariableList constants;
	
	private String programUUID;

	private StudyTypeDto studyType;

	private String description;

	private String startDate;

	private String endDate;

	private String studyUpdate;

	private String objective;

	private String createdBy;

	private Boolean locked;

	private boolean isFolder;

	public Study() {
	}

	public Study(final int id, final VariableList conditions, final VariableList constants, final StudyTypeDto studyType, final String description) {
		this.id = id;
		this.conditions = conditions;
		this.constants = constants;
		this.studyType = studyType;
		this.description = description;
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public String getObjective() {
		return this.objective;
	}

	public Integer getPrimaryInvestigator() {
		return this.getDisplayValueAsInt(TermId.PI_ID);
	}

	public StudyTypeDto getType() {
		return studyType;
	}

	public Integer getStartDate() {
		return Integer.valueOf(this.startDate);
	}

	public Integer getEndDate() {
		return (this.endDate != null && !this.endDate.isEmpty() ? Integer.valueOf(this.endDate): null);
	}

	public Integer getUser() {
		return Integer.valueOf(this.getCreatedBy());
	}


	public String getDisplayValue(final TermId termId) {
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

	private Integer getDisplayValueAsInt(final TermId termId) {
		Integer value = null;
		final String strValue = this.getDisplayValue(termId);
		if (strValue != null && !"".equals(strValue)) {
			value = Integer.parseInt(strValue);
		}
		return value;
	}

	public VariableList getConditions() {
		return this.conditions.sort();
	}

	public void setConditions(final VariableList conditions) {
		this.conditions = conditions;
	}

	public VariableTypeList getConditionVariableTypes() {
		return this.conditions.getVariableTypes().sort();
	}

	public VariableList getConstants() {
		return this.constants.sort();
	}

	public void setConstants(final VariableList constants) {
		this.constants = constants;
	}

	public void print(final int indent) {
		Debug.println(indent, "Study: ");
		Debug.println(indent + 3, "Id: " + this.getId());
		Debug.println(indent + 3, "Name: " + this.getName());
		Debug.println(indent + 3, "Title: " + this.getDescription());

		Debug.println(indent + 3, "Conditions: ");
		for (final Variable condition : this.conditions.getVariables()) {
			condition.print(indent + 6);
		}

		Debug.println(indent + 3, "Constants: ");
		for (final Variable constant : this.constants.getVariables()) {
			constant.print(indent + 6);
		}
	}

	public VariableTypeList getVariableTypesByPhenotypicType(final PhenotypicType pheotypicType) {
		final VariableTypeList filteredFactors = new VariableTypeList();
		final VariableTypeList factors = this.getConditionVariableTypes();
		if (factors != null && factors.getVariableTypes() != null) {
			for (final DMSVariableType factor : factors.getVariableTypes()) {
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

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public void setStudyType(final StudyTypeDto studyType) {
		this.studyType = studyType;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public String getStudyUpdate() {
		return this.studyUpdate;
	}

	public void setStudyUpdate(final String studyUpdate) {
		this.studyUpdate = studyUpdate;
	}

	public StudyTypeDto getStudyType() {
		return this.studyType;
	}

	public void setObjective(final String objective) {
		this.objective = objective;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Boolean isLocked() {
		return locked;
	}

	public void setLocked(final Boolean locked) {
		this.locked = locked;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(final boolean folder) {
		isFolder = folder;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Study))
			return false;
		final Study study = (Study) o;
		return this.getId() == study.getId() && Objects.equals(this.getName(), study.getName()) && Objects
			.equals(this.getConditions(), study.getConditions()) && Objects.equals(this.getConstants(), study.getConstants()) && Objects
			.equals(this.getProgramUUID(), study.getProgramUUID()) && this.getStudyType() == study.getStudyType() && Objects
			.equals(this.getDescription(), study.getDescription()) && Objects.equals(this.getStartDate(), study.getStartDate()) && Objects
			.equals(this.getEndDate(), study.getEndDate()) && Objects.equals(this.getStudyUpdate(), study.getStudyUpdate()) && Objects
			.equals(this.getObjective(), study.getObjective()) && Objects.equals(this.getCreatedBy(), study.getCreatedBy()) && Objects
			.equals(this.isLocked(), study.isLocked());
	}

	@Override
	public int hashCode() {

		return Objects
			.hash(this.getId(), this.getName(), this.getConditions(), this.getConstants(), this.getProgramUUID(), this.getStudyType(), this.getDescription(), this
					.getStartDate(),
				this.getEndDate(), this.getStudyUpdate(), this.getObjective(), this.getCreatedBy(), this.isLocked());
	}

	@Override
	public String toString() {
		return "Study{" + "id=" + id + ", name='" + name + '\'' + ", conditions=" + conditions + ", constants=" + constants
			+ ", programUUID='" + programUUID + '\'' + ", studyType=" + studyType + ", description='" + description + '\'' + ", startDate='"
			+ startDate + '\'' + ", endDate='" + endDate + '\'' + ", studyUpdate='" + studyUpdate + '\'' + ", objective='" + objective
			+ '\'' + ", createdBy='" + createdBy + '\'' + ", locked='" + locked + '\'' +'}';
	}
}
