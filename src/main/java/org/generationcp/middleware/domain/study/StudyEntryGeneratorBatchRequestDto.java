package org.generationcp.middleware.domain.study;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class StudyEntryGeneratorBatchRequestDto {

	private Integer entryTypeId;
	private Integer listId;

	//TODO: move SearchCompositeDto to common package
	private SearchCompositeDto<Integer, Integer> searchComposite;

	public Integer getEntryTypeId() {
		return entryTypeId;
	}

	public void setEntryTypeId(final Integer entryTypeId) {
		this.entryTypeId = entryTypeId;
	}


	public Integer getListId() {
		return listId;
	}

	public void setListId(final Integer listId) {
		this.listId = listId;
	}

	public SearchCompositeDto<Integer, Integer> getSearchComposite() {
		return this.searchComposite;
	}

	public void setSearchComposite(
		final SearchCompositeDto<Integer, Integer> searchComposite) {
		this.searchComposite = searchComposite;
	}

}
