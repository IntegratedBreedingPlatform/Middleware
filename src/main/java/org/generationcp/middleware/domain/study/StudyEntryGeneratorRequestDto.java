package org.generationcp.middleware.domain.study;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class StudyEntryGeneratorRequestDto {

	private Integer entryTypeId;

	private SearchCompositeDto<Integer, Integer> searchComposite;

	public Integer getEntryTypeId() {
		return this.entryTypeId;
	}

	public void setEntryTypeId(final Integer entryTypeId) {
		this.entryTypeId = entryTypeId;
	}

	public SearchCompositeDto<Integer, Integer> getSearchComposite() {
		return this.searchComposite;
	}

	public void setSearchComposite(
		final SearchCompositeDto<Integer, Integer> searchComposite) {
		this.searchComposite = searchComposite;
	}

}
