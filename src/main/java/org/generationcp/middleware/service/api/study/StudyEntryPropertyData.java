package org.generationcp.middleware.service.api.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class StudyEntryPropertyData {

	private Integer studyEntryPropertyId;
	private String value;
	private Integer variableId;

	public Integer getStudyEntryPropertyId() {
		return studyEntryPropertyId;
	}

	public void setStudyEntryPropertyId(final Integer studyEntryPropertyId) {
		this.studyEntryPropertyId = studyEntryPropertyId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}
}
