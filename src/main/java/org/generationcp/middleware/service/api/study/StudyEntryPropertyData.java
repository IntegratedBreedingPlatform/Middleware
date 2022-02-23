package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyEntryPropertyData implements Serializable {

	private Integer studyEntryPropertyId;
	private Integer variableId;
	private String value;
	private Integer categoricalValueId;

	public StudyEntryPropertyData() {
	}

	public StudyEntryPropertyData(final String value) {
		this.value = value;
	}

	public StudyEntryPropertyData(final Integer studyEntryPropertyId, final Integer variableId, final String value,
		final Integer categoricalValueId) {
		this.studyEntryPropertyId = studyEntryPropertyId;
		this.variableId = variableId;
		this.value = value;
		this.categoricalValueId = categoricalValueId;
	}

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

	public Integer getCategoricalValueId() {
		return categoricalValueId;
	}

	public void setCategoricalValueId(final Integer categoricalValueId) {
		this.categoricalValueId = categoricalValueId;
	}

	// TODO: review this method name
	public String getPropertyValue() {
		if (this.categoricalValueId != null) {
			return String.valueOf(this.categoricalValueId);
		}
		return this.value;
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
