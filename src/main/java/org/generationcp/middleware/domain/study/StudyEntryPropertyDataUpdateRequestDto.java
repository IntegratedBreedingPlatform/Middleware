package org.generationcp.middleware.domain.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class StudyEntryPropertyDataUpdateRequestDto {

	private List<Integer> entryIds;

	private Integer variableId;

	private String value;

	public StudyEntryPropertyDataUpdateRequestDto() {

	}

	public StudyEntryPropertyDataUpdateRequestDto(final List<Integer> entryIds, final Integer variableId, final String value) {
		this.entryIds = entryIds;
		this.variableId = variableId;
		this.value = value;
	}

	public List<Integer> getEntryIds() {
		return entryIds;
	}

	public void setEntryIds(final List<Integer> entryIds) {
		this.entryIds = entryIds;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
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
