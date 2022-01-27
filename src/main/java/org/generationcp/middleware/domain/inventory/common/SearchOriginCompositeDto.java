package org.generationcp.middleware.domain.inventory.common;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class SearchOriginCompositeDto {

	private Integer searchRequestId;
	private SearchOrigin searchOrigin;

	public SearchOriginCompositeDto() {

	}

	public Integer getSearchRequestId() {
		return searchRequestId;
	}

	public void setSearchRequestId(final Integer searchRequestId) {
		this.searchRequestId = searchRequestId;
	}

	public SearchOrigin getSearchOrigin() {
		return searchOrigin;
	}

	public void setSearchOrigin(final SearchOrigin searchOrigin) {
		this.searchOrigin = searchOrigin;
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

	public enum SearchOrigin {
		MANAGE_STUDY_SOURCE, GERMPLASM_SEARCH;
	}
}
