package org.generationcp.middleware.domain.inventory.common;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Set;

@AutoProperty
public class SearchCompositeDto<X, Y> {

	private X searchRequest;

	private Set<Y> itemIds;

	private X study;

	public X getStudy() {
		return this.study;
	}

	public void setStudy(final X study) {
		this.study = study;
	}

	public X getSearchRequest() {
		return searchRequest;
	}

	public void setSearchRequest(final X searchRequest) {
		this.searchRequest = searchRequest;
	}

	public Set<Y> getItemIds() {
		return itemIds;
	}

	public void setItemIds(Set<Y> itemIds) {
		this.itemIds = itemIds;
	}

	public boolean isValid() {
		return !((searchRequest == null && (itemIds == null || itemIds.isEmpty()))
			|| (searchRequest != null && itemIds != null));
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
