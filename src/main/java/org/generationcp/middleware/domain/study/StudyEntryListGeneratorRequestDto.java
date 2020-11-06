package org.generationcp.middleware.domain.study;

import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class StudyEntryListGeneratorRequestDto {

	private Integer listId;

	public Integer getListId() {
		return this.listId;
	}

	public void setListId(final Integer listId) {
		this.listId = listId;
	}

}
