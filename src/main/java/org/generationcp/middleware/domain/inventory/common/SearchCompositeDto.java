package org.generationcp.middleware.domain.inventory.common;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Set;

@AutoProperty
public class SearchCompositeDto<T> {

	private T searchRequest;

	private Set<Integer> itemIds;

	public T getSearchRequest() {
		return searchRequest;
	}

	public void setSearchRequest(final T searchRequest) {
		this.searchRequest = searchRequest;
	}

	public Set<Integer> getItemIds() {
		return itemIds;
	}

	public void setItemIds(Set<Integer> itemIds) {
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
